/*
 Copyright 2013 Thomas Struller-Baumann, struller-baumann.de

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package de.strullerbaumann.visualee.ui.graph.boundary;

import de.strullerbaumann.visualee.dependency.boundary.DependencyFilter;
import de.strullerbaumann.visualee.dependency.entity.Dependency;
import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.javasource.boundary.JavaSourceContainer;
import de.strullerbaumann.visualee.javasource.entity.JavaSource;
import de.strullerbaumann.visualee.maven.GraphMojo;
import de.strullerbaumann.visualee.ui.graph.control.Description;
import de.strullerbaumann.visualee.ui.graph.control.HTMLManager;
import de.strullerbaumann.visualee.ui.graph.entity.Graph;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public final class GraphCreator {

   private static final Logger LOGGER = Logger.getLogger(GraphCreator.class.getName());
   private static String htmlTemplate;
   private static final Map<String, List> GRAPH_TITLES = Collections.unmodifiableMap(new HashMap<String, List>() {
      {
         put("graphOnlyCDIJPA", Arrays.asList("Only CDI/JPA relevant classes of ", new DependencyFilter().filterAllTypes()));
         put("graphAllClasses", Arrays.asList("All classes of ", null));
         put("graphEventObserverClasses", Arrays.asList("Event/Observer classes of ", new DependencyFilter()
                 .addType(DependencyType.EVENT)
                 .addType(DependencyType.OBSERVES)));
         put("graphEJBClasses", Arrays.asList("Only EJB classes of ", new DependencyFilter()
                 .addType(DependencyType.EJB)));
         put("graphInstanceClasses", Arrays.asList("Only Instance classes of ", new DependencyFilter()
                 .addType(DependencyType.INSTANCE)));
         put("graphInjectClasses", Arrays.asList("Only Inject classes of ", new DependencyFilter()
                 .addType(DependencyType.INJECT)));
         put("graphProducesClasses", Arrays.asList("Only Produces classes of ", new DependencyFilter()
                 .addType(DependencyType.PRODUCES)));
         put("graphInstanceProducesClasses", Arrays.asList("Only Instance and Produces classes of ", new DependencyFilter()
                 .addType(DependencyType.INSTANCE)
                 .addType(DependencyType.PRODUCES)));
         put("graphInjectInstanceProducesClasses", Arrays.asList("Only Inject, Instance and Produces classes of ", new DependencyFilter()
                 .addType(DependencyType.INJECT)
                 .addType(DependencyType.INSTANCE)
                 .addType(DependencyType.PRODUCES)));
         put("graphResourcesClasses", Arrays.asList("Only Resource classes of ", new DependencyFilter()
                 .addType(DependencyType.RESOURCE)));
         put("graphJPAClasses", Arrays.asList("Only JPA classes of ", new DependencyFilter()
                 .addType(DependencyType.ONE_TO_ONE)
                 .addType(DependencyType.ONE_TO_MANY)
                 .addType(DependencyType.MANY_TO_ONE)
                 .addType(DependencyType.MANY_TO_MANY)));
      }
   });

   private GraphCreator() {
   }

   static JsonObjectBuilder buildJSONNode(JavaSource javaSource) {
      JsonObjectBuilder node = Json.createObjectBuilder();
      node.add("name", javaSource.toString())
              .add("group", javaSource.getGroup())
              .add("description", Description.generateDescription(javaSource))
              .add("sourcecode", javaSource.getEscapedSourceCode())
              .add("id", javaSource.getId());

      return node;
   }

   static JsonArrayBuilder buildJSONNodes(DependencyFilter filter) {
      List<JavaSource> relevantClasses = JavaSourceContainer.getInstance().getRelevantClasses(filter);
      JsonArrayBuilder nodesArray = Json.createArrayBuilder();
      int id = 0;
      for (JavaSource javaSource : JavaSourceContainer.getInstance().getJavaSources()) {
         if (filter == null || relevantClasses.contains(javaSource)) {
            javaSource.setId(id);
            nodesArray.add(buildJSONNode(javaSource));
            id++;
         }
      }

      return nodesArray;
   }

   // TODO Unittest
   static JsonArrayBuilder buildJSONLinks(DependencyFilter filter) {
      JsonArrayBuilder linksArray = Json.createArrayBuilder();
      for (JavaSource myJavaClass : JavaSourceContainer.getInstance().getJavaSources()) {
         int target = myJavaClass.getId();
         int value = 1;
         for (Dependency dependency : myJavaClass.getInjected()) {
            if (filter == null || filter.contains(dependency.getDependencyType())) {
               int source = dependency.getJavaSourceTo().getId();
               DependencyType type = dependency.getDependencyType();
               JsonObjectBuilder linksBuilder = Json.createObjectBuilder();
               if (DependencyType.isInverseDirection(type)) {
                  linksBuilder.add("source", target);
                  linksBuilder.add("target", source);
               } else {
                  linksBuilder.add("source", source);
                  linksBuilder.add("target", target);
               }
               linksBuilder.add("value", value);
               linksBuilder.add("type", type.toString());
               linksArray.add(linksBuilder);
            }
         }
      }

      return linksArray;
   }

   private static void setMojoAttributes(Graph graph, List<GraphMojo> graphMojos) {
      for (GraphMojo graphMojo : graphMojos) {
         if (graphMojo.getName().equals(graph.getName())) {
            graph.setDistance(graphMojo.getDistance());
            graph.setLinkdistance(graphMojo.getLinkdistance());
            graph.setGravity(graphMojo.getGravity());
            graph.setGraphSize(graphMojo.getGraphsize());
            graph.setFontsize(graphMojo.getFontsize());
            break;
         }
      }
   }

   public static Graph generateGraph(String fileName,
           String title,
           List<GraphMojo> graphMojos,
           DependencyFilter filter,
           InputStream htmlTemplateIS,
           File outputdirectory) {
      Graph graph = new Graph();
      graph.setName(fileName);
      File jsonFile = new File(outputdirectory.toString() + File.separatorChar + fileName + ".json");
      graph.setJsonFile(jsonFile);
      File htmlFile = new File(outputdirectory.toString() + File.separatorChar + fileName + ".html");
      graph.setHtmlFile(htmlFile);
      graph.setHtmlTemplateIS(htmlTemplateIS);

      setMojoAttributes(graph, graphMojos);
      graph.setTitle(title);
      JsonObjectBuilder builder = Json.createObjectBuilder();
      // Nodes
      JsonArrayBuilder nodesArray = buildJSONNodes(filter);
      builder.add("nodes", nodesArray);
      graph.setCountClasses(nodesArray.build().size());
      // Links
      JsonArrayBuilder linksArray = buildJSONLinks(filter);
      builder.add("links", linksArray);
      JsonObject json = builder.build();
      try (PrintStream ps = new PrintStream(graph.getJsonFile())) {
         ps.println(json.toString());
      } catch (FileNotFoundException ex) {
         LOGGER.log(Level.SEVERE, "Didn't found file " + graph.getJsonFile().getName(), ex);
      }

      return graph;
   }

   public static void generateGraphs(File rootFolder, File outputdirectory, InputStream htmlTemplateIS, List<GraphMojo> graphMojos) {
      // Load HTML-Template, if not already done (Maven modules)
      if (htmlTemplate == null) {
         htmlTemplate = HTMLManager.loadHTMLTemplate(htmlTemplateIS, "graphTemplate");
      }
      for (String graphName : GRAPH_TITLES.keySet()) {
         Graph graph = GraphCreator.generateGraph(graphName,
                 (String) GRAPH_TITLES.get(graphName).get(0) + rootFolder.getPath(),
                 graphMojos,
                 (DependencyFilter) GRAPH_TITLES.get(graphName).get(1),
                 htmlTemplateIS,
                 outputdirectory);
         graph.calculateAttributes();
         HTMLManager.generateHTML(graph, htmlTemplate);
      }
   }
}

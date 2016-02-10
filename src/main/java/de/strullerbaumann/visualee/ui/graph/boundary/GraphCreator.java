package de.strullerbaumann.visualee.ui.graph.boundary;

/*
 * #%L
 * visualee
 * %%
 * Copyright (C) 2013 Thomas Struller-Baumann
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import de.strullerbaumann.visualee.dependency.boundary.DependencyContainer;
import de.strullerbaumann.visualee.dependency.boundary.DependencyFilter;
import de.strullerbaumann.visualee.dependency.entity.Dependency;
import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.logging.LogProvider;
import de.strullerbaumann.visualee.source.boundary.JavaSourceContainer;
import de.strullerbaumann.visualee.source.entity.JavaSource;
import de.strullerbaumann.visualee.ui.graph.control.Description;
import de.strullerbaumann.visualee.ui.graph.control.HTMLManager;
import de.strullerbaumann.visualee.ui.graph.entity.Graph;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de)
 */
public final class GraphCreator {

   private static int id;
   private static String graphTemplate;
   private static final Map<String, List> GRAPHS = Collections.unmodifiableMap(new HashMap<String, List>() {
      {
         put("graphOnlyCDIJPA",
                 Arrays.asList("Only CDI/JPA relevant classes of ", new DependencyFilter().filterAllTypes()));
         put("graphAllClasses",
                 Arrays.asList("All classes of ", null));
         put("graphEventObserverClasses",
                 Arrays.asList("Event/Observer classes of ", new DependencyFilter()
                         .addType(DependencyType.EVENT)
                         .addType(DependencyType.OBSERVES)));
         put("graphEJBClasses",
                 Arrays.asList("Only EJB classes of ", new DependencyFilter()
                         .addType(DependencyType.EJB)));
         put("graphInstanceClasses",
                 Arrays.asList("Only Instance classes of ", new DependencyFilter()
                         .addType(DependencyType.INSTANCE)));
         put("graphInjectClasses",
                 Arrays.asList("Only Inject classes of ", new DependencyFilter()
                         .addType(DependencyType.INJECT)));
         put("graphProducesClasses",
                 Arrays.asList("Only Produces classes of ", new DependencyFilter()
                         .addType(DependencyType.PRODUCES)));
         put("graphInstanceProducesClasses",
                 Arrays.asList("Only Instance and Produces classes of ", new DependencyFilter()
                         .addType(DependencyType.INSTANCE)
                         .addType(DependencyType.PRODUCES)));
         put("graphConnectedInjectProducesClasses",
                 Arrays.asList("Only directly connected Inject and Produces classes of ", new DependencyFilter()
                         .addType(DependencyType.PRODUCES)
                         .addType(DependencyType.INJECT)
                         .setDirectlyConnected(true)));
         put("graphConnectedInjectInstanceProducesClasses",
                 Arrays.asList("Only directly connected Inject, Instance and Produces classes of ", new DependencyFilter()
                         .addType(DependencyType.PRODUCES)
                         .addType(DependencyType.INJECT)
                         .addType(DependencyType.INSTANCE)
                         .setDirectlyConnected(true)));
         put("graphConnectedInstanceProducesClasses",
                 Arrays.asList("Only directly connected Instance and Produces classes of ", new DependencyFilter()
                         .addType(DependencyType.PRODUCES)
                         .addType(DependencyType.INSTANCE)
                         .setDirectlyConnected(true)));
         put("graphResourcesClasses",
                 Arrays.asList("Only Resource classes of ", new DependencyFilter()
                         .addType(DependencyType.RESOURCE)));
         put("graphJPAClasses",
                 Arrays.asList("Only JPA classes of ", new DependencyFilter()
                         .addType(DependencyType.ONE_TO_ONE)
                         .addType(DependencyType.ONE_TO_MANY)
                         .addType(DependencyType.MANY_TO_ONE)
                         .addType(DependencyType.MANY_TO_MANY)));
      }
   });

   private GraphCreator() {
   }

   static JsonObjectBuilder buildJSONNode(JavaSource javaSource) {
      // this id processing is necessary, because d3.js needs consecutive ids
      javaSource.setId(id);
      id++;
      JsonObjectBuilder node = Json.createObjectBuilder();
      node.add("name", javaSource.toString())
              .add("group", javaSource.getGroup())
              .add("description", Description.generateDescription(javaSource))
              .add("sourcecode", javaSource.getEscapedSourceCode())
              .add("id", javaSource.getId());
      return node;
   }

   static JsonArrayBuilder buildJSONNodes(DependencyFilter filter) {
      Set<JavaSource> relevantClasses = DependencyContainer.getInstance().getFilteredJavaSources(filter);
      JsonArrayBuilder nodesArray = Json.createArrayBuilder();
      for (JavaSource javaSource : JavaSourceContainer.getInstance().getJavaSources()) {
         if (filter == null || relevantClasses.contains(javaSource)) {
            nodesArray.add(buildJSONNode(javaSource));
         }
      }

      return nodesArray;
   }

   static JsonArrayBuilder buildJSONLinks(DependencyFilter filter) {
      JsonArrayBuilder linksArray = Json.createArrayBuilder();
      int value = 1;
      Set<JavaSource> relevantClasses = DependencyContainer.getInstance().getFilteredJavaSources(filter);
      for (JavaSource javaSource : relevantClasses) {
         for (Dependency d : DependencyContainer.getInstance().getDependencies(javaSource)) {
            DependencyType type = d.getDependencyType();
            if (filter == null
                    || (relevantClasses.contains(d.getJavaSourceTo()) && filter.contains(type))) {
               int source = d.getJavaSourceFrom().getId();
               int target = d.getJavaSourceTo().getId();
               JsonObjectBuilder linksBuilder = Json.createObjectBuilder();
               if (DependencyType.isInverseDirection(type)) {
                  linksBuilder.add("source", source);
                  linksBuilder.add("target", target);
               } else {
                  linksBuilder.add("source", target);
                  linksBuilder.add("target", source);
               }
               linksBuilder.add("value", value);
               linksBuilder.add("type", type.toString());
               linksArray.add(linksBuilder);
            }
         }
      }
      return linksArray;
   }

   public static Graph generateGraph(String fileName,
           String title,
           DependencyFilter filter,
           File outputdirectory) {
      id = 0;
      Graph graph = new Graph();
      graph.setName(fileName);
      File jsonFile = new File(outputdirectory.toString() + File.separatorChar + fileName + ".json");
      graph.setJsonFile(jsonFile);
      File htmlFile = new File(outputdirectory.toString() + File.separatorChar + fileName + ".html");
      graph.setHtmlFile(htmlFile);
      graph.setTitle(title);
      GraphConfigurator.configGraph(graph);
      JsonObjectBuilder builder = Json.createObjectBuilder();
      // Nodes
      JsonArrayBuilder nodesArray = buildJSONNodes(filter);
      builder.add("nodes", nodesArray);
      // Links
      JsonArrayBuilder linksArray = buildJSONLinks(filter);
      builder.add("links", linksArray);
      JsonObject json = builder.build();
      try (PrintStream ps = new PrintStream(graph.getJsonFile())) {
         ps.println(json.toString());
      } catch (FileNotFoundException ex) {
         LogProvider.getInstance().error("Didn't found file " + graph.getJsonFile().getName(), ex);
      }
      return graph;
   }

   public static void generateGraphs(File rootFolder, File outputdirectory, String graphTemplatePath) {
      if (!outputdirectory.exists()) {
         outputdirectory.mkdir();
      }
      // Load graph-Template, if not already done (Maven modules)
      if (graphTemplate == null) {
         graphTemplate = HTMLManager.loadHTMLTemplate(graphTemplatePath);
      }
      for (String graphName : GRAPHS.keySet()) {
         Graph graph = GraphCreator.generateGraph(graphName,
                 (String) GRAPHS.get(graphName).get(0) + rootFolder.getPath(),
                 (DependencyFilter) GRAPHS.get(graphName).get(1),
                 outputdirectory);
         HTMLManager.generateHTML(graph, graphTemplate);
      }
   }
}

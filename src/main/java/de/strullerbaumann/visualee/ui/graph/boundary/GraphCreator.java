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
import de.strullerbaumann.visualee.ui.graph.control.Description;
import de.strullerbaumann.visualee.ui.graph.control.HTMLManager;
import de.strullerbaumann.visualee.ui.graph.entity.Graph;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
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

   private GraphCreator() {
   }

   static Graph initGraph(File outputdirectory, String fileName, InputStream htmlTemplateIS) {
      Graph graph = new Graph();
      File jsonFile = new File(outputdirectory.toString() + File.separatorChar + fileName + ".json");
      graph.setJsonFile(jsonFile);
      File htmlFile = new File(outputdirectory.toString() + File.separatorChar + fileName + ".html");
      graph.setHtmlFile(htmlFile);
      graph.setHtmlTemplateIS(htmlTemplateIS);
      return graph;
   }

   public static Graph generateGraph(String fileName,
           DependencyFilter filter,
           InputStream htmlTemplateIS,
           File outputdirectory) {
      Graph graph = initGraph(outputdirectory, fileName, htmlTemplateIS);
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
         if (filter == null || (filter != null && relevantClasses.contains(javaSource))) {
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

   public static void generateGraphs(File rootFolder, File outputdirectory, InputStream htmlTemplateIS) {
      // 3. Load HTML-Template, if not already done (Maven modules)
      if (htmlTemplate == null) {
         htmlTemplate = HTMLManager.loadHTMLTemplate(htmlTemplateIS, "graphTemplate");
      }
      // GRAPH - only CDI-relevant classes
      DependencyFilter cdiFilterOnlyCDI = new DependencyFilter().filterAllTypes();
      Graph graphOnlyCDI = GraphCreator.generateGraph("graphOnlyCDI", cdiFilterOnlyCDI, htmlTemplateIS, outputdirectory);
      graphOnlyCDI.setTitle("Only CDI relevant classes of " + rootFolder.getPath());
      graphOnlyCDI.calculateDimensions();
      HTMLManager.generateHTML(graphOnlyCDI, htmlTemplate);

      // GRAPH - all classes
      Graph graphAllClasses = GraphCreator.generateGraph("graphAllClasses", null, htmlTemplateIS, outputdirectory);
      graphAllClasses.setTitle("All classes of " + rootFolder.getPath());
      graphAllClasses.calculateDimensions();
      HTMLManager.generateHTML(graphAllClasses, htmlTemplate);

      // GRAPH - only Event/Observer classes
      DependencyFilter cdiFilterEventObserver = new DependencyFilter()
              .addType(DependencyType.EVENT)
              .addType(DependencyType.OBSERVES);
      Graph graphEventObserverClasses = GraphCreator.generateGraph("graphEventObserverClasses", cdiFilterEventObserver, htmlTemplateIS, outputdirectory);
      graphEventObserverClasses.setTitle("Event/Observer classes of " + rootFolder.getPath());
      graphEventObserverClasses.calculateDimensions();
      HTMLManager.generateHTML(graphEventObserverClasses, htmlTemplate);

      // GRAPH - only EJB classes
      DependencyFilter cdiFilterEJB = new DependencyFilter()
              .addType(DependencyType.EJB);
      Graph graphEJBClasses = GraphCreator.generateGraph("graphEJBClasses", cdiFilterEJB, htmlTemplateIS, outputdirectory);
      graphEJBClasses.setTitle("Only EJB classes of " + rootFolder.getPath());
      graphEJBClasses.calculateDimensions();
      HTMLManager.generateHTML(graphEJBClasses, htmlTemplate);

      // GRAPH - only Instance classes
      DependencyFilter cdiFilterInstance = new DependencyFilter()
              .addType(DependencyType.INSTANCE);
      Graph graphInstanceClasses = GraphCreator.generateGraph("graphInstanceClasses", cdiFilterInstance, htmlTemplateIS, outputdirectory);
      graphInstanceClasses.setTitle("Only Instance classes of " + rootFolder.getPath());
      graphInstanceClasses.calculateDimensions();
      HTMLManager.generateHTML(graphInstanceClasses, htmlTemplate);

      // GRAPH - only Inject classes
      DependencyFilter cdiFilterInject = new DependencyFilter()
              .addType(DependencyType.INJECT);
      Graph graphInjectClasses = GraphCreator.generateGraph("graphInjectClasses", cdiFilterInject, htmlTemplateIS, outputdirectory);
      graphInjectClasses.setTitle("Only Inject classes of " + rootFolder.getPath());
      graphInjectClasses.calculateDimensions();
      HTMLManager.generateHTML(graphInjectClasses, htmlTemplate);

      // GRAPH - only Produces classes
      DependencyFilter cdiFilterProduces = new DependencyFilter()
              .addType(DependencyType.PRODUCES);
      Graph graphProducesClasses = GraphCreator.generateGraph("graphProducesClasses", cdiFilterProduces, htmlTemplateIS, outputdirectory);
      graphProducesClasses.setTitle("Only Produces classes of " + rootFolder.getPath());
      graphProducesClasses.calculateDimensions();
      HTMLManager.generateHTML(graphProducesClasses, htmlTemplate);

      // GRAPH - only Resources classes
      DependencyFilter cdiFilterResources = new DependencyFilter()
              .addType(DependencyType.RESOURCE);
      Graph graphResourcesClasses = GraphCreator.generateGraph("graphResourcesClasses", cdiFilterResources, htmlTemplateIS, outputdirectory);
      graphResourcesClasses.setTitle("Only Resource classes of " + rootFolder.getPath());
      graphResourcesClasses.calculateDimensions();
      HTMLManager.generateHTML(graphResourcesClasses, htmlTemplate);

      // GRAPH - only JPA classes
      DependencyFilter filterJPA = new DependencyFilter()
              .addType(DependencyType.ONE_TO_ONE)
              .addType(DependencyType.ONE_TO_MANY)
              .addType(DependencyType.MANY_TO_ONE)
              .addType(DependencyType.MANY_TO_MANY);
      Graph graphJPAClasses = GraphCreator.generateGraph("graphJPAClasses", filterJPA, htmlTemplateIS, outputdirectory);
      graphJPAClasses.setTitle("Only JPA classes of " + rootFolder.getPath());
      graphJPAClasses.calculateDimensions();
      HTMLManager.generateHTML(graphJPAClasses, htmlTemplate);
   }
}

/*
 * Created on 10.07.2013 - 09:27:30
 *
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.ui;

import de.strullerbaumann.visualee.dependency.DependenciyType;
import de.strullerbaumann.visualee.dependency.Dependency;
import de.strullerbaumann.visualee.dependency.DependencyFilter;
import de.strullerbaumann.visualee.resources.Description;
import de.strullerbaumann.visualee.resources.JavaSource;
import de.strullerbaumann.visualee.resources.JavaSourceContainer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
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

   private GraphCreator() {
   }

   public static Graph generateGraph(String fileName,
           File outputdirectory,
           DependencyFilter filter,
           boolean onlyRelevantClasses,
           InputStream htmlTemplateIS) {
      Graph graph = initGraph(outputdirectory, fileName, htmlTemplateIS);

      List<JavaSource> relevantClasses = JavaSourceContainer.getInstance().getRelevantClasses(filter);
      JsonObjectBuilder builder = Json.createObjectBuilder();

      // Nodes
      JsonArrayBuilder nodesArray = buildJSONNodes(JavaSourceContainer.getInstance(), onlyRelevantClasses, relevantClasses);
      builder.add("nodes", nodesArray);
      graph.setCountClasses(nodesArray.build().size());

      // Links
      JsonArrayBuilder linksArray = buildJSONLinks(JavaSourceContainer.getInstance(), filter);
      builder.add("links", linksArray);

      JsonObject json = builder.build();
      try (PrintStream ps = new PrintStream(graph.getJsonFile())) {
         ps.println(json.toString());
      } catch (FileNotFoundException ex) {
         Logger.getLogger(GraphCreator.class.getName()).log(Level.SEVERE, "Didn't found file " + graph.getJsonFile().getName(), ex);
      }

      return graph;
   }

   // TODO Unittest
   protected static Graph initGraph(File outputdirectory, String fileName, InputStream htmlTemplateIS) {
      Graph graph = new Graph();
      File jsonFile = new File(outputdirectory + "/" + fileName + ".json");
      graph.setJsonFile(jsonFile);
      File htmlFile = new File(outputdirectory + "/" + fileName + ".html");
      graph.setHtmlFile(htmlFile);
      graph.setHtmlTemplateIS(htmlTemplateIS);
      return graph;
   }

   // TODO Unittest
   protected static JsonArrayBuilder buildJSONNodes(JavaSourceContainer javaSourceContainer,
           boolean onlyRelevantClasses,
           List<JavaSource> relevantClasses) {
      JsonArrayBuilder nodesArray = Json.createArrayBuilder();
      int id = 0;
      for (JavaSource javaSource : javaSourceContainer.getJavaSources()) {
         if (!onlyRelevantClasses || (onlyRelevantClasses && relevantClasses.contains(javaSource))) {
            javaSource.setId(id);
            nodesArray.add(buildJSONNode(javaSource));
            id++;
         }
      }
      return nodesArray;
   }

   // TODO Unittest
   protected static JsonObjectBuilder buildJSONNode(JavaSource javaSource) {
      JsonObjectBuilder node = Json.createObjectBuilder();
      node.add("name", javaSource.toString())
              .add("group", javaSource.getGroup())
              .add("description", Description.generateDescription(javaSource))
              .add("sourcecode", javaSource.getEscapedSourceCode())
              .add("id", javaSource.getId());

      return node;
   }

   // TODO Unittest
   protected static JsonArrayBuilder buildJSONLinks(JavaSourceContainer javaSourceContainer, DependencyFilter filter) {
      JsonArrayBuilder linksArray = Json.createArrayBuilder();
      for (JavaSource myJavaClass : javaSourceContainer.getJavaSources()) {
         int target = myJavaClass.getId();
         int value = 1;
         for (Dependency dependency : myJavaClass.getInjected()) {
            if (filter == null || filter.contains(dependency.getDependencyType())) {
               int source = dependency.getJavaSourceTo().getId();
               DependenciyType type = dependency.getDependencyType();
               JsonObjectBuilder linksBuilder = Json.createObjectBuilder();
               if (isInverseDirection(type)) {
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

   protected static boolean isInverseDirection(DependenciyType type) {
      return Arrays.asList(
              DependenciyType.EVENT,
              DependenciyType.PRODUCES,
              DependenciyType.OBSERVES,
              DependenciyType.ONE_TO_MANY,
              DependenciyType.ONE_TO_ONE,
              DependenciyType.MANY_TO_ONE,
              DependenciyType.MANY_TO_MANY).contains(type);
   }
}

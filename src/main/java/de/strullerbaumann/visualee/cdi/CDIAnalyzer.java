/*
 * Created on 08.04.2013 - 14:40:17 
 * 
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.cdi;

import de.strullerbaumann.visualee.resources.FileHelper;
import de.strullerbaumann.visualee.resources.HTMLHelper;
import de.strullerbaumann.visualee.resources.JavaFile;
import de.strullerbaumann.visualee.resources.JavaFileExaminer;
import de.strullerbaumann.visualee.resources.JavaFilesContainer;
import de.strullerbaumann.visualee.resources.JsonHelper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class CDIAnalyzer {

   private static JavaFilesContainer javaFilesContainer;
   private static InputStream htmlTemplateIS;
   private static String htmlTemplate;

   public static void analyze(File rootFolder, File outputdirectory, InputStream htmlTemplateIS) {
      CDIAnalyzer.htmlTemplateIS = htmlTemplateIS;

      // 1. Load Javafiles
      final List<File> javaFiles = FileHelper.searchFiles(rootFolder, ".java");
      javaFilesContainer = new JavaFilesContainer();
      for (File javaFile : javaFiles) {
         JavaFile myJavaFile = new JavaFile(javaFile);
         javaFilesContainer.add(myJavaFile);
      }
      // 2. Examine Javafiles
      try {
         JavaFileExaminer.getInstance().examine(javaFilesContainer);
      } catch (FileNotFoundException ex) {
         Logger.getLogger(CDIAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
         Logger.getLogger(CDIAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
      }
      // 3. Load HTML-Template
      htmlTemplate = HTMLHelper.loadHTMLTemplate(htmlTemplateIS);
      // 4. Prepare outputdirectory
      if (!outputdirectory.exists()) {
         outputdirectory.mkdir();
      }
      // 5. Generate Graphs
      // GRAPH - only CDI-relevant classes
      CDIGraph graphOnlyCDI = generateCDIGraph("graphOnlyCDI", outputdirectory, null, true);
      graphOnlyCDI.setTitle("Only CDI relevant classes of " + rootFolder.getPath());
      graphOnlyCDI.calculateDimensions();
      HTMLHelper.generateHTML(graphOnlyCDI, htmlTemplate);

      // GRAPH - all classes
      CDIGraph graphAllClasses = generateCDIGraph("graphAllClasses", outputdirectory, null, false);
      graphAllClasses.setTitle("All classes of " + rootFolder.getPath());
      graphAllClasses.calculateDimensions();
      HTMLHelper.generateHTML(graphAllClasses, htmlTemplate);

      // GRAPH - only Event/Observer classes
      CDIFilter cdiFilter = new CDIFilter().addCDIType(CDIType.EVENT).addCDIType(CDIType.OBSERVES);
      CDIGraph graphEventObserverClasses = generateCDIGraph("graphEventObserverClasses", outputdirectory, cdiFilter, true);
      graphEventObserverClasses.setTitle("Event/Observer classes of " + rootFolder.getPath());
      graphEventObserverClasses.calculateDimensions();
      HTMLHelper.generateHTML(graphEventObserverClasses, htmlTemplate);
   }

   private static CDIGraph generateCDIGraph(String fileName, File outputdirectory, CDIFilter cdiFilter, boolean onlyCDIRelevantClasses) {
      CDIGraph graph = new CDIGraph();
      File jsonFile = new File(outputdirectory + "/" + fileName + ".json");
      graph.setJsonFile(jsonFile);
      File htmlFile = new File(outputdirectory + "/" + fileName + ".html");
      graph.setHtmlFile(htmlFile);
      graph.setHtmlTemplateIS(htmlTemplateIS);
      if (onlyCDIRelevantClasses) {
         graph.setGravity("0.01");
      } else {
         graph.setGravity("0.06");
      }

      // 3. Graphen für die Javaklassen erstellen
      // JSON erzeugen
      int countClasses = 0;
      // CDI relevante Klassen ermitteln, also alle welche CDI haben oder für CDI hergenommen werden
      // und nur Event oder Observer haben
      List<JavaFile> classesCDIRelated = javaFilesContainer.getCDIRelevantClasses(cdiFilter);
      try (PrintStream ps = new PrintStream(jsonFile)) {
         // TODO schöner coden
         if (classesCDIRelated.isEmpty()) {  // Was gefunden?
            ps.println("{");
            ps.println("    \"nodes\":      []");
            ps.println("}");
            return graph;
         }
         // Nodes ausgeben
         int iNode = 0;
         ps.println("{");
         ps.println("    \"nodes\":      [");
         StringBuilder nodesJSON = new StringBuilder();
         for (JavaFile myJavaClass : javaFilesContainer.getMyJavaFiles()) {
            // if (classesCDIRelated.contains(myJavaClass)) {  // nur CDI-relevante
            if (!onlyCDIRelevantClasses || (onlyCDIRelevantClasses && classesCDIRelated.contains(myJavaClass))) {
               myJavaClass.setId(iNode);
               String description = JsonHelper.generateDescription(myJavaClass);
               nodesJSON.append(JsonHelper.getJSONNode(myJavaClass.toString(), myJavaClass.getGroup(), description, iNode));
               if (iNode < javaFilesContainer.getMyJavaFiles().size() - 1) {
                  nodesJSON.append("    ,");
               }
               iNode++;
            }
         }

         if (nodesJSON.length() > 0) { // Was gefunden?
            countClasses = iNode + 1; //+1 da iNode mit 0 beginnt
            nodesJSON.deleteCharAt(nodesJSON.length() - 1);   //das letzte , löschen
            ps.println(nodesJSON.toString());
            ps.println("    ],");
            // Links ausgeben
            ps.println("    \"links\":      [");
            StringBuilder linksJSON = new StringBuilder();
            final String linkSeparator = "    ," + System.lineSeparator();
            boolean hasLinks = false;
            for (JavaFile myJavaClass : javaFilesContainer.getMyJavaFiles()) {
               int target = myJavaClass.getId();
               int value = 1;
               for (CDIDependency dependency : myJavaClass.getInjected()) {
                  if (cdiFilter == null || cdiFilter.contains(dependency.getCdiType())) {
                     int source = dependency.getMyJavaFileTo().getId();
                     CDIType cdiType = dependency.getCdiType();
                     linksJSON.append(JsonHelper.getJSONLink(source, target, value, cdiType));
                     linksJSON.append(linkSeparator);
                     hasLinks = true;
                  }
               }
            }
            if (hasLinks) {
               String strLinks = linksJSON.substring(0, linksJSON.lastIndexOf(linkSeparator));  //letztes Komma abschneiden
               ps.println(strLinks);
            }
            ps.println("    ]");
            ps.println("}");
         }
      } catch (FileNotFoundException ex) {
         Logger.getLogger(CDIAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
      }
      graph.setCountClasses(countClasses);
      graph.setCountCDIClasses(countClasses);

      return graph;
   }
}
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
package de.strullerbaumann.visualee.resources;

import de.strullerbaumann.visualee.dependency.DependencyAnalyzer;
import de.strullerbaumann.visualee.ui.Graph;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public final class HTMLManager {

   private HTMLManager() {
   }

   public static String loadHTMLTemplate(InputStream graphTemplate, String htmlName) {
      // So spart man sich mehrfaches laden des Templates und auch umständliche reopen des InputStreams
      // InputStream, da im Plugin per getResource auf das html im jar zugegriffen wird)
      StringBuilder htmlTemplateBuilder = new StringBuilder();
      BufferedReader br = null;
      try {
         br = new BufferedReader(new InputStreamReader(graphTemplate));
         String line;
         while ((line = br.readLine()) != null) {
            htmlTemplateBuilder.append(line);
            htmlTemplateBuilder.append(System.lineSeparator());
         }
      } catch (FileNotFoundException ex) {
         Logger.getLogger(HTMLManager.class.getName()).log(Level.SEVERE, "can not load " + htmlName, ex);
      } catch (IOException ex) {
         Logger.getLogger(HTMLManager.class.getName()).log(Level.SEVERE, "can not load " + htmlName, ex);
      } finally {
         if (br != null) {
            try {
               br.close();
            } catch (IOException ex) {
               Logger.getLogger(HTMLManager.class.getName()).log(Level.SEVERE, "can not close " + htmlName, ex);
            }
         }
      }
      return htmlTemplateBuilder.toString();
   }

   public static void generateIndexHTML(File outputdirectory, InputStream indexHtmlIS, String title) {
      String indexHtml = loadHTMLTemplate(indexHtmlIS, "index.html");
      indexHtml = indexHtml.replaceAll("INDEX_PROJECT_TITLE", title);
      SimpleDateFormat sdf = new SimpleDateFormat();
      sdf.applyPattern("dd.MM.yyyy ' - ' HH:mm:ss");
      indexHtml = indexHtml.replaceAll("INDEX_CREATIONDATE", "Created " + sdf.format(new Date()));

      File htmlFile = new File(outputdirectory.getAbsolutePath() + "/index.html");
      try (PrintStream ps = new PrintStream(htmlFile)) {
         ps.println(indexHtml);
      } catch (FileNotFoundException ex) {
         Logger.getLogger(DependencyAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   public static void generateHTML(Graph graph, String htmlTemplate) {
      String html = htmlTemplate;
      html = html.replaceAll("DI_TEMPLATE_JSON_FILE", graph.getJsonFile().getName());
      html = html.replaceAll("DI_TEMPLATE_WIDTH", graph.getWidthString());
      html = html.replaceAll("DI_TEMPLATE_HEIGHT", graph.getHeightString());
      html = html.replaceAll("DI_TEMPLATE_TITLE", graph.getTitle());
      SimpleDateFormat sdf = new SimpleDateFormat();
      sdf.applyPattern("dd.MM.yyyy ' - ' HH:mm:ss");
      html = html.replaceAll("DI_TEMPLATE_CREATIONDATE", "Created " + sdf.format(new Date()));
      try (PrintStream ps = new PrintStream(graph.getHtmlFile())) {
         ps.println(html);
      } catch (FileNotFoundException ex) {
         Logger.getLogger(DependencyAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}

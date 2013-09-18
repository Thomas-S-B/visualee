package de.strullerbaumann.visualee.ui.graph.control;

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
import de.strullerbaumann.visualee.logging.LogProvider;
import de.strullerbaumann.visualee.ui.graph.entity.Graph;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public final class HTMLManager {

   private HTMLManager() {
   }

   public static String loadHTMLTemplate(InputStream graphTemplate, String htmlName) {
      //InputStream because html is accessed via getResource->jar in the plugin
      StringBuilder htmlTemplateBuilder = new StringBuilder();
      try (BufferedReader br = new BufferedReader(new InputStreamReader(graphTemplate))) {
         String line;
         while ((line = br.readLine()) != null) {
            htmlTemplateBuilder.append(line);
            htmlTemplateBuilder.append(System.lineSeparator());
         }
      } catch (IOException ex) {
         LogProvider.getInstance().error("can't load " + htmlName, ex);
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
         LogProvider.getInstance().error("Didn't found index.html", ex);
      }
   }

   public static void generateHTML(Graph graph, String htmlTemplate) {
      String html = htmlTemplate;
      html = html.replaceAll("DI_TEMPLATE_JSON_FILE", graph.getJsonFile().getName());
      html = html.replaceAll("DI_TEMPLATE_TITLE", graph.getTitle());
      html = html.replaceAll("DI_TEMPLATE_DISTANCE", graph.getDistanceString());
      html = html.replaceAll("DI_TEMPLATE_LINKDISTANCE", graph.getLinkdistanceString());
      html = html.replaceAll("DI_TEMPLATE_FONTSIZE", graph.getFontsizeString());
      html = html.replaceAll("DI_TEMPLATE_GRAVITY", graph.getGravityString());
      html = html.replaceAll("DI_TEMPLATE_GRAPHSIZE", graph.getGraphSizeString());
      SimpleDateFormat sdf = new SimpleDateFormat();
      sdf.applyPattern("dd.MM.yyyy ' - ' HH:mm:ss");
      html = html.replaceAll("DI_TEMPLATE_CREATIONDATE", "Created " + sdf.format(new Date()));
      try (PrintStream ps = new PrintStream(graph.getHtmlFile())) {
         ps.println(html);
      } catch (FileNotFoundException ex) {
         LogProvider.getInstance().error("Didn't found " + graph.getHtmlFile(), ex);
      }
   }
}

/*
 * Created on 11.04.2013 - 09:52:30 
 * 
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.resources;

import de.strullerbaumann.visualee.cdi.CDIAnalyzer;
import de.strullerbaumann.visualee.cdi.CDIGraph;
import java.io.BufferedReader;
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
public class HTMLHelper {

   public static String loadHTMLTemplate(InputStream graphTemplate) {
      // So spart man sich ehrfaches laden des Templates und auch umständliche reopen des InputStreams
      // InputStream, da im Plugin per getResource auf das html im jar zugegriffen wird)
      StringBuilder htmlTemplateBuilder = new StringBuilder();
      BufferedReader br = null;
      try {
         br = new BufferedReader(new InputStreamReader(graphTemplate));
         String line;
         while ((line = br.readLine()) != null) {
            //htmlTemplateBuilder.append(line.trim());
            htmlTemplateBuilder.append(line);
            htmlTemplateBuilder.append(System.lineSeparator());
         }
      } catch (FileNotFoundException ex) {
         Logger.getLogger(CDIAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
         Logger.getLogger(CDIAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
         if (br != null) {
            try {
               br.close();
            } catch (IOException ex) {
               Logger.getLogger(CDIAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      }
      return htmlTemplateBuilder.toString();
   }

   public static void generateHTML(CDIGraph graph, String htmlTemplate) {
      String html = htmlTemplate.toString();
      html = html.replaceAll("CDI_TEMPLATE_JSON_FILE", graph.getJsonFile().getName());
      html = html.replaceAll("CDI_TEMPLATE_WIDTH", graph.getWidthString());
      html = html.replaceAll("CDI_TEMPLATE_HEIGHT", graph.getHeightString());
      html = html.replaceAll("CDI_TEMPLATE_GRAVITY", graph.getGravity());
      html = html.replaceAll("CDI_TEMPLATE_TITLE", graph.getTitle());
      SimpleDateFormat sdf = new SimpleDateFormat();
      sdf.applyPattern("dd.MM.yyyy ' - ' HH:mm:ss");
      html = html.replaceAll("CDI_TEMPLATE_CREATIONDATE", "Created " + sdf.format(new Date()));
      try (PrintStream ps = new PrintStream(graph.getHtmlFile())) {
         ps.println(html);
      } catch (FileNotFoundException ex) {
         Logger.getLogger(CDIAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}

/*
 * Created on 02.08.2013 - 10:42:49
 *
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee;

import de.strullerbaumann.visualee.dependency.boundary.DependencyAnalyzer;
import de.strullerbaumann.visualee.resources.FileManager;
import de.strullerbaumann.visualee.ui.graph.control.HTMLManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class VisualEE {

   public static void main(String[] args) {
      String projectHome = "/home/thomas/NetBeansProjects/lightfish/lightfish";
      String sourceFolder = projectHome + "/src/";
      String outputdirectory = projectHome + "/visualee/";
      InputStream indexIS;
      InputStream graphTemplateIS;
      try {
         indexIS = new FileInputStream("/home/thomas/NetBeansProjects/visualee/src/main/resources/html/index.html");
         HTMLManager.generateIndexHTML(new File(outputdirectory), indexIS, sourceFolder);
         graphTemplateIS = new FileInputStream("/home/thomas/NetBeansProjects/visualee/src/main/resources/html/graphTemplate.html");
         DependencyAnalyzer.analyze(new File(sourceFolder), new File(outputdirectory), graphTemplateIS);
      } catch (FileNotFoundException ex) {
         Logger.getLogger(VisualEE.class.getName()).log(Level.SEVERE, "cannot find graphTemplate.html", ex);
      }
      try {
         FileManager.copyFolder(new File("/home/thomas/NetBeansProjects/visualee/src/main/resources/css/"), new File(outputdirectory + "/css/"));
         FileManager.copyFolder(new File("/home/thomas/NetBeansProjects/visualee/src/main/resources/js/"), new File(outputdirectory + "/js/"));
      } catch (IOException ex) {
         Logger.getLogger(VisualEE.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}

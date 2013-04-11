/*
 * Created on 13.12.2012 - 09:14:20 
 * 
 * Copyright(c) 2012 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.cdi;

import java.io.File;
import java.io.InputStream;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class CDIGraph {

   private int countClasses;  //wird benötigt, da die Dimensionen daraus ereechnet werden und jeder Graph hat unterschiedliche Anzahl von Klassen
   private int countCDIClasses;  //wird benötigt, da die Dimensionen daraus ereechnet werden und jeder Graph hat unterschiedliche Anzahl von Klassen
   private int width;
   private int height;
   private File htmlFile;
   // private File htmlTemplateFile;
   private InputStream htmlTemplateIS;
   private File jsonFile;
   private String title;
   private String gravity;

   public int getCountCDIClasses() {
      return countCDIClasses;
   }

   public void setCountCDIClasses(int countCDIClasses) {
      this.countCDIClasses = countCDIClasses;
   }

   public String getGravity() {
      return gravity;
   }

   public void setGravity(String gravity) {
      this.gravity = gravity;
   }

   public int getCountClasses() {
      return countClasses;
   }

   public void setCountClasses(int countClasses) {
      this.countClasses = countClasses;
   }

   public int getWidth() {
      return width;
   }

   public String getWidthString() {
      return Integer.toString(width);
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public int getHeight() {
      return height;
   }

   public String getHeightString() {
      return Integer.toString(height);
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public File getHtmlFile() {
      return htmlFile;
   }

   public void setHtmlFile(File htmlFile) {
      this.htmlFile = htmlFile;
   }

   public InputStream getHtmlTemplateIS() {
      return htmlTemplateIS;
   }

   public void setHtmlTemplateIS(InputStream htmlTemplateIS) {
      this.htmlTemplateIS = htmlTemplateIS;
   }

   public File getJsonFile() {
      return jsonFile;
   }

   public void setJsonFile(File jsonFile) {
      this.jsonFile = jsonFile;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public void calculateDimensions() {
      setWidth(getCountCDIClasses() * 15);
      setHeight(getCountCDIClasses() * 10);

      if (getWidth() < 600) {
         setWidth(600);
      }
      if (getHeight() < 500) {
         setHeight(500);
      }
   }
}

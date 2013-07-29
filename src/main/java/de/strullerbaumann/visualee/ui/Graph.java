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
package de.strullerbaumann.visualee.ui;

import java.io.File;
import java.io.InputStream;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class Graph {

   private static final int CLASS_SIZE = 20;
   private static final int MIN_WIDTH = 600;
   private static final int MIN_HEIGHT = 500;
   private int countClasses;
   private int width;
   private int height;
   private File htmlFile;
   private InputStream htmlTemplateIS;
   private File jsonFile;
   private String title;

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
      setWidth(getCountClasses() * CLASS_SIZE);
      setHeight(getCountClasses() * CLASS_SIZE);

      if (getWidth() < MIN_WIDTH) {
         setWidth(MIN_WIDTH);
      }
      if (getHeight() < MIN_HEIGHT) {
         setHeight(MIN_HEIGHT);
      }
   }
}

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
package de.strullerbaumann.visualee.ui.graph.entity;

import de.strullerbaumann.visualee.dependency.boundary.DependencyFilter;
import java.io.File;
import java.io.InputStream;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class Graph {

   private static final int DEFAULT_DISTANCE = 160;
   private static final int DEFAULT_GRAVITY = 15;
   private static final int DEFAULT_GRAPHSIZE = 900;
   private static final int DEFAULT_FONTSIZE = 90;
   private static final int CLASS_SIZE = 20;
   private static final int MIN_WIDTH = 400;
   private static final int MIN_HEIGHT = 400;
   private int countClasses;
   private int width;
   private int height;
   private int distance;
   private int gravity;
   private int graphSize;
   private int fontsize;
   private File htmlFile;
   private InputStream htmlTemplateIS;
   private File jsonFile;
   private String name;
   private String title;
   private DependencyFilter dependencyFilter;

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

   public int getDistance() {
      return distance;
   }

   public String getDistanceString() {
      return Integer.toString(distance);
   }

   public void setDistance(int distance) {
      this.distance = distance;
   }

   public int getGravity() {
      return gravity;
   }

   public String getGravityString() {
      return Integer.toString(gravity);
   }

   public void setGravity(int gravity) {
      this.gravity = gravity;
   }

   public int getGraphSize() {
      return graphSize;
   }

   public String getGraphSizeString() {
      return Integer.toString(graphSize);
   }

   public void setGraphSize(int graphSize) {
      this.graphSize = graphSize;
   }

   public int getFontsize() {
      return fontsize;
   }

   public String getFontsizeString() {
      return Integer.toString(fontsize);
   }

   public void setFontsize(int fontsize) {
      this.fontsize = fontsize;
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

   public DependencyFilter getDependencyFilter() {
      return dependencyFilter;
   }

   public void setDependencyFilter(DependencyFilter dependencyFilter) {
      this.dependencyFilter = dependencyFilter;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void calculateAttributes() {
      if (getDistance() == 0) {
         setDistance(DEFAULT_DISTANCE);
      }
      if (getGravity() == 0) {
         setGravity(DEFAULT_GRAVITY);
      }
      if (getGraphSize() == 0) {
         setGraphSize(DEFAULT_GRAPHSIZE);
      }
      if (getFontsize() == 0) {
         setFontsize(DEFAULT_FONTSIZE);
      }

      setWidth(countClasses * CLASS_SIZE);
      setHeight(countClasses * CLASS_SIZE);

      if (getWidth() < MIN_WIDTH) {
         setWidth(MIN_WIDTH);
      }
      if (getHeight() < MIN_HEIGHT) {
         setHeight(MIN_HEIGHT);
      }
   }
}

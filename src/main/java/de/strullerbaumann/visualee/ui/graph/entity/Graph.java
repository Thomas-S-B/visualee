package de.strullerbaumann.visualee.ui.graph.entity;

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
import de.strullerbaumann.visualee.dependency.boundary.DependencyFilter;
import java.io.File;

/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de)
 */
public class Graph {

   private int distance;
   private int gravity;
   private int graphWidth;
   private int graphHeight;
   private int fontsize;
   private File htmlFile;
   private File jsonFile;
   private String name;
   private String title;
   private DependencyFilter dependencyFilter;

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

   public int getGraphWidth() {
      return graphWidth;
   }

   public String getGraphWidthString() {
      return Integer.toString(graphWidth);
   }

   public void setGraphWidth(int graphWidth) {
      this.graphWidth = graphWidth;
   }

   public int getGraphHeight() {
      return graphHeight;
   }

   public String getGraphHeightString() {
      return Integer.toString(graphHeight);
   }

   public void setGraphHeight(int graphHeight) {
      this.graphHeight = graphHeight;
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
}

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
/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de)
 */
public class GraphConfig {

   private int distance;
   private int gravity;
   private int graphwidth;
   private int graphheight;
   private int fontsize;
   private String name;

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

   public int getGraphwidth() {
      return graphwidth;
   }

   public String getGraphwidthString() {
      return Integer.toString(graphwidth);
   }

   public void setGraphwidth(int graphwidth) {
      this.graphwidth = graphwidth;
   }

   public int getGraphheight() {
      return graphheight;
   }

   public String getGraphheightString() {
      return Integer.toString(graphheight);
   }

   public void setGraphheight(int graphheight) {
      this.graphheight = graphheight;
   }

   public int getFontsize() {
      return fontsize;
   }

   public void setFontsize(int fontsize) {
      this.fontsize = fontsize;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}

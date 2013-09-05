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
package de.strullerbaumann.visualee.maven;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class GraphMojo {

   private int distance;
   private int gravity;
   private int graphsize;
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

   public int getGraphsize() {
      return graphsize;
   }

   public String getGraphsizeString() {
      return Integer.toString(graphsize);
   }

   public void setGraphsize(int graphSize) {
      this.graphsize = graphSize;
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

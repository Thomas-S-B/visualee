package de.strullerbaumann.visualee.ui.graph.boundary;

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
import de.strullerbaumann.visualee.ui.graph.entity.Graph;
import de.strullerbaumann.visualee.ui.graph.entity.GraphConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de)
 */
public final class GraphConfigurator {

   private static final String DEFAULT_GRAPHNAME = "default";
   private static final int DEFAULT_DISTANCE = 160;
   private static final int DEFAULT_GRAVITY = 15;
   private static final int DEFAULT_GRAPH_WIDTH = 900;
   private static final int DEFAULT_GRAPH_HEIGHT = 800;
   private static final int DEFAULT_FONTSIZE = 90;
   private static final Map<String, GraphConfig> GRAPHCONFIGS = new HashMap<>();

   private GraphConfigurator() {
   }

   public static void setGraphConfigs(List<GraphConfig> graphConfigsList) {
      GRAPHCONFIGS.clear();
      for (GraphConfig graphConfig : graphConfigsList) {
         GRAPHCONFIGS.put(graphConfig.getName(), graphConfig);
      }
   }

   public static void configGraph(Graph graph) {
      // First set Attributes with defaults
      graph.setDistance(DEFAULT_DISTANCE);
      graph.setGravity(DEFAULT_GRAVITY);
      graph.setGraphWidth(DEFAULT_GRAPH_WIDTH);
      graph.setGraphHeight(DEFAULT_GRAPH_HEIGHT);
      graph.setFontsize(DEFAULT_FONTSIZE);
      // Is there a default configuration?
      GraphConfig graphConfig = GRAPHCONFIGS.get(DEFAULT_GRAPHNAME);
      setGraphConfig(graph, graphConfig);
      // Is there a indiviual configuration?
      graphConfig = GRAPHCONFIGS.get(graph.getName());
      setGraphConfig(graph, graphConfig);
   }

   public static void setGraphConfig(Graph graph, GraphConfig graphConfig) {
      if (graphConfig != null) {
         if (graphConfig.getDistance() > 0) {
            graph.setDistance(graphConfig.getDistance());
         }
         if (graphConfig.getGravity() > 0) {
            graph.setGravity(graphConfig.getGravity());
         }
         if (graphConfig.getGraphwidth() > 0) {
            graph.setGraphWidth(graphConfig.getGraphwidth());
         }
         if (graphConfig.getGraphheight() > 0) {
            graph.setGraphHeight(graphConfig.getGraphheight());
         }
         if (graphConfig.getFontsize() > 0) {
            graph.setFontsize(graphConfig.getFontsize());
         }
      }
   }
}

/*
 * Created on 13.09.2013 - 10:43:29
 *
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.ui.graph.boundary;

import de.strullerbaumann.visualee.ui.graph.entity.Graph;
import de.strullerbaumann.visualee.ui.graph.entity.GraphConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public final class GraphConfigurator {

   private static final String DEFAULT_GRAPHNAME = "default";
   private static final int DEFAULT_DISTANCE = 200;
   private static final int DEFAULT_LINKDISTANCE = 160;
   private static final int DEFAULT_GRAVITY = 15;
   private static final int DEFAULT_GRAPHSIZE = 900;
   private static final int DEFAULT_FONTSIZE = 90;
   private static Map<String, GraphConfig> graphConfigs = new HashMap<>();

   private GraphConfigurator() {
   }

   public static void setGraphConfigs(List<GraphConfig> graphConfigsList) {
      graphConfigs.clear();
      for (GraphConfig graphConfig : graphConfigsList) {
         graphConfigs.put(graphConfig.getName(), graphConfig);
      }
   }

   public static void configGraph(Graph graph) {
      // First set Attributes with defaults
      graph.setDistance(DEFAULT_DISTANCE);
      graph.setLinkdistance(DEFAULT_LINKDISTANCE);
      graph.setGravity(DEFAULT_GRAVITY);
      graph.setGraphSize(DEFAULT_GRAPHSIZE);
      graph.setFontsize(DEFAULT_FONTSIZE);
      // Is there a default configuration?
      GraphConfig graphConfig = graphConfigs.get(DEFAULT_GRAPHNAME);
      setGraphConfig(graph, graphConfig);
      // Is there a indiviual configuration?
      graphConfig = graphConfigs.get(graph.getName());
      setGraphConfig(graph, graphConfig);
   }

   public static void setGraphConfig(Graph graph, GraphConfig graphConfig) {
      if (graphConfig != null) {
         if (graphConfig.getDistance() > 0) {
            graph.setDistance(graphConfig.getDistance());
         }
         if (graphConfig.getLinkdistance() > 0) {
            graph.setLinkdistance(graphConfig.getLinkdistance());
         }
         if (graphConfig.getGravity() > 0) {
            graph.setGravity(graphConfig.getGravity());
         }
         if (graphConfig.getGraphsize() > 0) {
            graph.setGraphSize(graphConfig.getGraphsize());
         }
         if (graphConfig.getFontsize() > 0) {
            graph.setFontsize(graphConfig.getFontsize());
         }
      }
   }
}

/*
 * Created on 11.04.2013 - 09:34:42 
 * 
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.resources;

import de.strullerbaumann.visualee.cdi.CDIType;
import de.strullerbaumann.visualee.cdi.CDIDependency;
import static de.strullerbaumann.visualee.cdi.CDIType.EJB;
import static de.strullerbaumann.visualee.cdi.CDIType.EVENT;
import static de.strullerbaumann.visualee.cdi.CDIType.INJECT;
import static de.strullerbaumann.visualee.cdi.CDIType.INSTANCE;
import static de.strullerbaumann.visualee.cdi.CDIType.OBSERVES;
import static de.strullerbaumann.visualee.cdi.CDIType.PRODUCES;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class JsonHelper {

   public static String getJSONNode(String name, int group, String description, int id) {
      StringBuilder nodeJSON = new StringBuilder();
      nodeJSON.append("    {").append(System.lineSeparator());
      nodeJSON.append("        \"name\": \"").append(name).append("\",").append(System.lineSeparator());
      nodeJSON.append("        \"group\": ").append(group).append(",").append(System.lineSeparator());
      nodeJSON.append("        \"description\": ").append(description).append(",").append(System.lineSeparator());
      nodeJSON.append("        \"id\": ").append(id).append(System.lineSeparator());
      nodeJSON.append("    }").append(System.lineSeparator());
      return nodeJSON.toString();
   }

   public static String getJSONLink(int source, int target, int value, CDIType cdiType) {
      StringBuilder linkJSON = new StringBuilder();
      linkJSON.append("    {").append(System.lineSeparator());
      if (cdiType == CDIType.EVENT || cdiType == CDIType.PRODUCES) {
         // Bei Events und Produces gerade andersrum, s. d. der Pfeil zum Observer bzw. zur Produces Klasse gehen soll
         linkJSON.append("        \"source\": ").append(target).append(",").append(System.lineSeparator());
         linkJSON.append("        \"target\": ").append(source).append(",").append(System.lineSeparator());
      } else {
         linkJSON.append("        \"source\": ").append(source).append(",").append(System.lineSeparator());
         linkJSON.append("        \"target\": ").append(target).append(",").append(System.lineSeparator());
      }
      linkJSON.append("        \"value\": ").append(value).append(",").append(System.lineSeparator());
      linkJSON.append("        \"type\": ");
      switch (cdiType) {
         case EJB:
            linkJSON.append("\"ejb\"").append(System.lineSeparator());
            break;
         case EVENT:
            linkJSON.append("\"event\"").append(System.lineSeparator());
            break;
         case INJECT:
            linkJSON.append("\"inject\"").append(System.lineSeparator());
            break;
         case OBSERVES:
            linkJSON.append("\"observes\"").append(System.lineSeparator());
            break;
         case PRODUCES:
            linkJSON.append("\"produces\"").append(System.lineSeparator());
            break;
         case INSTANCE:
            linkJSON.append("\"instance\"").append(System.lineSeparator());
            break;
      }
      linkJSON.append("    }").append(System.lineSeparator());
      return linkJSON.toString();
   }

   public static String generateDescription(JavaFile myJavaClass) {
      StringBuilder description = new StringBuilder();
      description.append("\"");
      description.append("<br/><nobr>Package: ");
      description.append(myJavaClass.getPackagePath());
      description.append("</nobr><br/>");
      for (CDIDependency dependency : myJavaClass.getInjected()) {
         switch (dependency.getCdiType()) {
            case EJB:
               description.append("<br/><nobr>");
               description.append(dependency.getMyJavaFileTo());
               description.append(" is injected as an EJB");
               description.append("</nobr>");
               break;
            case EVENT:
               description.append("<br/><nobr>");
               description.append("Fires ");
               description.append(dependency.getMyJavaFileTo());
               description.append(" as an event");
               description.append("</nobr>");
               break;
            case INJECT:
               description.append("<br/><nobr>");
               description.append(dependency.getMyJavaFileTo());
               description.append(" is injected");
               description.append("</nobr>");
               break;
            case OBSERVES:
               description.append("<br/><nobr>");
               description.append("Observes ");
               description.append(dependency.getMyJavaFileTo());
               description.append(" events");
               description.append("</nobr>");
               break;
            case PRODUCES:
               description.append("<br/><nobr>");
               description.append("Produces ");
               description.append(dependency.getMyJavaFileTo());
               description.append("</nobr>");
               break;
            case INSTANCE:
               description.append("<br/><nobr>");
               description.append(dependency.getMyJavaFileTo());
               description.append(" is injected as an instance");
               description.append("</nobr>");
               break;
            default:
               description.append("<br/><nobr>");
               description.append(dependency.getMyJavaFileTo());
               description.append(" (");
               description.append(dependency.getCdiType());
               description.append(")");
               description.append("</nobr>");
               break;
         }
      }
      description.append("\"");

      return description.toString();
   }
}

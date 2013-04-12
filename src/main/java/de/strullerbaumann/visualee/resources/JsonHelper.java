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

   public static String getJSONNode(String name, int group, String description, String sourcecode, int id) {
      StringBuilder nodeJSON = new StringBuilder();
      nodeJSON.append("    {").append(System.lineSeparator());
      nodeJSON.append("        \"name\": \"").append(name).append("\",").append(System.lineSeparator());
      nodeJSON.append("        \"group\": ").append(group).append(",").append(System.lineSeparator());
      nodeJSON.append("        \"description\": ").append(description).append(",").append(System.lineSeparator());
      nodeJSON.append("        \"sourcecode\": ").append(sourcecode).append(",").append(System.lineSeparator());
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
      description.append("<nobr>Package: ");
      description.append(myJavaClass.getPackagePath());
      description.append("</nobr>");

      StringBuilder descriptionEJB = new StringBuilder();
      StringBuilder descriptionEvent = new StringBuilder();
      StringBuilder descriptionInject = new StringBuilder();
      StringBuilder descriptionObserves = new StringBuilder();
      StringBuilder descriptionProduces = new StringBuilder();
      StringBuilder descriptionInstance = new StringBuilder();
      StringBuilder descriptionDefault = new StringBuilder();

      for (CDIDependency dependency : myJavaClass.getInjected()) {
         switch (dependency.getCdiType()) {
            case EJB:
               descriptionEJB.append("<br/><nobr>");
               descriptionEJB.append(dependency.getMyJavaFileTo());
               descriptionEJB.append(" is injected as an EJB");
               descriptionEJB.append("</nobr>");
               break;
            case EVENT:
               descriptionEvent.append("<br/><nobr>");
               descriptionEvent.append("Fires ");
               descriptionEvent.append(dependency.getMyJavaFileTo());
               descriptionEvent.append(" as an event");
               descriptionEvent.append("</nobr>");
               break;
            case INJECT:
               descriptionInject.append("<br/><nobr>");
               descriptionInject.append(dependency.getMyJavaFileTo());
               descriptionInject.append(" is injected");
               descriptionInject.append("</nobr>");
               break;
            case OBSERVES:
               descriptionObserves.append("<br/><nobr>");
               descriptionObserves.append("Observes ");
               descriptionObserves.append(dependency.getMyJavaFileTo());
               descriptionObserves.append(" events");
               descriptionObserves.append("</nobr>");
               break;
            case PRODUCES:
               descriptionProduces.append("<br/><nobr>");
               descriptionProduces.append("Produces ");
               descriptionProduces.append(dependency.getMyJavaFileTo());
               descriptionProduces.append("</nobr>");
               break;
            case INSTANCE:
               descriptionInstance.append("<br/><nobr>");
               descriptionInstance.append(dependency.getMyJavaFileTo());
               descriptionInstance.append(" is injected as an instance");
               descriptionInstance.append("</nobr>");
               break;
            default:
               descriptionDefault.append("<br/><nobr>");
               descriptionDefault.append(dependency.getMyJavaFileTo());
               descriptionDefault.append(" (");
               descriptionDefault.append(dependency.getCdiType());
               descriptionDefault.append(")");
               descriptionDefault.append("</nobr>");
               break;
         }
      }

      if (descriptionEJB.length() > 0) {
         description.append("<br/><br/>");
         description.append("EJB:");
         description.append(descriptionEJB);
         description.append("</nobr>");
      }
      if (descriptionEvent.length() > 0) {
         description.append("<br/><br/>");
         description.append("Events:");
         description.append(descriptionEvent);
         description.append("</nobr>");
      }
      if (descriptionInject.length() > 0) {
         description.append("<br/><br/>");
         description.append("Injects:");
         description.append(descriptionInject);
         description.append("</nobr>");
      }
      if (descriptionObserves.length() > 0) {
         description.append("<br/><br/>");
         description.append("Observes:");
         description.append(descriptionObserves);
         description.append("</nobr>");
      }
      if (descriptionProduces.length() > 0) {
         description.append("<br/><br/>");
         description.append("Produces:");
         description.append(descriptionProduces);
         description.append("</nobr>");
      }
      if (descriptionInstance.length() > 0) {
         description.append("<br/><br/>");
         description.append("Instances:");
         description.append(descriptionInstance);
         description.append("</nobr>");
      }
      if (descriptionDefault.length() > 0) {
         description.append("<br/><br/>");
         description.append(descriptionDefault);
         description.append("</nobr>");
      }

      description.append("\"");

      return description.toString();
   }

   public static String escapeStringToJson(String input) {
      // &lt; and &gt; are important, e.g. a sourcecode like "List<Scripts> ..." causes problems with the javascript in the ui
      return input = input.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n").replace("<", "&lt;").replace(">", "&gt;");
   }

   public static String generateSourcecode(JavaFile myJavaClass) {
      StringBuilder sourcecodeJSON = new StringBuilder();
      sourcecodeJSON.append("\"");
      sourcecodeJSON.append(escapeStringToJson(myJavaClass.getSourceCode()));
      sourcecodeJSON.append("\"");
      return sourcecodeJSON.toString();
   }
}

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

import de.strullerbaumann.visualee.dependency.DependenciyType;
import static de.strullerbaumann.visualee.dependency.DependenciyType.EJB;
import static de.strullerbaumann.visualee.dependency.DependenciyType.EVENT;
import static de.strullerbaumann.visualee.dependency.DependenciyType.INJECT;
import static de.strullerbaumann.visualee.dependency.DependenciyType.INSTANCE;
import static de.strullerbaumann.visualee.dependency.DependenciyType.OBSERVES;
import static de.strullerbaumann.visualee.dependency.DependenciyType.PRODUCES;
import de.strullerbaumann.visualee.dependency.Dependency;
import de.strullerbaumann.visualee.resources.JavaSource;
import java.util.HashMap;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public final class Description {

   private Description() {
   }

   public static String generateDescription(JavaSource javaSource) {
      StringBuilder description = new StringBuilder();

      description.append("<nobr>Package: ")
              .append(javaSource.getPackagePath())
              .append("</nobr>");

      HashMap<DependenciyType, StringBuilder> descriptionParts = new HashMap<>();
      for (DependenciyType type : DependenciyType.values()) {
         descriptionParts.put(type, new StringBuilder());
      }

      for (Dependency dependency : javaSource.getInjected()) {
         StringBuilder descriptionPart = descriptionParts.get(dependency.getDependencyType());
         assert descriptionPart != null : "Unknown DependencyType";

         descriptionPart.append("<br/><nobr>");
         switch (dependency.getDependencyType()) {
            case EJB:
               descriptionPart.append(dependency.getJavaSourceTo())
                       .append(" is injected as an EJB");
               break;
            case EVENT:
               descriptionPart.append("Fires ")
                       .append(dependency.getJavaSourceTo())
                       .append(" as an event");
               break;
            case INJECT:
               descriptionPart.append(dependency.getJavaSourceTo())
                       .append(" is injected");
               break;
            case OBSERVES:
               descriptionPart.append("Observes ")
                       .append(dependency.getJavaSourceTo())
                       .append(" events");
               break;
            case PRODUCES:
               descriptionPart.append("Produces ")
                       .append(dependency.getJavaSourceTo());
               break;
            case INSTANCE:
               descriptionPart.append(dependency.getJavaSourceTo())
                       .append(" is injected as an instance");
               break;
            case ONE_TO_ONE:
               descriptionPart
                       .append("One to one relation to ")
                       .append(dependency.getJavaSourceTo());
               break;
            case ONE_TO_MANY:
               descriptionPart
                       .append("One to many relation to ")
                       .append(dependency.getJavaSourceTo());
               break;
            case MANY_TO_ONE:
               descriptionPart
                       .append("Many to one relation to ")
                       .append(dependency.getJavaSourceTo());
               break;
            case MANY_TO_MANY:
               descriptionPart
                       .append("Many to many relation to ")
                       .append(dependency.getJavaSourceTo());
               break;
         }
         descriptionPart.append("</nobr>");
      }

      for (DependenciyType type : descriptionParts.keySet()) {
         description.append(getDescriptionPart(getDescriptionHeader(type), descriptionParts.get(type).toString()));
      }

      return description.toString();
   }

   protected static String getDescriptionHeader(DependenciyType type) {
      switch (type) {
         case EJB:
            return "EJB:";
         case EVENT:
            return "Events:";
         case INJECT:
            return "Injects:";
         case OBSERVES:
            return "Observes:";
         case PRODUCES:
            return "Produces:";
         case INSTANCE:
            return "Instances:";
         case ONE_TO_ONE:
            return "One to one relations:";
         case ONE_TO_MANY:
            return "One to many relations:";
         case MANY_TO_ONE:
            return "Many to one relations:";
         case MANY_TO_MANY:
            return "Many to many relations:";
      }

      return null;
   }

   protected static String getDescriptionPart(String header, String description) {
      StringBuilder descriptionPart = new StringBuilder();
      if (description.length() > 0) {
         descriptionPart.append("<br/><br/>");
         if (header != null) {
            descriptionPart.append(header);
         }
         descriptionPart.append(description);
         descriptionPart.append("</nobr>");
      }
      return descriptionPart.toString();
   }
}

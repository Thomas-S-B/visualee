package de.strullerbaumann.visualee.ui.graph.control;

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
import de.strullerbaumann.visualee.dependency.boundary.DependencyContainer;
import de.strullerbaumann.visualee.dependency.entity.Dependency;
import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import static de.strullerbaumann.visualee.dependency.entity.DependencyType.EJB;
import static de.strullerbaumann.visualee.dependency.entity.DependencyType.EVENT;
import static de.strullerbaumann.visualee.dependency.entity.DependencyType.INJECT;
import static de.strullerbaumann.visualee.dependency.entity.DependencyType.INSTANCE;
import static de.strullerbaumann.visualee.dependency.entity.DependencyType.MANY_TO_MANY;
import static de.strullerbaumann.visualee.dependency.entity.DependencyType.MANY_TO_ONE;
import static de.strullerbaumann.visualee.dependency.entity.DependencyType.OBSERVES;
import static de.strullerbaumann.visualee.dependency.entity.DependencyType.ONE_TO_MANY;
import static de.strullerbaumann.visualee.dependency.entity.DependencyType.ONE_TO_ONE;
import static de.strullerbaumann.visualee.dependency.entity.DependencyType.PRODUCES;
import static de.strullerbaumann.visualee.dependency.entity.DependencyType.RESOURCE;
import de.strullerbaumann.visualee.source.entity.JavaSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de)
 */
public final class Description {

   private static final Map<DependencyType, String> HEADERS = Collections.unmodifiableMap(new HashMap<DependencyType, String>() {
      {
         put(EJB, "EJB:");
         put(EVENT, "Events:");
         put(INJECT, "Injects:");
         put(OBSERVES, "Observes:");
         put(PRODUCES, "Produces:");
         put(INSTANCE, "Instances:");
         put(RESOURCE, "Resources:");
         put(ONE_TO_ONE, "One to one relations:");
         put(ONE_TO_MANY, "One to many relations:");
         put(MANY_TO_ONE, "Many to one relations:");
         put(MANY_TO_MANY, "Many to many relations:");
      }
   });
   private static final String JAVASOURCE_TO = "!JAVASOURCE_TO!";
   private static final Map<DependencyType, String> DESCRIPTIONS = Collections.unmodifiableMap(new HashMap<DependencyType, String>() {
      {
         put(EJB, JAVASOURCE_TO + " is injected as an EJB");
         put(EVENT, "Fires " + JAVASOURCE_TO + " as an event");
         put(INJECT, JAVASOURCE_TO + " is injected");
         put(OBSERVES, "Observes " + JAVASOURCE_TO + " events");
         put(PRODUCES, "Produces " + JAVASOURCE_TO);
         put(INSTANCE, JAVASOURCE_TO + " is injected as an instance");
         put(RESOURCE, JAVASOURCE_TO + " is injected as a resource");
         put(ONE_TO_ONE, "One to one relation to " + JAVASOURCE_TO);
         put(ONE_TO_MANY, "One to many relation to " + JAVASOURCE_TO);
         put(MANY_TO_ONE, "Many to one relation to " + JAVASOURCE_TO);
         put(MANY_TO_MANY, "Many to many relation to " + JAVASOURCE_TO);
      }
   });

   private Description() {
   }

   public static String generateDescription(JavaSource javaSource) {
      StringBuilder description = new StringBuilder();

      description.append("<nobr>Package: ")
              .append(javaSource.getPackagePath())
              .append("</nobr>");

      HashMap<DependencyType, StringBuilder> descriptionParts = new HashMap<>();
      for (DependencyType type : DependencyType.values()) {
         descriptionParts.put(type, new StringBuilder());
      }

      for (Dependency dependency : DependencyContainer.getInstance().getDependencies(javaSource)) {
         StringBuilder descriptionPart = descriptionParts.get(dependency.getDependencyType());
         assert descriptionPart != null : "Unknown DependencyType";
         descriptionPart.append("<br/><nobr>");
         String desc = DESCRIPTIONS.get(dependency.getDependencyType());
         desc = desc.replaceAll(JAVASOURCE_TO, dependency.getJavaSourceTo().toString());
         descriptionPart.append(desc);
         descriptionPart.append("</nobr>");
      }

      for (DependencyType type : descriptionParts.keySet()) {
         description.append(getDescriptionPart(type, descriptionParts.get(type).toString()));
      }

      return description.toString();
   }

   static String getDescriptionHeader(DependencyType type) {
      return HEADERS.get(type);
   }

   static String getDescriptionPart(DependencyType type, String description) {

      String header = getDescriptionHeader(type);
      StringBuilder descriptionPart = new StringBuilder();
      if (description.length() > 0) {
         descriptionPart.append("<br/><br/>");
         if (header != null) {
            descriptionPart.append(header);
         }
         descriptionPart.append(description);
      }
      return descriptionPart.toString();
   }
}

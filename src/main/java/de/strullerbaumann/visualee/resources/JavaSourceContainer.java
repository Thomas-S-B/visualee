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
package de.strullerbaumann.visualee.resources;

import de.strullerbaumann.visualee.dependency.Dependency;
import de.strullerbaumann.visualee.dependency.DependencyFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public final class JavaSourceContainer {

   private static final Map<String, JavaSource> javaSources = new ConcurrentHashMap<>();

   private static class JavaSourceContainerHolder {

      private static final JavaSourceContainer INSTANCE = new JavaSourceContainer();
   }

   private JavaSourceContainer() {
   }

   public static JavaSourceContainer getInstance() {
      return JavaSourceContainer.JavaSourceContainerHolder.INSTANCE;
   }

   public Collection<JavaSource> getJavaSources() {
      return javaSources.values();
   }

   public void clear() {
      javaSources.clear();
   }

   public void add(JavaSource javaSource) {
      if (javaSource == null) {
         return;
      }
      if (!javaSources.containsKey(javaSource.getName())) {
         javaSources.put(javaSource.getName(), javaSource);
      }
   }

   public JavaSource getJavaSourceByName(String n) {
      return javaSources.get(n);
   }

   public List<JavaSource> getRelevantClasses() {
      return getRelevantClasses(null);
   }

   public List<JavaSource> getRelevantClasses(DependencyFilter filter) {
      List<JavaSource> relevantClasses = new ArrayList<>();
      for (JavaSource javaSource : getJavaSources()) {
         if (javaSource.getInjected().size() > 0) {
            for (Dependency dependency : javaSource.getInjected()) {
               if (filter == null || filter.contains(dependency.getDependencyType())) {
                  relevantClasses.add(dependency.getJavaSourceFrom());
                  relevantClasses.add(dependency.getJavaSourceTo());
               }
            }
         }
      }

      return relevantClasses;
   }
}

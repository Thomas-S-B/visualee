package de.strullerbaumann.visualee.dependency.boundary;

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
import de.strullerbaumann.visualee.dependency.entity.Dependency;
import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.source.entity.JavaSource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de>)
 */
public final class DependencyContainer {

   private static final List<Dependency> DEPENDENCIES = new ArrayList<>();

   private static class DependencyContainerHolder {

      private static final DependencyContainer INSTANCE = new DependencyContainer();
   }

   private DependencyContainer() {
   }

   public static DependencyContainer getInstance() {
      return DependencyContainer.DependencyContainerHolder.INSTANCE;
   }

   public void clear() {
      DEPENDENCIES.clear();
   }

   public void add(Dependency dependency) {
      DEPENDENCIES.add(dependency);
   }

   public void addAll(List<Dependency> addDependencies) {
      DEPENDENCIES.addAll(addDependencies);
   }

   public List<Dependency> getDependenciesOfType(DependencyType dependencyType) {
      List<Dependency> dependenciesOfType = new ArrayList<>();
      for (Dependency dependency : DEPENDENCIES) {
         if (dependency.getDependencyType().equals(dependencyType)) {
            dependenciesOfType.add(dependency);
         }
      }
      return dependenciesOfType;
   }

   //TODO simplify
   public Set<JavaSource> getFilteredJavaSources(DependencyFilter filter) {
      Set<JavaSource> filteredJavaSources = new HashSet<>();
      if (filter != null && filter.isDirectlyConnected()) {
         DependencyType primaryType = filter.getFilterTypes().get(0);
         for (Dependency d : getDependenciesOfType(primaryType)) {
            JavaSource to = d.getJavaSourceTo();
            List<Dependency> injects = new ArrayList<>();
            for (DependencyType dFilter : filter.getFilterTypes()) {
               if (!dFilter.equals(primaryType)) {
                  injects.addAll(findAllDependenciesWith(to, dFilter));
               }
            }
            if (injects.size() > 0) {
               filteredJavaSources.add(d.getJavaSourceFrom());
               filteredJavaSources.add(to);
               for (Dependency inject : injects) {
                  filteredJavaSources.add(inject.getJavaSourceFrom());
               }
            }
         }
      } else {
         for (Dependency dependency : DEPENDENCIES) {
            if (filter == null || filter.contains(dependency.getDependencyType())) {
               filteredJavaSources.add(dependency.getJavaSourceFrom());
               filteredJavaSources.add(dependency.getJavaSourceTo());
            }
         }
      }
      return filteredJavaSources;
   }

   Set<Dependency> findAllDependenciesWith(JavaSource javaSource, DependencyType dependencyType) {
      Set<Dependency> foundDependencies = new HashSet<>();
      for (Dependency dependency : getDependenciesOfType(dependencyType)) {
         if (dependency.getJavaSourceFrom().equals(javaSource) || dependency.getJavaSourceTo().equals(javaSource)) {
            foundDependencies.add(dependency);
         }
      }
      return foundDependencies;
   }

   public List<Dependency> getDependencies(JavaSource javaSource) {
      List<Dependency> foundDependencies = new ArrayList<>();
      for (Dependency d : DEPENDENCIES) {
         if (d.getJavaSourceFrom().equals(javaSource)) {
            foundDependencies.add(d);
         }
      }
      return foundDependencies;
   }
}

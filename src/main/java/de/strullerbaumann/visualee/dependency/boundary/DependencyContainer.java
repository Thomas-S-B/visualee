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
import de.strullerbaumann.visualee.logging.LogProvider;
import de.strullerbaumann.visualee.source.entity.JavaSource;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public final class DependencyContainer {

   private static List<Dependency> dependencies = new ArrayList<>();

   private static class DependencyContainerHolder {

      private static final DependencyContainer INSTANCE = new DependencyContainer();
   }

   private DependencyContainer() {
   }

   public static DependencyContainer getInstance() {
      return DependencyContainer.DependencyContainerHolder.INSTANCE;
   }

   /*
    public List<Dependency> getDependencies() {
    return dependencies;
    }
    */
   public void clear() {
      dependencies.clear();
   }

   public void add(Dependency dependency) {
      dependencies.add(dependency);
   }

   public void addAll(List<Dependency> addDependencies) {
      dependencies.addAll(addDependencies);
   }

   // TODO UnitTest
   private List<Dependency> findAllDependenciesWith(JavaSource javaSource, DependencyType dependencyType) {
      List<Dependency> foundDependencies = new ArrayList<>();
      for (Dependency dependency : getDependenciesOfType(dependencyType)) {
         if (dependency.getJavaSourceFrom().equals(javaSource) || dependency.getJavaSourceTo().equals(javaSource)) {
            //TODO dies als SET stat mit List damit man sich das contains spart?
            if (!foundDependencies.contains(dependency)) {
               foundDependencies.add(dependency);
            }
         }
      }

      return foundDependencies;
   }

   public List<Dependency> getDependencies(DependencyFilter filter) {
      List<Dependency> dependenciesFilter = new ArrayList<>();
      if (filter == null) {
         dependenciesFilter.addAll(dependencies);
      } else {
         for (DependencyType type : filter.getFilterTypes()) {
            for (Dependency d : getDependenciesOfType(type)) {
               if (!dependenciesFilter.contains(d)) {
                  dependenciesFilter.add(d);
               }
            }
         }
      }

      return dependenciesFilter;
   }

   public List<Dependency> getDependenciesOfType(DependencyType dependencyType) {
      List<Dependency> dependenciesOfType = new ArrayList<>();
      for (Dependency dependency : dependencies) {
         if (dependency.getDependencyType().equals(dependencyType)) {
            dependenciesOfType.add(dependency);
         }
      }
      return dependenciesOfType;
   }

   public List<JavaSource> getRelevantClasses() {
      return getRelevantClasses(null);
   }

   // TODO besser machen
   public List<JavaSource> getRelevantClasses(DependencyFilter filter) {
      List<JavaSource> relevantClasses = new ArrayList<>();

      if (filter != null && filter.isDirectlyConnected()) {
         LogProvider.getInstance().info("Found Dependencies: " + getDependenciesOfType(filter.getFilterTypes().get(0)));
         for (Dependency d : getDependenciesOfType(filter.getFilterTypes().get(0))) {
            LogProvider.getInstance().info("+++++++++ examinig dependency: " + d);
            //LogProvider.getInstance().info("Dependency: " + d);
            JavaSource from = d.getJavaSourceFrom();
            JavaSource to = d.getJavaSourceTo();
            List<Dependency> injects = new ArrayList<>();
            for (DependencyType dFilter : filter.getFilterTypes()) {
               if (!dFilter.equals(filter.getFilterTypes().get(0))) {
                  injects.addAll(findAllDependenciesWith(to, dFilter));
               }
            }
            LogProvider.getInstance().info("founded " + injects + " for " + to);
            if (injects.size() > 0) {
               if (!relevantClasses.contains(from)) {
                  relevantClasses.add(from);
                  LogProvider.getInstance().info("## Added1: " + from);
               }
               if (!relevantClasses.contains(to)) {
                  relevantClasses.add(to);
                  LogProvider.getInstance().info("## Added2: " + to);
               }
               for (Dependency inject : injects) {
                  LogProvider.getInstance().info("Inject Dependency: " + inject);
                  //if (!relevantClasses.contains(inject.getJavaSourceFrom())) {
                  relevantClasses.add(inject.getJavaSourceFrom());
                  LogProvider.getInstance().info("## Added3: " + inject.getJavaSourceFrom());
                  //}
               }
            }
         }
      } else {
         for (Dependency dependency : dependencies) {
            if (filter == null || filter.contains(dependency.getDependencyType())) {
               if (!relevantClasses.contains(dependency.getJavaSourceFrom())) {
                  relevantClasses.add(dependency.getJavaSourceFrom());
               }
               if (!relevantClasses.contains(dependency.getJavaSourceTo())) {
                  relevantClasses.add(dependency.getJavaSourceTo());
               }
            }
         }
      }
      return relevantClasses;
   }

   public List<Dependency> getDependencies(JavaSource javaSource) {
      List<Dependency> foundDependencies = new ArrayList<>();
      for (Dependency d : dependencies) {
         if (d.getJavaSourceFrom().equals(javaSource)) {
            foundDependencies.add(d);
         }
      }
      return foundDependencies;
   }
}

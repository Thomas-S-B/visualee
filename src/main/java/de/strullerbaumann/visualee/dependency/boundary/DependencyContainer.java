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
    public static List<Dependency> getDependencies() {
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
   private List<Dependency> findAllDependenciesWith(JavaSource javaSource2Find, DependencyType dependencyType) {
      List<Dependency> foundDependencies = new ArrayList<>();
      for (Dependency dependency : getDependenciesOfType(dependencyType)) {
         if (dependency.getJavaSourceFrom() == javaSource2Find || dependency.getJavaSourceTo() == javaSource2Find) {
            //TODO dies als SET stat mit List damit man sich das contains spart?
            if (!foundDependencies.contains(dependency)) {
               foundDependencies.add(dependency);
            }
         }
      }

      return foundDependencies;
   }

   public List<Dependency> getDependenciesOfType(DependencyType dependencyType) {
      List<Dependency> dependenciesOfType = new ArrayList<>();
      for (Dependency dependency : dependencies) {
         if (dependency.getDependencyType() == dependencyType) {
            dependenciesOfType.add(dependency);
         }
      }
      return dependenciesOfType;
   }

   public List<JavaSource> getRelevantClasses() {
      return getRelevantClasses(null);
   }

   // TODO UnitTest mit directlyConnected
   public List<JavaSource> getRelevantClasses(DependencyFilter filter) {
      List<JavaSource> relevantClasses = new ArrayList<>();

      if (filter != null && filter.isDirectlyConnected()) {
         //todo
         List<JavaSource> producesClasses = getRelevantClasses(new DependencyFilter().addType(DependencyType.PRODUCES));
         for (JavaSource producesClass : producesClasses) {
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

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
package de.strullerbaumann.visualee.dependency;

import de.strullerbaumann.visualee.resources.JavaSource;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class Dependency {

   private DependenciyType dependencyType;
   private JavaSource javaSourceFrom;
   private JavaSource javaSourceTo;

   public Dependency() {
   }

   public Dependency(DependenciyType dependencyType, JavaSource javaSourceFrom, JavaSource javaSourceTo) {
      this.dependencyType = dependencyType;
      this.javaSourceFrom = javaSourceFrom;
      this.javaSourceTo = javaSourceTo;
   }

   public DependenciyType getDependencyType() {
      return dependencyType;
   }

   public void setDependencyType(DependenciyType dependencyType) {
      this.dependencyType = dependencyType;
   }

   public JavaSource getJavaSourceFrom() {
      return javaSourceFrom;
   }

   public void setJavaSourceFrom(JavaSource javaSourceFrom) {
      this.javaSourceFrom = javaSourceFrom;
   }

   public JavaSource getJavaSourceTo() {
      return javaSourceTo;
   }

   public void setJavaSourceTo(JavaSource javaSourceTo) {
      this.javaSourceTo = javaSourceTo;
   }
}

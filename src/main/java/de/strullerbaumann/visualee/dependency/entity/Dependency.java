package de.strullerbaumann.visualee.dependency.entity;

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
import de.strullerbaumann.visualee.source.entity.JavaSource;
import java.util.Objects;

/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de)
 */
public class Dependency {

   private static final int HASH = 7;
   private static final int HASH_MULTIPLIER = 53;
   private DependencyType dependencyType;
   private JavaSource javaSourceFrom;
   private JavaSource javaSourceTo;

   public Dependency() {
   }

   public Dependency(DependencyType dependencyType, JavaSource javaSourceFrom, JavaSource javaSourceTo) {
      this.dependencyType = dependencyType;
      this.javaSourceFrom = javaSourceFrom;
      this.javaSourceTo = javaSourceTo;
   }

   public DependencyType getDependencyType() {
      return dependencyType;
   }

   public void setDependencyType(DependencyType dependencyType) {
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

   @Override
   public int hashCode() {
      int hash = HASH;
      hash = HASH_MULTIPLIER * hash + Objects.hashCode(this.dependencyType);
      hash = HASH_MULTIPLIER * hash + Objects.hashCode(this.javaSourceFrom);
      hash = HASH_MULTIPLIER * hash + Objects.hashCode(this.javaSourceTo);
      return hash;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final Dependency other = (Dependency) obj;
      if (this.dependencyType != other.dependencyType) {
         return false;
      }
      if (!Objects.equals(this.javaSourceFrom, other.javaSourceFrom)) {
         return false;
      }
      return Objects.equals(this.javaSourceTo, other.javaSourceTo);
   }

   @Override
   public String toString() {
      return "Dependency " + dependencyType + " from " + javaSourceFrom + " to " + javaSourceTo;
   }
}

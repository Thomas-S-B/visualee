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
import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de)
 */
public class DependencyFilter {

   private List<DependencyType> filterTypes = new ArrayList<>();
   private boolean directlyConnected = false;

   public DependencyFilter addType(DependencyType dependencyType) {
      filterTypes.add(dependencyType);
      return this;
   }

   public boolean contains(DependencyType dependencyType) {
      return filterTypes.contains(dependencyType);
   }

   public DependencyFilter clearFilter() {
      filterTypes.clear();
      return this;
   }

   public DependencyFilter filterAllTypes() {
      filterTypes = Arrays.asList(DependencyType.values());
      return this;
   }

   public List<DependencyType> getFilterTypes() {
      return filterTypes;
   }

   public boolean isDirectlyConnected() {
      return directlyConnected;
   }

   public DependencyFilter setDirectlyConnected(boolean directlyConnected) {
      this.directlyConnected = directlyConnected;
      return this;
   }
}

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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class DependencyFilter {

   private List<DependenciyType> filterTypes = new ArrayList<>();

   public DependencyFilter addType(DependenciyType dependencyType) {
      filterTypes.add(dependencyType);
      return this;
   }

   public List<DependenciyType> getFilterTypes() {
      return filterTypes;
   }

   public boolean contains(DependenciyType dependencyType) {
      return filterTypes.contains(dependencyType);
   }

   public DependencyFilter clearFilter() {
      filterTypes.clear();
      return this;
   }
}

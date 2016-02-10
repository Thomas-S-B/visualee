package de.strullerbaumann.visualee.filter.boundary;

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
import de.strullerbaumann.visualee.filter.entity.Filter;
import de.strullerbaumann.visualee.logging.LogProvider;
import de.strullerbaumann.visualee.source.entity.JavaSource;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de)
 */
public final class FilterContainer {

   private static final List<Filter> FILTERS = new ArrayList<>();

   public boolean isOk(JavaSource javaSource) {
      for (Filter filter : FILTERS) {
         if ((!filter.isExclude() && filter.isOk(javaSource)) || (filter.isExclude() && !filter.isOk(javaSource))) {
            LogProvider.getInstance().debug("Filtered " + javaSource.getFullClassName() + " because of " + filter);
            return false;
         } else {
         }
      }
      return true;
   }

   private static class FilterContainerHolder {

      private static final FilterContainer INSTANCE = new FilterContainer();
   }

   private FilterContainer() {
   }

   public static FilterContainer getInstance() {
      return FilterContainer.FilterContainerHolder.INSTANCE;
   }

   public List<Filter> getFilters() {
      return FILTERS;
   }

   public void clear() {
      FILTERS.clear();
   }

   public void add(Filter filter) {
      if (filter == null) {
         return;
      }
      FILTERS.add(filter);
   }

}

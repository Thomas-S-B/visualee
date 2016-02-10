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
import de.strullerbaumann.visualee.filter.entity.ClassFilter;
import de.strullerbaumann.visualee.filter.entity.Filter;
import de.strullerbaumann.visualee.filter.entity.FilterConfig;
import de.strullerbaumann.visualee.filter.entity.PackageFilter;
import de.strullerbaumann.visualee.filter.entity.SourcecodeFilter;
import de.strullerbaumann.visualee.logging.LogProvider;
import java.util.List;

/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de)
 */
public final class FilterConfigurator {

   FilterConfigurator() {
   }

   public static void setFilterConfigs(List<FilterConfig> filterConfigsList) {
      FilterContainer.getInstance().clear();
      for (FilterConfig filterConfig : filterConfigsList) {
         Filter filter = null;
         if (PackageFilter.getType().equalsIgnoreCase(filterConfig.getType())) {
            filter = new PackageFilter();
         }
         if (ClassFilter.getType().equalsIgnoreCase(filterConfig.getType())) {
            filter = new ClassFilter();
         }
         if (SourcecodeFilter.getType().equalsIgnoreCase(filterConfig.getType())) {
            filter = new SourcecodeFilter();
         }
         if (filter == null) {
            LogProvider.getInstance().warn("Unknown filter configured: " + filterConfig.getType());
         } else {
            filter.setFilterToken(filterConfig.getFilterToken());
            filter.setExclude(filterConfig.getExclude());
            FilterContainer.getInstance().add(filter);
         }
      }
   }

}

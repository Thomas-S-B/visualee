/*
 * Copyright 2016 Thomas Struller-Baumann.
 *
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
 */
package de.strullerbaumann.visualee.filter.boundary;

/*
 * #%L
 * visualee
 * %%
 * Copyright (C) 2013 - 2016 Thomas Struller-Baumann
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
import de.strullerbaumann.visualee.filter.entity.FilterConfig;
import de.strullerbaumann.visualee.filter.entity.PackageFilter;
import de.strullerbaumann.visualee.filter.entity.SourcecodeFilter;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de)
 */
public class FilterConfiguratorTest {

   public FilterConfiguratorTest() {
   }

   @Before
   public void setUpTest() {
      FilterContainer.getInstance().clear();
   }

   @Test
   public void testSetFilterConfigsPackage() {
      List<FilterConfig> filters = new ArrayList<>();
      FilterConfig filterConfig = new FilterConfig();
      filterConfig.setType("pAckage");
      filters.add(filterConfig);
      FilterConfigurator.setFilterConfigs(filters);

      assertTrue(FilterContainer.getInstance().getFilters().get(0) instanceof PackageFilter);
   }

   @Test
   public void testSetFilterConfigsClass() {
      List<FilterConfig> filters = new ArrayList<>();
      FilterConfig filterConfig = new FilterConfig();
      filterConfig.setType("claSs");
      filters.add(filterConfig);
      FilterConfigurator.setFilterConfigs(filters);

      assertTrue(FilterContainer.getInstance().getFilters().get(0) instanceof ClassFilter);
   }

   @Test
   public void testSetFilterConfigsSource() {
      List<FilterConfig> filters = new ArrayList<>();
      FilterConfig filterConfig = new FilterConfig();
      filterConfig.setType("sourCe");
      filters.add(filterConfig);
      FilterConfigurator.setFilterConfigs(filters);

      assertTrue(FilterContainer.getInstance().getFilters().get(0) instanceof SourcecodeFilter);
   }

   @Test
   public void testSetFilterUnknown() {
      List<FilterConfig> filters = new ArrayList<>();
      FilterConfig filterConfig = new FilterConfig();
      filterConfig.setType("abcdefg");
      filters.add(filterConfig);
      FilterConfigurator.setFilterConfigs(filters);

      assertTrue(FilterContainer.getInstance().getFilters().isEmpty());
   }

}

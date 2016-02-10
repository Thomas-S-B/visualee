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
package de.strullerbaumann.visualee.filter.entity;

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
import de.strullerbaumann.visualee.source.entity.JavaSource;

/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de)
 */
public abstract class Filter {

   private boolean exclude = true;
   private String filterToken = "";

   /**
    * @return true, if the filter is an exclude-filter
    */
   public boolean isExclude() {
      return exclude;
   }

   /**
    * @param exclude the exclude to set, set it to true, if you need an exclude
    * filter.
    */
   public void setExclude(boolean exclude) {
      this.exclude = exclude;
   }

   /**
    * @return the filterToken
    */
   public String getFilterToken() {
      return filterToken;
   }

   /**
    * @param filterToken the filterToken to set
    */
   public void setFilterToken(String filterToken) {
      this.filterToken = filterToken;
   }

   public String getCludeString() {
      return exclude ? "Excluding" : "Including";
   }

   @Override
   public abstract String toString();

   public abstract boolean isOk(JavaSource javaSource);

}

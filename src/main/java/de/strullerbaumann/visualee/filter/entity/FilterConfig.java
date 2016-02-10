package de.strullerbaumann.visualee.filter.entity;

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
/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de)
 */
public class FilterConfig {

   private String type;
   private String filterToken;
   private boolean exclude = true;

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

   /**
    * @return the exclude
    */
   public boolean getExclude() {
      return exclude;
   }

   /**
    * @param exclude the exclude to set
    */
   public void setExclude(boolean exclude) {
      this.exclude = exclude;
   }

   /**
    * @return the type
    */
   public String getType() {
      return type;
   }

   /**
    * @param type the type to set
    */
   public void setType(String type) {
      this.type = type;
   }

}

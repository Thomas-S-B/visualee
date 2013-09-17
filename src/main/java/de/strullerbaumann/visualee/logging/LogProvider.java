package de.strullerbaumann.visualee.logging;

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
import org.apache.maven.plugin.logging.Log;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class LogProvider {

   private static Log log;

   private LogProvider() {
   }

   public static LogProvider getInstance() {
      return LogProviderHolder.INSTANCE;
   }

   private static class LogProviderHolder {

      private static final LogProvider INSTANCE = new LogProvider();
   }

   public void setLog(Log log) {
      LogProvider.log = log;
   }

   public void info(String content) {
      if (log == null) {
         System.out.println(content);
      } else {
         log.info(content);
      }
   }

   public void warn(String content) {
      if (log == null) {
         System.out.println(content);
      } else {
         log.warn(content);
      }
   }

   public void debug(String content) {
      if (log == null) {
         System.out.println(content);
      } else {
         log.debug(content);
      }
   }

   public void error(String content, Throwable t) {
      if (log == null) {
         System.err.println(content + t.getMessage());
      } else {
         log.error(content, t);
      }
   }
}

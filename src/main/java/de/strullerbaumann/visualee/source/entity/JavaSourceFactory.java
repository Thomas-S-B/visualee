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
package de.strullerbaumann.visualee.source.entity;

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
import de.strullerbaumann.visualee.filter.boundary.FilterContainer;
import java.nio.file.Path;

/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de)
 */
public class JavaSourceFactory {

   public static JavaSourceFactory getInstance() {
      return JavaSourceFactoryHolder.INSTANCE;
   }

   private static class JavaSourceFactoryHolder {

      private static final JavaSourceFactory INSTANCE = new JavaSourceFactory();
   }

   private JavaSourceFactory() {
   }

   public JavaSource newJavaSource(String className) {
      JavaSource javaSource = new JavaSource(className);
      if (FilterContainer.getInstance().isOk(javaSource)) {
         return javaSource;
      }
      return null;
   }

   public JavaSource newJavaSourceByFilename(Path javaPath) {
      JavaSource javaSource = new JavaSource(javaPath);
      if (FilterContainer.getInstance().isOk(javaSource)) {
         return javaSource;
      }
      return null;
   }

}

package de.strullerbaumann.visualee.source.boundary;

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
import de.strullerbaumann.visualee.resources.FileManager;
import de.strullerbaumann.visualee.source.entity.JavaSource;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public final class JavaSourceContainer {

   private static final Map<String, JavaSource> javaSources = new ConcurrentHashMap<>();

   private static class JavaSourceContainerHolder {

      private static final JavaSourceContainer INSTANCE = new JavaSourceContainer();
   }

   private JavaSourceContainer() {
   }

   public static JavaSourceContainer getInstance() {
      return JavaSourceContainer.JavaSourceContainerHolder.INSTANCE;
   }

   public Collection<JavaSource> getJavaSources() {
      return javaSources.values();
   }

   public void clear() {
      javaSources.clear();
   }

   public void add(JavaSource javaSource) {
      if (javaSource == null) {
         return;
      }
      if (!javaSources.containsKey(javaSource.getName())) {
         javaSources.put(javaSource.getName(), javaSource);
      }
   }

   public JavaSource getJavaSourceByName(String n) {
      return javaSources.get(n);
   }

   /*
    public void loadJavaFiles(File rootFolder) {
    final List<File> javaFiles = FileManager.searchFiles(rootFolder, ".java");
    for (File javaFile : javaFiles) {
    JavaSource javaSource = new JavaSource(javaFile);
    JavaSourceContainer.getInstance().add(javaSource);
    javaSource.loadSourceCode();
    }
    }
    */
   public void loadJavaFiles(String rootFolder) {
      final List<Path> javaFiles = FileManager.searchFiles(rootFolder, ".java");
      for (Path javaFile : javaFiles) {
         JavaSource javaSource = new JavaSource(javaFile);
         JavaSourceContainer.getInstance().add(javaSource);
         javaSource.loadSourceCode();
      }
   }

}

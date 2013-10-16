package de.strullerbaumann.visualee.resources;

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
import de.strullerbaumann.visualee.logging.LogProvider;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public final class FileManager {

   private FileManager() {
   }

   /**
    * Searches for files filtered by the fileextension in the given directory
    *
    * @param dir
    * @param extension
    * @return
    */
   public static List<File> searchFiles(File dir, String extension) {
      File[] files = dir.listFiles();
      List<File> matches = new ArrayList<>();
      if (files != null) {
         for (File file : files) {
            if (file.getName().toLowerCase().endsWith(extension)) {
               matches.add(file);
            }
            if (file.isDirectory()) {
               matches.addAll(searchFiles(file, extension));
            }
         }
      }
      return matches;
   }

   /**
    * Exports Files from the jar to a given directory
    *
    * @param clazz
    * @param sourceFolder
    * @param fileName
    * @param targetFolder
    */
   public static void export(Class clazz, String sourceFolder, String fileName, File targetFolder) {
      if (!targetFolder.exists()) {
         targetFolder.mkdir();
      }
      File dstResourceFolder = new File(targetFolder + sourceFolder + File.separatorChar);
      if (!dstResourceFolder.exists()) {
         dstResourceFolder.mkdir();
      }
      try (InputStream in = clazz.getResourceAsStream(sourceFolder + fileName)) {
         Path out = FileSystems.getDefault().getPath(targetFolder + sourceFolder + fileName);
         Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException exc) {
         LogProvider.getInstance().error("Can't export " + fileName + " from " + sourceFolder + " (jar) to " + targetFolder, exc);
      }
   }
}

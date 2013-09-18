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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public final class FileManager {

   private static final int BUFFER_SIZE = 1024;

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
         for (int i = 0; i < files.length; i++) {
            if (files[i].getName().toLowerCase().endsWith(extension)) {
               matches.add(files[i]);
            }
            if (files[i].isDirectory()) {
               matches.addAll(searchFiles(files[i], extension));
            }
         }
      }
      return matches;
   }

   public static void copyFolder(File src, File dest) throws IOException {
      if (src.isDirectory()) {
         //if directory not exists, create it
         if (!dest.exists()) {
            dest.mkdir();
         }
         //list all the directory contents
         String files[] = src.list();
         for (String file : files) {
            //construct the src and dest file structure
            File srcFile = new File(src, file);
            File destFile = new File(dest, file);
            //recursive copy
            copyFolder(srcFile, destFile);
         }
      } else {
         OutputStream out;
         try (InputStream in = new FileInputStream(src)) {
            out = new FileOutputStream(dest);
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = in.read(buffer)) > 0) {
               out.write(buffer, 0, length);
            }
         }
         out.close();
      }
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
      try {
         if (!targetFolder.exists()) {
            targetFolder.mkdir();
         }
         File dstResourceFolder = new File(targetFolder + sourceFolder + File.separatorChar);
         if (!dstResourceFolder.exists()) {
            dstResourceFolder.mkdir();
         }
         try (InputStream is = clazz.getResourceAsStream(sourceFolder + fileName);
                 OutputStream os = new FileOutputStream(targetFolder + sourceFolder + fileName)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = is.read(buffer)) > 0) {
               os.write(buffer, 0, length);
            }
         }
      } catch (Exception exc) {
         LogProvider.getInstance().error("Can't export " + fileName + " from " + sourceFolder + " (jar) to " + targetFolder, exc);
      }
   }
}

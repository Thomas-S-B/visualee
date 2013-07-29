/*
 Copyright 2013 Thomas Struller-Baumann, struller-baumann.de

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package de.strullerbaumann.visualee.resources;

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
}

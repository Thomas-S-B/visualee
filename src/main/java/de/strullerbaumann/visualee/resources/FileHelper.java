/*
 * Created on 08.04.2013 - 09:18:44 
 * 
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
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
public class FileHelper {

   public static List<File> searchFiles(File dir, String extension) {
      File[] files = dir.listFiles();
      List<File> matches = new ArrayList<>();
      if (files != null) {
         for (int i = 0; i < files.length; i++) {
            if (files[i].getName().toLowerCase().endsWith(extension)) {
               matches.add(files[i]);
            }
            if (files[i].isDirectory()) {
               matches.addAll(searchFiles(files[i], extension)); // fügt der ArrayList die ArrayList mit den Treffern aus dem Unterordner hinzu
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
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
               out.write(buffer, 0, length);
            }
         }
         out.close();
      }
   }
}

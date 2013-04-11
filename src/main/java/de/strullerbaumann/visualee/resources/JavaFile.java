/*
 * Created on 15.11.2012 - 12:08:02 
 * 
 * Copyright(c) 2012 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.resources;

import de.strullerbaumann.visualee.cdi.CDIDependency;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class JavaFile {

   private File javaFile;
   // private List<MyJavaFile> injected;
   private List<CDIDependency> injected;
   private int id;   //wird benötigt für D3, da die Links auf "IDs" der Nodes losgehen mit 0 beginnend
   private int group;   // Gruppennr., Nodes aus dem selben Package haben die selbe Gruppennr.
   private String packagePath;

   public JavaFile(File javaClassFile) {
      this.javaFile = javaClassFile;
      injected = new ArrayList<>();
   }

   public File getJavaFile() {
      return javaFile;
   }

   public void setJavaFile(File classFile) {
      this.javaFile = classFile;
   }

   public List<CDIDependency> getInjected() {
      return injected;
   }

   public void setInjected(List<CDIDependency> injected) {
      this.injected = injected;
   }

   @Override
   public String toString() {
      return this.getJavaFile().getName().substring(0, this.getJavaFile().getName().indexOf(".java")); //ohne Endung .java
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public int getGroup() {
      return group;
   }

   public void setGroup(int group) {
      this.group = group;
   }

   public String getPackagePath() {
      return packagePath;
   }

   public void setPackagePath(String packagePath) {
      this.packagePath = packagePath;
   }
}

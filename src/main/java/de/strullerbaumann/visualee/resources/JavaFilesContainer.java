/*
 * Created on 15.11.2012 - 12:09:44 
 * 
 * Copyright(c) 2012 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.resources;

import de.strullerbaumann.visualee.cdi.CDIDependency;
import de.strullerbaumann.visualee.cdi.CDIFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class JavaFilesContainer {

   private Map<String, JavaFile> myJavaClasses;

   public JavaFilesContainer() {
      myJavaClasses = new HashMap<>();
   }

   public Collection<JavaFile> getMyJavaFiles() {
      return myJavaClasses.values();
   }

   public void add(JavaFile myJavaClass) {
      if (myJavaClass != null && myJavaClass.getJavaFile() != null) {
         myJavaClasses.put(myJavaClass.getJavaFile().getName(), myJavaClass);
      }
   }

   public JavaFile getMyJavaFileByName(String n) {
      return myJavaClasses.get(n);
   }

   public List<JavaFile> getCDIRelevantClasses() {
      return getCDIRelevantClasses(null);
   }

   public List<JavaFile> getCDIRelevantClasses(CDIFilter cdiFilter) {
      List<JavaFile> classesCDIRelated = new ArrayList<>();
      for (JavaFile myJavaClass : getMyJavaFiles()) {
         if (myJavaClass.getInjected().size() > 0) {
            for (CDIDependency dependency : myJavaClass.getInjected()) {
               if (cdiFilter == null || cdiFilter.contains(dependency.getCdiType())) {
                  classesCDIRelated.add(dependency.getMyJavaFileFrom());
                  classesCDIRelated.add(dependency.getMyJavaFileTo());
               }
            }
         }
      }

      return classesCDIRelated;
   }
}

/*
 * Created on 05.04.2013 - 12:58:18 
 * 
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.cdi;

import de.strullerbaumann.visualee.resources.JavaFile;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class CDIDependency {

   private CDIType cdiType;
   private JavaFile myJavaFileFrom;
   private JavaFile myJavaFileTo;

   public CDIDependency() {
   }

   public CDIDependency(CDIType cdiType, JavaFile myJavaFileFrom, JavaFile myJavaFileTo) {
      this.cdiType = cdiType;
      this.myJavaFileFrom = myJavaFileFrom;
      this.myJavaFileTo = myJavaFileTo;
   }

   public CDIType getCdiType() {
      return cdiType;
   }

   public void setCdiType(CDIType cdiType) {
      this.cdiType = cdiType;
   }

   public JavaFile getMyJavaFileFrom() {
      return myJavaFileFrom;
   }

   public void setMyJavaFileFrom(JavaFile myJavaFileFrom) {
      this.myJavaFileFrom = myJavaFileFrom;
   }

   public JavaFile getMyJavaFileTo() {
      return myJavaFileTo;
   }

   public void setMyJavaFileTo(JavaFile myJavaFileTo) {
      this.myJavaFileTo = myJavaFileTo;
   }
}

/*
 * Created on 29.07.2013 - 16:34:40
 *
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.resources;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class JavaSourceContainerTest {

   public JavaSourceContainerTest() {
   }

   @Test
   public void testGetJavaSourceByName() {
      JavaSource javaSource1 = new JavaSource("DataPoint");
      JavaSourceContainer.getInstance().add(javaSource1);
      JavaSource javaSource2 = new JavaSource("int");
      JavaSourceContainer.getInstance().add(javaSource2);
      JavaSource javaSource3 = new JavaSource("MyTestClass");
      JavaSourceContainer.getInstance().add(javaSource3);

      assertNotNull(JavaSourceContainer.getInstance().getJavaSourceByName("MyTestClass"));
      assertEquals("MyTestClass", JavaSourceContainer.getInstance().getJavaSourceByName("MyTestClass").getName());
      assertNotNull(JavaSourceContainer.getInstance().getJavaSourceByName("int"));
      assertEquals("int", JavaSourceContainer.getInstance().getJavaSourceByName("int").getName());
      assertNotNull(JavaSourceContainer.getInstance().getJavaSourceByName("DataPoint"));
      assertEquals("DataPoint", JavaSourceContainer.getInstance().getJavaSourceByName("DataPoint").getName());
   }
}

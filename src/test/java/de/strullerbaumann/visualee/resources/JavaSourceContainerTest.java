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
      JavaSourceContainer.getInstance().clear();
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

   @Test
   public void testPutJavaSourceDoublette() {
      JavaSourceContainer.getInstance().clear();
      String name = "DataPoint";
      JavaSource javaSource1 = new JavaSource(name);
      String sourceCode1 = "Test source code for JavaSource1";
      javaSource1.setSourceCode(sourceCode1);
      JavaSourceContainer.getInstance().add(javaSource1);
      JavaSource javaSource2 = new JavaSource(name);
      javaSource2.setSourceCode("");
      JavaSourceContainer.getInstance().add(javaSource2);

      assertNotNull(JavaSourceContainer.getInstance().getJavaSourceByName(name));
      assertEquals(1, JavaSourceContainer.getInstance().getJavaSources().size());
      assertEquals(name, JavaSourceContainer.getInstance().getJavaSourceByName(name).getName());
      assertEquals(sourceCode1, JavaSourceContainer.getInstance().getJavaSourceByName(name).getSourceCode());
   }
}

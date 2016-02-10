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
import de.strullerbaumann.visualee.dependency.boundary.DependencyContainer;
import de.strullerbaumann.visualee.source.entity.JavaSource;
import de.strullerbaumann.visualee.source.entity.JavaSourceFactory;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de>)
 */
public class JavaSourceContainerTest {

   public JavaSourceContainerTest() {
   }

   @Before
   public void init() {
      DependencyContainer.getInstance().clear();
   }

   @Test
   public void testGetJavaSourceByName() {
      JavaSourceContainer.getInstance().clear();
      JavaSource javaSource1 = JavaSourceFactory.getInstance().newJavaSource("DataPoint");
      JavaSourceContainer.getInstance().add(javaSource1);
      JavaSource javaSource2 = JavaSourceFactory.getInstance().newJavaSource("int");
      JavaSourceContainer.getInstance().add(javaSource2);
      JavaSource javaSource3 = JavaSourceFactory.getInstance().newJavaSource("MyTestClass");
      JavaSourceContainer.getInstance().add(javaSource3);

      assertNotNull(JavaSourceContainer.getInstance().getJavaSourceByName("MyTestClass"));
      assertEquals("MyTestClass", JavaSourceContainer.getInstance().getJavaSourceByName("MyTestClass").getName());
      assertNotNull(JavaSourceContainer.getInstance().getJavaSourceByName("int"));
      assertEquals("int", JavaSourceContainer.getInstance().getJavaSourceByName("int").getName());
      assertNotNull(JavaSourceContainer.getInstance().getJavaSourceByName("DataPoint"));
      assertEquals("DataPoint", JavaSourceContainer.getInstance().getJavaSourceByName("DataPoint").getName());
   }

   @Test
   public void testAddJavaSourceDoublette() {
      JavaSourceContainer.getInstance().clear();
      String name = "DataPoint";
      JavaSource javaSource1 = JavaSourceFactory.getInstance().newJavaSource(name);
      String sourceCode1 = "Test source code for JavaSource1 (listen to Brian Blade - Season of change";
      javaSource1.setSourceCode(sourceCode1);
      JavaSourceContainer.getInstance().add(javaSource1);
      JavaSource javaSource2 = JavaSourceFactory.getInstance().newJavaSource(name);
      javaSource2.setSourceCode("");
      JavaSourceContainer.getInstance().add(javaSource2);

      assertNotNull(JavaSourceContainer.getInstance().getJavaSourceByName(name));
      assertEquals(1, JavaSourceContainer.getInstance().getJavaSources().size());
      assertEquals(name, JavaSourceContainer.getInstance().getJavaSourceByName(name).getName());
      assertEquals(sourceCode1, JavaSourceContainer.getInstance().getJavaSourceByName(name).getSourceCode());
   }

   @Test
   public void testAddNullJavaSource() {
      JavaSourceContainer.getInstance().clear();
      JavaSource javaSource = null;
      JavaSourceContainer.getInstance().add(javaSource);

      assertEquals(0, JavaSourceContainer.getInstance().getJavaSources().size());
   }
}

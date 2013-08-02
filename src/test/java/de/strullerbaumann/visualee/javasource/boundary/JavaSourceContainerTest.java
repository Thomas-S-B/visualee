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
package de.strullerbaumann.visualee.javasource.boundary;

import de.strullerbaumann.visualee.dependency.boundary.DependencyFilter;
import de.strullerbaumann.visualee.dependency.entity.Dependency;
import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.javasource.entity.JavaSource;
import java.util.ArrayList;
import java.util.List;
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
   public void testAddJavaSourceDoublette() {
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

   @Test
   public void testAddNullJavaSource() {
      JavaSourceContainer.getInstance().clear();
      JavaSource javaSource1 = null;

      JavaSourceContainer.getInstance().add(javaSource1);
      assertEquals(0, JavaSourceContainer.getInstance().getJavaSources().size());
   }

   @Test
   public void testGetRelevantClasses() {
      JavaSourceContainer.getInstance().clear();
      int count = 10;

      JavaSource javaSourceInj = new JavaSource("Testinject");
      JavaSourceContainer.getInstance().add(javaSourceInj);

      for (int i = 0; i < count; i++) {
         String name = "Testclass " + i;
         JavaSource javaSource = new JavaSource(name);
         List<Dependency> injected = new ArrayList<>();
         injected.add(new Dependency(DependencyType.INJECT, javaSource, javaSourceInj));
         javaSource.setInjected(injected);
         JavaSourceContainer.getInstance().add(javaSource);
      }

      // + 1 because of the javaSourceInj
      assertEquals(count + 1, JavaSourceContainer.getInstance().getRelevantClasses().size());
   }

   @Test
   public void testGetRelevantClassesFilter() {
      JavaSourceContainer.getInstance().clear();
      int count = 10;
      int count1 = 0;
      int count2 = 0;

      DependencyType type1 = DependencyType.INJECT;
      DependencyType type2 = DependencyType.EJB;

      JavaSource javaSourceType1 = new JavaSource("Testinject");
      JavaSourceContainer.getInstance().add(javaSourceType1);

      JavaSource javaSourceType2 = new JavaSource("TestEjb");
      JavaSourceContainer.getInstance().add(javaSourceType2);

      for (int i = 0; i < count; i++) {
         String name = "Testclass " + i;
         JavaSource javaSource = new JavaSource(name);
         List<Dependency> injected = new ArrayList<>();
         if (i % 2 > 0) {
            injected.add(new Dependency(type1, javaSource, javaSourceType1));
            count1++;
         } else {
            injected.add(new Dependency(type2, javaSource, javaSourceType2));
            count2++;
         }
         javaSource.setInjected(injected);
         JavaSourceContainer.getInstance().add(javaSource);
      }

      // + 1 because of the injected javaSourceType1
      DependencyFilter filter1 = new DependencyFilter().addType(type1);
      assertEquals(count1 + 1, JavaSourceContainer.getInstance().getRelevantClasses(filter1).size());

      // + 1 because of the injected javaSourceType2
      DependencyFilter filter2 = new DependencyFilter().addType(type2);
      assertEquals(count2 + 1, JavaSourceContainer.getInstance().getRelevantClasses(filter2).size());
   }
}

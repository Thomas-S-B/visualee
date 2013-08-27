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
package de.strullerbaumann.visualee.examiner.cdi;

import de.strullerbaumann.visualee.dependency.entity.Dependency;
import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.javasource.entity.JavaSource;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class ExaminerInjectTest {

   private ExaminerInject examiner;

   public ExaminerInjectTest() {
   }

   @Before
   public void init() {
      examiner = new ExaminerInject();
   }

   @Test
   public void testIsRelevantType() {
      for (DependencyType dependencyType : DependencyType.values()) {
         if (dependencyType == DependencyType.INJECT) {
            assertTrue(examiner.isRelevantType(DependencyType.INJECT));
         } else {
            assertFalse(examiner.isRelevantType(dependencyType));
         }
      }
   }

   @Test
   public void testgetTypeFromToken() {
      String sourceLine;
      DependencyType actual;

      sourceLine = "My test sourcecode";
      actual = examiner.getTypeFromToken(sourceLine);
      assertEquals(null, actual);

      sourceLine = "@Inject TestCalss myTestClass;";
      actual = examiner.getTypeFromToken(sourceLine);
      assertEquals(DependencyType.INJECT, actual);
   }

   @Test
   public void testFindAndSetAttributesSetInject() {
      JavaSource javaSource;
      String sourceCode;

      javaSource = new JavaSource("MyTestClass");
      sourceCode = "public abstract class MyTestClass<K, E extends SingleIdEntity<K>> implements CrudAccessor<K, E>, Serializable {\n"
              + "protected EntityManager entityManager;\n"
              + "private Class<E> entityClass;\n"
              + "@Inject\n"
              + "TestClass testclass;\n"
              + "@Inject\n"
              + "Instance<TestClass2> testclass2;\n"
              + "@Inject\n"
              + "Event<TestClass3> testclass3;\n"
              + "@Inject\n"
              + "Instance<SnapshotCollector> snapshotCollectorInstance;\n"
              + "@Inject\n"
              + "protected void setEntityManager(EntityManager entityManager) {\n"
              + "        this.entityManager = entityManager;\n"
              + "}\n";

      javaSource.setSourceCode(sourceCode);
      examiner.examine(javaSource);
      assertEquals(2, javaSource.getInjected().size());

      Dependency dependency;
      dependency = javaSource.getInjected().get(0);
      assertEquals(DependencyType.INJECT, dependency.getDependencyType());
      assertEquals("MyTestClass", dependency.getJavaSourceFrom().getName());
      assertEquals("TestClass", dependency.getJavaSourceTo().getName());
      dependency = javaSource.getInjected().get(1);
      assertEquals(DependencyType.INJECT, dependency.getDependencyType());
      assertEquals("MyTestClass", dependency.getJavaSourceFrom().getName());
      assertEquals("EntityManager", dependency.getJavaSourceTo().getName());
   }

   @Test
   public void testFindAndSetAttributesInjectSetterWithAnnotations() {
      JavaSource javaSource;
      String sourceCode;

      javaSource = new JavaSource("ZeiterfassungEingabeModel");
      sourceCode = "public class ZeiterfassungEingabeModel implements Serializable\n"
              + "{\n"
              + "@Inject\n"
              + "protected void setBuchungsMonat(@Current @Zeiterfassung Date buchungsMonat)\n"
              + "{\n";

      javaSource.setSourceCode(sourceCode);
      examiner.examine(javaSource);
      assertEquals(1, javaSource.getInjected().size());

      Dependency dependency;
      dependency = javaSource.getInjected().get(0);
      assertEquals(DependencyType.INJECT, dependency.getDependencyType());
      assertEquals("ZeiterfassungEingabeModel", dependency.getJavaSourceFrom().getName());
      assertEquals("Date", dependency.getJavaSourceTo().getName());
   }
}

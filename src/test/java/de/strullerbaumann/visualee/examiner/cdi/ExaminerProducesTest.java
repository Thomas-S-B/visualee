package de.strullerbaumann.visualee.examiner.cdi;

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
import de.strullerbaumann.visualee.dependency.entity.Dependency;
import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.source.entity.JavaSource;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class ExaminerProducesTest {

   private ExaminerProduces examiner;

   public ExaminerProducesTest() {
   }

   @Before
   public void init() {
      examiner = new ExaminerProduces();
      DependencyContainer.getInstance().clear();
   }

   @Test
   public void testIsRelevantType() {
      for (DependencyType dependencyType : DependencyType.values()) {
         if (dependencyType == DependencyType.PRODUCES) {
            assertTrue(examiner.isRelevantType(DependencyType.PRODUCES));
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

      sourceLine = "@Produces";
      actual = examiner.getTypeFromToken(sourceLine);
      assertEquals(DependencyType.PRODUCES, actual);

      //Resource - REST not
      sourceLine = "@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})";
      actual = examiner.getTypeFromToken(sourceLine);
      assertEquals(null, actual);
   }

   @Test
   public void testFindAndSetAttributesProduces() {
      JavaSource javaSource;
      Dependency dependency;
      String sourceCode;

      javaSource = new JavaSource("DatabaseProducer");
      sourceCode = "package org.agoncal.application.petstore.util;\n"
              + "import javax.enterprise.inject.Produces;\n"
              + "import javax.persistence.EntityManager;\n"
              + "import javax.persistence.PersistenceContext;\n"
              + "public class DatabaseProducer {\n"
              + "@Produces\n"
              + "    @PersistenceContext(unitName = \"applicationPetstorePU\")\n"
              + "    private EntityManager em;\n"
              + "}\n";

      javaSource.setSourceCode(sourceCode);
      examiner.examine(javaSource);
      dependency = DependencyContainer.getInstance().getDependencies(javaSource).get(0);
      assertEquals(1, DependencyContainer.getInstance().getDependencies(javaSource).size());
      assertEquals(DependencyType.PRODUCES, dependency.getDependencyType());
      assertEquals("DatabaseProducer", dependency.getJavaSourceFrom().getName());
      assertEquals("EntityManager", dependency.getJavaSourceTo().getName());
   }

   @Test
   public void testFindAndSetAttributesStaticProduces() {
      JavaSource javaSource;
      String sourceCode;

      javaSource = new JavaSource("LoggerProducer");
      sourceCode = "package de.dasd.dasdas.utils.logging;\n"
              + "import javax.enterprise.inject.Produces;\n"
              + "import javax.enterprise.inject.spi.InjectionPoint;\n"
              + "import org.apache.commons.logging.Log;\n"
              + "import org.apache.commons.logging.LogFactory;\n"
              + "public class LoggerProducer {\n"
              + "    @Produces\n"
              + "    public static Log getLogger(InjectionPoint injectionPoint) {\n"
              + "        Class<?> targetClass = injectionPoint.getMember().getDeclaringClass();\n"
              + "        return LogFactory.getLog(targetClass);\n"
              + "    }\n"
              + "    private LoggerProducer() {\n"
              + "    }\n"
              + "}\n";

      javaSource.setSourceCode(sourceCode);
      examiner.examine(javaSource);
      assertEquals(1, DependencyContainer.getInstance().getDependencies(javaSource).size());

      Dependency dependency;
      dependency = DependencyContainer.getInstance().getDependencies(javaSource).get(0);
      assertEquals(DependencyType.PRODUCES, dependency.getDependencyType());
      assertEquals("LoggerProducer", dependency.getJavaSourceFrom().getName());
      assertEquals("Log", dependency.getJavaSourceTo().getName());
   }
}

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
public class ExaminerInstanceTest {

   private ExaminerInstance examiner;

   public ExaminerInstanceTest() {
   }

   @Before
   public void init() {
      examiner = new ExaminerInstance();
   }

   @Test
   public void testIsRelevantType() {
      for (DependencyType dependencyType : DependencyType.values()) {
         if (dependencyType == DependencyType.INSTANCE || dependencyType == DependencyType.INJECT) {
            assertTrue(examiner.isRelevantType(DependencyType.INSTANCE));
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
   public void testFindAndSetAttributesSetInstance() {
      JavaSource javaSource;
      String sourceCode;

      javaSource = new JavaSource("EscalationNotificationBroker");
      sourceCode = "@Singleton\n"
              + "@ConcurrencyManagement(ConcurrencyManagementType.BEAN)\n"
              + "public class EscalationNotificationBroker {\n"
              + "private static final Logger LOG = Logger.getLogger(EscalationNotificationBroker.class.getName());\n"
              + "@Inject NotifierStore notifierStore;\n"
              + "@Inject @Any Instance<Transmitter> transmitters;\n"
              + "@Inject AsyncTransmitterService asyncService;\n"
              + "}";

      javaSource.setSourceCode(sourceCode);
      examiner.examine(javaSource);
      assertEquals(1, javaSource.getInjected().size());

      Dependency dependency;
      dependency = javaSource.getInjected().get(0);
      assertEquals(DependencyType.INSTANCE, dependency.getDependencyType());
      assertEquals("EscalationNotificationBroker", dependency.getJavaSourceFrom().getName());
      assertEquals("Transmitter", dependency.getJavaSourceTo().getName());
   }

   @Test
   public void testFindAndSetAttributesSetInstanceGeneric() {
      JavaSource javaSource;
      String sourceCode;

      javaSource = new JavaSource("SnapshotProvider");
      sourceCode = "public class EscalationNotificationBroker {\n"
              + "@Inject\n"
              + "   @SnapshotDataCollector"
              + "   Instance<DataCollector<?>> dataCollectors;"
              + "}";

      javaSource.setSourceCode(sourceCode);
      examiner.examine(javaSource);
      assertEquals(1, javaSource.getInjected().size());

      Dependency dependency;
      dependency = javaSource.getInjected().get(0);
      assertEquals(DependencyType.INSTANCE, dependency.getDependencyType());
      assertEquals("SnapshotProvider", dependency.getJavaSourceFrom().getName());
      assertEquals("DataCollector", dependency.getJavaSourceTo().getName());
   }
}

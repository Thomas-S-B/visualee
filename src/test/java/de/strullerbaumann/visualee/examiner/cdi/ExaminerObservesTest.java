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
import de.strullerbaumann.visualee.examiner.SourceCodeProvider;
import de.strullerbaumann.visualee.source.entity.JavaSource;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class ExaminerObservesTest {

   private ExaminerObserves examiner;

   public ExaminerObservesTest() {
   }

   @Before
   public void init() {
      examiner = new ExaminerObserves();
   }

   @Test
   public void testIsRelevantType() {
      for (DependencyType dependencyType : DependencyType.values()) {
         if (dependencyType == DependencyType.OBSERVES) {
            assertTrue(examiner.isRelevantType(DependencyType.OBSERVES));
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

      sourceLine = "public void onEscalationBrowserRequest(@Observes Escalation escalation) {";
      actual = examiner.getTypeFromToken(sourceLine);
      assertEquals(DependencyType.OBSERVES, actual);
   }

   @Test
   public void testFindAndSetAttributesObserves() {
      JavaSource javaSource;
      Dependency dependency;
      String sourceCode;

      javaSource = new JavaSource("SnapshotEscalator");
      sourceCode = SourceCodeProvider.getTestSourceCodeBeforeBody()
              + "public void escalate(@Observes @Severity(Severity.Level.HEARTBEAT) Snapshot current) {\n"
              + "List<Script> scripts = this.scripting.activeScripts();\n"
              + "try {\n"
              + "Bindings binding = this.scriptEngine.createBindings();\n"
              + "binding.put(\"current\", current);\n"
              + "Snapshot recent = this.recentSnapshots.get(current.getInstanceName());\n"
              + "binding.put(\"previous\", recent);\n"
              + "long start = System.currentTimeMillis();\n"
              + "} catch (Exception e) {\n"
              + "throw new IllegalStateException(\"Exception during script evaluation: \" + e, e);\n"
              + "}\n"
              + "}\n";


      javaSource.setSourceCode(sourceCode);
      examiner.examine(javaSource);
      dependency = javaSource.getInjected().get(0);
      assertEquals(1, javaSource.getInjected().size());
      assertEquals(DependencyType.OBSERVES, dependency.getDependencyType());
      assertEquals("SnapshotEscalator", dependency.getJavaSourceFrom().getName());
      assertEquals("Snapshot", dependency.getJavaSourceTo().getName());
   }
}

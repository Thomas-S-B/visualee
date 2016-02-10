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
import de.strullerbaumann.visualee.source.entity.JavaSourceFactory;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de>)
 */
public class ExaminerResourceTest {

   private ExaminerResource examiner;

   public ExaminerResourceTest() {
   }

   @Before
   public void init() {
      examiner = new ExaminerResource();
      DependencyContainer.getInstance().clear();
   }

   @Test
   public void testIsRelevantType() {
      for (DependencyType dependencyType : DependencyType.values()) {
         if (dependencyType == DependencyType.RESOURCE) {
            assertTrue(examiner.isRelevantType(DependencyType.RESOURCE));
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

      sourceLine = "@Resource";
      actual = examiner.getTypeFromToken(sourceLine);
      assertEquals(DependencyType.RESOURCE, actual);

      sourceLine = "@Resource(lookup = \"jdbc/ee-demos\")";
      actual = examiner.getTypeFromToken(sourceLine);
      assertEquals(DependencyType.RESOURCE, actual);
   }

   @Test
   public void testFindAndSetAttributesResource() {
      JavaSource javaSource;
      Dependency dependency;
      String sourceCode;

      javaSource = JavaSourceFactory.getInstance().newJavaSource("DatabaseConnectionProducer");
      sourceCode = "package de.gedoplan.buch.eedemos.cdi.producer;\n"
              + "import javax.annotation.Resource;\n"
              + "import javax.enterprise.context.ApplicationScoped;\n"
              + "import javax.enterprise.inject.Produces;\n"
              + "import javax.sql.DataSource;\n"
              + "@ApplicationScoped\n"
              + "public class DatabaseConnectionProducer\n"
              + "{\n"
              + "@Resource(lookup = \"jdbc/ee-demos\")\n"
              + "@Produces\n"
              + "private DataSource dataSource;\n"
              + "}\n";

      javaSource.setSourceCode(sourceCode);
      examiner.examine(javaSource);
      dependency = DependencyContainer.getInstance().getDependencies(javaSource).get(0);
      assertEquals(1, DependencyContainer.getInstance().getDependencies(javaSource).size());
      assertEquals(DependencyType.RESOURCE, dependency.getDependencyType());
      assertEquals("DatabaseConnectionProducer", dependency.getJavaSourceFrom().getName());
      assertEquals("DataSource", dependency.getJavaSourceTo().getName());
   }

   @Test
   public void testFindAndSetAttributesResourceMappedName() {
      JavaSource javaSource;
      Dependency dependency;
      String sourceCode;

      javaSource = JavaSourceFactory.getInstance().newJavaSource("SimplifiedMessageReceiver");
      sourceCode = "package de.x.y;\n"
              + "public class SimplifiedMessageReceiver {\n"
              + "@Resource(mappedName=\"java:global/jms/myQueue2\")\n"
              + "Queue myQueue;\n"
              + "}\n";
      javaSource.setSourceCode(sourceCode);
      examiner.examine(javaSource);
      dependency = DependencyContainer.getInstance().getDependencies(javaSource).get(0);
      assertEquals(1, DependencyContainer.getInstance().getDependencies(javaSource).size());
      assertEquals(DependencyType.RESOURCE, dependency.getDependencyType());
      assertEquals("SimplifiedMessageReceiver", dependency.getJavaSourceFrom().getName());
      assertEquals("Queue", dependency.getJavaSourceTo().getName());
   }

   @Test
   public void testFindAndSetAttributesResourceInstance() {
      JavaSource javaSource;
      Dependency dependency;
      String sourceCode;

      javaSource = JavaSourceFactory.getInstance().newJavaSource("ResourceCollector");
      sourceCode = "@SnapshotDataCollector\n"
              + "public class ResourceCollector extends AbstractRestDataCollector<List<ConnectionPool>> {\n"
              + "    private static final String RESOURCES = \"resources\";\n"
              + "    @Inject\n"
              + "    @ResourceDataCollector\n"
              + "    Instance<SpecificResourceCollector> specificCollector;\n"
              + "    @Inject\n"
              + "    Instance<Boolean> parallelDataCollection;\n"
              + "    @Resource\n"
              + "    TimerService timerService;\n"
              + "    @Inject\n"
              + "    ParallelDataCollectionExecutor parallelExecutor;\n"
              + "}\n";

      javaSource.setSourceCode(sourceCode);
      examiner.examine(javaSource);
      assertEquals(1, DependencyContainer.getInstance().getDependencies(javaSource).size());
      dependency = DependencyContainer.getInstance().getDependencies(javaSource).get(0);
      assertEquals(DependencyType.RESOURCE, dependency.getDependencyType());
      assertEquals("ResourceCollector", dependency.getJavaSourceFrom().getName());
      assertEquals("TimerService", dependency.getJavaSourceTo().getName());
   }
}

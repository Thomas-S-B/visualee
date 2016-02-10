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
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class ExaminerEventTest {

   private ExaminerEvent examiner;

   public ExaminerEventTest() {
   }

   @Before
   public void init() {
      examiner = new ExaminerEvent();
   }

   @Test
   public void testIsRelevantType() {
      for (DependencyType dependencyType : DependencyType.values()) {
         if (dependencyType == DependencyType.EVENT || dependencyType == DependencyType.INJECT) {
            assertTrue(examiner.isRelevantType(DependencyType.EVENT));
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
      DependencyContainer.getInstance().clear();
      JavaSource javaSource;
      String sourceCode;

      javaSource = JavaSourceFactory.getInstance().newJavaSource("TunguskaGate");
      sourceCode = "@WebServlet(name = \"TunguskaGate\", urlPatterns = {\"/live/*\"}, asyncSupported = true)\n"
              + "public class TunguskaGate extends HttpServlet {\n"
              + "    @Inject @SnapshotDataCollector\n"
              + "    Event<BrowserWindow> events;\n"
              + "    private final static Logger LOG = Logger.getLogger(TunguskaGate.class.getName());\n"
              + "    @Override\n"
              + "    protected void doGet(HttpServletRequest request, HttpServletResponse response)\n"
              + "            throws ServletException, IOException {\n"
              + "        AsyncContext startAsync = request.startAsync();\n"
              + "        String channel = extractChannel(request.getRequestURI());\n"
              + "        LOG.info(\"Browser is requesting \" + channel);\n"
              + "        if(channel==null||channel.trim().isEmpty()){\n"
              + "            channel = MonitoringController.COMBINED_SNAPSHOT_NAME;\n"
              + "        }\n"
              + "        BrowserWindow browser = new BrowserWindow(startAsync,channel);\n"
              + "        LOG.info(\"Registering browser window(\"+ browser.hashCode() +\") for channel \" + channel);\n"
              + "        events.fire(browser);\n"
              + "        LOG.fine(\"Event sent\");\n"
              + "    }\n"
              + "    }\n";

      javaSource.setSourceCode(sourceCode);
      examiner.examine(javaSource);
      assertEquals(1, DependencyContainer.getInstance().getDependencies(javaSource).size());

      Dependency dependency;
      dependency = DependencyContainer.getInstance().getDependencies(javaSource).get(0);
      assertEquals(DependencyType.EVENT, dependency.getDependencyType());
      assertEquals("TunguskaGate", dependency.getJavaSourceFrom().getName());
      assertEquals("BrowserWindow", dependency.getJavaSourceTo().getName());
   }

   @Test
   public void testFindEvents() {
      DependencyContainer.getInstance().clear();
      JavaSource javaSource;
      String sourceCode;

      javaSource = JavaSourceFactory.getInstance().newJavaSource("Events");
      sourceCode = "     @Stateless\n"
              + "public class Events {\n"
              + "    @PersistenceContext\n"
              + " private EntityManager em;\n"
              + " @Inject\n"
              + " private EventValidator validator;\n"
              + " @Inject\n"
              + " private PrintingService ps;\n"
              + " @Inject\n"
              + " Event<AirEvent> monitoring;\n"
              + " public List<AirEvent> getEvents() {\n"
              + "     return this.em.createNamedQuery(AirEvent.all).getResultList();\n"
              + " }\n"
              + "    }\n";

      javaSource.setSourceCode(sourceCode);
      examiner.examine(javaSource);
      assertEquals(1, DependencyContainer.getInstance().getDependencies(javaSource).size());

      Dependency dependency;
      dependency = DependencyContainer.getInstance().getDependencies(javaSource).get(0);
      assertEquals(DependencyType.EVENT, dependency.getDependencyType());
      assertEquals("Events", dependency.getJavaSourceFrom().getName());
      assertEquals("AirEvent", dependency.getJavaSourceTo().getName());
   }
}

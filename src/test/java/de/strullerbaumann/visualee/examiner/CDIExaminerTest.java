/*
 * Created on 29.07.2013 - 15:34:06
 *
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.examiner;

import de.strullerbaumann.visualee.dependency.Dependency;
import de.strullerbaumann.visualee.dependency.DependenciyType;
import de.strullerbaumann.visualee.resources.JavaSource;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class CDIExaminerTest {

   public CDIExaminerTest() {
   }

   @Test
   public void testIsRelevantType() {
      CDIExaminer cdiExaminer = new CDIExaminer();
      assertTrue(cdiExaminer.isRelevantType(DependenciyType.EJB));
      assertTrue(cdiExaminer.isRelevantType(DependenciyType.EVENT));
      assertTrue(cdiExaminer.isRelevantType(DependenciyType.INJECT));
      assertTrue(cdiExaminer.isRelevantType(DependenciyType.INSTANCE));
      assertTrue(cdiExaminer.isRelevantType(DependenciyType.OBSERVES));
      assertTrue(cdiExaminer.isRelevantType(DependenciyType.PRODUCES));
      assertFalse(cdiExaminer.isRelevantType(DependenciyType.MANY_TO_MANY));
      assertFalse(cdiExaminer.isRelevantType(DependenciyType.MANY_TO_ONE));
      assertFalse(cdiExaminer.isRelevantType(DependenciyType.ONE_TO_MANY));
      assertFalse(cdiExaminer.isRelevantType(DependenciyType.ONE_TO_ONE));
   }

   @Test
   public void testFindAndSetAttributesObserves() {
      CDIExaminer cdiExaminer = new CDIExaminer();
      JavaSource javaSource;
      Dependency dependency;
      String sourceCode;

      // Many to many
      javaSource = new JavaSource("SnapshotEscalator");
      sourceCode = getTestSourceCodeBeforeBody()
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
      cdiExaminer.examine(javaSource);
      dependency = javaSource.getInjected().get(0);
      assertEquals(1, javaSource.getInjected().size());
      assertEquals(DependenciyType.OBSERVES, dependency.getDependencyType());
      assertEquals("SnapshotEscalator", dependency.getJavaSourceFrom().getName());
      assertEquals("Snapshot", dependency.getJavaSourceTo().getName());
   }

   @Test
   public void testFindAndSetAttributesSetInject() {
      CDIExaminer cdiExaminer = new CDIExaminer();
      JavaSource javaSource;
      String sourceCode;

      javaSource = new JavaSource("MyTestClass");
      sourceCode = "public abstract class MyTestClass<K, E extends SingleIdEntity<K>> implements CrudAccessor<K, E>, Serializable {\n"
              + "protected EntityManager entityManager;\n"
              + "private Class<E> entityClass;\n"
              + "@Inject\n"
              + "protected void setEntityManager(EntityManager entityManager) {\n"
              + "        this.entityManager = entityManager;\n"
              + "}\n";

      javaSource.setSourceCode(sourceCode);
      cdiExaminer.examine(javaSource);
      assertEquals(1, javaSource.getInjected().size());

      Dependency dependency;
      dependency = javaSource.getInjected().get(0);
      assertEquals(DependenciyType.INJECT, dependency.getDependencyType());
      assertEquals("MyTestClass", dependency.getJavaSourceFrom().getName());
      assertEquals("EntityManager", dependency.getJavaSourceTo().getName());
   }

   @Test
   public void testFindAndSetAttributesStaticInject() {
      CDIExaminer cdiExaminer = new CDIExaminer();
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
      cdiExaminer.examine(javaSource);
      assertEquals(1, javaSource.getInjected().size());

      Dependency dependency;
      dependency = javaSource.getInjected().get(0);
      assertEquals(DependenciyType.PRODUCES, dependency.getDependencyType());
      assertEquals("LoggerProducer", dependency.getJavaSourceFrom().getName());
      assertEquals("Log", dependency.getJavaSourceTo().getName());
   }

   @Test
   public void testFindAndSetAttributesInjectSetterWithAnnotations() {
      CDIExaminer cdiExaminer = new CDIExaminer();
      JavaSource javaSource;
      String sourceCode;

      javaSource = new JavaSource("ZeiterfassungEingabeModel");
      sourceCode = "public class ZeiterfassungEingabeModel implements Serializable\n"
              + "{\n"
              + "@Inject\n"
              + "protected void setBuchungsMonat(@Current @Zeiterfassung Date buchungsMonat)\n"
              + "{\n";

      javaSource.setSourceCode(sourceCode);
      cdiExaminer.examine(javaSource);
      assertEquals(1, javaSource.getInjected().size());

      Dependency dependency;
      dependency = javaSource.getInjected().get(0);
      assertEquals(DependenciyType.INJECT, dependency.getDependencyType());
      assertEquals("ZeiterfassungEingabeModel", dependency.getJavaSourceFrom().getName());
      assertEquals("Date", dependency.getJavaSourceTo().getName());
   }

   @Test
   public void testFindAndSetAttributesIgnoreComments() {
      CDIExaminer cdiExaminer = new CDIExaminer();
      JavaSource javaSource;
      String sourceCode;

      javaSource = new JavaSource("Cocktail");
      sourceCode = "//@Entity\n"
              + "//@Access(AccessType.FIELD)\n"
              + "public class Cocktail implements Comparable<Cocktail>\n"
              + "{\n"
              + "// @Id\n"
              + "private String             id;\n"
              + "private String             name;\n"
              + "// @ManyToMany\n"
              + "private Set<CocktailZutat> zutaten = new HashSet<CocktailZutat>();\n"
              + "// @ManyToOne\n"
              + "private CocktailZutat      basisZutat;\n"
              + "public Cocktail(String id, String name)\n"
              + "{\n"
              + "this.id = id;\n"
              + "this.name = name;\n"
              + "}\n";

      javaSource.setSourceCode(sourceCode);
      cdiExaminer.examine(javaSource);
      assertEquals(0, javaSource.getInjected().size());
   }

   @Test
   public void testFindAndSetAttributesIgnoreCommentBlocks() {
      CDIExaminer cdiExaminer = new CDIExaminer();
      JavaSource javaSource;
      String sourceCode;

      javaSource = new JavaSource("CocktailModel");
      sourceCode = "@Model\n"
              + "public class CocktailModel implements Serializable\n"
              + "{\n"
              + "  private List<Cocktail>     nonAlcoholicCocktails;\n"
              + "  private List<Cocktail>     alcoholicCocktails;\n"
              + "  /*\n"
              + "  @Inject\n"
              + "  private CocktailRepository cocktailRepository;\n"
              + "    */\n"
              + "  public List<Cocktail> getNonAlcoholicCocktails()\n"
              + "{\n"
              + "return this.nonAlcoholicCocktails;\n"
              + "}\n";

      javaSource.setSourceCode(sourceCode);
      cdiExaminer.examine(javaSource);
      assertEquals(0, javaSource.getInjected().size());
   }

   // TODO auslagern siehe auch JavaSourceExaminerTest
   private String getTestSourceCode() {
      return getTestSourceCodeBeforeBody() + getTestSourceCodeBody();
   }

   // TODO auslagern siehe auch JavaSourceExaminerTest
   private String getTestSourceCodeBeforeBody() {
      return "/*\n"
              + " Copyright 2013 Thomas Struller-Baumann, struller-baumann.de\n"
              + "\n"
              + " Licensed under the Apache License, Version 2.0 (the \"License\");\n"
              + " you may not use this file except in compliance with the License.\n"
              + " You may obtain a copy of the License at\n"
              + "\n"
              + " http://www.apache.org/licenses/LICENSE-2.0\n"
              + "\n"
              + " Unless required by applicable law or agreed to in writing, software\n"
              + " distributed under the License is distributed on an \"AS IS\" BASIS,\n"
              + " WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
              + " See the License for the specific language governing permissions and\n"
              + " limitations under the License.\n"
              + " */\n"
              + "package de.strullerbaumann.visualee.resources;\n"
              + "\n"
              + "import de.strullerbaumann.visualee.cdi.CDIDependency;\n"
              + "import de.strullerbaumann.visualee.cdi.CDIType;\n"
              + "import java.io.BufferedReader;\n"
              + "import java.io.FileNotFoundException;\n"
              + "import java.io.FileReader;\n"
              + "import java.io.IOException;\n"
              + "import java.util.HashMap;\n"
              + "import java.util.Map;\n"
              + "import java.util.Scanner;\n"
              + "import java.util.logging.Level;\n"
              + "import java.util.logging.Logger;\n"
              + "\n"
              + "/**\n"
              + " *\n"
              + " * @author Thomas Struller-Baumann <thomas at struller-baumann.de>\n"
              + " */\n"
              + "public class JavaSourceExaminer {\n"
              + "\n";
   }

   // TODO auslagern siehe auch JavaSourceExaminerTest
   private String getTestSourceCodeBody() {
      return "   private JavaSourceContainer javaSourceContainer;\n"
              + "    private static class JavaSourceExaminerHolder {\n"
              + "        private static final JavaSourceExaminer INSTANCE = new JavaSourceExaminer();\n"
              + "    }\n"
              + "    private JavaSourceExaminer() {\n"
              + "    }\n"
              + "    public static JavaSourceExaminer getInstance() {\n"
              + "        return JavaSourceExaminerHolder.INSTANCE;\n"
              + "    }\n"
              + "}\n";
   }
}

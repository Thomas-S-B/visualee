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
package de.strullerbaumann.visualee.examiner;

import de.strullerbaumann.visualee.dependency.entity.Dependency;
import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.javasource.entity.JavaSource;
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
      assertTrue(cdiExaminer.isRelevantType(DependencyType.EJB));
      assertTrue(cdiExaminer.isRelevantType(DependencyType.EVENT));
      assertTrue(cdiExaminer.isRelevantType(DependencyType.INJECT));
      assertTrue(cdiExaminer.isRelevantType(DependencyType.INSTANCE));
      assertTrue(cdiExaminer.isRelevantType(DependencyType.OBSERVES));
      assertTrue(cdiExaminer.isRelevantType(DependencyType.PRODUCES));
      assertFalse(cdiExaminer.isRelevantType(DependencyType.MANY_TO_MANY));
      assertFalse(cdiExaminer.isRelevantType(DependencyType.MANY_TO_ONE));
      assertFalse(cdiExaminer.isRelevantType(DependencyType.ONE_TO_MANY));
      assertFalse(cdiExaminer.isRelevantType(DependencyType.ONE_TO_ONE));
   }

   @Test
   public void testFindAndSetAttributesObserves() {
      CDIExaminer cdiExaminer = new CDIExaminer();
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
      cdiExaminer.examine(javaSource);
      dependency = javaSource.getInjected().get(0);
      assertEquals(1, javaSource.getInjected().size());
      assertEquals(DependencyType.OBSERVES, dependency.getDependencyType());
      assertEquals("SnapshotEscalator", dependency.getJavaSourceFrom().getName());
      assertEquals("Snapshot", dependency.getJavaSourceTo().getName());
   }

   @Test
   public void testFindAndSetAttributesProduces() {
      CDIExaminer cdiExaminer = new CDIExaminer();
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
      cdiExaminer.examine(javaSource);
      dependency = javaSource.getInjected().get(0);
      assertEquals(1, javaSource.getInjected().size());
      assertEquals(DependencyType.PRODUCES, dependency.getDependencyType());
      assertEquals("DatabaseProducer", dependency.getJavaSourceFrom().getName());
      assertEquals("EntityManager", dependency.getJavaSourceTo().getName());
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
      assertEquals(DependencyType.INJECT, dependency.getDependencyType());
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
      assertEquals(DependencyType.PRODUCES, dependency.getDependencyType());
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
      assertEquals(DependencyType.INJECT, dependency.getDependencyType());
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
}

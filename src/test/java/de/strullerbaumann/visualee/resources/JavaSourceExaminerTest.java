 /*
 * Created on 11.07.2013 - 11:19:01
 *
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.resources;

import de.strullerbaumann.visualee.cdi.CDIDependency;
import de.strullerbaumann.visualee.cdi.CDIType;
import java.util.Scanner;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class JavaSourceExaminerTest {

    public JavaSourceExaminerTest() {
    }

    @Test
    public void testCountChar() {
        String inputString;
        int actual = 0;

        inputString = "My test de(sciption((asddaads)fdf))saddassd";
        actual = JavaSourceExaminer.getInstance().countChar(inputString, '(');
        assertEquals(3, actual);
        actual = JavaSourceExaminer.getInstance().countChar(inputString, ')');
        assertEquals(3, actual);

        inputString = "(My te))(st (de(scip))tion((asddaads)fdf)))sad(dass)d)";
        actual = JavaSourceExaminer.getInstance().countChar(inputString, '(');
        assertEquals(7, actual);
        actual = JavaSourceExaminer.getInstance().countChar(inputString, ')');
        assertEquals(10, actual);
    }

    @Test
    public void testGetCDITypeFromLine() {
        String sourceLine;
        CDIType actual;

        sourceLine = "My test desciption";
        actual = JavaSourceExaminer.getInstance().getCDITypeFromLine(sourceLine);
        assertEquals(null, actual);

        sourceLine = "@EJB";
        actual = JavaSourceExaminer.getInstance().getCDITypeFromLine(sourceLine);
        assertEquals(CDIType.EJB, actual);

        sourceLine = "@EJB(name = \"java:global/test/test-ejb/TestService\", beanInterface = TestService.class)";
        actual = JavaSourceExaminer.getInstance().getCDITypeFromLine(sourceLine);
        assertEquals(CDIType.EJB, actual);

        sourceLine = "@Inject TestCalss myTestClass;";
        actual = JavaSourceExaminer.getInstance().getCDITypeFromLine(sourceLine);
        assertEquals(CDIType.INJECT, actual);

        sourceLine = "public void onEscalationBrowserRequest(@Observes Escalation escalation) {";
        actual = JavaSourceExaminer.getInstance().getCDITypeFromLine(sourceLine);
        assertEquals(CDIType.OBSERVES, actual);

        sourceLine = "@Produces";
        actual = JavaSourceExaminer.getInstance().getCDITypeFromLine(sourceLine);
        assertEquals(CDIType.PRODUCES, actual);

        sourceLine = "@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})";
        actual = JavaSourceExaminer.getInstance().getCDITypeFromLine(sourceLine);
        assertEquals(null, actual);
    }

    @Test
    public void testFindAndSetPackage() {
        JavaSource javaSource = new JavaSource("TestClass");
        javaSource.setSourceCode(getTestSourceCode());
        JavaSourceExaminer.getInstance().findAndSetPackage(javaSource);

        String expected = "de.strullerbaumann.visualee.resources";
        String actual = javaSource.getPackagePath();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetClassBody() {
        JavaSource javaSource = new JavaSource("TestClass");
        javaSource.setSourceCode(getTestSourceCode());

        String expected = getTestSourceCodeBody();
        String actual = JavaSourceExaminer.getInstance().getClassBody(javaSource.getSourceCode());

        assertEquals(expected, actual);
    }

    @Test
    public void testScanAfterClosedParenthesis() {
        JavaSource javaSource;
        String sourceCode;
        String actual;
        String expected;
        Scanner scanner;
        String currentToken;

        javaSource = new JavaSource("TestClass");
        sourceCode = "@NotNull(groups = PersistenceConstraint.class)\n"
                + "private Album album;\n";
        javaSource.setSourceCode(sourceCode);
        scanner = JavaSourceExaminer.getInstance().getSourceCodeScanner(javaSource.getSourceCode());
        currentToken = scanner.next(); // now @NotNull((groups
        JavaSourceExaminer.getInstance().scanAfterClosedParenthesis(currentToken, scanner);
        expected = "Album";
        actual = scanner.next();
        assertEquals(expected, actual);

        javaSource = new JavaSource("TestClass");
        sourceCode = "@NotNull((groups = PersistenceConstraint.class) saddas)\n"
                + "private Album2 album;\n";
        javaSource.setSourceCode(sourceCode);
        scanner = JavaSourceExaminer.getInstance().getSourceCodeScanner(javaSource.getSourceCode());
        currentToken = scanner.next(); // now @NotNull((groups
        JavaSourceExaminer.getInstance().scanAfterClosedParenthesis(currentToken, scanner);
        expected = "Album2";
        actual = scanner.next();
        assertEquals(expected, actual);
    }

    @Test
    public void testFindAndSetAttributesManyToOne() {
        JavaSource javaSource;
        CDIDependency dependency;
        String sourceCode;

        // Many to one
        javaSource = new JavaSource("MyTestClass");
        sourceCode = getTestSourceCodeBeforeBody()
                + "@ManyToOne(cascade = { CascadeType.DETACH })\n"
                + "@JoinColumn(name = \"ALBUMID\", nullable = false)\n"
                + "@NotNull(groups = PersistenceConstraint.class)\n"
                + "private Album album;\n";
        javaSource.setSourceCode(sourceCode);
        JavaSourceExaminer.getInstance().javaSourceContainer = new JavaSourceContainer();
        JavaSourceExaminer.getInstance().findAndSetAttributes(javaSource);
        dependency = javaSource.getInjected().get(0);
        assertEquals(1, javaSource.getInjected().size());
        assertEquals(CDIType.MANY_TO_ONE, dependency.getCdiType());
        assertEquals("MyTestClass", dependency.getJavaSourceFrom().getName());
        assertEquals("Album", dependency.getJavaSourceTo().getName());
    }

    @Test
    public void testFindAndSetAttributesManyToMany() {
        JavaSource javaSource;
        CDIDependency dependency;
        String sourceCode;

        // Many to many
        javaSource = new JavaSource("User");
        sourceCode = getTestSourceCodeBeforeBody()
                + "@ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.LAZY)\n"
                + "@JoinTable(name = \"USERS_GROUPS\", joinColumns = {\n"
                + "   @JoinColumn(name = \"USERID\", nullable = false)}, inverseJoinColumns = {\n"
                + "   @JoinColumn(name = \"GROUPID\", nullable = false)})\n"
                + "private Set<Group> groups;\n";
        javaSource.setSourceCode(sourceCode);
        JavaSourceExaminer.getInstance().javaSourceContainer = new JavaSourceContainer();
        JavaSourceExaminer.getInstance().findAndSetAttributes(javaSource);
        dependency = javaSource.getInjected().get(0);
        assertEquals(1, javaSource.getInjected().size());
        assertEquals(CDIType.MANY_TO_MANY, dependency.getCdiType());
        assertEquals("User", dependency.getJavaSourceFrom().getName());
        assertEquals("Group", dependency.getJavaSourceTo().getName());
    }

    @Test
    public void testFindAndSetAttributesObserves() {
        JavaSource javaSource;
        CDIDependency dependency;
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
        JavaSourceExaminer.getInstance().javaSourceContainer = new JavaSourceContainer();
        JavaSourceExaminer.getInstance().findAndSetAttributes(javaSource);
        dependency = javaSource.getInjected().get(0);
        assertEquals(1, javaSource.getInjected().size());
        assertEquals(CDIType.OBSERVES, dependency.getCdiType());
        assertEquals("SnapshotEscalator", dependency.getJavaSourceFrom().getName());
        assertEquals("Snapshot", dependency.getJavaSourceTo().getName());
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
                + "protected void setEntityManager(EntityManager entityManager) {\n"
                + "        this.entityManager = entityManager;\n"
                + "}\n";

        javaSource.setSourceCode(sourceCode);
        JavaSourceExaminer.getInstance().javaSourceContainer = new JavaSourceContainer();
        JavaSourceExaminer.getInstance().findAndSetAttributes(javaSource);
        assertEquals(1, javaSource.getInjected().size());

        CDIDependency dependency;
        dependency = javaSource.getInjected().get(0);
        assertEquals(CDIType.INJECT, dependency.getCdiType());
        assertEquals("MyTestClass", dependency.getJavaSourceFrom().getName());
        assertEquals("EntityManager", dependency.getJavaSourceTo().getName());
    }

    @Test
    public void testFindAndSetAttributesStaticInject() {
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
        JavaSourceExaminer.getInstance().javaSourceContainer = new JavaSourceContainer();
        JavaSourceExaminer.getInstance().findAndSetAttributes(javaSource);
        assertEquals(1, javaSource.getInjected().size());

        CDIDependency dependency;
        dependency = javaSource.getInjected().get(0);
        assertEquals(CDIType.PRODUCES, dependency.getCdiType());
        assertEquals("LoggerProducer", dependency.getJavaSourceFrom().getName());
        assertEquals("Log", dependency.getJavaSourceTo().getName());
    }

    @Test
    public void testFindAndSetAttributesIgnoreComments() {
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
        JavaSourceExaminer.getInstance().javaSourceContainer = new JavaSourceContainer();
        JavaSourceExaminer.getInstance().findAndSetAttributes(javaSource);
        assertEquals(0, javaSource.getInjected().size());
    }

    @Test
    public void testFindAndSetAttributesIgnoreCommentBlocks() {
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
        JavaSourceExaminer.getInstance().javaSourceContainer = new JavaSourceContainer();
        JavaSourceExaminer.getInstance().findAndSetAttributes(javaSource);
        assertEquals(0, javaSource.getInjected().size());
    }

    @Test
    public void testGetJavaSourceByName() {
        JavaSourceContainer javaSourceContainer = new JavaSourceContainer();
        JavaSource javaSource1 = new JavaSource("DataPoint");
        javaSourceContainer.add(javaSource1);
        JavaSource javaSource2 = new JavaSource("int");
        javaSourceContainer.add(javaSource2);
        JavaSource javaSource3 = new JavaSource("MyTestClass");
        javaSourceContainer.add(javaSource3);

        JavaSourceExaminer.getInstance().javaSourceContainer = javaSourceContainer;

        assertNotNull(JavaSourceExaminer.getInstance().getJavaSourceByName("MyTestClass"));
        assertEquals("MyTestClass", JavaSourceExaminer.getInstance().getJavaSourceByName("MyTestClass").getName());
        assertNotNull(JavaSourceExaminer.getInstance().getJavaSourceByName("int"));
        assertEquals("int", JavaSourceExaminer.getInstance().getJavaSourceByName("int").getName());
        assertNotNull(JavaSourceExaminer.getInstance().getJavaSourceByName("DataPoint"));
        assertEquals("DataPoint", JavaSourceExaminer.getInstance().getJavaSourceByName("DataPoint").getName());
    }

    private String getTestSourceCode() {
        return getTestSourceCodeBeforeBody() + getTestSourceCodeBody();
    }

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

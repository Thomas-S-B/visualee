package de.strullerbaumann.visualee.source.entity;

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
import de.strullerbaumann.visualee.testdata.TestDataProvider;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class JavaSourceTest {

   public JavaSourceTest() {
   }

   @Test
   public void testConstructor() {
      String expected = "test2myTestJavaFile";
      Path javaFile = Paths.get("/test1/" + expected + ".java");
      JavaSource javaSource = new JavaSource(javaFile);
      assertEquals(expected, javaSource.getName());
   }

   @Test
   public void testGetEscapedSourceCode() {

      String testSource = "public void escalate(@Observes @Severity(Severity.Level.HEARTBEAT) Snapshot current) {\n"
              + "      List<Script> scripts = this.scripting.activeScripts();\n"
              + "      try {";
      String expected = "public void escalate(@Observes @Severity(Severity.Level.HEARTBEAT) Snapshot current) {\n"
              + "      List&lt;Script&gt; scripts = this.scripting.activeScripts();\n"
              + "      try {";

      JavaSource javaSource = new JavaSource("TestSource");
      javaSource.setSourceCode(testSource);

      assertEquals(expected, javaSource.getEscapedSourceCode());
   }

   @Test
   public void testGetSourceCodeWithoutComments() {

      String testSource = "public void escalate(@Observes @Severity(Severity.Level.HEARTBEAT) Snapshot current) {\n"
              + "      // this is a comment\n"
              + "      List<Script> scripts = this.scripting.activeScripts();\n"
              + "      /* commentblock\n"
              + "      * \n"
              + "      * commentblock\n"
              + "      */\n"
              + "      //this is also a comment - give them a try: Gilad Hekselman - Split Life\n"
              + "      try {";
      String expected = "public void escalate(@Observes @Severity(Severity.Level.HEARTBEAT) Snapshot current) {\n"
              + "      List<Script> scripts = this.scripting.activeScripts();\n"
              + "      try {\n";

      JavaSource javaSource = new JavaSource("TestSource");
      javaSource.setSourceCode(testSource);

      assertEquals(expected, javaSource.getSourceCodeWithoutComments());
   }

   @Test
   public void testGetDependenciesOfType() {
      JavaSource javaSource1 = new JavaSource("Testclass01");
      JavaSource javaSource2 = new JavaSource("Testclass02");
      JavaSource javaSource3 = new JavaSource("Testclass03");
      JavaSource javaSource4 = new JavaSource("Testclass04");

      Dependency dependency1_2 = new Dependency(DependencyType.INJECT, javaSource1, javaSource2);
      Dependency dependency1_3 = new Dependency(DependencyType.INJECT, javaSource1, javaSource3);
      Dependency dependency1_4 = new Dependency(DependencyType.EVENT, javaSource1, javaSource4);
      DependencyContainer.getInstance().add(dependency1_2);
      DependencyContainer.getInstance().add(dependency1_3);
      DependencyContainer.getInstance().add(dependency1_4);

      assertEquals(2, DependencyContainer.getInstance().getDependenciesOfType(DependencyType.INJECT).size());
      assertEquals(1, DependencyContainer.getInstance().getDependenciesOfType(DependencyType.EVENT).size());
      assertEquals(0, DependencyContainer.getInstance().getDependenciesOfType(DependencyType.EJB).size());
   }

   @Test
   public void testFindAndSetPackage() {
      JavaSource javaSource = new JavaSource("TestClass");
      javaSource.setSourceCode(TestDataProvider.getTestSourceCode());
      javaSource.findAndSetPackage();

      String expected = "de.strullerbaumann.visualee.resources";
      String actual = javaSource.getPackagePath();

      assertEquals(expected, actual);
   }

   @Test
   public void testFindAndSetPackageHarder() {
      JavaSource javaSource;
      String sourceCode;

      javaSource = new JavaSource("MyTestClass");
      sourceCode = "package de.test1.test2.test3;\n"
              + "public class MyTestClass {\n"
              + "private Class<E> entityClass;\n"
              + "}\n";
      javaSource.setSourceCode(sourceCode);
      javaSource.findAndSetPackage();
      assertEquals("de.test1.test2.test3", javaSource.getPackagePath());

      //Ignore token package which is not defining a package
      javaSource = new JavaSource("MyTestClass");
      sourceCode = "// package as a comment\n"
              + "package de.test1.test2.test3.test4;\n"
              + "public class MyTestClass {\n"
              + "private Class<E> entityClass;\n"
              + "}\n";
      javaSource.setSourceCode(sourceCode);
      javaSource.findAndSetPackage();
      assertEquals("de.test1.test2.test3.test4", javaSource.getPackagePath());
   }

   @Test(expected = IllegalArgumentException.class)
   public void testFindAndSetPackageInsufficientTokens() {
      JavaSource javaSource;
      String sourceCode;

      javaSource = new JavaSource("MyTestClass");
      sourceCode = "package";
      javaSource.setSourceCode(sourceCode);
      javaSource.findAndSetPackage();
   }

}

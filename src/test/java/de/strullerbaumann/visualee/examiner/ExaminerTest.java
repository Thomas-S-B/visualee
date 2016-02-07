package de.strullerbaumann.visualee.examiner;

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
import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.filter.boundary.FilterContainer;
import de.strullerbaumann.visualee.source.entity.JavaSource;
import de.strullerbaumann.visualee.source.entity.JavaSourceFactory;
import de.strullerbaumann.visualee.testdata.TestDataProvider;
import java.util.Scanner;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class ExaminerTest {

   public ExaminerTest() {
   }

   @BeforeClass
   public static void setUpClass() {
      FilterContainer.getInstance().clear();
   }

   @Test
   public void testIsRelevantType() {
   }

   @Test
   public void testGetSourceCodeScanner() {
   }

   @Test
   public void testisAJavaToken() {
      String inputString;
      boolean actual;

      inputString = "Joshua Redman - Compass";
      actual = ExaminerImpl.isAJavaToken(inputString);
      assertEquals(false, actual);

      inputString = "void";
      actual = ExaminerImpl.isAJavaToken(inputString);
      assertEquals(true, actual);

      inputString = "static";
      actual = ExaminerImpl.isAJavaToken(inputString);
      assertEquals(true, actual);

      inputString = "@Annoation";
      actual = ExaminerImpl.isAJavaToken(inputString);
      assertEquals(true, actual);
   }

   @Test
   public void testCleanPrimitives() {
      String inputString;
      String actual;

      inputString = "boolean";
      actual = ExaminerImpl.cleanPrimitives(inputString);
      assertEquals("Boolean", actual);

      inputString = "char";
      actual = ExaminerImpl.cleanPrimitives(inputString);
      assertEquals("Character", actual);

      inputString = "byte";
      actual = ExaminerImpl.cleanPrimitives(inputString);
      assertEquals("Byte", actual);

      inputString = "short";
      actual = ExaminerImpl.cleanPrimitives(inputString);
      assertEquals("Short", actual);

      inputString = "int";
      actual = ExaminerImpl.cleanPrimitives(inputString);
      assertEquals("Integer", actual);

      inputString = "long";
      actual = ExaminerImpl.cleanPrimitives(inputString);
      assertEquals("Long", actual);

      inputString = "float";
      actual = ExaminerImpl.cleanPrimitives(inputString);
      assertEquals("Float", actual);

      inputString = "double";
      actual = ExaminerImpl.cleanPrimitives(inputString);
      assertEquals("Double", actual);

      inputString = "doubleTrouble";
      actual = ExaminerImpl.cleanPrimitives(inputString);
      assertEquals("doubleTrouble", actual);

      inputString = "floatInTheBoat";
      actual = ExaminerImpl.cleanPrimitives(inputString);
      assertEquals("floatInTheBoat", actual);

      inputString = "Short";
      actual = ExaminerImpl.cleanPrimitives(inputString);
      assertEquals("Short", actual);

      inputString = "";
      actual = ExaminerImpl.cleanPrimitives(inputString);
      assertEquals("", actual);

      inputString = "int[]";
      actual = ExaminerImpl.cleanPrimitives(inputString);
      assertEquals("Integer[]", actual);
   }

   @Test
   public void testisAValdiClassName() {
      String inputString;
      boolean actual;

      inputString = "String";
      actual = ExaminerImpl.isAValidClassName(inputString);
      assertEquals(true, actual);

      inputString = "int";
      actual = ExaminerImpl.isAValidClassName(inputString);
      assertEquals(false, actual);

      inputString = "Integer";
      actual = ExaminerImpl.isAValidClassName(inputString);
      assertEquals(true, actual);

      inputString = "boolean";
      actual = ExaminerImpl.isAValidClassName(inputString);
      assertEquals(false, actual);

      inputString = ";";
      actual = ExaminerImpl.isAValidClassName(inputString);
      assertEquals(false, actual);
   }

   @Test
   public void testGetClassBody() {
      JavaSource javaSource = JavaSourceFactory.getInstance().newJavaSource("TestClass");
      javaSource.setSourceCode(TestDataProvider.getTestSourceCode());

      String expected = TestDataProvider.getTestSourceCodeBody();
      String actual = ExaminerImpl.getClassBody(javaSource.getSourceCode());

      assertEquals(expected, actual);
   }

   @Test
   public void testCountChar() {
      String inputString;
      int actual;

      inputString = "My test de(sciption((Lage Lund - Standards)fdf))saddassd";
      actual = ExaminerImpl.countChar(inputString, '(');
      assertEquals(3, actual);
      actual = ExaminerImpl.countChar(inputString, ')');
      assertEquals(3, actual);

      inputString = "(My te))(st (de(scip))tion((asddaads)fdf)))sad(dass)d)";
      actual = ExaminerImpl.countChar(inputString, '(');
      assertEquals(7, actual);
      actual = ExaminerImpl.countChar(inputString, ')');
      assertEquals(10, actual);

      inputString = "My test de(sciption((Lage Lund - Standards)fdf))saddassd";
      actual = ExaminerImpl.countChar(inputString, 'X');
      assertEquals(0, actual);
   }

   @Test
   public void testScanAfterQuote() {
      JavaSource javaSource;
      String sourceCode;
      String actual;
      String expected;
      Scanner scanner;
      String currentToken;

      javaSource = JavaSourceFactory.getInstance().newJavaSource("TestClass");
      sourceCode = "out.println(\"<h1>UserTransaction obtained using @Inject</h1>\");\n"
              + "mytoken";
      javaSource.setSourceCode(sourceCode);
      scanner = Examiner.getSourceCodeScanner(javaSource.getSourceCode());
      currentToken = scanner.next(); // now out.println(\"<h1>UserTransaction
      ExaminerImpl.scanAfterQuote(currentToken, scanner);
      expected = "mytoken";
      actual = scanner.next();
      assertEquals(expected, actual);

      javaSource = JavaSourceFactory.getInstance().newJavaSource("TestClass");
      sourceCode = "out.println(\"<title>UserTransaction obtained using @Inject</title>\");\n"
              + "out.println(\"<h1>UserTransaction obtained using @Inject</h1>);\n"
              + "mytoken";
      javaSource.setSourceCode(sourceCode);
      scanner = Examiner.getSourceCodeScanner(javaSource.getSourceCode());
      currentToken = scanner.next(); // now out.println(\"<h1>UserTransaction\"
      ExaminerImpl.scanAfterQuote(currentToken, scanner);
      expected = "out.println(\"<h1>UserTransaction";
      actual = scanner.next();
      assertEquals(expected, actual);

      javaSource = JavaSourceFactory.getInstance().newJavaSource("TestClass");
      sourceCode = "@NotNull((groups = PersistenceConstraint.class) saddas)\n"
              + "private Album2 album;\n";
      javaSource.setSourceCode(sourceCode);
      scanner = Examiner.getSourceCodeScanner(javaSource.getSourceCode());
      currentToken = scanner.next(); // now @NotNull((groups
      ExaminerImpl.scanAfterQuote(currentToken, scanner);
      expected = "=";
      actual = scanner.next();
      assertEquals(expected, actual);

      javaSource = JavaSourceFactory.getInstance().newJavaSource("TestClass");
      sourceCode = "LOG.log(Level.FINE, \"Added {0} to watch channel {1}\", new Object[]{browserWindow.hashCode(), browserWindow.getChannel()});";
      javaSource.setSourceCode(sourceCode);
      scanner = Examiner.getSourceCodeScanner(javaSource.getSourceCode());
      scanner.next(); //LOG.log(Level.FINE,
      currentToken = scanner.next(); //"Added...
      ExaminerImpl.scanAfterQuote(currentToken, scanner);
      expected = "new";
      actual = scanner.next();
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

      javaSource = JavaSourceFactory.getInstance().newJavaSource("TestClass");
      sourceCode = "@NotNull(groups = PersistenceConstraint.class)\n"
              + "private Album album;\n";
      javaSource.setSourceCode(sourceCode);
      scanner = Examiner.getSourceCodeScanner(javaSource.getSourceCode());
      currentToken = scanner.next(); // now @NotNull((groups
      ExaminerImpl.scanAfterClosedParenthesis(currentToken, scanner);
      expected = "Album";
      actual = scanner.next();
      assertEquals(expected, actual);

      javaSource = JavaSourceFactory.getInstance().newJavaSource("TestClass");
      sourceCode = "@NotNull((groups = PersistenceConstraint.class) saddas)\n"
              + "private Album2 album;\n";
      javaSource.setSourceCode(sourceCode);
      scanner = Examiner.getSourceCodeScanner(javaSource.getSourceCode());
      currentToken = scanner.next(); // now @NotNull((groups
      ExaminerImpl.scanAfterClosedParenthesis(currentToken, scanner);
      expected = "Album2";
      actual = scanner.next();   // scan after private
      assertEquals(expected, actual);

      javaSource = JavaSourceFactory.getInstance().newJavaSource("TestClass");
      sourceCode = "@Resource(mappedName=\"java:global/jms/myQueue2\")\n"
              + "private Album2 album;\n";
      javaSource.setSourceCode(sourceCode);
      scanner = Examiner.getSourceCodeScanner(javaSource.getSourceCode());
      currentToken = scanner.next();
      actual = ExaminerImpl.scanAfterClosedParenthesis(currentToken, scanner);
      expected = "private";
      assertEquals(expected, actual);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testScanAfterClosedParenthesisInsufficientTokens() {
      JavaSource javaSource;
      String sourceCode;
      Scanner scanner;
      String currentToken;

      javaSource = JavaSourceFactory.getInstance().newJavaSource("TestClass");
      sourceCode = "@NotNull(groups";
      javaSource.setSourceCode(sourceCode);
      scanner = Examiner.getSourceCodeScanner(javaSource.getSourceCode());
      currentToken = scanner.next(); // now @NotNull((groups
      ExaminerImpl.scanAfterClosedParenthesis(currentToken, scanner);
   }

   @Test
   public void testJumpOverJavaToken() {
      JavaSource javaSource;
      String sourceCode;
      String actual;
      String expected;
      Scanner scanner;
      String currentToken;

      javaSource = JavaSourceFactory.getInstance().newJavaSource("TestClass");
      sourceCode = "@NotNull(groups = PersistenceConstraint.class)\n"
              + "private Album album;\n";
      javaSource.setSourceCode(sourceCode);
      scanner = Examiner.getSourceCodeScanner(javaSource.getSourceCode());
      currentToken = scanner.next(); // now @NotNull((groups
      expected = "Album";
      actual = ExaminerImpl.jumpOverJavaToken(currentToken, scanner);
      assertEquals(expected, actual);

      javaSource = JavaSourceFactory.getInstance().newJavaSource("TestClass");
      sourceCode = "@NotNull((groups = PersistenceConstraint.class) saddas)\n"
              + "protected Album2 album;\n";
      javaSource.setSourceCode(sourceCode);
      scanner = Examiner.getSourceCodeScanner(javaSource.getSourceCode());
      currentToken = scanner.next(); // now @NotNull((groups
      expected = "Album2";
      actual = ExaminerImpl.jumpOverJavaToken(currentToken, scanner);
      assertEquals(expected, actual);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testJumpOverTokenInsufficientTokens() {
      JavaSource javaSource;
      String sourceCode;
      Scanner scanner;
      String currentToken;

      javaSource = JavaSourceFactory.getInstance().newJavaSource("TestClass");
      sourceCode = "public";
      javaSource.setSourceCode(sourceCode);
      scanner = Examiner.getSourceCodeScanner(javaSource.getSourceCode());
      currentToken = scanner.next(); // now public

      ExaminerImpl.jumpOverJavaToken(currentToken, scanner);
   }

   @Test
   public void testCleanupGeneric() {
      String inputString;
      String actual;

      inputString = "DataCollector<?";
      actual = ExaminerImpl.cleanupGeneric(inputString);
      assertEquals("DataCollector", actual);

      inputString = "DataCollector";
      actual = ExaminerImpl.cleanupGeneric(inputString);
      assertEquals("DataCollector", actual);
   }

   @Test
   public void testExtractClassInstanceOrEvent() {
      String inputString;
      String actual;

      inputString = "Instance<TestClassInstance>";
      actual = ExaminerImpl.extractClassInstanceOrEvent(inputString);
      assertEquals("TestClassInstance", actual);

      inputString = "Event<TestClassEvent>";
      actual = ExaminerImpl.extractClassInstanceOrEvent(inputString);
      assertEquals("TestClassEvent", actual);

      inputString = "TestClass";
      actual = ExaminerImpl.cleanupGeneric(inputString);
      assertEquals("TestClass", actual);
   }

   public class ExaminerImpl extends Examiner {

      @Override
      public boolean isRelevantType(DependencyType cdiType) {
         return false;
      }

      @Override
      public void examine(JavaSource javaSource) {
      }

      @Override
      protected DependencyType getTypeFromToken(String token) {
         return null;
      }

      @Override
      protected void examineDetail(JavaSource javaSource, Scanner scanner, String token, DependencyType type) {
      }
   }
}

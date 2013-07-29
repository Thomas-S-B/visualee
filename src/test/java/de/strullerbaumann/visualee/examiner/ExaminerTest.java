/*
 * Created on 29.07.2013 - 15:33:13
 *
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.examiner;

import de.strullerbaumann.visualee.dependency.DependenciyType;
import de.strullerbaumann.visualee.resources.JavaSource;
import java.util.Scanner;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class ExaminerTest {

   public ExaminerTest() {
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

      inputString = "asddaads";
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
   public void testGetClassBody() {
      JavaSource javaSource = new JavaSource("TestClass");
      javaSource.setSourceCode(getTestSourceCode());

      String expected = getTestSourceCodeBody();
      String actual = ExaminerImpl.getClassBody(javaSource.getSourceCode());

      assertEquals(expected, actual);
   }

   @Test
   public void testGetCDITypeFromLine() {
      String sourceLine;
      DependenciyType actual;

      sourceLine = "My test desciption";
      actual = ExaminerImpl.getTypeFromToken(sourceLine);
      assertEquals(null, actual);

      sourceLine = "@EJB";
      actual = ExaminerImpl.getTypeFromToken(sourceLine);
      assertEquals(DependenciyType.EJB, actual);

      sourceLine = "@EJB(name = \"java:global/test/test-ejb/TestService\", beanInterface = TestService.class)";
      actual = ExaminerImpl.getTypeFromToken(sourceLine);
      assertEquals(DependenciyType.EJB, actual);

      sourceLine = "@Inject TestCalss myTestClass;";
      actual = ExaminerImpl.getTypeFromToken(sourceLine);
      assertEquals(DependenciyType.INJECT, actual);

      sourceLine = "public void onEscalationBrowserRequest(@Observes Escalation escalation) {";
      actual = ExaminerImpl.getTypeFromToken(sourceLine);
      assertEquals(DependenciyType.OBSERVES, actual);

      sourceLine = "@Produces";
      actual = ExaminerImpl.getTypeFromToken(sourceLine);
      assertEquals(DependenciyType.PRODUCES, actual);

      sourceLine = "@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})";
      actual = ExaminerImpl.getTypeFromToken(sourceLine);
      assertEquals(null, actual);
   }

   @Test
   public void testCountChar() {
      String inputString;
      int actual;

      inputString = "My test de(sciption((asddaads)fdf))saddassd";
      actual = ExaminerImpl.countChar(inputString, '(');
      assertEquals(3, actual);
      actual = ExaminerImpl.countChar(inputString, ')');
      assertEquals(3, actual);

      inputString = "(My te))(st (de(scip))tion((asddaads)fdf)))sad(dass)d)";
      actual = ExaminerImpl.countChar(inputString, '(');
      assertEquals(7, actual);
      actual = ExaminerImpl.countChar(inputString, ')');
      assertEquals(10, actual);
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
      scanner = Examiner.getSourceCodeScanner(javaSource.getSourceCode());
      currentToken = scanner.next(); // now @NotNull((groups
      ExaminerImpl.scanAfterClosedParenthesis(currentToken, scanner);
      expected = "Album";
      actual = scanner.next();
      assertEquals(expected, actual);

      javaSource = new JavaSource("TestClass");
      sourceCode = "@NotNull((groups = PersistenceConstraint.class) saddas)\n"
              + "private Album2 album;\n";
      javaSource.setSourceCode(sourceCode);
      scanner = Examiner.getSourceCodeScanner(javaSource.getSourceCode());
      currentToken = scanner.next(); // now @NotNull((groups
      ExaminerImpl.scanAfterClosedParenthesis(currentToken, scanner);
      expected = "Album2";
      actual = scanner.next();
      assertEquals(expected, actual);
   }

   public class ExaminerImpl extends Examiner {

      @Override
      public boolean isRelevantType(DependenciyType cdiType) {
         return false;
      }

      @Override
      public void examine(JavaSource javaSource) {
      }
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

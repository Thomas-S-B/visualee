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

import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.javasource.entity.JavaSource;
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
      javaSource.setSourceCode(SourceCodeProvider.getTestSourceCode());

      String expected = SourceCodeProvider.getTestSourceCodeBody();
      String actual = ExaminerImpl.getClassBody(javaSource.getSourceCode());

      assertEquals(expected, actual);
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

   @Test
   public void testJumpOverJavaToken() {
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
      expected = "Album";
      actual = ExaminerImpl.jumpOverJavaToken(currentToken, scanner);
      assertEquals(expected, actual);

      javaSource = new JavaSource("TestClass");
      sourceCode = "@NotNull((groups = PersistenceConstraint.class) saddas)\n"
              + "protected Album2 album;\n";
      javaSource.setSourceCode(sourceCode);
      scanner = Examiner.getSourceCodeScanner(javaSource.getSourceCode());
      currentToken = scanner.next(); // now @NotNull((groups
      expected = "Album2";
      actual = ExaminerImpl.jumpOverJavaToken(currentToken, scanner);
      assertEquals(expected, actual);
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
   }
}

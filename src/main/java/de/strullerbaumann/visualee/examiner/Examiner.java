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
import de.strullerbaumann.visualee.source.boundary.JavaSourceContainer;
import de.strullerbaumann.visualee.source.entity.JavaSource;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public abstract class Examiner {

   private static final String[] JAVA_TOKENS = {
      "void",
      "private",
      "protected",
      "transient",
      "public",
      "static",
      "@"
   };

   public Examiner() {
      JavaSourceInspector.getInstance().registerExaminer(this);
   }

   protected abstract boolean isRelevantType(DependencyType type);

   protected abstract DependencyType getTypeFromToken(String token);

   public abstract void examine(JavaSource javaSource);

   protected static Scanner getSourceCodeScanner(String sourceCode) {
      Scanner scanner = new Scanner(sourceCode);
      scanner.useDelimiter("[ \t\r\n]+");
      return scanner;
   }

   protected static String getClassBody(String sourceCode) {
      StringBuilder classBody = new StringBuilder();
      boolean isInBodyNow = false;
      try (Scanner scanner = new Scanner(sourceCode)) {
         scanner.useDelimiter("[\n]+");
         while (scanner.hasNext()) {
            String token = scanner.next();
            if (!isInBodyNow) {
               if (token.indexOf("class ") > -1) {
                  isInBodyNow = true;
               }
            } else {
               classBody.append(token).append("\n");
            }
         }
      }

      return classBody.toString();
   }

   protected static int countChar(String string, char char2Find) {
      int count = 0;
      for (int i = 0; i < string.length(); i++) {
         if (string.charAt(i) == char2Find) {
            count++;
         }
      }
      return count;
   }

   // TODO better
   protected static String scanAfterQuote(String currentToken, Scanner scanner) {
      String token = currentToken;
      if (token.contains("\"") && countChar(token, '"') < 2) {
         token = scanner.next();
         while (!token.contains("\"")) {
            if (scanner.hasNext()) {
               token = scanner.next();
            } else {
               break;
            }
         }
      }
      return token;
   }

   protected static String scanAfterClosedParenthesis(String currentToken, Scanner scanner) {
      int countParenthesisOpen = countChar(currentToken, '(');
      int countParenthesisClose = countChar(currentToken, ')');

      if (countParenthesisOpen == countParenthesisClose) {
         return currentToken;
      }

      Deque<Integer> stack = new ArrayDeque<>();
      for (int iCount = 0; iCount < countParenthesisOpen - countParenthesisClose; iCount++) {
         stack.push(1);
      }
      String token = scanner.next();

      whilestack:
      do {
         for (Examiner examiner : JavaSourceInspector.getInstance().getExaminers()) {
            if (examiner.getTypeFromToken(token) != null) {
               break whilestack;
            }
         }
         if (token.indexOf('(') > -1) {
            for (int iCount = 0; iCount < countChar(token, '('); iCount++) {
               stack.push(1);
            }
         }
         if (token.indexOf(')') > -1) {
            for (int iCount = 0; iCount < countChar(token, ')'); iCount++) {
               stack.pop();
            }
         }
         if (scanner.hasNext()) {
            token = scanner.next();
         } else {
            break whilestack;
         }
      } while (stack.size() > 0);

      return token;
   }

   protected void createDependency(String className, DependencyType type, JavaSource javaSource) {
      JavaSource injectedJavaSource = JavaSourceContainer.getInstance().getJavaSourceByName(className);
      if (injectedJavaSource == null) {
         // Generate a new JavaSource, which is not explicit in the sources (e.g. Integer, String etc.)
         injectedJavaSource = new JavaSource(className);
         JavaSourceContainer.getInstance().add(injectedJavaSource);
      }
      Dependency dependency = new Dependency(type, javaSource, injectedJavaSource);
      javaSource.getInjected().add(dependency);
   }

   protected static boolean isAJavaToken(String token) {
      for (String javaToken : JAVA_TOKENS) {
         if (token.indexOf(javaToken) > - 1) {
            return true;
         }
      }
      return false;
   }

   protected static String jumpOverJavaToken(String token, Scanner scanner) {
      String nextToken = token;
      while (isAJavaToken(nextToken)) {
         if (nextToken.startsWith("@") && nextToken.indexOf('(') > -1 && !nextToken.endsWith(")")) {
            nextToken = scanAfterClosedParenthesis(nextToken, scanner);
         } else {
            nextToken = scanner.next();
         }
      }
      return nextToken;
   }

   protected static void findAndSetPackage(JavaSource javaSource) {
      Scanner scanner = Examiner.getSourceCodeScanner(javaSource.getSourceCode());
      while (scanner.hasNext()) {
         String token = scanner.next();
         if (javaSource.getPackagePath() == null && token.equals("package")) {
            token = scanner.next();
            String packagePath = token.substring(0, token.indexOf(';'));
            javaSource.setPackagePath(packagePath);
         }
      }
   }

   protected static String cleanupGeneric(String className) {
      //clears Genericsyntax from a classname
      //e.g. "DataCollector<?" becomes "DataCollector"
      String cleanedName = className;
      int posGeneric = className.indexOf('<');
      if (posGeneric > -1) {
         cleanedName = className.substring(0, posGeneric);
      }

      return cleanedName;
   }
}

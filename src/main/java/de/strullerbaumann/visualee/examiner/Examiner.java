/*
 * Created on 29.07.2013 - 14:36:09
 *
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.examiner;

import de.strullerbaumann.visualee.dependency.DependenciyType;
import de.strullerbaumann.visualee.dependency.Dependency;
import de.strullerbaumann.visualee.resources.JavaSource;
import de.strullerbaumann.visualee.resources.JavaSourceContainer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public abstract class Examiner {

   protected abstract boolean isRelevantType(DependenciyType type);

   public abstract void examine(JavaSource javaSource);

   protected static Scanner getSourceCodeScanner(String sourceCode) {
      Scanner scanner = new Scanner(sourceCode);
      scanner.useDelimiter("[ \t\r\n]+");
      return scanner;
   }

   protected static boolean isAJavaToken(String line) {
      String[] javaTokens = {"void", "private", "protected", "transient", "public", "static", "@"};

      for (String javaToken : javaTokens) {
         if (line.indexOf(javaToken) > - 1) {
            return true;
         }
      }

      return false;
   }

   protected static String getClassBody(String sourceCode) {
      // todo evtl doch interface class abstract abfragen kann ja ein produces mit { sein
      StringBuilder classBody = new StringBuilder();
      boolean isInBodyNow = false;
      try (Scanner scanner = new Scanner(sourceCode)) {
         scanner.useDelimiter("[\n]+");
         while (scanner.hasNext()) {
            String line = scanner.next();
            if (!isInBodyNow) {
               // In Class/Interface-Body?
               if (line.indexOf('{') > -1) {
                  isInBodyNow = true;
               }
            } else {
               classBody.append(line).append("\n");
            }
         }
      }

      return classBody.toString();
   }

   protected static DependenciyType getTypeFromLine(String line) {
      DependenciyType type = null;
      if (line.indexOf("@EJB") > -1) {
         type = DependenciyType.EJB;
      }
      if (line.indexOf("@Inject") > -1) {
         type = DependenciyType.INJECT;
      }
      if (line.indexOf("@Observes") > -1) {
         type = DependenciyType.OBSERVES;
      }
      // Identify @Produces form Inject (@Produces form WS is @Produces(...)
      // Inject: http://docs.oracle.com/javaee/6/api/javax/enterprise/inject/Produces.html
      // WS: http://docs.oracle.com/javaee/6/api/javax/ws/rs/Produces.html
      if (line.indexOf("@Produces") > -1 && line.indexOf("@Produces(") < 0) {
         type = DependenciyType.PRODUCES;
      }
      if (line.indexOf("@OneToOne") > -1) {
         type = DependenciyType.ONE_TO_ONE;
      }
      if (line.indexOf("@OneToMany") > -1) {
         type = DependenciyType.ONE_TO_MANY;
      }
      if (line.indexOf("@ManyToOne") > -1) {
         type = DependenciyType.MANY_TO_ONE;
      }
      if (line.indexOf("@ManyToMany") > -1) {
         type = DependenciyType.MANY_TO_MANY;
      }
      return type;
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

   protected static String scanAfterClosedParenthesis(String currentToken, Scanner scanner) {
      Deque<Integer> stack = new ArrayDeque<>();
      int iStack = 1;

      int countParenthesis = countChar(currentToken, '(');
      for (int iCount = 0; iCount < countParenthesis; iCount++) {
         stack.push(iStack);
         iStack++;
      }
      String line = scanner.next();
      boolean bEnd = false;
      while (stack.size() > 0 && !bEnd) {
         if (getTypeFromLine(line) != null) {
            break;
         }
         if (line.indexOf('(') > -1) {
            int countOpenParenthesis = countChar(line, '(');
            for (int iCount = 0; iCount < countOpenParenthesis; iCount++) {
               stack.push(iStack);
               iStack++;
            }
         }
         if (line.indexOf(')') > -1) {
            int countClosedParenthesis = countChar(line, ')');
            for (int iCount = 0; iCount < countClosedParenthesis; iCount++) {
               stack.pop();
               iStack++;
            }
         }
         if (scanner.hasNext()) {
            line = scanner.next();
         } else {
            bEnd = true;
         }
         iStack++;
      }

      return line;
   }

   protected void createDependency(String className, DependenciyType type, JavaSource javaSource) {
      // Create the dependency
      JavaSource injectedJavaSource = JavaSourceContainer.getInstance().getJavaSourceByName(className);
      if (injectedJavaSource == null) {
         // Generate a new JavaSource, which is not explicit in the sources (e.g. Integer, String etc.)
         injectedJavaSource = new JavaSource(className);
         JavaSourceContainer.getInstance().add(injectedJavaSource);
         Logger.getLogger(Examiner.class.getName()).log(Level.INFO, "+++ generated new javasource: {0}", className);
      }
      Dependency dependency = new Dependency(type, javaSource, injectedJavaSource);
      javaSource.getInjected().add(dependency);
   }

   // TODO Unittest
   protected String jumpOverJavaToken(String line, Scanner scanner) {
      String token = line;
      while (isAJavaToken(token)) {
         token = scanner.next();
      }
      return token;
   }
}

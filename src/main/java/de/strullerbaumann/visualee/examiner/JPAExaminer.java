/*
 * Created on 29.07.2013 - 14:32:48
 *
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.examiner;

import de.strullerbaumann.visualee.dependency.DependenciyType;
import de.strullerbaumann.visualee.resources.JavaSource;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class JPAExaminer extends Examiner {

   @Override
   public void examine(JavaSource javaSource) {
      // Examine class body
      try (Scanner scanner = getSourceCodeScanner(getClassBody(javaSource.getSourceCodeWithoutComments()))) {
         while (scanner.hasNext()) {
            String line = scanner.next();
            DependenciyType type = getTypeFromLine(line);
            if (isRelevantType(type)) {
               // Find the associated Class
               if (line.indexOf('(') > - 1) {
                  line = scanAfterClosedParenthesis(line, scanner);
               }
               while (scanner.hasNext() && (isAJavaToken(line))) {
                  // possible tokens now in line are e.g. Principal, Greeter(PhraseBuilder, Event<Person>, AsyncService ...
                  if (line.indexOf('(') > - 1) {
                     line = scanAfterClosedParenthesis(line, scanner);
                  } else {
                     line = scanner.next();
                  }
               }

               if (line.indexOf('<') > - 1 && line.indexOf('>') > - 1) {
                  // e.g. Set<Group> becomes to Group
                  line = line.substring(line.indexOf('<') + 1, line.indexOf('>'));
               }
               createDependency(line, type, javaSource);
            }
         }
      }

   }

   @Override
   protected boolean isRelevantType(DependenciyType type) {
      return Arrays.asList(
              DependenciyType.ONE_TO_MANY,
              DependenciyType.ONE_TO_ONE,
              DependenciyType.MANY_TO_ONE,
              DependenciyType.MANY_TO_MANY).contains(type);
   }
}

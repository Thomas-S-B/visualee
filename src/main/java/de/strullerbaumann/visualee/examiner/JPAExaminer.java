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
            String token = scanner.next();
            DependenciyType type = getTypeFromToken(token);
            if (isRelevantType(type)) {
               // Find the associated Class
               if (token.indexOf('(') > - 1) {
                  token = scanAfterClosedParenthesis(token, scanner);
               }
               while (scanner.hasNext() && (isAJavaToken(token))) {
                  // possible tokens now are e.g. Principal, Greeter(PhraseBuilder, Event<Person>, AsyncService ...
                  if (token.indexOf('(') > - 1) {
                     token = scanAfterClosedParenthesis(token, scanner);
                  } else {
                     token = scanner.next();
                  }
               }

               if (token.indexOf('<') > - 1 && token.indexOf('>') > - 1) {
                  // e.g. Set<Group> becomes to Group
                  token = token.substring(token.indexOf('<') + 1, token.indexOf('>'));
               }
               createDependency(token, type, javaSource);
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

/*
 * Created on 29.07.2013 - 14:46:04
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
public class CDIExaminer extends Examiner {

   @Override
   public void examine(JavaSource javaSource) {
      try (Scanner scanner = getSourceCodeScanner(getClassBody(javaSource.getSourceCodeWithoutComments()))) {
         while (scanner.hasNext()) {
            String token = scanner.next();
            DependenciyType type = getTypeFromToken(token);
            if (isRelevantType(type)) {
               token = scanner.next();
               token = jumpOverJavaToken(token, scanner);

               // possible tokens now are e.g. Principal, Greeter(PhraseBuilder, Event<Person>, AsyncService ...
               if (token.indexOf('(') > - 1) {
                  // Greeter(PhraseBuilder becomes PhraseBuilder
                  token = token.substring(token.indexOf('(') + 1);
               }
               if (token.indexOf('<') > - 1 && token.indexOf('>') > - 1) {
                  // e.g. the token is now e.g. Event<BrowserWindow>
                  if (token.startsWith("Event<")) {
                     // set type to Event (it could be setted before as an Inject)
                     type = DependenciyType.EVENT;
                  }
                  // e.g. the token is now e.g. Instance<GlassfishAuthenticator>
                  if (token.startsWith("Instance<")) {
                     // set type to Event (it could be setted before as an Inject)
                     type = DependenciyType.INSTANCE;
                  }
                  // e.g. Event<Person> becomes to Person
                  token = token.substring(token.indexOf('<') + 1, token.indexOf('>'));
               }
               token = jumpOverJavaToken(token, scanner);
               if (token.indexOf('(') > - 1) {
                  token = token.substring(token.indexOf('(') + 1);
               }
               String className = jumpOverJavaToken(token, scanner);

               createDependency(className, type, javaSource);
            }
         }
      }
   }

   @Override
   protected boolean isRelevantType(DependenciyType type) {
      return Arrays.asList(
              DependenciyType.EJB,
              DependenciyType.EVENT,
              DependenciyType.INJECT,
              DependenciyType.INSTANCE,
              DependenciyType.OBSERVES,
              DependenciyType.PRODUCES).contains(type);
   }
}

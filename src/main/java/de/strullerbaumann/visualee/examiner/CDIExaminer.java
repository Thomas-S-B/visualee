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
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class CDIExaminer extends Examiner {

   @Override
   protected boolean isRelevantType(DependencyType type) {
      return Arrays.asList(
              DependencyType.EJB,
              DependencyType.EVENT,
              DependencyType.INJECT,
              DependencyType.INSTANCE,
              DependencyType.OBSERVES,
              DependencyType.PRODUCES).contains(type);
   }

   @Override
   public void examine(JavaSource javaSource) {
      try (Scanner scanner = getSourceCodeScanner(getClassBody(javaSource.getSourceCodeWithoutComments()))) {
         while (scanner.hasNext()) {
            String token = scanner.next();
            DependencyType type = getTypeFromToken(token);
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
                     type = DependencyType.EVENT;
                  }
                  // e.g. the token is now e.g. Instance<GlassfishAuthenticator>
                  if (token.startsWith("Instance<")) {
                     // set type to Event (it could be setted before as an Inject)
                     type = DependencyType.INSTANCE;
                  }
                  // e.g. Event<Person> becomes Person
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
}

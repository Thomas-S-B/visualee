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
package de.strullerbaumann.visualee.examiner.cdi;

import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.examiner.Examiner;
import de.strullerbaumann.visualee.javasource.entity.JavaSource;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class ExaminerEvent extends Examiner {

   @Override
   protected boolean isRelevantType(DependencyType type) {
      return Arrays.asList(DependencyType.EVENT,
              DependencyType.INJECT).contains(type);
   }

   @Override
   protected DependencyType getTypeFromToken(String token) {
      DependencyType type = null;
      if (token.indexOf("@Inject") > -1) {
         type = DependencyType.INJECT;
      }
      return type;
   }

   @Override
   public void examine(JavaSource javaSource) {
      try (Scanner scanner = getSourceCodeScanner(getClassBody(javaSource.getSourceCodeWithoutComments()))) {
         while (scanner.hasNext()) {
            String token = scanner.next();
            DependencyType type = getTypeFromToken(token);
            if (isRelevantType(type)) {
               token = jumpOverJavaToken(token, scanner);
               // e.g. the token is now e.g. Event<GlassfishAuthenticator>
               if (token.startsWith("Event<")) {
                  // e.g. Event<Person> becomes Person
                  token = token.substring(token.indexOf('<') + 1, token.indexOf('>'));
                  token = jumpOverJavaToken(token, scanner);
                  if (token.indexOf('(') > - 1) {
                     token = token.substring(token.indexOf('(') + 1);
                  }
                  String className = jumpOverJavaToken(token, scanner);
                  className = cleanupGeneric(className);
                  createDependency(className, DependencyType.EVENT, javaSource);
               }
            }
         }
      }
   }
}

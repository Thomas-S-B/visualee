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
package de.strullerbaumann.visualee.examiner.jpa;

import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.examiner.Examiner;
import de.strullerbaumann.visualee.javasource.entity.JavaSource;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class ExaminerJPA extends Examiner {

   @Override
   protected boolean isRelevantType(DependencyType type) {
      return Arrays.asList(
              DependencyType.ONE_TO_MANY,
              DependencyType.ONE_TO_ONE,
              DependencyType.MANY_TO_ONE,
              DependencyType.MANY_TO_MANY).contains(type);
   }

   @Override
   public void examine(JavaSource javaSource) {
      // Examine class body
      try (Scanner scanner = getSourceCodeScanner(getClassBody(javaSource.getSourceCodeWithoutComments()))) {
         while (scanner.hasNext()) {
            String token = scanner.next();
            DependencyType type = getTypeFromToken(token);
            if (isRelevantType(type)) {
               // Find the associated Class
               if (token.indexOf('(') > - 1) {
                  token = scanAfterClosedParenthesis(token, scanner);
               }
               while (scanner.hasNext() && (isAJavaToken(token))) {
                  if (token.indexOf('(') > - 1) {
                     token = scanAfterClosedParenthesis(token, scanner);
                  } else {
                     token = scanner.next();
                  }
               }
               if (token.indexOf('<') > - 1 && token.indexOf('>') > - 1) {
                  // e.g. Set<Group> becomes Group
                  token = token.substring(token.indexOf('<') + 1, token.indexOf('>'));
               }
               createDependency(token, type, javaSource);
            }
         }
      }
   }

   @Override
   protected DependencyType getTypeFromToken(String token) {
      DependencyType type = null;
      if (token.indexOf("@OneToOne") > -1) {
         type = DependencyType.ONE_TO_ONE;
      }
      if (token.indexOf("@OneToMany") > -1) {
         type = DependencyType.ONE_TO_MANY;
      }
      if (token.indexOf("@ManyToOne") > -1) {
         type = DependencyType.MANY_TO_ONE;
      }
      if (token.indexOf("@ManyToMany") > -1) {
         type = DependencyType.MANY_TO_MANY;
      }
      return type;
   }
}

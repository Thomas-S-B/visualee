package de.strullerbaumann.visualee.examiner.cdi;

/*
 * #%L
 * visualee
 * %%
 * Copyright (C) 2013 Thomas Struller-Baumann
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.examiner.Examiner;
import de.strullerbaumann.visualee.source.entity.JavaSource;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class ExaminerEJB extends Examiner {

   @Override
   protected boolean isRelevantType(DependencyType type) {
      return Arrays.asList(DependencyType.EJB).contains(type);
   }

   @Override
   protected DependencyType getTypeFromToken(String token) {
      DependencyType type = null;
      if (token.indexOf("@EJB") > -1) {
         type = DependencyType.EJB;
      }
      return type;
   }

   @Override
   public void examine(JavaSource javaSource) {
      //http://docs.oracle.com/javaee/6/api/javax/ejb/EJB.html
      try (Scanner scanner = getSourceCodeScanner(getClassBody(javaSource.getSourceCodeWithoutComments()))) {
         while (scanner.hasNext()) {
            String token = scanner.next();
            DependencyType type = getTypeFromToken(token);
            if (isRelevantType(type)) {
               token = jumpOverJavaToken(token, scanner);
               // possible tokens now are e.g. Principal, Greeter(PhraseBuilder, Event<Person>, AsyncService ...
               if (token.indexOf('(') > - 1) {
                  // Greeter(PhraseBuilder becomes PhraseBuilder
                  token = token.substring(token.indexOf('(') + 1);
               }
               String className = jumpOverJavaToken(token, scanner);
               createDependency(className, DependencyType.EJB, javaSource);
            }
         }
      }
   }
}

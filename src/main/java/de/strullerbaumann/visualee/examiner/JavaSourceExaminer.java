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

import de.strullerbaumann.visualee.resources.JavaSource;
import de.strullerbaumann.visualee.resources.JavaSourceContainer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public final class JavaSourceExaminer {

   private List<Examiner> examiners = new ArrayList<>();

   private static class JavaSourceExaminerHolder {

      private static final JavaSourceExaminer INSTANCE = new JavaSourceExaminer();
   }

   private JavaSourceExaminer() {
      examiners.add(new CDIExaminer());
      examiners.add(new JPAExaminer());
   }

   public static JavaSourceExaminer getInstance() {
      return JavaSourceExaminerHolder.INSTANCE;
   }

   public void examine() {
      // Init javaSources
      for (JavaSource javaSource : JavaSourceContainer.getInstance().getJavaSources()) {
         findAndSetPackage(javaSource);
      }
      // Examine javaSources
      for (JavaSource javaSource : JavaSourceContainer.getInstance().getJavaSources()) {
         for (Examiner examiner : examiners) {
            examiner.examine(javaSource);
         }
      }
      setGroupNrs();
   }

   // TODO UnitTest
   protected void setGroupNrs() {
      // Group durchnumerieren/setzen aus packagePaths
      // alle aus demselben Package haben die selbe groupNr
      Map<String, Integer> packagePaths = new HashMap<>();
      int groupNr = 1;
      for (JavaSource javaSource : JavaSourceContainer.getInstance().getJavaSources()) {
         if (!packagePaths.containsKey(javaSource.getPackagePath())) {
            packagePaths.put(javaSource.getPackagePath(), groupNr);
            groupNr++;
         }
      }
      for (JavaSource javaSource : JavaSourceContainer.getInstance().getJavaSources()) {
         int group = packagePaths.get(javaSource.getPackagePath());
         javaSource.setGroup(group);
      }
   }

   protected static void findAndSetPackage(JavaSource javaSource) {
      Scanner scanner = Examiner.getSourceCodeScanner(javaSource.getSourceCode());
      while (scanner.hasNext()) {
         String token = scanner.next();
         if (javaSource.getPackagePath() == null && token.equals("package")) {
            token = scanner.next();
            //without ; at the end
            String packagePath = token.substring(0, token.indexOf(';'));
            javaSource.setPackagePath(packagePath);
         }
      }
   }
}

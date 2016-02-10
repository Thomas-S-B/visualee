package de.strullerbaumann.visualee.examiner;

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
import de.strullerbaumann.visualee.logging.LogProvider;
import de.strullerbaumann.visualee.source.boundary.JavaSourceContainer;
import de.strullerbaumann.visualee.source.entity.JavaSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de)
 */
public final class JavaSourceInspector {

   private static final List<Examiner> EXAMINERS = new ArrayList<>();

   private static class JavaSourceExaminerHolder {

      private static final JavaSourceInspector INSTANCE = new JavaSourceInspector();
   }

   private JavaSourceInspector() {
   }

   public static JavaSourceInspector getInstance() {
      return JavaSourceExaminerHolder.INSTANCE;
   }

   public void registerExaminer(Examiner examiner) {
      EXAMINERS.add(examiner);
   }

   List<Examiner> getExaminers() {
      return EXAMINERS;
   }

   public void examine() {
      // Examine javaSources
      for (JavaSource javaSource : JavaSourceContainer.getInstance().getJavaSources()) {
         LogProvider.getInstance().debug("Examining: " + javaSource.getFullClassName());
         for (Examiner examiner : getExaminers()) {
            examiner.examine(javaSource);
         }
      }
      setGroupNrs();
   }

   void setGroupNrs() {
      // all javasources from the same package should have the same groupNr
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
}

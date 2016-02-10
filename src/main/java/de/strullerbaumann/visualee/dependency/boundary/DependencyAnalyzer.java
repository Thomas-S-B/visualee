package de.strullerbaumann.visualee.dependency.boundary;

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
import de.strullerbaumann.visualee.examiner.JavaSourceInspector;
import de.strullerbaumann.visualee.examiner.cdi.ExaminerEJB;
import de.strullerbaumann.visualee.examiner.cdi.ExaminerEvent;
import de.strullerbaumann.visualee.examiner.cdi.ExaminerInject;
import de.strullerbaumann.visualee.examiner.cdi.ExaminerInstance;
import de.strullerbaumann.visualee.examiner.cdi.ExaminerObserves;
import de.strullerbaumann.visualee.examiner.cdi.ExaminerProduces;
import de.strullerbaumann.visualee.examiner.cdi.ExaminerResource;
import de.strullerbaumann.visualee.examiner.jpa.ExaminerJPA;
import de.strullerbaumann.visualee.source.boundary.JavaSourceContainer;

/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de)
 */
public final class DependencyAnalyzer {

   private static class DependencyAnalyzerHolder {

      private static final DependencyAnalyzer INSTANCE = new DependencyAnalyzer();
   }

   private DependencyAnalyzer() {
   }

   public static DependencyAnalyzer getInstance() {
      return DependencyAnalyzerHolder.INSTANCE;
   }

   public void analyze(String rootFolder) {
      JavaSourceContainer.getInstance().clear();
      JavaSourceContainer.getInstance().loadJavaFiles(rootFolder);

      //Register Examiners
      JavaSourceInspector.getInstance().registerExaminer(new ExaminerEJB());
      JavaSourceInspector.getInstance().registerExaminer(new ExaminerEvent());
      JavaSourceInspector.getInstance().registerExaminer(new ExaminerInject());
      JavaSourceInspector.getInstance().registerExaminer(new ExaminerInstance());
      JavaSourceInspector.getInstance().registerExaminer(new ExaminerJPA());
      JavaSourceInspector.getInstance().registerExaminer(new ExaminerObserves());
      JavaSourceInspector.getInstance().registerExaminer(new ExaminerProduces());
      JavaSourceInspector.getInstance().registerExaminer(new ExaminerResource());

      JavaSourceInspector.getInstance().examine();
   }
}

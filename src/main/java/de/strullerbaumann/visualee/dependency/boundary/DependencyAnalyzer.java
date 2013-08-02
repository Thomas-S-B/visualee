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
package de.strullerbaumann.visualee.dependency.boundary;

import de.strullerbaumann.visualee.examiner.JavaSourceExaminer;
import de.strullerbaumann.visualee.javasource.boundary.JavaSourceContainer;
import de.strullerbaumann.visualee.ui.graph.boundary.GraphCreator;
import java.io.File;
import java.io.InputStream;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public final class DependencyAnalyzer {

   private DependencyAnalyzer() {
   }

   public static void analyze(File rootFolder, File outputdirectory, InputStream htmlTemplateIS) {
      JavaSourceContainer.getInstance().clear();
      JavaSourceContainer.getInstance().loadJavaFiles(rootFolder);
      JavaSourceExaminer.getInstance().examine();
      if (!outputdirectory.exists()) {
         outputdirectory.mkdir();
      }
      GraphCreator.generateGraphs(rootFolder, htmlTemplateIS, outputdirectory);
   }
}

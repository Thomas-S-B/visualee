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
package de.strullerbaumann.visualee.dependency;

import de.strullerbaumann.visualee.examiner.JavaSourceExaminer;
import de.strullerbaumann.visualee.resources.FileManager;
import de.strullerbaumann.visualee.resources.HTMLManager;
import de.strullerbaumann.visualee.resources.JavaSource;
import de.strullerbaumann.visualee.resources.JavaSourceContainer;
import de.strullerbaumann.visualee.ui.Graph;
import de.strullerbaumann.visualee.ui.GraphCreator;
import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public final class DependencyAnalyzer {

   private static String htmlTemplate;

   private DependencyAnalyzer() {
   }

   public static void analyze(File rootFolder, File outputdirectory, InputStream htmlTemplateIS) {
      // 1. Load Javafiles
      final List<File> javaFiles = FileManager.searchFiles(rootFolder, ".java");
      for (File javaFile : javaFiles) {
         JavaSource javaSource = new JavaSource(javaFile);
         JavaSourceContainer.getInstance().add(javaSource);
      }
      // 2. Examine Javafiles
      JavaSourceExaminer.getInstance().examine();
      // 3. Load HTML-Template, if not already done (Maven modules)
      if (htmlTemplate == null) {
         htmlTemplate = HTMLManager.loadHTMLTemplate(htmlTemplateIS, "graphTemplate");
      }
      // 4. Prepare outputdirectory
      if (!outputdirectory.exists()) {
         outputdirectory.mkdir();
      }
      // 5. Generate Graphs
      // GRAPH - only CDI-relevant classes
      Graph graphOnlyCDI = GraphCreator.generateGraph("graphOnlyCDI", outputdirectory, null, true, htmlTemplateIS);
      graphOnlyCDI.setTitle("Only CDI relevant classes of " + rootFolder.getPath());
      graphOnlyCDI.calculateDimensions();
      HTMLManager.generateHTML(graphOnlyCDI, htmlTemplate);

      // GRAPH - all classes
      Graph graphAllClasses = GraphCreator.generateGraph("graphAllClasses", outputdirectory, null, false, htmlTemplateIS);
      graphAllClasses.setTitle("All classes of " + rootFolder.getPath());
      graphAllClasses.calculateDimensions();
      HTMLManager.generateHTML(graphAllClasses, htmlTemplate);

      // GRAPH - only Event/Observer classes
      DependencyFilter cdiFilterEventObserver = new DependencyFilter().addType(DependenciyType.EVENT).addType(DependenciyType.OBSERVES);
      Graph graphEventObserverClasses = GraphCreator.generateGraph("graphEventObserverClasses", outputdirectory, cdiFilterEventObserver, true, htmlTemplateIS);
      graphEventObserverClasses.setTitle("Event/Observer classes of " + rootFolder.getPath());
      graphEventObserverClasses.calculateDimensions();
      HTMLManager.generateHTML(graphEventObserverClasses, htmlTemplate);

      // GRAPH - only Instance classes
      DependencyFilter cdiFilterInstance = new DependencyFilter().addType(DependenciyType.INSTANCE);
      Graph graphInstanceClasses = GraphCreator.generateGraph("graphInstanceClasses", outputdirectory, cdiFilterInstance, true, htmlTemplateIS);
      graphInstanceClasses.setTitle("Only Instance classes of " + rootFolder.getPath());
      graphInstanceClasses.calculateDimensions();
      HTMLManager.generateHTML(graphInstanceClasses, htmlTemplate);

      // GRAPH - only Inject classes
      DependencyFilter cdiFilterInject = new DependencyFilter().addType(DependenciyType.INJECT);
      Graph graphInjectClasses = GraphCreator.generateGraph("graphInjectClasses", outputdirectory, cdiFilterInject, true, htmlTemplateIS);
      graphInjectClasses.setTitle("Only Inject classes of " + rootFolder.getPath());
      graphInjectClasses.calculateDimensions();
      HTMLManager.generateHTML(graphInjectClasses, htmlTemplate);

      // GRAPH - only Produces classes
      DependencyFilter cdiFilterProduces = new DependencyFilter().addType(DependenciyType.PRODUCES);
      Graph graphProducesClasses = GraphCreator.generateGraph("graphProducesClasses", outputdirectory, cdiFilterProduces, true, htmlTemplateIS);
      graphProducesClasses.setTitle("Only Produces classes of " + rootFolder.getPath());
      graphProducesClasses.calculateDimensions();
      HTMLManager.generateHTML(graphProducesClasses, htmlTemplate);

      // GRAPH - only JPA classes
      DependencyFilter filterJPA = new DependencyFilter().addType(DependenciyType.ONE_TO_ONE).addType(DependenciyType.ONE_TO_MANY).addType(DependenciyType.MANY_TO_ONE).addType(DependenciyType.MANY_TO_MANY);
      Graph graphJPAClasses = GraphCreator.generateGraph("graphJPAClasses", outputdirectory, filterJPA, true, htmlTemplateIS);
      graphJPAClasses.setTitle("Only JPA classes of " + rootFolder.getPath());
      graphJPAClasses.calculateDimensions();
      HTMLManager.generateHTML(graphJPAClasses, htmlTemplate);
   }
}

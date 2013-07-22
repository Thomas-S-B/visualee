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
package de.strullerbaumann.visualee.cdi;

import de.strullerbaumann.visualee.resources.FileManager;
import de.strullerbaumann.visualee.resources.HTMLManager;
import de.strullerbaumann.visualee.resources.JavaSource;
import de.strullerbaumann.visualee.resources.JavaSourceContainer;
import de.strullerbaumann.visualee.resources.JavaSourceExaminer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class CDIAnalyzer {

    private static JavaSourceContainer javaSourceContainer;
    private static String htmlTemplate;

    public static void analyze(File rootFolder, File outputdirectory, InputStream htmlTemplateIS) {
        // 1. Load Javafiles
        final List<File> javaFiles = FileManager.searchFiles(rootFolder, ".java");
        javaSourceContainer = new JavaSourceContainer();
        for (File javaFile : javaFiles) {
            JavaSource javaSource = new JavaSource(javaFile);
            javaSourceContainer.add(javaSource);
        }
        // 2. Examine Javafiles
        try {
            JavaSourceExaminer.getInstance().examine(javaSourceContainer);
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(CDIAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(CDIAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        CDIGraph graphOnlyCDI = CDIGraphCreator.generateCDIGraph("graphOnlyCDI", outputdirectory, null, true, htmlTemplateIS, javaSourceContainer);
        graphOnlyCDI.setTitle("Only CDI relevant classes of " + rootFolder.getPath());
        graphOnlyCDI.calculateDimensions();
        HTMLManager.generateHTML(graphOnlyCDI, htmlTemplate);

        // GRAPH - all classes
        CDIGraph graphAllClasses = CDIGraphCreator.generateCDIGraph("graphAllClasses", outputdirectory, null, false, htmlTemplateIS, javaSourceContainer);
        graphAllClasses.setTitle("All classes of " + rootFolder.getPath());
        graphAllClasses.calculateDimensions();
        HTMLManager.generateHTML(graphAllClasses, htmlTemplate);

        // GRAPH - only Event/Observer classes
        CDIFilter cdiFilterEventObserver = new CDIFilter().addCDIType(CDIType.EVENT).addCDIType(CDIType.OBSERVES);
        CDIGraph graphEventObserverClasses = CDIGraphCreator.generateCDIGraph("graphEventObserverClasses", outputdirectory, cdiFilterEventObserver, true, htmlTemplateIS, javaSourceContainer);
        graphEventObserverClasses.setTitle("Event/Observer classes of " + rootFolder.getPath());
        graphEventObserverClasses.calculateDimensions();
        HTMLManager.generateHTML(graphEventObserverClasses, htmlTemplate);

        // GRAPH - only Instance classes
        CDIFilter cdiFilterInstance = new CDIFilter().addCDIType(CDIType.INSTANCE);
        CDIGraph graphInstanceClasses = CDIGraphCreator.generateCDIGraph("graphInstanceClasses", outputdirectory, cdiFilterInstance, true, htmlTemplateIS, javaSourceContainer);
        graphInstanceClasses.setTitle("Only Instance classes of " + rootFolder.getPath());
        graphInstanceClasses.calculateDimensions();
        HTMLManager.generateHTML(graphInstanceClasses, htmlTemplate);

        // GRAPH - only Inject classes
        CDIFilter cdiFilterInject = new CDIFilter().addCDIType(CDIType.INJECT);
        CDIGraph graphInjectClasses = CDIGraphCreator.generateCDIGraph("graphInjectClasses", outputdirectory, cdiFilterInject, true, htmlTemplateIS, javaSourceContainer);
        graphInjectClasses.setTitle("Only Inject classes of " + rootFolder.getPath());
        graphInjectClasses.calculateDimensions();
        HTMLManager.generateHTML(graphInjectClasses, htmlTemplate);

        // GRAPH - only Produces classes
        CDIFilter cdiFilterProduces = new CDIFilter().addCDIType(CDIType.PRODUCES);
        CDIGraph graphProducesClasses = CDIGraphCreator.generateCDIGraph("graphProducesClasses", outputdirectory, cdiFilterProduces, true, htmlTemplateIS, javaSourceContainer);
        graphProducesClasses.setTitle("Only Produces classes of " + rootFolder.getPath());
        graphProducesClasses.calculateDimensions();
        HTMLManager.generateHTML(graphProducesClasses, htmlTemplate);

        // GRAPH - only JPA classes
        CDIFilter cdiFilterJPA = new CDIFilter().addCDIType(CDIType.ONE_TO_ONE).addCDIType(CDIType.ONE_TO_MANY).addCDIType(CDIType.MANY_TO_ONE).addCDIType(CDIType.MANY_TO_MANY);
        CDIGraph graphJPAClasses = CDIGraphCreator.generateCDIGraph("graphJPAClasses", outputdirectory, cdiFilterJPA, true, htmlTemplateIS, javaSourceContainer);
        graphJPAClasses.setTitle("Only JPA classes of " + rootFolder.getPath());
        graphJPAClasses.calculateDimensions();
        HTMLManager.generateHTML(graphJPAClasses, htmlTemplate);
    }
}

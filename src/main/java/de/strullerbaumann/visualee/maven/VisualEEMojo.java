package de.strullerbaumann.visualee.maven;

/*
 Copyright 2013 Thomas Struller-Baumann, struller-baumann.de

 Licensed under the Apache License, Version 2.0 (the "License")
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
import de.strullerbaumann.visualee.dependency.boundary.DependencyAnalyzer;
import de.strullerbaumann.visualee.resources.FileManager;
import de.strullerbaumann.visualee.ui.graph.boundary.GraphConfigurator;
import de.strullerbaumann.visualee.ui.graph.boundary.GraphCreator;
import de.strullerbaumann.visualee.ui.graph.control.HTMLManager;
import de.strullerbaumann.visualee.ui.graph.entity.GraphConfig;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal to visualize EE-Dependencies
 *
 * @goal visualize
 * @phase process-sources
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class VisualEEMojo extends AbstractMojo {

   /**
    * @parameter expression="${session}"
    * @required
    * @readonly
    */
   private MavenSession mavenSession;
   /**
    * Base directory of the project.
    *
    * @parameter default-value="${basedir}"
    * @required
    * @readonly
    */
   private File basedir;
   /**
    * Location for the generated visualee files.
    *
    * @parameter expression="${project.build.directory}"
    * @required
    */
   private File outputdirectory;
   /**
    * Graphs Properties.
    *
    * @parameter
    */
   private List<GraphConfig> graphs;
   private static final String JS_DIR = "/js/";
   private static final String CSS_DIR = "/css/";
   private static final String[] CSS_DIR_FILES = {"style.css", "jquery-ui.css"};
   private static final String[] JS_DIR_FILES = {"d3.v3.min.js", "jquery-2.0.3.min.js", "jquery-ui-1.9.2.min.js", "classgraph.js", "LICENSE"};
   private static final String HEADER_FOOTER = "#######################################################";

   @Override
   public void execute() throws MojoExecutionException {
      //Ensure only one execution (important for multi module poms)
      if (isThisRootDir()) {
         getLog().info(HEADER_FOOTER);
         getLog().info("### VisualEE-Plugin");
         InputStream indexIS = getClass().getResourceAsStream("/html/index.html");
         InputStream graphTemplateIS = getClass().getResourceAsStream("/html/graphTemplate.html");
         for (String exportFile : CSS_DIR_FILES) {
            FileManager.export(getClass(), CSS_DIR, exportFile, outputdirectory.getAbsoluteFile());
         }
         for (String exportFile : JS_DIR_FILES) {
            FileManager.export(getClass(), JS_DIR, exportFile, outputdirectory.getAbsoluteFile());
         }
         //Examine all java-files under the projectroot
         String sourceFolder = mavenSession.getExecutionRootDirectory();
         if (sourceFolder != null) {
            HTMLManager.generateIndexHTML(outputdirectory, indexIS, sourceFolder);
            getLog().info("### Analyzing sourcefolder: " + sourceFolder);
            File sourceFolderDir = new File(sourceFolder);
            DependencyAnalyzer.getInstance().analyze(sourceFolderDir);
            getLog().info("### Generating graphs");
            GraphConfigurator.setGraphConfigs(graphs);
            GraphCreator.generateGraphs(sourceFolderDir, outputdirectory, graphTemplateIS);
            getLog().info("### Done, visualization can be found in");
            getLog().info("### " + outputdirectory + File.separatorChar + "index.html");
            getLog().info(HEADER_FOOTER);
         } else {
            getLog().error("### Cannot find src-folder");
         }
      }
   }

   protected boolean isThisRootDir() {
      return mavenSession.getExecutionRootDirectory().equalsIgnoreCase(basedir.toString());
   }
}

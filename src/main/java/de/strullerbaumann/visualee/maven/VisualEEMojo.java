package de.strullerbaumann.visualee.maven;

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
import de.strullerbaumann.visualee.dependency.boundary.DependencyAnalyzer;
import de.strullerbaumann.visualee.filter.boundary.FilterConfigurator;
import de.strullerbaumann.visualee.filter.boundary.FilterContainer;
import de.strullerbaumann.visualee.filter.entity.Filter;
import de.strullerbaumann.visualee.filter.entity.FilterConfig;
import de.strullerbaumann.visualee.logging.LogProvider;
import de.strullerbaumann.visualee.resources.FileManager;
import de.strullerbaumann.visualee.source.boundary.JavaSourceContainer;
import de.strullerbaumann.visualee.ui.graph.boundary.GraphConfigurator;
import de.strullerbaumann.visualee.ui.graph.boundary.GraphCreator;
import de.strullerbaumann.visualee.ui.graph.control.HTMLManager;
import de.strullerbaumann.visualee.ui.graph.entity.GraphConfig;
import java.io.File;
import java.util.List;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal to visualize EE-Dependencies
 *
 * @goal visualize
 * @phase process-sources
 * @author Thomas Struller-Baumann (contact at struller-baumann.de)
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
    * Encoding of the Javasource-Files, which are examined
    *
    * @parameter default-value="UTF-8"
    */
   private String encoding;
   /**
    * Graphs Properties.
    *
    * @parameter
    */
   private List<GraphConfig> graphs;

   /**
    * Filters Properties.
    *
    * @parameter
    */
   private List<FilterConfig> filters;

   private static final String JS_DIR = "/js/";
   private static final String CSS_DIR = "/css/";
   private static final String[] CSS_DIR_FILES = {
      "style.css",
      "jquery-ui.css"};
   private static final String[] JS_DIR_FILES = {
      "d3.v3.min.js",
      "jquery-2.0.3.min.js",
      "jquery-ui-1.9.2.min.js",
      "classgraph.js",
      "LICENSE"};
   private static final String HEADER_FOOTER = "#######################################################";

   @Override
   public void execute() throws MojoExecutionException {
      LogProvider.getInstance().setLog(getLog());
      //Ensure only one execution (important for multi module poms)
      if (isThisRootDir()) {
         getLog().info(HEADER_FOOTER);
         getLog().info("VisualEE-Plugin");
         checkCreateDirs(outputdirectory);
         for (String exportFile : CSS_DIR_FILES) {
            FileManager.export(CSS_DIR, exportFile, outputdirectory.getAbsoluteFile());
         }
         for (String exportFile : JS_DIR_FILES) {
            FileManager.export(JS_DIR, exportFile, outputdirectory.getAbsoluteFile());
         }
         //Examine all java-files in projectroot
         String sourceFolder = mavenSession.getExecutionRootDirectory();
         if (sourceFolder != null) {
            // Set encoding
            if (encoding != null) {
               JavaSourceContainer.setEncoding(encoding);
            }
            getLog().info("Using encoding: " + JavaSourceContainer.getEncoding());

            // Set filters
            if (filters != null) {
               FilterConfigurator.setFilterConfigs(filters);
               getLog().info("Active filters: ");
               for (Filter filter : FilterContainer.getInstance().getFilters()) {
                  getLog().info("   " + filter);
               }
            } else {
               getLog().info("No filters configured.");
            }

            HTMLManager.generateIndexHTML(outputdirectory, "/html/index.html", sourceFolder);

            getLog().info("Analyzing sourcefolder: " + sourceFolder);
            DependencyAnalyzer.getInstance().analyze(sourceFolder);
            getLog().info("Generating graphs");

            // Set graphs
            if (graphs != null) {
               GraphConfigurator.setGraphConfigs(graphs);
            }

            File sourceFolderDir = new File(sourceFolder);
            GraphCreator.generateGraphs(sourceFolderDir, outputdirectory, "/html/graphTemplate.html");
            getLog().info("Done, visualization can be found in");
            getLog().info(outputdirectory.toString() + File.separatorChar + "index.html");
            getLog().info(HEADER_FOOTER);
         } else {
            getLog().error("Can't find src-folder");
         }
      }
   }

   /**
    * checks is the directory exists or creates it assuming it cannot be null
    *
    * @param dirPath
    */
   protected void checkCreateDirs(File dirPath) {
      if (!dirPath.exists()) {
         dirPath.mkdirs();
      }
   }

   protected boolean isThisRootDir() {
      return mavenSession.getExecutionRootDirectory().equalsIgnoreCase(basedir.toString());
   }
}

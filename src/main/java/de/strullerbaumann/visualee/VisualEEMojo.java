package de.strullerbaumann.visualee;

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
import de.strullerbaumann.visualee.cdi.CDIAnalyzer;
import de.strullerbaumann.visualee.resources.HTMLManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal to visualize CDI-Dependencies
 *
 * @goal visualize
 *
 * @phase process-sources
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class VisualEEMojo extends AbstractMojo {

    /**
     * The source directories containing the sources to be processed.
     *
     * @parameter expression="${project.compileSourceRoots}"
     * @required
     * @readonly
     */
    private List<String> compileSourceRoots;
    /**
     * Location of the visual files.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputdirectory;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("#######################################################");
        getLog().info("### VisualEE-Plugin");

        InputStream indexIS = getClass().getResourceAsStream("/html/index.html");
        InputStream graphTemplateIS = getClass().getResourceAsStream("/html/graphTemplate.html");

        export("/css/", "style.css", outputdirectory.getAbsoluteFile());
        export("/css/", "jquery-ui.css", outputdirectory.getAbsoluteFile());
        export("/js/", "d3.v3.min.js", outputdirectory.getAbsoluteFile());
        export("/js/", "jquery-2.0.3.min.js", outputdirectory.getAbsoluteFile());
        export("/js/", "jquery-ui-1.9.2.min.js", outputdirectory.getAbsoluteFile());
        export("/js/", "classgraph.js", outputdirectory.getAbsoluteFile());
        export("/js/", "LICENSE", outputdirectory.getAbsoluteFile());

        // Only inspect src-folder, and not e.g. target-folder
        String sourceFolder = getSourceFolder();
        if (sourceFolder != null) {
            HTMLManager.generateIndexHTML(outputdirectory, indexIS, sourceFolder);
            getLog().info("### Analyzing sourcefolder: " + sourceFolder);
            CDIAnalyzer.analyze(new File(sourceFolder), outputdirectory, graphTemplateIS);
            getLog().info("### Done, visualization can be found in");
            getLog().info("### " + outputdirectory + File.separatorChar + "index.html");
            getLog().info("#######################################################");
        } else {
            getLog().error("### Cannot find src-folder");
        }

    }

    private String getSourceFolder() {
        for (String sourceFolder : compileSourceRoots) {
            if (sourceFolder.indexOf(File.separatorChar + "src" + File.separatorChar) > -1) {
                return sourceFolder + File.separatorChar;
            }
        }
        return null;
    }

    //Exports Files from jar to a given directory
    private void export(String sourceFolder, String fileName, File targetFolder) {
        try {
            if (!targetFolder.exists()) {
                targetFolder.mkdir();
            }
            File dstResourceFolder = new File(targetFolder + sourceFolder + File.separatorChar);
            if (!dstResourceFolder.exists()) {
                dstResourceFolder.mkdir();
            }
            try (InputStream is = getClass().getResourceAsStream(sourceFolder + fileName); OutputStream os = new FileOutputStream(targetFolder + sourceFolder + fileName)) {
                byte[] buffer = new byte[4096];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            }
        }
        catch (Exception exc) {
            Logger.getLogger(VisualEEMojo.class.getName()).log(Level.INFO, null, exc);
        }
    }
}

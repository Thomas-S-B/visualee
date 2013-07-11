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
package de.strullerbaumann.visualee.resources;

import de.strullerbaumann.visualee.cdi.CDIDependency;
import de.strullerbaumann.visualee.cdi.CDIType;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class JavaSourceExaminer {

    private JavaSourceContainer javaSourceContainer;

    private static class JavaSourceExaminerHolder {

        private static final JavaSourceExaminer INSTANCE = new JavaSourceExaminer();
    }

    private JavaSourceExaminer() {
    }

    public static JavaSourceExaminer getInstance() {
        return JavaSourceExaminerHolder.INSTANCE;
    }

    public void examine(JavaSourceContainer javaSourceContainer) throws FileNotFoundException, IOException {
        this.javaSourceContainer = javaSourceContainer;
        for (JavaSource javaSource : javaSourceContainer.getJavaSources()) {
            findAndSetAttributes(javaSource);
        }

        // Group durchnumerieren/setzen aus packagePaths (diese sind ja jetzt alle belegt)
        // alles aus dem sleben Package haben die selbe griup
        Map<String, Integer> packagePaths = new HashMap<>();
        int groupNr = 1;
        for (JavaSource javaSource : javaSourceContainer.getJavaSources()) {
            if (!packagePaths.containsKey(javaSource.getPackagePath())) {
                packagePaths.put(javaSource.getPackagePath(), groupNr);
                groupNr++;
            }
        }
        // Nun alle Group setzen
        for (JavaSource javaSource : javaSourceContainer.getJavaSources()) {
            int group = packagePaths.get(javaSource.getPackagePath());
            javaSource.setGroup(group);
        }
    }

    public JavaSource getJavaSourceByName(String className) {
        for (JavaSource javaSource : javaSourceContainer.getJavaSources()) {
            if (javaSource.getJavaFile() == null) {
                if (className.equals(javaSource.getName())) {
                    return javaSource;
                }
            } else {
                if (javaSource.getJavaFile().getName().endsWith(className + ".java")) {
                    return javaSource;
                }
            }
        }
        return null;
    }

    private void loadSourceCode(JavaSource javaSource) throws FileNotFoundException, IOException {
        StringBuilder sourceCode = new StringBuilder();
        if (javaSource.getJavaFile() != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(javaSource.getJavaFile()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sourceCode.append(line).append('\n');
                }
            }
        }
        javaSource.setSourceCode(sourceCode.toString());
    }

    public void findAndSetAttributes(JavaSource javaSource) throws FileNotFoundException, IOException {
        if (javaSource.getJavaFile() == null) {
            return;
        }
        loadSourceCode(javaSource);
        // try (Scanner scanner = new Scanner(new FileReader(javaSource.getJavaFile()))) {
        boolean inBody = false;
        try (Scanner scanner = new Scanner(javaSource.getJavaFile())) {
            scanner.useDelimiter("[ \t\r\n]+");
            while (scanner.hasNext()) {
                String line = scanner.next();
                // Find and set package
                if (line.equals("package")) {
                    line = scanner.next();
                    String packagePath = line.substring(0, line.indexOf(';'));  //delete ; at the end
                    javaSource.setPackagePath(packagePath);
                }

                if (!inBody && line.indexOf("{") > -1) {    // In Class/Interface-Body?
                    inBody = true;
                }

                if (inBody) {
                    CDIType cdiType = getCDITypeFromLine(line);

                    if (cdiType != null) {
                        line = scanner.next();
                        while (line.indexOf("@") > - 1 || line.indexOf("private") > - 1 || line.indexOf("protected") > - 1 || line.indexOf("transient") > - 1 || line.indexOf("public") > - 1) {
                            line = scanner.next();
                        }
                        // possible tokens now in line are e.g. Principal, Greeter(PhraseBuilder, Event<Person>, AsyncService ...
                        if (line.indexOf("(") > - 1) {
                            line = line.substring(line.indexOf("(") + 1);  // Greeter(PhraseBuilder becomes to PhraseBuilder
                        }
                        if (line.indexOf("<") > - 1 && line.indexOf(">") > - 1) {
                            if (line.startsWith("Event<")) { // e.g. Event<BrowserWindow> events;
                                cdiType = CDIType.EVENT;   // set CDIType to Event (it could be setted before as an Inject)
                            }
                            if (line.startsWith("Instance<")) { // e.g. Instance<GlassfishAuthenticator> authenticator;
                                cdiType = CDIType.INSTANCE;   // set CDIType to Event (it could be setted before as an Inject)
                            }
                            line = line.substring(line.indexOf("<") + 1, line.indexOf(">"));  // Event<Person> becomes to Person
                        }
                        String className = line;
                        JavaSource injectedFound = JavaSourceExaminer.getInstance().getJavaSourceByName(className);
                        //if (injectedFound == null) {
                        // Da die Klasse nicht gefunden wurde, ist diese nicht aus den Projektsourcen, sondern extern
                        // Für diese wird dann ein neues JavaSource angelegt
                        if (injectedFound != null) {
                            CDIDependency dependency = new CDIDependency(cdiType, javaSource, injectedFound);
                            javaSource.getInjected().add(dependency);
                        } else {
                            // Genrate a new JavaSource, which is not explicit in the sources (e.g. Integer, String etc.)
                            JavaSource newJavaSource = new JavaSource(className);
                            this.javaSourceContainer.add(newJavaSource);
                            // Logger.getLogger(JavaSourceExaminer.class.getName()).log(Level.INFO, "### keine Klasse in den Sourcen gefunden zu: {0} in {1}", new Object[]{className, javaSource.getName()});
                            CDIDependency dependency = new CDIDependency(cdiType, javaSource, newJavaSource);
                            javaSource.getInjected().add(dependency);
                        }
                    }
                }
            }
        }

    }

    protected CDIType getCDITypeFromLine(String line) {
        // Find and set CDI-Dependencies
        CDIType cdiType = null;
        if (line.indexOf("@EJB") > -1) {
            cdiType = CDIType.EJB;
        }
        if (line.indexOf("@Inject") > -1) {
            cdiType = CDIType.INJECT;
        }
        if (line.indexOf("@Observes") > -1) {
            cdiType = CDIType.OBSERVES;
        }
        // Identify @Produces form Inject (@Produces form WS is @Produces(...)
        // Inject: http://docs.oracle.com/javaee/6/api/javax/enterprise/inject/Produces.html
        // WS: http://docs.oracle.com/javaee/6/api/javax/ws/rs/Produces.html
        if (line.indexOf("@Produces") > -1 && line.indexOf("@Produces(") < 0) {
            cdiType = CDIType.PRODUCES;
        }
        return cdiType;
    }
}

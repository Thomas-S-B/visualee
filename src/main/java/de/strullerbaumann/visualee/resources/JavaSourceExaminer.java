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
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class JavaSourceExaminer {

    protected JavaSourceContainer javaSourceContainer;

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
        // alle aus demselben Package haben die selbe groupNr
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
            if (className.equals(javaSource.getName())) {
                return javaSource;
            }
        }
        return null;
    }

    private void loadSourceCode(JavaSource javaSource) {
        if (javaSource.getJavaFile() == null) {
            return;
        }
        StringBuilder sourceCode = new StringBuilder();
        if (javaSource.getJavaFile() != null) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(javaSource.getJavaFile()));
                String line;
                while ((line = br.readLine()) != null) {
                    sourceCode.append(line).append('\n');
                }
            }
            catch (IOException ex) {
                Logger.getLogger(JavaSourceExaminer.class.getName()).log(Level.SEVERE, "Problems while reading " + javaSource.getJavaFile(), ex);
            }
        }
        javaSource.setSourceCode(sourceCode.toString());
    }

    protected Scanner getSourceCodeScanner(String sourceCode) {
        Scanner scanner = new Scanner(sourceCode);
        // scanner.useDelimiter("[ \t\r\n\\(\\)]+");
        scanner.useDelimiter("[ \t\r\n]+");
        return scanner;
    }

    protected void findAndSetPackage(JavaSource javaSource) {
        Scanner scanner = getSourceCodeScanner(javaSource.getSourceCode());
        while (scanner.hasNext()) {
            String line = scanner.next();
            if (javaSource.getPackagePath() == null && line.equals("package")) {
                line = scanner.next();
                String packagePath = line.substring(0, line.indexOf(';'));  //without ; at the end
                javaSource.setPackagePath(packagePath);
            }
        }
    }

    protected String getClassBody(String sourceCode) {
        // todo evtl doch interface class abstract abfragen kann ja ein produces mit { sein
        StringBuilder classBody = new StringBuilder();
        boolean isInBodyNow = false;
        try (Scanner scanner = new Scanner(sourceCode)) {
            scanner.useDelimiter("[\n]+");
            while (scanner.hasNext()) {
                String line = scanner.next();
                if (!isInBodyNow) {
                    if (line.indexOf("{") > -1) {    // In Class/Interface-Body?
                        isInBodyNow = true;
                    }
                } else {
                    classBody.append(line).append("\n");
                }
            }
        }

        return classBody.toString();
    }

    public void findAndSetAttributes(JavaSource javaSource) {
        // Init javaSource
        loadSourceCode(javaSource);
        findAndSetPackage(javaSource);

        // Examine class body
        try (Scanner scanner = getSourceCodeScanner(getClassBody(javaSource.getSourceCode()))) {
            while (scanner.hasNext()) {
                String line = scanner.next();
                CDIType cdiType = getCDITypeFromLine(line);
                if (cdiType != null) {
                    // JPA
                    if (cdiType == CDIType.ONE_TO_ONE
                            || cdiType == CDIType.ONE_TO_MANY
                            || cdiType == CDIType.MANY_TO_ONE
                            || cdiType == CDIType.MANY_TO_MANY) {

                        // Find the associated Class
                        if (line.indexOf("(") > - 1) {
                            line = scanAfterClosedParenthesis(line, scanner);
                        }
                        while (scanner.hasNext() && (line.indexOf("@") > - 1
                                || line.indexOf("private") > - 1
                                || line.indexOf("protected") > - 1
                                || line.indexOf("transient") > - 1
                                || line.indexOf("public") > - 1)) {
                            // possible tokens now in line are e.g. Principal, Greeter(PhraseBuilder, Event<Person>, AsyncService ...
                            if (line.indexOf("(") > - 1) {
                                line = scanAfterClosedParenthesis(line, scanner);
                            } else {
                                line = scanner.next();
                            }
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

                        // Create the dependency
                        String className = line;
                        JavaSource injectedJavaSource = JavaSourceExaminer.getInstance().getJavaSourceByName(className);
                        if (injectedJavaSource == null) {
                            // Generate a new JavaSource, which is not explicit in the sources (e.g. Integer, String etc.)
                            injectedJavaSource = new JavaSource(className);
                            this.javaSourceContainer.add(injectedJavaSource);
                        }
                        CDIDependency dependency = new CDIDependency(cdiType, javaSource, injectedJavaSource);
                        javaSource.getInjected().add(dependency);
                    } else {
                        // CDI
                        line = scanner.next();
                        while (line.indexOf("@") > - 1
                                || line.indexOf("private") > - 1
                                || line.indexOf("protected") > - 1
                                || line.indexOf("transient") > - 1
                                || line.indexOf("public") > - 1) {
                            line = scanner.next();
                        }
                        // possible tokens now in line are e.g. Principal, Greeter(PhraseBuilder, Event<Person>, AsyncService ...
                        if (line.indexOf("(") > - 1) {
                            line = line.substring(line.indexOf("(") + 1); // Greeter(PhraseBuilder becomes to PhraseBuilder
                        }
                        if (line.indexOf("<") > - 1 && line.indexOf(">") > - 1) {
                            if (line.startsWith("Event<")) { // e.g. Event<BrowserWindow> events;
                                cdiType = CDIType.EVENT; // set CDIType to Event (it could be setted before as an Inject)
                            }
                            if (line.startsWith("Instance<")) { // e.g. Instance<GlassfishAuthenticator> authenticator;
                                cdiType = CDIType.INSTANCE; // set CDIType to Event (it could be setted before as an Inject)
                            }
                            line = line.substring(line.indexOf("<") + 1, line.indexOf(">")); // Event<Person> becomes to Person
                        }
                        String className = line;
                        JavaSource injectedFound = JavaSourceExaminer.getInstance().getJavaSourceByName(className);
                        // Da die Klasse nicht gefunden wurde, ist diese nicht aus den Projektsourcen, sondern extern oder vom JDK
                        // Für diese wird dann ein neues JavaSource angelegt
                        if (injectedFound != null) {
                            CDIDependency dependency = new CDIDependency(cdiType, javaSource, injectedFound);
                            javaSource.getInjected().add(dependency);
                        } else {
                            // Generate a new JavaSource, which is not explicit in the sources (e.g. Integer, String, double etc.)
                            JavaSource newJavaSource = new JavaSource(className);
                            this.javaSourceContainer.add(newJavaSource);
                            CDIDependency dependency = new CDIDependency(cdiType, javaSource, newJavaSource);
                            javaSource.getInjected().add(dependency);
                        }
                    }
                }
            }
        }
    }

    protected int countChar(String str, char char2Find) {
        str = str.toLowerCase();
        char2Find = Character.toLowerCase(char2Find);
        int count = 0;
        for (int pos = -1; (pos = str.indexOf(char2Find, pos + 1)) != -1; count++);
        return count;
    }

    protected String scanAfterClosedParenthesis(String currentToken, Scanner scanner) {
        Deque<Integer> stack = new ArrayDeque<>();
        int iStack = 1;

        int countParenthesis = countChar(currentToken, '(');
        for (int iCount = 0; iCount < countParenthesis; iCount++) {
            stack.push(iStack);
            iStack++;
        }
        String line = currentToken;
        line = scanner.next();
        boolean bEnd = false;
        while (stack.size() > 0 && !bEnd) {
            if (getCDITypeFromLine(line) != null) {
                break;
            };
            if (line.indexOf("(") > -1) {
                int countOpenParenthesis = countChar(line, '(');
                for (int iCount = 0; iCount < countOpenParenthesis; iCount++) {
                    stack.push(iStack);
                    iStack++;
                }
            }
            if (line.indexOf(")") > -1) {
                int countClosedParenthesis = countChar(line, ')');
                for (int iCount = 0; iCount < countClosedParenthesis; iCount++) {
                    stack.pop();
                    iStack++;
                }
            }
            if (scanner.hasNext()) {
                line = scanner.next();
            } else {
                bEnd = true;
            }
            iStack++;
        }

        return line;
    }

    protected CDIType getCDITypeFromLine(String line) {
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
        if (line.indexOf("@OneToOne") > -1) {
            cdiType = CDIType.ONE_TO_ONE;
        }
        if (line.indexOf("@OneToMany") > -1) {
            cdiType = CDIType.ONE_TO_MANY;
        }
        if (line.indexOf("@ManyToOne") > -1) {
            cdiType = CDIType.MANY_TO_ONE;
        }
        if (line.indexOf("@ManyToMany") > -1) {
            cdiType = CDIType.MANY_TO_MANY;
        }
        return cdiType;
    }
}

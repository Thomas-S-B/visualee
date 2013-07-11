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
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class JavaSource {

    private File javaFile;
    private List<CDIDependency> injected;
    private int id;   // for D3.js, links need ids's form the nodes (id's start with 0)
    private int group;   // Nodes form the same package have the same group-number
    private String packagePath;
    private String sourceCode;
    private String name;

    public JavaSource(File javaFile) {
        this.javaFile = javaFile;
        this.name = javaFile.getName().substring(0, javaFile.getName().indexOf(".java"));
        injected = new ArrayList<>();
        sourceCode = "";
    }

    public JavaSource(String name) {
        this.name = name;
        injected = new ArrayList<>();
        sourceCode = "Not available";
    }

    public File getJavaFile() {
        return javaFile;
    }

    public void setJavaFile(File javaFile) {
        this.javaFile = javaFile;
    }

    public List<CDIDependency> getInjected() {
        return injected;
    }

    public void setInjected(List<CDIDependency> injected) {
        this.injected = injected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public String getEscapedSourceCode() {
        // &lt; and &gt; are important, e.g. a sourcecode like "List<Scripts> ..." causes problems with the javascript in the ui
        return sourceCode.replace("<", "&lt;").replace(">", "&gt;");
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

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
import de.strullerbaumann.visualee.cdi.CDIFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class JavaSourceContainer {

    private Map<String, JavaSource> javaSources;

    public JavaSourceContainer() {
        javaSources = new ConcurrentHashMap<>();
    }

    public Collection<JavaSource> getJavaSources() {
        return javaSources.values();
    }

    public void add(JavaSource javaSource) {
        if (javaSource == null) {
            return;
        }
        if (javaSource.getJavaFile() != null) {
            javaSources.put(javaSource.getJavaFile().getName(), javaSource);
        } else {
            javaSources.put(javaSource.getName(), javaSource);
        }
    }

    public JavaSource getJavaSourceByName(String n) {
        return javaSources.get(n);
    }

    public List<JavaSource> getCDIRelevantClasses() {
        return getCDIRelevantClasses(null);
    }

    public List<JavaSource> getCDIRelevantClasses(CDIFilter cdiFilter) {
        List<JavaSource> classesCDIRelated = new ArrayList<>();
        for (JavaSource javaSource : getJavaSources()) {
            if (javaSource.getInjected().size() > 0) {
                for (CDIDependency dependency : javaSource.getInjected()) {
                    if (cdiFilter == null || cdiFilter.contains(dependency.getCdiType())) {
                        classesCDIRelated.add(dependency.getJavaSourceFrom());
                        classesCDIRelated.add(dependency.getJavaSourceTo());
                    }
                }
            }
        }

        return classesCDIRelated;
    }
}

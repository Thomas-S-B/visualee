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
import static de.strullerbaumann.visualee.cdi.CDIType.EJB;
import static de.strullerbaumann.visualee.cdi.CDIType.EVENT;
import static de.strullerbaumann.visualee.cdi.CDIType.INJECT;
import static de.strullerbaumann.visualee.cdi.CDIType.INSTANCE;
import static de.strullerbaumann.visualee.cdi.CDIType.OBSERVES;
import static de.strullerbaumann.visualee.cdi.CDIType.PRODUCES;
import java.util.HashMap;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class Description {

    public static String generateDescription(JavaSource javaSource) {
        StringBuilder description = new StringBuilder();

        description.append("<nobr>Package: ")
                .append(javaSource.getPackagePath())
                .append("</nobr>");

        HashMap<CDIType, StringBuilder> descriptionParts = new HashMap<>();
        for (CDIType type : CDIType.values()) {
            descriptionParts.put(type, new StringBuilder());
        }

        for (CDIDependency dependency : javaSource.getInjected()) {
            StringBuilder descriptionPart = descriptionParts.get(dependency.getCdiType());
            assert descriptionPart != null : "Unknown CDIType";

            descriptionPart.append("<br/><nobr>");
            switch (dependency.getCdiType()) {
                case EJB:
                    descriptionPart.append(dependency.getJavaSourceTo())
                            .append(" is injected as an EJB");
                    break;
                case EVENT:
                    descriptionPart.append("Fires ")
                            .append(dependency.getJavaSourceTo())
                            .append(" as an event");
                    break;
                case INJECT:
                    descriptionPart.append(dependency.getJavaSourceTo())
                            .append(" is injected");
                    break;
                case OBSERVES:
                    descriptionPart.append("Observes ")
                            .append(dependency.getJavaSourceTo())
                            .append(" events");
                    break;
                case PRODUCES:
                    descriptionPart.append("Produces ")
                            .append(dependency.getJavaSourceTo());
                    break;
                case INSTANCE:
                    descriptionPart.append(dependency.getJavaSourceTo())
                            .append(" is injected as an instance");
                    break;
            }
            descriptionPart.append("</nobr>");
        }

        for (CDIType cdiType : descriptionParts.keySet()) {
            description.append(getDescriptionPart(getDescriptionHeader(cdiType), descriptionParts.get(cdiType).toString()));
        }

        return description.toString();
    }

    protected static String getDescriptionHeader(CDIType cdiType) {
        switch (cdiType) {
            case EJB:
                return "EJB:";
            case EVENT:
                return "Events:";
            case INJECT:
                return "Injects:";
            case OBSERVES:
                return "Observes:";
            case PRODUCES:
                return "Produces:";
            case INSTANCE:
                return "Instances:";
        }

        return null;
    }

    protected static String getDescriptionPart(String header, String description) {
        StringBuilder descriptionPart = new StringBuilder();
        if (description.length() > 0) {
            descriptionPart.append("<br/><br/>");
            if (header != null) {
                descriptionPart.append(header);
            }
            descriptionPart.append(description);
            descriptionPart.append("</nobr>");
        }
        return descriptionPart.toString();
    }
}

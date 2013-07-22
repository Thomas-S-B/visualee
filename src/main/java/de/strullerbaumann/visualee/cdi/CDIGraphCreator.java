/*
 * Created on 10.07.2013 - 09:27:30
 *
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.cdi;

import de.strullerbaumann.visualee.resources.Description;
import de.strullerbaumann.visualee.resources.JavaSource;
import de.strullerbaumann.visualee.resources.JavaSourceContainer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class CDIGraphCreator {

    public static CDIGraph generateCDIGraph(String fileName, File outputdirectory, CDIFilter cdiFilter, boolean onlyCDIRelevantClasses, InputStream htmlTemplateIS, JavaSourceContainer javaSourceContainer) {
        CDIGraph graph = new CDIGraph();
        File jsonFile = new File(outputdirectory + "/" + fileName + ".json");
        graph.setJsonFile(jsonFile);
        File htmlFile = new File(outputdirectory + "/" + fileName + ".html");
        graph.setHtmlFile(htmlFile);
        graph.setHtmlTemplateIS(htmlTemplateIS);

        // Graphen für die Javaklassen erstellen
        // JSON erzeugen
        // CDI relevante Klassen ermitteln, also alle welche CDI haben oder für CDI hergenommen werden
        // und nur Event oder Observer haben
        List<JavaSource> classesCDIRelated = javaSourceContainer.getCDIRelevantClasses(cdiFilter);
        JsonObjectBuilder builder = Json.createObjectBuilder();

        // Nodes
        JsonArrayBuilder nodesArray = Json.createArrayBuilder();
        int id = 0;
        for (JavaSource javaSource : javaSourceContainer.getJavaSources()) {
            if (!onlyCDIRelevantClasses || (onlyCDIRelevantClasses && classesCDIRelated.contains(javaSource))) {
                javaSource.setId(id);
                JsonObjectBuilder node = Json.createObjectBuilder();
                node.add("name", javaSource.toString())
                        .add("group", javaSource.getGroup())
                        .add("description", Description.generateDescription(javaSource))
                        .add("sourcecode", javaSource.getEscapedSourceCode())
                        .add("id", id);
                nodesArray.add(node);
                id++;
            }
        }
        builder.add("nodes", nodesArray);

        // Links
        JsonArrayBuilder linksArray = Json.createArrayBuilder();
        for (JavaSource myJavaClass : javaSourceContainer.getJavaSources()) {
            int target = myJavaClass.getId();
            int value = 1;
            for (CDIDependency dependency : myJavaClass.getInjected()) {
                if (cdiFilter == null || cdiFilter.contains(dependency.getCdiType())) {
                    int source = dependency.getJavaSourceTo().getId();
                    CDIType cdiType = dependency.getCdiType();
                    JsonObjectBuilder linksBuilder = Json.createObjectBuilder();
                    if (cdiType == CDIType.EVENT
                            || cdiType == CDIType.PRODUCES
                            || cdiType == CDIType.OBSERVES
                            || cdiType == CDIType.ONE_TO_MANY
                            || cdiType == CDIType.ONE_TO_ONE
                            || cdiType == CDIType.MANY_TO_ONE
                            || cdiType == CDIType.MANY_TO_MANY) {
                        // Bei Events, Produces, OneToMany und Observers gerade andersrum, s. d. der Pfeil zum Observer bzw. zur Produces Klasse gehen soll
                        linksBuilder.add("source", target);
                        linksBuilder.add("target", source);
                    } else {
                        linksBuilder.add("source", source);
                        linksBuilder.add("target", target);
                    }
                    linksBuilder.add("value", value);
                    linksBuilder.add("type", cdiType.toString());
                    linksArray.add(linksBuilder);
                }
            }
        }

        builder.add("links", linksArray);
        JsonObject json = builder.build();
        try (PrintStream ps = new PrintStream(jsonFile)) {
            ps.println(json.toString());
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(CDIAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
        graph.setCountClasses(id);

        return graph;
    }
}

/*
 * Created on 10.07.2013 - 15:32:13
 *
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.resources;

import java.io.File;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class JavaSourceTest {

    public JavaSourceTest() {
    }

    @Test
    public void testConstructor() {
        String expected = "test2myTestJavaFile";
        File javaFile = new File("/test1/" + expected + ".java");
        JavaSource javaSource = new JavaSource(javaFile);
        assertEquals(expected, javaSource.getName());
    }

    @Test
    public void testGetEscapedSourceCode() {

        String testSource = "public void escalate(@Observes @Severity(Severity.Level.HEARTBEAT) Snapshot current) {\n"
                + "      List<Script> scripts = this.scripting.activeScripts();\n"
                + "      try {";
        String expected = "public void escalate(@Observes @Severity(Severity.Level.HEARTBEAT) Snapshot current) {\n"
                + "      List&lt;Script&gt; scripts = this.scripting.activeScripts();\n"
                + "      try {";

        JavaSource javaSource = new JavaSource("TestSource");
        javaSource.setSourceCode(testSource);

        assertEquals(expected, javaSource.getEscapedSourceCode());
    }

    @Test
    public void testGetSourceCodeWithoutComments() {

        String testSource = "public void escalate(@Observes @Severity(Severity.Level.HEARTBEAT) Snapshot current) {\n"
                + "      // this is a comment\n"
                + "      List<Script> scripts = this.scripting.activeScripts();\n"
                + "      /* commentblock\n"
                + "      * \n"
                + "      * commentblock\n"
                + "      */\n"
                + "      //this is also a comment\n"
                + "      try {";
        String expected = "public void escalate(@Observes @Severity(Severity.Level.HEARTBEAT) Snapshot current) {\n"
                + "      List<Script> scripts = this.scripting.activeScripts();\n"
                + "      try {\n";

        JavaSource javaSource = new JavaSource("TestSource");
        javaSource.setSourceCode(testSource);

        assertEquals(expected, javaSource.getSourceCodeWithoutComments());
    }
}

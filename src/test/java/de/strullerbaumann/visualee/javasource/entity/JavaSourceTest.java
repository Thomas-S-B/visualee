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
package de.strullerbaumann.visualee.javasource.entity;

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
              + "      //this is also a comment - give them a try: Gilad Hekselman - Split Life\n"
              + "      try {";
      String expected = "public void escalate(@Observes @Severity(Severity.Level.HEARTBEAT) Snapshot current) {\n"
              + "      List<Script> scripts = this.scripting.activeScripts();\n"
              + "      try {\n";

      JavaSource javaSource = new JavaSource("TestSource");
      javaSource.setSourceCode(testSource);

      assertEquals(expected, javaSource.getSourceCodeWithoutComments());
   }
}

/*
 * Created on 29.07.2013 - 16:44:09
 *
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.examiner;

import de.strullerbaumann.visualee.resources.JavaSource;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class JavaSourceExaminerTest {

   public JavaSourceExaminerTest() {
   }

   @Test
   public void testSetGroupNrs() {
   }

   @Test
   public void testGetSourceCodeScanner() {
   }

   @Test
   public void testFindAndSetPackage() {
      JavaSource javaSource = new JavaSource("TestClass");
      javaSource.setSourceCode(getTestSourceCode());
      JavaSourceExaminer.findAndSetPackage(javaSource);

      String expected = "de.strullerbaumann.visualee.resources";
      String actual = javaSource.getPackagePath();

      assertEquals(expected, actual);
   }

   // TODO auslagern siehe auch ExaminerTest
   private String getTestSourceCode() {
      return getTestSourceCodeBeforeBody() + getTestSourceCodeBody();
   }

   // TODO auslagern siehe auch ExaminerTest
   private String getTestSourceCodeBeforeBody() {
      return "/*\n"
              + " Copyright 2013 Thomas Struller-Baumann, struller-baumann.de\n"
              + "\n"
              + " Licensed under the Apache License, Version 2.0 (the \"License\");\n"
              + " you may not use this file except in compliance with the License.\n"
              + " You may obtain a copy of the License at\n"
              + "\n"
              + " http://www.apache.org/licenses/LICENSE-2.0\n"
              + "\n"
              + " Unless required by applicable law or agreed to in writing, software\n"
              + " distributed under the License is distributed on an \"AS IS\" BASIS,\n"
              + " WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
              + " See the License for the specific language governing permissions and\n"
              + " limitations under the License.\n"
              + " */\n"
              + "package de.strullerbaumann.visualee.resources;\n"
              + "\n"
              + "import de.strullerbaumann.visualee.cdi.CDIDependency;\n"
              + "import de.strullerbaumann.visualee.cdi.CDIType;\n"
              + "import java.io.BufferedReader;\n"
              + "import java.io.FileNotFoundException;\n"
              + "import java.io.FileReader;\n"
              + "import java.io.IOException;\n"
              + "import java.util.HashMap;\n"
              + "import java.util.Map;\n"
              + "import java.util.Scanner;\n"
              + "import java.util.logging.Level;\n"
              + "import java.util.logging.Logger;\n"
              + "\n"
              + "/**\n"
              + " *\n"
              + " * @author Thomas Struller-Baumann <thomas at struller-baumann.de>\n"
              + " */\n"
              + "public class JavaSourceExaminer {\n"
              + "\n";
   }

   // TODO auslagern siehe auch ExaminerTest
   private String getTestSourceCodeBody() {
      return "   private JavaSourceContainer javaSourceContainer;\n"
              + "    private static class JavaSourceExaminerHolder {\n"
              + "        private static final JavaSourceExaminer INSTANCE = new JavaSourceExaminer();\n"
              + "    }\n"
              + "    private JavaSourceExaminer() {\n"
              + "    }\n"
              + "    public static JavaSourceExaminer getInstance() {\n"
              + "        return JavaSourceExaminerHolder.INSTANCE;\n"
              + "    }\n"
              + "}\n";
   }
}

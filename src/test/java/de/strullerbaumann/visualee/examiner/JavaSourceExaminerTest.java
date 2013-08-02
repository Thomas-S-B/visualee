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
package de.strullerbaumann.visualee.examiner;

import de.strullerbaumann.visualee.javasource.entity.JavaSource;
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
      javaSource.setSourceCode(SourceCodeProvider.getTestSourceCode());
      Examiner.findAndSetPackage(javaSource);

      String expected = "de.strullerbaumann.visualee.resources";
      String actual = javaSource.getPackagePath();

      assertEquals(expected, actual);
   }
}

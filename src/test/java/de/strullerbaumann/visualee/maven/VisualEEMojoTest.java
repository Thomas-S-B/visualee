/*
 * Created on 02.08.2013 - 18:14:48
 *
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class VisualEEMojoTest {

   public VisualEEMojoTest() {
   }

   @Test
   public void testGetSourceFolder() {
      String actual;
      String expected = getParsedFileSeparator("//jimi/hendrix/MyTestRoot/src/main");

      VisualEEMojo visualEEMojo = new VisualEEMojo();
      List<String> testSourceRoots = new ArrayList<>();
      testSourceRoots.add(getParsedFileSeparator("//jimi/hendrix/MyTestRoot"));
      testSourceRoots.add(expected);
      testSourceRoots.add(getParsedFileSeparator("//jimi/hendrix/MyTestRoot/target"));
      testSourceRoots.add(getParsedFileSeparator("//jimi/hendrix/MyTestRoot/src/main/java"));
      testSourceRoots.add(getParsedFileSeparator("//jimi/hendrix/MyTestRoot/target/classes"));

      actual = visualEEMojo.getSourceFolder(testSourceRoots);
      assertEquals(expected + File.separatorChar, actual);
   }

   private String getParsedFileSeparator(String input) {
      return input.replaceAll("/", Character.toString(File.separatorChar));
   }
}

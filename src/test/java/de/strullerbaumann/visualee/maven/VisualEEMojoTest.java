/*
 * Created on 02.08.2013 - 18:14:48
 *
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.maven;

import java.io.File;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class VisualEEMojoTest {

   public VisualEEMojoTest() {
   }

   private String getParsedFileSeparator(String input) {
      return input.replaceAll("/", Character.toString(File.separatorChar));
   }
}

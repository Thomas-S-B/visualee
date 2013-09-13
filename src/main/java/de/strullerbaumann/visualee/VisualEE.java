/*
 * Created on 13.09.2013 - 16:27:40
 *
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee;

import de.strullerbaumann.visualee.dependency.boundary.DependencyAnalyzer;
import java.io.File;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class VisualEE {

   public static void main(String args[]) {
      File sourceFolderDir = new File("/home/thomas/work/NetBeansProjects/ee-demos");
      DependencyAnalyzer.getInstance().analyze(sourceFolderDir);
   }
}

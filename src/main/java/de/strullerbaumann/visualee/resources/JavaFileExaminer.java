/*
 * Created on 15.11.2012 - 12:07:47 
 * 
 * Copyright(c) 2012 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.resources;

import de.strullerbaumann.visualee.cdi.CDIType;
import de.strullerbaumann.visualee.cdi.CDIDependency;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class JavaFileExaminer {

   private JavaFilesContainer javaFileContainer;

   private JavaFileExaminer() {
   }

   public static JavaFileExaminer getInstance() {
      return JavaFileExaminerHolder.INSTANCE;
   }

   public void examine(JavaFilesContainer javaClassContainer) throws FileNotFoundException, IOException {
      this.javaFileContainer = javaClassContainer;
      for (JavaFile myJavaClass : javaClassContainer.getMyJavaFiles()) {
         findAndSetAttributes(myJavaClass);
      }

      // Group durchnumerieren/setzen aus packagePaths (diese sind ja jetzt alle belegt)
      // alles aus dem sleben Package haben die selbe griup
      Map<String, Integer> packagePaths = new HashMap<>();
      int groupNr = 1;
      for (JavaFile myJavaClass : javaClassContainer.getMyJavaFiles()) {
         if (!packagePaths.containsKey(myJavaClass.getPackagePath())) {
            packagePaths.put(myJavaClass.getPackagePath(), groupNr);
            groupNr++;
         }
      }
      // Nun alle Group setzen
      for (JavaFile myJavaClass : javaClassContainer.getMyJavaFiles()) {
         int group = packagePaths.get(myJavaClass.getPackagePath());
         myJavaClass.setGroup(group);
      }

   }

   public JavaFile getMyJavaClassByName(String className) {
      for (JavaFile myJavaFile : javaFileContainer.getMyJavaFiles()) {
         if (myJavaFile.getJavaFile().getName().endsWith(className + ".java")) {
            return myJavaFile;
         }
      }
      return null;
   }

   public void findAndSetAttributes(JavaFile myJavaClass) throws FileNotFoundException, IOException {
      try (Scanner scanner = new Scanner(new FileReader(myJavaClass.getJavaFile()))) {
         scanner.useDelimiter("[ \t\r\n]+");

         while (scanner.hasNext()) {
            String line = scanner.next();
            // Find and set package
            if (line.equals("package")) {
               line = scanner.next();
               String packagePath = line.substring(0, line.indexOf(';'));  //das ; am Ende wegmachen
               myJavaClass.setPackagePath(packagePath);
            }

            // Find and set CDI-Dependencies
            CDIType cdiType = null;
            if (line.indexOf("@EJB") > -1) {
               cdiType = CDIType.EJB;
            }
            if (line.indexOf("@Inject") > -1) {
               cdiType = CDIType.INJECT;
            }
            if (line.indexOf("@Observes") > -1) {
               cdiType = CDIType.OBSERVES;
            }
            if (line.indexOf("@Produces") > -1) {
               cdiType = CDIType.PRODUCES;
            }

            if (cdiType != null) {
               // if (line.indexOf("@Inject") > -1 || line.indexOf("@EJB") > -1 || line.indexOf("@Produces") > -1 || line.indexOf("@Observes") > -1) {
               // Wir haben einen Inject
               // nächste Zeile lesen
               line = scanner.next();
               while (line.indexOf("@") > - 1 || line.indexOf("private") > - 1 || line.indexOf("public") > - 1) {
                  line = scanner.next();
               }
               // mögliche Tokens die jetzt in line stehen
               // Principal
               // Greeter(PhraseBuilder
               // Event<Person>
               // AsyncService
               //
               if (line.indexOf("(") > - 1) {
                  line = line.substring(line.indexOf("(") + 1);  // Greeter(PhraseBuilder wird zu PhraseBuilder
               }
               if (line.indexOf("<") > - 1 && line.indexOf(">") > - 1) {
                  if (line.startsWith("Event<")) { // z.B. Event<BrowserWindow> events;
                     cdiType = CDIType.EVENT;   // der CDIType muss auf Event sein (kann vorher Inject sein)
                  }
                  if (line.startsWith("Instance<")) { // z.B. Instance<GlassfishAuthenticator> authenticator;
                     cdiType = CDIType.INSTANCE;   // der CDIType muss auf Event sein (kann vorher Inject sein)
                  }

                  line = line.substring(line.indexOf("<") + 1, line.indexOf(">"));  // Event<Person> wird zu Person
               }
               String className = line;
               JavaFile injectedFound = JavaFileExaminer.getInstance().getMyJavaClassByName(className);
               if (injectedFound != null) {
                  CDIDependency dependency = new CDIDependency(cdiType, myJavaClass, injectedFound);
                  myJavaClass.getInjected().add(dependency);
               }
            }
         }
      }
   }

   private static class JavaFileExaminerHolder {

      private static final JavaFileExaminer INSTANCE = new JavaFileExaminer();
   }
   // Beispiel:
   // aus classFolder "/home/thomas/work/NetBeansProjects/wissen/target/wissen-1.0-SNAPSHOT/WEB-INF/classes"
   // und javaClassFile "/home/thomas/work/NetBeansProjects/wissen/target/wissen-1.0-SNAPSHOT/WEB-INF/classes/de/strullerbaumann/wissen/presentation/MemoBean.class"
   // liefert es dann "de.strullerbaumann.wissen.presentation.MemoBean"
   /*
    public String getPackageJavaName(File classFolder, File javaClassFile) {
    String classFolderPath = classFolder.getAbsolutePath();
    String classPath = javaClassFile.getAbsolutePath();
      
    String packageClassName = classPath.substring(classFolderPath.length()+1); //+1 wegen dem führenden /
    packageClassName = packageClassName.substring(0, packageClassName.indexOf(".class"));  // .class am Ende entfernen
    packageClassName = packageClassName.replace('/', '.'); // alle Delimiter mit . ersetzen
      
    return packageClassName;
    }
    */
}

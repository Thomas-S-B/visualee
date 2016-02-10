package de.strullerbaumann.visualee.testdata;

import de.strullerbaumann.visualee.dependency.boundary.DependencyContainer;
import de.strullerbaumann.visualee.dependency.entity.Dependency;
import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.source.boundary.JavaSourceContainer;
import de.strullerbaumann.visualee.source.entity.JavaSource;
import de.strullerbaumann.visualee.source.entity.JavaSourceFactory;

/*
 * #%L
 * visualee
 * %%
 * Copyright (C) 2013 Thomas Struller-Baumann
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de)
 */
public final class TestDataProvider {

   public static String getTestSourceCode() {
      return getTestSourceCodeBeforeBody() + getTestSourceCodeBody();
   }

   public static String getTestSourceCodeBeforeBody() {
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
              + " * @author Thomas Struller-Baumann (contact at struller-baumann.de)\n"
              + " */\n"
              + "public class JavaSourceExaminer {\n"
              + "\n";
   }

   public static String getTestSourceCodeBody() {
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

   public static void createSampleDependencies() {
      JavaSourceContainer.getInstance().clear();
      DependencyContainer.getInstance().clear();

      JavaSource producer = JavaSourceFactory.getInstance().newJavaSource("Producer");
      JavaSourceContainer.getInstance().add(producer);

      JavaSource product1 = JavaSourceFactory.getInstance().newJavaSource("Product1");
      JavaSourceContainer.getInstance().add(product1);
      JavaSource product2 = JavaSourceFactory.getInstance().newJavaSource("Product2");
      JavaSourceContainer.getInstance().add(product2);
      JavaSource product3 = JavaSourceFactory.getInstance().newJavaSource("Product3");
      JavaSourceContainer.getInstance().add(product3);
      JavaSource product4 = JavaSourceFactory.getInstance().newJavaSource("Product4");
      JavaSourceContainer.getInstance().add(product4);

      JavaSource instance1 = JavaSourceFactory.getInstance().newJavaSource("Instance1");
      JavaSourceContainer.getInstance().add(instance1);
      JavaSource instance2 = JavaSourceFactory.getInstance().newJavaSource("Instance2");
      JavaSourceContainer.getInstance().add(instance2);
      JavaSource instance3 = JavaSourceFactory.getInstance().newJavaSource("Instance3");
      JavaSourceContainer.getInstance().add(instance3);
      JavaSource instance4 = JavaSourceFactory.getInstance().newJavaSource("Instance4");
      JavaSourceContainer.getInstance().add(instance4);

      Dependency dProducer_Product1 = new Dependency(DependencyType.PRODUCES, producer, product1);
      DependencyContainer.getInstance().add(dProducer_Product1);
      Dependency dProducer_Product2 = new Dependency(DependencyType.PRODUCES, producer, product2);
      DependencyContainer.getInstance().add(dProducer_Product2);
      Dependency dProducer_Product3 = new Dependency(DependencyType.PRODUCES, producer, product3);
      DependencyContainer.getInstance().add(dProducer_Product3);
      Dependency dProducer_Product4 = new Dependency(DependencyType.PRODUCES, producer, product4);
      DependencyContainer.getInstance().add(dProducer_Product4);

      Dependency dProduct1_Instance1 = new Dependency(DependencyType.INSTANCE, instance1, product1);
      DependencyContainer.getInstance().add(dProduct1_Instance1);
      Dependency dProduct2_Instance2 = new Dependency(DependencyType.INSTANCE, instance2, product2);
      DependencyContainer.getInstance().add(dProduct2_Instance2);
      Dependency dProduct3_Instance3 = new Dependency(DependencyType.INSTANCE, instance3, product3);
      DependencyContainer.getInstance().add(dProduct3_Instance3);
      Dependency dProduct4_Instance4 = new Dependency(DependencyType.INSTANCE, instance4, product4);
      DependencyContainer.getInstance().add(dProduct4_Instance4);

      //Add some not relevant (direct connected) JavaSources
      JavaSource notRelevant1 = JavaSourceFactory.getInstance().newJavaSource("NotRelevant1");
      JavaSourceContainer.getInstance().add(notRelevant1);
      JavaSource notRelevant2 = JavaSourceFactory.getInstance().newJavaSource("NotRelevant2");
      JavaSourceContainer.getInstance().add(notRelevant2);
      JavaSource notRelevant3 = JavaSourceFactory.getInstance().newJavaSource("NotRelevant3");
      JavaSourceContainer.getInstance().add(notRelevant3);

      Dependency dInject1_notRelevant1 = new Dependency(DependencyType.INSTANCE, instance1, notRelevant1);
      DependencyContainer.getInstance().add(dInject1_notRelevant1);
      Dependency dInject1_notRelevant2 = new Dependency(DependencyType.INSTANCE, instance1, notRelevant2);
      DependencyContainer.getInstance().add(dInject1_notRelevant2);

      Dependency dInject3_notRelevant1 = new Dependency(DependencyType.INSTANCE, instance3, notRelevant1);
      DependencyContainer.getInstance().add(dInject3_notRelevant1);
      Dependency dInject3_notRelevant3 = new Dependency(DependencyType.INSTANCE, instance3, notRelevant3);
      DependencyContainer.getInstance().add(dInject3_notRelevant3);
   }

   public static String getTestCompleteSourceCodeExample() {
      return ""
              + "package org.lightfish.business.heartbeat.boundary;\n"
              + "import java.io.Writer;\n"
              + "import java.util.ArrayList;\n"
              + "import java.util.List;\n"
              + "import java.util.concurrent.ConcurrentLinkedQueue;\n"
              + "import java.util.logging.Level;\n"
              + "import java.util.logging.Logger;\n"
              + "import javax.ejb.ConcurrencyManagement;\n"
              + "import javax.ejb.ConcurrencyManagementType;\n"
              + "import javax.ejb.Singleton;\n"
              + "import javax.enterprise.event.Observes;\n"
              + "import javax.inject.Inject;\n"
              + "import org.lightfish.business.heartbeat.control.Serializer;\n"
              + "import org.lightfish.business.servermonitoring.boundary.Severity;\n"
              + "import org.lightfish.business.servermonitoring.entity.Snapshot;\n"
              + "import org.lightfish.presentation.publication.AsyncMultiWriter;\n"
              + "import org.lightfish.presentation.publication.BrowserWindow;\n"
              + "/**\n"
              + " *\n"
              + " * @author Adam Bien, blog.adam-bien.com\n"
              + " */\n"
              + "@Singleton\n"
              + "@ConcurrencyManagement(ConcurrencyManagementType.BEAN)\n"
              + "public class SnapshotEventBroker {\n"
              + "    private ConcurrentLinkedQueue<BrowserWindow> browsers = new ConcurrentLinkedQueue<>();\n"
              + "    @Inject\n"
              + "    Logger LOG;\n"
              + "    @Inject\n"
              + "    Serializer serializer;\n"
              + "    public void onBrowserRequest(@Observes BrowserWindow browserWindow) {\n"
              + "        LOG.log(Level.FINE, \"Added {0} to watch channel {1}\", new Object[]{browserWindow.hashCode(), browserWindow.getChannel()});\n"
              + "        browsers.add(browserWindow);\n"
              + "    }\n"
              + "    public void onNewSnapshot(@Observes @Severity(Severity.Level.HEARTBEAT) Snapshot snapshot) {\n"
              + "        LOG.info(\"SnapshotEventBroker.oneNewSnapshot: \" + snapshot.getId());\n"
              + "        List<BrowserWindow> currentBrowsers = new ArrayList<>(browsers);\n"
              + "        List<BrowserWindow> staging = new ArrayList<>(browsers.size());\n"
              + "        AsyncMultiWriter multiWriter = new AsyncMultiWriter();\n"
              + "        for (BrowserWindow browserWindow : currentBrowsers) {\n"
              + "            if (browserWindow.getChannel() == null || browserWindow.getChannel().trim().isEmpty()) {\n"
              + "                LOG.log(Level.INFO, \"Found a browser window({0}) with no channel\", browserWindow.hashCode());\n"
              + "            }\n"
              + "            if (snapshot.getInstanceName().equals(browserWindow.getChannel())) {\n"
              + "                LOG.log(Level.FINEST, \"Staging {0}\", browserWindow.hashCode());\n"
              + "                Writer writer = browserWindow.getWriter();\n"
              + "                if (writer != null) {\n"
              + "                    multiWriter.addWriter(writer);\n"
              + "                    staging.add(browserWindow);\n"
              + "                } else {\n"
              + "                    browsers.remove(browserWindow);\n"
              + "                }\n"
              + "            }\n"
              + "        }\n"
              + "        browsers.removeAll(staging);\n"
              + "        LOG.finest(\"Serializing snapshot to all staged browser windows\");\n"
              + "        serializer.serialize(snapshot, multiWriter);\n"
              + "        for (BrowserWindow browserWindow : staging) {\n"
              + "            LOG.log(Level.FINEST, \"Sending {0}\", browserWindow.hashCode());\n"
              + "            browserWindow.send();\n"
              + "        }\n"
              + "    }\n"
              + "    void send(BrowserWindow browserWindow, Snapshot snapshot) {\n"
              + "        Writer writer = browserWindow.getWriter();\n"
              + "        serializer.serialize(snapshot, writer);\n"
              + "        browserWindow.send();\n"
              + "    }\n"
              + "}\n";
   }
}

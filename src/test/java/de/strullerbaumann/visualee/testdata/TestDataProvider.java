package de.strullerbaumann.visualee.testdata;

import de.strullerbaumann.visualee.dependency.boundary.DependencyContainer;
import de.strullerbaumann.visualee.dependency.entity.Dependency;
import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.source.boundary.JavaSourceContainer;
import de.strullerbaumann.visualee.source.entity.JavaSource;

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
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
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
              + " * @author Thomas Struller-Baumann <thomas at struller-baumann.de>\n"
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

      JavaSource producer = new JavaSource("Producer");
      JavaSourceContainer.getInstance().add(producer);

      JavaSource product1 = new JavaSource("Product1");
      JavaSourceContainer.getInstance().add(product1);
      JavaSource product2 = new JavaSource("Product2");
      JavaSourceContainer.getInstance().add(product2);
      JavaSource product3 = new JavaSource("Product3");
      JavaSourceContainer.getInstance().add(product3);
      JavaSource product4 = new JavaSource("Product4");
      JavaSourceContainer.getInstance().add(product4);

      JavaSource instance1 = new JavaSource("Instance1");
      JavaSourceContainer.getInstance().add(instance1);
      JavaSource instance2 = new JavaSource("Instance2");
      JavaSourceContainer.getInstance().add(instance2);
      JavaSource instance3 = new JavaSource("Instance3");
      JavaSourceContainer.getInstance().add(instance3);
      JavaSource instance4 = new JavaSource("Instance4");
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
      JavaSource notRelevant1 = new JavaSource("NotRelevant1");
      JavaSourceContainer.getInstance().add(notRelevant1);
      JavaSource notRelevant2 = new JavaSource("NotRelevant2");
      JavaSourceContainer.getInstance().add(notRelevant2);
      JavaSource notRelevant3 = new JavaSource("NotRelevant3");
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
}

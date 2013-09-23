package de.strullerbaumann.visualee.source.boundary;

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
import de.strullerbaumann.visualee.dependency.boundary.DependencyContainer;
import de.strullerbaumann.visualee.dependency.boundary.DependencyFilter;
import de.strullerbaumann.visualee.dependency.entity.Dependency;
import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.source.entity.JavaSource;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class JavaSourceContainerTest {

   public JavaSourceContainerTest() {
   }

   @Before
   public void init() {
      DependencyContainer.getInstance().clear();
   }

   @Test
   public void testGetJavaSourceByName() {
      JavaSourceContainer.getInstance().clear();
      JavaSource javaSource1 = new JavaSource("DataPoint");
      JavaSourceContainer.getInstance().add(javaSource1);
      JavaSource javaSource2 = new JavaSource("int");
      JavaSourceContainer.getInstance().add(javaSource2);
      JavaSource javaSource3 = new JavaSource("MyTestClass");
      JavaSourceContainer.getInstance().add(javaSource3);

      assertNotNull(JavaSourceContainer.getInstance().getJavaSourceByName("MyTestClass"));
      assertEquals("MyTestClass", JavaSourceContainer.getInstance().getJavaSourceByName("MyTestClass").getName());
      assertNotNull(JavaSourceContainer.getInstance().getJavaSourceByName("int"));
      assertEquals("int", JavaSourceContainer.getInstance().getJavaSourceByName("int").getName());
      assertNotNull(JavaSourceContainer.getInstance().getJavaSourceByName("DataPoint"));
      assertEquals("DataPoint", JavaSourceContainer.getInstance().getJavaSourceByName("DataPoint").getName());
   }

   @Test
   public void testAddJavaSourceDoublette() {
      JavaSourceContainer.getInstance().clear();
      String name = "DataPoint";
      JavaSource javaSource1 = new JavaSource(name);
      String sourceCode1 = "Test source code for JavaSource1 (listen to Brian Blade - Season of change";
      javaSource1.setSourceCode(sourceCode1);
      JavaSourceContainer.getInstance().add(javaSource1);
      JavaSource javaSource2 = new JavaSource(name);
      javaSource2.setSourceCode("");
      JavaSourceContainer.getInstance().add(javaSource2);

      assertNotNull(JavaSourceContainer.getInstance().getJavaSourceByName(name));
      assertEquals(1, JavaSourceContainer.getInstance().getJavaSources().size());
      assertEquals(name, JavaSourceContainer.getInstance().getJavaSourceByName(name).getName());
      assertEquals(sourceCode1, JavaSourceContainer.getInstance().getJavaSourceByName(name).getSourceCode());
   }

   @Test
   public void testAddNullJavaSource() {
      JavaSourceContainer.getInstance().clear();
      JavaSource javaSource1 = null;

      JavaSourceContainer.getInstance().add(javaSource1);
      assertEquals(0, JavaSourceContainer.getInstance().getJavaSources().size());
   }

   @Test
   public void testGetRelevantClasses() {
      JavaSourceContainer.getInstance().clear();
      int count = 10;

      JavaSource javaSourceInj = new JavaSource("Testinject");
      JavaSourceContainer.getInstance().add(javaSourceInj);

      for (int i = 0; i < count; i++) {
         String name = "Testclass " + i;
         JavaSource javaSource = new JavaSource(name);
         List<Dependency> injected = new ArrayList<>();
         injected.add(new Dependency(DependencyType.INJECT, javaSource, javaSourceInj));
         DependencyContainer.getInstance().addAll(injected);
         JavaSourceContainer.getInstance().add(javaSource);
      }

      // + 1 because of the javaSourceInj
      assertEquals(count + 1, DependencyContainer.getInstance().getRelevantClasses().size());
   }

   @Test
   public void testGetRelevantClassesFilter() {
      JavaSourceContainer.getInstance().clear();
      int count = 10;
      int count1 = 0;
      int count2 = 0;

      DependencyType type1 = DependencyType.INJECT;
      DependencyType type2 = DependencyType.EJB;

      JavaSource javaSourceType1 = new JavaSource("Testinject");
      JavaSourceContainer.getInstance().add(javaSourceType1);

      JavaSource javaSourceType2 = new JavaSource("TestEjb");
      JavaSourceContainer.getInstance().add(javaSourceType2);

      for (int i = 0; i < count; i++) {
         String name = "Testclass " + i;
         JavaSource javaSource = new JavaSource(name);
         List<Dependency> injected = new ArrayList<>();
         if (i % 2 > 0) {
            injected.add(new Dependency(type1, javaSource, javaSourceType1));
            count1++;
         } else {
            injected.add(new Dependency(type2, javaSource, javaSourceType2));
            count2++;
         }
         JavaSourceContainer.getInstance().add(javaSource);
         DependencyContainer.getInstance().addAll(injected);
      }

      // + 1 because of the injected javaSourceType1
      DependencyFilter filter1 = new DependencyFilter().addType(type1);
      assertEquals(count1 + 1, DependencyContainer.getInstance().getRelevantClasses(filter1).size());

      // + 1 because of the injected javaSourceType2
      DependencyFilter filter2 = new DependencyFilter().addType(type2);
      assertEquals(count2 + 1, DependencyContainer.getInstance().getRelevantClasses(filter2).size());
   }

   @Test
   @Ignore
   public void testGetRelevantClassesDirectlyConnected() {
      JavaSourceContainer.getInstance().clear();
      int count = 10;

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

      JavaSource inject1 = new JavaSource("Inject1");
      JavaSourceContainer.getInstance().add(inject1);
      JavaSource inject2 = new JavaSource("Inject2");
      JavaSourceContainer.getInstance().add(inject2);
      JavaSource inject3 = new JavaSource("Inject3");
      JavaSourceContainer.getInstance().add(inject3);
      JavaSource inject4 = new JavaSource("Inject4");
      JavaSourceContainer.getInstance().add(inject4);

      Dependency dProducer_Product1 = new Dependency(DependencyType.PRODUCES, producer, product1);
      DependencyContainer.getInstance().add(dProducer_Product1);
      Dependency dProducer_Product2 = new Dependency(DependencyType.PRODUCES, producer, product2);
      DependencyContainer.getInstance().add(dProducer_Product2);
      Dependency dProducer_Product3 = new Dependency(DependencyType.PRODUCES, producer, product3);
      DependencyContainer.getInstance().add(dProducer_Product3);
      Dependency dProducer_Product4 = new Dependency(DependencyType.PRODUCES, producer, product4);
      DependencyContainer.getInstance().add(dProducer_Product4);

      Dependency dProduct1_Inject1 = new Dependency(DependencyType.INJECT, product1, inject1);
      DependencyContainer.getInstance().add(dProduct1_Inject1);
      Dependency dProduct2_Inject2 = new Dependency(DependencyType.INJECT, product2, inject2);
      DependencyContainer.getInstance().add(dProduct2_Inject2);
      Dependency dProduct3_Inject3 = new Dependency(DependencyType.INJECT, product3, inject3);
      DependencyContainer.getInstance().add(dProduct3_Inject3);
      Dependency dProduct4_Inject4 = new Dependency(DependencyType.INJECT, product4, inject4);
      DependencyContainer.getInstance().add(dProduct4_Inject4);


      DependencyFilter filter = new DependencyFilter()
              .addType(DependencyType.INSTANCE)
              .addType(DependencyType.PRODUCES)
              .setDirectlyConnected(true);
      //assertEquals(9, JavaSourceContainer.getInstance().getRelevantClasses(filter));

      JavaSource notRelevant1 = new JavaSource("NotRelevant1");
      JavaSourceContainer.getInstance().add(notRelevant1);
      JavaSource notRelevant2 = new JavaSource("NotRelevant2");
      JavaSourceContainer.getInstance().add(notRelevant2);
      JavaSource notRelevant3 = new JavaSource("NotRelevant3");
      JavaSourceContainer.getInstance().add(notRelevant3);

      Dependency dInject1_notRelevant1 = new Dependency(DependencyType.INJECT, inject1, notRelevant1);
      DependencyContainer.getInstance().add(dInject1_notRelevant1);
      Dependency dInject1_notRelevant2 = new Dependency(DependencyType.INJECT, inject1, notRelevant2);
      DependencyContainer.getInstance().add(dInject1_notRelevant2);

      Dependency dInject3_notRelevant1 = new Dependency(DependencyType.INJECT, inject3, notRelevant1);
      DependencyContainer.getInstance().add(dInject3_notRelevant1);

      assertEquals(9, DependencyContainer.getInstance().getRelevantClasses(filter));
   }
}

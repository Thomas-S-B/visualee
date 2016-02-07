package de.strullerbaumann.visualee.dependency.boundary;

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
import de.strullerbaumann.visualee.dependency.entity.Dependency;
import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.source.boundary.*;
import de.strullerbaumann.visualee.source.entity.JavaSource;
import de.strullerbaumann.visualee.source.entity.JavaSourceFactory;
import de.strullerbaumann.visualee.testdata.TestDataProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class DependencyContainerTest {

   public DependencyContainerTest() {
   }

   @Before
   public void init() {
      DependencyContainer.getInstance().clear();
   }

   @Test
   public void testGetRelevantClassesFilter() {
      JavaSourceContainer.getInstance().clear();
      int count = 10;
      int count1 = 0;
      int count2 = 0;

      DependencyType type1 = DependencyType.INJECT;
      DependencyType type2 = DependencyType.EJB;

      JavaSource javaSourceType1 = JavaSourceFactory.getInstance().newJavaSource("Testinject");
      JavaSourceContainer.getInstance().add(javaSourceType1);

      JavaSource javaSourceType2 = JavaSourceFactory.getInstance().newJavaSource("TestEjb");
      JavaSourceContainer.getInstance().add(javaSourceType2);

      for (int i = 0; i < count; i++) {
         String name = "Testclass " + i;
         JavaSource javaSource = JavaSourceFactory.getInstance().newJavaSource(name);
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
      assertEquals(count1 + 1, DependencyContainer.getInstance().getFilteredJavaSources(filter1).size());

      // + 1 because of the injected javaSourceType2
      DependencyFilter filter2 = new DependencyFilter().addType(type2);
      assertEquals(count2 + 1, DependencyContainer.getInstance().getFilteredJavaSources(filter2).size());
   }

   @Test
   public void testGetRelevantClassesDirectlyConnected() {
      DependencyFilter filter = new DependencyFilter()
              .addType(DependencyType.PRODUCES)
              .addType(DependencyType.INSTANCE)
              .setDirectlyConnected(true);
      TestDataProvider.createSampleDependencies();
      // only the nine JavaSources connected in the style "Producer-->ProductX-->InjectX" should be delivered
      assertEquals(9, DependencyContainer.getInstance().getFilteredJavaSources(filter).size());
   }

   @Test
   public void testFindAllDependenciesWith() {
      TestDataProvider.createSampleDependencies();
      JavaSource producer = JavaSourceContainer.getInstance().getJavaSourceByName("Producer");
      JavaSource product1 = JavaSourceContainer.getInstance().getJavaSourceByName("Product1");
      Set<Dependency> foundedDependencies = DependencyContainer.getInstance().findAllDependenciesWith(producer, DependencyType.PRODUCES);
      assertEquals(4, foundedDependencies.size());
      foundedDependencies = DependencyContainer.getInstance().findAllDependenciesWith(producer, DependencyType.EVENT);
      assertEquals(0, foundedDependencies.size());
      foundedDependencies = DependencyContainer.getInstance().findAllDependenciesWith(product1, DependencyType.INSTANCE);
      assertEquals(1, foundedDependencies.size());
   }
}

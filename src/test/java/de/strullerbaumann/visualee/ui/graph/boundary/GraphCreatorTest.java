package de.strullerbaumann.visualee.ui.graph.boundary;

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
import de.strullerbaumann.visualee.dependency.boundary.DependencyFilter;
import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.source.boundary.JavaSourceContainer;
import de.strullerbaumann.visualee.source.entity.JavaSource;
import de.strullerbaumann.visualee.testdata.TestDataProvider;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class GraphCreatorTest {

   public GraphCreatorTest() {
   }

   @Test
   @Ignore
   public void testBuildJSONNode() {
      String name = "MyTestClass";
      String sourcecode = "test source code - Matt Pavolka Group - Something People Can Use";

      JavaSource javaSource = new JavaSource("MyTestClass");
      javaSource.setGroup(2);
      javaSource.setId(1);
      javaSource.setPackagePath("de.test.test2");
      javaSource.setSourceCode(sourcecode);

      JsonObjectBuilder job = GraphCreator.buildJSONNode(javaSource);
      JsonObject node = job.build();
      assertEquals(name, node.getString("name"));
      assertEquals(2, node.getInt("group"));
      assertEquals(1, node.getInt("id"));
      assertNotNull(node.getString("description"));
      assertEquals(sourcecode, node.getString("sourcecode"));
   }

   @Test
   public void testBuildJSONNodes() {
      JavaSourceContainer.getInstance().clear();
      int count = 10;

      String namePrefix = "Testclass ";
      for (int i = 0; i < count; i++) {
         String name = namePrefix + i;
         JavaSource javaSource = new JavaSource(name);
         JavaSourceContainer.getInstance().add(javaSource);
      }

      JsonArray nodes = GraphCreator.buildJSONNodes(null).build();
      assertEquals(count, nodes.size());
   }

   @Test
   public void testBuildJSONLinks() {
      TestDataProvider.createSampleDependencies();
      JsonArray links = GraphCreator.buildJSONLinks(null).build();
      assertEquals(12, links.size());

      DependencyFilter filter = new DependencyFilter()
              .addType(DependencyType.PRODUCES)
              .addType(DependencyType.INSTANCE)
              .setDirectlyConnected(true);
      links = GraphCreator.buildJSONLinks(filter).build();
      assertEquals(8, links.size());
   }
}

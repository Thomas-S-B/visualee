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
package de.strullerbaumann.visualee.ui.graph.boundary;

import de.strullerbaumann.visualee.dependency.entity.Dependency;
import de.strullerbaumann.visualee.javasource.boundary.JavaSourceContainer;
import de.strullerbaumann.visualee.javasource.entity.JavaSource;
import java.util.ArrayList;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class GraphCreatorTest {

   public GraphCreatorTest() {
   }

   @Test
   public void testBuildJSONNode() {
      String name = "MyTestClass";
      String sourcecode = "test source code";

      JavaSource javaSource = new JavaSource("MyTestClass");
      javaSource.setGroup(2);
      javaSource.setId(1);
      javaSource.setInjected(new ArrayList<Dependency>());
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
         List<Dependency> injected = new ArrayList<>();
         javaSource.setInjected(injected);
         JavaSourceContainer.getInstance().add(javaSource);
      }

      JsonArray nodes = GraphCreator.buildJSONNodes(null).build();
      assertEquals(count, nodes.size());
   }
}

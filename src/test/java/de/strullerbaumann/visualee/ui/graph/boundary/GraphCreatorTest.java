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

import de.strullerbaumann.visualee.dependency.entity.DependencyType;
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
   public void testIsInverseDirection() {
      assertFalse(GraphCreator.isInverseDirection(DependencyType.EJB));
      assertTrue(GraphCreator.isInverseDirection(DependencyType.EVENT));
      assertFalse(GraphCreator.isInverseDirection(DependencyType.INJECT));
      assertFalse(GraphCreator.isInverseDirection(DependencyType.INSTANCE));
      assertTrue(GraphCreator.isInverseDirection(DependencyType.MANY_TO_MANY));
      assertTrue(GraphCreator.isInverseDirection(DependencyType.MANY_TO_ONE));
      assertTrue(GraphCreator.isInverseDirection(DependencyType.OBSERVES));
      assertTrue(GraphCreator.isInverseDirection(DependencyType.ONE_TO_MANY));
      assertTrue(GraphCreator.isInverseDirection(DependencyType.ONE_TO_ONE));
      assertTrue(GraphCreator.isInverseDirection(DependencyType.PRODUCES));
   }
}

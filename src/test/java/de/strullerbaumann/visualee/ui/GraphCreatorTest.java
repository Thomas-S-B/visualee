/*
 * Created on 26.07.2013 - 09:36:37
 *
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.ui;

import de.strullerbaumann.visualee.dependency.DependenciyType;
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
      assertFalse(GraphCreator.isInverseDirection(DependenciyType.EJB));
      assertTrue(GraphCreator.isInverseDirection(DependenciyType.EVENT));
      assertFalse(GraphCreator.isInverseDirection(DependenciyType.INJECT));
      assertFalse(GraphCreator.isInverseDirection(DependenciyType.INSTANCE));
      assertTrue(GraphCreator.isInverseDirection(DependenciyType.MANY_TO_MANY));
      assertTrue(GraphCreator.isInverseDirection(DependenciyType.MANY_TO_ONE));
      assertTrue(GraphCreator.isInverseDirection(DependenciyType.OBSERVES));
      assertTrue(GraphCreator.isInverseDirection(DependenciyType.ONE_TO_MANY));
      assertTrue(GraphCreator.isInverseDirection(DependenciyType.ONE_TO_ONE));
      assertTrue(GraphCreator.isInverseDirection(DependenciyType.PRODUCES));
   }
}

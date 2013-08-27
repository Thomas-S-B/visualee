/*
 * Created on 23.08.2013 - 09:49:55
 *
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.dependency.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class DependencyTypeTest {

   public DependencyTypeTest() {
   }

   @Test
   public void testIsInverseDirection() {
      assertFalse(DependencyType.isInverseDirection(DependencyType.EJB));
      assertTrue(DependencyType.isInverseDirection(DependencyType.EVENT));
      assertFalse(DependencyType.isInverseDirection(DependencyType.INJECT));
      assertFalse(DependencyType.isInverseDirection(DependencyType.INSTANCE));
      assertTrue(DependencyType.isInverseDirection(DependencyType.MANY_TO_MANY));
      assertTrue(DependencyType.isInverseDirection(DependencyType.MANY_TO_ONE));
      assertTrue(DependencyType.isInverseDirection(DependencyType.OBSERVES));
      assertTrue(DependencyType.isInverseDirection(DependencyType.ONE_TO_MANY));
      assertTrue(DependencyType.isInverseDirection(DependencyType.ONE_TO_ONE));
      assertTrue(DependencyType.isInverseDirection(DependencyType.PRODUCES));
   }
}

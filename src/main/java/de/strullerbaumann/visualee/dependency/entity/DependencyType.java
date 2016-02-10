package de.strullerbaumann.visualee.dependency.entity;

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
import java.util.Arrays;

/**
 *
 * @author Thomas Struller-Baumann (contact at struller-baumann.de)
 */
public enum DependencyType {

   INJECT,
   EVENT,
   PRODUCES,
   EJB,
   OBSERVES,
   INSTANCE,
   RESOURCE,
   ONE_TO_ONE,
   ONE_TO_MANY,
   MANY_TO_ONE,
   MANY_TO_MANY;

   public static boolean isInverseDirection(DependencyType type) {
      return Arrays.asList(
              DependencyType.EVENT,
              DependencyType.PRODUCES,
              DependencyType.OBSERVES,
              DependencyType.ONE_TO_MANY,
              DependencyType.ONE_TO_ONE,
              DependencyType.MANY_TO_ONE,
              DependencyType.MANY_TO_MANY).contains(type);
   }
}

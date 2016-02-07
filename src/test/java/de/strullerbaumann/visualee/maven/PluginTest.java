package de.strullerbaumann.visualee.maven;

/*
 * #%L
 * visualee
 * %%
 * Copyright (C) 2013 - 2016 Thomas Struller-Baumann
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

import java.io.File;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PluginTest {


    @Test
    public void testCreateDirs (){
        VisualEEMojo vm = new VisualEEMojo();

        File f = new File("oneLevelDir");
        vm.checkCreateDirs(f);
        assertTrue(f.exists());

        f = new File("two/LevelDir");
        vm.checkCreateDirs(f);
        assertTrue(new File("two").exists());
        assertTrue(f.exists());

        f = new File("three/Level/Dir");
        vm.checkCreateDirs(f);
        assertTrue(new File("three").exists());
        assertTrue(new File("three/Level").exists());
        assertTrue(f.exists());

        f = new File("");
        vm.checkCreateDirs(f);
        assertFalse(f.exists());
    }

}

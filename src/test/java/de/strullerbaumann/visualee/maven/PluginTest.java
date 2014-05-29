package de.strullerbaumann.visualee.maven;

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

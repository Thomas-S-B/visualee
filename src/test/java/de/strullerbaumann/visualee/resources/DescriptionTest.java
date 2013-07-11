/*
 * Created on 10.07.2013 - 16:45:45
 *
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.resources;

import de.strullerbaumann.visualee.cdi.CDIDependency;
import de.strullerbaumann.visualee.cdi.CDIType;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class DescriptionTest {

    public DescriptionTest() {
    }

    /**
     * Test of generateDescription method, of class Description.
     */
    @Test
    public void testGenerateDescription() {
        JavaSource javaSource1 = new JavaSource("TestJavaSource1");
        JavaSource javaSource2 = new JavaSource("TestJavaSource2");
        JavaSource javaSource3 = new JavaSource("TestJavaSource3");
        String testPackage = "//my/test/package/path";

        javaSource1.setPackagePath(testPackage);

        CDIDependency dependency12 = new CDIDependency(CDIType.INJECT, javaSource1, javaSource2);
        CDIDependency dependency13 = new CDIDependency(CDIType.OBSERVES, javaSource1, javaSource3);
        List<CDIDependency> dependencies = new ArrayList<>();
        dependencies.add(dependency12);
        dependencies.add(dependency13);
        javaSource1.setInjected(dependencies);

        String actual = Description.generateDescription(javaSource1);

        assertTrue(actual.indexOf(testPackage) > 0);
        assertTrue(actual.indexOf(javaSource2.getName()) > 0);
        assertTrue(actual.indexOf(javaSource3.getName()) > 0);
    }

    /**
     * Test of getDescriptionHeader method, of class Description.
     */
    @Test
    public void testGetDescriptionHeader() {
        for (CDIType cdiType : CDIType.values()) {
            assertNotNull("No descriptionheader for CDIType " + cdiType.name(), Description.getDescriptionHeader(cdiType));
        }
    }

    /**
     * Test of getDescriptionPart method, of class Description.
     */
    @Test
    public void testGetDescriptionPart() {
        String header = "TestHeader";
        String description = "My test desciption";
        String actual = Description.getDescriptionPart(header, description);

        assertTrue(actual.indexOf(header) > 0);
        assertTrue(actual.indexOf(description) > 0);
    }
}

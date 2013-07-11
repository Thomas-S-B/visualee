/*
 * Created on 11.07.2013 - 11:19:01
 *
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.resources;

import de.strullerbaumann.visualee.cdi.CDIType;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class JavaSourceExaminerTest {

    public JavaSourceExaminerTest() {
    }

    /**
     * Test of getCDITypeFromLine method, of class JavaSourceExaminer.
     */
    @Test
    public void testGetCDITypeFromLine() {
        String sourceLine;
        CDIType actual;

        sourceLine = "My test desciption";
        actual = JavaSourceExaminer.getInstance().getCDITypeFromLine(sourceLine);
        assertEquals(null, actual);

        sourceLine = "@EJB";
        actual = JavaSourceExaminer.getInstance().getCDITypeFromLine(sourceLine);
        assertEquals(CDIType.EJB, actual);

        sourceLine = "@EJB(name = \"java:global/test/test-ejb/TestService\", beanInterface = TestService.class)";
        actual = JavaSourceExaminer.getInstance().getCDITypeFromLine(sourceLine);
        assertEquals(CDIType.EJB, actual);

        sourceLine = "@Inject TestCalss myTestClass;";
        actual = JavaSourceExaminer.getInstance().getCDITypeFromLine(sourceLine);
        assertEquals(CDIType.INJECT, actual);

        sourceLine = "public void onEscalationBrowserRequest(@Observes Escalation escalation) {";
        actual = JavaSourceExaminer.getInstance().getCDITypeFromLine(sourceLine);
        assertEquals(CDIType.OBSERVES, actual);

        sourceLine = "@Produces";
        actual = JavaSourceExaminer.getInstance().getCDITypeFromLine(sourceLine);
        assertEquals(CDIType.PRODUCES, actual);

        sourceLine = "@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})";
        actual = JavaSourceExaminer.getInstance().getCDITypeFromLine(sourceLine);
        assertEquals(null, actual);

    }
}

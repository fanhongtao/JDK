/*
 * XmlConfigUtilsTest.java
 * JUnit based test
 *
 * Created on July 13, 2006, 4:10 PM
 *
 * @(#)XmlConfigUtilsTest.java	1.2 06/08/02
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

package com.sun.jmx.examples.scandir.config;

import junit.framework.*;
import java.io.File;

/**
 * Unit tests for {@code XmlConfigUtils}
 *
 * @author Sun Microsystems, 2006 - All rights reserved.
 */
public class XmlConfigUtilsTest extends TestCase {

    public XmlConfigUtilsTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(XmlConfigUtilsTest.class);

        return suite;
    }


    /**
     * Test of writeToFile method, of class XmlConfigUtils.
     */
    public void testWriteToFile() throws Exception {
        System.out.println("writeToFile");

        final File file = File.createTempFile("test",".xml");
        file.deleteOnExit();

        final String tmp = System.getProperty("java.io.tmpdir");

        DirectoryScannerConfig dir1 =
                new DirectoryScannerConfig("scan2");
        dir1.setRootDirectory(tmp);
        ScanManagerConfig bean = new ScanManagerConfig("session2");
        bean.putScan(dir1);
        XmlConfigUtils instance = new XmlConfigUtils(file.getPath());

        instance.writeToFile(bean);
    }

    /**
     * Test of readFromFile method, of class com.sun.jmx.examples.scandir.config.XmlConfigUtils.
     */
    public void testReadFromFile() throws Exception {
        System.out.println("readFromFile");

        final String tmp = System.getProperty("java.io.tmpdir");
        final File file = File.createTempFile("test",".xml");
        file.deleteOnExit();

        DirectoryScannerConfig dir1 =
                new DirectoryScannerConfig("scan1");
        dir1.setRootDirectory(tmp);
        ScanManagerConfig bean = new ScanManagerConfig("session1");
        bean.putScan(dir1);
        XmlConfigUtils instance = new XmlConfigUtils(file.getPath());

        instance.writeToFile(bean);

        ScanManagerConfig expResult = bean;
        ScanManagerConfig result = instance.readFromFile();
        System.out.println(result);
        assertEquals(expResult, result);


    }

}

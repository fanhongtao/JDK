/*
 * @(#)Version.java	1.12 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

package com.sun.corba.se.impl.util;
import java.util.Date;

public class Version {

    public static final String  PROJECT_NAME = "RMI-IIOP";
    public static final String  VERSION = "1.0";
    public static final String  BUILD = "0.0";
    public static final String  BUILD_TIME = "unknown";
    public static final String  FULL = PROJECT_NAME + " " + VERSION + " (" 
	+ BUILD_TIME + ")";
    
    public static String asString () {
        return FULL;
    }
    
    public static void main (String[] args) {
        System.out.println(FULL);
    }
}

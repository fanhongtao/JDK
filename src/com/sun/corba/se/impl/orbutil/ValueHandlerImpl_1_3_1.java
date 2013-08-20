/*
 * @(#)ValueHandlerImpl_1_3_1.java	1.6 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.impl.orbutil;

import org.omg.CORBA.TCKind;

/**
 * This class overrides behavior of our current ValueHandlerImpl to
 * provide backwards compatibility with JDK 1.3.1.
 */
public class ValueHandlerImpl_1_3_1 
    extends com.sun.corba.se.impl.io.ValueHandlerImpl 
{
    public ValueHandlerImpl_1_3_1() {}

    public ValueHandlerImpl_1_3_1(boolean isInputStream) {
	super(isInputStream);
    }

    /**
     * Our JDK 1.3 and JDK 1.3.1 behavior subclasses override this.
     * The correct behavior is for a Java char to map to a CORBA wchar,
     * but our older code mapped it to a CORBA char.
     */
    protected TCKind getJavaCharTCKind() {
        return TCKind.tk_char;
    }

    /**
     * RepositoryId_1_3_1 performs an incorrect repId calculation
     * when using serialPersistentFields and one of the fields no longer
     * exists on the class itself.
     */
    public boolean useFullValueDescription(Class clazz, String repositoryID) 
	throws java.io.IOException
    {        
        return RepositoryId_1_3_1.useFullValueDescription(clazz, repositoryID);
    }
    
    /**
     * Installs the legacy IIOPOutputStream_1_3_1 which does
     * PutFields/GetFields incorrectly.  Bug 4407244.
     */
    protected final String getOutputStreamClassName() {
        return "com.sun.corba.se.impl.orbutil.IIOPOutputStream_1_3_1";
    }

    /**
     * Installs the legacy IIOPInputStream_1_3_1 which does
     * PutFields/GetFields incorrectly.  Bug 4407244.
     */
    protected final String getInputStreamClassName() {
        return "com.sun.corba.se.impl.orbutil.IIOPInputStream_1_3_1";
    }
}

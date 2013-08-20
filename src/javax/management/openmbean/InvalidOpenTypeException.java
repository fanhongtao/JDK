/*
 * @(#)InvalidOpenTypeException.java	3.17 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package javax.management.openmbean;


// java import
//
import java.io.Serializable;


// jmx import
//


/**
 * This runtime exception is thrown to indicate that the <i>open type</i> of an <i>open data</i> value
 * is not the one expected.
 *
 * @version     3.17  03/12/19
 * @author      Sun Microsystems, Inc.
 *
 * @since 1.5
 * @since.unbundled JMX 1.1
 */
public class InvalidOpenTypeException 
    extends IllegalArgumentException
    implements Serializable {

    private static final long serialVersionUID = -2837312755412327534L;

    /** An InvalidOpenTypeException with no detail message.  */
    public InvalidOpenTypeException() {
	super();
    }

    /**
     * An InvalidOpenTypeException with a detail message.
     *
     * @param msg the detail message.
     */
    public InvalidOpenTypeException(String msg) {
	super(msg);
    }

}

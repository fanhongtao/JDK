/*
 * @(#)MissingResourceException.java	1.12 00/01/19
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

/*
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
 *
 * The original version of this source code and documentation
 * is copyrighted and owned by Taligent, Inc., a wholly-owned
 * subsidiary of IBM. These materials are provided under terms
 * of a License Agreement between Taligent and Sun. This technology
 * is protected by multiple US and International patents.
 *
 * This notice and attribution to Taligent may not be removed.
 * Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java.util;

/**
 * Signals that a resource is missing.
 * @see java.lang.Exception
 * @see ResourceBundle
 * @version     1.12, 01/19/00
 * @author      Mark Davis
 * @since       JDK1.1
 */
public
class MissingResourceException extends RuntimeException {

    /**
     * Constructs a MissingResourceException with the specified information.
     * A detail message is a String that describes this particular exception.
     * @param s the detail message
     * @param classname the name of the resource class
     * @param key the key for the missing resource.
     */
    public MissingResourceException(String s, String className, String key) {
        super(s);
        this.className = className;
        this.key = key;
    }

    /**
     * Gets parameter passed by constructor.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Gets parameter passed by constructor.
     */
    public String getKey() {
        return key;
    }

    //============ privates ============

    /**
     * The class name of the resource bundle requested by the user.
     * @serial
     */
    private String className;

    /**
     * The name of the specific resource requested by the user.
     * @serial
     */
    private String key;
}

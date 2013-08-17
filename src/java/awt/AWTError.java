/*
 * @(#)AWTError.java	1.12 00/02/02
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.awt;

/**
 * Thrown when a serious Abstract Window Toolkit error has occurred. 
 *
 * @version 	1.12 02/02/00
 * @author 	Arthur van Hoff
 */
public class AWTError extends Error {

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = -1819846354050686206L;

    /**
     * Constructs an instance of <code>AWTError</code> with the specified 
     * detail message. 
     * @param   msg   the detail message.
     * @since   JDK1.0
     */
    public AWTError(String msg) {
	super(msg);
    }
}

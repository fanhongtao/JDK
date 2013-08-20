// $Id: DatatypeConfigurationException.java,v 1.1 2004/03/06 00:22:23 jsuttor Exp $
/*
 * @(#)DatatypeConfigurationException.java	1.2 04/07/26
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.datatype;

/**
 * <p>Indicates a serious configuration error.</p>
 * 
 * TODO: support all constructors
 *
 * @author <a href="mailto:Jeff.Suttor@Sun.com">Jeff Suttor</a>
 * @version $Revision: 1.1 $, $Date: 2004/03/06 00:22:23 $
 * @since 1.5
 */

public class DatatypeConfigurationException extends Exception {

    /**
     * <p>Create a new <code>DatatypeConfigurationException</code> with
     * no specified detail mesage and cause.</p>
     */

    public DatatypeConfigurationException() {
        super();
    }

    /**
     * <p>Create a new <code>DatatypeConfigurationException</code> with
	 * the specified detail message.</p>
     *
	 * @param message The detail message.
     */
    
    public DatatypeConfigurationException(String message) {
        super(message);
    }

	/**
	 * <p>Create a new <code>DatatypeConfigurationException</code> with
	 * the specified detail message and cause.</p>
	 *
	 * @param message The detail message.
	 * @param cause The cause.  A <code>null</code> value is permitted, and indicates that the cause is nonexistent or unknown.
	 */
    
	public DatatypeConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * <p>Create a new <code>DatatypeConfigurationException</code> with
	 * the specified cause.</p>
	 *
	 * @param cause The cause.  A <code>null</code> value is permitted, and indicates that the cause is nonexistent or unknown.
	 */
    
	public DatatypeConfigurationException(Throwable cause) {
		super(cause);
	}
}

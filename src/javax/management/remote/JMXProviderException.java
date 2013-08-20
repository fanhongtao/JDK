/*
 * @(#)JMXProviderException.java	1.11 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package javax.management.remote;

import java.io.IOException;
import java.util.Map;

/**
 * <p>Exception thrown by {@link JMXConnectorFactory} when a provider
 * exists for the required protocol but cannot be used for some
 * reason.</p>
 *
 * @see JMXConnectorFactory#connect(JMXServiceURL, Map)
 * @since 1.5
 * @since.unbundled 1.0
 */
public class JMXProviderException extends IOException {

    private static final long serialVersionUID = -3166703627550447198L;

    /**
     * <p>Constructs a <code>JMXProviderException</code> with no
     * specified detail message.</p>
     */
    public JMXProviderException() {
    }

    /**
     * <p>Constructs a <code>JMXProviderException</code> with the
     * specified detail message.</p>
     *
     * @param message the detail message
     */
    public JMXProviderException(String message) {
	super(message);
    }

    /**
     * <p>Constructs a <code>JMXProviderException</code> with the
     * specified detail message and nested exception.</p>
     *
     * @param message the detail message
     * @param cause the nested exception
     */
    public JMXProviderException(String message, Throwable cause) {
	super(message);
	this.cause = cause;
    }

    public Throwable getCause() {
	return cause;
    }

    /**
     * @serial An exception that caused this exception to be thrown.
     *         This field may be null.
     * @see #getCause()
     **/
    private Throwable cause = null;
}

/*
 * @(#)JMXConnectorProvider.java	1.14 04/05/05
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package javax.management.remote;

import java.io.IOException;
import java.util.Map;

/**
 * <p>A provider for creating JMX API connector clients using a given
 * protocol.  Instances of this interface are created by {@link
 * JMXConnectorFactory} as part of its {@link
 * JMXConnectorFactory#newJMXConnector(JMXServiceURL, Map)
 * newJMXConnector} method.</p>
 *
 * @since 1.5
 * @since.unbundled 1.0
 */
public interface JMXConnectorProvider {
    /**
     * <p>Creates a new connector client that is ready to connect
     * to the connector server at the given address.  Each successful
     * call to this method produces a different
     * <code>JMXConnector</code> object.</p>
     *
     * @param serviceURL the address of the connector server to connect to.
     *
     * @param environment a read-only Map containing named attributes
     * to determine how the connection is made.  Keys in this map must
     * be Strings.  The appropriate type of each associated value
     * depends on the attribute.</p>
     *
     * @return a <code>JMXConnector</code> representing the new
     * connector client.  Each successful call to this method produces
     * a different object.
     *
     * @exception NullPointerException if <code>serviceURL</code> or
     * <code>environment</code> is null.
     *
     * @exception IOException if the connection cannot be made because
     * of a communication problem.
     */
    public JMXConnector newJMXConnector(JMXServiceURL serviceURL,
					Map<String,?> environment)
	    throws IOException;
}

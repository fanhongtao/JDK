/*
 * @(#)ClientProvider.java	1.14 04/02/13
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.remote.protocol.iiop;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.management.remote.JMXConnectorProvider;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnector;

public class ClientProvider implements JMXConnectorProvider {

    public JMXConnector newJMXConnector(JMXServiceURL serviceURL,
					Map<String,?> environment) 
	    throws IOException {
	if (!serviceURL.getProtocol().equals("iiop")) {
	    throw new MalformedURLException("Protocol not iiop: " +
					    serviceURL.getProtocol());
	}
	return new RMIConnector(serviceURL, environment);
    }
}

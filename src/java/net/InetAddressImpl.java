/*
 * @(#)InetAddressImpl.java	1.2 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

/*
 * Package private interface to "implementation" used by 
 * {@link InetAddress}.
 * <p> 
 * See {@link java.net.Inet4AddressImp} and 
 * {@link java.net.Inet6AddressImp}.
 *
 * @since 1.4
 */
interface InetAddressImpl {

    String getLocalHostName() throws UnknownHostException;
    byte[][]
        lookupAllHostAddr(String hostname) throws UnknownHostException;
    String getHostByAddr(byte[] addr) throws UnknownHostException;

    InetAddress anyLocalAddress();
    InetAddress loopbackAddress();
}


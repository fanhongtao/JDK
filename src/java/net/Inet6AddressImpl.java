/*
 * @(#)Inet6AddressImpl.java	1.2 03/01/23 
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.net;

/*
 * Package private implementation of InetAddressImpl for dual
 * IPv4/IPv6 stack.
 * <p>
 * If InetAddress.preferIPv6Address is true then anyLocalAddress(),
 * loopbackAddress(), and localHost() will return IPv6 addresses, 
 * otherwise IPv4 addresses.
 *
 * @since 1.4
 */

class Inet6AddressImpl implements InetAddressImpl {
    public native String getLocalHostName() throws UnknownHostException;
    public native byte[][]
        lookupAllHostAddr(String hostname) throws UnknownHostException;
    public native String getHostByAddr(byte[] addr) throws UnknownHostException;

    public synchronized InetAddress anyLocalAddress() {
        if (anyLocalAddress == null) {
            if (InetAddress.preferIPv6Address) {
                anyLocalAddress = new Inet6Address();
                anyLocalAddress.hostName = "::";
            } else {
                anyLocalAddress = (new Inet4AddressImpl()).anyLocalAddress();
            }
        }
        return anyLocalAddress;
    }

    public synchronized InetAddress loopbackAddress() {
        if (loopbackAddress == null) {
             if (InetAddress.preferIPv6Address) {
                 byte[] loopback = 
			{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01};
                 loopbackAddress = new Inet6Address("localhost", loopback);
             } else {
                loopbackAddress = (new Inet4AddressImpl()).loopbackAddress();
             }
        }
        return loopbackAddress;
    }

    private InetAddress      anyLocalAddress;
    private InetAddress      loopbackAddress;
}


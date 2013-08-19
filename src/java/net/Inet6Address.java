/*
 * @(#)Inet6Address.java	1.25 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

import java.security.AccessController;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.InvalidObjectException;
import sun.security.action.*;

/**
 * This class represents an Internet Protocol version 6 (IPv6) address.
 * Defined by <a href="http://www.ietf.org/rfc/rfc2373.txt">
 * <i>RFC&nbsp;2373: IP Version 6 Addressing Architecture</i></a>.
 *
 * <h4> <A NAME="format">Textual representation of IP addresses<a> </h4>
 *
 * Textual representation of IPv6 address used as input to methods
 * takes one of the following forms:
 * 
 * <ol>
 *   <li><p> <A NAME="lform">The preferred form<a> is x:x:x:x:x:x:x:x, where the 'x's are
 *   the hexadecimal values of the eight 16-bit pieces of the
 *   address. This is the full form.  For example,
 *
 *   <blockquote><table cellpadding=0 cellspacing=0 summary="layout">
 *   <tr><td><tt>1080:0:0:0:8:800:200C:417A</tt><td></tr>
 *   </table></blockquote>
 *
 *   <p> Note that it is not necessary to write the leading zeros in
 *   an individual field. However, there must be at least one numeral
 *   in every field, except as described below.</li>
 *
 *   <li><p> Due to some methods of allocating certain styles of IPv6
 *   addresses, it will be common for addresses to contain long
 *   strings of zero bits. In order to make writing addresses
 *   containing zero bits easier, a special syntax is available to
 *   compress the zeros. The use of "::" indicates multiple groups
 *   of 16-bits of zeros. The "::" can only appear once in an address.
 *   The "::" can also be used to compress the leading and/or trailing
 *   zeros in an address. For example,
 *
 *   <blockquote><table cellpadding=0 cellspacing=0 summary="layout">
 *   <tr><td><tt>1080::8:800:200C:417A</tt><td></tr>
 *   </table></blockquote>
 *
 *   <li><p> An alternative form that is sometimes more convenient
 *   when dealing with a mixed environment of IPv4 and IPv6 nodes is
 *   x:x:x:x:x:x:d.d.d.d, where the 'x's are the hexadecimal values
 *   of the six high-order 16-bit pieces of the address, and the 'd's
 *   are the decimal values of the four low-order 8-bit pieces of the
 *   standard IPv4 representation address, for example,
 *
 *   <blockquote><table cellpadding=0 cellspacing=0 summary="layout">
 *   <tr><td><tt>::FFFF:129.144.52.38</tt><td></tr>
 *   <tr><td><tt>::129.144.52.38</tt><td></tr>
 *   </table></blockquote>
 *
 *   <p> where "::FFFF:d.d.d.d" and "::d.d.d.d" are, respectively, the
 *   general forms of an IPv4-mapped IPv6 address and an
 *   IPv4-compatible IPv6 address. Note that the IPv4 portion must be
 *   in the "d.d.d.d" form. The following forms are invalid:
 *
 *   <blockquote><table cellpadding=0 cellspacing=0 summary="layout">
 *   <tr><td><tt>::FFFF:d.d.d</tt><td></tr>
 *   <tr><td><tt>::FFFF:d.d</tt><td></tr>
 *   <tr><td><tt>::d.d.d</tt><td></tr>
 *   <tr><td><tt>::d.d</tt><td></tr>
 *   </table></blockquote>
 *
 *   <p> The following form:
 *
 *   <blockquote><table cellpadding=0 cellspacing=0 summary="layout">
 *   <tr><td><tt>::FFFF:d</tt><td></tr>
 *   </table></blockquote>
 *
 *   <p> is valid, however it is an unconventional representation of
 *   the IPv4-compatible IPv6 address,
 *
 *   <blockquote><table cellpadding=0 cellspacing=0 summary="layout">
 *   <tr><td><tt>::255.255.0.d</tt><td></tr>
 *   </table></blockquote>
 *
 *   <p> while "::d" corresponds to the general IPv6 address
 *   "0:0:0:0:0:0:0:d".</li>
 * </ol>
 *
 * <p> For methods that return a textual representation as output
 * value, the full form is used. Inet6Address will return the full
 * form because it is unambiguous when used in combination with other
 * textual data.
 *
 * <h4> Special IPv6 address </h4>
 *
 * <blockquote>
 * <table cellspacing=2 summary="Description of IPv4-mapped address"> <tr><th valign=top><i>IPv4-mapped address</i></th>
 *         <td>Of the form::ffff:w.x.y.z, this IPv6 address is used to
 *         represent an IPv4 address. It allows the native program to
 *         use the same address data structure and also the same
 *         socket when communicating with both IPv4 and IPv6 nodes.
 *
 *         <p>In InetAddress and Inet6Address, it is used for internal
 *         representation; it has no functional role. Java will never
 *         return an IPv4-mapped address.  These classes can take an
 *         IPv4-mapped address as input, both in byte array and text
 *         representation. However, it will be converted into an IPv4
 *         address.</td></tr>
 * </table></blockquote>
 */
public final
class Inet6Address extends InetAddress {
    final static int INADDRSZ = 16;

    /* 
     * cached scope_id - for link-local address use only.
     */
    private transient int cached_scope_id = 0;

    /**
     * Holds a 128-bit (16 bytes) IPv6 address.
     *
     * @serial
     */
    byte[] ipaddress;

    private static final long serialVersionUID = 6880410070516793377L;

    /*
     * Perform initializations.
     */
    static {
        init();
    }

    Inet6Address() {
	super();
	hostName = null;
	ipaddress = new byte[INADDRSZ];
	family = IPv6;
    }

    Inet6Address(String hostName, byte addr[]) {
	this.hostName = hostName;
	if (addr.length == INADDRSZ) { // normal IPv6 address
	    family = IPv6;
	    ipaddress = (byte[])addr.clone();
	} 
    }

    private void readObject(ObjectInputStream s) 
	throws IOException, ClassNotFoundException {
	s.defaultReadObject();
	
	ipaddress = (byte[])ipaddress.clone();

	// Check that our invariants are satisfied
	if (ipaddress.length != INADDRSZ) {
	    throw new InvalidObjectException("invalid address length: "+
					     ipaddress.length);
	}
	
	if (family != IPv6) {
	    throw new InvalidObjectException("invalid address family type");
	}
    }
    
    /**
     * Utility routine to check if the InetAddress is an IP multicast
     * address. 11111111 at the start of the address identifies the
     * address as being a multicast address.
     *
     * @return a <code>boolean</code> indicating if the InetAddress is
     * an IP multicast address
     * @since JDK1.1
     */
    public boolean isMulticastAddress() {
	return ((ipaddress[0] & 0xff) == 0xff);
    }

    /**
     * Utility routine to check if the InetAddress in a wildcard address.
     * @return a <code>boolean</code> indicating if the Inetaddress is
     *         a wildcard address.
     * @since 1.4
     */        
    public boolean isAnyLocalAddress() {
	byte test = 0x00;
	for (int i = 0; i < INADDRSZ; i++) {
	    test |= ipaddress[i];
	}
	return (test == 0x00);
    }

    /**
     * Utility routine to check if the InetAddress is a loopback address. 
     *
     * @return a <code>boolean</code> indicating if the InetAddress is 
     * a loopback address; or false otherwise.
     * @since 1.4
     */
    public boolean isLoopbackAddress() {
	byte test = 0x00;
	for (int i = 0; i < 15; i++) {
	    test |= ipaddress[i];
	}
	return (test == 0x00) && (ipaddress[15] == 0x01);
    }

    /**
     * Utility routine to check if the InetAddress is an link local address. 
     *
     * @return a <code>boolean</code> indicating if the InetAddress is 
     * a link local address; or false if address is not a link local unicast address.
     * @since 1.4
     */
    public boolean isLinkLocalAddress() {
	return ((ipaddress[0] & 0xff) == 0xfe 
		&& (ipaddress[1] & 0xc0) == 0x80);
    }

    /**
     * Utility routine to check if the InetAddress is a site local address. 
     *
     * @return a <code>boolean</code> indicating if the InetAddress is 
     * a site local address; or false if address is not a site local unicast address.
     * @since 1.4
     */
    public boolean isSiteLocalAddress() {
	return ((ipaddress[0] & 0xff) == 0xfe 
		&& (ipaddress[1] & 0xc0) == 0xc0);
    }

    /**
     * Utility routine to check if the multicast address has global scope.
     *
     * @return a <code>boolean</code> indicating if the address has 
     *         is a multicast address of global scope, false if it is not 
     *         of global scope or it is not a multicast address
     * @since 1.4
     */
    public boolean isMCGlobal() {
	return ((ipaddress[0] & 0xff) == 0xff
		&& (ipaddress[1] & 0x0f) == 0x0e);
    }

    /**
     * Utility routine to check if the multicast address has node scope.
     *
     * @return a <code>boolean</code> indicating if the address has 
     *         is a multicast address of node-local scope, false if it is not 
     *         of node-local scope or it is not a multicast address
     * @since 1.4
     */
    public boolean isMCNodeLocal() {
	return ((ipaddress[0] & 0xff) == 0xff
		&& (ipaddress[1] & 0x0f) == 0x01);
    }

    /**
     * Utility routine to check if the multicast address has link scope.
     *
     * @return a <code>boolean</code> indicating if the address has 
     *         is a multicast address of link-local scope, false if it is not 
     *         of link-local scope or it is not a multicast address
     * @since 1.4
     */
    public boolean isMCLinkLocal() {
	return ((ipaddress[0] & 0xff) == 0xff
		&& (ipaddress[1] & 0x0f) == 0x02);
    }

    /**
     * Utility routine to check if the multicast address has site scope.
     *
     * @return a <code>boolean</code> indicating if the address has 
     *         is a multicast address of site-local scope, false if it is not 
     *         of site-local scope or it is not a multicast address
     * @since 1.4
     */
    public boolean isMCSiteLocal() {
	return ((ipaddress[0] & 0xff) == 0xff
		&& (ipaddress[1] & 0x0f) == 0x05);
    }

    /**
     * Utility routine to check if the multicast address has organization scope.
     *
     * @return a <code>boolean</code> indicating if the address has 
     *         is a multicast address of organization-local scope, 
     *         false if it is not of organization-local scope 
     *         or it is not a multicast address
     * @since 1.4
     */
    public boolean isMCOrgLocal() {
	return ((ipaddress[0] & 0xff) == 0xff
		&& (ipaddress[1] & 0x0f) == 0x08);
    }  

    /**
     * Returns the raw IP address of this <code>InetAddress</code>
     * object. The result is in network byte order: the highest order
     * byte of the address is in <code>getAddress()[0]</code>.
     *
     * @return  the raw IP address of this object.
     */
    public byte[] getAddress() {
	return (byte[])ipaddress.clone();
    }

    /**
     * Returns the IP address string in textual presentation.
     *
     * @return  the raw IP address in a string format.
     */
    public String getHostAddress() {
	return numericToTextFormat(ipaddress);
    }

    /**
     * Returns a hashcode for this IP address.
     *
     * @return  a hash code value for this IP address.
     */
    public int hashCode() {
	if (ipaddress != null) {

	    int hash = 0;
	    int i=0;
  	    while (i<INADDRSZ) {
		int j=0;
		int component=0;
		while (j<4 && i<INADDRSZ) {
		    component = (component << 8) + ipaddress[i];
		    j++; 
		    i++;
		}
		hash += component;
	    }
	    return hash;

	} else {
	    return 0;
	}
    }
    
    /**
     * Compares this object against the specified object.
     * The result is <code>true</code> if and only if the argument is
     * not <code>null</code> and it represents the same IP address as
     * this object.
     * <p>
     * Two instances of <code>InetAddress</code> represent the same IP
     * address if the length of the byte arrays returned by
     * <code>getAddress</code> is the same for both, and each of the
     * array components is the same for the byte arrays.
     *
     * @param   obj   the object to compare against.
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     * @see     java.net.InetAddress#getAddress()
     */
    public boolean equals(Object obj) {
	if (obj == null || 
	    !(obj instanceof Inet6Address))
	    return false;

	Inet6Address inetAddr = (Inet6Address)obj;

	for (int i = 0; i < INADDRSZ; i++) {
	    if (ipaddress[i] != inetAddr.ipaddress[i])
		return false;
	}
	
	return true;
    }

    /**
     * Utility routine to check if the InetAddress is an
     * IPv4 mapped IPv6 address. 
     *
     * @return a <code>boolean</code> indicating if the InetAddress is 
     * an IPv4 mapped IPv6 address; or false if address is IPv4 address.
     */
    static boolean isIPv4MappedAddress(byte[] addr) {
	if (addr.length < INADDRSZ) {
	    return false;
	}
	if ((addr[0] == 0x00) && (addr[1] == 0x00) && 
	    (addr[2] == 0x00) && (addr[3] == 0x00) && 
	    (addr[4] == 0x00) && (addr[5] == 0x00) && 
	    (addr[6] == 0x00) && (addr[7] == 0x00) && 
	    (addr[8] == 0x00) && (addr[9] == 0x00) && 
	    (addr[10] == (byte)0xff) && 
	    (addr[11] == (byte)0xff))  {   
	    return true;
	}
	return false;
    }

    static byte[] convertFromIPv4MappedAddress(byte[] addr) {
	if (isIPv4MappedAddress(addr)) {
	    byte[] newAddr = new byte[Inet4Address.INADDRSZ];
	    System.arraycopy(addr, 12, newAddr, 0, Inet4Address.INADDRSZ);
	    return newAddr;
	}
	return null;
    }

    /**
     * Utility routine to check if the InetAddress is an
     * IPv4 compatible IPv6 address. 
     *
     * @return a <code>boolean</code> indicating if the InetAddress is 
     * an IPv4 compatible IPv6 address; or false if address is IPv4 address.
     * @since 1.4
     */
    public boolean isIPv4CompatibleAddress() {
	if ((ipaddress[0] == 0x00) && (ipaddress[1] == 0x00) && 
	    (ipaddress[2] == 0x00) && (ipaddress[3] == 0x00) && 
	    (ipaddress[4] == 0x00) && (ipaddress[5] == 0x00) && 
	    (ipaddress[6] == 0x00) && (ipaddress[7] == 0x00) && 
	    (ipaddress[8] == 0x00) && (ipaddress[9] == 0x00) && 
	    (ipaddress[10] == 0x00) && (ipaddress[11] == 0x00))  {   
	    return true;
	}
	return false;
    }

    // Utilities
    private final static int INT16SZ = 2;
    /*
     * Convert IPv6 binary address into presentation (printable) format.
     *
     * @param src a byte array representing the IPv6 numeric address
     * @return a String representing an IPv6 address in 
     *         textual representation format
     * @since 1.4
     */
    static String numericToTextFormat(byte[] src)
    {
	StringBuffer sb = new StringBuffer(39);
	for (int i = 0; i < (INADDRSZ / INT16SZ); i++) {
	    sb.append(Integer.toHexString(((src[i<<1]<<8) & 0xff00)
					  | (src[(i<<1)+1] & 0xff)));
	    if (i < (INADDRSZ / INT16SZ) -1 ) {
	       sb.append(":");
	    }
	}
	return sb.toString();
    }

    /* 
     * Convert IPv6 presentation level address to network order binary form.
     * credit:
     *  Converted from C code from Solaris 8 (inet_pton)
     *
     * @param src a String representing an IPv6 address in textual format
     * @return a byte array representing the IPv6 numeric address
     * @since 1.4
     */
    static byte[] textToNumericFormat(String src)
    {
	if (src.length() == 0) {
	    return null;
	}

	int colonp;
	char ch;
	boolean saw_xdigit;
	int val;
	char[] srcb = src.toCharArray();
	byte[] dst = new byte[INADDRSZ];

	colonp = -1;
	int i = 0, j = 0;
	/* Leading :: requires some special handling. */
	if (srcb[i] == ':')
	    if (srcb[++i] != ':')
		return null;
	int curtok = i;
	saw_xdigit = false;
	val = 0;
	while (i < srcb.length) {
	    ch = srcb[i++];
	    int chval = Character.digit(ch, 16);
	    if (chval != -1) {
		val <<= 4;
		val |= chval;
		if (val > 0xffff)
		    return null;
		saw_xdigit = true;
		continue;
	    }
	    if (ch == ':') {
		curtok = i;
		if (!saw_xdigit) {
		    if (colonp != -1)
			return null;
		    colonp = j;
		    continue;
		} else if (i == srcb.length) {
		    return null;
		}
		if (j + INT16SZ > INADDRSZ)
		    return null;
		dst[j++] = (byte) ((val >> 8) & 0xff);
		dst[j++] = (byte) (val & 0xff);
		saw_xdigit = false;
		val = 0;
		continue;
	    }
	    if (ch == '.' && ((j + Inet4Address.INADDRSZ) <= INADDRSZ)) {
		byte[] v4addr = Inet4Address.textToNumericFormat(src.substring(curtok));
		if (v4addr == null) {
		    return null;
		}
		for (int k = 0; k < Inet4Address.INADDRSZ; k++) {
		    dst[j++] = v4addr[k];
		}
		saw_xdigit = false;
		break;	/* '\0' was seen by inet_pton4(). */
	    }
	    return null;
	}
	if (saw_xdigit) {
	    if (j + INT16SZ > INADDRSZ)
		return null;
	    dst[j++] = (byte) ((val >> 8) & 0xff);
	    dst[j++] = (byte) (val & 0xff);
	}

	if (colonp != -1) {
	    int n = j - colonp;
	    
	    if (j == INADDRSZ)
		return null;
	    for (i = 1; i <= n; i++) {
		dst[INADDRSZ - i] = dst[colonp + n - i];
		dst[colonp + n - i] = 0;
	    }
	    j = INADDRSZ;
	}
	if (j != INADDRSZ)
	    return null;
	byte[] newdst = convertFromIPv4MappedAddress(dst);
	if (newdst != null) {
	    return newdst;
	} else {
	    return dst;
	}
    }

    /**
     * Perform class load-time initializations.
     */
    private static native void init();

}


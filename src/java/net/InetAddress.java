/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

import java.util.HashMap;
import java.util.Random;
import java.security.AccessController;
import sun.security.action.*;
import sun.net.InetAddressCachePolicy;

/**
 * This class represents an Internet Protocol (IP) address.
 * <p>
 * Applications should use the methods <code>getLocalHost</code>,
 * <code>getByName</code>, or <code>getAllByName</code> to
 * create a new <code>InetAddress</code> instance.
 *
 * @author  Chris Warth
 * @version 1.71, 02/06/02
 * @see     java.net.InetAddress#getAllByName(java.lang.String)
 * @see     java.net.InetAddress#getByName(java.lang.String)
 * @see     java.net.InetAddress#getLocalHost()
 * @since   JDK1.0
 */
public final
class InetAddress implements java.io.Serializable {

    /**
     * @serial
     */
    String hostName;

    /*
     * Currently we only deal effectively with 32-bit addresses.
     * However this field can be expanded to be a byte array
     * or a 64-bit quantity without too much effort.
     *
     * @serial
     */
    int address;

    /**
     * @serial
     */
    int family;

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 3286316764910316507L;

    /*
     * Load net library into runtime, and perform initializations.
     */
    static {
	AccessController.doPrivileged(new LoadLibraryAction("net"));
        init();
    }

    /**
     * Constructor for the Socket.accept() method.
     * This creates an empty InetAddress, which is filled in by
     * the accept() method.  This InetAddress, however, is not
     * put in the address cache, since it is not created by name.
     */
    InetAddress() {
      family = impl.getInetFamily();
    }

    /**
     * Creates an InetAddress with the specified host name and IP address.
     * @param hostName the specified host name
     * @param addr the specified IP address.  The address is expected in
     *	      network byte order.
     * @exception UnknownHostException If the address is unknown.
     */
    InetAddress(String hostName, byte addr[]) {
	this.hostName = hostName;
	this.family = impl.getInetFamily();
	/*
	 * We must be careful here to maintain the network byte
	 * order of the address.  As it comes in, the most
	 * significant byte of the address is in addr[0].  It
	 * actually doesn't matter what order they end up in the
	 * array, as long as it is documented and consistent.
	 */
	address  = addr[3] & 0xFF;
	address |= ((addr[2] << 8) & 0xFF00);
	address |= ((addr[1] << 16) & 0xFF0000);
	address |= ((addr[0] << 24) & 0xFF000000);
    }

    /**
     * Utility routine to check if the InetAddress is an
     * IP multicast address. IP multicast address is a Class D
     * address i.e first four bits of the address are 1110.
     * @return a <code>boolean</code> indicating if the InetAddress is 
     * an IP multicast address
     * @since   JDK1.1
     */
    public boolean isMulticastAddress() {
	return ((address & 0xf0000000) == 0xe0000000);
    }

    /**
     * Gets the host name for this IP address.
     *
     * <p>If there is a security manager, its
     * <code>checkConnect</code> method is first called
     * with the hostname and <code>-1</code> 
     * as its arguments to see if the operation is allowed.
     *
     * @return the host name for this IP address.
     * 
     * @exception  SecurityException  if a security manager exists and its  
     *  <code>checkConnect</code> method doesn't allow the operation .
     * 
     * @see SecurityManager#checkConnect
     */
    public String getHostName() {
	return getHostName(true);
    }

    /**
     * Returns the hostname for this address.
     * If the host is equal to null, then this address refers to any
     * of the local machine's available network addresses.
     * this is package private so SocketPermission can make calls into
     * here without a security check.
     *
     * <p>If there is a security manager, this method first
     * calls its <code>checkConnect</code> method
     * with the hostname and <code>-1</code> 
     * as its arguments to see if the calling code is allowed to know
     * the hostname for this IP address, i.e., to connect to the host.
     * 
     * @return  the host name for this IP address.
     * 
     * @param check make security check if true
     * 
     * @exception  SecurityException  if a security manager exists and its  
     *  <code>checkConnect</code> method doesn't allow the connection.
     * 
     * @see SecurityManager#checkConnect
     */
    String getHostName(boolean check) {
	if (hostName == null) {
	    try {
		// first lookup the hostname
		hostName = impl.getHostByAddr(address);

		/* check to see if calling code is allowed to know
		 * the hostname for this IP address, ie, connect to the host
		 */
		if (check) {
		    SecurityManager sec = System.getSecurityManager();
		    if (sec != null) {
			sec.checkConnect(hostName, -1);
		    }
		}

		/* now get all the IP addresses for this hostname,
		 * and make sure one of them matches the original IP
		 * address. We do this to try and prevent spoofing.
		 */

		InetAddress[] arr = getAllByName0(hostName, check);
		boolean ok = false;

		if(arr != null) {
		    for(int i = 0; !ok && i < arr.length; i++) {
			//System.out.println("check "+this+" "+arr[i]);
			ok = (address == arr[i].address);
		    }
		}

		//XXX: if it looks a spoof just return the address?
		if (!ok) {
		    hostName = getHostAddress();
		    return getHostAddress();
		}

	    } catch (SecurityException e) {
		hostName = getHostAddress();
	    } catch (UnknownHostException e) {
		hostName = getHostAddress();
	    }
	}
	return hostName;
    }

    /**
     * Returns the raw IP address of this <code>InetAddress</code>
     * object. The result is in network byte order: the highest order
     * byte of the address is in <code>getAddress()[0]</code>.
     *
     * @return  the raw IP address of this object.
     */
    public byte[] getAddress() {
	byte[] addr = new byte[4];

	addr[0] = (byte) ((address >>> 24) & 0xFF);
	addr[1] = (byte) ((address >>> 16) & 0xFF);
	addr[2] = (byte) ((address >>> 8) & 0xFF);
	addr[3] = (byte) (address & 0xFF);
	return addr;
    }

    /**
     * Returns the IP address string "%d.%d.%d.%d".
     *
     * @return  the raw IP address in a string format.
     * @since   JDK1.0.2
     */
    public String getHostAddress() {
         return ((address >>> 24) & 0xFF) + "." +
                ((address >>> 16) & 0xFF) + "." +
                ((address >>>  8) & 0xFF) + "." +
                ((address >>>  0) & 0xFF);
     }


    /**
     * Returns a hashcode for this IP address.
     *
     * @return  a hash code value for this IP address.
     */
    public int hashCode() {
	return address;
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
	return (obj != null) && (obj instanceof InetAddress) &&
	    (((InetAddress)obj).address == address);
    }

    /**
     * Converts this IP address to a <code>String</code>.
     *
     * @return  a string representation of this IP address.
     */
    public String toString() {
	return getHostName() + "/" + getHostAddress();
    }

    /*
     * Cached addresses - our own litle nis, not!
     */
    private static HashMap	    addressCache = new HashMap();
    private static InetAddress	    unknownAddress;
    private static InetAddress      localHost;
    private static InetAddress[]    unknown_array; // put THIS in cache
    static InetAddress	    anyLocalAddress;
    static InetAddressImpl  impl;

    private static HashMap          lookupTable = new HashMap();

    static final class CacheEntry {

	CacheEntry(String hostname, Object address, long expiration) {
	    this.hostname = hostname;
	    this.address = address;
	    this.expiration = expiration;
	}

	String hostname;
	Object address;
	long expiration;
    }

    private static void cacheAddress(String hostname, Object address) {
	// if the cache policy is to cache nothing, just return
	int policy = InetAddressCachePolicy.get();
	if (policy == 0) {
	    return;
	}
	long expiration = -1;
	if (policy != InetAddressCachePolicy.FOREVER) {
	    expiration = System.currentTimeMillis() + (policy * 1000);
	}
	cacheAddress(hostname, address, expiration);
    }

    private static void cacheAddress(String hostname, Object address, long expiration) {
        hostname = hostname.toLowerCase();
	synchronized (addressCache) {
	    CacheEntry entry = (CacheEntry)addressCache.get(hostname);
	    if (entry == null) {
		entry = new CacheEntry(hostname, address, expiration);
		addressCache.put(hostname, entry);
	    } else {
		entry.address = address;
		entry.expiration = expiration;
	    }
	}
    }

    private static Object getCachedAddress(String hostname) {
        hostname = hostname.toLowerCase();
	if (InetAddressCachePolicy.get() == 0) {
	    return null;
	}
	synchronized (addressCache) {
	    CacheEntry entry = (CacheEntry)addressCache.get(hostname);
	    if (entry != null && entry.expiration < System.currentTimeMillis() &&
		entry.expiration >= 0) {
		entry = null;
	    }
	    return entry != null ? entry.address : null;
	}
    }

    /*
     * generic localHost to give back to applets
     * - private so not API delta
     */
    private static InetAddress      loopbackHost;

    static {
	/*
	 * Property "impl.prefix" will be prepended to the classname
	 * of the implementation object we instantiate, to which we
	 * delegate the real work (like native methods).  This
	 * property can vary across implementations of the java.
	 * classes.  The default is an empty String "".
	 */
	String prefix = (String)AccessController.doPrivileged(
		      new GetPropertyAction("impl.prefix", ""));

	try {
	    impl = null;
	    impl = (InetAddressImpl)(Class.forName("java.net." + prefix +
						   "InetAddressImpl")
				     .newInstance());
	} catch (ClassNotFoundException e) {
	    System.err.println("Class not found: java.net." + prefix +
			       "InetAddressImpl:\ncheck impl.prefix property " +
			       "in your properties file.");
	} catch (InstantiationException e) {
	    System.err.println("Could not instantiate: java.net." + prefix +
			       "InetAddressImpl:\ncheck impl.prefix property " +
			       "in your properties file.");
	} catch (IllegalAccessException e) {
	    System.err.println("Cannot access class: java.net." + prefix +
			       "InetAddressImpl:\ncheck impl.prefix property " +
			       "in your properties file.");
	}

	if (impl == null) {
	    try {
		impl = (InetAddressImpl)(Class.forName("java.net.InetAddressImpl")
					 .newInstance());
	    } catch (Exception e) {
		throw new Error("System property impl.prefix incorrect");
	    }
	}

	unknownAddress = new InetAddress();
	anyLocalAddress = new InetAddress();
	impl.makeAnyLocalAddress(anyLocalAddress);
	byte[] IP = new byte[4];
	IP[0] = 0x7F;
	IP[1] = 0x00;
	IP[2] = 0x00;
	IP[3] = 0x01;
	loopbackHost = new InetAddress("localhost", IP);

	/* find the local host name */
	try {
	    localHost = new InetAddress();
	    localHost.hostName = impl.getLocalHostName();
	    /* we explicitly leave the address of the local host
	     * uninitialized.  A DNS lookup in this, the static
	     * initializer, will cause a machine disconnected
	     * from the network to hang - it'll be trying to query
	     * a DNS server that isn't there.
	     *
	     * Instead, we just get the hostname of the local host.
	     * The native code for this just calls gethostname()
	     * which should be pretty innocuous - it shouldn't try
	     * to contact a DNS server.  If any application
	     * calls InetAddress.getLocalHost(), we initialize
	     * the local host's address there if not already initialized.
	     *
	     * Note that for this to work it is also essential that
	     * the localHost InetAddress is _NOT_ put into the address cache
	     * here in the static initializer (which happens if we call
	     * getByName() from the static initializer).  It _IS_ OK
	     * to put it in the addressCache after initialization.
	     *
	     * The unitialized state of the localHost's address is -1,
	     * or IP address 255.255.255.255 which we know cannot be
	     * a legal host address.
	     */
	    localHost.address = -1;
	} catch (Exception ex) { /* this shouldn't happen */
	    localHost = unknownAddress;
	}

	/* cache the name/address pair "0.0.0.0"/0.0.0.0 */
	String unknownByAddr = "0.0.0.0";
	unknown_array = new InetAddress[1];
	unknown_array[0] = new InetAddress(unknownByAddr,
					   unknownAddress.getAddress());
	cacheAddress(unknownByAddr, unknown_array, InetAddressCachePolicy.FOREVER);
    }

    /**
     * Determines the IP address of a host, given the host's name. The
     * host name can either be a machine name, such as
     * "<code>java.sun.com</code>", or a string representing its IP
     * address, such as "<code>206.26.48.100</code>".
     *
     * @param      host   the specified host, or <code>null</code> for the
     *                    local host.
     * @return     an IP address for the given host name.
     * @exception  UnknownHostException  if no IP address for the
     *               <code>host</code> could be found.
     */
    public static InetAddress getByName(String host)
	throws UnknownHostException {
	Object obj = null;
	if (host == null || host.length() == 0) {
	    return loopbackHost;
	}
        if (!Character.isDigit(host.charAt(0))) {
	    return getAllByName0(host)[0];
	} else {
	    /* The string (probably) represents a numerical IP address.
	     * Parse it into an int, don't do uneeded reverese lookup,
	     * leave hostName null, don't cache.  If it isn't an IP address,
	     * (i.e., not "%d.%d.%d.%d") or if any element > 0xFF,
	     * we treat it as a hostname, and lookup that way.
	     * This seems to be 100% compliant to the RFC1123 spec:
	     * a partial hostname like 3com.domain4 is technically valid.
	     */

	    int IP = 0x00;
	    int hitDots = 0;
	    char[] data = host.toCharArray();

	    for(int i = 0; i < data.length; i++) {
		char c = data[i];
		if (c < 48 || c > 57) { // !digit
		    return getAllByName0(host)[0];
		}
		int b = 0x00;
		while(c != '.') {
		    if (c < 48 || c > 57) { // !digit
			return getAllByName0(host)[0];
		    }
		    b = b*10 + c - '0';

		    if (++i >= data.length)
			break;
		    c = data[i];
		}
		if(b > 0xFF) { /* bogus - bigger than a byte */
		    return getAllByName0(host)[0];
		}
		IP = (IP << 8) + b;
		hitDots++;
	    }

	    if(hitDots != 4 || host.endsWith(".")) {
		return getAllByName0(host)[0];
	    }

	    InetAddress in = new InetAddress();
	    in.address = IP;
	    in.hostName = null;
	    return in;
	}

    }

    /**
     * Determines all the IP addresses of a host, given the host's name.
     * The host name can either be a machine name, such as
     * "<code>java.sun.com</code>", or a string representing
     * its IP address, such as "<code>206.26.48.100</code>".
     *
     * <p>If there is a security manager and <code>host</code> is not 
     * null and <code>host.length() </code> is not equal to zero, the
     * security manager's
     * <code>checkConnect</code> method is called
     * with the hostname and <code>-1</code> 
     * as its arguments to see if the operation is allowed.
     *
     * @param      host   the name of the host.
     * @return     an array of all the IP addresses for a given host name.
     * 
     * @exception  UnknownHostException  if no IP address for the
     *               <code>host</code> could be found.
     * @exception  SecurityException  if a security manager exists and its  
     *               <code>checkConnect</code> method doesn't allow the operation.
     * 
     * @see SecurityManager#checkConnect
     */
    public static InetAddress[] getAllByName(String host)
	throws UnknownHostException {

	if (host == null || host.length() == 0) {
	    throw new UnknownHostException("empty string");
	}
	if(Character.isDigit(host.charAt(0))) {
	    InetAddress[] ret = new InetAddress[1];
	    ret[0] = getByName(host);
	    return ret;
	} else {
	    return getAllByName0(host);
	}
    }

    private static InetAddress[] getAllByName0 (String host)
	throws UnknownHostException
    {
	return getAllByName0(host, true);
    }

    /**
     * package private so SocketPermission can call it
     */
    static InetAddress[] getAllByName0 (String host, boolean check)
	throws UnknownHostException  {
	/* If it gets here it is presumed to be a hostname */
	/* Cache.get can return: null, unknownAddress, or InetAddress[] */
        Object obj = null;
	Object objcopy = null;

	/* make sure the connection to the host is allowed, before we
	 * give out a hostname
	 */
	if (check) {
	    SecurityManager security = System.getSecurityManager();
	    if (security != null) {
		security.checkConnect(host, -1);
	    }
	}

	obj = getCachedAddress(host);

	/* If no entry in cache, then do the host lookup */

	if (obj == null) {
	    obj = getAddressFromNameService(host);
	}
        if (obj == unknown_array)
            throw new UnknownHostException(host);

	/* Make a copy of the InetAddress array */
	try {
	    objcopy = ((InetAddress [])obj).clone();
	    // the following line is a hack, to ensure that the code
	    // can compile for both the broken compiler and the fixed one.
	    if (objcopy == null)
		throw new CloneNotSupportedException();
	} catch (CloneNotSupportedException cnse) {
	    cnse.printStackTrace();
	}

	return (InetAddress [])objcopy;
    }

    private static Object getAddressFromNameService(String host) {
	Object obj = null;

	// Check whether the host is in the lookupTable.
	// 1) If the host isn't in the lookupTable when
	//    checkLookupTable() is called, checkLookupTable()
	//    would add the host in the lookupTable and
	//    return null. So we will do the lookup.
	// 2) If the host is in the lookupTable when
	//    checkLookupTable() is called, the current thread
	//    would be blocked until the host is removed
	//    from the lookupTable. Then this thread
	//    should try to look up the addressCache.
	//     i) if it found the address in the
	//        addressCache, checkLookupTable()  would
	//        return the address.
	//     ii) if it didn't find the address in the
	//         addressCache for any reason,
	//         it should add the host in the
	//         lookupTable and return null so the
	//         following code would do  a lookup itself.
	if ((obj = checkLookupTable(host)) == null) {
	    // This is the first thread which looks up the address 
	    // this host or the cache entry for this host has been
	    // expired so this thread should do the lookup.
	    try {
		/*
		 * Do not put the call to lookup() inside the
		 * constructor.  if you do you will still be
		 * allocating space when the lookup fails.
		 */
		byte[][] byte_array;
		byte_array = impl.lookupAllHostAddr(host);
		InetAddress[] addr_array =
		    new InetAddress[byte_array.length];

		for (int i = 0; i < byte_array.length; i++) {
		    byte addr[] = byte_array[i];
		    addr_array[i] = new InetAddress(host, addr);
		}
		obj = addr_array;
	    } catch (UnknownHostException e) {
		obj  = unknown_array;
	    } finally {
		// Cache the address.
		cacheAddress(host, obj);
		// Delete the host from the lookupTable, and
		// notify all threads waiting for the monitor
		// for lookupTable.
		updateLookupTable(host);
	    }
	}

	return obj;
    }
	
		
    private static Object checkLookupTable(String host) {
	// make sure obj  is null.
	Object obj = null;
	
	synchronized (lookupTable) {
	    // If the host isn't in the lookupTable, add it in the
	    // lookuptable and return null. The caller should do
	    // the lookup.
	    if (lookupTable.containsKey(host) == false) {
		lookupTable.put(host, null);
		return obj;
	    }

	    // If the host is in the lookupTable, it means that another
	    // thread is trying to look up the address of this host.
	    // This thread should wait.
	    while (lookupTable.containsKey(host)) {
		try {
		    lookupTable.wait();
		} catch (InterruptedException e) {
		}
	    }
	}

	// The other thread has finished looking up the address of
	// the host. This thread should retry to get the address
	// from the addressCache. If it doesn't get the address from
	// the cache,  it will try to look up the address itself.
	obj = getCachedAddress(host);
	if (obj == null) {
	    synchronized (lookupTable) {
		lookupTable.put(host, null);
	    }
	}
	 
	return obj;
    }

    private static void updateLookupTable(String host) {
	synchronized (lookupTable) {
	    lookupTable.remove(host);
	    lookupTable.notifyAll();
	}
    }

    /**
     * Returns the local host.
     *
     * <p>If there is a security manager, its
     * <code>checkConnect</code> method is called
     * with the local host name and <code>-1</code> 
     * as its arguments to see if the operation is allowed.
     *
     * @return     the IP address of the local host.
     * 
     * @exception  UnknownHostException  if no IP address for the
     *               <code>host</code> could be found.
     * @exception  SecurityException  if a security manager exists and its  
     *               <code>checkConnect</code> method doesn't allow the operation.
     * 
     * @see SecurityManager#checkConnect
     */
    public synchronized static InetAddress getLocalHost() throws UnknownHostException {
        if (localHost.equals(unknownAddress)) {
	    throw new UnknownHostException();
	}

        /* make sure the connection to the host is allowed: if yes,
	 * return the "real" localHost; if not, return loopback "127.0.0.1"
	 */
	SecurityManager security = System.getSecurityManager();
	try {

	    /* If the localhost's address is not initialized yet, initialize
	     * it.  It is no longer initialized in the static initializer
	     * (see comment there).
	     */

	    if (localHost.address == -1) {
		localHost = getAllByName(localHost.hostName)[0];
		/* This puts it in the address cache as well */
	    }

	    if (security != null)
		security.checkConnect(localHost.getHostName(), -1);
	} catch (java.lang.SecurityException e) {
	    return loopbackHost;
	}
	return localHost;
    }

    /**
     * Perform class load-time initializations.
     */
    private static native void init();
}

class InetAddressImpl {
    native String getLocalHostName() throws UnknownHostException;
    native void makeAnyLocalAddress(InetAddress addr);
    native byte[][]
        lookupAllHostAddr(String hostname) throws UnknownHostException;
    native String getHostByAddr(int addr) throws UnknownHostException;
    native int getInetFamily();
}

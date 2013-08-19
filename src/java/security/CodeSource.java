/*
 * @(#)CodeSource.java	1.32 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package java.security;


import java.net.URL;
import java.net.SocketPermission;
import java.util.Hashtable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.*;

/**
 *
 * <p>This class extends the concept of a codebase to
 * encapsulate not only the location (URL) but also the certificate(s)
 * that were used to verify signed code originating from that
 * location.
 *
 * @version 	1.32, 01/23/03
 * @author Li Gong
 * @author Roland Schemers
 */

public class CodeSource implements java.io.Serializable {

    /**
     * The code location.
     *
     * @serial
     */
    private URL location;

    // certificates
    private transient java.security.cert.Certificate certs[];

    // cached SocketPermission used for matchLocation
    private transient SocketPermission sp;

    /**
     * Constructs a CodeSource and associates it with the specified 
     * location and set of certificates.
     * 
     * @param url the location (URL).
     * 
     * @param certs the certificate(s).
     */
    public CodeSource(URL url, java.security.cert.Certificate certs[]) {
	this.location = url;
	if (certs != null) 
	    this.certs = (java.security.cert.Certificate[]) certs.clone();
    }

    /**
     * Returns the hash code value for this object.
     *
     * @return a hash code value for this object.
     */

    public int hashCode() {
	if (location != null)
	    return location.hashCode();
	else 
	    return 0;
    }

    /**
     * Tests for equality between the specified object and this
     * object. Two CodeSource objects are considered equal if their 
     * locations are of identical value and if the two sets of 
     * certificates are of identical values. It is not required that
     * the certificates be in the same order.
     * 
     * @param obj the object to test for equality with this object.
     * 
     * @return true if the objects are considered equal, false otherwise.
     */
    public boolean equals(Object obj) {
	if (obj == this) 
	    return true;

	// objects types must be equal
	if (!(obj instanceof CodeSource))
	    return false;

	CodeSource cs = (CodeSource) obj;

	// URLs must match
	if (location == null) {
	    // if location is null, then cs.location must be null as well
	    if (cs.location != null) return false;
	} else {
	    // if location is not null, then it must equal cs.location
	    if (!location.equals(cs.location)) return false;
	}

	// certs must match
	if (certs == null) {
	    // if certs is null, then cs.certs must be null as well
	    if (cs.certs != null) return false;
	} else {
	    // if certs is not null, then it must equal cs.certs
	    // equality means that both arrays of certs are the same set
	    // step 1 -- every cert in certs[] must match one in cs.certs[]
	    if (cs.certs == null) 
		return false;

	    boolean match;
	    for (int i = 0; i < certs.length; i++) {
		match = false;
		for (int j = 0; j < cs.certs.length; j++) {
		    if (certs[i].equals(cs.certs[j])) {
			match = true;
			break;
		    }
		}
		if (!match) return false;
	    }
	    // step 2 -- every key in cs.certs[] must match one in certs[]
	    for (int i = 0; i < cs.certs.length; i++) {
		match = false;
		for (int j = 0; j < certs.length; j++) {
		    if (cs.certs[i].equals(certs[j])) {
			match = true;
			break;
		    }
		}
		if (!match) return false;
	    }
	}
       
	// they must be equal if we got here...
	return true;
    }

    /**
     * Returns the location associated with this CodeSource.
     * 
     * @return the location (URL).
     */
    public final URL getLocation() {
	/* since URL is practically immutable, returning itself is not
           a security problem */
	return this.location;
    }

    /**
     * Returns the certificates associated with this CodeSource.
     * 
     * @return the certificates
     */
    public final java.security.cert.Certificate[] getCertificates() {
	/* return a clone copy, to avoid malicious modification to the
	   original object */
	if (this.certs != null) {
	    return (java.security.cert.Certificate[])this.certs.clone();
	} else {
	    return null;
	}
    }

    /**
     * Returns true if this CodeSource object "implies" the specified CodeSource.
     * <P>
     * More specifically, this method makes the following checks, in order. 
     * If any fail, it returns false. If they all succeed, it returns true.<p>
     * <ol>
     * <li> <i>codesource</i> must not be null.
     * <li> If this object's certificates are not null, then all
     * of this object's certificates must be present in <i>codesource</i>'s 
     * certificates.
     * <li> If this object's location (getLocation()) is not null, then the 
     * following checks are made against this object's location and 
     * <i>codesource</i>'s:<p>
     *   <ol>
     *     <li>  <i>codesource</i>'s location must not be null.
     *
     *     <li>  If this object's location 
     *           equals <i>codesource</i>'s location, then return true.
     *
     *     <li>  This object's protocol (getLocation().getProtocol()) must be
     *           equal to <i>codesource</i>'s protocol.
     *
     *     <li>  If this object's host (getLocation().getHost()) is not null,  
     *           then the SocketPermission
     *           constructed with this object's host must imply the
     *           SocketPermission constructed with <i>codesource</i>'s host.
     *
     *     <li>  If this object's port (getLocation().getPort()) is not 
     *           equal to -1 (that is, if a port is specified), it must equal 
     *           <i>codesource</i>'s port.
     *
     *     <li>  If this object's file (getLocation().getFile()) doesn't equal
     *           <i>codesource</i>'s file, then the following checks are made:
     *           If this object's file ends with "/-",
     *           then <i>codesource</i>'s file must start with this object's
     *           file (exclusive the trailing "-").
     *           If this object's file ends with a "/*",
     *           then <i>codesource</i>'s file must start with this object's
     *           file and must not have any further "/" separators.
     *           If this object's file doesn't end with a "/", 
     *           then <i>codesource</i>'s file must match this object's 
     *           file with a '/' appended.
     *
     *     <li>  If this object's reference (getLocation().getRef()) is 
     *           not null, it must equal <i>codesource</i>'s reference.
     *
     *   </ol>
     * </ol>
     * <p>
     * For example, the codesource objects with the following locations
     * and null certificates all imply
     * the codesource with the location "http://java.sun.com/classes/foo.jar"
     * and null certificates:
     * <pre>
     *     http:
     *     http://*.sun.com/classes/*
     *     http://java.sun.com/classes/-
     *     http://java.sun.com/classes/foo.jar
     * </pre>
     * 
     * Note that if this CodeSource has a null location and a null
     * certificate chain, then it implies every other CodeSource.
     *
     * @param codesource CodeSource to compare against.
     *
     * @return true if the specified codesource is implied by this codesource,
     * false if not.  
     */
 
    public boolean implies(CodeSource codesource)
    {
	if (codesource == null)
	    return false;

	return matchCerts(codesource) && matchLocation(codesource);
    }

   
    /**
     * Returns true if all the certs in this
     * CodeSource are also in <i>that</i>.
     * 
     * @param that the CodeSource to check against.
     */
    private boolean matchCerts(CodeSource that)
    {
	// match any key
	if (this.certs == null) 
	    return true;

	// if certs are null, and this.certs is not null, return false
	if (that.certs == null)
	    return false;

	boolean match;
	for (int i=0; i < this.certs.length; i++) {
	    match = false;
	    for (int j=0; j < that.certs.length; j++) {
		if (this.certs[i].equals(that.certs[j])) {
		    match = true;
		    break;
		}
	    }
	    if (!match) return false;
	}
	return true;
    }

    /**
     * Returns true if two CodeSource's have the "same" location.
     * 
     * @param that CodeSource to compare against
     */
    private boolean matchLocation(CodeSource that)
	{
	    if (location == null) {
		return true;
	    }

	    if ((that == null) || (that.location == null))
		return false;

	    if (location.equals(that.location))
		return true;

	    if (!location.getProtocol().equals(that.location.getProtocol()))
		return false;

	    if ((location.getHost() != null)) {
		if ((location.getHost().equals("") || 
		    location.getHost().equals("localhost")) &&
		    (that.location.getHost().equals("") || 
		     that.location.getHost().equals("localhost"))) {
		    // ok
		} else if (!location.getHost().equals(
					     that.location.getHost())) {
		    if (this.sp == null) {
			this.sp = 
			    new SocketPermission(location.getHost(),"resolve");
		    }
		    if (that.sp == null) {
			if (that.location.getHost() == null ||
				that.location.getHost().equals(""))
			    return false;
			that.sp = 
		       new SocketPermission(that.location.getHost(),"resolve");
		    }

		    boolean ok = this.sp.implies(that.sp);
		    if (!ok)
			return false;
		}
	    }

	    if (location.getPort() != -1) {
		if (location.getPort() != that.location.getPort())
		    return false;
	    }

	    if (location.getFile().endsWith("/-")) {
		// Matches the directory and (recursively) all files
		// and subdirectories contained in that directory.
		// For example, "/a/b/-" implies anything that starts with
		// "/a/b/"
		String thisPath = location.getFile().substring(0,
                                                location.getFile().length()-1);
		if (!that.location.getFile().startsWith(thisPath))
		    return false;
	    } else if (location.getFile().endsWith("/*")) {
		// Matches the directory and all the files contained in that
		// directory.
		// For example, "/a/b/*" implies anything that starts with
		// "/a/b/" but has no further slashes
		int last = that.location.getFile().lastIndexOf('/');
		if (last == -1) 
		    return false;
		String thisPath = location.getFile().substring(0,
                                                location.getFile().length()-1);
		String thatPath = that.location.getFile().substring(0, last+1);
		if (!thatPath.equals(thisPath))
		    return false;
	    } else {
		// Exact matches only.
		// For example, "/a/b" and "/a/b/" both imply "/a/b/" 
		if ((!that.location.getFile().equals(location.getFile()))
		&& (!that.location.getFile().equals(location.getFile()+"/"))) {
		    return false;
		}
	    }

	    if (location.getRef() == null)
		return true;
	    else 
		return location.getRef().equals(that.location.getRef());
	}

    /**
     * Returns a string describing this CodeSource, telling its
     * URL and certificates.
     * 
     * @return information about this CodeSource.
     */
    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append("(");
	sb.append(this.location);
	if (this.certs != null && this.certs.length > 0) {
	    for (int i=0; i < this.certs.length; i++) {
		sb.append( " "+this.certs[i]);
	    }
	} else {
	    sb.append(" <no certificates>");
	}
	sb.append(")");
	return sb.toString();
    }

    /**
     * Writes this object out to a stream (i.e., serializes it).
     *
     * @serialData An initial <code>URL</code> is followed by an
     * <code>int</code> indicating the number of certificates to follow 
     * (a value of "zero" denotes that there are no certificates associated
     * with this object).
     * Each certificate is written out starting with a <code>String</code>
     * denoting the certificate type, followed by an
     * <code>int</code> specifying the length of the certificate encoding,
     * followed by the certificate encoding itself which is written out as an
     * array of bytes.
     */
    private synchronized void writeObject(java.io.ObjectOutputStream oos)
        throws IOException
    {
	oos.defaultWriteObject();

	if (certs==null || certs.length==0) {
	    oos.writeInt(0);
	} else {
	    // write out the total number of certs
	    oos.writeInt(certs.length);
	    // write out each cert, including its type
	    for (int i=0; i < certs.length; i++) {
		java.security.cert.Certificate cert = certs[i];
		try {
		    oos.writeUTF(cert.getType());
		    byte[] encoded = cert.getEncoded();
		    oos.writeInt(encoded.length);
		    oos.write(encoded);
		} catch (CertificateEncodingException cee) {
		    throw new IOException(cee.getMessage());
		}
	    }
	}
    }

    /**
     * Restores this object from a stream (i.e., deserializes it).
     */
    private synchronized void readObject(java.io.ObjectInputStream ois)
	throws IOException, ClassNotFoundException
    {
	CertificateFactory cf;
	Hashtable cfs=null;

	ois.defaultReadObject();

	// process any new-style certs in the stream (if present)
	int size = ois.readInt();
	if (size > 0) {
	    // we know of 3 different cert types: X.509, PGP, SDSI, which
	    // could all be present in the stream at the same time
	    cfs = new Hashtable(3);
	    this.certs = new java.security.cert.Certificate[size];
	}

	for (int i=0; i<size; i++) {
	    // read the certificate type, and instantiate a certificate
	    // factory of that type (reuse existing factory if possible)
	    String certType = ois.readUTF();
	    if (cfs.containsKey(certType)) {
		// reuse certificate factory
		cf = (CertificateFactory)cfs.get(certType);
	    } else {
		// create new certificate factory
		try {
		    cf = CertificateFactory.getInstance(certType);
		} catch (CertificateException ce) {
		    throw new ClassNotFoundException
			("Certificate factory for "+certType+" not found");
		}
		// store the certificate factory so we can reuse it later
		cfs.put(certType, cf);
	    }
	    // parse the certificate
	    byte[] encoded=null;
	    try {
		encoded = new byte[ois.readInt()];
	    } catch (OutOfMemoryError oome) {
		throw new IOException("Certificate too big");
	    }
	    ois.readFully(encoded);
	    ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
	    try {
		this.certs[i] = cf.generateCertificate(bais);
	    } catch (CertificateException ce) {
		throw new IOException(ce.getMessage());
	    }
	    bais.close();
	}
    }
}


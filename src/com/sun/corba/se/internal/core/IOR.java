/*
 * @(#)IOR.java	1.95 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

package com.sun.corba.se.internal.core;

import java.io.StringWriter;
import java.io.IOException;

import java.util.Iterator ;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;

import javax.rmi.CORBA.Util;

import com.sun.corba.se.internal.orbutil.HexOutputStream;
import com.sun.corba.se.internal.orbutil.MinorCodes;
import com.sun.corba.se.internal.util.JDKBridge;
import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.core.StandardIIOPProfileTemplate;
import com.sun.corba.se.internal.orbutil.ORBUtility;
import com.sun.corba.se.internal.corba.EncapsInputStream;

import com.sun.corba.se.internal.ior.JavaCodebaseComponent ;
import com.sun.corba.se.internal.ior.CodeSetsComponent ;
import com.sun.corba.se.internal.ior.ObjectKey ;
import com.sun.corba.se.internal.ior.ObjectKeyFactory ;
import com.sun.corba.se.internal.ior.ObjectKeyTemplate ;
import com.sun.corba.se.internal.ior.POAObjectKeyTemplate ;
import com.sun.corba.se.internal.ior.ObjectId ;
import com.sun.corba.se.internal.ior.POAId ;
import com.sun.corba.se.internal.ior.IIOPAddress ;
import com.sun.corba.se.internal.ior.IIOPAddressImpl ;
import com.sun.corba.se.internal.ior.IIOPProfileTemplate ;
import com.sun.corba.se.internal.ior.IIOPProfile ;
import com.sun.corba.se.internal.ior.IORTemplate ;
import com.sun.corba.se.internal.ior.TaggedComponent ;
import com.sun.corba.se.internal.ior.TaggedComponentFactoryFinder ;
import com.sun.corba.se.internal.POA.POAORB ;

import org.omg.IOP.TAG_JAVA_CODEBASE ;
import org.omg.IOP.TAG_INTERNET_IOP ;

public class IOR extends com.sun.corba.se.internal.ior.IOR
{
    public static final IOR NULL = new IOR();

    protected ORB factory = null ;

    // Cached lookups
    protected IIOPProfile iop = null ;

    protected String codebase = null ;
    protected boolean cachedCodebase = false;

    private byte[] objectId = null ;

    private boolean lookedForPOAId = false ;
    private POAId poaid = null ;

    private boolean checkedIsLocal = false ;
    private boolean cachedIsLocal = false ;

    // initialize-on-demand holder
    private static class LocalCodeBaseSingletonHolder {
	public static JavaCodebaseComponent comp ;

	static {
	    String localCodebase = JDKBridge.getLocalCodebase() ;
	    if (localCodebase == null)
		comp = null ;
	    else
		comp = new JavaCodebaseComponent( localCodebase ) ;
	}
    }

    private Object cookie ;

    public synchronized Object getCookie()
    {
	return cookie ;
    }

    public synchronized void setCookie( Object cookie ) 
    {
	this.cookie = cookie ;
    }

    /*
     * Only for the NULL instance.
     */
    private IOR() {
	this( (ORB)null ) ;
    }

    public IOR( ORB factory) {
	this( factory, "" ) ;
    }

    public IOR( ORB factory, String typeId )
    {
	super( typeId ) ;
	this.factory = factory ;
    }

    public IOR( ORB factory, String typeId, IORTemplate template,
	ObjectId id )
    {
	super( typeId, template, id ) ;
	this.factory = factory ;
    }

    public IOR( ORB factory, String typeId,
	String host, int port,
	byte[] key ) throws SystemException
    {
	this( factory, typeId, host, port,
            ObjectKeyFactory.get().create(factory, key)) ;
    }

    public IOR( ORB factory, String typeId,
	String host, int port,
	ObjectKey key ) throws SystemException
    {
	this( factory, typeId, host, port, key, null ) ;
    }

    public IOR( ORB factory, String typeId,
	String host, int port,
	ObjectKey okey, Object servant ) throws SystemException
    {
    	this(factory, typeId );

	GIOPVersion version = factory.getGIOPVersion() ;
	int major = version.getMajor() ;
	int minor = version.getMinor() ;

	init( host, port, major, minor, okey, servant ) ;
    }

    public IOR( ORB factory, String typeId,
	String host, int port, int major, int minor,
	ObjectKey okey, Object servant ) throws SystemException
    {
    	this(factory, typeId );

	init( host, port, major, minor, okey, servant ) ;
    }

    private void init( String host, int port, int major, int minor,
	ObjectKey okey, Object servant )
    {
	ObjectKeyTemplate ktemp = okey.getTemplate() ;
	ObjectId id = okey.getId() ;

	IIOPAddress addr = new IIOPAddressImpl( host, port ) ;
	IIOPProfileTemplate ptemp = new StandardIIOPProfileTemplate(
	    addr, major, minor, ktemp, servant, factory ) ;

	IIOPProfile profile = new IIOPProfile( id, ptemp ) ;
	add( profile ) ;
	makeImmutable() ;
    }

    /** This constructor is added to create IOR's from the URL based
     *  names for the INS. The constructor is same as others except for the
     *  Object key which will be a String instead of octets. There will be no
     *  conversion of the String to Hexadecimal Bytes.
     */
    public IOR( ORB factory, String typeId,
	String host, int port, int major, int minor,
	String asciiBasedKey, Object servant ) throws SystemException
    {
    	this(factory, typeId, host, port, major, minor,
	    ObjectKeyFactory.get().create( factory, asciiBasedKey.getBytes() ),
	    servant );
    }

    private static final int NIBBLES_PER_BYTE = 2 ;
    private static final int UN_SHIFT = 4 ; // "UPPER NIBBLE" shift factor for <<

    /** This static method takes a Stringified IOR and converts it into IOR object.
      * It is the caller's responsibility to only pass strings that start with "IOR:".
      */
    public static org.omg.CORBA.Object getIORFromString(ORB factory,
	String str )
    {
	// Length must be even for str to be valid
	if ( (str.length() & 1) == 1 )
	    throw new org.omg.CORBA.DATA_CONVERSION(
		MinorCodes.BAD_STRINGIFIED_IOR_LEN,
		CompletionStatus.COMPLETED_NO ) ;

	byte[] buf = new byte[(str.length() - PREFIX_LENGTH) / NIBBLES_PER_BYTE];
	for (int i=PREFIX_LENGTH, j=0; i < str.length(); i +=NIBBLES_PER_BYTE, j++) {
	     buf[j] = (byte)((ORBUtility.hexOf(str.charAt(i)) << UN_SHIFT) & 0xF0);
	     buf[j] |= (byte)(ORBUtility.hexOf(str.charAt(i+1)) & 0x0F);
	}
	EncapsInputStream s = new EncapsInputStream(factory, buf, buf.length);
	s.consumeEndian();
	return s.read_Object() ;
    }

    public IOR(InputStream s)
    {
	super( s ) ;
	this.factory = (ORB)(s.orb()) ;
        cachedCodebase = false;

        if (!is_nil()) {
            // If there is no codebase in this IOR and there IS a
            // java.rmi.server.codebase property set, we need to
            // update the IOR with the local codebase.  Note that
	    // there is only one instance of the local codebase, but it
	    // can be safely shared in multiple IORs since it is immutable.
	    if (uncachedGetCodeBase() == null) {
		JavaCodebaseComponent jcc = LocalCodeBaseSingletonHolder.comp ;

		if (jcc != null) {
		    IIOPProfileTemplate temp = getProfile().getTemplate() ;
		    temp.add( jcc ) ;

                    codebase = jcc.getURLs() ;
                    cachedCodebase = true;
                }
            }
        }

	makeImmutable() ;
    }

    private String uncachedGetCodeBase() {
	Iterator iter = getProfile().getTemplate().iteratorById(
	    TAG_JAVA_CODEBASE.value ) ;

	if (iter.hasNext()) {
	    JavaCodebaseComponent jcbc = (JavaCodebaseComponent)(iter.next()) ;
	    return jcbc.getURLs() ;
	}

	return null ;
    }

    public synchronized String getCodebase() {
	if (!cachedCodebase) {
	    cachedCodebase = true ;
	    codebase = uncachedGetCodeBase() ;
	}

	return codebase ;
    }

    /** Get the IIOP Profile from this IOR.  We are assuming that there
    * can be only one at present.   If more than one IIOP profile is
    * present, we will throw an exception here.
    */
    public IIOPProfile getProfile()
    {
	if (iop == null) {
	    Iterator iter = iteratorById( TAG_INTERNET_IOP.value ) ;
	    if (iter.hasNext())
		iop = (IIOPProfile)(iter.next()) ;

// If there are multiple profile we always return the first one.
//	    if (iter.hasNext())
//		throw new INTERNAL( MinorCodes.MULT_IIOP_PROF_NOT_SUPPORTED,
//		    CompletionStatus.COMPLETED_NO ) ;
	}

	if (iop != null)
	    return iop ;

        // if we come to this point then no IIOP Profile
        // is present.  Therefore, throw an INV_OBJREF exception.
        throw new INV_OBJREF( MinorCodes.NO_PROFILE_PRESENT,
			      CompletionStatus.COMPLETED_NO );
    }

    public synchronized byte[] getObjectId()
    {
	if (objectId == null) {
	    IIOPProfile prof = getProfile() ;
	    ObjectId oid = prof.getObjectId() ;
	    objectId = oid.getId() ;
	}

	return objectId ;
    }
    
    public synchronized POAId getPOAId() 
    {
	if (!lookedForPOAId) {
	    lookedForPOAId = true ;
	    IIOPProfile prof = getProfile() ;
	    ObjectKeyTemplate oktemp = 
		prof.getTemplate().getObjectKeyTemplate() ;

	    if (oktemp instanceof POAObjectKeyTemplate) {
		POAObjectKeyTemplate poktemp = (POAObjectKeyTemplate)oktemp ;
		poaid = poktemp.getPOAId() ;
	    }
	}

	return poaid ;
    }

    /**
     * @return the ORBVersion associated with the object key in the IOR.
     */
    public ORBVersion getORBVersion() {
        return this.getProfile().getTemplate().
            getObjectKeyTemplate().getORBVersion();
    }

    public String stringify()
    {
        return stringify(factory);
    }

    public boolean is_nil()
    {
        //
        // The check for typeId length of 0 below is commented out
        // as a workaround for a bug in ORBs which send a
        // null objref with a non-empty typeId string.
        //
	return ((size() == 0) /* && (typeId.length() == 0) */);
    }

    public boolean isEquivalent(IOR ior)
    {
	return getProfile().isEquivalent(ior.getProfile());
    }

    public synchronized boolean isLocal()
    {
	if (factory == null)
	    return false ;

	if (!checkedIsLocal) {
	    checkedIsLocal = true ;
	    IIOPProfile iop = getProfile() ;
	    IIOPProfileTemplate ptemp = iop.getTemplate() ;
	    String host = ptemp.getPrimaryAddress().getHost() ;
	    ObjectKeyTemplate ktemp = ptemp.getObjectKeyTemplate() ;

	    cachedIsLocal = factory.isLocalHost( host ) && factory.isLocalServerId(
		ktemp.getSubcontractId(), ktemp.getServerId() ) && 
		factory.isLocalServerPort( ptemp.getPrimaryAddress().getPort( ) );
	}

	return cachedIsLocal ;
    }

    /**
     * (Compute and) Return GIOPVersion for this IOR
     * Requests created against this IOR will be of the
     * return Version.
     */
    public synchronized GIOPVersion getGIOPVersion(){

        if (this.giopVersion != null) {
            return this.giopVersion;
        }

        byte major = getProfile().getTemplate().getMajorVersion();
        byte minor = getProfile().getTemplate().getMinorVersion();

        this.giopVersion = GIOPVersion.getInstance(major, minor);

        //REVISIT: Should we be cloning here before a return?
        return this.giopVersion;
    }

    private GIOPVersion giopVersion = null;

    public ServerSubcontract getServerSubcontract()
    {
	ObjectKeyTemplate temp = getProfile().getTemplate().getObjectKeyTemplate() ;
	int scid = temp.getSubcontractId() ;
	ServerSubcontract sc = factory.getSubcontractRegistry().getServerSubcontract( scid ) ;
	return sc ;
    }

    public com.sun.corba.se.internal.core.IOR addComponent(
	org.omg.IOP.TaggedComponent component
    )
    {
	// Get information from IOR
	String typeId = getTypeId() ;
	IIOPProfile iop = getProfile() ;

	// Get Information from IIOPProfile
	ObjectId oid = iop.getObjectId() ;
	IIOPProfileTemplate ptemp = iop.getTemplate() ;

	// Get Information from IIOPProfileTemplate
	byte major = ptemp.getMajorVersion() ;
	byte minor = ptemp.getMinorVersion() ;
	IIOPAddress addr = ptemp.getPrimaryAddress() ;
	ObjectKeyTemplate oktemp = ptemp.getObjectKeyTemplate() ;

	// Create new IIOPProfileTemplate
	IIOPProfileTemplate ptempResult = new IIOPProfileTemplate(
	    major, minor, addr, oktemp ) ;

	// Add tagged components from old IIOPProfileTemplate to the new one
	Iterator iter = ptemp.iterator() ;
	while (iter.hasNext()) {
	    Object obj = iter.next() ;
	    ptempResult.add( obj ) ;
	}

	// Convert component to ior.TaggedComponent and add to the new
	// profile
	TaggedComponent comp = TaggedComponentFactoryFinder.getFinder().
	    create( factory, component ) ;
	ptempResult.add( comp ) ;

	// Create new IIOPProfile
	IIOPProfile iopResult = new IIOPProfile( oid, ptempResult ) ;

	// Create new IOR
	IOR result = new IOR( factory, typeId ) ;
	result.add( iopResult ) ;
	result.makeImmutable() ;
	return result ;
    }

    public void dump( )
    {
	IIOPProfile prof = getProfile() ;
	IIOPProfileTemplate ptemp = prof.getTemplate() ;
	ObjectId oid = prof.getObjectId() ;
	ObjectKeyTemplate oktemp = ptemp.getObjectKeyTemplate() ;

	System.out.println( "Contents of IOR:" ) ;
	System.out.println( "\tTypeId = " + getTypeId() ) ;
	System.out.println( "\tIIOPProfileTemplate = " ) ;
	System.out.println( "\t\tmajor version = " + ptemp.getMajorVersion() ) ;
	System.out.println( "\t\tminor version = " + ptemp.getMinorVersion() ) ;
	System.out.println( "\t\thost name = " + ptemp.getPrimaryAddress().getHost() ) ;
	System.out.println( "\t\tport number = " + ptemp.getPrimaryAddress().getPort() ) ;
	System.out.println( "\t\tObject Key Template:" ) ;
	System.out.println( "\t\t\tSubcontract id = " + oktemp.getSubcontractId() ) ;
	System.out.println( "\t\t\tServer id = " + oktemp.getServerId() ) ;
    }

    public boolean isTransactional()
    {
	IIOPProfile prof = getProfile() ;
	IIOPProfileTemplate ptemp = prof.getTemplate() ;
	ObjectKeyTemplate oktemp = ptemp.getObjectKeyTemplate() ;
	int scid = oktemp.getSubcontractId() ;
	boolean result = ((scid & 1) == 1) ;

	if (factory != null)
	    if (((POAORB)factory).subcontractDebugFlag)
		System.out.println( "core.IOR.isTransactional returns " +
		result ) ;

	return result ;
    }
}

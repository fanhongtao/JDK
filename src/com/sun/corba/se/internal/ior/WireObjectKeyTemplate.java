/*
 * @(#)WireObjectKeyTemplate.java	1.17 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/WireObjectKey.java

package com.sun.corba.se.internal.ior;

import com.sun.corba.se.internal.core.ORBVersion ;
import com.sun.corba.se.internal.core.ORBVersionImpl ;
import com.sun.corba.se.internal.orbutil.SubcontractList ;
import com.sun.corba.se.internal.orbutil.MinorCodes ;
import com.sun.corba.se.internal.ior.ObjectKeyTemplate ;
import com.sun.corba.se.internal.ior.ObjectId ;
import com.sun.corba.se.internal.iiop.CDRInputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA.CompletionStatus ;
import org.omg.CORBA.BAD_OPERATION ;
import org.omg.CORBA.ORB ;
import org.omg.CORBA.OctetSeqHolder ;

/**
 * @author 
 */
public class WireObjectKeyTemplate implements ObjectKeyTemplate 
{
    public boolean equals( Object obj )
    {
	if (obj == null)
	    return false ;

	return obj instanceof WireObjectKeyTemplate ;
    }

    private byte[] getId( InputStream is ) 
    {
	CDRInputStream cis = (CDRInputStream)is ;
	int len = cis.getBufferLength() ;
	byte[] result = new byte[ len ] ;
	cis.read_octet_array( result, 0, len ) ;
	return result ;
    }

    public WireObjectKeyTemplate( InputStream is, OctetSeqHolder osh ) 
    {
	osh.value = getId( is ) ;
    }

    public void write( ObjectId id, OutputStream os ) 
    {
	byte[] key = id.getId() ;
	os.write_octet_array( key, 0, key.length ) ;
    }

    public int getSubcontractId()
    {
	return SubcontractList.defaultSubcontract ;
    }

    /** While it might make sense to throw an exception here, this causes
    * problems since we need to check whether unusual object references
    * are local or not.  It seems that the easiest way to handle this is
    * to return an invalid server id.
    */
    public int getServerId() 
    {
	return -1 ;
	// throw new BAD_OPERATION( MinorCodes.SERVER_ID_NOT_AVAILABLE,
	    // CompletionStatus.COMPLETED_NO ) ;
    }

    /** Adapter ID is not available, since our
    * ORB did not implement the object carrying this key.
    */
    public byte[] getAdapterId( ORB orb )
    {
	throw new BAD_OPERATION( MinorCodes.ADAPTER_ID_NOT_AVAILABLE,
	    CompletionStatus.COMPLETED_NO ) ;
    }

    public ORBVersion getORBVersion() 
    {
	return ORBVersionImpl.FOREIGN ;
    }
}

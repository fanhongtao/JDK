/*
 * @(#)OldJIDLObjectKeyTemplate.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#)OldJIDLObjectKeyTemplate.java	1.5 00/10/11

package com.sun.corba.se.internal.ior;

import com.sun.corba.se.internal.ior.ObjectId ;
import com.sun.corba.se.internal.ior.ObjectKeyFactory ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import com.sun.corba.se.internal.iiop.CDRInputStream ;
import com.sun.corba.se.internal.core.ORBVersion ;
import com.sun.corba.se.internal.core.ORBVersionImpl ;
import org.omg.CORBA.ORB ;
import org.omg.CORBA.OctetSeqHolder ;
import org.omg.CORBA.INTERNAL ;
import org.omg.CORBA.CompletionStatus ;
import com.sun.corba.se.internal.orbutil.MinorCodes ;

/**
 * Handles object keys created by JDK ORBs from before JDK 1.4.0. 
 */
public final class OldJIDLObjectKeyTemplate extends OldObjectKeyTemplateBase
{
    /**
     * JDK 1.3.1 FCS did not include a version byte at the end of
     * its object keys.  JDK 1.3.1_01 included the byte with the
     * value 1.  Anything below 1 is considered an invalid value.
     */
    public static final byte NULL_PATCH_VERSION = 0;

    byte patchVersion = OldJIDLObjectKeyTemplate.NULL_PATCH_VERSION;

    public OldJIDLObjectKeyTemplate( byte[] key, int magic, int scid, CDRInputStream is,
	OctetSeqHolder osh ) 
    {
	super( magic, scid, is.read_long() );
	osh.value = readObjectKey( is ) ;
        
        /**
         * Beginning with JDK 1.3.1_01, a byte was placed at the end of
         * the object key with a value indicating the patch version.
         * JDK 1.3.1_01 had the value 1.  If other patches are necessary
         * which involve ORB versioning changes, they should increment
         * the patch version.
         *
         * Note that if we see a value greater than 1 in this code, we
         * will treat it as if we're talking to JDK 1.4.0 or greater.
         *
         * WARNING: This code is sensitive to changes in CDRInputStream
         * getPosition.  It assumes that the CDRInputStream is an
         * encapsulation whose position can be compared to the object
         * key array length.
         */
        if (magic == ObjectKeyTemplate.JAVAMAGIC_NEW &&
            key.length > is.getPosition()) {

            patchVersion = is.read_octet();

            if (patchVersion == ObjectKeyTemplate.JDK1_3_1_01_PATCH_LEVEL)
                setORBVersion(ORBVersionImpl.JDK1_3_1_01);
            else
            if (patchVersion > ObjectKeyTemplate.JDK1_3_1_01_PATCH_LEVEL)
                setORBVersion(ORBVersionImpl.NEWER);
            else
                throw new INTERNAL("Invalid JDK 1.3.1 patch level: "
                                   + patchVersion,
                                   MinorCodes.INVALID_JDK1_3_1_PATCH_LEVEL,
                                   CompletionStatus.COMPLETED_NO);
        }
    }
    
    /**
     * @param scid
     * @param serverid
     * @return 
     * @exception 
     * @author 
     * @roseuid 3915FF9803AA
     */
    public OldJIDLObjectKeyTemplate(int magic, int scid, int serverid) 
    {
	super( magic, scid, serverid ) ; 
    }
   
    protected void write( OutputStream os )
    {
	os.write_long( getMagic() ) ;
	os.write_long( getSubcontractId() ) ;
	os.write_long( getServerId() ) ;
    }

    public void write(ObjectId objectId, OutputStream os) 
    {
        super.write(objectId, os);

        if (patchVersion != OldJIDLObjectKeyTemplate.NULL_PATCH_VERSION)
           os.write_octet( patchVersion ) ;        
    }
}

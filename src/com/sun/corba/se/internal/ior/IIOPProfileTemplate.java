/*
 * @(#)IIOPProfileTemplate.java	1.18 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/IIOPProfileTemplate.java

package com.sun.corba.se.internal.ior;

import java.util.LinkedList ;

import org.omg.IOP.TAG_INTERNET_IOP ;

import org.omg.CORBA_2_3.portable.OutputStream ;

import com.sun.corba.se.internal.ior.TaggedProfileTemplate ;
import com.sun.corba.se.internal.ior.ObjectKeyTemplate ;
import com.sun.corba.se.internal.ior.IIOPAddress ;
import com.sun.corba.se.internal.ior.ObjectId ;
import com.sun.corba.se.internal.ior.IdEncapsulationBase ;
import com.sun.corba.se.internal.corba.EncapsOutputStream ;
import com.sun.corba.se.internal.iiop.CDROutputStream ;
import com.sun.corba.se.internal.core.GIOPVersion ;

/**
 * @author 
 * If getMinorVersion==0, this does not contain any tagged components
 */
public class IIOPProfileTemplate extends IdEncapsulationContainerBase 
    implements TaggedProfileTemplate 
{
    private byte major;
    private byte minor;
    private IIOPAddress primary ;
    private ObjectKeyTemplate okeyTemplate;
    
    public boolean equals( Object obj )
    {
	if (obj == null)
	    return false ;

	if (!(obj instanceof IIOPProfileTemplate))
	    return false ;

	IIOPProfileTemplate other = (IIOPProfileTemplate)obj ;

	return super.equals( obj ) &&
	    (major == other.major) && (minor == other.minor) &&
	    primary.equals( other.primary ) &&
	    okeyTemplate.equals( other.okeyTemplate ) ;
    }

    public TaggedProfile create( ObjectId id ) 
    {
	return new IIOPProfile( id, this ) ;
    }

    public byte getMajorVersion()
    {
	return major ;
    }

    public byte getMinorVersion()
    {
	return minor ;
    }

    public IIOPAddress getPrimaryAddress() 
    {
	return primary ;
    }

    public ObjectKeyTemplate getObjectKeyTemplate()
    {
	return okeyTemplate ;
    }
    
    public IIOPProfileTemplate( byte major, byte minor,
	IIOPAddress primary, ObjectKeyTemplate template ) 
    {
	this.major = major ;
	this.minor = minor ;
	this.primary = primary ;
	this.okeyTemplate = template ;
	if (minor == 0)
	    makeImmutable() ;
    }
    
    /**
     * @param location
     * @param id
     * @param os
     * @return void
     * @exception 
     * @author 
     * @roseuid 3911D7170126
     */
    public void write( ObjectId id, OutputStream os) 
    {
	os.write_octet( major ) ;
	os.write_octet( minor ) ;
	primary.write( os ) ;

	// Note that this is NOT an encapsulation: do not marshal
	// the endianness flag.  However, the length is required.
	// Note that this cannot be accomplished with a codec!

        // Use the byte order of the given stream
        OutputStream kos
            = new EncapsOutputStream( os.orb(),
                                      ((CDROutputStream)os).isLittleEndian() ) ;

	okeyTemplate.write( id, kos ) ;
	IdEncapsulationBase.writeOutputStream(
	    (CDROutputStream)kos, os ) ;

	if (minor > 0) 
	    writeIdEncapsulationSequence( os ) ;
    }
    
    /**
     * @return int
     * @exception 
     * @author 
     * @roseuid 39132884035F
     */
    public int getId() 
    {
	return TAG_INTERNET_IOP.value ;
    }

    public boolean isEquivalent( IIOPProfileTemplate temp )
    {
	return primary.equals( temp.primary ) &&
	    okeyTemplate.equals( temp.okeyTemplate ) ;
    }
}

/*
 * @(#)IIOPProfile.java	1.28 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/IIOPProfile.java

package com.sun.corba.se.internal.ior;

import java.util.List ;
import java.util.Iterator ;
import com.sun.corba.se.internal.ior.IdEncapsulationBase ;
import com.sun.corba.se.internal.ior.TaggedProfile ;
import com.sun.corba.se.internal.ior.ObjectId ;
import com.sun.corba.se.internal.ior.IIOPProfileTemplate ;
import com.sun.corba.se.internal.ior.IIOPAddressImpl ;
import com.sun.corba.se.internal.ior.ObjectKey ;
import com.sun.corba.se.internal.ior.ObjectKeyFactory ;
import com.sun.corba.se.internal.ior.AlternateIIOPAddressComponent ;
import com.sun.corba.se.internal.ior.TaggedComponentFactoryFinder ;
import org.omg.CORBA.BAD_PARAM;
import org.omg.IOP.TAG_ALTERNATE_IIOP_ADDRESS ;
import org.omg.IOP.TAG_INTERNET_IOP;
import org.omg.CORBA.ORB ;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA_2_3.portable.OutputStream ;
import org.omg.CORBA_2_3.portable.InputStream ;
import com.sun.corba.se.internal.iiop.CDRInputStream ;
import com.sun.corba.se.internal.corba.EncapsInputStream ;
import com.sun.corba.se.internal.corba.EncapsOutputStream ;
import com.sun.corba.se.internal.core.GIOPVersion ;
import com.sun.corba.se.internal.orbutil.MinorCodes;

/**
 * @author
 */
public class IIOPProfile extends IdEncapsulationBase
    implements TaggedProfile
{
    private ObjectId id;
    private final IIOPProfileTemplate template;

    public boolean equals( Object obj )
    {
	if (obj == null)
	    return false ;

	if (!(obj instanceof IIOPProfile))
	    return false ;

	IIOPProfile other = (IIOPProfile)obj ;

	return id.equals( other.id ) && template.equals( other.template ) ;
    }

    public ObjectId getObjectId()
    {
	return id ;
    }

    public IIOPProfileTemplate getTemplate()
    {
	return template ;
    }

    public IIOPProfile( ObjectId id, IIOPProfileTemplate template )
    {
	this.id = id ;
	this.template = template ;
    }

    public IIOPProfile( InputStream is )
    {
	InputStream istr = IdEncapsulationBase.getEncapsulationStream( is ) ;

	// First, read all of the IIOP IOR data
	byte major = istr.read_octet() ;
	byte minor = istr.read_octet() ;
	IIOPAddress primary = new IIOPAddressImpl( istr ) ;
	byte[] key = IdEncapsulationBase.readOctets( istr ) ;

	ORB orb = ((CDRInputStream)is).orb() ;
	ObjectKey okey = ObjectKeyFactory.get().create( orb, key ) ;
	id = okey.getId() ;

	template = new IIOPProfileTemplate( major, minor, primary,
	    okey.getTemplate() ) ;

	// Handle any tagged components (if applicable)
	if (minor > 0)
	    template.readIdEncapsulationSequence(
		TaggedComponentFactoryFinder.getFinder(), istr ) ;
    }

    public IIOPProfile(org.omg.CORBA.ORB orb,
            org.omg.IOP.TaggedProfile profile) {

        if (profile == null ||
                profile.tag != TAG_INTERNET_IOP.value ||
                profile.profile_data == null) {
            throw new BAD_PARAM(MinorCodes.INVALID_TAGGED_PROFILE,
				CompletionStatus.COMPLETED_NO);
        }

        EncapsInputStream istr = new EncapsInputStream(orb,
                                                       profile.profile_data,
                                                       profile.profile_data.length);
	istr.consumeEndian();

        // First, read all of the IIOP IOR data
	byte major = istr.read_octet() ;
	byte minor = istr.read_octet() ;
	IIOPAddress primary = new IIOPAddressImpl(istr);
	byte[] key = IdEncapsulationBase.readOctets(istr);

	ObjectKey okey = ObjectKeyFactory.get().create(orb, key);
	id = okey.getId();

	template = new IIOPProfileTemplate(major, minor, primary,
	                                   okey.getTemplate());

	// Handle any tagged components (if applicable)
	if (minor > 0) {
	    template.readIdEncapsulationSequence(
		TaggedComponentFactoryFinder.getFinder(), istr);
        }
    }

    /**
     * @param arg0
     * @return void
     * @exception
     * @author
     * @roseuid 39135D2D02EC
     */
    public void writeContents(OutputStream os)
    {
	template.write( id, os ) ;
    }

    /**
     * @return int
     * @exception
     * @author
     * @roseuid 39135D2E00C7
     */
    public int getId()
    {
	return template.getId() ;
    }

    public boolean isEquivalent( IIOPProfile prof )
    {
	return id.equals( prof.id ) && template.isEquivalent( prof.template ) ;
    }

    public ObjectKey getObjectKey()
    {
	ObjectKeyTemplate oktemp = template.getObjectKeyTemplate() ;
	ObjectKey result = new ObjectKey( oktemp, id ) ;
	return result ;
    }

    public org.omg.IOP.TaggedProfile getIOPProfile( ORB orb )
    {
	EncapsOutputStream os = new EncapsOutputStream( orb ) ;
	os.write_long( getId() ) ;
	write( os ) ;
	InputStream is = (InputStream)(os.create_input_stream()) ;
	return org.omg.IOP.TaggedProfileHelper.read( is ) ;
    }

    /**
     * @return org.omg.IOP.TaggedComponent[]
     * @exception
     * @author
     * @roseuid 3980B6A4037F
     */
    public org.omg.IOP.TaggedComponent[] getIOPComponents(
	ORB orb, int id )
    {
	int count = 0 ;
	Iterator iter = template.iteratorById( id ) ;
	while (iter.hasNext()) {
	    iter.next() ;
	    count++ ;
	}

	org.omg.IOP.TaggedComponent[] result = new
	    org.omg.IOP.TaggedComponent[count] ;

	int index = 0 ;
	iter = template.iteratorById( id ) ;
	while (iter.hasNext()) {
	    TaggedComponent comp = (TaggedComponent)(iter.next()) ;
	    result[index++] = comp.getIOPComponent( orb ) ;
	}

	return result ;
    }
}

/*
 * @(#)IORInfoImpl.java	1.21 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.Interceptors;

import org.omg.PortableInterceptor.IORInfo;

import com.sun.corba.se.interceptor.IORInfoExt;
import com.sun.corba.se.interceptor.UnknownType;
import com.sun.corba.se.internal.POA.POAImpl;
import com.sun.corba.se.internal.ior.IORTemplate;
import com.sun.corba.se.internal.ior.TaggedProfileTemplate;
import com.sun.corba.se.internal.ior.TaggedComponentFactoryFinder;
import org.omg.IOP.TaggedComponent;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INV_POLICY;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Policy;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORB ;

import java.util.*;

/**
 * IORInfoImpl is the implementation of the IORInfo class, as described
 * in orbos/99-12-02, section 7.  
 */

public final class IORInfoImpl 
    extends LocalObject 
    implements IORInfo, IORInfoExt
{
    
    // The ORB
    private ORB orb;
    
    // The POAImpl associated with this IORInfo object.
    private POAImpl poaImpl;

    /**
     * Creates a new IORInfo implementation.  This info object will establish
     * tagged components with the template for the provided IOR Template.
     */
    IORInfoImpl( ORB orb, POAImpl poaImpl ) {
        this.orb = orb;
	this.poaImpl = poaImpl;
    }

    /**
     * An ORB service implementation may determine what server side policy 
     * of a particular type is in effect for an IOR being constructed by 
     * calling the get_effective_policy operation.  When the IOR being 
     * constructed is for an object implemented using a POA, all Policy 
     * objects passed to the PortableServer::POA::create_POA call that 
     * created that POA are accessible via get_effective_policy.
     * <p>
     * If a policy for the given type is not known to the ORB, then this 
     * operation will raise INV_POLICY with a standard minor code of 2.
     *
     * @param type The CORBA::PolicyType specifying the type of policy to 
     *   return.
     * @return The effective CORBA::Policy object of the requested type.
     *   If the given policy type is known, but no policy of that tpye is 
     *   in effect, then this operation will return a nil object reference.
     */
    public Policy get_effective_policy (int type) {
	return poaImpl.get_effective_policy( type );
    }

    /**
     * A portable ORB service implementation calls this method from its 
     * implementation of establish_components to add a tagged component to 
     * the set which will be included when constructing IORs.  The 
     * components in this set will be included in all profiles.
     * <p>
     * Any number of components may exist with the same component ID.
     *
     * @param tagged_component The IOP::TaggedComponent to add
     */
    public void add_ior_component (TaggedComponent tagged_component) {
	if( tagged_component == null ) nullParam();
        addIORComponentToProfileInternal( tagged_component, 
					  poaImpl.getIORTemplate().iterator());
    }

    /**
     * A portable ORB service implementation calls this method from its 
     * implementation of establish_components to add a tagged component to 
     * the set which will be included when constructing IORs.  The 
     * components in this set will be included in the specified profile.
     * <p>
     * Any number of components may exist with the same component ID.
     * <p>
     * If the given profile ID does not define a known profile or it is 
     * impossible to add components to thgat profile, BAD_PARAM is raised 
     * with a minor code of TBD_BP + 3.
     *
     * @param tagged_component The IOP::TaggedComponent to add.
     * @param profile_id The IOP::ProfileId tof the profile to which this 
     *     component will be added.
     */
    public void add_ior_component_to_profile ( 
        TaggedComponent tagged_component, int profile_id ) 
    {
	if( tagged_component == null ) nullParam();
        addIORComponentToProfileInternal( 
	    tagged_component, poaImpl.getIORTemplate().iteratorById( 
	    profile_id ) );
    }

    /**
     * @param type The type of the server port
     *     (see connection.ORBSocketFactory for discussion).
     * @return The listen port number for that type.
     * @throws UnknownType if no port of the given type is found.
     */
    public int getServerPort(String type)
	throws
	    UnknownType
    {
	int port = ((com.sun.corba.se.internal.Interceptors.PIORB)orb)
	           .getServerPort(type);
	if (port == -1) {
	    throw new UnknownType();
	}
	return port;
    }
    
    /**
     * Internal utility method to add an IOR component to the set of profiles
     * present in the iterator.
     */
    private void addIORComponentToProfileInternal( 
        TaggedComponent tagged_component, Iterator iterator )
    {
        // Convert the given IOP::TaggedComponent into the appropriate
        // type for the TaggedProfileTemplate
        TaggedComponentFactoryFinder finder = 
            TaggedComponentFactoryFinder.getFinder();
        Object newTaggedComponent = finder.create( orb, tagged_component );
        
        // Iterate through TaggedProfileTemplates and add the given tagged
        // component to the appropriate one(s).
	boolean found = false;
        while( iterator.hasNext() ) {
	    found = true;
            TaggedProfileTemplate taggedProfileTemplate = 
                (TaggedProfileTemplate)iterator.next();
	    taggedProfileTemplate.add( newTaggedComponent );
        }

	// If no profile was found with the given id, throw a BAD_PARAM:
	// (See orbos/00-08-06, section 21.5.3.3.)
	if( !found ) {
	    throw new BAD_PARAM( 
		"Profile ID does not define a known profile or it is " +
		"impossible to add components to that profile.", 
		com.sun.corba.se.internal.Interceptors.MinorCodes.
		INVALID_PROFILE_ID, CompletionStatus.COMPLETED_NO );
	}
    }
    
    /**
     * Called when an invalid null parameter was passed.  Throws a
     * BAD_PARAM with a minor code of 1
     */
    private void nullParam() 
        throws BAD_PARAM 
    {
        throw new BAD_PARAM( 
            com.sun.corba.se.internal.orbutil.MinorCodes.NULL_PARAM, 
	    CompletionStatus.COMPLETED_NO );
    }

}

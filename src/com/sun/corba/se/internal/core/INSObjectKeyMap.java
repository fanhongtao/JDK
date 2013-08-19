/*
 * @(#)INSObjectKeyMap.java	1.6 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.core;

import java.util.Hashtable;

/**
 * This is a Singleton class which stores a map of INSObjectKey to the IOR
 * and Subcontract. This map is used by SibcontractRegistry and 
 * ServerSubcontract to reply INS locateMessages.
 * NOTE: This class is a singleton and will be shared among multiple ORB's
 * in the same process. If you register INS Service with the same name from
 * different ORBs then it will have a wrong effect of overwriting the old
 * registration.
 */
public class INSObjectKeyMap {
    private static INSObjectKeyMap instance = null;
    private static boolean isInitialized = false;

    private static class INSObjectKeyMapHolder {
	static final INSObjectKeyMap value = new INSObjectKeyMap() ;
    }

    public static INSObjectKeyMap getInstance( ) {
	return INSObjectKeyMapHolder.value ;
    }

    // objectKeyMap will have INS ASCII based ObjectKey as the key and
    // INSObjectKeyEntry as the entry, which will contain the target IOR and
    // subcontract.
    private Hashtable objectKeyMap;

    private INSObjectKeyMap( ) {
        objectKeyMap = new Hashtable();
    }

    public void setEntry( String objectKey, INSObjectKeyEntry entry )
    {
        objectKeyMap.put( objectKey, entry );
    }

    public INSObjectKeyEntry getEntry( String objectKey ) {
        return (INSObjectKeyEntry) objectKeyMap.get( objectKey ); 
    }
}


 

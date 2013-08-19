/*
 * @(#)NTSystem.java	1.6 02/02/19
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.security.auth.module;

import javax.security.auth.login.LoginException;

/**
 * <p> This class implementation retrieves and makes available NT
 * security information for the current user.
 * 
 * @version 1.9, 01/11/00
 */
public class NTSystem {
    
    private native void getCurrent();
    
    private native void logoff0() throws LoginException;

    private String userName;
    private String domain;
    private String domainSID;
    private String userSID;
    private String groupIDs[];
    private String primaryGroupID;
    private long impersonationToken;
    
    static boolean loadedLibrary = false;

    /**
     * Instantiate an <code>NTSystem</code> and load
     * the native library to access the underlying system information.
     */
    public NTSystem() {
        if (!loadedLibrary) {
            loadNative();
            loadedLibrary = true;
        }
	    this.userName = "";
        this.domain = "";
        this.domainSID = "";
        this.userSID = "";
        this.groupIDs = new String [0];
        this.primaryGroupID = "";
        this.impersonationToken = 0;
	    getCurrent();
	}
    
    /**
     * Get the username for the current NT user.
     *
     * <p>
     *
     * @return the username for the current NT user.
     */
    public String getName() {
        return userName;
    }
    
    /**
     * Get the domain for the current NT user.
     *
     * <p>
     *
     * @return the domain for the current NT user.
     */
    public String getDomain() {
        return domain;
    }
    
    /**
     * Get a printable SID for the current NT user's domain.
     *
     * <p>
     *
     * @return a printable SID for the current NT user's domain.
     */
    public String getDomainSID() {
        return domainSID;
    }
        
    /**
     * Get a printable SID for the current NT user.
     *
     * <p>
     *
     * @return a printable SID for the current NT user.
     */
    public String getUserSID() {
        return userSID;
    }
    
    /**
     * Get a printable primary group SID for the current NT user.
     *
     * <p>
     *
     * @return the primary group SID for the current NT user.
     */
    public String getPrimaryGroupID() {
        return primaryGroupID;
    }
    
    /**
     * Get the printable group SIDs for the current NT user.
     *
     * <p>
     *
     * @return the group SIDs for the current NT user.
     */
    public String[] getGroupIDs() {
        return groupIDs;
    }
    
    /**
     * Get an impersonation token for the current NT user.
     *
     * <p>
     *
     * @return an impersonation token for the current NT user.
     */
    public long getImpersonationToken() {
        return impersonationToken;
    }
    
    /**
     * Clean up NT resources when done.
     *
     * <p>
     *
     */
    void logoff() throws LoginException {
        LoginException le = null;
        try {
            logoff0();
        }
        catch (LoginException e) { le = e; }
        impersonationToken = 0;
        userName = null;
        domain = null;
        domainSID = null;
        userSID = null;
        primaryGroupID = null;
        for (int i = 0; i<groupIDs.length; i++)
            groupIDs[i] = null;
        groupIDs = null;
        if (le != null) throw le;
    }
    
    /**
     * Clean up NT resources during garbage collection, in case <code>LoginContext.logout()</code> was not called.
     *
     * <p>
     *
     */
    protected void finalize() {
        if (impersonationToken != 0) { // attempt resource cleanup
            try {
                logoff0();
            }
            catch (Exception e) {}
        }
        return;
    }
    
    private void loadNative() {
	System.loadLibrary("jaas_nt");
    }
}

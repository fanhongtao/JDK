/*
 * @(#)VersionString.java	1.9 03/01/23
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package jnlp.sample.util;
import java.util.ArrayList;
import java.util.StringTokenizer;

/*
 * Utility class that knows to handle version strings
 * A version string is of the form:
 *
 *  (version-id ('+'?) ' ') *
 *
 */
public class VersionString {
    private ArrayList _versionIds;
    
    /** Constructs a VersionString object from string */
    public VersionString(String vs) {
	_versionIds = new ArrayList();
	if (vs != null) {
	    StringTokenizer st = new StringTokenizer(vs, " ", false);
	    while(st.hasMoreElements()) {
		// Note: The VersionID class takes care of a postfixed '+'
		_versionIds.add(new VersionID(st.nextToken()));
	    }
	}
    }
    
    /** Check if this VersionString object contains the VersionID m */
    public boolean contains(VersionID m) {
	for(int i = 0; i < _versionIds.size(); i++) {
	    VersionID vi = (VersionID)_versionIds.get(i);
	    boolean check = vi.match(m);
	    if (check) return true;
	}
	return false;
    }
       
    /** Check if this VersionString object contains the VersionID m, given as a string */
    public boolean contains(String versionid) {
	return contains(new VersionID(versionid));
    }
    
    /** Check if this VersionString object contains anything greater than m */
    public boolean containsGreaterThan(VersionID m) {
        for(int i = 0; i < _versionIds.size(); i++) {
            VersionID vi = (VersionID)_versionIds.get(i);
            boolean check = vi.isGreaterThan(m);
            if (check) return true;
        }
        return false;
    }   
  
    /** Check if this VersionString object contains anything greater than the VersionID m, given as a string */
    public boolean containsGreaterThan(String versionid) {
	return containsGreaterThan(new VersionID(versionid));
    }
    
    /** Check if the versionString 'vs' contains the VersionID 'vi' */
    static public boolean contains(String vs, String vi) {
	return (new VersionString(vs)).contains(vi);
    }

    /** Pretty-print object */
    public String toString() {
	StringBuffer sb = new StringBuffer();
	for(int i = 0; i < _versionIds.size(); i++) {
	    sb.append(_versionIds.get(i).toString());
	    sb.append(' ');
	}
	return sb.toString();
    }
}




/*
 * @(#)RepositorySupport.java	1.65 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.mbeanserver;


// java import
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;

// RI import
import javax.management.* ; 
import com.sun.jmx.defaults.ServiceName;
import com.sun.jmx.trace.Trace;

/**
 * The RepositorySupport  implements the Repository interface.
 * This repository does not support persistency.
 *
 * @since 1.5
 */
public class RepositorySupport  implements  Repository { 
    
    // Private fields -------------------------------------------->

    /**
     * An object name for query describing the whole set of mbeans.
     * Optimization helper for queries.
     */
    private final static ObjectName _WholeWordQueryObjectName;
    static {
	try {
	    _WholeWordQueryObjectName = new ObjectName("*:*");
	} catch (MalformedObjectNameException e) {
	    throw new UnsupportedOperationException(e.getMessage());
	}
    }
    
    /**
     * two int utilities to minimize wildmatch method stack frame overhead
     * during recursions.
     */
    private static int _slen;
    private static int _plen;

    /**
     * The structure for storing the objects is very basic .
     * A Hashtable is used for storing the different domains
     * For each domain, a hashtable contains the instances with
     * canonical key property list string as key and named object
     * aggregated from given object name and mbean instance as value.
     */
    private final Hashtable domainTb;
    
    /**
     * Number of elements contained in the Repository
     */
    private int nbElements = 0;
  
    /**
     * Domain name of the server the repository is attached to.
     * It is quicker to store the information in the repository rather
     * than querying the framework each time the info is required.
     */
    private final String domain;
    
    /** The name of this class to be used for tracing */
    private final static String dbgTag = "Repository";

    // Private fields <=============================================
    

    // Private methods --------------------------------------------->

    // TRACES & DEBUG
    //---------------
    
    private final static boolean isTraceOn() {
	return Trace.isSelected(Trace.LEVEL_TRACE, Trace.INFO_MBEANSERVER);
    }
    
    private final static void trace(String clz, String func, String info) {
	Trace.send(Trace.LEVEL_TRACE, Trace.INFO_MBEANSERVER, clz, func, 
		   info);
    }
    
    private final static void trace(String func, String info) {
        trace(dbgTag, func, info);
    }
    
    private final static boolean isDebugOn() {
        return Trace.isSelected(Trace.LEVEL_DEBUG, Trace.INFO_MBEANSERVER);
    }
    
    private final static void debug(String clz, String func, String info) {
        Trace.send(Trace.LEVEL_DEBUG, Trace.INFO_MBEANSERVER, clz, func, 
		   info);
    }
    
    private final static void debug(String func, String info) {
        debug(dbgTag, func, info);
    }
    
    /* This class is used to match an ObjectName against a pattern. */
    private final static class ObjectNamePattern {
        private final char[]   domain;
        private final String[] keys;
        private final String[] values;
        private final String   properties;
        private final boolean  isPropertyPattern;
        
        /**
         * The ObjectName pattern against which ObjectNames are matched.
         **/
        public  final  ObjectName pattern;
        
        /**
         * Builds a new ObjectNamePattern object from an ObjectName pattern.
         * @param pattern The ObjectName pattern under examination.
         **/
        public ObjectNamePattern(ObjectName pattern) {
            this(pattern.isPattern(),pattern.getDomain(),
                 pattern.isPropertyPattern(),
                 pattern.getCanonicalKeyPropertyListString(),
                 pattern.getKeyPropertyList(),pattern);
        }

        /**
         * Builds a new ObjectNamePattern object from an ObjectName pattern
         * constituents.
         * @param domainPattern pattern.isPattern().
         * @param domain pattern.getDomain().
         * @param propertyPattern pattern.isPropertyPattern().
         * @param canonicalProps pattern.getCanonicalKeyPropertyListString()
         * @param keyPropertyList pattern.getKeyPropertyList()
         * @param pattern The ObjectName pattern under examination.
         **/
        ObjectNamePattern(boolean domainPattern, String domain, 
                          boolean propertyPattern, String canonicalProps,
                          Hashtable keyPropertyList, ObjectName pattern) {
            final int len = (keyPropertyList==null?0:keyPropertyList.size());
            final Enumeration e = 
                (keyPropertyList==null?null:keyPropertyList.keys());
            this.domain = (domain == null?null:domain.toCharArray());
            this.keys   = new String[len];
            this.values = new String[len];
            for (int i = 0 ; i < len ; i++ ) {
                final String k = (String)e.nextElement();
                keys[i]   = k;
                values[i] = (String)keyPropertyList.get(k);
            }
            this.properties = canonicalProps;
            this.isPropertyPattern = propertyPattern;
            this.pattern = pattern;
        }

        /**
         * Return true if the given ObjectName matches the ObjectName pattern
         * for which this object has been built.
         * WARNING: domain name is not considered here because it is supposed
         *          not to be wildcard when called. PropertyList is also 
	 *          supposed not to be zero-length.
         * @param name The ObjectName we want to match against the pattern.
         * @return true if <code>name</code> matches the pattern.
         **/
        public boolean matchKeys(ObjectName name) {
            if (isPropertyPattern) {
                // Every property inside pattern should exist in name 
                for (int i= keys.length -1; i >= 0 ; i--) {

                    // find value in given object name for key at current 
		    // index in receiver
                    String v = name.getKeyProperty(keys[i]);

                    // did we find a value for this key ?
                    if (v == null) return false; 
                    
                    // if this property is ok (same key, same value), 
		    // go to next
                    if (v.equals(values[i])) continue; 
                    return false;
                }
                return true;
            } else {
                if (keys.length != name.getKeyPropertyList().size()) 
		    return false;
                final String p1 = name.getCanonicalKeyPropertyListString();
                final String p2 = properties;
                // if (p1 == null) return (p2 == null);
                // if (p2 == null) return p1.equals("");
                return (p1.equals(p2));
            }
        }
    }

    /**
     * Add all the matching objects from the given hashtable in the 
     * result set for the given ObjectNamePattern
     * Do not check whether the domains match (only check for matching
     * key property lists - see <i>matchKeys()</i>)
     **/
    private final void addAllMatching(final Hashtable moiTb, final Set result,
				      final ObjectNamePattern pattern) {
	synchronized (moiTb) {
	    for (Enumeration e = moiTb.elements(); e.hasMoreElements();) {   
		final NamedObject no = (NamedObject) e.nextElement();
                final ObjectName on = no.getName();

		// if all couples (property, value) are contained 
		if (pattern.matchKeys(on)) result.add(no);
	    }
	}
    }
    
    private final void addNewDomMoi(final Object object, final String dom, 
				    final ObjectName name) {
	final Hashtable moiTb= new Hashtable();
	domainTb.put(dom, moiTb);
	moiTb.put(name.getCanonicalKeyPropertyListString(), 
		  new NamedObject(name, object));
	nbElements++;
    }
    
    /*
     * Tests whether string s is matched by pattern p.
     * Supports "?", "*" each of which may be escaped with "\";
     * Not yet supported: internationalization; "\" inside brackets.<P>
     * Wildcard matching routine by Karl Heuer.  Public Domain.<P>
     */  
    private static boolean wildmatch(char[] s, char[] p, int si, int pi) {
        char c;
	// Be careful: this is dangerous: it works because wildmatch
	// is protected by a synchronized block on domainTb
        _slen = s.length;
        _plen = p.length;
	// end of comment.

        while (pi < _plen) {            // While still string
            c = p[pi++];
            if (c == '?') {
                if (++si > _slen) return false;
            } else if (c == '*') {        // Wildcard
                if (pi >= _plen) return true;
                do {
                    if (wildmatch(s,p,si,pi)) return true;
                } while (++si < _slen);
                return false;
            } else {
                if (si >= _slen || c != s[si++]) return false;
            }
        }
        return (si == _slen);
    }

    /**
     * Retrieves the named object contained in repository
     * from the given objectname.
     */
    private NamedObject retrieveNamedObject(ObjectName name) {

        // No patterns inside reposit
	if (name.isPattern() == true) return null;

	// Extract the domain name.       
	String dom= name.getDomain().intern();

        // Default domain case
	if (dom.length() == 0) {
            dom = domain;
        }

        Object tmp_object = domainTb.get(dom);
	if (tmp_object == null) {
            return null; // No domain containing registered object names
        }

        // If name exists in repository, we will get it now 
	Hashtable moiTb= (Hashtable) tmp_object;
            
        Object o = moiTb.get(name.getCanonicalKeyPropertyListString());
        if (o != null ) {
            return (NamedObject) o;
        }
        else return null;
    }

    // Private methods <=============================================

    // Protected methods --------------------------------------------->
    // Protected methods <=============================================


    // Public methods --------------------------------------------->

    /**
     * Construct a new repository with the given default domain.
     *
     */
    public RepositorySupport(String domain) {
	domainTb= new Hashtable(5);
        
	if (domain != null && domain.length() != 0) 
	    this.domain = domain;
	else 
	    this.domain = ServiceName.DOMAIN;

        // Creates an new hastable for the default domain
	domainTb.put(this.domain.intern(), new Hashtable());

	// ------------------------------ 
	// ------------------------------
    }	
    
    /**
     * The purpose of this method is to provide a unified way to provide 
     * whatever configuration information is needed by the specific 
     * underlying implementation of the repository.
     *
     * @param configParameters An list containing the configuration 
     *        parameters needed by the specific Repository Service 
     *        implementation.
     */
    public void setConfigParameters(ArrayList configParameters) {
	return;
    } 

    /**
     * Returns the list of domains in which any MBean is currently
     * registered.
     *
     * @since.unbundled JMX RI 1.2
     */
    public String[] getDomains() {
	final ArrayList result;
	synchronized(domainTb) {
	    // Temporary list
	    result = new ArrayList(domainTb.size());

	    // Loop over all domains
	    for (Enumeration e = domainTb.keys();e.hasMoreElements();) {
		// key = domain name
		final String key = (String)e.nextElement();
		if (key == null) continue;

		// If no MBean in domain continue
		final Hashtable t = (Hashtable)domainTb.get(key);
		if (t == null || t.size()==0) continue;

		// Some MBean are registered => add to result.
		result.add(key);
	    }
	}

	// Make an array from result.
	return (String[]) result.toArray(new String[result.size()]); 
	
    }


  
    /**
     * Indicates whether or not the Repository Service supports filtering. If
     * the Repository Service does not support filtering, the MBean Server
     * will perform filtering.
     *
     * @return  true if filtering is supported, false otherwise.
     */
    public boolean isFiltering() {
	// Let the MBeanServer perform the filtering !     
	return false;
    }
    
    /**
     * Stores an MBean associated with its object name in the repository.
     *
     *@param object MBean to be stored in the repository.
     *@param name MBean object name.
     *
     */
    public void addMBean(final Object object, ObjectName name) 
	throws InstanceAlreadyExistsException {
	
	if (isTraceOn()) {
	    trace("addMBean", "name=" + name);
	}  

	// Extract the domain name. 
	String dom = name.getDomain().intern();     
	boolean to_default_domain = false;

        // Set domain to default if domain is empty and not already set
	if (dom.length() == 0) {

	     try {
                name = new ObjectName(domain + name.toString());

            } catch (MalformedObjectNameException e) {

                if (isDebugOn()) {
                    debug("addMBean",
			  "Unexpected MalformedObjectNameException");
                }
            }
	}
	
	// Do we have default domain ?
	if (dom == domain) {
	    to_default_domain = true;
	    dom = domain;
	} else {
	    to_default_domain = false;
	}

	// ------------------------------ 
	// ------------------------------

	// Validate name for an object     
	if (name.isPattern() == true) {
	    throw new RuntimeOperationsException(
	     new IllegalArgumentException("Repository: cannot add mbean for pattern name " + name.toString()));
	}

        // Domain cannot be JMImplementation if entry does not exists
        if ( !to_default_domain &&
             dom.equals("JMImplementation") &&
             domainTb.containsKey("JMImplementation")) {

		throw new RuntimeOperationsException(
		  new IllegalArgumentException(
                      "Repository: domain name cannot be JMImplementation"));
	    }	         

	// If domain not already exists, add it to the hash table
	final Hashtable moiTb= (Hashtable) domainTb.get(dom);
	if (moiTb == null) {
	    addNewDomMoi(object, dom, name);
	    return;
	}
        else {
            // Add instance if not already present
            String cstr = name.getCanonicalKeyPropertyListString();
            Object elmt= moiTb.get(cstr);
            if (elmt != null) {
                throw new InstanceAlreadyExistsException(name.toString());
            } else {
                nbElements++;
                moiTb.put(cstr, new NamedObject(name, object));
            }
        }
    } 
    
    /**
     * Checks whether an MBean of the name specified is already stored in
     * the repository.
     *
     * @param name name of the MBean to find.
     *
     * @return  true if the MBean is stored in the repository, 
     *          false otherwise.
     *
     */
    public boolean contains(ObjectName name) {
	
	if (isTraceOn()) {
	    trace("contains", "name=" + name);
	}  
	return (retrieveNamedObject(name) != null);
    }
    
    /**
     * Retrieves the MBean of the name specified from the repository. The
     * object name must match exactly.
     *
     * @param name name of the MBean to retrieve.
     *
     * @return  The retrieved MBean if it is contained in the repository, 
     *          null otherwise.
     *
     */
    public Object retrieve(ObjectName name) {
	
	// ------------------------------ 
	// ------------------------------
	if (isTraceOn()) {
	    trace("retrieve", "name=" + name);
	}

        // Calls internal retrieve method to get the named object
        NamedObject no = retrieveNamedObject(name);
        if (no == null) return null;
        else return no.getObject();

    } 

    
    /**
     * Selects and retrieves the list of MBeans whose names match the specified
     * object name pattern and which match the specified query expression 
     * (optionally).
     *
     * @param pattern The name of the MBean(s) to retrieve - may be a specific 
     * object or a name pattern allowing multiple MBeans to be selected.
     * @param query query expression to apply when selecting objects - this 
     * parameter will be ignored when the Repository Service does not 
     * support filtering.
     *
     * @return  The list of MBeans selected. There may be zero, one or many 
     *          MBeans returned in the set.
     *
     */
    public Set query(ObjectName pattern, QueryExp query) {
	
	// ------------------------------ 
	// ------------------------------
        ObjectNamePattern on_pattern = null; // intermediate Object name pattern for performance
	final HashSet result = new HashSet();
	
	// The following filter cases are considered :
	// null, "", "*:*"" :  names in all domains
	// ":*" : names in defaultDomain
	// "domain:*" : names in the specified domain
	// "domain:[key=value], *"
	
        // Surely one of the most frequent case ... query on the whole world
        ObjectName name = null;
        if (pattern == null ||
            pattern.getCanonicalName().length() == 0 ||
            pattern.equals(_WholeWordQueryObjectName))
           name = _WholeWordQueryObjectName;
        else name = pattern;
	
	// If pattern is not a pattern, retrieve this mbean !
	if (!name.isPattern()) {       
		final NamedObject no = retrieveNamedObject(name);
		if (no != null) result.add(no);
	    return result;
	}
	
	// all  names in all domains
	if  (name == _WholeWordQueryObjectName) {   
	    synchronized(domainTb) {
		for(final Enumeration e = domainTb.elements(); 
		    e.hasMoreElements();) {	
		    final Hashtable moiTb = (Hashtable) e.nextElement();
                    result.addAll(moiTb.values());
		}
	    }
	    return result;
	}

        String canonical_key_property_list_string = name.getCanonicalKeyPropertyListString();

	// all names in default domain
	//
	// DF: fix 4618986 - take into account the case where the 
	//     property list is not empty.
	//
	if (name.getDomain().length() == 0) {
	    final Hashtable moiTb = (Hashtable) domainTb.get(domain);
	    if  (canonical_key_property_list_string.length() == 0) {
                result.addAll(moiTb.values());
	    } else {
                if (on_pattern == null) 
		    on_pattern = new ObjectNamePattern(name);
		addAllMatching(moiTb,result,on_pattern);
	    }
	    return result;
	}
	
	// Pattern matching in the domain name (*, ?)
        synchronized (domainTb) {
            char[] dom2Match = name.getDomain().toCharArray();
            String nextDomain;
            char [] theDom;
            for (final Enumeration enumi = domainTb.keys(); enumi.hasMoreElements();) {
                nextDomain = (String) enumi.nextElement();
                theDom = nextDomain.toCharArray();

                if (wildmatch(theDom, dom2Match, 0, 0)) {
                    final Hashtable moiTb = 
			(Hashtable) domainTb.get(nextDomain);

                    if (canonical_key_property_list_string.length() == 0)
                        result.addAll(moiTb.values());
                    else {
                        if (on_pattern == null) 
			    on_pattern = new ObjectNamePattern(name);
                        addAllMatching(moiTb,result,on_pattern);
                    }
                }
            }
        }
	return result;
    }
    
    /**
     * Removes an MBean from the repository.
     *
     * @param name name of the MBean to remove.
     *
     * @exception InstanceNotFoundException The MBean does not exist in 
     *            the repository.
     */
    public void remove(final ObjectName name) 
	throws InstanceNotFoundException {

        // Debugging stuff
	if (isTraceOn()) {
	    trace("remove", "name=" + name);
	}  

	// Extract domain name.       
	String dom= name.getDomain().intern();

        // Default domain case
	if (dom.length() == 0) dom = domain;

        // Find the domain subtable
        Object tmp_object =  domainTb.get(dom);
	if (tmp_object == null) {
	    throw new InstanceNotFoundException(name.toString());
	}

        // Remove the corresponding element
	Hashtable moiTb= (Hashtable) tmp_object;
	if (moiTb.remove(name.getCanonicalKeyPropertyListString()) == null) {
	    throw new InstanceNotFoundException(name.toString());
	}

        // We removed it !
	nbElements--;
        
        // No more object for this domain, we remove this domain hashtable
        if (moiTb.isEmpty()) {
            domainTb.remove(dom);

            // set a new default domain table (always present)
            // need to reinstantiate a hashtable because of possible
            // big buckets array size inside table, never cleared, 
	    // thus the new !
            if (dom == domain) 
		domainTb.put(domain, new Hashtable());
        }
    } 
    
    /**
     * Gets the number of MBeans stored in the repository.
     *
     * @return  Number of MBeans.
     */
    
    public Integer getCount() {
	return new Integer(nbElements);
    }
    
    /**
     * Gets the name of the domain currently used by default in the 
     * repository.
     *
     * @return  A string giving the name of the default domain name.
     */
    public  String getDefaultDomain() {
	return domain;
    }

}




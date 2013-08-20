/*
 * @(#)MBeanFeatureInfo.java	1.22 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management;


/**
 * <p>Provides general information for an MBean descriptor object.
 * The feature described can be an attribute, an operation, a
 * parameter, or a notification.  Instances of this class are
 * immutable.  Subclasses may be mutable but this is not
 * recommended.</p>
 *
 * @since 1.5
 */
public class MBeanFeatureInfo implements java.io.Serializable  { 
     

    /* Serial version */
    static final long serialVersionUID = 3952882688968447265L;

    /**
     * The name of the feature.  It is recommended that subclasses call
     * {@link #getName} rather than reading this field, and that they
     * not change it.
     *
     * @serial The name of the feature.  
     */
    protected String name;
    
    /**
     * The human-readable description of the feature.  It is
     * recommended that subclasses call {@link #getDescription} rather
     * than reading this field, and that they not change it.
     *
     * @serial The human-readable description of the feature.
     */
    protected String description;
    
    
    /**
     * Constructs an <CODE>MBeanFeatureInfo</CODE> object.
     *
     * @param name The name of the feature.
     * @param description A human readable description of the feature. 
     */    
    public MBeanFeatureInfo(String name, String description)
	    throws IllegalArgumentException {
	this.name = name;    
	this.description = description;
    }


    /**
     * Returns the name of the feature.  
     *
     * @return the name of the feature.
     */
    public String getName() {
	return name;
    }
    
    /**
     * Returns the human-readable description of the feature.
     *
     * @return the human-readable description of the feature.
     */
    public String getDescription() {
	return description;
    }  

    /**
     * Compare this MBeanFeatureInfo to another.
     *
     * @param o the object to compare to.
     *
     * @return true iff <code>o</code> is an MBeanFeatureInfo such
     * that its {@link #getName()} and {@link #getDescription()}
     * values are equal (not necessarily identical) to those of this
     * MBeanFeatureInfo.
     */
    public boolean equals(Object o) {
	if (o == this)
	    return true;
	if (!(o instanceof MBeanFeatureInfo))
	    return false;
	MBeanFeatureInfo p = (MBeanFeatureInfo) o;
	return (p.getName().equals(getName()) &&
		p.getDescription().equals(getDescription()));
    }

    public int hashCode() {
	return getName().hashCode() ^ getDescription().hashCode();
    }
}

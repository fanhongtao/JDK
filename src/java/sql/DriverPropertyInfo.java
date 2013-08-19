/*
 * @(#)DriverPropertyInfo.java	1.20 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.sql;

/**
 * <p>Driver properties for making a connection. The
 * <code>DriverPropertyInfo</code> class is of interest only to advanced programmers
 * who need to interact with a Driver via the method
 * <code>getDriverProperties</code> to discover
 * and supply properties for connections.
 */

public class DriverPropertyInfo {

    /**
     * Constructs a <code>DriverPropertyInfo</code> object with a name and value;
	 * other members default to their initial values.
     *
     * @param name the name of the property
     * @param value the current value, which may be null
     */
    public DriverPropertyInfo(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * The name of the property.
     */
    public String name;

    /**
     * A brief description of the property, which may be null.
     */
    public String description = null;

    /**
     * The <code>required</code> field is <code>true</code> if a value must be 
	 * supplied for this property
     * during <code>Driver.connect</code> and <code>false</code> otherwise.
     */
    public boolean required = false;

    /**
     * The <code>value</code> field specifies the current value of 
	 * the property, based on a combination of the information
	 * supplied to the method <code>getPropertyInfo</code>, the
     * Java environment, and the driver-supplied default values.  This field
     * may be null if no value is known.
     */
    public String value = null;

    /**
     * An array of possible values if the value for the field 
	 * <code>DriverPropertyInfo.value</code> may be selected
	 * from a particular set of values; otherwise null.
     */
    public String[] choices = null;
}

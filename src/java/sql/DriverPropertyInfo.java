/*
 * @(#)DriverPropertyInfo.java	1.7 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.sql;

/**
 * <p>The DriverPropertyInfo class is only of interest to advanced programmers
 * who need to interact with a Driver via getDriverProperties to discover
 * and supply properties for connections.
 */

public class DriverPropertyInfo {

    /**
     * Constructor a DriverPropertyInfo with a name and value; other
     * members default to their initial values.
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
     * A brief description of the property.  This may be null.
     */
    public String description = null;

    /**
     * "required" is true if a value must be supplied for this property
     * during Driver.connect.  Otherwise the property is optional.
     */
    public boolean required = false;

    /**
     * "value" specifies the current value of the property, based on a
     * combination of the information supplied to getPropertyInfo, the
     * Java environment, and driver supplied default values.  This
     * may be null if no value is known.
     */
    public String value = null;

    /**
     * If the value may be selected from a particular set of values,
     * then this is an array of the possible values.  Otherwise it should
     * be null.
     */
    public String[] choices = null;
}

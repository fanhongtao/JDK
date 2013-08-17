/*
 * @(#)SetOverrideType.java	1.9 00/02/02
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package org.omg.CORBA;

/**
 * This is the mapping of a CORBA enum tagging
 * SET_OVERRIDE and ADD_OVERRIDE.
 *
 * @author OMG
 * @version 1.9 02/02/00
 * @since   JDK1.2
 */

public class SetOverrideType implements org.omg.CORBA.portable.IDLEntity {
    
    /**
     * The <code>int</code> constant for the enum value SET_OVERRIDE.
     */
    public static final int _SET_OVERRIDE = 0;

    /**
     * The <code>int</code> constant for the enum value ADD_OVERRIDE.
     */
    public static final int _ADD_OVERRIDE = 1;

    /**
     * The <code>SetOverrideType</code> constant for the enum value SET_OVERRIDE.
     */
    public static final SetOverrideType SET_OVERRIDE = new SetOverrideType(_SET_OVERRIDE);

    /**
     * The <code>SetOverrideType</code> constant for the enum value ADD_OVERRIDE.
     */
    public static final SetOverrideType ADD_OVERRIDE = new SetOverrideType(_ADD_OVERRIDE);

    /**
     * Retrieves the value of this <code>SetOverrideType</code> instance.
     *
     * @return  the <code>int</code> for this <code>SetOverrideType</code> instance.
     */
    public int value() {
        return _value;
    }

    /**
     * Converts the given <code>int</code> to the corresponding
     * <code>SetOverrideType</code> instance.
     *
     * @param  i the <code>int</code> to convert.  It must be one of
     *         the <code>int</code> constants in the class
     *         <code>SetOverrideType</code>.
     * @return  the <code>SetOverrideType</code> instance whose value
     *       matches the given <code>int</code>
     * @exception  BAD_PARAM  if the given <code>int</code> does not
     *       match the value of
     *       any <code>SetOverrideType</code> instance
     */
    public static SetOverrideType from_int(int i) throws org.omg.CORBA.BAD_PARAM
    {
        switch (i) {
	case _SET_OVERRIDE:
	    return SET_OVERRIDE;
	case _ADD_OVERRIDE:
	    return ADD_OVERRIDE;
	default:
	    throw new org.omg.CORBA.BAD_PARAM();
        }
    }

    /**
     * Constructs the <code>SetOverrideType</code> instance given a
     * <code>int</code>.
     * @param _value the value that is passed in could be one of 
     * SET_OVERRIDE or ADD_OVERRIDE
     */
    protected SetOverrideType(int _value){
        this._value = _value;
    }
    private int _value;
}

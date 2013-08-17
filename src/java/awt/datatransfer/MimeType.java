/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.datatransfer;

import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;
import java.util.Enumeration;


/**
 * A Multipurpose Internet Mail Extension (MIME) type, as defined
 * in RFC 2045 and 2046.
 *
 * THIS IS *NOT* - REPEAT *NOT* - A PUBLIC CLASS! DataFlavor IS
 * THE PUBLIC INTERFACE, AND THIS IS PROVIDED AS A ***PRIVATE***
 * (THAT IS AS IN *NOT* PUBLIC) HELPER CLASS!
 */
class MimeType implements Externalizable, Cloneable {

    /*
     * serialization support
     */

    static final long serialVersionUID = -6568722458793895906L;

    /**
     * Constructor for externalization. This constructor should not be
     * called directly by an application, since the result will be an
     * uninitialized, immutable MimeType object.
     */
    public MimeType() {
    }
    
    /**
     * Constructor that builds a MimeType from a String.
     */
    public MimeType(String rawdata) throws MimeTypeParseException {
        parse(rawdata);
    }

    /**
     * Constructor that builds a MimeType with the given primary and sub
type
     * but has an empty parameter list.
     */
    public MimeType(String primary, String sub) throws MimeTypeParseException {
	this(primary, sub, new MimeTypeParameterList());
    }

    /**
     * Constructor used to initialize MimeType, with a pre-defined 
     * and valid (or empty) parameter list.
     */

    public MimeType(String primary, String sub, MimeTypeParameterList mtpl) throws
MimeTypeParseException {
        //    check to see if primary is valid
        if(isValidToken(primary)) {
            primaryType = primary.toLowerCase();
        } else {
            throw new MimeTypeParseException("Primary type is invalid.");
        }
        
        //    check to see if sub is valid
        if(isValidToken(sub)) {
            subType = sub.toLowerCase();
        } else {
            throw new MimeTypeParseException("Sub type is invalid.");
        }
        
	try {
	    mtpl = (MimeTypeParameterList)mtpl.clone();
	} catch (CloneNotSupportedException cnse) {
	    throw new RuntimeException("failed to clone parameter list");
	}

	try {
            parameters = (MimeTypeParameterList)mtpl.clone();
	} catch (CloneNotSupportedException cnse) {
	    throw new RuntimeException("cannot clone parameter list");
	}
    }

    public int hashCode() {

	// We sum up the hash codes for all of the strings. This
	// way, the order of the strings is irrelevant
	int code = 0;
	code += primaryType.hashCode();
	code += subType.hashCode();
	code += parameters.hashCode();
	return code;
    } // hashCode()

    /**
     * MimeTypes are equals if their primary types, subtypes, and
     * parameters are all equal. No default values are taken into
     * account.
     */
    public boolean equals(Object thatObject) {
	if (!(thatObject instanceof MimeType)) {
	    return false;
	}
	MimeType that = (MimeType)thatObject;
	boolean isIt = 
	    ((this.primaryType.equals(that.primaryType)) &&
	     (this.subType.equals(that.subType)) &&
	     (this.parameters.equals(that.parameters)));
	return isIt;
    } // equals()
    
    /**
     * A routine for parsing the MIME type out of a String.
     */
    private void parse(String rawdata) throws MimeTypeParseException {
	//System.out.println("MimeType.parse("+rawdata+")");
        int slashIndex = rawdata.indexOf('/');
        int semIndex = rawdata.indexOf(';');
        if((slashIndex < 0) && (semIndex < 0)) {
            //    neither character is present, so treat it
            //    as an error
            throw new MimeTypeParseException("Unable to find a sub type.");
        } else if((slashIndex < 0) && (semIndex >= 0)) {
            //    we have a ';' (and therefore a parameter list),
            //    but now '/' indicating a sub type is present
            throw new MimeTypeParseException("Unable to find a sub type.");
        } else if((slashIndex >= 0) && (semIndex < 0)) {
            //    we have a primary and sub type but no parameter list
            primaryType = rawdata.substring(0,
slashIndex).trim().toLowerCase();
            subType = rawdata.substring(slashIndex +
1).trim().toLowerCase();
            parameters = new MimeTypeParameterList();
        } else if (slashIndex < semIndex) {
            //    we have all three items in the proper sequence
            primaryType = rawdata.substring(0,
slashIndex).trim().toLowerCase();
            subType = rawdata.substring(slashIndex + 1,
semIndex).trim().toLowerCase();
            parameters = new
MimeTypeParameterList(rawdata.substring(semIndex));
        } else {
            //    we have a ';' lexically before a '/' which means we have a primary type
            //    & a parameter list but no sub type
            throw new MimeTypeParseException("Unable to find a sub type.");
        }
        
        //    now validate the primary and sub types
        
        //    check to see if primary is valid
        if(!isValidToken(primaryType)) {
            throw new MimeTypeParseException("Primary type is invalid.");
        }
        
        //    check to see if sub is valid
        if(!isValidToken(subType)) {
            throw new MimeTypeParseException("Sub type is invalid.");
        }
    }

    /**
     * Retrieve the primary type of this object.
     */
    public String getPrimaryType() {
        return primaryType;
    }

    /**
     * Retrieve the sub type of this object.
     */
    public String getSubType() {
        return subType;
    }

    /**
     * Retrieve a copy of this object's parameter list.
     */
    public MimeTypeParameterList getParameters() {
	try {
            return (MimeTypeParameterList)parameters.clone();
	} catch (CloneNotSupportedException cnse) {
	    throw new RuntimeException("cannot clone parameter list");
	}
    }

    /**
     * Retrieve the value associated with the given name, or null if there
     * is no current association.
     */
    public String getParameter(String name) {
        return parameters.get(name);
    }

    /**
     * Set the value to be associated with the given name, replacing
     * any previous association.
     *
     * @throw IllegalArgumentException if parameter or value is illegal
     */
    public void setParameter(String name, String value) {
        parameters.set(name, value);
    }

    /**
     * Remove any value associated with the given name.
     *
     * @throw IllegalArgumentExcpetion if parameter may not be deleted
     */
    public void removeParameter(String name) {
        parameters.remove(name);
    }

    /**
     * Return the String representation of this object.
     */
    public String toString() {
        return getBaseType() + parameters.toString();
    }

    /**
     * Return a String representation of this object
     * without the parameter list.
     */
    public String getBaseType() {
        return primaryType + "/" + subType;
    }
    
    /**
     * Determine of the primary and sub type of this object is
     * the same as the what is in the given type.
     */
    public boolean match(MimeType type) {
	if (type == null)
            return false;
        return primaryType.equals(type.getPrimaryType())
                    && (subType.equals("*")
                            || type.getSubType().equals("*")
                            || (subType.equals(type.getSubType())));
    }
    
    /**
     * Determine of the primary and sub type of this object is
     * the same as the content type described in rawdata.
     */
    public boolean match(String rawdata) throws MimeTypeParseException {
        if (rawdata == null)
            return false;
        return match(new MimeType(rawdata));
    }
    
    /**
     * The object implements the writeExternal method to save its contents
     * by calling the methods of DataOutput for its primitive values or
     * calling the writeObject method of ObjectOutput for objects, strings
     * and arrays.
     * @exception IOException Includes any I/O exceptions that may occur
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(toString());
    }

    /**
     * The object implements the readExternal method to restore its
     * contents by calling the methods of DataInput for primitive
     * types and readObject for objects, strings and arrays.  The
     * readExternal method must read the values in the same sequence
     * and with the same types as were written by writeExternal.
     * @exception ClassNotFoundException If the class for an object being
     *              restored cannot be found.
     */
    public void readExternal(ObjectInput in) throws IOException,
ClassNotFoundException {
        try {
            parse(in.readUTF());
        } catch(MimeTypeParseException e) {
            throw new IOException(e.toString());
        }
    }

    /**
     * @return a clone of this object
     */

    public Object clone() throws CloneNotSupportedException {
	try {
	    return new MimeType(primaryType, subType, (MimeTypeParameterList)parameters);
	} catch (MimeTypeParseException mtpe) { // this should not occur
	    throw new CloneNotSupportedException();
	}
    }

    private String    primaryType;
    private String    subType;
    private MimeTypeParameterList parameters;
    
    //    below here be scary parsing related things

    /**
     * Determine whether or not a given character belongs to a legal token.
     */
    private static boolean isTokenChar(char c) {
        return ((c > 040) && (c < 0177)) && (TSPECIALS.indexOf(c) < 0);
    }
    
    /**
     * Determine whether or not a given string is a legal token.
     */
    private boolean isValidToken(String s) {
        int len = s.length();
        if(len > 0) {
            for (int i = 0; i < len; ++i) {
                char c = s.charAt(i);
                if (!isTokenChar(c)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * A string that holds all the special chars.
     */

    private static final String TSPECIALS = "()<>@,;:\\\"/[]?=";
    
} // class MimeType

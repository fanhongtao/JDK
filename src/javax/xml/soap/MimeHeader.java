/*
 * $Id: MimeHeader.java,v 1.2 2004/04/02 01:24:17 ofung Exp $
 * $Revision: 1.2 $
 * $Date: 2004/04/02 01:24:17 $
 */

/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.xml.soap;


/**
 * An object that stores a MIME header name and its value. One or more
 * <code>MimeHeader</code> objects may be contained in a <code>MimeHeaders</code>
 * object.  
 *
 * @see MimeHeaders
 */
public class MimeHeader {

   private String name;
   private String value;

   /**
    * Constructs a <code>MimeHeader</code> object initialized with the given
    * name and value.
    *
    * @param name a <code>String</code> giving the name of the header
    * @param value a <code>String</code> giving the value of the header
    */
    public MimeHeader(String name, String value) {
	this.name = name;
	this.value = value;
    }

    /**
     * Returns the name of this <code>MimeHeader</code> object.
     *
     * @return the name of the header as a <code>String</code>
     */
    public String getName() {
	return name;
    }

    /**
     * Returns the value of this <code>MimeHeader</code> object.
     *
     * @return 	the value of the header as a <code>String</code>
     */
    public String getValue() {
	return value;
    }
}

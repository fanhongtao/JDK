/*
 * $Id: MimeHeader.java,v 1.3 2005/04/05 20:49:48 mk125090 Exp $
 * $Revision: 1.3 $
 * $Date: 2005/04/05 20:49:48 $
 */

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
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

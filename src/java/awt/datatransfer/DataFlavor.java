/*
 * @(#)DataFlavor.java	1.4 97/03/10
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */

package java.awt.datatransfer;

import java.io.InputStream;

/**
 * Each instance represents the opaque concept of a data format as would
 * appear on a clipboard, during drag and drop, or in a file system.
 *
 * @version 	1.4, 03/10/97
 * @author	Blake Sullivan
 */
public class DataFlavor {

   /**
    * The DataFlavor representing a Java Unicode String class, where:
    * <p>
    * representationClass = java.lang.String<br>
    * mimeType            = "application/x-java-serialized-object"        
    * <p> 
    */
    public static DataFlavor stringFlavor;

   /**
    * The DataFlavor representing plain text with unicode encoding, where:
    * <p>
    * representationClass = InputStream<br>
    * mimeType            = "text/plain; charset=unicode"        
    * <p> 
    */
    public static DataFlavor plainTextFlavor; 
		
    static final String serializedObjectMimeType = "application/x-java-serialized-object";
    static Class ioInputStreamClass;

    static {
	try {
	    stringFlavor = new DataFlavor(Class.forName("java.lang.String"), "Unicode String");
	    plainTextFlavor = new DataFlavor("text/plain; charset=unicode", "Plain Text");
	    ioInputStreamClass = Class.forName("java.io.InputStream");
	} catch (ClassNotFoundException e) {

	}
    } 

    /* placeholder for caching any platform-specific data for flavor */
    int atom;      
  
   /**
    * Mime type for this DataFlavor.  (See RFC 1521 for an explanation
    * of Mime types)  The type name is stored internally in the following
    * cannonical order to make comparisons simpler
    *   1. type, subtype, and parameter names are converted to lowercase
    *
    *   2. parameters are ordered by parameter name
    *
    *   3. character set parameter names are converted to lowercase (they
    *      are the exception to the rule that parameter names should be
    *      case sensitive
    *
    *   4. White space is compressed
    *
    */
    private String mimeType;
    
    /** Human-presentable name for this DataFlavor. Localizable. **/
    private String humanPresentableName;  
  
    /** Java class of objects this DataFlavor represents **/
    private Class representationClass;
  
   /**
    * Construct a DataFlavor that represents a Java class
    * <p>
    * The returned DataFlavor will have the following characteristics
    * <p>
    * representationClass = representationClass<br>
    * mimeType            = application/x-java-serialized-object        
    * <p>
    * @param representationClass the class used to transfer data in this flavor
    * @param humanPresentableName the human-readible string used to identify this flavor
    */
    public DataFlavor(Class representationClass, String humanPresentableName) {
	this.mimeType = serializedObjectMimeType;
	this.representationClass = representationClass;
      	this.humanPresentableName = humanPresentableName;
    }

   /**
    * Construct a DataFlavor that represents a MimeType
    * <p>
    * The returned DataFlavor will have the following characteristics:
    * <p>
    * If the mimeType is
    * "application/x-java-serialized-object; class=<representation class>",
    * the result is the same as calling
    * new DataFlavor(Class:forName(<representation class>) as above
    * <p>
    * otherwise:
    * <p>
    * representationClass = InputStream<br>
    * mimeType            = mimeType         
    * <p>
    * @param mimeType the string used to identify the MIME type for this flavor
    * @param humanPresentableName the human-readible string used to identify this flavor
    */
    public DataFlavor(String mimeType, String humanPresentableName) {
	this.mimeType = mimeType;
	this.representationClass = ioInputStreamClass;
      	this.humanPresentableName = humanPresentableName;
    }       
  
   /**
    * Returns the MIME type string for this DataFlavor
    */ 
    public String getMimeType() {
    	return mimeType;
    }

   /**
    *  Returns the Class which objects supporting this DataFlavor
    *  will return when this DataFlavor is requested.
    */
    public Class getRepresentationClass() {
    	return representationClass;
    }
  
   /**
    * Returns the human presentable name for the data foramt that this
    * DataFlavor represents.  This name would be localized for different
    * countries
    */
    public String getHumanPresentableName() {
    	return humanPresentableName;
    }
  
   /**
    * Sets the human presentable name for the data format that this
    * DataFlavor represents. This name would be localized for different
    * countries
    */
    public void setHumanPresentableName(String humanPresentableName) {
    	humanPresentableName = humanPresentableName;
    }

    public boolean equals(DataFlavor dataFlavor) {
	return (isMimeTypeEqual(dataFlavor) &&
	 	dataFlavor.getRepresentationClass() == representationClass);
    }
  
    /**
     * Is the string representation of the MIME type passed in equivalent
     * to the MIME type of this DataFlavor.  This may involve adding default
     * attributes for some MIME types (like adding charset=US-ASCII to
     * text/plain MIME types that have no charset parameter specified)
     */
    public boolean isMimeTypeEqual(String mimeType) {
	// This is too simplistic
	return mimeType.equals(this.mimeType);
    }
  
    /**
     * Convenience function equivalent to calling:
     * isMimeTypeEqual(dataFlavor.getMimeType());
     */
    public final boolean isMimeTypeEqual(DataFlavor dataFlavor) {
	return isMimeTypeEqual(dataFlavor.getMimeType());
    }
    
   /**
    * Called on DataFlavor for every MIME Type parameter to allow DataFlavor
    * subclasses to handle special parameters like the text/plain charset
    * parameters, whose values are case insensitive.  (MIME type parameter
    * values are supposed to be case sensitive.
    * <p>
    * This method is called for each parameter name/value pair and should
    * return the normalized representation of the parameterValue
    */
    protected String normalizeMimeTypeParameter(String parameterName, String parameterValue) {
	return parameterName+"="+parameterValue;	
    }
  
   /**
    * Called for each MIME type string to give DataFlavor subtypes the
    * opportunity to change how the normalization of MIME types is accomplished.
    * One possible use would be to add default parameter/value pairs in cases
    * where none are present in the MIME type string passed in
    */
    protected String normalizeMimeType(String mimeType) {
	return mimeType;	
    }
}


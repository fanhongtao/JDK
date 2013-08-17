/*
 * @(#)DataFlavor.java	1.34 01/01/23
 *
 * Copyright 1996-2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 * 
 */

package java.awt.datatransfer;

import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;


/**
 * Each instance represents the opaque concept of a data format as would
 * appear on a clipboard, during drag and drop, or in a file system.
 *
 * @version     1, 34
 * @author	Blake Sullivan
 * @author	Laurence P. G. Cable
 */

public class DataFlavor implements Externalizable, Cloneable {
    
    static final long serialVersionUID = 8367026044764648243L;
    
    static final Class ioInputStreamClass = java.io.InputStream.class;
    
    /**
     * tried to load a class from: the bootstrap loader, the system loader,
     * the context loader (if one is present) and finally the loader specified
     *
     * @param fallback the fallback loader
     *
     * @throws ClassNotFoundException
     */
    protected final static Class tryToLoadClass(String className,
						ClassLoader fallback) 
        throws ClassNotFoundException
    {
	ClassLoader systemClassLoader = (ClassLoader)
	    java.security.AccessController.doPrivileged(
                new java.security.PrivilegedAction() {
		    public Object run() {
			ClassLoader cl = Thread.currentThread().
			    getContextClassLoader();
			return (cl != null)
			    ? cl
			    : ClassLoader.getSystemClassLoader();
		    }
		    });
	
	try {
	    return Class.forName(className, true, systemClassLoader);
	} catch (ClassNotFoundException e2) {
	    if (fallback != null) {
		return Class.forName(className, true, fallback);
	    } else {
		throw new ClassNotFoundException(className);
	    }
	}
    }
    
    /*
     * private initializer
     */
    
    static private DataFlavor createConstant(Class rc, String prn) {
	try {
	    return new DataFlavor(rc, prn);
	} catch (Exception e) {
	    return null;
	}
    }
    
    /*
     * private initializer
     */
    
    static private DataFlavor createConstant(String mt, String prn) {
	try {
	    return new DataFlavor(mt, prn);
	} catch (Exception e) {
	    return null;
	}
    }
    
    /**
     * The DataFlavor representing a Java Unicode String class, where:
     * <p>
     * representationClass = java.lang.String<br>
     * mimeType            = "application/x-java-serialized-object"        
     * <p> 
     */
    
    public static final DataFlavor stringFlavor = createConstant(java.lang.String.class, "Unicode String");
    
    /**
     * The DataFlavor representing plain text with unicode encoding, where:
     * <p>
     * representationClass = InputStream<br>
     * mimeType            = "text/plain; charset=unicode"        
     * <p> 
     */
    
    public static final DataFlavor plainTextFlavor = createConstant("text/plain; charset=unicode; class=java.io.InputStream", "Plain Text"); 
    
    
    /**
     * a MIME Content-Type of application/x-java-serialized-object represents
     * a graph of Java object(s) that have been made persistent.
     *
     * The representation class associated with this DataFlavor identifies
     * the Java type of an object returned as a reference from an invocation
     * java.awt.datatransfer.getTransferData().
     *
     */
    
    public static final String javaSerializedObjectMimeType = "application/x-java-serialized-object";
    
    /**
     * To transfer a list of files to/from Java (and the underlying
     * platform) a DataFlavor of this type/subtype and representation class
     * of java.util.List is used.
     *
     * Each element of the list is required/guaranteed to be of type
     * java.io.File.
     */
    
    public static final DataFlavor javaFileListFlavor = createConstant("application/x-java-file-list;class=java.util.List", null);

    /**
     * to transfer a reference to an arbitrary Java object reference that
     * has no associated MIME Content-type, across a Transferable interface
     * WITHIN THE SAME JVM, a DataFlavor with this type/subtype is used,
     * with a representationClass equal to the type of the class/interface
     * being passed across the Transferble.
     *
     * The object reference returned from Transferable.getTransferData()
     * for a DataFlavor with this MIME Content-Type is required to be
     * an instanceof the representation Class of the DataFlavor.
     */
 
    public static final String javaJVMLocalObjectMimeType = "application/x-java-jvm-local-objectref";
    
    /**
     * In order to pass a live link to a Remote object via a Drag and Drop
     * ACTION_LINK operation a Mime Content Type of application/x-java-remote-object
     * should be used, where the representation class of the DataFlavor 
     * represents the type of the Remote interface to be transferred.
     */

    public static final String javaRemoteObjectMimeType = "application/x-java-remote-object";

    /**
     * Construct a fully specified DataFlavor
     */
    
    private DataFlavor(String primaryType, String subType, MimeTypeParameterList params, Class representationClass, String humanPresentableName) {
	super();
	
	if (params == null) params = new MimeTypeParameterList();
	
	params.set("class", representationClass.getName());
	
	if (humanPresentableName == null) {
	    humanPresentableName = (String)params.get("humanPresentableName");
	    
	    if (humanPresentableName == null)
	        humanPresentableName = primaryType + "/" + subType;
	}
	
	try {
	    mimeType = new MimeType(primaryType, subType, params);
	} catch (MimeTypeParseException mtpe) {
	    throw new IllegalArgumentException("MimeType Parse Exception: " + mtpe.getMessage());
	}
	
	this.representationClass  = representationClass;
	this.humanPresentableName = humanPresentableName;
	
	mimeType.removeParameter("humanPresentableName");
    }
    
    /**
     * Construct a DataFlavor that represents a Java class
     * <p>
     * The returned DataFlavor will have the following characteristics
     * <p>
     * representationClass = representationClass<br>
     * mimeType            = application/x-java-serialized-object        
     * <p>
     * @param representationClass the class used to transfer data in this flavor
     * @param humanPresentableName the human-readable string used to identify 
     *                 this flavor.
     *                 If this parameter is null then the value of the 
     *                 the MIME Content Type is used.
     */
    public DataFlavor(Class representationClass, String humanPresentableName) {
        this("application", "x-java-serialized-object", null, representationClass, humanPresentableName);
    }

    /**
     * Construct a DataFlavor that represents a MimeType
     * <p>
     * The returned DataFlavor will have the following characteristics:
     * <p>
     * If the mimeType is
     * "application/x-java-serialized-object; class=&lt;representation class&gt;",
     * the result is the same as calling
     * new DataFlavor(Class:forName(&lt;representation class&gt;) as above
     * <p>
     * otherwise:
     * <p>
     * representationClass = InputStream<br>
     * mimeType            = mimeType         
     * <p>
     * @param mimeType the string used to identify the MIME type for this flavor.
     *                 If the the mimeType does not specify a
     *                 "class=" parameter, or if the class is not successfully
     *                 loaded, then an IllegalArgumentException is thrown.
     * @param humanPresentableName the human-readable string used to identify 
     *                 this flavor.
     *                 If this parameter is null then the value of the 
     *                 the MIME Content Type is used.
     */
    public DataFlavor(String mimeType, String humanPresentableName) {
	super();
	
	try {
	    initialize(mimeType, humanPresentableName, this.getClass().getClassLoader());
	} catch (MimeTypeParseException mtpe) {
	    throw new IllegalArgumentException("failed to parse:" + mimeType);
	} catch (ClassNotFoundException cnfe) {
	    throw new IllegalArgumentException("cant find specified class: " + cnfe.getMessage());
	}
    }
    
    /**
     * Construct a DataFlavor that represents a MimeType
     * <p>
     * The returned DataFlavor will have the following characteristics:
     * <p>
     * If the mimeType is
     * "application/x-java-serialized-object; class=&lt;representation class&gt;",
     * the result is the same as calling
     * new DataFlavor(Class:forName(&lt;representation class&gt;) as above
     * <p>
     * otherwise:
     * <p>
     * representationClass = InputStream<br>
     * mimeType            = mimeType         
     * <p>
     * @param mimeType the string used to identify the MIME type for this flavor
     * @param humanPresentableName the human-readible string used to identify this flavor
     */
    
    public DataFlavor(String mimeType, String humanPresentableName, ClassLoader classLoader) throws ClassNotFoundException {
	super();
	try {
	    initialize(mimeType, humanPresentableName, classLoader);
	} catch (MimeTypeParseException mtpe) {
	    throw new IllegalArgumentException("failed to parse:" + mimeType);
	}
    }       
    
    /**
     * Construct a DataFlavor from a Mime Type string.
     * The string must specify a "class=<fully specified Java class name>"
     * parameter in order to succeed in constructing a DataFlavor.
     * 
     * @param mimeType the string used to identify the MIME type for this flavor.
     *                 If the the mimeType does not specify a
     *                 "class=" parameter, or if the class is not successfully
     *                 loaded, then an IllegalArgumentException is thrown.
     */
    
    public DataFlavor(String mimeType) throws ClassNotFoundException {
	super();
	try {
	    initialize(mimeType, null, this.getClass().getClassLoader());
	} catch (MimeTypeParseException mtpe) {
	    throw new IllegalArgumentException("failed to parse:" + mimeType);
	}
    }

   /**
    * common initialization code called from various constructors.
    *
    * @param mimeType The MIME Content Type (must have a class= param)
    * @param humanPresentableName The human Presentable Name or null
    * @param classLoader The fallback class loader to resolve against
    *
    * @throws MimeTypeParseException
    * @throws ClassNotFoundException
    *
    * @see tryToLoadClass
    */
    private void initialize(String mimeType, String humanPresentableName, ClassLoader classLoader) throws MimeTypeParseException, ClassNotFoundException {
	
        this.mimeType = new MimeType(mimeType); // throws
	
	String rcn = getParameter("class");
	
	if (rcn == null) {
            if ("application/x-java-serialized-object".equals(this.mimeType.getBaseType()))
		    
                throw new IllegalArgumentException("no representation class specified for:" + mimeType);
            else
                representationClass = java.io.InputStream.class; // default
	} else { // got a class name
	    representationClass = DataFlavor.tryToLoadClass(rcn, classLoader);
	}
	
	this.mimeType.setParameter("class", representationClass.getName());
	
	if (humanPresentableName == null) {
	    humanPresentableName = this.mimeType.getParameter("humanPresentableName");
	    if (humanPresentableName == null) 
      		humanPresentableName = this.mimeType.getPrimaryType() + "/" + this.mimeType.getSubType();
	}
	
	this.humanPresentableName = humanPresentableName; // set it.
	
	this.mimeType.removeParameter("humanPresentableName"); // just in case
    }
    
    /**
     * used by clone implementation
     */
    
    private DataFlavor(MimeType mt, Class rc, String hrn, int a) {
	super();
	
	mimeType             = mt;
	representationClass  = rc;
	humanPresentableName = hrn;
	atom		     = a;
    }
    
    public String toString() {
        String string = getClass().getName();
        string += "["+paramString()+"]";
        return string;
    }
    
    private String paramString() {
        String params = "";
        params += "representationclass=";
        if (representationClass == null) {
           params += "null";
        } else {
           params += representationClass.getName();
        }
	params += ",mimetype=["+getMimeType()+"]";
        return params;
    }
    
    /**
     * Returns the MIME type string for this DataFlavor
     */ 
    public String getMimeType() {
    	return mimeType.toString();
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
     * @return the primary MIME type of this DataFlavor
     */
    
    public String getPrimaryType() { return mimeType.getPrimaryType(); }
    
    /**
     * @return the Sub MIME type of this DataFlavor
     */

    public String getSubType() { return mimeType.getSubType(); }
    
    /**
     * @return the value of the name parameter
     */
    
    public String getParameter(String paramName) {
	return paramName.equals("humanPresentableName") ? humanPresentableName : mimeType.getParameter(paramName);
    }
    
    /**
     * Sets the human presentable name for the data format that this
     * DataFlavor represents. This name would be localized for different
     * countries
     */
    
    public void setHumanPresentableName(String humanPresentableName) {
    	this.humanPresentableName = humanPresentableName;
    }
    
    /**
     * If the object is an instance of DataFlavor, representationClass will be
     * compared while it will not be if the object is a String.
     *
     * @return if the objects are equal
     */
    
    public boolean equals(Object o) {
	return ((o instanceof DataFlavor) && equals((DataFlavor)o)) ||
	    ((o instanceof String)     && equals((String)o));
    }
    
    /**
     * Compare this DataFlavor against another DataFlavor object
     * both mimeType and representationClass are considered
     *
     * @return if the DataFlavors represent the same type.
     */
    
    public boolean equals(DataFlavor dataFlavor) {
	if (dataFlavor == null)
	    return false;
	if (representationClass == null) {
	    if (dataFlavor.getRepresentationClass() != null) {
		return false;
             } else if (mimeType == null) {
                 return dataFlavor.mimeType == null;
             } else return mimeType.match(dataFlavor.mimeType);
	} else if (mimeType == null) {
	    return representationClass.equals(dataFlavor.getRepresentationClass()) 
		&& dataFlavor.mimeType == null;
	}
	return mimeType.match(dataFlavor.mimeType) 
	    && representationClass.equals(dataFlavor.getRepresentationClass());
    }
    
    /**
     * Compare only the mineType against the passed in String
     * and representationClass is not considered in the comparison.
     * If representationClass needs to be compared, then
     *     equals(new DataFlavor(s))
     * may be used.
     *
     * @return if the String (MimeType) is equal
     */
    
    public boolean equals(String s) {
   	if (s == null || mimeType == null)
	    return false;
	return isMimeTypeEqual(s);
    }
    
    /**
     * Is the string representation of the MIME type passed in equivalent
     * to the MIME type of this DataFlavor? Parameters are not incuded in
     * the comparison. This may involve adding default
     * attributes for some MIME types (like adding charset=US-ASCII to
     * text/plain MIME types that have no charset parameter specified)
     */
    
    public boolean isMimeTypeEqual(String mimeType) {
	try {
	    return this.mimeType.match(new MimeType(mimeType));
	} catch (MimeTypeParseException mtpe) {
	    return false; 
	}
    }
    
    /**
     * Compare the mimeType of two DataFlavor objects
     * no parameters are considered
     *
     * @return if the MimeTypes are equal
     */
    
    public final boolean isMimeTypeEqual(DataFlavor dataFlavor) {
	return isMimeTypeEqual(dataFlavor.mimeType);
    }
    
    /**
     * Compare the mimeType of two DataFlavor objects
     * no parameters are considered
     *
     * @return if the MimeTypes are equal
     */
    
    private boolean isMimeTypeEqual(MimeType mtype) {
	return mimeType.match(mtype);
    }

   /**
    * does the DataFlavor represent a serialized object?
    */

    public boolean isMimeTypeSerializedObject() {
        if (mimeType == null)
            return false;
	return isMimeTypeEqual(javaSerializedObjectMimeType);
    }

   /**
    * does the DataFlavor represent a java.io.InputStream
    */

    public boolean isRepresentationClassInputStream() {
	return ioInputStreamClass.isAssignableFrom(representationClass);
    }

   /**
    * @return true if the representation class can be serialized
    */

    public boolean isRepresentationClassSerializable() {
	return java.io.Serializable.class.isAssignableFrom(representationClass);
    }

   /**
    * @return true if the representation class is Remote
    */

    public boolean isRepresentationClassRemote() {
	return java.rmi.Remote.class.isAssignableFrom(representationClass);
    }

   /**
    * @return if the DataFlavor specified represents a Serialized Object
    */

    public boolean isFlavorSerializedObjectType() {
	return isRepresentationClassSerializable() && equals(javaSerializedObjectMimeType);
    }

    /**
     * @return if the DataFlavor specified represents a Remote Object
     */
  
    public boolean isFlavorRemoteObjectType() {
	return isRepresentationClassRemote() 
	    && isRepresentationClassSerializable() 
	    && equals(javaRemoteObjectMimeType);
    }

  
   /**
    * @return if flavor specified represents a List of File objects
    */

   public boolean isFlavorJavaFileListType() {
        if (mimeType == null || representationClass == null)
            return false;
	return java.util.List.class.isAssignableFrom(representationClass) &&
               mimeType.match(javaFileListFlavor.mimeType);

   }

   /**
    * Serialize this DataFlavor
    */

   public synchronized void writeExternal(ObjectOutput os) throws IOException {
	mimeType.setParameter("humanPresentableName", humanPresentableName);
	os.writeObject(mimeType);
	mimeType.removeParameter("humanPresentableName");
   }

   /**
    * restore this DataFlavor from an Serialized state
    */

   public synchronized void readExternal(ObjectInput is) throws IOException , ClassNotFoundException {
	mimeType = (MimeType)is.readObject();

	humanPresentableName = mimeType.getParameter("humanPresentableName");

	mimeType.removeParameter("humanPresentableName");

	String rcn = mimeType.getParameter("class");

	if (rcn == null) throw new IOException("no class parameter specified in: " + mimeType);

	DataFlavor.tryToLoadClass(rcn, this.getClass().getClassLoader());
   }

   public DataFlavor() { super(); } // this is here for serialization only.

   /**
    * @return a clone of this DataFlavor
    */

   public Object clone() throws CloneNotSupportedException {
	if (mimeType == null) 
		return new DataFlavor((MimeType)null, representationClass, humanPresentableName, atom);
	return new DataFlavor((MimeType)mimeType.clone(), representationClass, humanPresentableName, atom);
   }

   /**
    * Called on DataFlavor for every MIME Type parameter to allow DataFlavor
    * subclasses to handle special parameters like the text/plain charset
    * parameters, whose values are case insensitive.  (MIME type parameter
    * values are supposed to be case sensitive.
    * <p>
    * This method is called for each parameter name/value pair and should
    * return the normalized representation of the parameterValue
    *
    * This method is never invoked by this implementation from 1.1 onwards
    *
    * @deprecated
    */
    protected String normalizeMimeTypeParameter(String parameterName, String parameterValue) {
	return parameterName+"="+parameterValue;	
    }
  
   /**
    * Called for each MIME type string to give DataFlavor subtypes the
    * opportunity to change how the normalization of MIME types is accomplished.
    * One possible use would be to add default parameter/value pairs in cases
    * where none are present in the MIME type string passed in
    *
    * This method is never invoked by this implementation from 1.1 onwards
    *
    * @deprecated
    */
    protected String normalizeMimeType(String mimeType) {
	return mimeType;	
    }

    /*
     * fields
     */

    /* placeholder for caching any platform-specific data for flavor */

    transient int	atom;      
  
    /* Mime Type of DataFlavor */

    MimeType		mimeType;

    /** Human-presentable name for this DataFlavor. Localizable. **/

    private String	humanPresentableName;  
  
    /** Java class of objects this DataFlavor represents **/

    private Class	representationClass;
}

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.datatransfer;

import java.awt.Toolkit;
import java.io.*;
import java.util.*;
import sun.awt.SunToolkit;
import sun.awt.DataTransferer;

/**
 * Each instance represents the opaque concept of a data format as would
 * appear on a clipboard, during drag and drop, or in a file system.
 * <p>
 * <code>DataFlavor</code> objects are constant and never change once
 * instantiated.
 * </p>
 *
 * @version     1.54, 02/06/02
 * @author      Blake Sullivan
 * @author      Laurence P. G. Cable
 * @author      Jeff Dunn
 */
public class DataFlavor implements Externalizable, Cloneable {
    
    private static final long serialVersionUID = 8367026044764648243L;
    private static final Class ioInputStreamClass = java.io.InputStream.class;
    
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
     * This DataFlavor has been <b>deprecated</b> because (1) Its
     * representation is an InputStream, an 8-bit based representation,
     * while Unicode is a 16-bit character set; and (2) The charset "unicode"
     * is not well-defined. "unicode" implies a particular platform's
     * implementation of Unicode, not a cross-platform implementation.
     *
     * @deprecated as of 1.3. Use <code>DataFlavor.getReaderForText(
     *             Transferable)</code> instead of <code>Transferable.
     *             getTransferData(DataFlavor.plainTextFlavor)</code>.
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
     * Constructs a new DataFlavor.  This constructor is provided only for 
     * the purpose of supporting the Externalizable interface.  It is not 
     * intended for public (client) use.
     *
     * @since 1.2
     */
    public DataFlavor() { 
        super(); 
    } 

    /**
     * Cloning constructor. Package-private.
     */
    DataFlavor(DataFlavor that) {
        this.mimeType = null;
        if (that.mimeType != null) {
            try {
                this.mimeType = (MimeType)that.mimeType.clone();
            } catch (CloneNotSupportedException e) {
            }
        }
        this.representationClass = that.representationClass;
        this.humanPresentableName = that.humanPresentableName;
        this.atom = that.atom;
    } // DataFlavor()

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
     * The string can specify a "class=<fully specified Java class name>"
     * parameter to create a DataFlavor with the desired representation 
     * class. If the string does not contain "class=" parameter,
     * java.io.InputStream is used as default.
     * 
     * @param mimeType the string used to identify the MIME type for this flavor.
     *                 If the class specified by "class=" parameter is not
     *                 successfully loaded, then an IllegalArgumentException
     *                 is thrown.
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
        atom                 = a;
    }

    /**
     * String representation of this <code>DataFlavor</code>  
     * and its parameters. The result String contains name of   
     * <code>DataFlavor</code> class, representation class
     * and Mime type of this Flavor.
     *
     * @return  string representation of this <code>DataFlavor</code>
     */
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
        params += ";mimetype=";
        if (mimeType == null) {
            params += "null";
        } else {
            params += mimeType.getBaseType();
        }
        return params;
    }

    public static final DataFlavor getTextPlainUnicodeFlavor() {
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	SunToolkit sunToolkit = null;
	String encoding = null;
	if (toolkit instanceof SunToolkit) {
	    encoding = ((SunToolkit)toolkit).getDefaultUnicodeEncoding();
	}
	return new DataFlavor(
	    "text/plain; charset="+encoding
	    +"; class=java.io.InputStream", "Plain Text"); 
    } // getTextPlainUnicodeFlavor()

    /**
     * @return the best (highest fidelity) flavor in an encoding supported
     *         by the JDK, or null if none can be found.
     */
    public static final DataFlavor selectBestTextFlavor(
					DataFlavor[] availableFlavors) {
	if (availableFlavors == null) {
	    return null;
	}
	DataFlavor selectedFlavor = null;
	for (int i = 0; 
	     (selectedFlavor == null) && (i < availableFlavors.length); 
	     ++i) {
	    DataFlavor flavor = availableFlavors[i];
	    if (flavor.match(stringFlavor)) {
		// A serialized java.lang.String
		selectedFlavor = flavor;
		continue;
	    }
	    String primaryType = flavor.getPrimaryType();
	    if ((primaryType == null) || (!("text".equals(primaryType)))) {
		continue;
	    }
	    String charset = flavor.getParameter("charset");
	    if (charset == null) {
		// Take the platform default
		selectedFlavor = flavor;
	    } else {
		if (DataTransferer.isEncodingSupported(charset)) {
		    selectedFlavor = flavor;
		}
	    }
	}
	//System.out.println("selectBestTextFlavor()->"+ selectedFlavor);
	return selectedFlavor;
    } // selectBestTextFlavor()

    /**
     * Gets a reader for an input stream, decoded for the expected
     * charset (encoding). This works only if the
     * representation class of this flavor is java.io.InputStream
     * (or a subclass). In case of representation class being Reader 
     * the result will be this class itself.
     *
     * @return a Reader to read the data
     *
     * @exception IllegalArgumentException if the representation class
     *            is not java.io.InputStream or java.lang.String
     * @exception IllegalArgumentException if the charset (character
     *            encoding) is not supported by the JDK.
     * @exception IllegalArgumentException if the transferable has null 
     *            data.
     * @exception NullPointerException if the transferable argument is 
     *            null.
     * @exception UnsupportedEncodingException if the encoding is
     *            not supported by this flavor.
     * @exception UnsupportedFlavorException if the transferable does
     *  	  not support this flavor.
     * @exception IOException if the data cannot be read because of an
     *            I/O error.
     */
    public Reader getReaderForText(Transferable transferable) 
	throws UnsupportedFlavorException, IOException
    {
	Object transferObject = transferable.getTransferData(this);
	if (transferObject == null) {
	    throw new IllegalArgumentException("getTransferData() returned null");
	}

	if (transferObject instanceof Reader) {
	    return (Reader)transferObject;
	} else if (transferObject instanceof String) {
	    return new StringReader((String)transferObject);
	} else if (transferObject instanceof InputStream) {
	    InputStream stream = (InputStream)transferObject;
	    String encoding = DataTransferer.getTextCharset(this);
	    if (encoding == null) {
	        return new InputStreamReader(stream);
	    } else {
	        return new InputStreamReader(stream, encoding);
	    }
	} else {
	    throw new IllegalArgumentException(
		"transfer data is not InputStream or String or Reader");
	}
    } // getReaderForText()
    
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
     * If the object is an instance of DataFlavor, representationClass
     * and MIME type will be compared.
     * This method does not use equals(String) method, so it does not
     * return <code>true</code> for the objects of String type.
     *
     * @return if the objects are equal
     */
    
    public boolean equals(Object o) {
        return ((o instanceof DataFlavor) && equals((DataFlavor)o));
    }

    /**
     * Two DataFlavors are considered equal if and only if their
     * MIME primary type and subtype and representation class are
     * equal. Additionally, if the primary type is "text", the 
     * charset parameter must also be equal. If
     * either DataFlavor is of primary type "text", but no charset
     * is specified, the platform default charset is assumed for
     * that DataFlavor.
     *
     * @return if the DataFlavors represent exactly the same type.
     */
    public boolean equals(DataFlavor that) {
        if (that == null) {
            return false;
	}
	if (this == that) {
	    return true;
	}

	if (representationClass == null) {
            if (that.getRepresentationClass() != null) {
                return false;
	    }
	} else {
	    if (!representationClass.equals(that.getRepresentationClass())) {
	        return false;
	    }
	}

	if (mimeType == null) {
	    if (that.mimeType != null) {
	        return false;
	    }
	} else {
	    if (!mimeType.match(that.mimeType)) {
	        return false;
	    }

	    if ("text".equals(getPrimaryType())) {
		String thisCharset = DataTransferer.getTextCharset(this);
		String thatCharset = DataTransferer.getTextCharset(that);
		if (thisCharset == null) {
		    if (thatCharset != null) {
			return false;
		    }
		} else {
		    if (!thisCharset.equals(thatCharset)) {
			return false;
		    }
		}
	    }
	}

	return true;
    }
    
    /**
     * Compare only the mimeType against the passed in String
     * and representationClass is not considered in the comparison.
     * If representationClass needs to be compared, then
     *     equals(new DataFlavor(s))
     * may be used.
     *
     * @deprecated As inconsistent with <code>hashCode()</code> contract,
     *             use <code>isMimeTypeEqual(String)</code> instead.
     * @return if the String (MimeType) is equal
     */
    public boolean equals(String s) {
        if (s == null || mimeType == null)
            return false;
        return isMimeTypeEqual(s);
    }
    
    /**
     * Returns hash code for this <code>DataFlavor</code>.
     * For two equal DataFlavors, hash codes are equal. For the String
     * that matches <code>DataFlavor.equals(String)</code>, it is not
     * guaranteed that DataFlavor's hash code is equal to the hash code
     * of the String.
     *
     * @return a hash code for this DataFlavor
     */
    public int hashCode() {
        int representationClassPortion = 0, mimeTypePortion = 0,
	    charsetPortion = 0;

	if (representationClass != null) {
	    representationClassPortion = representationClass.hashCode();
	}

	if (mimeType != null) {
	    String primaryType = mimeType.getPrimaryType();
	    if (primaryType != null) {
	        mimeTypePortion = primaryType.hashCode();
		if ("text".equals(primaryType)) { 
		    String charset = DataTransferer.getTextCharset(this);
		    if (charset != null) {
		        charsetPortion = charset.hashCode();
		    }
		}
	    }
	}

	int total = representationClassPortion + mimeTypePortion +
	    charsetPortion;

	return (total != 0) ? total : 25431009;
    }

    /**
     * Two DataFlavors match if their primary types, subtypes, 
     * and representation classes are all equal. Additionally, if 
     * the primary type is "text", the charset parameter is also
     * considered. If either DataFlavor is of primary type "text",
     * but no charset is specified, the platform default charset
     * is assumed for that DataFlavor.
     */
    public boolean match(DataFlavor that) {
	if (that == null) {
	    return false;
	}

	if ((this.mimeType == null) || (that.mimeType == null)) {
	    return false;
	}

	String thisPrimaryType = this.getPrimaryType();
	String thatPrimaryType = that.getPrimaryType();
	if ((thisPrimaryType == null) || (thatPrimaryType == null) ||
	    (!thisPrimaryType.equals(thatPrimaryType))) {
	    return false;
	}

	String thisSubType = this.getSubType();
	String thatSubType = that.getSubType();
	if ((thisSubType == null) || (thatSubType == null) ||
	    (!thisSubType.equals(thatSubType))) {
	    return false;
	}

	Class thisRepresentationClass = this.getRepresentationClass();
	Class thatRepresentationClass = that.getRepresentationClass();
	if ((thisRepresentationClass == null) || 
	    (thatRepresentationClass == null) ||
	    (!thisRepresentationClass.equals(thatRepresentationClass))) {
	    return false;
	}

	if (thisPrimaryType.equals("text")) {
	    String thisCharset = DataTransferer.getTextCharset(this);
	    String thatCharset = DataTransferer.getTextCharset(that);
	    if ((thisCharset == null) || (thatCharset == null) ||
		(!thisCharset.equals(thatCharset))) {
		return false;
	    }
	}

	return true;
    } // match()
    
    /**
     * Returns whether the string representation of the MIME type passed in
     * is equivalent to the MIME type of this <code>DataFlavor</code>.
     * Parameters are not incuded in the comparison. The comparison may involve
     * adding default attributes for some MIME types (such as adding
     * <code>charset=US-ASCII</code> to text/plain MIME types that have
     * no <code>charset</code> parameter specified).
     *
     * @param mimeType the string representation of the MIME type
     * @return true if the string representation of the MIME type passed in is
     *         equivalent to the MIME type of this <code>DataFlavor</code>;
     *         false otherwise.
     * @throws NullPointerException if mimeType is <code>null</code>
     */
    public boolean isMimeTypeEqual(String mimeType) {
	// JCK Test DataFlavor0117: if 'mimeType' is null, throw NPE
	if (mimeType == null) {
	    throw new NullPointerException("mimeType");
	}
	if (this.mimeType == null) {
            return false;
        }
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
        return isMimeTypeEqual(javaSerializedObjectMimeType);
    }

    public final Class getDefaultRepresentationClass() {
        return ioInputStreamClass;
    }

    public final String getDefaultRepresentationClassAsString() {
        return getDefaultRepresentationClass().getName();
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
        return isRepresentationClassSerializable() && isMimeTypeEqual(javaSerializedObjectMimeType);
    }

    /**
     * @return if the DataFlavor specified represents a Remote Object
     */
  
    public boolean isFlavorRemoteObjectType() {
        return isRepresentationClassRemote() 
            && isRepresentationClassSerializable() 
            && isMimeTypeEqual(javaRemoteObjectMimeType);
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

        representationClass = DataFlavor.tryToLoadClass(rcn, this.getClass().getClassLoader());
   }

   /**
    * @return a clone of this DataFlavor
    */

    public Object clone() throws CloneNotSupportedException {
        DataFlavor clonedFlavor = new DataFlavor(this);
        return clonedFlavor;
    } // clone()

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
        return parameterValue;        
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

    //DEBUG void debugTestMimeEquals(DataFlavor that) {
    //DEBUG     String areThey = "?????";
    //DEBUG     if ((this.mimeType != null) && (that.mimeType != null)) {
    //DEBUG         if (this.mimeType.equals(that.mimeType)) {
    //DEBUG             areThey = " TRUE";
    //DEBUG         } else {
    //DEBUG             areThey = "FALSE";
    //DEBUG         }
    //DEBUG     }
    //DEBUG     System.out.println(areThey + ": " + this.mimeType);
    //DEBUG     System.out.println("     "+ ": " + that.mimeType);
    //DEBUG }

    /*
     * fields
     */

    /* placeholder for caching any platform-specific data for flavor */

    transient int       atom;      
  
    /* Mime Type of DataFlavor */

    MimeType            mimeType;

    private String      humanPresentableName;  
  
    /** Java class of objects this DataFlavor represents **/

    private Class       representationClass;

} // class DataFlavor

/*
 * @(#)XMLDecoder.java	1.20 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.beans;

import java.io.*; 
import java.util.*; 

import org.xml.sax.*;

import javax.xml.parsers.SAXParserFactory;  
import javax.xml.parsers.ParserConfigurationException;  
import javax.xml.parsers.SAXParser;  

/**
 * The <code>XMLDecoder</code> class is used to read XML documents 
 * created using the <code>XMLEncoder</code> and is used just like 
 * the <code>ObjectInputStream</code>. For example, one can use 
 * the following fragment to read the first object defined 
 * in an XML document written by the <code>XMLEncoder</code> 
 * class: 
 * <pre>
 *       XMLDecoder d = new XMLDecoder(
 *                          new BufferedInputStream(
 *                              new FileInputStream("Test.xml")));
 *       Object result = d.readObject();
 *       d.close();
 * </pre>
 * 
 * @see XMLEncoder
 * @see java.io.ObjectInputStream
 *
 * @since 1.4
 * 
 * @version 1.5 11/20/00
 * @author Philip Milne
 */
public class XMLDecoder { 
    private InputStream in; 
    private Object owner; 
    private ExceptionListener exceptionListener; 
    private ObjectHandler handler; 
    
    /** 
     * Creates a new input stream for reading archives  
     * created by the <code>XMLEncoder</code> class. 
     *
     * @param in The underlying stream. 
     *
     * @see XMLEncoder#XMLEncoder(OutputStream)
     */ 
    public XMLDecoder(InputStream in) { 
        this(in, null); 
    } 
    
    /** 
     * Creates a new input stream for reading archives  
     * created by the <code>XMLEncoder</code> class. 
     *
     * @param in The underlying stream. 
     * @param owner The owner of this stream. 
     *
     */ 
    public XMLDecoder(InputStream in, Object owner) { 
        this(in, owner, null); 
    } 
    
    /** 
     * Creates a new input stream for reading archives  
     * created by the <code>XMLEncoder</code> class. 
     *
     * @param in the underlying stream. 
     * @param owner the owner of this stream. 
     *
     */ 
    public XMLDecoder(InputStream in, Object owner, ExceptionListener exceptionListener) { 
        this.in = in;  
        setOwner(owner);  
        setExceptionListener(exceptionListener); 
        Statement.setCaching(true); 
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
           SAXParser saxParser = factory.newSAXParser();
           handler = new ObjectHandler(this);
           saxParser.parse(in, handler); 
        } 
        catch (ParserConfigurationException e) {
            getExceptionListener().exceptionThrown(e);
        } 
        catch (SAXException se) { 
            Exception e = se.getException(); 
            getExceptionListener().exceptionThrown((e == null) ? se : e); 
        }
        catch (IOException ioe) { 
            getExceptionListener().exceptionThrown(ioe); 
        }
    } 
    
    /**
     * This method closes the input stream associated 
     * with this stream. 
     */
    public void close() { 
        Statement.setCaching(false); 
        try { 
            in.close(); 
        } 
        catch (IOException e) { 
            getExceptionListener().exceptionThrown(e); 
        }
    }
    
    /** 
     * Sets the exception handler for this stream to <code>exceptionListener</code>. 
     * The exception handler is notified when this stream catches recoverable 
     * exceptions.
     * 
     * @param exceptionListener The exception handler for this stream. 
     *
     * @see #getExceptionListener
     */ 
    public void setExceptionListener(ExceptionListener exceptionListener) { 
        this.exceptionListener = exceptionListener; 
    } 
    
    /**
     * Gets the exception handler for this stream. 
     * 
     * @return The exception handler for this stream. 
     *
     * @see #setExceptionListener
     */ 
    public ExceptionListener getExceptionListener() { 
        return (exceptionListener != null) ? exceptionListener : Statement.defaultExceptionListener;
    } 
    
    /** 
     * Reads the next object from the underlying input stream. 
     *
     * @return the next object read
     *
     * @throws ArrayIndexOutOfBoundsException if the stream contains no objects (or no more objects)
     *
     * @see XMLEncoder#writeObject
     */ 
    public Object readObject() { 
        return handler.dequeueResult(); 
    } 
	
    /** 
     * Sets the owner of this decoder to <code>owner</code>. 
     * 
     * @param owner The owner of this decoder. 
     *
     * @see #getOwner
     */ 
    public void setOwner(Object owner) { 
        this.owner = owner; 
	}
	    
    /**
     * Gets the owner of this decoder. 
     * 
     * @return The owner of this decoder. 
     *
     * @see #setOwner
     */ 
    public Object getOwner() {
	    return owner; 
    }
}

class MutableExpression extends Expression { 
    private Object property; 
    private Vector argV = new Vector();  

    private String capitalize(String propertyName) { 
        if (propertyName.length() == 0) { 
            return propertyName; 
        }
        return propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    } 
    
    public MutableExpression() { 
        super(null, null, null); 
    } 
    
    public Object[] getArguments() { 
    	return argV.toArray(); 
    }
    
    public String getMethodName() { 
    	if (property == null) { 
            return super.getMethodName(); 
    	} 
        int setterArgs = (property instanceof String) ? 1 : 2; 
        String methodName = (argV.size() == setterArgs) ? "set" : "get"; 
        if (property instanceof String) { 
            return methodName + capitalize((String)property); 
        }
        else { 
            return methodName; 
        }
    }
    
    public void addArg(Object arg) { 
    	argV.add(arg); 
    } 
    
    public void setTarget(Object target) { 
    	this.target = target; 
    } 
    
    public void setMethodName(String methodName) { 
    	this.methodName = methodName; 
    }
    public void setProperty(Object property) { 
    	this.property = property; 
    }
}

class ObjectHandler extends HandlerBase {
    private Hashtable environment; 
    private Vector expStack; 
    private StringBuffer chars; 
    private XMLDecoder is; 
    private int itemsRead = 0; 

    public ObjectHandler(XMLDecoder is) { 
        environment = new Hashtable();
        expStack = new Vector(); 
        chars = new StringBuffer(); 
        this.is = is; 
    } 
    
    private Object getValue(Expression exp) { 
        try { 
            return exp.getValue(); 
        }
        catch (Exception e) { 
            is.getExceptionListener().exceptionThrown(e); 
            return null; 
        }
    }
    
    private void addArg(Object arg) { 
        // System.out.println("addArg: " + instanceName(arg)); 
        lastExp().addArg(arg); 
    } 
    
    private Object pop(Vector v) { 
        int last = v.size()-1; 
        Object result = v.get(last); 
        v.remove(last); 
        return result; 
    }
    
    private Object eval() { 
        return getValue((Expression)lastExp()); 
    }
    
    private MutableExpression lastExp() { 
        return (MutableExpression)expStack.lastElement(); 
    } 
    
    Object dequeueResult() { 
        // System.out.println("dequeueResult: " + expStack);
        Object[] results = lastExp().getArguments(); 
        return results[itemsRead++]; 
    }
    
    private boolean isPrimitive(String name) { 
        return name != "void" && Statement.typeNameToClass(name) != null;
    }
    
    private void simulateException(String message) { 
        Exception e = new Exception(message); 
    	e.fillInStackTrace(); 
        is.getExceptionListener().exceptionThrown(e); 
    } 
	
    private Class classForName(String name) { 
        try { 
            return Statement.classForName(name); 
        }
        catch (ClassNotFoundException e) { 
            is.getExceptionListener().exceptionThrown(e);
        } 
        return null; 
    } 
    
    private HashMap getAttributes(AttributeList attrs) { 
        HashMap attributes = new HashMap(); 
        if (attrs != null && attrs.getLength() > 0) {
            for(int i = 0; i < attrs.getLength(); i++) { 
                attributes.put(attrs.getName(i), attrs.getValue(i)); 
            }
        } 
        return attributes; 
    }
    
    public void startElement(String name, AttributeList attrs) throws SAXException { 
        // System.out.println("startElement" + name);
        name = name.intern(); // Xerces parser does not supply unique tag names.
        chars.setLength(0); 
        if (name == "null" || 
            name == "string" || 
            name == "class" || 
            isPrimitive(name)) { 
            return; 
        }
        HashMap attributes = getAttributes(attrs);  
        
        MutableExpression e = new MutableExpression(); 
        
        // Target
        String className = (String)attributes.get("class"); 
        if (className != null) {
            e.setTarget(classForName(className));
        } 
        
        // Property
        Object property = attributes.get("property"); 
        String index = (String)attributes.get("index"); 
        if (index != null) { 
            property = new Integer(index); 
            e.addArg(property); 
        } 
        e.setProperty(property); 
        
        // Method
        String methodName = (String)attributes.get("method"); 
        if (methodName == null && property == null) {
            methodName = "new"; 
        } 
        e.setMethodName(methodName); 
        
        // Tags
        if (name == "void") { 
            if (e.getTarget() == null) { // this check is for "void class="foo" method= ..." 
	        e.setTarget(eval()); 
	    }
        }
    	else if (name == "array") { 
            // The class attribute means sub-type for arrays. 
            String subtypeName = (String)attributes.get("class"); 
            Class subtype = (subtypeName == null) ? Object.class : classForName(subtypeName); 
            String length = (String)attributes.get("length"); 
            if (length != null) { 
                e.setTarget(java.lang.reflect.Array.class); 
                e.addArg(subtype); 
                e.addArg(new Integer(length)); 
            }
            else { 
                Class arrayClass = java.lang.reflect.Array.newInstance(subtype, 0).getClass(); 
                e.setTarget(arrayClass); 
            }
        } 
        else if (name == "java") { 
            e.setValue(is); // The outermost scope is the stream itself. 
        }
	else if (name == "object") { 
	}
	else { 
            simulateException("Unrecognized opening tag: " + name + " " + attrsToString(attrs)); 
	    return; 
        } 
		
        // ids
        String idName = (String)attributes.get("id"); 
        if (idName != null) { 
            environment.put(idName, e); 
        }
		
        // idrefs
        String idrefName = (String)attributes.get("idref"); 
        if (idrefName != null) { 
            e.setValue(lookup(idrefName)); 
        } 
        
        // fields
        String fieldName = (String)attributes.get("field"); 
        if (fieldName != null) { 
            e.setValue(getFieldValue(e.getTarget(), fieldName)); 
        }
        expStack.add(e); 
    }
    
    private Object getFieldValue(Object target, String fieldName) { 
        try { 
            Class type = target.getClass(); 
            if (type == Class.class) { 
                type = (Class)target; 
            } 
            java.lang.reflect.Field f = type.getField(fieldName); 
            return f.get(target); 
        }
        catch (Exception e) { 
            is.getExceptionListener().exceptionThrown(e); 
            return null; 
        }
    }            
            
    private String attrsToString(AttributeList attrs) { 
        StringBuffer b = new StringBuffer(); 
        for (int i = 0; i < attrs.getLength (); i++) {
            b.append(attrs.getName(i)+"=\""+attrs.getValue(i)+"\" ");
        }
        return b.toString(); 
    }            
            
    public void characters(char buf [], int offset, int len) throws SAXException {
        chars.append(new String(buf, offset, len));
    }

    private Object lookup(String s) { 
        Expression e = (Expression)environment.get(s); 
        if (e == null) { 
            simulateException("Unbound variable: " + s); 
        }
        return getValue(e); 
    }

    public void endElement(String name) throws SAXException {
        // System.out.println("endElement: " + expStack);
        name = name.intern(); // Xerces parser does not supply unique tag names.
        if (name == "null") {
            addArg(null); 
            return; 
        }
        if (name == "java") { 
            return; 
        }
        if (name == "string") { 
            addArg(chars.toString()); 
            return; 
        }
        if (name == "class") { 
            addArg(classForName(chars.toString())); 
            return; 
        }
        if (isPrimitive(name)) { 
            Class wrapper = Expression.typeNameToClass(name); 
            Expression e = new Expression(wrapper, "new", new Object[]{chars.toString()}); 
            addArg(getValue(e)); 
            return; 
        }
        if (name == "object" || name == "array" || name == "void") { 
            Expression e = (Expression)pop(expStack); 
            Object value = getValue(e); 
            if (name == "object" || name == "array") { 
                addArg(value); 
            }
        }
        else { 
            simulateException("Unrecognized closing tag: " + name);
        }
    }
}



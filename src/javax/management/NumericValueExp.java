/*
 * @(#)NumericValueExp.java	4.24 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;

import java.security.AccessController;
import java.security.PrivilegedAction;

import com.sun.jmx.mbeanserver.GetPropertyAction;

/**
 * This class represents numbers that are arguments to relational constraints.
 * A NumericValueExp may be used anywhere a ValueExp is required.
 *
 * @serial include
 *
 * @since 1.5
 */
class NumericValueExp extends QueryEval implements ValueExp { 

    // Serialization compatibility stuff:
    // Two serial forms are supported in this class. The selected form depends
    // on system property "jmx.serial.form":
    //  - "1.0" for JMX 1.0
    //  - any other value for JMX 1.1 and higher
    //
    // Serial version for old serial form 
    private static final long oldSerialVersionUID = -6227876276058904000L;
    //
    // Serial version for new serial form 
    private static final long newSerialVersionUID = -4679739485102359104L;
    //
    // Serializable fields in old serial form
    private static final ObjectStreamField[] oldSerialPersistentFields = 
    {
	new ObjectStreamField("longVal", Long.TYPE),
	new ObjectStreamField("doubleVal", Double.TYPE),
	new ObjectStreamField("valIsLong", Boolean.TYPE)
    };
    //
    // Serializable fields in new serial form
    private static final ObjectStreamField[] newSerialPersistentFields = 
    {
	new ObjectStreamField("val", Number.class)
    };
    //
    // Actual serial version and serial form
    private static final long serialVersionUID;
    /**
     * @serialField val Number The {@link Number} representing the numeric value
     */
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat = false;  
    static {
	try {
	    PrivilegedAction act = new GetPropertyAction("jmx.serial.form");
	    String form = (String) AccessController.doPrivileged(act);
	    compat = (form != null && form.equals("1.0"));
	} catch (Exception e) {
	    // OK: exception means no compat with 1.0, too bad
	}
	if (compat) {
	    serialPersistentFields = oldSerialPersistentFields;
	    serialVersionUID = oldSerialVersionUID;
	} else {
	    serialPersistentFields = newSerialPersistentFields;
	    serialVersionUID = newSerialVersionUID;
	}
    }
    //
    // END Serialization compatibility stuff

    /**
     * @serial The {@link Number} representing the numeric value
     */
    private Number val = new Double(0);
        
    /**
     * Basic constructor.
     */   
    public NumericValueExp() { 
    } 

    /** Creates a new NumericValue representing the numeric literal <val>.*/    
    NumericValueExp(Number val) 
    {
      this.val = val;
    }

    /**
     * Returns a double numeric value
     */
    public double doubleValue()  { 
      if (val instanceof Long || val instanceof Integer)
      {
        return (double)(val.longValue());
      }
      return val.doubleValue();
    } 

    /**
     * Returns a long numeric value
     */
    public long longValue()  { 
      if (val instanceof Long || val instanceof Integer)
      {
        return val.longValue();
      }
      return (long)(val.doubleValue());
    } 

    /**
     * Returns true is if the numeric value is a long, false otherwise.
     */
    public boolean isLong()  { 
	return (val instanceof Long || val instanceof Integer);
    } 
    
    /**
     * Returns the string representing the object
     */    
    public String toString()  { 
      if (val instanceof Long || val instanceof Integer)
      {
        return String.valueOf(val.longValue());
      }
      return String.valueOf(val.doubleValue());
    }     

    /**
     * Applies the ValueExp on a MBean.
     *
     * @param name The name of the MBean on which the ValueExp will be applied.
     *
     * @return  The <CODE>ValueExp</CODE>.
     *
     * @exception BadStringOperationException
     * @exception BadBinaryOpValueExpException
     * @exception BadAttributeValueExpException 
     * @exception InvalidApplicationException
     */   
    public ValueExp apply(ObjectName name) throws BadStringOperationException, BadBinaryOpValueExpException,
	BadAttributeValueExpException, InvalidApplicationException  { 
	return this;
    } 

    /**
     * Deserializes a {@link NumericValueExp} from an {@link ObjectInputStream}.
     */
    private void readObject(ObjectInputStream in)
	    throws IOException, ClassNotFoundException {
      if (compat)
      {
        // Read an object serialized in the old serial form
        //
        double doubleVal;
        long longVal;
        boolean isLong;
        ObjectInputStream.GetField fields = in.readFields();
        doubleVal = fields.get("doubleVal", (double)0);
	if (fields.defaulted("doubleVal"))
        {
          throw new NullPointerException("doubleVal");
        }
        longVal = fields.get("longVal", (long)0);
	if (fields.defaulted("longVal"))
        {
          throw new NullPointerException("longVal");
        }
        isLong = fields.get("valIsLong", false);
	if (fields.defaulted("valIsLong"))
        {
          throw new NullPointerException("valIsLong");
        }
        if (isLong)
        {
          this.val = new Long(longVal);
        }
        else
        {
          this.val = new Double(doubleVal);
        }
      }
      else
      {
        // Read an object serialized in the new serial form
        //
        in.defaultReadObject();
      }
    }


    /**
     * Serializes a {@link NumericValueExp} to an {@link ObjectOutputStream}.
     */
    private void writeObject(ObjectOutputStream out)
	    throws IOException {
      if (compat)
      {
        // Serializes this instance in the old serial form
        //
        ObjectOutputStream.PutField fields = out.putFields();
	fields.put("doubleVal", doubleValue());
	fields.put("longVal", longValue());
	fields.put("valIsLong", isLong());
	out.writeFields();
      }
      else
      {
        // Serializes this instance in the new serial form
        //
        out.defaultWriteObject();
      }
    }
 }

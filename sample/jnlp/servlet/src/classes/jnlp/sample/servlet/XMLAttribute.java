/*
 * @(#)XMLAttribute.java	1.1 03/05/18
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package jnlp.sample.servlet;

/** Class that contains information about a specific attribute
 */
public class XMLAttribute {
    private String _name;
    private String _value;
    private XMLAttribute _next;
    
    public XMLAttribute(String name, String value) {
	_name = name;
	_value = value;
	_next = null;
    }
    
    public XMLAttribute(String name, String value, XMLAttribute next) {
	_name = name;
	_value = value;
	_next = next;
    }
    
    public String getName()  { return _name; }
    public String getValue() { return _value; }
    public XMLAttribute getNext() { return _next; }
    public void setNext(XMLAttribute next) { _next = next; }
    
    public boolean equals(Object o) {
	if (o == null || !(o instanceof XMLAttribute)) return false;
	XMLAttribute other = (XMLAttribute)o;
	return
	    match(_name, other._name) &&
	    match(_value, other._value) &&
	    match(_next, other._next);
    }
    
    private static boolean match(Object o1, Object o2) {
	if (o1 == null) return (o2 == null);
	return o1.equals(o2);
    }
    
    public String toString() {
	if (_next != null) {
	    return _name + "=\"" + _value + "\" " + _next.toString();
	} else {
	    return _name + "=\"" + _value + "\"";
	}
    }
}




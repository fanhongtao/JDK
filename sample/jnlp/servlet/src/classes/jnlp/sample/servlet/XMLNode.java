/*
 * @(#)XMLNode.java	1.1 03/05/18
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package jnlp.sample.servlet;

import java.io.PrintWriter;
import java.io.StringWriter;

/** Class that contains information about an XML Node
 */
public class XMLNode {
    private boolean _isElement;     // Element/PCTEXT
    private String _name;
    private XMLAttribute _attr;
    private XMLNode _parent;  // Parent Node
    private XMLNode _nested;  // Nested XML tags
    private XMLNode _next;    // Following XML tag on the same level
    
    /** Creates a PCTEXT node */
    public XMLNode(String name) {
        this(name, null, null, null);
        _isElement = false;
    }
    
    /** Creates a ELEMENT node */
    public XMLNode(String name, XMLAttribute attr) {
        this(name, attr, null, null);
    }
    
    /** Creates a ELEMENT node */
    public XMLNode(String name, XMLAttribute attr, XMLNode nested, XMLNode next) {
        _isElement = true;
        _name = name;
        _attr = attr;
        _nested = nested;
        _next = next;
        _parent = null;
    }
    
    public String getName()  { return _name; }
    public XMLAttribute getAttributes() { return _attr; }
    public XMLNode getNested() { return _nested; }
    public XMLNode getNext() { return _next; }
    public boolean isElement() { return _isElement; }
    
    public void setParent(XMLNode parent) { _parent = parent; }
    public XMLNode getParent() { return _parent; }
        
    public void setNext(XMLNode next)     { _next = next; }
    public void setNested(XMLNode nested) { _nested = nested; }
    
    public boolean equals(Object o) {
        if (o == null || !(o instanceof XMLNode)) return false;
        XMLNode other = (XMLNode)o;
        boolean result =
            match(_name, other._name) &&
            match(_attr, other._attr) &&
            match(_nested, other._nested) &&
            match(_next, other._next);
        return result;
    }
    
    public String getAttribute(String name) {
        XMLAttribute cur = _attr;
        while(cur != null) {
            if (name.equals(cur.getName())) return cur.getValue();
            cur = cur.getNext();
        }
        return "";
    }
    
    private static boolean match(Object o1, Object o2) {
        if (o1 == null) return (o2 == null);
        return o1.equals(o2);
    }
    
    public void printToStream(PrintWriter out) {
        printToStream(out, 0);
    }
    
    public void printToStream(PrintWriter out, int n) {
        if (!isElement()) {
            out.print(_name);
        } else {
            if (_nested == null) {
                String attrString = (_attr == null) ? "" : (" " + _attr.toString());
                lineln(out, n, "<" + _name + attrString + "/>");
            } else {
                String attrString = (_attr == null) ? "" : (" " + _attr.toString());
                lineln(out, n, "<" + _name + attrString + ">");
                _nested.printToStream(out, n + 1);
                if (_nested.isElement()) {
                    lineln(out, n, "</" + _name + ">");
                } else {
                    out.print("</" + _name + ">");
                }
            }
        }
        if (_next != null) {
            _next.printToStream(out, n);
        }
    }
    
    private static void lineln(PrintWriter out, int indent, String s) {
        out.println("");
        for(int i = 0; i < indent; i++) {
            out.print("  ");
        }
        out.print(s);
    }
    
    public String toString() {
        StringWriter sw = new StringWriter(1000);
        PrintWriter pw = new PrintWriter(sw);
        printToStream(pw);
        pw.close();
        return sw.toString();
    }
}



/*
 * @(#)XMLParsing.java	1.5 03/05/18
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package jnlp.sample.servlet;

import javax.xml.parsers.*;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.*;
import org.w3c.dom.*;

/** Contains handy methods for looking up information
 *  stored in XMLNodes.
 */
public class XMLParsing {
       
    public static XMLNode convert(Node n) {
        if (n == null) {
	    return null;
        } else if (n instanceof Text) {
	    Text tn = (Text)n;
	    return new XMLNode(tn.getNodeValue());
        } else if (n instanceof Element) {
	    Element en = (Element)n;
	    
	    XMLAttribute xmlatts = null;
	    NamedNodeMap attributes = en.getAttributes();
	    for(int i = attributes.getLength() - 1; i >= 0; i--) {
		Attr ar = (Attr)attributes.item(i);
		xmlatts = new XMLAttribute(ar.getName(), ar.getValue(), xmlatts);
	    }
	    
	    // Convert childern
	    XMLNode thisNode = new XMLNode(en.getNodeName(), xmlatts, null, null);;
	    XMLNode last = null;
	    Node nn = en.getFirstChild();
	    while(nn != null) {
		if (thisNode.getNested() == null) {
		    last = convert(nn);
		    thisNode.setNested(last);
		} else {
		    XMLNode nnode = convert(nn);
		    last.setNext(nnode);
		    last = nnode;
		}
		last.setParent(thisNode);
		nn = nn.getNextSibling();
	    }
	    
	    return thisNode;
        }
        return null;
    }
    
    /** Returns true if the path exists in the document, otherwise false */
    static public boolean isElementPath(XMLNode root, String path) {
        return findElementPath(root, path) != null;
    }
    
    
    /** Returns a string describing the current location in the DOM */
    static public String getPathString(XMLNode e) {
        return (e == null || !(e.isElement())) ? "" : getPathString(e.getParent()) + "<" + e.getName() + ">";
    }
    
    
    /** Like getElementContents(...) but with a defaultValue of null */
    static public String getElementContent(XMLNode root, String path) {
        return getElementContent(root, path, null);
    }
    
    /** Like getElementContents(...) but with a defaultValue of null */
    static public String[] getMultiElementContent(XMLNode root, String path) {
        final List list = new ArrayList();
	visitElements(root, path, new ElementVisitor() {
		    public void visitElement(XMLNode n) {
			String value = getElementContent(n, "");
			if (value != null) list.add(value);
		    }
		});
	if (list.size() == 0) return null;
	return (String[])list.toArray(new String[list.size()]);
    }
    
    /** Returns the value of the last element tag in the path, e.g.,  <..><tag>value</tag>. The DOM is assumes
     *  to be normalized. If no value is found, the defaultvalue is returned
     */
    static public String getElementContent(XMLNode root, String path, String defaultvalue) {
        XMLNode e = findElementPath(root, path);
	if (e == null) return defaultvalue;
        XMLNode n = e.getNested();
        if (n != null && !n.isElement()) return n.getName();
	return defaultvalue;
    }
    
    /** Parses a path string of the form <tag1><tag2><tag3> and returns the specific Element
     *  node for that tag, or null if it does not exist. If multiple elements exists with same
     *  path the first is returned
     */
    static public XMLNode findElementPath(XMLNode elem, String path) {
	// End condition. Root null -> path does not exist
	if (elem == null) return null;
	// End condition. String empty, return current root
	if (path == null || path.length() == 0) return elem;
	
	// Strip of first tag
	int idx = path.indexOf('>');
	String head = path.substring(1, idx);
	String tail = path.substring(idx + 1);
	return findElementPath(findChildElement(elem, head), tail);
    }
    
    /** Returns an child element with the current tag name or null. */
    static public XMLNode findChildElement(XMLNode elem, String tag) {
	XMLNode n = elem.getNested();
	while(n != null) {
	    if (n.isElement() && n.getName().equals(tag)) return n;
	    n = n.getNext();
	}
	return null;
    }
    
    /** Iterator class */
    public abstract static class ElementVisitor {
	abstract public void visitElement(XMLNode e);
    }
    
    /** Visits all elements which matches the <path>. The iteration is only
     *  done on the last elment in the path.
     */
    static public void visitElements(XMLNode root, String path, ElementVisitor ev) {
	// Get last element in path
	int idx = path.lastIndexOf('<');
	String head = path.substring(0, idx);
	String tag  = path.substring(idx + 1, path.length() - 1);
	
	XMLNode elem = findElementPath(root, head);
	if (elem == null) return;
	
	// Iterate through all child nodes
	XMLNode n = elem.getNested();
	while(n != null) {
	    if (n.isElement() && n.getName().equals(tag)) {
		ev.visitElement(n);
	    }
	    n = n.getNext();
	}
    }
    
    static public void visitChildrenElements(XMLNode elem, ElementVisitor ev) {
	// Iterate through all child nodes
	XMLNode n = elem.getNested();
	while(n != null) {
	    if (n.isElement()) ev.visitElement(n);
	    n = n.getNext();
	}
    }
}


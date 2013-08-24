/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: SAX2DOM.java,v 1.1.2.3 2006/09/27 18:40:59 spericas Exp $
 */


package com.sun.org.apache.xalan.internal.xsltc.trax;

import java.util.Stack;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Constants;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * @author G. Todd Miller
 */
public class SAX2DOM implements ContentHandler, LexicalHandler, Constants {

    private Node _root = null;
    private Document _document = null;
    private Stack _nodeStk = new Stack();
    private Vector _namespaceDecls = null;
    private Node _lastSibling = null;

    public SAX2DOM() throws ParserConfigurationException {
	final DocumentBuilderFactory factory =
		DocumentBuilderFactory.newInstance();
	_document = factory.newDocumentBuilder().newDocument();
	_root = _document;
    }

    public SAX2DOM(Node root) throws ParserConfigurationException {
	_root = root;
	if (root instanceof Document) {
	  _document = (Document)root;
	}
	else if (root != null) {
	  _document = root.getOwnerDocument();
	}
	else {
	  final DocumentBuilderFactory factory =
		DocumentBuilderFactory.newInstance();
	  _document = factory.newDocumentBuilder().newDocument();
	  _root = _document;
	}
    }

    public Node getDOM() {
	return _root;
    }

    public void characters(char[] ch, int start, int length) {
	final Node last = (Node)_nodeStk.peek();

        // No text nodes can be children of root (DOM006 exception)
        if (last != _document) {
            final String text = new String(ch, start, length);
            if( _lastSibling != null && _lastSibling.getNodeType() == Node.TEXT_NODE ){
                  ((Text)_lastSibling).appendData(text);
            }
            else{
                _lastSibling = last.appendChild(_document.createTextNode(text));
            }
        }
    }

    public void startDocument() {
	_nodeStk.push(_root);
    }

    public void endDocument() {
        _nodeStk.pop();
    }

    public void startElement(String namespace, String localName, String qName,
	Attributes attrs)
    {
	final Element tmp = (Element)_document.createElementNS(namespace, qName);

	// Add namespace declarations first
	if (_namespaceDecls != null) {
	    final int nDecls = _namespaceDecls.size();
	    for (int i = 0; i < nDecls; i++) {
		final String prefix = (String) _namespaceDecls.elementAt(i++);

		if (prefix == null || prefix.equals(EMPTYSTRING)) {
		    tmp.setAttributeNS(XMLNS_URI, XMLNS_PREFIX,
			(String) _namespaceDecls.elementAt(i));
		}
		else {
		    tmp.setAttributeNS(XMLNS_URI, XMLNS_STRING + prefix,
			(String) _namespaceDecls.elementAt(i));
		}
	    }
	    _namespaceDecls.clear();
	}

        // Add attributes to element
        final int nattrs = attrs.getLength();
        for (int i = 0; i < nattrs; i++) {
            // checking if Namespace processing is being done
            String attQName = attrs.getQName(i);
            String attURI = attrs.getURI(i);
            String attrLocalName = attrs.getLocalName(i);
            
            if (attrLocalName == null || attrLocalName.equals(EMPTYSTRING)) {
                tmp.setAttribute(attQName, attrs.getValue(i));
                if (attrs.getType(i).equals("ID")) {
                    tmp.setIdAttribute(attQName, true);
                }
            } else {
                tmp.setAttributeNS(attURI, attQName, attrs.getValue(i));
                if (attrs.getType(i).equals("ID")) {
                    tmp.setIdAttributeNS(attURI, attrLocalName, true);
                }
            }
        }

	// Append this new node onto current stack node
	Node last = (Node)_nodeStk.peek();
	last.appendChild(tmp);

	// Push this node onto stack
	_nodeStk.push(tmp);
        _lastSibling = null;
    }

    public void endElement(String namespace, String localName, String qName) {
	_nodeStk.pop();
        _lastSibling = null;
    }

    public void startPrefixMapping(String prefix, String uri) {
	if (_namespaceDecls == null) {
	    _namespaceDecls = new Vector(2);
	}
	_namespaceDecls.addElement(prefix);
	_namespaceDecls.addElement(uri);
    }

    public void endPrefixMapping(String prefix) {
	// do nothing
    }

    /**
     * This class is only used internally so this method should never
     * be called.
     */
    public void ignorableWhitespace(char[] ch, int start, int length) {
    }

    /**
     * adds processing instruction node to DOM.
     */
    public void processingInstruction(String target, String data) {
	final Node last = (Node)_nodeStk.peek();
	ProcessingInstruction pi = _document.createProcessingInstruction(
		target, data);
	if (pi != null){
          last.appendChild(pi);
          _lastSibling = pi;
        }
    }

    /**
     * This class is only used internally so this method should never
     * be called.
     */
    public void setDocumentLocator(Locator locator) {
    }

    /**
     * This class is only used internally so this method should never
     * be called.
     */
    public void skippedEntity(String name) {
    }


    /**
     * Lexical Handler method to create comment node in DOM tree.
     */
    public void comment(char[] ch, int start, int length) {
	final Node last = (Node)_nodeStk.peek();
	Comment comment = _document.createComment(new String(ch,start,length));
	if (comment != null){
          last.appendChild(comment);
          _lastSibling = comment;
        }
    }

    // Lexical Handler methods- not implemented
    public void startCDATA() { }
    public void endCDATA() { }
    public void startEntity(java.lang.String name) { }
    public void endDTD() { }
    public void endEntity(String name) { }
    public void startDTD(String name, String publicId, String systemId)
        throws SAXException { }

}

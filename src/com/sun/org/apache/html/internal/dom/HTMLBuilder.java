/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999,2000 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package com.sun.org.apache.html.internal.dom;


import java.util.Vector;

import com.sun.org.apache.xerces.internal.dom.ElementImpl;
import com.sun.org.apache.xerces.internal.dom.ProcessingInstructionImpl;
import com.sun.org.apache.xerces.internal.dom.TextImpl;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/**
 * This is a SAX document handler that is used to build an HTML document.
 * It can build a document from any SAX parser, but is specifically tuned
 * for working with the OpenXML HTML parser.
 * 
 * 
 * @version $Revision: 1.6 $ $Date: 2003/05/08 20:13:09 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 */
public class HTMLBuilder
    implements DocumentHandler
{


    /**
     * The document that is being built.
     */
    protected HTMLDocumentImpl    _document;
    
    
    /**
     * The current node in the document into which elements, text and
     * other nodes will be inserted. This starts as the document iself
     * and reflects each element that is currently being parsed.
     */
    protected ElementImpl        _current;
    
    /**
     * A reference to the current locator, this is generally the parser
     * itself. The locator is used to locate errors and identify the
     * source locations of elements.
     */
    private Locator         _locator;


    /**
     * Applies only to whitespace appearing between element tags in element content,
     * as per the SAX definition, and true by default.
     */
    private boolean         _ignoreWhitespace = true;


    /**
     * Indicates whether finished building a document. If so, can start building
     * another document. Must be initially true to get the first document processed.
     */
    private boolean         _done = true;


    /**    
     * The document is only created the same time as the document element, however, certain
     * nodes may precede the document element (comment and PI), and they are accumulated
     * in this vector.
     */
    protected Vector         _preRootNodes;

    
    public void startDocument()
        throws SAXException
    {
        if ( ! _done )
	    throw new SAXException( "HTM001 State error: startDocument fired twice on one builder." );
	_document = null;
	_done = false;
    }


    public void endDocument()
        throws SAXException
    {
        if ( _document == null )
            throw new SAXException( "HTM002 State error: document never started or missing document element." );
	if ( _current != null )
	    throw new SAXException( "HTM003 State error: document ended before end of document element." );
        _current = null;
	_done = true;
    }


    public synchronized void startElement( String tagName, AttributeList attrList )
        throws SAXException
    {
        ElementImpl elem;
        int         i;
        
	if ( tagName == null )
	    throw new SAXException( "HTM004 Argument 'tagName' is null." );

	// If this is the root element, this is the time to create a new document,
	// because only know we know the document element name and namespace URI.
	if ( _document == null )
	{
	    // No need to create the element explicitly.
	    _document = new HTMLDocumentImpl();
	    elem = (ElementImpl) _document.getDocumentElement();
	    _current = elem;
	    if ( _current == null )
		throw new SAXException( "HTM005 State error: Document.getDocumentElement returns null." );

	    // Insert nodes (comment and PI) that appear before the root element.
	    if ( _preRootNodes != null )
	    {
		for ( i = _preRootNodes.size() ; i-- > 0 ; )
		    _document.insertBefore( (Node) _preRootNodes.elementAt( i ), elem );
		_preRootNodes = null;
	    }
	     
	}
	else
	{
	    // This is a state error, indicates that document has been parsed in full,
	    // or that there are two root elements.
	    if ( _current == null )
		throw new SAXException( "HTM006 State error: startElement called after end of document element." );
	    elem = (ElementImpl) _document.createElement( tagName );
	    _current.appendChild( elem );
	    _current = elem;
	}

	// Add the attributes (specified and not-specified) to this element.
        if ( attrList != null )
        {
            for ( i = 0 ; i < attrList.getLength() ; ++ i )
                elem.setAttribute( attrList.getName( i ), attrList.getValue( i ) );
        }
    }

    
    public void endElement( String tagName )
        throws SAXException
    {
        if ( _current == null )
            throw new SAXException( "HTM007 State error: endElement called with no current node." );
	if ( ! _current.getNodeName().equalsIgnoreCase( tagName ))
	    throw new SAXException( "HTM008 State error: mismatch in closing tag name " + tagName + "\n" + tagName);

	// Move up to the parent element. When you reach the top (closing the root element).
	// the parent is document and current is null.
	if ( _current.getParentNode() == _current.getOwnerDocument() )
	    _current = null;
	else
	    _current = (ElementImpl) _current.getParentNode();
    }


    public void characters( String text )
        throws SAXException
    {
	if ( _current == null )
            throw new SAXException( "HTM009 State error: character data found outside of root element." );
	_current.appendChild( new TextImpl( _document, text ) );
    }

    
    public void characters( char[] text, int start, int length )
        throws SAXException
    {
	if ( _current == null )
            throw new SAXException( "HTM010 State error: character data found outside of root element." );
	_current.appendChild( new TextImpl( _document, new String( text, start, length ) ) );
    }
    
    
    public void ignorableWhitespace( char[] text, int start, int length )
        throws SAXException
    {
        Node    node;
        
        if ( ! _ignoreWhitespace )
	    _current.appendChild( new TextImpl( _document, new String( text, start, length ) ) );
     }
    
    
    public void processingInstruction( String target, String instruction )
        throws SAXException
    {
        Node    node;
        
	// Processing instruction may appear before the document element (in fact, before the
	// document has been created, or after the document element has been closed.
        if ( _current == null && _document == null )
	{
	    if ( _preRootNodes == null )
		_preRootNodes = new Vector();
	    _preRootNodes.addElement( new ProcessingInstructionImpl( null, target, instruction ) );
	}
	else
        if ( _current == null && _document != null )
	    _document.appendChild( new ProcessingInstructionImpl( _document, target, instruction ) );
	else
	    _current.appendChild( new ProcessingInstructionImpl( _document, target, instruction ) );
    }
    
    
    public HTMLDocument getHTMLDocument()
    {
        return (HTMLDocument) _document;
    }

    
    public void setDocumentLocator( Locator locator )
    {
        _locator = locator;
    }


}

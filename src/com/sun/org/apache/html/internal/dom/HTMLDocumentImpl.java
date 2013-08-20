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


import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Locale;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xerces.internal.dom.NodeImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLBodyElement;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLFrameSetElement;
import org.w3c.dom.html.HTMLHeadElement;
import org.w3c.dom.html.HTMLHtmlElement;
import org.w3c.dom.html.HTMLTitleElement;


/**
 * Implements an HTML document. Provides access to the top level element in the
 * document, its body and title.
 * <P>
 * Several methods create new nodes of all basic types (comment, text, element,
 * etc.). These methods create new nodes but do not place them in the document
 * tree. The nodes may be placed in the document tree using {@link
 * org.w3c.dom.Node#appendChild} or {@link org.w3c.dom.Node#insertBefore}, or
 * they may be placed in some other document tree.
 * <P>
 * Note: &lt;FRAMESET&gt; documents are not supported at the moment, neither
 * are direct document writing ({@link #open}, {@link #write}) and HTTP attribute
 * methods ({@link #getURL}, {@link #getCookie}).
 *
 *
 * @version $Revision: 1.18 $ $Date: 2004/02/17 07:14:48 $
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLDocument
 */
public class HTMLDocumentImpl
    extends DocumentImpl
    implements HTMLDocument
{


    /**
     * Holds {@link HTMLCollectionImpl} object with live collection of all
     * anchors in document. This reference is on demand only once.
     */
    private HTMLCollectionImpl    _anchors;


    /**
     * Holds {@link HTMLCollectionImpl} object with live collection of all
     * forms in document. This reference is on demand only once.
     */
    private HTMLCollectionImpl    _forms;


    /**
     * Holds {@link HTMLCollectionImpl} object with live collection of all
     * images in document. This reference is on demand only once.
     */
    private HTMLCollectionImpl    _images;


    /**
     * Holds {@link HTMLCollectionImpl} object with live collection of all
     * links in document. This reference is on demand only once.
     */
    private HTMLCollectionImpl    _links;


    /**
     * Holds {@link HTMLCollectionImpl} object with live collection of all
     * applets in document. This reference is on demand only once.
     */
    private HTMLCollectionImpl    _applets;


    /**
     * Holds string writer used by direct manipulation operation ({@link #open}.
     * {@link #write}, etc) to write new contents into the document and parse
     * that text into a document tree.
     */
    private StringWriter        _writer;


    /**
     * Holds names and classes of HTML element types. When an element with a
     * particular tag name is created, the matching {@link java.lang.Class}
     * is used to create the element object. For example, &lt;A&gt; matches
     * {@link HTMLAnchorElementImpl}. This static table is shared across all
     * HTML documents.
     *
     * @see #createElement
     */
    private static Hashtable        _elementTypesHTML;


    /**
     * Signature used to locate constructor of HTML element classes. This
     * static array is shared across all HTML documents.
     *
     * @see #createElement
     */
    private static final Class[]    _elemClassSigHTML =
                new Class[] { HTMLDocumentImpl.class, String.class };


    /**
     */
    public HTMLDocumentImpl()
    {
        super();
        populateElementTypes();
    }


    public synchronized Element getDocumentElement()
    {
        Node    html;
        Node    child;
        Node    next;

        // The document element is the top-level HTML element of the HTML
        // document. Only this element should exist at the top level.
        // If the HTML element is found, all other elements that might
        // precede it are placed inside the HTML element.
        html = getFirstChild();
        while ( html != null )
        {
            if ( html instanceof HTMLHtmlElement )
            {
                // REVISIT: [Q] Why is this code even here? In fact, the
                //          original code is in error because it will
                //          try to move ALL nodes to be children of the
                //          HTML tag. This is not the intended behavior
                //          for comments and processing instructions
                //          outside the root element; it will throw a
                //          hierarchy request error exception for doctype
                //          nodes; *and* this code shouldn't even be
                //          needed because the parser should never build
                //          a document that contains more than a single
                //          root element, anyway! -Ac
                /***
                synchronized ( html )
                {
                    child = getFirstChild();
                    while ( child != null && child != html )
                    {
                        next = child.getNextSibling();
                        html.appendChild( child );
                        child = next;
                    }
                }
                /***/
                return (HTMLElement) html;
            }
            html = html.getNextSibling();
        }

        // HTML element must exist. Create a new element and dump the
        // entire contents of the document into it in the same order as
        // they appear now.
        html = new HTMLHtmlElementImpl( this, "HTML" );
        child = getFirstChild();
        while ( child != null )
        {
            next = child.getNextSibling();
            html.appendChild( child );
            child = next;
        }
        appendChild( html );
        return (HTMLElement) html;
    }


    /**
     * Obtains the &lt;HEAD&gt; element in the document, creating one if does
     * not exist before. The &lt;HEAD&gt; element is the first element in the
     * &lt;HTML&gt; in the document. The &lt;HTML&gt; element is obtained by
     * calling {@link #getDocumentElement}. If the element does not exist, one
     * is created.
     * <P>
     * Called by {@link #getTitle}, {@link #setTitle}, {@link #getBody} and
     * {@link #setBody} to assure the document has the &lt;HEAD&gt; element
     * correctly placed.
     *
     * @return The &lt;HEAD&gt; element
     */
    public synchronized HTMLElement getHead()
    {
        Node    head;
        Node    html;
        Node    child;
        Node    next;

        // Call getDocumentElement() to get the HTML element that is also the
        // top-level element in the document. Get the first element in the
        // document that is called HEAD. Work with that.
        html = getDocumentElement();
        synchronized ( html )
        {
            head = html.getFirstChild();
            while ( head != null && ! ( head instanceof HTMLHeadElement ) )
                head = head.getNextSibling();
            // HEAD exists but might not be first element in HTML: make sure
            // it is and return it.
            if ( head != null )
            {
                synchronized ( head )
                {
                    child = html.getFirstChild();
                    while ( child != null && child != head )
                    {
                        next = child.getNextSibling();
                        head.insertBefore( child, head.getFirstChild() );
                        child = next;
                    }
                }
                return (HTMLElement) head;
            }

            // Head does not exist, create a new one, place it at the top of the
            // HTML element and return it.
            head = new HTMLHeadElementImpl( this, "HEAD" );
            html.insertBefore( head, html.getFirstChild() );
        }
        return (HTMLElement) head;
    }


    public synchronized String getTitle()
    {
        HTMLElement head;
        NodeList    list;
        Node        title;

        // Get the HEAD element and look for the TITLE element within.
        // When found, make sure the TITLE is a direct child of HEAD,
        // and return the title's text (the Text node contained within).
        head = getHead();
        title = head.getElementsByTagName( "TITLE" ).item( 0 );
        list = head.getElementsByTagName( "TITLE" );
        if ( list.getLength() > 0 ) {
            title = list.item( 0 );
            return ( (HTMLTitleElement) title ).getText();
        }
        // No TITLE found, return an empty string.
        return "";
    }


    public synchronized void setTitle( String newTitle )
    {
        HTMLElement head;
        NodeList    list;
        Node        title;

        // Get the HEAD element and look for the TITLE element within.
        // When found, make sure the TITLE is a direct child of HEAD,
        // and set the title's text (the Text node contained within).
        head = getHead();
        list = head.getElementsByTagName( "TITLE" );
        if ( list.getLength() > 0 ) {
            title = list.item( 0 );
            if ( title.getParentNode() != head )
                head.appendChild( title );
            ( (HTMLTitleElement) title ).setText( newTitle );
        }
        else
        {
            // No TITLE found, create a new element and place it at the end
            // of the HEAD element.
            title = new HTMLTitleElementImpl( this, "TITLE" );
            ( (HTMLTitleElement) title ).setText( newTitle );
            head.appendChild( title );
        }
    }


    public synchronized HTMLElement getBody()
    {
        Node    html;
        Node    head;
        Node    body;
        Node    child;
        Node    next;

        // Call getDocumentElement() to get the HTML element that is also the
        // top-level element in the document. Get the first element in the
        // document that is called BODY. Work with that.
        html = getDocumentElement();
        head = getHead();
        synchronized ( html )
        {
            body = head.getNextSibling();
            while ( body != null && ! ( body instanceof HTMLBodyElement )
                    && ! ( body instanceof HTMLFrameSetElement ) )
                body = body.getNextSibling();

            // BODY/FRAMESET exists but might not be second element in HTML
            // (after HEAD): make sure it is and return it.
            if ( body != null )
            {
                synchronized ( body )
                {
                    child = head.getNextSibling();
                    while ( child != null && child != body )
                    {
                        next = child.getNextSibling();
                        body.insertBefore( child, body.getFirstChild() );
                        child = next;
                    }
                }
                return (HTMLElement) body;
            }

            // BODY does not exist, create a new one, place it in the HTML element
            // right after the HEAD and return it.
            body = new HTMLBodyElementImpl( this, "BODY" );
            html.appendChild( body );
        }
        return (HTMLElement) body;
    }


    public synchronized void setBody( HTMLElement newBody )
    {
        Node    html;
        Node    body;
        Node    head;
        Node    child;
        NodeList list;

        synchronized ( newBody )
        {
            // Call getDocumentElement() to get the HTML element that is also the
            // top-level element in the document. Get the first element in the
            // document that is called BODY. Work with that.
            html = getDocumentElement();
            head = getHead();
            synchronized ( html )
            {
                list = this.getElementsByTagName( "BODY" );
                if ( list.getLength() > 0 ) {
                    // BODY exists but might not follow HEAD in HTML. If not,
                    // make it so and replce it. Start with the HEAD and make
                    // sure the BODY is the first element after the HEAD.
                    body = list.item( 0 );
                    synchronized ( body )
                    {
                        child = head;
                        while ( child != null )
                        {
                            if ( child instanceof Element )
                            {
                                if ( child != body )
                                    html.insertBefore( newBody, child );
                                else
                                    html.replaceChild( newBody, body );
                                return;
                            }
                            child = child.getNextSibling();
                        }
                        html.appendChild( newBody );
                    }
                    return;
                }
                // BODY does not exist, place it in the HTML element
                // right after the HEAD.
                html.appendChild( newBody );
            }
        }
    }


    public synchronized Element getElementById( String elementId )
    {
        return getElementById( elementId, this );
    }


    public NodeList getElementsByName( String elementName )
    {
        return new NameNodeListImpl( this, elementName );
    }


    public final NodeList getElementsByTagName( String tagName )
    {
        return super.getElementsByTagName( tagName.toUpperCase(Locale.ENGLISH) );
    }


    public final NodeList getElementsByTagNameNS( String namespaceURI,
                                                  String localName )
    {
        if ( namespaceURI != null && namespaceURI.length() > 0 )
            return super.getElementsByTagNameNS( namespaceURI, localName.toUpperCase(Locale.ENGLISH) );
        else
            return super.getElementsByTagName( localName.toUpperCase(Locale.ENGLISH) );
    }


    /**
     * Xerces-specific constructor. "localName" is passed in, so we don't need
     * to create a new String for it.
     *
     * @param namespaceURI The namespace URI of the element to
     *                     create.
     * @param qualifiedName The qualified name of the element type to
     *                      instantiate.
     * @param localName     The local name of the element to instantiate.
     * @return Element A new Element object with the following attributes:
     * @throws DOMException INVALID_CHARACTER_ERR: Raised if the specified
     *                      name contains an invalid character.
     */
    public Element createElementNS(String namespaceURI, String qualifiedName,
                                   String localpart)
        throws DOMException
    {
        return createElementNS(namespaceURI, qualifiedName);
    }

    public Element createElementNS( String namespaceURI, String qualifiedName )
    {
        if ( namespaceURI == null || namespaceURI.length() == 0 )
            return createElement( qualifiedName );
        else {
            return super.createElementNS( namespaceURI, qualifiedName );
        }
    }


    public Element createElement( String tagName )
        throws DOMException
    {
        Class        elemClass;
        Constructor    cnst;

        // First, make sure tag name is all upper case, next get the associated
        // element class. If no class is found, generate a generic HTML element.
        // Do so also if an unexpected exception occurs.
        tagName = tagName.toUpperCase(Locale.ENGLISH);
        elemClass = (Class) _elementTypesHTML.get( tagName );
        if ( elemClass != null )
        {
            // Get the constructor for the element. The signature specifies an
            // owner document and a tag name. Use the constructor to instantiate
            // a new object and return it.
            try
            {
                cnst = elemClass.getConstructor( _elemClassSigHTML );
                return (Element) cnst.newInstance( new Object[] { this, tagName } );
            }
            catch ( Exception except )
            {
                Throwable thrw;

                if ( except instanceof java.lang.reflect.InvocationTargetException )
                    thrw = ( (java.lang.reflect.InvocationTargetException) except ).getTargetException();
                else
                    thrw = except;
//                System.out.println( "Exception " + thrw.getClass().getName() );
//                System.out.println( thrw.getMessage() );

                throw new IllegalStateException( "HTM15 Tag '" + tagName + "' associated with an Element class that failed to construct.\n" + tagName);
            }
        }
        return new HTMLElementImpl( this, tagName );
    }


    /**
     * Creates an Attribute having this Document as its OwnerDoc.
     * Overrides {@link DocumentImpl#createAttribute} and returns
     * and attribute whose name is lower case.
     *
     * @param name The name of the attribute
     * @return An attribute whose name is all lower case
     * @throws DOMException(INVALID_NAME_ERR) if the attribute name
     *   is not acceptable
     */
    public Attr createAttribute( String name )
        throws DOMException
    {
        return super.createAttribute( name.toLowerCase(Locale.ENGLISH) );
    }


    public String getReferrer()
    {
        // Information not available on server side.
        return null;
    }


    public String getDomain()
    {
        // Information not available on server side.
        return null;
    }


    public String getURL()
    {
        // Information not available on server side.
        return null;
    }


    public String getCookie()
    {
        // Information not available on server side.
        return null;
    }


    public void setCookie( String cookie )
    {
        // Information not available on server side.
    }


    public HTMLCollection getImages()
    {
        // For more information see HTMLCollection#collectionMatch
        if ( _images == null )
            _images = new HTMLCollectionImpl( getBody(), HTMLCollectionImpl.IMAGE );
        return _images;
    }


    public HTMLCollection getApplets()
    {
        // For more information see HTMLCollection#collectionMatch
        if ( _applets == null )
            _applets = new HTMLCollectionImpl( getBody(), HTMLCollectionImpl.APPLET );
        return _applets;
    }


    public HTMLCollection getLinks()
    {
        // For more information see HTMLCollection#collectionMatch
        if ( _links == null )
            _links = new HTMLCollectionImpl( getBody(), HTMLCollectionImpl.LINK );
        return _links;
    }


    public HTMLCollection getForms()
    {
        // For more information see HTMLCollection#collectionMatch
        if ( _forms == null )
            _forms = new HTMLCollectionImpl( getBody(), HTMLCollectionImpl.FORM );
        return _forms;
    }


    public HTMLCollection getAnchors()
    {
        // For more information see HTMLCollection#collectionMatch
        if ( _anchors == null )
            _anchors = new HTMLCollectionImpl( getBody(), HTMLCollectionImpl.ANCHOR );
        return _anchors;
    }


    public void open()
    {
        // When called an in-memory is prepared. The document tree is still
        // accessible the old way, until this writer is closed.
        if ( _writer == null )
            _writer = new StringWriter();
    }


    public void close()
    {
        // ! NOT IMPLEMENTED, REQUIRES PARSER !
        if ( _writer != null )
        {
            _writer = null;
        }
    }


    public void write( String text )
    {
        // Write a string into the in-memory writer.
        if ( _writer != null )
            _writer.write( text );
    }


    public void writeln( String text )
    {
        // Write a line into the in-memory writer.
        if ( _writer != null )
            _writer.write( text + "\n" );
    }


    public Node cloneNode( boolean deep )
    {
        HTMLDocumentImpl    clone;
        NodeImpl            node;

        clone = new HTMLDocumentImpl();
        if ( deep ) {
            node = (NodeImpl) getFirstChild();
            while ( node != null ) {
                clone.appendChild( clone.importNode( node, true ) );
                node = (NodeImpl) node.getNextSibling();
            }
        }
        return clone;
    }


    /**
     * Recursive method retreives an element by its <code>id</code> attribute.
     * Called by {@link #getElementById(String)}.
     *
     * @param elementId The <code>id</code> value to look for
     * @return The node in which to look for
     */
    private Element getElementById( String elementId, Node node )
    {
        Node    child;
        Element    result;

        child = node.getFirstChild();
        while ( child != null )
        {
            if ( child instanceof Element )
            {
                if ( elementId.equals( ( (Element) child ).getAttribute( "id" ) ) )
                    return (Element) child;
                result = getElementById( elementId, child );
                if ( result != null )
                    return result;
            }
            child = child.getNextSibling();
        }
        return null;
    }


    /**
     * Called by the constructor to populate the element types list (see {@link
     * #_elementTypesHTML}). Will be called multiple times but populate the list
     * only the first time. Replacement for static constructor.
     */
    private synchronized static void populateElementTypes()
    {
        // This class looks like it is due to some strange
        // (read: inconsistent) JVM bugs.
        // Initially all this code was placed in the static constructor,
        // but that caused some early JVMs (1.1) to go mad, and if a
        // class could not be found (as happened during development),
        // the JVM would die.
        // Bertrand Delacretaz <bdelacretaz@worldcom.ch> pointed out
        // several configurations where HTMLAnchorElementImpl.class
        // failed, forcing me to revert back to Class.forName().

        if ( _elementTypesHTML != null )
            return;
        _elementTypesHTML = new Hashtable( 63 );
        populateElementType( "A", "HTMLAnchorElementImpl" );
        populateElementType( "APPLET", "HTMLAppletElementImpl" );
        populateElementType( "AREA", "HTMLAreaElementImpl" );
        populateElementType( "BASE",  "HTMLBaseElementImpl" );
        populateElementType( "BASEFONT", "HTMLBaseFontElementImpl" );
        populateElementType( "BLOCKQUOTE", "HTMLQuoteElementImpl" );
        populateElementType( "BODY", "HTMLBodyElementImpl" );
        populateElementType( "BR", "HTMLBRElementImpl" );
        populateElementType( "BUTTON", "HTMLButtonElementImpl" );
        populateElementType( "DEL", "HTMLModElementImpl" );
        populateElementType( "DIR", "HTMLDirectoryElementImpl" );
        populateElementType( "DIV",  "HTMLDivElementImpl" );
        populateElementType( "DL", "HTMLDListElementImpl" );
        populateElementType( "FIELDSET", "HTMLFieldSetElementImpl" );
        populateElementType( "FONT", "HTMLFontElementImpl" );
        populateElementType( "FORM", "HTMLFormElementImpl" );
        populateElementType( "FRAME","HTMLFrameElementImpl" );
        populateElementType( "FRAMESET", "HTMLFrameSetElementImpl" );
        populateElementType( "HEAD", "HTMLHeadElementImpl" );
        populateElementType( "H1", "HTMLHeadingElementImpl" );
        populateElementType( "H2", "HTMLHeadingElementImpl" );
        populateElementType( "H3", "HTMLHeadingElementImpl" );
        populateElementType( "H4", "HTMLHeadingElementImpl" );
        populateElementType( "H5", "HTMLHeadingElementImpl" );
        populateElementType( "H6", "HTMLHeadingElementImpl" );
        populateElementType( "HR", "HTMLHRElementImpl" );
        populateElementType( "HTML", "HTMLHtmlElementImpl" );
        populateElementType( "IFRAME", "HTMLIFrameElementImpl" );
        populateElementType( "IMG", "HTMLImageElementImpl" );
        populateElementType( "INPUT", "HTMLInputElementImpl" );
        populateElementType( "INS", "HTMLModElementImpl" );
        populateElementType( "ISINDEX", "HTMLIsIndexElementImpl" );
        populateElementType( "LABEL", "HTMLLabelElementImpl" );
        populateElementType( "LEGEND", "HTMLLegendElementImpl" );
        populateElementType( "LI", "HTMLLIElementImpl" );
        populateElementType( "LINK", "HTMLLinkElementImpl" );
        populateElementType( "MAP", "HTMLMapElementImpl" );
        populateElementType( "MENU", "HTMLMenuElementImpl" );
        populateElementType( "META", "HTMLMetaElementImpl" );
        populateElementType( "OBJECT", "HTMLObjectElementImpl" );
        populateElementType( "OL", "HTMLOListElementImpl" );
        populateElementType( "OPTGROUP", "HTMLOptGroupElementImpl" );
        populateElementType( "OPTION", "HTMLOptionElementImpl" );
        populateElementType( "P", "HTMLParagraphElementImpl" );
        populateElementType( "PARAM", "HTMLParamElementImpl" );
        populateElementType( "PRE", "HTMLPreElementImpl" );
        populateElementType( "Q", "HTMLQuoteElementImpl" );
        populateElementType( "SCRIPT", "HTMLScriptElementImpl" );
        populateElementType( "SELECT", "HTMLSelectElementImpl" );
        populateElementType( "STYLE", "HTMLStyleElementImpl" );
        populateElementType( "TABLE", "HTMLTableElementImpl" );
        populateElementType( "CAPTION", "HTMLTableCaptionElementImpl" );
        populateElementType( "TD", "HTMLTableCellElementImpl" );
        populateElementType( "TH", "HTMLTableCellElementImpl" );
        populateElementType( "COL", "HTMLTableColElementImpl" );
        populateElementType( "COLGROUP", "HTMLTableColElementImpl" );
        populateElementType( "TR", "HTMLTableRowElementImpl" );
        populateElementType( "TBODY", "HTMLTableSectionElementImpl" );
        populateElementType( "THEAD", "HTMLTableSectionElementImpl" );
        populateElementType( "TFOOT", "HTMLTableSectionElementImpl" );
        populateElementType( "TEXTAREA", "HTMLTextAreaElementImpl" );
        populateElementType( "TITLE", "HTMLTitleElementImpl" );
        populateElementType( "UL", "HTMLUListElementImpl" );
    }


    private static void populateElementType( String tagName, String className )
    {
        try {
            _elementTypesHTML.put( tagName,
                ObjectFactory.findProviderClass("com.sun.org.apache.html.internal.dom." + className,
                    HTMLDocumentImpl.class.getClassLoader(), true) );
        } catch ( Exception except ) {
            new RuntimeException( "HTM019 OpenXML Error: Could not find or execute class " + className + " implementing HTML element " + tagName
                                  + "\n" + className + "\t" + tagName);
        }
    }


}


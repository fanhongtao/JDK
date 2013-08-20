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


import java.util.Locale;

import com.sun.org.apache.xerces.internal.dom.ElementImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLFormElement;


/**
 * Implements an HTML-specific element, an {@link org.w3c.dom.Element} that
 * will only appear inside HTML documents. This element extends {@link
 * com.sun.org.apache.xerces.internal.dom.ElementImpl} by adding methods for directly
 * manipulating HTML-specific attributes. All HTML elements gain access to
 * the <code>id</code>, <code>title</code>, <code>lang</code>,
 * <code>dir</code> and <code>class</code> attributes. Other elements
 * add their own specific attributes.
 * 
 * 
 * @version $Revision: 1.6 $ $Date: 2003/05/08 20:13:09 $
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLElement
 */
public class HTMLElementImpl
    extends ElementImpl
    implements HTMLElement
{


    /**
     * Constructor required owner document and element tag name. Will be called
     * by the constructor of specific element types but with a known tag name.
     * Assures that the owner document is an HTML element.
     * 
     * @param owner The owner HTML document
     * @param tagName The element's tag name
     */
    HTMLElementImpl( HTMLDocumentImpl owner, String tagName )
    {
        super( owner, tagName.toUpperCase(Locale.ENGLISH) );
    }
    
    
    public String getId()
    {
        return getAttribute( "id" );
    }
    
    
    public void setId( String id )
    {
        setAttribute( "id", id );
    }
    
    
    public String getTitle()
    {
        return getAttribute( "title" );
    }
    
    
    public void setTitle( String title )
    {
        setAttribute( "title", title );
    }
    
    
    public String getLang()
    {
        return getAttribute( "lang" );
    }
    
    
    public void setLang( String lang )
    {
        setAttribute( "lang", lang );
    }
    
    
    public String getDir()
    {
        return getAttribute( "dir" );
    }
    
    
    public void setDir( String dir )
    {
        setAttribute( "dir", dir );
    }

    
    public String getClassName()
    {
        return getAttribute( "class" );
    }

    
    public void setClassName( String className )
    {
        setAttribute( "class", className );
    }
    
    
    /**
     * Convenience method used to translate an attribute value into an integer
     * value. Returns the integer value or zero if the attribute is not a
     * valid numeric string.
     * 
     * @param value The value of the attribute
     * @return The integer value, or zero if not a valid numeric string
     */
    int getInteger( String value )
    {
        try
        {
            return Integer.parseInt( value );
        }
        catch ( NumberFormatException except )
        {
            return 0;
        }
    }
    
    
    /**
     * Convenience method used to translate an attribute value into a boolean
     * value. If the attribute has an associated value (even an empty string),
     * it is set and true is returned. If the attribute does not exist, false
     * is returend.
     * 
     * @param value The value of the attribute
     * @return True or false depending on whether the attribute has been set
     */
    boolean getBinary( String name )
    {
        return ( getAttributeNode( name ) != null );
    }
    
    
    /**
     * Convenience method used to set a boolean attribute. If the value is true,
     * the attribute is set to an empty string. If the value is false, the attribute
     * is removed. HTML 4.0 understands empty strings as set attributes.
     * 
     * @param name The name of the attribute
     * @param value The value of the attribute
     */
    void setAttribute( String name, boolean value )
    {
        if ( value )
            setAttribute( name, name );
        else
            removeAttribute( name );
    }


    public Attr getAttributeNode( String attrName )
    {
	return super.getAttributeNode( attrName.toLowerCase(Locale.ENGLISH) );
    }


    public Attr getAttributeNodeNS( String namespaceURI,
				    String localName )
    {
	if ( namespaceURI != null && namespaceURI.length() > 0 )
	    return super.getAttributeNodeNS( namespaceURI, localName );
	else
	    return super.getAttributeNode( localName.toLowerCase(Locale.ENGLISH) );
    }
    
    
    public String getAttribute( String attrName )
    {
	return super.getAttribute( attrName.toLowerCase(Locale.ENGLISH) );
    }


    public String getAttributeNS( String namespaceURI,
				  String localName )
    {
	if ( namespaceURI != null && namespaceURI.length() > 0 )
	    return super.getAttributeNS( namespaceURI, localName );
	else
	    return super.getAttribute( localName.toLowerCase(Locale.ENGLISH) );
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
     * Convenience method used to capitalize a one-off attribute value before it
     * is returned. For example, the align values "LEFT" and "left" will both
     * return as "Left".
     * 
     * @param value The value of the attribute
     * @return The capitalized value
     */
    String capitalize( String value )
    {
        char[]    chars;
        int        i;
        
        // Convert string to charactares. Convert the first one to upper case,
        // the other characters to lower case, and return the converted string.
        chars = value.toCharArray();
        if ( chars.length > 0 )
        {
            chars[ 0 ] = Character.toUpperCase( chars[ 0 ] );
            for ( i = 1 ; i < chars.length ; ++i )
                chars[ i ] = Character.toLowerCase( chars[ i ] );
            return String.valueOf( chars );
        }
        return value;
    }
    

    /**
     * Convenience method used to capitalize a one-off attribute value before it
     * is returned. For example, the align values "LEFT" and "left" will both
     * return as "Left".
     * 
     * @param name The name of the attribute
     * @return The capitalized value
     */
    String getCapitalized( String name )
    {
        String    value;
        char[]    chars;
        int        i;
        
        value = getAttribute( name );
        if ( value != null )
        {
            // Convert string to charactares. Convert the first one to upper case,
            // the other characters to lower case, and return the converted string.
            chars = value.toCharArray();
            if ( chars.length > 0 )
            {
                chars[ 0 ] = Character.toUpperCase( chars[ 0 ] );
                for ( i = 1 ; i < chars.length ; ++i )
                    chars[ i ] = Character.toLowerCase( chars[ i ] );
                return String.valueOf( chars );
            }
        }
        return value;
    }

    
    /**
     * Convenience method returns the form in which this form element is contained.
     * This method is exposed for form elements through the DOM API, but other
     * elements have no access to it through the API.
     */
    public HTMLFormElement getForm()
    {
        Node    parent;
        
        parent = getParentNode(); 
        while ( parent != null )
        {
            if ( parent instanceof HTMLFormElement )
                return (HTMLFormElement) parent;
            parent = parent.getParentNode();
        }
        return null;
    }


}


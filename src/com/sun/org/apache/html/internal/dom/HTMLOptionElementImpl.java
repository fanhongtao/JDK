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


import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLOptionElement;
import org.w3c.dom.html.HTMLSelectElement;


/**
 * @version $Revision: 1.6 $ $Date: 2003/05/08 20:13:09 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLOptionElement
 * @see com.sun.org.apache.xerces.internal.dom.ElementImpl
 */
public class HTMLOptionElementImpl
    extends HTMLElementImpl
    implements HTMLOptionElement
{

    

    public boolean getDefaultSelected()
    {
        // ! NOT FULLY IMPLEMENTED !
        return getBinary( "default-selected" );
    }
    
    
    public void setDefaultSelected( boolean defaultSelected )
    {
        // ! NOT FULLY IMPLEMENTED !
        setAttribute( "default-selected", defaultSelected );
    }

  
    public String getText()
    {
        Node    child;
        String    text;
        
        // Find the Text nodes contained within this element and return their
        // concatenated value. Required to go around comments, entities, etc.
        child = getFirstChild();
        text = "";
        while ( child != null )
        {
            if ( child instanceof Text )
                text = text + ( (Text) child ).getData();
            child = child.getNextSibling();
        }
        return text;
    }
    
    
    public void setText( String text )
    {
        Node    child;
        Node    next;
        
        // Delete all the nodes and replace them with a single Text node.
        // This is the only approach that can handle comments and other nodes.
        child = getFirstChild();
        while ( child != null )
        {
            next = child.getNextSibling();
            removeChild( child );
            child = next;
        }
        insertBefore( getOwnerDocument().createTextNode( text ), getFirstChild() );
    }
    
    
    public int getIndex()
    {
        Node        parent;
        NodeList    options;
        int            i;
        
        // Locate the parent SELECT. Note that this OPTION might be inside a
        // OPTGROUP inside the SELECT. Or it might not have a parent SELECT.
        // Everything is possible. If no parent is found, return -1.
        parent = getParentNode();
        while ( parent != null && ! ( parent instanceof HTMLSelectElement ) )
            parent = parent.getParentNode();
        if ( parent != null )
        {
            // Use getElementsByTagName() which creates a snapshot of all the
            // OPTION elements under the SELECT. Access to the returned NodeList
            // is very fast and the snapshot solves many synchronization problems.
            options = ( (HTMLElement) parent ).getElementsByTagName( "OPTION" );
            for ( i = 0 ; i < options.getLength() ; ++i )
                if ( options.item( i ) == this )
                    return i;
        }
        return -1;
    }
    
    
    public void setIndex( int index )
    {
        Node        parent;
        NodeList    options;
        Node        item;
        
        // Locate the parent SELECT. Note that this OPTION might be inside a
        // OPTGROUP inside the SELECT. Or it might not have a parent SELECT.
        // Everything is possible. If no parent is found, just return.
        parent = getParentNode();
        while ( parent != null && ! ( parent instanceof HTMLSelectElement ) )
            parent = parent.getParentNode();
        if ( parent != null )
        {
            // Use getElementsByTagName() which creates a snapshot of all the
            // OPTION elements under the SELECT. Access to the returned NodeList
            // is very fast and the snapshot solves many synchronization problems.
            // Make sure this OPTION is not replacing itself.
            options = ( (HTMLElement) parent ).getElementsByTagName( "OPTION" );
            if ( options.item( index ) != this )
            {
                // Remove this OPTION from its parent. Place this OPTION right
                // before indexed OPTION underneath it's direct parent (might
                // be an OPTGROUP).
                getParentNode().removeChild( this );
                item = options.item( index );
                item.getParentNode().insertBefore( this, item );
            }
        }
    }
  
  
    public boolean getDisabled()
    {
        return getBinary( "disabled" );
    }
    
    
    public void setDisabled( boolean disabled )
    {
        setAttribute( "disabled", disabled );
    }

    
      public String getLabel()
    {
        return capitalize( getAttribute( "label" ) );
    }
    
    
    public void setLabel( String label )
    {
        setAttribute( "label", label );
    }

    
    public boolean getSelected()
    {
        return getBinary( "selected" );
    }
  
  
    public void setSelected( boolean selected )
    {
        setAttribute( "selected", selected );
    }
    
        
    public String getValue()
    {
        return getAttribute( "value" );
    }
    
    
    public void setValue( String value )
    {
        setAttribute( "value", value );
    }

    
    /**
     * Constructor requires owner document.
     * 
     * @param owner The owner HTML document
     */
    public HTMLOptionElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }


}


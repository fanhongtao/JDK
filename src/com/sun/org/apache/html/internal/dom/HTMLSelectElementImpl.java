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
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLOptionElement;
import org.w3c.dom.html.HTMLSelectElement;


/**
 * @version $Revision: 1.7 $ $Date: 2003/05/08 20:13:09 $
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLSelectElement
 * @see com.sun.org.apache.xerces.internal.dom.ElementImpl
 */
public class HTMLSelectElementImpl
    extends HTMLElementImpl
    implements HTMLSelectElement, HTMLFormControl
{
    
    
    public String getType()
    {
        return getAttribute( "type" );
    }

    
      public String getValue()
    {
        return getAttribute( "value" );
    }
    
    
    public void setValue( String value )
    {
        setAttribute( "value", value );
    }

    
    public int getSelectedIndex()
    {
        NodeList    options;
        int            i;
        
        // Use getElementsByTagName() which creates a snapshot of all the
        // OPTION elements under this SELECT. Access to the returned NodeList
        // is very fast and the snapshot solves many synchronization problems.
        // Locate the first selected OPTION and return its index. Note that
        // the OPTION might be under an OPTGROUP.
        options = getElementsByTagName( "OPTION" );
        for ( i = 0 ; i < options.getLength() ; ++i )
            if ( ( (HTMLOptionElement) options.item( i ) ).getSelected() )
                return i;
        return -1;
    }
    
    
    public void setSelectedIndex( int selectedIndex )
    {
        NodeList    options;
        int            i;
        
        // Use getElementsByTagName() which creates a snapshot of all the
        // OPTION elements under this SELECT. Access to the returned NodeList
        // is very fast and the snapshot solves many synchronization problems.
        // Change the select so all OPTIONs are off, except for the
        // selectIndex-th one.
        options = getElementsByTagName( "OPTION" );
        for ( i = 0 ; i < options.getLength() ; ++i )
            ( (HTMLOptionElementImpl) options.item( i ) ).setSelected( i == selectedIndex );
    }

  
    public HTMLCollection getOptions()
    {
        if ( _options == null )
            _options = new HTMLCollectionImpl( this, HTMLCollectionImpl.OPTION );
        return _options;
    }
    

    public int getLength()
    {
        return getOptions().getLength();
    }
    
    
    public boolean getDisabled()
    {
        return getBinary( "disabled" );
    }
    
    
    public void setDisabled( boolean disabled )
    {
        setAttribute( "disabled", disabled );
    }

    
      public boolean getMultiple()
    {
        return getBinary( "multiple" );
    }
    
    
    public void setMultiple( boolean multiple )
    {
        setAttribute( "multiple", multiple );
    }

  
      public String getName()
    {
        return getAttribute( "name" );
    }
    
    
    public void setName( String name )
    {
        setAttribute( "name", name );
    }

    
    public int getSize()
    {
        return getInteger( getAttribute( "size" ) );
    }
    
    
    public void setSize( int size )
    {
        setAttribute( "size", String.valueOf( size ) );
    }

  
    public int getTabIndex()
    {
        return getInteger( getAttribute( "tabindex" ) );
    }
    
    
    public void setTabIndex( int tabIndex )
    {
        setAttribute( "tabindex", String.valueOf( tabIndex ) );
    }

    
    public void add( HTMLElement element, HTMLElement before )
    {
        insertBefore( element, before );
    }
  
  
    public void remove( int index )
    {
        NodeList    options;
        Node        removed;
        
        // Use getElementsByTagName() which creates a snapshot of all the
        // OPTION elements under this SELECT. Access to the returned NodeList
        // is very fast and the snapshot solves many synchronization problems.
        // Remove the indexed OPTION from it's parent, this might be this
        // SELECT or an OPTGROUP.
        options = getElementsByTagName( "OPTION" );
        removed = options.item( index );
        if ( removed != null )
            removed.getParentNode().removeChild ( removed );
    }

  
    public void               blur()
    {
        // No scripting in server-side DOM. This method is moot.
    }
      
      
    public void               focus()
    {
        // No scripting in server-side DOM. This method is moot.
    }

    /*
     * Explicit implementation of getChildNodes() to avoid problems with
     * overriding the getLength() method hidden in the super class.
     */
    public NodeList getChildNodes() {
        return getChildNodesUnoptimized();
    }
  
    /**
     * Constructor requires owner document.
     * 
     * @param owner The owner HTML document
     */
    public HTMLSelectElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }


    private HTMLCollection    _options;
  
  
}


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
import org.w3c.dom.html.HTMLTableCellElement;
import org.w3c.dom.html.HTMLTableElement;
import org.w3c.dom.html.HTMLTableRowElement;
import org.w3c.dom.html.HTMLTableSectionElement;


/**
 * @version $Revision: 1.6 $ $Date: 2003/05/08 20:13:09 $
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLTableRowElement
 * @see com.sun.org.apache.xerces.internal.dom.ElementImpl
 */
public class HTMLTableRowElementImpl
    extends HTMLElementImpl
    implements HTMLTableRowElement
{

    
    public int getRowIndex()
    {
        Node    parent;
        
        parent = getParentNode();
        if ( parent instanceof HTMLTableSectionElement )
            parent = parent.getParentNode();
        if ( parent instanceof HTMLTableElement )
            return getRowIndex( parent );;
        return -1;
    }
    
    
    public void setRowIndex( int rowIndex )
    {
        Node    parent;
        
        parent = getParentNode();
        if ( parent instanceof HTMLTableSectionElement )
            parent = parent.getParentNode();
        if ( parent instanceof HTMLTableElement )
            ( (HTMLTableElementImpl) parent ).insertRowX( rowIndex, this );
    }

  
    public int getSectionRowIndex()
    {
        Node    parent;
        
        parent = getParentNode();
        if ( parent instanceof HTMLTableSectionElement )
            return getRowIndex( parent );
        else
            return -1;
    }
    
    
    public void setSectionRowIndex( int sectionRowIndex )
    {
        Node    parent;
        
        parent = getParentNode();
        if ( parent instanceof HTMLTableSectionElement )
            ( (HTMLTableSectionElementImpl) parent ).insertRowX( sectionRowIndex, this );
    }
  
  
    int getRowIndex( Node parent )
    {
        NodeList    rows;
        int            i;
        
        // Use getElementsByTagName() which creates a snapshot of all the
        // TR elements under the TABLE/section. Access to the returned NodeList
        // is very fast and the snapshot solves many synchronization problems.
        rows = ( (HTMLElement) parent ).getElementsByTagName( "TR" );
        for ( i = 0 ; i < rows.getLength() ; ++i )
            if ( rows.item( i ) == this )
                return i;
        return -1;
    }

  
    public HTMLCollection  getCells()
    {
        if ( _cells == null )
            _cells = new HTMLCollectionImpl( this, HTMLCollectionImpl.CELL );
        return _cells;
    }
    
    
    public void setCells( HTMLCollection cells )
    {
        Node    child;
        int        i;
        
        child = getFirstChild();
        while ( child != null )
        {
            removeChild( child );
            child = child.getNextSibling();
        }
        i = 0;
        child = cells.item( i );
        while ( child != null )
        {
            appendChild ( child );
            ++i;
            child = cells.item( i );
        }
    }

  
    public HTMLElement insertCell( int index )
    {
        Node        child;
        HTMLElement    newCell;
        
        newCell = new HTMLTableCellElementImpl( (HTMLDocumentImpl) getOwnerDocument(), "TD" );
        child = getFirstChild();
        while ( child != null )
        {
            if ( child instanceof HTMLTableCellElement )
            {
                if ( index == 0 )
                {
                    insertBefore( newCell, child );
                    return newCell;
                }
                --index;
            }
            child = child.getNextSibling();
        }
        appendChild( newCell );
        return newCell;
    }
    
    
    public void deleteCell( int index )
    {
        Node    child;
        
        child = getFirstChild();
        while ( child != null )
        {
            if ( child instanceof HTMLTableCellElement )
            {
                if ( index == 0 )
                {
                    removeChild ( child );
                    return;
                }
                --index;
            }
            child = child.getNextSibling();
        }
    }

  
    public String getAlign()
    {
        return capitalize( getAttribute( "align" ) );
    }
    
    
    public void setAlign( String align )
    {
        setAttribute( "align", align );
    }

    
    public String getBgColor()
    {
        return getAttribute( "bgcolor" );
    }
    
    
    public void setBgColor( String bgColor )
    {
        setAttribute( "bgcolor", bgColor );
    }

  
    public String getCh()
    {
        String    ch;
        
        // Make sure that the access key is a single character.
        ch = getAttribute( "char" );
        if ( ch != null && ch.length() > 1 )
            ch = ch.substring( 0, 1 );
        return ch;
    }
    
    
    public void setCh( String ch )
    {
        // Make sure that the access key is a single character.
        if ( ch != null && ch.length() > 1 )
            ch = ch.substring( 0, 1 );
        setAttribute( "char", ch );
    }

    
    public String getChOff()
    {
        return getAttribute( "charoff" );
    }
    
    
    public void setChOff( String chOff )
    {
        setAttribute( "charoff", chOff );
    }
  
  
    public String getVAlign()
    {
        return capitalize( getAttribute( "valign" ) );
    }
    
    
    public void setVAlign( String vAlign )
    {
        setAttribute( "valign", vAlign );
    }

    
      /**
     * Constructor requires owner document.
     * 
     * @param owner The owner HTML document
     */
    public HTMLTableRowElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }
  
  
    HTMLCollection    _cells;

  
}


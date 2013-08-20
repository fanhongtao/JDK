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
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLTableCaptionElement;
import org.w3c.dom.html.HTMLTableElement;
import org.w3c.dom.html.HTMLTableRowElement;
import org.w3c.dom.html.HTMLTableSectionElement;


/**
 * @version $Revision: 1.10 $ $Date: 2003/07/05 18:38:14 $
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLAnchorElement
 * @see com.sun.org.apache.xerces.internal.dom.ElementImpl
 */
public class HTMLTableElementImpl
    extends HTMLElementImpl
    implements HTMLTableElement
{
    
    
    public synchronized HTMLTableCaptionElement getCaption()
    {
        Node    child;
        
        child = getFirstChild();
        while ( child != null )
        {
            if ( child instanceof HTMLTableCaptionElement &&
                 child.getNodeName().equals( "CAPTION" ) )
                return (HTMLTableCaptionElement) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    
    public synchronized void setCaption( HTMLTableCaptionElement caption )
    {
        if ( caption != null && ! caption.getTagName().equals( "CAPTION" ) )
            throw new IllegalArgumentException( "HTM016 Argument 'caption' is not an element of type <CAPTION>." );
        deleteCaption();
        if ( caption != null )
            appendChild( caption );
    }
    
    
    public synchronized HTMLElement createCaption()
    {
        HTMLElement    section;
        
        section = getCaption();
        if ( section != null )
            return section;
        section = new HTMLTableCaptionElementImpl( (HTMLDocumentImpl) getOwnerDocument(), "CAPTION" );
        appendChild( section );
        return section;
    }

  
    public synchronized void deleteCaption()
    {
        Node    old;
        
        old = getCaption();
        if ( old != null )
            removeChild ( old );
    }
    
    public synchronized HTMLTableSectionElement getTHead()
    {
        Node    child;
        
        child = getFirstChild();
        while ( child != null )
        {
            if ( child instanceof HTMLTableSectionElement &&
                 child.getNodeName().equals( "THEAD" ) )
                return (HTMLTableSectionElement) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    
    public synchronized void setTHead( HTMLTableSectionElement tHead )
    {
        if ( tHead != null && ! tHead.getTagName().equals( "THEAD" ) )
            throw new IllegalArgumentException( "HTM017 Argument 'tHead' is not an element of type <THEAD>." );
        deleteTHead();
        if ( tHead != null )
            appendChild( tHead );
    }
    
    
    public synchronized HTMLElement createTHead()
    {
        HTMLElement    section;
        
        section = getTHead();
        if ( section != null )
            return section;
        section = new HTMLTableSectionElementImpl( (HTMLDocumentImpl) getOwnerDocument(), "THEAD" );
        appendChild( section );
        return section;
    }

    
    public synchronized void deleteTHead()
    {
        Node    old;
        
        old = getTHead();
        if ( old != null )
            removeChild ( old );
    }
    
    public synchronized HTMLTableSectionElement getTFoot()
    {
        Node    child;
        
        child = getFirstChild();
        while ( child != null )
        {
            if ( child instanceof HTMLTableSectionElement &&
                 child.getNodeName().equals( "TFOOT" ) )
                return (HTMLTableSectionElement) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    
    public synchronized void setTFoot( HTMLTableSectionElement tFoot )
    {
        if ( tFoot != null && ! tFoot.getTagName().equals( "TFOOT" ) )
            throw new IllegalArgumentException( "HTM018 Argument 'tFoot' is not an element of type <TFOOT>." );
        deleteTFoot();
        if ( tFoot != null )
            appendChild( tFoot );
    }
    
    
    public synchronized HTMLElement createTFoot()
    {
        HTMLElement    section;
        
        section = getTFoot();
        if ( section != null )
            return section;
        section = new HTMLTableSectionElementImpl( (HTMLDocumentImpl) getOwnerDocument(), "TFOOT" );
        appendChild( section );
        return section;
    }

    
    public synchronized void deleteTFoot()
    {
        Node    old;
        
        old = getTFoot();
        if ( old != null )
            removeChild ( old );
    }
    
    public HTMLCollection getRows()
    {
        if ( _rows == null )
            _rows = new HTMLCollectionImpl( this, HTMLCollectionImpl.ROW );
        return _rows;
    }
    

    public HTMLCollection getTBodies()
    {
        if ( _bodies == null )
            _bodies = new HTMLCollectionImpl( this, HTMLCollectionImpl.TBODY );
        return _bodies;
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
  
  
    public String getBorder()
    {
        return getAttribute( "border" );
    }
    
    
    public void setBorder( String border )
    {
        setAttribute( "border", border );
    }

    
    public String getCellPadding()
    {
        return getAttribute( "cellpadding" );
    }
    
    
    public void setCellPadding( String cellPadding )
    {
        setAttribute( "cellpadding", cellPadding );
    }
    
    
    public String getCellSpacing()
    {
        return getAttribute( "cellspacing" );
    }
    
    
    public void setCellSpacing( String cellSpacing )
    {
        setAttribute( "cellspacing", cellSpacing );
    }
    
    
    public String getFrame()
    {
        return capitalize( getAttribute( "frame" ) );
    }
    
    
    public void setFrame( String frame )
    {
        setAttribute( "frame", frame );
    }
    
    
    public String getRules()
    {
        return capitalize( getAttribute( "rules" ) );
    }
    
    
    public void setRules( String rules )
    {
        setAttribute( "rules", rules );
    }
    
    
    public String getSummary()
    {
        return getAttribute( "summary" );
    }
    
    
    public void setSummary( String summary )
    {
        setAttribute( "summary", summary );
    }

  
      public String getWidth()
    {
        return getAttribute( "width" );
    }
    
    
    public void setWidth( String width )
    {
        setAttribute( "width", width );
    }

    
    public HTMLElement insertRow( int index )
    {
        HTMLTableRowElementImpl    newRow;

        newRow = new HTMLTableRowElementImpl( (HTMLDocumentImpl) getOwnerDocument(), "TR" );
        //newRow.insertCell( 0 );
        insertRowX( index, newRow );
        return newRow;
    }
        
        
    void insertRowX( int index, HTMLTableRowElementImpl newRow )
    {
        Node    child;
        Node    lastSection = null;
                
        child = getFirstChild();
        while ( child != null )
        {
            if ( child instanceof HTMLTableRowElement )
            {
                if ( index == 0 )
                {
                    insertBefore( newRow, child );
                    return;
                }
            }
            else
            if ( child instanceof HTMLTableSectionElementImpl )
            {
                lastSection = child;
                index = ( (HTMLTableSectionElementImpl) child ).insertRowX( index, newRow );
                if ( index < 0 )
                    return;
            }
            child = child.getNextSibling();
        }
        if ( lastSection != null )
            lastSection.appendChild( newRow );
        else
            appendChild( newRow );
    }
    
    
    public synchronized void deleteRow( int index )
    {
        Node    child;
        
        child = getFirstChild();
        while ( child != null )
        {
            if ( child instanceof HTMLTableRowElement )
            {
                if ( index == 0 )
                {
                    removeChild ( child );
                    return;
                }
                --index;
            }
            else
            if ( child instanceof HTMLTableSectionElementImpl )
            {
                index = ( (HTMLTableSectionElementImpl) child ).deleteRowX( index );
                if ( index < 0 )
                    return;
            }
            child = child.getNextSibling();
        }
    }

  
    /**
     * Constructor requires owner document.
     * 
     * @param owner The owner HTML document
     */
    public HTMLTableElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }
    
  
    private HTMLCollectionImpl    _rows;
    
    
    private HTMLCollectionImpl    _bodies;
  
    
}


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


import org.w3c.dom.html.HTMLObjectElement;
import org.w3c.dom.Document;


/**
 * @version $Revision: 1.2 $ $Date: 2003/11/18 00:22:49 $
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLObjectElement
 * @see com.sun.org.apache.xerces.internal.dom.ElementImpl
 */
public class HTMLObjectElementImpl
    extends HTMLElementImpl
    implements HTMLObjectElement, HTMLFormControl
{
    
    

    public String getCode()
    {
        return getAttribute( "code" );
    }
    
    
    public void setCode( String code )
    {
        setAttribute( "code", code );
    }

  
    public String getAlign()
    {
        return capitalize( getAttribute( "align" ) );
    }
    
    
    public void setAlign( String align )
    {
        setAttribute( "align", align );
    }
  
    
    public String getArchive()
    {
        return getAttribute( "archive" );
    }
    
    
    public void setArchive( String archive )
    {
        setAttribute( "archive", archive );
    }
    
    public String getBorder()
    {
        return getAttribute( "border" );
    }
    
    
    public void setBorder( String border )
    {
        setAttribute( "border", border );
    }

    
    public String getCodeBase()
    {
        return getAttribute( "codebase" );
    }
    
    
    public void setCodeBase( String codeBase )
    {
        setAttribute( "codebase", codeBase );
    }

    
    public String getCodeType()
    {
        return getAttribute( "codetype" );
    }
    
    
    public void setCodeType( String codeType )
    {
        setAttribute( "codetype", codeType );
    }

    
    public String getData()
    {
        return getAttribute( "data" );
    }
    
    
    public void setData( String data )
    {
        setAttribute( "data", data );
    }

  
      public boolean getDeclare()
    {
        return getBinary( "declare" );
    }
    
    
    public void setDeclare( boolean declare )
    {
        setAttribute( "declare", declare );
    }

    
    public String getHeight()
    {
        return getAttribute( "height" );
    }
    
    
    public void setHeight( String height )
    {
        setAttribute( "height", height );
    }

    
    public String getHspace()
    {
        return getAttribute( "hspace" );
    }
    
    
    public void setHspace( String hspace )
    {
        setAttribute( "hspace", hspace );
    }
  
    public String getName()
    {
        return getAttribute( "name" );
    }
    
    
    public void setName( String name )
    {
        setAttribute( "name", name );
    }

    
    public String getStandby()
    {
        return getAttribute( "standby" );
    }
    
    
    public void setStandby( String standby )
    {
        setAttribute( "standby", standby );
    }
  
      public int getTabIndex()
    {
        try
        {
            return Integer.parseInt( getAttribute( "tabindex" ) );
        }
        catch ( NumberFormatException except )
        {
            return 0;
        }
    }
    
    
    public void setTabIndex( int tabIndex )
    {
        setAttribute( "tabindex", String.valueOf( tabIndex ) );
    }

    
    public String getType()
    {
        return getAttribute( "type" );
    }
    
    
    public void setType( String type )
    {
        setAttribute( "type", type );
    }
    
    
    public String getUseMap()
    {
        return getAttribute( "useMap" );
    }
    
    
    public void setUseMap( String useMap )
    {
        setAttribute( "useMap", useMap );
    }
    
    
    public String getVspace()
    {
        return getAttribute( "vspace" );
    }
    
    
    public void setVspace( String vspace )
    {
        setAttribute( "vspace", vspace );
    }

  
    public String getWidth()
    {
        return getAttribute( "width" );
    }
    
    
    public void setWidth( String width )
    {
        setAttribute( "width", width );
    }

    
    
    /**
     * Constructor requires owner document.
     * 
     * @param owner The owner HTML document
     */
    public HTMLObjectElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }

    public Document getContentDocument( ) 
    {
        return null; //PENDING
    }
}


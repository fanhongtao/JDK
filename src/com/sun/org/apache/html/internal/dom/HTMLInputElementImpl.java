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


import org.w3c.dom.html.HTMLInputElement;


/**
 * @version $Revision: 1.6 $ $Date: 2003/05/08 20:13:09 $
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLInputElement
 * @see com.sun.org.apache.xerces.internal.dom.ElementImpl
 */
public class HTMLInputElementImpl
    extends HTMLElementImpl
    implements HTMLInputElement, HTMLFormControl
{
    
    
    public String getDefaultValue()
    {
        // ! NOT FULLY IMPLEMENTED !
        return getAttribute( "defaultValue" );
    }
    
    
    public void setDefaultValue( String defaultValue )
    {
        // ! NOT FULLY IMPLEMENTED !
        setAttribute( "defaultValue", defaultValue );
    }
    
    
    public boolean getDefaultChecked()
    {
        // ! NOT FULLY IMPLEMENTED !
        return getBinary( "defaultChecked" );
    }
    
    
    public void setDefaultChecked( boolean defaultChecked )
    {
        // ! NOT FULLY IMPLEMENTED !
        setAttribute( "defaultChecked", defaultChecked );
    }
  
    
    public String getAccept()
    {
        return getAttribute( "accept" );
    }
    
    
    public void setAccept( String accept )
    {
        setAttribute( "accept", accept );
    }   
    
    
    public String getAccessKey()
    {
        String    accessKey;
        
        // Make sure that the access key is a single character.
        accessKey = getAttribute( "accesskey" );
        if ( accessKey != null && accessKey.length() > 1 )
            accessKey = accessKey.substring( 0, 1 );
        return accessKey;
    }
    
    
    public void setAccessKey( String accessKey )
    {
        // Make sure that the access key is a single character.    
        if ( accessKey != null && accessKey.length() > 1 )
            accessKey = accessKey.substring( 0, 1 );
        setAttribute( "accesskey", accessKey );
    }
    
    
    public String getAlign()
    {
        return capitalize( getAttribute( "align" ) );
    }
    
    
    public void setAlign( String align )
    {
        setAttribute( "align", align );
    }
    
    
    public String getAlt()
    {
        return getAttribute( "alt" );
    }
    
    
    public void setAlt( String alt )
    {
        setAttribute( "alt", alt );
    }
    
    
    public boolean getChecked()
    {
        return getBinary( "checked" );
    }
    
    
    public void setChecked( boolean checked )
    {
        setAttribute( "checked", checked );
    }
  
    
    public boolean getDisabled()
    {
        return getBinary( "disabled" );
    }
    
    
    public void setDisabled( boolean disabled )
    {
        setAttribute( "disabled", disabled );
    }
    
    
    public int getMaxLength()
    {
        return getInteger( getAttribute( "maxlength" ) );
    }
    
    
    public void setMaxLength( int maxLength )
    {
        setAttribute( "maxlength", String.valueOf( maxLength ) );
    }
    
    
    public String getName()
    {
        return getAttribute( "name" );
    }
    
    
    public void setName( String name )
    {
        setAttribute( "name", name );
    }
    
    
    public boolean getReadOnly()
    {
        return getBinary( "readonly" );
    }
    
    
    public void setReadOnly( boolean readOnly )
    {
        setAttribute( "readonly", readOnly );
    }
    
    
    public String getSize()
    {
        return getAttribute( "size" );
    }
    
    
    public void setSize( String size )
    {
        setAttribute( "size", size );
    }
    
    
    public String getSrc()
    {
        return getAttribute( "src" );
    }
    
    
    public void setSrc( String src )
    {
        setAttribute( "src", src );
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
    
    
    public String getUseMap()
    {
        return getAttribute( "useMap" );
    }
    
    
    public void setUseMap( String useMap )
    {
        setAttribute( "useMap", useMap );
    }
    
    
    public String getValue()
    {
        return getAttribute( "value" );
    }
    
    
    public void setValue( String value )
    {
        setAttribute( "value", value );
    }
    
    
    public void blur()
    {
        // No scripting in server-side DOM. This method is moot.
    }
    
    
    public void focus()
    {
        // No scripting in server-side DOM. This method is moot.
    }
    
    
    public void select()
    {
        // No scripting in server-side DOM. This method is moot.
    }
    
    
    public void click()
    {
        // No scripting in server-side DOM. This method is moot.
    }

  
    /**
     * Constructor requires owner document.
     * 
     * @param owner The owner HTML document
     */
    public HTMLInputElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }


}



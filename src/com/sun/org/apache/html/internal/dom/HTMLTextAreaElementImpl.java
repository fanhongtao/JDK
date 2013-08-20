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


import org.w3c.dom.html.HTMLTextAreaElement;


/**
 * @version $Revision: 1.6 $ $Date: 2003/05/08 20:13:09 $
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLTextAreaElement
 * @see com.sun.org.apache.xerces.internal.dom.ElementImpl
 */
public class HTMLTextAreaElementImpl
    extends HTMLElementImpl
    implements HTMLTextAreaElement, HTMLFormControl
{
    
    
    public String getDefaultValue()
    {
        // ! NOT FULLY IMPLEMENTED !
        return getAttribute( "default-value" );
    }
    
    
    public void setDefaultValue( String defaultValue )
    {
        // ! NOT FULLY IMPLEMENTED !
        setAttribute( "default-value", defaultValue );
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

    
    public int getCols()
    {
        return getInteger( getAttribute( "cols" ) );
    }
    
    
    public void setCols( int cols )
    {
        setAttribute( "cols", String.valueOf( cols ) );
    }
  
  
    public boolean getDisabled()
    {
        return getBinary( "disabled" );
    }
    
    
    public void setDisabled( boolean disabled )
    {
        setAttribute( "disabled", disabled );
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

    
       public int getRows()
    {
        return getInteger( getAttribute( "rows" ) );
    }
    
    
    public void setRows( int rows )
    {
        setAttribute( "rows", String.valueOf( rows ) );
    }

  
       public int getTabIndex()
    {
        return getInteger( getAttribute( "tabindex" ) );
    }
    
    
    public void setTabIndex( int tabIndex )
    {
        setAttribute( "tabindex", String.valueOf( tabIndex ) );
    }

  
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
    
      
      /**
     * Constructor requires owner document.
     * 
     * @param owner The owner HTML document
     */
    public HTMLTextAreaElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }

  
}


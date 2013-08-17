/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * 4. The names "Xalan" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.utils;


/**
 * The standard SAX implementation of LocatorImpl is not serializable,
 * limiting its utility as "a persistent snapshot of a locator".
 * This is a quick hack to make it so. Note that it makes more sense
 * in many cases to set up fields to hold this data rather than pointing
 * at another object... but that decision should be made on architectural
 * grounds rather than serializability.
 *<p>
 * It isn't clear whether subclassing LocatorImpl and adding serialization
 * methods makes more sense than copying it and just adding Serializable
 * to its interface. Since it's so simple, I've taken the latter approach
 * for now.
 *
 * @see org.xml.sax.helpers.LocatorImpl
 * @see org.xml.sax.Locator Locator
 * @since XalanJ2
 * @author Joe Kesselman
 * @version 1.0
 */
public class SerializableLocatorImpl
implements org.xml.sax.Locator, java.io.Serializable

{
    /**
     * Zero-argument constructor.
     *
     * <p>SAX says "This will not normally be useful, since the main purpose
     * of this class is to make a snapshot of an existing Locator." In fact,
     * it _is_ sometimes useful when you want to construct a new Locator
     * pointing to a specific location... which, after all, is why the
     * setter methods are provided.
     * </p>
     */
    public SerializableLocatorImpl ()
    {
    }
    
    
    /**
     * Copy constructor.
     *
     * <p>Create a persistent copy of the current state of a locator.
     * When the original locator changes, this copy will still keep
     * the original values (and it can be used outside the scope of
     * DocumentHandler methods).</p>
     *
     * @param locator The locator to copy.
     */
    public SerializableLocatorImpl (org.xml.sax.Locator locator)
    {
        setPublicId(locator.getPublicId());
        setSystemId(locator.getSystemId());
        setLineNumber(locator.getLineNumber());
        setColumnNumber(locator.getColumnNumber());
    }
    
    
    ////////////////////////////////////////////////////////////////////
    // Implementation of org.xml.sax.Locator
    ////////////////////////////////////////////////////////////////////
    
    
    /**
     * Return the saved public identifier.
     *
     * @return The public identifier as a string, or null if none
     *         is available.
     * @see org.xml.sax.Locator#getPublicId
     * @see #setPublicId
     */
    public String getPublicId ()
    {
        return publicId;
    }
    
    
    /**
     * Return the saved system identifier.
     *
     * @return The system identifier as a string, or null if none
     *         is available.
     * @see org.xml.sax.Locator#getSystemId
     * @see #setSystemId
     */
    public String getSystemId ()
    {
        return systemId;
    }
    
    
    /**
     * Return the saved line number (1-based).
     *
     * @return The line number as an integer, or -1 if none is available.
     * @see org.xml.sax.Locator#getLineNumber
     * @see #setLineNumber
     */
    public int getLineNumber ()
    {
        return lineNumber;
    }
    
    
    /**
     * Return the saved column number (1-based).
     *
     * @return The column number as an integer, or -1 if none is available.
     * @see org.xml.sax.Locator#getColumnNumber
     * @see #setColumnNumber
     */
    public int getColumnNumber ()
    {
        return columnNumber;
    }
    
    
    ////////////////////////////////////////////////////////////////////
    // Setters for the properties (not in org.xml.sax.Locator)
    ////////////////////////////////////////////////////////////////////
    
    
    /**
     * Set the public identifier for this locator.
     *
     * @param publicId The new public identifier, or null 
     *        if none is available.
     * @see #getPublicId
     */
    public void setPublicId (String publicId)
    {
        this.publicId = publicId;
    }
    
    
    /**
     * Set the system identifier for this locator.
     *
     * @param systemId The new system identifier, or null 
     *        if none is available.
     * @see #getSystemId
     */
    public void setSystemId (String systemId)
    {
        this.systemId = systemId;
    }
    
    
    /**
     * Set the line number for this locator (1-based).
     *
     * @param lineNumber The line number, or -1 if none is available.
     * @see #getLineNumber
     */
    public void setLineNumber (int lineNumber)
    {
        this.lineNumber = lineNumber;
    }
    
    
    /**
     * Set the column number for this locator (1-based).
     *
     * @param columnNumber The column number, or -1 if none is available.
     * @see #getColumnNumber
     */
    public void setColumnNumber (int columnNumber)
    {
        this.columnNumber = columnNumber;
    }
    
    
    ////////////////////////////////////////////////////////////////////
    // Internal state.
    ////////////////////////////////////////////////////////////////////
    
    /**
     * The public ID.
     * @serial
     */
    private String publicId;
    
    /**
     * The system ID.
     * @serial
     */
    private String systemId;
    
    /**
     * The line number.
     * @serial
     */
    private int lineNumber;
    
    /**
     * The column number.
     * @serial
     */
    private int columnNumber;
    
}

// end of LocatorImpl.java

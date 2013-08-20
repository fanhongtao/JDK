/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  
 * All rights reserved.
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

package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XNIException;

/**
 * An XNI parser configuration exception. This exception class extends
 * <code>XNIException</code> in order to differentiate between general
 * parsing errors and configuration errors.
 *
 * @author Andy Clark, IBM
 *
 * @version $Id: XMLConfigurationException.java,v 1.4 2002/01/29 01:15:19 lehors Exp $
 */
public class XMLConfigurationException
    extends XNIException {

    //
    // Constants
    //

    /** Exception type: identifier not recognized. */
    public static final short NOT_RECOGNIZED = 0;

    /** Exception type: identifier not supported. */
    public static final short NOT_SUPPORTED = 1;

    //
    // Data
    //

    /** Exception type. */
    protected short fType;

    /** Identifier. */
    protected String fIdentifier;

    //
    // Constructors
    //

    /** 
     * Constructs a configuration exception with the specified type
     * and feature/property identifier.
     *
     * @param type       The type of the exception.
     * @param identifier The feature or property identifier.
     *
     * @see #NOT_RECOGNIZED
     * @see #NOT_SUPPORTED
     */
    public XMLConfigurationException(short type, String identifier) {
        super(identifier);
        fType = type;
        fIdentifier = identifier;
    } // <init>(short,String)

    /** 
     * Constructs a configuration exception with the specified type,
     * feature/property identifier, and error message
     *
     * @param type       The type of the exception.
     * @param identifier The feature or property identifier.
     * @param message    The error message.
     *
     * @see #NOT_RECOGNIZED
     * @see #NOT_SUPPORTED
     */
    public XMLConfigurationException(short type, String identifier,
                                     String message) {
        super(message);
        fType = type;
        fIdentifier = identifier;
    } // <init>(short,String,String)

    //
    // Public methods
    //

    /** 
     * Returns the exception type. 
     *
     * @see #NOT_RECOGNIZED
     * @see #NOT_SUPPORTED
     */
    public short getType() {
        return fType;
    } // getType():short

    /** Returns the feature or property identifier. */
    public String getIdentifier() {
        return fIdentifier;
    } // getIdentifier():String

} // class XMLConfigurationException

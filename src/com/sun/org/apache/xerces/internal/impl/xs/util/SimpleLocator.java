/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002, 2003 The Apache Software Foundation.  All rights 
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
 * originally based on software copyright (c) 2001, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.xni.XMLLocator;

/**
 * An XMLLocator implementation used for schema error reporting.
 * 
 * @author Sandy Gao, IBM
 * @version $Id: SimpleLocator.java,v 1.3 2003/03/26 04:19:23 neilg Exp $
 */
public class SimpleLocator implements XMLLocator {
    String lsid, esid;
    int line, column;
    
    public SimpleLocator() {
    }
    
    public SimpleLocator(String lsid, String esid, int line, int column) {
        this.line = line;
        this.column = column;
        this.lsid = lsid;
        this.esid = esid;
    }
    
    public void setValues(String lsid, String esid, int line, int column) {
        this.line = line;
        this.column = column;
        this.lsid = lsid;
        this.esid = esid;
    }
    
    public int getLineNumber() {
        return line;
    }
    
    public int getColumnNumber() {
        return column;
    }

    public String getPublicId() {
        return null;
    }

    public String getExpandedSystemId() {
        return esid;
    }

    public String getLiteralSystemId() {
        return lsid;
    }

    public String getBaseSystemId() {
        return null;
    }
	/**
	 * @see com.sun.org.apache.xerces.internal.xni.XMLLocator#setColumnNumber(int)
	 */
	public void setColumnNumber(int col) {
        this.column = col;
    }

	/**
	 * @see com.sun.org.apache.xerces.internal.xni.XMLLocator#setLineNumber(int)
	 */
	public void setLineNumber(int line) {
        this.line = line;
    }

	/**
	 * @see com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier#setBaseSystemId(String)
	 */
	public void setBaseSystemId(String systemId) {}

	/**
	 * @see com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier#setExpandedSystemId(String)
	 */
	public void setExpandedSystemId(String systemId) {
        esid = systemId;
    }

	/**
	 * @see com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier#setLiteralSystemId(String)
	 */
	public void setLiteralSystemId(String systemId) {
        lsid = systemId;
    }

	/**
	 * @see com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier#setPublicId(String)
	 */
	public void setPublicId(String publicId) {}

    /** Returns the encoding of the current entity.  
     * Since these locators are used in the construction of
     * XMLParseExceptions, which know nothing about encodings, there is
     * no point in having this object deal intelligently 
     * with encoding information.
     */
    public String getEncoding() {
        return null;
    }

}

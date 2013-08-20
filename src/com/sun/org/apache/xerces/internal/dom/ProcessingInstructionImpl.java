/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights 
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

package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

/**
 * Processing Instructions (PIs) permit documents to carry
 * processor-specific information alongside their actual content. PIs
 * are most common in XML, but they are supported in HTML as well.
 *
 * This class inherits from CharacterDataImpl to reuse its setNodeValue method.
 *
 * @version $Id: ProcessingInstructionImpl.java,v 1.12 2003/01/17 22:40:54 elena Exp $
 * @since  PR-DOM-Level-1-19980818.
 */
public class ProcessingInstructionImpl
    extends CharacterDataImpl
    implements ProcessingInstruction {

    //
    // Constants
    //

    /** Serialization version. */
    static final long serialVersionUID = 7554435174099981510L;

    //
    // Data
    //

    protected String target;

    //
    // Constructors
    //

    /** Factory constructor. */
    public ProcessingInstructionImpl(CoreDocumentImpl ownerDoc,
                                     String target, String data) {
        super(ownerDoc, data);
        this.target = target;
    }

    //
    // Node methods
    //

    /**
     * A short integer indicating what type of node this is. The named
     * constants for this value are defined in the org.w3c.dom.Node interface.
     */
    public short getNodeType() {
        return Node.PROCESSING_INSTRUCTION_NODE;
    }

    /**
     * Returns the target
     */
    public String getNodeName() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return target;
    }

    //
    // ProcessingInstruction methods
    //

    /**
     * A PI's "target" states what processor channel the PI's data
     * should be directed to. It is defined differently in HTML and XML.
     * <p>
     * In XML, a PI's "target" is the first (whitespace-delimited) token
     * following the "<?" token that begins the PI.
     * <p>
     * In HTML, target is always null.
     * <p>
     * Note that getNodeName is aliased to getTarget.
     */
    public String getTarget() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return target;

    } // getTarget():String

    /**
     * A PI's data content tells the processor what we actually want it
     * to do.  It is defined slightly differently in HTML and XML.
     * <p>
     * In XML, the data begins with the non-whitespace character
     * immediately after the target -- @see getTarget().
     * <p>
     * In HTML, the data begins with the character immediately after the
     * "&lt;?" token that begins the PI.
     * <p>
     * Note that getNodeValue is aliased to getData
     */
    public String getData() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return data;

    } // getData():String

    /**
     * Change the data content of this PI.
     * Note that setData is aliased to setNodeValue.
     * @see #getData().
     * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) if node is read-only.
     */
    public void setData(String data) {
        // Hand off to setNodeValue for code-reuse reasons (mutation
        // events, readonly protection, synchronizing, etc.)
        setNodeValue(data);
    } // setData(String)



   /**
     * DOM Level 3 WD - Experimental.
     * Retrieve baseURI
     */
    public String getBaseURI() {

        if (needsSyncData()) {
            synchronizeData();
        }
        return ownerNode.getBaseURI();
    }


} // class ProcessingInstructionImpl

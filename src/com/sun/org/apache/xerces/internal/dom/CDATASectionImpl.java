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

import org.w3c.dom.CDATASection;
import org.w3c.dom.Node;

/**
 * XML provides the CDATA markup to allow a region of text in which
 * most of the XML delimiter recognition does not take place. This is
 * intended to ease the task of quoting XML fragments and other
 * programmatic information in a document's text without needing to
 * escape these special characters. It's primarily a convenience feature
 * for those who are hand-editing XML.
 * <P>
 * CDATASection is an Extended DOM feature, and is not used in HTML 
 * contexts.
 * <P>
 * Within the DOM, CDATASections are treated essentially as Text
 * blocks. Their distinct type is retained in order to allow us to
 * properly recreate the XML syntax when we write them out.
 * <P>
 * Reminder: CDATA IS NOT A COMPLETELY GENERAL SOLUTION; it can't
 * quote its own end-of-block marking. If you need to write out a
 * CDATA that contains the ]]> sequence, it's your responsibility to
 * split that string over two successive CDATAs at that time.
 * <P>
 * CDATA does not participate in Element.normalize() processing.
 *
 * @version $Id: CDATASectionImpl.java,v 1.6 2002/01/29 01:15:07 lehors Exp $
 * @since  PR-DOM-Level-1-19980818.
 */
public class CDATASectionImpl 
    extends TextImpl 
    implements CDATASection {

    //
    // Constants
    //

    /** Serialization version. */
    static final long serialVersionUID = 2372071297878177780L;

    //
    // Constructors
    //

    /** Factory constructor for creating a CDATA section. */
    public CDATASectionImpl(CoreDocumentImpl ownerDoc, String data) {
        super(ownerDoc, data);
    }  
    
    //
    // Node methods
    //

    /** 
     * A short integer indicating what type of node this is. The named
     * constants for this value are defined in the org.w3c.dom.Node interface.
     */
    public short getNodeType() {
        return Node.CDATA_SECTION_NODE;
    }
  
    /** Returns the node name. */
    public String getNodeName() {
        return "#cdata-section";
    }

} // class CDATASectionImpl

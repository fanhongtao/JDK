/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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

package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSNotationDecl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import org.w3c.dom.Element;

/**
 * The notation declaration schema component traverser.
 *
 * <notation
 *   id = ID
 *   name = NCName
 *   public = anyURI
 *   system = anyURI
 *   {any attributes with non-schema namespace . . .}>
 *   Content: (annotation?)
 * </notation>
 *
 * @author Rahul Srivastava, Sun Microsystems Inc.
 * @author Elena Litani, IBM
 * @version $Id: XSDNotationTraverser.java,v 1.12 2004/01/22 15:22:53 sandygao Exp $
 */
class  XSDNotationTraverser extends XSDAbstractTraverser {

    XSDNotationTraverser (XSDHandler handler,
                          XSAttributeChecker gAttrCheck) {
        super(handler, gAttrCheck);
    }

    XSNotationDecl traverse(Element elmNode,
                            XSDocumentInfo schemaDoc,
                            SchemaGrammar grammar) {

        // General Attribute Checking for elmNode
        Object[] attrValues = fAttrChecker.checkAttributes(elmNode, true, schemaDoc);
        //get attributes
        String  nameAttr   = (String) attrValues[XSAttributeChecker.ATTIDX_NAME];

        String  publicAttr = (String) attrValues[XSAttributeChecker.ATTIDX_PUBLIC];
        String  systemAttr = (String) attrValues[XSAttributeChecker.ATTIDX_SYSTEM];
        if (nameAttr == null) {
            reportSchemaError("s4s-att-must-appear", new Object[]{SchemaSymbols.ELT_NOTATION, SchemaSymbols.ATT_NAME}, elmNode);
            fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            return null;
        }

		if (systemAttr == null && publicAttr == null)
			reportSchemaError("s4s-att-must-appear", new Object[]{SchemaSymbols.ELT_NOTATION, "system or public"}, elmNode);
        XSNotationDecl notation = new XSNotationDecl();
        notation.fName = nameAttr;
        notation.fTargetNamespace = schemaDoc.fTargetNamespace;
        notation.fPublicId = publicAttr;
        notation.fSystemId = systemAttr;

        //check content
        Element content = DOMUtil.getFirstChildElement(elmNode);
        XSAnnotationImpl annotation = null;

        if (content != null) {
            // traverse annotation if any
            if (DOMUtil.getLocalName(content).equals(SchemaSymbols.ELT_ANNOTATION)) {
                annotation = traverseAnnotationDecl(content, attrValues, false, schemaDoc);
                content = DOMUtil.getNextSiblingElement(content);
            }
        }
        notation.fAnnotation = annotation;
        if (content!=null){
             Object[] args = new Object [] {SchemaSymbols.ELT_NOTATION, "(annotation?)", DOMUtil.getLocalName(content)};
             reportSchemaError("s4s-elt-must-match.1", args, content);

        }
        grammar.addGlobalNotationDecl(notation);
        fAttrChecker.returnAttrArray(attrValues, schemaDoc);

        return notation;
    }
}

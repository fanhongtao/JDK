/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;

import  com.sun.org.apache.xerces.internal.util.DOMUtil;
import  org.w3c.dom.Element;
import com.sun.org.apache.xerces.internal.impl.xs.identity.*;
import com.sun.org.apache.xerces.internal.impl.xpath.*;

/**
 * This class contains code that all three IdentityConstraint
 * traversers (the XSDUniqueTraverser, XSDKeyTraverser and
 * XSDKeyrefTraverser) rely upon.
 *
 * @version $Id: XSDAbstractIDConstraintTraverser.java,v 1.8 2003/06/23 16:35:22 neilg Exp $
 */
class XSDAbstractIDConstraintTraverser extends XSDAbstractTraverser {

    public XSDAbstractIDConstraintTraverser (XSDHandler handler,
                                  XSAttributeChecker gAttrCheck) {
        super(handler, gAttrCheck);
    }

    void traverseIdentityConstraint(IdentityConstraint ic,
            Element icElem, XSDocumentInfo schemaDoc, Object [] icElemAttrs) {

        // General Attribute Checking will have been done on icElem by caller

        // check for <annotation> and get selector
        Element sElem = DOMUtil.getFirstChildElement(icElem);
        if(sElem == null) {
            reportSchemaError("s4s-elt-must-match.2",
                              new Object[]{"identity constraint", "(annotation?, selector, field+)"},
                              icElem);
            return;
        }

        // General Attribute Checking on sElem
        // first child could be an annotation
        if (DOMUtil.getLocalName(sElem).equals(SchemaSymbols.ELT_ANNOTATION)) {
            ic.addAnnotation(traverseAnnotationDecl(sElem, icElemAttrs, false, schemaDoc));
            sElem = DOMUtil.getNextSiblingElement(sElem);
        }
        // if no more children report an error
        if(sElem == null) {
            reportSchemaError("s4s-elt-must-match.2", new Object[]{"identity constraint", "(annotation?, selector, field+)"}, icElem);
            return;
        }
        Object [] attrValues = fAttrChecker.checkAttributes(sElem, false, schemaDoc);
        
        // if more than one annotation report an error
        if(!DOMUtil.getLocalName(sElem).equals(SchemaSymbols.ELT_SELECTOR)) {
            reportSchemaError("s4s-elt-must-match.1", new Object[]{"identity constraint", "(annotation?, selector, field+)", SchemaSymbols.ELT_SELECTOR}, sElem);
        }
        // and make sure <selector>'s content is fine:
        Element selChild = DOMUtil.getFirstChildElement(sElem);
        
        if (selChild !=null) {
            // traverse annotation if any
            if (DOMUtil.getLocalName(selChild).equals(SchemaSymbols.ELT_ANNOTATION)) {
                ic.addAnnotation(traverseAnnotationDecl(selChild, attrValues, false, schemaDoc));
                selChild = DOMUtil.getNextSiblingElement(selChild);
            }
            else {
                reportSchemaError("s4s-elt-must-match.1", new Object[]{SchemaSymbols.ELT_SELECTOR, "(annotation?)", DOMUtil.getLocalName(selChild)}, selChild);
            }
            if (selChild != null) {
                reportSchemaError("s4s-elt-must-match.1", new Object [] {SchemaSymbols.ELT_SELECTOR, "(annotation?)", DOMUtil.getLocalName(selChild)}, selChild);
            }
        }

        String sText = ((String)attrValues[XSAttributeChecker.ATTIDX_XPATH]);
        if(sText == null) {
            reportSchemaError("s4s-att-must-appear", new Object [] {SchemaSymbols.ELT_SELECTOR, SchemaSymbols.ATT_XPATH}, sElem);
            return;
        }
        sText = sText.trim();

        Selector.XPath sXpath = null;
        try {
            sXpath = new Selector.XPath(sText, fSymbolTable,
                                        schemaDoc.fNamespaceSupport);
            Selector selector = new Selector(sXpath, ic);
            ic.setSelector(selector);
        }
        catch (XPathException e) {
            reportSchemaError(e.getKey(), new Object[]{sText}, sElem);
            // put back attr values...
            fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            return;
        }

        // put back attr values...
        fAttrChecker.returnAttrArray(attrValues, schemaDoc);

        // get fields
        Element fElem = DOMUtil.getNextSiblingElement(sElem);
        if(fElem == null) {
            reportSchemaError("s4s-elt-must-match.2", new Object[]{"identity constraint", "(annotation?, selector, field+)"}, sElem);
        }
        while (fElem != null) {
            // General Attribute Checking
            attrValues = fAttrChecker.checkAttributes(fElem, false, schemaDoc);

            if(!DOMUtil.getLocalName(fElem).equals(SchemaSymbols.ELT_FIELD))
                reportSchemaError("s4s-elt-must-match.1", new Object[]{"identity constraint", "(annotation?, selector, field+)", SchemaSymbols.ELT_FIELD}, fElem);
            
            // and make sure <field>'s content is fine:
            Element fieldChild = DOMUtil.getFirstChildElement(fElem);
            if (fieldChild != null) {            
                // traverse annotation
                if (DOMUtil.getLocalName(fieldChild).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    ic.addAnnotation(traverseAnnotationDecl(fieldChild, attrValues, false, schemaDoc));
                    fieldChild = DOMUtil.getNextSiblingElement(fieldChild);
                }
            }
            if (fieldChild != null) {
                reportSchemaError("s4s-elt-must-match.1", new Object [] {SchemaSymbols.ELT_FIELD, "(annotation?)", DOMUtil.getLocalName(fieldChild)}, fieldChild);
            }
            String fText = ((String)attrValues[XSAttributeChecker.ATTIDX_XPATH]);
            if(fText == null) {
                reportSchemaError("s4s-att-must-appear", new Object [] {SchemaSymbols.ELT_FIELD, SchemaSymbols.ATT_XPATH}, fElem);
                return;
            }
            fText = fText.trim();
            try {
                Field.XPath fXpath = new Field.XPath(fText, fSymbolTable,
                                                     schemaDoc.fNamespaceSupport);
                Field field = new Field(fXpath, ic);
                ic.addField(field);
            }
            catch (XPathException e) {
                reportSchemaError(e.getKey(), new Object[]{fText}, fElem);
                // put back attr values...
                fAttrChecker.returnAttrArray(attrValues, schemaDoc);
                return;
            }
            fElem = DOMUtil.getNextSiblingElement(fElem);
            // put back attr values...
            fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        }

    } // traverseIdentityConstraint(IdentityConstraint,Element, XSDocumentInfo)
} // XSDAbstractIDConstraintTraverser


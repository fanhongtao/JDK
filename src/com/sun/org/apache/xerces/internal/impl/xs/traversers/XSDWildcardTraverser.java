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

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSWildcardDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import org.w3c.dom.Element;

/**
 * The wildcard schema component traverser.
 *
 * <any
 *   id = ID
 *   maxOccurs = (nonNegativeInteger | unbounded)  : 1
 *   minOccurs = nonNegativeInteger : 1
 *   namespace = ((##any | ##other) | List of (anyURI | (##targetNamespace | ##local)) )  : ##any
 *   processContents = (lax | skip | strict) : strict
 *   {any attributes with non-schema namespace . . .}>
 *   Content: (annotation?)
 * </any>
 *
 * <anyAttribute
 *   id = ID
 *   namespace = ((##any | ##other) | List of (anyURI | (##targetNamespace | ##local)) )  : ##any
 *   processContents = (lax | skip | strict) : strict
 *   {any attributes with non-schema namespace . . .}>
 *   Content: (annotation?)
 * </anyAttribute>
 *
 * @author Rahul Srivastava, Sun Microsystems Inc.
 * @author Sandy Gao, IBM
 *
 * @version $Id: XSDWildcardTraverser.java,v 1.11 2003/06/23 16:35:22 neilg Exp $
 */
class XSDWildcardTraverser extends XSDAbstractTraverser {

    /**
     * constructor
     *
     * @param  handler
     * @param  errorReporter
     * @param  gAttrCheck
     */
    XSDWildcardTraverser (XSDHandler handler,
                          XSAttributeChecker gAttrCheck) {
        super(handler, gAttrCheck);
    }


    /**
     * Traverse <any>
     *
     * @param  elmNode
     * @param  schemaDoc
     * @param  grammar
     * @return the wildcard node index
     */
    XSParticleDecl traverseAny(Element elmNode,
                               XSDocumentInfo schemaDoc,
                               SchemaGrammar grammar) {

        // General Attribute Checking for elmNode
        Object[] attrValues = fAttrChecker.checkAttributes(elmNode, false, schemaDoc);
        XSWildcardDecl wildcard = traverseWildcardDecl(elmNode, attrValues, schemaDoc, grammar);

        // for <any>, need to create a new particle to reflect the min/max values
        XSParticleDecl particle = null;
        if (wildcard != null) {
            int min = ((XInt)attrValues[XSAttributeChecker.ATTIDX_MINOCCURS]).intValue();
            int max = ((XInt)attrValues[XSAttributeChecker.ATTIDX_MAXOCCURS]).intValue();
            if (max != 0) {
                if (fSchemaHandler.fDeclPool !=null) {
                    particle = fSchemaHandler.fDeclPool.getParticleDecl();
                } else {        
                    particle = new XSParticleDecl();
                }
                particle.fType = XSParticleDecl.PARTICLE_WILDCARD;
                particle.fValue = wildcard;
                particle.fMinOccurs = min;
                particle.fMaxOccurs = max;
            }
        }

        fAttrChecker.returnAttrArray(attrValues, schemaDoc);

        return particle;
    }


    /**
     * Traverse <anyAttribute>
     *
     * @param  elmNode
     * @param  schemaDoc
     * @param  grammar
     * @return the wildcard node index
     */
    XSWildcardDecl traverseAnyAttribute(Element elmNode,
                                        XSDocumentInfo schemaDoc,
                                        SchemaGrammar grammar) {

        // General Attribute Checking for elmNode
        Object[] attrValues = fAttrChecker.checkAttributes(elmNode, false, schemaDoc);
        XSWildcardDecl wildcard = traverseWildcardDecl(elmNode, attrValues, schemaDoc, grammar);
        fAttrChecker.returnAttrArray(attrValues, schemaDoc);

        return wildcard;
    }


    /**
     *
     * @param  elmNode
     * @param  attrValues
     * @param  schemaDoc
     * @param  grammar
     * @return the wildcard node index
     */
     XSWildcardDecl traverseWildcardDecl(Element elmNode,
                                         Object[] attrValues,
                                         XSDocumentInfo schemaDoc,
                                         SchemaGrammar grammar) {

        //get all attributes
        XSWildcardDecl wildcard = new XSWildcardDecl();
        // namespace type
        XInt namespaceTypeAttr = (XInt) attrValues[XSAttributeChecker.ATTIDX_NAMESPACE];
        wildcard.fType = namespaceTypeAttr.shortValue();
        // namespace list
        wildcard.fNamespaceList = (String[])attrValues[XSAttributeChecker.ATTIDX_NAMESPACE_LIST];
        // process contents
        XInt processContentsAttr = (XInt) attrValues[XSAttributeChecker.ATTIDX_PROCESSCONTENTS];
        wildcard.fProcessContents = processContentsAttr.shortValue();

        //check content
        Element child = DOMUtil.getFirstChildElement(elmNode);
        XSAnnotationImpl annotation = null;
        if (child != null)
        {
            if (DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
                annotation = traverseAnnotationDecl(child, attrValues, false, schemaDoc);
                child = DOMUtil.getNextSiblingElement(child);
            }

            if (child != null) {
                reportSchemaError("s4s-elt-must-match.1", new Object[]{"wildcard", "(annotation?)", DOMUtil.getLocalName(child)}, elmNode);
            }
        }
        wildcard.fAnnotation = annotation;

        return wildcard;

    } // traverseWildcardDecl

} // XSDWildcardTraverser

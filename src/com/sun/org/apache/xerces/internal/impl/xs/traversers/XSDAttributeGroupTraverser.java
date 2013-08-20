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
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeGroupDecl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.QName;
import org.w3c.dom.Element;

/**
 * The attribute group definition schema component traverser.
 *
 * <attributeGroup
 *   id = ID
 *   name = NCName
 *   ref = QName
 *   {any attributes with non-schema namespace . . .}>
 *   Content: (annotation?, ((attribute | attributeGroup)*, anyAttribute?))
 * </attributeGroup>
 *
 * @author Rahul Srivastava, Sun Microsystems Inc.
 * @author Sandy Gao, IBM
 *
 * @version $Id: XSDAttributeGroupTraverser.java,v 1.11 2003/06/23 16:35:22 neilg Exp $
 */
class XSDAttributeGroupTraverser extends XSDAbstractTraverser {

    XSDAttributeGroupTraverser (XSDHandler handler,
                                XSAttributeChecker gAttrCheck) {

        super(handler, gAttrCheck);
    }


    XSAttributeGroupDecl traverseLocal(Element elmNode,
                                       XSDocumentInfo schemaDoc,
                                       SchemaGrammar grammar) {

        // General Attribute Checking for elmNode declared locally
        Object[] attrValues = fAttrChecker.checkAttributes(elmNode, false, schemaDoc);

        // get attribute
        QName   refAttr	= (QName)   attrValues[XSAttributeChecker.ATTIDX_REF];

        XSAttributeGroupDecl attrGrp = null;

        // ref should be here.
        if (refAttr == null) {
            reportSchemaError("s4s-att-must-appear", new Object[]{"attributeGroup (local)", "ref"}, elmNode);
            fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            return null;
        }

        // get global decl
        attrGrp = (XSAttributeGroupDecl)fSchemaHandler.getGlobalDecl(schemaDoc, XSDHandler.ATTRIBUTEGROUP_TYPE, refAttr, elmNode);


        // no children are allowed here except annotation, which is optional.
        Element child = DOMUtil.getFirstChildElement(elmNode);
        if (child != null) {
            String childName = DOMUtil.getLocalName(child);
            if (childName.equals(SchemaSymbols.ELT_ANNOTATION)) {
                traverseAnnotationDecl(child, attrValues, false, schemaDoc);
                child = DOMUtil.getNextSiblingElement(child);
            }

            if (child != null) {
                Object[] args = new Object [] {refAttr.rawname, "(annotation?)", DOMUtil.getLocalName(child)};
                reportSchemaError("s4s-elt-must-match.1", args, child);
            }
         } // if

        fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        return attrGrp;

    } // traverseLocal

    XSAttributeGroupDecl traverseGlobal(Element elmNode,
                                        XSDocumentInfo schemaDoc,
                                        SchemaGrammar grammar) {

        XSAttributeGroupDecl attrGrp = new XSAttributeGroupDecl();

        // General Attribute Checking for elmNode declared globally
        Object[] attrValues = fAttrChecker.checkAttributes(elmNode, true, schemaDoc);

        String  nameAttr   = (String) attrValues[XSAttributeChecker.ATTIDX_NAME];

        // global declaration must have a name
        if (nameAttr == null) {
            reportSchemaError("s4s-att-must-appear", new Object[]{"attributeGroup (global)", "name"}, elmNode);
            nameAttr = "no name";
        }

        attrGrp.fName = nameAttr;
        attrGrp.fTargetNamespace = schemaDoc.fTargetNamespace;

        // check the content
        Element child = DOMUtil.getFirstChildElement(elmNode);
        XSAnnotationImpl annotation = null;

        if (child!=null) {
            String childName = DOMUtil.getLocalName(child);
            if (childName.equals(SchemaSymbols.ELT_ANNOTATION)) {
              annotation = traverseAnnotationDecl(child, attrValues, false, schemaDoc);
              child = DOMUtil.getNextSiblingElement(child);
          }
        }

        // Traverse the attribute and attribute group elements and fill in the 
        // attributeGroup structure

        Element nextNode = traverseAttrsAndAttrGrps(child, attrGrp, schemaDoc, grammar, null);
        if (nextNode!=null) {
            // An invalid element was found...
            Object[] args = new Object [] {nameAttr, "(annotation?, ((attribute | attributeGroup)*, anyAttribute?))", DOMUtil.getLocalName(nextNode)};
            reportSchemaError("s4s-elt-must-match.1", args, nextNode);
        } 
 
        // Remove prohibited attributes from the set
        attrGrp.removeProhibitedAttrs();
        
        // check for restricted redefine:
        XSAttributeGroupDecl redefinedAttrGrp = (XSAttributeGroupDecl)fSchemaHandler.getGrpOrAttrGrpRedefinedByRestriction(
                XSDHandler.ATTRIBUTEGROUP_TYPE, 
                new QName(XMLSymbols.EMPTY_STRING, nameAttr, nameAttr, schemaDoc.fTargetNamespace), 
                schemaDoc, elmNode); 
        if(redefinedAttrGrp != null) {
            Object[] errArgs = attrGrp.validRestrictionOf(nameAttr, redefinedAttrGrp);
            if (errArgs != null) {
                reportSchemaError((String)errArgs[errArgs.length-1], errArgs, child);            	
                reportSchemaError("src-redefine.7.2.2", new Object [] {nameAttr, errArgs[errArgs.length-1]}, child);
            }
        }

        attrGrp.fAnnotation = annotation;

        // make an entry in global declarations.
        grammar.addGlobalAttributeGroupDecl(attrGrp);

        fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        return attrGrp;

    } // traverseGlobal

} // XSDAttributeGroupTraverser

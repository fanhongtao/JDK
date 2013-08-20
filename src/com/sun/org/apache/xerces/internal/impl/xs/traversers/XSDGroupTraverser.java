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
import com.sun.org.apache.xerces.internal.impl.xs.XSGroupDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSModelGroupImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.QName;
import org.w3c.dom.Element;

/**
 * The model group schema component traverser.
 *
 * <group
 *   name = NCName>
 *   Content: (annotation?, (all | choice | sequence))
 * </group>
 *
 * @author Rahul Srivastava, Sun Microsystems Inc.
 * @author Elena Litani, IBM
 * @author Lisa Martin,  IBM
 * @version $Id: XSDGroupTraverser.java,v 1.19 2004/02/03 17:27:45 sandygao Exp $
 */
class  XSDGroupTraverser extends XSDAbstractParticleTraverser {

    XSDGroupTraverser (XSDHandler handler,
                       XSAttributeChecker gAttrCheck) {

        super(handler, gAttrCheck);
    }

    XSParticleDecl traverseLocal(Element elmNode,
                                 XSDocumentInfo schemaDoc,
                                 SchemaGrammar grammar) {

        // General Attribute Checking for elmNode declared locally
        Object[] attrValues = fAttrChecker.checkAttributes(elmNode, false,
                              schemaDoc);
        QName refAttr = (QName) attrValues[XSAttributeChecker.ATTIDX_REF];
        XInt  minAttr = (XInt)  attrValues[XSAttributeChecker.ATTIDX_MINOCCURS];
        XInt  maxAttr = (XInt)  attrValues[XSAttributeChecker.ATTIDX_MAXOCCURS];

        XSGroupDecl group = null;

        // ref should be here.
        if (refAttr == null) {
            reportSchemaError("s4s-att-must-appear", new Object[]{"group (local)", "ref"}, elmNode);
        } else {
            // get global decl
            // index is a particle index.
            group = (XSGroupDecl)fSchemaHandler.getGlobalDecl(schemaDoc, XSDHandler.GROUP_TYPE, refAttr, elmNode);
        }

        // no children other than "annotation?" are allowed
        Element child = DOMUtil.getFirstChildElement(elmNode);
        if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
            // REVISIT:  put this somewhere
            traverseAnnotationDecl(child, attrValues, false, schemaDoc);
            child = DOMUtil.getNextSiblingElement(child);
        }
        if (child != null) {
            reportSchemaError("s4s-elt-must-match.1", new Object[]{"group (local)", "(annotation?)", DOMUtil.getLocalName(elmNode)}, elmNode);
        }

        int minOccurs = minAttr.intValue();
        int maxOccurs = maxAttr.intValue();

        XSParticleDecl particle = null;

        // not empty group, not empty particle
        if (group != null && group.fModelGroup != null &&
            !(minOccurs == 0 && maxOccurs == 0)) {
            // create a particle to contain this model group
            if (fSchemaHandler.fDeclPool != null) {
                particle = fSchemaHandler.fDeclPool.getParticleDecl();
            } else {        
                particle = new XSParticleDecl();
            }
            particle.fType = XSParticleDecl.PARTICLE_MODELGROUP;
            particle.fValue = group.fModelGroup;
            particle.fMinOccurs = minOccurs;
            particle.fMaxOccurs = maxOccurs;
        }

        fAttrChecker.returnAttrArray(attrValues, schemaDoc);

        return particle;

    } // traverseLocal

    XSGroupDecl traverseGlobal(Element elmNode,
                               XSDocumentInfo schemaDoc,
                               SchemaGrammar grammar) {

        // General Attribute Checking for elmNode declared globally
        Object[] attrValues = fAttrChecker.checkAttributes(elmNode, true,
                              schemaDoc);
        String  strNameAttr = (String)  attrValues[XSAttributeChecker.ATTIDX_NAME];

        // must have a name
        if (strNameAttr == null) {
            reportSchemaError("s4s-att-must-appear", new Object[]{"group (global)", "name"}, elmNode);
        }

        XSGroupDecl group = null;
        XSParticleDecl particle = null;

        // must have at least one child
        Element l_elmChild = DOMUtil.getFirstChildElement(elmNode);
        XSAnnotationImpl annotation = null;
        if (l_elmChild == null) {
            reportSchemaError("s4s-elt-must-match.2",
                              new Object[]{"group (global)", "(annotation?, (all | choice | sequence))"},
                              elmNode);
        } else {
            // Create the group defi up-front, so it can be passed
            // to the traversal methods
            group = new XSGroupDecl();

            String childName = l_elmChild.getLocalName();
            if (childName.equals(SchemaSymbols.ELT_ANNOTATION)) {
                annotation = traverseAnnotationDecl(l_elmChild, attrValues, true, schemaDoc);
                l_elmChild = DOMUtil.getNextSiblingElement(l_elmChild);
                if (l_elmChild != null)
                    childName = l_elmChild.getLocalName();
            }

            if (l_elmChild == null) {
                reportSchemaError("s4s-elt-must-match.2",
                                  new Object[]{"group (global)", "(annotation?, (all | choice | sequence))"},
                                  elmNode);
            } else if (childName.equals(SchemaSymbols.ELT_ALL)) {
                particle = traverseAll(l_elmChild, schemaDoc, grammar, CHILD_OF_GROUP, group);
            } else if (childName.equals(SchemaSymbols.ELT_CHOICE)) {
                particle = traverseChoice(l_elmChild, schemaDoc, grammar, CHILD_OF_GROUP, group);
            } else if (childName.equals(SchemaSymbols.ELT_SEQUENCE)) {
                particle = traverseSequence(l_elmChild, schemaDoc, grammar, CHILD_OF_GROUP, group);
            } else {
                reportSchemaError("s4s-elt-must-match.1",
                                  new Object[]{"group (global)", "(annotation?, (all | choice | sequence))", DOMUtil.getLocalName(l_elmChild)},
                                  l_elmChild);
            }

            if (l_elmChild != null &&
                DOMUtil.getNextSiblingElement(l_elmChild) != null) {
                reportSchemaError("s4s-elt-must-match.1",
                                  new Object[]{"group (global)", "(annotation?, (all | choice | sequence))",
                                               DOMUtil.getLocalName(DOMUtil.getNextSiblingElement(l_elmChild))},
                                  DOMUtil.getNextSiblingElement(l_elmChild));
            }

            // add global group declaration to the grammar
            if (strNameAttr != null) {
                group.fName = strNameAttr;
                group.fTargetNamespace = schemaDoc.fTargetNamespace;
                if (particle != null)
                    group.fModelGroup = (XSModelGroupImpl)particle.fValue;
                group.fAnnotation = annotation;
                grammar.addGlobalGroupDecl(group);
            }
            else {
                // name attribute is not there, don't return this group.
                group = null;
            }
        }
        if(group != null) { 
            // store groups redefined by restriction in the grammar so
            // that we can get at them at full-schema-checking time.
            Object redefinedGrp = fSchemaHandler.getGrpOrAttrGrpRedefinedByRestriction(XSDHandler.GROUP_TYPE,
                new QName(XMLSymbols.EMPTY_STRING, strNameAttr, strNameAttr, schemaDoc.fTargetNamespace),
                schemaDoc, elmNode);
            if(redefinedGrp != null) {
                // store in grammar
                grammar.addRedefinedGroupDecl(group, (XSGroupDecl)redefinedGrp,
                                              fSchemaHandler.element2Locator(elmNode));
            }
        }

        fAttrChecker.returnAttrArray(attrValues, schemaDoc);

        return group;

    } // traverseGlobal
}

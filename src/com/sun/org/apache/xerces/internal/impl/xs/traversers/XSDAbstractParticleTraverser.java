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
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSModelGroupImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import org.w3c.dom.Element;

/**
 * @author Elena Litani, IBM
 * @author Sandy Gao, IBM
 * @version $Id: XSDAbstractParticleTraverser.java,v 1.16 2004/02/04 18:48:15 mrglavas Exp $
 */
abstract class XSDAbstractParticleTraverser extends XSDAbstractTraverser {

    XSDAbstractParticleTraverser (XSDHandler handler,
                                  XSAttributeChecker gAttrCheck) {
        super(handler, gAttrCheck);
    }

    /**
     *
     * Traverse the "All" declaration
     *
     * <all
     *   id = ID
     *   maxOccurs = 1 : 1
     *   minOccurs = (0 | 1) : 1>
     *   Content: (annotation? , element*)
     * </all>
     **/
    XSParticleDecl traverseAll(Element allDecl,
                               XSDocumentInfo schemaDoc,
                               SchemaGrammar grammar,
                               int allContextFlags,
                               XSObject parent) {

        // General Attribute Checking

        Object[] attrValues = fAttrChecker.checkAttributes(allDecl, false, schemaDoc);

        Element child = DOMUtil.getFirstChildElement(allDecl);

        XSAnnotationImpl annotation = null;
        if (child !=null) {
            // traverse Annotation
            if (DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
                annotation = traverseAnnotationDecl(child, attrValues, false, schemaDoc);
                child = DOMUtil.getNextSiblingElement(child);
            }
        }
        String childName = null;
        XSParticleDecl particle;
        fPArray.pushContext();

        for (; child != null; child = DOMUtil.getNextSiblingElement(child)) {

            particle = null;
            childName = DOMUtil.getLocalName(child);

            // Only elements are allowed in <all>
            if (childName.equals(SchemaSymbols.ELT_ELEMENT)) {
                particle = fSchemaHandler.fElementTraverser.traverseLocal(child, schemaDoc, grammar, PROCESSING_ALL_EL, parent);
            }
            else {
                Object[] args = {"all", "(annotation?, element*)", DOMUtil.getLocalName(child)};
                reportSchemaError("s4s-elt-must-match.1", args, child);
            }

            if (particle != null)
                fPArray.addParticle(particle);
        }

        particle = null;
        XInt minAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_MINOCCURS];
        XInt maxAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_MAXOCCURS];
        Long defaultVals = (Long)attrValues[XSAttributeChecker.ATTIDX_FROMDEFAULT];
            
        XSModelGroupImpl group = new XSModelGroupImpl();
        group.fCompositor = XSModelGroupImpl.MODELGROUP_ALL;
        group.fParticleCount = fPArray.getParticleCount();
        group.fParticles = fPArray.popContext();
        group.fAnnotation = annotation;
        particle = new XSParticleDecl();
        particle.fType = XSParticleDecl.PARTICLE_MODELGROUP;
        particle.fMinOccurs = minAtt.intValue();
        particle.fMaxOccurs = maxAtt.intValue();
        particle.fValue = group;

        particle = checkOccurrences(particle,
                                    SchemaSymbols.ELT_ALL,
                                    (Element)allDecl.getParentNode(),
                                    allContextFlags,
                                    defaultVals.longValue());
        fAttrChecker.returnAttrArray(attrValues, schemaDoc);

        return particle;
    }

    /**
     * Traverse the Sequence declaration
     *
     * <sequence
     *   id = ID
     *   maxOccurs = string
     *   minOccurs = nonNegativeInteger>
     *   Content: (annotation? , (element | group | choice | sequence | any)*)
     * </sequence>
     *
     * @param seqDecl
     * @param schemaDoc
     * @param grammar
     * @return
     */
    XSParticleDecl traverseSequence(Element seqDecl,
                                    XSDocumentInfo schemaDoc,
                                    SchemaGrammar grammar,
                                    int allContextFlags,
                                    XSObject parent) {

        return traverseSeqChoice(seqDecl, schemaDoc, grammar, allContextFlags, false, parent);
    }

    /**
     * Traverse the Choice declaration
     *
     * <choice
     *   id = ID
     *   maxOccurs = string
     *   minOccurs = nonNegativeInteger>
     *   Content: (annotation? , (element | group | choice | sequence | any)*)
     * </choice>
     *
     * @param choiceDecl
     * @param schemaDoc
     * @param grammar
     * @return
     */
    XSParticleDecl traverseChoice(Element choiceDecl,
                                  XSDocumentInfo schemaDoc,
                                  SchemaGrammar grammar,
                                  int allContextFlags,
                                  XSObject parent) {

        return traverseSeqChoice (choiceDecl, schemaDoc, grammar, allContextFlags, true, parent);
    }

    /**
     * Common traversal for <choice> and <sequence>
     *
     * @param decl
     * @param schemaDoc
     * @param grammar
     * @param choice    If traversing <choice> this parameter is true.
     * @return
     */
    private XSParticleDecl traverseSeqChoice(Element decl,
                                             XSDocumentInfo schemaDoc,
                                             SchemaGrammar grammar,
                                             int allContextFlags,
                                             boolean choice,
                                             XSObject parent) {

        // General Attribute Checking
        Object[] attrValues = fAttrChecker.checkAttributes(decl, false, schemaDoc);

        Element child = DOMUtil.getFirstChildElement(decl);
        XSAnnotationImpl annotation = null;
        if (child !=null) {
            // traverse Annotation
            if (DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
                annotation = traverseAnnotationDecl(child, attrValues, false, schemaDoc);
                child = DOMUtil.getNextSiblingElement(child);
            }
        }
        boolean hadContent = false;
        String childName = null;
        XSParticleDecl particle;
        fPArray.pushContext();

        for (;child != null;child = DOMUtil.getNextSiblingElement(child)) {

            particle = null;

            childName = DOMUtil.getLocalName(child);
            if (childName.equals(SchemaSymbols.ELT_ELEMENT)) {
                particle = fSchemaHandler.fElementTraverser.traverseLocal(child, schemaDoc, grammar, NOT_ALL_CONTEXT, parent);
            }
            else if (childName.equals(SchemaSymbols.ELT_GROUP)) {
                particle = fSchemaHandler.fGroupTraverser.traverseLocal(child, schemaDoc, grammar);

                // A content type of all can only appear
                // as the content type of a complex type definition.
                if (hasAllContent(particle)) {
                    // don't insert the "all" particle, otherwise we won't be
                    // able to create DFA from this content model
                    particle = null;
                    reportSchemaError("cos-all-limited.1.2", null, child);
                }

            }
            else if (childName.equals(SchemaSymbols.ELT_CHOICE)) {
                particle = traverseChoice(child, schemaDoc, grammar, NOT_ALL_CONTEXT, parent);
            }
            else if (childName.equals(SchemaSymbols.ELT_SEQUENCE)) {
                particle = traverseSequence(child, schemaDoc, grammar, NOT_ALL_CONTEXT, parent);
            }
            else if (childName.equals(SchemaSymbols.ELT_ANY)) {
                particle = fSchemaHandler.fWildCardTraverser.traverseAny(child, schemaDoc, grammar);
            }
            else {
                Object [] args;
                if (choice) {
                    args = new Object[]{"choice", "(annotation?, (element | group | choice | sequence | any)*)", DOMUtil.getLocalName(child)};
                }
                else {
                    args = new Object[]{"sequence", "(annotation?, (element | group | choice | sequence | any)*)", DOMUtil.getLocalName(child)};
                }
                reportSchemaError("s4s-elt-must-match.1", args, child);
            }

            if (particle != null)
                fPArray.addParticle(particle);
        }

        particle = null;
        
        XInt minAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_MINOCCURS];
        XInt maxAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_MAXOCCURS];
        Long defaultVals = (Long)attrValues[XSAttributeChecker.ATTIDX_FROMDEFAULT];

        XSModelGroupImpl group = new XSModelGroupImpl();
        group.fCompositor = choice ? XSModelGroupImpl.MODELGROUP_CHOICE : XSModelGroupImpl.MODELGROUP_SEQUENCE;
        group.fParticleCount = fPArray.getParticleCount();
        group.fParticles = fPArray.popContext();
        group.fAnnotation = annotation;
        particle = new XSParticleDecl();
        particle.fType = XSParticleDecl.PARTICLE_MODELGROUP;
        particle.fMinOccurs = minAtt.intValue();
        particle.fMaxOccurs = maxAtt.intValue();
        particle.fValue = group;

        particle = checkOccurrences(particle,
                                    choice ? SchemaSymbols.ELT_CHOICE : SchemaSymbols.ELT_SEQUENCE,
                                    (Element)decl.getParentNode(),
                                    allContextFlags,
                                    defaultVals.longValue());
        fAttrChecker.returnAttrArray(attrValues, schemaDoc);

        return particle;
    }

    // Determines whether a content spec tree represents an "all" content model
    protected boolean hasAllContent(XSParticleDecl particle) {
        // If the content is not empty, is the top node ALL?
        if (particle != null && particle.fType == XSParticleDecl.PARTICLE_MODELGROUP) {
            return ((XSModelGroupImpl)particle.fValue).fCompositor == XSModelGroupImpl.MODELGROUP_ALL;
        }

        return false;
    }

    // the inner class: used to store particles for model groups
    // to avoid creating a new Vector in each model group, or when traversing
    // each model group, we use this one big array to store all particles
    // for model groups. when the traversal finishes, this class returns an
    // XSParticleDecl[] containing all particles for the current model group.
    // it's possible that we need to traverse another model group while
    // traversing one (one inside another one; referring to a global group,
    // etc.), so we have push/pos context methods to save the same of the
    // current traversal before starting the traversal of another model group.
    protected static class ParticleArray {
        // big array to contain all particles
        XSParticleDecl[] fParticles = new XSParticleDecl[10];
        // the ending position of particles in the array for each context
        // index 0 is reserved, with value 0. index 1 is used for the fist
        // context. so that the number of particles for context 'i' can be
        // computed simply by fPos[i] - fPos[i-1].
        int[] fPos = new int[5];
        // number of contexts
        int fContextCount = 0;
        
        // start a new context (start traversing a new model group)
        void pushContext() {
            fContextCount++;
            // resize position array if necessary
            if (fContextCount == fPos.length) {
                int newSize = fContextCount * 2;
                int[] newArray = new int[newSize];
                System.arraycopy(fPos, 0, newArray, 0, fContextCount);
                fPos = newArray;
            }
            // the initial ending position of the current context is the
            // ending position of the previsous context. which means there is
            // no particle for the current context yet.
            fPos[fContextCount] = fPos[fContextCount-1];
        }
        
        // get the number of particles of this context (model group)
        int getParticleCount() {
            return fPos[fContextCount] - fPos[fContextCount-1];
        }
        
        // add a particle to the current context
        void addParticle(XSParticleDecl particle) {
            // resize the particle array if necessary
            if (fPos[fContextCount] == fParticles.length) {
                int newSize = fPos[fContextCount] * 2;
                XSParticleDecl[] newArray = new XSParticleDecl[newSize];
                System.arraycopy(fParticles, 0, newArray, 0, fPos[fContextCount]);
                fParticles = newArray;
            }
            fParticles[fPos[fContextCount]++] = particle;
        }
        
        // end the current context, and return an array of particles
        XSParticleDecl[] popContext() {
            int count = fPos[fContextCount] - fPos[fContextCount-1];
            XSParticleDecl[] array = null;
            if (count != 0) {
                array = new XSParticleDecl[count];
                System.arraycopy(fParticles, fPos[fContextCount-1], array, 0, count);
                // clear the particle array, to release memory
                for (int i = fPos[fContextCount-1]; i < fPos[fContextCount]; i++)
                    fParticles[i] = null;
            }
            fContextCount--;
            return array;
        }
        
    }

    // the big particle array to hold all particles in model groups
    ParticleArray fPArray = new ParticleArray();
}

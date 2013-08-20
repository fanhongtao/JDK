/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002,2003 The Apache Software Foundation.  All rights
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

package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.xs.*;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;

/**
 * Store schema model group declaration.
 *
 * @author Sandy Gao, IBM
 *
 * @version $Id: XSModelGroupImpl.java,v 1.7 2004/04/12 20:15:35 sandygao Exp $
 */
public class XSModelGroupImpl implements XSModelGroup {

    // types of model groups
    // REVISIT: can't use same constants as those for particles, because
    // there are place where the constants are used together. For example,
    // to check whether the content is an element or a sequence.
    public static final short MODELGROUP_CHOICE       = 101;
    public static final short MODELGROUP_SEQUENCE     = 102;
    public static final short MODELGROUP_ALL          = 103;

    // compositor of the model group
    public short fCompositor;

    // particles
    public XSParticleDecl[] fParticles = null;
    public int fParticleCount = 0;

    // this particle's optional annotation
    public XSAnnotationImpl fAnnotation;

    // whether this model group contains nothing
    public boolean isEmpty() {
        for (int i = 0; i < fParticleCount; i++) {
            if (!fParticles[i].isEmpty())
                return false;
        }
        return true;
    }

    /**
     * 3.8.6 Effective Total Range (all and sequence) and
     *       Effective Total Range (choice)
     * The following methods are used to return min/max range for a particle.
     * They are not exactly the same as it's described in the spec, but all the
     * values from the spec are retrievable by these methods.
     */
    public int minEffectiveTotalRange() {
        if (fCompositor == MODELGROUP_CHOICE)
            return minEffectiveTotalRangeChoice();
        else
            return minEffectiveTotalRangeAllSeq();
    }

    // return the sum of all min values of the particles
    private int minEffectiveTotalRangeAllSeq() {
        int total = 0;
        for (int i = 0; i < fParticleCount; i++)
            total += fParticles[i].minEffectiveTotalRange();
        return total;
    }

    // return the min of all min values of the particles
    private int minEffectiveTotalRangeChoice() {
        int min = 0, one;
        if (fParticleCount > 0)
            min = fParticles[0].minEffectiveTotalRange();

        for (int i = 1; i < fParticleCount; i++) {
            one = fParticles[i].minEffectiveTotalRange();
            if (one < min)
                min = one;
        }

        return min;
    }

    public int maxEffectiveTotalRange() {
        if (fCompositor == MODELGROUP_CHOICE)
            return maxEffectiveTotalRangeChoice();
        else
            return maxEffectiveTotalRangeAllSeq();
    }

    // if one of the max value of the particles is unbounded, return unbounded;
    // otherwise return the sum of all max values
    private int maxEffectiveTotalRangeAllSeq() {
        int total = 0, one;
        for (int i = 0; i < fParticleCount; i++) {
            one = fParticles[i].maxEffectiveTotalRange();
            if (one == SchemaSymbols.OCCURRENCE_UNBOUNDED)
                return SchemaSymbols.OCCURRENCE_UNBOUNDED;
            total += one;
        }
        return total;
    }

    // if one of the max value of the particles is unbounded, return unbounded;
    // otherwise return the max of all max values
    private int maxEffectiveTotalRangeChoice() {
        int max = 0, one;
        if (fParticleCount > 0) {
            max = fParticles[0].maxEffectiveTotalRange();
            if (max == SchemaSymbols.OCCURRENCE_UNBOUNDED)
                return SchemaSymbols.OCCURRENCE_UNBOUNDED;
        }

        for (int i = 1; i < fParticleCount; i++) {
            one = fParticles[i].maxEffectiveTotalRange();
            if (one == SchemaSymbols.OCCURRENCE_UNBOUNDED)
                return SchemaSymbols.OCCURRENCE_UNBOUNDED;
            if (one > max)
                max = one;
        }
        return max;
    }

    /**
     * get the string description of this particle
     */
    private String fDescription = null;
    public String toString() {
        if (fDescription == null) {
            StringBuffer buffer = new StringBuffer();
            if (fCompositor == MODELGROUP_ALL)
                buffer.append("all(");
            else
                buffer.append('(');
            if (fParticleCount > 0)
                buffer.append(fParticles[0].toString());
            for (int i = 1; i < fParticleCount; i++) {
                if (fCompositor == MODELGROUP_CHOICE)
                    buffer.append('|');
                else
                    buffer.append(',');
                buffer.append(fParticles[i].toString());
            }
            buffer.append(')');
            fDescription = buffer.toString();
        }
        return fDescription;
    }

    public void reset(){
        fCompositor = MODELGROUP_SEQUENCE;
        fParticles = null;
        fParticleCount = 0;
        fDescription = null;
        fAnnotation = null;
    }

    /**
     * Get the type of the object, i.e ELEMENT_DECLARATION.
     */
    public short getType() {
        return XSConstants.MODEL_GROUP;
    }

    /**
     * The <code>name</code> of this <code>XSObject</code> depending on the
     * <code>XSObject</code> type.
     */
    public String getName() {
        return null;
    }

    /**
     * The namespace URI of this node, or <code>null</code> if it is
     * unspecified.  defines how a namespace URI is attached to schema
     * components.
     */
    public String getNamespace() {
        return null;
    }

    /**
     * {compositor} One of all, choice or sequence. The valid constants values
     * are: ALL, CHOICE, SEQUENCE.
     */
    public short getCompositor() {
        if (fCompositor == MODELGROUP_CHOICE)
            return XSModelGroup.COMPOSITOR_CHOICE;
        else if (fCompositor == MODELGROUP_SEQUENCE)
            return XSModelGroup.COMPOSITOR_SEQUENCE;
        else
            return XSModelGroup.COMPOSITOR_ALL;
    }

    /**
     * {particles} A list of particles
     */
    public XSObjectList getParticles() {
        return new XSObjectListImpl(fParticles, fParticleCount);
    }

    /**
     * Optional. Annotation.
     */
    public XSAnnotation getAnnotation() {
        return fAnnotation;
    }

    /**
     * @see com.sun.org.apache.xerces.internal.xs.XSObject#getNamespaceItem()
     */
    public XSNamespaceItem getNamespaceItem() {
        return null;
    }

} // class XSModelGroupImpl

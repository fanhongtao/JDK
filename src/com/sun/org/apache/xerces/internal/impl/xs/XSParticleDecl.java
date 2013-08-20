/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights
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

import com.sun.org.apache.xerces.internal.xs.XSConstants;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSParticle;
import com.sun.org.apache.xerces.internal.xs.XSTerm;

/**
 * Store schema particle declaration.
 *
 * @author Sandy Gao, IBM
 *
 * @version $Id: XSParticleDecl.java,v 1.12 2003/11/11 20:14:58 sandygao Exp $
 */
public class XSParticleDecl implements XSParticle {

    // types of particles
    public static final short PARTICLE_EMPTY        = 0;
    public static final short PARTICLE_ELEMENT      = 1;
    public static final short PARTICLE_WILDCARD     = 2;
    public static final short PARTICLE_MODELGROUP   = 3;
    public static final short PARTICLE_ZERO_OR_MORE = 4;
    public static final short PARTICLE_ZERO_OR_ONE  = 5;
    public static final short PARTICLE_ONE_OR_MORE  = 6;

    // type of the particle
    public short fType = PARTICLE_EMPTY;
    
    // term of the particle
    // for PARTICLE_ELEMENT : the element decl
    // for PARTICLE_WILDCARD: the wildcard decl
    // for PARTICLE_MODELGROUP: the model group
    public XSTerm fValue = null;

    // minimum occurrence of this particle
    public int fMinOccurs = 1;
    // maximum occurrence of this particle
    public int fMaxOccurs = 1;

    // clone this decl
    public XSParticleDecl makeClone() {
        XSParticleDecl particle = new XSParticleDecl();
        particle.fType = fType;
        particle.fMinOccurs = fMinOccurs;
        particle.fMaxOccurs = fMaxOccurs;
        particle.fDescription = fDescription;
        particle.fValue = fValue;
        return particle;
    }
    
    /**
     * 3.9.6 Schema Component Constraint: Particle Emptiable
     * whether this particle is emptible
     */
    public boolean emptiable() {
        return minEffectiveTotalRange() == 0;
    }

    // whether this particle contains nothing
    public boolean isEmpty() {
        if (fType == PARTICLE_EMPTY)
             return true;
        if (fType == PARTICLE_ELEMENT || fType == PARTICLE_WILDCARD)
            return false; 

        return ((XSModelGroupImpl)fValue).isEmpty();
    }

    /**
     * 3.8.6 Effective Total Range (all and sequence) and
     *       Effective Total Range (choice)
     * The following methods are used to return min/max range for a particle.
     * They are not exactly the same as it's described in the spec, but all the
     * values from the spec are retrievable by these methods.
     */
    public int minEffectiveTotalRange() {
        if (fType == XSParticleDecl.PARTICLE_EMPTY) {
            return 0;
        }
        if (fType == PARTICLE_MODELGROUP) {
            return ((XSModelGroupImpl)fValue).minEffectiveTotalRange() * fMinOccurs;
        }
        return fMinOccurs;
    }

    public int maxEffectiveTotalRange() {
        if (fType == XSParticleDecl.PARTICLE_EMPTY) {
            return 0;
        }
        if (fType == PARTICLE_MODELGROUP) {
            int max = ((XSModelGroupImpl)fValue).maxEffectiveTotalRange();
            if (max == SchemaSymbols.OCCURRENCE_UNBOUNDED)
                return SchemaSymbols.OCCURRENCE_UNBOUNDED;
            if (max != 0 && fMaxOccurs == SchemaSymbols.OCCURRENCE_UNBOUNDED)
                return SchemaSymbols.OCCURRENCE_UNBOUNDED;
            return max * fMaxOccurs;
        }
        return fMaxOccurs;
    }

    /**
     * get the string description of this particle
     */
    private String fDescription = null;
    public String toString() {
        if (fDescription == null) {
            StringBuffer buffer = new StringBuffer();
            appendParticle(buffer);
            if (!(fMinOccurs == 0 && fMaxOccurs == 0 ||
                  fMinOccurs == 1 && fMaxOccurs == 1)) {
                buffer.append("{" + fMinOccurs);
                if (fMaxOccurs == SchemaSymbols.OCCURRENCE_UNBOUNDED)
                    buffer.append("-UNBOUNDED");
                else if (fMinOccurs != fMaxOccurs)
                    buffer.append("-" + fMaxOccurs);
                buffer.append("}");
            }
            fDescription = buffer.toString();
        }
        return fDescription;
    }

    /**
     * append the string description of this particle to the string buffer
     * this is for error message.
     */
    void appendParticle(StringBuffer buffer) {
        switch (fType) {
        case PARTICLE_EMPTY:
            buffer.append("EMPTY");
            break;
        case PARTICLE_ELEMENT:
        case PARTICLE_WILDCARD:
            buffer.append('(');
            buffer.append(fValue.toString());
            buffer.append(')');
            break;
        case PARTICLE_MODELGROUP:
            buffer.append(fValue.toString());
            break;
        }
    }

    public void reset(){
        fType = PARTICLE_EMPTY;
        fValue = null;
        fMinOccurs = 1;
        fMaxOccurs = 1;
        fDescription = null;
    }

    /**
     * Get the type of the object, i.e ELEMENT_DECLARATION.
     */
    public short getType() {
        return XSConstants.PARTICLE;
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
     * {min occurs} determines the minimum number of terms that can occur.
     */
    public int getMinOccurs() {
        return fMinOccurs;
    }

    /**
     * {max occurs} whether the maxOccurs value is unbounded.
     */
    public boolean getMaxOccursUnbounded() {
        return fMaxOccurs == SchemaSymbols.OCCURRENCE_UNBOUNDED;
    }

    /**
     * {max occurs} determines the maximum number of terms that can occur.
     */
    public int getMaxOccurs() {
        return fMaxOccurs;
    }

    /**
     * {term} One of a model group, a wildcard, or an element declaration.
     */
    public XSTerm getTerm() {
        return fValue;
    }

	/**
	 * @see com.sun.org.apache.xerces.internal.xs.XSObject#getNamespaceItem()
	 */
	public XSNamespaceItem getNamespaceItem() {
		return null;
	}

} // class XSParticle

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

package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSComplexTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSConstants;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMapImpl;

/**
 * The XML representation for an element declaration
 * schema component is an <element> element information item
 *
 * @author Elena Litani, IBM
 * @author Sandy Gao, IBM
 * @version $Id: XSElementDecl.java,v 1.14 2003/11/12 23:17:33 sandygao Exp $
 */
public class XSElementDecl implements XSElementDeclaration {

    // scopes
    public final static short     SCOPE_ABSENT        = 0;
    public final static short     SCOPE_GLOBAL        = 1;
    public final static short     SCOPE_LOCAL         = 2;

    // name of the element
    public String fName = null;
    // target namespace of the element
    public String fTargetNamespace = null;
    // type of the element
    public XSTypeDefinition fType = null;
    // misc flag of the element: nillable/abstract/fixed
    short fMiscFlags = 0;
    public short fScope = XSConstants.SCOPE_ABSENT;
    // enclosing complex type, when the scope is local
    XSComplexTypeDecl fEnclosingCT = null;
    // block set (disallowed substitutions) of the element
    public short fBlock = XSConstants.DERIVATION_NONE;
    // final set (substitution group exclusions) of the element
    public short fFinal = XSConstants.DERIVATION_NONE;
    // optional annotation
    public XSAnnotationImpl fAnnotation = null;
    // value constraint value
    public ValidatedInfo fDefault = null;
    // the substitution group affiliation of the element
    public XSElementDecl fSubGroup = null;
    // identity constraints
    static final int INITIAL_SIZE = 2;
    int fIDCPos = 0;
    IdentityConstraint[] fIDConstraints = new IdentityConstraint[INITIAL_SIZE];

    private static final short CONSTRAINT_MASK = 3;
    private static final short NILLABLE        = 4;
    private static final short ABSTRACT        = 8;

    // methods to get/set misc flag
    public void setConstraintType(short constraintType) {
        // first clear the bits
        fMiscFlags ^= (fMiscFlags & CONSTRAINT_MASK);
        // then set the proper one
        fMiscFlags |= (constraintType & CONSTRAINT_MASK);
    }
    public void setIsNillable() {
        fMiscFlags |= NILLABLE;
    }
    public void setIsAbstract() {
        fMiscFlags |= ABSTRACT;
    }
    public void setIsGlobal() {
        fScope = SCOPE_GLOBAL;
    }
    public void setIsLocal(XSComplexTypeDecl enclosingCT) {
        fScope = SCOPE_LOCAL;
        fEnclosingCT = enclosingCT;
    }

    public void addIDConstraint(IdentityConstraint idc) {
        if (fIDCPos == fIDConstraints.length) {
            fIDConstraints = resize(fIDConstraints, fIDCPos*2);
        }
        fIDConstraints[fIDCPos++] = idc;
    }

    public IdentityConstraint[] getIDConstraints() {
        if (fIDCPos == 0) {
            return null;
        }
        if (fIDCPos < fIDConstraints.length) {
            fIDConstraints = resize(fIDConstraints, fIDCPos);
        }
        return fIDConstraints;
    }

    static final IdentityConstraint[] resize(IdentityConstraint[] oldArray, int newSize) {
        IdentityConstraint[] newArray = new IdentityConstraint[newSize];
        System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
        return newArray;
    }

    /**
     * get the string description of this element
     */
    private String fDescription = null;
    public String toString() {
        if (fDescription == null) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("\"");
            if (fTargetNamespace != null)
                buffer.append(fTargetNamespace);
            buffer.append("\"");
            buffer.append(":");
            buffer.append(fName);
            fDescription = buffer.toString();
        }
        return fDescription;
    }

    /**
     * get the hash code
     */
    public int hashCode() {
        int code = fName.hashCode();
        if (fTargetNamespace != null)
            code = (code<<16)+fTargetNamespace.hashCode();
        return code;
    }

    /**
     * whether two decls are the same
     */
    public boolean equals(Object o) {
        return o == this;
    }

    /**
      * Reset current element declaration
      */
    public void reset(){

        fName = null;
        fTargetNamespace = null;
        fType = null;
        fMiscFlags = 0;
        fBlock = XSConstants.DERIVATION_NONE;
        fFinal = XSConstants.DERIVATION_NONE;
        fDefault = null;
        fAnnotation = null;
        fSubGroup = null;
        // reset identity constraints
        for (int i=0;i<fIDCPos;i++) {
            fIDConstraints[i] = null;
        }

        fIDCPos = 0;
    }

    /**
     * Get the type of the object, i.e ELEMENT_DECLARATION.
     */
    public short getType() {
        return XSConstants.ELEMENT_DECLARATION;
    }

    /**
     * The <code>name</code> of this <code>XSObject</code> depending on the
     * <code>XSObject</code> type.
     */
    public String getName() {
        return fName;
    }

    /**
     * The namespace URI of this node, or <code>null</code> if it is
     * unspecified.  defines how a namespace URI is attached to schema
     * components.
     */
    public String getNamespace() {
        return fTargetNamespace;
    }

    /**
     * Either a simple type definition or a complex type definition.
     */
    public XSTypeDefinition getTypeDefinition() {
        return fType;
    }

    /**
     * Optional. Either global or a complex type definition (
     * <code>ctDefinition</code>). This property is absent in the case of
     * declarations within named model groups: their scope will be
     * determined when they are used in the construction of complex type
     * definitions.
     */
    public short getScope() {
        return fScope;
    }

    /**
     * Locally scoped declarations are available for use only within the
     * complex type definition identified by the <code>scope</code>
     * property.
     */
    public XSComplexTypeDefinition getEnclosingCTDefinition() {
        return fEnclosingCT;
    }

    /**
     * A value constraint: one of default, fixed.
     */
    public short getConstraintType() {
        return (short)(fMiscFlags & CONSTRAINT_MASK);
    }

    /**
     * A value constraint: The actual value (with respect to the {type
     * definition})
     */
    public String getConstraintValue() {
        // REVISIT: SCAPI: what's the proper representation
        return getConstraintType() == XSConstants.VC_NONE ?
               null :
               fDefault.stringValue();
    }

    /**
     * If {nillable} is true, then an element may also be valid if it carries
     * the namespace qualified attribute with [local name] nil from
     * namespace http://www.w3.org/2001/XMLSchema-instance and value true
     * (see xsi:nil (2.6.2)) even if it has no text or element content
     * despite a {content type} which would otherwise require content.
     */
    public boolean getNillable() {
        return ((fMiscFlags & NILLABLE) != 0);
    }

    /**
     * {identity-constraint definitions} A set of constraint definitions.
     */
    public XSNamedMap getIdentityConstraints() {
        return new XSNamedMapImpl(fIDConstraints, fIDCPos);
    }

    /**
     * {substitution group affiliation} Optional. A top-level element
     * definition.
     */
    public XSElementDeclaration getSubstitutionGroupAffiliation() {
        return fSubGroup;
    }

    /**
     * Convenience method. Check if <code>exclusion</code> is a substitution
     * group exclusion for this element declaration.
     * @param exclusion Extension, restriction or none. Represents final
     *   set for the element.
     * @return True if <code>exclusion</code> is a part of the substitution
     *   group exclusion subset.
     */
    public boolean isSubstitutionGroupExclusion(short exclusion) {
        return (fFinal & exclusion) != 0;
    }

    /**
     * Specifies if this declaration can be nominated as
     * the {substitution group affiliation} of other
     * element declarations having the same {type definition}
     * or types derived therefrom.
     *
     * @return A bit flag representing {extension, restriction} or NONE.
     */
    public short getSubstitutionGroupExclusions() {
        return fFinal;
    }

    /**
     * Convenience method. Check if <code>disallowed</code> is a disallowed
     * substitution for this element declaration.
     * @param disallowed Substitution, extension, restriction or none.
     *   Represents a block set for the element.
     * @return True if <code>disallowed</code> is a part of the substitution
     *   group exclusion subset.
     */
    public boolean isDisallowedSubstitution(short disallowed) {
        return (fBlock & disallowed) != 0;
    }

    /**
     * The supplied values for {disallowed substitutions}
     *
     * @return A bit flag representing {substitution, extension, restriction} or NONE.
     */
    public short getDisallowedSubstitutions() {
        return fBlock;
    }

    /**
     * {abstract} A boolean.
     */
    public boolean getAbstract() {
        return ((fMiscFlags & ABSTRACT) != 0);
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
        // REVISIT: implement
        return null;
    }

    public Object getActualVC() {
        return fDefault.actualValue;
    }

    public short getActualVCType() {
        return fDefault.actualValueType;
    }

    public ShortList getItemValueTypes() {
        return fDefault.itemValueTypes;
    }

} // class XSElementDecl

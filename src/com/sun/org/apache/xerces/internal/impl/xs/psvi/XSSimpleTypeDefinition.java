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

package com.sun.org.apache.xerces.internal.impl.xs.psvi;

/**
 * This interface represents the Simple Type Definition schema component.
 * The interface may be updated or replaced. 
 */
public interface XSSimpleTypeDefinition extends XSTypeDefinition {
    // Variety definitions
    /**
     * The variety is absent for the anySimpleType definition.
     */
    public static final short VARIETY_ABSENT            = 0;
    /**
     * <code>Atomic</code> type.
     */
    public static final short VARIETY_ATOMIC            = 1;
    /**
     * <code>List</code> type.
     */
    public static final short VARIETY_LIST              = 2;
    /**
     * <code>Union</code> type.
     */
    public static final short VARIETY_UNION             = 3;

    // Facets
    /**
     * No facets defined.
     */
    public static final short FACET_NONE                = 0;
    /**
     * 4.3.1 Length
     */
    public static final short FACET_LENGTH              = 1;
    /**
     * 4.3.2 minLength. 
     */
    public static final short FACET_MINLENGTH           = 2;
    /**
     * 4.3.3 maxLength.
     */
    public static final short FACET_MAXLENGTH           = 4;
    /**
     * 4.3.4 pattern.
     */
    public static final short FACET_PATTERN             = 8;
    /**
     * 4.3.5 whitespace.
     */
    public static final short FACET_WHITESPACE          = 16;
    /**
     * 4.3.7 maxInclusive.
     */
    public static final short FACET_MAXINCLUSIVE        = 32;
    /**
     * 4.3.9 maxExclusive.
     */
    public static final short FACET_MAXEXCLUSIVE        = 64;
    /**
     * 4.3.9 minExclusive.
     */
    public static final short FACET_MINEXCLUSIVE        = 128;
    /**
     * 4.3.10 minInclusive.
     */
    public static final short FACET_MININCLUSIVE        = 256;
    /**
     * 4.3.11 totalDigits .
     */
    public static final short FACET_TOTALDIGITS         = 512;
    /**
     * 4.3.12 fractionDigits.
     */
    public static final short FACET_FRACTIONDIGITS      = 1024;
    /**
     * 4.3.5 enumeration.
     */
    public static final short FACET_ENUMERATION         = 2048;

    /**
     * A constant defined for the 'ordered' fundamental facet: Not ordered.
     */
    public static final short ORDERED_FALSE             = 0;
    /**
     * A constant defined for the 'ordered' fundamental facet: partially 
     * ordered.
     */
    public static final short ORDERED_PARTIAL           = 1;
    /**
     * A constant defined for the 'ordered' fundamental facet: total ordered.
     */
    public static final short ORDERED_TOTAL             = 2;
    /**
     * [variety]: one of {atomic, list, union} or absent 
     */
    public short getVariety();

    /**
     * If variety is <code>atomic</code> the primitive type definition (a 
     * built-in primitive datatype definition or the simple ur-type 
     * definition) is available, otherwise <code>null</code>. 
     */
    public XSSimpleTypeDefinition getPrimitiveType();

    /**
     * If variety is <code>list</code> the item type definition (an atomic or 
     * union simple type definition) is available, otherwise 
     * <code>null</code>. 
     */
    public XSSimpleTypeDefinition getItemType();

    /**
     * If variety is <code>union</code> the list of member type definitions (a 
     * non-empty sequence of simple type definitions) is available, 
     * otherwise <code>null</code>. 
     */
    public XSObjectList getMemberTypes();

    /**
     * [facets]: get all facets defined on this type. The value is a bit 
     * combination of FACET_XXX constants of all defined facets. 
     */
    public short getDefinedFacets();

    /**
     * Convenience method. [Facets]: check whether a facet is defined on this 
     * type.
     * @param facetName  The name of the facet. 
     * @return  True if the facet is defined, false otherwise.
     */
    public boolean isDefinedFacet(short facetName);

    /**
     * [facets]: get all facets defined and fixed on this type.
     */
    public short getFixedFacets();

    /**
     * Convenience method. [Facets]: check whether a facet is defined and 
     * fixed on this type. 
     * @param facetName  The name of the facet. 
     * @return  True if the facet is fixed, false otherwise.
     */
    public boolean isFixedFacet(short facetName);

    /**
     * Convenience method. Returns a value of a single constraining facet for 
     * this simple type definition. This method must not be used to retrieve 
     * values for <code>enumeration</code> and <code>pattern</code> facets. 
     * @param facetName The name of the facet, i.e. 
     *   <code>FACET_LENGTH, FACET_TOTALDIGITS </code> (see 
     *   <code>XSConstants</code>).To retrieve value for pattern or 
     *   enumeration, see <code>enumeration</code> and <code>pattern</code>.
     * @return A value of the facet specified in <code>facetName</code> for 
     *   this simple type definition or <code>null</code>. 
     */
    public String getLexicalFacetValue(short facetName);

    /**
     * Returns a list of enumeration values. 
     */
    public StringList getLexicalEnumeration();

    /**
     * Returns a list of pattern values. 
     */
    public StringList getLexicalPattern();

    /**
     *  Fundamental Facet: ordered 
     */
    public short getOrdered();

    /**
     * Fundamental Facet: cardinality. 
     */
    public boolean getFinite();

    /**
     * Fundamental Facet: bounded. 
     */
    public boolean getBounded();

    /**
     * Fundamental Facet: numeric. 
     */
    public boolean getNumeric();

    /**
     * Optional. A set of [annotation]s. 
     */
    public XSObjectList getAnnotations();
    /** 
     * @return list of constraining facets.
     * This method must not be used to retrieve 
     * values for <code>enumeration</code> and <code>pattern</code> facets.
     */
    public XSObjectList getFacets();
    
    /** 
     * @return list of enumeration and pattern facets.
     */
    public XSObjectList getMultiValueFacets();

}

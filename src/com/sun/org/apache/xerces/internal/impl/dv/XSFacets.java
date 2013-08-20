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
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.impl.dv;

import java.util.Vector;

import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;

/**
 * The class used to pass all facets to {@link XSSimpleType#applyFacets}.
 *
 * @author Sandy Gao, IBM
 *
 * @version $Id: XSFacets.java,v 1.8 2003/11/11 20:14:58 sandygao Exp $
 */
public class XSFacets {

    /**
     * value of length facet.
     */
    public int length;

    /**
     * value of minLength facet.
     */
    public int minLength;

    /**
     * value of maxLength facet.
     */
    public int maxLength;

    /**
     * value of whiteSpace facet.
     */
    public short whiteSpace;

    /**
     * value of totalDigits facet.
     */
    public int totalDigits;

    /**
     * value of fractionDigits facet.
     */
    public int fractionDigits;

    /**
     * string containing value of pattern facet, for multiple patterns values
     * are ORed together.
     */
    public String pattern;

    /**
     * Vector containing values of Enumeration facet, as String's.
     */
    public Vector enumeration;

    /**
     * An array parallel to "Vector enumeration". It contains namespace context
     * of each enumeration value. Elements of this vector are NamespaceContext
     * objects.
     */
    public Vector enumNSDecls;

    /**
     * value of maxInclusive facet.
     */
    public String maxInclusive;

    /**
     * value of maxExclusive facet.
     */
    public String maxExclusive;

    /**
     * value of minInclusive facet.
     */
    public String minInclusive;

    /**
     * value of minExclusive facet.
     */
    public String minExclusive;
    
    
   
    public XSAnnotation lengthAnnotation;
    public XSAnnotation minLengthAnnotation;
    public XSAnnotation maxLengthAnnotation;
    public XSAnnotation whiteSpaceAnnotation;
    public XSAnnotation totalDigitsAnnotation;
    public XSAnnotation fractionDigitsAnnotation;
    public XSObjectListImpl patternAnnotations;
    public XSObjectList enumAnnotations;
    public XSAnnotation maxInclusiveAnnotation;
    public XSAnnotation maxExclusiveAnnotation;
    public XSAnnotation minInclusiveAnnotation;
    public XSAnnotation minExclusiveAnnotation;
    
    public void reset(){
        lengthAnnotation = null;
        minLengthAnnotation = null;
        maxLengthAnnotation = null;
        whiteSpaceAnnotation = null;
        totalDigitsAnnotation = null;
        fractionDigitsAnnotation = null;
        patternAnnotations = null;
        enumAnnotations = null;
        maxInclusiveAnnotation = null;
        maxExclusiveAnnotation = null;
        minInclusiveAnnotation = null;
        minExclusiveAnnotation = null;
    }
}

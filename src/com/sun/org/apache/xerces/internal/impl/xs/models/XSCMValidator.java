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

package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;

import java.util.Vector;

/**
 * Note: State of the content model is stored in the validator
 *
 * @author Sandy Gao, IBM
 * @author Elena Litani, IBM
 * @version $Id: XSCMValidator.java,v 1.5 2003/03/11 15:48:33 sandygao Exp $
 */
public interface XSCMValidator {


    public static final short FIRST_ERROR = -1;

    // on subsequent errors the validator should not report
    // an error
    //
    public static final short SUBSEQUENT_ERROR = -2;

    /**
     * This methods to be called on entering a first element whose type
     * has this content model. It will return the initial state of the content model
     *
     * @return Start state of the content model
     */
    public int[] startContentModel();


    /**
     * The method corresponds to one transaction in the content model.
     *
     * @param elementName
     * @param state  Current state
     * @return element decl or wildcard decl that
     *         corresponds to the element from the Schema grammar
     */
    public Object oneTransition (QName elementName, int[] state, SubstitutionGroupHandler subGroupHandler);


    /**
     * The method indicates the end of list of children
     *
     * @param state  Current state of the content model
     * @return true if the last state was a valid final state
     */
    public boolean endContentModel (int[] state);

    /**
     * check whether this content violates UPA constraint.
     *
     * @param errors to hold the UPA errors
     * @return true if this content model contains other or list wildcard
     */
    public boolean checkUniqueParticleAttribution(SubstitutionGroupHandler subGroupHandler) throws XMLSchemaException;

    /**
     * Check which elements are valid to appear at this point. This method also
     * works if the state is in error, in which case it returns what should
     * have been seen.
     * 
     * @param state  the current state
     * @return       a Vector whose entries are instances of
     *               either XSWildcardDecl or XSElementDecl.
     */
    public Vector whatCanGoHere(int[] state);
    
} // XSCMValidator

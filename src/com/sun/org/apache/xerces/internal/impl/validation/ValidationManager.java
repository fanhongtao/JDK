/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  
 * All rights reserved.
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

package com.sun.org.apache.xerces.internal.impl.validation;

import java.util.Vector;

/**
 * ValidationManager is a coordinator property for validators in the 
 * pipeline. Each validator must know how to interact with
 * this property. Validators are not required to know what kind of 
 * other validators present in the pipeline, but should understand
 * that there are others and that some coordination is required.
 * 
 * @author Elena Litani, IBM
 * @version $Id: ValidationManager.java,v 1.8 2003/05/08 20:11:56 elena Exp $
 */
public class ValidationManager {

    protected final Vector fVSs = new Vector();
    protected boolean fGrammarFound = false;

    // used by the DTD validator to tell other components that it has a
    // cached DTD in hand so there's no reason to 
    // scan external subset or entity decls.
    protected boolean fCachedDTD = false;    
    
    /**
     * Each validator should call this method to add its ValidationState into
     * the validation manager.
     */
    public final void addValidationState(ValidationState vs) {
        fVSs.addElement(vs);
    }

    /**
     * Set the information required to validate entity values.
     */
    public final void setEntityState(EntityState state) {
        for (int i = fVSs.size()-1; i >= 0; i--) {
            ((ValidationState)fVSs.elementAt(i)).setEntityState(state);
        }
    }
    
    public final void setGrammarFound(boolean grammar){
        fGrammarFound = grammar;
    }
        
    public final boolean isGrammarFound(){
        return fGrammarFound;
    }

    public final void setCachedDTD(boolean cachedDTD) {
        fCachedDTD = cachedDTD;
    } // setCachedDTD(boolean)

    public final boolean isCachedDTD() {
        return fCachedDTD;
    } // isCachedDTD():  boolean
    
        
    public final void reset (){
        fVSs.removeAllElements();
        fGrammarFound = false;
        fCachedDTD = false;
    }
}

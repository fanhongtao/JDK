/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.
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
 * originally based on software copyright (c) 2001, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.impl.validation;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Implementation of ValidationContext inteface. Used to establish an
 * environment for simple type validation.
 *
 * @author Elena Litani, IBM
 * @version $Id: ValidationState.java,v 1.13 2003/06/05 21:50:30 neilg Exp $
 */
public class ValidationState implements ValidationContext {

    //
    // private data
    //
    private boolean fExtraChecking              = true;
    private boolean fFacetChecking              = true;
    private boolean fNormalize                  = true;
    private boolean fNamespaces                 = true;

    private EntityState fEntityState            = null;
    private NamespaceContext fNamespaceContext  = null;
    private SymbolTable fSymbolTable            = null;

    //REVISIT: Should replace with a lighter structure.
    private final Hashtable fIdTable    = new Hashtable();
    private final Hashtable fIdRefTable = new Hashtable();
    private final static Object fNullValue = new Object();

    //
    // public methods
    //
    public void setExtraChecking(boolean newValue) {
        fExtraChecking = newValue;
    }

    public void setFacetChecking(boolean newValue) {
        fFacetChecking = newValue;
    }

    public void setNormalizationRequired (boolean newValue) {
          fNormalize = newValue;
    }

    public void setUsingNamespaces (boolean newValue) {
          fNamespaces = newValue;
    }

    public void setEntityState(EntityState state) {
        fEntityState = state;
    }

    public void setNamespaceSupport(NamespaceContext namespace) {
        fNamespaceContext = namespace;
    }

    public void setSymbolTable(SymbolTable sTable) {
        fSymbolTable = sTable;
    }

    /**
     * return null if all IDREF values have a corresponding ID value;
     * otherwise return the first IDREF value without a matching ID value.
     */
    public String checkIDRefID () {
        Enumeration en = fIdRefTable.keys();

        String key;
        while (en.hasMoreElements()) {
            key = (String)en.nextElement();
            if (!fIdTable.containsKey(key)) {
                  return key;
            }
        }
        return null;
    }

    public void reset () {
        fExtraChecking = true;
        fFacetChecking = true;
        fNamespaces = true;
        fIdTable.clear();
        fIdRefTable.clear();
        fEntityState = null;
        fNamespaceContext = null;
        fSymbolTable = null;
    }

    /**
     * The same validation state can be used to validate more than one (schema)
     * validation roots. Entity/Namespace/Symbol are shared, but each validation
     * root needs its own id/idref tables. So we need this method to reset only
     * the two tables.
     */
    public void resetIDTables() {
        fIdTable.clear();
        fIdRefTable.clear();
    }

    //
    // implementation of ValidationContext methods
    //

    // whether to do extra id/idref/entity checking
    public boolean needExtraChecking() {
        return fExtraChecking;
    }

    // whether to validate against facets
    public boolean needFacetChecking() {
        return fFacetChecking;
    }

    public boolean needToNormalize (){
        return fNormalize;
    }

    public boolean useNamespaces() {
        return fNamespaces;
    }

    // entity
    public boolean isEntityDeclared (String name) {
        if (fEntityState !=null) {
            return fEntityState.isEntityDeclared(getSymbol(name));
        }
        return false;
    }
    public boolean isEntityUnparsed (String name) {
        if (fEntityState !=null) {
            return fEntityState.isEntityUnparsed(getSymbol(name));
        }
        return false;
    }

    // id
    public boolean isIdDeclared(String name) {
        return fIdTable.containsKey(name);
    }
    public void addId(String name) {
        fIdTable.put(name, fNullValue);
    }

    // idref
    public void addIdRef(String name) {
        fIdRefTable.put(name, fNullValue);
    }
    // get symbols

    public String getSymbol (String symbol) {
        if (fSymbolTable != null)
            return fSymbolTable.addSymbol(symbol);
        // if there is no symbol table, we return java-internalized string,
        // because symbol table strings are also java-internalzied.
        // this guarantees that the returned string from this method can be
        // compared by reference with other symbol table string. -SG
        return symbol.intern();
    }
    // qname, notation
    public String getURI(String prefix) {
        if (fNamespaceContext !=null) {
            return fNamespaceContext.getURI(prefix);
        }
        return null;
    }

}

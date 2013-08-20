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
 * originally based on software copyright (c) 2003, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package com.sun.org.apache.xerces.internal.xinclude;

import java.util.Enumeration;

import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;

/**
 * This implementation of NamespaceContext has the ability to maintain multiple
 * scopes of namespace/prefix bindings.  This is useful it situtions when it is
 * not always appropriate for elements to inherit the namespace bindings of their
 * ancestors (such as included elements in XInclude).
 * 
 * When searching for a URI to match a prefix, or a prefix to match a URI, it is
 * searched for in the current context, then the ancestors of the current context,
 * up to the beginning of the current scope.  Other scopes are not searched.
 * 
 * @author Peter McCracken, IBM
 * 
 * @version $Id: MultipleScopeNamespaceSupport.java,v 1.5 2004/01/22 16:08:58 mrglavas Exp $
 */
public class MultipleScopeNamespaceSupport extends NamespaceSupport {

    protected int[] fScope = new int[8];
    protected int fCurrentScope;

    /**
     * 
     */
    public MultipleScopeNamespaceSupport() {
        super();
        fCurrentScope = 0;
        fScope[0] = 0;
    }

    /**
     * @param context
     */
    public MultipleScopeNamespaceSupport(NamespaceContext context) {
        super(context);
        fCurrentScope = 0;
        fScope[0] = 0;
    }

    /* (non-Javadoc)
     * @see com.sun.org.apache.xerces.internal.xni.NamespaceContext#getAllPrefixes()
     */
    public Enumeration getAllPrefixes() {
        int count = 0;
        if (fPrefixes.length < (fNamespace.length / 2)) {
            // resize prefix array          
            String[] prefixes = new String[fNamespaceSize];
            fPrefixes = prefixes;
        }
        String prefix = null;
        boolean unique = true;
        for (int i = fContext[fScope[fCurrentScope]];
            i <= (fNamespaceSize - 2);
            i += 2) {
            prefix = fNamespace[i];
            for (int k = 0; k < count; k++) {
                if (fPrefixes[k] == prefix) {
                    unique = false;
                    break;
                }
            }
            if (unique) {
                fPrefixes[count++] = prefix;
            }
            unique = true;
        }
        return new Prefixes(fPrefixes, count);
    }

    public int getScopeForContext(int context) {
        int scope = fCurrentScope;
                while (context < fScope[scope]) {
                    scope--;
                }
        return scope;
    }

    /* (non-Javadoc)
     * @see com.sun.org.apache.xerces.internal.xni.NamespaceContext#getPrefix(java.lang.String)
     */
    public String getPrefix(String uri) {
        return getPrefix(uri, fNamespaceSize, fScope[fCurrentScope]);
    }

    /* (non-Javadoc)
     * @see com.sun.org.apache.xerces.internal.xni.NamespaceContext#getURI(java.lang.String)
     */
    public String getURI(String prefix) {
        return getURI(prefix, fNamespaceSize, fScope[fCurrentScope]);
    }

    public String getPrefix(String uri, int context) {
        return getPrefix(uri, fContext[context+1], fScope[getScopeForContext(context)]);
    }

    public String getURI(String prefix, int context) {
        return getURI(prefix, fContext[context+1], fScope[getScopeForContext(context)]);
    }

    public String getPrefix(String uri, int start, int end) {
        // this saves us from having a copy of each of these in fNamespace for each scope
        if (uri == NamespaceContext.XML_URI) {
            return XMLSymbols.PREFIX_XML;
        }
        if (uri == NamespaceContext.XMLNS_URI) {
            return XMLSymbols.PREFIX_XMLNS;
        }

        // find uri in current context
        for (int i = start; i > end; i -= 2) {
            if (fNamespace[i - 1] == uri) {
                if (getURI(fNamespace[i - 2]) == uri)
                    return fNamespace[i - 2];
            }
        }

        // uri not found
        return null;
    }

    public String getURI(String prefix, int start, int end) {
        // this saves us from having a copy of each of these in fNamespace for each scope
        if (prefix == XMLSymbols.PREFIX_XML) {
            return NamespaceContext.XML_URI;
        }
        if (prefix == XMLSymbols.PREFIX_XMLNS) {
            return NamespaceContext.XMLNS_URI;
        }

        // find prefix in current context
        for (int i = start; i > end; i -= 2) {
            if (fNamespace[i - 2] == prefix) {
                return fNamespace[i - 1];
            }
        }

        // prefix not found
        return null;
    }

    /**
     * Onlys resets the current scope -- all namespaces defined in lower scopes
     * remain valid after a call to reset.
     */
    public void reset() {
        fCurrentContext = fScope[fCurrentScope];
        fNamespaceSize = fContext[fCurrentContext];
    }

    /**
     * Begins a new scope.  None of the previous namespace bindings will be used,
     * until the new scope is popped with popScope()
     */
    public void pushScope() {
        if (fCurrentScope + 1 == fScope.length) {
            int[] contextarray = new int[fScope.length * 2];
            System.arraycopy(fScope, 0, contextarray, 0, fScope.length);
            fScope = contextarray;
        }
        pushContext();
        fScope[++fCurrentScope] = fCurrentContext;
    }

    /**
     * Pops the current scope.  The namespace bindings from the new current scope
     * are then used for searching for namespaces and prefixes.
     */
    public void popScope() {
        fCurrentContext = fScope[fCurrentScope--];
        popContext();
    }
}

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

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * A class used to hold the internal schema grammar set for the current instance
 * @author Sandy Gao, IBM
 * @version $Id: XSGrammarBucket.java,v 1.8 2003/09/23 21:42:31 mrglavas Exp $
 */
public class XSGrammarBucket {

    // Data

    /**
     * Hashtable that maps between Namespace and a Grammar
     */
    Hashtable fGrammarRegistry = new Hashtable();
    SchemaGrammar fNoNSGrammar = null;

    /**
     * Get the schema grammar for the specified namespace
     *
     * @param namespace
     * @return SchemaGrammar associated with the namespace
     */
    public SchemaGrammar getGrammar(String namespace) {
        if (namespace == null)
            return fNoNSGrammar;
        return (SchemaGrammar)fGrammarRegistry.get(namespace);
    }

    /**
     * Put a schema grammar into the registry
     * This method is for internal use only: it assumes that a grammar with
     * the same target namespace is not already in the bucket.
     *
     * @param grammar   the grammar to put in the registry
     */
    public void putGrammar(SchemaGrammar grammar) {
        if (grammar.getTargetNamespace() == null)
            fNoNSGrammar = grammar;
        else
            fGrammarRegistry.put(grammar.getTargetNamespace(), grammar);
    }

    /**
     * put a schema grammar and any grammars imported by it (directly or
     * inderectly) into the registry. when a grammar with the same target
     * namespace is already in the bucket, and different from the one being
     * added, it's an error, and no grammar will be added into the bucket.
     *
     * @param grammar   the grammar to put in the registry
     * @param deep      whether to add imported grammars
     * @return          whether the process succeeded
     */
    public boolean putGrammar(SchemaGrammar grammar, boolean deep) {
        // whether there is one with the same tns
        SchemaGrammar sg = getGrammar(grammar.fTargetNamespace);
        if (sg != null) {
            // if the one we have is different from the one passed, it's an error
            return sg == grammar;
        }
        // not deep import, then just add this one grammar
        if (!deep) {
            putGrammar(grammar);
            return true;
        }

        // get all imported grammars, and make a copy of the Vector, so that
        // we can recursively process the grammars, and add distinct ones
        // to the same vector
        Vector currGrammars = (Vector)grammar.getImportedGrammars();
        if (currGrammars == null) {
            putGrammar(grammar);
            return true;
        }
        
        Vector grammars = ((Vector)currGrammars.clone());
        SchemaGrammar sg1, sg2;
        Vector gs;
        // for all (recursively) imported grammars
        for (int i = 0; i < grammars.size(); i++) {
            // get the grammar
            sg1 = (SchemaGrammar)grammars.elementAt(i);
            // check whether the bucket has one with the same tns
            sg2 = getGrammar(sg1.fTargetNamespace);
            if (sg2 == null) {
                // we need to add grammars imported by sg1 too
                gs = sg1.getImportedGrammars();
                // for all grammars imported by sg2, but not in the vector
                // we add them to the vector
                if(gs == null) continue;
                for (int j = gs.size() - 1; j >= 0; j--) {
                    sg2 = (SchemaGrammar)gs.elementAt(j);
                    if (!grammars.contains(sg2))
                        grammars.addElement(sg2);
                }
            }
            // we found one with the same target namespace
            // if the two grammars are not the same object, then it's an error
            else if (sg2 != sg1) {
                return false;
            }
        }

        // now we have all imported grammars stored in the vector. add them
        putGrammar(grammar);
        for (int i = grammars.size() - 1; i >= 0; i--)
            putGrammar((SchemaGrammar)grammars.elementAt(i));

        return true;
    }

    /**
     * get all grammars in the registry
     *
     * @return an array of SchemaGrammars.
     */
    public SchemaGrammar[] getGrammars() {
        // get the number of grammars
        int count = fGrammarRegistry.size() + (fNoNSGrammar==null ? 0 : 1);
        SchemaGrammar[] grammars = new SchemaGrammar[count];
        // get grammars with target namespace
        Enumeration schemas = fGrammarRegistry.elements();
        int i = 0;
        while (schemas.hasMoreElements())
            grammars[i++] = (SchemaGrammar)schemas.nextElement();
        // add the grammar without target namespace, if any
        if (fNoNSGrammar != null)
            grammars[count-1] = fNoNSGrammar;
        return grammars;
    }

    /**
     * Clear the registry.
     * REVISIT: update to use another XSGrammarBucket
     */
    public void reset() {
        fNoNSGrammar = null;
        fGrammarRegistry.clear();
    }

} // class XSGrammarBucket

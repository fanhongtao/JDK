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
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.impl.xpath.XPathException;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;

/**
 * Schema identity constraint selector.
 *
 * @author Andy Clark, IBM
 * @version $Id: Selector.java,v 1.16 2003/11/15 22:03:22 neilg Exp $
 */
public class Selector {

    //
    // Data
    //

    /** XPath. */
    protected Selector.XPath fXPath;

    /** Identity constraint. */
    protected IdentityConstraint fIdentityConstraint;

    // the Identity constraint we're the matcher for.  Only
    // used for selectors!
    protected IdentityConstraint fIDConstraint;

    //
    // Constructors
    //

    /** Constructs a selector. */
    public Selector(Selector.XPath xpath, 
                    IdentityConstraint identityConstraint) {
        fXPath = xpath;
        fIdentityConstraint = identityConstraint;
    } // <init>(Selector.XPath,IdentityConstraint)

    //
    // Public methods
    //

    /** Returns the selector XPath. */
    public com.sun.org.apache.xerces.internal.impl.xpath.XPath getXPath() {
        return fXPath;
    } // getXPath():com.sun.org.apache.xerces.internal.v1.schema.identity.XPath

    /** Returns the identity constraint. */
    public IdentityConstraint getIDConstraint() {
        return fIdentityConstraint;
    } // getIDConstraint():IdentityConstraint

    // factory method

    /** Creates a selector matcher. 
     * @param activator     The activator for this selector's fields.
     * @param initialDepth  The depth in the document at which this matcher began its life;
     *                          used in correctly handling recursive elements.
     */
    public XPathMatcher createMatcher(FieldActivator activator, int initialDepth) {
        return new Selector.Matcher(fXPath, activator, initialDepth);
    } // createMatcher(FieldActivator):XPathMatcher

    //
    // Object methods
    //

    /** Returns a string representation of this object. */
    public String toString() {
        return fXPath.toString();
    } // toString():String

    //
    // Classes
    //

    /**
     * Schema identity constraint selector XPath expression.
     *
     * @author Andy Clark, IBM
     * @version $Id: Selector.java,v 1.16 2003/11/15 22:03:22 neilg Exp $
     */
    public static class XPath
    extends com.sun.org.apache.xerces.internal.impl.xpath.XPath {

        //
        // Constructors
        //

        /** Constructs a selector XPath expression. */
        public XPath(String xpath, SymbolTable symbolTable, 
                     NamespaceContext context) throws XPathException {
            super(normalize(xpath), symbolTable, context);
            // verify that an attribute is not selected
            for (int i=0;i<fLocationPaths.length;i++) {
                com.sun.org.apache.xerces.internal.impl.xpath.XPath.Axis axis =
                fLocationPaths[i].steps[fLocationPaths[i].steps.length-1].axis;
                if (axis.type == XPath.Axis.ATTRIBUTE) {
                    throw new XPathException("c-selector-xpath");
                }
            }

        } // <init>(String,SymbolTable,NamespacesScope)

        private static String normalize(String xpath) {
            // NOTE: We have to prefix the selector XPath with "./" in
            //       order to handle selectors such as "." that select
            //       the element container because the fields could be
            //       relative to that element. -Ac
            //       Unless xpath starts with a descendant node -Achille Fokoue
            //      ... or a '.' or a '/' - NG
            //  And we also need to prefix exprs to the right of | with ./ - NG
            StringBuffer modifiedXPath = new StringBuffer(xpath.length()+5);
            int unionIndex = -1;
            do {
                if(!(xpath.trim().startsWith("/") ||xpath.trim().startsWith("."))) {
                    modifiedXPath.append("./"); 
                }
                unionIndex = xpath.indexOf('|');
                if(unionIndex == -1) {
                    modifiedXPath.append(xpath);
                    break;
                }
                modifiedXPath.append(xpath.substring(0,unionIndex+1));
                xpath = xpath.substring(unionIndex+1, xpath.length());
            } while(true);
            return modifiedXPath.toString();
        }

    } // class Selector.XPath

    /**
     * Selector matcher.
     *
     * @author Andy Clark, IBM
     */
    public class Matcher
    extends XPathMatcher {

        //
        // Data
        //

        /** Field activator. */
        protected FieldActivator fFieldActivator;

        /** Initial depth in the document at which this matcher was created. */
        protected int fInitialDepth;

        /** Element depth. */
        protected int fElementDepth;

        /** Depth at match. */
        protected int fMatchedDepth;

        //
        // Constructors
        //

        /** Constructs a selector matcher. */
        public Matcher(Selector.XPath xpath, FieldActivator activator,
                int initialDepth) {
            super(xpath);
            fFieldActivator = activator;
            fInitialDepth = initialDepth;
        } // <init>(Selector.XPath,FieldActivator)

        //
        // XMLDocumentFragmentHandler methods
        //

        public void startDocumentFragment(){
            super.startDocumentFragment();
            fElementDepth = 0;
            fMatchedDepth = -1;
        } // startDocumentFragment()

        /**
         * The start of an element. If the document specifies the start element
         * by using an empty tag, then the startElement method will immediately
         * be followed by the endElement method, with no intervening methods.
         * 
         * @param element    The name of the element.
         * @param attributes The element attributes.
         * @param elementDecl:  The element declaration 
         *
         */
        public void startElement(QName element, XMLAttributes attributes) {
            super.startElement(element, attributes);
            fElementDepth++;
            // activate the fields, if selector is matched
            //int matched = isMatched();

            if (isMatched()) {
/*            (fMatchedDepth == -1 && ((matched & MATCHED) == MATCHED)) ||
                    ((matched & MATCHED_DESCENDANT) == MATCHED_DESCENDANT)) { */
                fMatchedDepth = fElementDepth;
                fFieldActivator.startValueScopeFor(fIdentityConstraint, fInitialDepth);
                int count = fIdentityConstraint.getFieldCount();
                for (int i = 0; i < count; i++) {
                    Field field = fIdentityConstraint.getFieldAt(i);
                    XPathMatcher matcher = fFieldActivator.activateField(field, fInitialDepth);
                    matcher.startElement(element, attributes);
                }
            }

        } // startElement(QName,XMLAttrList,int)

        public void endElement(QName element, XSTypeDefinition type, boolean nillable, Object actualValue) {
            super.endElement(element, type, nillable, actualValue);
            if (fElementDepth-- == fMatchedDepth) {
                fMatchedDepth = -1;
                fFieldActivator.endValueScopeFor(fIdentityConstraint, fInitialDepth);
            }
        }

        /** Returns the identity constraint. */
        public IdentityConstraint getIdentityConstraint() {
            return fIdentityConstraint;
        } // getIdentityConstraint():IdentityConstraint

        /** get the initial depth at which this selector matched. */
        public int getInitialDepth() {
            return fInitialDepth;
        } // getInitialDepth():  int


    } // class Matcher

} // class Selector

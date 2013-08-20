/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 */

package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;

/**
 * <p>Creates nodes.</p>
 * 
 * @author  Neeraj Bajaj
 * @version $Revision: 1.2 $, $Date: 2004/01/22 20:36:54 $
 */
public class CMNodeFactory {

    /**
     * Property identifier: error reporter.
     */
    private static final String ERROR_REPORTER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;
    
    /**
     * <p>Output extra debugging messages to {@link System.err}.</p>
     */
    private static final boolean DEBUG = false;
    
    /**
     * <p>Count of number of nodes created.</p>
     */
    private int nodeCount = 0;
    
    /**
     * Error reporter. This property identifier is:
     * http://apache.org/xml/properties/internal/error-reporter
     */
    private XMLErrorReporter fErrorReporter = null;
    
    /**
     * Default constructor.
     */
    public CMNodeFactory() {

        if (DEBUG) {
            System.err.println("CMNodeFactory()");
        }
    }
    
    /**
     * <p>Reset internal state using <code>componentManager</code> provided values.</p>
     * 
     * @param componentManager {@link XMLComponentManager} to provide new values for internal state.
     */
    public void reset(XMLComponentManager componentManager) {
        
        if (DEBUG) {
            System.err.println("CMNodeFactory#reset("
            + "componentManager[" + componentManager.toString() + "])");
        }

        // error reporter
        fErrorReporter = (XMLErrorReporter) componentManager.getProperty(ERROR_REPORTER);
    } //reset()
    
    /**
     * <p>Create a new leaf node as defined by the params.</p>
     * 
     * @param type Type of leaf node to return.
     * @param leaf Leaf <code>Object</code>
     * @param id ID of leaf to return.
     * @param position Position of leaf to return.
     * 
     * @return New node as defined by the params.
     */
    public CMNode getCMLeafNode(int type, Object leaf, int id, int position) {
        
        // this is a new node
        nodeCount++;

        if (DEBUG) {
            System.err.println("CMNodeFactory#getCMLeafNode("
            + "type[" + type + "], "
            + "leaf[" + leaf.toString() + "], "
            + "id[" + id + "], "
            + "position[" + position + "])\n"
            + "\tnodeCount=" + nodeCount);
        }
                
        // create new node as defined by the params
        return new XSCMLeaf(type, leaf, id, position);
    }
    
    /**
     * <p>Create a leaf node as defined by the params.</p>
     * 
     * @param type Type of node to create.
     * @param childNode Child node.
     * 
     * @return New node as defined by the params.
     */
    public CMNode getCMUniOpNode(int type, CMNode childNode) {

        // this is a new node
        nodeCount++;

        if (DEBUG) {
            System.err.println("CMNodeFactory#getCMUniOpNode("
            + "type[" + type + "], "
            + "childNode[" + childNode.toString() + "])\n"
            + "\tnodeCount=" + nodeCount);
        }
        
        // create new node as defined by the params
        return new XSCMUniOp(type, childNode);
    }

    /**
     * <p>Create a leaf node as defined by the params.</p>
     * 
     * @param type Type of node to create.
     * @param leftNode Left node.
     * @param rightNode Right node.
     * 
     * @return New node as defined by the params.
     */
    public CMNode getCMBinOpNode(int type, CMNode leftNode, CMNode rightNode) {

        // this is a new node
        nodeCount++;

        if (DEBUG) {
            System.err.println("CMNodeFactory#getCMBinOpNode("
            + "type[" + type + "], "
            + "leftNode[" + leftNode.toString() + "], "
            + "rightNode[" + rightNode.toString() + "])\n"
            + "\tnodeCount=" + nodeCount);
        }
        
        // create new node as defined by the params
        return new XSCMBinOp(type, leftNode, rightNode);
    }

    /**
     * <p>Reset the internal node count to 0.</p>
     */
    public void resetNodeCount() {
        nodeCount = 0;
        
        if (DEBUG) {
            System.err.println("CMNodeFactory#resetNodeCount: "
                + "nodeCount=" + nodeCount + " (after reset)");
        }
    }
    
    /**
     * <p>Sets the value of a property. This method is called by the component
     * manager any time after reset when a property changes value.</p>
     * 
     * <p> <strong>Note:</strong> Components should silently ignore properties
     * that do not affect the operation of the component.</p>
     *
     * @param propertyId The property identifier.
     * @param value The value of the property.
     */
    public void setProperty(String propertyId, Object value) {
        
        if (DEBUG) {
            System.err.println("CMNodeFactory#setProperty("
            + "propertyId[" + propertyId + "], "
            + "value[" + value.toString() + "])");
        }

        // Xerces properties?
        if (propertyId.startsWith(Constants.XERCES_PROPERTY_PREFIX)) {
            String property = propertyId.substring(Constants.XERCES_PROPERTY_PREFIX.length());
                        
            // error reporter?
            if (property.equals(Constants.ERROR_REPORTER_PROPERTY)) {
                fErrorReporter = (XMLErrorReporter) value;
                return;
            }
            
            // silently ignore unknown Xerces property
            return;
        } else {
            // silently ignore unknown non-Xerces property
            return;
        }

    } // setProperty(String,Object)
} // CMNodeFactory()

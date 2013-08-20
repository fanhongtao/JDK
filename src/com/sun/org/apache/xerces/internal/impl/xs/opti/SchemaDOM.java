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

package com.sun.org.apache.xerces.internal.impl.xs.opti;

import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Vector;
import java.util.Enumeration;

/**
 * @author Rahul Srivastava, Sun Microsystems Inc.
 * @author Sandy Gao, IBM
 *
 * @version $Id: SchemaDOM.java,v 1.4 2003/07/03 15:15:58 neilg Exp $
 */
public class SchemaDOM extends DefaultDocument {

    static final int relationsRowResizeFactor = 15;
    static final int relationsColResizeFactor = 10;

    NodeImpl[][] relations;
    // parent must be an element in this scheme
    ElementImpl parent;
    int currLoc;
    int nextFreeLoc;
    boolean hidden;

    // for annotation support:
    StringBuffer fAnnotationBuffer = null;

    public SchemaDOM() {
        reset();
    }
    

    public void startElement(QName element, XMLAttributes attributes,
                             int line, int column) {
        ElementImpl node = new ElementImpl(line, column);
        processElement(element, attributes, node);
        // now the current node added, becomes the parent
        parent = node;
    }
    
    
    public void emptyElement(QName element, XMLAttributes attributes,
                             int line, int column) {
        ElementImpl node = new ElementImpl(line, column);
        processElement(element, attributes, node);
    }
    
    
    private void processElement(QName element, XMLAttributes attributes, ElementImpl node) {

        // populate node
        node.prefix = element.prefix;
        node.localpart = element.localpart;
        node.rawname = element.rawname;
        node.uri = element.uri;
        node.schemaDOM = this;
        
        // set the attributes
        Attr[] attrs = new Attr[attributes.getLength()];
        for (int i=0; i<attributes.getLength(); i++) {
            attrs[i] = new AttrImpl(null, 
                                    attributes.getPrefix(i), 
                                    attributes.getLocalName(i), 
                                    attributes.getQName(i), 
                                    attributes.getURI(i), 
                                    attributes.getValue(i));
        }
        node.attrs = attrs;
        
        // check if array needs to be resized
        if (nextFreeLoc == relations.length) {
            resizeRelations();
        }
        
        // store the current parent
        //if (relations[currLoc][0] == null || relations[currLoc][0] != parent) {
        if (relations[currLoc][0] != parent) {
            relations[nextFreeLoc][0] = parent;
            currLoc = nextFreeLoc++;
        }
        
        // add the current node as child of parent
        boolean foundPlace = false;
        int i = 1;
        for (i = 1; i<relations[currLoc].length; i++) {
            if (relations[currLoc][i] == null) {
                foundPlace = true;
                break;
            }
        }
        
        if (!foundPlace) {
            resizeRelations(currLoc);
        }
        relations[currLoc][i] = node;
        
        parent.parentRow = currLoc;
        node.row = currLoc;
        node.col = i;
    }
    
    
    public void endElement()  {
        // the parent of current parent node becomes the parent
        // for the next node.
        currLoc = parent.row;
        parent = (ElementImpl)relations[currLoc][0];
    }
    
    // note that this will only be called within appinfo/documentation
    void comment(XMLString text) {
        fAnnotationBuffer.append("<!--").append(text.toString()).append("-->");
    }

    // note that this will only be called within appinfo/documentation
    void processingInstruction(String target, String data) {
        fAnnotationBuffer.append("<?").append(target).append(" ").append(data).append("?>");
    }
    
    // note that this will only be called within appinfo/documentation
    void characters(XMLString text ) {
        // need to handle &s and <s
        for(int i=text.offset; i<text.offset+text.length; i++ ) {
            if(text.ch[i] == '&') {
                fAnnotationBuffer.append("&amp;");
            } else if (text.ch[i] == '<') {
                fAnnotationBuffer.append("&lt;");
            } else {
                fAnnotationBuffer.append(text.ch[i]);
            }
        }
    }

    void endAnnotationElement(QName elemName, boolean complete) {
        if(complete) {
            fAnnotationBuffer.append("\n</").append(elemName.rawname).append(">");
            // note that this is always called after endElement on <annotation>'s
            // child and before endElement on annotation.
            // hence, we must make this the child of the current
            // parent's only child.
            ElementImpl child = (ElementImpl)relations[currLoc][1];

            // check if array needs to be resized
            if (nextFreeLoc == relations.length) {
                resizeRelations();
            }
            int newRow = child.parentRow = nextFreeLoc++; 
        
            // now find the place to insert this node
            boolean foundPlace = false;
            int i = 1;
            for (; i<relations[newRow].length; i++) {
                if (relations[newRow][i] == null) {
                    foundPlace = true;
                    break;
                }
            }
        
            if (!foundPlace) {
                resizeRelations(newRow);
            }
            relations[newRow][i] = new TextImpl(fAnnotationBuffer, this, newRow, i);
            // apparently, there is no sensible way of resetting
            // these things
            fAnnotationBuffer = null;
        } else      //capturing character calls
            fAnnotationBuffer.append("</").append(elemName.rawname).append(">");
    }

    void startAnnotationCDATA() {
        fAnnotationBuffer.append("<![CDATA[");
    }
    
    void endAnnotationCDATA() {
        fAnnotationBuffer.append("]]>");
    }
    
    private void resizeRelations() {
        NodeImpl[][] temp = new NodeImpl[relations.length+relationsRowResizeFactor][];
        System.arraycopy(relations, 0, temp, 0, relations.length);
        for (int i = relations.length ; i < temp.length ; i++) {
            temp[i] = new NodeImpl[relationsColResizeFactor];
        }
        relations = temp;
    }
    
    private void resizeRelations(int i) {
        NodeImpl[] temp = new NodeImpl[relations[i].length+relationsColResizeFactor];
        System.arraycopy(relations[i], 0, temp, 0, relations[i].length);
        relations[i] = temp;
    }
    
    
    public void reset() {
        
        // help out the garbage collector
        if(relations != null) 
            for(int i=0; i<relations.length; i++) 
                for(int j=0; j<relations[i].length; j++) 
                    relations[i][j] = null;
        relations = new NodeImpl[relationsRowResizeFactor][];
        parent = new ElementImpl(0, 0);
        parent.rawname = "DOCUMENT_NODE";
        currLoc = 0;
        nextFreeLoc = 1;
        for (int i=0; i<relationsRowResizeFactor; i++) {
            relations[i] = new NodeImpl[relationsColResizeFactor];
        }
        relations[currLoc][0] = parent;
    }
    
    
    public void printDOM() {
        /*
        for (int i=0; i<relations.length; i++) {
            if (relations[i][0] != null) {
            for (int j=0; j<relations[i].length; j++) {
                if (relations[i][j] != null) {
                    System.out.print(relations[i][j].nodeType+"-"+relations[i][j].parentRow+"  ");
                }
            }
            System.out.println("");
            }
        }
        */
        //traverse(getDocumentElement(), 0);
    }
    
    
    // debug methods
    
    public static void traverse(Node node, int depth) {
        indent(depth);
        System.out.print("<"+node.getNodeName());
        
        if (node.hasAttributes()) {
            NamedNodeMap attrs = node.getAttributes();
            for (int i=0; i<attrs.getLength(); i++) {
                System.out.print("  "+((Attr)attrs.item(i)).getName()+"=\""+((Attr)attrs.item(i)).getValue()+"\"");
            }
        }
        
        if (node.hasChildNodes()) {
            System.out.println(">");
            depth+=4;
            for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                traverse(child, depth);
            }
            depth-=4;
            indent(depth);
            System.out.println("</"+node.getNodeName()+">");
        }
        else {
            System.out.println("/>");
        }
    }
    
    public static void indent(int amount) {
        for (int i = 0; i < amount; i++) {
            System.out.print(' ');
        }
    }
    
    // org.w3c.dom methods
    public Element getDocumentElement() {
        // this returns a parent node, known to be an ElementImpl
        return (ElementImpl)relations[0][1];
    }
    
    // commence the serialization of an annotation
    void startAnnotation(QName elemName, XMLAttributes attributes,
                NamespaceContext namespaceContext) {
        if(fAnnotationBuffer == null) fAnnotationBuffer = new StringBuffer(256);
        fAnnotationBuffer.append("<").append(elemName.rawname).append(" ");

        // attributes are a bit of a pain.  To get this right, we have to keep track
        // of the namespaces we've seen declared, then examine the namespace context
        // for other namespaces so that we can also include them.
        // optimized for simplicity and the case that not many
        // namespaces are declared on this annotation...
        Vector namespaces = new Vector();
        for(int i=0; i<attributes.getLength(); i++) {
            String aValue = attributes.getValue(i);
            String aPrefix = attributes.getPrefix(i);
            // if it's xmlns, must be a namespace decl
            namespaces.addElement(aValue);
            fAnnotationBuffer.append(attributes.getQName(i)).append("=\"").append(aValue).append("\" ");
        }
        // now we have to look through currently in-scope namespaces to see what
        // wasn't declared here
        Enumeration currPrefixes = namespaceContext.getAllPrefixes();
        while(currPrefixes.hasMoreElements()) {
            String prefix = (String)currPrefixes.nextElement();
            String uri = namespaceContext.getURI(prefix);
            if(!namespaces.contains(uri)) {
                // have to declare this one
                if(prefix == XMLSymbols.EMPTY_STRING) 
                    fAnnotationBuffer.append("xmlns").append("=\"").append(uri).append("\" ");
                else 
                    fAnnotationBuffer.append("xmlns:").append(prefix).append("=\"").append(uri).append("\" ");
            }
        }
        fAnnotationBuffer.append(">\n");
    }
    void startAnnotationElement(QName elemName, XMLAttributes attributes) {
        fAnnotationBuffer.append("<").append(elemName.rawname).append(" ");
        for(int i=0; i<attributes.getLength(); i++) {
            String aValue = attributes.getValue(i);
            fAnnotationBuffer.append(" ").append(attributes.getQName(i)).append("=\"").append(processAttValue(aValue)).append("\" ");
        }
        fAnnotationBuffer.append(">");
    }
    
    private static String processAttValue(String original) {
        // normally, nothing will happen
        StringBuffer newVal = new StringBuffer(original.length());
        for(int i=0; i<original.length(); i++) {
            char currChar = original.charAt(i);
            if(currChar == '"') {
                newVal.append("&quot;");
            } else if (currChar == '>') {
                newVal.append("&gt;");
            } else if (currChar == '&') {
                newVal.append("&amp;");
            } else {
                newVal.append(currChar);
            }
        }
        return newVal.toString();
    }
}

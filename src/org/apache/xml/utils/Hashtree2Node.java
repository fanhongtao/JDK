/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights 
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
 * 4. The names "Xalan" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 2000, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xml.utils;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * <meta name="usage" content="general"/>
 * Simple static utility to convert Hashtable to a Node.  
 *
 * Please maintain JDK 1.1.x compatibility; no Collections!
 *
 * @see org.apache.xalan.xslt.EnvironmentCheck
 * @see org.apache.xalan.lib.Extensions
 * @author shane_curcuru@us.ibm.com
 * @version $Id: Hashtree2Node.java,v 1.1 2002/06/21 15:39:34 curcuru Exp $
 */
public abstract class Hashtree2Node
{

    /**
     * Convert a Hashtable into a Node tree.  
     * 
     * <p>The hash may have either Hashtables as values (in which 
     * case we recurse) or other values, in which case we print them 
     * as &lt;item> elements, with a 'key' attribute with the value 
     * of the key, and the element contents as the value.</p>
     *
     * <p>If args are null we simply return without doing anything. 
     * If we encounter an error, we will attempt to add an 'ERROR' 
     * Element with exception info; if that doesn't work we simply 
     * return without doing anything else byt printStackTrace().</p>
     *
     * @param hash to get info from (may have sub-hashtables)
     * @param name to use as parent element for appended node
     * futurework could have namespace and prefix as well
     * @param container Node to append our report to
     * @param factory Document providing createElement, etc. services
     */
    public static void appendHashToNode(Hashtable hash, String name, 
            Node container, Document factory)
    {
        // Required arguments must not be null
        if ((null == container) || (null == factory) || (null == hash))
        {
            return;
        }

        // name we will provide a default value for
        String elemName = null;
        if ((null == name) || ("".equals(name)))
            elemName = "appendHashToNode";
        else
            elemName = name;

        try
        {
            Element hashNode = factory.createElement(elemName);
            container.appendChild(hashNode);

            Enumeration enum = hash.keys();
            Vector v = new Vector();

            while (enum.hasMoreElements())
            {
                Object key = enum.nextElement();
                String keyStr = key.toString();
                Object item = hash.get(key);

                if (item instanceof Hashtable)
                {
                    // Ensure a pre-order traversal; add this hashes 
                    //  items before recursing to child hashes
                    // Save name and hash in two steps
                    v.addElement(keyStr);
                    v.addElement((Hashtable) item);
                }
                else
                {
                    try
                    {
                        // Add item to node
                        Element node = factory.createElement("item");
                        node.setAttribute("key", keyStr);
                        node.appendChild(factory.createTextNode((String)item));
                        hashNode.appendChild(node);
                    }
                    catch (Exception e)
                    {
                        Element node = factory.createElement("item");
                        node.setAttribute("key", keyStr);
                        node.appendChild(factory.createTextNode("ERROR: Reading " + key + " threw: " + e.toString()));
                        hashNode.appendChild(node);
                    }
                }
            }

            // Now go back and do the saved hashes
            enum = v.elements();
            while (enum.hasMoreElements())
            {
                // Retrieve name and hash in two steps
                String n = (String) enum.nextElement();
                Hashtable h = (Hashtable) enum.nextElement();

                appendHashToNode(h, n, hashNode, factory);
            }
        }
        catch (Exception e2)
        {
            // Ooops, just bail (suggestions for a safe thing 
            //  to do in this case appreciated)
            e2.printStackTrace();
        }
    }    
}

/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.utils;

/**
 * <meta name="usage" content="advanced"/>
 * The class that implements this interface can resolve prefixes to
 * namespaces. Examples would include resolving the meaning of a
 * prefix at a particular point in a document, or mapping the prefixes
 * used in an XPath expression.
 */
public interface PrefixResolver
{

  /**
   * Given a namespace, get the corrisponding prefix.  This assumes that
   * the PrefixResolver holds its own namespace context, or is a namespace
   * context itself.
   *
   * @param prefix The prefix to look up, which may be an empty string ("") for the default Namespace.
   *
   * @return The associated Namespace URI, or null if the prefix
   *         is undeclared in this context.
   */
  String getNamespaceForPrefix(String prefix);

  /**
   * Given a namespace, get the corresponding prefix, based on the context node.
   *
   * @param prefix The prefix to look up, which may be an empty string ("") for the default Namespace.
   * @param context The node context from which to look up the URI.
   *
   * @return The associated Namespace URI as a string, or null if the prefix
   *         is undeclared in this context.
   */
  String getNamespaceForPrefix(String prefix, org.w3c.dom.Node context);

  /**
   * Return the base identifier.
   *
   * @return The base identifier from where relative URIs should be absolutized, or null 
   * if the base ID is unknown.
   * <p>
   * CAVEAT: Note that the base URI in an XML document may vary with where
   * you are in the document, if part of the doc's contents were brought in
   * via an external entity reference or if mechanisms such as xml:base have
   * been used. Unless this PrefixResolver is bound to a specific portion of
   * the document, or has been kept up to date via some other mechanism, it
   * may not accurately reflect that context information.
   */
  public String getBaseIdentifier();
  
  public boolean handlesNullPrefixes();
}

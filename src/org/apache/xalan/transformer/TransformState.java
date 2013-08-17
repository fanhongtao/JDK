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
package org.apache.xalan.transformer;

import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import javax.xml.transform.Transformer;

/**
 * This interface is meant to be used by a consumer of
 * SAX2 events produced by Xalan, and enables the consumer
 * to get information about the state of the transform.  It
 * is primarily intended as a tooling interface.  A content
 * handler can get a reference to a TransformState by implementing
 * the TransformerClient interface.  Xalan will check for
 * that interface before it calls startDocument, and, if it
 * is implemented, pass in a TransformState reference to the
 * setTransformState method.
 *
 * <p>Note that the current stylesheet and root stylesheet can
 * be retrieved from the ElemTemplateElement obtained from
 * either getCurrentElement() or getCurrentTemplate().</p>
 */
public interface TransformState
{

  /**
   * Retrieves the stylesheet element that produced
   * the SAX event.
   *
   * <p>Please note that the ElemTemplateElement returned may
   * be in a default template, and thus may not be
   * defined in the stylesheet.</p>
   *
   * @return the stylesheet element that produced the SAX event.
   */
  ElemTemplateElement getCurrentElement();

  /**
   * This method retrieves the current context node
   * in the source tree.
   *
   * @return the current context node in the source tree.
   */
  Node getCurrentNode();

  /**
   * This method retrieves the xsl:template
   * that is in effect, which may be a matched template
   * or a named template.
   *
   * <p>Please note that the ElemTemplate returned may
   * be a default template, and thus may not have a template
   * defined in the stylesheet.</p>
   *
   * @return the xsl:template that is in effect
   */
  ElemTemplate getCurrentTemplate();

  /**
   * This method retrieves the xsl:template
   * that was matched.  Note that this may not be
   * the same thing as the current template (which
   * may be from getCurrentElement()), since a named
   * template may be in effect.
   *
   * <p>Please note that the ElemTemplate returned may
   * be a default template, and thus may not have a template
   * defined in the stylesheet.</p>
   *
   * @return the xsl:template that was matched.
   */
  ElemTemplate getMatchedTemplate();

  /**
   * Retrieves the node in the source tree that matched
   * the template obtained via getMatchedTemplate().
   *
   * @return the node in the source tree that matched
   * the template obtained via getMatchedTemplate().
   */
  Node getMatchedNode();

  /**
   * Get the current context node list.
   *
   * @return the current context node list.
   */
  NodeIterator getContextNodeList();

  /**
   * Get the TrAX Transformer object in effect.
   *
   * @return the TrAX Transformer object in effect.
   */
  Transformer getTransformer();
}

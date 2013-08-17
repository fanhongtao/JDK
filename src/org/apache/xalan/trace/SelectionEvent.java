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
package org.apache.xalan.trace;

import org.w3c.dom.*;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.XPath;
import org.apache.xpath.objects.XObject;

/**
 * <meta name="usage" content="advanced"/>
 * Event triggered by selection of a node in the style stree.
 */
public class SelectionEvent implements java.util.EventListener
{

  /**
   * The node in the style tree where the event occurs.
   */
  public final ElemTemplateElement m_styleNode;

  /**
   * The XSLT processor instance.
   */
  public final TransformerImpl m_processor;

  /**
   * The current context node.
   */
  public final Node m_sourceNode;

  /**
   * The attribute name from which the selection is made.
   */
  public final String m_attributeName;

  /**
   * The XPath that executed the selection.
   */
  public final XPath m_xpath;

  /**
   * The result of the selection.
   */
  public final XObject m_selection;

  /**
   * Create an event originating at the given node of the style tree.
   * 
   * @param processor The XSLT TransformerFactory.
   * @param sourceNode The current context node.
   * @param mode The current mode.
   * @param styleNode node in the style tree reference for the event.
   * Should not be null.  That is not enforced.
   * @param attributeName The attribute name from which the selection is made.
   * @param xpath The XPath that executed the selection.
   * @param selection The result of the selection.
   */
  public SelectionEvent(TransformerImpl processor, Node sourceNode,
                        ElemTemplateElement styleNode, String attributeName,
                        XPath xpath, XObject selection)
  {

    this.m_processor = processor;
    this.m_sourceNode = sourceNode;
    this.m_styleNode = styleNode;
    this.m_attributeName = attributeName;
    this.m_xpath = xpath;
    this.m_selection = selection;
  }
}

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
package org.apache.xalan.extensions;

import org.apache.xpath.XPathVisitor;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.FuncExtFunction;
import org.apache.xalan.templates.StylesheetRoot;

/**
 * When {@link org.apache.xalan.templates.StylesheetHandler} creates 
 * an {@link org.apache.xpath.XPath}, the ExpressionVisitor
 * visits the XPath expression. For any extension functions it 
 * encounters, it instructs StylesheetRoot to register the
 * extension namespace. 
 * 
 * This mechanism is required to locate extension functions
 * that may be embedded within an expression.
 */
public class ExpressionVisitor extends XPathVisitor
{
  private StylesheetRoot m_sroot;
  
  /**
   * The constructor sets the StylesheetRoot variable which
   * is used to register extension namespaces.
   * @param sroot the StylesheetRoot that is being constructed.
   */
  public ExpressionVisitor (StylesheetRoot sroot)
  {
    m_sroot = sroot;
  }
  
  /**
   * If the function is an extension function, register the namespace.
   * 
   * @param owner The current XPath object that owns the expression.
   * @param function The function currently being visited.
   * 
   * @return true to continue the visit in the subtree, if any.
   */
  public boolean visitFunction(ExpressionOwner owner, Function func)
	{
    if (func instanceof FuncExtFunction)
    {
      String namespace = ((FuncExtFunction)func).getNamespace();
      m_sroot.getExtensionNamespacesManager().registerExtension(namespace);      
    }
		return true;
	}
}
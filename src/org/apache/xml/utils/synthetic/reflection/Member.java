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
package org.apache.xml.utils.synthetic.reflection;

import org.apache.xml.utils.synthetic.SynthesisException;

/**
 * <meta name="usage" content="internal"/>
 * Member is an interface that reflects identifying
 * information about a single member (a field or a method)
 * or a constructor.
 * <p>
 * Note that this is <strong>not</strong> currently derived from
 * java.lang.reflect.Member, due to questions about how to handle
 * declarignClass.
 *
 * @see org.apache.xml.utils.synthetic.Class
 */
public interface Member
{

  /**
   * Returns the Class object representing the class or
   * interface that declares the member or constructor
   * represented by this Member.
   *
   */
  public abstract org.apache.xml.utils.synthetic.Class getDeclaringClass();

  /**
   * Returns the Java language modifiers for the
   * member or constructor represented by this
   * Member, as an integer. The Modifier class should
   * be used to decode the modifiers in the integer.
   *
   */
  public abstract int getModifiers();

  /**
   * Returns the Class object representing the class or
   * interface that declares the member or constructor
   * represented by this Member.
   *
   * @param declaringClass
   *
   * @throws SynthesisException
   */
  public abstract void setDeclaringClass(
    org.apache.xml.utils.synthetic.Class declaringClass)
      throws SynthesisException;

  /**
   * Returns the Java language modifiers for the
   * member or constructor represented by this
   * Member, as an integer. The Modifier class should
   * be used to decode the modifiers in the integer.
   *
   * @param modifiers
   *
   * @throws SynthesisException
   */
  public abstract void setModifiers(int modifiers) throws SynthesisException;
}

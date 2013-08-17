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
package org.apache.xml.utils.synthetic;

/**
 * <meta name="usage" content="internal"/>
 * Class SynthesisException <needs-comment/>
 */
public class SynthesisException extends Exception
{

  /** Field code.
   *  @serial          */
  int code;

  // Manefest constants

  /** Field SYNTAX          */
  public static final int SYNTAX = 0;

  /** Field UNSUPPORTED          */
  public static final int UNSUPPORTED = 1;

  /** Field REIFIED          */
  public static final int REIFIED = 2;

  /** Field UNREIFIED          */
  public static final int UNREIFIED = 3;

  /** Field WRONG_OWNER          */
  public static final int WRONG_OWNER = 4;

  /** Field errToString          */
  public static final String[] errToString = {
    "(Syntax error; specific message should be passed in)",
    "Feature not yet supported",
    "Can't change features of 'real' class",
    "Can't yet instantiate/invoke without 'real' class",
    "Can't add Member to an object other than its declarer", };

  /**
   * Constructor SynthesisException
   *
   *
   * @param code
   */
  public SynthesisException(int code)
  {

    super(errToString[code]);

    this.code = code;
  }

  /**
   * Constructor SynthesisException
   *
   *
   * @param code
   * @param msg
   */
  public SynthesisException(int code, String msg)
  {

    super(msg);

    this.code = code;
  }

  /**
   * Method getCode 
   *
   *
   */
  int getCode()
  {
    return code;
  }
}

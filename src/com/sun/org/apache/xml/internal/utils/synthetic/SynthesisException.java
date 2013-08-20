/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: SynthesisException.java,v 1.6 2004/02/17 04:23:24 minchau Exp $
 */
package com.sun.org.apache.xml.internal.utils.synthetic;

/**
 * Class SynthesisException <needs-comment/>
 * @xsl.usage internal
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

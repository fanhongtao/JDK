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
 * $Id: String.java,v 1.7 2004/02/17 04:35:12 minchau Exp $
 */
package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;

/**
 * The 'string()' operation expression executer.
 */
public class String extends UnaryOperation
{

  /**
   * Apply the operation to two operands, and return the result.
   *
   *
   * @param right non-null reference to the evaluated right operand.
   *
   * @return non-null reference to the XObject that represents the result of the operation.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject operate(XObject right) throws javax.xml.transform.TransformerException
  {
    return (XString)right.xstr(); // semi-safe cast.
  }
}

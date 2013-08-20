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
 * $Id: Member.java,v 1.6 2004/02/17 04:24:21 minchau Exp $
 */
package com.sun.org.apache.xml.internal.utils.synthetic.reflection;

import com.sun.org.apache.xml.internal.utils.synthetic.SynthesisException;

/**
 * Member is an interface that reflects identifying
 * information about a single member (a field or a method)
 * or a constructor.
 * <p>
 * Note that this is <strong>not</strong> currently derived from
 * java.lang.reflect.Member, due to questions about how to handle
 * declarignClass.
 *
 * @see com.sun.org.apache.xml.internal.utils.synthetic.Class
 * @xsl.usage internal
 */
public interface Member
{

  /**
   * Returns the Class object representing the class or
   * interface that declares the member or constructor
   * represented by this Member.
   *
   */
  public abstract com.sun.org.apache.xml.internal.utils.synthetic.Class getDeclaringClass();

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
    com.sun.org.apache.xml.internal.utils.synthetic.Class declaringClass)
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

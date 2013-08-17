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
package org.apache.xalan.serialize;

import javax.xml.transform.OutputKeys;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import java.io.IOException;
import java.io.InputStream;

import org.apache.xalan.templates.OutputProperties;
import org.apache.xml.utils.WrappedRuntimeException;

/**
 * Factory for creating serializers.
 */
public abstract class SerializerFactory
{

  /*
   * Associates output methods to serializer classes.
   * (Don't use this right now.  -sb
   */

  // private static Hashtable _serializers = new Hashtable();

  /**
   * Associates output methods to default output formats.
   */
  private static Hashtable _formats = new Hashtable();

  /**
   * Returns a serializer for the specified output method. Returns
   * null if no implementation exists that supports the specified
   * output method. For a list of the default output methods see
   * {@link Method}.
   *
   * @param format The output format
   * @return A suitable serializer, or null
   * @throws IllegalArgumentException (apparently -sc) if method is 
   * null or an appropriate serializer can't be found
   * @throws WrappedRuntimeException (apparently -sc) if an 
   * exception is thrown while trying to find serializer
   */
  public static Serializer getSerializer(Properties format)
  {

    Serializer ser = null;

    try
    {
      Class cls;
      String method = format.getProperty(OutputKeys.METHOD);

      if (method == null)
        throw new IllegalArgumentException(
          "The output format has a null method name");

      cls = null;  // (Class)_serializers.get(method);

      if (cls == null)
      {
        String className =
          format.getProperty(OutputProperties.S_KEY_CONTENT_HANDLER);

        if (null == className)
        {
          throw new IllegalArgumentException(
            "The output format must have a '"
            + OutputProperties.S_KEY_CONTENT_HANDLER + "' property!");
        }

        cls = Class.forName(className);

        // _serializers.put(method, cls);
      }

      ser = (Serializer) cls.newInstance();

      ser.setOutputFormat(format);
    }
    catch (Exception e)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(e);
    }

    return ser;
  }
}

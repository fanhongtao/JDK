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
 * $Id: SerializerFactory.java,v 1.6 2004/02/23 10:29:37 aruny Exp $
 */
package com.sun.org.apache.xml.internal.serializer;

import java.util.Hashtable;
import java.util.Properties;

import javax.xml.transform.OutputKeys;

import com.sun.org.apache.xml.internal.res.XMLErrorResources;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import org.xml.sax.ContentHandler;

/**
 * Factory for creating serializers.
 */
public abstract class SerializerFactory
{
  /**
   * Associates output methods to default output formats.
   */
  private static Hashtable m_formats = new Hashtable();

  /**
   * Returns a serializer for the specified output method. 
   * If no implementation exists that supports the specified output method
   * an exception of some type will be thrown.
   * For a list of the default output methods see {@link Method}.
   *
   * @param format The output format, minimally the "method" property must be set.
   * @return A suitable serializer.
   * @throws IllegalArgumentException if method is
   * null or an appropriate serializer can't be found
   * @throws Exception if the class for the serializer is found but does not
   * implement ContentHandler.
   * @throws WrappedRuntimeException if an exception is thrown while trying to find serializer
   */
  public static Serializer getSerializer(Properties format)
  {
      Serializer ser;

      try
      {
        String method = format.getProperty(OutputKeys.METHOD);

        if (method == null)
          throw new IllegalArgumentException(
            "The output format has a null method name");

        String className =
            format.getProperty(OutputPropertiesFactory.S_KEY_CONTENT_HANDLER);


        if (null == className)
        {
            // Missing Content Handler property, load default using OutputPropertiesFactory
            Properties methodDefaults =
                OutputPropertiesFactory.getDefaultMethodProperties(method);
            className = 
            methodDefaults.getProperty(OutputPropertiesFactory.S_KEY_CONTENT_HANDLER);
                if (null == className)
                throw new IllegalArgumentException(
                    "The output format must have a '"
                    + OutputPropertiesFactory.S_KEY_CONTENT_HANDLER + "' property!");
        }



        ClassLoader loader = ObjectFactory.findClassLoader();

        Class cls = ObjectFactory.findProviderClass(className, loader, true);

        // _serializers.put(method, cls);

        Object obj = cls.newInstance();

        if (obj instanceof SerializationHandler)
        {
              // this is one of the supplied serializers
            ser = (Serializer) cls.newInstance();
            ser.setOutputFormat(format);
        }
        else
        {
              /*
               *  This  must be a user defined Serializer.
               *  It had better implement ContentHandler.
               */
               if (obj instanceof ContentHandler)
               {

                  /*
                   * The user defined serializer defines ContentHandler,
                   * but we need to wrap it with ToXMLSAXHandler which
                   * will collect SAX-like events and emit true
                   * SAX ContentHandler events to the users handler.
                   */
                  className = SerializerConstants.DEFAULT_SAX_SERIALIZER;
                  cls = ObjectFactory.findProviderClass(className, loader, true);
                  SerializationHandler sh =
                      (SerializationHandler) cls.newInstance();
                  sh.setContentHandler( (ContentHandler) obj);
                  sh.setOutputFormat(format);

                  ser = sh;
               }
               else
               {
                  // user defined serializer does not implement
                  // ContentHandler, ... very bad
                   throw new Exception(
                       XMLMessages.createXMLMessage(
                           XMLErrorResources.ER_SERIALIZER_NOT_CONTENTHANDLER,
                               new Object[] { className}));
               }

        }
      }
      catch (Exception e)
      {
        throw new com.sun.org.apache.xml.internal.utils.WrappedRuntimeException(e);
      }

      // If we make it to here ser is not null.
      return ser;
  }
}

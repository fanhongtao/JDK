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
package org.apache.xpath.functions;

import java.util.Properties;

import java.io.BufferedInputStream;
import java.io.InputStream;

import java.lang.ClassLoader;

import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.res.XPATHErrorResources;

//import org.w3c.dom.Node;

import java.util.Vector;

import org.apache.xpath.XPathContext;
import org.apache.xpath.XPath;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XString;

/**
 * <meta name="usage" content="advanced"/>
 * Execute the SystemProperty() function.
 */
public class FuncSystemProperty extends FunctionOneArg
{

  /** 
   * The path/filename of the property file: XSLTInfo.properties  
   * Maintenance note: see also org.apache.xalan.processor.TransformerFactoryImpl.XSLT_PROPERTIES
   */
  static String XSLT_PROPERTIES = "org/apache/xalan/res/XSLTInfo.properties";
	
	/** a zero length Class array used in loadPropertyFile() */
  private static final Class[] NO_CLASSES = new Class[0];

  /** a zero length Object array used in loadPropertyFile() */
  private static final Object[] NO_OBJS = new Object[0];


  /**
   * Execute the function.  The function must return
   * a valid object.
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {

    String fullName = m_arg0.execute(xctxt).str();
    int indexOfNSSep = fullName.indexOf(':');
    String result;
    String propName = "";
    
    /* List of properties where the name of the property argument is 
     *  to be looked for. */
    Properties xsltInfo = new Properties();

    loadPropertyFile(XSLT_PROPERTIES, xsltInfo);

    if (indexOfNSSep > 0)
    {
      String prefix = (indexOfNSSep >= 0)
                      ? fullName.substring(0, indexOfNSSep) : "";
      String namespace;

      namespace = xctxt.getNamespaceContext().getNamespaceForPrefix(prefix);
      propName = (indexOfNSSep < 0)
                 ? fullName : fullName.substring(indexOfNSSep + 1);

      if (namespace.startsWith("http://www.w3.org/XSL/Transform")
              || namespace.equals("http://www.w3.org/1999/XSL/Transform"))
      {
        result = xsltInfo.getProperty(propName);

        if (null == result)
        {
          warn(xctxt, XPATHErrorResources.WG_PROPERTY_NOT_SUPPORTED,
               new Object[]{ fullName });  //"XSL Property not supported: "+fullName);

          return XString.EMPTYSTRING;
        }
      }
      else
      {
        warn(xctxt, XPATHErrorResources.WG_DONT_DO_ANYTHING_WITH_NS,
             new Object[]{ namespace,
                           fullName });  //"Don't currently do anything with namespace "+namespace+" in property: "+fullName);

        try
        {
          result = System.getProperty(propName);

          if (null == result)
          {

            // result = System.getenv(propName);
            return XString.EMPTYSTRING;
          }
        }
        catch (SecurityException se)
        {
          warn(xctxt, XPATHErrorResources.WG_SECURITY_EXCEPTION,
               new Object[]{ fullName });  //"SecurityException when trying to access XSL system property: "+fullName);

          return XString.EMPTYSTRING;
        }
      }
    }
    else
    {
      try
      {
        result = System.getProperty(fullName);

        if (null == result)
        {

          // result = System.getenv(fullName);
          return XString.EMPTYSTRING;
        }
      }
      catch (SecurityException se)
      {
        warn(xctxt, XPATHErrorResources.WG_SECURITY_EXCEPTION,
             new Object[]{ fullName });  //"SecurityException when trying to access XSL system property: "+fullName);

        return XString.EMPTYSTRING;
      }
    }

    if (propName.equals("version") && result.length() > 0)
    {
      try
      {
        // Needs to return the version number of the spec we conform to.
        return new XNumber(1.0);
      }
      catch (Exception ex)
      {
        return new XString(result);
      }
    }
    else
      return new XString(result);
  }

  /**
   * Retrieve a propery bundle from a specified file
   * 
   * @param file The string name of the property file.  The name 
   * should already be fully qualified as path/filename
   * @param target The target property bag the file will be placed into.
   */
  public void loadPropertyFile(String file, Properties target)
  {

    InputStream is = null;

    try
    {
      try {
        java.lang.reflect.Method getCCL = Thread.class.getMethod("getContextClassLoader", NO_CLASSES);
        if (getCCL != null) {
          ClassLoader contextClassLoader = (ClassLoader) getCCL.invoke(Thread.currentThread(), NO_OBJS);
          is = contextClassLoader.getResourceAsStream(file); // file should be already fully specified
        }
      }
      catch (Exception e) {}

      if (is == null) {
        // NOTE! For the below getResourceAsStream in Sun JDK 1.1.8M
        //  we apparently must add the leading slash character - I 
        //  don't know why, but if it's not there, we throw an NPE from the below loading
        is = FuncSystemProperty.class.getResourceAsStream("/" + file); // file should be already fully specified
      }

      // get a buffered version
      BufferedInputStream bis = new BufferedInputStream(is);

      target.load(bis);  // and load up the property bag from this
      bis.close();  // close out after reading
    }
    catch (Exception ex)
    {
      // ex.printStackTrace();
      throw new org.apache.xml.utils.WrappedRuntimeException(ex);
    }
  }
}

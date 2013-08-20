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
package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.templates.Constants;
//import com.sun.org.apache.xalan.internal.transformer.TransformerImpl;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xpath.internal.ExtensionsProvider;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;

/**
 * Execute the ExtElementAvailable() function.
 * @xsl.usage advanced
 */
public class FuncExtElementAvailable extends FunctionOneArg
{

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

    String prefix;
    String namespace;
    String methName;

    String fullName = m_arg0.execute(xctxt).str();
    int indexOfNSSep = fullName.indexOf(':');

    if (indexOfNSSep < 0)
    {
      prefix = "";
      namespace = Constants.S_XSLNAMESPACEURL;
      methName = fullName;
    }
    else
    {
      prefix = fullName.substring(0, indexOfNSSep);
      namespace = xctxt.getNamespaceContext().getNamespaceForPrefix(prefix);
      if (null == namespace)
        return XBoolean.S_FALSE;
      methName= fullName.substring(indexOfNSSep + 1);
    }

    if (namespace.equals(Constants.S_XSLNAMESPACEURL)
    ||  namespace.equals(Constants.S_BUILTIN_EXTENSIONS_URL))
    {
          // <<<<<<<   TIGER SPECIFIC CHANGE >>>>>>>>>
          // As we are not supporting Xalan interpretive we are taking away the functionality
          // dependent on XSLT interpretive Transformer. Only way supported is to use XSLTC
          // and the execution path needed for supporting standard XPath API defined by
          // JAXP 1.3 . This method is overridden in XPath implementation to support
          // standard XPath functionality with xpath package of Xalan

        return XBoolean.S_FALSE;
    }
    else
    {
      //dml
      ExtensionsProvider extProvider = (ExtensionsProvider)xctxt.getOwnerObject();
      return extProvider.elementAvailable(namespace, methName)
             ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
  }
}

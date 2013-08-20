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
 * $Id: Method.java,v 1.2 2004/02/17 04:18:19 minchau Exp $
 */
package com.sun.org.apache.xml.internal.serializer;

/**
 * This class defines the constants which are the names of the four default
 * output methods.
 * <p>
 * Four default output methods are defined: XML, HTML, XHTML and TEXT.
 * Serializers may support additional output methods. The names of
 * these output methods should be encoded as <tt>namespace:local</tt>.
 *
 * @version Alpha
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 */
public final class Method
{

  /**
   * The output method for XML documents: <tt>xml</tt>.
   */
  public static final String XML = "xml";

  /**
   * The output method for HTML documents: <tt>html</tt>.
   */
  public static final String HTML = "html";

  /**
   * The output method for XHTML documents: <tt>xhtml</tt>.
   */
  public static final String XHTML = "xhtml";

  /**
   * The output method for text documents: <tt>text</tt>.
   */
  public static final String TEXT = "text";
  
  /**
   * The "internal" method, just used when no method is 
   * specified in the style sheet, and a serializer of this type wraps either an
   * XML or HTML type (depending on the first tag in the output being html or
   * not)
   */  
  public static final String UNKNOWN = "";
}

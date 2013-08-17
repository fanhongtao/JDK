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
package org.apache.xml.utils;

import javax.xml.transform.TransformerException;

import org.apache.xml.utils.URI;
import org.apache.xml.utils.URI.MalformedURIException;

import java.io.*;

import java.lang.StringBuffer;

/**
 * <meta name="usage" content="internal"/>
 * This class is used to resolve relative URIs and SystemID 
 * strings into absolute URIs.
 */
public class SystemIDResolver
{

  /**
   * Get absolute URI from a given relative URI. 
   * The URI is resolved relative to the system property "user.dir"
   *
   *
   * @param uri Relative URI to resolve
   *
   * @return Resolved absolute URI or the input relative URI if 
   * it could not be resolved.
   */
  public static String getAbsoluteURIFromRelative(String uri)
  {

    String curdir = System.getProperty("user.dir");

    if (null != curdir)
    {
      String base;
      if (curdir.startsWith(File.separator))
        base = "file://" + curdir;
      else
        base = "file:///" + curdir;
      if (uri != null)
        uri = base + System.getProperty("file.separator") + uri;
      else
        uri = base + System.getProperty("file.separator");
    }

    if (null != uri && (uri.indexOf('\\') > -1))
      uri = uri.replace('\\', '/');

    return uri;
  }
  
  /**
   * Take a SystemID string and try and turn it into a good absolute URL.
   *
   * @param urlString url A URL string, which may be relative or absolute.
   *
   * @return The resolved absolute URI
   * @throws TransformerException thrown if the string can't be turned into a URL.
   */
  public static String getAbsoluteURI(String url)
          throws TransformerException
  {
    if (url.startsWith(".."))
      url = new File(url).getAbsolutePath();
    if (url.startsWith(File.separator))
      url = "file://" + url;
    else if (url.indexOf(':') < 0)
    {
      url = getAbsoluteURIFromRelative(url);
    }
    else if (url.startsWith("file:") && url.charAt(5) != '/') 
    {
      url = getAbsoluteURIFromRelative(url.substring(5));
    }
    return url;
  }


  /**
   * Take a SystemID string and try and turn it into a good absolute URL.
   *
   * @param urlString SystemID string
   * @param base Base URI to use to resolve the given systemID
   *
   * @return The resolved absolute URI
   * @throws TransformerException thrown if the string can't be turned into a URL.
   */
  public static String getAbsoluteURI(String urlString, String base)
          throws TransformerException
  {
    boolean isAbsouteUrl = false;
    boolean needToResolve = false;    
 
    if(urlString.startsWith("file:") && urlString.charAt(5) != '/') 
    {
      needToResolve = true;
    }
    else if (urlString.indexOf(':') > 0)
      isAbsouteUrl = true;
    else if (urlString.startsWith(File.separator))
    {
      urlString = "file://" + urlString;
      isAbsouteUrl = true;
    }

    if ((!isAbsouteUrl) && ((null == base)
            || (base.indexOf(':') < 0)))
    {
      if (base != null && base.startsWith(File.separator))
        base = "file://" + base;
      else
        base = getAbsoluteURIFromRelative(base);
    }

    // bit of a hack here.  Need to talk to URI person to see if this can be fixed.
    if ((null != base) && needToResolve) 
         
    {
      if(base.equals(urlString))
      {
        base = "";
      }
      else
      {
        urlString = urlString.substring(5);
        isAbsouteUrl = false;
      }
    }   

    // This is probably a bad idea, we should at least check for quotes...
    if (null != base && (base.indexOf('\\') > -1))
      base = base.replace('\\', '/');

    if (null != urlString && (urlString.indexOf('\\') > -1))
      urlString = urlString.replace('\\', '/');

    URI uri;

    try
    {
      if ((null == base) || (base.length() == 0) || (isAbsouteUrl))
      {
        uri = new URI(urlString);
      }
      else
      {
        URI baseURI = new URI(base);

        uri = new URI(baseURI, urlString);
      }
    }
    catch (MalformedURIException mue)
    {
      throw new TransformerException(mue);
    }

    String uriStr = uri.toString();
    return uriStr;
  }
}

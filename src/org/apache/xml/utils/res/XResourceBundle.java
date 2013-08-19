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
package org.apache.xml.utils.res;

import java.util.*;

/**
 * <meta name="usage" content="internal"/>
 * The default (english) resource bundle.
 */
public class XResourceBundle extends ListResourceBundle
{

  /** Error resource constants */
  public static final String ERROR_RESOURCES =
    "org.apache.xalan.res.XSLTErrorResources", XSLT_RESOURCE =
    "org.apache.xml.utils.res.XResourceBundle", LANG_BUNDLE_NAME =
    "org.apache.xml.utils.res.XResources", MULT_ORDER =
    "multiplierOrder", MULT_PRECEDES = "precedes", MULT_FOLLOWS =
    "follows", LANG_ORIENTATION = "orientation", LANG_RIGHTTOLEFT =
    "rightToLeft", LANG_LEFTTORIGHT = "leftToRight", LANG_NUMBERING =
    "numbering", LANG_ADDITIVE = "additive", LANG_MULT_ADD =
    "multiplicative-additive", LANG_MULTIPLIER =
    "multiplier", LANG_MULTIPLIER_CHAR =
    "multiplierChar", LANG_NUMBERGROUPS = "numberGroups", LANG_NUM_TABLES =
    "tables", LANG_ALPHABET = "alphabet", LANG_TRAD_ALPHABET = "tradAlphabet";

  /**
   * Return a named ResourceBundle for a particular locale.  This method mimics the behavior
   * of ResourceBundle.getBundle().
   *
   * @param className Name of local-specific subclass.
   * @param locale the locale to prefer when searching for the bundle
   */
  public static final XResourceBundle loadResourceBundle(
          String className, Locale locale) throws MissingResourceException
  {

    String suffix = getResourceSuffix(locale);

    //System.out.println("resource " + className + suffix);
    try
    {
      
      // first try with the given locale
      String resourceName = className + suffix;
      return (XResourceBundle) ResourceBundle.getBundle(resourceName, locale);
    }
    catch (MissingResourceException e)
    {
      try  // try to fall back to en_US if we can't load
      {

        // Since we can't find the localized property file,
        // fall back to en_US.
        return (XResourceBundle) ResourceBundle.getBundle(
          XSLT_RESOURCE, new Locale("en", "US"));
      }
      catch (MissingResourceException e2)
      {

        // Now we are really in trouble.
        // very bad, definitely very bad...not going to get very far
        throw new MissingResourceException(
          "Could not load any resource bundles.", className, "");
      }
    }
  }

  /**
   * Return the resource file suffic for the indicated locale
   * For most locales, this will be based the language code.  However
   * for Chinese, we do distinguish between Taiwan and PRC
   *
   * @param locale the locale
   * @return an String suffix which canbe appended to a resource name
   */
  private static final String getResourceSuffix(Locale locale)
  {

    String lang = locale.getLanguage();
    String country = locale.getCountry();
    String variant = locale.getVariant();
    String suffix = "_" + locale.getLanguage();

    if (lang.equals("zh"))
      suffix += "_" + country;

    if (country.equals("JP"))
      suffix += "_" + country + "_" + variant;

    return suffix;
  }

  /**
   * Get the association list.
   *
   * @return The association list.
   */
  public Object[][] getContents()
  {
    return contents;
  }

  /** The association list. */
  static final Object[][] contents =
  {
    { "ui_language", "en" }, { "help_language", "en" }, { "language", "en" },
    { "alphabet",
      new char[]{ 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
                  'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
                  'Y', 'Z' } },
    { "tradAlphabet",
      new char[]{ 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
                  'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
                  'Y', 'Z' } },

    //language orientation
    { "orientation", "LeftToRight" },

    //language numbering   
    { "numbering", "additive" },
  };
}

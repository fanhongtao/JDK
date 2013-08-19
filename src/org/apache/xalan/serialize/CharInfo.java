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

import java.util.BitSet;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.net.*;

import java.util.Hashtable;

import org.apache.xml.utils.CharKey;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;

/**
 * This class provides services that tell if a character should have
 * special treatement, such as entity reference substitution or normalization
 * of a newline character.  It also provides character to entity reference
 * lookup.
 *
 * DEVELOPERS: See Known Issue in the constructor.
 */
public class CharInfo
{

  /** Bit map that tells if a given character should have special treatment. */
  BitSet m_specialsMap = new BitSet(65535);

  /** Lookup table for characters to entity references. */
  private Hashtable m_charToEntityRef = new Hashtable();

  /**
   * The name of the HTML entities file.
   * If specified, the file will be resource loaded with the default class loader.
   */
  public static String HTML_ENTITIES_RESOURCE = "HTMLEntities.res";

  /**
   * The name of the XML entities file.
   * If specified, the file will be resource loaded with the default class loader.
   */
  public static String XML_ENTITIES_RESOURCE = "XMLEntities.res";

  /** The linefeed character, which the parser should always normalize. */
  public static char S_LINEFEED = 0x0A;

  /** The carriage return character, which the parser should always normalize. */
  public static char S_CARRIAGERETURN = 0x0D;

  /** a zero length Class array used in the constructor */
  private static final Class[] NO_CLASSES = new Class[0];

  /** a zero length Object array used in the constructor */
  private static final Object[] NO_OBJS = new Object[0];


  /**
   * Constructor that reads in a resource file that describes the mapping of
   * characters to entity references.
   *
   * Resource files must be encoded in UTF-8 and have a format like:
   * <pre>
   * # First char # is a comment
   * Entity numericValue
   * quot 34
   * amp 38
   * </pre>
   * (Note: Why don't we just switch to .properties files? Oct-01 -sc)
   *
   * @param entitiesResource Name of entities resource file that should
   * be loaded, which describes that mapping of characters to entity references.
   */
  public CharInfo(String entitiesResource)
  {

    InputStream is = null;
    BufferedReader reader = null;
    int index;
    String name;
    String value;
    int code;
    String line;

    try
    {
      try
      {
        // Maintenance note: we should evaluate replacing getting the 
        //  ClassLoader with javax.xml.transform.FactoryFinder.findClassLoader()
        //  or similar code
        ClassLoader cl = CharInfo.class.getClassLoader();

        if (cl == null) {
          is = ClassLoader.getSystemResourceAsStream(entitiesResource);
        } else {
          is = cl.getResourceAsStream(entitiesResource);
        }
      }
      catch (Exception e) {}

      if (is == null)
        is = CharInfo.class.getResourceAsStream(entitiesResource);

      if (is == null)
      {
        URL url = new URL(entitiesResource);

        is = url.openStream();
      }

      if (is == null)
        throw new RuntimeException(XSLMessages.createMessage(XSLTErrorResources.ER_RESOURCE_COULD_NOT_FIND, new Object[]{entitiesResource, entitiesResource }));

      // Fix Bugzilla#4000: force reading in UTF-8
      //  This creates the de facto standard that Xalan's resource 
      //  files must be encoded in UTF-8. This should work in all JVMs.
      //
      // %REVIEW% KNOWN ISSUE: IT FAILS IN MICROSOFT VJ++, which
      // didn't implement the UTF-8 encoding. Theoretically, we should
      // simply let it fail in that case, since the JVM is obviously
      // broken if it doesn't support such a basic standard.  But
      // since there are still some users attempting to use VJ++ for
      // development, we have dropped in a fallback which makes a
      // second attempt using the platform's default encoding. In VJ++
      // this is apparently ASCII, which is subset of UTF-8... and
      // since the strings we'll be reading here are also primarily
      // limited to the 7-bit ASCII range (at least, in English
      // versions of Xalan), this should work well enough to keep us
      // on the air until we're ready to officially decommit from
      // VJ++.
      try
      {
	reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
      }
      catch(java.io.UnsupportedEncodingException e)
      {
	reader = new BufferedReader(new InputStreamReader(is));
      }

      line = reader.readLine();

      while (line != null)
      {
        if (line.length() == 0 || line.charAt(0) == '#')
        {
          line = reader.readLine();

          continue;
        }

        index = line.indexOf(' ');

        if (index > 1)
        {
          name = line.substring(0, index);

          ++index;

          if (index < line.length())
          {
            value = line.substring(index);
            index = value.indexOf(' ');

            if (index > 0)
              value = value.substring(0, index);

            code = Integer.parseInt(value);

            defineEntity(name, (char) code);
          }
        }

        line = reader.readLine();
      }

      is.close();
      m_specialsMap.set(S_LINEFEED);
      m_specialsMap.set(S_CARRIAGERETURN);
    }
    catch (Exception except)
    {
      throw new RuntimeException(XSLMessages.createMessage(XSLTErrorResources.ER_RESOURCE_COULD_NOT_LOAD, new Object[]{entitiesResource,  except.toString(), entitiesResource, except.toString() }));
    }
    finally
    {
      if (is != null)
      {
        try
        {
          is.close();
        }
        catch (Exception except){}
      }
    }
  }

  /**
   * Defines a new character reference. The reference's name and value are
   * supplied. Nothing happens if the character reference is already defined.
   * <p>Unlike internal entities, character references are a string to single
   * character mapping. They are used to map non-ASCII characters both on
   * parsing and printing, primarily for HTML documents. '&lt;amp;' is an
   * example of a character reference.</p>
   *
   * @param name The entity's name
   * @param value The entity's value
   */
  protected void defineEntity(String name, char value)
  {
    CharKey character = new CharKey(value);

    m_charToEntityRef.put(character, name);
    m_specialsMap.set(value);
  }
  
  private CharKey m_charKey = new CharKey();

  /**
   * Resolve a character to an entity reference name.
   *
   * This is reusing a stored key object, in an effort to avoid
   * heap activity. Unfortunately, that introduces a threading risk.
   * Simplest fix for now is to make it a synchronized method, or to give
   * up the reuse; I see very little performance difference between them.
   * Long-term solution would be to replace the hashtable with a sparse array
   * keyed directly from the character's integer value; see DTM's
   * string pool for a related solution.
   *
   * @param value character value that should be resolved to a name.
   *
   * @return name of character entity, or null if not found.
   */
  synchronized 
  public String getEntityNameForChar(char value)
  {
    // CharKey m_charKey = new CharKey(); //Alternative to synchronized
    m_charKey.setChar(value);
    return (String) m_charToEntityRef.get(m_charKey);
  }

  /**
   * Tell if the character argument should have special treatment.
   *
   * @param value character value.
   *
   * @return true if the character should have any special treatment.
   */
  public boolean isSpecial(char value)
  {
    return m_specialsMap.get(value);
  }
}

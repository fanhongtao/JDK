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
package org.apache.xpath.res;

import java.util.*;

/**
 * <meta name="usage" content="internal"/>
 * The default (english) resource bundle.
 */
public class XPATHErrorResourceBundle extends ListResourceBundle
{

  /**
   * Get the association list.
   *
   * @return The association list.
   */
  public Object[][] getContents()
  {
    return contents;
  }

  /** The association list.         */
  static final Object[][] contents =
  {
    { "ui_language", "en" }, { "help_language", "en" }, { "language", "en" },
    { "ERROR0001", "0001" }, { "ERROR0002", "0002" }, { "ERROR0003", "0003" },
    { "ERROR0004", "0004" }, { "ERROR0005", "0005" }, { "ERROR0006", "0006" },
    { "ERROR0007", "0007" }, { "ERROR0008", "0008" }, { "ERROR0009", "0009" },
    { "ERROR0010", "0010" }, { "ERROR0011", "0011" }, { "ERROR0012", "0012" },
    { "ERROR0013", "0013" }, { "ERROR0014", "0014" }, { "ERROR0015", "0015" },
    { "ERROR0016", "0016" }, { "ERROR0017", "0017" }, { "ERROR0018", "0018" },
    { "ERROR0019", "0019" }, { "ERROR0020", "0020" }, { "ERROR0021", "0021" },
    { "ERROR0022", "0022" }, { "ERROR0023", "0023" }, { "ERROR0024", "0024" },
    { "ERROR0025", "0025" }, { "ERROR0026", "0026" }, { "ERROR0027", "0027" },
    { "ERROR0028", "0028" }, { "ERROR0029", "0029" }, { "ERROR0030", "0030" },
    { "ERROR0031", "0031" }, { "ERROR0032", "0032" }, { "ERROR0033", "0033" },
    { "ERROR0034", "0034" }, { "ERROR0035", "0035" }, { "ERROR0036", "0036" },
    { "ERROR0037", "0037" }, { "ERROR0038", "0038" }, { "ERROR0039", "0039" },
    { "ERROR0040", "0040" }, { "ERROR0041", "0041" }, { "ERROR0042", "0042" },
    { "ERROR0043", "0043" }, { "ERROR0044", "0044" }, { "ERROR0045", "0045" },
    { "ERROR0046", "0046" }, { "ERROR0047", "0047" }, { "ERROR0048", "0048" },
    { "ERROR0049", "0049" }, { "ERROR0050", "0050" }, { "ERROR0051", "0051" },
    { "ERROR0052", "0052" }, { "ERROR0053", "0053" }, { "ERROR0054", "0054" },
    { "ERROR0055", "0055" }, { "ERROR0056", "0056" }, { "ERROR0057", "0057" },
    { "ERROR0058", "0058" }, { "ERROR0059", "0059" }, { "ERROR0060", "0060" },

    { "ERROR0061", "0061" }, { "ERROR0062", "0062" }, { "ERROR0063", "0063" },
    { "ERROR0064", "0064" }, { "ERROR0065", "0065" }, { "ERROR0066", "0066" },
    { "ERROR0067", "0067" }, { "ERROR0068", "0068" }, { "ERROR0069", "0069" },
    { "ERROR0070", "0070" }, { "ERROR0071", "0071" }, { "ERROR0072", "0072" },
    { "ERROR0073", "0073" }, { "ERROR0074", "0074" }, { "ERROR0075", "0075" },
    { "ERROR0076", "0076" }, { "ERROR0077", "0077" }, { "ERROR0078", "0078" },
    { "ERROR0079", "0079" }, { "ERROR0080", "0080" }, { "ERROR0081", "0081" },
    { "ERROR0082", "0082" }, { "ERROR0083", "0083" }, { "ERROR0084", "0084" },
    { "ERROR0085", "0085" }, { "ERROR0086", "0086" }, { "ERROR0087", "0087" },
    { "ERROR0088", "0088" }, { "ERROR0089", "0089" }, { "ERROR0090", "0090" },
    { "ERROR0091", "0091" }, { "ERROR0092", "0092" }, { "ERROR0093", "0093" },
    { "ERROR0094", "0094" }, { "ERROR0095", "0095" }, { "ERROR0096", "0096" },
    { "ERROR0097", "0097" }, { "ERROR0098", "0098" }, { "ERROR0099", "0099" },
    { "ERROR0100", "0100" }, { "ERROR0101", "0101" }, { "ERROR0102", "0102" },
    { "ERROR0103", "0103" }, { "ERROR0104", "0104" }, { "ERROR0105", "0105" },
    { "ERROR0106", "0106" }, { "ERROR0107", "0107" }, { "ERROR0108", "0108" },
    { "ERROR0109", "0109" }, { "ERROR0110", "0110" }, { "ERROR0111", "0111" },
    { "ERROR0112", "0112" }, { "ERROR0113", "0113" }, { "ERROR0114", "0114" },
    { "ERROR0115", "0115" }, { "ERROR0116", "0116" },

    { "WARNING0001", "0001" }, { "WARNING0002", "0002" },
    { "WARNING0003", "0003" }, { "WARNING0004", "0004" },
    { "WARNING0005", "0005" }, { "WARNING0006", "0006" },
    { "WARNING0007", "0007" }, { "WARNING0008", "0008" },
    { "WARNING0009", "0009" }, { "WARNING0010", "0010" },
    { "WARNING0011", "0011" }
  };
}

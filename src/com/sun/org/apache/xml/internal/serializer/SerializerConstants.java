/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
 * $Id: SerializerConstants.java,v 1.2 2004/02/17 04:18:19 minchau Exp $
 */
package com.sun.org.apache.xml.internal.serializer;

/**
 * @author minchau
 *
 * Constants used in serialization, such as the string "xmlns"
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
public interface SerializerConstants
{

    /** To insert ]]> in a CDATA section by ending the last CDATA section with
     * ]] and starting the next CDATA section with >
     */
    static final String CDATA_CONTINUE = "]]]]><![CDATA[>";
    /**
     * The constant "]]>"
     */
    static final String CDATA_DELIMITER_CLOSE = "]]>";
    static final String CDATA_DELIMITER_OPEN = "<![CDATA[";

    static final char[] CNTCDATA = CDATA_CONTINUE.toCharArray();
    static final char[] BEGCDATA = CDATA_DELIMITER_OPEN.toCharArray();
    static final char[] ENDCDATA = CDATA_DELIMITER_CLOSE.toCharArray();

    static final String EMPTYSTRING = "";

    static final String ENTITY_AMP = "&amp;";
    static final String ENTITY_CRLF = "&#xA;";
    static final String ENTITY_GT = "&gt;";
    static final String ENTITY_LT = "&lt;";
    static final String ENTITY_QUOT = "&quot;";

    static final String XML_PREFIX = "xml";
    static final String XMLNS_PREFIX = "xmlns";
    static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
   
    public static final String DEFAULT_SAX_SERIALIZER="com.sun.org.apache.xml.internal.serializer.ToXMLSAXHandler";
}

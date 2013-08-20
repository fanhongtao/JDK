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
 * $Id: EncodingInfo.java,v 1.2 2004/02/17 04:18:18 minchau Exp $
 */
package com.sun.org.apache.xml.internal.serializer;
/**
 * Holds information about a given encoding, which is the Java name for the
 * encoding, the equivalent ISO name, and the integer value of the last pritable
 * character in the encoding.
 */
public class EncodingInfo extends Object
{

    /**
     * The ISO encoding name.
     */
    final String name;

    /**
     * The name used by the Java convertor.
     */
    final String javaName;

    /**
     * The last printable character.
     */
    final int lastPrintable;

    /**
     * Create an EncodingInfo object based on the name, java name, and the
     * max character size.
     *
     * @param name non-null reference to the ISO name.
     * @param javaName non-null reference to the Java encoding name.
     * @param lastPrintable The maximum character that can be written.
     */
    public EncodingInfo(String name, String javaName, int lastPrintable)
    {

        this.name = name;
        this.javaName = javaName;
        this.lastPrintable = lastPrintable;
    }
}

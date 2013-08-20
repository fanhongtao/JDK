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
 * $Id: DOMSerializer.java,v 1.3 2004/02/17 04:18:18 minchau Exp $
 */
package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;

import org.w3c.dom.Node;

/**
 * Interface for a DOM serializer implementation.
 * <p>
 * The DOM serializer is a facet of a serializer. A serializer may or may
 * not support a DOM serializer.
 * <p>
 * Example:
 * <pre>
 * Document     doc;
 * Serializer   ser;
 * OutputStream os;
 *
 * ser.setOutputStream( os );
 * ser.asDOMSerializer( doc );
 * </pre>
 *
 *
 * @version Alpha
 * @author <a href="mailto:Scott_Boag/CAM/Lotus@lotus.com">Scott Boag</a>
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 */
public interface DOMSerializer
{
    /**
     * Serializes the DOM node. Throws an exception only if an I/O
     * exception occured while serializing.
     *
     * @param node the DOM node to serialize
     * @throws IOException if an I/O exception occured while serializing
     */
    public void serialize(Node node) throws IOException;
}

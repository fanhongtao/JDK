/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999,2000 The Apache Software Foundation.  All rights
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
 * 4. The names "Xerces" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xml.dtm.ref;
import java.util.Vector;
import java.util.Hashtable;

/** <p>CustomStringPool is an example of appliction provided data structure
 * for a DTM implementation to hold symbol references, e.g. elelment names.
 * It will follow the DTMDStringPool interface and use two simple methods
 * indexToString(int i) and stringToIndex(Sring s) to map between a set of
 * string values and a set of integer index values.  Therefore, an application
 * may improve DTM processing speed by substituting the DTM symbol resolution
 * tables with application specific quick symbol resolution tables.</p>
 *
 * %REVIEW% The only difference between this an DTMStringPool seems to be that
 * it uses a java.lang.Hashtable full of Integers rather than implementing its
 * own hashing. Joe deliberately avoided that approach when writing
 * DTMStringPool, since it is both much more memory-hungry and probably slower
 * -- especially in JDK 1.1.x, where Hashtable is synchronized. We need to
 * either justify this implementation or discard it.
 *
 * <p>Status: In progress, under discussion.</p>
 * */
public class CustomStringPool extends DTMStringPool {
        //final Vector m_intToString;
        //static final int HASHPRIME=101;
        //int[] m_hashStart=new int[HASHPRIME];
        final Hashtable m_stringToInt = new Hashtable();
        public static final int NULL=-1;

        public CustomStringPool()
        {
                super();
                /*m_intToString=new Vector();
                System.out.println("In constructor m_intToString is " + 
                                                                                         ((null == m_intToString) ? "null" : "not null"));*/
                //m_stringToInt=new Hashtable();
                //removeAllElements();
        }

        public void removeAllElements()
        {
                m_intToString.removeAllElements();
                if (m_stringToInt != null) 
                        m_stringToInt.clear();
        }

        /** @return string whose value is uniquely identified by this integer index.
         * @throws java.lang.ArrayIndexOutOfBoundsException
         *  if index doesn't map to a string.
         * */
        public String indexToString(int i)
        throws java.lang.ArrayIndexOutOfBoundsException
        {
                return(String) m_intToString.elementAt(i);
        }

        /** @return integer index uniquely identifying the value of this string. */
        public int stringToIndex(String s)
        {
                if (s==null) return NULL;
                Integer iobj=(Integer)m_stringToInt.get(s);
                if (iobj==null) {
                        m_intToString.addElement(s);
                        iobj=new Integer(m_intToString.size());
                        m_stringToInt.put(s,iobj);
                }
                return iobj.intValue();
        }
}

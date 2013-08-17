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

// %REVIEW% Should this be based on SuballocatedIntVector instead?
// (Unclear. Pools will rarely be huge. But if they ever are...)
import org.apache.xml.utils.IntVector;
import java.util.Vector;

/** <p>DTMStringPool is an "interning" mechanism for strings. It will
 * create a stable 1:1 mapping between a set of string values and a set of
 * integer index values, so the integers can be used to reliably and
 * uniquely identify (and when necessary retrieve) the strings.</p>
 *
 * <p>Design Priorities:
 * <ul>
 * <li>String-to-index lookup speed is critical.</li>
 * <li>Index-to-String lookup speed is slightly less so.</li>
 * <li>Threadsafety is not guaranteed at this level.
 * Enforce that in the application if needed.</li>
 * <li>Storage efficiency is an issue but not a huge one.
 * It is expected that string pools won't exceed about 2000 entries.</li>
 * </ul>
 * </p>
 *
 * <p>Implementation detail: A standard Hashtable is relatively
 * inefficient when looking up primitive int values, especially when
 * we're already maintaining an int-to-string vector.  So I'm
 * maintaining a simple hash chain within this class.</p>
 *
 * <p>NOTE: There is nothing in the code that has a real dependency upon
 * String. It would work with any object type that implements reliable
 * .hashCode() and .equals() operations. The API enforces Strings because
 * it's safer that way, but this could trivially be turned into a general
 * ObjectPool if one was needed.</p>
 *
 * <p>Status: Passed basic test in main().</p>
 * */
public class DTMStringPool
{
  Vector m_intToString;
  static final int HASHPRIME=101;
  int[] m_hashStart=new int[HASHPRIME];
  IntVector m_hashChain;
  public static final int NULL=-1;

  public DTMStringPool()
    {
      m_intToString=new Vector();
      m_hashChain=new IntVector(512);
      removeAllElements();
      
      // -sb Add this to force empty strings to be index 0.
      stringToIndex("");
    }
  
  public void removeAllElements()
    {
      m_intToString.removeAllElements();
      for(int i=0;i<HASHPRIME;++i)
        m_hashStart[i]=NULL;
      m_hashChain.removeAllElements();
    }

  /** @return string whose value is uniquely identified by this integer index.
   * @throws java.lang.ArrayIndexOutOfBoundsException
   *  if index doesn't map to a string.
   * */ 
  public String indexToString(int i)
    throws java.lang.ArrayIndexOutOfBoundsException
    {
      if(i==NULL) return null;
      return (String) m_intToString.elementAt(i);
    }

  /** @return integer index uniquely identifying the value of this string. */ 
  public int stringToIndex(String s)
    {
      if(s==null) return NULL;
      
      int hashslot=s.hashCode()%HASHPRIME;
      if(hashslot<0) hashslot=-hashslot;

      // Is it one we already know?
      int hashlast=m_hashStart[hashslot];
      int hashcandidate=hashlast;
      while(hashcandidate!=NULL)
        {
          if(m_intToString.elementAt(hashcandidate).equals(s))
            return hashcandidate;

          hashlast=hashcandidate;
          hashcandidate=m_hashChain.elementAt(hashcandidate);
        }
      
      // New value. Add to tables.
      int newIndex=m_intToString.size();
      m_intToString.addElement(s);

      m_hashChain.addElement(NULL);	// Initialize to no-following-same-hash
      if(hashlast==NULL)  // First for this hash
        m_hashStart[hashslot]=newIndex;
      else // Link from previous with same hash
        m_hashChain.setElementAt(newIndex,hashlast);

      return newIndex;
    }

  /** Command-line unit test driver. This test relies on the fact that
   * this version of the pool assigns indices consecutively, starting
   * from zero, as new unique strings are encountered.
   */
  public static void main(String[] args)
  {
    String[] word={
      "Zero","One","Two","Three","Four","Five",
      "Six","Seven","Eight","Nine","Ten",
      "Eleven","Twelve","Thirteen","Fourteen","Fifteen",
      "Sixteen","Seventeen","Eighteen","Nineteen","Twenty",
      "Twenty-One","Twenty-Two","Twenty-Three","Twenty-Four",
      "Twenty-Five","Twenty-Six","Twenty-Seven","Twenty-Eight",
      "Twenty-Nine","Thirty","Thirty-One","Thirty-Two",
      "Thirty-Three","Thirty-Four","Thirty-Five","Thirty-Six",
      "Thirty-Seven","Thirty-Eight","Thirty-Nine"};

    DTMStringPool pool=new DTMStringPool();

    System.out.println("If no complaints are printed below, we passed initial test.");

    for(int pass=0;pass<=1;++pass)
      {
        int i;

        for(i=0;i<word.length;++i)
          {
            int j=pool.stringToIndex(word[i]);
            if(j!=i)
              System.out.println("\tMismatch populating pool: assigned "+
                                 j+" for create "+i);
          }

        for(i=0;i<word.length;++i)
          {
            int j=pool.stringToIndex(word[i]);
            if(j!=i)
              System.out.println("\tMismatch in stringToIndex: returned "+
                                 j+" for lookup "+i);
          }

        for(i=0;i<word.length;++i)
          {
            String w=pool.indexToString(i);
            if(!word[i].equals(w))
              System.out.println("\tMismatch in indexToString: returned"+
                                 w+" for lookup "+i);
          }
        
        pool.removeAllElements();
        
        System.out.println("\nPass "+pass+" complete\n");
      } // end pass loop
  }
}

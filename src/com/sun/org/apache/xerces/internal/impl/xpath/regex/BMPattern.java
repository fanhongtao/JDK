/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights 
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

package com.sun.org.apache.xerces.internal.impl.xpath.regex;

import java.text.CharacterIterator;

/**
 * Boyer-Moore searcher.
 *
 * @version $Id: BMPattern.java,v 1.3 2002/08/09 15:18:17 neilg Exp $
 */
public class BMPattern {
    char[] pattern;
    int[] shiftTable;
    boolean ignoreCase;

    public BMPattern(String pat, boolean ignoreCase) {
        this(pat, 256, ignoreCase);
    }

    public BMPattern(String pat, int tableSize, boolean ignoreCase) {
        this.pattern = pat.toCharArray();
        this.shiftTable = new int[tableSize];
        this.ignoreCase = ignoreCase;

        int length = pattern.length;
        for (int i = 0;  i < this.shiftTable.length;  i ++)
            this.shiftTable[i] = length;

        for (int i = 0;  i < length;  i ++) {
            char ch = this.pattern[i];
            int diff = length-i-1;
            int index = ch % this.shiftTable.length;
            if (diff < this.shiftTable[index])
                this.shiftTable[index] = diff;
            if (this.ignoreCase) {
                ch = Character.toUpperCase(ch);
                index = ch % this.shiftTable.length;
                if (diff < this.shiftTable[index])
                    this.shiftTable[index] = diff;
                ch = Character.toLowerCase(ch);
                index = ch % this.shiftTable.length;
                if (diff < this.shiftTable[index])
                    this.shiftTable[index] = diff;
            }
        }
    }

    /**
     *
     * @return -1 if <var>iterator</var> does not contain this pattern.
     */
    public int matches(CharacterIterator iterator, int start, int limit) {
        if (this.ignoreCase)  return this.matchesIgnoreCase(iterator, start, limit);
        int plength = this.pattern.length;
        if (plength == 0)  return start;
        int index = start+plength;
        while (index <= limit) {
            int pindex = plength;
            int nindex = index+1;
            char ch;
            do {
                if ((ch = iterator.setIndex(--index)) != this.pattern[--pindex])
                    break;
                if (pindex == 0)
                    return index;
            } while (pindex > 0);
            index += this.shiftTable[ch % this.shiftTable.length]+1;
            if (index < nindex)  index = nindex;
        }
        return -1;
    }

    /**
     *
     * @return -1 if <var>str</var> does not contain this pattern.
     */
    public int matches(String str, int start, int limit) {
        if (this.ignoreCase)  return this.matchesIgnoreCase(str, start, limit);
        int plength = this.pattern.length;
        if (plength == 0)  return start;
        int index = start+plength;
        while (index <= limit) {
            //System.err.println("Starts at "+index);
            int pindex = plength;
            int nindex = index+1;
            char ch;
            do {
                if ((ch = str.charAt(--index)) != this.pattern[--pindex])
                    break;
                if (pindex == 0)
                    return index;
            } while (pindex > 0);
            index += this.shiftTable[ch % this.shiftTable.length]+1;
            if (index < nindex)  index = nindex;
        }
        return -1;
    }
    /**
     *
     * @return -1 if <var>chars</char> does not contain this pattern.
     */
    public int matches(char[] chars, int start, int limit) {
        if (this.ignoreCase)  return this.matchesIgnoreCase(chars, start, limit);
        int plength = this.pattern.length;
        if (plength == 0)  return start;
        int index = start+plength;
        while (index <= limit) {
            //System.err.println("Starts at "+index);
            int pindex = plength;
            int nindex = index+1;
            char ch;
            do {
                if ((ch = chars[--index]) != this.pattern[--pindex])
                    break;
                if (pindex == 0)
                    return index;
            } while (pindex > 0);
            index += this.shiftTable[ch % this.shiftTable.length]+1;
            if (index < nindex)  index = nindex;
        }
        return -1;
    }

    int matchesIgnoreCase(CharacterIterator iterator, int start, int limit) {
        int plength = this.pattern.length;
        if (plength == 0)  return start;
        int index = start+plength;
        while (index <= limit) {
            int pindex = plength;
            int nindex = index+1;
            char ch;
            do {
                char ch1 = ch = iterator.setIndex(--index);
                char ch2 = this.pattern[--pindex];
                if (ch1 != ch2) {
                    ch1 = Character.toUpperCase(ch1);
                    ch2 = Character.toUpperCase(ch2);
                    if (ch1 != ch2 && Character.toLowerCase(ch1) != Character.toLowerCase(ch2))
                        break;
                }
                if (pindex == 0)
                    return index;
            } while (pindex > 0);
            index += this.shiftTable[ch % this.shiftTable.length]+1;
            if (index < nindex)  index = nindex;
        }
        return -1;
    }
    
    int matchesIgnoreCase(String text, int start, int limit) {
        int plength = this.pattern.length;
        if (plength == 0)  return start;
        int index = start+plength;
        while (index <= limit) {
            int pindex = plength;
            int nindex = index+1;
            char ch;
            do {
                char ch1 = ch = text.charAt(--index);
                char ch2 = this.pattern[--pindex];
                if (ch1 != ch2) {
                    ch1 = Character.toUpperCase(ch1);
                    ch2 = Character.toUpperCase(ch2);
                    if (ch1 != ch2 && Character.toLowerCase(ch1) != Character.toLowerCase(ch2))
                        break;
                }
                if (pindex == 0)
                    return index;
            } while (pindex > 0);
            index += this.shiftTable[ch % this.shiftTable.length]+1;
            if (index < nindex)  index = nindex;
        }
        return -1;
    }
    int matchesIgnoreCase(char[] chars, int start, int limit) {
        int plength = this.pattern.length;
        if (plength == 0)  return start;
        int index = start+plength;
        while (index <= limit) {
            int pindex = plength;
            int nindex = index+1;
            char ch;
            do {
                char ch1 = ch = chars[--index];
                char ch2 = this.pattern[--pindex];
                if (ch1 != ch2) {
                    ch1 = Character.toUpperCase(ch1);
                    ch2 = Character.toUpperCase(ch2);
                    if (ch1 != ch2 && Character.toLowerCase(ch1) != Character.toLowerCase(ch2))
                        break;
                }
                if (pindex == 0)
                    return index;
            } while (pindex > 0);
            index += this.shiftTable[ch % this.shiftTable.length]+1;
            if (index < nindex)  index = nindex;
        }
        return -1;
    }

    /*
    public static void main(String[] argv) {
        try {
            int[] shiftTable = new int[256];
            initializeBoyerMoore(argv[0], shiftTable, true);
            int o = -1;
            CharacterIterator ite = new java.text.StringCharacterIterator(argv[1]);
            long start = System.currentTimeMillis();
            //for (int i = 0;  i < 10000;  i ++)
                o = searchIgnoreCasesWithBoyerMoore(ite, 0, argv[0], shiftTable);
            start = System.currentTimeMillis()-start;
            System.out.println("Result: "+o+", Elapsed: "+start);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/
}


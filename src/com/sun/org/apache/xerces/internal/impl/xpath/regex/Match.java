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
 * 
 * An instance of this class has ranges captured in matching.
 *
 * @see RegularExpression#matches(char[], int, int, Match)
 * @see RegularExpression#matches(char[], Match)
 * @see RegularExpression#matches(java.text.CharacterIterator, Match)
 * @see RegularExpression#matches(java.lang.String, int, int, Match)
 * @see RegularExpression#matches(java.lang.String, Match)
 * @author TAMURA Kent &lt;kent@trl.ibm.co.jp&gt;
 * @version $Id: Match.java,v 1.4 2002/08/09 15:18:17 neilg Exp $
 */
public class Match implements Cloneable {
    int[] beginpos = null;
    int[] endpos = null;
    int nofgroups = 0;

    CharacterIterator ciSource = null;
    String strSource = null;
    char[] charSource = null;

    /**
     * Creates an instance.
     */
    public Match() {
    }

    /**
     *
     */
    public synchronized Object clone() {
        Match ma = new Match();
        if (this.nofgroups > 0) {
            ma.setNumberOfGroups(this.nofgroups);
            if (this.ciSource != null)  ma.setSource(this.ciSource);
            if (this.strSource != null)  ma.setSource(this.strSource);
            for (int i = 0;  i < this.nofgroups;  i ++) {
                ma.setBeginning(i, this.getBeginning(i));
                ma.setEnd(i, this.getEnd(i));
            }
        }
        return ma;
    }

    /**
     *
     */
    protected void setNumberOfGroups(int n) {
        int oldn = this.nofgroups;
        this.nofgroups = n;
        if (oldn <= 0
            || oldn < n || n*2 < oldn) {
            this.beginpos = new int[n];
            this.endpos = new int[n];
        }
        for (int i = 0;  i < n;  i ++) {
            this.beginpos[i] = -1;
            this.endpos[i] = -1;
        }
    }

    /**
     *
     */
    protected void setSource(CharacterIterator ci) {
        this.ciSource = ci;
        this.strSource = null;
        this.charSource = null;
    }
    /**
     *
     */
    protected void setSource(String str) {
        this.ciSource = null;
        this.strSource = str;
        this.charSource = null;
    }
    /**
     *
     */
    protected void setSource(char[] chars) {
        this.ciSource = null;
        this.strSource = null;
        this.charSource = chars;
    }

    /**
     *
     */
    protected void setBeginning(int index, int v) {
        this.beginpos[index] = v;
    }

    /**
     *
     */
    protected void setEnd(int index, int v) {
        this.endpos[index] = v;
    }

    /**
     * Return the number of regular expression groups.
     * This method returns 1 when the regular expression has no capturing-parenthesis.
     */
    public int getNumberOfGroups() {
        if (this.nofgroups <= 0)
            throw new IllegalStateException("A result is not set.");
        return this.nofgroups;
    }

    /**
     * Return a start position in the target text matched to specified regular expression group.
     *
     * @param index Less than <code>getNumberOfGroups()</code>.
     */
    public int getBeginning(int index) {
        if (this.beginpos == null)
            throw new IllegalStateException("A result is not set.");
        if (index < 0 || this.nofgroups <= index)
            throw new IllegalArgumentException("The parameter must be less than "
                                               +this.nofgroups+": "+index);
        return this.beginpos[index];
    }

    /**
     * Return an end position in the target text matched to specified regular expression group.
     *
     * @param index Less than <code>getNumberOfGroups()</code>.
     */
    public int getEnd(int index) {
        if (this.endpos == null)
            throw new IllegalStateException("A result is not set.");
        if (index < 0 || this.nofgroups <= index)
            throw new IllegalArgumentException("The parameter must be less than "
                                               +this.nofgroups+": "+index);
        return this.endpos[index];
    }

    /**
     * Return an substring of the target text matched to specified regular expression group.
     *
     * @param index Less than <code>getNumberOfGroups()</code>.
     */
    public String getCapturedText(int index) {
        if (this.beginpos == null)
            throw new IllegalStateException("match() has never been called.");
        if (index < 0 || this.nofgroups <= index)
            throw new IllegalArgumentException("The parameter must be less than "
                                               +this.nofgroups+": "+index);
        String ret;
        int begin = this.beginpos[index], end = this.endpos[index];
        if (begin < 0 || end < 0)  return null;
        if (this.ciSource != null) {
            ret = REUtil.substring(this.ciSource, begin, end);
        } else if (this.strSource != null) {
            ret = this.strSource.substring(begin, end);
        } else {
            ret = new String(this.charSource, begin, end-begin);
        }
        return ret;
    }
}

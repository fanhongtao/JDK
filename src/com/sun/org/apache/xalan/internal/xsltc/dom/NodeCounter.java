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
 * $Id: NodeCounter.java,v 1.10 2004/02/16 22:54:59 minchau Exp $
 */

package com.sun.org.apache.xalan.internal.xsltc.dom;

import java.util.Vector;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 * @author Morten Jorgensen
 */
public abstract class NodeCounter implements Axis {
    public static final int END = DTM.NULL;

    protected int _node = END;
    protected int _nodeType = DOM.FIRST_TYPE - 1;
    protected int _value = Integer.MIN_VALUE;

    public final DOM          _document;
    public final DTMAxisIterator _iterator;
    public final Translet     _translet;

    protected String _format;
    protected String _lang;
    protected String _letterValue;
    protected String _groupSep;
    protected int    _groupSize;

    private boolean separFirst = true;
    private boolean separLast = false;
    private Vector separToks = null;
    private Vector formatToks = null;
    private int nSepars  = 0;
    private int nFormats = 0;

    private static String[] Thousands = 
        {"", "m", "mm", "mmm" };
    private static String[] Hundreds = 
	{"", "c", "cc", "ccc", "cd", "d", "dc", "dcc", "dccc", "cm"};
    private static String[] Tens = 
	{"", "x", "xx", "xxx", "xl", "l", "lx", "lxx", "lxxx", "xc"};
    private static String[] Ones = 
	{"", "i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix"};

    protected NodeCounter(Translet translet,
			  DOM document, DTMAxisIterator iterator) {
	_translet = translet;
	_document = document;
	_iterator = iterator;
    }

    /** 
     * Set the start node for this counter. The same <tt>NodeCounter</tt>
     * object can be used multiple times by resetting the starting node.
     */
    abstract public NodeCounter setStartNode(int node);

    /** 
     * If the user specified a value attribute, use this instead of 
     * counting nodes.
     */
    public NodeCounter setValue(int value) {
	_value = value;
	return this;
    }

    /**
     * Sets formatting fields before calling formatNumbers().
     */
    protected void setFormatting(String format, String lang, String letterValue,
				 String groupSep, String groupSize) {
	_lang = lang;
	_format = format;
	_groupSep = groupSep;
	_letterValue = letterValue;

	try {
	    _groupSize = Integer.parseInt(groupSize);
	}
	catch (NumberFormatException e) {
	    _groupSize = 0;
	}

	final int length = _format.length();
	boolean isFirst = true;
	separFirst = true;
	separLast = false;

        separToks = new Vector();
        formatToks = new Vector();

	/* 
	 * Tokenize the format string into alphanumeric and non-alphanumeric
	 * tokens as described in M. Kay page 241.
	 */
	for (int j = 0, i = 0; i < length;) {
            char c = _format.charAt(i);
            for (j = i; Character.isLetterOrDigit(c);) {
                if (++i == length) break;
		c = _format.charAt(i);
            }
            if (i > j) {
                if (isFirst) {
                    separToks.addElement(".");
                    isFirst = separFirst = false;
                }
                formatToks.addElement(_format.substring(j, i));
            }

            if (i == length) break;

            c = _format.charAt(i);
            for (j = i; !Character.isLetterOrDigit(c);) {
                if (++i == length) break;
                c = _format.charAt(i);
                isFirst = false;
            }
            if (i > j) {
                separToks.addElement(_format.substring(j, i));
            }
        }

	nSepars = separToks.size();
	nFormats = formatToks.size(); 
	if (nSepars > nFormats) separLast = true;

	if (separFirst) nSepars--;
	if (separLast) nSepars--;
	if (nSepars == 0) {
	    separToks.insertElementAt(".", 1);
 	    nSepars++;
	}
	if (separFirst) nSepars ++;
    }

    /**
     * Sets formatting fields to their default values.
     */
    public NodeCounter setDefaultFormatting() {
	setFormatting("1", "en", "alphabetic", null, null);
	return this;
    }

    /**
     * Returns the position of <tt>node</tt> according to the level and 
     * the from and count patterns.
     */
    abstract public String getCounter();

    /**
     * Returns the position of <tt>node</tt> according to the level and 
     * the from and count patterns. This position is converted into a
     * string based on the arguments passed.
     */
    public String getCounter(String format, String lang, String letterValue,
			     String groupSep, String groupSize) {
	setFormatting(format, lang, letterValue, groupSep, groupSize);
	return getCounter();
    }

    /**
     * Returns true if <tt>node</tt> matches the count pattern. By
     * default a node matches the count patterns if it is of the 
     * same type as the starting node.
     */
    public boolean matchesCount(int node) {
	return _nodeType == _document.getExpandedTypeID(node);
    }

    /**
     * Returns true if <tt>node</tt> matches the from pattern. By default, 
     * no node matches the from pattern.
     */
    public boolean matchesFrom(int node) {
	return false;
    }

    /**
     * Format a single value according to the format parameters.
     */
    protected String formatNumbers(int value) {
	return formatNumbers(new int[] { value });
    }

    /**
     * Format a sequence of values according to the format paramaters
     * set by calling setFormatting().
     */
    protected String formatNumbers(int[] values) {
	final int nValues = values.length;
	final int length = _format.length();

	boolean isEmpty = true;
	for (int i = 0; i < nValues; i++)
	    if (values[i] != Integer.MIN_VALUE)
		isEmpty = false;
	if (isEmpty) return("");

	// Format the output string using the values array and the fmt. tokens
	boolean isFirst = true;
	int t = 0, n = 0, s = 1;
	final StringBuffer buffer = new StringBuffer();

	// Append separation token before first digit/letter/numeral
	if (separFirst) buffer.append((String)separToks.elementAt(0));

	// Append next digit/letter/numeral and separation token
	while (n < nValues) {
	    final int value = values[n];
	    if (value != Integer.MIN_VALUE) {
		if (!isFirst) buffer.append((String) separToks.elementAt(s++));
		formatValue(value, (String)formatToks.elementAt(t++), buffer);
		if (t == nFormats) t--;
		if (s >= nSepars) s--;
		isFirst = false;
	    }
	    n++;
	}

	// Append separation token after last digit/letter/numeral
	if (separLast) buffer.append((String)separToks.lastElement());
	return buffer.toString();
    }

    /**
     * Format a single value based on the appropriate formatting token. 
     * This method is based on saxon (Michael Kay) and only implements
     * lang="en".
     */
    private void formatValue(int value, String format, StringBuffer buffer) {
        char c = format.charAt(0);

        if (Character.isDigit(c)) {
            char zero = (char)(c - Character.getNumericValue(c));

            StringBuffer temp = buffer;
            if (_groupSize > 0) {
                temp = new StringBuffer();
            }
            String s = "";
            int n = value;
            while (n > 0) {
                s = (char) ((int) zero + (n % 10)) + s;
                n = n / 10;
            }
                
            for (int i = 0; i < format.length() - s.length(); i++) {
                temp.append(zero);
            }
            temp.append(s);
            
            if (_groupSize > 0) {
                for (int i = 0; i < temp.length(); i++) {
                    if (i != 0 && ((temp.length() - i) % _groupSize) == 0) {
                        buffer.append(_groupSep);
                    }
                    buffer.append(temp.charAt(i));
                }
            }
        } 
	else if (c == 'i' && !_letterValue.equals("alphabetic")) {
            buffer.append(romanValue(value));
        } 
	else if (c == 'I' && !_letterValue.equals("alphabetic")) {
            buffer.append(romanValue(value).toUpperCase());
        } 
	else {
	    int min = (int) c;
	    int max = (int) c;

	    // Special case for Greek alphabet 
	    if (c >= 0x3b1 && c <= 0x3c9) {
		max = 0x3c9;	// omega
	    }
	    else {
		// General case: search for end of group
		while (Character.isLetterOrDigit((char) (max + 1))) {
		    max++;
		}
	    }
            buffer.append(alphaValue(value, min, max));
        }
    }

    private String alphaValue(int value, int min, int max) {
        if (value <= 0) {
	    return "" + value;
	}

        int range = max - min + 1;
        char last = (char)(((value-1) % range) + min);
        if (value > range) {
            return alphaValue((value-1) / range, min, max) + last;
        } 
	else {
            return "" + last;
        }
    }

    private String romanValue(int n) {
        if (n <= 0 || n > 4000) {
	    return "" + n;
	}
        return
	    Thousands[n / 1000] +
	    Hundreds[(n / 100) % 10] +
	    Tens[(n/10) % 10] +
	    Ones[n % 10];
    }
}


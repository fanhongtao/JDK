/*
 * @(#)StreamTokenizer.java	1.21 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.io;


/**
 * The <code>StreamTokenizer</code> class takes an input stream and 
 * parses it into "tokens", allowing the tokens to be 
 * read one at a time. The parsing process is controlled by a table 
 * and a number of flags that can be set to various states. The 
 * stream tokenizer can recognize identifiers, numbers, quoted 
 * strings, and various comment styles. 
 * <p>
 * Each byte read from the input stream is regarded as a character 
 * in the range <code>'&#92;u0000'</code> through <code>'&#92;u00FF'</code>. 
 * The character value is used to look up five possible attributes of 
 * the character: <i>white space</i>, <i>alphabetic</i>, 
 * <i>numeric</i>, <i>string quote</i>, and <i>comment character</i>. 
 * Each character can have zero or more of these attributes. 
 * <p>
 * In addition, an instance has four flags. These flags indicate: 
 * <ul>
 * <li>Whether line terminators are to be returned as tokens or treated 
 *     as white space that merely separates tokens. 
 * <li>Whether C-style comments are to be recognized and skipped. 
 * <li>Whether C++-style comments are to be recognized and skipped. 
 * <li>Whether the characters of identifiers are converted to lowercase. 
 * </ul>
 * <p>
 * A typical application first constructs an instance of this class, 
 * sets up the syntax tables, and then repeatedly loops calling the 
 * <code>nextToken</code> method in each iteration of the loop until 
 * it returns the value <code>TT_EOF</code>. 
 *
 * @author  James Gosling
 * @version 1.21, 07/01/98
 * @see     java.io.StreamTokenizer#nextToken()
 * @see     java.io.StreamTokenizer#TT_EOF
 * @since   JDK1.0
 */

public class StreamTokenizer {

    /* Only one of these will be non-null */
    private Reader reader = null;
    private InputStream input = null;

    private char buf[] = new char[20];
    private int peekc;
    private boolean pushedBack;
    private boolean forceLower;
    /** The line number of the last token read */
    private int LINENO = 1;

    private boolean eolIsSignificantP = false;
    private boolean slashSlashCommentsP = false;
    private boolean slashStarCommentsP = false;

    private byte ctype[] = new byte[256];
    private static final byte CT_WHITESPACE = 1;
    private static final byte CT_DIGIT = 2;
    private static final byte CT_ALPHA = 4;
    private static final byte CT_QUOTE = 8;
    private static final byte CT_COMMENT = 16;

    /** 
     * After a call to the <code>nextToken</code> method, this field 
     * contains the type of the token just read. For a single character 
     * token, its value is the single character, converted to an integer. 
     * For a quoted string token (see , its value is the quote character. 
     * Otherwise, its value is one of the following: 
     * <ul>
     * <li><code>TT_WORD</code> indicates that the token is a word.
     * <li><code>TT_NUMBER</code> indicates that the token is a number.
     * <li><code>TT_EOL</code> indicates that the end of line has been read. 
     *     The field can only have this value if the 
     *     <code>eolIsSignificant</code> method has been called with the 
     *     argument <code>true</code>. 
     * <li><code>TT_EOF</code> indicates that the end of the input stream 
     *     has been reached. 
     * </ul>
     *
     * @see     java.io.StreamTokenizer#eolIsSignificant(boolean)
     * @see     java.io.StreamTokenizer#nextToken()
     * @see     java.io.StreamTokenizer#quoteChar(int)
     * @see     java.io.StreamTokenizer#TT_EOF
     * @see     java.io.StreamTokenizer#TT_EOL
     * @see     java.io.StreamTokenizer#TT_NUMBER
     * @see     java.io.StreamTokenizer#TT_WORD
     */
    public int ttype = TT_NOTHING;

    /** 
     * A constant indicating that the end of the stream has been read. 
     */
    public static final int TT_EOF = -1;

    /** 
     * A constant indicating that the end of the line has been read. 
     */
    public static final int TT_EOL = '\n';

    /** 
     * A constant indicating that a number token has been read. 
     */
    public static final int TT_NUMBER = -2;

    /** 
     * A constant indicating that a word token has been read. 
     */
    public static final int TT_WORD = -3;

    /* A constant indicating that no token has been read, used for
     * initializing ttype.  FIXME This could be made public and
     * made available as the part of the API in a future release.
     */
    private static final int TT_NOTHING = -4;
    
    /**
     * If the current token is a word token, this field contains a 
     * string giving the characters of the word token. When the current 
     * token is a quoted string token, this field contains the body of 
     * the string. 
     * <p>
     * The current token is a word when the value of the 
     * <code>ttype</code> field is <code>TT_WORD</code>. The current token is
     * a quoted string token when the value of the <code>ttype</code> field is
     * a quote character.
     *
     * @see     java.io.StreamTokenizer#quoteChar(int)
     * @see     java.io.StreamTokenizer#TT_WORD
     * @see     java.io.StreamTokenizer#ttype
     * @since JDK1.0
     */
    public String sval;

    /**
     * If the current token is a number, this field contains the value 
     * of that number. The current token is a number when the value of 
     * the <code>ttype</code> field is <code>TT_NUMBER</code>. 
     *
     * @see     java.io.StreamTokenizer#TT_NUMBER
     * @see     java.io.StreamTokenizer#ttype
     */
    public double nval;

    /** Private constructor that initializes everything except the streams. */
    private StreamTokenizer() {
	wordChars('a', 'z');
	wordChars('A', 'Z');
	wordChars(128 + 32, 255);
	whitespaceChars(0, ' ');
	commentChar('/');
	quoteChar('"');
	quoteChar('\'');
	parseNumbers();
    }

    /**
     * Creates a stream tokenizer that parses the specified input 
     * stream. The stream tokenizer is initialized to the following 
     * default state: 
     * <ul>
     * <li>All byte values <code>'A'</code> through <code>'Z'</code>, 
     *     <code>'a'</code> through <code>'z'</code>, and 
     *     <code>'&#92;u00A0'</code> through <code>'&#92;u00FF'</code> are
     *     considered to be alphabetic. 
     * <li>All byte values <code>'&#92;u0000'</code> through 
     *     <code>'&#92;u0020'</code> are considered to be white space. 
     * <li><code>'/'</code> is a comment character. 
     * <li>Single quote <code>'&#92;''</code> and double quote <code>'"'</code> 
     *     are string quote characters. 
     * <li>Numbers are parsed. 
     * <li>Ends of lines are treated as white space, not as separate tokens. 
     * <li>C-style and C++-style comments are not recognized. 
     * </ul>
     *
     * @deprecated As of JDK version 1.1, the preferred way to tokenize an
     * input stream is to convert it into a character stream, for example:
     * <p>
     * <pre>
     *   Reader r = new BufferedReader(new InputStreamReader(is));
     *   StreamTokenizer st = new StreamTokenizer(r);
     * </pre>
     *
     * @param      is        an input stream.
     * @see        java.io.BufferedReader
     * @see        java.io.InputStreamReader
     * @see        java.io.StreamTokenizer#StreamTokenizer(java.io.Reader)
     */
    public StreamTokenizer(InputStream is) {
	this();
	input = is;
    }

    /**
     * Create a tokenizer that parses the given character stream.
     * @since   JDK1.1
     */
    public StreamTokenizer(Reader r) {
	this();
	reader = r;
    }

    /** 
     * Resets this tokenizer's syntax table so that all characters are 
     * "ordinary." See the <code>ordinaryChar</code> method 
     * for more information on a character being ordinary. 
     *
     * @see     java.io.StreamTokenizer#ordinaryChar(int)
     */
    public void resetSyntax() {
	for (int i = ctype.length; --i >= 0;)
	    ctype[i] = 0;
    }

    /** 
     * Specifies that all characters <i>c</i> in the range 
     * <code>low&nbsp;&lt;=&nbsp;<i>c</i>&nbsp;&lt;=&nbsp;high</code> 
     * are word constituents. A word token consists of a word constituent 
     * followed by zero or more word constituents or number constituents. 
     *
     * @param   low   the low end of the range.
     * @param   hi    the high end of the range.
     */
    public void wordChars(int low, int hi) {
	if (low < 0)
	    low = 0;
	if (hi >= ctype.length)
	    hi = ctype.length - 1;  
	while (low <= hi)
	    ctype[low++] |= CT_ALPHA;
    }

    /** 
     * Specifies that all characters <i>c</i> in the range 
     * <code>low&nbsp;&lt;=&nbsp;<i>c</i>&nbsp;&lt;=&nbsp;high</code> 
     * are white space characters. White space characters serve only to 
     * separate tokens in the input stream. 
     *
     * @param   low   the low end of the range.
     * @param   hi    the high end of the range.
     */
    public void whitespaceChars(int low, int hi) {
	if (low < 0)
	    low = 0;
	if (hi >= ctype.length)
	    hi = ctype.length - 1;
	while (low <= hi)
	    ctype[low++] = CT_WHITESPACE;
    }

    /** 
     * Specifies that all characters <i>c</i> in the range 
     * <code>low&nbsp;&lt;=&nbsp;<i>c</i>&nbsp;&lt;=&nbsp;high</code> 
     * are "ordinary" in this tokenizer. See the 
     * <code>ordinaryChar</code> method for more information on a 
     * character being ordinary. 
     *
     * @param   low   the low end of the range.
     * @param   hi    the high end of the range.
     * @see     java.io.StreamTokenizer#ordinaryChar(int)
     */
    public void ordinaryChars(int low, int hi) {
	if (low < 0)
	    low = 0;
	if (hi >= ctype.length)
	    hi = ctype.length - 1;
	while (low <= hi)
	    ctype[low++] = 0;
    }

    /** 
     * Specifies that the character argument is "ordinary" 
     * in this tokenizer. It removes any special significance the 
     * character has as a comment character, word component, string 
     * delimiter, white space, or number character. When such a character 
     * is encountered by the parser, the parser treates it as a
     * single-character token and sets <code>ttype</code> field to the
     * character value. 
     *
     * @param   ch   the character.
     * @see     java.io.StreamTokenizer#ttype
     */
    public void ordinaryChar(int ch) {
        if (ch >= 0 && ch < ctype.length)
  	    ctype[ch] = 0;
    }

    /** 
     * Specified that the character argument starts a single-line 
     * comment. All characters from the comment character to the end of 
     * the line are ignored by this stream tokenizer. 
     *
     * @param   ch   the character.
     */
    public void commentChar(int ch) {
        if (ch >= 0 && ch < ctype.length)
	    ctype[ch] = CT_COMMENT;
    }

    /** 
     * Specifies that matching pairs of this character delimit string 
     * constants in this tokenizer. 
     * <p>
     * When the <code>nextToken</code> method encounters a string 
     * constant, the <code>ttype</code> field is set to the string 
     * delimiter and the <code>sval</code> field is set to the body of 
     * the string. 
     * <p>
     * If a string quote character is encountered, then a string is 
     * recognized, consisting of all characters after (but not including) 
     * the string quote character, up to (but not including) the next 
     * occurrence of that same string quote character, or a line 
     * terminator, or end of file. The usual escape sequences such as 
     * <code>"&#92;n"</code> and <code>"&#92;t"</code> are recognized and 
     * converted to single characters as the string is parsed. 
     *
     * @param   ch   the character.
     * @see     java.io.StreamTokenizer#nextToken()
     * @see     java.io.StreamTokenizer#sval
     * @see     java.io.StreamTokenizer#ttype
     */
    public void quoteChar(int ch) {
        if (ch >= 0 && ch < ctype.length)
 	    ctype[ch] = CT_QUOTE;
    }

    /** 
     * Specifies that numbers should be parsed by this tokenizer. The 
     * syntax table of this tokenizer is modified so that each of the twelve
     * characters:
     * <ul><code>
     *      0 1 2 3 4 5 6 7 8 9 . -
     * </code></ul>
     * <p>
     * has the "numeric" attribute. 
     * <p>
     * When the parser encounters a word token that has the format of a 
     * double precision floating-point number, it treats the token as a 
     * number rather than a word, by setting the the <code>ttype</code> 
     * field to the value <code>TT_NUMBER</code> and putting the numeric 
     * value of the token into the <code>nval</code> field. 
     *
     * @see     java.io.StreamTokenizer#nval
     * @see     java.io.StreamTokenizer#TT_NUMBER
     * @see     java.io.StreamTokenizer#ttype
     */
    public void parseNumbers() {
	for (int i = '0'; i <= '9'; i++)
	    ctype[i] |= CT_DIGIT;
	ctype['.'] |= CT_DIGIT;
	ctype['-'] |= CT_DIGIT;
    }

    /**
     * Determines whether or not ends of line are treated as tokens.
     * If the flag argument is true, this tokenizer treats end of lines 
     * as tokens; the <code>nextToken</code> method returns 
     * <code>TT_EOL</code> and also sets the <code>ttype</code> field to 
     * this value when an end of line is read. 
     * <p>
     * A line is a sequence of characters ending with either a 
     * carriage-return character (<code>'&#92;r'</code>) or a newline 
     * character (<code>'&#92;n'</code>). In addition, a carriage-return 
     * character followed immediately by a newline character is treated 
     * as a single end-of-line token. 
     * <p>
     * If the <code>flag</code> is false, end-of-line characters are 
     * treated as white space and serve only to separate tokens. 
     *
     * @param   flag   <code>true</code> indicates that end-of-line characters
     *                 are separate tokens; <code>false</code> indicates that
     *                 end-of-line characters are white space.
     * @see     java.io.StreamTokenizer#nextToken()
     * @see     java.io.StreamTokenizer#ttype
     * @see     java.io.StreamTokenizer#TT_EOL
     */
    public void eolIsSignificant(boolean flag) {
	eolIsSignificantP = flag;
    }

    /** 
     * Determines whether or not the tokenizer recognizes C-style comments.
     * If the flag argument is <code>true</code>, this stream tokenizer 
     * recognizes C-style comments. All text between successive 
     * occurrences of <code>/*</code> and <code>*&#47;</code> are discarded. 
     * <p>
     * If the flag argument is <code>false</code>, then C-style comments 
     * are not treated specially. 
     *
     * @param   flag   <code>true</code> indicates to recognize and ignore
     *                 C-style comments.
     */
    public void slashStarComments(boolean flag) {
	slashStarCommentsP = flag;
    }

    /** 
     * Determines whether or not the tokenizer recognizes C++-style comments.
     * If the flag argument is <code>true</code>, this stream tokenizer 
     * recognizes C++-style comments. Any occurrence of two consecutive 
     * slash characters (<code>'/'</code>) is treated as the beginning of 
     * a comment that extends to the end of the line. 
     * <p>
     * If the flag argument is <code>false</code>, then C++-style 
     * comments are not treated specially. 
     *
     * @param   flag   <code>true</code> indicates to recognize and ignore
     *                 C++-style comments.
     */
    public void slashSlashComments(boolean flag) {
	slashSlashCommentsP = flag;
    }

    /**
     * Determines whether or not word token are automatically lowercased.
     * If the flag argument is <code>true</code>, then the value in the 
     * <code>sval</code> field is lowercased whenever a word token is 
     * returned (the <code>ttype</code> field has the 
     * value <code>TT_WORD</code> by the <code>nextToken</code> method 
     * of this tokenizer. 
     * <p>
     * If the flag argument is <code>false</code>, then the 
     * <code>sval</code> field is not modified. 
     *
     * @param   fl   <code>true</code> indicates that all word tokens should
     *               be lowercased.
     * @see     java.io.StreamTokenizer#nextToken()
     * @see     java.io.StreamTokenizer#ttype
     * @see     java.io.StreamTokenizer#TT_WORD
     */
    public void lowerCaseMode(boolean fl) {
	forceLower = fl;
    }

    /** Read the next character */
    private int read() throws IOException {
	if (reader != null)
	    return reader.read();
	else if (input != null)
	    return input.read();
	else
	    throw new IllegalStateException();
    }

    /** 
     * Parses the next token from the input stream of this tokenizer. 
     * The type of the next token is returned in the <code>ttype</code> 
     * field. Additional information about the token may be in the 
     * <code>nval</code> field or the <code>sval</code> field of this 
     * tokenizer. 
     * <p>
     * Typical clients of this
     * class first set up the syntax tables and then sit in a loop
     * calling nextToken to parse successive tokens until TT_EOF
     * is returned. 
     *
     * @return     the value of the <code>ttype</code> field.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.StreamTokenizer#nval
     * @see        java.io.StreamTokenizer#sval
     * @see        java.io.StreamTokenizer#ttype
     */
    public int nextToken() throws IOException {
	if (pushedBack) {
	    pushedBack = false;
	    return ttype;
	}
	byte ct[] = ctype;
	int c; 
	sval = null;

	if (ttype == TT_NOTHING) {
	    c = read();
	    if (c >= 0)    // ttype is surely overwritten below to its correct value.
	        ttype = c; // for now we just make sure it isn't TT_NOTHING
	} else {
	    c = peekc;
	}
	
	if (c < 0)
	    return ttype = TT_EOF;
	int ctype = c < 256 ? ct[c] : CT_ALPHA;
	while ((ctype & CT_WHITESPACE) != 0) {
	    if (c == '\r') {
		LINENO++;
		c = read();
		if (c == '\n')
		    c = read();
		if (eolIsSignificantP) {
		    peekc = c;
		    return ttype = TT_EOL;
		}
	    } else {
		if (c == '\n') {
		    LINENO++;
		    if (eolIsSignificantP) {
			peekc = read();
			return ttype = TT_EOL;
		    }
		}
		c = read();
	    }
	    if (c < 0)
		return ttype = TT_EOF;
	    ctype = c < 256 ? ct[c] : CT_ALPHA;
	}
	if ((ctype & CT_DIGIT) != 0) {
	    boolean neg = false;
	    if (c == '-') {
		c = read();
		if (c != '.' && (c < '0' || c > '9')) {
		    peekc = c;
		    return ttype = '-';
		}
		neg = true;
	    }
	    double v = 0;
	    int decexp = 0;
	    int seendot = 0;
	    while (true) {
		if (c == '.' && seendot == 0)
		    seendot = 1;
		else if ('0' <= c && c <= '9') {
		    v = v * 10 + (c - '0');
		    decexp += seendot;
		} else
		    break;
		c = read();
	    }
	    peekc = c;
	    if (decexp != 0) {
		double denom = 10;
		decexp--;
		while (decexp > 0) {
		    denom *= 10;
		    decexp--;
		}
		/* do one division of a likely-to-be-more-accurate number */
		v = v / denom;
	    }
	    nval = neg ? -v : v;
	    return ttype = TT_NUMBER;
	}
	if ((ctype & CT_ALPHA) != 0) {
	    int i = 0;
	    do {
		if (i >= buf.length) {
		    char nb[] = new char[buf.length * 2];
		    System.arraycopy(buf, 0, nb, 0, buf.length);
		    buf = nb;
		}
		buf[i++] = (char) c;
		c = read();
		ctype = c < 0 ? CT_WHITESPACE : c < 256 ? ct[c] : CT_ALPHA;
	    } while ((ctype & (CT_ALPHA | CT_DIGIT)) != 0);
	    peekc = c;
	    sval = String.copyValueOf(buf, 0, i);
	    if (forceLower)
		sval = sval.toLowerCase();
	    return ttype = TT_WORD;
	}
	if ((ctype & CT_COMMENT) != 0) {
	    while ((c = read()) != '\n' && c != '\r' && c >= 0);
	    peekc = c;
	    return nextToken();
	}
	if ((ctype & CT_QUOTE) != 0) {
	    ttype = c;
	    int i = 0;
	    // invariants (because \Octal needs a lookahead):
	    //      (i)  c contains char value 
	    //      (ii) peekc contains the lookahead
	    peekc = read(); 
	    while (peekc >= 0 && peekc != ttype && peekc != '\n' && peekc != '\r') {
	        if (peekc == '\\') {
   		    c = read();
		    int first = c;   // to allow \377, but not \477
		    if (c >= '0' && c <= '7') {
			c = c - '0';
			int c2 = read();
			if ('0' <= c2 && c2 <= '7') {
			    c = (c << 3) + (c2 - '0');
			    c2 = read();
			    if ('0' <= c2 && c2 <= '7' && first <= '3') {
				c = (c << 3) + (c2 - '0');
				peekc = read();
			    } else
				peekc = c2;
			} else
			  peekc = c2;
		    } else {
  		        switch (c) {
			case 'a':
			    c = 0x7;
			    break;
			case 'b':
			    c = '\b';
			    break;
			case 'f':
			    c = 0xC;
			    break;
			case 'n':
			    c = '\n';
			    break;
		        case 'r':
			    c = '\r';
			    break;
			case 't':
			    c = '\t';
			    break;
			case 'v':
			    c = 0xB;
			    break;
			}
			peekc = read();
		    }
		} else {
		    c = peekc;
		    peekc = read();
		}
		
		if (i >= buf.length) {
		    char nb[] = new char[buf.length * 2];
		    System.arraycopy(buf, 0, nb, 0, buf.length);
		    buf = nb;
		}
		buf[i++] = (char) c;
	    }
	    if (peekc == ttype)  // keep \n or \r intact in peekc
	        peekc = read();
	    sval = String.copyValueOf(buf, 0, i);
	    return ttype;
	}
	if (c == '/' && (slashSlashCommentsP || slashStarCommentsP)) {
	    c = read();
	    if (c == '*' && slashStarCommentsP) {
		int prevc = 0;
		while ((c = read()) != '/' || prevc != '*') {
		    if (c == '\r') {
			LINENO++;
			c = read();
			if (c == '\n') {
			    c = read();
			}
		    } else {
		        if (c == '\n') {
			    LINENO++;
			    c = read();
			}
		    }
		    if (c < 0)
		        return ttype = TT_EOF;
		    prevc = c;
		}
		peekc = read();
		return nextToken();
	    } else if (c == '/' && slashSlashCommentsP) {
	        while ((c = read()) != '\n' && c != '\r' && c >= 0);
	        peekc = c;
		return nextToken();
	    } else {
		peekc = c;
		return ttype = '/';
	    }
	}
	peekc = read();
	return ttype = c;
    }

    /**
     * Causes the next call to the <code>nextToken</code> method of this 
     * tokenizer to return the current value in the <code>ttype</code> 
     * field, and not to modify the value in the <code>nval</code> or 
     * <code>sval</code> field. 
     *
     * @see     java.io.StreamTokenizer#nextToken()
     * @see     java.io.StreamTokenizer#nval
     * @see     java.io.StreamTokenizer#sval
     * @see     java.io.StreamTokenizer#ttype
     */
    public void pushBack() {
        if (ttype != TT_NOTHING)   // no-op if nextToken() not called
	    pushedBack = true;
    }

    /**
     * Return the current line number.
     *
     * @return  the current line number of this stream tokenizer.
     */
    public int lineno() {
	return LINENO;
    }

    /**
     * Returns the string representation of the current stream token. 
     *
     * @return  a string representation of the token specified by the
     *          <code>ttype</code>, <code>nval</code>, and <code>sval</code>
     *          fields.
     * @see     java.io.StreamTokenizer#nval
     * @see     java.io.StreamTokenizer#sval
     * @see     java.io.StreamTokenizer#ttype
     */
    public String toString() {
	String ret;
	switch (ttype) {
	  case TT_EOF:
	    ret = "EOF";
	    break;
	  case TT_EOL:
	    ret = "EOL";
	    break;
	  case TT_WORD:
	    ret = sval;
	    break;
	  case TT_NUMBER:
	    ret = "n=" + nval;
	    break;
   	  case TT_NOTHING:  
	    ret = "NOTHING";
	    break;
	  default:{
		char s[] = new char[3];
		s[0] = s[2] = '\'';
		s[1] = (char) ttype;
		ret = new String(s);
		break;
	    }
	}
	return "Token[" + ret + "], line " + LINENO;
    }

}

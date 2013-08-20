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
 * $Id: xpath.lex,v 1.10 2004/02/24 19:15:03 minchau Exp $
 */
/*
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 * @author Morten Jorgensen
 *
 */
package com.sun.org.apache.xalan.internal.xsltc.compiler;
import com.sun.java_cup.internal.runtime.Symbol;


class XPathLexer implements com.sun.java_cup.internal.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 65536;
	private final int YY_EOF = 65537;
	public final int YYEOF = -1;
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private boolean yy_at_bol;
	private int yy_lexical_state;

	XPathLexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	XPathLexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private XPathLexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int YYINITIAL = 0;
	private final int yy_state_dtrans[] = {
		0
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	static private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NOT_ACCEPT,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NOT_ACCEPT,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NOT_ACCEPT,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NOT_ACCEPT,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NOT_ACCEPT,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NOT_ACCEPT,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NOT_ACCEPT,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NOT_ACCEPT,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NOT_ACCEPT,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NOT_ACCEPT,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NOT_ACCEPT,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NOT_ACCEPT,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NOT_ACCEPT,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NOT_ACCEPT,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NOT_ACCEPT,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NOT_ACCEPT,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NOT_ACCEPT,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NOT_ACCEPT,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NOT_ACCEPT,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NOT_ACCEPT,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NOT_ACCEPT,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NOT_ACCEPT,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NOT_ACCEPT,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NOT_ACCEPT,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NOT_ACCEPT,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NOT_ACCEPT,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NOT_ACCEPT,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NOT_ACCEPT,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NOT_ACCEPT,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR,
		/* 139 */ YY_NO_ANCHOR,
		/* 140 */ YY_NO_ANCHOR,
		/* 141 */ YY_NO_ANCHOR,
		/* 142 */ YY_NO_ANCHOR,
		/* 143 */ YY_NO_ANCHOR,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NO_ANCHOR,
		/* 147 */ YY_NO_ANCHOR,
		/* 148 */ YY_NO_ANCHOR,
		/* 149 */ YY_NO_ANCHOR,
		/* 150 */ YY_NO_ANCHOR,
		/* 151 */ YY_NO_ANCHOR,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NO_ANCHOR,
		/* 154 */ YY_NO_ANCHOR,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR,
		/* 159 */ YY_NO_ANCHOR,
		/* 160 */ YY_NO_ANCHOR,
		/* 161 */ YY_NO_ANCHOR,
		/* 162 */ YY_NO_ANCHOR,
		/* 163 */ YY_NO_ANCHOR,
		/* 164 */ YY_NO_ANCHOR,
		/* 165 */ YY_NO_ANCHOR,
		/* 166 */ YY_NO_ANCHOR,
		/* 167 */ YY_NO_ANCHOR,
		/* 168 */ YY_NO_ANCHOR,
		/* 169 */ YY_NO_ANCHOR,
		/* 170 */ YY_NO_ANCHOR,
		/* 171 */ YY_NO_ANCHOR,
		/* 172 */ YY_NO_ANCHOR,
		/* 173 */ YY_NO_ANCHOR,
		/* 174 */ YY_NO_ANCHOR,
		/* 175 */ YY_NO_ANCHOR,
		/* 176 */ YY_NO_ANCHOR,
		/* 177 */ YY_NO_ANCHOR,
		/* 178 */ YY_NO_ANCHOR,
		/* 179 */ YY_NO_ANCHOR,
		/* 180 */ YY_NO_ANCHOR,
		/* 181 */ YY_NO_ANCHOR,
		/* 182 */ YY_NO_ANCHOR,
		/* 183 */ YY_NOT_ACCEPT,
		/* 184 */ YY_NOT_ACCEPT,
		/* 185 */ YY_NO_ANCHOR,
		/* 186 */ YY_NOT_ACCEPT,
		/* 187 */ YY_NO_ANCHOR,
		/* 188 */ YY_NOT_ACCEPT,
		/* 189 */ YY_NO_ANCHOR,
		/* 190 */ YY_NO_ANCHOR,
		/* 191 */ YY_NO_ANCHOR,
		/* 192 */ YY_NO_ANCHOR,
		/* 193 */ YY_NO_ANCHOR,
		/* 194 */ YY_NO_ANCHOR,
		/* 195 */ YY_NO_ANCHOR,
		/* 196 */ YY_NO_ANCHOR,
		/* 197 */ YY_NO_ANCHOR,
		/* 198 */ YY_NO_ANCHOR,
		/* 199 */ YY_NO_ANCHOR,
		/* 200 */ YY_NO_ANCHOR,
		/* 201 */ YY_NO_ANCHOR,
		/* 202 */ YY_NO_ANCHOR,
		/* 203 */ YY_NO_ANCHOR,
		/* 204 */ YY_NO_ANCHOR,
		/* 205 */ YY_NO_ANCHOR,
		/* 206 */ YY_NO_ANCHOR,
		/* 207 */ YY_NO_ANCHOR,
		/* 208 */ YY_NO_ANCHOR,
		/* 209 */ YY_NO_ANCHOR,
		/* 210 */ YY_NO_ANCHOR,
		/* 211 */ YY_NO_ANCHOR,
		/* 212 */ YY_NO_ANCHOR,
		/* 213 */ YY_NO_ANCHOR,
		/* 214 */ YY_NO_ANCHOR,
		/* 215 */ YY_NO_ANCHOR,
		/* 216 */ YY_NO_ANCHOR,
		/* 217 */ YY_NO_ANCHOR,
		/* 218 */ YY_NO_ANCHOR,
		/* 219 */ YY_NO_ANCHOR,
		/* 220 */ YY_NO_ANCHOR,
		/* 221 */ YY_NO_ANCHOR,
		/* 222 */ YY_NO_ANCHOR,
		/* 223 */ YY_NO_ANCHOR,
		/* 224 */ YY_NO_ANCHOR,
		/* 225 */ YY_NO_ANCHOR,
		/* 226 */ YY_NO_ANCHOR,
		/* 227 */ YY_NO_ANCHOR,
		/* 228 */ YY_NO_ANCHOR,
		/* 229 */ YY_NO_ANCHOR,
		/* 230 */ YY_NO_ANCHOR,
		/* 231 */ YY_NO_ANCHOR
	};
	static private int yy_cmap[] = unpackFromString(1,65538,
"54:9,27:2,54,27:2,54:18,27,17,53,54,15,54:2,55,25,26,1,3,11,4,13,2,56:10,10" +
",54,18,16,19,54,12,44,57:3,46,57:3,51,57:4,48,52,43,57,47,50,45,57:3,49,57:" +
"2,41,54,42,54,58,54,35,38,29,5,21,39,33,36,6,57,20,37,8,28,9,30,57,31,32,23" +
",34,7,40,24,22,57,54,14,54:58,59,54:8,57:23,54,57:31,54,57:58,54:2,57:11,54" +
":2,57:8,54,57:53,54,57:68,54:9,57:36,54:3,57:2,54:4,57:30,54:56,57:89,54:18" +
",57:7,54:14,59:2,54:46,59:70,54:26,59:2,54:36,57,59,57:3,54,57,54,57:20,54," +
"57:44,54,57:7,54:3,57,54,57,54,57,54,57,54,57:18,54:13,57:12,54,57:66,54,57" +
":12,54,57:36,54,59:4,54:9,57:53,54:2,57:2,54:2,57:2,54:3,57:28,54:2,57:8,54" +
":2,57:2,54:55,57:38,54:2,57,54:7,57:38,54:10,59:17,54,59:23,54,59:3,54,59,5" +
"4,59:2,54,59,54:11,57:27,54:5,57:3,54:46,57:26,54:5,59,57:10,59:8,54:13,56:" +
"10,54:6,59,57:71,54:2,57:5,54,57:15,54,57:4,54,57,59:15,57:2,59:2,54,59:4,5" +
"4:2,56:10,54:519,59:3,54,57:53,54:2,59,57,59:16,54:3,59:4,54:3,57:10,59:2,5" +
"4:2,56:10,54:17,59:3,54,57:8,54:2,57:2,54:2,57:22,54,57:7,54,57,54:3,57:4,5" +
"4:2,59,54,59:7,54:2,59:2,54:2,59:3,54:9,59,54:4,57:2,54,57:3,59:2,54:2,56:1" +
"0,57:2,54:16,59,54:2,57:6,54:4,57:2,54:2,57:22,54,57:7,54,57:2,54,57:2,54,5" +
"7:2,54:2,59,54,59:5,54:4,59:2,54:2,59:3,54:11,57:4,54,57,54:7,56:10,59:2,57" +
":3,54:12,59:3,54,57:7,54,57,54,57:3,54,57:22,54,57:7,54,57:2,54,57:5,54:2,5" +
"9,57,59:8,54,59:3,54,59:3,54:18,57,54:5,56:10,54:17,59:3,54,57:8,54:2,57:2," +
"54:2,57:22,54,57:7,54,57:2,54:2,57:4,54:2,59,57,59:6,54:3,59:2,54:2,59:3,54" +
":8,59:2,54:4,57:2,54,57:3,54:4,56:10,54:18,59:2,54,57:6,54:3,57:3,54,57:4,5" +
"4:3,57:2,54,57,54,57:2,54:3,57:2,54:3,57:3,54:3,57:8,54,57:3,54:4,59:5,54:3" +
",59:3,54,59:4,54:9,59,54:15,56:9,54:17,59:3,54,57:8,54,57:3,54,57:23,54,57:" +
"10,54,57:5,54:4,59:7,54,59:3,54,59:4,54:7,59:2,54:9,57:2,54:4,56:10,54:18,5" +
"9:2,54,57:8,54,57:3,54,57:23,54,57:10,54,57:5,54:4,59:7,54,59:3,54,59:4,54:" +
"7,59:2,54:7,57,54,57:2,54:4,56:10,54:18,59:2,54,57:8,54,57:3,54,57:23,54,57" +
":16,54:4,59:6,54:2,59:3,54,59:4,54:9,59,54:8,57:2,54:4,56:10,54:145,57:46,5" +
"4,57,59,57:2,59:7,54:5,57:6,59:9,54,56:10,54:39,57:2,54,57,54:2,57:2,54,57," +
"54:2,57,54:6,57:4,54,57:7,54,57:3,54,57,54,57,54:2,57:2,54,57:2,54,57,59,57" +
":2,59:6,54,59:2,57,54:2,57:5,54,59,54,59:6,54:2,56:10,54:62,59:2,54:6,56:10" +
",54:11,59,54,59,54,59,54:4,59:2,57:8,54,57:33,54:7,59:20,54,59:6,54:4,59:6," +
"54,59,54,59:21,54:3,59:7,54,59,54:230,57:38,54:10,57:39,54:9,57,54,57:2,54," +
"57:3,54,57,54,57:2,54,57:5,54:41,57,54,57,54,57,54:11,57,54,57,54,57,54:3,5" +
"7:2,54:3,57,54:5,57:3,54,57,54,57,54,57,54,57,54:3,57:2,54:3,57:2,54,57,54:" +
"40,57,54:9,57,54:2,57,54:2,57:2,54:7,57:2,54,57,54,57:7,54:40,57,54:4,57,54" +
":8,57,54:3078,57:156,54:4,57:90,54:6,57:22,54:2,57:6,54:2,57:38,54:2,57:6,5" +
"4:2,57:8,54,57,54,57,54,57,54,57:31,54:2,57:53,54,57:7,54,57,54:3,57:3,54,5" +
"7:7,54:3,57:4,54:2,57:6,54:4,57:13,54:5,57:3,54,57:7,54:211,59:13,54:4,59,5" +
"4:68,57,54:3,57:2,54:2,57,54:81,57:3,54:3714,59,54,58,54:25,58:9,59:6,54,59" +
":5,54:11,57:84,54:4,59:2,54:2,59:2,54:2,57:90,54,59:3,54:6,57:40,54:7379,58" +
":20902,54:3162,57:11172,54:10332,0:2")[0];

	static private int yy_rmap[] = unpackFromString(1,232,
"0,1:2,2,1:2,3,4,1,5,6,1:3,7,8,1:5,9,1,10:2,1:3,11,1:5,12,10,1,10:5,1:2,10,1" +
":2,13,1,10,1,14,10,15,16,1:2,10:4,17,1:2,18,19,20,21,22,23,24,25,1,23,10,26" +
":2,27,5,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,5" +
"0,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,7" +
"5,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,1" +
"00,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118," +
"119,120,121,122,123,124,125,126,127,128,129,130,10,131,132,133,134,135,136," +
"137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155" +
",156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,17" +
"4,175,176,177,178,179")[0];

	static private int yy_nxt[][] = unpackFromString(180,60,
"1,2,3,4,5,6,65,182,202,68,7,8,9,10,11,12,13,66,14,15,209,182:2,213,182,16,1" +
"7,18,216,218,219,182,220,182:2,221,182:3,222,182,19,20,182:10,69,72,75,21,1" +
"82:2,72,-1:62,22,-1:61,182:2,71,182:3,64,-1:2,74,-1:6,182,77,182:3,-1:3,182" +
":13,-1:2,182:10,-1:3,74,182,74:2,-1:10,25,-1:50,26,-1:71,27,-1:42,28,-1:19," +
"30,-1:26,67,-1:2,70,-1:29,31,-1:56,34,-1:42,21,-1:7,182:6,64,-1:2,74,-1:6,1" +
"82:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:56,28,-1:59,34,-1:7,153,18" +
"2:5,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,207" +
",182:5,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4," +
"231,182:5,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1" +
":4,156,182:5,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2" +
",-1:4,182:6,64,-1:2,74,-1:6,182:5,120,-1,122,181,182:12,-1:2,182:10,-1:3,74" +
",182,74:2,-1,36,-1:3,101:5,-1:2,78,-1:7,101:5,-1:3,101:13,-1:2,101:10,-1:4," +
"101:2,-1:5,182,23,182:4,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3," +
"74,182,74:2,-1:16,29,-1:87,80,-1:19,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:3," +
"24,182:9,-1:2,182:10,-1:3,74,182,74:2,-1,73:52,32,73:6,-1:49,82,-1:14,182:3" +
",35,182:2,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1" +
",76:54,33,76:4,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:4,103,182:8,-1:2,1" +
"82:10,-1:3,74,182,74:2,-1:4,182,37,182:4,64,-1:2,74,-1:6,182:5,-1:3,182:13," +
"-1:2,182:10,-1:3,74,182,74:2,-1:45,183,-1:18,182:6,64,-1:2,74,-1:6,182:2,38" +
",182:2,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:43,84,-1:20,182:6,64,-1:" +
"2,74,-1:6,182:4,189,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:47,184,-1:1" +
"6,182,105,182:4,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,7" +
"4:2,-1:46,94,-1:17,182:4,191,182,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182" +
":10,-1:3,74,182,74:2,-1:26,42,-1:37,182:2,203,182:3,64,-1:2,74,-1:6,182:5,-" +
"1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:25,98,-1,90,-1:36,182:5,190,64,-" +
"1:2,74,-1:6,182,226,182:3,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:26,43" +
",-1:37,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:3,204,182:9,-1:2,182:10,-1:3,74" +
",182,74:2,-1:47,102,-1:16,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:9,109,182:3," +
"-1:2,182:10,-1:3,74,182,74:2,-1:50,188,-1:13,182:6,64,-1:2,74,-1:6,182:3,11" +
"1,182,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:26,45,-1:37,182,39,182:4," +
"64,-1:2,74,-1:6,182:5,-1:3,182,210,182:11,-1:2,182:10,-1:3,74,182,74:2,-1:2" +
"6,46,-1:37,101:6,-1:3,101,-1:6,101:5,-1:3,101:13,-1:2,101:10,-1:3,101:4,-1:" +
"48,104,-1:15,182:6,64,-1:2,74,-1:6,182:5,-1:3,182,214,182:11,-1:2,182:10,-1" +
":3,74,182,74:2,-1:19,48,-1:44,182:6,64,-1:2,74,-1:6,182,117,182:3,-1:3,182:" +
"13,-1:2,182:10,-1:3,74,182,74:2,-1:51,112,-1:12,182:4,121,182,64,-1:2,74,-1" +
":6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:26,50,-1:37,182:6,64,-" +
"1:2,74,-1:6,182:5,-1:3,182:11,40,182,-1:2,182:10,-1:3,74,182,74:2,-1:25,114" +
",-1,110,-1:36,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:3,126,182:9,-1:2,182:10," +
"-1:3,74,182,74:2,-1:52,116,-1:11,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:9,127" +
",182:3,-1:2,182:10,-1:3,74,182,74:2,-1:26,55,-1:37,182:6,64,-1:2,74,-1:6,18" +
"2:3,128,182,88,-1,90,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:48,118,-1:15,18" +
"2:6,64,-1:2,74,-1:6,182,129,182:3,92,-1,186,182:13,-1:2,182:10,-1:3,74,182," +
"74:2,-1:19,56,-1:44,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:4,130,182:8,-1:2,1" +
"82:10,-1:3,74,182,74:2,-1:26,62,-1:37,182:6,64,-1:2,74,-1:6,182,206,182:3,-" +
"1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:25,124,-1,122,-1:36,182,41,182:4" +
",64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:26,63,-1" +
":37,182:6,64,-1:2,74,-1:6,182:5,-1:3,133,182:12,-1:2,182:10,-1:3,74,182,74:" +
"2,-1:4,182:2,134,182:3,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,7" +
"4,182,74:2,-1:4,182:5,136,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:" +
"3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:3,128,182,-1:2,90,182:13,-1:2," +
"182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182,129,182:3,-1:2,186,1" +
"82:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,18" +
"2:2,137,182:10,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:" +
"5,-1:3,182:4,195,182:8,-1:2,182:10,-1:3,74,182,74:2,-1:4,182,138,182:4,64,-" +
"1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1" +
":2,74,-1:6,182:3,44,182,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6" +
",64,-1:2,74,-1:6,182:5,-1:3,182:10,139,182:2,-1:2,182:10,-1:3,74,182,74:2,-" +
"1:4,182:6,64,-1:2,74,-1:6,182:3,140,182,-1:3,182:13,-1:2,182:10,-1:3,74,182" +
",74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:12,223,-1:2,182:10,-1:3,74," +
"182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:7,141,182:5,-1:2,182:10," +
"-1:3,74,182,74:2,-1:4,182:2,143,182:3,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:" +
"2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:6,144,1" +
"82:6,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:5,145,64,-1:2,74,-1:6,182:5,-1:3" +
",182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3," +
"182,146,182:11,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:" +
"3,147,182,108,-1,110,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2" +
",74,-1:6,182:5,-1:3,148,182:12,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-" +
"1:2,74,-1:6,182:3,149,182,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182" +
":6,64,-1:2,74,-1:6,182:5,-1:3,182:3,47,182:9,-1:2,182:10,-1:3,74,182,74:2,-" +
"1:4,182:6,64,-1:2,74,-1:6,182,49,182:3,-1:3,182:13,-1:2,182:10,-1:3,74,182," +
"74:2,-1:4,182:6,64,-1:2,74,-1:6,182:3,147,182,-1:2,110,182:13,-1:2,182:10,-" +
"1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:5,51,182:7,-1:2,1" +
"82:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182,52,182:3,-1:3,182:13," +
"-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:5,53" +
",182:7,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:3,54,182" +
",-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5," +
"-1:3,182:5,154,182:7,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:5,155,64,-1:2,74" +
",-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,157,182:5,64,-1:2" +
",74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2," +
"74,-1:6,182:5,-1:3,182:3,158,182:9,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:5," +
"159,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182" +
":2,160,182:3,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2" +
",-1:4,211,182:5,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,7" +
"4:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:3,224,182:9,-1:2,182:10,-1:3," +
"74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,215,182:12,-1:2,182:10,-1" +
":3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:10,162,182:2,-1:2," +
"182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:9,165,182" +
":3,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182,166,182:3,-1" +
":3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:3,168" +
",182,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:2,169,182:3,64,-1:2," +
"74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,7" +
"4,-1:6,182:5,-1:3,182:9,170,182:3,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,6" +
"4,-1:2,74,-1:6,182,171,182:3,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4," +
"182:6,64,-1:2,74,-1:6,182:5,-1:3,182:3,172,182:9,-1:2,182:10,-1:3,74,182,74" +
":2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,173,182:12,-1:2,182:10,-1:3,74,182" +
",74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:11,57,182,-1:2,182:10,-1:3," +
"74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:9,175,182:3,-1:2,182:" +
"10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:6,176,182:6,-" +
"1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:5,58," +
"182:7,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,18" +
"2:5,59,182:7,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5," +
"-1:3,182:11,60,182,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6," +
"182:5,-1:3,182,177,182:11,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,7" +
"4,-1:6,182:3,178,182,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:2,17" +
"9,182:3,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4" +
",182:5,180,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-" +
"1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,61,182:12,-1:2,182:10,-1:3,74,182,74:2" +
",-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:2,122,181,182:12,-1:2,182:10,-1:3,74,1" +
"82,74:2,-1:45,86,-1:60,96,-1:17,182:4,107,182,64,-1:2,74,-1:6,182:5,-1:3,18" +
"2:13,-1:2,182:10,-1:3,74,182,74:2,-1:25,100,-1,186,-1:36,182:6,64,-1:2,74,-" +
"1:6,182:5,-1:3,182:9,113,182:3,-1:2,182:10,-1:3,74,182,74:2,-1:50,106,-1:13" +
",182:6,64,-1:2,74,-1:6,182:3,115,182,-1:3,182:13,-1:2,182:10,-1:3,74,182,74" +
":2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182,193,182:11,-1:2,182:10,-1:3,74" +
",182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182,119,182:3,-1:3,182:13,-1:2,182:10," +
"-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:4,135,182:8,-1:2" +
",182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182,131,182:3,-1:3,182:" +
"13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,196,1" +
"82:12,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,18" +
"2:4,227,182:8,-1:2,182:10,-1:3,74,182,74:2,-1:4,182,198,182:4,64,-1:2,74,-1" +
":6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:" +
"6,182:3,142,182,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2" +
",74,-1:6,182:5,-1:3,182:7,208,182:5,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6" +
",64,-1:2,74,-1:6,182:5,-1:3,150,182:12,-1:2,182:10,-1:3,74,182,74:2,-1:4,18" +
"2:2,161,182:3,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:" +
"2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,174,182:12,-1:2,182:10,-1:3,74,182," +
"74:2,-1:4,182:5,79,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,18" +
"2,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:9,123,182:3,-1:2,182:10,-1" +
":3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182,125,182:3,-1:3,182:13,-1:2,18" +
"2:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182,132,182:3,-1:3,182:13," +
"-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,197,182:" +
"12,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:4" +
",200,182:8,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1" +
":3,151,182:12,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182,8" +
"1,182:3,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6" +
",182,192,182:3,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2," +
"74,-1:6,182:5,-1:3,182:4,163,182:8,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6," +
"64,-1:2,74,-1:6,182:5,-1:3,152,182:12,-1:2,182:10,-1:3,74,182,74:2,-1:4,182" +
":6,64,-1:2,74,-1:6,182,83,182:3,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1" +
":4,182:6,64,-1:2,74,-1:6,182,194,182:3,-1:3,182:13,-1:2,182:10,-1:3,74,182," +
"74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:4,164,182:8,-1:2,182:10,-1:3" +
",74,182,74:2,-1:4,182:5,85,64,-1:2,74,-1:6,182:5,-1:3,182:7,87,182:5,-1:2,1" +
"82:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182:4,167,182:" +
"8,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:5,185,64,-1:2,74,-1:6,182:5,-1:3,18" +
"2:8,89,182:4,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5," +
"-1:3,182:3,91,182:3,93,182:5,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:" +
"2,74,-1:6,182,95,182:3,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6," +
"64,-1:2,74,-1:6,182:3,97,182,-1:3,99,182:12,-1:2,182:10,-1:3,74,182,74:2,-1" +
":4,182:5,187,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2" +
",-1:4,182:2,199,182:3,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74" +
",182,74:2,-1:4,217,182:5,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3" +
",74,182,74:2,-1:4,182:2,201,182:3,64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,18" +
"2:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3,182,205,182:11," +
"-1:2,182:10,-1:3,74,182,74:2,-1:4,182:2,212,182:3,64,-1:2,74,-1:6,182:5,-1:" +
"3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,182:5,-1:3" +
",182:9,225,182:3,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6,64,-1:2,74,-1:6,18" +
"2:5,-1:3,182:10,228,182:2,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:2,229,182:3" +
",64,-1:2,74,-1:6,182:5,-1:3,182:13,-1:2,182:10,-1:3,74,182,74:2,-1:4,182:6," +
"64,-1:2,74,-1:6,182:5,-1:3,182:4,230,182:8,-1:2,182:10,-1:3,74,182,74:2");

	public com.sun.java_cup.internal.runtime.Symbol next_token ()
		throws java.io.IOException, 
Exception

		{
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

return new Symbol(sym.EOF);
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{ return new Symbol(sym.STAR); }
					case -3:
						break;
					case 3:
						{ return new Symbol(sym.SLASH); }
					case -4:
						break;
					case 4:
						{ return new Symbol(sym.PLUS); }
					case -5:
						break;
					case 5:
						{ return new Symbol(sym.MINUS); }
					case -6:
						break;
					case 6:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -7:
						break;
					case 7:
						{ throw new Exception(yytext()); }
					case -8:
						break;
					case 8:
						{ return new Symbol(sym.COMMA); }
					case -9:
						break;
					case 9:
						{ return new Symbol(sym.ATSIGN); }
					case -10:
						break;
					case 10:
						{ return new Symbol(sym.DOT); }
					case -11:
						break;
					case 11:
						{ return new Symbol(sym.VBAR); }
					case -12:
						break;
					case 12:
						{ return new Symbol(sym.DOLLAR); }
					case -13:
						break;
					case 13:
						{ return new Symbol(sym.EQ); }
					case -14:
						break;
					case 14:
						{ return new Symbol(sym.LT); }
					case -15:
						break;
					case 15:
						{ return new Symbol(sym.GT); }
					case -16:
						break;
					case 16:
						{ return new Symbol(sym.LPAREN); }
					case -17:
						break;
					case 17:
						{ return new Symbol(sym.RPAREN); }
					case -18:
						break;
					case 18:
						{ /* ignore white space. */ }
					case -19:
						break;
					case 19:
						{ return new Symbol(sym.LBRACK); }
					case -20:
						break;
					case 20:
						{ return new Symbol(sym.RBRACK); }
					case -21:
						break;
					case 21:
						{ return new Symbol(sym.INT, new Long(yytext())); }
					case -22:
						break;
					case 22:
						{ return new Symbol(sym.DSLASH); }
					case -23:
						break;
					case 23:
						{ return new Symbol(sym.ID); }
					case -24:
						break;
					case 24:
						{ return new Symbol(sym.OR); }
					case -25:
						break;
					case 25:
						{ return new Symbol(sym.DCOLON); }
					case -26:
						break;
					case 26:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -27:
						break;
					case 27:
						{ return new Symbol(sym.DDOT); }
					case -28:
						break;
					case 28:
						{ return new Symbol(sym.REAL, new Double(yytext())); }
					case -29:
						break;
					case 29:
						{ return new Symbol(sym.NE); }
					case -30:
						break;
					case 30:
						{ return new Symbol(sym.LE); }
					case -31:
						break;
					case 31:
						{ return new Symbol(sym.GE); }
					case -32:
						break;
					case 32:
						{ return new Symbol(sym.Literal,
			      yytext().substring(1, yytext().length() - 1)); }
					case -33:
						break;
					case 33:
						{ return new Symbol(sym.Literal,
			      yytext().substring(1, yytext().length() - 1)); }
					case -34:
						break;
					case 34:
						{ return new Symbol(sym.REAL, new Double(yytext())); }
					case -35:
						break;
					case 35:
						{ return new Symbol(sym.DIV); }
					case -36:
						break;
					case 36:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -37:
						break;
					case 37:
						{ return new Symbol(sym.MOD); }
					case -38:
						break;
					case 38:
						{ return new Symbol(sym.KEY); }
					case -39:
						break;
					case 39:
						{ return new Symbol(sym.AND); }
					case -40:
						break;
					case 40:
						{ return new Symbol(sym.SELF); }
					case -41:
						break;
					case 41:
						{ return new Symbol(sym.CHILD); }
					case -42:
						break;
					case 42:
						{ return new Symbol(sym.TEXT); }
					case -43:
						break;
					case 43:
						{ return new Symbol(sym.NODE); }
					case -44:
						break;
					case 44:
						{ return new Symbol(sym.PARENT); }
					case -45:
						break;
					case 45:
						{ return new Symbol(sym.TEXT); }
					case -46:
						break;
					case 46:
						{ return new Symbol(sym.NODE); }
					case -47:
						break;
					case 47:
						{ return new Symbol(sym.ANCESTOR); }
					case -48:
						break;
					case 48:
						{ return new Symbol(sym.PATTERN); }
					case -49:
						break;
					case 49:
						{ return new Symbol(sym.NAMESPACE); }
					case -50:
						break;
					case 50:
						{ return new Symbol(sym.COMMENT); }
					case -51:
						break;
					case 51:
						{ return new Symbol(sym.PRECEDING); }
					case -52:
						break;
					case 52:
						{ return new Symbol(sym.ATTRIBUTE); }
					case -53:
						break;
					case 53:
						{ return new Symbol(sym.FOLLOWING); }
					case -54:
						break;
					case 54:
						{ return new Symbol(sym.DESCENDANT); }
					case -55:
						break;
					case 55:
						{ return new Symbol(sym.COMMENT); }
					case -56:
						break;
					case 56:
						{ return new Symbol(sym.EXPRESSION); }
					case -57:
						break;
					case 57:
						{ return new Symbol(sym.ANCESTORORSELF); }
					case -58:
						break;
					case 58:
						{ return new Symbol(sym.PRECEDINGSIBLING); }
					case -59:
						break;
					case 59:
						{ return new Symbol(sym.FOLLOWINGSIBLING); }
					case -60:
						break;
					case 60:
						{ return new Symbol(sym.DESCENDANTORSELF); }
					case -61:
						break;
					case 61:
						{ return new Symbol(sym.PIPARAM); }
					case -62:
						break;
					case 62:
						{ return new Symbol(sym.PI); }
					case -63:
						break;
					case 63:
						{ return new Symbol(sym.PI); }
					case -64:
						break;
					case 65:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -65:
						break;
					case 66:
						{ throw new Exception(yytext()); }
					case -66:
						break;
					case 68:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -67:
						break;
					case 69:
						{ throw new Exception(yytext()); }
					case -68:
						break;
					case 71:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -69:
						break;
					case 72:
						{ throw new Exception(yytext()); }
					case -70:
						break;
					case 74:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -71:
						break;
					case 75:
						{ throw new Exception(yytext()); }
					case -72:
						break;
					case 77:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -73:
						break;
					case 79:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -74:
						break;
					case 81:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -75:
						break;
					case 83:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -76:
						break;
					case 85:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -77:
						break;
					case 87:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -78:
						break;
					case 89:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -79:
						break;
					case 91:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -80:
						break;
					case 93:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -81:
						break;
					case 95:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -82:
						break;
					case 97:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -83:
						break;
					case 99:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -84:
						break;
					case 101:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -85:
						break;
					case 103:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -86:
						break;
					case 105:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -87:
						break;
					case 107:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -88:
						break;
					case 109:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -89:
						break;
					case 111:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -90:
						break;
					case 113:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -91:
						break;
					case 115:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -92:
						break;
					case 117:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -93:
						break;
					case 119:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -94:
						break;
					case 121:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -95:
						break;
					case 123:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -96:
						break;
					case 125:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -97:
						break;
					case 126:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -98:
						break;
					case 127:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -99:
						break;
					case 128:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -100:
						break;
					case 129:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -101:
						break;
					case 130:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -102:
						break;
					case 131:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -103:
						break;
					case 132:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -104:
						break;
					case 133:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -105:
						break;
					case 134:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -106:
						break;
					case 135:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -107:
						break;
					case 136:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -108:
						break;
					case 137:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -109:
						break;
					case 138:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -110:
						break;
					case 139:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -111:
						break;
					case 140:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -112:
						break;
					case 141:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -113:
						break;
					case 142:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -114:
						break;
					case 143:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -115:
						break;
					case 144:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -116:
						break;
					case 145:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -117:
						break;
					case 146:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -118:
						break;
					case 147:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -119:
						break;
					case 148:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -120:
						break;
					case 149:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -121:
						break;
					case 150:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -122:
						break;
					case 151:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -123:
						break;
					case 152:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -124:
						break;
					case 153:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -125:
						break;
					case 154:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -126:
						break;
					case 155:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -127:
						break;
					case 156:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -128:
						break;
					case 157:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -129:
						break;
					case 158:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -130:
						break;
					case 159:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -131:
						break;
					case 160:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -132:
						break;
					case 161:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -133:
						break;
					case 162:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -134:
						break;
					case 163:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -135:
						break;
					case 164:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -136:
						break;
					case 165:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -137:
						break;
					case 166:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -138:
						break;
					case 167:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -139:
						break;
					case 168:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -140:
						break;
					case 169:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -141:
						break;
					case 170:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -142:
						break;
					case 171:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -143:
						break;
					case 172:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -144:
						break;
					case 173:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -145:
						break;
					case 174:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -146:
						break;
					case 175:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -147:
						break;
					case 176:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -148:
						break;
					case 177:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -149:
						break;
					case 178:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -150:
						break;
					case 179:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -151:
						break;
					case 180:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -152:
						break;
					case 181:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -153:
						break;
					case 182:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -154:
						break;
					case 185:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -155:
						break;
					case 187:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -156:
						break;
					case 189:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -157:
						break;
					case 190:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -158:
						break;
					case 191:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -159:
						break;
					case 192:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -160:
						break;
					case 193:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -161:
						break;
					case 194:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -162:
						break;
					case 195:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -163:
						break;
					case 196:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -164:
						break;
					case 197:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -165:
						break;
					case 198:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -166:
						break;
					case 199:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -167:
						break;
					case 200:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -168:
						break;
					case 201:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -169:
						break;
					case 202:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -170:
						break;
					case 203:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -171:
						break;
					case 204:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -172:
						break;
					case 205:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -173:
						break;
					case 206:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -174:
						break;
					case 207:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -175:
						break;
					case 208:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -176:
						break;
					case 209:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -177:
						break;
					case 210:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -178:
						break;
					case 211:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -179:
						break;
					case 212:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -180:
						break;
					case 213:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -181:
						break;
					case 214:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -182:
						break;
					case 215:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -183:
						break;
					case 216:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -184:
						break;
					case 217:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -185:
						break;
					case 218:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -186:
						break;
					case 219:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -187:
						break;
					case 220:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -188:
						break;
					case 221:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -189:
						break;
					case 222:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -190:
						break;
					case 223:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -191:
						break;
					case 224:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -192:
						break;
					case 225:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -193:
						break;
					case 226:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -194:
						break;
					case 227:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -195:
						break;
					case 228:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -196:
						break;
					case 229:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -197:
						break;
					case 230:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -198:
						break;
					case 231:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -199:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}

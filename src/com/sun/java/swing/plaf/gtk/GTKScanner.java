/*
 * @(#)GTKScanner.java	1.40 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import java.io.*;
import java.util.HashMap;

/**
 * @author  Shannon Hickey
 * @version 1.40 12/19/03
 */
class GTKScanner {

    public static final String CHARS_a_2_z = "abcdefghijklmnopqrstuvwxyz";
    public static final String CHARS_A_2_Z = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String CHARS_DIGITS = "0123456789";

    public static final int TOKEN_EOF                 = -1;
    public static final int TOKEN_LEFT_PAREN          = '(';
    public static final int TOKEN_RIGHT_PAREN         = ')';
    public static final int TOKEN_LEFT_CURLY          = '{';
    public static final int TOKEN_RIGHT_CURLY         = '}';
    public static final int TOKEN_LEFT_BRACE          = '[';
    public static final int TOKEN_RIGHT_BRACE         = ']';
    public static final int TOKEN_EQUAL_SIGN          = '=';
    public static final int TOKEN_COMMA               = ',';
    public static final int TOKEN_NONE                = 256;
    public static final int TOKEN_ERROR               = TOKEN_NONE + 1;
    public static final int TOKEN_CHAR                = TOKEN_ERROR + 1;
    public static final int TOKEN_BINARY              = TOKEN_CHAR + 1;
    public static final int TOKEN_OCTAL               = TOKEN_BINARY + 1;
    public static final int TOKEN_INT                 = TOKEN_OCTAL + 1;
    public static final int TOKEN_HEX                 = TOKEN_INT + 1;
    public static final int TOKEN_FLOAT               = TOKEN_HEX + 1;
    public static final int TOKEN_STRING              = TOKEN_FLOAT + 1;
    public static final int TOKEN_SYMBOL              = TOKEN_STRING + 1;
    public static final int TOKEN_IDENTIFIER          = TOKEN_SYMBOL + 1;
    public static final int TOKEN_IDENTIFIER_NULL     = TOKEN_IDENTIFIER + 1;
    public static final int TOKEN_LAST                = TOKEN_IDENTIFIER_NULL + 1;

    public static final int ERR_UNKNOWN               = 0;
    public static final int ERR_UNEXP_EOF             = ERR_UNKNOWN + 1;
    public static final int ERR_UNEXP_EOF_IN_STRING   = ERR_UNEXP_EOF + 1;
    public static final int ERR_UNEXP_EOF_IN_COMMENT  = ERR_UNEXP_EOF_IN_STRING + 1;
    public static final int ERR_NON_DIGIT_IN_CONST    = ERR_UNEXP_EOF_IN_COMMENT + 1;
    public static final int ERR_DIGIT_RADIX           = ERR_NON_DIGIT_IN_CONST + 1;
    public static final int ERR_FLOAT_RADIX           = ERR_DIGIT_RADIX + 1;
    public static final int ERR_FLOAT_MALFORMED       = ERR_FLOAT_RADIX + 1;

    String whiteSpaceChars = " \t\r\n";
    String identifierFirst = CHARS_a_2_z + CHARS_A_2_Z + "_";
    String identifierNth = CHARS_a_2_z + CHARS_A_2_Z + "_-" + CHARS_DIGITS;
    String commentSingle = "#\n";
    boolean caseSensitive = false;
    boolean scanCommentMulti = true;
    boolean scanIdentifier = true;
    boolean scanIdentifier1Char = false;
    boolean scanIdentifierNULL = false;
    boolean scanSymbols = true;
    boolean scanBinary = false;
    boolean scanOctal = true;
    boolean scanFloat = true;
    boolean scanHex = true;
    boolean scanHexDollar = false;
    boolean scanStringSq = true;
    boolean scanStringDq = true;
    boolean numbers2Int = true;
    boolean int2Float = false;
    boolean identifier2String = false;
    boolean char2Token = true;
    boolean symbol2Token = false;

    private static class ScannerKey {

        private int scope;
        private String symbol;

        public int value = -1;

        ScannerKey(int scope, String symbol) {
            this.scope = scope;
            this.symbol = symbol;
        }

        public boolean equals(Object o) {
            if (o instanceof ScannerKey) {
                ScannerKey comp = (ScannerKey)o;
                return scope == comp.scope && symbol.equals(comp.symbol);
            }

            return false;
        }

        public int hashCode() {
            int result = 17;
            result = 37 * result + scope;
            result = 37 * result + symbol.hashCode();
            return result;
        }

    }

    static class TokenValue {
        long longVal;
        double doubleVal;
        char charVal;
        String stringVal;

        TokenValue() {
            clear();
        }

        void copyFrom(TokenValue other) {
            longVal = other.longVal;
            doubleVal = other.doubleVal;
            charVal = other.charVal;
            stringVal = other.stringVal;
        }

        void clear() {
            longVal = 0L;
            doubleVal = 0.0D;
            charVal = (char)0;
            stringVal = null;
        }
    }
    
    private String inputName;

    private HashMap symbolTable = new HashMap();

    private Reader reader;

    int currToken;
    TokenValue currValue = new TokenValue();
    int currLine;
    int currPosition;

    int nextToken;
    TokenValue nextValue = new TokenValue();
    int nextLine;
    int nextPosition;

    int currScope = 0;
    private static int nextUniqueScope = 1;

    private static final int CHAR_EOF = -1;
    private static final int CHAR_NONE = -2;

    private int peekedChar = CHAR_NONE;

    private ScannerKey lookupKey = new ScannerKey(0, null);

    public GTKScanner() {
        clearScanner();
    }

    public void clearScanner() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException ioe) {
            }
            
            reader = null;
        }

        inputName = null;
        
        currToken = TOKEN_NONE;
        currValue.clear();
        currLine = 1;
        currPosition = 0;

        nextToken = TOKEN_NONE;
        nextValue.clear();
        nextLine = 1;
        nextPosition = 0;

        currScope = 0;

        peekedChar = CHAR_NONE;
    }

    public void scanReader(Reader r, String inputName) {
        if (r == null) {
            return;
        }

        if (reader != null) {
            clearScanner();
        }

        reader = r;
        this.inputName = inputName;
    }

    public static int getUniqueScopeID() {
        return nextUniqueScope++;
    }

    public int setScope(int scope) {
        int oldScope = currScope;
        currScope = scope;
        return oldScope;
    }

    public void addSymbol(String symbol, int value) {
        if (symbol == null) {
            return;
        }

        ScannerKey key = lookupSymbol(symbol);

        if (key == null) {
            key = new ScannerKey(currScope, caseSensitive ? symbol : symbol.toLowerCase());
            symbolTable.put(key, key);
        }

        key.value = value;
    }

    public boolean containsSymbol(String symbol) {
        return lookupSymbol(symbol) != null;
    }

    private ScannerKey lookupSymbol(String symbol) {
        lookupKey.scope = currScope;
        lookupKey.symbol = (caseSensitive ? symbol : symbol.toLowerCase());
        return (ScannerKey)symbolTable.get(lookupKey);
    }

    public void clearSymbolTable() {
        symbolTable.clear();
    }

    public int peekNextToken() throws IOException {
        if (nextToken == TOKEN_NONE) {
            readAToken();

            switch(nextToken) {
                case TOKEN_SYMBOL:
                    if (symbol2Token) {
                        nextToken = (int)nextValue.longVal;
                    }
                    break;
                case TOKEN_IDENTIFIER:
                    if (identifier2String) {
                        nextToken = TOKEN_STRING;
                    }
                    break;
                case TOKEN_HEX:
                case TOKEN_OCTAL:
                case TOKEN_BINARY:
                    if (numbers2Int) {
                        nextToken = TOKEN_INT;
                    }
                    break;
            }

            if (nextToken == TOKEN_INT && int2Float) {
                nextToken = TOKEN_FLOAT;
                nextValue.doubleVal = nextValue.longVal;
            }            
        }

        return nextToken;
    }

    public int getToken() throws IOException {
        currToken = peekNextToken();
        currValue.copyFrom(nextValue);
        currLine = nextLine;
        currPosition = nextPosition;

        if (currToken != TOKEN_EOF) {
            nextToken = TOKEN_NONE;
        }

        return currToken;
    }

    private int peekNextChar() throws IOException {
        if (peekedChar == CHAR_NONE) {
            peekedChar = reader.read();
        }

        return peekedChar;
    }

    private int getChar() throws IOException {
        int ch = peekNextChar();

        if (ch != CHAR_EOF) {
            peekedChar = CHAR_NONE;

            if (ch == '\n') {
                nextPosition = 0;
                nextLine++;
            } else {
                nextPosition++;
            }
        }

        return ch;
    }


    // ----- scanning methods and variables ----- //

    private StringBuffer sb;
    private TokenValue value = new TokenValue();
    private int token;
    private int ch;

    private boolean skipSpaceAndComments() throws IOException {
        while(ch != CHAR_EOF) {
            if (whiteSpaceChars.indexOf(ch) != -1) {
                // continue
            } else if (scanCommentMulti && ch == '/' && peekNextChar() == '*') {
                getChar();
                
                while((ch = getChar()) != CHAR_EOF) {
                    if (ch == '*' && peekNextChar() == '/') {
                        getChar();
                        break;
                    }
                }

                if (ch == CHAR_EOF) {
                    return false;
                }
            } else if (commentSingle.length() == 2 && ch == commentSingle.charAt(0)) {
                while((ch = getChar()) != CHAR_EOF) {
                    if (ch == commentSingle.charAt(1)) {
                        break;
                    }
                }
                
                if (ch == CHAR_EOF) {
                    return false;
                }
            } else {
                break;
            }
            
            ch = getChar();
        }
        
        return true;
    }

    private void readAToken() throws IOException {
        boolean inString = false;

        nextValue.clear();
        sb = null;

        do {
            value.clear();
            token = TOKEN_NONE;

            ch = getChar();
            
            if (!skipSpaceAndComments()) {
                token = TOKEN_ERROR;
                value.longVal = ERR_UNEXP_EOF_IN_COMMENT;
            } else if (scanIdentifier && ch != CHAR_EOF && identifierFirst.indexOf(ch) != -1) {
                checkForIdentifier();
                handleOrdinaryChar();
            } else {
                switch(ch) {
                    case CHAR_EOF:
                        token = TOKEN_EOF;
                        break;
                    case '"':
                        if (!scanStringDq) {
                            handleOrdinaryChar();
                        } else {
                            token = TOKEN_STRING;
                            inString = true;
                            
                            sb = new StringBuffer();

                            while ((ch = getChar()) != CHAR_EOF) {
                                if (ch == '"') {
                                    inString = false;
                                    break;
                                } else {
                                    if (ch == '\\') {
                                        ch = getChar();
                                        switch(ch) {
                                            case CHAR_EOF:
                                                break;
                                            case '\\':
                                                sb.append('\\');
                                                break;
                                            case 'n':
                                                sb.append('\n');
                                                break;
                                            case 'r':
                                                sb.append('\r');
                                                break;
                                            case 't':
                                                sb.append('\t');
                                                break;
                                            case 'f':
                                                sb.append('\f');
                                                break;
                                            case 'b':
                                                sb.append('\b');
                                                break;
                                            case '0':
                                            case '1':
                                            case '2':
                                            case '3':
                                            case '4':
                                            case '5':
                                            case '6':
                                            case '7':
                                                int i = ch - '0';
                                                int nextCh = peekNextChar();

                                                if (nextCh >= '0' && nextCh <= '7') {
                                                    ch = getChar();
                                                    i = i * 8 + ch - '0';
                                                    nextCh = peekNextChar();
                                                    if (nextCh >= '0' && nextCh <= '7') {
                                                        ch = getChar();
                                                        i = i * 8 + ch - '0';
                                                    }
                                                }

                                                sb.append((char)i);
                                                break;
                                            default:
                                                sb.append((char)ch);
                                                break;
                                        }
                                    } else {
                                        sb.append((char)ch);
                                    }
                                }
                            }

                            ch = CHAR_EOF;
                        }

                        break;
                    case '\'':
                        if (!scanStringSq) {
                            handleOrdinaryChar();
                        } else {
                            token = TOKEN_STRING;
                            inString = true;
                            
                            sb = new StringBuffer();

                            while ((ch = getChar()) != CHAR_EOF) {
                                if (ch == '\'') {
                                    inString = false;
                                    break;
                                } else {
                                    sb.append((char)ch);
                                }
                            }

                            ch = CHAR_EOF;
                        }

                        break;
                    case '$':
                        if (!scanHexDollar) {
                            handleOrdinaryChar();
                        } else {
                            token = TOKEN_HEX;
                            ch = getChar();
                            scanNumber(false);
                        }

                        break;
                    case '.':
                        if (!scanFloat) {
                            handleOrdinaryChar();
                        } else {
                            token = TOKEN_FLOAT;
                            ch = getChar();
                            scanNumber(true);
                        }

                        break;
                    case '0':
                        if (scanOctal) {
                            token = TOKEN_OCTAL;
                        } else {
                            token = TOKEN_INT;
                        }

                        ch = peekNextChar();

                        if (scanHex && (ch == 'x' || ch == 'X')) {
                            token = TOKEN_HEX;
                            getChar();
                            ch = getChar();
                            if (ch == CHAR_EOF) {
                                token = TOKEN_ERROR;
                                value.longVal = ERR_UNEXP_EOF;
                                break;
                            }

                            if (char2int(ch, 16) < 0) {
                                token = TOKEN_ERROR;
                                value.longVal = ERR_DIGIT_RADIX;
                                ch = CHAR_EOF;
                                break;
                            }
                        } else if (scanBinary && (ch == 'b' || ch == 'B')) {
                            token = TOKEN_BINARY;
                            getChar();
                            ch = getChar();
                            if (ch == CHAR_EOF) {
                                token = TOKEN_ERROR;
                                value.longVal = ERR_UNEXP_EOF;
                                break;
                            }

                            if (char2int(ch, 2) < 0) {
                                token = TOKEN_ERROR;
                                value.longVal = ERR_NON_DIGIT_IN_CONST;
                                ch = CHAR_EOF;
                                break;
                            }
                        } else {
                            ch = '0';
                        }

                        // purposely fall through
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        scanNumber(false);
                        break;
                    default:
                        handleOrdinaryChar();
                        break;
                }
            }
        } while (ch != CHAR_EOF);

        if (inString) {
            token = TOKEN_ERROR;
            value.longVal = ERR_UNEXP_EOF_IN_STRING;
            sb = null;
        }

        if (sb != null) {
            value.stringVal = sb.toString();
            sb = null;
        }

        if (token == TOKEN_IDENTIFIER) {
            if (scanSymbols) {
                int scope = currScope;
                ScannerKey key = lookupSymbol(value.stringVal);

                if (key != null) {
                    value.stringVal = null;
                    token = TOKEN_SYMBOL;
                    value.longVal = key.value;
                }
            }

            if (token == TOKEN_IDENTIFIER && scanIdentifierNULL & value.stringVal.length() == 4) {
                if ("NULL".equals(caseSensitive ? value.stringVal : value.stringVal.toUpperCase())) {
                    token = TOKEN_IDENTIFIER_NULL;
                }
            }
        }

        nextToken = token;
        nextValue.copyFrom(value);
    }

    private void handleOrdinaryChar() throws IOException {
        if (ch != CHAR_EOF) {
            if (char2Token) {
                token = ch;
            } else {
                token = TOKEN_CHAR;
                value.charVal = (char)ch;
            }

            ch = CHAR_EOF;
        }
    }

    private void checkForIdentifier() throws IOException {
        if (ch != CHAR_EOF && identifierNth.indexOf(peekNextChar()) != -1) {
            token = TOKEN_IDENTIFIER;

            sb = new StringBuffer();
            sb.append((char)ch);

            do {
                ch = getChar();
                sb.append((char)ch);
                ch = peekNextChar();
            } while (ch != CHAR_EOF && identifierNth.indexOf(ch) != -1);

            ch = CHAR_EOF;
        } else if (scanIdentifier1Char) {
            token = TOKEN_IDENTIFIER;
            value.stringVal = String.valueOf((char)ch);

            ch = CHAR_EOF;
        }
    }

    private static int char2int(int c, int base) {
        if (c >= '0' && c <= '9') {
            c -= '0';
        } else if (c >= 'A' && c <= 'Z') {
            c -= 'A' - 10;
        } else if (c >= 'a' && c <= 'z') {
            c -= 'a' - 10;
        } else {
            return -1;
        }

        return c < base ? c : -1;
    }

    private void scanNumber(boolean seenDot) throws IOException {
        boolean inNumber = true;

        if (token == TOKEN_NONE) {
            token = TOKEN_INT;
        }
        
        sb = new StringBuffer(seenDot ? "0." : "");
        sb.append((char)ch);

        do {
            boolean isExponent = (token == TOKEN_FLOAT && (ch == 'e' || ch == 'E'));

            ch = peekNextChar();

            if (char2int(ch, 36) >= 0
                    || (scanFloat && ch == '.')
                    || (isExponent && (ch == '+' || ch == '-'))) {
                ch = getChar();

                switch(ch) {
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        sb.append((char)ch);
                        break;
                    case '.':
                        if (token != TOKEN_INT && token != TOKEN_OCTAL) {
                            value.longVal = (token == TOKEN_FLOAT ? ERR_FLOAT_MALFORMED : ERR_FLOAT_RADIX);
                            token = TOKEN_ERROR;
                            inNumber = false;
                        } else {
                            token = TOKEN_FLOAT;
                            sb.append((char)ch);
                        }
                        break;
                    case '+':
                    case '-':
                        if (token != TOKEN_FLOAT) {
                            token = TOKEN_ERROR;
                            value.longVal = ERR_NON_DIGIT_IN_CONST;
                            inNumber = false;
                        } else {
                            sb.append((char)ch);
                        }
                        break;
                    case 'E':
                    case 'e':
                        if ((token != TOKEN_HEX && !scanFloat)
                                || (token != TOKEN_HEX
                                    && token != TOKEN_OCTAL
                                    && token != TOKEN_FLOAT
                                    && token != TOKEN_INT)) {
                            token = TOKEN_ERROR;
                            value.longVal = ERR_NON_DIGIT_IN_CONST;
                            inNumber = false;
                        } else {
                            if (token != TOKEN_HEX) {
                                token = TOKEN_FLOAT;
                            }
                            sb.append((char)ch);
                        }
                        break;
                    default:
                        if (token != TOKEN_HEX) {
                            token = TOKEN_ERROR;
                            value.longVal = ERR_NON_DIGIT_IN_CONST;
                        } else {
                            sb.append((char)ch);
                        }
                        break;
                }
            } else {
                inNumber = false;
            }
        } while (inNumber);

        try {
            switch(token) {
                case TOKEN_INT:
                    value.longVal = Long.parseLong(sb.toString(), 10);
                    break;
                case TOKEN_FLOAT:
                    value.doubleVal = Double.parseDouble(sb.toString());
                    break;
                case TOKEN_HEX:
                    value.longVal = Long.parseLong(sb.toString(), 16);
                    break;
                case TOKEN_OCTAL:
                    value.longVal = Long.parseLong(sb.toString(), 8);
                    break;
                case TOKEN_BINARY:
                    value.longVal = Long.parseLong(sb.toString(), 2);
                    break;
            }
        } catch (NumberFormatException nfe) {
            // PENDING(shannonh) - in some cases this could actually be ERR_DIGIT_RADIX
            token = TOKEN_ERROR;
            value.longVal = ERR_NON_DIGIT_IN_CONST;
        }

        sb = null;
        ch = CHAR_EOF;
    }

    public void printMessage(String message, boolean isError) {
        System.err.print(inputName + ":" + currLine + ": ");

        if (isError) {
            System.err.print("error: ");
        }

        System.err.println(message);
    }

    // PENDING(shannonh) - a good implementation of this method is needed
    public void unexpectedToken(int expected, String symbolName, String message, boolean isError) {
        String prefix = "lexical error or unexpected token, expected valid token";

        if (message != null) {
            prefix += " - " + message;
        }

        printMessage(prefix, isError);
    }

}

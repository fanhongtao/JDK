/*
 * @(#)PropertyParser.java	1.12 05/12/22
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import java.util.HashMap;
import java.awt.Color;
import java.awt.Insets;
import java.io.*;

/**
 * @author  Shannon Hickey
 * @version 1.12 12/22/05
 */
abstract class PropertyParser {

    // Whether or not simple strings passed from GTKParser need to be escaped
    // for rescanning/parsing by this parser. The default is false since most
    // property parsers either get complex values to parse, or simple string
    // values.
    public boolean needSimpleStringsEscaped() {
        return false;
    }

    private static final PropertyParser INSETS_PARSER = new PropertyParser() {
        public Object parse(String source) {
            GTKScanner scanner = new GTKScanner();
            scanner.scanReader(new StringReader(source), source);

            try {
                int left;
                int right;
                int top;
                int bottom;

                if (scanner.getToken() != GTKScanner.TOKEN_LEFT_CURLY) {
                    return null;
                }
                
                if (scanner.getToken() != GTKScanner.TOKEN_INT) {
                    return null;
                }
                
                left = (int)scanner.currValue.longVal;
                
                if (scanner.getToken() != GTKScanner.TOKEN_COMMA) {
                    return null;
                }
                
                if (scanner.getToken() != GTKScanner.TOKEN_INT) {
                    return null;
                }
                
                right = (int)scanner.currValue.longVal;
                
                if (scanner.getToken() != GTKScanner.TOKEN_COMMA) {
                    return null;
                }
                
                if (scanner.getToken() != GTKScanner.TOKEN_INT) {
                    return null;
                }
                
                top = (int)scanner.currValue.longVal;
                
                if (scanner.getToken() != GTKScanner.TOKEN_COMMA) {
                    return null;
                }
                
                if (scanner.getToken() != GTKScanner.TOKEN_INT) {
                    return null;
                }
                
                bottom = (int)scanner.currValue.longVal;
                
                if (scanner.getToken() != GTKScanner.TOKEN_RIGHT_CURLY) {
                    return null;
                }
                
                return new Insets(top, left, bottom, right);
            } catch (IOException ioe) {
            } finally {
                scanner.clearScanner();
            }

            return null;
        }
    };

    private static final PropertyParser BOOLEAN_PARSER = new PropertyParser() {
        public Object parse(String source) {
            source = source.trim();

            if (source.equals("TRUE")) {
                return Boolean.TRUE;
            } else if (source.equals("FALSE")) {
                return Boolean.FALSE;
            }
            
            return null;
        }
    };

    private static final PropertyParser SHADOW_PARSER = new PropertyParser() {
        public Object parse(String source) {
            source = source.trim();
            
            if (source.equals("SHADOW_IN")) {
                return new Integer(GTKConstants.SHADOW_IN);
            } else if (source.equals("SHADOW_OUT")) {
                return new Integer(GTKConstants.SHADOW_OUT);
            } else if (source.equals("SHADOW_ETCHED_IN")) {
                return new Integer(GTKConstants.SHADOW_ETCHED_IN);
            } else if (source.equals("SHADOW_ETCHED_OUT")) {
                return new Integer(GTKConstants.SHADOW_ETCHED_OUT);
            } else if (source.equals("SHADOW_NONE")) {
                return new Integer(GTKConstants.SHADOW_NONE);
            }
            
            return null;
        }
    };

    private static final PropertyParser FOCUS_LINE_PARSER = new PropertyParser() {
        public Object parse(String source) {
            int len = source.length();
            int[] retVal = new int[len];

            for (int i = 0; i < len; i++) {
                retVal[i] = (int)source.charAt(i);
            }

            if (retVal.length == 0 ||
                retVal.length == 1 && retVal[0] == 0) {
                // solid line
                retVal = new int[] { Integer.MAX_VALUE };

            } else if (retVal.length == 1) {
                // "\x" actually means "\x\x"
                int value = retVal[0];
                retVal = new int[] { value, value };
            }
            return retVal;
        }
    };

    private static final PropertyParser COLOR_PARSER = new PropertyParser() {
        // We may receive simple strings such as "red" or "#CCCCCC". These need
        // to be escaped by GTKParser first so we can rescan/parse them here.
        public boolean needSimpleStringsEscaped() {
            return true;
        }

        public Object parse(String source) {
            GTKScanner scanner = new GTKScanner();
            scanner.scanReader(new StringReader(source), source);

            try {
                Color[] ret = new Color[1];
                int token = GTKParser.parseColor(scanner, ret, 0);
                if (token == GTKScanner.TOKEN_NONE) {
                    return ret[0];
                }
            } catch (IOException ioe) {
            } finally {
                scanner.clearScanner();
            }

            return null;
        }
    };
    private static final HashMap PARSERS = new HashMap();
    
    static {
        PARSERS.put("default-border", INSETS_PARSER);
        PARSERS.put("interior-focus", BOOLEAN_PARSER);
        PARSERS.put("shadow-type", SHADOW_PARSER);
        PARSERS.put("focus-line-pattern", FOCUS_LINE_PARSER);
        PARSERS.put("cursor-color", COLOR_PARSER);
    }

    public static PropertyParser getParserFor(String type) {
        return (PropertyParser)PARSERS.get(type);
    }

    public abstract Object parse(String source);

}

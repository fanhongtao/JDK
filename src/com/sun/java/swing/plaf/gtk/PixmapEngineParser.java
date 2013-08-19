/*
 * @(#)PixmapEngineParser.java	1.17 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import java.io.IOException;
import java.util.ArrayList;
import java.awt.Font;
import java.awt.Insets;

/**
 * A parser for the "pixmap" engine sections in GTK rc theme files.
 *
 * @author  Shannon Hickey
 * @version 1.17 01/23/03
 */
class PixmapEngineParser extends GTKEngineParser {

    private static final int SYM_IMAGE             = GTKScanner.TOKEN_LAST + 1;
    private static final int SYM_FUNCTION          = SYM_IMAGE + 1;
    private static final int SYM_FILE              = SYM_FUNCTION + 1;
    private static final int SYM_STRETCH           = SYM_FILE + 1;
    private static final int SYM_RECOLORABLE       = SYM_STRETCH + 1;
    private static final int SYM_BORDER            = SYM_RECOLORABLE + 1;
    private static final int SYM_DETAIL            = SYM_BORDER + 1;
    private static final int SYM_STATE             = SYM_DETAIL + 1;
    private static final int SYM_SHADOW            = SYM_STATE + 1;
    private static final int SYM_GAP_SIDE          = SYM_SHADOW + 1;
    private static final int SYM_GAP_FILE          = SYM_GAP_SIDE + 1;
    private static final int SYM_GAP_BORDER        = SYM_GAP_FILE + 1;
    private static final int SYM_GAP_START_FILE    = SYM_GAP_BORDER + 1;
    private static final int SYM_GAP_START_BORDER  = SYM_GAP_START_FILE + 1;
    private static final int SYM_GAP_END_FILE      = SYM_GAP_START_BORDER + 1;
    private static final int SYM_GAP_END_BORDER    = SYM_GAP_END_FILE + 1;
    private static final int SYM_OVERLAY_FILE      = SYM_GAP_END_BORDER + 1;
    private static final int SYM_OVERLAY_BORDER    = SYM_OVERLAY_FILE + 1;
    private static final int SYM_OVERLAY_STRETCH   = SYM_OVERLAY_BORDER + 1;
    private static final int SYM_ARROW_DIRECTION   = SYM_OVERLAY_STRETCH + 1;
    private static final int SYM_ORIENTATION       = SYM_ARROW_DIRECTION + 1;
    private static final int SYM_D_HLINE           = SYM_ORIENTATION + 1;
    private static final int SYM_D_VLINE           = SYM_D_HLINE + 1;
    private static final int SYM_D_SHADOW          = SYM_D_VLINE + 1;
    private static final int SYM_D_POLYGON         = SYM_D_SHADOW + 1;
    private static final int SYM_D_ARROW           = SYM_D_POLYGON + 1;
    private static final int SYM_D_DIAMOND         = SYM_D_ARROW + 1;
    private static final int SYM_D_OVAL            = SYM_D_DIAMOND + 1;
    private static final int SYM_D_STRING          = SYM_D_OVAL + 1;
    private static final int SYM_D_BOX             = SYM_D_STRING + 1;
    private static final int SYM_D_FLAT_BOX        = SYM_D_BOX + 1;
    private static final int SYM_D_CHECK           = SYM_D_FLAT_BOX + 1;
    private static final int SYM_D_OPTION          = SYM_D_CHECK + 1;
    private static final int SYM_D_CROSS           = SYM_D_OPTION + 1;
    private static final int SYM_D_RAMP            = SYM_D_CROSS + 1;
    private static final int SYM_D_TAB             = SYM_D_RAMP + 1;
    private static final int SYM_D_SHADOW_GAP      = SYM_D_TAB + 1;
    private static final int SYM_D_BOX_GAP         = SYM_D_SHADOW_GAP + 1;
    private static final int SYM_D_EXTENSION       = SYM_D_BOX_GAP + 1;
    private static final int SYM_D_FOCUS           = SYM_D_EXTENSION + 1;
    private static final int SYM_D_SLIDER          = SYM_D_FOCUS + 1;
    private static final int SYM_D_ENTRY           = SYM_D_SLIDER + 1;
    private static final int SYM_D_HANDLE          = SYM_D_ENTRY + 1;
    private static final int SYM_D_STEPPER         = SYM_D_HANDLE + 1;
    private static final int SYM_TRUE              = SYM_D_STEPPER + 1;
    private static final int SYM_FALSE             = SYM_TRUE + 1;
    private static final int SYM_TOP               = SYM_FALSE + 1;
    private static final int SYM_UP                = SYM_TOP + 1;
    private static final int SYM_BOTTOM            = SYM_UP + 1;
    private static final int SYM_DOWN              = SYM_BOTTOM + 1;
    private static final int SYM_LEFT              = SYM_DOWN + 1;
    private static final int SYM_RIGHT             = SYM_LEFT + 1;
    private static final int SYM_NORMAL            = SYM_RIGHT + 1;
    private static final int SYM_ACTIVE            = SYM_NORMAL + 1;
    private static final int SYM_PRELIGHT          = SYM_ACTIVE + 1;
    private static final int SYM_SELECTED          = SYM_PRELIGHT + 1;
    private static final int SYM_INSENSITIVE       = SYM_SELECTED + 1;
    private static final int SYM_NONE              = SYM_INSENSITIVE + 1;
    private static final int SYM_IN                = SYM_NONE + 1;
    private static final int SYM_OUT               = SYM_IN + 1;
    private static final int SYM_ETCHED_IN         = SYM_OUT + 1;
    private static final int SYM_ETCHED_OUT        = SYM_ETCHED_IN + 1;
    private static final int SYM_HORIZONTAL        = SYM_ETCHED_OUT + 1;
    private static final int SYM_VERTICAL          = SYM_HORIZONTAL + 1;

    private static final int[] symbolVals = {
        SYM_IMAGE, SYM_FUNCTION, SYM_FILE, SYM_STRETCH, SYM_RECOLORABLE,
        SYM_BORDER, SYM_DETAIL, SYM_STATE, SYM_SHADOW, SYM_GAP_SIDE,
        SYM_GAP_FILE, SYM_GAP_BORDER, SYM_GAP_START_FILE, SYM_GAP_START_BORDER,
        SYM_GAP_END_FILE, SYM_GAP_END_BORDER, SYM_OVERLAY_FILE, SYM_OVERLAY_BORDER,
        SYM_OVERLAY_STRETCH, SYM_ARROW_DIRECTION, SYM_ORIENTATION, SYM_D_HLINE,
        SYM_D_VLINE, SYM_D_SHADOW, SYM_D_POLYGON, SYM_D_ARROW, SYM_D_DIAMOND,
        SYM_D_OVAL, SYM_D_STRING, SYM_D_BOX, SYM_D_FLAT_BOX, SYM_D_CHECK,
        SYM_D_OPTION, SYM_D_CROSS, SYM_D_RAMP, SYM_D_TAB, SYM_D_SHADOW_GAP,
        SYM_D_BOX_GAP, SYM_D_EXTENSION, SYM_D_FOCUS, SYM_D_SLIDER, SYM_D_ENTRY,
        SYM_D_HANDLE, SYM_D_STEPPER, SYM_TRUE, SYM_FALSE, SYM_TOP, SYM_UP,
        SYM_BOTTOM, SYM_DOWN, SYM_LEFT, SYM_RIGHT, SYM_NORMAL, SYM_ACTIVE,
        SYM_PRELIGHT, SYM_SELECTED, SYM_INSENSITIVE, SYM_NONE, SYM_IN, SYM_OUT,
        SYM_ETCHED_IN, SYM_ETCHED_OUT, SYM_HORIZONTAL, SYM_VERTICAL
    };

    private static final String[] symbolNames = {
        "image",            // SYM_IMAGE
        "function",         // SYM_FUNCTION
        "file",             // SYM_FILE
        "stretch",          // SYM_STRETCH
        "recolorable",      // SYM_RECOLORABLE
        "border",           // SYM_BORDER
        "detail",           // SYM_DETAIL
        "state",            // SYM_STATE
        "shadow",           // SYM_SHADOW
        "gap_side",         // SYM_GAP_SIDE
        "gap_file",         // SYM_GAP_FILE
        "gap_border",       // SYM_GAP_BORDER
        "gap_start_file",   // SYM_GAP_START_FILE
        "gap_start_border", // SYM_GAP_START_BORDER
        "gap_end_file",     // SYM_GAP_END_FILE
        "gap_end_border",   // SYM_GAP_END_BORDER
        "overlay_file",     // SYM_OVERLAY_FILE
        "overlay_border",   // SYM_OVERLAY_BORDER
        "overlay_stretch",  // SYM_OVERLAY_STRETCH
        "arrow_direction",  // SYM_ARROW_DIRECTION
        "orientation",      // SYM_ORIENTATION

        "HLINE",            // SYM_D_HLINE
        "VLINE",            // SYM_D_VLINE
        "SHADOW",           // SYM_D_SHADOW
        "POLYGON",          // SYM_D_POLYGON
        "ARROW",            // SYM_D_ARROW
        "DIAMOND",          // SYM_D_DIAMOND
        "OVAL",             // SYM_D_OVAL
        "STRING",           // SYM_D_STRING
        "BOX",              // SYM_D_BOX
        "FLAT_BOX",         // SYM_D_FLAT_BOX
        "CHECK",            // SYM_D_CHECK
        "OPTION",           // SYM_D_OPTION
        "CROSS",            // SYM_D_CROSS
        "RAMP",             // SYM_D_RAMP
        "TAB",              // SYM_D_TAB
        "SHADOW_GAP",       // SYM_D_SHADOW_GAP
        "BOX_GAP",          // SYM_D_BOX_GAP
        "EXTENSION",        // SYM_D_EXTENSION
        "FOCUS",            // SYM_D_FOCUS
        "SLIDER",           // SYM_D_SLIDER
        "ENTRY",            // SYM_D_ENTRY
        "HANDLE",           // SYM_D_HANDLE
        "STEPPER",          // SYM_D_STEPPER

        "TRUE",             // SYM_TRUE
        "FALSE",            // SYM_FALSE

        "TOP",              // SYM_TOP
        "UP",               // SYM_UP
        "BOTTOM",           // SYM_BOTTOM
        "DOWN",             // SYM_DOWN
        "LEFT",             // SYM_LEFT
        "RIGHT",            // SYM_RIGHT

        "NORMAL",           // SYM_NORMAL
        "ACTIVE",           // SYM_ACTIVE
        "PRELIGHT",         // SYM_PRELIGHT
        "SELECTED",         // SYM_SELECTED
        "INSENSITIVE",      // SYM_INSENSITIVE

        "NONE",             // SYM_NONE
        "IN",               // SYM_IN
        "OUT",              // SYM_OUT
        "ETCHED_IN",        // SYM_ETCHED_IN
        "ETCHED_OUT",       // SYM_ETCHED_OUT
        "HORIZONTAL",       // SYM_HORIZONTAL
        "VERTICAL"          // SYM_VERTICAL
    };

    private static class PixmapEngineInfo extends GTKParser.EngineInfo {

        ArrayList pInfos = new ArrayList();

        GTKStyle constructGTKStyle(GTKStyle.GTKStateInfo[] infoArray,
                                   CircularIdentityList props,
                                   Font font,
                                   int xThickness,
                                   int yThickness,
                                   GTKStyle.GTKStockIconInfo[] stockArray) {

            PixmapStyle.Info[] pInfoArray = null;
            if (pInfos.size() != 0) {
                pInfoArray = new PixmapStyle.Info[pInfos.size()];
                pInfoArray = (PixmapStyle.Info[])pInfos.toArray(pInfoArray);
            }

            return new PixmapStyle(infoArray,
                                   props,
                                   font,
                                   xThickness,
                                   yThickness,
                                   stockArray,
                                   pInfoArray);
        }
    }

    private GTKScanner scanner;
    private GTKParser parser;
    private PixmapEngineInfo engineInfo;

    private void registerSymbolsIfNecessary() {
        if (scanner.containsSymbol(symbolNames[0])) {
            return;
        }

        for (int i = 0; i < symbolNames.length; i++) {
            scanner.addSymbol(symbolNames[i], symbolVals[i]);
        }
    }

    int parse(GTKScanner scanner,
              GTKParser parser,
              GTKParser.EngineInfo[] retVal) throws IOException {

        this.scanner = scanner;
        this.parser = parser;

        if (retVal[0] == null) {
            engineInfo = new PixmapEngineInfo();
        } else {
            engineInfo = (PixmapEngineInfo)retVal[0];
        }

        int oldScope = scanner.setScope(uniqueScopeID);
        registerSymbolsIfNecessary();

        PixmapStyle.Info info[] = new PixmapStyle.Info[1];

        int token;

        token = scanner.peekNextToken();
        while (token != GTKScanner.TOKEN_RIGHT_CURLY) {
            switch(token) {
                case SYM_IMAGE:
                    token = parseImage(info);
                    break;
                default:
                    scanner.getToken();
                    token = GTKScanner.TOKEN_RIGHT_CURLY;
                    break;
            }

            if (token != GTKScanner.TOKEN_NONE) {
                return token;
            }

            engineInfo.pInfos.add(info[0]);

            token = scanner.peekNextToken();
        }

        scanner.getToken();

        retVal[0] = engineInfo;

        scanner.setScope(oldScope);
        return GTKScanner.TOKEN_NONE;
    }

    private int parseImage(PixmapStyle.Info[] retVal) throws IOException {
        int token;

        token = scanner.getToken();
        if (token != SYM_IMAGE) {
            return SYM_IMAGE;
        }

        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_LEFT_CURLY) {
            return GTKScanner.TOKEN_LEFT_CURLY;
        }

        PixmapStyle.Info info = new PixmapStyle.Info();

        // to hold the return value from parseFile
        String[] fileName = new String[1];
        
        // to hold the return value from parseStretch
        boolean[] stretch = new boolean[1];
        
        // to hold the return value from parseBorder
        Insets[] insets = new Insets[1];

        token = scanner.peekNextToken();
        while (token != GTKScanner.TOKEN_RIGHT_CURLY) {
            switch(token) {
                case SYM_FUNCTION:
                    token = parseFunction(info);
                    break;
                case SYM_RECOLORABLE:
                    token = parseRecolorable(info);
                    break;
                case SYM_DETAIL:
                    token = parseDetail(info);
                    break;
                case SYM_STATE:
                    token = parseState(info);
                    break;
                case SYM_SHADOW:
                    token = parseShadow(info);
                    break;
                case SYM_GAP_SIDE:
                    token = parseGapSide(info);
                    break;
                case SYM_ARROW_DIRECTION:
                    token = parseArrowDirection(info);
                    break;
                case SYM_ORIENTATION:
                    token = parseOrientation(info);
                    break;
                case SYM_FILE:
                    token = parseFile(fileName);
                    if (token == GTKScanner.TOKEN_NONE) {
                        info.image = fileName[0];
                    }
                    break;
                case SYM_BORDER:
                    token = parseBorder(insets);
                    if (token == GTKScanner.TOKEN_NONE) {
                        info.fileInsets = insets[0];
                    }
                    break;
                case SYM_STRETCH:
                    token = parseStretch(stretch);
                    if (token == GTKScanner.TOKEN_NONE) {
                        info.stretch = stretch[0];
                    }
                    break;
                case SYM_GAP_FILE:
                    token = parseFile(fileName);
                    if (token == GTKScanner.TOKEN_NONE) {
                        info.gapImage = fileName[0];
                    }
                    break;
                case SYM_GAP_BORDER:
                    token = parseBorder(insets);
                    if (token == GTKScanner.TOKEN_NONE) {
                        info.gapInsets = insets[0];
                    }
                    break;
                case SYM_GAP_START_FILE:
                    token = parseFile(fileName);
                    if (token == GTKScanner.TOKEN_NONE) {
                        info.gapStartImage = fileName[0];
                    }
                    break;
                case SYM_GAP_START_BORDER:
                    token = parseBorder(insets);
                    if (token == GTKScanner.TOKEN_NONE) {
                        info.gapStartInsets = insets[0];
                    }
                    break;
                case SYM_GAP_END_FILE:
                    token = parseFile(fileName);
                    if (token == GTKScanner.TOKEN_NONE) {
                        info.gapEndImage = fileName[0];
                    }
                    break;
                case SYM_GAP_END_BORDER:
                    token = parseBorder(insets);
                    if (token == GTKScanner.TOKEN_NONE) {
                        info.gapEndInsets = insets[0];
                    }
                    break;
                case SYM_OVERLAY_FILE:
                    token = parseFile(fileName);
                    if (token == GTKScanner.TOKEN_NONE) {
                        info.overlayImage = fileName[0];
                    }
                    break;
                case SYM_OVERLAY_BORDER:
                    token = parseBorder(insets);
                    if (token == GTKScanner.TOKEN_NONE) {
                        info.overlayInsets = insets[0];
                    }
                    break;
                case SYM_OVERLAY_STRETCH:
                    token = parseStretch(stretch);
                    if (token == GTKScanner.TOKEN_NONE) {
                        info.overlayStretch = stretch[0];
                    }
                    break;
                default:
                    scanner.getToken();
                    token = GTKScanner.TOKEN_RIGHT_CURLY;
                    break;
            }

            if (token != GTKScanner.TOKEN_NONE) {
                return token;
            }

            token = scanner.peekNextToken();
        }

        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_RIGHT_CURLY) {
            return GTKScanner.TOKEN_RIGHT_CURLY;
        }

        // PENDING(shannonh) - may want to do some validation of the
        //                     info before we return it

        retVal[0] = info;
        return GTKScanner.TOKEN_NONE;
    }

    private int parseFunction(PixmapStyle.Info info) throws IOException {
        int token;

        token = scanner.getToken();
        if (token != SYM_FUNCTION) {
            return SYM_FUNCTION;
        }

        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }

        token = scanner.getToken();
        if (token >= SYM_D_HLINE && token <= SYM_D_STEPPER) {
            info.setFunction(symbolNames[token - SYM_IMAGE]);
        }
        
        // PENDING(shannonh) - should we complain if not a valid function?

        return GTKScanner.TOKEN_NONE;
    }

    private int parseRecolorable(PixmapStyle.Info info) throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != SYM_RECOLORABLE) {
            return SYM_RECOLORABLE;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }
        
        token = scanner.getToken();
        if (token == SYM_TRUE) {
            info.recolorable = true;
        } else if (token == SYM_FALSE) {
            info.recolorable = false;
        } else {
            return SYM_TRUE;
        }
        
        return GTKScanner.TOKEN_NONE;
    }
    
    private int parseDetail(PixmapStyle.Info info) throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != SYM_DETAIL) {
            return SYM_DETAIL;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_STRING) {
            return GTKScanner.TOKEN_STRING;
        }

        info.setDetail(scanner.currValue.stringVal);

        return GTKScanner.TOKEN_NONE;
    }

    private int parseState(PixmapStyle.Info info) throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != SYM_STATE) {
            return SYM_STATE;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }
        
        token = scanner.getToken();
        switch(token) {
            case SYM_NORMAL:
                info.componentState = SynthConstants.ENABLED;
                break;
            case SYM_ACTIVE:
                info.componentState = SynthConstants.PRESSED;
                break;
            case SYM_PRELIGHT:
                info.componentState = SynthConstants.MOUSE_OVER;
                break;
            case SYM_SELECTED:
                info.componentState = SynthConstants.SELECTED;
                break;
            case SYM_INSENSITIVE:
                info.componentState = SynthConstants.DISABLED;
                break;
            default:
                return SYM_NORMAL;
        }

        return GTKScanner.TOKEN_NONE;
    }

    private int parseShadow(PixmapStyle.Info info) throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != SYM_SHADOW) {
            return SYM_SHADOW;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }
        
        token = scanner.getToken();
        switch(token) {
            case SYM_NONE:
                info.shadow = GTKConstants.SHADOW_NONE;
                break;
            case SYM_IN:
                info.shadow = GTKConstants.SHADOW_IN;
                break;
            case SYM_OUT:
                info.shadow = GTKConstants.SHADOW_OUT;
                break;
            case SYM_ETCHED_IN:
                info.shadow = GTKConstants.SHADOW_ETCHED_IN;
                break;
            case SYM_ETCHED_OUT:
                info.shadow = GTKConstants.SHADOW_ETCHED_OUT;
                break;
            default:
                return SYM_NONE;
        }

        return GTKScanner.TOKEN_NONE;
    }

    private int parseGapSide(PixmapStyle.Info info) throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != SYM_GAP_SIDE) {
            return SYM_GAP_SIDE;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }
        
        token = scanner.getToken();
        switch(token) {
            case SYM_TOP:
                info.gapSide = GTKConstants.TOP;
                break;
            case SYM_BOTTOM:
                info.gapSide = GTKConstants.BOTTOM;
                break;
            case SYM_LEFT:
                info.gapSide = GTKConstants.LEFT;
                break;
            case SYM_RIGHT:
                info.gapSide = GTKConstants.RIGHT;
                break;
            default:
                return SYM_TOP;
        }

        return GTKScanner.TOKEN_NONE;
    }

    private int parseArrowDirection(PixmapStyle.Info info) throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != SYM_ARROW_DIRECTION) {
            return SYM_ARROW_DIRECTION;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }
        
        token = scanner.getToken();
        switch(token) {
            case SYM_UP:
                info.arrowDirection = GTKConstants.ARROW_UP;
                break;
            case SYM_DOWN:
                info.arrowDirection = GTKConstants.ARROW_DOWN;
                break;
            case SYM_LEFT:
                info.arrowDirection = GTKConstants.ARROW_LEFT;
                break;
            case SYM_RIGHT:
                info.arrowDirection = GTKConstants.ARROW_RIGHT;
                break;
            default:
                return SYM_UP;
        }

        return GTKScanner.TOKEN_NONE;
    }
    
    private int parseOrientation(PixmapStyle.Info info) throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != SYM_ORIENTATION) {
            return SYM_ORIENTATION;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }
        
        token = scanner.getToken();
        switch(token) {
            case SYM_HORIZONTAL:
                info.orientation = GTKConstants.HORIZONTAL;
                break;
            case SYM_VERTICAL:
                info.orientation = GTKConstants.VERTICAL;
                break;
            default:
                return SYM_HORIZONTAL;
        }
        
        return GTKScanner.TOKEN_NONE;
    }

    private int parseFile(String[] retVal) throws IOException {
        int token;

        token = scanner.getToken();
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_STRING) {
            return GTKScanner.TOKEN_STRING;
        }

        retVal[0] = parser.resolvePixmapPath(scanner.currValue.stringVal);

        return GTKScanner.TOKEN_NONE;
    }

    private int parseStretch(boolean[] retVal) throws IOException {
        int token;
        
        token = scanner.getToken();
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }
        
        token = scanner.getToken();
        switch(token) {
            case SYM_TRUE:
                retVal[0] = true;
                break;
            case SYM_FALSE:
                retVal[0] = false;
                break;
            default:
                return SYM_TRUE;
        }
        
        return GTKScanner.TOKEN_NONE;
    }

    private int parseBorder(Insets[] retVal) throws IOException {
        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;
        
        scanner.getToken();
        
        if (scanner.getToken() != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }
        
        if (scanner.getToken() != GTKScanner.TOKEN_LEFT_CURLY) {
            return GTKScanner.TOKEN_LEFT_CURLY;
        }
        
        if (scanner.getToken() != GTKScanner.TOKEN_INT) {
            return GTKScanner.TOKEN_INT;
        }
        
        left = (int)scanner.currValue.longVal;
        
        if (scanner.getToken() != GTKScanner.TOKEN_COMMA) {
            return GTKScanner.TOKEN_COMMA;
        }
        
        if (scanner.getToken() != GTKScanner.TOKEN_INT) {
            return GTKScanner.TOKEN_INT;
        }
        
        right = (int)scanner.currValue.longVal;
        
        if (scanner.getToken() != GTKScanner.TOKEN_COMMA) {
            return GTKScanner.TOKEN_COMMA;
        }
        
        if (scanner.getToken() != GTKScanner.TOKEN_INT) {
            return GTKScanner.TOKEN_INT;
        }
        
        top = (int)scanner.currValue.longVal;
        
        if (scanner.getToken() != GTKScanner.TOKEN_COMMA) {
            return GTKScanner.TOKEN_COMMA;
        }
        
        if (scanner.getToken() != GTKScanner.TOKEN_INT) {
            return GTKScanner.TOKEN_INT;
        }
        
        bottom = (int)scanner.currValue.longVal;
        
        if (scanner.getToken() != GTKScanner.TOKEN_RIGHT_CURLY) {
            return GTKScanner.TOKEN_RIGHT_CURLY;
        }

        retVal[0] = new Insets(top, left, bottom, right);

        return GTKScanner.TOKEN_NONE;
    }

}

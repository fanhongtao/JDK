/*
 * @(#)BlueprintEngineParser.java	1.12 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import java.io.IOException;
import java.util.ArrayList;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Color;
import javax.swing.plaf.synth.SynthConstants;

/**
 * A parser for the "blueprint" engine sections in GTK rc theme files.
 *
 * @author  Shannon Hickey
 * @version 1.12 12/19/03
 */
class BlueprintEngineParser extends GTKEngineParser {

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
    private static final int SYM_PARENT_TYPE       = SYM_ORIENTATION + 1;
    private static final int SYM_COLORIZE_COLOR    = SYM_PARENT_TYPE + 1;
    private static final int SYM_ICON_COLORIZE     = SYM_COLORIZE_COLOR + 1;
    private static final int SYM_ICON_COLORIZE_ANCESTOR_TYPE = SYM_ICON_COLORIZE + 1;
    private static final int SYM_USE_AS_BKG_MASK = SYM_ICON_COLORIZE_ANCESTOR_TYPE + 1;
    private static final int SYM_OVERLAY_RECOLORABLE = SYM_USE_AS_BKG_MASK + 1;
    private static final int SYM_OVERLAY_COLORIZE_COLOR = SYM_OVERLAY_RECOLORABLE + 1;

    // SYM_D_HLINE and SYM_D_STEPPER are assumed to begin and end the function symbols.
    // When adding new symbols, only function symbols should be added between them.
    private static final int SYM_D_HLINE           = SYM_OVERLAY_COLORIZE_COLOR + 1;
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
    private static final int SYM_D_LAYOUT          = SYM_D_HANDLE + 1;
    private static final int SYM_D_BACKGROUND      = SYM_D_LAYOUT + 1;
    private static final int SYM_D_STEPPER         = SYM_D_BACKGROUND + 1;
    // end function symbols

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
        SYM_OVERLAY_STRETCH, SYM_ARROW_DIRECTION, SYM_ORIENTATION,
        SYM_PARENT_TYPE, SYM_COLORIZE_COLOR, SYM_ICON_COLORIZE,
        SYM_ICON_COLORIZE_ANCESTOR_TYPE, SYM_USE_AS_BKG_MASK,
        SYM_OVERLAY_RECOLORABLE, SYM_OVERLAY_COLORIZE_COLOR, SYM_D_HLINE,
        SYM_D_VLINE, SYM_D_SHADOW, SYM_D_POLYGON, SYM_D_ARROW, SYM_D_DIAMOND,
        SYM_D_OVAL, SYM_D_STRING, SYM_D_BOX, SYM_D_FLAT_BOX, SYM_D_CHECK,
        SYM_D_OPTION, SYM_D_CROSS, SYM_D_RAMP, SYM_D_TAB, SYM_D_SHADOW_GAP,
        SYM_D_BOX_GAP, SYM_D_EXTENSION, SYM_D_FOCUS, SYM_D_SLIDER, SYM_D_ENTRY,
        SYM_D_HANDLE, SYM_D_LAYOUT, SYM_D_BACKGROUND,        
        SYM_D_STEPPER, SYM_TRUE, SYM_FALSE, SYM_TOP, SYM_UP,
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
        "parent_type",      // SYM_PARENT_TYPE
        "colorize_color",   // SYM_COLORIZE_COLOR
        "icon_colorize",    // SYM_ICON_COLORIZE
        "icon_colorize_ancestor_type",    // SYM_ICON_COLORIZE_ANCESTOR_TYPE
        "use_as_bkg_mask",        // SYM_USE_AS_BKG_MASK
        "overlay_recolorable",    // SYM_OVERLAY_RECOLORABLE
        "overlay_colorize_color", // SYM_OVERLAY_COLORIZE_COLOR

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
        "LAYOUT",           // SYM_D_LAYOUT
        "BACKGROUND",       // SYM_D_BACKGROUND
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

    private static class BlueprintEngineInfo extends GTKParser.EngineInfo {

        ArrayList pInfos = new ArrayList();
        boolean iconColorize = false;
        ArrayList iconAncestorTypes = null;
        Color colorizeColor = null;

        GTKStyle constructGTKStyle(GTKStyle.GTKStateInfo[] infoArray,
                                   CircularIdentityList props,
                                   Font font,
                                   int xThickness,
                                   int yThickness,
                                   GTKStyle.GTKStockIconInfo[] stockArray) {

            BlueprintStyle.Info[] pInfoArray = null;
            if (pInfos.size() != 0) {
                pInfoArray = new BlueprintStyle.Info[pInfos.size()];
                pInfoArray = (BlueprintStyle.Info[])pInfos.toArray(pInfoArray);
            }

            String[] ancestorArray = null;
            if (iconAncestorTypes != null && iconAncestorTypes.size() != 0) {
                ancestorArray = new String[iconAncestorTypes.size()];
                ancestorArray = (String[])iconAncestorTypes.toArray(ancestorArray);
            }

            return new BlueprintStyle(infoArray,
                                   props,
                                   font,
                                   xThickness,
                                   yThickness,
                                   stockArray,
                                   pInfoArray,
                                   iconColorize,
                                   ancestorArray,
                                   colorizeColor);
        }
    }

    private GTKScanner scanner;
    private GTKParser parser;
    private BlueprintEngineInfo engineInfo;

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
            engineInfo = new BlueprintEngineInfo();
        } else {
            engineInfo = (BlueprintEngineInfo)retVal[0];
        }

        int oldScope = scanner.setScope(uniqueScopeID);
        registerSymbolsIfNecessary();

        BlueprintStyle.Info info[] = new BlueprintStyle.Info[1];

        int token;

        token = scanner.peekNextToken();
        while (token != GTKScanner.TOKEN_RIGHT_CURLY) {
            
            info[0] = null;
            
            switch(token) {
                case SYM_IMAGE:
                    token = parseImage(info);
                    break;
                case SYM_COLORIZE_COLOR:
                    Color[] col = new Color[1];
                    token = parseColorizeColor(col);
                    if (token == GTKScanner.TOKEN_NONE) {
                        engineInfo.colorizeColor = col[0];
                    }
                    break;
                case SYM_ICON_COLORIZE:
                    token = parseIconColorize(engineInfo);
                    break;
                case SYM_ICON_COLORIZE_ANCESTOR_TYPE:
                    // consume token
                    scanner.getToken();

                    if (engineInfo.iconAncestorTypes == null) {
                        engineInfo.iconAncestorTypes = new ArrayList();
                    }

                    token = parseStringList(engineInfo.iconAncestorTypes);
                    break;
                default:
                    scanner.getToken();
                    token = GTKScanner.TOKEN_RIGHT_CURLY;
                    break;
            }

            if (token != GTKScanner.TOKEN_NONE) {
                return token;
            }

            if (info[0] != null) {
                engineInfo.pInfos.add(info[0]);
            }

            token = scanner.peekNextToken();
        }

        scanner.getToken();

        retVal[0] = engineInfo;

        scanner.setScope(oldScope);
        return GTKScanner.TOKEN_NONE;
    }

    private int parseImage(BlueprintStyle.Info[] retVal) throws IOException {
        int token;

        token = scanner.getToken();
        if (token != SYM_IMAGE) {
            return SYM_IMAGE;
        }

        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_LEFT_CURLY) {
            return GTKScanner.TOKEN_LEFT_CURLY;
        }

        BlueprintStyle.Info info = new BlueprintStyle.Info();

        // to hold the return value from parseFile
        String[] fileName = new String[1];
        
        // to hold the return value from parseStretch
        // and parseRecolorable
        boolean[] bool = new boolean[1];
        
        // to hold the return value from parseBorder
        Insets[] insets = new Insets[1];

        // to hold the return value from parseColorizeColor
        Color[] col = new Color[1];

        token = scanner.peekNextToken();
        while (token != GTKScanner.TOKEN_RIGHT_CURLY) {
            switch(token) {
                case SYM_FUNCTION:
                    token = parseFunction(info);
                    break;
                case SYM_RECOLORABLE:
                    token = parseRecolorable(bool);
                    if (token == GTKScanner.TOKEN_NONE) {
                        info.recolorable = bool[0];
                    }
                    break;
                case SYM_OVERLAY_RECOLORABLE:
                    token = parseRecolorable(bool);
                    if (token == GTKScanner.TOKEN_NONE) {
                        info.overlayRecolorable = bool[0];
                    }
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
                    token = parseStretch(bool);
                    if (token == GTKScanner.TOKEN_NONE) {
                        info.stretch = bool[0];
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
                    token = parseStretch(bool);
                    if (token == GTKScanner.TOKEN_NONE) {
                        info.overlayStretch = bool[0];
                    }
                    break;
                case SYM_PARENT_TYPE:
                    // consume token
                    scanner.getToken();

                    if (info.parentTypeList == null) {
                        info.parentTypeList = new ArrayList();
                    }

                    token = parseStringList(info.parentTypeList);
                    break;
                case SYM_COLORIZE_COLOR:
                    token = parseColorizeColor(col);
                    if (token == GTKScanner.TOKEN_NONE) {
                        info.colorizeColor = col[0];
                    }
                    break;
                case SYM_OVERLAY_COLORIZE_COLOR:
                    token = parseColorizeColor(col);
                    if (token == GTKScanner.TOKEN_NONE) {
                        info.overlayColorizeColor = col[0];
                    }
                    break;
                case SYM_USE_AS_BKG_MASK:
                    token = parseUseAsBkgMask(info);
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

    private int parseFunction(BlueprintStyle.Info info) throws IOException {
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

    private int parseRecolorable(boolean[] retVal) throws IOException {
        int token;
        
        scanner.getToken();
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }
        
        token = scanner.getToken();
        if (token == SYM_TRUE) {
            retVal[0] = true;
        } else if (token == SYM_FALSE) {
            retVal[0] = false;
        } else {
            return SYM_TRUE;
        }
        
        return GTKScanner.TOKEN_NONE;
    }
    
    private int parseDetail(BlueprintStyle.Info info) throws IOException {
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

    private int parseState(BlueprintStyle.Info info) throws IOException {
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

    private int parseShadow(BlueprintStyle.Info info) throws IOException {
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

    private int parseGapSide(BlueprintStyle.Info info) throws IOException {
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

    private int parseArrowDirection(BlueprintStyle.Info info) throws IOException {
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
    
    private int parseOrientation(BlueprintStyle.Info info) throws IOException {
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

    private int parseColorizeColor(Color[] retVal) throws IOException {
        int token;

        scanner.getToken();
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }
        
        token = scanner.getToken();

        switch(token) {
            case GTKScanner.TOKEN_LEFT_CURLY:
                int red;
                int green;
                int blue;
                int alpha = 255;

                token = scanner.getToken();
                if (token == GTKScanner.TOKEN_INT) {
                    red = intColorVal(scanner.currValue.longVal);
                } else if (token == GTKScanner.TOKEN_FLOAT) {
                    red = intColorVal(scanner.currValue.doubleVal);
                } else {
                    return GTKScanner.TOKEN_FLOAT;
                }
                
                token = scanner.getToken();
                if (token != GTKScanner.TOKEN_COMMA) {
                    return GTKScanner.TOKEN_COMMA;
                }

                token = scanner.getToken();
                if (token == GTKScanner.TOKEN_INT) {
                    green = intColorVal(scanner.currValue.longVal);
                } else if (token == GTKScanner.TOKEN_FLOAT) {
                    green = intColorVal(scanner.currValue.doubleVal);
                } else {
                    return GTKScanner.TOKEN_FLOAT;
                }

                token = scanner.getToken();
                if (token != GTKScanner.TOKEN_COMMA) {
                    return GTKScanner.TOKEN_COMMA;
                }

                token = scanner.getToken();
                if (token == GTKScanner.TOKEN_INT) {
                    blue = intColorVal(scanner.currValue.longVal);
                } else if (token == GTKScanner.TOKEN_FLOAT) {
                    blue = intColorVal(scanner.currValue.doubleVal);
                } else {
                    return GTKScanner.TOKEN_FLOAT;
                }

                token = scanner.getToken();
                if (token == GTKScanner.TOKEN_COMMA) {
                    token = scanner.getToken();
                    if (token == GTKScanner.TOKEN_INT) {
                        alpha = intColorVal(scanner.currValue.longVal);
                    } else if (token == GTKScanner.TOKEN_FLOAT) {
                        alpha = intColorVal(scanner.currValue.doubleVal);
                    } else {
                        return GTKScanner.TOKEN_FLOAT;
                    }
                    
                    token = scanner.getToken();
                }

                if (token != GTKScanner.TOKEN_RIGHT_CURLY) {
                    return GTKScanner.TOKEN_RIGHT_CURLY;
                }

                retVal[0] = new Color(red, green, blue, alpha);

                break;
            case GTKScanner.TOKEN_STRING:
                Color color = parseColorString(scanner.currValue.stringVal);

                if (color == null) {
                    scanner.printMessage("Invalid color constant '" +
                                              scanner.currValue.stringVal
                                              + "'", false);
                    return GTKScanner.TOKEN_STRING;
                }

                retVal[0] = color;

                break;
            default:
                return GTKScanner.TOKEN_STRING;
        }
        
        return GTKScanner.TOKEN_NONE;
    }

    private static Color parseColorString(String str) {
        if (str.charAt(0) == '#') {
            str = str.substring(1);

            int i = str.length();

            if (i < 3 || i > 12 || (i % 3) != 0) {
                return null;
            }

            i /= 3;

            int r;
            int g;
            int b;

            try {
                r = Integer.parseInt(str.substring(0, i), 16);
                g = Integer.parseInt(str.substring(i, i * 2), 16);
                b = Integer.parseInt(str.substring(i * 2, i * 3), 16);
            } catch (NumberFormatException nfe) {
                return null;
            }

            if (i == 4) {
                return new Color(r / 65535.0f, g / 65535.0f, b / 65535.0f);
            } else if (i == 1) {
                return new Color(r / 15.0f, g / 15.0f, b / 15.0f);
            } else if (i == 2) {
                return new Color(r, g, b);
            } else {
                return new Color(r / 4095.0f, g / 4095.0f, b / 4095.0f);
            }
        } else {
            return XColors.lookupColor(str);
        }
    }

    private static int intColorVal(long col) {
        int color = (int)Math.max(Math.min(col, 255), 0);
        return color;
    }

    private static int intColorVal(double col) {
        float color = (float)Math.max(Math.min(col, 1.0f), 0.0f);
        return (int)(color * 255);
    }

    private int parseIconColorize(BlueprintEngineInfo engineInfo) throws IOException {
        int token;

        token = scanner.getToken();
        if (token != SYM_ICON_COLORIZE) {
            return SYM_ICON_COLORIZE;
        }

        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }

        token = scanner.getToken();
        if (token == SYM_TRUE) {
            engineInfo.iconColorize = true;
        } else if (token == SYM_FALSE) {
            engineInfo.iconColorize = false;
        } else {
            return SYM_TRUE;
        }

        return GTKScanner.TOKEN_NONE;
    }

    /**
     * Helper method for parsing string list assignments such as the following:
     * = {"FOO", "BAR", "SJH"}
     * The scanner is expected to start returning tokens at the equal sign when
     * this method is invoked. It will parse the strings in the list into the
     * given <code>ArrayList</code> parameter.
     *
     * NOTE: The <code>Strings</code> that are added to the
     *       <code>ArrayList</code> will have been <code>intern()</code>ed
     *       first.
     *
     * @return  <code>GTKScanner.TOKEN_NONE</code> if the parse was successful,
     *          otherwise the token that was expected but not received.
     */
    private int parseStringList(ArrayList list) throws IOException {
        int token;

        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }

        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_LEFT_CURLY) {
            return GTKScanner.TOKEN_LEFT_CURLY;
        }
        
        token = scanner.getToken();
        while (token == GTKScanner.TOKEN_STRING) {
            list.add(scanner.currValue.stringVal.intern());
            
            token = scanner.getToken();
            
            if (token == GTKScanner.TOKEN_RIGHT_CURLY) {
                continue;
            }
            
            if (token != GTKScanner.TOKEN_COMMA) {
                return GTKScanner.TOKEN_COMMA;
            }
            
            token = scanner.getToken();
        }
        
        if (token != GTKScanner.TOKEN_RIGHT_CURLY) {
            return GTKScanner.TOKEN_RIGHT_CURLY;
        }
        
        return GTKScanner.TOKEN_NONE;
    }

    private int parseUseAsBkgMask(BlueprintStyle.Info info) throws IOException {
        int token;

        token = scanner.getToken();
        if (token != SYM_USE_AS_BKG_MASK) {
            return SYM_USE_AS_BKG_MASK;
        }

        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }

        token = scanner.getToken();
        switch(token) {
            case SYM_TRUE:
                info.useAsBkgMask = true;
                break;
            case SYM_FALSE:
                info.useAsBkgMask = false;
                break;
            default:
                return SYM_TRUE;
        }
        
        return GTKScanner.TOKEN_NONE;
    }

}

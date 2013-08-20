/*
 * @(#)BluecurveEngineParser.java	1.5 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import java.io.IOException;
import java.util.ArrayList;
import java.awt.Font;
import java.awt.Insets;

/**
 * A parser for the "bluecurve" engine sections in GTK rc theme files.
 *
 * @author  Shannon Hickey
 * @version 1.5 12/19/03
 */
class BluecurveEngineParser extends GTKEngineParser {

    private static final int SYM_CONTRAST = GTKScanner.TOKEN_LAST + 1;
    private static final int SYM_WIDE     = SYM_CONTRAST + 1;
    private static final int SYM_TRUE     = SYM_WIDE + 1;
    private static final int SYM_FALSE    = SYM_TRUE + 1;

    private static final int[] symbolVals = {
        SYM_CONTRAST, SYM_WIDE, SYM_TRUE, SYM_FALSE
    };

    private static final String[] symbolNames = {
        "contrast",    // SYM_CONTRAST
        "wide",        // SYM_WIDE
        "TRUE",        // SYM_TRUE
        "FALSE"        // SYM_FALSE
    };

    private static class BluecurveEngineInfo extends GTKParser.EngineInfo {
        GTKStyle constructGTKStyle(GTKStyle.GTKStateInfo[] infoArray,
                                   CircularIdentityList props,
                                   Font font,
                                   int xThickness,
                                   int yThickness,
                                   GTKStyle.GTKStockIconInfo[] stockArray) {
            return new BluecurveStyle(infoArray,
                                      props,
                                      font,
                                      xThickness,
                                      yThickness,
                                      stockArray);
        }
    }

    private GTKScanner scanner;
    private GTKParser parser;
    private BluecurveEngineInfo engineInfo;

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
            engineInfo = new BluecurveEngineInfo();
        } else {
            engineInfo = (BluecurveEngineInfo)retVal[0];
        }

        int oldScope = scanner.setScope(uniqueScopeID);
        registerSymbolsIfNecessary();

        // At this time we don't know how bluecurve allows
        // itself to be customized, so we'll just skip over
        // the block.
        int token;
        int curlys = 1;

        while (curlys > 0) {
            token = scanner.getToken();
            switch(token) {
                case GTKScanner.TOKEN_EOF:
                case GTKScanner.TOKEN_ERROR:
                    return GTKScanner.TOKEN_RIGHT_CURLY;
                case GTKScanner.TOKEN_LEFT_CURLY:
                    curlys++;
                    break;
                case GTKScanner.TOKEN_RIGHT_CURLY:
                    curlys--;
                    break;
                default:
                    // ignore
            }
        }

        retVal[0] = engineInfo;

        scanner.setScope(oldScope);
        return GTKScanner.TOKEN_NONE;
    }

}

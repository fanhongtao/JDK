/*
 * @(#)GTKParser.java	1.78 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.util.regex.PatternSyntaxException;
import javax.swing.plaf.ColorUIResource;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;

/**
 * @author  Shannon Hickey
 * @version 1.78 01/23/03
 */
class GTKParser {
    
    private ArrayList freeScanners = new ArrayList();
    
    private HashMap namedStyles = new HashMap();
    
    private ArrayList assignments = new ArrayList();

    private HashMap settings = new HashMap();

    private File[] pixmapPaths = null;
    
    private ArrayList dirStack = new ArrayList();
    
    private HashMap engineParsers = new HashMap();

    // Register parsers here for now. Later we can add methods to register
    // new parser classes.
    {
        engineParsers.put("pixmap", "com.sun.java.swing.plaf.gtk.PixmapEngineParser");
        engineParsers.put("bluecurve", "com.sun.java.swing.plaf.gtk.BluecurveEngineParser");
    }
    
    private GTKScanner scanner;

    private final String CWD = (String)AccessController.doPrivileged(
                                           new GetPropertyAction("user.dir"));

    static class Symbol {
        
        public String name;
        public int val;
        
        public Symbol(String name, int val) {
            this.name = name;
            this.val = val;
        }
    }

    private static final Symbol SYMBOL_INVALID = new Symbol("invalid", GTKScanner.TOKEN_LAST);
    private static final Symbol SYMBOL_INCLUDE = new Symbol("include", SYMBOL_INVALID.val + 1);
    private static final Symbol SYMBOL_NORMAL = new Symbol("NORMAL", SYMBOL_INCLUDE.val + 1);
    private static final Symbol SYMBOL_ACTIVE = new Symbol("ACTIVE", SYMBOL_NORMAL.val + 1);
    private static final Symbol SYMBOL_PRELIGHT = new Symbol("PRELIGHT", SYMBOL_ACTIVE.val + 1);
    private static final Symbol SYMBOL_SELECTED = new Symbol("SELECTED", SYMBOL_PRELIGHT.val + 1);
    private static final Symbol SYMBOL_INSENSITIVE = new Symbol("INSENSITIVE", SYMBOL_SELECTED.val + 1);
    private static final Symbol SYMBOL_FG = new Symbol("fg", SYMBOL_INSENSITIVE.val + 1);
    private static final Symbol SYMBOL_BG = new Symbol("bg", SYMBOL_FG.val + 1);
    private static final Symbol SYMBOL_TEXT = new Symbol("text", SYMBOL_BG.val + 1);
    private static final Symbol SYMBOL_BASE = new Symbol("base", SYMBOL_TEXT.val + 1);
    private static final Symbol SYMBOL_XTHICKNESS = new Symbol("xthickness", SYMBOL_BASE.val + 1);
    private static final Symbol SYMBOL_YTHICKNESS = new Symbol("ythickness", SYMBOL_XTHICKNESS.val + 1);
    private static final Symbol SYMBOL_FONT = new Symbol("font", SYMBOL_YTHICKNESS.val + 1);
    private static final Symbol SYMBOL_FONTSET = new Symbol("fontset", SYMBOL_FONT.val + 1);
    private static final Symbol SYMBOL_FONT_NAME = new Symbol("font_name", SYMBOL_FONTSET.val + 1);
    private static final Symbol SYMBOL_BG_PIXMAP = new Symbol("bg_pixmap", SYMBOL_FONT_NAME.val + 1);
    private static final Symbol SYMBOL_PIXMAP_PATH = new Symbol("pixmap_path", SYMBOL_BG_PIXMAP.val + 1);
    private static final Symbol SYMBOL_STYLE = new Symbol("style", SYMBOL_PIXMAP_PATH.val + 1);
    private static final Symbol SYMBOL_BINDING = new Symbol("binding", SYMBOL_STYLE.val + 1);
    private static final Symbol SYMBOL_BIND = new Symbol("bind", SYMBOL_BINDING.val + 1);
    private static final Symbol SYMBOL_WIDGET = new Symbol("widget", SYMBOL_BIND.val + 1);
    private static final Symbol SYMBOL_WIDGET_CLASS = new Symbol("widget_class", SYMBOL_WIDGET.val + 1);
    private static final Symbol SYMBOL_CLASS = new Symbol("class", SYMBOL_WIDGET_CLASS.val + 1);
    private static final Symbol SYMBOL_LOWEST = new Symbol("lowest", SYMBOL_CLASS.val + 1);
    private static final Symbol SYMBOL_GTK = new Symbol("gtk", SYMBOL_LOWEST.val + 1);
    private static final Symbol SYMBOL_APPLICATION = new Symbol("application", SYMBOL_GTK.val + 1);
    private static final Symbol SYMBOL_THEME = new Symbol("theme", SYMBOL_APPLICATION.val + 1);
    private static final Symbol SYMBOL_RC = new Symbol("rc", SYMBOL_THEME.val + 1);
    private static final Symbol SYMBOL_HIGHEST = new Symbol("highest", SYMBOL_RC.val + 1);
    private static final Symbol SYMBOL_ENGINE = new Symbol("engine", SYMBOL_HIGHEST.val + 1);
    private static final Symbol SYMBOL_MODULE_PATH = new Symbol("module_path", SYMBOL_ENGINE.val + 1);
    private static final Symbol SYMBOL_IM_MODULE_PATH = new Symbol("im_module_path", SYMBOL_MODULE_PATH.val + 1);
    private static final Symbol SYMBOL_IM_MODULE_FILE = new Symbol("im_module_file", SYMBOL_IM_MODULE_PATH.val + 1);
    private static final Symbol SYMBOL_STOCK = new Symbol("stock", SYMBOL_IM_MODULE_FILE.val + 1);
    private static final Symbol SYMBOL_LTR = new Symbol("LTR", SYMBOL_STOCK.val + 1);
    private static final Symbol SYMBOL_RTL = new Symbol("RTL", SYMBOL_LTR.val + 1);
    private static final Symbol SYMBOL_LAST = new Symbol("last", SYMBOL_RTL.val + 1);
    
    private static final Symbol[] symbols = {
        SYMBOL_INCLUDE, SYMBOL_NORMAL, SYMBOL_ACTIVE, SYMBOL_PRELIGHT,
        SYMBOL_SELECTED, SYMBOL_INSENSITIVE, SYMBOL_FG, SYMBOL_BG,
        SYMBOL_TEXT, SYMBOL_BASE, SYMBOL_XTHICKNESS, SYMBOL_YTHICKNESS,
        SYMBOL_FONT, SYMBOL_FONTSET, SYMBOL_FONT_NAME, SYMBOL_BG_PIXMAP,
        SYMBOL_PIXMAP_PATH, SYMBOL_STYLE, SYMBOL_BINDING, SYMBOL_BIND,
        SYMBOL_WIDGET, SYMBOL_WIDGET_CLASS, SYMBOL_CLASS, SYMBOL_LOWEST,
        SYMBOL_GTK, SYMBOL_APPLICATION, SYMBOL_THEME, SYMBOL_RC,
        SYMBOL_HIGHEST, SYMBOL_ENGINE, SYMBOL_MODULE_PATH,
        SYMBOL_IM_MODULE_FILE, SYMBOL_STOCK, SYMBOL_LTR, SYMBOL_RTL
    };
    
    private static class StyleInfo {
        String name;
        
        static final int NUM_STATES = 5;
        
        static final int NORMAL = 0;
        static final int PRELIGHT = 1;
        static final int ACTIVE = 2;
        static final int INSENSITIVE = 3;
        static final int SELECTED = 4;
        
        Color[] fg = new Color[NUM_STATES];
        Color[] bg = new Color[NUM_STATES];
        Color[] text = new Color[NUM_STATES];
        Color[] base = new Color[NUM_STATES];
        String[] bgPixmapName = new String[NUM_STATES];
        
        Font font = null;
        
        int xThickness = GTKStyle.UNDEFINED_THICKNESS;
        int yThickness = GTKStyle.UNDEFINED_THICKNESS;

        // An array of HashMaps. The first HashMap is for stock
        // icons defined in this style. The other elements are
        // those inherited from parent.
        ArrayList stocks = null;
        
        CircularIdentityList props = null;
        
        EngineInfo engineInfo = null;

        private GTKStyle cachedStyle = null;
        private static GTKStyle EMPTY_STYLE = new GTKStyle();
        
        StyleInfo(String name) {
            this.name = name;
        }
        
        private void initStocksIfNecessary() {
            if (stocks == null) {
                stocks = new ArrayList();
                // for stock icons defined in this style
                stocks.add(new HashMap());
            }
        }

        void addStockItem(String id, GTKStyle.GTKIconSource[] sources) {
            initStocksIfNecessary();
            
            GTKStyle.GTKStockIconInfo iconInfo = new GTKStyle.GTKStockIconInfo(id, sources);
            
            HashMap map = (HashMap)stocks.get(0);
            map.put(id, iconInfo);                
        }
        
        void addProperty(String klass, String prop, Object value) {
            if (props == null) {
                props = new CircularIdentityList();
            }
            
            CircularIdentityList subList = (CircularIdentityList)props.get(klass);
            
            if (subList == null) {
                subList = new CircularIdentityList();
                props.set(klass, subList);
            }
            
            subList.set(prop, value);
        }
        
        void copyDataFrom(StyleInfo other) {
            for (int i = 0; i < NUM_STATES; i++) {
                fg[i] = other.fg[i];
                bg[i] = other.bg[i];
                text[i] = other.text[i];
                base[i] = other.base[i];
                bgPixmapName[i] = other.bgPixmapName[i];
            }

            xThickness = other.xThickness;
            yThickness = other.yThickness;
            font = other.font;
            
            if (other.stocks != null) {
                initStocksIfNecessary();
                stocks.addAll(other.stocks);
            }
            
            if (props == null) {
                props = GTKStyle.cloneClassSpecificValues(other.props);
            } else {
                GTKStyle.addClassSpecificValues(other.props, props);
            }
        }
        
        GTKStyle toGTKStyle() {
            if (cachedStyle != null) {
                return cachedStyle;
            }
            
            ArrayList stateInfos = new ArrayList();
            
            for (int i = 0; i < NUM_STATES; i++) {
                Color[] colors = null;

                if (fg[i] != null
                        || bg[i] != null
                        || text[i] != null
                        || base[i] != null) {
                    colors = new Color[GTKColorType.MAX_COUNT];
                    colors[GTKColorType.FOREGROUND.getID()] = fg[i];
                    colors[GTKColorType.BACKGROUND.getID()] = bg[i];
                    colors[GTKColorType.TEXT_FOREGROUND.getID()] = text[i];
                    colors[GTKColorType.TEXT_BACKGROUND.getID()] = base[i];
                }
                
                if (colors != null || bgPixmapName[i] != null) {
                    GTKStyle.GTKStateInfo stateInfo =
                       new GTKStyle.GTKStateInfo(toSynthState(i),
                                                 null, null, null,
                                                 colors, bgPixmapName[i]);
                    stateInfos.add(stateInfo);
                }
            }
            
            GTKStyle.GTKStateInfo[] infoArray = null;
            if (stateInfos.size() != 0) {
                infoArray = new GTKStyle.GTKStateInfo[stateInfos.size()];
                infoArray = (GTKStyle.GTKStateInfo[])stateInfos.toArray(infoArray);
            }

            GTKStyle.GTKStockIconInfo[] stockArray = stocksToArray();

            // if this style has engine information, delegate the creation
            if (engineInfo != null) {
                cachedStyle = engineInfo.constructGTKStyle(infoArray,
                                                           props,
                                                           font,
                                                           xThickness,
                                                           yThickness,
                                                           stockArray);
            // otherwise, create a regular GTKStyle
            } else if (infoArray != null
                           || stockArray != null
                           || props != null
                           || font != null
                           || xThickness != GTKStyle.UNDEFINED_THICKNESS
                           || yThickness != GTKStyle.UNDEFINED_THICKNESS) {
                cachedStyle = new GTKStyle(infoArray,
                                           props,
                                           font,
                                           xThickness,
                                           yThickness,
                                           stockArray);
            } else {
                cachedStyle = EMPTY_STYLE;
            }

            return cachedStyle;
        }

        private GTKStyle.GTKStockIconInfo[] stocksToArray() {
            if (stocks == null) {
                return null;
            }
            
            ArrayList tmpList = new ArrayList();
            
            HashMap[] maps = new HashMap[stocks.size()];
            maps = (HashMap[])stocks.toArray(maps);
            
            for (int i = 0; i < maps.length; i++) {
                tmpList.addAll(maps[i].values());
            }
            
            GTKStyle.GTKStockIconInfo[] retVal = new GTKStyle.GTKStockIconInfo[tmpList.size()];
            retVal = (GTKStyle.GTKStockIconInfo[])tmpList.toArray(retVal);
            
            return retVal;
        }

        private static int toSynthState(int ourState) {
            switch(ourState) {
                case NORMAL: return SynthConstants.ENABLED;
                case PRELIGHT: return SynthConstants.MOUSE_OVER;
                case ACTIVE: return SynthConstants.PRESSED;
                case INSENSITIVE: return SynthConstants.DISABLED;
                case SELECTED: return SynthConstants.SELECTED;
            }
            
            // should not happen
            return SynthConstants.ENABLED;
        }
    }

    static abstract class EngineInfo {
        private String engineName;
        
        abstract GTKStyle constructGTKStyle(GTKStyle.GTKStateInfo[] infoArray,
                                            CircularIdentityList props,
                                            Font font,
                                            int xThickness,
                                            int yThickness,
                                            GTKStyle.GTKStockIconInfo[] stockArray);
    }

    private static class Assignment {
        int type;
        String pattern;
        StyleInfo info;
        
        Assignment(int type, String pattern, StyleInfo info) {
            this.type = type;
            this.pattern = pattern;
            this.info = info;
        }
        
        public String toString() {
            String sVal = "";
            
            switch(type) {
                case GTKStyleFactory.WIDGET: sVal = "widget, "; break;
                case GTKStyleFactory.WIDGET_CLASS: sVal = "widget_class, "; break;
                case GTKStyleFactory.CLASS: sVal = "class, "; break;
            }
            
            sVal += pattern + ", ";
            sVal += info.name;
            
            return sVal;
        }
    }

    private static Symbol getSymbol(int symbol) {
        if (symbol > SYMBOL_INVALID.val && symbol < SYMBOL_LAST.val) {
            for (int i = 0; i < symbols.length; i++) {
                if (symbols[i].val == symbol) {
                    return symbols[i];
                }
            }
        }
        
        return null;
    }

    public GTKParser() {
        freeScanners.add(createScanner());
    }
    
    public void parseString(String str) throws IOException {
        StringReader reader = new StringReader(str);
        parseReader(reader, "-");
    }
    
    public void parseFile(File file, String name) throws IOException {
        if (!file.canRead() || !file.isFile()) {
            return;
        }
        
        File parent = file.getParentFile();
        if (parent == null) {
            parent = new File(CWD);
        }
        
        dirStack.add(parent);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            parseReader(reader, name);
        } finally {
            dirStack.remove(dirStack.size() - 1);
        }

        // PENDING(shannonh) - This is where we should look up and parse
        //                     the locale-specific version of the file.
    }

    private void parseReader(Reader reader, String name) throws IOException {
        int len = freeScanners.size();
        
        if (len == 0) {
            scanner = createScanner();
        } else {
            scanner = (GTKScanner)freeScanners.remove(len - 1);
        }
        
        scanner.scanReader(reader, name);
        
        try {
            parseCurrent();
        } finally {
            scanner.clearScanner();
            freeScanners.add(scanner);
        }
    }
    
    private static GTKScanner createScanner() {
        GTKScanner scanner = new GTKScanner();

        // configure scanner for GTK rc files
        scanner.caseSensitive = true;
        scanner.scanBinary = true;
        scanner.scanHexDollar = true;
        scanner.symbol2Token = true;
        
        for (int i = 0; i < symbols.length; i++) {
            scanner.addSymbol(symbols[i].name, symbols[i].val);
        }
        
        return scanner;
    }
    
    public void loadStylesInto(GTKStyleFactory factory) {
        Assignment[] assigns = new Assignment[assignments.size()];
        assigns = (Assignment[])assignments.toArray(assigns);
        
        for (int i = 0; i < assigns.length; i++) {
            Assignment assign = assigns[i];
            GTKStyle style = assign.info.toGTKStyle();
            
            if (style != StyleInfo.EMPTY_STYLE) {
                try {
                    factory.addStyle(style, assign.pattern, assign.type);
                } catch (PatternSyntaxException pse) {
                    // should not happen
                }
            }
        }
    }

    public HashMap getGTKSettings() {
        return settings;
    }

    public void clearParser() {
        namedStyles.clear();
        settings.clear();
        assignments.clear();
        dirStack.clear();
        pixmapPaths = null;
    }



//------------------------- Parsing Methods ------------------------------//

    private void parseCurrent() throws IOException {
        while (true) {
            if (scanner.peekNextToken() == GTKScanner.TOKEN_EOF) {
                break;
            }

            int expected = parseStatement();

            if (expected != GTKScanner.TOKEN_NONE) {
                String symbolName = null;
                String msg = null;

                if (scanner.currScope == 0) {
                    Symbol lookup;

                    lookup = getSymbol(expected);
                    if (lookup != null) {
                        msg = "e.g. `" + lookup.name + "'";
                    }

                    lookup = getSymbol(scanner.currToken);
                    if (lookup != null) {
                        symbolName = lookup.name;
                    }
                }

                scanner.unexpectedToken(expected, symbolName, msg, true);
                break;
            }
        }
    }
    
    private int parseStatement() throws IOException {
        int token;
        
        token = scanner.peekNextToken();
        if (token == SYMBOL_INCLUDE.val) {
            return parseInclude();
        } else if (token == SYMBOL_STYLE.val) {
            return parseStyle();
        } else if (token == SYMBOL_BINDING.val) {
            return parseBinding();
        } else if (token == SYMBOL_PIXMAP_PATH.val) {
            return parsePixmapPath();
        } else if (token == SYMBOL_WIDGET.val
                       || token == SYMBOL_WIDGET_CLASS.val
                       || token == SYMBOL_CLASS.val) {
            return parseAssignment(token);
        } else if (token == SYMBOL_MODULE_PATH.val) {
            return parseModulePath();
        } else if (token == SYMBOL_IM_MODULE_FILE.val) {
            return parseIMModuleFile();
        } else if (token == GTKScanner.TOKEN_IDENTIFIER) {
            return parseIdentifier();
        }

        scanner.getToken();
        return SYMBOL_STYLE.val;
    }
    
    private int parseInclude() throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != SYMBOL_INCLUDE.val) {
            return SYMBOL_INCLUDE.val;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_STRING) {
            return GTKScanner.TOKEN_STRING;
        }
        
        File parseFile = null;
        
        String name = scanner.currValue.stringVal;
        File file = new File(name);

        if (file.isAbsolute()) {
            parseFile = file;
        } else {
            File[] dirs = new File[dirStack.size()];
            dirs = (File[])dirStack.toArray(dirs);
            
            for (int i = dirs.length - 1; i >= 0; i--) {
                file = new File(dirs[i], name);
                if (file.exists()) {
                    parseFile = file;
                    break;
                }
            }
        }
        
        if (parseFile == null) {
            scanner.printMessage("Unable to find include file: \"" + name + "\"", false);
        } else {
            // save the current scanner and recurse
            GTKScanner savedScanner = scanner;

            try {
                parseFile(file, name);
            } catch (IOException ioe) {
                savedScanner.printMessage("(" + ioe.toString()
                                              + ") while parsing include file: \""
                                              + name
                                              + "\"", false);
            }

            // restore the scanner
            scanner = savedScanner;
        }
        
        return GTKScanner.TOKEN_NONE;
    }
    
    private int parseStyle() throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != SYMBOL_STYLE.val) {
            return SYMBOL_STYLE.val;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_STRING) {
            return GTKScanner.TOKEN_STRING;
        }
        
        StyleInfo info = (StyleInfo)namedStyles.get(scanner.currValue.stringVal);
        
        if (info == null) {
            info = new StyleInfo(scanner.currValue.stringVal);
        }
        
        token = scanner.peekNextToken();
        if (token == GTKScanner.TOKEN_EQUAL_SIGN) {
            token = scanner.getToken();
            token = scanner.getToken();
            
            if (token != GTKScanner.TOKEN_STRING) {
                return GTKScanner.TOKEN_STRING;
            }
            
            StyleInfo parent = (StyleInfo)namedStyles.get(scanner.currValue.stringVal);
            if (parent != null) {
                info.copyDataFrom(parent);
            }
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_LEFT_CURLY) {
            return GTKScanner.TOKEN_LEFT_CURLY;
        }

        token = scanner.peekNextToken();
        while (token != GTKScanner.TOKEN_RIGHT_CURLY) {
            if (token == SYMBOL_FG.val
                    || token == SYMBOL_BG.val
                    || token == SYMBOL_TEXT.val
                    || token == SYMBOL_BASE.val) {
                token = parseColorSetting(token, info);
            } else if (token == SYMBOL_XTHICKNESS.val
                           || token == SYMBOL_YTHICKNESS.val) {
                token = parseThickness(token, info);
            } else if (token == SYMBOL_BG_PIXMAP.val) {
                token = parseBGPixmap(info);
            } else if (token == SYMBOL_FONT.val
                           || token == SYMBOL_FONTSET.val
                           || token == SYMBOL_FONT_NAME.val) {
                token = parseFont(token, info);
            } else if (token == SYMBOL_ENGINE.val) {
                token = parseEngine(info);
            } else if (token == SYMBOL_STOCK.val) {
                token = parseStock(info);
            } else if (token == GTKScanner.TOKEN_IDENTIFIER) {
                token = parseIdentifierInStyle(info);
            } else {
                scanner.getToken();
                token = GTKScanner.TOKEN_RIGHT_CURLY;
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
        
        namedStyles.put(info.name, info);
        
        return GTKScanner.TOKEN_NONE;
    }
    
    private int parseBinding() throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != SYMBOL_BINDING.val) {
            return SYMBOL_BINDING.val;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_STRING) {
            return GTKScanner.TOKEN_STRING;
        }
        
        token = ignoreBlock();
        if (token != GTKScanner.TOKEN_NONE) {
            return token;
        }
        
        scanner.printMessage("Binding specification is unsupported, ignoring", false);
        
        return GTKScanner.TOKEN_NONE;
    }
    
    private int parsePixmapPath() throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != SYMBOL_PIXMAP_PATH.val) {
            return SYMBOL_PIXMAP_PATH.val;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_STRING) {
            return GTKScanner.TOKEN_STRING;
        }
        
        pixmapPaths = null;
        
        ArrayList tempPaths = new ArrayList();
        
        StringTokenizer tok = new StringTokenizer(scanner.currValue.stringVal, File.pathSeparator);
        while (tok.hasMoreTokens()) {
            String path = tok.nextToken();
            File file = new File(path);
            if (file.isAbsolute()) {
                tempPaths.add(file);
            } else {
                scanner.printMessage("Pixmap path element: \"" + path + "\" must be absolute", false);
            }
        }
        
        if (tempPaths.size() > 0) {
            pixmapPaths = new File[tempPaths.size()];
            pixmapPaths = (File[])tempPaths.toArray(pixmapPaths);
        }
        
        return GTKScanner.TOKEN_NONE;
    }
    
    private int parseAssignment(int expVal) throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != expVal) {
            return expVal;
        }
        
        int type;
        String pattern;
        StyleInfo info;
        
        boolean isBinding;
        
        if (token == SYMBOL_WIDGET.val) {
            type = GTKStyleFactory.WIDGET;
        } else if (token == SYMBOL_WIDGET_CLASS.val) {
            type = GTKStyleFactory.WIDGET_CLASS;
        } else if (token == SYMBOL_CLASS.val) {
            type = GTKStyleFactory.CLASS;
        } else {
            return SYMBOL_WIDGET_CLASS.val;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_STRING) {
            return GTKScanner.TOKEN_STRING;
        }
        
        pattern = scanner.currValue.stringVal;
        
        token = scanner.getToken();
        if (token == SYMBOL_STYLE.val) {
            isBinding = false;
        } else if (token == SYMBOL_BINDING.val) {
            isBinding = true;
        } else {
            return SYMBOL_STYLE.val;
        }
        
        token = scanner.peekNextToken();
        if (token == ':') {
            token = scanner.getToken();

            token = scanner.getToken();
            if (token != SYMBOL_LOWEST.val
                    && token != SYMBOL_GTK.val
                    && token != SYMBOL_APPLICATION.val
                    && token != SYMBOL_THEME.val
                    && token != SYMBOL_RC.val
                    && token != SYMBOL_HIGHEST.val) {
                return SYMBOL_APPLICATION.val;
            }
            
            scanner.printMessage("Priority specification is unsupported, ignoring", false);
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_STRING) {
            return GTKScanner.TOKEN_STRING;
        }

        // PENDING(shannonh) - When we start handling priority, the information will
        //                     probably be stored as part of an Assignment here
        if (isBinding) {
            // PENDING(shannonh) - Key binding support
            scanner.printMessage("Binding assignment is unsupported, ignoring", false);
        } else {
            info = (StyleInfo)namedStyles.get(scanner.currValue.stringVal);
            if (info == null) {
                return GTKScanner.TOKEN_STRING;
            }
            
            Assignment assignment = new Assignment(type, pattern, info);
            assignments.add(assignment);
        }
        
        return GTKScanner.TOKEN_NONE;
    }
    
    private int parseModulePath() throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != SYMBOL_MODULE_PATH.val) {
            return SYMBOL_MODULE_PATH.val;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_STRING) {
            return GTKScanner.TOKEN_STRING;
        }
        
        scanner.printMessage("module_path directive is now ignored", false);
        
        return GTKScanner.TOKEN_NONE;
    }
    
    private int parseIMModuleFile() throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != SYMBOL_IM_MODULE_FILE.val) {
            return SYMBOL_IM_MODULE_FILE.val;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_STRING) {
            return GTKScanner.TOKEN_STRING;
        }

        scanner.printMessage("im_module_file directive is unsupported, ignoring", false);
        
        return GTKScanner.TOKEN_NONE;
    }
    
    private int parseIdentifier() throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_IDENTIFIER) {
            return GTKScanner.TOKEN_IDENTIFIER;
        }
        
        String prop;
        Object[] value = new Object[1];

        StringBuffer buf = new StringBuffer(scanner.currValue.stringVal);

        String validChars = GTKScanner.CHARS_A_2_Z
                                + GTKScanner.CHARS_a_2_z
                                + GTKScanner.CHARS_DIGITS
                                + "-";

        // some weird logic that GTK does
        int len = buf.length();
        for (int i = 0; i < len; i++) {
            if (validChars.indexOf(buf.charAt(i)) == -1) {
                buf.setCharAt(i, '-');
            }
        }
        
        prop = buf.toString().intern();
        
        token = parsePropertyAssignment(value);
        if (token != GTKScanner.TOKEN_NONE) {
            return token;
        }

        settings.put(prop, value[0]);

        return GTKScanner.TOKEN_NONE;
    }
    
    private int parseColorSetting(int expVal, StyleInfo info) throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != expVal) {
            return expVal;
        }
        
        Color[] cols = null;
        
        if (token == SYMBOL_FG.val) {
            cols = info.fg;
        } else if (token == SYMBOL_BG.val) {
            cols = info.bg;
        } else if (token == SYMBOL_TEXT.val) {
            cols = info.text;
        } else if (token == SYMBOL_BASE.val) {
            cols = info.base;
        } else {
            return SYMBOL_FG.val;
        }
        
        int[] state = new int[1];
        token = parseState(state);
        
        if (token != GTKScanner.TOKEN_NONE) {
            return token;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }
        
        return parseColor(cols, state[0]);
    }
    
    private int parseState(int[] retVal) throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_LEFT_BRACE) {
            return GTKScanner.TOKEN_LEFT_BRACE;
        }
        
        token = scanner.getToken();
        if (token == SYMBOL_NORMAL.val) {
            retVal[0] = StyleInfo.NORMAL;
        } else if (token == SYMBOL_ACTIVE.val) {
            retVal[0] = StyleInfo.ACTIVE;
        } else if (token == SYMBOL_PRELIGHT.val) {
            retVal[0] = StyleInfo.PRELIGHT;
        } else if (token == SYMBOL_SELECTED.val) {
            retVal[0] = StyleInfo.SELECTED;
        } else if (token == SYMBOL_INSENSITIVE.val) {
            retVal[0] = StyleInfo.INSENSITIVE;
        } else {
            return SYMBOL_NORMAL.val;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_RIGHT_BRACE) {
            return GTKScanner.TOKEN_RIGHT_BRACE;
        }
        
        return GTKScanner.TOKEN_NONE;
    }
    
    private int parseColor(Color[] colors, int index) throws IOException {
        int token;
        
        long lVal;
        double dVal;
        
        float red;
        float green;
        float blue;
        
        token = scanner.getToken();
        
        switch(token) {
            case GTKScanner.TOKEN_LEFT_CURLY:
                token = scanner.getToken();
                if (token == GTKScanner.TOKEN_INT) {
                    red = javaColorVal(scanner.currValue.longVal);
                } else if (token == GTKScanner.TOKEN_FLOAT) {
                    red = javaColorVal(scanner.currValue.doubleVal);
                } else {
                    return GTKScanner.TOKEN_FLOAT;
                }
                
                token = scanner.getToken();
                if (token != GTKScanner.TOKEN_COMMA) {
                    return GTKScanner.TOKEN_COMMA;
                }
                
                token = scanner.getToken();
                if (token == GTKScanner.TOKEN_INT) {
                    green = javaColorVal(scanner.currValue.longVal);
                } else if (token == GTKScanner.TOKEN_FLOAT) {
                    green = javaColorVal(scanner.currValue.doubleVal);
                } else {
                    return GTKScanner.TOKEN_FLOAT;
                }
                
                token = scanner.getToken();
                if (token != GTKScanner.TOKEN_COMMA) {
                    return GTKScanner.TOKEN_COMMA;
                }
                
                token = scanner.getToken();
                if (token == GTKScanner.TOKEN_INT) {
                    blue = javaColorVal(scanner.currValue.longVal);
                } else if (token == GTKScanner.TOKEN_FLOAT) {
                    blue = javaColorVal(scanner.currValue.doubleVal);
                } else {
                    return GTKScanner.TOKEN_FLOAT;
                }
                
                token = scanner.getToken();
                if (token != GTKScanner.TOKEN_RIGHT_CURLY) {
                    return GTKScanner.TOKEN_RIGHT_CURLY;
                }
                
                colors[index] = new ColorUIResource(red, green, blue);
                
                break;
            case GTKScanner.TOKEN_STRING:
                Color color = parseColorString(scanner.currValue.stringVal);

                if (color == null) {
                    scanner.printMessage("Invalid color constant '" +
                                              scanner.currValue.stringVal
                                              + "'", false);
                    return GTKScanner.TOKEN_STRING;
                }

                colors[index] = color;
                
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
                return new ColorUIResource(r / 65535.0f, g / 65535.0f, b / 65535.0f);
            } else if (i == 1) {
                return new ColorUIResource(r / 15.0f, g / 15.0f, b / 15.0f);
            } else if (i == 2) {
                return new ColorUIResource(r, g, b);
            } else {
                return new ColorUIResource(r / 4095.0f, g / 4095.0f, b / 4095.0f);
            }
        } else {
            return XColors.lookupColor(str);
        }
    }
    
    private static float javaColorVal(long col) {
        int color = (int)Math.max(Math.min(col, 65535), 0);
        return col / 65535.0f;
    }
    
    private static float javaColorVal(double col) {
        float color = (float)Math.max(Math.min(col, 1.0f), 0.0f);
        return color;
    }

    private int parseThickness(int expVal, StyleInfo info) throws IOException {
        int token;
        boolean isXThickness;

        token = scanner.getToken();
        if (token != expVal) {
            return expVal;
        }

        if (token == SYMBOL_XTHICKNESS.val) {
            isXThickness = true;
        } else if (token == SYMBOL_YTHICKNESS.val) {
            isXThickness = false;
        } else {
            return SYMBOL_XTHICKNESS.val;
        }

        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }

        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_INT) {
            return GTKScanner.TOKEN_INT;
        }

        int thickness = (int)scanner.currValue.longVal;

        if (isXThickness) {
            info.xThickness = thickness;
        } else {
            info.yThickness = thickness;
        }

        return GTKScanner.TOKEN_NONE;
    }

    private int parseBGPixmap(StyleInfo info) throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != SYMBOL_BG_PIXMAP.val) {
            return SYMBOL_BG_PIXMAP.val;
        }
        
        int[] state = new int[1];
        token = parseState(state);
        
        if (token != GTKScanner.TOKEN_NONE) {
            return token;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_STRING) {
            return GTKScanner.TOKEN_STRING;
        }
        
        String pixmapStr = null;
        
        String str = scanner.currValue.stringVal;
        
        if (str.equals("<none>") || str.equals("<parent>")) {
            pixmapStr = str.intern();
        } else {
            pixmapStr = resolvePixmapPath(str);
        }
        
        if (pixmapStr == null) {
            scanner.printMessage("Unable to locate image file in pixmap_path: \"" + str + "\"", false);
        } else {
            info.bgPixmapName[state[0]] = pixmapStr;
        }
        
        return GTKScanner.TOKEN_NONE;
    }

    String resolvePixmapPath(String str) {
        // search in pixmap path
        if (pixmapPaths != null) {
            for (int i = 0; i < pixmapPaths.length; i++) {
                File file = new File(pixmapPaths[i], str);
                if (file.canRead()) {
                    return file.getAbsolutePath();
                }
            }
        }
        
        // search in rc directory stack
        File[] dirs = new File[dirStack.size()];
        dirs = (File[])dirStack.toArray(dirs);
        
        for (int i = dirs.length - 1; i >= 0; i--) {
            File file = new File(dirs[i], str);
            if (file.canRead()) {
                return file.getAbsolutePath();
            }
        }
        
        return null;
    }

    private int parseFont(int expVal, StyleInfo info) throws IOException {
        int token;
        boolean isPango;
        
        token = scanner.getToken();
        if (token != expVal) {
            return expVal;
        }
        
        if (token == SYMBOL_FONT_NAME.val) {
            isPango = true;
        } else if (token == SYMBOL_FONT.val
                       || token == SYMBOL_FONTSET.val) {
            isPango = false;
        } else {
            return SYMBOL_FONT_NAME.val;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_STRING) {
            return GTKScanner.TOKEN_STRING;
        }
        
        // only need to parse pango font names
        if (isPango) {
            String pangoName = scanner.currValue.stringVal;
            
            info.font = PangoFonts.lookupFont(pangoName);
        }
        
        return GTKScanner.TOKEN_NONE;
    }

    private GTKEngineParser getParser(String engineName) {
        Object o = engineParsers.get(engineName);
        
        if (o == null) {
            return null;
        }
        
        if (o instanceof GTKEngineParser) {
            return (GTKEngineParser)o;
        }
        
        GTKEngineParser parser = null;
        
        try {
            parser = (GTKEngineParser)Class.forName((String)o).newInstance();
        } catch (ClassNotFoundException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }
        
        if (parser == null) {
            // no need to keep trying to load it every time
            engineParsers.remove(engineName);
        } else {
            // replace the name with an instance of a parser
            engineParsers.put(engineName, parser);
        }
        
        return parser;
    }

    private int parseEngine(StyleInfo info) throws IOException {
        int token;

        token = scanner.getToken();
        if (token != SYMBOL_ENGINE.val) {
            return SYMBOL_ENGINE.val;
        }

        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_STRING) {
            return GTKScanner.TOKEN_STRING;
        }

        String engineName = scanner.currValue.stringVal;

        // engine "" {} means to use the default engine
        if (engineName.length() == 0) {
            token = scanner.getToken();
            if (token != GTKScanner.TOKEN_LEFT_CURLY) {
                return GTKScanner.TOKEN_LEFT_CURLY;
            }

            token = scanner.getToken();
            if (token != GTKScanner.TOKEN_RIGHT_CURLY) {
                return GTKScanner.TOKEN_RIGHT_CURLY;
            }

            info.engineInfo = null;

            return GTKScanner.TOKEN_NONE;
        }

        GTKEngineParser parser = getParser(engineName);

        if (parser == null) {
            token = ignoreBlock();
            if (token != GTKScanner.TOKEN_NONE) {
                return token;
            }
            
            scanner.printMessage("Engine \"" + engineName + "\" is unsupported, ignoring", false);
        } else {
            token = scanner.getToken();
            if (token != GTKScanner.TOKEN_LEFT_CURLY) {
                return GTKScanner.TOKEN_LEFT_CURLY;
            }

            EngineInfo[] engineInfo = new EngineInfo[1];

            // only pass in the existing engine info if it came from this parser
            if (info.engineInfo != null && engineName.equals(info.engineInfo.engineName)) {
                engineInfo[0] = info.engineInfo;
            }

            token = parser.parse(scanner, this, engineInfo);
            if (token != GTKScanner.TOKEN_NONE) {
                return token;
            }

            // tag the returned engine info with the engine name
            if (engineInfo[0] != null) {
                engineInfo[0].engineName = engineName;
            }

            info.engineInfo = engineInfo[0];
        }
        
        return GTKScanner.TOKEN_NONE;
    }

    private int parseStock(StyleInfo info) throws IOException {
        String id;

        int token;
        
        token = scanner.getToken();
        if (token != SYMBOL_STOCK.val) {
            return SYMBOL_STOCK.val;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_LEFT_BRACE) {
            return GTKScanner.TOKEN_LEFT_BRACE;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_STRING) {
            return GTKScanner.TOKEN_STRING;
        }
        
        id = scanner.currValue.stringVal;
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_RIGHT_BRACE) {
            return GTKScanner.TOKEN_RIGHT_BRACE;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_EQUAL_SIGN) {
            return GTKScanner.TOKEN_EQUAL_SIGN;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_LEFT_CURLY) {
            return GTKScanner.TOKEN_LEFT_CURLY;
        }
        
        ArrayList iconSources = new ArrayList();

        // This array will be used first to hold the return value from
        // parseIconSource and then the variable will be used in
        // converting iconSources to an array.
        GTKStyle.GTKIconSource[] sources = new GTKStyle.GTKIconSource[1];

        token = scanner.peekNextToken();
        while (token != GTKScanner.TOKEN_RIGHT_CURLY) {
            token = parseIconSource(sources);
            
            if (token != GTKScanner.TOKEN_NONE) {
                return token;
            }
            
            token = scanner.getToken();
            if (token != GTKScanner.TOKEN_COMMA
                    && token != GTKScanner.TOKEN_RIGHT_CURLY) {
                return GTKScanner.TOKEN_RIGHT_CURLY;
            }
            
            if (sources[0] != null) {
                iconSources.add(sources[0]);
            }
        }
        
        if (iconSources.size() != 0) {
            sources = new GTKStyle.GTKIconSource[iconSources.size()];
            sources = (GTKStyle.GTKIconSource[])iconSources.toArray(sources);
            info.addStockItem(id, sources);
        }
        
        return GTKScanner.TOKEN_NONE;
    }
    
    private GTKStyle.GTKIconSource createIconSource(String path,
                                                    int direction,
                                                    int state,
                                                    String size) {
        String resolvedPath = resolvePixmapPath(path);

        if (resolvedPath != null) {
            return new GTKStyle.GTKIconSource(resolvedPath, direction, state, size);
        }
        
        return null;
    }
    
    private int parseIconSource(GTKStyle.GTKIconSource[] retVal) throws IOException {
        int token;

        String pixmapStr = null;
        int direction = GTKConstants.UNDEFINED;
        int state = GTKConstants.UNDEFINED;
        String size = null;

        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_LEFT_CURLY) {
            return GTKScanner.TOKEN_LEFT_CURLY;
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_STRING) {
            return GTKScanner.TOKEN_STRING;
        }
        
        pixmapStr = scanner.currValue.stringVal;
        
        token = scanner.getToken();
        if (token == GTKScanner.TOKEN_RIGHT_CURLY) {
            retVal[0] = createIconSource(pixmapStr, direction, state, size);
            return GTKScanner.TOKEN_NONE;
        } else if (token != GTKScanner.TOKEN_COMMA) {
            return GTKScanner.TOKEN_COMMA;
        }
        
        token = scanner.getToken();
        if (token == SYMBOL_RTL.val) {
            direction = GTKConstants.RTL;
        } else if (token == SYMBOL_LTR.val) {
            direction = GTKConstants.LTR;
        } else if (token == '*') {
            // nothing
        } else {
            return SYMBOL_RTL.val;
        }
        
        token = scanner.getToken();
        if (token == GTKScanner.TOKEN_RIGHT_CURLY) {
            retVal[0] = createIconSource(pixmapStr, direction, state, size);
            return GTKScanner.TOKEN_NONE;
        } else if (token != GTKScanner.TOKEN_COMMA) {
            return GTKScanner.TOKEN_COMMA;
        }
        
        token = scanner.getToken();
        if (token == SYMBOL_NORMAL.val) {
            state = SynthConstants.ENABLED;
        } else if (token == SYMBOL_ACTIVE.val) {
            state = SynthConstants.PRESSED;
        } else if (token == SYMBOL_PRELIGHT.val) {
            state = SynthConstants.MOUSE_OVER;
        } else if (token == SYMBOL_SELECTED.val) {
            state = SynthConstants.SELECTED;
        } else if (token == SYMBOL_INSENSITIVE.val) {
            state = SynthConstants.DISABLED;
        } else if (token == '*') {
            // nothing
        } else {
            return SYMBOL_PRELIGHT.val;
        }
        
        token = scanner.getToken();
        if (token == GTKScanner.TOKEN_RIGHT_CURLY) {
            retVal[0] = createIconSource(pixmapStr, direction, state, size);
            return GTKScanner.TOKEN_NONE;
        } else if (token != GTKScanner.TOKEN_COMMA) {
            return GTKScanner.TOKEN_COMMA;
        }
        
        token = scanner.getToken();
        if (token != '*') {
            if (token != GTKScanner.TOKEN_STRING) {
                return GTKScanner.TOKEN_STRING;
            }

            size = scanner.currValue.stringVal;

            // if an invalid size, use * instead
            if (GTKStyle.getIconSize(size) == null) {
                size = null;
            }
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_RIGHT_CURLY) {
            return GTKScanner.TOKEN_RIGHT_CURLY;
        }
        
        retVal[0] = createIconSource(pixmapStr, direction, state, size);
        
        return GTKScanner.TOKEN_NONE;
    }

    private int parseIdentifierInStyle(StyleInfo info) throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_IDENTIFIER
                || scanner.currValue.stringVal.charAt(0) < 'A'
                || scanner.currValue.stringVal.charAt(0) > 'Z') {
            return GTKScanner.TOKEN_IDENTIFIER;
        }
        
        String klass;
        String prop;
        Object[] value = new Object[1];
        
        klass = scanner.currValue.stringVal.intern();
        
        // check the next two tokens to make sure they're both ':'
        if (scanner.getToken() != ':' || scanner.getToken() != ':') {
            return ':';
        }
        
        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_IDENTIFIER) {
            return GTKScanner.TOKEN_IDENTIFIER;
        }
        
        StringBuffer buf = new StringBuffer(scanner.currValue.stringVal);
        
        String validChars = GTKScanner.CHARS_A_2_Z
                                + GTKScanner.CHARS_a_2_z
                                + GTKScanner.CHARS_DIGITS
                                + "-";

        // some weird logic that GTK does
        int len = buf.length();
        for (int i = 0; i < len; i++) {
            if (validChars.indexOf(buf.charAt(i)) == -1) {
                buf.setCharAt(i, '-');
            }
        }
        
        prop = buf.toString().intern();
        
        token = parsePropertyAssignment(value);
        if (token != GTKScanner.TOKEN_NONE) {
            return token;
        }

        PropertyParser pp = null;
        if (value[0] instanceof String && (pp = PropertyParser.getParserFor(prop)) != null) {
            Object parsedVal = pp.parse((String)value[0]);

            if (parsedVal == null) {
                scanner.printMessage("Failed to parse property value \"" + value[0] + "\" for `"
                                     + klass + "::" + prop + "'", false);
            } else {
                info.addProperty(klass, prop, parsedVal);
            }
        } else {
            info.addProperty(klass, prop, value[0]);
        }

        return GTKScanner.TOKEN_NONE;
    }
    
    private int parsePropertyAssignment(Object[] retVal) throws IOException {
        int token;
        
        token = scanner.getToken();
        if (token != '=') {
            return '=';
        }

        // save the scanner mode
        boolean scanIdentifier = scanner.scanIdentifier;
        boolean scanSymbols = scanner.scanSymbols;
        boolean identifier2String = scanner.identifier2String;
        boolean char2Token = scanner.char2Token;
        boolean scanIdentifierNULL = scanner.scanIdentifierNULL;
        boolean numbers2Int = scanner.numbers2Int;

        // modify the scanner mode for our purposes
        scanner.scanIdentifier = true;
        scanner.scanSymbols = false;
        scanner.identifier2String = false;
        scanner.char2Token = true;
        scanner.scanIdentifierNULL = false;
        scanner.numbers2Int = true;

        boolean negate = false;
        
        if (scanner.peekNextToken() == '-') {
            scanner.getToken();
            negate = true;
        }
        
        token = scanner.peekNextToken();
        switch(token) {
            case GTKScanner.TOKEN_INT:
                scanner.getToken();
                retVal[0] = new Long(negate ? -scanner.currValue.longVal : scanner.currValue.longVal);
                token = GTKScanner.TOKEN_NONE;
                break;
            case GTKScanner.TOKEN_FLOAT:
                scanner.getToken();
                retVal[0] = new Double(negate ? -scanner.currValue.doubleVal : scanner.currValue.doubleVal);
                token = GTKScanner.TOKEN_NONE;
                break;
            case GTKScanner.TOKEN_STRING:
                scanner.getToken();
                if (negate) {
                    token = GTKScanner.TOKEN_INT;
                } else {
                    retVal[0] = scanner.currValue.stringVal;
                    token = GTKScanner.TOKEN_NONE;
                }
                break;
            case GTKScanner.TOKEN_IDENTIFIER:
            case GTKScanner.TOKEN_LEFT_PAREN:
            case GTKScanner.TOKEN_LEFT_CURLY:
            case GTKScanner.TOKEN_LEFT_BRACE:
                if (negate) {
                    token = GTKScanner.TOKEN_INT;
                } else {
                    StringBuffer result = new StringBuffer();

                    token = parseComplexPropVal(result, GTKScanner.TOKEN_EOF);
                    if (token == GTKScanner.TOKEN_NONE) {
                        result.append(' ');
                        retVal[0] = result.toString();
                    }
                }
                break;
            default:
                scanner.getToken();
                token = GTKScanner.TOKEN_INT;
                break;
        }
        
        // restore the scanner mode
        scanner.scanIdentifier = scanIdentifier;
        scanner.scanSymbols = scanSymbols;
        scanner.identifier2String = identifier2String;
        scanner.char2Token = char2Token;
        scanner.scanIdentifierNULL = scanIdentifierNULL;
        scanner.numbers2Int = numbers2Int;
        
        return token;
    }
    
    private int parseComplexPropVal(StringBuffer into, int delim) throws IOException {
        int token;
        
        token = scanner.getToken();
        switch(token) {
            case GTKScanner.TOKEN_INT:
                into.append(" 0x");
                into.append(Long.toHexString(scanner.currValue.longVal));
                break;
            case GTKScanner.TOKEN_FLOAT:
                into.append(' ');
                into.append(scanner.currValue.doubleVal);
                break;
            case GTKScanner.TOKEN_STRING:
                into.append(" \"");
                into.append(escapeString(scanner.currValue.stringVal));
                into.append('"');
                break;
            case GTKScanner.TOKEN_IDENTIFIER:
                into.append(' ');
                into.append(scanner.currValue.stringVal);
                break;
            case GTKScanner.TOKEN_LEFT_PAREN:
                into.append(' ');
                into.append((char)token);
                token = parseComplexPropVal(into, GTKScanner.TOKEN_RIGHT_PAREN);
                if (token != GTKScanner.TOKEN_NONE) {
                    return token;
                }
                break;
            case GTKScanner.TOKEN_LEFT_CURLY:
                into.append(' ');
                into.append((char)token);
                token = parseComplexPropVal(into, GTKScanner.TOKEN_RIGHT_CURLY);
                if (token != GTKScanner.TOKEN_NONE) {
                    return token;
                }
                break;
            case GTKScanner.TOKEN_LEFT_BRACE:
                into.append(' ');
                into.append((char)token);
                token = parseComplexPropVal(into, GTKScanner.TOKEN_RIGHT_BRACE);
                if (token != GTKScanner.TOKEN_NONE) {
                    return token;
                }
                break;
            default:
                if (token >= GTKScanner.TOKEN_NONE || token <= GTKScanner.TOKEN_EOF) {
                    return delim != GTKScanner.TOKEN_EOF ? delim : GTKScanner.TOKEN_STRING;
                }
                into.append(' ');
                into.append((char)token);
                if (token == delim) {
                    return GTKScanner.TOKEN_NONE;
                }
        }
        
        if (delim == GTKScanner.TOKEN_EOF) {
            return GTKScanner.TOKEN_NONE;
        } else {
            return parseComplexPropVal(into, delim);
        }
    }

    private String escapeString(String source) {
        int len = source.length();
        
        StringBuffer result = new StringBuffer(len * 4);
        
        for (int i = 0; i < len; i++) {
            char ch = source.charAt(i);
            
            switch(ch) {
                case '\b':
                    result.append("\\b");
                    break;
                case '\f':
                    result.append("\\f");
                    break;
                case '\n':
                    result.append("\\n");
                    break;
                case '\r':
                    result.append("\\r");
                    break;
                case '\t':
                    result.append("\\t");
                    break;
                case '\\':
                    result.append("\\\\");
                    break;
                case '"':
                    result.append("\\\"");
                    break;
                default:
                    if (ch < ' ' || ch > '~') {
                        result.append('\\');
                        result.append(Integer.toOctalString(ch));
                    } else {
                        result.append((char)ch);
                    }
                    break;
            }
        }

        return result.toString();
    }

    private int ignoreBlock() throws IOException {
        int token;

        token = scanner.getToken();
        if (token != GTKScanner.TOKEN_LEFT_CURLY) {
            return GTKScanner.TOKEN_LEFT_CURLY;
        }

        int curlys = 1;

        while (curlys > 0) {
            token = scanner.getToken();
            switch(token) {
                case GTKScanner.TOKEN_EOF:
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

        return GTKScanner.TOKEN_NONE;
    }

    // for testing
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java GTKParser <gtkrc file> <gtkrc file>....");
            System.exit(1);
        }
        
        GTKParser parser = new GTKParser();
        
        try {
            for (int i = 0; i < args.length; i++) {
                parser.parseFile(new File(args[i]), args[i]);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
        parser.printNamedStyles();
        System.out.println();
        parser.printSettings();
        System.out.println();
        parser.printAssignments();
    }

    // for testing
    private void printNamedStyles() {
        System.out.println("===== Named Styles =====");
        
        StyleInfo[] infos = new StyleInfo[namedStyles.size()];
        infos = (StyleInfo[])namedStyles.values().toArray(infos);
        
        for (int i = 0; i < infos.length; i++) {
            StyleInfo info = infos[i];
            
            System.out.println("NAME: " + info.name);
            GTKStyle style = info.toGTKStyle();
            System.out.println(style == StyleInfo.EMPTY_STYLE ? "EMPTY_STYLE" : style.toString());
            System.out.println("---------------------------");
        }
    }

    // for testing
    private void printSettings() {
        System.out.println("===== GTK Settings =====");

        Iterator iter = settings.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }

    // for testing    
    private void printAssignments() {
        System.out.println("===== Assignments =====");
        
        Assignment[] assigns = new Assignment[assignments.size()];
        assigns = (Assignment[])assignments.toArray(assigns);
        
        for (int i = 0; i < assigns.length; i++) {
            System.out.println(assigns[i]);
        }
    }

}

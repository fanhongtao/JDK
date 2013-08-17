/*
 * @(#)Font2DTest.java	1.1 99/11/19
 *
 * Copyright (c) 1998, 1999 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.applet.Applet;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Vector;
import java.util.ResourceBundle;
import java.util.Locale;
import java.io.*;

import com.sun.image.codec.jpeg.*;

/**
 * Font2DTest.  
 *
 * @version @(#)Font2DTest.java	1.1 99/11/19
 * @author Chris Campbell
 */
public class Font2DTest extends Applet implements ActionListener, ItemListener {

    Font2DCanvas symbols;
    TextField tfBaseText;
    TextField tfFontSize;
    TextArea taUnicodeText;
    FontList fontList;
    Choice styleList;
    Choice rangeList;
    Choice transformList;
    Choice methodList;
    Choice displayList;
    Checkbox cbAntialias;
    Checkbox cbFracMetrics;
    Hashtable styles;
    Hashtable ranges;
    Button btnUpdateText;
    PrinterJob printerJob;
    PageFormat pageFormat;

    static final int ASCII_BASE    = 0x0000;
    static final int GREEK_BASE    = 0x0370;
    static final int HEBREW_BASE   = 0x0590;
    static final int ARABIC_BASE   = 0x0600;
    static final int THAI_BASE     = 0x0E00;
    static final int GEORGIAN_BASE = 0x10A0;
    static final int PUNCT_BASE    = 0x2000;
    static final int ARROWS_BASE   = 0x2190;
    static final int SYMBOL_BASE   = 0x2200;
    static final int SHAPES_BASE   = 0x25A0;
    static final int DINGBAT_BASE  = 0x2700;

    public void init() { 
        int i;

        styles = new Hashtable();
        styles.put("Plain", new Integer(Font.PLAIN));
        styles.put("Bold", new Integer(Font.BOLD));
        styles.put("Italic", new Integer(Font.ITALIC));
        styles.put("Bold Italic", new Integer(Font.BOLD | Font.ITALIC));

        String[] styleNames = {"Plain", "Bold", "Italic", "Bold Italic"}; 

        ranges = new Hashtable();
        ranges.put("ASCII", new Integer(ASCII_BASE));
        ranges.put("Greek", new Integer(GREEK_BASE));
        ranges.put("Hebrew", new Integer(HEBREW_BASE));
        ranges.put("Arabic", new Integer(ARABIC_BASE));
        ranges.put("Thai", new Integer(THAI_BASE));
        ranges.put("Georgian", new Integer(GEORGIAN_BASE));
        ranges.put("Punctuation", new Integer(PUNCT_BASE));
        ranges.put("Arrows", new Integer(ARROWS_BASE));
        ranges.put("Symbols", new Integer(SYMBOL_BASE));
        ranges.put("Shapes", new Integer(SHAPES_BASE));
        ranges.put("Dingbats", new Integer(DINGBAT_BASE));

        String[] rangeNames = {"ASCII", "Greek", "Hebrew", "Arabic", "Thai", 
                               "Georgian", "Punctuation", "Arrows", "Symbols",
                               "Shapes", "Dingbats", "Other..."};

        String[] transformNames = {"None", "Translate", "Rotate", 
                                   "Scale", "Shear"}; 

        String[] methodNames = {"drawChars()", "drawString(String)",
                                "drawString(Iterator)", "drawGlyphVector()", 
                                "drawBytes()", "TextLayout.draw()"};

        String[] displayNames = {"Unicode Range", "User Text", 
                                 "All Glyphs", "Resource Text"};

        setSize(850,700);
        setLayout(new BorderLayout());

        if (getFrame() != null) {
            MenuBar mbMenu;
            boolean bExit = false;
            if ((mbMenu = getFrame().getMenuBar()) == null) {
                mbMenu = new MenuBar();
                getFrame().setMenuBar(mbMenu);
                bExit = true;
            } 
            Menu menuFile = new Menu("File");
            menuFile.addActionListener(this);
            mbMenu.add(menuFile);
            Menu menuHelp = new Menu("Help");
            menuHelp.addActionListener(this);
            mbMenu.add(menuHelp);
            
            setupMenuComponent("Read Text Data...", menuFile);
            setupMenuComponent("Read Image Data...", menuFile);
            setupMenuComponent("Write Image Data...", menuFile);
            menuFile.add(new MenuItem("-"));
            setupMenuComponent("Read Control Data...", menuFile);
            setupMenuComponent("Write Control Data...", menuFile);
            menuFile.add(new MenuItem("-"));
            setupMenuComponent("Page Setup...", menuFile);
            setupMenuComponent("Print...", menuFile);
            if (bExit) {
                menuFile.add(new MenuItem("-"));
                setupMenuComponent("Exit", menuFile);
            }
            setupMenuComponent("Readme", menuHelp);
        }            

        Panel panelNorth = new Panel();
        panelNorth.add(new Label("Font:", Label.RIGHT));
        fontList = new FontList(7);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();
        for (i = 0; i < fontNames.length; i++) {
            if (fontNames[i].equals("") == false) {
                Font f = new Font(fontNames[i], Font.PLAIN, 16);
                // REMIND: Solaris crashes trying to access fonts here
                if (!System.getProperty("os.name").startsWith("Sun")) {
                    if (canDisplayRange(f, ASCII_BASE)) {
                        fontList.add("*" + fontNames[i]);
                    } else {
                        fontList.add(fontNames[i]);
                    }
                } else {
                    fontList.add(fontNames[i]);
                }
            }
        }
        fontList.addItemListener(this);
        fontList.select(0);
        panelNorth.add(fontList);
        panelNorth.add(new Label("      "));

        Panel panelFont = new Panel(new GridLayout(5, 2));
        panelFont.add(new Label("Style:", Label.RIGHT));
        styleList = new Choice();
        for (i = 0; i < styleNames.length; i++) {
            styleList.addItem(styleNames[i]);
        }
        styleList.addItemListener(this);
        panelFont.add(styleList);
        panelFont.add(new Label("Size:", Label.RIGHT));
        tfFontSize = new TextField("16", 3);
        tfFontSize.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tfFontSize.addActionListener(this);
        panelFont.add(tfFontSize);
        panelFont.add(new Label("Transform:", Label.RIGHT));
        transformList = new Choice();
        for (i = 0; i < transformNames.length; i++) {
            transformList.addItem(transformNames[i]);
        }
        transformList.addItemListener(this);
        panelFont.add(transformList);   
        panelFont.add(new Label(""));
        panelFont.add(new Label(""));
        cbAntialias = new Checkbox("Antialiasing");
        cbAntialias.addItemListener(this);
        panelFont.add(cbAntialias);
        cbFracMetrics = new Checkbox("Fractional Metrics");
        cbFracMetrics.addItemListener(this);
        panelFont.add(cbFracMetrics);
        panelNorth.add(panelFont);

        Panel panelRange = new Panel(new GridLayout(4, 2));
        panelRange.add(new Label("Unicode Range:", Label.RIGHT));
        rangeList = new Choice();
        for (i = 0; i < rangeNames.length; i++) {
            rangeList.addItem(rangeNames[i]);
        }
        rangeList.addItemListener(this);
        panelRange.add(rangeList);
        panelRange.add(new Label("Unicode Base:", Label.RIGHT));
        tfBaseText = new TextField(Integer.toHexString(ASCII_BASE), 4);
        tfBaseText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tfBaseText.addActionListener(this);
        tfBaseText.setEditable(false);
        panelRange.add(tfBaseText);
        panelRange.add(new Label("Draw Method:", Label.RIGHT));
        methodList = new Choice();
        for (i = 0; i < methodNames.length; i++) {
            methodList.addItem(methodNames[i]);
        }
        methodList.addItemListener(this);
        panelRange.add(methodList);
        panelRange.add(new Label("Display Mode:", Label.RIGHT));
        displayList = new Choice();
        for (i = 0; i < displayNames.length; i++) {
            displayList.addItem(displayNames[i]);
        }
        displayList.addItemListener(this);
        panelRange.add(displayList);
        panelNorth.add(panelRange);
        add("North", panelNorth);

        Font defaultFont = new Font(fontList.getSelectedItem(), 
                      ((Integer)styles.get(styleList.getSelectedItem())).intValue(), 
                      Integer.valueOf(tfFontSize.getText()).intValue());

        Panel panelCenter = new Panel();
        panelCenter.setLayout(new GridLayout(1, 1));
        ScrollPane spLeft = new ScrollPane();
        symbols = new Font2DCanvas(this, defaultFont, ASCII_BASE);
        spLeft.add(symbols);
        panelCenter.add(spLeft);
        add("Center", panelCenter);

        Panel panelEast = new Panel(new BorderLayout());
        taUnicodeText = new TextArea("", 10, 20, TextArea.SCROLLBARS_NONE);
        panelEast.add("Center", taUnicodeText);
        Panel panelSouthEast = new Panel();
        btnUpdateText = new Button("Update");
        btnUpdateText.addActionListener(this);
        panelSouthEast.add(btnUpdateText);
        panelEast.add("South", panelSouthEast);
        add("East", panelEast);
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (e.getItemSelectable().equals(fontList)) {
                setSymbolFont();
            } else if (e.getItemSelectable().equals(styleList)) {
                setSymbolFont();
            } else if (e.getItemSelectable().equals(rangeList)) {
                String rangeName = (String)e.getItem();
                if (rangeName.equals("Other...")) {
                    tfBaseText.setEditable(true);
                    symbols.setBase(Integer.valueOf(tfBaseText.getText(), 16).intValue());
                } else {
                    int rangeBase = ((Integer)ranges.get(rangeName)).intValue();
                    symbols.setBase(rangeBase);
                    tfBaseText.setText(Integer.toHexString(rangeBase));
                    tfBaseText.setEditable(false);
                }
                // REMIND: Doesn't work on Solaris
                if (!System.getProperty("os.name").startsWith("Sun")) {
                    setupFontList();
                }
            } else if (e.getItemSelectable().equals(transformList)) {
                setSymbolFont();
            } else if (e.getItemSelectable().equals(methodList)) {
                symbols.setMethod((String)e.getItem());
            } else if (e.getItemSelectable().equals(displayList)) {
                symbols.setDisplayType((String)e.getItem());
            }
        }

        if (e.getItemSelectable().equals(cbAntialias)) {
            symbols.setAntialiasing(cbAntialias.getState());
        } else if (e.getItemSelectable().equals(cbFracMetrics)) {
            symbols.setFractionalMetrics(cbFracMetrics.getState());
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(tfBaseText)) {
            try {
                symbols.setBase(Integer.valueOf(e.getActionCommand(), 16).intValue());
            } catch (NumberFormatException nfe) {
                Toolkit.getDefaultToolkit().beep();
                tfBaseText.select(0, Integer.MAX_VALUE);
            }
            // REMIND: Doesn't work on Solaris
            if (!System.getProperty("os.name").startsWith("Sun")) {
                setupFontList();
            }
        } else if (e.getSource().equals(tfFontSize)) {
            try {
                setSymbolFont();
            } catch (NumberFormatException nfe) {
                Toolkit.getDefaultToolkit().beep();
                tfFontSize.select(0, Integer.MAX_VALUE);
            }
        } else if (e.getSource().equals(btnUpdateText)) {
            symbols.setDisplayText(convertControlCodes(taUnicodeText.getText()));
            displayList.select("User Text");
            symbols.setDisplayType("User Text");
        } else {
            String action = e.getActionCommand();

            if (action.equals("Read Image Data...")) {
                onReadData();
            } else if (action.equals("Write Image Data...")) {
                onWriteData();
            } else if (action.equals("Read Text Data...")) {
                onReadTextData();
            } else if (action.equals("Read Control Data...")) {
                onReadControlData();      
            } else if (action.equals("Write Control Data...")) {
                onWriteControlData();
            } else if (action.equals("Page Setup...")) {
                onPageSetup();
            } else if (action.equals("Print...")) { 
                onPrint();
            } else if (action.equals("Exit")) {
                System.exit(0);
            } else if (action.equals("Readme")) {
                Font2DTestTextFrame sttf = new Font2DTestTextFrame("README",
                                                               "README.txt");
            }
        }
    }

    public void onReadData() {
        FileDialog lfd = new FileDialog(getFrame(), 
                                        "Load data...", FileDialog.LOAD);
        lfd.show();

        String filename = lfd.getFile(); 
        if (filename != null) {
            String filepath = lfd.getDirectory() + lfd.getFile();
                 
            if (filename.endsWith(".jpg")) {
                ImageFrame iframe = new ImageFrame(lfd.getFile(), filepath);
            } 
        }
    }

    public void onWriteData() {
        String fontname = fontList.getSelectedItem();
        if (fontname.startsWith("*")) {
            fontname = fontname.substring(1, fontname.length());
        }
            
        FileDialog sfd = new FileDialog(getFrame(), 
                                        "Save data...", FileDialog.SAVE);
        sfd.setFile(fontname + "." + 
                    styleList.getSelectedItem() + "." + 
                    tfFontSize.getText() + "." + 
                    tfBaseText.getText() + ".jpg");
        sfd.show();
        if (sfd.getFile() != null) {
            symbols.writeSymbolImage(sfd.getDirectory() + sfd.getFile());
        }
    }

    public void onReadControlData() {
        FileDialog lfd = new FileDialog(getFrame(), "Load control data...",
                                        FileDialog.LOAD);
        lfd.show();
        if (lfd.getFile() != null) {
            readControlInfo(lfd.getDirectory() + lfd.getFile());
        }          
    }

    public void onWriteControlData() {
        FileDialog sfd = new FileDialog(getFrame(), "Save control data...", 
                                        FileDialog.SAVE);
        sfd.setFile("control.data");
        sfd.show();
        if (sfd.getFile() != null) {
            writeControlInfo(sfd.getDirectory() + sfd.getFile());
        }           
    }

    public void onReadTextData() {
        FileDialog lfd = new FileDialog(getFrame(), 
                                        "Load data...", FileDialog.LOAD);
        lfd.show();

        String filename = lfd.getFile(); 
        if (filename != null) {
            String filepath = lfd.getDirectory() + lfd.getFile();
                 
            symbols.setDisplayText(getFileTextVector(filepath)); 
        }        
    }

    public void onPageSetup() {
        if (printerJob == null) {
            printerJob = PrinterJob.getPrinterJob();
        } 
        if (pageFormat == null) {
            pageFormat = printerJob.defaultPage();
        }

        pageFormat = printerJob.pageDialog(pageFormat);
    }

    public void onPrint() {
        if (printerJob == null) {
            printerJob = PrinterJob.getPrinterJob();
        } 
        if (pageFormat == null) {
            pageFormat = printerJob.defaultPage();
        }

        printerJob.setPrintable(symbols, pageFormat);

        if (printerJob.printDialog()) {
            try {
                printerJob.print();
            } catch (PrinterException pe) {
                System.err.println("Error printing the current symbol canvas");
            }
        }

        printerJob = null;
    }

    private Vector convertControlCodes(String str) {
        Vector retvec = new Vector();
        String holdstr;
        String retstr; 
        StringTokenizer lnst = new StringTokenizer(str, "\n");
        StringTokenizer ccst;
        while (lnst.hasMoreTokens()) {
            retstr = "";
            holdstr = lnst.nextToken();
            ccst = new StringTokenizer(holdstr, "\\");
            while (ccst.hasMoreTokens()) {
                holdstr = ccst.nextToken(); 
                if (holdstr.startsWith("u") && (holdstr.length() >= 5)) {
                    retstr += (char)Integer.valueOf(holdstr.substring(1, 5), 16).intValue() +
                              (holdstr.substring(5, holdstr.length()));
                } else {
                    retstr += holdstr;
                }
            }
            retvec.add(retstr);
        }  
        return retvec;
    }

    public void setSymbolFont() {
        String fontname = fontList.getSelectedItem();
        if (fontname.startsWith("*")) {
            fontname = fontname.substring(1, fontname.length());
        }

        symbols.setFont(new Font(fontname, 
                            ((Integer)styles.get(styleList.getSelectedItem())).intValue(), 
                            Integer.valueOf(tfFontSize.getText()).intValue()), 
                        transformList.getSelectedItem());
    }

    protected boolean canDisplayRange(Font font, int rangeBase) {
        for (int i = rangeBase; i < (rangeBase + 256); i++) {       
            if (font.canDisplay((char)i)) {
                return true;
            } 
        }
        return false;
    } 

    private void setupFontList() {
        int base = Integer.valueOf(tfBaseText.getText(), 16).intValue();
        int selected = fontList.getSelectedIndex();
        String fontname;
        Font f;

        for (int i = 0; i < fontList.getItemCount(); i++) {
            fontname = fontList.getItem(i);
            if (fontname.startsWith("*")) {
                fontname = fontname.substring(1, fontname.length());
            }

            f = new Font(fontname, Font.PLAIN, 16);
            // REMIND: Solaris crashes trying to access fonts here
            if (canDisplayRange(f, base)) {
                fontList.replaceItem("*" + fontname, i);
            } else {
                fontList.replaceItem(fontname, i);
            }    
        }

        fontList.select(selected);
    }

    protected Vector getFileTextVector(String filename) {
        String holdString;
        Vector stringVector = new Vector();

        // read UTF8 encoded files
        try {
            FileInputStream fis = new FileInputStream(filename);
            InputStreamReader isr = new InputStreamReader(fis, "UTF8");
            BufferedReader in = new BufferedReader(isr);
            while ((holdString = in.readLine()) != null) {
                stringVector.addElement(holdString);
            }
            in.close();
        } catch (java.io.IOException ioe) {
            System.err.println("Error reading text from file: " + filename);
        }         

        return stringVector;
    }

    private void readControlInfo(String filename) {
        String holdstr;
        String fontname;

        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            if (!in.readLine().equals("# Font2DTest control info")) {
                System.err.println("Invalid control info file: " + filename);
                in.close();
                return;
            }
            holdstr = in.readLine();
            for (int i = 0; i < fontList.getItemCount(); i++) {
                fontname = fontList.getItem(i);
                if (fontname.startsWith("*")) {
                    fontname = fontname.substring(1, fontname.length());
                }

                if (fontname.equals(holdstr)) {
                    fontList.select(i);
                    fontList.makeVisible(i);
                    break;
                }
            }
            styleList.select(in.readLine());
            tfFontSize.setText(in.readLine());
            transformList.select(in.readLine());
            cbAntialias.setState(in.readLine().equals("AAOn"));
            cbFracMetrics.setState(in.readLine().equals("FMOn"));
            rangeList.select(in.readLine());
            tfBaseText.setText(in.readLine());
            methodList.select(in.readLine());
            displayList.select(in.readLine());
            in.close();
        } catch (java.io.IOException ioe) {
            System.err.println("Error reading control text from file: " + 
                                   filename);
        } 

        setSymbolFont();
        symbols.setBase(Integer.valueOf(tfBaseText.getText(), 16).intValue());
        symbols.setMethod(methodList.getSelectedItem());
        symbols.setAntialiasing(cbAntialias.getState());
        symbols.setFractionalMetrics(cbFracMetrics.getState());
        symbols.setDisplayType(displayList.getSelectedItem());
        // REMIND: Doesn't work on Solaris
        if (!System.getProperty("os.name").startsWith("Sun")) {
            setupFontList();
        }
    }

    private void writeControlInfo(String filename) {
        String fontname;

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write("# Font2DTest control info\n");

            fontname = fontList.getSelectedItem();
            if (fontname.startsWith("*")) {
                fontname = fontname.substring(1, fontname.length());
            }       

            out.write(fontname + "\n");
            out.write(styleList.getSelectedItem() + "\n");
            out.write(tfFontSize.getText() + "\n");
            out.write(transformList.getSelectedItem() + "\n");
            out.write((cbAntialias.getState() ? "AAOn" : "AAOff") + "\n");
            out.write((cbFracMetrics.getState() ? "FMOn" : "FMOff") + "\n");
            out.write(rangeList.getSelectedItem() + "\n");
            out.write(tfBaseText.getText() + "\n");
            out.write(methodList.getSelectedItem() + "\n");
            out.write(displayList.getSelectedItem() + "\n");
            out.close();
        } catch (java.io.IOException ioe) {
            System.err.println("Error writing control text to file: " + 
                                   filename);
        } 
    }

    private void setupMenuComponent(String text, Menu menu) {
        MenuItem mi = new MenuItem(text);
        mi.setActionCommand(text);
        menu.add(mi);
    }

    public Frame getFrame() {
        Component c = this;
        while (c != null && !(c instanceof Frame)) {
            c = c.getParent();
        }
        return (Frame) c;
    }

    static class MyAdapter extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }

    public static void main(String args[]) {
        Font2DTest font2dTest = new Font2DTest();
        Frame f = new Frame("Font2DTest");

        f.add("Center", font2dTest);

        font2dTest.init();
        font2dTest.start();

        f.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = 850;
        int h = 700;
        f.setLocation(screenSize.width/2 - w/2, screenSize.height/2 - h/2);
        f.setSize(w, h);
        f.addWindowListener(new MyAdapter());
        f.show();
    }
}

class Font2DCanvas extends Canvas implements Printable {
    Font font;
    int charHeight;
    int charWidth;
    int charBase;
    int drawMethod;
    int displayType;
    int pageImgHeight;
    int pageHeight;
    Object antialias;
    Object fracMetrics;
    BufferedImage offscr;
    boolean hasChanged;
    Vector dispText;
    Vector userText;
    Vector resourceText;
    Font2DTest font2dtest;

    static final int DRAW_CHARS           = 0;
    static final int DRAW_STRING_STRING   = 1;
    static final int DRAW_STRING_ITERATOR = 2;
    static final int DRAW_GLYPH_VECTOR    = 3;
    static final int DRAW_BYTES           = 4;
    static final int DRAW_TEXT_LAYOUT     = 5;

    static final int DISPLAY_RANGE     = 0;
    static final int DISPLAY_TEXT      = 1;
    static final int DISPLAY_GLYPHS    = 2;
    static final int DISPLAY_RESOURCES = 3;

    public Font2DCanvas(Font2DTest f2dt, Font font, int base) {
        this.font2dtest = f2dt;
        this.font     = font;
        charWidth     = 1;
        charHeight    = 1;
        pageImgHeight = 0;
        pageHeight    = 0;
        charBase      = base;
        hasChanged    = true;
        antialias     = RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
        fracMetrics   = RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
        dispText      = new Vector();
        dispText.add("This is Java 2D!");
        userText      = dispText;
        drawMethod    = DRAW_CHARS;
        displayType   = DISPLAY_RANGE;
        setupResourceStrings();
        repaint();
    }

    public void setBase(int base) {
        charBase = base;
        hasChanged = true;
        repaint();
    }

    public void setFont(Font font, String transform) {
        this.font = font;
        if (transform.equals("None") == false) {
            setFontTransform(transform);
        }
        hasChanged = true;
        resizeCanvas(false);
        repaint();
    }

    public void setFontTransform(String transform) {
        AffineTransform at = new AffineTransform();

        if (transform.equals("Translate")) {
            at.translate(10, 10);
        } else if (transform.equals("Rotate")) {
            at.rotate(Math.PI / 6);
        } else if (transform.equals("Scale")) {
            at.scale(2, 2);
        } else if (transform.equals("Shear")) {
            at.shear(.4, 0);
        }

        font = font.deriveFont(at);
    }

    public void setMethod(String method) {
        if (method.equals("drawChars()")) {
            drawMethod = DRAW_CHARS;
        } else if (method.equals("drawString(String)")) {
            drawMethod = DRAW_STRING_STRING;
        } else if (method.equals("drawString(Iterator)")) {
            drawMethod = DRAW_STRING_ITERATOR;
        } else if (method.equals("drawGlyphVector()")) {
            drawMethod = DRAW_GLYPH_VECTOR;
        } else if (method.equals("drawBytes()")) {
            drawMethod = DRAW_BYTES;
        } else if (method.equals("TextLayout.draw()")) {
            drawMethod = DRAW_TEXT_LAYOUT;
        }

        hasChanged = true;
        repaint();
    }

    public void setDisplayType(String type) {
        if (type.equals("Unicode Range")) {
            displayType = DISPLAY_RANGE;
        } else if (type.equals("User Text")) {
            dispText = userText;
            displayType = DISPLAY_TEXT;
        } else if (type.equals("All Glyphs")) {
            displayType = DISPLAY_GLYPHS;
        } else if (type.equals("Resource Text")) {
            displayType = DISPLAY_TEXT;
            userText = dispText;
            dispText = resourceText;
        }

        hasChanged = true;
        repaint();
    }
          
    public void setDisplayText(Vector textVector) {
        userText = textVector;
        dispText = userText;

        if (displayType == DISPLAY_TEXT) {
            hasChanged = true;
            repaint();
        }
    }

    protected void setupResourceStrings() {
        ResourceBundle rb;
        String holdString;
        String filename = "./resources/resource.data";
        resourceText = new Vector();

        File f = new File(filename);
        if (!f.exists()) {
            resourceText.add("Valid resource.data file needed for resource text");
            return;
        }

        try {
            BufferedReader in = new BufferedReader(new FileReader(f));
            while ((holdString = in.readLine()) != null) {
                if (holdString.length() >= 5) {
                    rb = ResourceBundle.getBundle("resources.TextResources", 
                                     new Locale(holdString.substring(0, 2),
                                                holdString.substring(3, 5)));
                    resourceText.add(rb.getString("string"));
                }
            }
            in.close();
        } catch (java.io.IOException ioe) {
            resourceText.add("Error reading resource text from file: " + 
                                 filename);
        } catch (java.util.MissingResourceException mre) {
            resourceText.add("Missing resource files... (*.properties files)");
        }
    }

    public void setAntialiasing(boolean aa) {
        antialias = (aa ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON :
                          RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        hasChanged = true;
        repaint();
    }

    public void setFractionalMetrics(boolean fm) {
        fracMetrics = (fm ? RenderingHints.VALUE_FRACTIONALMETRICS_ON :
                            RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        hasChanged = true;
        repaint();
    }

    public void resizeCanvas(boolean printing) {
        Graphics2D g2d = (Graphics2D)getGraphics();
        if (g2d != null) {
            int canvasWidth = 0;
            int canvasHeight = 0;

            g2d.setFont(font);
            RenderingHints hints = new RenderingHints(
                      RenderingHints.KEY_TEXT_ANTIALIASING, antialias);
            hints.put(RenderingHints.KEY_FRACTIONALMETRICS, fracMetrics);
            g2d.setRenderingHints(hints);
            FontRenderContext frc = g2d.getFontRenderContext();
            Rectangle2D rect = g2d.getFont().getMaxCharBounds(frc);
            if (displayType == DISPLAY_RANGE) {
                charWidth = (int)rect.getWidth() + 4;
                charHeight = (int)rect.getHeight() + 3;
                canvasWidth = (charWidth * 21) + 10;
                canvasHeight = (charHeight * 16) + getParentHeight();
            } else if (displayType == DISPLAY_TEXT) {
                int maxLineWidth = 1;
                String holdString;
                charWidth = (int)rect.getWidth();
                charHeight = (int)rect.getHeight() + 2;
                for (int i = 0; i < dispText.size(); i++) {
                    holdString = (String)dispText.elementAt(i);
                    if (holdString.length() > maxLineWidth) {
                        maxLineWidth = holdString.length();
                    }
                }
                canvasWidth = charWidth * maxLineWidth;
                canvasHeight = (charHeight * dispText.size()) + getParentHeight();
            } else {
                int numGlyphs = g2d.getFont().getNumGlyphs();
                charWidth = (int)rect.getWidth() + 4;
                charHeight = (int)rect.getHeight() + 3;
                canvasWidth = (charWidth * 21) + 10;
                canvasHeight = (charHeight * ((numGlyphs / 16) + 1)) + getParentHeight();
            }
        
            if (!printing) {
                setSize(canvasWidth, canvasHeight);
                resizeParent();
            }
        }
    }

    public void resizeParent() {
        Component c = getParent();
        if (c instanceof ScrollPane) {
            c.validate();
        }
    }

    public int getParentHeight() {
        Component c = getParent();
        if (c != null) {
            return c.getHeight();
        } else {
            return 0;
        }
    }

    public void writeSymbolImage(String filename) {
        try {
            FileOutputStream out = new FileOutputStream(filename);
            BufferedImage image = (BufferedImage)createImage(getWidth(), 
                                                             getHeight());
            Graphics g = image.getGraphics();
            paint(g);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam jep = encoder.getDefaultJPEGEncodeParam(image);
            jep.setQuality(1.0f, false);
            encoder.setJPEGEncodeParam(jep);
            encoder.encode(image);
            g.dispose();
            out.close();
        } catch (java.io.FileNotFoundException fnfe) {
            System.err.println("File not found: " + filename);
        } catch (java.io.IOException ioe) {
            System.err.println("Could not write file: " + filename);
        }
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        if ((offscr == null) || (hasChanged == true)) {
            hasChanged = false;
            resizeCanvas(false);
            Dimension d = getSize();
            offscr = (BufferedImage)createImage(d.width, d.height);     
            Graphics2D offg2d = offscr.createGraphics();
            offg2d.setColor(getBackground());
            offg2d.fill(new Rectangle(0, 0, d.width, d.height));
            offg2d.setFont(font);
            offg2d.setColor(Color.black);
            RenderingHints hints = new RenderingHints(
                      RenderingHints.KEY_TEXT_ANTIALIASING, antialias);
            hints.put(RenderingHints.KEY_FRACTIONALMETRICS, fracMetrics);
            offg2d.setRenderingHints(hints);

            if (displayType == DISPLAY_RANGE) {
                paintRangeOffscreen(offg2d, 0, false);
            } else if (displayType == DISPLAY_TEXT) {
                paintTextOffscreen(offg2d, 0, false);
            } else {
                paintGlyphsOffscreen(offg2d, 0, false);
            }
        }
        g2d.drawImage(offscr, 0, 0, null);
    }

    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        Graphics2D g2d = (Graphics2D)g;

        resizeCanvas(true);

        pageImgHeight = (int)pageFormat.getImageableHeight();
        pageHeight = (int)pageFormat.getHeight();

        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g2d.setFont(font);
        g2d.setColor(Color.black);
        RenderingHints hints = new RenderingHints(
                  RenderingHints.KEY_TEXT_ANTIALIASING, antialias);
        hints.put(RenderingHints.KEY_FRACTIONALMETRICS, fracMetrics);
        g2d.setRenderingHints(hints);

        if (displayType == DISPLAY_RANGE) {
            return paintRangeOffscreen(g2d, pageIndex, true);
        } else if (displayType == DISPLAY_TEXT) {
            return paintTextOffscreen(g2d, pageIndex, true);
        } else {
            return paintGlyphsOffscreen(g2d, pageIndex, true);
        }
    }

    public int paintRangeOffscreen(Graphics2D g2d, 
                                   int pageIndex, boolean printing) {
        FontRenderContext frc = g2d.getFontRenderContext();
        Font labelFont = new Font("Monospaced", Font.PLAIN, 12);
        char[] carray = new char[1];
        int c = charBase;
        int x = 0;
        int y = 0;
        int yp = charHeight;
        int yh = getHeaderLineHeight(g2d) * 4;
        int pitop = (pageImgHeight - yh - charHeight) * pageIndex;
        int pibot = (pageImgHeight - yh - charHeight) * (pageIndex + 1);
        boolean modified = false;

        g2d.setFont(labelFont);
        int startx = (int)g2d.getFont().getStringBounds("0000", frc).getWidth() 
                         + charWidth;
        g2d.setFont(font);

        for (int v = 0; v < 16; v++) {
            if (printing) {
                if (yp >= pibot) {
                    return Printable.PAGE_EXISTS; 
                } else if (yp < pitop) {
                    yp += charHeight;
                    c += 16;
                    continue;
                } else {
                    if (!modified) {
                        paintHeader(g2d, pageIndex);
                        y = yh + charHeight;
                    } else {
                        y += charHeight;
                    }
                    modified = true;
                }
            } else {
                y = yp;
            }

            g2d.setFont(labelFont);
            g2d.drawString(Integer.toHexString(c), 10, y);
            g2d.setFont(font);
            x = startx;
            
            for (int h = 0; h < 16; h++) {
                carray[0] = (char)c++;
                if (g2d.getFont().canDisplay(carray[0]) || printing) {
                    paintString(g2d, new String(carray, 0, 1), x, y);
                } else {
                    g2d.setColor(Color.red);
                    paintString(g2d, new String(carray, 0, 1), x, y);
                    g2d.setColor(Color.black);
                } 
                x += charWidth;
            }
            yp += charHeight;
        }

        if (modified) {
            return Printable.PAGE_EXISTS;
        } else {
            return Printable.NO_SUCH_PAGE;
        }
    }

    public int paintTextOffscreen(Graphics2D g2d, 
                                   int pageIndex, boolean printing) {
        String holdString;
        int y = 0;
        int yp = charHeight;
        int yh = getHeaderLineHeight(g2d) * 4;
        int pitop = (pageImgHeight - yh - charHeight) * pageIndex;
        int pibot = (pageImgHeight - yh - charHeight) * (pageIndex + 1);
        boolean modified = false;

        for (int i = 0; i < dispText.size(); i++) {
            if (printing) {
                if (yp >= pibot) {
                    return Printable.PAGE_EXISTS; 
                } else if (yp < pitop) {
                    yp += charHeight;
                    continue;
                } else {
                    if (!modified) {
                        paintHeader(g2d, pageIndex);
                        y = yh + charHeight;
                    } else {
                        y += charHeight;
                    }
                    modified = true;
                }
            } else {
                y = yp;
            }

            holdString = (String)dispText.elementAt(i);
            paintString(g2d, holdString, charWidth, y);
            yp += charHeight;
        }

        if (modified) {
            return Printable.PAGE_EXISTS;
        } else {
            return Printable.NO_SUCH_PAGE;
        }
    }

    public int paintGlyphsOffscreen(Graphics2D g2d, 
                                    int pageIndex, boolean printing) {
        FontRenderContext frc = g2d.getFontRenderContext();
        Font g2dFont = g2d.getFont();
        int[] glyphIndices = new int[1];
        int numGlyphs = g2d.getFont().getNumGlyphs();
        int numRows = (((numGlyphs % 16) == 0) ? (numGlyphs / 16) : 
                                                 (numGlyphs / 16) + 1);
        int x = 0;
        int y = 0;
        int yp = charHeight;
        int yh = getHeaderLineHeight(g2d) * 4;
        int pitop = (pageImgHeight - yh - charHeight) * pageIndex;
        int pibot = (pageImgHeight - yh - charHeight) * (pageIndex + 1);
        Font labelFont = new Font("Monospaced", Font.PLAIN, 12);
        GlyphVector gv;
        boolean modified = false;

        g2d.setFont(labelFont);
        int startx = (int)g2d.getFont().getStringBounds("0000", frc).getWidth() 
                         + charWidth;
        g2d.setFont(g2dFont);

        for (int h = 0; h < numRows; h++) {
            if (printing) {
                if (yp >= pibot) {
                    return Printable.PAGE_EXISTS; 
                } else if (yp < pitop) {
                    yp += charHeight;
                    continue;
                } else {
                    if (!modified) {
                        paintHeader(g2d, pageIndex);
                        y = yh + charHeight;
                    } else {
                        y += charHeight;
                    }
                    modified = true;
                }
            } else {
                y = yp;
            }

            g2d.setFont(labelFont);
            g2d.drawString(Integer.toHexString(h * 16), 10, y);
            g2d.setFont(g2dFont);
            x = startx;

            for (int v = 0; v < 16; v++) {
                if (numGlyphs > ((h * 16) + v)) {
                    glyphIndices[0] = (h * 16) + v;
                    gv = g2dFont.createGlyphVector(frc, glyphIndices);
                    g2d.drawGlyphVector(gv, x, y);
                } else {
                    g2d.setColor(Color.red);
                    glyphIndices[0] = g2dFont.getMissingGlyphCode();
                    gv = g2dFont.createGlyphVector(frc, glyphIndices);
                    g2d.drawGlyphVector(gv, x, y);
                    g2d.setColor(Color.black);
                }
                x += charWidth;
            }
            yp += charHeight;
        }    
        
        if (modified) {
            return Printable.PAGE_EXISTS;
        } else {
            return Printable.NO_SUCH_PAGE;
        }
    }

    protected int getHeaderLineHeight(Graphics2D g2d) {
        Font currentFont = g2d.getFont();

        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
        FontRenderContext frc = g2d.getFontRenderContext();
        Rectangle2D rect = g2d.getFont().getMaxCharBounds(frc);  

        g2d.setFont(currentFont);

        return (int)rect.getHeight() + 2; 
    }

    public int paintHeader(Graphics2D g2d, int pageIndex) {
        String str;
        Font currentFont = g2d.getFont();
        
        g2d.setFont(new Font("Serif", Font.PLAIN, 12));
        FontRenderContext frc = g2d.getFontRenderContext();
        Rectangle2D rect = g2d.getFont().getMaxCharBounds(frc);
        int h = getHeaderLineHeight(g2d);

        String fontstr = font2dtest.fontList.getSelectedItem();
        if (fontstr.startsWith("*")) {
            fontstr = fontstr.substring(1, fontstr.length());
        }

        str = "Font: " + fontstr + " (" + 
                         font2dtest.styleList.getSelectedItem() + " " +
                         font2dtest.tfFontSize.getText() + ")";
        g2d.drawString(str, 5, h);

        str = "Properties: " +
              (font2dtest.cbAntialias.getState() ? "AAOn, " : "AAOff, ") +
              (font2dtest.cbFracMetrics.getState() ? "FMOn, " : "FMOff, ") +
              font2dtest.transformList.getSelectedItem();
        g2d.drawString(str, 5, h * 2);

        str = "Description: " +
              font2dtest.methodList.getSelectedItem() + " of " +
              font2dtest.displayList.getSelectedItem();
        if (displayType == DISPLAY_RANGE) {
            str += " for " + 
                   font2dtest.rangeList.getSelectedItem() + " (0x" +
                   font2dtest.tfBaseText.getText() + ")";
        }
        g2d.drawString(str, 5, h * 3);

        str = "Page: " + (pageIndex + 1);
        g2d.drawString(str, 5, h * 4);

        g2d.setFont(currentFont);

        return (h * 4);
    }

    public void paintString(Graphics2D g2d, String str, int x, int y) {
        int len = str.length();
        FontRenderContext frc = g2d.getFontRenderContext();

        switch (drawMethod) {
        case DRAW_GLYPH_VECTOR:
            GlyphVector gv = g2d.getFont().createGlyphVector(frc, str);
            g2d.drawGlyphVector(gv, (float)x, (float)y);
            break;
        case DRAW_STRING_STRING:
            g2d.drawString(str, x, y);
            break;
        case DRAW_STRING_ITERATOR:
            AttributedString as = new AttributedString(str);
            as.addAttribute(TextAttribute.FONT, g2d.getFont());
            AttributedCharacterIterator aci = as.getIterator();
            g2d.drawString(aci, x, y);
            break;
        case DRAW_CHARS:
            char[] carray = new char[1024];
            if (len > 0) {
                str.getChars(0, len, carray, 0);
                g2d.drawChars(carray, 0, len, x, y);
            }
            break;
        case DRAW_BYTES:
            byte[] barray;
            if (len > 0) {
                barray = str.getBytes();
                g2d.drawBytes(barray, 0, len, x, y);
            }
            break;
        case DRAW_TEXT_LAYOUT:
            TextLayout tl = new TextLayout(str, g2d.getFont(), frc);
            tl.draw(g2d, x, y);
            break;
        default:
            break;
        }
    }
}

class ImageCanvas extends Canvas {
    BufferedImage image;

    public ImageCanvas() {
    }

    public void setImage(String filename) {
        try {
            FileInputStream in = new FileInputStream(filename);
            JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
            image = decoder.decodeAsBufferedImage();
            in.close();
        } catch (java.io.IOException ioe) {
            image = null;
            System.err.println("Could not load image file: " + filename);
        } catch (ImageFormatException ife) {
            image = null;
            System.err.println("Bad image format: " + filename);
        }

        if (image != null) {
            setSize(image.getWidth(), image.getHeight());
            resizeParent();
        }
        repaint();
    }

    public void resizeParent() {
        Component c = getParent();
        if (c instanceof ScrollPane) {
            c.validate();
        }
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

        if (image != null) {
            g2d.drawImage(image, 0, 0, this);
        } else {
            g2d.setPaint(g2d.getBackground());
            g2d.fill(new Rectangle(0, 0, getWidth(), getHeight()));
        }
    }
}

class ImageFrame extends Frame {
    ImageCanvas imageCanvas;
    ScrollPane scrollPane;

    public ImageFrame(String title, String filename) {
        super(title);
        imageCanvas = new ImageCanvas();
        imageCanvas.setImage(filename);
        scrollPane = new ScrollPane();
        scrollPane.add(imageCanvas);
        add("Center", scrollPane);
        pack();
        setSize(400, 500);
        show();
        addWindowListener(new MyAdapter());
    }

    static class MyAdapter extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            e.getWindow().dispose();
        }
    }
}

class FontList extends List {
    public FontList(int rows) {
        super(rows);
    }

    public Dimension getPreferredSize() {
        return new Dimension(200, 125);
    }
}

class Font2DTestTextFrame extends Frame {
    
    public Font2DTestTextFrame(String title, String filename) {
        super(title);
        TextArea ta = new TextArea(24, 80);  
        ta.setEditable(false);
        String text = "";
        String holdString;
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));

            while ((holdString = in.readLine()) != null) {
                text += holdString + "\n";
            }

            in.close();

            ta.setText(text);
            add("Center", ta);
            pack();
            show();
            addWindowListener(new MyAdapter());
        } catch (java.io.IOException ioe) {
            System.err.println("Error reading text from file: " + filename);
        }         
    }

    static class MyAdapter extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            e.getWindow().dispose();
        }
    }
}

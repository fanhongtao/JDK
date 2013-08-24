/*
 * @(#)SwingUtilities2.java	1.29 06/04/18
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing;

import java.security.*;
import java.lang.reflect.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.print.PrinterGraphics;
import java.text.AttributedCharacterIterator;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultCaret;
import javax.swing.table.TableCellRenderer;
import sun.swing.PrintColorUIResource;
import sun.print.ProxyPrintGraphics;
import sun.awt.AppContext;
import sun.font.FontDesignMetrics;
import sun.java2d.SunGraphics2D;
import sun.security.action.GetPropertyAction;
import sun.security.util.SecurityConstants;
import java.io.*;

/**
 * A collection of utility methods for Swing.
 * <p>
 * <b>WARNING:</b> While this class is public, it should not be treated as
 * public API and its API may change in incompatable ways between dot dot
 * releases and even patch releases. You should not rely on this class even
 * existing.
 *
 * @version 1.29 04/18/06
 */
public class SwingUtilities2 {
    // Maintain a cache of CACHE_SIZE fonts and the left side bearing
    // of the characters falling into the range MIN_CHAR_INDEX to
    // MAX_CHAR_INDEX. The values in fontCache are created as needed.
    private static LSBCacheEntry[] fontCache;
    // Windows defines 6 font desktop properties, we will therefore only
    // cache the metrics for 6 fonts.
    private static final int CACHE_SIZE = 6;
    // nextIndex in fontCache to insert a font into.
    private static int nextIndex;
    // LSBCacheEntry used to search in fontCache to see if we already
    // have an entry for a particular font
    private static LSBCacheEntry searchKey;

    // getLeftSideBearing will consult all characters that fall in the
    // range MIN_CHAR_INDEX to MAX_CHAR_INDEX.
    private static final int MIN_CHAR_INDEX = (int)'W';
    private static final int MAX_CHAR_INDEX = (int)'W' + 1;

    private static final FontRenderContext DEFAULT_FRC = new FontRenderContext(
                             null, false, false);

    /**
     * FontRenderContext with antialiased turned on.
     */
    public static final FontRenderContext AA_FRC;

    //
    // To determine if a JComponent should use AA text the following is
    // used:
    // 1. Is the system property 'swing.aatext' defined, return the value of
    //    the system property.
    // 2. Use the JComponent client property AA_TEXT_PROPERTY_KEY.  To 
    //    avoid having this property persist between look and feels changes
    //    the value of this property is set to false in JComponent.setUI
    //

    /**
     * Whether or not text is drawn anti-aliased.  This is only used if
     * <code>AA_TEXT_DEFINED</code> is true.
     */
    private static final boolean AA_TEXT;

    /**
     * Whether or not the system property 'swing.aatext' is defined.
     */
    private static final boolean AA_TEXT_DEFINED;

    /**
     * Key used in client properties to indicate whether or not the component
     * should use aa text.
     */
    public static final Object AA_TEXT_PROPERTY_KEY =
                          new StringBuffer("AATextPropertyKey");

    /**
     * Used to tell a text component, being used as an editor for table
     * or tree, how many clicks it took to start editing.
     */
    private static final StringBuilder SKIP_CLICK_COUNT =
        new StringBuilder("skipClickCount");

    /**
     * Whether or not the system proprety 'sun.swing.enableImprovedDragGesture'
     * is defined, indicating that we should enable the fix for 4521075
     * and start drag recognition on the first press without requiring a
     * selection.
     */
    public static final boolean DRAG_FIX;

    // security stuff
    private static Field inputEvent_CanAccessSystemClipboard_Field = null;
    private static final String UntrustedClipboardAccess = 
        "UNTRUSTED_CLIPBOARD_ACCESS_KEY";

    static {
        fontCache = new LSBCacheEntry[CACHE_SIZE];
        Object aa = java.security.AccessController.doPrivileged(
               new GetPropertyAction("swing.aatext"));
        AA_TEXT_DEFINED = (aa != null);
        AA_TEXT = "true".equals(aa);
        AA_FRC = new FontRenderContext(null, true, false);

        Object dragFix = java.security.AccessController.doPrivileged(
            new GetPropertyAction("sun.swing.enableImprovedDragGesture"));
        DRAG_FIX = (dragFix != null);
    }

    //
    // WARNING WARNING WARNING WARNING WARNING WARNING
    // Many of the following methods are invoked from older API. 
    // As this older API was not passed a Component, a null Component may
    // now be passsed in.  For example, SwingUtilities.computeStringWidth
    // is implemented to call SwingUtilities2.stringWidth, the
    // SwingUtilities variant does not take a JComponent, as such
    // SwingUtilities2.stringWidth can be passed a null Component.
    // In other words, if you add new functionality to these methods you
    // need to gracefully handle null.
    //

    /**
     * Returns whether or not text should be drawn antialiased.
     *
     * @param c JComponent to test.
     * @return Whether or not text should be drawn antialiased for the
     *         specified component.
     */
    private static boolean drawTextAntialiased(JComponent c) {
        if (!AA_TEXT_DEFINED) {
            if (c != null) {
                // Check if the component wants aa text
                return ((Boolean)c.getClientProperty(
                                  AA_TEXT_PROPERTY_KEY)).booleanValue();
            }
            // No component, assume aa is off
            return false;
        }
        // 'swing.aatext' was defined, use its value.
        return AA_TEXT;
    }

    /**
     * Returns whether or not text should be drawn antialiased.
     *
     * @param aaText Whether or not aa text has been turned on for the
     *        component.
     * @return Whether or not text should be drawn antialiased.
     */
    public static boolean drawTextAntialiased(boolean aaText) {
        if (!AA_TEXT_DEFINED) {
            // 'swing.aatext' wasn't defined, use the components aa text value.
            return aaText;
        }
        // 'swing.aatext' was defined, use its value.
        return AA_TEXT;
    }

    /**
     * Returns the left side bearing of the first character of string. The
     * left side bearing is calculated from the passed in
     * FontMetrics.  If the passed in String is less than one
     * character, this will throw a StringIndexOutOfBoundsException exception.
     *
     * @param c JComponent that will display the string
     * @param fm FontMetrics used to measure the String width
     * @param string String to get the left side bearing for.
     */
    public static int getLeftSideBearing(JComponent c, FontMetrics fm,
                                         String string) {
        return getLeftSideBearing(c, fm, string.charAt(0));
    }


    /**
     * Returns the left side bearing of the first character of string. The
     * left side bearing is calculated from the passed in FontMetrics.
     *
     * @param c JComponent that will display the string
     * @param fm FontMetrics used to measure the String width
     * @param char Character to get the left side bearing for.
     */
    public static int getLeftSideBearing(JComponent c, FontMetrics fm,
                                         char firstChar) {
        int charIndex = (int)firstChar;
        if (charIndex < MAX_CHAR_INDEX && charIndex >= MIN_CHAR_INDEX) {
            byte[] lsbs = null;

            FontRenderContext frc = getFRC(c, fm);
            Font font = fm.getFont();
            synchronized(SwingUtilities2.class) {
                LSBCacheEntry entry = null;
                if (searchKey == null) {
                    searchKey = new LSBCacheEntry(frc, font);
                }
                else {
                    searchKey.reset(frc, font);
                }
                // See if we already have an entry for this pair
                for (LSBCacheEntry cacheEntry : fontCache) {
                    if (searchKey.equals(cacheEntry)) {
                        entry = cacheEntry;
                        break;
                    }
                }
                if (entry == null) {
                    // No entry for this pair, add it.
                    entry = searchKey;
                    fontCache[nextIndex] = searchKey;
                    searchKey = null;
                    nextIndex = (nextIndex + 1) % CACHE_SIZE;
                }
                return entry.getLeftSideBearing(firstChar);
            }
        }
        return 0;
    }


    /**
     * Returns the FontMetrics for the current Font of the passed
     * in Graphics.  This method is used when a Graphics
     * is available, typically when painting.  If a Graphics is not
     * available the JComponent method of the same name should be used.
     * <p>
     * Callers should pass in a non-null JComponent, the exception
     * to this is if a JComponent is not readily available at the time of
     * painting.
     * <p>
     * This does not necessarily return the FontMetrics from the
     * Graphics.
     *
     * @param c JComponent requesting FontMetrics, may be null
     * @param g Graphics Graphics
     */
    public static FontMetrics getFontMetrics(JComponent c, Graphics g) {
        return getFontMetrics(c, g, g.getFont());
    }


    /**
     * Returns the FontMetrics for the specified Font.
     * This method is used when a Graphics is available, typically when
     * painting.  If a Graphics is not available the JComponent method of
     * the same name should be used.
     * <p>
     * Callers should pass in a non-null JComonent, the exception
     * to this is if a JComponent is not readily available at the time of
     * painting.
     * <p>
     * This does not necessarily return the FontMetrics from the
     * Graphics.
     *
     * @param c JComponent requesting FontMetrics, may be null
     * @param c Graphics Graphics
     * @param font Font to get FontMetrics for
     */
    public static FontMetrics getFontMetrics(JComponent c, Graphics g,
                                             Font font) {
        if (c != null) {
            // Note: We assume that we're using the FontMetrics
            // from the widget to layout out text, otherwise we can get
            // mismatches when printing.
            return c.getFontMetrics(font);
        }
        return Toolkit.getDefaultToolkit().getFontMetrics(font);
    }


    /**
     * Returns the width of the passed in String.
     *
     * @param c JComponent that will display the string, may be null
     * @param fm FontMetrics used to measure the String width
     * @param string String to get the width of
     */
    public static int stringWidth(JComponent c, FontMetrics fm, String string){
        return fm.stringWidth(string);
    }



    /**
     * Clips the passed in String to the space provided.
     *
     * @param c JComponent that will display the string, may be null
     * @param fm FontMetrics used to measure the String width
     * @param string String to display
     * @param availTextWidth Amount of space that the string can be drawn in
     * @return Clipped string that can fit in the provided space.
     */
    public static String clipStringIfNecessary(JComponent c, FontMetrics fm,
                                               String string,
                                               int availTextWidth) {
        if ((string == null) || (string.equals("")))  {
            return "";
        }
        int textWidth = SwingUtilities2.stringWidth(c, fm, string);
        if (textWidth > availTextWidth) {
            return SwingUtilities2.clipString(c, fm, string, availTextWidth);
        }
        return string;
    }


    /**
     * Clips the passed in String to the space provided.  NOTE: this assumes
     * the string does not fit in the available space.
     *
     * @param c JComponent that will display the string, may be null
     * @param fm FontMetrics used to measure the String width
     * @param string String to display
     * @param availTextWidth Amount of space that the string can be drawn in
     * @return Clipped string that can fit in the provided space.
     */
    public static String clipString(JComponent c, FontMetrics fm,
                                    String string, int availTextWidth) {
        // c may be null here.
        String clipString = "...";
        int width = SwingUtilities2.stringWidth(c, fm, clipString);
        // NOTE: This does NOT work for surrogate pairs and other fun
        // stuff
        int nChars = 0;
        for(int max = string.length(); nChars < max; nChars++) {
            width += fm.charWidth(string.charAt(nChars));
            if (width > availTextWidth) {
                break;
            }
        }
        string = string.substring(0, nChars) + clipString;
        return string;
    }

    /**
     * Returns the FontRenderContext for the passed in FontMetrics or
     * for the passed in JComponent if FontMetrics is null
     */
    private static FontRenderContext getFRC(JComponent c, FontMetrics fm) {
        // c may be null.
        if (fm instanceof FontDesignMetrics) {
            return ((FontDesignMetrics)fm).getFRC();
        }
        if (fm == null && c != null) {
            //we do it this way because we need first case to 
            //work as fast as possible
            return getFRC(c, c.getFontMetrics(c.getFont()));
        }

        // PENDING: This shouldn't really happen, but if it does we
        // should try and handle AA as necessary.
        assert false;
        return DEFAULT_FRC;
    }


    /**
     * Draws the string at the specified location.
     *
     * @param c JComponent that will display the string, may be null
     * @param g Graphics to draw the text to
     * @param text String to display
     * @param x X coordinate to draw the text at
     * @param y Y coordinate to draw the text at
     */
    public static void drawString(JComponent c, Graphics g, String text,
                                  int x, int y) {
        // c may be null

        // All non-editable widgets that draw strings call into this
        // methods.  By non-editable that means widgets like JLabel, JButton
        // but NOT JTextComponents.
        if ( text == null || text.length() <= 0 ) { //no need to paint empty strings
            return;
        }
        if (isPrinting(g)) {
            Graphics2D g2d = getGraphics2D(g);
            if (g2d != null) {
                TextLayout layout = new TextLayout(text, g2d.getFont(),
                                                   DEFAULT_FRC);

                /* Use alternate print color if specified */
                Color col = g2d.getColor();
                if (col instanceof PrintColorUIResource) {
                    g2d.setColor(((PrintColorUIResource)col).getPrintColor());
                }

                layout.draw(g2d, x, y);
                
                g2d.setColor(col);

                return;
            }
        } 

        // If we get here we're not printing
        if (drawTextAntialiased(c) && (g instanceof Graphics2D)) {
            Graphics2D g2 = (Graphics2D)g;
            Object oldAAValue = g2.getRenderingHint(
                                       RenderingHints.KEY_TEXT_ANTIALIASING);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.drawString(text, x, y);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                    oldAAValue);
        }
        else {
            g.drawString(text, x, y);
        }
    }


    /**
     * Draws the string at the specified location underlining the specified
     * character.
     *
     * @param c JComponent that will display the string, may be null
     * @param g Graphics to draw the text to
     * @param text String to display
     * @param underlinedIndex Index of a character in the string to underline
     * @param x X coordinate to draw the text at
     * @param y Y coordinate to draw the text at
     */
    public static void drawStringUnderlineCharAt(JComponent c,Graphics g,
                           String text, int underlinedIndex, int x,int y) {
        SwingUtilities2.drawString(c, g, text, x, y);
        if (underlinedIndex >= 0 && underlinedIndex < text.length() ) {
            // PENDING: this needs to change.
            FontMetrics fm = g.getFontMetrics();
            int underlineRectX = x + SwingUtilities2.stringWidth(c,
                                      fm, text.substring(0,underlinedIndex));
            int underlineRectY = y;
            int underlineRectWidth = fm.charWidth(text.
                                                  charAt(underlinedIndex));
            int underlineRectHeight = 1;
            g.fillRect(underlineRectX, underlineRectY + 1,
                       underlineRectWidth, underlineRectHeight);
        }
    }


    /**
     * A variation of locationToIndex() which only returns an index if the
     * Point is within the actual bounds of a list item (not just in the cell)
     * and if the JList has the "List.isFileList" client property set.
     * Otherwise, this method returns -1.
     * This is used to make WindowsL&F JFileChooser act like native dialogs.
     */
    public static int loc2IndexFileList(JList list, Point point) {
        int index = list.locationToIndex(point);
        if (index != -1) {
            Object bySize = list.getClientProperty("List.isFileList");
            if (bySize instanceof Boolean && ((Boolean)bySize).booleanValue() &&
                !pointIsInActualBounds(list, index, point)) {
                index = -1;
            }
        }
        return index;
    }


    /**
     * Returns true if the given point is within the actual bounds of the
     * JList item at index (not just inside the cell).
     */
    private static boolean pointIsInActualBounds(JList list, int index,
                                                Point point) {
        ListCellRenderer renderer = list.getCellRenderer();
        ListModel dataModel = list.getModel();
        Object value = dataModel.getElementAt(index);
        Component item = renderer.getListCellRendererComponent(list,
                          value, index, false, false);
        Dimension itemSize = item.getPreferredSize();
        Rectangle cellBounds = list.getCellBounds(index, index);
	if (!item.getComponentOrientation().isLeftToRight()) {
	    cellBounds.x += (cellBounds.width - itemSize.width);
	}
        cellBounds.width = itemSize.width;
        cellBounds.height = itemSize.height;

	return cellBounds.contains(point);
    }


    /**
     * Returns true if the given point is outside the preferredSize of the
     * item at the given row of the table.  (Column must be 0).
     * Does not check the "Table.isFileList" property. That should be checked
     * before calling this method.
     * This is used to make WindowsL&F JFileChooser act like native dialogs.
     */
    public static boolean pointOutsidePrefSize(JTable table, int row, int column, Point p) {
        if (table.convertColumnIndexToModel(column) != 0 || row == -1) {
            return true;
        }
        TableCellRenderer tcr = table.getCellRenderer(row, column);
        Object value = table.getValueAt(row, column);
        Component cell = tcr.getTableCellRendererComponent(table, value, false,
                false, row, column);
        Dimension itemSize = cell.getPreferredSize();
        Rectangle cellBounds = table.getCellRect(row, column, false);
        cellBounds.width = itemSize.width;
        cellBounds.height = itemSize.height;

        // See if coords are inside
        // ASSUME: mouse x,y will never be < cell's x,y
        assert (p.x >= cellBounds.x && p.y >= cellBounds.y);
        if (p.x > cellBounds.x + cellBounds.width ||
                p.y > cellBounds.y + cellBounds.height) {
            return true;
        }
        return false;
    }

    /**
     * Ignore mouse events if the component is null, not enabled, or the event
     * is not associated with the left mouse button.
     */
    public static boolean shouldIgnore(MouseEvent me, JComponent c) {
        return c == null || !c.isEnabled()
                         || !SwingUtilities.isLeftMouseButton(me);
    }

    /**
     * Request focus on the given component if it doesn't already have it
     * and <code>isRequestFocusEnabled()</code> returns true.
     */
    public static void adjustFocus(JComponent c) {
        if (!c.hasFocus() && c.isRequestFocusEnabled()) {
            c.requestFocus();
        }
    }

    /**
     * The following draw functions have the same semantic as the
     * Graphics methods with the same names. 
     * 
     * this is used for printing
     */
    public static int drawChars(JComponent c, Graphics g,
                                 char[] data,
                                 int offset,
                                 int length,
                                 int x,
                                 int y) { 
        if ( length <= 0 ) { //no need to paint empty strings
            return x;
        }
        int nextX = x + getFontMetrics(c, g).charsWidth(data, offset, length);
        if (isPrinting(g)) {
            Graphics2D g2d = getGraphics2D(g);
            if (g2d != null) {
                FontRenderContext deviceFontRenderContext = g2d.
                    getFontRenderContext();
                FontRenderContext frc = getFRC(c, null);
                if (frc.isAntiAliased() || frc.usesFractionalMetrics()) {
                    frc = new FontRenderContext(frc.getTransform(), false, false);
                }
                if (frc != null  
                    && ! isFontRenderContextCompatible(deviceFontRenderContext,
                                                       frc)) {
                    TextLayout layout = 
                        new TextLayout(new String(data,offset,length),
                                       g2d.getFont(),
                                       frc);

                    /* Use alternate print color if specified */
                    Color col = g2d.getColor();
                    if (col instanceof PrintColorUIResource) {
                        g2d.setColor(((PrintColorUIResource)col).getPrintColor());
                    }

                    layout.draw(g2d,x,y);

                    g2d.setColor(col);

                    return nextX;
                }  
            }
        } 
        // Assume we're not printing if we get here.
        if (drawTextAntialiased(c) && (g instanceof Graphics2D)) {
            Graphics2D g2 = (Graphics2D)g;
            Object oldAAValue = g2.getRenderingHint(
                                       RenderingHints.KEY_TEXT_ANTIALIASING);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.drawChars(data, offset, length, x, y);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                oldAAValue);
        }
        else {
            g.drawChars(data, offset, length, x, y);
        }
        return nextX;
    }

    /* 
     * see documentation for drawChars
     * returns the advance
     */
    public static float drawString(JComponent c, Graphics g,
                                   AttributedCharacterIterator iterator,
                                   int x,
                                   int y) {

        float retVal;
        boolean isPrinting = isPrinting(g);
        Color col = g.getColor();

        if (isPrinting) {
            /* Use alternate print color if specified */
            if (col instanceof PrintColorUIResource) {
                g.setColor(((PrintColorUIResource)col).getPrintColor());
            }
        }

        Graphics2D g2d = getGraphics2D(g);
        if (g2d == null) {
            g.drawString(iterator,x,y); //for the cases where advance
                                        //matters it should not happen
            retVal = x;                   

        } else {
            FontRenderContext frc;
            if (isPrinting) {
                frc = getFRC(c, null);
                if (frc.isAntiAliased() || frc.usesFractionalMetrics()) {
                    frc = new FontRenderContext(frc.getTransform(), false, false);
                }
            } else if (drawTextAntialiased(c)) {
                frc = AA_FRC;
            } else {
                frc = g2d.getFontRenderContext();
            }
            TextLayout layout = new TextLayout(iterator, frc);
            layout.draw(g2d, x, y);
            retVal = layout.getAdvance();
        }

        if (isPrinting) {
            g.setColor(col);
        }

        return retVal;
    }

    /*
     * Checks if two given FontRenderContexts are compatible.
     * Compatible means no special handling needed for text painting
     */
    public static boolean isFontRenderContextCompatible(FontRenderContext frc1,
                                                        FontRenderContext frc2) {
        return (frc1 != null) ? frc1.equals(frc2) : frc2 == null;
    }
    
    /* 
     * Tries it best to get Graphics2D out of the given Graphics
     * returns null if can not derive it.
     */
    public static Graphics2D getGraphics2D(Graphics g) {
        if (g instanceof Graphics2D) {
            return (Graphics2D) g;
        } else if (g instanceof ProxyPrintGraphics) {
            return (Graphics2D)(((ProxyPrintGraphics)g).getGraphics());
        } else {
            return null;
        }
    }

    /*
     * Returns FontRendedrContext associated with JComponent 
     * see JComponent.getFontMetrics 
     */ 
    public static FontRenderContext getFontRenderContext(Component c) { 
        if (c == null) {
            return DEFAULT_FRC;
        } else {
            return getFRC(null, c.getFontMetrics(c.getFont()));
        }
    }

    /*
     * returns true if the Graphics is print Graphics
     * false otherwise
     */
    static boolean isPrinting(Graphics g) {
        return (g instanceof PrinterGraphics || g instanceof PrintGraphics);
    }

    /**
     * Determines whether the SelectedTextColor should be used for painting text
     * foreground for the specified highlight.
     *
     * Returns true only if the highlight painter for the specified highlight
     * is the swing painter (whether inner class of javax.swing.text.DefaultHighlighter
     * or com.sun.java.swing.plaf.windows.WindowsTextUI) and its background color
     * is null or equals to the selection color of the text component.
     *
     * This is a hack for fixing both bugs 4761990 and 5003294
     */
    public static boolean useSelectedTextColor(Highlighter.Highlight h, JTextComponent c) {
        Highlighter.HighlightPainter painter = h.getPainter();
        String painterClass = painter.getClass().getName();
        if (painterClass.indexOf("javax.swing.text.DefaultHighlighter") != 0 &&
                painterClass.indexOf("com.sun.java.swing.plaf.windows.WindowsTextUI") != 0) {
            return false;
        }
        try {
            DefaultHighlighter.DefaultHighlightPainter defPainter =
                    (DefaultHighlighter.DefaultHighlightPainter) painter;
            if (defPainter.getColor() != null &&
                    !defPainter.getColor().equals(c.getSelectionColor())) {
                return false;
            }
        } catch (ClassCastException e) {
            return false;
        }
        return true;
    }

    /**
     * LSBCacheEntry is used to cache the left side bearing (lsb) for
     * a particular <code>Font</code> and <code>FontRenderContext</code>.
     * This only caches characters that fall in the range
     * <code>MIN_CHAR_INDEX</code> to <code>MAX_CHAR_INDEX</code>.
     */
    private static class LSBCacheEntry {
        // Used to indicate a particular entry in lsb has not been set.
        private static final byte UNSET = Byte.MAX_VALUE;
        // Used in creating a GlyphVector to get the lsb
        private static final char[] oneChar = new char[1];

        private byte[] lsbCache;
        private Font font;
        private FontRenderContext frc;

        public LSBCacheEntry(FontRenderContext frc, Font font) {
            lsbCache = new byte[MAX_CHAR_INDEX - MIN_CHAR_INDEX];
            reset(frc, font);
        }

        public void reset(FontRenderContext frc, Font font) {
            this.font = font;
            this.frc = frc;
            for (int counter = lsbCache.length - 1; counter >= 0; counter--) {
                lsbCache[counter] = UNSET;
            }
        }

        public int getLeftSideBearing(char aChar) {
            int index = aChar - MIN_CHAR_INDEX;
            assert (index >= 0 && index < (MAX_CHAR_INDEX - MIN_CHAR_INDEX));
            byte lsb = lsbCache[index];
            if (lsb == UNSET) {
                oneChar[0] = aChar;
                GlyphVector gv = font.createGlyphVector(frc, oneChar);
                lsb = (byte)gv.getGlyphPixelBounds(0, frc, 0f, 0f).x;
                lsbCache[index] = lsb;
            }
            return lsb;
        }

        public boolean equals(Object entry) {
            if (entry == this) {
                return true;
            }
            if (!(entry instanceof LSBCacheEntry)) {
                return false;
            }
            LSBCacheEntry oEntry = (LSBCacheEntry)entry;
            return (font.equals(oEntry.font) &&
                    frc.equals(oEntry.frc));
        }

        public int hashCode() {
            int result = 17;
            if (font != null) {
                result = 37 * result + font.hashCode();
            }
            if (frc != null) {
                result = 37 * result + frc.hashCode();
            }
            return result;
        }
    }



    /*
     * here goes the fix for 4856343 [Problem with applet interaction
     * with system selection clipboard]
     * 
     * NOTE. In case isTrustedContext() no checking
     * are to be performed
     */


    /**
     * checks the security permissions for accessing system clipboard
     * 
     * for untrusted context (see isTrustedContext) checks the
     * permissions for the current event being handled
     *
     */
    public static boolean canAccessSystemClipboard() {
        boolean canAccess = false;
        if (!GraphicsEnvironment.isHeadless()) {
            SecurityManager sm = System.getSecurityManager();
            if (sm == null) {
                canAccess = true;
            } else {
                try {
                    sm.checkSystemClipboardAccess();
                    canAccess = true;  
                } catch (SecurityException e) {
                }
                if (canAccess && ! isTrustedContext()) {
                    canAccess = canCurrentEventAccessSystemClipboard(true);
                }
            }
        }
        return canAccess;
    }

    /**
     * Returns true if EventQueue.getCurrentEvent() has the permissions to
     * access the system clipboard
     */
    public static boolean canCurrentEventAccessSystemClipboard() {
        return  isTrustedContext()
            || canCurrentEventAccessSystemClipboard(false);
    }
    
    /**
     * Returns true if the given event has permissions to access the
     * system clipboard
     * 
     * @param e AWTEvent to check
     */
    public static boolean canEventAccessSystemClipboard(AWTEvent e) {
        return isTrustedContext() 
            || canEventAccessSystemClipboard(e, false);
    }
    
    /**
     * returns canAccessSystemClipboard field from InputEvent
     *
     * @param ie InputEvent to get the field from 
     */
    private static synchronized boolean inputEvent_canAccessSystemClipboard(InputEvent ie) {
        if (inputEvent_CanAccessSystemClipboard_Field == null) { 
            inputEvent_CanAccessSystemClipboard_Field =
                (Field)AccessController.doPrivileged(
                    new java.security.PrivilegedAction() {
                        public Object run() {
                            Field field = null;
                            try {
                                field = InputEvent.class.
                                    getDeclaredField("canAccessSystemClipboard");
                                field.setAccessible(true);
                                return field;
                            } catch (SecurityException e) {
                            } catch (NoSuchFieldException e) {
                            }
                            return null;
                        }
                    });
        }
        if (inputEvent_CanAccessSystemClipboard_Field == null) { 
            return false;
        }
        boolean ret = false;
        try {
            ret = inputEvent_CanAccessSystemClipboard_Field.
                getBoolean(ie);
        } catch(IllegalAccessException e) {
        } 
        return ret;
    }

    /**
     * Returns true if the given event is corrent gesture for
     * accessing clipboard
     * 
     * @param ie InputEvent to check
     */

    private static boolean isAccessClipboardGesture(InputEvent ie) {
        boolean allowedGesture = false;
        if (ie instanceof KeyEvent) { //we can validate only keyboard gestures
            KeyEvent ke = (KeyEvent)ie;
            int keyCode = ke.getKeyCode();
            int keyModifiers = ke.getModifiers();
            switch(keyCode) {
            case KeyEvent.VK_C:
            case KeyEvent.VK_V:
            case KeyEvent.VK_X:
                allowedGesture = (keyModifiers == InputEvent.CTRL_MASK);
                break;
            case KeyEvent.VK_INSERT:
                allowedGesture = (keyModifiers == InputEvent.CTRL_MASK ||
                                  keyModifiers == InputEvent.SHIFT_MASK);
                break;
            case KeyEvent.VK_COPY:
            case KeyEvent.VK_PASTE:
            case KeyEvent.VK_CUT:
                allowedGesture = true;
                break;
            case KeyEvent.VK_DELETE:
                allowedGesture = ( keyModifiers == InputEvent.SHIFT_MASK);
                break;
            }
        } 
        return allowedGesture;
    }

    /**
     * Returns true if e has the permissions to
     * access the system clipboard and if it is allowed gesture (if
     * checkGesture is true)
     *
     * @param e AWTEvent to check
     * @param checkGesture boolean
     */
    private static boolean canEventAccessSystemClipboard(AWTEvent e, 
                                                        boolean checkGesture) {
        if (EventQueue.isDispatchThread()) { 
            /*
             * Checking event permissions makes sense only for event
             * dispathing thread 
             */
            if (e instanceof InputEvent 
                && (! checkGesture || isAccessClipboardGesture((InputEvent)e))) {
                return inputEvent_canAccessSystemClipboard((InputEvent)e);
            } else { 
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * Returns true if EventQueue.getCurrentEvent() has the permissions to
     * access the system clipboard and if it is allowed gesture (if
     * checkGesture true)
     * 
     * @param checkGesture boolean 
     */
    private static boolean canCurrentEventAccessSystemClipboard(boolean 
                                                               checkGesture) {
        AWTEvent event = EventQueue.getCurrentEvent();
        return canEventAccessSystemClipboard(event, checkGesture);
    }

    /**
     * see RFE 5012841 [Per AppContect security permissions] for the
     * details
     *
     */ 
    private static boolean isTrustedContext() {
        return (System.getSecurityManager() == null) 
            || (AppContext.getAppContext().
                get(UntrustedClipboardAccess) == null);
    }

    public static String displayPropertiesToCSS(Font font, Color fg) {
        StringBuffer rule = new StringBuffer("body {");
        if (font != null) {
            rule.append(" font-family: ");
            rule.append(font.getFamily());
            rule.append(" ; ");
            rule.append(" font-size: ");
            rule.append(font.getSize());
            rule.append("pt ;");
            if (font.isBold()) {
                rule.append(" font-weight: 700 ; ");
            }
            if (font.isItalic()) {
                rule.append(" font-style: italic ; ");
            }
        }
        if (fg != null) {
            rule.append(" color: #");
            if (fg.getRed() < 16) {
                rule.append('0');
            }
            rule.append(Integer.toHexString(fg.getRed()));
            if (fg.getGreen() < 16) {
                rule.append('0');
            }
            rule.append(Integer.toHexString(fg.getGreen()));
            if (fg.getBlue() < 16) {
                rule.append('0');
            }
            rule.append(Integer.toHexString(fg.getBlue()));
            rule.append(" ; ");
        }
        rule.append(" }");
        return rule.toString();
    }

    /**
     * Utility method that creates a <code>UIDefaults.LazyValue</code> that
     * creates an <code>ImageIcon</code> <code>UIResource</code> for the
     * specified image file name. The image is loaded using
     * <code>getResourceAsStream</code>, starting with a call to that method
     * on the base class parameter. If it cannot be found, searching will
     * continue through the base class' inheritance hierarchy, up to and
     * including <code>rootClass</code>.
     *
     * @param baseClass the first class to use in searching for the resource
     * @param rootClass an ancestor of <code>baseClass</code> to finish the
     *                  search at
     * @param imageFile the name of the file to be found
     * @return a lazy value that creates the <code>ImageIcon</code>
     *         <code>UIResource</code> for the image,
     *         or null if it cannot be found
     */
    public static Object makeIcon(final Class<?> baseClass,
                                  final Class<?> rootClass,
                                  final String imageFile) {

        return new UIDefaults.LazyValue() {
            public Object createValue(UIDefaults table) {
                /* Copy resource into a byte array.  This is
                 * necessary because several browsers consider
                 * Class.getResource a security risk because it
                 * can be used to load additional classes.
                 * Class.getResourceAsStream just returns raw
                 * bytes, which we can convert to an image.
                 */
                byte[] buffer = (byte[])
                    java.security.AccessController.doPrivileged(
                        new java.security.PrivilegedAction() {
                    public Object run() {
                        try {
                            InputStream resource = null;
                            Class<?> srchClass = baseClass;

                            while (srchClass != null) {
                                resource = srchClass.getResourceAsStream(imageFile);

                                if (resource != null || srchClass == rootClass) {
                                    break;
                                }

                                srchClass = srchClass.getSuperclass();
                            }

                            if (resource == null) {
                                return null; 
                            }

                            BufferedInputStream in = 
                                new BufferedInputStream(resource);
                            ByteArrayOutputStream out = 
                                new ByteArrayOutputStream(1024);
                            byte[] buffer = new byte[1024];
                            int n;
                            while ((n = in.read(buffer)) > 0) {
                                out.write(buffer, 0, n);
                            }
                            in.close();
                            out.flush();
                            return out.toByteArray();
                        } catch (IOException ioe) {
                            System.err.println(ioe.toString());
                        }
                        return null;
                    }
                });

                if (buffer == null) {
                    return null;
                }
                if (buffer.length == 0) {
                    System.err.println("warning: " + imageFile + 
                                       " is zero-length");
                    return null;
                }

                return new IconUIResource(new ImageIcon(buffer));
            }
        };
    }

    /**
     * Sets the {@code SKIP_CLICK_COUNT} client property on the component
     * if it is an instance of {@code JTextComponent} with a
     * {@code DefaultCaret}. This property, used for text components acting
     * as editors in a table or tree, tells {@code DefaultCaret} how many
     * clicks to skip before starting selection.
     */
    public static void setSkipClickCount(Component comp, int count) {
        if (comp instanceof JTextComponent
                && ((JTextComponent) comp).getCaret() instanceof DefaultCaret) {

            ((JTextComponent) comp).putClientProperty(SKIP_CLICK_COUNT, count);
        }
    }

    /**
     * Return the MouseEvent's click count, possibly reduced by the value of
     * the component's {@code SKIP_CLICK_COUNT} client property. Clears
     * the {@code SKIP_CLICK_COUNT} property if the mouse event's click count
     * is 1. In order for clearing of the property to work correctly, there
     * must be a mousePressed implementation on the caller with this
     * call as the first line.
     */
    public static int getAdjustedClickCount(JTextComponent comp, MouseEvent e) {
        int cc = e.getClickCount();

        if (cc == 1) {
            comp.putClientProperty(SKIP_CLICK_COUNT, null);
        } else {
            Integer sub = (Integer) comp.getClientProperty(SKIP_CLICK_COUNT);
            if (sub != null) {
                return cc - sub;
            }
        }

        return cc;
    }
}

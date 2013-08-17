/*
 * @(#)SymbolTest.java	1.2 98/12/10
 *
 * Copyright (c) 1997 Sun Microsystems, Inc. All Rights Reserved.
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
import java.awt.event.*;
import java.applet.Applet;

public class SymbolTest extends Applet implements ActionListener, ItemListener {

    SymbolCanvas symbols; 
    TextField baseText;

    static final int SYMBOL_BASE = 0x2200;
    static final int DINGBAT_BASE = 0x2700;
    static final int GREEK_BASE = 0x0370;

    public void init() {
	setLayout(new BorderLayout());

        Panel panel = new Panel();

        panel.add(new Label("Font:"));
        Choice fontList = new Choice();

        String[] fontNames = getToolkit().getFontList();

        for (int i = 0; i < fontNames.length; i++) {
            fontList.addItem(fontNames[i]);
        }
        fontList.addItemListener(this);
        panel.add(fontList);
        Font defaultFont = new Font(fontNames[0], Font.PLAIN, 16);

        panel.add(new Label("Unicode base:"));
        baseText = new TextField(Integer.toHexString(DINGBAT_BASE), 4);
        baseText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        baseText.addActionListener(this);
        panel.add(baseText);
        add("North", panel);

        ScrollPane sp = new ScrollPane();
        symbols = new SymbolCanvas(defaultFont, DINGBAT_BASE);
        sp.add(symbols);
        add("Center", sp);

        add("South", new Label("Symbols=0x2200, Dingbats=0x2700, Greek=0x0370"));
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String fontName = (String)e.getItem();
            symbols.setFont(new Font(fontName, Font.PLAIN, 16));
        }
    }

    public void actionPerformed(ActionEvent e) {
        try {
            int newBase = Integer.valueOf(e.getActionCommand(), 16).intValue();
            symbols.setBase(newBase);
        } catch (NumberFormatException nfe) {
            Toolkit.getDefaultToolkit().beep();
            baseText.select(0, Integer.MAX_VALUE);
        }
    }

    /*
     * This class demonstrates how adapter classes can be used to avoid
     * creating empty methods to satisfy an event listener interface.
     * Being a nested class, a separate class file won't be created 
     * (which would be overkill for implementing this functionality).
     */
    static class MyAdapter extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }

    public static void main(String args[]) {
	Frame f = new Frame("SymbolTest");
	SymbolTest symbolTest = new SymbolTest();

	symbolTest.init();
	symbolTest.start();

	f.add("Center", symbolTest);
	f.pack();
	f.setSize(400, 500);
	f.show();

        f.addWindowListener(new MyAdapter());
    }
}

class SymbolCanvas extends Canvas {
    Font font;
    int charHeight;
    int charWidth;
    int charBase;

    public SymbolCanvas(Font font, int base) {
        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
        charHeight = fm.getHeight() + 3;
        charWidth = fm.getMaxAdvance() + 4;
        charBase = base;
        setSize(charWidth * 16 + 60, charHeight * 16 + 10);
    }

    public void setBase(int base) {
        charBase = base;
        repaint();
    }

    public void setFont(Font font) {
        this.font = font;
        repaint();
    }

    public void paint(Graphics g) {
        g.setFont(font);
        g.setColor(Color.black);
        char[] carray = new char[1];
        int c = charBase;
        int y = 20;
        for (int v = 0; v < 16; v++) {
            g.drawString(Integer.toHexString(c), 10, y);
            int x = 60;
            for (int h = 0; h < 16; h++) {
                carray[0] = (char)c++;
                g.drawChars(carray, 0, 1, x, y);
                x += charWidth;
            }
            y += charHeight;
        }
    }
}

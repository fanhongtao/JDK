/*
 * @(#)GlobalControls.java	1.21 99/09/07
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
import javax.swing.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;


/**
 * Global Controls panel for changing graphic attributes of
 * the demo surface.
 */
public class GlobalControls extends JPanel implements ItemListener, ChangeListener {

    static String[] screenNames = { 
            "Auto Screen", "On Screen", "Off Screen",
            "INT_RGB", "INT_ARGB", "INT_ARGB_PRE", "INT_BGR",
            "3BYTE_BGR", "4BYTE_ABGR", "4BYTE_ABGR_PRE", "USHORT_565_RGB",
            "USHORT_555_RGB", "BYTE_GRAY", "USHORT_GRAY", "BYTE_BINARY",
            "BYTE_INDEXED", "BYTE_BINARY 2 bit", "BYTE_BINARY 4 bit"};
    static JComboBox screenCombo;
    public TextureChooser texturechooser;
    public JCheckBox aliasCB, renderCB, toolBarCB;
    public JCheckBox compositeCB, textureCB;
    public JSlider slider;
    public Object obj;

    private Font font = new Font("serif", Font.PLAIN, 10);


    public GlobalControls() {
        setLayout(new GridBagLayout());
        setBorder(new TitledBorder(new EtchedBorder(), "Global Controls"));

        aliasCB = createCheckBox("Anti-Aliasing", true, 0);
        renderCB = createCheckBox("Rendering Quality", false, 1);
        textureCB = createCheckBox("Texture", false, 2);
        compositeCB = createCheckBox("AlphaComposite", false, 3);

        screenCombo = new JComboBox();
        screenCombo.setPreferredSize(new Dimension(120, 18));
        screenCombo.setLightWeightPopupEnabled(true);
        screenCombo.setFont(font);
        for (int i = 0; i < screenNames.length; i++) {
            screenCombo.addItem(screenNames[i]);
        } 
        screenCombo.addItemListener(this);
        Java2Demo.addToGridBag(this, screenCombo, 0, 4, 1, 1, 0.0, 0.0);

        toolBarCB = createCheckBox("Tools", false, 5);

        slider = new JSlider(JSlider.HORIZONTAL, 0, 200, 30);
        slider.addChangeListener(this);
        TitledBorder tb = new TitledBorder(new EtchedBorder());
        tb.setTitleFont(font);
        tb.setTitle("Anim delay = 30 ms");
        slider.setBorder(tb);
        slider.setMinimumSize(new Dimension(80,46));
        Java2Demo.addToGridBag(this, slider, 0, 6, 1, 1, 1.0, 1.0);

        texturechooser = new TextureChooser(0);
        Java2Demo.addToGridBag(this, texturechooser, 0, 7, 1, 1, 1.0, 1.0);
    }


    private JCheckBox createCheckBox(String s, boolean b, int y) {
        JCheckBox cb = new JCheckBox(s, b);
        cb.setFont(font);
        cb.setHorizontalAlignment(JCheckBox.LEFT);
        cb.addItemListener(this);
        Java2Demo.addToGridBag(this, cb, 0, y, 1, 1, 1.0, 1.0);
        return cb;
    }


    public void stateChanged(ChangeEvent e) {
        int value = slider.getValue();
        TitledBorder tb = (TitledBorder) slider.getBorder();
        tb.setTitle("Anim delay = " + String.valueOf(value) + " ms");
        int index = Java2Demo.tabbedPane.getSelectedIndex()-1;
        DemoGroup dg = Java2Demo.group[index];
        JPanel p = dg.getPanel();
        for (int i = 0; i < p.getComponentCount(); i++) {
            DemoPanel dp = (DemoPanel) p.getComponent(i);
            if (dp.tools != null && dp.tools.slider != null) {
                dp.tools.slider.setValue(value);
            }
        } 
        slider.repaint();
    }


    public void itemStateChanged(ItemEvent e) {
        if (Java2Demo.tabbedPane.getSelectedIndex() != 0) {
            obj = e.getSource();
            int index = Java2Demo.tabbedPane.getSelectedIndex()-1;
            Java2Demo.group[index].setup(true);
            obj = null;
        }
    }


    public Dimension getPreferredSize() {
        return new Dimension(135,260);
    }
}

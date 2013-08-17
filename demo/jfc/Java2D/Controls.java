/*
 * @(#)Controls.java	1.16 98/09/21
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */


import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;


/**
 * Global Controls panel for changing graphic attributes of
 * the demo surface.
 */
public class Controls extends JPanel implements ItemListener {

    public TextureChooser texturechooser;
    public JCheckBox aliasCB, renderCB, toolBarCB;
    public JCheckBox compositeCB, textureCB;
    public JCheckBox verboseCB;
    public JComboBox imgTypeCombo;

    private Font font = new Font("serif", Font.PLAIN, 10);


    public Controls() {
        setLayout(new GridBagLayout());
        setBorder(new TitledBorder(new EtchedBorder(), "Global Controls"));

        aliasCB = createCheckBox("Anti-Aliasing", true, 0);
        renderCB = createCheckBox("Rendering Quality", false, 1);
        textureCB = createCheckBox("Texture", false, 2);
        compositeCB = createCheckBox("AlphaComposite", false, 3);

        imgTypeCombo = new JComboBox();
        imgTypeCombo.setPreferredSize(new Dimension(120, 18));
        imgTypeCombo.setFont(font);
        imgTypeCombo.addItem("Auto Screen");
        imgTypeCombo.addItem("On Screen");
        imgTypeCombo.addItem("Off Screen");
        imgTypeCombo.addItem("INT_RGB");
        imgTypeCombo.addItem("INT_ARGB");
        imgTypeCombo.addItem("INT_ARGB_PRE");
        imgTypeCombo.addItem("INT_BGR");
        imgTypeCombo.addItem("3BYTE_BGR");
        imgTypeCombo.addItem("4BYTE_ABGR");
        imgTypeCombo.addItem("4BYTE_ABGR_PRE");
        imgTypeCombo.addItem("USHORT_565_RGB");
        imgTypeCombo.addItem("USHORT_555_RGB");
        imgTypeCombo.addItem("BYTE_GRAY");
        imgTypeCombo.addItem("USHORT_GRAY");
        imgTypeCombo.addItem("BYTE_BINARY");
        imgTypeCombo.setSelectedIndex(0);
        imgTypeCombo.addItemListener(this);
        Java2Demo.addToGridBag(this, imgTypeCombo, 0, 4, 1, 1, 0.0, 0.0);

        toolBarCB = createCheckBox("ToolBar", false, 5);
        verboseCB = createCheckBox("Verbose", false, 6);

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


    public void itemStateChanged(ItemEvent e) {
        Java2Demo.group[Java2Demo.tabbedPane.getSelectedIndex()].setup(true);
    }


    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    public Dimension getPreferredSize() {
        return new Dimension(140,240);
    }
}

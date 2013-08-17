/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.metal;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import java.beans.*;
import java.util.EventListener;
import java.io.Serializable;
import javax.swing.plaf.basic.BasicDesktopIconUI;

/**
 * Metal desktop icon.
 *
 * @version 1.15 02/06/02
 * @author Steve Wilson
 */
public class MetalDesktopIconUI extends BasicDesktopIconUI
{

    JButton button;
    JLabel label;
    TitleListener titleListener;

    public static ComponentUI createUI(JComponent c)    {
        return new MetalDesktopIconUI();
    }

    public MetalDesktopIconUI() {
    }

    protected void installDefaults() {
        super.installDefaults();
        LookAndFeel.installColorsAndFont(desktopIcon, "DesktopIcon.background", "DesktopIcon.foreground", "DesktopIcon.font");
	desktopIcon.setOpaque(true);
    }
   
    protected void installComponents() {
	JInternalFrame frame = desktopIcon.getInternalFrame();
	Icon icon = frame.getFrameIcon();
	String title = frame.getTitle();

	button = new JButton (title, icon);
	button.addActionListener( new ActionListener() {
	                          public void actionPerformed(ActionEvent e) { deiconize(); }} );
	button.setFont(desktopIcon.getFont());
	button.setBackground(desktopIcon.getBackground());
	button.setForeground(desktopIcon.getForeground());

	int buttonH = button.getPreferredSize().height;

	Icon drag = new MetalBumps((buttonH/3), buttonH,
				   MetalLookAndFeel.getControlHighlight(),
				   MetalLookAndFeel.getControlDarkShadow(),
				   MetalLookAndFeel.getControl());
	label = new JLabel(drag);

	label.setBorder( new MatteBorder( 0, 2, 0, 1, desktopIcon.getBackground()) );
	desktopIcon.setLayout(new BorderLayout(2, 0));
	desktopIcon.add(button, BorderLayout.CENTER);
	desktopIcon.add(label, BorderLayout.WEST);
	desktopIcon.getInternalFrame().addPropertyChangeListener( titleListener = new TitleListener() );
    }

    protected void uninstallComponents() {
	desktopIcon.setLayout(null);
	desktopIcon.remove(label);
	desktopIcon.remove(button);
	desktopIcon.getInternalFrame().removePropertyChangeListener(titleListener);	
    }
 
    public Dimension getPreferredSize(JComponent c) {
	return null;
    }
  
    class TitleListener implements PropertyChangeListener {
        public void propertyChange (PropertyChangeEvent e) {
  	  if (e.getPropertyName().equals("title")) {
	    button.setText((String)e.getNewValue());
	  }

  	  if (e.getPropertyName().equals("frameIcon")) {
	    button.setIcon((Icon)e.getNewValue());
	  }
	}

    }
}



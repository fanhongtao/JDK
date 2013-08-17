/*
 * @(#)DemoMetalTheme.java	1.8 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * This class describes a theme using large fonts.
 * It's great for giving demos of your software to a group
 * where people will have trouble seeing what you're doing.
 *
 * @version 1.8 12/03/01
 * @author Steve Wilson
 */
public class DemoMetalTheme extends DefaultMetalTheme {

    public String getName() { return "Presentation"; }

    private final FontUIResource controlFont = new FontUIResource("Dialog", Font.BOLD, 18);
    private final FontUIResource systemFont = new FontUIResource("Dialog", Font.PLAIN, 18);
    private final FontUIResource userFont = new FontUIResource("SansSerif", Font.PLAIN, 18);
    private final FontUIResource smallFont = new FontUIResource("Dialog", Font.PLAIN, 14);

    public FontUIResource getControlTextFont() { return controlFont;}
    public FontUIResource getSystemTextFont() { return systemFont;}
    public FontUIResource getUserTextFont() { return userFont;}
    public FontUIResource getMenuTextFont() { return controlFont;}
    public FontUIResource getWindowTitleFont() { return controlFont;}
    public FontUIResource getSubTextFont() { return smallFont;}

    public void addCustomEntriesToTable(UIDefaults table) {
         super.addCustomEntriesToTable(table);

         final int internalFrameIconSize = 22;
         table.put("InternalFrame.closeIcon", MetalIconFactory.getInternalFrameCloseIcon(internalFrameIconSize));
         table.put("InternalFrame.maximizeIcon", MetalIconFactory.getInternalFrameMaximizeIcon(internalFrameIconSize));
         table.put("InternalFrame.iconifyIcon", MetalIconFactory.getInternalFrameMinimizeIcon(internalFrameIconSize));
         table.put("InternalFrame.minimizeIcon", MetalIconFactory.getInternalFrameAltMaximizeIcon(internalFrameIconSize));


         table.put( "ScrollBar.width", new Integer(21) );



    }

}

/*
 * @(#)BigContrastMetalTheme.java	1.11 01/12/03
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
 * This class describes a theme using "green" colors.
 *
 * @version 1.11 12/03/01
 * @author Steve Wilson
 */
public class BigContrastMetalTheme extends ContrastMetalTheme {

    public String getName() { return "Low Vision"; }

    private final FontUIResource controlFont = new FontUIResource("Dialog", Font.BOLD, 24);
    private final FontUIResource systemFont = new FontUIResource("Dialog", Font.PLAIN, 24);
    private final FontUIResource windowTitleFont = new FontUIResource("Dialog", Font.BOLD, 24);
    private final FontUIResource userFont = new FontUIResource("SansSerif", Font.PLAIN, 24);
    private final FontUIResource smallFont = new FontUIResource("Dialog", Font.PLAIN, 20);


    public FontUIResource getControlTextFont() { return controlFont;}
    public FontUIResource getSystemTextFont() { return systemFont;}
    public FontUIResource getUserTextFont() { return userFont;}
    public FontUIResource getMenuTextFont() { return controlFont;}
    public FontUIResource getWindowTitleFont() { return windowTitleFont;}
    public FontUIResource getSubTextFont() { return smallFont;}

    public void addCustomEntriesToTable(UIDefaults table) {
         super.addCustomEntriesToTable(table);

         final int internalFrameIconSize = 30;
         table.put("InternalFrame.closeIcon", MetalIconFactory.getInternalFrameCloseIcon(internalFrameIconSize));
         table.put("InternalFrame.maximizeIcon", MetalIconFactory.getInternalFrameMaximizeIcon(internalFrameIconSize));
         table.put("InternalFrame.iconifyIcon", MetalIconFactory.getInternalFrameMinimizeIcon(internalFrameIconSize));
         table.put("InternalFrame.minimizeIcon", MetalIconFactory.getInternalFrameAltMaximizeIcon(internalFrameIconSize));


	Border blackLineBorder = new BorderUIResource( new MatteBorder( 2,2,2,2, Color.black) );
	Border textBorder = blackLineBorder;

        table.put( "ToolTip.border", blackLineBorder);
	table.put( "TitledBorder.border", blackLineBorder);


        table.put( "TextField.border", textBorder);
        table.put( "PasswordField.border", textBorder);
        table.put( "TextArea.border", textBorder);
        table.put( "TextPane.font", textBorder);

        table.put( "ScrollPane.border", blackLineBorder);

        table.put( "ScrollBar.width", new Integer(25) );



    }
}

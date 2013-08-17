/*
 * @(#)BigContrastMetalTheme.java	1.7 98/08/26
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


import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * This class describes a theme using "green" colors.
 *
 * @version 1.7 08/26/98
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
         table.put("InternalFrame.iconizeIcon", MetalIconFactory.getInternalFrameMinimizeIcon(internalFrameIconSize));
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

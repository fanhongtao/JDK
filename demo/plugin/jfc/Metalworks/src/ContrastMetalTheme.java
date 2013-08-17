/*
 * @(#)ContrastMetalTheme.java	1.14 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * This class describes a higher-contrast Metal Theme.
 *
 * @version 1.14 12/03/01
 * @author Michael C. Albers
 */

public class ContrastMetalTheme extends DefaultMetalTheme {

    public String getName() { return "Contrast"; }

    private final ColorUIResource primary1 = new ColorUIResource(0, 0, 0);
    private final ColorUIResource primary2 = new ColorUIResource(204, 204, 204);
    private final ColorUIResource primary3 = new ColorUIResource(255, 255, 255);
    private final ColorUIResource primaryHighlight = new ColorUIResource(102,102,102);

    private final ColorUIResource secondary2 = new ColorUIResource(204, 204, 204);
    private final ColorUIResource secondary3 = new ColorUIResource(255, 255, 255);
    private final ColorUIResource controlHighlight = new ColorUIResource(102,102,102);

    protected ColorUIResource getPrimary1() { return primary1; } 
    protected ColorUIResource getPrimary2() { return primary2; }
    protected ColorUIResource getPrimary3() { return primary3; }
    public ColorUIResource getPrimaryControlHighlight() { return primaryHighlight;}

    protected ColorUIResource getSecondary2() { return secondary2; }
    protected ColorUIResource getSecondary3() { return secondary3; }
    public ColorUIResource getControlHighlight() { return super.getSecondary3(); }

    public ColorUIResource getFocusColor() { return getBlack(); }

    public ColorUIResource getTextHighlightColor() { return getBlack(); }
    public ColorUIResource getHighlightedTextColor() { return getWhite(); }
  
    public ColorUIResource getMenuSelectedBackground() { return getBlack(); }
    public ColorUIResource getMenuSelectedForeground() { return getWhite(); }
    public ColorUIResource getAcceleratorForeground() { return getBlack(); }
    public ColorUIResource getAcceleratorSelectedForeground() { return getWhite(); }


    public void addCustomEntriesToTable(UIDefaults table) {

        Border blackLineBorder = new BorderUIResource(new LineBorder( getBlack() ));
        Border whiteLineBorder = new BorderUIResource(new LineBorder( getWhite() ));

	Object textBorder = new BorderUIResource( new CompoundBorder(
						       blackLineBorder,
					               new BasicBorders.MarginBorder()));

        table.put( "ToolTip.border", blackLineBorder);
	table.put( "TitledBorder.border", blackLineBorder);
        table.put( "Table.focusCellHighlightBorder", whiteLineBorder);
        table.put( "Table.focusCellForeground", getWhite());

        table.put( "TextField.border", textBorder);
        table.put( "PasswordField.border", textBorder);
        table.put( "TextArea.border", textBorder);
        table.put( "TextPane.font", textBorder);


    }

}

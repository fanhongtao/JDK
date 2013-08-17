/*
 * @(#)KhakiMetalTheme.java	1.6 01/12/03
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
 * This class describes a theme using "khaki" colors.
 *
 * @version 1.6 12/03/01
 * @author Steve Wilson
 */
public class KhakiMetalTheme extends DefaultMetalTheme {

    public String getName() { return "Sandstone"; }

    private final ColorUIResource primary1 = new ColorUIResource( 87,  87,  47);
    private final ColorUIResource primary2 = new ColorUIResource(159, 151, 111);
    private final ColorUIResource primary3 = new ColorUIResource(199, 183, 143);

    private final ColorUIResource secondary1 = new ColorUIResource( 111,  111,  111);
    private final ColorUIResource secondary2 = new ColorUIResource(159, 159, 159);
    private final ColorUIResource secondary3 = new ColorUIResource(231, 215, 183);

    protected ColorUIResource getPrimary1() { return primary1; }
    protected ColorUIResource getPrimary2() { return primary2; }
    protected ColorUIResource getPrimary3() { return primary3; }

    protected ColorUIResource getSecondary1() { return secondary1; }
    protected ColorUIResource getSecondary2() { return secondary2; }
    protected ColorUIResource getSecondary3() { return secondary3; }

}

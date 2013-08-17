/*
 * @(#)AquaTheme.java	1.5 01/12/03
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
 * This class describes a theme using "blue-green" colors.
 *
 * 1.5 12/03/01
 * @author Steve Wilson
 */
public class AquaTheme extends DefaultMetalTheme {

    public String getName() { return "Aqua"; }

    private final ColorUIResource primary1 = new ColorUIResource(102, 153, 153);
    private final ColorUIResource primary2 = new ColorUIResource(128, 192, 192);
    private final ColorUIResource primary3 = new ColorUIResource(159, 235, 235);

    protected ColorUIResource getPrimary1() { return primary1; }
    protected ColorUIResource getPrimary2() { return primary2; }
    protected ColorUIResource getPrimary3() { return primary3; }

}

/*
 * @(#)KhakiMetalTheme.java	1.3 98/08/26
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
 * This class describes a theme using "khaki" colors.
 *
 * @version 1.3 08/26/98
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

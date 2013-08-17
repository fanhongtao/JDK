/*
 * @(#)RubyTheme.java	1.5 01/12/03
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
 * This class describes a theme using red colors.
 *
 * @version 1.5 12/03/01
 * @author Jeff Dinkins
 */
public class RubyTheme extends DefaultMetalTheme {

    public String getName() { return "Ruby"; }

    private final ColorUIResource primary1 = new ColorUIResource(80, 10, 22);
    private final ColorUIResource primary2 = new ColorUIResource(193, 10, 44);
    private final ColorUIResource primary3 = new ColorUIResource(244, 10, 66); 

    protected ColorUIResource getPrimary1() { return primary1; }  
    protected ColorUIResource getPrimary2() { return primary2; } 
    protected ColorUIResource getPrimary3() { return primary3; } 

}

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package javax.swing.plaf.metal;

import javax.swing.plaf.*;
import javax.swing.*;
import java.awt.*;

/**
 * This class describes the default Metal Theme.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.19 02/06/02
 * @author Steve Wilson
 */
public class DefaultMetalTheme extends MetalTheme {

    private final ColorUIResource primary1 = new ColorUIResource(102, 102, 153);
    private final ColorUIResource primary2 = new ColorUIResource(153, 153, 204);
    private final ColorUIResource primary3 = new ColorUIResource(204, 204, 255);

    private final ColorUIResource secondary1 = new ColorUIResource(102, 102, 102);
    private final ColorUIResource secondary2 = new ColorUIResource(153, 153, 153);
    private final ColorUIResource secondary3 = new ColorUIResource(204, 204, 204);

    private FontUIResource controlFont;
    private FontUIResource systemFont;
    private FontUIResource userFont;
    private FontUIResource smallFont;

    public String getName() { return "Steel"; }

    public DefaultMetalTheme() { }

// these are blue in Metal Default Theme
    protected ColorUIResource getPrimary1() { return primary1; } 
    protected ColorUIResource getPrimary2() { return primary2; }
    protected ColorUIResource getPrimary3() { return primary3; }

// these are gray in Metal Default Theme
    protected ColorUIResource getSecondary1() { return secondary1; }
    protected ColorUIResource getSecondary2() { return secondary2; }
    protected ColorUIResource getSecondary3() { return secondary3; }


    // note the properties listed here can currently be used by people
    // providing runtimes to hint what fonts are good.  For example the bold 
    // dialog font looks bad on a Mac, so Apple could use this property to 
    // hint at a good font.
    //
    // However, we don't promise to support these forever.  We may move 
    // to getting these from the swing.properties file, or elsewhere.

    public FontUIResource getControlTextFont() { 
	if (controlFont == null) {
	    try {		
		controlFont = new FontUIResource(Font.getFont("swing.plaf.metal.controlFont", new Font("Dialog", Font.BOLD, 12)));
	    } catch (Exception e) {
		controlFont = new FontUIResource("Dialog", Font.BOLD, 12);
	    }
	}
	return controlFont;
    }

    public FontUIResource getSystemTextFont() { 
	if (systemFont == null) {
	    try {		
		systemFont = new FontUIResource(Font.getFont("swing.plaf.metal.systemFont", new Font("Dialog", Font.PLAIN, 12)));
	    } catch (Exception e) {
		systemFont =  new FontUIResource("Dialog", Font.PLAIN, 12);
	    }
	}	
	return systemFont;
    }

    public FontUIResource getUserTextFont() { 
	if (userFont == null) {
	    try {		
		userFont = new FontUIResource(Font.getFont("swing.plaf.metal.userFont", new Font("Dialog", Font.PLAIN, 12)));
	    } catch (Exception e) {
		userFont =  new FontUIResource("Dialog", Font.PLAIN, 12);
	    }
	}	
	return userFont;
    }

    public FontUIResource getMenuTextFont() { 
	if (controlFont == null) {
	    try {		
		controlFont = new FontUIResource(Font.getFont("swing.plaf.metal.controlFont", new Font("Dialog", Font.BOLD, 12)));
	    } catch (Exception e) {
		controlFont = new FontUIResource("Dialog", Font.BOLD, 12);
	    }
	}
	return controlFont;
    }

    public FontUIResource getWindowTitleFont() { 
	if (controlFont == null) {
	    try {		
		controlFont = new FontUIResource(Font.getFont("swing.plaf.metal.controlFont", new Font("Dialog", Font.BOLD, 12)));
	    } catch (Exception e) {
		controlFont = new FontUIResource("Dialog", Font.BOLD, 12);
	    }
	}
	return controlFont;
    }
    public FontUIResource getSubTextFont() { 
	if (smallFont == null) {
	    try {		
		smallFont = new FontUIResource(Font.getFont("swing.plaf.metal.smallFont", new Font("Dialog", Font.PLAIN, 10)));
	    } catch (Exception e) {
		smallFont = new FontUIResource("Dialog", Font.PLAIN, 10);
	    }
	}	
	return smallFont;
    }
}

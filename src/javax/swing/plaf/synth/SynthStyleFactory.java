/*
 * @(#)SynthStyleFactory.java	1.8 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.plaf.synth;

import java.awt.*;
import java.util.*;
import javax.swing.plaf.*;
import javax.swing.*;

/**
 * Factory used for obtaining <code>SynthStyle</code>s.  Each of the
 * Synth <code>ComponentUI</code>s will call into the current
 * <code>SynthStyleFactory</code> to obtain a <code>SynthStyle</code>
 * for each of the distinct regions they have.
 * <p>
 * The following example creates a custom <code>SynthStyleFactory</code>
 * that returns a different style based on the <code>Region</code>:
 * <pre>
 * class MyStyleFactory extends SynthStyleFactory {
 *     public SynthStyle getStyle(JComponent c, Region id) {
 *         if (id == Region.BUTTON) {
 *             return buttonStyle;
 *         }
 *         else if (id == Region.TREE) {
 *             return treeStyle;
 *         }
 *         return defaultStyle;
 *     }
 * }
 * SynthLookAndFeel laf = new SynthLookAndFeel();
 * UIManager.setLookAndFeel(laf);
 * SynthLookAndFeel.setStyleFactory(new MyStyleFactory());
 * </pre>
 *
 * @see SynthStyleFactory
 * @see SynthStyle
 *
 * @version 1.8, 12/19/03
 * @since 1.5
 * @author Scott Violet
 */
public abstract class SynthStyleFactory {
    /**
     * Creates a <code>SynthStyleFactory</code>.
     */
    public SynthStyleFactory() {
    }

    /**
     * Returns the style for the specified Component.
     *
     * @param c Component asking for
     * @param id Region identifier
     * @return SynthStyle for region.
     */
    public abstract SynthStyle getStyle(JComponent c, Region id);
}

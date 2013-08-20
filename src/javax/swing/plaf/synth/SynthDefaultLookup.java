/*
 * @(#)SynthDefaultLookup.java	1.4 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.plaf.synth;

import sun.swing.DefaultLookup;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import sun.swing.plaf.synth.SynthUI;

/**
 * SynthDefaultLookup redirects all lookup calls to the SynthContext.
 *
 * @version 1.4 12/19/03
 * @author Scott Violet
 */
class SynthDefaultLookup extends DefaultLookup {
    public Object getDefault(JComponent c, ComponentUI ui, String key) {
        if (!(ui instanceof SynthUI)) {
            Object value = super.getDefault(c, ui, key);
            return value;
        }
        SynthContext context = ((SynthUI)ui).getContext(c);
        Object value = context.getStyle().get(context, key);
        context.dispose();
        return value;
    }
}

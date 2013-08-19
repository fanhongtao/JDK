/*
 * @(#)DefaultSynthStyle.java	1.19 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * @version 1.19, 01/23/03
 * @author Scott Violet
 */
class DefaultSynthStyle extends SynthStyle implements Cloneable{
    /**
     * Should the component be opaque?
     */
    private boolean opaque;
    /**
     * Insets.
     */
    private Insets insets;
    /**
     * Information specific to ComponentState.
     */
    StateInfo[] states;
    /**
     * User specific data.
     */
    Map data;

    /**
     * Nullary constructor, intended for subclassers.
     */
    protected DefaultSynthStyle() {
    }

    /**
     * Creates a new DefaultSynthStyle that is a copy of the passed in
     * style.
     */
    public DefaultSynthStyle(DefaultSynthStyle style) {
        opaque = style.opaque;
        if (style.insets != null) {
            insets = new Insets(style.insets.top, style.insets.left,
                                style.insets.bottom, style.insets.right);
        }
        if (style.states != null) {
            states = new StateInfo[style.states.length];
            for (int counter = style.states.length - 1; counter >= 0;
                     counter--) {
                states[counter] = (StateInfo)style.states[counter].clone();
            }
        }
        if (style.data != null) {
            data = new HashMap();
            data.putAll(style.data);
        }
    }

    public DefaultSynthStyle(Insets insets, boolean opaque,
                             StateInfo[] states, Map data) {
        this.insets = insets;
        this.opaque = opaque;
        this.states = states;
        this.data = data;
    }

    protected Color _getColor(JComponent c, Region id, int state,
                              ColorType type) {
        StateInfo si = getStateInfo(state);

        if (si != null) {
            return si.getColor(type);
        }
        return null;
    }

    protected Font _getFont(JComponent c, Region id, int state) {
        StateInfo si = getStateInfo(state);

        if (si != null) {
            return si.getFont();
        }
        return null;
    }

    public Insets getInsets(SynthContext state, Insets to) {
        if (to == null) {
            to = new Insets(0, 0, 0, 0);
        }
        if (insets != null) {
            to.left = insets.left;
            to.right = insets.right;
            to.top = insets.top;
            to.bottom = insets.bottom;
        }
        else {
            to.left = to.right = to.top = to.bottom = 0;
        }
        return to;
    }

    /**
     * Returns the Border for the passed in Component. This may return null.
     */
    public SynthPainter getBorderPainter(SynthContext ss) {
        StateInfo si = getStateInfo(ss.getComponentState());

        if (si != null) {
            return si.getBorderPainter();
        }
        return null;
    }

    /**
     * Returns the Painter used to paint the background.
     */
    public SynthPainter getBackgroundPainter(SynthContext ss) {
        StateInfo si = getStateInfo(ss.getComponentState());

        if (si != null) {
            return si.getBackgroundPainter();
        }
        return null;
    }

    /**
     * Returns the value to initialize the opacity property of the Component
     * to. A Style should NOT assume the opacity will remain this value, the
     * developer may reset it or override it.
     */
    public boolean isOpaque(SynthContext ss) {
        return opaque;
    }

    public Object get(SynthContext state, Object key) {
        if (data != null) {
            return data.get(key);
        }
        return null;
    }

    /**
     * Creates a clone of the receiver.
     */
    public Object clone() {
        DefaultSynthStyle style;
        try {
            style = (DefaultSynthStyle)super.clone();
        } catch (CloneNotSupportedException cnse) {
            return null;
        }
        if (states != null) {
            style.states = new StateInfo[states.length];
            for (int counter = states.length - 1; counter >= 0; counter--) {
                style.states[counter] = (StateInfo)states[counter].clone();
            }
        }
        if (data != null) {
            style.data = new HashMap();
            style.data.putAll(data);
        }
        return style;
    }

    /**
     * Merges the contents of this Style with that of the passed in Style,
     * returning the resulting merged syle.
     */
    public DefaultSynthStyle addTo(DefaultSynthStyle style) {
        if (insets != null) {
            style.insets = this.insets;
        }
        style.opaque = opaque;
        if (states != null) {
            if (style.states == null) {
                style.states = new StateInfo[states.length];
                for (int counter = states.length - 1; counter >= 0; counter--){
                    if (states[counter] != null) {
                        style.states[counter] = (StateInfo)states[counter].
                                                clone();
                    }
                }
            }
            else {
                // Find the number of new states in unique
                int unique = 0;
                for (int thisCounter = states.length - 1; thisCounter >= 0;
                         thisCounter--) {
                    int state = states[thisCounter].getComponentState();
                    boolean found = false;

                    for (int oCounter = style.states.length - 1; oCounter >= 0;
                             oCounter--) {
                        if (state == style.states[oCounter].
                                           getComponentState()) {
                            states[thisCounter].addTo(style.states[oCounter]);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        unique++;
                    }
                }
                if (unique != 0) {
                    // There are states that exist in this Style that
                    // don't exist in the other style, recreate the array
                    // and add them.
                    StateInfo[] newStates = new StateInfo[
                                   unique + style.states.length];
                    int newIndex = style.states.length;

                    System.arraycopy(style.states, 0, newStates, 0,
                                     style.states.length);
                    for (int thisCounter = states.length - 1; thisCounter >= 0;
                             thisCounter--) {
                        int state = states[thisCounter].getComponentState();
                        boolean found = false;

                        for (int oCounter = style.states.length - 1;
                                 oCounter >= 0; oCounter--) {
                            if (state == style.states[oCounter].
                                               getComponentState()) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            newStates[newIndex++] = (StateInfo)states[
                                      thisCounter].clone();
                        }
                    }
                    style.states = newStates;
                }
            }
        }
        if (data != null) {
            if (style.data == null) {
                style.data = new HashMap();
            }
            style.data.putAll(data);
        }
        return style;
    }

    StateInfo getStateInfo(int state) {
        if (states != null) {
            int bestCount = 0;
            int bestIndex = -1;

            for (int counter = states.length - 1; counter >= 0; counter--) {
                int matchCount = states[counter].getMatchCount(state);

                if (matchCount > bestCount) {
                    bestIndex = counter;
                    bestCount = matchCount;
                }
            }
            if (bestIndex != -1) {
                return states[bestIndex];
            }
        }
        return null;
    }


    /**
     * StateInfo represents Style information specific to a ComponentState.
     */
    public static class StateInfo {
        private SynthPainter borderPainter;
        private SynthPainter backgroundPainter;
        private Font font;
        Color[] colors;
        private int state;

        public StateInfo(int state, SynthPainter bPainter,
                         SynthPainter bgPainter, Font font, Color[] colors) {
            this.state = state;
            this.borderPainter = bPainter;
            this.backgroundPainter = bgPainter;
            this.font = font;
            this.colors = colors;
        }

        public StateInfo(StateInfo info) {
            this.state = info.state;
            this.borderPainter = info.borderPainter;
            this.backgroundPainter = info.backgroundPainter;
            this.font = info.font;
            if (info.colors != null) {
                this.colors = new Color[info.colors.length];
                System.arraycopy(info.colors, 0, colors, 0,info.colors.length);
            }
        }

        public SynthPainter getBorderPainter() {
            return borderPainter;
        }

        public SynthPainter getBackgroundPainter() {
            return backgroundPainter;
        }

        public Font getFont() {
            return font;
        }

        protected Color[] getColors() {
            return colors;
        }

        public Color getColor(ColorType type) {
            if (colors != null) {
                int id = type.getID();

                if (id < colors.length) {
                    return colors[id];
                }
            }
            return null;
        }

        public StateInfo addTo(StateInfo info) {
            info.borderPainter = borderPainter;
            info.backgroundPainter = backgroundPainter;
            if (font != null) {
                info.font = font;
            }
            if (colors != null) {
                if (info.colors == null) {
                    info.colors = new Color[colors.length];
                    System.arraycopy(colors, 0, info.colors, 0,
                                     colors.length);
                }
                else {
                    if (info.colors.length < colors.length) {
                        Color[] old = info.colors;

                        info.colors = new Color[colors.length];
                        System.arraycopy(old, 0, info.colors, 0, old.length);
                    }
                    for (int counter = colors.length - 1; counter >= 0;
                             counter--) {
                        if (colors[counter] != null) {
                            info.colors[counter] = colors[counter];
                        }
                    }
                }
            }
            return info;
        }

        public int getComponentState() {
            return state;
        }

        /**
         * Returns the number of states that are similar between the
         * ComponentState this StateInfo represents and val.
         */
        private final int getMatchCount(int val) {
            // This comes from BigInteger.bitCnt
            val &= state;
            val -= (0xaaaaaaaa & val) >>> 1;
            val = (val & 0x33333333) + ((val >>> 2) & 0x33333333);
            val = val + (val >>> 4) & 0x0f0f0f0f;
            val += val >>> 8;
            val += val >>> 16;
            return val & 0xff;
        }

        public Object clone() {
            return new StateInfo(this);
        }
    }
}

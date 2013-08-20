/*
 * @(#)PixmapStyle.java	1.19 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import javax.swing.plaf.synth.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.security.*;
import sun.swing.plaf.synth.*;

/**
 * PixmapStyle extends GTKStyle adding support for a set of <code>Info</code>s.
 *
 * @version 1.19, 12/19/03
 * @author Scott Violet
 */
class PixmapStyle extends GTKStyle implements GTKConstants {
    /**
     * There should only ever be one pixmap engine.
     */
    private static final GTKEngine PIXMAP_ENGINE = new PixmapEngine();


    /**
     * Set of Infos used to determine what to paint.
     */
    private Info[] info;


    /**
     * Creates a duplicate of the passed in style.
     */
    public PixmapStyle(DefaultSynthStyle style) {
        super(style);
        if (style instanceof PixmapStyle) {
            this.info = ((PixmapStyle)style).info;
        }
    }

    /**
     * Creates a PixmapStyle from the passed in arguments.
     */
    public PixmapStyle(StateInfo[] states,
                       CircularIdentityList classSpecificValues,
                       Font font,
                       int xThickness, int yThickness,
                       GTKStockIconInfo[] icons,
                       Info[] info) {
        super(states, classSpecificValues, font, xThickness, yThickness, icons);
        this.info = info;
    }

    /**
     * Adds the state of this PixmapStyle to that of <code>s</code>
     * returning a combined SynthStyle.
     */
    public DefaultSynthStyle addTo(DefaultSynthStyle s) {
        if (!(s instanceof PixmapStyle)) {
            s = new PixmapStyle(s);
        }
        PixmapStyle style = (PixmapStyle)super.addTo(s);
        if (info != null) {
            if (style.info == null) {
                style.info = info;
            }
            else {
                // Place the more specific first.
                Info[] merged = new Info[style.info.length + info.length];
                System.arraycopy(info, 0, merged, 0, info.length);
                System.arraycopy(style.info, 0, merged, info.length,
                                 style.info.length);
                style.info = merged;
            }
        }
        return style;
    }

    /**
     * Creates a copy of the reciever and returns it.
     */
    public Object clone() {
        PixmapStyle style = (PixmapStyle)super.clone();

        // Info is immutable, no need to clone it.
        style.info = this.info;
        return style;
    }

    /**
     * Returns a GTKEngine to use for rendering.
     */
    public GTKEngine getEngine(SynthContext context) {
        return PIXMAP_ENGINE;
    }

    /**
     * Returns the first instance of Info that matches the past in args, may
     * return null if nothing matches.
     *
     * @param function String name for the painting method
     * @param detail Description of what is being painted
     * @param componentState State of the Component
     * @param shadow Shadow type
     * @param orientation Orientation of what is being painted
     * @param gapSide Side of the gap being drawn
     * @param arrowDirection direction of the arrow.
     * @return Best matching Info, or null if none match
     */
    public Info getInfo(String function, String detail, int componentState,
                        int shadow, int orientation, int gapSide,
                        int arrowDirection) {
        if (info != null) {
            for (int counter = 0, max = info.length; counter < max;counter++) {
                if (info[counter].getFunction() == function && info[counter].
                              getMatchCount(detail, componentState, shadow,
                              orientation, gapSide, arrowDirection) != -1) {
                    return info[counter];
                }
            }
        }
        return null;
    }

    /**
     * Returns the number of non-null arugments and arguments that are
     * != UNDEFINED.
     */
    private int getMaxMatchCount(int componentState, int shadow,
                                 int orientation, int gapSide,
                                 int arrowDirection, String detail) {
        int count = 0;

        if (detail != null) {
            count++;
        }
        if (componentState != UNDEFINED) {
            count++;
        }
        if (shadow != UNDEFINED) {
            count++;
        }
        if (orientation != UNDEFINED) {
            count++;
        }
        if (gapSide != UNDEFINED) {
            count++;
        }
        if (arrowDirection != UNDEFINED) {
            count++;
        }
        return count;
    }


    /**
     * A description of how to paint a portion of a widget.
     */
    public static class Info {
        // match data
        private String function = null;
        private String detail = null;
        int gapSide = UNDEFINED;
        int orientation = UNDEFINED;
        int componentState = UNDEFINED;
        int shadow = UNDEFINED;
        int arrowDirection = UNDEFINED;

        boolean recolorable = false;

        // background
        Object image = null;
        Insets fileInsets = null;
        boolean stretch = false;

        // overlay
        Object overlayImage = null;
        Insets overlayInsets = null;
        boolean overlayStretch = false;

        // gap start
        Object gapStartImage = null;
        Insets gapStartInsets = null;

        // gap
        Object gapImage = null;
        Insets gapInsets = null;

        // gap end
        Object gapEndImage = null;
        Insets gapEndInsets = null;

        public void setFunction(String function) {
            this.function = function.intern();
        }

        public void setDetail(String detail) {
            this.detail = detail.intern();
        }

        public String getFunction() {
            return function;
        }

        public Image getImage() {
            image = getImage(image);
            return (Image)image;
        }

        public boolean isRecolorable() {
            return recolorable;
        }

        public Insets getImageInsets() {
            return fileInsets;
        }

        public boolean getStretch() {
            return stretch;
        }

        public String getDetail() {
            return detail;
        }

        public int getComponentState() {
            return componentState;
        }

        public int getShadow() {
            return shadow;
        }

        public int getGapSide() {
            return gapSide;
        }

        public Image getGapImage() {
            gapImage = getImage(gapImage);
            return (Image)gapImage;
        }

        public Insets getGapInsets() {
            return gapInsets;
        }

        public Image getGapStartImage() {
            gapStartImage = getImage(gapStartImage);
            return (Image)gapStartImage;
        }

        public Insets getGapStartInsets() {
            return gapStartInsets;
        }

        public Image getGapEndImage() {
            gapEndImage = getImage(gapEndImage);
            return (Image)gapEndImage;
        }

        public Insets getGapEndInsets() {
            return gapEndInsets;
        }

        public Image getOverlayImage() {
            overlayImage = getImage(overlayImage);
            return (Image)overlayImage;
        }

        public Insets getOverlayInsets() {
            return overlayInsets;
        }

        public boolean getOverlayStretch() {
            return overlayStretch;
        }

        public int getArrowDirection() {
            return arrowDirection;
        }

        public int getOrientation() {
            return orientation;
        }


        private Image getImage(final Object o) {
            if (o == null || o instanceof Image) {
                return (Image)o;
            }

            ImageIcon ii = (ImageIcon)AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return new ImageIcon((String)o);
                }
            });

            if (ii.getIconWidth() > 0 && ii.getIconHeight() > 0) {
                return ii.getImage();
            }
            
            return null;
        }

        /**
         * Will return < 0 if doesn't match, otherwise return the
         * number of parameters that match.
         */
        int getMatchCount(String detail, int componentState, int shadow,
                          int orientation, int gapSide, int arrowDirection) {
            int matchCount = 0;

            if (this.componentState != UNDEFINED) {
                if (componentState != UNDEFINED &&
                             this.componentState != componentState) {
                    return -1;
                }
                matchCount++;
            }
            if (this.shadow != UNDEFINED) {
                if (shadow != UNDEFINED && this.shadow != shadow) {
                    return -1;
                }
                matchCount++;
            }
            if (this.orientation != UNDEFINED) {
                if (orientation != UNDEFINED &&
                                   this.orientation != orientation) {
                    return -1;
                }
                matchCount++;
            }
            if (this.gapSide != UNDEFINED) {
                if (gapSide != UNDEFINED && this.gapSide != gapSide) {
                    return -1;
                }
                matchCount++;
            }
            if (this.arrowDirection != UNDEFINED) {
                if (arrowDirection != UNDEFINED &&
                                      this.arrowDirection != arrowDirection) {
                    return -1;
                }
                matchCount++;
            }
            if (this.detail != null) {
                if (this.detail != detail) {
                    return -1;
                }
                matchCount++;
            }
            return matchCount;
        }

        /**
         * Returns true if this Info matches that of the passed in Info.
         * This differs from equals in so far as this only compares the
         * properties that are used in lookup vs the actual images or
         * insets.
         *
         * @return true if the receiver and info can be considered equal
         *         for lookup.
         */
        boolean matches(Info info) {
            return (info.function == function && info.detail == detail &&
                    info.componentState == componentState &&
                    info.shadow == shadow && info.gapSide == gapSide &&
                    info.arrowDirection == arrowDirection &&
                    info.orientation == orientation);
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();

            buf.append("IMAGE:\n");

            if (function != null) {
                buf.append("    function=").append(function).append('\n');
            }

            if (detail != null) {
                buf.append("    detail=").append(detail).append('\n');
            }

            if (gapSide != UNDEFINED) {
                buf.append("    gapSide=");
                buf.append(getSideName(gapSide)).append('\n');
            }

            if (orientation != UNDEFINED) {
                buf.append("    orientation=");
                buf.append(getOrientName(orientation)).append('\n');
            }

            if (componentState != UNDEFINED) {
                buf.append("    componentState=");
                buf.append(getStateName(componentState, "UNDEFINED")).append('\n');
            }

            if (shadow != UNDEFINED) {
                buf.append("    shadow=");
                buf.append(getShadowName(shadow)).append('\n');
            }

            if (arrowDirection != UNDEFINED) {
                buf.append("    arrowDirection=");
                buf.append(getArrowDirectionName(arrowDirection)).append('\n');
            }

            if (recolorable != false) {
                buf.append("    recolorable=").append(recolorable).append('\n');
            }

            if (image != null) {
                buf.append("    image=").append(image).append('\n');
            }

            if (fileInsets != null) {
                buf.append("    fileInsets=").append(fileInsets).append('\n');
            }

            if (stretch != false) {
                buf.append("    stretch=").append(stretch).append('\n');
            }

            if (overlayImage != null) {
                buf.append("    overlayImage=").append(overlayImage).append('\n');
            }

            if (overlayInsets != null) {
                buf.append("    overlayInsets=").append(overlayInsets).append('\n');
            }

            if (overlayStretch != false) {
                buf.append("    overlayStretch=").append(overlayStretch).append('\n');
            }

            if (gapStartImage != null) {
                buf.append("    gapStartImage=").append(gapStartImage).append('\n');
            }

            if (gapStartInsets != null) {
                buf.append("    gapStartInsets=").append(gapStartInsets).append('\n');
            }

            if (gapImage != null) {
                buf.append("    gapImage=").append(gapImage).append('\n');
            }

            if (gapInsets != null) {
                buf.append("    gapInsets=").append(gapInsets).append('\n');
            }

            if (gapEndImage != null) {
                buf.append("    gapEndImage=").append(gapEndImage).append('\n');
            }

            if (gapEndInsets != null) {
                buf.append("    gapEndInsets=").append(gapEndInsets).append('\n');
            }

            buf.deleteCharAt(buf.length() - 1);

            return buf.toString();
        }
        
        private static String getSideName(int side) {
            switch(side) {
                case TOP: return "TOP";
                case BOTTOM: return "BOTTOM";
                case LEFT: return "LEFT";
                case RIGHT: return "RIGHT";
                case UNDEFINED: return "UNDEFINED";
            }
            
            return "UNKNOWN";
        }
        
        private static String getOrientName(int orient) {
            switch(orient) {
                case HORIZONTAL: return "HORIZONTAL";
                case VERTICAL: return "VERTICAL";
                case UNDEFINED: return "UNDEFINED";
            }
            
            return "UNKNOWN";
        }
        
        private static String getShadowName(int shadow) {
            switch(shadow) {
                case SHADOW_IN: return "SHADOW_IN";
                case SHADOW_OUT: return "SHADOW_OUT";
                case SHADOW_ETCHED_IN: return "SHADOW_ETCHED_IN";
                case SHADOW_ETCHED_OUT: return "SHADOW_ETCHED_OUT";
                case SHADOW_NONE: return "SHADOW_NONE";
                case UNDEFINED: return "UNDEFINED";
            }
            
            return "UNKNOWN";
        }

        private static String getArrowDirectionName(int dir) {
            switch(dir) {
                case ARROW_UP: return "ARROW_UP";
                case ARROW_DOWN: return "ARROW_DOWN";
                case ARROW_LEFT: return "ARROW_LEFT";
                case ARROW_RIGHT: return "ARROW_RIGHT";
                case UNDEFINED: return "UNDEFINED";
            }

            return "UNKNOWN";
        }
    }

    public String toString() {
        if (info == null) {
            return super.toString();
        } else {
            StringBuffer buf = new StringBuffer(super.toString());
            
            if (buf.length() > 0) {
                buf.append('\n');
            }

            buf.append("*** Pixmap Engine Info ***\n");
            for (int i = 0; i < info.length; i++) {
                buf.append(info[i].toString()).append('\n');
            }

            // remove last newline
            buf.deleteCharAt(buf.length() - 1);
            
            return buf.toString();
        }
    }

}

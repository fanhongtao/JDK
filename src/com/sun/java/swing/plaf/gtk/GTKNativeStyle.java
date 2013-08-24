/*
 * GTKNativeStyle.java
 *
 * @(#)GTKNativeStyle.java	1.4 06/01/18
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.synth.*;

import com.sun.java.swing.plaf.gtk.GTKNativeEngine.WidgetType;
import com.sun.java.swing.plaf.gtk.GTKConstants.StateType;
import sun.awt.image.CachingSurfaceManager;
import sun.awt.image.SurfaceManager;
import sun.awt.UNIXToolkit;

/**
 *
 * @version 1.4, 01/18/06
 * @author Kirill Kirichenko
 */
class GTKNativeStyle extends GTKStyle {
    
    private native int native_get_xthickness(int widgetType);
    private native int native_get_ythickness(int widgetType);
    private native int native_get_color_for_state(int widgetType, 
            int state, int typeID);
    private native Object native_get_class_value(int widgetType, String key);
    private native String native_get_pango_font_name(int widgetType);
    private native Dimension native_get_image_dimension(int widgetType, int state);
    private native void native_get_image(int[] buffer, int width, int height,
                                         int widgetType, int state);
    
    /**
     * Type of the GTK widget that has this kind of style.
     * I.e. it is responsible for the style.
     */
    private final int widgetType;
    private final int colorWidgetType;

    private final int xThickness, yThickness;
    private final Font pangoFont;
    
    /**
     * Background images for this style.
     * They are uploaded by demand.
     */
    private Object[] images = new Object[5];
    
    private static final Object EMPTY_IMAGE_TAG = "<none>";
    
    GTKNativeStyle(Font font, WidgetType widgetType) {
        super(font);
        this.widgetType = widgetType.ordinal();
        this.colorWidgetType = ((widgetType == WidgetType.MENU) ?
                                WidgetType.MENU_ITEM : widgetType).ordinal();
        int thicknessWidgetType = ((widgetType == WidgetType.MENU_BAR) ?
                         WidgetType.MENU_ITEM : widgetType).ordinal();

        String pangoFontName = null;
        synchronized(sun.awt.UNIXToolkit.GTK_LOCK) {
            xThickness = native_get_xthickness(thicknessWidgetType);
            yThickness = native_get_ythickness(thicknessWidgetType);
            pangoFontName = native_get_pango_font_name(this.widgetType);
        }
        pangoFont = (pangoFontName != null) ?
            PangoFonts.lookupFont(pangoFontName) : null;
    }

    Color getStyleSpecificColor(SynthContext context, int state, ColorType type) {
        state = GTKLookAndFeel.synthStateToGTKStateType(state).ordinal();
        synchronized(sun.awt.UNIXToolkit.GTK_LOCK) {
            int rgb = native_get_color_for_state(colorWidgetType, state, type.getID());
            return new ColorUIResource(rgb);
        }
    }

    /**
     * Returns the font for the specified state.
     *
     * @param c JComponent the style is associated with
     * @param id Region identifier
     * @param state State of the region.
     * @return Font to render with
     */
    protected Font getFontForState(JComponent c, Region id, int state) {
        if (pangoFont != null) {
            return pangoFont;
        } else {
            return super.getFontForState(c, id, state);
        }
    }    
    
    /**
     * Returns the X thickness to use for this GTKStyle.
     *
     * @return x thickness.
     */
    int getXThickness() {
        return xThickness;
    }

    /**
     * Returns the Y thickness to use for this GTKStyle.
     *
     * @return x thickness.
     */
    int getYThickness() {
        return yThickness;
    }
    
    /**
     * Returns the value for a class specific property. A class specific value
     * is a value that will be picked up based on class hierarchy.
     *
     * @param context SynthContext indentifying requestor
     * @param key Key identifying class specific value
     * @return Value, or null if one has not been defined.
     */
    Object getClassSpecificValue(Region region, String key) {
        synchronized(sun.awt.UNIXToolkit.GTK_LOCK) {
            return native_get_class_value(widgetType, key);
        }
    }

    /**
     * Returns true if the style should fill in the background of the
     * specified context for the specified state.
     */
    boolean fillBackground(SynthContext context, int state) {
        Object image = getBackgroundImage(state);
        return image == EMPTY_IMAGE_TAG;
    }

    /**
     * Returns the Icon to fill the background in with for the specified
     * context and state.
     */
    Image getBackgroundImage(SynthContext context, int state) {
        Object image = getBackgroundImage(state);
        return (image == EMPTY_IMAGE_TAG) ? null : (Image)image;
    }

    private Object getBackgroundImage(int state) {
        state = GTKLookAndFeel.synthStateToGTKStateType(state).ordinal();
        
        if (images[state] != null) {
            return images[state];
        }
        
        Dimension size = null;
        
        synchronized(sun.awt.UNIXToolkit.GTK_LOCK) {
            size = native_get_image_dimension(widgetType, state);
        }
        
        if (size != null) {
            BufferedImage image = new BufferedImage(size.width, size.height, 
                                                    BufferedImage.TYPE_INT_RGB);

            // Getting the DataBuffer for an image (as it's done below) defeats
            // possible future acceleration.
            // Calling setLocalAccelerationEnabled on that image's surface
            // manager re-enables it.
            boolean accelerated = false;
            CachingSurfaceManager csm = null;
            SurfaceManager sm = SurfaceManager.getManager(image);
            if (sm instanceof CachingSurfaceManager) {
                csm = (CachingSurfaceManager)sm;
                accelerated = csm.isLocalAccelerationEnabled();
            }

            DataBufferInt data = (DataBufferInt)image.getRaster().getDataBuffer();
            synchronized(sun.awt.UNIXToolkit.GTK_LOCK) {
                native_get_image(data.getData(), size.width, size.height,
                                 widgetType, state);
            }
            
            if (csm != null && accelerated != csm.isLocalAccelerationEnabled()) {
                csm.setLocalAccelerationEnabled(accelerated);
                csm.rasterChanged();
            }
            
            return images[state] = image;
        }
        
        return images[state] = EMPTY_IMAGE_TAG;
    }

    Icon getStyleSpecificIcon(String key, TextDirection direction, int type) {
        Image img = ((UNIXToolkit)Toolkit.getDefaultToolkit()).
                getStockIcon(widgetType, key, type, direction.ordinal(), null);
        
         return (img != null) ? new ImageIcon(img) : null;
    }
}

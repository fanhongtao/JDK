/*
 * @(#)GTKStyleFactory.java	1.34 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import com.sun.java.swing.plaf.gtk.GTKNativeEngine.WidgetType;
import javax.swing.plaf.synth.*;
import javax.swing.*;
import java.util.*;

import java.awt.Font;
import java.awt.Toolkit;
import sun.awt.UNIXToolkit;

/**
 *
 * @author Scott Violet
 */

class GTKStyleFactory extends SynthStyleFactory {
    /**
     * States if there is native GTK support.
     */
    private static final boolean isNativeGtk;
    
    static {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        isNativeGtk = (toolkit instanceof UNIXToolkit &&
                      ((UNIXToolkit)toolkit).checkGTK());
    }

    /**
     * Saves all styles that have been accessed.
     */
    private Map<WidgetType,GTKStyle> stylesCache;
    
    private Font defaultFont;
    
    GTKStyleFactory() {
        stylesCache = new HashMap<WidgetType, GTKStyle>();
    }
    
    /**
     * Returns the <code>GTKStyle</code> to use based on the
     * <code>Region</code> id
     *
     * @param c this parameter isn't used, may be null.
     * @param id of the region to get the style.
     */
    public synchronized SynthStyle getStyle(JComponent c, Region id) {
        WidgetType wt = GTKNativeEngine.getWidgetType(c, id);
        
        GTKStyle result = stylesCache.get(wt);
        if (result == null) {
            result = isNativeGtk ? 
                     new GTKNativeStyle(defaultFont, wt) : 
                     new GTKDefaultStyle(defaultFont);
            
            stylesCache.put(wt, result);
        }
        
        return result;
    }

    void initStyles(Font defaultFont) {
        this.defaultFont = defaultFont;
        stylesCache.clear();
    }
}

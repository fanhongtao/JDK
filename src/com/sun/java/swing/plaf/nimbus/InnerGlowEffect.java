/*
 * @(#)InnerGlowEffect.java	1.2 07/12/12
 *
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.nimbus;

import java.awt.Color;

/**
 * InnerGlowEffect
 *
 * @author Created by Jasper Potts (Jun 21, 2007)
 * @version 1.0
 */
class InnerGlowEffect extends InnerShadowEffect {
    InnerGlowEffect() {
        distance = 0;
        color = new Color(255, 255, 211);
    }
}

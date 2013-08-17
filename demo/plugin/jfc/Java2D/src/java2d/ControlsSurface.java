/*
 * @(#)ControlsSurface.java	1.4 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java2d;

import java.awt.Component;

/**
 * The class to utilize custom controls for a Demo.
 */
public abstract class ControlsSurface extends Surface implements CustomControlsContext {

    public void setControls(Component[] controls) {
        this.controls = controls;
    }
  
    public void setConstraints(String[] constraints) {
        this.constraints = constraints;
    }
    
    public String[] getConstraints() {
        return constraints;
    }

    public Component[] getControls() { 
        return controls;
    }

    public void handleThread(int state) {
        for (int i = 0; i < controls.length; i++) {
            if (state == CustomControlsContext.START) {
                if (controls[i] instanceof CustomControls) {
                    ((CustomControls) controls[i]).start();
                }
            } else if (state == CustomControlsContext.STOP) {
                if (controls[i] instanceof CustomControls) {
                    ((CustomControls) controls[i]).stop();
                }
            }
        }
    }

    private Component[] controls;
    private String[] constraints = { java.awt.BorderLayout.NORTH };
}

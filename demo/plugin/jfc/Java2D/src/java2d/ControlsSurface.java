/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

/*
 * @(#)ControlsSurface.java	1.6 03/01/23
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

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.awt.Component;

/**
 * ControlsSurface or AnimatingControlsSurface classes implement 
 * this interface.
 */
public interface CustomControlsContext {

    public static final int START = 0;
    public static final int STOP = 1;

    public String[] getConstraints();
    public Component[] getControls();
    public void setControls(Component[] controls);
    public void setConstraints(String[] constraints);
    public void handleThread(int state);
}

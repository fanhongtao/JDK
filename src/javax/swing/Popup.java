/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import java.awt.*;

    /*
     * The following interface describes what a popup should implement.
     * We do this because JPopupMenu uses popup that can be windows or
     * panels. 
     */
interface Popup {
    public void show(Component invoker);
    public boolean isShowing();
    public void hide();
    
    public Rectangle getBoundsOnScreen();
    public void setLocationOnScreen(int x,int y);
    
    public void setSize(int width,int height);
    public int  getWidth();
    public int  getHeight();
    
    public void addComponent(Component aComponent,Object constraints);
    public void removeComponent(Component c);
    
    public void pack();
}

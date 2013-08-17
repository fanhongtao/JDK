/*
 * @(#)Popup.java	1.3 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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

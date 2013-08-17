/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package com.sun.java.swing.plaf.windows;

import javax.swing.DefaultDesktopManager;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.util.Vector;

/**
 * This class implements a DesktopManager which more closely follows 
 * the MDI model than the DefaultDesktopManager.  Unlike the
 * DefaultDesktopManager policy, MDI requires that the selected
 * and activated child frames are the same, and that that frame
 * always be the top-most window.
 * <p>
 * The maximized state is managed by the DesktopManager with MDI,
 * instead of just being a property of the individual child frame.
 * This means that if the currently selected window is maximized
 * and another window is selected, that new window will be maximized.
 *
 * @see javax.swing.DefaultDesktopManager
 * @version 1.12 02/06/02
 * @author Thomas Ball
 */
public class WindowsDesktopManager extends DefaultDesktopManager 
        implements java.io.Serializable {

    /* The frame which is currently selected/activated.
     * We store this value to enforce MDI's single-selection model.
     */
    JInternalFrame currentFrame;
    JInternalFrame initialFrame;	  

    /* The list of frames, sorted by order of creation.
     * This list is necessary because by default the order of
     * child frames in the JDesktopPane changes during frame
     * activation (the activated frame is moved to index 0).
     * We preserve the creation order so that "next" and "previous"
     * frame actions make sense.
     */
    Vector childFrames = new Vector(1);

    public void closeFrame(JInternalFrame f) {
        if (f == currentFrame) { activateNextFrame(); }
        childFrames.removeElement(f);
        super.closeFrame(f);
    }

    public void activateFrame(JInternalFrame f) {
        try {
            super.activateFrame(f);

            // If this is the first activation, add to child list.
            if (childFrames.indexOf(f) == -1) {
                childFrames.addElement(f);
            }

            if (currentFrame != null && f != currentFrame) {
                // If the current frame is maximized, transfer that 
                // attribute to the frame being activated.
                if (currentFrame.isMaximum()) {
                    currentFrame.setMaximum(false);
                    f.setMaximum(true);
                }
                if (currentFrame.isSelected()) {
                    currentFrame.setSelected(false);
                }
            }

            if (!f.isSelected()) {
                f.setSelected(true);
            }
            currentFrame = f;
        } catch (PropertyVetoException e) {}
    }

    private void switchFrame(boolean next) {
        if (currentFrame == null) {
	    // initialize first frame we find
	    if (initialFrame != null)
	      activateFrame(initialFrame);
            return;
        }

        int count = childFrames.size();
        if (count <= 1) {
            // No other child frames.
            return;
        }

        int currentIndex = childFrames.indexOf(currentFrame);
        if (currentIndex == -1) {
            // should never happen...
            return;
        }

        int nextIndex;
        if (next) {
            nextIndex = currentIndex + 1;
            if (nextIndex == count) {
                nextIndex = 0;
            }
        } else {
            nextIndex = currentIndex - 1;
            if (nextIndex == -1) {
                nextIndex = count - 1;
            }
        }
        JInternalFrame f = (JInternalFrame)childFrames.elementAt(nextIndex);
        activateFrame(f);
        currentFrame = f;
    }
    
    /**
     * Activate the next child JInternalFrame, as determined by
     * the frames' Z-order.  If there is only one child frame, it
     * remains activated.  If there are no child frames, nothing 
     * happens.  */
    public void activateNextFrame() {
        switchFrame(true);
    }

    /** same as above but will activate a frame if none
     *  have been selected
     */
    public void activateNextFrame(JInternalFrame f){
      initialFrame = f;
      switchFrame(true);
    }
    
    /**
     * Activate the previous child JInternalFrame, as determined by
     * the frames' Z-order.  If there is only one child frame, it
     * remains activated.  If there are no child frames, nothing 
     * happens.
     */
    public void activatePreviousFrame() {
        switchFrame(false);
    }
}

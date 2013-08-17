/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.util.Hashtable;
import java.awt.event.KeyEvent;
import java.awt.Component;

/**
 * Swing Focus Manager
 *
 * @version 1.12 02/06/02
 * @author Arnaud Weber
 */
public abstract class FocusManager {

    /** This property name is used to get the FocusManager implementation
     *  that should be used for a given UI
     */
    public static final String FOCUS_MANAGER_CLASS_PROPERTY = 
        "FocusManagerClassName";

    private static final Object focusManagerKey = FocusManager.class;

    /** Return the FocusManager for the calling thread 
     *  There is one FocusManager per thread group
     */
    public static FocusManager getCurrentManager() {
        FocusManager result = 
            (FocusManager)SwingUtilities.appContextGet(focusManagerKey);
        if(result == null) {
            String className = 
                UIManager.getString(FOCUS_MANAGER_CLASS_PROPERTY);
            try {
                Class c = Class.forName(className);
                if(c != null) {
                    result = (FocusManager) c.newInstance();
                }
            } catch (ClassNotFoundException e) {
                System.out.println("Cannot find class " + className + " " + e);
                result = null;
            } catch (InstantiationException e) {
                System.out.println("Cannot instantiate class " + className + " " + e);
                result = null;
            } catch (IllegalAccessException e) {
                System.out.println("Cannot access class " + className + " " + e);
                result = null;
            }
            
            if(result == null) {
                result = new DefaultFocusManager();
            }
            SwingUtilities.appContextPut(focusManagerKey, result);
        }
        return result;
    }

    /** Set the FocusManager that should be used for the calling 
     *  thread. <b>aFocusManager</b> will be the default focus
     *  manager for the calling thread's thread group.
     */
    public static void setCurrentManager(FocusManager aFocusManager) {
        if (aFocusManager != null) {
            SwingUtilities.appContextPut(focusManagerKey, aFocusManager);
        } else {
            SwingUtilities.appContextRemove(focusManagerKey);
        }
    }

    /** Disable Swing's focus manager for the calling thread's thread group.
     *  Call this method if your application mixes java.awt components and
     *  swing's components. Your application will then use the awt focus 
     *  manager.
     */
    public static void disableSwingFocusManager() {
        setCurrentManager(new DisabledFocusManager());
    }

    /** Return whether Swing's focus manager is enabled **/
    public static boolean isFocusManagerEnabled() {
        FocusManager fm = getCurrentManager();
        return !(fm instanceof DisabledFocusManager);
    }

    /** This method is called by JComponents when a key event occurs.
     *  JComponent gives key events to the focus manager
     *  first, then to key listeners, then to the keyboard UI dispatcher.
     *  This method should look at the key event and change the focused
     *  component if the key event matches the receiver's focus manager
     *  hot keys. For example the default focus manager will change the
     *  focus if the key event matches TAB or Shift + TAB.
     *  The focus manager should call consume() on <b>anEvent</b> if 
     *  <code>anEvent</code> has been processed. 
     *  <code>focusedComponent</code> is the component that currently has
     *  the focus.
     *  Note: FocusManager will receive both KEY_PRESSED and KEY_RELEASED
     *  key events. If one event is consumed, the other one should be consumed
     *  too.
     */
    public abstract void processKeyEvent(Component focusedComponent,KeyEvent anEvent);


    /** Cause the focus manager to set the focus on the next focusable component 
     *  You can call this method to cause the focus manager to focus the next component
     **/
    public abstract void focusNextComponent(Component aComponent);

    /** Cause the focus manager to set the focus on the previous focusable component 
     *  You can call this methid to cause the focus manager to focus the previous component
     */
    public abstract void focusPreviousComponent(Component aComponent);

    static class DisabledFocusManager extends FocusManager {
        public void processKeyEvent(Component focusedComponent,KeyEvent anEvent) {}
        public void focusNextComponent(Component c) {}
        public void focusPreviousComponent(Component c) {}
    }
}

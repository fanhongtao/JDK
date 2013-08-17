/*
 * @(#)InputContext.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.awt.im;

import java.awt.Component;
import java.util.Locale;
import java.awt.AWTEvent;
import java.lang.Character.Subset;
import sun.awt.im.InputMethod;
import sun.awt.im.InputMethodContext;

/**
 * An InputContext object manages the communication between text editing
 * components and input methods. It dispatches events between them, and
 * forwards requests for information from the input method to the text
 * editing component. It also lets text editing components select input
 * methods by locale.
 *
 * <p>
 * By default, one InputContext instance is created per Window instance,
 * and this input context is shared by all components within the window's
 * container hierarchy. However, this means that only one text input
 * operation is possible at any one time within a window, and that the
 * text needs to be committed when moving the focus from one text component
 * to another. If this is not desired, text components can create their
 * own input context instances.
 *
 * <p>
 * Not all platforms and locales support input methods. Where input methods are
 * unavailable, input contexts can still be created and used; the
 * InputContext instance methods return without doing anything (selectLocale
 * returns false).
 *
 * @see java.awt.Component#getInputContext
 * @see java.awt.Component#enableInputMethods
 * @version 1.14 05/09/98
 * @author JavaSoft Asia/Pacific
 */

public class InputContext {

    /**
     * Constructs an InputContext.
     */
    protected InputContext() {
        // real implementation is in sun.awt.im.InputContext
    }

    /**
     * Returns a new InputContext instance.
     */
    public static InputContext getInstance() {
	return new InputMethodContext();
    }

    /**
     * Selects an input method that supports the given locale.
     * If the currently selected input method supports the desired locale
     * or if there's no input method available that supports the desired
     * locale, the current input method remains active. Otherwise, an input
     * method is selected that supports text input for the desired locale.
     * Before switching to a different input method, any currently uncommitted
     * text is committed.
     * If the platform does not support input methods or the desired locale,
     * then false is returned.
     *
     * <p>
     * A text editing component may call this method, for example, when
     * the user changes the insertion point, so that the user can
     * immediately continue typing in the language of the surrounding text.
     *
     * @param locale The desired new locale.
     * @return Whether the input method that's active after this call
     *         supports the desired locale.
     */
    public boolean selectInputMethod(Locale locale) {
        // real implementation is in sun.awt.im.InputContext
        return false;
    }

    /**
     * Sets the subsets of the Unicode character set that input methods of this input
     * context should be allowed to input. Null may be passed in to
     * indicate that all characters are allowed. The initial value
     * is null. The setting applies to the current input method as well
     * as input methods selected after this call is made. However,
     * applications cannot rely on this call having the desired effect,
     * since this setting cannot be passed on to all host input methods -
     * applications still need to apply their own character validation.
     * If the platform does not support input methods, then this method
     * has no effect.
     *
     * @param subsets The subsets of the Unicode character set from which characters may be input
     */
    public void setCharacterSubsets(Subset[] subsets) {
        // real implementation is in sun.awt.im.InputContext
    }
    
    /**
     * Dispatches an event to the active input method. Called by AWT.
     * If the platform does not support input methods, then the event
     * will never be consumed.
     *
     * @param event The event
     */
    public synchronized void dispatchEvent(AWTEvent event) {
        // real implementation is in sun.awt.im.InputContext
    }

    /**
     * Notifies the input context that a client component has been
     * removed from its containment hierarchy, or that input method
     * support has been disabled for the component. This method is
     * usually called from java.awt.Component.removeNotify() of the
     * client component. Potentially pending input from input methods
     * for this component is discarded.
     *
     * @param client Client component
     */
    public void removeNotify(Component client) {
	// real implementation is in sun.awt.im.InputContext
    }

    /**
     * Ends any input composition that may currently be going on in this
     * context. Depending on the platform and possibly user preferences,
     * this may commit or delete uncommitted text. Any changes to the text
     * are communicated to the active component using an input method event.
     * If the platform does not support input methods, then this method
     * has no effect.
     *
     * <p>
     * A text editing component may call this in a variety of situations,
     * for example, when the user moves the insertion point within the text
     * (but outside the composed text), or when the component's text is
     * saved to a file or copied to the clipboard.
     *
     */
    public synchronized void endComposition() {
        // real implementation is in sun.awt.im.InputContext
    }

    /**
     * Disposes of the input context and release the resources used by it.
     * Called by AWT.
     * If the platform does not support input methods, then this method
     * has no effect.
     */
    public void dispose() {
        // real implementation is in sun.awt.im.InputContext
    }

    /**
     * Returns a control object from the current input method, or null. A
     * control object provides methods that control the behavior of the
     * input method or obtain information from the input method. The type
     * of the object is an input method specific class. Clients have to
     * compare the result against known input method control object
     * classes and cast to the appropriate class to invoke the methods
     * provided.
     * If the platform does not support input methods, then null
     * is returned.
     *
     * @return A control object from the current input method, or null.
     */
    public Object getInputMethodControlObject() {
        // real implementation is in sun.awt.im.InputContext
        return null;
    }

}

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import java.awt.Color;
import java.awt.Graphics;

import java.text.Format;
import java.text.NumberFormat;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import javax.swing.event.*;
import javax.accessibility.*;
import javax.swing.plaf.ProgressBarUI;


/**
 * A component that displays an integer value within a bounded 
 * interval. A progress bar typically communicates the progress of an 
 * event by displaying its percentage of completion and possibly a textual
 * display of this percentage.
 *
 * <p>
 *
 * For further documentation and examples see
 * <a
 href="http://java.sun.com/docs/books/tutorial/uiswing/components/progress.html">How to Monitor Progress</a>,
 * a section in <em>The Java Tutorial.</em>
 *
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @beaninfo
 *      attribute: isContainer false
 *    description: A component that displays an integer value.
 *
 * @version 1.80 02/06/02
 * @author Michael C. Albers
 */
public class JProgressBar extends JComponent implements SwingConstants, Accessible
{
    /**
     * @see #getUIClassID
     */
    private static final String uiClassID = "ProgressBarUI";

    /**
     * The orientation to display the progress bar.
     * The default is HORIZONTAL.
     */
    protected int orientation;
    /**
     * Whether to display the border around the progress bar.
     * The default is true.
     */
    protected boolean paintBorder;
    /**
     * The data structure that holds the various values for the progress bar.
     */
    protected BoundedRangeModel model;
    /**
     * A optional String that can be displayed on the progress bar.
     * The default is null. Setting this to a non-null value does not
     * imply that the String will be displayed.
     */
    protected String progressString;
    /**
     * Whether to textually display a String on the progress bar.
     * The default is false. Setting this to true will cause a textual
     * display of the progress to de rendered on the progress bar. If
     * the progressString is null, the percentage done to be displayed 
     * on the progress bar. If the progressString is non-null, it is
     * rendered on the progress bar.
     */
    protected boolean paintString;
    /**
     * The default minimum for a progress bar is 0.
     */
    static final private int defaultMinimum = 0;
    /**
     * The default maximum for a progress bar is 100.
     */
    static final private int defaultMaximum = 100;
    /**
     * The default orientation for a progress bar is HORIZONTAL.
     */
    static final private int defaultOrientation = HORIZONTAL;

    /**
     * Only one ChangeEvent is needed per instance since the
     * event's only interesting property is the immutable source, which
     * is the progress bar.
     */
    protected transient ChangeEvent changeEvent = null;
    protected ChangeListener changeListener = null;

    /**
     * Format used when displaying percent complete.
     */
    private transient Format format;


   /**
     * Creates a horizontal progress bar.
     * The default orientation for progress bars is
     * <code>JProgressBar.HORIZONTAL</code>.
     * By default, the String is set to <code>null</code> and the 
     * StringPainted is not painted.
     * The border is painted by default.
     * Uses the defaultMinimum (0) and defaultMaximum (100).
     * Uses the defaultMinimum for the initial value of the progress bar.
     */
    public JProgressBar()
    {
	this(defaultOrientation);
    }

   /**
     * Creates a progress bar with the specified orientation, which can be 
     * either <code>JProgressBar.VERTICAL</code> or 
     * <code>JProgressBar.HORIZONTAL</code>.
     * By default, the String is set to <code>null</code> and the 
     * StringPainted is not painted.
     * The border is painted by default.
     * Uses the defaultMinimum (0) and defaultMaximum (100).
     * Uses the defaultMinimum for the initial value of the progress bar.
     */
    public JProgressBar(int orient)
    {
	this(orient, defaultMinimum, defaultMaximum);
    }


    /**
     * Creates a horizontal progress bar, which is the default.
     * By default, the String is set to <code>null</code> and the 
     * StringPainted is not painted.
     * The border is painted by default.
     * Uses the specified minimum and maximum.
     * Uses the specified minimum for the initial value of the progress bar.
     */
    public JProgressBar(int min, int max)
    {
	this(defaultOrientation, min, max);
    }


    /**
     * Creates a progress bar using the specified orientation,
     * minimum, and maximum.
     * By default, the String is set to <code>null</code> and the 
     * StringPainted is not painted.
     * The border is painted by default.
     * Sets the inital value of the progress bar to the specified minimum.
     * The BoundedRangeModel that sits underneath the progress bar
     * handles any issues that may arrise from improperly setting the 
     * minimum, value, and maximum on the progress bar.
     *
     * @see BoundedRangeModel
     * @see #setOrientation
     * @see #setBorderPainted
     * @see #setStringPainted
     * @see #setString
     */
    public JProgressBar(int orient, int min, int max)
    {
	// Creating the model this way is a bit simplistic, but
	//  I believe that it is the the most common usage of this
	//  component - it's what people will expect.
        setModel(new DefaultBoundedRangeModel(min, 0, min, max));
        updateUI();

        setOrientation(orient);      // documented with set/getOrientation()
        setBorderPainted(true);      // documented with is/setBorderPainted()
	setStringPainted(false);     // see setStringPainted
	setString(null);             // see getString
    }


    /**
     * Creates a horizontal progress bar, the default orientation.
     * By default, the String is set to <code>null</code> and the 
     * StringPainted is not painted.
     * The border is painted by default.
     * Uses the specified BoundedRangeModel
     * which holds the minimum, value, and maximum.
     *
     * @see BoundedRangeModel
     * @see #setOrientation
     * @see #setBorderPainted
     * @see #setStringPainted
     * @see #setString
     */
    public JProgressBar(BoundedRangeModel newModel)
    {
        setModel(newModel);
        updateUI();

        setOrientation(defaultOrientation);  // see setOrientation()
        setBorderPainted(true);              // see setBorderPainted()
	setStringPainted(false);             // see setStringPainted
	setString(null);                     // see getString
    }


    /**
     * Returns <code>JProgressBar.VERTICAL</code> or 
     * <code>JProgressBar.HORIZONTAL</code>, depending on the orientation
     * of the progress bar. The default orientation is 
     * <code>HORIZONTAL</code>.
     *
     * @return HORIZONTAL or VERTICAL
     * @see #setOrientation
     */
    public int getOrientation() {
        return orientation;
    }


   /**
     * Sets the progress bar's orientation to <I>newOrientation</I>, which
     * must be <code>JProgressBar.VERTICAL</code> or 
     * <code>JProgressBar.HORIZONTAL</code>. The default orientation 
     * is <code>HORIZONTAL</code>.
     *
     * @param  newOrientation  HORIZONTAL or VERTICAL
     * @exception      IllegalArgumentException    if <I>newOrientation</I>
     *                                              is an illegal value
     * @see #getOrientation
     *
     * @beaninfo
     *    preferred: true
     *        bound: true
     *    attribute: visualUpdate true
     *  description: Set the progress bar's orientation.
     */
    public void setOrientation(int newOrientation) {
        if (orientation != newOrientation) {
            switch (newOrientation) {
            case VERTICAL:
            case HORIZONTAL:
                int oldOrientation = orientation;
                orientation = newOrientation;
                firePropertyChange("orientation", oldOrientation, newOrientation);
                if (accessibleContext != null) {
                    accessibleContext.firePropertyChange(
			    AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                            ((oldOrientation == VERTICAL) 
			     ? AccessibleState.VERTICAL 
			     : AccessibleState.HORIZONTAL),
                            ((orientation == VERTICAL) 
			     ? AccessibleState.VERTICAL 
			     : AccessibleState.HORIZONTAL));
	        }
                break;
            default:
                throw new IllegalArgumentException(newOrientation +
                                             " is not a legal orientation");
            }
            revalidate();
        }
    }


    /**
     * Returns true if the progress bar will render a string onto
     * the representation of the progress bar. Returns false if it
     * will not do this rendering. The default is false - the progress
     * bar does not draw the string by default.
     *
     * @return whether the progress bar renders a string
     * @see    #setStringPainted
     * @see    #setString
     */
    public boolean isStringPainted() {
        return paintString;
    }


    /**
     * Sets whether the progress bar will render a string.
     *
     * @param   b       true if the progress bar will render a string.
     * @see     #isStringPainted
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: Whether the progress bar will render a string.
     */
    public void setStringPainted(boolean b) {
        boolean oldValue = paintString;
        paintString = b;
        firePropertyChange("stringPainted", oldValue, paintString);
        if (paintString != oldValue) {
	    revalidate();
            repaint();
        }
    }


    /**
     * Returns the current value of the Progress String.
     * If you are providing a custom Progress String via this method,
     * you will want to ensure that you call setString() before
     * you call getString();
     *
     * @return the value of the percent string
     * @see    #setString
     */
    public String getString(){
	if (progressString != null) {
	    return progressString;
	} else {
            if (format == null) {
                format = NumberFormat.getPercentInstance();
            }
            return format.format(new Double(getPercentComplete()));
	}
    }

    /**
     * Sets the value of the Progress String. By default,
     * this String is set to <code>null</code>.
     * If you are providing a custom Progress String via this method,
     * you will want to ensure that you call setString() before
     * you call getString().
     * If you have provided a custom String and want to revert to 
     * the built-in behavior, set the String back to <code>null</code>.
     *
     * @param  s       the value of the percent string
     * @see    #getString
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: Whether the progress bar will render a percent string
     */
    public void setString(String s){
        String oldValue = progressString;
	progressString = s;
        firePropertyChange("string", oldValue, progressString);
        if (progressString == null || oldValue == null || !progressString.equals(oldValue)) {
	    repaint();
        }
    }

    /**
     * Returns the percentage/percent complete for the progress bar.
     * Note that, as a double, this number is between 0.00 and 1.00.
     *
     * @return the percent complete for this progress bar. 
     */
    public double getPercentComplete() {
	long span = model.getMaximum() - model.getMinimum();
	double currentValue = model.getValue();
	double pc = (currentValue - model.getMinimum()) / span;
	return pc;
    }

    /**
     * Returns true if the progress bar has a border or false if it does not.
     * By default, this is true - the progress bar paints it's border.
     *
     * @return whether the progress bar paints its border
     * @see    #setBorderPainted
     * @beaninfo
     *  description: Does the progress bar paint its border
     */
    public boolean isBorderPainted() {
        return paintBorder;
    }

    /**
     * Sets whether the progress bar should paint its border.
     * By default, this is true - paint the border.
     *
     * @param   b       true if the progress bar paints its border
     * @see     #isBorderPainted
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: Whether the progress bar should paint its border.
     */
    public void setBorderPainted(boolean b) {
        boolean oldValue = paintBorder;
        paintBorder = b;
        firePropertyChange("borderPainted", oldValue, paintBorder);
        if (paintBorder != oldValue) {
            repaint();
        }
    }

    /**
     * Paint the progress bar's border if BorderPainted property is true.
     * 
     * @param g  the Graphics context within which to paint the border
     * @see #paint
     * @see #setBorder
     * @see #isBorderPainted
     * @see #setBorderPainted
     */
    protected void paintBorder(Graphics g) {    
        if (isBorderPainted()) {
            super.paintBorder(g);
        }
    }


    /**
     * Returns the L&F object that renders this component.
     *
     * @return the ProgressBarUI object that renders this component
     */
    public ProgressBarUI getUI() {
        return (ProgressBarUI)ui;
    }

    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui  the ProgressBarUI L&F object
     * @see UIDefaults#getUI
     * @beaninfo
     *       expert: true
     *    attribute: visualUpdate true
     *  description: The ProgressBarUI implementation that defines the progress bar's look and feel.
     */
    public void setUI(ProgressBarUI ui) {
        super.setUI(ui);
    }


    /**
     * Notification from the UIFactory that the L&F has changed. 
     * Called to replace the UI with the latest version from the 
     * UIFactory.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((ProgressBarUI)UIManager.getUI(this));
    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "ProgressBarUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     * @beaninfo
     *        expert: true
     *   description: A string that specifies the name of the L&F class.
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /* We pass each Change event to the listeners with the
     * the progress bar as the event source.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    private class ModelListener implements ChangeListener, Serializable {
        public void stateChanged(ChangeEvent e) {
            fireStateChanged();
        }
    }

    /* Subclasses that want to handle ChangeEvents differently
     * can override this to return a subclass of ModelListener or
     * another ChangeListener implementation.
     */
    protected ChangeListener createChangeListener() {
        return new ModelListener();
    }

    /**
     * Adds a ChangeListener to the button.
     *
     * @param l the ChangeListener to add
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    
    /**
     * Removes a ChangeListener from the button.
     *
     * @param l the ChangeListener to remove
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }
        
    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireStateChanged() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ChangeListener.class) {
                // Lazily create the event:
                if (changeEvent == null)
                    changeEvent = new ChangeEvent(this);
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }          
        }
    } 
      
    /**
     * Returns the data model used by the JProgressBar.
     *
     * @return the BoundedRangeModel currently in use
     * @see    BoundedRangeModel
     */
    public BoundedRangeModel getModel() {
        return model;
    }

    /**
     * Sets the data model used by the JProgressBar.
     *
     * @param  newModel the BoundedRangeModel to use
     * @see    BoundedRangeModel
     * @beaninfo
     *    expert: true
     * description: The data model used by the JProgressBar.
     */
    public void setModel(BoundedRangeModel newModel) {
        // PENDING(???) setting the same model to multiple bars is broken; listeners
        BoundedRangeModel oldModel = getModel();

        if (newModel != oldModel) {
            if (oldModel != null) {
                oldModel.removeChangeListener(changeListener);
                changeListener = null;
            }

            model = newModel;

            if (newModel != null) {
                changeListener = createChangeListener();
                newModel.addChangeListener(changeListener);
            }

	    if (accessibleContext != null) {
                accessibleContext.firePropertyChange(
		        AccessibleContext.ACCESSIBLE_VALUE_PROPERTY,
                        (oldModel== null 
			 ? null : new Integer(oldModel.getValue())),
                        (newModel== null 
			 ? null : new Integer(newModel.getValue())));
	    }

            model.setExtent(0);
            repaint();
        }
    }


    /* All of the model methods are implemented by delegation. */

    /**
     * Returns the model's current value. The value is always between the 
     * model's minimum and maximum values, inclusive. By default, the value
     * equals the minimum.
     *
     * @return  the value
     * @see     #setValue
     * @see     BoundedRangeModel
     */
    public int getValue() { return getModel().getValue(); }

    /**
     * Returns the model's minimum value.
     * By default, this is <code>0</code>.
     *
     * @return  an int -- the model's minimum
     * @see     #setMinimum
     * @see     BoundedRangeModel
     */
    public int getMinimum() { return getModel().getMinimum(); }

    /**
     * Returns the model's maximum value.
     * By default, this is <code>100</code>.
     *
     * @return  an int -- the model's maximum
     * @see     #setMaximum
     * @see     BoundedRangeModel
     */
    public int getMaximum() { return getModel().getMaximum(); }

    /**
     * Sets the model's current value to <I>x</I>.
     * The underlying BoundedRangeModel will handle any mathematical
     * issues arrising from assigning faulty values.
     *
     * @param   x       the new value
     * @see     #getValue
     * @see     BoundedRangeModel#setValue
     * @beaninfo
     *    preferred: true
     *  description: The model's current value.
     */
    public void setValue(int n) { 
        BoundedRangeModel brm = getModel();
	int oldValue = brm.getValue();
	brm.setValue(n);
 
	if (accessibleContext != null) {
            accessibleContext.firePropertyChange(
		    AccessibleContext.ACCESSIBLE_VALUE_PROPERTY,
                    new Integer(oldValue),
                    new Integer(brm.getValue()));
	}
    }

    /**
     * Sets the model's minimum to <I>x</I>.
     * The underlying BoundedRangeModel will handle any mathematical
     * issues arrising from assigning faulty values.
     * <p>
     * Notifies any listeners if the data changes.
     *
     * @param  x       the new minimum
     * @see    #getMinimum
     * @see    #addChangeListener
     * @see    BoundedRangeModel
     * @beaninfo
     *  preferred: true
     * description: The model's minimum value.
     */
    public void setMinimum(int n) { getModel().setMinimum(n); }

    /**
     * Sets the model's maximum to <I>x</I>.
     * The underlying BoundedRangeModel will handle any mathematical
     * issues arrising from assigning faulty values.
     * <p>
     * Notifies any listeners if the data changes.
     *
     * @param  x       the new maximum
     * @see    #getMaximum
     * @see    #addChangeListener
     * @see    BoundedRangeModel
     * @beaninfo
     *    preferred: true
     *  description: The model's maximum value.
     */
    public void setMaximum(int n) { getModel().setMaximum(n); }


    /** 
     * See readObject() and writeObject() in JComponent for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    ui.installUI(this);
	}
    }


    /**
     * Returns a string representation of this JProgressBar. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JProgressBar.
     */
    protected String paramString() {
	String orientationString = (orientation == HORIZONTAL ?
				    "HORIZONTAL" : "VERTICAL");
	String paintBorderString = (paintBorder ?
				    "true" : "false");
	String progressStringString = (progressString != null ?
				       progressString : "");
	String paintStringString = (paintString ?
				    "true" : "false");

	return super.paramString() +
	",orientation=" + orientationString +
	",paintBorder=" + paintBorderString +
	",paintString=" + paintStringString +
	",progressString=" + progressStringString;
    }

/////////////////
// Accessibility support
////////////////

    /**
     * Gets the AccessibleContext associated with this JProgressBar. 
     * For progress bars, the AccessibleContext takes the form of an 
     * AccessibleJProgressBar. 
     * A new AccessibleJProgressBar instance is created if necessary.
     *
     * @return an AccessibleJProgressBar that serves as the 
     *         AccessibleContext of this JProgressBar
     * @beaninfo
     *       expert: true
     *  description: The AccessibleContext associated with this ProgressBar.
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJProgressBar();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>JProgressBar</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to progress bar user-interface 
     * elements.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class AccessibleJProgressBar extends AccessibleJComponent
        implements AccessibleValue {

        /**
         * Get the state set of this object.
         *
         * @return an instance of AccessibleState containing the current state 
         * of the object
         * @see AccessibleState
         */
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = super.getAccessibleStateSet();
            if (getModel().getValueIsAdjusting()) {
                states.add(AccessibleState.BUSY);
            }
            if (getOrientation() == VERTICAL) {
                states.add(AccessibleState.VERTICAL);
            } else {
                states.add(AccessibleState.HORIZONTAL);
            }
            return states;
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PROGRESS_BAR;
        }

        /**
         * Get the AccessibleValue associated with this object.  In the
         * implementation of the Java Accessibility API for this class, 
	 * return this object, which is responsible for implementing the
         * AccessibleValue interface on behalf of itself.
	 * 
	 * @return this object
         */
        public AccessibleValue getAccessibleValue() {
            return this;
        }

        /**
         * Get the accessible value of this object.
         *
         * @return The current value of this object.
         */
        public Number getCurrentAccessibleValue() {
            return new Integer(getValue());
        }

        /**
         * Set the value of this object as a Number.
         *
         * @return True if the value was set.
         */
        public boolean setCurrentAccessibleValue(Number n) {
            if (n instanceof Integer) {
                setValue(n.intValue());
                return true;
            } else {
                return false;
            }
        }

        /**
         * Get the minimum accessible value of this object.
         *
         * @return The minimum value of this object.
         */
        public Number getMinimumAccessibleValue() {
            return new Integer(getMinimum());
        }

        /**
         * Get the maximum accessible value of this object.
         *
         * @return The maximum value of this object.
         */
        public Number getMaximumAccessibleValue() {
            return new Integer(getMaximum());
        }

    } // AccessibleJProgressBar
}

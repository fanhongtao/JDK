/*
 * @(#)JSlider.java	1.105 04/05/12
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.accessibility.*;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import java.util.*;
import java.beans.*;


/**
 * A component that lets the user graphically select a value by sliding
 * a knob within a bounded interval. The slider can show both 
 * major tick marks and minor tick marks between them. The number of
 * values between the tick marks is controlled with 
 * <code>setMajorTickSpacing</code> and <code>setMinorTickSpacing</code>. 
 * <p>
 * For further information and examples see
 * <a
 href="http://java.sun.com/docs/books/tutorial/uiswing/components/slider.html">How to Use Sliders</a>,
 * a section in <em>The Java Tutorial.</em>
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @beaninfo
 *      attribute: isContainer false
 *    description: A component that supports selecting a integer value from a range.
 * 
 * @version 1.105 05/12/04
 * @author David Kloba
 */
public class JSlider extends JComponent implements SwingConstants, Accessible {
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "SliderUI";

    private boolean paintTicks = false;
    private boolean paintTrack = true;
    private boolean paintLabels = false;
    private boolean isInverted = false;

    /**
     * The data model that handles the numeric maximum value,
     * minimum value, and current-position value for the slider.
     */
    protected BoundedRangeModel sliderModel;

    /**
     * The number of values between the major tick marks -- the 
     * larger marks that break up the minor tick marks.
     */
    protected int majorTickSpacing;

    /**
     * The number of values between the minor tick marks -- the 
     * smaller marks that occur between the major tick marks.
     * @see #setMinorTickSpacing
     */
    protected int minorTickSpacing;

    /**
     * If true, the knob (and the data value it represents) 
     * resolve to the closest tick mark next to where the user
     * positioned the knob.  The default is false.
     * @see #setSnapToTicks
     */
    protected boolean snapToTicks = false;

    /**
     * If true, the knob (and the data value it represents) 
     * resolve to the closest slider value next to where the user
     * positioned the knob.
     */
    boolean snapToValue = true;

    /**
     * @see #setOrientation
     */
    protected int orientation;


    /**
     */
    private Dictionary labelTable;


    /**
     * The changeListener (no suffix) is the listener we add to the
     * Sliders model.  By default this listener just forwards events
     * to ChangeListeners (if any) added directly to the slider.
     * 
     * @see #addChangeListener
     * @see #createChangeListener
     */
    protected ChangeListener changeListener = createChangeListener();


    /**
     * Only one <code>ChangeEvent</code> is needed per slider instance since the
     * event's only (read-only) state is the source property.  The source
     * of events generated here is always "this". The event is lazily
     * created the first time that an event notification is fired.
     * 
     * @see #fireStateChanged
     */
    protected transient ChangeEvent changeEvent = null;


    private void checkOrientation(int orientation) {
        switch (orientation) {
        case VERTICAL:
        case HORIZONTAL:
            break;
        default:
            throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
        }
    }


    /**
     * Creates a horizontal slider with the range 0 to 100 and
     * an initial value of 50.
     */
    public JSlider() {
        this(HORIZONTAL, 0, 100, 50);
    }


    /**
     * Creates a slider using the specified orientation with the 
     * range 0 to 100 and an initial value of 50.
     */
    public JSlider(int orientation) {
        this(orientation, 0, 100, 50);
    }


    /**
     * Creates a horizontal slider using the specified min and max
     * with an initial value equal to the average of the min plus max.
     */
    public JSlider(int min, int max) {
        this(HORIZONTAL, min, max, (min + max) / 2);
    }


    /**
     * Creates a horizontal slider using the specified min, max and value.
     */
    public JSlider(int min, int max, int value) {
        this(HORIZONTAL, min, max, value);
    }


    /**
     * Creates a slider with the specified orientation and the
     * specified minimum, maximum, and initial values.
     * 
     * @exception IllegalArgumentException if orientation is not one of VERTICAL, HORIZONTAL
     *
     * @see #setOrientation
     * @see #setMinimum
     * @see #setMaximum
     * @see #setValue
     */
    public JSlider(int orientation, int min, int max, int value) 
    {
        checkOrientation(orientation);
        this.orientation = orientation;
        sliderModel = new DefaultBoundedRangeModel(value, 0, min, max);
        sliderModel.addChangeListener(changeListener);
        updateUI();
    }


    /**
     * Creates a horizontal slider using the specified
     * BoundedRangeModel.
     */
    public JSlider(BoundedRangeModel brm) 
    {
        this.orientation = JSlider.HORIZONTAL;
        setModel(brm);
        sliderModel.addChangeListener(changeListener);
        updateUI();
    }


    /**
     * Gets the UI object which implements the L&F for this component.
     *
     * @return the SliderUI object that implements the Slider L&F
     */
    public SliderUI getUI() {
        return(SliderUI)ui;
    }


    /**
     * Sets the UI object which implements the L&F for this component.
     *
     * @param ui the SliderUI L&F object
     * @see UIDefaults#getUI
     * @beaninfo
     *        bound: true
     *       hidden: true
     *    attribute: visualUpdate true
     *  description: The UI object that implements the slider's LookAndFeel. 
     */
    public void setUI(SliderUI ui) {
        super.setUI(ui);
    }


    /**
     * Resets the UI property to a value from the current look and feel.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        updateLabelUIs();
        setUI((SliderUI)UIManager.getUI(this));
    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "SliderUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /**
     * We pass Change events along to the listeners with the 
     * the slider (instead of the model itself) as the event source.
     */
    private class ModelListener implements ChangeListener, Serializable {
        public void stateChanged(ChangeEvent e) {
            fireStateChanged();
        }
    }


    /**
     * Subclasses that want to handle model ChangeEvents differently
     * can override this method to return their own ChangeListener 
     * implementation.  The default ChangeListener just forwards 
     * ChangeEvents to the ChangeListeners added directly to the slider.
     * 
     * @see #fireStateChanged
     */
    protected ChangeListener createChangeListener() {
        return new ModelListener();
    }


    /**
     * Adds a ChangeListener to the slider.
     *
     * @param l the ChangeListener to add
     * @see #fireStateChanged
     * @see #removeChangeListener
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }


    /**
     * Removes a ChangeListener from the slider.
     *
     * @param l the ChangeListener to remove
     * @see #fireStateChanged
     * @see #addChangeListener

     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }


    /**
     * Returns an array of all the <code>ChangeListener</code>s added
     * to this JSlider with addChangeListener().
     *
     * @return all of the <code>ChangeListener</code>s added or an empty
     *         array if no listeners have been added
     * @since 1.4
     */
    public ChangeListener[] getChangeListeners() {
        return (ChangeListener[])listenerList.getListeners(
                ChangeListener.class);
    }


    /**
     * Send a ChangeEvent, whose source is this Slider, to
     * each listener.  This method method is called each time 
     * a ChangeEvent is received from the model.
     * 
     * @see #addChangeListener
     * @see EventListenerList
     */
    protected void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i]==ChangeListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }
        }
    }   


    /**
     * Returns data model that handles the sliders three 
     * fundamental properties: minimum, maximum, value.
     * 
     * @see #setModel
     */
    public BoundedRangeModel getModel() {
        return sliderModel;
    }


    /**
     * Sets the model that handles the sliders three 
     * fundamental properties: minimum, maximum, value.
     * 
     * @see #getModel
     * @beaninfo
     *       bound: true
     * description: The sliders BoundedRangeModel.
     */
    public void setModel(BoundedRangeModel newModel) 
    {
        BoundedRangeModel oldModel = getModel();

        if (oldModel != null) {
            oldModel.removeChangeListener(changeListener);
        }

        sliderModel = newModel;

        if (newModel != null) {
            newModel.addChangeListener(changeListener);

            if (accessibleContext != null) {
                accessibleContext.firePropertyChange(
                                                    AccessibleContext.ACCESSIBLE_VALUE_PROPERTY,
                                                    (oldModel == null 
                                                     ? null : new Integer(oldModel.getValue())),
                                                    (newModel == null 
                                                     ? null : new Integer(newModel.getValue())));
            }
        }

        firePropertyChange("model", oldModel, sliderModel);
    }


    /**
     * Returns the sliders value.
     * @return the models value property
     * @see #setValue
     */
    public int getValue() { 
        return getModel().getValue(); 
    }


    /**
     * Sets the sliders current value.  This method just forwards
     * the value to the model.
     * 
     * @see #getValue
     * @beaninfo
     *   preferred: true
     * description: The sliders current value.
     */
    public void setValue(int n) { 
        BoundedRangeModel m = getModel();
        int oldValue = m.getValue();
        if (oldValue == n) {
            return;
        }
        m.setValue(n);

        if (accessibleContext != null) {
            accessibleContext.firePropertyChange(
                                                AccessibleContext.ACCESSIBLE_VALUE_PROPERTY,
                                                new Integer(oldValue),
                                                new Integer(m.getValue()));
        }
    }


    /**
     * Returns the minimum value supported by the slider. 
     *
     * @return the value of the models minimum property
     * @see #setMinimum
     */
    public int getMinimum() { 
        return getModel().getMinimum(); 
    }


    /**
     * Sets the models minimum property.
     *
     * @see #getMinimum
     * @see BoundedRangeModel#setMinimum
     * @beaninfo
     *       bound: true
     *   preferred: true
     * description: The sliders minimum value.
     */
    public void setMinimum(int minimum) { 
        int oldMin = getModel().getMinimum();
        getModel().setMinimum(minimum); 
        firePropertyChange( "minimum", new Integer( oldMin ), new Integer( minimum ) );
    }


    /**
     * Returns the maximum value supported by the slider.
     *
     * @return the value of the models maximum property
     * @see #setMaximum
     */
    public int getMaximum() { 
        return getModel().getMaximum(); 
    }


    /**
     * Sets the models maximum property.  
     * 
     * @see #getMaximum
     * @see BoundedRangeModel#setMaximum
     * @beaninfo
     *       bound: true
     *   preferred: true
     * description: The sliders maximum value.
     */
    public void setMaximum(int maximum) { 
        int oldMax = getModel().getMaximum();
        getModel().setMaximum(maximum); 
        firePropertyChange( "maximum", new Integer( oldMax ), new Integer( maximum ) );
    }


    /**
     * True if the slider knob is being dragged.
     * 
     * @return the value of the models valueIsAdjusting property
     * @see #setValueIsAdjusting
     */
    public boolean getValueIsAdjusting() { 
        return getModel().getValueIsAdjusting(); 
    }


    /**
     * Sets the models valueIsAdjusting property.  Slider look and
     * feel implementations should set this property to true when 
     * a knob drag begins, and to false when the drag ends.  The
     * slider model will not generate ChangeEvents while
     * valueIsAdjusting is true.
     * 
     * @see #getValueIsAdjusting
     * @see BoundedRangeModel#setValueIsAdjusting
     * @beaninfo
     *      expert: true
     * description: True if the slider knob is being dragged.
     */
    public void setValueIsAdjusting(boolean b) { 
        BoundedRangeModel m = getModel();   
        boolean oldValue = m.getValueIsAdjusting();
        m.setValueIsAdjusting(b);

        if ((oldValue != b) && (accessibleContext != null)) {
            accessibleContext.firePropertyChange(
                                                AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                                                ((oldValue) ? AccessibleState.BUSY : null),
                                                ((b) ? AccessibleState.BUSY : null));
        }
    }


    /**
     * Returns the "extent" -- the range of values "covered" by the knob.
     * @return an int representing the extent
     * @see #setExtent
     * @see BoundedRangeModel#getExtent
     */
    public int getExtent() { 
        return getModel().getExtent(); 
    }


    /**
     * Sets the size of the range "covered" by the knob.  Most look
     * and feel implementations will change the value by this amount
     * if the user clicks on either side of the knob.
     * 
     * @see #getExtent
     * @see BoundedRangeModel#setExtent
     * @beaninfo
     *      expert: true
     * description: Size of the range covered by the knob.
     */
    public void setExtent(int extent) { 
        getModel().setExtent(extent); 
    }


    /**
     * Return this slider's vertical or horizontal orientation.
     * @return VERTICAL or HORIZONTAL
     * @see #setOrientation
     */
    public int getOrientation() { 
        return orientation; 
    }


    /**
     * Set the scrollbars orientation to either VERTICAL or HORIZONTAL.
     * 
     * @exception IllegalArgumentException if orientation is not one of VERTICAL, HORIZONTAL
     * @see #getOrientation
     * @beaninfo
     *    preferred: true
     *        bound: true
     *    attribute: visualUpdate true
     *  description: Set the scrollbars orientation to either VERTICAL or HORIZONTAL.
     *         enum: VERTICAL JSlider.VERTICAL 
     *               HORIZONTAL JSlider.HORIZONTAL
     * 
     */
    public void setOrientation(int orientation) 
    { 
        checkOrientation(orientation);
        int oldValue = this.orientation;
        this.orientation = orientation;
        firePropertyChange("orientation", oldValue, orientation);

        if ((oldValue != orientation) && (accessibleContext != null)) {
            accessibleContext.firePropertyChange(
                                                AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                                                ((oldValue == VERTICAL) 
                                                 ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL),
                                                ((orientation == VERTICAL) 
                                                 ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL));
        }
        if (orientation != oldValue) {
            revalidate();
        }
    }



    /**
     * Returns the dictionary of what labels to draw at which values.
     *
     * @return the <code>Dictionary</code> containing labels and
     *    where to draw them
     */
    public Dictionary getLabelTable() {
/*
        if ( labelTable == null && getMajorTickSpacing() > 0 ) {
            setLabelTable( createStandardLabels( getMajorTickSpacing() ) );
        }
*/
        return labelTable;
    }


    /**
     * Used to specify what label will be drawn at any given value.
     * The key-value pairs are of this format:
     * <code>{ Integer value, java.swing.JComponent label }</code>.
     *
     * @see #createStandardLabels
     * @see #getLabelTable
     * @beaninfo
     *       hidden: true
     *        bound: true
     *    attribute: visualUpdate true
     *  description: Specifies what labels will be drawn for any given value.
     */
    public void setLabelTable( Dictionary labels ) {
        Dictionary oldTable = labelTable;
        labelTable = labels;
        updateLabelUIs();
        firePropertyChange("labelTable", oldTable, labelTable );
        if (labels != oldTable) {
            revalidate();
            repaint();
        }
    }


    /**
     * Resets the UI property to a value from the current look and feel.
     *
     * @see JComponent#updateUI
     */
    protected void updateLabelUIs() {
        if ( getLabelTable() == null ) {
            return;
        }
        Enumeration labels = getLabelTable().keys();
        while ( labels.hasMoreElements() ) {
            Object value = getLabelTable().get( labels.nextElement() );
            if ( value instanceof JComponent ) {
                JComponent component = (JComponent)value;
                component.updateUI();
                component.setSize( component.getPreferredSize()  );
            }
        }
    }


    /**
     * Creates a hashtable that will draw text labels starting at the
     * slider minimum using the increment specified.
     * If you call <code>createStandardLabels( 10 )</code>
     * and the slider minimum is
     * zero, then it will make labels for the values 0, 10, 20, 30, and so on.
     * @see #setLabelTable
     */
    public Hashtable createStandardLabels( int increment ) {
        return createStandardLabels( increment, getMinimum() );
    }


    /**
     * Creates a hashtable that will draw text labels starting at the
     * start point
     * specified using the increment specified. If you call
     * <code>createStandardLabels( 10, 2 )</code>,
     * then it will make labels for the values 2, 12, 22, 32, and so on.
     * @see #setLabelTable
     * @exception IllegalArgumentException if slider label start point
     * 		out of range or if label increment is less than or equal
     *		to zero
     */
    public Hashtable createStandardLabels( int increment, int start ) {
        if ( start > getMaximum() || start < getMinimum() ) {
            throw new IllegalArgumentException( "Slider label start point out of range." );
        }

        if ( increment <= 0 ) {
            throw new IllegalArgumentException( "Label incremement must be > 0" );
        }

        class SmartHashtable extends Hashtable implements PropertyChangeListener {
            int increment = 0;
            int start = 0;
            boolean startAtMin = false;

            class LabelUIResource extends JLabel implements UIResource {
                public LabelUIResource( String text, int alignment ) {
                    super( text, alignment );
                    setName("Slider.label");
                }
            }

            public SmartHashtable( int increment, int start ) {
                super();
                this.increment = increment;
                this.start = start;
                startAtMin = start == getMinimum();
                createLabels();
            }

            public void propertyChange( PropertyChangeEvent e ) {
                if ( e.getPropertyName().equals( "minimum" ) && startAtMin ) {
                    start = getMinimum();
                }

                if ( e.getPropertyName().equals( "minimum" ) ||
                     e.getPropertyName().equals( "maximum" ) ) {

                    Enumeration keys = getLabelTable().keys();
                    Object key = null;
                    Hashtable hashtable = new Hashtable();

                    // Save the labels that were added by the developer
                    while ( keys.hasMoreElements() ) {
                        key = keys.nextElement();
                        Object value = getLabelTable().get( key );
                        if ( !(value instanceof LabelUIResource) ) {
                            hashtable.put( key, value );
                        }
                    }

                    clear();
                    createLabels();

                    // Add the saved labels
                    keys = hashtable.keys();
                    while ( keys.hasMoreElements() ) {
                        key = keys.nextElement();
                        put( key, hashtable.get( key ) );
                    }

                    ((JSlider)e.getSource()).setLabelTable( this );
                }
            }

            void createLabels() {
                for ( int labelIndex = start; labelIndex <= getMaximum(); labelIndex += increment ) {
                    put( new Integer( labelIndex ), new LabelUIResource( ""+labelIndex, JLabel.CENTER ) );
                }
            }
        }

        SmartHashtable table = new SmartHashtable( increment, start );

        if ( getLabelTable() != null && (getLabelTable() instanceof PropertyChangeListener) ) {
            removePropertyChangeListener( (PropertyChangeListener)getLabelTable() );
        }

        addPropertyChangeListener( table );

        return table;
    }


    /**
     * Returns true if the value-range shown for the slider is reversed,
     *
     * @return true if the slider values are reversed from their normal order
     * @see #setInverted
     */
    public boolean getInverted() { 
        return isInverted; 
    }


    /**
     * Specify true to reverse the value-range shown for the slider and false to
     * put the value range in the normal order.  The order depends on the
     * slider's <code>ComponentOrientation</code> property.  Normal (non-inverted)
     * horizontal sliders with a <code>ComponentOrientation</code> value of 
     * <code>LEFT_TO_RIGHT</code> have their maximum on the right.  
     * Normal horizontal sliders with a <code>ComponentOrientation</code> value of
     * <code>RIGHT_TO_LEFT</code> have their maximum on the left.  Normal vertical 
     * sliders have their maximum on the top.  These labels are reversed when the 
     * slider is inverted.
     *
     * @param b  true to reverse the slider values from their normal order
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: If true reverses the slider values from their normal order 
     * 
     */
    public void setInverted( boolean b ) { 
        boolean oldValue = isInverted;
        isInverted = b; 
        firePropertyChange("inverted", oldValue, isInverted);
        if (b != oldValue) {
            repaint();
        }
    }


    /**
     * This method returns the major tick spacing.  The number that is returned
     * represents the distance, measured in values, between each major tick mark.
     * If you have a slider with a range from 0 to 50 and the major tick spacing
     * is set to 10, you will get major ticks next to the following values:
     * 0, 10, 20, 30, 40, 50.
     *
     * @return the number of values between major ticks
     * @see #setMajorTickSpacing
     */
    public int getMajorTickSpacing() { 
        return majorTickSpacing; 
    }


    /**
     * This method sets the major tick spacing.  The number that is passed-in
     * represents the distance, measured in values, between each major tick mark.
     * If you have a slider with a range from 0 to 50 and the major tick spacing
     * is set to 10, you will get major ticks next to the following values:
     * 0, 10, 20, 30, 40, 50.
     *
     * @see #getMajorTickSpacing
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: Sets the number of values between major tick marks.
     * 
     */
    public void setMajorTickSpacing(int n) {
        int oldValue = majorTickSpacing;
        majorTickSpacing = n;
        if ( labelTable == null && getMajorTickSpacing() > 0 && getPaintLabels() ) {
            setLabelTable( createStandardLabels( getMajorTickSpacing() ) );
        }
        firePropertyChange("majorTickSpacing", oldValue, majorTickSpacing);
        if (majorTickSpacing != oldValue && getPaintTicks()) {
            repaint();
        }
    }



    /**
     * This method returns the minor tick spacing.  The number that is returned
     * represents the distance, measured in values, between each minor tick mark.
     * If you have a slider with a range from 0 to 50 and the minor tick spacing
     * is set to 10, you will get minor ticks next to the following values:
     * 0, 10, 20, 30, 40, 50.
     *
     * @return the number of values between minor ticks
     * @see #getMinorTickSpacing
     */
    public int getMinorTickSpacing() { 
        return minorTickSpacing; 
    }


    /**
     * This method sets the minor tick spacing.  The number that is passed-in
     * represents the distance, measured in values, between each minor tick mark.
     * If you have a slider with a range from 0 to 50 and the minor tick spacing
     * is set to 10, you will get minor ticks next to the following values:
     * 0, 10, 20, 30, 40, 50.
     *
     * @see #getMinorTickSpacing
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: Sets the number of values between minor tick marks.
     */
    public void setMinorTickSpacing(int n) { 
        int oldValue = minorTickSpacing;
        minorTickSpacing = n; 
        firePropertyChange("minorTickSpacing", oldValue, minorTickSpacing);
        if (minorTickSpacing != oldValue && getPaintTicks()) {
            repaint();
        }
    }


    /**
     * Returns true if the knob (and the data value it represents) 
     * resolve to the closest tick mark next to where the user
     * positioned the knob.
     *
     * @return true if the value snaps to the nearest tick mark, else false
     * @see #setSnapToTicks
     */
    public boolean getSnapToTicks() { 
        return snapToTicks; 
    }


    /**
     * Returns true if the knob (and the data value it represents) 
     * resolve to the closest slider value next to where the user
     * positioned the knob.
     *
     * @return true if the value snaps to the nearest slider value, else false
     */
    boolean getSnapToValue() { 
        return snapToValue; 
    }


    /**
     * Specifying true makes the knob (and the data value it represents) 
     * resolve to the closest tick mark next to where the user
     * positioned the knob.
     *
     * @param b  true to snap the knob to the nearest tick mark
     * @see #getSnapToTicks
     * @beaninfo
     *       bound: true
     * description: If true snap the knob to the nearest tick mark.
     */
    public void setSnapToTicks(boolean b) { 
        boolean oldValue = snapToTicks;
        snapToTicks = b; 
        firePropertyChange("snapToTicks", oldValue, snapToTicks);
    }


    /**
     * Specifying true makes the knob (and the data value it represents) 
     * resolve to the closest slider value next to where the user
     * positioned the knob. If the snapToTicks property has been set to
     * true, the snap-to-ticks behavior will prevail.
     *
     * @param b  true to snap the knob to the nearest slider value
     * @see #getSnapToValue
     * @see #setSnapToTicks
     * @beaninfo
     *       bound: true
     * description: If true snap the knob to the nearest slider value.
     */
    void setSnapToValue(boolean b) { 
        boolean oldValue = snapToValue;
        snapToValue = b; 
        firePropertyChange("snapToValue", oldValue, snapToValue);
    }


    /**
     * Tells if tick marks are to be painted.
     * @return true if tick marks are painted, else false
     * @see #setPaintTicks
     */
    public boolean getPaintTicks() { 
        return paintTicks; 
    }


    /**
     * Determines whether tick marks are painted on the slider.
     * @see #getPaintTicks
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: If true tick marks are painted on the slider.
     */
    public void setPaintTicks(boolean b) { 
        boolean oldValue = paintTicks;
        paintTicks = b;
        firePropertyChange("paintTicks", oldValue, paintTicks);
        if (paintTicks != oldValue) {
            revalidate();
            repaint();
        }
    }

    /**
     * Tells if the track (area the slider slides in) is to be painted.
     * @return true if track is painted, else false
     * @see #setPaintTrack
     */
    public boolean getPaintTrack() { 
        return paintTrack; 
    }


    /**
     * Determines whether the track is painted on the slider.
     * @see #getPaintTrack
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: If true, the track is painted on the slider.
     */
    public void setPaintTrack(boolean b) { 
        boolean oldValue = paintTrack;
        paintTrack = b;
        firePropertyChange("paintTrack", oldValue, paintTrack);
        if (paintTrack != oldValue) {
            repaint();
        }
    }


    /**
     * Tells if labels are to be painted.
     * @return true if labels are painted, else false
     * @see #setPaintLabels
     */
    public boolean getPaintLabels() { 
        return paintLabels; 
    }


    /**
     * Determines whether labels are painted on the slider.
     * @see #getPaintLabels
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: If true labels are painted on the slider.
     */
    public void setPaintLabels(boolean b) {
        boolean oldValue = paintLabels;
        paintLabels = b;
        if ( labelTable == null && getMajorTickSpacing() > 0 ) {
            setLabelTable( createStandardLabels( getMajorTickSpacing() ) );
        }
        firePropertyChange("paintLabels", oldValue, paintLabels);
        if (paintLabels != oldValue) {
            revalidate();
            repaint();
        }
    }   


    /** 
     * See readObject() and writeObject() in JComponent for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (getUIClassID().equals(uiClassID)) {
            byte count = JComponent.getWriteObjCounter(this);
            JComponent.setWriteObjCounter(this, --count);
            if (count == 0 && ui != null) {
                ui.installUI(this);
            }
        }
    }


    /**
     * Returns a string representation of this JSlider. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JSlider.
     */
    protected String paramString() {
        String paintTicksString = (paintTicks ?
                                   "true" : "false");
        String paintTrackString = (paintTrack ?
                                   "true" : "false");
        String paintLabelsString = (paintLabels ?
                                    "true" : "false");
        String isInvertedString = (isInverted ?
                                   "true" : "false");
        String snapToTicksString = (snapToTicks ?
                                    "true" : "false");
        String snapToValueString = (snapToValue ?
                                    "true" : "false");
        String orientationString = (orientation == HORIZONTAL ?
                                    "HORIZONTAL" : "VERTICAL");

        return super.paramString() +
        ",isInverted=" + isInvertedString +
        ",majorTickSpacing=" + majorTickSpacing +
        ",minorTickSpacing=" + minorTickSpacing +
        ",orientation=" + orientationString +
        ",paintLabels=" + paintLabelsString +
        ",paintTicks=" + paintTicksString +
        ",paintTrack=" + paintTrackString +
        ",snapToTicks=" + snapToTicksString +
        ",snapToValue=" + snapToValueString;
    }


/////////////////
// Accessibility support
////////////////

    /**
     * Gets the AccessibleContext associated with this JSlider. 
     * For sliders, the AccessibleContext takes the form of an 
     * AccessibleJSlider. 
     * A new AccessibleJSlider instance is created if necessary.
     *
     * @return an AccessibleJSlider that serves as the 
     *         AccessibleContext of this JSlider
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJSlider();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>JSlider</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to slider user-interface elements.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running
     * the same version of Swing.  As of 1.4, support for long term storage
     * of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package.
     * Please see {@link java.beans.XMLEncoder}.
     */
    protected class AccessibleJSlider extends AccessibleJComponent
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
            if (getValueIsAdjusting()) {
                states.add(AccessibleState.BUSY);
            }
            if (getOrientation() == VERTICAL) {
                states.add(AccessibleState.VERTICAL);
            }
            else {
                states.add(AccessibleState.HORIZONTAL);
            }
            return states;
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SLIDER;
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
	    // TIGER - 4422535 
            if (n == null) {
		return false;
	    }
	    setValue(n.intValue());
	    return true;
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
	    // TIGER - 4422362
	    BoundedRangeModel model = JSlider.this.getModel();
            return new Integer(model.getMaximum() - model.getExtent());
        }
    } // AccessibleJSlider
}

/*
 * @(#)DefaultHSBChooserPanel.java	1.19 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package javax.swing.colorchooser; 

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.image.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Implements the default HSB Color chooser
 *
 *  @version 1.19 12/03/01
 *  @author Tom Santos
 *  @author Steve Wilson
 *  @author Mark Davidson
 */
class DefaultHSBChooserPanel extends AbstractColorChooserPanel implements ChangeListener {

    private AbstractHSBImage palette;
    private AbstractHSBImage sliderPalette;

    // Cached instances of the various palette and slider images.
    private AbstractHSBImage satbrightImage;
    private AbstractHSBImage hueImage;
    private AbstractHSBImage huebrightImage;
    private AbstractHSBImage satImage;
    private AbstractHSBImage huesatImage;
    private AbstractHSBImage brightImage;

    private JSlider slider;
    private JSpinner hField; 
    private JSpinner sField;
    private JSpinner bField;

    private JTextField redField; 
    private JTextField greenField;
    private JTextField blueField;

    private boolean isAdjusting = false; // Flag which indicates that values are set internally
    private Point paletteSelection = new Point();
    private JLabel paletteLabel;
    private JLabel sliderPaletteLabel;

    private JRadioButton hRadio;
    private JRadioButton sRadio;
    private JRadioButton bRadio;

    private Image paletteImage;
    private Image sliderImage;

    private static final int PALETTE_DIMENSION = 200;
    private static final int MAX_HUE_VALUE = 359;
    private static final int MAX_SATURATION_VALUE = 100;
    private static final int MAX_BRIGHTNESS_VALUE = 100;

    private int sliderType = HUE_SLIDER;

    private static final int HUE_SLIDER = 0;
    private static final int SATURATION_SLIDER = 1;
    private static final int BRIGHTNESS_SLIDER = 2;

    public DefaultHSBChooserPanel() {
        super();
    }

    private void addPaletteListeners() {
        paletteLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e ) {
                float[] hsb = new float[3];
                palette.getHSBForLocation( e.getX(), e.getY(), hsb );
                updateHSB( hsb[0], hsb[1], hsb[2] );
            }
        });

        paletteLabel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged( MouseEvent e ){
                int labelWidth = paletteLabel.getWidth();

                int labelHeight = paletteLabel.getHeight();
                int x = e.getX();
                int y = e.getY();

                if ( x >= labelWidth ) {
                    x = labelWidth - 1;
                }

                if ( y >= labelHeight ) {
                    y = labelHeight - 1;
                }

                if ( x < 0 ) {
                    x = 0;
                }

                if ( y < 0 ) {
                    y = 0;
                }
                
                float[] hsb = new float[3];
                palette.getHSBForLocation( x, y, hsb );
                updateHSB( hsb[0], hsb[1], hsb[2] );
            }
        });
    }

    private void updatePalette( float h, float s, float b ) {
        int x = 0;
        int y = 0;

        switch ( sliderType ) {
        case HUE_SLIDER:
            if ( h != palette.getHue() ) {
                palette.setHue( h );
                palette.nextFrame( 0 );
            }
            x = PALETTE_DIMENSION - (int)(s * PALETTE_DIMENSION);
            y = PALETTE_DIMENSION - (int)(b * PALETTE_DIMENSION);
            break;
        case SATURATION_SLIDER:
            if ( s != palette.getSaturation() ) {
                palette.setSaturation( s );
                palette.nextFrame( 0 );
            }
            x = (int)(h * PALETTE_DIMENSION);
            y = PALETTE_DIMENSION - (int)(b * PALETTE_DIMENSION);
            break;
        case BRIGHTNESS_SLIDER:
            if ( b != palette.getBrightness() ) {
                palette.setBrightness( b );
                palette.nextFrame( 0 );
            }
            x = (int)(h * PALETTE_DIMENSION);
            y = PALETTE_DIMENSION - (int)(s * PALETTE_DIMENSION);
            break;
        }

        paletteSelection.setLocation( x, y );
        paletteLabel.repaint(); 
    }

    private void updateSlider( float h, float s, float b ) {
        // Update the slider palette if necessary.
        // When the slider is the hue slider or the hue hasn't changed,
        // the hue of the palette will not need to be updated.
        if (sliderType != HUE_SLIDER && h != sliderPalette.getHue() ) {
            sliderPalette.setHue( h );
            sliderPalette.nextFrame( 0 );
        }
        
        float value = 0f;

        switch ( sliderType ) {
        case HUE_SLIDER:
            value = h;
            break;
        case SATURATION_SLIDER:
            value = s;
            break;
        case BRIGHTNESS_SLIDER:
            value = b;
            break;
        }

        slider.setValue( Math.round(value * (slider.getMaximum())) );
    }

    private void updateHSBTextFields( float hue, float saturation, float brightness ) {
        int h =  Math.round(hue * 359); 
        int s =  Math.round(saturation * 100); 
        int b =  Math.round(brightness * 100); 

        if (((Integer)hField.getValue()).intValue() != h) {
            hField.setValue(new Integer(h));
        }
        if (((Integer)sField.getValue()).intValue() != s) {
            sField.setValue(new Integer(s));
        }
        if (((Integer)bField.getValue()).intValue() != b) {
            bField.setValue(new Integer(b));
        }
    }

    /** 
     * Updates the values of the RGB fields to reflect the new color change
     */
    private void updateRGBTextFields( Color color ) {
        redField.setText(String.valueOf(color.getRed()));
        greenField.setText(String.valueOf(color.getGreen()));
        blueField.setText(String.valueOf(color.getBlue()));
    }

    /** 
     * Main internal method of updating the ui controls and the color model.
     */
    private void updateHSB( float h, float s, float b ) {
        if ( !isAdjusting ) {
            isAdjusting = true;

            updatePalette( h, s, b );
            updateSlider( h, s, b );
            updateHSBTextFields( h, s, b );

            Color color = Color.getHSBColor(h, s, b);
            updateRGBTextFields( color );

            getColorSelectionModel().setSelectedColor( color );

            isAdjusting = false;
        }
    }
    
    /**
      * Invoked automatically when the model's state changes.
      * It is also called by <code>installChooserPanel</code> to allow
      * you to set up the initial state of your chooser.
      * Override this method to update your <code>ChooserPanel</code>.
      */
    public void updateChooser() {
        if ( !isAdjusting ) {
            float[] hsb = getHSBColorFromModel();
            updateHSB( hsb[0], hsb[1], hsb[2] ); 
        }
    }
    
    /**
     * Invoked when the panel is removed from the chooser.
     */
    public void uninstallChooserPanel(JColorChooser enclosingChooser) {
    	super.uninstallChooserPanel(enclosingChooser);
    }
    
    /** 
     * Returns an float array containing the HSB values of the selected color from
     * the ColorSelectionModel
     */
    private float[] getHSBColorFromModel()  {
        Color color = getColorFromModel();
        float[] hsb = new float[3];
        Color.RGBtoHSB( color.getRed(), color.getGreen(), color.getBlue(), hsb );
        
        return hsb;
    }

    /**
     * Builds a new chooser panel.
     */
    protected void buildChooser() {
        setLayout(new BorderLayout());
        JComponent spp = buildSliderPalettePanel();
        add(spp, BorderLayout.BEFORE_LINE_BEGINS);

        JPanel controlHolder = new JPanel(new SmartGridLayout(1,3));
        JComponent hsbControls = buildHSBControls();
        controlHolder.add(hsbControls);

        controlHolder.add(new JLabel(" ")); // spacer

        JComponent rgbControls = buildRGBControls();
        controlHolder.add(rgbControls);

        controlHolder.setBorder(new EmptyBorder( 10, 5, 10, 5));
        add( controlHolder, BorderLayout.CENTER);
    }

    /** 
     * Creates the panel with the uneditable RGB field
     */
    private JComponent buildRGBControls() {
        JPanel panel = new JPanel(new SmartGridLayout(2,3));

        Color color = getColorFromModel();
        redField = new JTextField( String.valueOf(color.getRed()), 3 );
        redField.setEditable(false);
        redField.setHorizontalAlignment( JTextField.RIGHT );

        greenField = new JTextField(String.valueOf(color.getGreen()), 3 );
        greenField.setEditable(false);
        greenField.setHorizontalAlignment( JTextField.RIGHT );

        blueField = new JTextField( String.valueOf(color.getBlue()), 3 );
        blueField.setEditable(false);
        blueField.setHorizontalAlignment( JTextField.RIGHT );

        String redString = UIManager.getString("ColorChooser.hsbRedText");
        String greenString = UIManager.getString("ColorChooser.hsbGreenText");
        String blueString = UIManager.getString("ColorChooser.hsbBlueText");

        panel.add( new JLabel(redString) );
        panel.add( redField );
        panel.add( new JLabel(greenString) );
        panel.add( greenField );
        panel.add( new JLabel(blueString) );
        panel.add( blueField );
        
        return panel;                  
    }

    /** 
     * Creates the panel with the editable HSB fields and the radio buttons.
     */
    private JComponent buildHSBControls() {

        String hueString = UIManager.getString("ColorChooser.hsbHueText");
        String saturationString = UIManager.getString("ColorChooser.hsbSaturationText");
        String brightnessString = UIManager.getString("ColorChooser.hsbBrightnessText");

        RadioButtonHandler handler = new RadioButtonHandler();

        hRadio = new JRadioButton(hueString);
        hRadio.addActionListener(handler);
        hRadio.setSelected(true);

        sRadio = new JRadioButton(saturationString);
        sRadio.addActionListener(handler);

        bRadio = new JRadioButton(brightnessString);
        bRadio.addActionListener(handler);

        ButtonGroup group = new ButtonGroup();
        group.add(hRadio);
        group.add(sRadio);
        group.add(bRadio);

        float[] hsb = getHSBColorFromModel();
        
        hField = new JSpinner(new SpinnerNumberModel((int)(hsb[0] * 359), 0, 359, 1));
        sField = new JSpinner(new SpinnerNumberModel((int)(hsb[1] * 100), 0, 100, 1));
        bField = new JSpinner(new SpinnerNumberModel((int)(hsb[2] * 100), 0, 100, 1));

        hField.addChangeListener(this);
        sField.addChangeListener(this);
        bField.addChangeListener(this);

        JPanel panel = new JPanel( new SmartGridLayout(2, 3) );
        
        panel.add(hRadio);
        panel.add(hField);
        panel.add(sRadio);
        panel.add(sField);
        panel.add(bRadio);
        panel.add(bField);

        return panel;
    }
    
    /** 
     * Handler for the radio button classes.
     */
    private class RadioButtonHandler implements ActionListener  {
        public void actionPerformed(ActionEvent evt)  {
            Object obj = evt.getSource();
        
            if (obj instanceof JRadioButton)  {
                JRadioButton button = (JRadioButton)obj;
                if (button == hRadio)
                    setHueMode();
            
                if (button == sRadio)
                    setSaturationMode();

                if (button == bRadio)
                    setBrightnessMode();
                
                updateChooser();
                repaint();
            }
        }
    }


    // Callback methods for radio button selection.
    
    private void setHueMode() {
        
        float[] hsb = getHSBColorFromModel();

        if (satbrightImage == null && hueImage == null)  {
            satbrightImage = new SaturationBrightnessImage(PALETTE_DIMENSION, PALETTE_DIMENSION, hsb[0]);
            hueImage = new HueImage( 16, PALETTE_DIMENSION);
        } else {
            satbrightImage.setHue(hsb[0]);
        }
        
        setMode(HUE_SLIDER, MAX_HUE_VALUE, true, satbrightImage, hueImage);
    }

    private void setSaturationMode() {
        float[] hsb = getHSBColorFromModel();
        
        if (huebrightImage == null && satImage == null)  {
            huebrightImage = new HueBrightnessImage( PALETTE_DIMENSION, PALETTE_DIMENSION, hsb[0], hsb[1]);
            satImage = new SaturationImage(16, PALETTE_DIMENSION, hsb[0]);
        } else {
        	huebrightImage.setHue(hsb[0]);
        	huebrightImage.setBrightness(hsb[1]);
            satImage.setHue(hsb[0]);
        }

        setMode(SATURATION_SLIDER, MAX_SATURATION_VALUE, false, huebrightImage, satImage);
    }
    
    private void setBrightnessMode() {
        float[] hsb = getHSBColorFromModel();
        
        if (huesatImage == null && brightImage == null)  {
        	huesatImage = new HueSaturationImage(PALETTE_DIMENSION, PALETTE_DIMENSION, hsb[0], hsb[2]);
            brightImage = new BrightnessImage( 16, PALETTE_DIMENSION, hsb[0]);
        } else {
        	huesatImage.setHue(hsb[0]);
            huesatImage.setBrightness(hsb[2]);
            brightImage.setHue(hsb[0]);
        }
        
        setMode(BRIGHTNESS_SLIDER, MAX_BRIGHTNESS_VALUE, false, huesatImage, brightImage);
    }
    
    /** 
     * Reconfigures the palette and slider to reflect the current mode.
     */
    private void setMode(int type, int maximum, boolean inverted, 
                            AbstractHSBImage pImage, AbstractHSBImage sImage)  {
        isAdjusting = true; // Ensure no events propagate from changing slider value.

        sliderType = type;
        slider.setInverted(inverted);
        slider.setMaximum(maximum);
         
        // Set the palette square.
        palette = pImage;
        if (paletteImage != null)  {
            paletteImage.flush();
        }
        paletteImage = Toolkit.getDefaultToolkit().createImage( palette );
        paletteLabel.setIcon( new ImageIcon( paletteImage ) );
            
        // Set the slider image.
        sliderPalette = sImage;
        if (sliderImage != null)  {
            sliderImage.flush();
        }
        sliderImage = Toolkit.getDefaultToolkit().createImage( sliderPalette );
        sliderPaletteLabel.setIcon( new ImageIcon( sliderImage ) );

        isAdjusting = false;
        
    }
    
    protected JComponent buildSliderPalettePanel() {

        // This slider has to have a minimum of 0.  A lot of math in this file is simplified due to this. 
        slider = new JSlider(JSlider.VERTICAL, 0, MAX_HUE_VALUE, 0);
        slider.setPaintTrack(false);
        slider.setPreferredSize(new Dimension(slider.getPreferredSize().width, PALETTE_DIMENSION + 15));
        slider.addChangeListener(this);

        paletteLabel = createPaletteLabel();
        addPaletteListeners();
        sliderPaletteLabel = new JLabel();
        
        JPanel panel = new JPanel();
        panel.add( paletteLabel );
        panel.add( slider );
        panel.add( sliderPaletteLabel );

        setHueMode();

        return panel;
    }

    protected JLabel createPaletteLabel() {
        return new JLabel() {
            protected void paintComponent( Graphics g ) {
                super.paintComponent( g );
                g.setColor( Color.white );
                g.drawOval( paletteSelection.x - 4, paletteSelection.y - 4, 8, 8 );
            }
        };
    }

    public String getDisplayName() {
        return UIManager.getString("ColorChooser.hsbNameText");
    }

    /**
     * Provides a hint to the look and feel as to the
     * <code>KeyEvent.VK</code> constant that can be used as a mnemonic to
     * access the panel. A return value <= 0 indicates there is no mnemonic.
     * <p>
     * The return value here is a hint, it is ultimately up to the look
     * and feel to honor the return value in some meaningful way.
     * <p>
     * This implementation looks up the value from the default
     * <code>ColorChooser.hsbMnemonic</code>, or if it 
     * isn't available (or not an <code>Integer</code>) returns -1.
     * The lookup for the default is done through the <code>UIManager</code>:
     * <code>UIManager.get("ColorChooser.rgbMnemonic");</code>.
     *
     * @return KeyEvent.VK constant identifying the mnemonic; <= 0 for no
     *         mnemonic
     * @see #getDisplayedMnemonicIndex
     * @since 1.4
     */
    public int getMnemonic() {
        return getInt("ColorChooser.hsbMnemonic", -1);
    }

    /**
     * Provides a hint to the look and feel as to the index of the character in
     * <code>getDisplayName</code> that should be visually identified as the
     * mnemonic. The look and feel should only use this if
     * <code>getMnemonic</code> returns a value > 0.
     * <p>
     * The return value here is a hint, it is ultimately up to the look
     * and feel to honor the return value in some meaningful way. For example,
     * a look and feel may wish to render each
     * <code>AbstractColorChooserPanel</code> in a <code>JTabbedPane</code>,
     * and further use this return value to underline a character in
     * the <code>getDisplayName</code>.
     * <p>
     * This implementation looks up the value from the default
     * <code>ColorChooser.rgbDisplayedMnemonicIndex</code>, or if it 
     * isn't available (or not an <code>Integer</code>) returns -1.
     * The lookup for the default is done through the <code>UIManager</code>:
     * <code>UIManager.get("ColorChooser.hsbDisplayedMnemonicIndex");</code>.
     *
     * @return Character index to render mnemonic for; -1 to provide no
     *                   visual identifier for this panel.
     * @see #getMnemonic
     * @since 1.4
     */
    public int getDisplayedMnemonicIndex() {
        return getInt("ColorChooser.hsbDisplayedMnemonicIndex", -1);
    }

    public Icon getSmallDisplayIcon() {
        return null;
    }

    public Icon getLargeDisplayIcon() {
        return null;
    }
    
    /** 
     * Base class for the slider and palette images.
     */
    abstract class AbstractHSBImage extends SyntheticImage {
        protected float h = .0f;
        protected float s = .0f;
        protected float b = .0f;
        protected float[] hsb = new float[3];
        protected boolean isDirty = true;

        protected AbstractHSBImage( int width, int height, float h, float s, float b ) {
            super( width, height );
            setHSB( h, s, b );
	    DefaultHSBChooserPanel.this.
		addHierarchyListener(new ThreadStopper());
        }

        public void setHSB( float h, float s, float b ) {
            setHue( h );
            setSaturation( s );
            setBrightness( b );
        }

        public final void setHue( float hue ) {
            h = hue;
        }

        public final void setSaturation( float saturation ) {
            s = saturation;
        }

        public final void setBrightness( float brightness ) {
            b = brightness;
        }

        public final float getHue() {
            return h;
        }

        public final float getSaturation() {
            return s;
        }

        public final float getBrightness() {
            return b;
        }

        protected boolean isStatic() {
            return false;
        }

        public synchronized void nextFrame( int param ) {
            isDirty = true;
            notifyAll();
        }

	/** 
	 * This method is where the performance bottle neck exists. Perhaps as
         * an optimization, getRGBForLocation can be overridden for all
         * the subclasses. 
	 */
        public int getRGBForLocation( int x, int y ) {
            getHSBForLocation( x, y, hsb );
            return Color.HSBtoRGB( hsb[0], hsb[1], hsb[2] );
        }

        public abstract void getHSBForLocation( int x, int y, float[] hsbArray ); 

        public synchronized void addConsumer(ImageConsumer ic){
            isDirty = true;
            super.addConsumer( ic );
        }
        
        /** 
         * Overriden to unblock the thread.
         */
        public synchronized void removeConsumer(ImageConsumer ic)  {
	    super.removeConsumer(ic);
            isDirty = true;
            notifyAll();
        }
        
        /** 
         * Overriden method from SyntheticImage
         */
        protected void computeRow( int y, int[] row ) {
            if ( y == 0 ) {
                synchronized ( this ) {
                    try {
                        while ( !isDirty ) {
                            wait();
                        }
                    } catch ( Exception e ) {
                    	System.out.println( e );
                    }
                    isDirty = false;
                }
            }
            for ( int i = 0; i < row.length; ++i ) {
                row[i] = getRGBForLocation( i, y );
            }  
        }

	class ThreadStopper implements HierarchyListener { 
	    public void hierarchyChanged(HierarchyEvent ev) {
		if ((ev.getChangeFlags() | HierarchyEvent.SHOWING_CHANGED) != 0){
		    if (DefaultHSBChooserPanel.this.isShowing()) {
			setIsUseful(true);
			isDirty = true;
			restartProduction();
		    } else {
			setIsUseful(false);
			isDirty = true;
			synchronized (AbstractHSBImage.this) {
			    AbstractHSBImage.this.notifyAll();
			}
		    }
		}
	    }
	}
    }
    

    // Square for H
    class SaturationBrightnessImage extends AbstractHSBImage {
        public SaturationBrightnessImage( int width, int height, float hue ) {
            super( width, height, hue, 1.0f, 1.0f );
        }

        public void getHSBForLocation( int x, int y, float[] hsbArray ) {
            float saturationStep = ((float)x) / width;
            float brightnessStep = ((float)y) / height;
            hsbArray[0] = h;
            hsbArray[1] = s - saturationStep; 
            hsbArray[2] = b - brightnessStep;
        }
    }

    // Square for S
    class HueBrightnessImage extends AbstractHSBImage {
        public HueBrightnessImage( int width, int height, float h, float s ) {
            super( width, height, h, s, 1.0f );
        }

        public void getHSBForLocation( int x, int y, float[] hsbArray ) {
            float brightnessStep = ((float)y) / height;
            float step = 1.0f / ((float)width);
            hsbArray[0] = x * step;
            hsbArray[1] = s;
            hsbArray[2] = 1.0f - brightnessStep;
        }
    }

    // Square for B
    class HueSaturationImage extends AbstractHSBImage {
        public HueSaturationImage( int width, int height, float h, float b ) {
            super( width, height, h, 1.0f, b );
        }

        public void getHSBForLocation( int x, int y, float[] hsbArray ) {
            float saturationStep = ((float)y) / height;
            float step = 1.0f / ((float)width);
            hsbArray[0] = x * step;
            hsbArray[1] = 1.0f - saturationStep;
            hsbArray[2] = b;
        }
    }

    // Slider for B
    class BrightnessImage extends AbstractHSBImage {
        protected int cachedY = -1;
        protected int cachedColor = 0;

        public BrightnessImage( int width, int height, float hue ) {
            super( width, height, hue, 1.0f, 1.0f );
        }

        public int getRGBForLocation( int x, int y ) {
            if ( y == cachedY ) {
            } else {
                cachedY = y;
                cachedColor = super.getRGBForLocation( x, y );
            }
            return cachedColor;
        }

        public void getHSBForLocation( int x, int y, float[] hsbArray ) {
            float brightnessStep = ((float)y) / height;
            hsbArray[0] = h;
            hsbArray[1] = s;
            hsbArray[2] = b - brightnessStep;
        }
    }

    // Slider for S
    class SaturationImage extends AbstractHSBImage {
        protected int cachedY = -1;
        protected int cachedColor = 0;

        public SaturationImage( int width, int height, float hue ) {
            super( width, height, hue, 1.0f, 1.0f );
        }

        public int getRGBForLocation( int x, int y ) {
            if ( y == cachedY ) {
            } else {
                cachedY = y;
                cachedColor = super.getRGBForLocation( x, y );
            }

            return cachedColor;
        }

        public void getHSBForLocation( int x, int y, float[] hsbArray ) {
            float saturationStep = ((float)y) / height;
            hsbArray[0] = h;
            hsbArray[1] = s - saturationStep;
            hsbArray[2] = b;
        }
    }

    // Slider for H
    class HueImage extends AbstractHSBImage {
        public HueImage( int width, int height ) {
            super( width, height, 0f, 1.0f, 1.0f );
        }

        protected boolean isStatic() {
            return true;
        }

        public void getHSBForLocation( int x, int y, float[] hsbArray ) {
            float step = 1.0f / ((float)height);
            hsbArray[0] = y * step;
            hsbArray[1] = s;
            hsbArray[2] = b;
        }
    }
    
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == slider) {
            boolean modelIsAdjusting = slider.getModel().getValueIsAdjusting();

            if (!modelIsAdjusting && !isAdjusting) {
                int sliderValue = slider.getValue();
                int sliderRange = slider.getMaximum();
                float value = (float)sliderValue / (float)sliderRange;

                float[] hsb = getHSBColorFromModel();

                switch ( sliderType ){
                    case HUE_SLIDER:
                        updateHSB(value, hsb[1], hsb[2]);
                        break;
                    case SATURATION_SLIDER:
                        updateHSB(hsb[0], value, hsb[2]);
                        break;
                    case BRIGHTNESS_SLIDER:
                        updateHSB(hsb[0], hsb[1], value);
                        break;
                }
            }
        } else if (e.getSource() instanceof JSpinner) {
            float hue = ((Integer)hField.getValue()).floatValue() / 359f;
            float saturation = ((Integer)sField.getValue()).floatValue() / 100f;
            float brightness = ((Integer)bField.getValue()).floatValue() / 100f;

            updateHSB(hue, saturation, brightness);
        }
    }

}    

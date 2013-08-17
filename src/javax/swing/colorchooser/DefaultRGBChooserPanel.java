/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.colorchooser;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;


/**
 * The standard RGB chooser.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.24 02/06/02
 * @author Steve Wilson
 * @see JColorChooser
 * @see AbstractColorChooserPanel
 */
class DefaultRGBChooserPanel extends AbstractColorChooserPanel implements ChangeListener {

  protected JSlider redSlider;
  protected JSlider greenSlider;
  protected JSlider blueSlider;
  protected JIntegerTextField redField;
  protected JIntegerTextField blueField;
  protected JIntegerTextField greenField;

  private final int minValue = 0;
  private final int maxValue = 255;

  boolean isAdjusting = false;

    public DefaultRGBChooserPanel() {
        super();
    }


    public void setColor( Color newColor ) {
	redSlider.setValue( newColor.getRed() );
	greenSlider.setValue( newColor.getGreen() );
	blueSlider.setValue( newColor.getBlue() );
    }

    public String getDisplayName() {
        return UIManager.getString("ColorChooser.rgbNameText");
    }

    public Icon getSmallDisplayIcon() {
        return null;
    }

    public Icon getLargeDisplayIcon() {
        return null;
    }
       
    /**
     * The background color, foreground color, and font are already set to the
     * defaults from the defaults table before this method is called.
     */									
    public void installChooserPanel(JColorChooser enclosingChooser) {
        super.installChooserPanel(enclosingChooser);
    }

    protected void buildChooser() {
      
        String redString = UIManager.getString("ColorChooser.rgbRedText");
        String greenString = UIManager.getString("ColorChooser.rgbGreenText");
        String blueString = UIManager.getString("ColorChooser.rgbBlueText");

	setLayout( new BorderLayout() );
	Color color = getColorFromModel();


	JPanel enclosure = new JPanel();
	enclosure.setLayout( new SmartGridLayout( 3, 3 ) );



	// The panel that holds the sliders

	add( enclosure, BorderLayout.CENTER );
	//	sliderPanel.setBorder(new LineBorder(Color.black));

	DocumentListener numberChange = new NumberListener();

	// The row for the red value
	JLabel l = new JLabel(redString);
	l.setDisplayedMnemonic(UIManager.getInt("ColorChooser.rgbRedMnemonic"));
	enclosure.add(l);
	redSlider = new JSlider( JSlider.HORIZONTAL, 0, 255, color.getRed() );
	redSlider.setMajorTickSpacing( 85 );
	redSlider.setMinorTickSpacing( 17 );
	redSlider.setPaintTicks( true );
	redSlider.setPaintLabels( true );
	enclosure.add( redSlider );
	redField = new JIntegerTextField(minValue, maxValue, color.getRed());
	l.setLabelFor(redSlider);
	JPanel redFieldHolder = new JPanel(new CenterLayout());
	redField.getDocument().addDocumentListener(numberChange);
	redFieldHolder.add(redField);
	enclosure.add(redFieldHolder);


	// The row for the green value
	l = new JLabel(greenString);
	l.setDisplayedMnemonic(UIManager.getInt("ColorChooser.rgbGreenMnemonic"));
	enclosure.add(l);
	greenSlider = new JSlider( JSlider.HORIZONTAL, 0, 255, color.getRed() );
	greenSlider.setMajorTickSpacing( 85 );
	greenSlider.setMinorTickSpacing( 17 );
	greenSlider.setPaintTicks( true );
	greenSlider.setPaintLabels( true );
	enclosure.add(greenSlider);
	greenField = new JIntegerTextField(minValue, maxValue, color.getGreen());
	l.setLabelFor(greenSlider);
	JPanel greenFieldHolder = new JPanel(new CenterLayout());
	greenFieldHolder.add(greenField);
	greenField.getDocument().addDocumentListener(numberChange);
	enclosure.add(greenFieldHolder);

	// The slider for the blue value
	l = new JLabel(blueString);
	l.setDisplayedMnemonic(UIManager.getInt("ColorChooser.rgbBlueMnemonic"));
	enclosure.add(l);
	blueSlider = new JSlider( JSlider.HORIZONTAL, 0, 255, color.getRed() );
	blueSlider.setMajorTickSpacing( 85 );
	blueSlider.setMinorTickSpacing( 17 );
	blueSlider.setPaintTicks( true );
	blueSlider.setPaintLabels( true );
	enclosure.add(blueSlider);
	blueField = new JIntegerTextField(minValue, maxValue, color.getBlue());
	l.setLabelFor(blueSlider);
	JPanel blueFieldHolder = new JPanel(new CenterLayout());
	blueFieldHolder.add(blueField);
	blueField.getDocument().addDocumentListener(numberChange);
	enclosure.add(blueFieldHolder);

	redSlider.addChangeListener( this );
	greenSlider.addChangeListener( this );
	blueSlider.addChangeListener( this );  
	
	redSlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);
	greenSlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);
	blueSlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);
    }

    public void uninstallChooserPanel(JColorChooser enclosingChooser) {
        super.uninstallChooserPanel(enclosingChooser);
    }

    public void updateChooser() {
        if (isAdjusting) {
	    return;
	}
	//	System.out.println("Adjusting");
        isAdjusting = true;

	Color color = getColorFromModel();
	int red = color.getRed();
	int blue = color.getBlue();
	int green = color.getGreen();


	redSlider.setValue(red);
	blueSlider.setValue(blue);
	greenSlider.setValue(green);

	if ( redField.getIntegerValue() != red )
	    redField.setText(String.valueOf(color.getRed()));
	if ( greenField.getIntegerValue() != green )
	    greenField.setText(String.valueOf(color.getGreen()));
	if ( blueField.getIntegerValue() != blue )
	    blueField.setText(String.valueOf(color.getBlue()));


	isAdjusting = false;
	//	System.out.println("Calling update");
    }

    public void stateChanged( ChangeEvent e ) {
	if ( e.getSource() instanceof JSlider ) {

	    int red = redSlider.getValue();
	    int green = greenSlider.getValue();
	    int blue = blueSlider.getValue() ;
  
	    Color color = new Color (red, green, blue);
	    
	    getColorSelectionModel().setSelectedColor(color);
	    //	System.out.println("setting model");

 	}
    }

    class NumberListener implements DocumentListener, Serializable {
        public void insertUpdate(DocumentEvent e) { updatePanel(e); }
        public void removeUpdate(DocumentEvent e)  { updatePanel(e); }
        public void changedUpdate(DocumentEvent e) { }

        private void updatePanel(DocumentEvent e) {
	    int red = redField.getIntegerValue();
	    int green = greenField.getIntegerValue();
	    int blue = blueField.getIntegerValue();
	    Color color = new Color (red, green, blue);

	    getColorSelectionModel().setSelectedColor(color);	    
	}
    }
}



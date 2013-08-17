/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;


/**
 * Provides the basic look and feel for a JColorChooser.
 * <p>
 * @version 1.26 02/06/02
 * @author Tom Santos
 * @author Steve Wilson
 */

public class BasicColorChooserUI extends ColorChooserUI
{
    JColorChooser chooser;

    JTabbedPane tabbedPane;
    JPanel singlePanel;

    JPanel previewPanelHolder;
    JComponent previewPanel;
    boolean isMultiPanel = false;

    protected AbstractColorChooserPanel[] defaultChoosers;

    protected ChangeListener previewListener;
    protected PropertyChangeListener propertyChangeListener;

    public static ComponentUI createUI(JComponent c) {
	return new BasicColorChooserUI();
    }

    protected AbstractColorChooserPanel[] createDefaultChoosers() {
        AbstractColorChooserPanel[] panels = ColorChooserComponentFactory.getDefaultChooserPanels();
	return panels;
    }

    protected void uninstallDefaultChoosers() {
        for( int i = 0 ; i < defaultChoosers.length; i++) {
       	    chooser.removeChooserPanel( defaultChoosers[i] );
	}
    }

    public void installUI( JComponent c ) {
        chooser = (JColorChooser)c;

        super.installUI( c );

	installDefaults();
	installListeners();

	tabbedPane = new JTabbedPane();
	singlePanel = new JPanel(new CenterLayout());

	chooser.setLayout( new BorderLayout() );

	defaultChoosers = createDefaultChoosers();
	chooser.setChooserPanels(defaultChoosers);


	installPreviewPanel();

    }

    public void uninstallUI( JComponent c ) {
	uninstallListeners();
        uninstallDefaultChoosers();
	uninstallDefaults();

	defaultChoosers = null;
	chooser = null;
	tabbedPane = null;


    }

    protected void installPreviewPanel() {
	previewPanelHolder = new JPanel(new CenterLayout());
	String previewString = UIManager.getString("ColorChooser.previewText");
	previewPanelHolder.setBorder(new TitledBorder(previewString));

	previewPanel = chooser.getPreviewPanel();
	if (previewPanel == null || previewPanel instanceof UIResource) { 
	  previewPanel = ColorChooserComponentFactory.getPreviewPanel(); // get from table?
	}
	previewPanel.setForeground(chooser.getColor());
	previewPanelHolder.add(previewPanel);
	chooser.add(previewPanelHolder, BorderLayout.SOUTH);
    }

    protected void installDefaults() {
        LookAndFeel.installColorsAndFont(chooser, "ColorChooser.background", 
                                              "ColorChooser.foreground",
                                              "ColorChooser.font");
    }

    protected void uninstallDefaults() {
    }

    
    protected void installListeners() {
        propertyChangeListener = createPropertyChangeListener();
	chooser.addPropertyChangeListener( propertyChangeListener );

	previewListener = new PreviewListener();
	chooser.getSelectionModel().addChangeListener(previewListener);
    }

    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyHandler();
    }
  
    protected void uninstallListeners() {
	chooser.removePropertyChangeListener( propertyChangeListener );
	chooser.getSelectionModel().removeChangeListener(previewListener);
    }


    class PreviewListener implements ChangeListener {
        public void stateChanged( ChangeEvent e ) {
	    ColorSelectionModel model = (ColorSelectionModel)e.getSource();
	    if (previewPanel != null) {
	        previewPanel.setForeground(model.getSelectedColor());
		previewPanel.repaint();
	    }
	}
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    public class PropertyHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent e) {

	    if ( e.getPropertyName().equals( JColorChooser.CHOOSER_PANELS_PROPERTY ) ) {
	        AbstractColorChooserPanel[] oldPanels = (AbstractColorChooserPanel[]) e.getOldValue();
	        AbstractColorChooserPanel[] newPanels = (AbstractColorChooserPanel[]) e.getNewValue();

		for (int i = 0; i < oldPanels.length; i++) {  // remove old panels		  
		   Container wrapper = oldPanels[i].getParent();
		    if (wrapper != null) {
		      Container parent = wrapper.getParent();
		      if (parent != null)
			  parent.remove(wrapper);  // remove from hierarchy
		      oldPanels[i].uninstallChooserPanel(chooser); // uninstall
		    }
		}

		int numNewPanels = newPanels.length;
		if (numNewPanels == 0) {  // removed all panels and added none
		    chooser.remove(tabbedPane);
		    return;
		} 
		else if (numNewPanels == 1) {  // one panel case
		    chooser.remove(tabbedPane);
		    JPanel centerWrapper = new JPanel( new CenterLayout() );
		    centerWrapper.add(newPanels[0]);
		    singlePanel.add(centerWrapper, BorderLayout.CENTER);
		    chooser.add(singlePanel);
		}
		else {   // multi-panel case
		    if ( oldPanels.length < 2 ) {// moving from single to multiple
		        chooser.remove(singlePanel);
			tabbedPane = new JTabbedPane();
			chooser.add(tabbedPane, BorderLayout.CENTER);
		    }

		    for (int i = 0; i < newPanels.length; i++) {	    
		        JPanel centerWrapper = new JPanel( new CenterLayout() );
			centerWrapper.add(newPanels[i]);
			tabbedPane.addTab(newPanels[i].getDisplayName(), centerWrapper);
		    }
		    
		  

		}

		for (int i = 0; i < newPanels.length; i++) {
		    newPanels[i].installChooserPanel(chooser);
		}

		

	 
	    }

	    if ( e.getPropertyName().equals( JColorChooser.PREVIEW_PANEL_PROPERTY ) ) {
	        JComponent oldPanel = (JComponent) e.getOldValue();
		JComponent newPanel = (JComponent) e.getNewValue();
		if (oldPanel != null) {  // fix for 4166059
		    chooser.remove(oldPanel);
		}
		chooser.add(newPanel, BorderLayout.SOUTH);
	    }
	}
    }

    
}




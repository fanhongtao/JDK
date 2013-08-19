/*
 * @(#)SynthColorChooserUI.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;


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
 * @version 1.7, 01/23/03 (based on BasicColorChooserUI v 1.38)
 * @author Tom Santos
 * @author Steve Wilson
 */
class SynthColorChooserUI extends ColorChooserUI implements SynthUI {
    private static final LayoutManager PANEL_LAYOUT = new CenterLayout();

    private SynthStyle style;

    JColorChooser chooser;

    JTabbedPane tabbedPane;
    JPanel singlePanel;

    JPanel previewPanelHolder;
    JComponent previewPanel;
    MouseListener previewMouseListener;
    boolean isMultiPanel = false;
    private static TransferHandler defaultTransferHandler = new ColorTransferHandler();

    protected AbstractColorChooserPanel[] defaultChoosers;

    protected ChangeListener previewListener;
    protected PropertyChangeListener propertyChangeListener;

    public static ComponentUI createUI(JComponent c) {
	return new SynthColorChooserUI();
    }

    protected AbstractColorChooserPanel[] createDefaultChoosers(
                           SynthContext context) {
        AbstractColorChooserPanel[] panels = (AbstractColorChooserPanel[])
                     context.getStyle().get(context, "ColorChooser.panels");

        if (panels == null) {
            panels = ColorChooserComponentFactory.getDefaultChooserPanels();
        }
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
        tabbedPane.setName("ColorChooser.tabPane");
	singlePanel = new JPanel(new CenterLayout());
        singlePanel.setName("ColorChooser.panel");

        chooser.setLayout(PANEL_LAYOUT);

        SynthContext context = getContext(c, ENABLED);
	defaultChoosers = createDefaultChoosers(context);
        context.dispose();

	chooser.setChooserPanels(defaultChoosers);

	previewPanelHolder = new JPanel(PANEL_LAYOUT);

        previewPanelHolder.setName("ColorChooser.previewPanelHolder");
	chooser.add(previewPanelHolder, BorderLayout.SOUTH);

	previewMouseListener = new MouseAdapter() {
	    public void mousePressed(MouseEvent e) {
		if (chooser.getDragEnabled()) {
		    TransferHandler th = chooser.getTransferHandler();
		    th.exportAsDrag(chooser, e, TransferHandler.COPY);
		}
	    }
	};

	installPreviewPanel();
	chooser.applyComponentOrientation(c.getComponentOrientation());
    }

    public void uninstallUI( JComponent c ) {
	chooser.remove(tabbedPane);
	chooser.remove(singlePanel);
	chooser.remove(previewPanelHolder);

	uninstallListeners();
        uninstallDefaultChoosers();
	uninstallDefaults();

	previewPanelHolder.remove(previewPanel);
	if (previewPanel instanceof UIResource) {
	    chooser.setPreviewPanel(null);
	}

	previewPanelHolder = null;
	previewPanel = null;
	defaultChoosers = null;
	chooser = null;
	tabbedPane = null;
    }

    protected void installPreviewPanel() {
	if (previewPanel != null) {
	    previewPanelHolder.remove(previewPanel);
	    previewPanel.removeMouseListener(previewMouseListener);
	}

	previewPanel = chooser.getPreviewPanel();
	if ((previewPanel != null) && (previewPanelHolder != null) && (chooser != null) && (previewPanel.getSize().getHeight()+previewPanel.getSize().getWidth() == 0)) {
	  chooser.remove(previewPanelHolder);
	  return;
	}
	if (previewPanel == null || previewPanel instanceof UIResource) { 
	  previewPanel = ColorChooserComponentFactory.getPreviewPanel(); // get from table?
	    chooser.setPreviewPanel(previewPanel);
	}
	previewPanel.setForeground(chooser.getColor());
	previewPanelHolder.add(previewPanel);
	previewPanel.addMouseListener(previewMouseListener);
    }

    protected void installDefaults() {
        fetchStyle(chooser);
	TransferHandler th = chooser.getTransferHandler();
	if (th == null || th instanceof UIResource) {
	    chooser.setTransferHandler(defaultTransferHandler);
	}
    }

    private void fetchStyle(JComponent c) {
        SynthContext context = getContext(c, ENABLED);
        style = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }

    protected void uninstallDefaults() {
        SynthContext context = getContext(chooser, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
	if (chooser.getTransferHandler() instanceof UIResource) {
	    chooser.setTransferHandler(null);
	}
        style = null;
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
	previewPanel.removeMouseListener(previewMouseListener);
    }


    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                    SynthLookAndFeel.getRegion(c), style, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }

    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SynthContext context, Graphics g) {
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
    class PropertyHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent e) {

            if (SynthLookAndFeel.shouldUpdateStyle(e)) {
                fetchStyle((JColorChooser)e.getSource());
            }
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
			chooser.add(tabbedPane, BorderLayout.CENTER);
		    }

		    for (int i = 0; i < newPanels.length; i++) {	    
		        JPanel centerWrapper = new JPanel( new CenterLayout() );
                        String name = newPanels[i].getDisplayName();
                        int mnemonic = newPanels[i].getMnemonic();
			centerWrapper.add(newPanels[i]);
			tabbedPane.addTab(name, centerWrapper);
                        if (mnemonic > 0) {
                            tabbedPane.setMnemonicAt(i, mnemonic);
                            tabbedPane.setDisplayedMnemonicIndexAt(
                                  i, newPanels[i].getDisplayedMnemonicIndex());
                        }
		    }
		    
		  

		}

		chooser.applyComponentOrientation(chooser.getComponentOrientation());
		for (int i = 0; i < newPanels.length; i++) {
		    newPanels[i].installChooserPanel(chooser);
		}

		

	 
	    }

	    if ( e.getPropertyName().equals( JColorChooser.PREVIEW_PANEL_PROPERTY ) ) {
		if (e.getNewValue() != previewPanel) {
		    installPreviewPanel();
		}
            }
	    if (e.getPropertyName().equals("componentOrientation")) {
		ComponentOrientation o = (ComponentOrientation)e.getNewValue();
		JColorChooser cc = (JColorChooser)e.getSource();
		if (o != (ComponentOrientation)e.getOldValue()) {
		    cc.applyComponentOrientation(o);
		    cc.updateUI();
		}
	    }
        }
    }

    static class ColorTransferHandler extends TransferHandler implements UIResource {

	ColorTransferHandler() {
	    super("color");
	}
    }
    
    static class CenterLayout implements LayoutManager, Serializable {
        public void addLayoutComponent(String name, Component comp) { }
        public void removeLayoutComponent(Component comp) { }

        public Dimension preferredLayoutSize( Container container ) {
            Component c = container.getComponent(0);
            if ( c != null ) {
                Dimension size = c.getPreferredSize();
                Insets insets = container.getInsets();

                return new Dimension(size.width + insets.left + insets.right,
                                     size.height + insets.top + insets.bottom);
            }
            else {
                return new Dimension(0, 0);
            }
        }

        public Dimension minimumLayoutSize(Container cont) {
            return preferredLayoutSize(cont);
        }

        public void layoutContainer(Container container) {
            if (container.getComponentCount() > 0) {
                Component c = container.getComponent(0);
                Dimension pref = c.getPreferredSize();
                int containerWidth = container.getWidth();
                int containerHeight = container.getHeight();
                Insets containerInsets = container.getInsets();

                containerWidth -= containerInsets.left +
                                       containerInsets.right;
                containerHeight -= containerInsets.top +
                                        containerInsets.bottom;

                int left = (containerWidth - pref.width) / 2 +
                            containerInsets.left;
                int right = (containerHeight - pref.height) / 2 +
                            containerInsets.top;

                c.setBounds(left, right, pref.width, pref.height);
            }
        }
    }
}

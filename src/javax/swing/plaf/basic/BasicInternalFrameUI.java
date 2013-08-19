/*
 * @(#)BasicInternalFrameUI.java	1.110 03/02/19
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import java.awt.*;
import java.awt.event.*;
import java.awt.peer.LightweightPeer;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.event.*;

import java.beans.*;
import java.io.Serializable;


/**
 * A basic L&F implementation of JInternalFrame.  
 *
 * @version 1.110 02/19/03
 * @author David Kloba
 * @author Rich Schiavi
 */
public class BasicInternalFrameUI extends InternalFrameUI 
{

    protected JInternalFrame frame;

    protected MouseInputAdapter          borderListener;
    protected PropertyChangeListener     propertyChangeListener;
    protected LayoutManager              internalFrameLayout;
    protected ComponentListener          componentListener;
    protected MouseInputListener         glassPaneDispatcher;
 
    protected JComponent northPane;
    protected JComponent southPane;
    protected JComponent westPane;
    protected JComponent eastPane;

    protected BasicInternalFrameTitlePane titlePane; // access needs this

    private static DesktopManager sharedDesktopManager;
    private boolean componentListenerAdded = false;

    private Rectangle parentBounds;

    private boolean dragging = false;

    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of Java 2 platform v1.3.
     */
    protected KeyStroke openMenuKey;

    private boolean keyBindingRegistered = false;
    private boolean keyBindingActive = false;

    private InternalFrameListener internalFrameListener = null;
    
/////////////////////////////////////////////////////////////////////////////
// ComponentUI Interface Implementation methods
/////////////////////////////////////////////////////////////////////////////
    public static ComponentUI createUI(JComponent b)    {
        return new BasicInternalFrameUI((JInternalFrame)b);
    }

    public BasicInternalFrameUI(JInternalFrame b)   {
    }
            
    public void installUI(JComponent c)   {

        frame = (JInternalFrame)c;        

	installDefaults();
	installListeners();
	installComponents();
	installKeyboardActions();

	frame.setOpaque(true);
    }

    public void uninstallUI(JComponent c) {
        if(c != frame)
            throw new IllegalComponentStateException(
                this + " was asked to deinstall() " 
                + c + " when it only knows about " 
                + frame + "."); 
                
	uninstallKeyboardActions();
	uninstallComponents();
	uninstallListeners();
	uninstallDefaults();
	frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	

        frame = null;
    }
    
    protected void installDefaults(){
	Icon frameIcon = frame.getFrameIcon();
        if (frameIcon == null || frameIcon instanceof UIResource) {
            frame.setFrameIcon(UIManager.getIcon("InternalFrame.icon"));
        }

	/* enable the content pane to inherit background color from its
	   parent by setting its background color to null. Fixes bug#
	   4268949. */
	JComponent contentPane = (JComponent) frame.getContentPane();
	if (contentPane != null) {
          Color bg = contentPane.getBackground();
	  if (bg instanceof UIResource)
	    contentPane.setBackground(null);
	}
        frame.setLayout(internalFrameLayout = createLayoutManager());
	frame.setBackground(UIManager.getLookAndFeelDefaults().getColor("control"));

	LookAndFeel.installBorder(frame, "InternalFrame.border");
	
    }
    protected void installKeyboardActions(){
      if (internalFrameListener == null)
	createInternalFrameListener();
      frame.addInternalFrameListener(internalFrameListener);

      ActionMap actionMap = getActionMap();
      SwingUtilities.replaceUIActionMap(frame, actionMap);
    }

    ActionMap getActionMap() {
      ActionMap map = (ActionMap)UIManager.get("InternalFrame.actionMap");
      if (map == null) {
        map = createActionMap();
	  if (map != null) {
	    UIManager.getLookAndFeelDefaults().put("InternalFrame.actionMap",
						   map);
	  }
      }
      return map;
    }

    ActionMap createActionMap() {
      ActionMap map = new ActionMapUIResource();
      // add action for the system menu
      map.put("showSystemMenu", new AbstractAction(){
	      public void actionPerformed(ActionEvent e){
		  titlePane.showSystemMenu();
	      }
	      public boolean isEnabled(){
		  return isKeyBindingActive();
	      }
	  });
      // Set the ActionMap's parent to the Auditory Feedback Action Map
      BasicLookAndFeel lf = (BasicLookAndFeel)UIManager.getLookAndFeel();
      ActionMap audioMap = lf.getAudioActionMap();
      map.setParent(audioMap);
      return map;
    }

    protected void installComponents(){
	setNorthPane(createNorthPane(frame));
	setSouthPane(createSouthPane(frame));
	setEastPane(createEastPane(frame));
	setWestPane(createWestPane(frame));
    }

    /*
     * @since 1.3
     */
    protected void installListeners() {
        borderListener = createBorderListener(frame);
	propertyChangeListener = createPropertyChangeListener();
	frame.addPropertyChangeListener(propertyChangeListener);
        installMouseHandlers(frame);
	glassPaneDispatcher = createGlassPaneDispatcher();
	frame.getGlassPane().addMouseListener(glassPaneDispatcher);
       	frame.getGlassPane().addMouseMotionListener(glassPaneDispatcher);
	componentListener =  createComponentListener();
        if (frame.getParent() != null) {
	  parentBounds = frame.getParent().getBounds();
	}
	if ((frame.getParent() != null) && !componentListenerAdded) {
	  frame.getParent().addComponentListener(componentListener);
	  componentListenerAdded = true;
	}
    }

    InputMap getInputMap(int condition) {
	if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
	    return createInputMap(condition);
	}
	return null;
    }

    InputMap createInputMap(int condition) {
	if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
	    Object[] bindings = (Object[])UIManager.get
		                          ("InternalFrame.windowBindings");

	    if (bindings != null) {
		return LookAndFeel.makeComponentInputMap(frame, bindings);
	    }
	}
	return null;
    }
    
    protected void uninstallDefaults() {
	Icon frameIcon = frame.getFrameIcon();
        if (frameIcon instanceof UIResource) {
            frame.setFrameIcon(null);
        }
        internalFrameLayout = null;
        frame.setLayout(null);
	LookAndFeel.uninstallBorder(frame);
    }

    protected void uninstallComponents(){
	setNorthPane(null);
	setSouthPane(null);
	setEastPane(null);
	setWestPane(null);
        titlePane = null;
    }

    /*
     * @since 1.3
     */
    protected void uninstallListeners() {
      if ((frame.getParent() != null) && componentListenerAdded) {
	frame.getParent().removeComponentListener(componentListener);
	componentListenerAdded = false;
      }
      componentListener = null;
      frame.getGlassPane().removeMouseListener(glassPaneDispatcher);
      frame.getGlassPane().removeMouseMotionListener(glassPaneDispatcher);
      glassPaneDispatcher = null;
      deinstallMouseHandlers(frame);      
      frame.removePropertyChangeListener(propertyChangeListener);
      propertyChangeListener = null;
      borderListener = null;
    }

    protected void uninstallKeyboardActions(){
        if (internalFrameListener != null) {
	    frame.removeInternalFrameListener(internalFrameListener);
        }
	SwingUtilities.replaceUIInputMap(frame, JComponent.
					 WHEN_IN_FOCUSED_WINDOW, null);
	SwingUtilities.replaceUIActionMap(frame, null);

    }

    protected LayoutManager createLayoutManager(){
      return new InternalFrameLayout();
    }

    protected PropertyChangeListener createPropertyChangeListener(){
      return new InternalFramePropertyChangeListener();
    }



    public Dimension getPreferredSize(JComponent x)    {
	if((JComponent)frame == x)
	    return frame.getLayout().preferredLayoutSize(x);
        return new Dimension(100, 100);
    }
    
    public Dimension getMinimumSize(JComponent x)  {
	if((JComponent)frame == x) {
	    return frame.getLayout().minimumLayoutSize(x);
        }
        return new Dimension(0, 0);
    }
    
    public Dimension getMaximumSize(JComponent x) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    

			
    /** 
      * Installs necessary mouse handlers on <code>newPane</code>
      * and adds it to the frame.
      * Reverse process for the <code>currentPane</code>. 
      */
    protected void replacePane(JComponent currentPane, JComponent newPane) {
        if(currentPane != null) {
            deinstallMouseHandlers(currentPane);
            frame.remove(currentPane);
        }
        if(newPane != null) {
           frame.add(newPane);
           installMouseHandlers(newPane);
        }
    }

    protected void deinstallMouseHandlers(JComponent c) {
      c.removeMouseListener(borderListener);
      c.removeMouseMotionListener(borderListener);
    }
  
    protected void installMouseHandlers(JComponent c) {
      c.addMouseListener(borderListener);
      c.addMouseMotionListener(borderListener);
    }
    
    protected JComponent createNorthPane(JInternalFrame w) {
      titlePane = new BasicInternalFrameTitlePane(w);
      return titlePane;
    }

    
    protected JComponent createSouthPane(JInternalFrame w) {
	return null;
    }

    protected JComponent createWestPane(JInternalFrame w) {
        return null;
    }

    protected JComponent createEastPane(JInternalFrame w) {
        return null;
    }


    protected MouseInputAdapter createBorderListener(JInternalFrame w) {
        return new BorderListener();
    }

    private InternalFrameListener getInternalFrameListener(){
      return internalFrameListener;
    }
    
    protected void createInternalFrameListener(){
      internalFrameListener = new BasicInternalFrameListener();
    }

    protected final boolean isKeyBindingRegistered(){
      return keyBindingRegistered;
    }

    protected final void setKeyBindingRegistered(boolean b){
      keyBindingRegistered = b;
    }

    public final boolean isKeyBindingActive(){
      return keyBindingActive;
    }

    protected final void setKeyBindingActive(boolean b){
      keyBindingActive = b;
    }


    protected void setupMenuOpenKey(){
	// PENDING(hania): Why are these WHEN_IN_FOCUSED_WINDOWs? Shouldn't
	// they be WHEN_ANCESTOR_OF_FOCUSED_COMPONENT?
	// Also, no longer registering on the desktopicon, the previous
	// action did nothing.
	InputMap map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	SwingUtilities.replaceUIInputMap(frame,
				      JComponent.WHEN_IN_FOCUSED_WINDOW, map);
	//ActionMap actionMap = getActionMap();
	//SwingUtilities.replaceUIActionMap(frame, actionMap);
    }

    protected void setupMenuCloseKey(){
    }      

    public JComponent getNorthPane() {
        return northPane;
    }

    public void setNorthPane(JComponent c) {
        if (northPane != null &&
                northPane instanceof BasicInternalFrameTitlePane) {
            ((BasicInternalFrameTitlePane)northPane).uninstallListeners();
        }
        replacePane(northPane, c);
        northPane = c;
    }

    public JComponent getSouthPane() {
        return southPane;
    }

    public void setSouthPane(JComponent c) {
        southPane = c;
    }

    public JComponent getWestPane() {
        return westPane;
    }

    public void setWestPane(JComponent c) {
        westPane = c;
    }

    public JComponent getEastPane() {
        return eastPane;
    }

    public void setEastPane(JComponent c) {
        eastPane = c;
    }

    public class InternalFramePropertyChangeListener implements
        PropertyChangeListener {
        /**
         * Detects changes in state from the JInternalFrame and handles
         * actions.
         */
        public void propertyChange(PropertyChangeEvent evt) {
            String prop = (String)evt.getPropertyName();
            JInternalFrame f = (JInternalFrame)evt.getSource();
            Object newValue = evt.getNewValue();
            Object oldValue = evt.getOldValue();

            if (JInternalFrame.IS_CLOSED_PROPERTY.equals(prop)) {
                if (newValue == Boolean.TRUE) {
                    if ((frame.getParent() != null) && componentListenerAdded) {
                        frame.getParent().removeComponentListener(
                                componentListener);
                    }
                    closeFrame(f);
                }
            } else if (JInternalFrame.IS_MAXIMUM_PROPERTY.equals(prop)) {
                if(newValue == Boolean.TRUE) {
                    maximizeFrame(f);
                } else {
                    minimizeFrame(f);
                }
            } else if(JInternalFrame.IS_ICON_PROPERTY.equals(prop)) {
                if (newValue == Boolean.TRUE) {
                    iconifyFrame(f);
                } else {
                    deiconifyFrame(f);
                }
            } else if (JInternalFrame.IS_SELECTED_PROPERTY.equals(prop)) {
                Component glassPane = f.getGlassPane();
                if (newValue == Boolean.TRUE && oldValue == Boolean.FALSE) {
                    activateFrame(f);
                    glassPane.setVisible(false);
                } else if (newValue == Boolean.FALSE &&
                           oldValue == Boolean.TRUE) {
                    deactivateFrame(f);
                    glassPane.setVisible(true);
                }
            } else if (prop.equals("ancestor")) {
                if (frame.getParent() != null) {
                    parentBounds = f.getParent().getBounds();
                } else {
                    parentBounds = null;
                }
                if ((frame.getParent() != null) && !componentListenerAdded) {
                    f.getParent().addComponentListener(componentListener);
                    componentListenerAdded = true;
                } else if ((newValue == null) && componentListenerAdded) {
                    if (f.getParent() != null) {
                        f.getParent().removeComponentListener(
                                componentListener);
                    }
                    componentListenerAdded = false;
                }
            } else if (JInternalFrame.TITLE_PROPERTY.equals(prop) ||
                    prop.equals("closable") || prop.equals("iconable") ||
                    prop.equals("maximizable")) {
                Dimension dim = frame.getMinimumSize();
                Dimension frame_dim = frame.getSize();
                if (dim.width > frame_dim.width) {
                    frame.setSize(dim.width, frame_dim.height);
                }
            }
        }
    }

  public class InternalFrameLayout implements LayoutManager {
    public void addLayoutComponent(String name, Component c) {}
    public void removeLayoutComponent(Component c) {}    
    public Dimension preferredLayoutSize(Container c)  {
	Dimension result;
	Insets i = frame.getInsets();
	
	result = new Dimension(frame.getRootPane().getPreferredSize());
	result.width += i.left + i.right;
	result.height += i.top + i.bottom;

        if(getNorthPane() != null) {
	    Dimension d = getNorthPane().getPreferredSize();
	    result.width = Math.max(d.width, result.width);
            result.height += d.height;
	}

        if(getSouthPane() != null) {
	    Dimension d = getSouthPane().getPreferredSize();
	    result.width = Math.max(d.width, result.width);
            result.height += d.height;
	}

        if(getEastPane() != null) {
	    Dimension d = getEastPane().getPreferredSize();
            result.width += d.width;
	    result.height = Math.max(d.height, result.height);
	}

        if(getWestPane() != null) {
	    Dimension d = getWestPane().getPreferredSize();
            result.width += d.width;
	    result.height = Math.max(d.height, result.height);
	}

	return result;
    }
    
    public Dimension minimumLayoutSize(Container c) {
        // The minimum size of the internal frame only takes into account the
        // title pane since you are allowed to resize the frames to the point
        // where just the title pane is visible.
        Dimension result = new Dimension();
        if (getNorthPane() != null &&
            getNorthPane() instanceof BasicInternalFrameTitlePane) {
              result = new Dimension(getNorthPane().getMinimumSize());
        }
	Insets i = frame.getInsets();
        result.width += i.left + i.right;
	result.height += i.top + i.bottom;
	
	return result;
    }
    
    public void layoutContainer(Container c) {
        Insets i = frame.getInsets();
        int cx, cy, cw, ch;
        
        cx = i.left;
        cy = i.top;
        cw = frame.getWidth() - i.left - i.right;
        ch = frame.getHeight() - i.top - i.bottom;
        
        if(getNorthPane() != null) {
            Dimension size = getNorthPane().getPreferredSize();
            getNorthPane().setBounds(cx, cy, cw, size.height);
            cy += size.height;
            ch -= size.height;
        }

        if(getSouthPane() != null) {
            Dimension size = getSouthPane().getPreferredSize();
            getSouthPane().setBounds(cx, frame.getHeight() 
                                                - i.bottom - size.height, 
                                                cw, size.height);
            ch -= size.height;
        }

        if(getWestPane() != null) {
            Dimension size = getWestPane().getPreferredSize();
            getWestPane().setBounds(cx, cy, size.width, ch);
            cw -= size.width;
            cx += size.width;           
        }

        if(getEastPane() != null) {
            Dimension size = getEastPane().getPreferredSize();
            getEastPane().setBounds(cw - size.width, cy, size.width, ch);
            cw -= size.width;           
        }
        
        if(frame.getRootPane() != null) {
            frame.getRootPane().setBounds(cx, cy, cw, ch);
        }
    }
  }

/// DesktopManager methods
    /** Returns the proper DesktopManager. Calls getDesktopPane() to 
      * find the JDesktop component and returns the desktopManager from
      * it. If this fails, it will return a default DesktopManager that 
      * should work in arbitrary parents.
      */
    protected DesktopManager getDesktopManager() {
	if(frame.getDesktopPane() != null 
	   && frame.getDesktopPane().getDesktopManager() != null)
	    return frame.getDesktopPane().getDesktopManager();
	if(sharedDesktopManager == null)
	  sharedDesktopManager = createDesktopManager();
	return sharedDesktopManager;	
    }
  
    protected DesktopManager createDesktopManager(){
      return new DefaultDesktopManager();
    }
    
    /**
     * This method is called when the user wants to close the frame.
     * The <code>playCloseSound</code> Action is fired.
     * This action is delegated to the desktopManager.
     */
    protected void closeFrame(JInternalFrame f) {
	// Internal Frame Auditory Cue Activation
	fireAudioAction("InternalFrame.closeSound");
	// delegate to desktop manager
	getDesktopManager().closeFrame(f);
    }

    /**
     * This method is called when the user wants to maximize the frame.
     * The <code>playMaximizeSound</code> Action is fired.
     * This action is delegated to the desktopManager.
     */
    protected void maximizeFrame(JInternalFrame f) {
	// Internal Frame Auditory Cue Activation
	fireAudioAction("InternalFrame.maximizeSound");
	// delegate to desktop manager
	getDesktopManager().maximizeFrame(f);
    }

    /**
     * This method is called when the user wants to minimize the frame.
     * The <code>playRestoreDownSound</code> Action is fired.
     * This action is delegated to the desktopManager.
     */
    protected void minimizeFrame(JInternalFrame f) {
	// Internal Frame Auditory Cue Activation
	if ( ! f.isIcon() ) {
	    // This method seems to regularly get called after an
	    // internal frame is iconified. Don't play this sound then.
	    fireAudioAction("InternalFrame.restoreDownSound");
	}
	// delegate to desktop manager
	getDesktopManager().minimizeFrame(f);
    }

    /**
     * This method is called when the user wants to iconify the frame.
     * The <code>playMinimizeSound</code> Action is fired.
     * This action is delegated to the desktopManager.
     */
    protected void iconifyFrame(JInternalFrame f) {
	// Internal Frame Auditory Cue Activation
	fireAudioAction("InternalFrame.minimizeSound");
	// delegate to desktop manager
	getDesktopManager().iconifyFrame(f);
    }

    /**
     * This method is called when the user wants to deiconify the frame.
     * The <code>playRestoreUpSound</code> Action is fired.
     * This action is delegated to the desktopManager.
     */
    protected void deiconifyFrame(JInternalFrame f) {
	// Internal Frame Auditory Cue Activation
	if ( ! f.isMaximum() ) {
	    // This method seems to regularly get called after an
	    // internal frame is maximized. Don't play this sound then.
	    fireAudioAction("InternalFrame.restoreUpSound");
	}
	// delegate to desktop manager
	getDesktopManager().deiconifyFrame(f);
    }

    /** This method is called when the frame becomes selected.
      * This action is delegated to the desktopManager.
      */
    protected void activateFrame(JInternalFrame f) {
	getDesktopManager().activateFrame(f);
    }
    /** This method is called when the frame is no longer selected.
      * This action is delegated to the desktopManager.
      */
    protected void deactivateFrame(JInternalFrame f) {
	getDesktopManager().deactivateFrame(f);
    }

    /**
     * Convenience method for firing off the auditory cue actions.
     *
     * @since 1.4
     */
    private void fireAudioAction (String actionName) {
	ActionMap map = frame.getActionMap();
	if (map != null) {
	    Action audioAction = map.get(actionName);
	    if (audioAction != null) {
		// pass off firing the Action to a utility method
		BasicLookAndFeel lf = (BasicLookAndFeel)
		                       UIManager.getLookAndFeel();
		lf.playSound(audioAction);
	    }
	}
    }

    /////////////////////////////////////////////////////////////////////////
    /// Border Listener Class
    /////////////////////////////////////////////////////////////////////////        
    /**
     * Listens for border adjustments.
     */
    protected class BorderListener extends MouseInputAdapter implements SwingConstants
    {
	// _x & _y are the mousePressed location in absolute coordinate system
        int _x, _y;
	// __x & __y are the mousePressed location in source view's coordinate system
	int __x, __y;
        Rectangle startingBounds;
        int resizeDir;
        
        
        protected final int RESIZE_NONE  = 0;
        private boolean discardRelease = false;
                
        int resizeCornerSize = 16;
        
	public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() > 1 && e.getSource() == getNorthPane()) {
		if(frame.isIconifiable() && frame.isIcon()) {
                    try { frame.setIcon(false); } catch (PropertyVetoException e2) { }
		} else if(frame.isMaximizable()) {
                    if(!frame.isMaximum())
                        try { frame.setMaximum(true); } catch (PropertyVetoException e2) { }
                    else
                        try { frame.setMaximum(false); } catch (PropertyVetoException e3) { }
		} 
            }
	}

        public void mouseReleased(MouseEvent e) {
           if (discardRelease) {
	     discardRelease = false;
	     return;
	  }
	    if (resizeDir == RESIZE_NONE) {
	        getDesktopManager().endDraggingFrame(frame);	
		dragging = false;
	    } else {
	      Container c = frame.getTopLevelAncestor();
	      if (c instanceof JFrame) {
		((JFrame)frame.getTopLevelAncestor()).getGlassPane().setCursor(
                  Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            
	        ((JFrame)frame.getTopLevelAncestor()).getGlassPane(
                  ).setVisible(false);
	      } else if (c instanceof JApplet) {
		((JApplet)c).getGlassPane().setCursor(
		  Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		((JApplet)c).getGlassPane().setVisible(false);
	      } else if (c instanceof JWindow) {
		((JWindow)c).getGlassPane().setCursor(
                  Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		((JWindow)c).getGlassPane().setVisible(false);
	      } else if (c instanceof JDialog) {
		((JDialog)c).getGlassPane().setCursor(
                  Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		((JDialog)c).getGlassPane().setVisible(false);
	      }
	      getDesktopManager().endResizingFrame(frame);
	    }
            _x = 0;
            _y = 0;
            __x = 0;
            __y = 0;
            startingBounds = null;
            resizeDir = RESIZE_NONE;
        }
                
        public void mousePressed(MouseEvent e) {
            Point p = SwingUtilities.convertPoint((Component)e.getSource(), 
                        e.getX(), e.getY(), null);
            __x = e.getX();
            __y = e.getY();
            _x = p.x;
            _y = p.y;
            startingBounds = frame.getBounds();
	    resizeDir = RESIZE_NONE;

            if(!frame.isSelected()) {
                try { frame.setSelected(true); }
		catch (PropertyVetoException e1) { }
            }

	    Insets i = frame.getInsets();

	    Point ep = new Point(__x, __y);
            if (e.getSource() == getNorthPane()) {
		Point np = getNorthPane().getLocation();
		ep.x += np.x;
		ep.y += np.y;
	    }

            if (e.getSource() == getNorthPane()) {
		if (ep.x > i.left && ep.y > i.top && ep.x < frame.getWidth() - i.right) {
		    getDesktopManager().beginDraggingFrame(frame);
		    dragging = true;
		    return;
		}
            }
	    if (!frame.isResizable()) {
	      return;
	    }

	    if (e.getSource() == frame || e.getSource() == getNorthPane()) {
                if (ep.x <= i.left) {
		    if (ep.y < resizeCornerSize + i.top) {
                        resizeDir = NORTH_WEST;
                    } else if (ep.y > frame.getHeight()
                              - resizeCornerSize - i.bottom) {
                        resizeDir = SOUTH_WEST;
		    } else {  
                        resizeDir = WEST;
}
                } else if (ep.x >= frame.getWidth() - i.right) {
		    if (ep.y < resizeCornerSize + i.top) {
                        resizeDir = NORTH_EAST;
                    } else if (ep.y > frame.getHeight() 
                              - resizeCornerSize - i.bottom) {
                        resizeDir = SOUTH_EAST;
		    } else {               
                        resizeDir = EAST;
		    }
                } else if (ep.y <= i.top) {
		    if (ep.x < resizeCornerSize + i.left) {
                        resizeDir = NORTH_WEST;
                    } else if (ep.x > frame.getWidth() 
                              - resizeCornerSize - i.right) {
                        resizeDir = NORTH_EAST;
                    } else {             
                        resizeDir = NORTH;
		    }
                } else if (ep.y >= frame.getHeight() - i.bottom) {
		    if (ep.x < resizeCornerSize + i.left) {
                        resizeDir = SOUTH_WEST;
                    } else if (ep.x > frame.getWidth()
                              - resizeCornerSize - i.right) {
                        resizeDir = SOUTH_EAST;
                    } else {                
                      resizeDir = SOUTH;
		    }
                } else {
		  /* the mouse press happened inside the frame, not in the
		     border */
		  discardRelease = true;
		  return;
		}
		Cursor s = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
                switch (resizeDir) {
		case SOUTH:
		  s = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
		  break; 
		case NORTH:
		  s = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
		  break; 
		case WEST:
		  s = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
		  break; 
		case EAST:
		  s = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
		  break; 
		case SOUTH_EAST:
		  s = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
		  break; 
		case SOUTH_WEST:
		  s = Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
		  break; 
		case NORTH_WEST:
		  s = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
		  break; 
		case NORTH_EAST:
		  s = Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
		  break;
		} 
		Container c = frame.getTopLevelAncestor();
		if (c instanceof JFrame){
		  ((JFrame)c).getGlassPane().setVisible(true);
		  ((JFrame)c).getGlassPane().setCursor(s);
		} else if (c instanceof JApplet){
		  ((JApplet)c).getGlassPane().setVisible(true);
		  ((JApplet)c).getGlassPane().setCursor(s);
		} else if (c instanceof JWindow){
		  ((JWindow)c).getGlassPane().setVisible(true);
		  ((JWindow)c).getGlassPane().setCursor(s);
		} else if (c instanceof JDialog){
		  ((JDialog)c).getGlassPane().setVisible(true);
		  ((JDialog)c).getGlassPane().setCursor(s);
		}
		getDesktopManager().beginResizingFrame(frame, resizeDir);
		return;
            }
        }

        public void mouseDragged(MouseEvent e) {   

	    if ( startingBounds == null ) {
	      // (STEVE) Yucky work around for bug ID 4106552
		 return;
	    }
                                     
            Point p = SwingUtilities.convertPoint((Component)e.getSource(), 
                    e.getX(), e.getY(), null);
            int deltaX = _x - p.x;
            int deltaY = _y - p.y;
	    Dimension min = frame.getMinimumSize();
	    Dimension max = frame.getMaximumSize();
	    int newX, newY, newW, newH;
	    Insets i = frame.getInsets();
        
            // Handle a MOVE 
            if (dragging) {
                if (frame.isMaximum() || ((e.getModifiers() &
                        InputEvent.BUTTON1_MASK) !=
                        InputEvent.BUTTON1_MASK)) {
                    // don't allow moving of frames if maximixed or left mouse
                    // button was not used.
                    return;
                }
		int pWidth, pHeight;
		Dimension s = frame.getParent().getSize();
		pWidth = s.width;
		pHeight = s.height;


	        newX = startingBounds.x - deltaX;
	        newY = startingBounds.y - deltaY;

		// Make sure we stay in-bounds
		if(newX + i.left <= -__x)
		    newX = -__x - i.left + 1;
		if(newY + i.top <= -__y)
		    newY = -__y - i.top + 1;
		if(newX + __x + i.right >= pWidth)
		    newX = pWidth - __x - i.right - 1;
		if(newY + __y + i.bottom >= pHeight)
		    newY =  pHeight - __y - i.bottom - 1;

		getDesktopManager().dragFrame(frame, newX, newY);
                return;
            }

            if(!frame.isResizable()) {
                return;
            }

	    newX = frame.getX();
	    newY = frame.getY();
	    newW = frame.getWidth();
	    newH = frame.getHeight();

            parentBounds = frame.getParent().getBounds();

            switch(resizeDir) {
            case RESIZE_NONE:
                return;
            case NORTH:      
		if(startingBounds.height + deltaY < min.height)
		    deltaY = -(startingBounds.height - min.height);
		else if(startingBounds.height + deltaY > max.height)
		    deltaY = max.height - startingBounds.height;
		if (startingBounds.y - deltaY < 0) {deltaY = startingBounds.y;}

		newX = startingBounds.x;
		newY = startingBounds.y - deltaY;
		newW = startingBounds.width;
		newH = startingBounds.height + deltaY;
                break;
            case NORTH_EAST:     
		if(startingBounds.height + deltaY < min.height)
		    deltaY = -(startingBounds.height - min.height);
		else if(startingBounds.height + deltaY > max.height)
		    deltaY = max.height - startingBounds.height;
		if (startingBounds.y - deltaY < 0) {deltaY = startingBounds.y;}

		if(startingBounds.width - deltaX < min.width)
		    deltaX = startingBounds.width - min.width;
		else if(startingBounds.width - deltaX > max.width)
		    deltaX = -(max.width - startingBounds.width);
		if (startingBounds.x + startingBounds.width - deltaX >
		    parentBounds.width) {
		  deltaX = startingBounds.x + startingBounds.width -
		    parentBounds.width;
		}

		newX = startingBounds.x;
		newY = startingBounds.y - deltaY;
		newW = startingBounds.width - deltaX;
		newH = startingBounds.height + deltaY;
                break;
            case EAST:      
		if(startingBounds.width - deltaX < min.width)
		    deltaX = startingBounds.width - min.width;
		else if(startingBounds.width - deltaX > max.width)
		    deltaX = -(max.width - startingBounds.width);
		if (startingBounds.x + startingBounds.width - deltaX >
		    parentBounds.width) {
		  deltaX = startingBounds.x + startingBounds.width -
		    parentBounds.width;
		}

		newW = startingBounds.width - deltaX;
		newH = startingBounds.height;
                break;
            case SOUTH_EAST:     
		if(startingBounds.width - deltaX < min.width)
		    deltaX = startingBounds.width - min.width;
		else if(startingBounds.width - deltaX > max.width)
		    deltaX = -(max.width - startingBounds.width);
		if (startingBounds.x + startingBounds.width - deltaX >
		    parentBounds.width) {
		  deltaX = startingBounds.x + startingBounds.width -
		    parentBounds.width;
		}

		if(startingBounds.height - deltaY < min.height)
		    deltaY = startingBounds.height - min.height;
		else if(startingBounds.height - deltaY > max.height)
		    deltaY = -(max.height - startingBounds.height);
		if (startingBounds.y + startingBounds.height - deltaY >
		     parentBounds.height) {
		  deltaY = startingBounds.y + startingBounds.height -
		    parentBounds.height ;
		}
			
		newW = startingBounds.width - deltaX;
		newH = startingBounds.height - deltaY;
                break;
            case SOUTH:      
		if(startingBounds.height - deltaY < min.height)
		    deltaY = startingBounds.height - min.height;
		else if(startingBounds.height - deltaY > max.height)
		    deltaY = -(max.height - startingBounds.height);
		if (startingBounds.y + startingBounds.height - deltaY >
		     parentBounds.height) {
		  deltaY = startingBounds.y + startingBounds.height -
		    parentBounds.height ;
		}

 		newW = startingBounds.width;
		newH = startingBounds.height - deltaY;
                break;
            case SOUTH_WEST:
		if(startingBounds.height - deltaY < min.height)
		    deltaY = startingBounds.height - min.height;
		else if(startingBounds.height - deltaY > max.height)
		    deltaY = -(max.height - startingBounds.height);
		if (startingBounds.y + startingBounds.height - deltaY >
		     parentBounds.height) {
		  deltaY = startingBounds.y + startingBounds.height -
		    parentBounds.height ;
		}

		if(startingBounds.width + deltaX < min.width)
		    deltaX = -(startingBounds.width - min.width);
		else if(startingBounds.width + deltaX > max.width)
		    deltaX = max.width - startingBounds.width;
		if (startingBounds.x - deltaX < 0) {
		  deltaX = startingBounds.x;
		}

		newX = startingBounds.x - deltaX;
		newY = startingBounds.y;
		newW = startingBounds.width + deltaX;
		newH = startingBounds.height - deltaY;
                break;
            case WEST:      
		if(startingBounds.width + deltaX < min.width)
		    deltaX = -(startingBounds.width - min.width);
		else if(startingBounds.width + deltaX > max.width)
		    deltaX = max.width - startingBounds.width;
		if (startingBounds.x - deltaX < 0) {
		  deltaX = startingBounds.x;
		}

		newX = startingBounds.x - deltaX;
		newY = startingBounds.y;
		newW = startingBounds.width + deltaX;
		newH = startingBounds.height;
                break;
            case NORTH_WEST:     
		if(startingBounds.width + deltaX < min.width)
		    deltaX = -(startingBounds.width - min.width);
		else if(startingBounds.width + deltaX > max.width)
		    deltaX = max.width - startingBounds.width;
		if (startingBounds.x - deltaX < 0) {
		  deltaX = startingBounds.x;
		}

		if(startingBounds.height + deltaY < min.height)
		    deltaY = -(startingBounds.height - min.height);
		else if(startingBounds.height + deltaY > max.height)
		    deltaY = max.height - startingBounds.height;
		if (startingBounds.y - deltaY < 0) {deltaY = startingBounds.y;}

		newX = startingBounds.x - deltaX;
		newY = startingBounds.y - deltaY;
		newW = startingBounds.width + deltaX;
		newH = startingBounds.height + deltaY;
                break;
            default:
                return;
            }
	    getDesktopManager().resizeFrame(frame, newX, newY, newW, newH);
	}

        public void mouseMoved(MouseEvent e)    {

	    if(!frame.isResizable())
		return;
		
            if (e.getSource() == frame || e.getSource() == getNorthPane()) {
                Insets i = frame.getInsets();
                Point ep = new Point(e.getX(), e.getY());
                if (e.getSource() == getNorthPane()) {
		    Point np = getNorthPane().getLocation();
		    ep.x += np.x;
		    ep.y += np.y;
	        }
                if(ep.x <= i.left) {
                    if(ep.y < resizeCornerSize + i.top)
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                    else if(ep.y > frame.getHeight() - resizeCornerSize - i.bottom)
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
                    else                
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                } else if(ep.x >= frame.getWidth() - i.right) {
                    if(ep.y < resizeCornerSize + i.top)
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                    else if(ep.y > frame.getHeight() - resizeCornerSize - i.bottom)
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                    else                
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                } else if(ep.y <= i.top) {
                    if(ep.x < resizeCornerSize + i.left)
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                    else if(ep.x > frame.getWidth() - resizeCornerSize - i.right)
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                    else                
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                } else if(ep.y >= frame.getHeight() - i.bottom) {
                    if(ep.x < resizeCornerSize + i.left)
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
                    else if(ep.x > frame.getWidth() - resizeCornerSize - i.right)
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                    else                
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                }
		else
		  frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));		return;
            }

	    frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
	}         

        public void mouseExited(MouseEvent e)    {
	    frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
	}
    };    /// End BorderListener Class

    protected class ComponentHandler implements ComponentListener
    {

      /**
       * Invoked when a JInternalFrame's parent's size changes.
       */
      public void componentResized(ComponentEvent e) {


	//
	// Get the JInternalFrame's parent container size
	//

	Rectangle parentNewBounds = ((Component) e.getSource()).getBounds();

	JInternalFrame.JDesktopIcon icon = null;

	if (frame != null) {
	  icon = frame.getDesktopIcon();
	
	  //
	  // Resize the internal frame if it is maximized and relocate
	  // the associated icon as well.
	  //

	  if ( frame.isMaximum() ) {
	    frame.setBounds(0, 0, parentNewBounds.width, parentNewBounds.height);
	  }
	}

	//
	// Relocate the icon base on the new parent bounds.
	//
	    
	if (icon != null) {
	  Rectangle iconBounds = icon.getBounds();
	  int y = iconBounds.y + (parentNewBounds.height - parentBounds.height);
	  icon.setBounds(iconBounds.x,y,iconBounds.width,iconBounds.height);
	}

	//
	// Update the new parent bounds for next resize.
	//
	
	if ( !parentBounds.equals(parentNewBounds) ) {
	  parentBounds = parentNewBounds;
	}


	  //
	  // Validate the component tree for this container.
	  //
	    
	if (frame != null) frame.validate();
      }

      /* Unused */
      public void componentMoved(ComponentEvent e) {}
      /* Unused */
      public void componentShown(ComponentEvent e) {}
      /* Unused */
      public void componentHidden(ComponentEvent e) {}
    }
    
    protected ComponentListener createComponentListener()
    {
      return new ComponentHandler();
    }


    private static boolean isDragging = false;
    protected class GlassPaneDispatcher implements MouseInputListener {
        private Component mouseEventTarget = null;
        private Component dragSource = null;

      /**
       * When inactive, mouse events are forwarded as appropriate either to 
       * the UI to activate the frame or to the underlying child component.
       */
      public void mousePressed(MouseEvent e) {
	// what is going on here is the GlassPane is up on the inactive
	// internalframe and want's to "catch" the first mousePressed on
	// the frame in order to give it to the BorderLister (and not the
	// underlying component) and let it activate the frame
	if (borderListener != null){
	  borderListener.mousePressed(e);
	} 
       	forwardMouseEvent(e);
      }

        /** 
         * Forward the mouseEntered event to the underlying child container.
         * @see #mousePressed
         */
        public void mouseEntered(MouseEvent e) {
            forwardMouseEvent(e);
        }
      /** 
     * Forward the mouseMoved event to the underlying child container.
     * @see #mousePressed
     */
      public void mouseMoved(MouseEvent e) {
        forwardMouseEvent(e);
      }
      /** 
     * Forward the mouseExited event to the underlying child container.
     * @see #mousePressed
     */
      public void mouseExited(MouseEvent e) {
        forwardMouseEvent(e);
      }
      /** 
     * Ignore mouseClicked events.
     * @see #mousePressed
     */
      public void mouseClicked(MouseEvent e) {
      }
      /** 
     * Forward the mouseReleased event to the underlying child container.
     * @see #mousePressed
     */
        public void mouseReleased(MouseEvent e) {
            forwardMouseEvent(e);
        }

      /** 
     * Forward the mouseDragged event to the underlying child container.
     * @see #mousePressed
     */
      public void mouseDragged(MouseEvent e) {
	forwardMouseEvent(e);
      }

        /**
         * Forward a mouse event to the current mouse target, setting it
         * if necessary.
         */
        private void forwardMouseEvent(MouseEvent e) {
            // We only want to do this for the selected internal frame.
            Component target =
                findComponentAt(frame.getRootPane().getLayeredPane(), 
                    e.getX(), e.getY());

            int id = e.getID();
            switch(id) {
                case MouseEvent.MOUSE_ENTERED:
                    if (isDragging && !frame.isSelected()) {
                        return;
                    }
                    if (target != mouseEventTarget) {
                        mouseEventTarget = target;
                    }
                    retargetMouseEvent(id, e, mouseEventTarget);
                    break;
                case MouseEvent.MOUSE_PRESSED:
                    if (target != mouseEventTarget) {
                        mouseEventTarget = target;
                    }
                    retargetMouseEvent(id, e, mouseEventTarget);
                    // Set the drag source in case we start dragging.
                    dragSource = target;
                    break;
                case MouseEvent.MOUSE_EXITED:
                    if (isDragging && !frame.isSelected()) {
                        return;
                    }
                    retargetMouseEvent(id, e, mouseEventTarget);
                    break;
                case MouseEvent.MOUSE_CLICKED:
                    retargetMouseEvent(id, e, mouseEventTarget);
                    break;
                case MouseEvent.MOUSE_MOVED:
                    if (target != mouseEventTarget) {
                        retargetMouseEvent(MouseEvent.MOUSE_EXITED, e,
                                mouseEventTarget);
                        mouseEventTarget = target;
                        retargetMouseEvent(MouseEvent.MOUSE_ENTERED, e,
                                mouseEventTarget);
                    }
                    retargetMouseEvent(id, e, mouseEventTarget);
                    break;
                case MouseEvent.MOUSE_DRAGGED:
                    if (!isDragging) {
                        isDragging = true;
                    }
                    retargetMouseEvent(id, e, dragSource);
                    break;
                case MouseEvent.MOUSE_RELEASED:
                    if (isDragging) {
                        retargetMouseEvent(id, e, dragSource);
                        isDragging = false;
                    } else {
                        retargetMouseEvent(id, e, mouseEventTarget);
                    }
            }
        }

    /*
     * Find the lightweight child component which corresponds to the
     * specified location.  This is similar to the new 1.2 API in
     * Container, but we need to run on 1.1.  The other changes are
     * due to Container.findComponentAt's use of package-private data.
     */
      private Component findComponentAt(Container c, int x, int y) {
        if (!c.contains(x, y)) {
	  return c;
        }
        int ncomponents = c.getComponentCount();
        Component component[] = c.getComponents();
        for (int i = 0 ; i < ncomponents ; i++) {
	  Component comp = component[i];
	  Point loc = comp.getLocation();
	  if ((comp != null) && (comp.contains(x - loc.x, y - loc.y)) &&
	      (comp.getPeer() instanceof LightweightPeer) &&
	      (comp.isVisible() == true)) {
	    // found a component that intersects the point, see if there
	    // is a deeper possibility.
	    if (comp instanceof Container) {
	      Container child = (Container) comp;
	      Point childLoc = child.getLocation();
	      Component deeper = findComponentAt(child,
						 x - childLoc.x, y - childLoc.y);
	      if (deeper != null) {
		return deeper;
	      }
	    } else {
	      return comp;
	    }
	  }
        }
        return c;
      }

        /* 
         * Dispatch an event clone, retargeted for the specified target.
         */
        private void retargetMouseEvent(int id, MouseEvent e,
                Component target) {
            if (target == null) {
                return;
            }
            // fix for bug #4202966 -- hania
            // When retargetting a mouse event, we need to translate
            // the event's coordinates relative to the target.

            Point p = SwingUtilities.convertPoint(frame.getLayeredPane(),
                                                e.getX(), e.getY(),
                                                target);
            MouseEvent retargeted = new MouseEvent(target, 
                id,
                e.getWhen(),
                e.getModifiers() | e.getModifiersEx(),
                p.x,
                p.y,
                e.getClickCount(),
                e.isPopupTrigger());
            target.dispatchEvent(retargeted);
        }
    }

    protected MouseInputListener createGlassPaneDispatcher(){
	return new GlassPaneDispatcher();
    }

    
    protected class BasicInternalFrameListener implements InternalFrameListener {
      public void internalFrameClosing(InternalFrameEvent e) {
      }

      public void internalFrameClosed(InternalFrameEvent e) {
	frame.removeInternalFrameListener(internalFrameListener);      
      }

      public void internalFrameOpened(InternalFrameEvent e) {
      }

      public void internalFrameIconified(InternalFrameEvent e) {
      }

      public void internalFrameDeiconified(InternalFrameEvent e) {
      }

      public void internalFrameActivated(InternalFrameEvent e) {
	if (!isKeyBindingRegistered()){
	  setKeyBindingRegistered(true);
	  setupMenuOpenKey();
	  setupMenuCloseKey();
	}
	if (isKeyBindingRegistered())
	  setKeyBindingActive(true);
      }


      public void internalFrameDeactivated(InternalFrameEvent e) {
	setKeyBindingActive(false);
      }
    }
    
}   /// End BasicInternalFrameUI Class

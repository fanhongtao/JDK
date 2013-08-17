/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.event.*;

import java.beans.*;
import java.io.Serializable;


/**
 * A basic L&F implementation of JInternalFrame.  
 *
 * @version 1.78 12/02/02
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

    private Rectangle parentBounds = null;

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
	frame.add(frame.getRootPane(), "Center");

	installDefaults();
	installComponents();
	installListeners();
	installKeyboardActions();

	setNorthPane(createNorthPane(frame));
	setSouthPane(createSouthPane(frame));
	setEastPane(createEastPane(frame));
	setWestPane(createWestPane(frame));

	
	frame.setOpaque(true);
        int height = (getNorthPane() != null ?
	  getNorthPane().getMinimumSize().height : 0) +
          frame.getInsets().top + frame.getInsets().bottom;
       frame.setMinimumSize(new Dimension(120, height));
    }   


    public void uninstallUI(JComponent c) {
        if(c != frame)
            throw new IllegalComponentStateException(
                this + " was asked to deinstall() " 
                + c + " when it only knows about " 
                + frame + "."); 
                
	uninstallDefaults();
	uninstallComponents();
	frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	

	setNorthPane(null);
	setSouthPane(null);
	setEastPane(null);
	setWestPane(null);

	uninstallListeners();
	uninstallKeyboardActions();

        frame.remove(frame.getRootPane());	
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

	LookAndFeel.installBorder(frame, "InternalFrame.border");
	
    }
    protected void installKeyboardActions(){
      if (internalFrameListener == null)
	createInternalFrameListener();
      frame.addInternalFrameListener(internalFrameListener);
    }

    protected void installComponents(){}

    protected void installListeners(){
        borderListener = createBorderListener(frame);
	propertyChangeListener = createPropertyChangeListener();
	frame.addPropertyChangeListener(propertyChangeListener);
        frame.setLayout(internalFrameLayout = createLayoutManager());
        installMouseHandlers(frame);
	glassPaneDispatcher = createGlassPaneDispatcher();
	frame.getGlassPane().addMouseListener(glassPaneDispatcher);
       	frame.getGlassPane().addMouseMotionListener(glassPaneDispatcher);
	componentListener =  createComponentListener();
	if ((frame.getParent() != null) && !componentListenerAdded) {
	  frame.getParent().addComponentListener(componentListener);
	  componentListenerAdded = true;
	  parentBounds = frame.getParent().getBounds();
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

    ActionMap getActionMap() {
	return createActionMap();
    }

    ActionMap createActionMap() {
	ActionMap map = new ActionMapUIResource();
	map.put("showSystemMenu", new AbstractAction(){
	    public void actionPerformed(ActionEvent e){
		titlePane.showSystemMenu();
	    }
	    public boolean isEnabled(){
		return isKeyBindingActive();
	    }
	});
	return map;
    }
    
    protected void uninstallDefaults() {
	Icon frameIcon = frame.getFrameIcon();
        if (frameIcon instanceof UIResource) {
            frame.setFrameIcon(null);
        }
	LookAndFeel.uninstallBorder(frame);
    }

    protected void uninstallComponents(){
    }

    protected void uninstallListeners(){
      deinstallMouseHandlers(frame);      
      frame.removePropertyChangeListener(propertyChangeListener);
      frame.setLayout(null);    
      internalFrameLayout = null;
      propertyChangeListener = null;
      frame.getGlassPane().removeMouseListener(glassPaneDispatcher);
      frame.getGlassPane().removeMouseMotionListener(glassPaneDispatcher);
      if ((frame.getParent() != null) && componentListenerAdded) {
	frame.getParent().removeComponentListener(componentListener);
        componentListenerAdded=false;
      }
      glassPaneDispatcher = null;
      componentListener = null;
      borderListener = null;
    }

    protected void uninstallKeyboardActions(){
      if (internalFrameListener != null)
	frame.removeInternalFrameListener(internalFrameListener);
      if (isKeyBindingRegistered()){
	  SwingUtilities.replaceUIInputMap(frame, JComponent.
					 WHEN_IN_FOCUSED_WINDOW, null);
	  SwingUtilities.replaceUIActionMap(frame, null);
      }

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
	if((JComponent)frame == x)
	    return frame.getLayout().minimumLayoutSize(x);
        return new Dimension(0, 0);
    }
    
    public Dimension getMaximumSize(JComponent x) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    

			
    /** Adds necessary mouseHandlers to currentPane and adds it to frame.
      * Reverse process for the newPane. 
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
	ActionMap actionMap = getActionMap();
	SwingUtilities.replaceUIActionMap(frame, actionMap);
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
//        replacePane(southPane, c);
        southPane = c;
    }

    public JComponent getWestPane() {
        return westPane;
    }

    public void setWestPane(JComponent c) {
//        replacePane(westPane, c);
        westPane = c;
    }

    public JComponent getEastPane() {
        return eastPane;
    }

    public void setEastPane(JComponent c) {
//        replacePane(eastPane, c);
        eastPane = c;
    }

  public class InternalFramePropertyChangeListener implements PropertyChangeListener
  {
    /** Detects changes in state from the JInternalFrame and handles actions.*/
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = (String)evt.getPropertyName();
        JInternalFrame f = (JInternalFrame)evt.getSource();
	Object newValue = evt.getNewValue();
	Object oldValue = evt.getOldValue();
	// ASSERT(frame == f) - This should always be true

        if(JInternalFrame.ROOT_PANE_PROPERTY.equals(prop)) {
	    if(oldValue != null)
		frame.remove((Component)oldValue);
	    if(newValue != null)
		frame.add((Component)newValue);
	/// Handle the action events from the Frame
	} else if(JInternalFrame.IS_CLOSED_PROPERTY.equals(prop)) {
	    if(newValue == Boolean.TRUE){
	      if ((frame.getParent() != null) && componentListenerAdded) {
	        frame.getParent().removeComponentListener(componentListener);
	      }
	      closeFrame(f);
	    }
	} else if(JInternalFrame.IS_MAXIMUM_PROPERTY.equals(prop)) {
	    if(newValue == Boolean.TRUE)
	      {
		maximizeFrame(f);
	      }
	    else 
	      {
	        minimizeFrame(f);
	      }
	} else if(JInternalFrame.IS_ICON_PROPERTY.equals(prop)) {
	    if (newValue == Boolean.TRUE) {
	      iconifyFrame(f);
	    }
	    else{
	      deiconifyFrame(f);
	    }
	} else if(JInternalFrame.IS_SELECTED_PROPERTY.equals(prop)) {
	  Component glassPane = f.getGlassPane();
	    if(newValue == Boolean.TRUE
	            && oldValue == Boolean.FALSE) {
		activateFrame(f);
		//	glassPane.removeMouseListener(glassPaneDispatcher);
		//	glassPane.removeMouseMotionListener(glassPaneDispatcher);
		glassPane.setVisible(false);
	    } else if(newValue == Boolean.FALSE
	            && oldValue == Boolean.TRUE) {
		deactivateFrame(f);
		//	glassPane.addMouseListener(glassPaneDispatcher);
		//	glassPane.addMouseMotionListener(glassPaneDispatcher);
		glassPane.setVisible(true);
	    }
	} else if ( prop.equals("ancestor") ) {
	  if ((frame.getParent() != null) && !componentListenerAdded ) {
	    f.getParent().addComponentListener(componentListener);
	    componentListenerAdded = true;
	    parentBounds = f.getParent().getBounds();
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
	
	result = frame.getRootPane().getPreferredSize();
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
	Dimension result;
	Insets i = frame.getInsets();
	
	result = frame.getRootPane().getMinimumSize();
	result.width += i.left + i.right;
	result.height += i.top + i.bottom;

        if(getNorthPane() != null) {
	    Dimension d = getNorthPane().getMinimumSize();
	    result.width = Math.max(d.width, result.width);
            result.height += d.height;
	}

        if(getSouthPane() != null) {
	    Dimension d = getSouthPane().getMinimumSize();
	    result.width = Math.max(d.width, result.width);
            result.height += d.height;
	}

        if(getEastPane() != null) {
	    Dimension d = getEastPane().getMinimumSize();
            result.width += d.width;
	    result.height = Math.max(d.height, result.height);
	}

        if(getWestPane() != null) {
	    Dimension d = getWestPane().getMinimumSize();
            result.width += d.width;
	    result.height = Math.max(d.height, result.height);
	}

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
    

    /** This method is called when the user wants to close the frame.
      * This action is delegated to the desktopManager.
      */
    protected void closeFrame(JInternalFrame f) {	
	getDesktopManager().closeFrame(f);
    }
    /** This method is called when the user wants to maximize the frame.
      * This action is delegated to the desktopManager.
      */
    protected void maximizeFrame(JInternalFrame f) {	
	getDesktopManager().maximizeFrame(f);
    }
    /** This method is called when the user wants to minimize the frame.
      * This action is delegated to the desktopManager.
      */
    protected void minimizeFrame(JInternalFrame f) {
	getDesktopManager().minimizeFrame(f);
    }
    /** This method is called when the user wants to iconify the frame.
      * This action is delegated to the desktopManager.
      */
    protected void iconifyFrame(JInternalFrame f) {
	getDesktopManager().iconifyFrame(f);
    }
    /** This method is called when the user wants to deiconify the frame.
      * This action is delegated to the desktopManager.
      */
    protected void deiconifyFrame(JInternalFrame f) {
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
	    if(resizeDir == RESIZE_NONE)
	        getDesktopManager().endDraggingFrame(frame);	
	    else
	        getDesktopManager().endResizingFrame(frame);
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

            if(!frame.isSelected()) {
                try { frame.setSelected(true); } catch (PropertyVetoException e1) { }
            }
            if(!frame.isResizable() || e.getSource() == getNorthPane()) {
                resizeDir = RESIZE_NONE;
		getDesktopManager().beginDraggingFrame(frame);
                return;
            }       

            if(e.getSource() == frame) {
                Insets i = frame.getInsets();
                if(e.getX() <= i.left) {
                    if(e.getY() < resizeCornerSize + i.top)
                        resizeDir = NORTH_WEST;
                    else if(e.getY() > frame.getHeight() 
					- resizeCornerSize - i.bottom)
                        resizeDir = SOUTH_WEST;
                    else                
                        resizeDir = WEST;
                } else if(e.getX() >= frame.getWidth() - i.right) {
                    if(e.getY() < resizeCornerSize + i.top)
                        resizeDir = NORTH_EAST;
                    else if(e.getY() > frame.getHeight() 
				- resizeCornerSize - i.bottom)
                        resizeDir = SOUTH_EAST;
                    else                
                        resizeDir = EAST;
                } else if(e.getY() <= i.top) {
                    if(e.getX() < resizeCornerSize + i.left)
                        resizeDir = NORTH_WEST;
                    else if(e.getX() > frame.getWidth() 
				- resizeCornerSize - i.right)
                        resizeDir = NORTH_EAST;
                    else                
                        resizeDir = NORTH;
                } else if(e.getY() >= frame.getHeight() - i.bottom) {
                    if(e.getX() < resizeCornerSize + i.left)
                        resizeDir = SOUTH_WEST;
                    else if(e.getX() > frame.getWidth() 
				- resizeCornerSize - i.right)
                        resizeDir = SOUTH_EAST;
                    else                
                        resizeDir = SOUTH;
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
                                     
            Point p; 
	    int newX, newY, newW, newH;
            int deltaX;
            int deltaY;
	    Dimension min;
	    Dimension max;
            p = SwingUtilities.convertPoint((Component)e.getSource(), 
                                        e.getX(), e.getY(), null);
        
            // Handle a MOVE 
            if(e.getSource() == getNorthPane()) {
                if (frame.isMaximum()) {
                    return;  // don't allow moving of maximized frames.
                }
		Insets i = frame.getInsets();
		int pWidth, pHeight;
		Dimension s = frame.getParent().getSize();
		pWidth = s.width;
		pHeight = s.height;


	        newX = startingBounds.x - (_x - p.x);
	        newY = startingBounds.y - (_y - p.y);
		// Make sure we stay in-bounds
		if(newX + i.left <= -__x)
		    newX = -__x - i.left;
		if(newY + i.top <= -__y)
		    newY = -__y - i.top;
		if(newX + __x + i.right > pWidth)
		    newX = pWidth - __x - i.right;
		if(newY + __y + i.bottom > pHeight)
		    newY =  pHeight - __y - i.bottom;

		getDesktopManager().dragFrame(frame, newX, newY);
                return;
            }

            if(!frame.isResizable()) {
                return;
            }

	    min = frame.getMinimumSize();
	    max = frame.getMaximumSize();
        
            deltaX = _x - p.x;
            deltaY = _y - p.y;

	    newX = frame.getX();
	    newY = frame.getY();
	    newW = frame.getWidth();
	    newH = frame.getHeight();

            switch(resizeDir) {
            case RESIZE_NONE:
                return;
            case NORTH:      
		if(startingBounds.height + deltaY < min.height)
		    deltaY = -(startingBounds.height - min.height);
		else if(startingBounds.height + deltaY > max.height)
		    deltaY = max.height - startingBounds.height;
		if (startingBounds.y - deltaY < 0) {deltaY = startingBounds.y;}

		newY = startingBounds.y - deltaY;
		newX = startingBounds.x;
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
		if (startingBounds.x + startingBounds.width - deltaX > parentBounds.width) {
            deltaX = startingBounds.x + startingBounds.width - parentBounds.width;
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
		if (startingBounds.x + startingBounds.width - deltaX > parentBounds.width) {
          deltaX = startingBounds.x + startingBounds.width - parentBounds.width;
	        }

		newW = startingBounds.width - deltaX;
		newH = startingBounds.height;
                break;
            case SOUTH_EAST:     
		if(startingBounds.width - deltaX < min.width)
		    deltaX = startingBounds.width - min.width;
		else if(startingBounds.width - deltaX > max.width)
		    deltaX = -(max.width - startingBounds.width);
		if (startingBounds.x + startingBounds.width - deltaX > parentBounds.width) {
          deltaX = startingBounds.x + startingBounds.width - parentBounds.width;
	        }

		if(startingBounds.height - deltaY < min.height)
		    deltaY = startingBounds.height - min.height;
		else if(startingBounds.height - deltaY > max.height)
		    deltaY = -(max.height - startingBounds.height);
		if (startingBounds.y + startingBounds.height - deltaY > parentBounds.height) {
         deltaY = startingBounds.y + startingBounds.height - parentBounds.height ;
                }
	
		newW = startingBounds.width - deltaX;
		newH = startingBounds.height - deltaY;
                break;
            case SOUTH:      
		if(startingBounds.height - deltaY < min.height)
		    deltaY = (startingBounds.height - min.height);
		else if(startingBounds.height - deltaY > max.height)
		    deltaY = -(max.height - startingBounds.height);

                if (startingBounds.y + startingBounds.height - deltaY > parentBounds.height) {
                   deltaY = startingBounds.y + startingBounds.height - parentBounds.height ;
                }

 		newW = startingBounds.width;
		newH = startingBounds.height - deltaY;
                break;
            case SOUTH_WEST:
		if(startingBounds.height - deltaY < min.height)
		    deltaY = (startingBounds.height - min.height);
		else if(startingBounds.height - deltaY > max.height)
		    deltaY = -(max.height - startingBounds.height);
                 if (startingBounds.y + startingBounds.height - deltaY > parentBounds.height) {
   		deltaY = startingBounds.y + startingBounds.height - parentBounds.height ;
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
		
            if(e.getSource() == frame) {
                Insets i = frame.getInsets();
                if(e.getX() <= i.left) {
                    if(e.getY() < resizeCornerSize + i.top)
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                    else if(e.getY() > frame.getHeight() - resizeCornerSize - i.bottom)
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
                    else                
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                } else if(e.getX() >= frame.getWidth() - i.right) {
                    if(e.getY() < resizeCornerSize + i.top)
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                    else if(e.getY() > frame.getHeight() - resizeCornerSize - i.bottom)
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                    else                
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                } else if(e.getY() <= i.top) {
                    if(e.getX() < resizeCornerSize + i.left)
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                    else if(e.getX() > frame.getWidth() - resizeCornerSize - i.right)
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                    else                
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                } else if(e.getY() >= frame.getHeight() - i.bottom) {
                    if(e.getX() < resizeCornerSize + i.left)
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
                    else if(e.getX() > frame.getWidth() - resizeCornerSize - i.right)
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                    else                
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                }
		return;
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


    protected class GlassPaneDispatcher implements MouseInputListener
    {
      /**
       * When inactive, mouse events are forwarded as appropriate either to 
       * the UI to activate the frame or to the underlying child component.
       *
       * In keeping with the MDI messaging model (which JInternalFrame
       * emulates), only the mousePressed event is forwarded to the UI
       * to activate the frame.  The mouseEntered, mouseMoved, and 
       * MouseExited events are forwarded to the underlying child
       * component, using methods derived from those in Container.
       * The other mouse events are purposely ignored, since they have
       * no meaning to either the frame or its children when the frame
       * is inactive.
       */
      public void mousePressed(MouseEvent e) {
	// what is going on here is the GlassPane is up on the inactive
	// internalframe and want's to "catch" the first mousePressed on
	// the frame in order to give it to the BorderLister (and not the
	// underlying component) and let it activate the frame
	if (borderListener != null){
	  borderListener.mousePressed(e);
	} 
	// fix for 4152560
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
     * Ignore mouseReleased events.
     * @see #mousePressed
     */
      public void mouseReleased(MouseEvent e) {
	//System.out.println("forward release");
	forwardMouseEvent(e);
      }
      /** 
     * Ignore mouseDragged events.
     * @see #mousePressed
     */
      public void mouseDragged(MouseEvent e) {
      }

      /* 
     * Forward a mouse event to the current mouse target, setting it
     * if necessary.
     */
      private void forwardMouseEvent(MouseEvent e) {
        Component target = findComponentAt(frame.getRootPane().getLayeredPane(), 
                                           e.getX(), e.getY());
	//if (e.getID() == MouseEvent.MOUSE_PRESSED) {
	//  System.out.println("Mouse pressed forwarded to " + target);
	//} else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
	//System.out.println("Mouse released forwarded to " + target);
	//} else if (e.getID() == MouseEvent.MOUSE_CLICKED) {
	//System.out.println("Mouse clicked forwarded to " + target);
	//}
        if (target != mouseEventTarget) {
	  setMouseTarget(target, e);
        }
        retargetMouseEvent(e.getID(), e);
      }

      private Component mouseEventTarget = null;

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
	      (comp.getPeer() instanceof java.awt.peer.LightweightPeer) &&
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
     * Set the child component to which events are forwarded, and
     * synthesize the appropriate mouseEntered and mouseExited events.
     */
      private void setMouseTarget(Component target, MouseEvent e) {
        if (mouseEventTarget != null) {
	  retargetMouseEvent(MouseEvent.MOUSE_EXITED, e);
        }
        mouseEventTarget = target;
        if (mouseEventTarget != null) {
	  retargetMouseEvent(MouseEvent.MOUSE_ENTERED, e);
        }
      }

      /* 
     * Dispatch an event clone, retargeted for the current mouse target.
     */
      void retargetMouseEvent(int id, MouseEvent e) {
        // fix for bug #4202966 -- hania
        // When retargetting a mouse event, we need to translate
        // the event's coordinates relative to the target.
        Point p = SwingUtilities.convertPoint(frame.getLayeredPane(),
                                              e.getX(), e.getY(),
                                              mouseEventTarget);
        MouseEvent retargeted = new MouseEvent(mouseEventTarget, 
                                               id, 
                                               e.getWhen(), 
                                               e.getModifiers(),
                                               p.x, 
                                               p.y, 
                                               e.getClickCount(), 
                                               e.isPopupTrigger());
        mouseEventTarget.dispatchEvent(retargeted);
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


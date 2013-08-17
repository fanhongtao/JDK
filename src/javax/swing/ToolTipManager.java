/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package javax.swing;

import java.awt.event.*;
import java.applet.*;
import java.awt.*;
import java.io.Serializable;
/**
 * Manages all the ToolTips in the system.
 *
 * @see JComponent#createToolTip
 * @version 1.44 06/01/99
 * @author Dave Moore
 * @author Rich Schiavi
 */
public class ToolTipManager extends MouseAdapter implements MouseMotionListener  {
    Timer enterTimer, exitTimer, insideTimer;
    String toolTipText;
    Point  preferredLocation;
    JComponent insideComponent;
    MouseEvent mouseEvent;
    boolean showImmediately;
    final static ToolTipManager sharedInstance = new ToolTipManager();
    Popup tipWindow;
    JToolTip tip;
    private PopupFactory popupFactory = new DefaultPopupFactory();

    private Rectangle popupRect = null;
    private Rectangle popupFrameRect = null;

    boolean enabled = true;
    boolean mouseAboveToolTip = false;
    private boolean tipShowing = false;
    private long timerEnter = 0;
   
    private KeyStroke postTip,hideTip;
    private Action postTipAction, hideTipAction;

    private FocusListener focusChangeListener = null;

    // PENDING(ges)
    protected boolean lightWeightPopupEnabled = true;
    protected boolean heavyWeightPopupEnabled = false;

    ToolTipManager() {
        enterTimer = new Timer(750, new insideTimerAction());
        enterTimer.setRepeats(false);
        exitTimer = new Timer(500, new outsideTimerAction());
        exitTimer.setRepeats(false);
        insideTimer = new Timer(4000, new stillInsideTimerAction());
        insideTimer.setRepeats(false);

	// create accessibility actions 
	postTip = KeyStroke.getKeyStroke(KeyEvent.VK_F1,Event.CTRL_MASK);
	postTipAction = new AbstractAction(){
	  public void actionPerformed(ActionEvent e){
	    if (tipWindow != null) {// showing we unshow
	      hideTipWindow();
	      insideComponent = null;
	    }
	    else {
	      hideTipWindow(); // be safe
	      enterTimer.stop();
	      exitTimer.stop();
	      insideTimer.stop();
	      insideComponent = (JComponent)e.getSource();
	      if (insideComponent != null){
		toolTipText = insideComponent.getToolTipText();
		preferredLocation = new Point(10,insideComponent.getHeight()+10);  // manual set
		showTipWindow();
		// put a focuschange listener on to bring the tip down
		if (focusChangeListener == null){
		  focusChangeListener = createFocusChangeListener();
		}
		insideComponent.addFocusListener(focusChangeListener); 
	      }
	    }
	  }
	};
	hideTip = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0);
	hideTipAction = new AbstractAction(){
	  public void actionPerformed(ActionEvent e){
	    hideTipWindow();
	    JComponent jc = (JComponent)e.getSource();
	    jc.removeFocusListener(focusChangeListener);
	    preferredLocation = null;
	    insideComponent = null;
	  }
	  public boolean isEnabled() {
	      // Only enable when the tooltip is showing, otherwise
	      // we will get in the way of any UI actions.
	      return tipShowing;
	  }
	};
    }

    /**
     * Enables or disables the tooltip.
     *
     * @param flag  true to enable the tip
     */
    public void setEnabled(boolean flag) {
        enabled = flag;
        if (!flag) {
            hideTipWindow();
        }
    }

    /**
     * Returns true if this object is enabled.
     *
     * @return true if this object is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * When displaying the JToolTip, the ToolTipManager choose to use a light weight JPanel if
     * it fits. This method allows you to disable this feature. You have to do disable
     * it if your application mixes light weight and heavy weights components.
     *
     */
    public void setLightWeightPopupEnabled(boolean aFlag){
	popupFactory.setLightWeightPopupEnabled(aFlag);
    }
    
    /**
     * Returns true if lightweight (all-Java) Tooltips are in use,
     * or false if heavyweight (native peer) Tooltips are being used.
     *
     * @return true if lightweight ToolTips are in use
     */
    public boolean isLightWeightPopupEnabled() { 
        return popupFactory.isLightWeightPopupEnabled();
    }


    /**
     * Specifies the initial delay value.
     *
     * @param milliseconds  the number of milliseconds
     *        to delay (after the cursor has paused) before displaying the
     *        tooltip
     * @see #getInitialDelay
     */
    public void setInitialDelay(int milliseconds) {
        enterTimer.setInitialDelay(milliseconds);
    }

    /**
     * Returns the initial delay value.
     *
     * @return an int representing the initial delay value
     * @see #setInitialDelay
     */
    public int getInitialDelay() {
        return enterTimer.getInitialDelay();
    }

    /**
     * Specifies the dismisal delay value.
     *
     * @param milliseconds  the number of milliseconds
     *        to delay (after the cursor has moved on) before taking away
     *        the tooltip
     * @see #getDismissDelay
     */
    public void setDismissDelay(int milliseconds) {
        insideTimer.setInitialDelay(milliseconds);
    }

    /**
     * Returns the dismisal delay value.
     *
     * @return an int representing the dismisal delay value
     * @see #setDismissDelay
     */
    public int getDismissDelay() {
        return insideTimer.getInitialDelay();
    }

    /**
     * Specifies the time to delay before reshowing the tooltip.
     *
     * @param milliseconds  the time in milliseconds
     *        to delay before reshowing the tooltip if the cursor stops again
     * @see #getReshowDelay
     */
    public void setReshowDelay(int milliseconds) {
        exitTimer.setInitialDelay(milliseconds);
    }

    /**
     * Returns the reshow delay value.
     *
     * @return an int representing the reshow delay value
     * @see #setReshowDelay
     */
    public int getReshowDelay() {
        return exitTimer.getInitialDelay();
    }

    void showTipWindow() {
        if(insideComponent == null || !insideComponent.isShowing())
            return;
        if (enabled) {
            Dimension size;
            Point screenLocation = insideComponent.getLocationOnScreen();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Point location = new Point();
	    boolean leftToRight 
                = SwingUtilities.isLeftToRight(insideComponent);

            // Just to be paranoid
            hideTipWindow();

            tip = insideComponent.createToolTip();
            tip.setTipText(toolTipText);
            size = tip.getPreferredSize();

            if(preferredLocation != null) {
                location.x = screenLocation.x + preferredLocation.x;
                location.y = screenLocation.y + preferredLocation.y;
		if (!leftToRight) {
		    location.x -= size.width;
		}
            } else {
                location.x = screenLocation.x + mouseEvent.getX();
                location.y = screenLocation.y + mouseEvent.getY() + 20;
		if (!leftToRight) {
		    if(location.x - size.width>=0) {
		        location.x -= size.width;
		    }
		}

                if (location.x + size.width > screenSize.width) {
                    location.x -= size.width;
                }
                if (location.y + size.height > screenSize.height) {
                    location.y -= (size.height + 20);
                }
            }

	    // we do not adjust x/y when using awt.Window tips
	    if (!heavyWeightPopupEnabled){

		if (popupRect == null){
		  popupRect = new Rectangle();
		}
		popupRect.setBounds(location.x,location.y,
				    size.width,size.height);

		int y = getPopupFitHeight(popupRect, insideComponent);
		int x = getPopupFitWidth(popupRect,insideComponent);

		if (y > 0){
		    location.y -= y;
		}
		if (x > 0){
		    // adjust
		    location.x -= x;
		}
	    }		

	    tipWindow = popupFactory.getPopup(tip,
					      insideComponent,
					      location.x,
					      location.y);
	    if (tipWindow instanceof Window) 
		((Window)tipWindow).addMouseListener(this);

	    tipWindow.show(insideComponent);
            insideTimer.start();
	    timerEnter = System.currentTimeMillis();
	    tipShowing = true;
        }
    }

    void hideTipWindow() {
        if (tipWindow != null) {
	    if (tipWindow instanceof Window) 
		((Window)tipWindow).removeMouseListener(this);
	    tipWindow.hide();
	    tipWindow = null;
	    tipShowing = false;
	    timerEnter = 0;
	    (tip.getUI()).uninstallUI(tip);
            tip = null;
            insideTimer.stop();
        }
    }

    /**
     * Returns a shared ToolTipManager instance.
     *
     * @return a shared ToolTipManager object
     */
    public static ToolTipManager sharedInstance() {
        return sharedInstance;
    }

    // add keylistener here to trigger tip for access
    /**
     * Register a component for tooltip management.
     * <p>This will register key bindings to show and hide the tooltip text
     * only if <code>component</code> has focus bindings. This is done
     * so that components that are not normally focus traversable, such
     * as JLabel, are not made focus traversable as a result of invoking
     * this method.
     *
     * @param component  a JComponent object
     * @see JComponent#isFocusTraversable
     */
    public void registerComponent(JComponent component) {
        component.removeMouseListener(this);
        component.addMouseListener(this);
	if (shouldRegisterBindings(component)) {
	    // register our accessibility keybindings for this component
	    // this will apply globally across L&F
	    // Post Tip: Ctrl+F1
	    // Unpost Tip: Esc and Ctrl+F1
	    InputMap inputMap = component.getInputMap(JComponent.WHEN_FOCUSED);
	    ActionMap actionMap = component.getActionMap();

	    if (inputMap != null && actionMap != null) {
		inputMap.put(postTip, "postTip");
		inputMap.put(hideTip, "hideTip");
		actionMap.put("postTip", postTipAction);
		actionMap.put("hideTip", hideTipAction);
	    }
	}
    }

    /**
     * Remove a component from tooltip control.
     *
     * @param component  a JComponent object
     */
    public void unregisterComponent(JComponent component) {
        component.removeMouseListener(this);
	if (shouldRegisterBindings(component)) {
	    InputMap inputMap = component.getInputMap(JComponent.WHEN_FOCUSED);
	    ActionMap actionMap = component.getActionMap();

	    if (inputMap != null && actionMap != null) {
		inputMap.remove(postTip);
		inputMap.remove(hideTip);
		actionMap.remove("postTip");
		actionMap.remove("hideTip");
	    }
	}
    }

    /**
     * Returns whether or not bindings should be registered on the given
     * Component. This is implemented to return true if the receiver has
     * a binding in any one of the InputMaps registered under the condition
     * <code>WHEN_FOCUSED</code>.
     * <p>
     * This does not use <code>isFocusTraversable</code> as
     * some components may override <code>isFocusTraversable</code> and
     * base the return value on something other than bindings. For example,
     * JButton bases its return value on its enabled state.
     */
    private boolean shouldRegisterBindings(JComponent component) {
	InputMap inputMap = component.getInputMap(JComponent.WHEN_FOCUSED,
						  false);
	while (inputMap != null && inputMap.size() == 0) {
	    inputMap = inputMap.getParent();
	}
	return (inputMap != null);
    }

    // implements java.awt.event.MouseListener
    public void mouseEntered(MouseEvent event) {
       // this is here for a workaround for a Solaris *application* only bug
       // in which an extra MouseExit/Enter events are generated when a Panel
       // initially is shown
      if ((tipShowing) && !lightWeightPopupEnabled)
	{
	    if (System.currentTimeMillis() - timerEnter < 200){
		return;
	    }
	}  
        if(event.getSource() == tipWindow)
            return;

        JComponent component = (JComponent)event.getSource();
        toolTipText = component.getToolTipText(event);
        preferredLocation = component.getToolTipLocation(event);

        exitTimer.stop();

	Point location = event.getPoint();
	// ensure tooltip shows only in proper place
	if (location.x < 0 || 
	    location.x >=component.getWidth() ||
	    location.y < 0 ||
	    location.y >= component.getHeight())
	  {
	    return;
	  }

        if (insideComponent != null) {
            enterTimer.stop();
        }

        component.addMouseMotionListener(this);

        insideComponent = component;
	//        if (toolTipText != null) {
	// fix for 4133318
	if (tipWindow != null){
	  // fix for 4139679
	  // without this - the tip flashes
	  // since we get extra enter from the
	  // tip window when being displayed over top
	  // of the component - the behaviour is
	  // the same whether or not we are over the
	  // component - so additional location checks unneeded
	  if (heavyWeightPopupEnabled){ 
	    return;
	  }
	  else {
	    mouseEvent = event;
            if (showImmediately) {
	      showTipWindow();
            } else {
	      enterTimer.start();
            }
	  }
        }
    }

    // implements java.awt.event.MouseListener
    public void mouseExited(MouseEvent event) {
       // this is here for a workaround for a Solaris *application* only bug
      //  when Panels are used
      if ((tipShowing) && !lightWeightPopupEnabled)
	{
	    if (System.currentTimeMillis() - timerEnter < 200)
	    {
		return;
	    }
	}  

        boolean shouldHide = true;
        if (insideComponent == null) {
            // Drag exit
        } 
        if(event.getSource() == tipWindow) {
	  // if we get an exit and have a heavy window
	  // we need to check if it if overlapping the inside component
            Container insideComponentWindow = insideComponent.getTopLevelAncestor();
            Rectangle b = tipWindow.getBoundsOnScreen();
            Point location = event.getPoint();
            location.x += b.x;
            location.y += b.y;

            b = insideComponentWindow.getBounds();
            location.x -= b.x;
            location.y -= b.y;
            
            location = SwingUtilities.convertPoint(null,location,insideComponent);
            if(location.x >= 0 && location.x < insideComponent.getWidth() &&
               location.y >= 0 && location.y < insideComponent.getHeight()) {
                shouldHide = false;
            } else
	      shouldHide = true;
        } else if(event.getSource() == insideComponent && tipWindow != null) {
            Point location = SwingUtilities.convertPoint(insideComponent,
                                                         event.getPoint(),
                                                         null);
            Rectangle bounds = insideComponent.getTopLevelAncestor().getBounds();
            location.x += bounds.x;
            location.y += bounds.y;

            bounds = tipWindow.getBoundsOnScreen();
            if(location.x >= bounds.x && location.x < (bounds.x + bounds.width) &&
               location.y >= bounds.y && location.y < (bounds.y + bounds.height)) {
                shouldHide = false;
            } else
                shouldHide = true;
        } 
        
        if(shouldHide) {        
            enterTimer.stop();
	    if (insideComponent != null) {
	      insideComponent.removeMouseMotionListener(this);
	    }
            insideComponent = null;
            toolTipText = null;
            mouseEvent = null;
            hideTipWindow();
            exitTimer.start();
        }
    }

    // implements java.awt.event.MouseListener
    public void mousePressed(MouseEvent event) {
        hideTipWindow();
        enterTimer.stop();
        showImmediately = false;
        mouseEvent = null;
    }

    // implements java.awt.event.MouseMotionListener
    public void mouseDragged(MouseEvent event) {
    }

    // implements java.awt.event.MouseMotionListener
    public void mouseMoved(MouseEvent event) {
        JComponent component = (JComponent)event.getSource();
        String newText = component.getToolTipText(event);
        Point  newPreferredLocation = component.getToolTipLocation(event);


       //System.out.println("Pringting here ");
       //System.out.println("newText is "+newText);
       //System.out.println("Poin is "+newPreferredLocation.toString());

        if (newText != null || newPreferredLocation != null) {
            mouseEvent = event;
            if (((newText != null && newText.equals(toolTipText)) || newText == null) &&
                ((newPreferredLocation != null && newPreferredLocation.equals(preferredLocation)) 
                 || newPreferredLocation == null)) {
                if (tipWindow != null) {
                    insideTimer.restart();
                } else {
                    enterTimer.restart();
                }
            } else {
                toolTipText = newText;
                preferredLocation = newPreferredLocation;
                if (showImmediately) {
                    hideTipWindow();
                    showTipWindow();
                } else {
                    enterTimer.restart();
                }
            }
        } else {
            toolTipText = null;
            preferredLocation = null;
            mouseEvent = null;
            hideTipWindow();
            enterTimer.stop();
            exitTimer.start();
        }
    }

    protected class insideTimerAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if(insideComponent != null && insideComponent.isShowing()) {
                showImmediately = true;
                showTipWindow();
            }
        }
    }

    protected class outsideTimerAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            showImmediately = false;
        }
    }

    protected class stillInsideTimerAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            hideTipWindow();
            enterTimer.stop();
            showImmediately = false;
            mouseEvent = null;
        }
    }

    static Frame frameForComponent(Component component) {
        while (!(component instanceof Frame)) {
            component = component.getParent();
        }
        return (Frame)component;
    }

  private FocusListener createFocusChangeListener(){
    return new FocusAdapter(){
      public void focusLost(FocusEvent evt){
	hideTipWindow();
	insideComponent = null;
	JComponent c = (JComponent)evt.getSource();
	c.removeFocusListener(focusChangeListener);
      }
    };
  }

  // Returns: 0 no adjust
  //         -1 can't fit
  //         >0 adjust value by amount returned
  private int getPopupFitWidth(Rectangle popupRectInScreen, Component invoker){
    if (invoker != null){
      Container parent;
      for (parent = invoker.getParent(); parent != null; parent = parent.getParent()){
	// fix internal frame size bug: 4139087 - 4159012
	if(parent instanceof JFrame || parent instanceof JDialog ||
	   parent instanceof JWindow) { // no check for awt.Frame since we use Heavy tips
	  return getWidthAdjust(parent.getBounds(),popupRectInScreen);
	} else if (parent instanceof JApplet || parent instanceof JInternalFrame) {
	  if (popupFrameRect == null){
	    popupFrameRect = new Rectangle();
	  }
	  Point p = parent.getLocationOnScreen();
	  popupFrameRect.setBounds(p.x,p.y,
				   parent.getBounds().width,
				   parent.getBounds().height);
	  return getWidthAdjust(popupFrameRect,popupRectInScreen);
	}
      }
    }
    return 0;
  }

  // Returns:  0 no adjust
  //          >0 adjust by value return
  private int getPopupFitHeight(Rectangle popupRectInScreen, Component invoker){
    if (invoker != null){
      Container parent;
      for (parent = invoker.getParent(); parent != null; parent = parent.getParent()){
	if(parent instanceof JFrame || parent instanceof JDialog ||
	   parent instanceof JWindow) {
	  return getHeightAdjust(parent.getBounds(),popupRectInScreen);
	} else if (parent instanceof JApplet || parent instanceof JInternalFrame) {
	  if (popupFrameRect == null){
	    popupFrameRect = new Rectangle();
	  }
	  Point p = parent.getLocationOnScreen();
	  popupFrameRect.setBounds(p.x,p.y,
				   parent.getBounds().width,
				   parent.getBounds().height);
	  return getHeightAdjust(popupFrameRect,popupRectInScreen);
	}
      }
    }
    return 0;
  }

  private int getHeightAdjust(Rectangle a, Rectangle b){
    if (b.y >= a.y && (b.y + b.height) <= (a.y + a.height))
      return 0;
    else
      return (((b.y + b.height) - (a.y + a.height)) + 5);
  }

  // Return the number of pixels over the edge we are extending.
  // If we are over the edge the ToolTipManager can adjust.
  // REMIND: what if the Tooltip is just too big to fit at all - we currently will just clip
  private int getWidthAdjust(Rectangle a, Rectangle b){
    //    System.out.println("width b.x/b.width: " + b.x + "/" + b.width +
    //		       "a.x/a.width: " + a.x + "/" + a.width);
    if (b.x >= a.x && (b.x + b.width) <= (a.x + a.width)){
      return 0;
    }
    else {
      return (((b.x + b.width) - (a.x +a.width)) + 5);
    }
  }
	

}

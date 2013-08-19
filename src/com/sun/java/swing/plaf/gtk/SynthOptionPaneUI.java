/*
 * @(#)SynthOptionPaneUI.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.OptionPaneUI;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;


/**
 * Provides the basic look and feel for a <code>JOptionPane</code>.
 * <code>BasicMessagePaneUI</code> provides a means to place an icon,
 * message and buttons into a <code>Container</code>.
 * Generally, the layout will look like:<p>
 * <pre>
 *        ------------------
 *        | i | message    |
 *        | c | message    |
 *        | o | message    |
 *        | n | message    |
 *        ------------------
 *        |     buttons    |
 *        |________________|
 * </pre>
 * icon is an instance of <code>Icon</code> that is wrapped inside a
 * <code>JLabel</code>.  The message is an opaque object and is tested
 * for the following: if the message is a <code>Component</code> it is
 * added to the <code>Container</code>, if it is an <code>Icon</code>
 * it is wrapped inside a <code>JLabel</code> and added to the 
 * <code>Container</code> otherwise it is wrapped inside a <code>JLabel</code>.
 * <p>
 * The above layout is used when the option pane's 
 * <code>ComponentOrientation</code> property is horizontal, left-to-right.
 * The layout will be adjusted appropriately for other orientations.
 * <p>
 * The <code>Container</code>, message, icon, and buttons are all
 * determined from abstract methods.
 * 
 * @version 1.11, 01/23/03 (based on BasicOptionPaneUI v 1.54)
 * @author James Gosling
 * @author Scott Violet
 * @author Amy Fowler
 */
class SynthOptionPaneUI extends OptionPaneUI implements SynthUI{
    public static final int MinimumWidth = 262;
    public static final int MinimumHeight = 90;

    private static String newline;

    private SynthStyle style;

    /**
     * <code>JOptionPane</code> that the receiver is providing the
     * look and feel for.
     */
    protected JOptionPane         optionPane;

    protected Dimension minimumSize;

    /** JComponent provide for input if optionPane.getWantsInput() returns
     * true. */
    protected JComponent          inputComponent;

    /** Component to receive focus when messaged with selectInitialValue. */
    protected Component           initialFocusComponent;

    /** This is set to true in validateComponent if a Component is contained
     * in either the message or the buttons. */
    protected boolean             hasCustomComponents;

    protected PropertyChangeListener propertyChangeListener;

    private int multiClickThreshold;


    static {
	newline = (String)java.security.AccessController.doPrivileged(
                                new GetPropertyAction("line.separator"));
        if (newline == null) {
            newline = "\n";
        }
    }


    /**
      * Creates a new BasicOptionPaneUI instance.
      */
    public static ComponentUI createUI(JComponent x) {
	return new SynthOptionPaneUI();
    }

   public static void loadActionMap(ActionMap map) {
        // NOTE: this needs to remain static. If you have a need to
        // have Actions that reference the UI in the ActionMap,
        // then you'll also need to change the registeration of the
        // ActionMap.
	map.put("close", new CloseAction());
	// Set the ActionMap's parent to the Auditory Feedback Action Map
        // PENDING: audio
/*
	BasicLookAndFeel lf = (BasicLookAndFeel)UIManager.getLookAndFeel();
	ActionMap audioMap = lf.getAudioActionMap();
	map.setParent(audioMap);
*/
    }

     /**
      * Installs the receiver as the L&F for the passed in
      * <code>JOptionPane</code>.
      */
    public void installUI(JComponent c) {
	optionPane = (JOptionPane)c;
        installDefaults();
        optionPane.setLayout(createLayoutManager());
	installComponents();
        installListeners(); 
        installKeyboardActions();
    }

    /**
      * Removes the receiver from the L&F controller of the passed in split
      * pane.
      */
    public void uninstallUI(JComponent c) {
        uninstallComponents();
        optionPane.setLayout(null);
        uninstallKeyboardActions();
        uninstallListeners();
        uninstallDefaults();
	optionPane = null;
    }

    protected void installDefaults() {
        fetchStyle(optionPane);
    }

    private void fetchStyle(JComponent c) {
        SynthContext context = getContext(c, ENABLED);
        SynthStyle oldStyle = style;

        style = SynthLookAndFeel.updateStyle(context, this);
        if (style != oldStyle) {
            minimumSize = (Dimension)style.get(context,
                                               "OptionPane.minimumSize");
            multiClickThreshold = style.getInt(
                      context, "OptionPane.buttonClickThreshhold", 0);
        }
        context.dispose();
    }

    protected void uninstallDefaults() {
        SynthContext context = getContext(optionPane, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    protected void installComponents() {
	optionPane.add(createMessageArea());
        
        Container separator = createSeparator();
        if (separator != null) {
            optionPane.add(separator);
            SynthContext context = getContext(optionPane, ENABLED);
            optionPane.add(Box.createVerticalStrut(context.getStyle().
                       getInt(context, "OptionPane.separatorPadding", 6)));
            context.dispose();
        }
	optionPane.add(createButtonArea());
	optionPane.applyComponentOrientation(optionPane.getComponentOrientation());
    }

    protected void uninstallComponents() {
	hasCustomComponents = false;
        inputComponent = null;
	initialFocusComponent = null;
	optionPane.removeAll();
    }

    protected LayoutManager createLayoutManager() {
        return new BoxLayout(optionPane, BoxLayout.Y_AXIS);
    }

    protected void installListeners() {
        if ((propertyChangeListener = createPropertyChangeListener()) != null) {
            optionPane.addPropertyChangeListener(propertyChangeListener);
        }
    }

    protected void uninstallListeners() {
        if (propertyChangeListener != null) {
            optionPane.removePropertyChangeListener(propertyChangeListener);
            propertyChangeListener = null;
        }
    }

    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    protected void installKeyboardActions() {
	InputMap map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

	SwingUtilities.replaceUIInputMap(optionPane, JComponent.
				       WHEN_IN_FOCUSED_WINDOW, map);
        LazyActionMap.installLazyActionMap(optionPane, SynthOptionPaneUI.class,
                                           "OptionPane.actionMap");
    }

    protected void uninstallKeyboardActions() {
	SwingUtilities.replaceUIInputMap(optionPane, JComponent.
				       WHEN_IN_FOCUSED_WINDOW, null);
	SwingUtilities.replaceUIActionMap(optionPane, null);
    }

    InputMap getInputMap(int condition) {
	if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
            SynthContext context = getContext(optionPane, ENABLED);
	    Object[] bindings = (Object[])context.getStyle().get
		                (context, "OptionPane.windowBindings");
            InputMap map = null;

	    if (bindings != null) {
		map = LookAndFeel.makeComponentInputMap(optionPane, bindings);
	    }
            context.dispose();
            return map;
	}
	return null;
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

    /**
     * Returns the minimum size the option pane should be. Primarily
     * provided for subclassers wishing to offer a different minimum size.
     */
    public Dimension getMinimumOptionPaneSize() {
        if (minimumSize == null) {
            return new Dimension(MinimumWidth, MinimumHeight);
        }
	return new Dimension(minimumSize.width,
			     minimumSize.height);
    }

    /**
     * If <code>c</code> is the <code>JOptionPane</code> the receiver
     * is contained in, the preferred
     * size that is returned is the maximum of the preferred size of
     * the <code>LayoutManager</code> for the <code>JOptionPane</code>, and
     * <code>getMinimumOptionPaneSize</code>.
     */
    public Dimension getPreferredSize(JComponent c) {
	if ((JOptionPane)c == optionPane) {
	    Dimension            ourMin = getMinimumOptionPaneSize();
	    LayoutManager        lm = c.getLayout();

	    if (lm != null) {
		Dimension         lmSize = lm.preferredLayoutSize(c);

		if (ourMin != null)
		    return new Dimension
			(Math.max(lmSize.width, ourMin.width),
			 Math.max(lmSize.height, ourMin.height));
		return lmSize;
	    }
	    return ourMin;
	}
	return null;
    }

    /**
     * Messaged from installComponents to create a Container containing the
     * body of the message. The icon is the created by calling
     * <code>addIcon</code>.
     */
    protected Container createMessageArea() {
        JPanel top = new JPanel();
        top.setName("OptionPane.messageArea");
	top.setLayout(new BorderLayout());

	/* Fill the body. */
	Container          body = new JPanel(new GridBagLayout());
	Container          realBody = new JPanel(new BorderLayout());

        body.setName("OptionPane.body");
        realBody.setName("OptionPane.realBody");

	if (getIcon() != null) {
            JPanel sep = new JPanel();
            sep.setName("OptionPane.separator");
            sep.setPreferredSize(new Dimension(15, 1));
	    realBody.add(sep, BorderLayout.BEFORE_LINE_BEGINS);
	}
	realBody.add(body, BorderLayout.CENTER);

	GridBagConstraints cons = new GridBagConstraints();
	cons.gridx = cons.gridy = 0;
	cons.gridwidth = GridBagConstraints.REMAINDER;
	cons.gridheight = 1;

        SynthContext context = getContext(optionPane, ENABLED);
	cons.anchor = context.getStyle().getInt(context,
                      "OptionPane.messageAnchor", GridBagConstraints.CENTER);
        context.dispose();

	cons.insets = new Insets(0,0,3,0);

	addMessageComponents(body, cons, getMessage(),
			  getMaxCharactersPerLineCount(), false);
	top.add(realBody, BorderLayout.CENTER);

	addIcon(top);
	return top;
    }

    /**
     * Creates the appropriate object to represent <code>msg</code> and
     * places it into <code>container</code>. If <code>msg</code> is an
     * instance of Component, it is added directly, if it is an Icon,
     * a JLabel is created to represent it, otherwise a JLabel is
     * created for the string, if <code>d</code> is an Object[], this
     * method will be recursively invoked for the children.
     * <code>internallyCreated</code> is true if Objc is an instance
     * of Component and was created internally by this method (this is
     * used to correctly set hasCustomComponents only if !internallyCreated).
     */
    protected void addMessageComponents(Container container,
				     GridBagConstraints cons,
				     Object msg, int maxll,
				     boolean internallyCreated) {
	if (msg == null) {
	    return;
        }
	if (msg instanceof Component) {
            // To workaround problem where Gridbad will set child
            // to its minimum size if its preferred size will not fit
            // within allocated cells
            if (msg instanceof JScrollPane || msg instanceof JPanel) {
                cons.fill = GridBagConstraints.BOTH;
                cons.weighty = 1;
            } else {
	        cons.fill = GridBagConstraints.HORIZONTAL;
            }
	    cons.weightx = 1;

	    container.add((Component) msg, cons);
	    cons.weightx = 0;
            cons.weighty = 0;
	    cons.fill = GridBagConstraints.NONE;
	    cons.gridy++;
	    if (!internallyCreated) {
		hasCustomComponents = true;
            }

	} else if (msg instanceof Object[]) {
	    Object [] msgs = (Object[]) msg;
	    for (int i = 0; i < msgs.length; i++) {
		addMessageComponents(container, cons, msgs[i], maxll, false);
            }

	} else if (msg instanceof Icon) {
	    JLabel label = new JLabel( (Icon)msg, SwingConstants.CENTER );
            configureMessageLabel(label);
	    addMessageComponents(container, cons, label, maxll, true);

	} else {
	    String s = msg.toString();
	    int len = s.length();
	    if (len <= 0) {
		return;
            }
	    int nl = -1;
	    int nll = 0;

	    if ((nl = s.indexOf(newline)) >= 0) {
		nll = newline.length();
	    } else if ((nl = s.indexOf("\r\n")) >= 0) {
	        nll = 2;
	    } else if ((nl = s.indexOf('\n')) >= 0) {
	        nll = 1;
	    }
	    if (nl >= 0) {
		// break up newlines
		if (nl == 0) {
                    JPanel breakPanel = new JPanel() {
		        public Dimension getPreferredSize() {
			    Font       f = getFont();
			    
			    if (f != null) {
				return new Dimension(1, f.getSize() + 2);
                            }
			    return new Dimension(0, 0);
		        }
                    };
                    breakPanel.setName("OptionPane.break");
		    addMessageComponents(container, cons, breakPanel, maxll,
                                         true);
		} else {
		    addMessageComponents(container, cons, s.substring(0, nl),
				      maxll, false);
                }
		addMessageComponents(container, cons, s.substring(nl + nll),
                                     maxll, false);

	    } else if (len > maxll) {
		Container c = Box.createVerticalBox();
                c.setName("OptionPane.verticalBox");
		burstStringInto(c, s, maxll);
		addMessageComponents(container, cons, c, maxll, true );

	    } else {
	        JLabel label;
		label = new JLabel( s, JLabel.LEADING );
                label.setName("OptionPane.label");
                configureMessageLabel(label);
		addMessageComponents(container, cons, label, maxll, true);
	    }
	}
    }

    /**
     * Returns the message to display from the JOptionPane the receiver is
     * providing the look and feel for.
     */
    protected Object getMessage() {
	inputComponent = null;
	if (optionPane != null) {
	    if (optionPane.getWantsInput()) {
		/* Create a user component to capture the input. If the
		   selectionValues are non null the component and there
		   are < 20 values it'll be a combobox, if non null and
		   >= 20, it'll be a list, otherwise it'll be a textfield. */
		Object             message = optionPane.getMessage();
		Object[]           sValues = optionPane.getSelectionValues();
		Object             inputValue = optionPane
		                           .getInitialSelectionValue();
		JComponent         toAdd;

		if (sValues != null) {
		    if (sValues.length < 20) {
			JComboBox            cBox = new JComboBox();

                        cBox.setName("OptionPane.comboBox");
			for(int counter = 0, maxCounter = sValues.length;
			    counter < maxCounter; counter++) {
			    cBox.addItem(sValues[counter]);
                        }
			if (inputValue != null) {
			    cBox.setSelectedItem(inputValue);
                        }
			inputComponent = cBox;
			toAdd = cBox;

		    } else {
			JList                list = new JList(sValues);
			JScrollPane          sp = new JScrollPane(list);

                        list.setName("OptionPane.scrollPane");
                        list.setName("OptionPane.list");
			list.setVisibleRowCount(10);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			if(inputValue != null)
			    list.setSelectedValue(inputValue, true);
			list.addMouseListener(new ListSelectionListener());
			toAdd = sp;
			inputComponent = list;
		    }

		} else {
		    MultiplexingTextField   tf = new MultiplexingTextField(20);

                    tf.setName("OptionPane.textField");
                    tf.setKeyStrokes(new KeyStroke[] {
                                     KeyStroke.getKeyStroke("ENTER") } );
		    if (inputValue != null) {
                        String inputString = inputValue.toString();
			tf.setText(inputString);
                        tf.setSelectionStart(0);
                        tf.setSelectionEnd(inputString.length());
                    }
		    tf.addActionListener(new TextFieldActionListener());
		    toAdd = inputComponent = tf;
		}

		Object[]           newMessage;

		if (message == null) {
		    newMessage = new Object[1];
		    newMessage[0] = toAdd;
		
		} else {
		    newMessage = new Object[2];
		    newMessage[0] = message;
		    newMessage[1] = toAdd;
		}
		return newMessage;
	    }
	    return optionPane.getMessage();
	}
	return null;
    }

    /**
     * Creates and adds a JLabel representing the icon returned from
     * <code>getIcon</code> to <code>top</code>. This is messaged from
     * <code>createMessageArea</code>
     */
    protected void addIcon(Container top) {
	/* Create the icon. */
	Icon                  sideIcon = getIcon();

	if (sideIcon != null) {
	    JLabel            iconLabel = new JLabel(sideIcon);

            iconLabel.setName("OptionPane.iconLabel");
	    iconLabel.setVerticalAlignment(SwingConstants.TOP);
	    top.add(iconLabel, BorderLayout.BEFORE_LINE_BEGINS);
	}
    }

    /**
     * Returns the icon from the JOptionPane the receiver is providing
     * the look and feel for, or the default icon as returned from
     * <code>getDefaultIcon</code>.
     */
    protected Icon getIcon() {
	Icon      mIcon = (optionPane == null ? null : optionPane.getIcon());

	if(mIcon == null && optionPane != null)
	    mIcon = getIconForType(optionPane.getMessageType());
	return mIcon;
    }

    /**
     * Returns the icon to use for the passed in type.
     */
    protected Icon getIconForType(int messageType) {
	if(messageType < 0 || messageType > 3)
	    return null;
        String propertyName = null;
	switch(messageType) {
	case 0:
	    propertyName = "OptionPane.errorIcon";
            break;
	case 1:
	    propertyName = "OptionPane.informationIcon";
            break;
	case 2:
	    propertyName = "OptionPane.warningIcon";
            break;
	case 3:
	    propertyName = "OptionPane.questionIcon";
            break;
	}
        if (propertyName != null) {
            SynthContext context = getContext(optionPane, ENABLED);
            Icon icon = context.getStyle().getIcon(context, propertyName);
            context.dispose();
            return icon;
        }
	return null;
    }

    /**
     * Returns the maximum number of characters to place on a line.
     */
    protected int getMaxCharactersPerLineCount() {
	return optionPane.getMaxCharactersPerLineCount();
    }

   /**
     * Recursively creates new JLabel instances to represent <code>d</code>.
     * Each JLabel instance is added to <code>c</code>.
     */
    protected void burstStringInto(Container c, String d, int maxll) {
	// Primitive line wrapping
	int len = d.length();
	if (len <= 0)
	    return;
	if (len > maxll) {
	    int p = d.lastIndexOf(' ', maxll);
	    if (p <= 0)
		p = d.indexOf(' ', maxll);
	    if (p > 0 && p < len) {
		burstStringInto(c, d.substring(0, p), maxll);
		burstStringInto(c, d.substring(p + 1), maxll);
		return;
	    }
	}
	JLabel label = new JLabel(d, JLabel.LEFT);
        label.setName("OptionPane.label");
        configureMessageLabel(label);
	c.add(label);
    }

    protected Container createSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);

        separator.setName("OptionPane.separator");
        return separator;
    }

    /**
     * Creates and returns a Container containing the buttons. The buttons
     * are created by calling <code>getButtons</code>.
     */
    protected Container createButtonArea() {
        JPanel bottom = new JPanel();
        bottom.setName("OptionPane.buttonArea");

        SynthContext context = getContext(optionPane, ENABLED);
        SynthStyle style = context.getStyle();
	bottom.setLayout(new ButtonAreaLayout(
           style.getBoolean(context, "OptionPane.sameSizeButtons", true),
           style.getInt(context, "OptionPane.buttonPadding", 10),
           style.getInt(context, "OptionPane.buttonOrientation",
                        SwingConstants.RIGHT),
           style.getBoolean(context, "OptionPane.isYesLast", true)));
	addButtonComponents(bottom, getButtons(context),
                            getInitialValueIndex());
        context.dispose();
	return bottom;
    }

    /**
     * Creates the appropriate object to represent each of the objects in
     * <code>buttons</code> and adds it to <code>container</code>. This
     * differs from addMessageComponents in that it will recurse on
     * <code>buttons</code> and that if button is not a Component
     * it will create an instance of JButton.
     */
    protected void addButtonComponents(Container container, Object[] buttons,
				 int initialIndex) {
	if (buttons != null && buttons.length > 0) {
	    boolean            sizeButtonsToSame = getSizeButtonsToSameWidth();
	    boolean            createdAll = true;
	    int                numButtons = buttons.length;
	    JButton[]          createdButtons = null;

	    if (sizeButtonsToSame) {
		createdButtons = new JButton[numButtons];
            }

	    for(int counter = 0; counter < numButtons; counter++) {
		Object       button = buttons[counter];
		Component    newComponent;

		if (button instanceof Component) {
		    createdAll = false;
		    newComponent = (Component)button;
		    container.add(newComponent);
		    hasCustomComponents = true;
		
		} else {
		    JButton      aButton;

                    if (button instanceof ButtonFactory) {
                        aButton = ((ButtonFactory)button).createButton();
                    }
                    else if (button instanceof Icon) {
                        aButton = new JButton((Icon)button);
                    }
                    else {
                        aButton = new JButton(button.toString());
                    }

                    aButton.setName("OptionPane.button");
		    aButton.setMultiClickThreshhold(multiClickThreshold);
                    configureButton(aButton);

		    container.add(aButton);

                    ActionListener buttonListener = createButtonActionListener(counter);
                    if (buttonListener != null) {
                        aButton.addActionListener(buttonListener);
                    }
		    newComponent = aButton;
		}
		if (sizeButtonsToSame && createdAll && 
		   (newComponent instanceof JButton)) {
		    createdButtons[counter] = (JButton)newComponent;
		}
		if (counter == initialIndex) {
		    initialFocusComponent = newComponent;
                    if (initialFocusComponent instanceof JButton) {
                        JButton defaultB = (JButton)initialFocusComponent;
                        defaultB.addAncestorListener(new AncestorListener() {
                           public void ancestorAdded(AncestorEvent e) { 
                               JButton defaultButton = (JButton)e.getComponent();
                               JRootPane root = SwingUtilities.getRootPane(defaultButton);
                               if (root != null) {
                                   root.setDefaultButton(defaultButton);
                               }
                           }
                           public void ancestorRemoved(AncestorEvent event) {}
                           public void ancestorMoved(AncestorEvent event) {}
                        });
                    }
		}
	    }
	    ((ButtonAreaLayout)container.getLayout()).
		              setSyncAllSizes((sizeButtonsToSame && createdAll));
	    /* Set the padding, windows seems to use 8 if <= 2 components,
	       otherwise 4 is used. It may actually just be the size of the
	       buttons is always the same, not sure. */
	    if (sizeButtonsToSame && createdAll) {
		JButton               aButton;
		int                   padSize;

		padSize = (numButtons <= 2? 8 : 4);

		for(int counter = 0; counter < numButtons; counter++) {
		    aButton = createdButtons[counter];
		    aButton.setMargin(new Insets(2, padSize, 2, padSize));
		}
	    }
	}
    }

    protected ActionListener createButtonActionListener(int buttonIndex) {
        return new ButtonActionListener(buttonIndex);
    }

    /**
     * Returns the buttons to display from the JOptionPane the receiver is
     * providing the look and feel for. If the JOptionPane has options
     * set, they will be provided, otherwise if the optionType is
     * YES_NO_OPTION, yesNoOptions is returned, if the type is
     * YES_NO_CANCEL_OPTION yesNoCancelOptions is returned, otherwise
     * defaultButtons are returned.
     */
    protected Object[] getButtons(SynthContext context) {
	if (optionPane != null) {
	    Object[] suppliedOptions = optionPane.getOptions();

	    if (suppliedOptions == null) {
                Object[] defaultOptions;
		int type = optionPane.getOptionType();
                Locale l = optionPane.getLocale();
                SynthStyle style = context.getStyle();
		if (type == JOptionPane.YES_NO_OPTION) {
                    defaultOptions = new ButtonFactory[2];
                    defaultOptions[0] = new ButtonFactory(
                        UIManager.getString("OptionPane.yesButtonText", l),
                        getMnemonic("OptionPane.yesButtonMnemonic", l),
                        style.getIcon(context, "OptionPane.yesIcon"));
                    defaultOptions[1] = new ButtonFactory(
                        UIManager.getString("OptionPane.noButtonText", l),
                        getMnemonic("OptionPane.noButtonMnemonic", l),
                        style.getIcon(context, "OptionPane.noIcon"));
		} else if (type == JOptionPane.YES_NO_CANCEL_OPTION) {
                    defaultOptions = new ButtonFactory[3];
                    defaultOptions[0] = new ButtonFactory(
                        UIManager.getString("OptionPane.yesButtonText", l),
                        getMnemonic("OptionPane.yesButtonMnemonic", l),
                        style.getIcon(context, "OptionPane.yesIcon"));
                    defaultOptions[1] = new ButtonFactory(
                        UIManager.getString("OptionPane.noButtonText",l),
                        getMnemonic("OptionPane.noButtonMnemonic", l),
                        style.getIcon(context, "OptionPane.noIcon"));
                    defaultOptions[2] = new ButtonFactory(
                        UIManager.getString("OptionPane.cancelButtonText",l),
                        getMnemonic("OptionPane.cancelButtonMnemonic", l),
                        style.getIcon(context, "OptionPane.cancelIcon"));
		} else if (type == JOptionPane.OK_CANCEL_OPTION) {
                    defaultOptions = new ButtonFactory[2];
                    defaultOptions[0] = new ButtonFactory(
                        UIManager.getString("OptionPane.okButtonText",l),
                        getMnemonic("OptionPane.okButtonMnemonic", l),
                        style.getIcon(context, "OptionPane.okIcon"));
                    defaultOptions[1] = new ButtonFactory(
                        UIManager.getString("OptionPane.cancelButtonText",l),
                        getMnemonic("OptionPane.cancelButtonMnemonic", l),
                        style.getIcon(context, "OptionPane.cancelIcon"));
		} else {
                    defaultOptions = new ButtonFactory[1];
                    defaultOptions[0] = new ButtonFactory(
                        UIManager.getString("OptionPane.okButtonText",l),
                        getMnemonic("OptionPane.okButtonMnemonic", l),
                        style.getIcon(context, "OptionPane.okIcon"));
                }
                return defaultOptions;
                
	    }
	    return suppliedOptions;
	}
	return null;
    }

    /**
     * Returns the mnemonic for the passed in key.
     */
    private int getMnemonic(String key, Locale l) {
        String value = (String)UIManager.get(key, l);

        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException nfe) { }
        return 0;
    }

    /**
     * Returns true, basic L&F wants all the buttons to have the same
     * width.
     */
    protected boolean getSizeButtonsToSameWidth() {
	return true;
    }

    /**
     * Returns the initial index into the buttons to select. The index
     * is calculated from the initial value from the JOptionPane and
     * options of the JOptionPane or 0.
     */
    protected int getInitialValueIndex() {
	if (optionPane != null) {
	    Object             iv = optionPane.getInitialValue();
	    Object[]           options = optionPane.getOptions();

	    if(options == null) {
		return 0;
	    }
	    else if(iv != null) {
		for(int counter = options.length - 1; counter >= 0; counter--){
		    if(options[counter].equals(iv))
			return counter;
		}
	    }
	}
	return -1;
    }

    /**
     * Sets the input value in the option pane the receiver is providing
     * the look and feel for based on the value in the inputComponent.
     */
    protected void resetInputValue() {
	if(inputComponent != null && (inputComponent instanceof JTextField)) {
	    optionPane.setInputValue(((JTextField)inputComponent).getText());

	} else if(inputComponent != null &&
                  (inputComponent instanceof JComboBox)) {
	    optionPane.setInputValue(((JComboBox)inputComponent)
				     .getSelectedItem());
	} else if(inputComponent != null) {
	    optionPane.setInputValue(((JList)inputComponent)
				     .getSelectedValue());
        }
    }


    /**
     * If inputComponent is non-null, the focus is requested on that,
     * otherwise request focus on the default value
     */
    public void selectInitialValue(JOptionPane op) {
	if (inputComponent != null)
	    inputComponent.requestFocus();
	else {
	    if (initialFocusComponent != null)
	        initialFocusComponent.requestFocus();

            if (initialFocusComponent instanceof JButton) {
                JRootPane root = SwingUtilities.getRootPane(initialFocusComponent);
                if (root != null) {
                    root.setDefaultButton((JButton)initialFocusComponent);
                }
            }
        }
    }

    /**
     * Returns true if in the last call to validateComponent the message
     * or buttons contained a subclass of Component.
     */
    public boolean containsCustomComponents(JOptionPane op) {
	return hasCustomComponents;
    }


    /**
     * <code>ButtonAreaLayout</code> behaves in a similar manner to
     * <code>FlowLayout</code>. It lays out all components from left to
     * right. If <code>syncAllSizes</code> is true, the sizes of each
     * component will be set to the largest preferred size width.
     *
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicOptionPaneUI.
     */  
    public static class ButtonAreaLayout implements LayoutManager {
	protected boolean           syncAllSizes;
	protected int               padding;
        private int orientation;
        private boolean reverseButtons;

	public ButtonAreaLayout(boolean syncAllSizes, int padding,
                                int orientation, boolean reverseButtons) {
	    this.syncAllSizes = syncAllSizes;
	    this.padding = padding;
            this.orientation = orientation;
            this.reverseButtons = reverseButtons;
	}

	public void setSyncAllSizes(boolean newValue) {
	    syncAllSizes = newValue;
	}

	public boolean getSyncAllSizes() {
	    return syncAllSizes;
	}

	public void setPadding(int newPadding) {
	    this.padding = newPadding;
	}

	public int getPadding() {
	    return padding;
	}

	public void addLayoutComponent(String string, Component comp) {
	}

        public void setOrientation(int orientation) {
            this.orientation = orientation;
        }

        public int getOrientation() {
            return orientation;
        }

        private int getOrientation(Container container) {
            if (container.getComponentOrientation().isLeftToRight()) {
                return getOrientation();
            }
            switch (getOrientation()) {
            case SwingConstants.LEFT:
                return SwingConstants.RIGHT;
            case SwingConstants.RIGHT:
                return SwingConstants.LEFT;
            case SwingConstants.CENTER:
                return SwingConstants.CENTER;
            }
            return SwingConstants.LEFT;
        }

	public void layoutContainer(Container container) {
	    Component[]      children = container.getComponents();

	    if(children != null && children.length > 0) {
		int               numChildren = children.length;
		Insets            insets = container.getInsets();
                int maxWidth = 0;
                int maxHeight = 0;
                int totalButtonWidth = 0;
                int x = 0;

                for(int counter = 0; counter < numChildren; counter++) {
                    Dimension pref = children[counter].getPreferredSize();
                    maxWidth = Math.max(maxWidth, pref.width);
                    maxHeight = Math.max(maxHeight, pref.height);
                    totalButtonWidth += pref.width;
                }
                if (getSyncAllSizes()) {
                    totalButtonWidth = maxWidth * numChildren;
                }
                totalButtonWidth += (numChildren - 1) * padding;

                switch (getOrientation(container)) {
                case SwingConstants.LEFT:
                    x = insets.left;
                    break;
                case SwingConstants.RIGHT:
                    x = container.getWidth() - insets.right - totalButtonWidth;
                    break;
                case SwingConstants.CENTER:
                    x = (container.getWidth() - totalButtonWidth) / 2;
                    break;
                }

                for (int counter = 0; counter < numChildren; counter++) {
                    int index = (reverseButtons) ? numChildren - counter - 1 :
                                counter;
                    Dimension pref = children[index].getPreferredSize();

                    if (getSyncAllSizes()) {
                        children[index].setBounds(x, insets.top,
                                                  maxWidth, maxHeight);
                    }
                    else {
                        children[index].setBounds(x, insets.top, pref.width,
                                                  pref.height);
                    }
                    x += children[index].getWidth() + padding;
                }
	    }
	}

	public Dimension minimumLayoutSize(Container c) {
	    if(c != null) {
		Component[]       children = c.getComponents();

		if(children != null && children.length > 0) {
		    Dimension     aSize;
		    int           numChildren = children.length;
		    int           height = 0;
		    Insets        cInsets = c.getInsets();
		    int           extraHeight = cInsets.top + cInsets.bottom;
		    int           extraWidth = cInsets.left + cInsets.right;

		    if (syncAllSizes) {
			int              maxWidth = 0;

			for(int counter = 0; counter < numChildren; counter++){
			    aSize = children[counter].getPreferredSize();
			    height = Math.max(height, aSize.height);
			    maxWidth = Math.max(maxWidth, aSize.width);
			}
			return new Dimension(extraWidth + (maxWidth * numChildren) + 
					     (numChildren - 1) * padding,
					     extraHeight + height);
		    }
		    else {
			int        totalWidth = 0;

			for(int counter = 0; counter < numChildren; counter++){
			    aSize = children[counter].getPreferredSize();
			    height = Math.max(height, aSize.height);
			    totalWidth += aSize.width;
			}
			totalWidth += ((numChildren - 1) * padding);
			return new Dimension(extraWidth + totalWidth, extraHeight + height);
		    }
		}
	    }
	    return new Dimension(0, 0);
	}

	public Dimension preferredLayoutSize(Container c) {
	    return minimumLayoutSize(c);
	}

	public void removeLayoutComponent(Component c) { }
    }


    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicOptionPaneUI.
     */  
    class PropertyChangeHandler implements PropertyChangeListener {
        /**
         * If the source of the PropertyChangeEvent <code>e</code> equals the
         * optionPane and is one of the ICON_PROPERTY, MESSAGE_PROPERTY,
         * OPTIONS_PROPERTY or INITIAL_VALUE_PROPERTY,
         * validateComponent is invoked.
         */
        public void propertyChange(PropertyChangeEvent e) {
	    if(e.getSource() == optionPane) {
                if (SynthLookAndFeel.shouldUpdateStyle(e)) {
                    fetchStyle(optionPane);
                }
		// Option Pane Auditory Cue Activation
		// only respond to "ancestor" changes
		// the idea being that a JOptionPane gets a JDialog when it is 
		// set to appear and loses it's JDialog when it is dismissed.
		if ("ancestor" == e.getPropertyName()) {
		    JOptionPane op = (JOptionPane)e.getSource();
		    boolean isComingUp;
		    
		    // if the old value is null, then the JOptionPane is being
		    // created since it didn't previously have an ancestor.
		    if (e.getOldValue() == null) {
			isComingUp = true;
		    } else {
			isComingUp = false;
		    }
		    
		    // figure out what to do based on the message type
		    switch (op.getMessageType()) {
		    case JOptionPane.PLAIN_MESSAGE:
			if (isComingUp) {
			    SynthLookAndFeel.playSound(optionPane,
                                             "OptionPane.informationSound");
			}
			break;
		    case JOptionPane.QUESTION_MESSAGE:
			if (isComingUp) {
			    SynthLookAndFeel.playSound(optionPane,
                                             "OptionPane.questionSound");
			}
			break;
		    case JOptionPane.INFORMATION_MESSAGE:
			if (isComingUp) {
			    SynthLookAndFeel.playSound(optionPane,
                                             "OptionPane.informationSound");
			}
			break;
		    case JOptionPane.WARNING_MESSAGE:
			if (isComingUp) {
			    SynthLookAndFeel.playSound(optionPane,
                                             "OptionPane.warningSound");
			}
			break;
		    case JOptionPane.ERROR_MESSAGE:
			if (isComingUp) {
			    SynthLookAndFeel.playSound(optionPane,
                                             "OptionPane.errorSound");
			}
			break;
		    default:
			System.err.println("Undefined JOptionPane type: " +
					   op.getMessageType());
			break;
		    }
		}
		// Visual activity
	        String         changeName = e.getPropertyName();

	        if(changeName.equals(JOptionPane.OPTIONS_PROPERTY) ||
    	           changeName.equals(JOptionPane.INITIAL_VALUE_PROPERTY) ||
	           changeName.equals(JOptionPane.ICON_PROPERTY) ||
	           changeName.equals(JOptionPane.MESSAGE_TYPE_PROPERTY) ||
	           changeName.equals(JOptionPane.OPTION_TYPE_PROPERTY) ||
	           changeName.equals(JOptionPane.MESSAGE_PROPERTY) ||
	           changeName.equals(JOptionPane.SELECTION_VALUES_PROPERTY) ||
	           changeName.equals(JOptionPane.INITIAL_SELECTION_VALUE_PROPERTY) ||
	           changeName.equals(JOptionPane.WANTS_INPUT_PROPERTY)) {
                   uninstallComponents();
                   installComponents();
                   optionPane.validate();
                }
		else if (changeName.equals("componentOrientation")) {
		    ComponentOrientation o = (ComponentOrientation)e.getNewValue();
		    JOptionPane op = (JOptionPane)e.getSource();
		    if (o != (ComponentOrientation)e.getOldValue()) {
			op.applyComponentOrientation(o);
		    }
		}
            }
	}
    }

    /**
     * Configures any necessary colors/fonts for the specified label
     * used representing the message.
     */
    private void configureMessageLabel(JLabel label) {
    }

    /**
     * Configures any necessary colors/fonts for the specified button
     * used representing the button portion of the optionpane.
     */
    private void configureButton(JButton button) {
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicOptionPaneUI.
     */  
    class ButtonActionListener implements ActionListener {
        protected int buttonIndex;

        public ButtonActionListener(int buttonIndex) {
            this.buttonIndex = buttonIndex;
        }

        public void actionPerformed(ActionEvent e) {
	    if (optionPane != null) {
   	        int optionType = optionPane.getOptionType();
	        Object[] options = optionPane.getOptions();

                /* If the option pane takes input, then store the input value
                 * if custom options were specified, if the option type is
                 * DEFAULT_OPTION, OR if option type is set to a predefined
                 * one and the user chose the affirmative answer.
                 */
 		if (inputComponent != null) {
                    if (options != null ||
                        optionType == JOptionPane.DEFAULT_OPTION ||
      		        ((optionType == JOptionPane.YES_NO_OPTION ||
 		         optionType == JOptionPane.YES_NO_CANCEL_OPTION ||
 		         optionType == JOptionPane.OK_CANCEL_OPTION) &&
 		         buttonIndex == 0)) {
 		        resetInputValue();
                    }
                }
	        if (options == null) {
		    if (optionType == JOptionPane.OK_CANCEL_OPTION &&
                        buttonIndex == 1) {
		        optionPane.setValue(new Integer(2));
                    
		    } else {
		        optionPane.setValue(new Integer(buttonIndex));
                    }
	        } else {
		    optionPane.setValue(options[buttonIndex]);
                }
	    }
        }
    }


    //
    // Classed used when optionPane.getWantsInput returns true.
    //

    /**
     * Listener when a JList is created to handle input from the user.
     */
    private class ListSelectionListener extends MouseAdapter
    {
	public void mousePressed(MouseEvent e) {
	    if (e.getClickCount() == 2) {
		JList     list = (JList)e.getSource();
		int       index = list.locationToIndex(e.getPoint());

		optionPane.setInputValue(list.getModel().getElementAt(index));
	    }
	}
    }

    /**
     * Listener when a JTextField is created to handle input from the user.
     */
    private class TextFieldActionListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) {
	    optionPane.setInputValue(((JTextField)e.getSource()).getText());
	}
    }


    /**
     * A JTextField that allows you to specify an array of KeyStrokes that
     * that will have their bindings processed regardless of whether or
     * not they are registered on the JTextField. This is used as we really
     * want the ActionListener to be notified so that we can push the
     * change to the JOptionPane, but we also want additional bindings
     * (those of the JRootPane) to be processed as well.
     */
    private static class MultiplexingTextField extends JTextField {
        private KeyStroke[] strokes;

        MultiplexingTextField(int cols) {
            super(cols);
        }

        /**
         * Sets the KeyStrokes that will be additional processed for
         * ancestor bindings.
         */
        void setKeyStrokes(KeyStroke[] strokes) {
            this.strokes = strokes;
        }

        protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
                                            int condition, boolean pressed) {
            boolean processed = super.processKeyBinding(ks, e, condition,
                                                        pressed);

            if (processed && condition != JComponent.WHEN_IN_FOCUSED_WINDOW) {
                for (int counter = strokes.length - 1; counter >= 0;
                         counter--) {
                    if (strokes[counter].equals(ks)) {
                        // Returning false will allow further processing
                        // of the bindings, eg our parent Containers will get a
                        // crack at them.
                        return false;
                    }
                }
            }
            return processed;
        }
    }



    // REMIND(aim,7/29/98): These actions should be broken
    // out into protected inner classes in the next release where
    // API changes are allowed
    /**
     * Registered in the ActionMap. Sets the value of the option pane
     * to <code>JOptionPane.CLOSED_OPTION</code>.
     */
    private static class CloseAction extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
	    JOptionPane optionPane = (JOptionPane)e.getSource();

	    optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
	}
    }


    /**
     * This class is used to create the default buttons.
     */
    private static class ButtonFactory {
        private String text;
        private int mnemonic;
        private Icon icon;

        ButtonFactory(String text, int mnemonic, Icon icon) {
            this.text = text;
            this.mnemonic = mnemonic;
            this.icon = icon;
        }

        JButton createButton() {
            JButton button = new JButton(text);
            button.setIcon(icon);
            if (mnemonic != 0) {
                button.setMnemonic(mnemonic);
            }
            return button;
        }
    }
}

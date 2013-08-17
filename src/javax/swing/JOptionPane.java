/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;
import javax.swing.plaf.OptionPaneUI;
import javax.accessibility.*;

/**
 * <code>JOptionPane</code> makes it easy to pop up a standard dialog box that
 * prompts users for a value or informs them of something.
 * For information about using <code>JOptionPane</code>, see
 * <a
 href="http://java.sun.com/docs/books/tutorial/uiswing/components/dialog.html">How to Make Dialogs</a>,
 * a section in <em>The Java Tutorial</em>.
 *
 * <p>
 *
 * While the <code>JOptionPane</code>
 * class may appear complex because of the large number of methods, almost
 * all uses of this class are one-line calls to one of the static 
 * <code>showXxxDialog</code> methods shown below:
 * <blockquote>
 * <table>
 * <tr align=top><td>showConfirmDialog<td>Asks a confirming question, 
 *                   like yes/no/cancel.
 * <tr align=top><td>showInputDialog<td>Prompt for some input.
 * <tr align=top><td>showMessageDialog<td>Tell the user about something 
 *                                        that has happened.
 * <tr align=top><td>showOptionDialog<td>The Grand Unification of the above three.
 * </table>
 * </blockquote>
 * Each of these methods also comes in a <code>showInternalXXX</code>
 * flavor, which uses an internal frame to hold the dialog box (see
 * {@link JInternalFrame}).
 * Multiple convenience methods have also been defined -- overloaded 
 * versions of the basic methods that use different parameter lists.
 * <p>
 * All dialogs are modal. Each <code>showXxxDialog</code> method blocks 
 * the current thread until the user's interaction is complete.
 * <p>
 * <table cellspacing=6 cellpadding=4 border=0 align=right>
 * <tr>
 * <td bgcolor=#FFe0d0 rowspan=2>
 * icon
 * <td bgcolor=#FFe0d0>
 * message
 * <tr>
 * <td bgcolor=#FFe0d0>
 * input value
 * <tr>
 * <td bgcolor=#FFe0d0 colspan=2>
 * option buttons
 * </table>
 * The basic appearance of one of these dialog boxes is generally
 * similar to the picture at the right, although the various
 * look-and-feels are
 * ultimatly responsible for the final result.
 * <br clear=all>
 * <p>
 * <b>Parameters:</b><br>
 * The parameters to these methods follow consistent patterns:
 * <blockquote>
 * <dl compact>
 * <dt>parentComponent<dd>
 * Defines the <code>Component</code> that is to be the parent of this
 * dialog box.
 * It is used in two ways: the <code>Frame</code> that contains
 * it is used as the <code>Frame</code>
 * parent for the dialog box, and its screen coordinates are used in
 * the placement of the dialog box. In general, the dialog box is placed
 * just below the component. This parameter may be <code>null</code>,
 * in which case a default <code>Frame</code> is used as the parent,
 * and the dialog will be
 * centered on the screen (depending on the L&F).
 * <dt><a name=message>message</a><dd>
 * A descriptive message to be placed in the dialog box.
 * In the most common usage, message is just a <code>String</code> or
 * <code>String</code> constant.
 * However, the type of this parameter is actually <code>Object</code>. Its 
 * interpretation depends on its type:
 * <dl compact>
 * <dt>Object[]<dd>An array of objects is interpreted as a series of
 *                 messages (one per object) arranged in a vertical stack.
 *                 The interpretation is recursive -- each object in the
 *                 array is interpreted according to its type. 
 * <dt>Component<dd>The <code>Component</code> is displayed in the dialog.
 * <dt>Icon<dd>The <code>Icon</code> is wrapped in a <code>JLabel</code>
 *		 and displayed in the dialog.
 * <dt>others<dd>The object is converted to a <code>String</code> by calling 
 *               its <code>toString</code> method. The result is wrapped in a
 *               <code>JLabel</code> and displayed.
 * </dl>
 * <dt>messageType<dd>Defines the style of the message. The Look and Feel
 * manager may lay out the dialog differently depending on this value, and
 * will often provide a default icon. The possible values are:
 * <ul>
 * <li>ERROR_MESSAGE
 * <li>INFORMATION_MESSAGE
 * <li>WARNING_MESSAGE
 * <li>QUESTION_MESSAGE
 * <li>PLAIN_MESSAGE
 * </ul>
 * <dt>optionType<dd>Defines the set of option buttons that appear at
 * the bottom of the dialog box:
 * <ul>
 * <li>DEFAULT_OPTION
 * <li>YES_NO_OPTION
 * <li>YES_NO_CANCEL_OPTION
 * <li>OK_CANCEL_OPTION
 * </ul>
 * You aren't limited to this set of option buttons.  You can provide any
 * buttons you want using the options parameter.
 * <dt>options<dd>A more detailed description of the set of option buttons
 * that will appear at the bottom of the dialog box. 
 * The usual value for the options parameter is an array of
 * <code>String</code>s. But 
 * the parameter type is an array of <code>Objects</code>.
 * A button is created for each object depending on its type:
 * <dl compact>
 * <dt>Component<dd>The component is added to the button row directly.
 * <dt>Icon<dd>A <code>JButton</code> is created with this as its label.
 * <dt>other<dd>The <code>Object</code> is converted to a string using its
 *              <code>toString</code> method and the result is used to
 *              label a <code>JButton</code>.
 * </dl>
 * <dt>icon<dd>A decorative icon to be placed in the dialog box. A default
 * value for this is determined by the <code>messageType</code> parameter.
 * <dt>title<dd>The title for the dialog box.
 * <dt>initialValue<dd>The default selection (input value).
 * </dl>
 * </blockquote>
 * <p>
 * When the selection is changed, <code>setValue</code> is invoked,
 * which generates a <code>PropertyChangeEvent</code>.
 * <p>
 * If a <code>JOptionPane</code> has configured to all input
 * <code>setWantsInput</code>
 * the bound property <code>JOptionPane.INPUT_VALUE_PROPERTY</code>
 *  can also be listened
 * to, to determine when the user has input or selected a value.
 * <p>
 * When one of the <code>showXxxDialog</code> methods returns an integer, 
 * the possible values are:<pre>
 *     YES_OPTION,
 *     NO_OPTION,
 *     CANCEL_OPTION,
 *     OK_OPTION, or
 *     CLOSED_OPTION.
 * </pre>
 * <b>Examples:</b>
 * <dl>
 * <dt>Show an error dialog that displays the message, 'alert':
 * <dd><code>
 * JOptionPane.showMessageDialog(null, "alert", "alert", JOptionPane.ERROR_MESSAGE);
 * </code><p>
 * <dt>Show an internal information dialog with the message, 'information':
 * <dd><code>
 * JOptionPane.showInternalMessageDialog(frame, "information",<br>
 *             <ul><ul>"information", JOptionPane.INFORMATION_MESSAGE);</ul></ul>
 * </code><p>
 * <dt>Show an information panel with the options yes/no and message 'choose one':
 * <dd><code>JOptionPane.showConfirmDialog(null,
 *             <ul><ul>"choose one", "choose one", JOptionPane.YES_NO_OPTION);</ul></ul>
 * </code><p>
 * <dt>Show an internal information dialog with the options yes/no/cancel and
 * message 'please choose one' and title information:
 * <dd><code>JOptionPane.showInternalConfirmDialog(frame,
 *             <ul><ul>"please choose one", "information",</ul></ul>
 *             <ul><ul>JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);</ul></ul>
 * </code><p>
 * <dt>Show a warning dialog with the options OK, CANCEL, title 'Warning', and
 * message 'Click OK to continue':
 * <dd><code>
 * Object[] options = { "OK", "CANCEL" };<br>
 * JOptionPane.showOptionDialog(null, "Click OK to continue", "Warning",
 *             <ul><ul>JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,</ul></ul>
 *             <ul><ul>null, options, options[0]);</ul></ul>
 * </code><p>
 * <dt>Show a dialog asking the user to type in a String:
 * <dd><code>
 * String inputValue = JOptionPane.showInputDialog("Please input a value");
 * </code><p>
 * <dt>Show a dialog asking the user to select a String:
 * <dd><code>
 * Object[] possibleValues = { "First", "Second", "Third" };<br>
 * Object selectedValue = JOptionPane.showInputDialog(null,
 *             <ul><ul>"Choose one", "Input",</ul></ul>
 *             <ul><ul>JOptionPane.INFORMATION_MESSAGE, null,</ul></ul>
 *             <ul><ul>possibleValues, possibleValues[0]);</ul></ul>
 * </code><p>
 * </dl>
 * <b>Direct Use:</b><br>
 * To create and use an <code>JOptionPane</code> directly, the
 * standard pattern is roughly as follows:
 * <pre>
 *     JOptionPane pane = new JOptionPane(<i>arguments</i>);
 *     pane.set<i>.Xxxx(...); // Configure</i>
 *     JDialog dialog = pane.createDialog(<i>parentComponent, title</i>);
 *     dialog.show();
 *     Object selectedValue = pane.getValue();
 *     if(selectedValue == null)
 *       return CLOSED_OPTION;
 *     <i>//If there is <b>not</b> an array of option buttons:</i>
 *     if(options == null) {
 *       if(selectedValue instanceof Integer)
 *          return ((Integer)selectedValue).intValue();
 *       return CLOSED_OPTION;
 *     }
 *     <i>//If there is an array of option buttons:</i>
 *     for(int counter = 0, maxCounter = options.length;
 *        counter < maxCounter; counter++) {
 *        if(options[counter].equals(selectedValue))
 *        return counter;
 *     }
 *     return CLOSED_OPTION;
 * </pre>
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JOptionPane">JOptionPane</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see JInternalFrame
 *
 * @beaninfo
 *      attribute: isContainer true
 *    description: A component which implements standard dialog box controls.
 *
 * @version 1.62 02/06/02
 * @author James Gosling
 * @author Scott Violet
 */
public class JOptionPane extends JComponent implements Accessible
{
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "OptionPaneUI";

    /**
     * Indicates that the user has not yet selected a value.
     */
    public static final Object      UNINITIALIZED_VALUE = "uninitializedValue";

    //
    // Option types
    //
    /** 
     * Type meaning Look and Feel should not supply any options -- only
     * use the options from the JOptionPane.
     */
    public static final int         DEFAULT_OPTION = -1;
    /** Type used for showConfirmDialog. */
    public static final int         YES_NO_OPTION = 0;
    /** Type used for showConfirmDialog. */
    public static final int         YES_NO_CANCEL_OPTION = 1;
    /** Type used for showConfirmDialog. */
    public static final int         OK_CANCEL_OPTION = 2;

    //
    // Return values.
    //
    /** Return value from class method if YES is chosen. */
    public static final int         YES_OPTION = 0;
    /** Return value from class method if NO is chosen. */
    public static final int         NO_OPTION = 1;
    /** Return value from class method if CANCEL is chosen. */
    public static final int         CANCEL_OPTION = 2;
    /** Return value form class method if OK is chosen. */
    public static final int         OK_OPTION = 0;
    /** Return value from class method if user closes window without selecting
     * anything, more than likely this should be treated as either a
     * CANCEL_OPTION or NO_OPTION. */
    public static final int         CLOSED_OPTION = -1;

    //
    // Message types. Used by the UI to determine what icon to display,
    // and possibly what behavior to give based on the type.
    //
    /** Used for error messages. */
    public static final int  ERROR_MESSAGE = 0;
    /** Used for information messages. */
    public static final int  INFORMATION_MESSAGE = 1;
    /** Used for warning messages. */
    public static final int  WARNING_MESSAGE = 2;
    /** Used for questions. */
    public static final int  QUESTION_MESSAGE = 3;
    /** No icon is used. */
    public static final int   PLAIN_MESSAGE = -1;

    /** Bound property name for icon. */
    public static final String      ICON_PROPERTY = "icon";
    /** Bound property name for message. */
    public static final String      MESSAGE_PROPERTY = "message";
    /** Bounds property name for value. */
    public static final String      VALUE_PROPERTY = "value";
    /** Bounds property namer for option. */
    public static final String      OPTIONS_PROPERTY = "options";
    /** Bounds property name for initialValue. */
    public static final String      INITIAL_VALUE_PROPERTY = "initialValue";
    /** Bounds property name for type. */
    public static final String      MESSAGE_TYPE_PROPERTY = "messageType";
    /** Bound property name for optionType. */
    public static final String      OPTION_TYPE_PROPERTY = "optionType";
    /** Bound property name for selectionValues. */
    public static final String      SELECTION_VALUES_PROPERTY = "selectionValues";
    /** Bound property name for initialSelectionValue. */
    public static final String      INITIAL_SELECTION_VALUE_PROPERTY = "initialSelectionValue";
    /** Bound property name for inputValue. */
    public static final String      INPUT_VALUE_PROPERTY = "inputValue";
    /** Bound property name for wantsInput. */
    public static final String      WANTS_INPUT_PROPERTY = "wantsInput";

    /** Icon used in pane. */
    transient protected Icon                  icon;
    /** Message to display. */
    transient protected Object                message;
    /** Options to display to the user. */
    transient protected Object[]              options;
    /** Value that should be initialy selected in options. */
    transient protected Object                initialValue;
    /** Message type. */
    protected int                   messageType;
    /** Option type, one of DEFAULT_OPTION, YES_NO_OPTION,
     * YES_NO_CANCEL_OPTION or OK_CANCEL_OPTION. */
    protected int                   optionType;
    /** Currently selected value, will be a valid option, or
     * UNINITIALIZED_VALUE or <code>null</code>. */
    transient protected Object                value;
    /** Array of values the user can choose from. Look and feel will
     * provide the UI component to choose this from. */
    protected transient Object[]              selectionValues;
    /** Value the user has input. */
    protected transient Object                inputValue;
    /** Initial value to select in selectionValues. */
    protected transient Object                initialSelectionValue;
    /** If true, a UI widget will be provided to the user to get input. */
    protected boolean                         wantsInput;


    /**
     * Shows a question-message dialog requesting input from the user. The 
     * dialog uses the default frame, which usually means it is centered on 
     * the screen. 
     *
     * @param message the <code>Object</code> to display
     */
    public static String showInputDialog(Object message) {
        return showInputDialog(null, message);
    }

    /**
     * Shows a question-message dialog requesting input from the user
     * parented to <code>parentComponent</code>.
     * The dialog is displayed in the Component's
     * frame, and is usually positioned below the <code>Component</code>. 
     *
     * @param parentComponent  the parent <code>Component</code> for the
     *		dialog
     * @param message  the <code>Object</code> to display
     */
    public static String showInputDialog(Component parentComponent, Object message){
        return showInputDialog(parentComponent, message, "Input", QUESTION_MESSAGE);
    }

    /**
     * Shows a dialog requesting input from the user parented to
     * <code>parentComponent</code> with the dialog having the title
     * <code>title</code> and message type <code>messageType</code>.
     *
     * @param parentComponent  the parent <code>Component</code> for the
     *			dialog
     * @param message  the <code>Object</code> to display
     * @param title    the <code>String</code> to display in the dialog
     *			title bar
     * @param messageType the type of message that is to be displayed:
     *                 ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *                 QUESTION_MESSAGE, or PLAIN_MESSAGE
     */
    public static String showInputDialog(Component parentComponent, Object message,
                                         String title, int messageType) {
        return (String)showInputDialog(parentComponent, message, title,
                                       messageType, null, null, null);
    }

    /**
     * Prompts the user for input in a blocking dialog where the
     * initial selection, possible selections, and all other options can
     * be specified. The user will able to choose from
     * <code>selectionValues</code>, where <code>null</code> implies the
     * user can input
     * whatever they wish, usually by means of a <code>JTextField</code>. 
     * <code>initialSelectionValue</code> is the initial value to prompt
     * the user with. It is up to the UI to decide how best to represent
     * the <code>selectionValues</code>, but usually a
     * <code>JComboBox</code>, <code>JList</code>, or
     * <code>JTextField</code> will be used.
     *
     * @param parentComponent  the parent <code>Component</code> for the
     *			dialog
     * @param message  the <code>Object</code> to display
     * @param title    the <code>String</code> to display in the
     *			dialog title bar
     * @param messageType the type of message to be displayed:
     *                  ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *                  QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @param icon     the Icon image to display
     * @param selectionValues an array of <code>Object</code>s that
     *			gives the possible selections
     * @param initialSelectionValue the value used to initialize the input
     *                 field
     * @return users input, or <code>null</code> meaning the user
     *			cancelled the input
     */
    public static Object showInputDialog(Component parentComponent, Object message,
                      String title, int messageType, Icon icon,
                      Object[] selectionValues, Object initialSelectionValue) {
        JOptionPane    pane = new JOptionPane(message, messageType,
                                              OK_CANCEL_OPTION, icon,
                                              null, null);

        pane.setWantsInput(true);
        pane.setSelectionValues(selectionValues);
        pane.setInitialSelectionValue(initialSelectionValue);

        JDialog        dialog = pane.createDialog(parentComponent, title);

        pane.selectInitialValue();
        dialog.show();

        Object value = pane.getInputValue();

        if(value == UNINITIALIZED_VALUE)
            return null;
        return value;
    }

    /**
     * Brings up a modal information-message dialog titled "Message".
     *
     * @param parentComponent determines the <code>Frame</code> in
     *		which the dialog is displayed; if <code>null</code>,
     *		or if the <code>parentComponent</code> has no
     *		<code>Frame</code>, a default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     */
    public static void showMessageDialog(Component parentComponent, Object message) {
        showMessageDialog(parentComponent, message, "Message", INFORMATION_MESSAGE);
    }

    /**
     * Brings up a dialog that displays a message using a default
     * icon determined by the <code>messageType</code> parameter.
     *
     * @param parentComponent determines the <code>Frame</code>
     *		in which the dialog is displayed; if <code>null</code>,
     *		or if the <code>parentComponent</code> has no
     *		<code>Frame</code>, a default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @param title     the title string for the dialog
     * @param messageType the type of message to be displayed:
     *                    ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *                    QUESTION_MESSAGE, or PLAIN_MESSAGE
     */
    public static void showMessageDialog(Component parentComponent, Object message,
                                         String title, int messageType) {
        showMessageDialog(parentComponent, message, title, messageType, null);
    }

    /**
     * Brings up a dialog displaying a message, specifying all parameters.
     *
     * @param parentComponent determines the <code>Frame</code> in which the
     *			dialog is displayed; if <code>null</code>,
     *			or if the <code>parentComponent</code> has no
     *			<code>Frame</code>, a 
     *                  default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @param title     the title string for the dialog
     * @param messageType the type of message to be displayed:
     *                    ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *                    QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @param icon      an icon to display in the dialog that helps the user
     *                  identify the kind of message that is being displayed
     */
    public static void showMessageDialog(Component parentComponent, Object message,
                                         String title, int messageType,
                                         Icon icon){
        showOptionDialog(parentComponent, message, title, DEFAULT_OPTION, 
                         messageType, icon, null, null);
    }

    /**
     * Brings up a modal dialog with the options Yes, No and Cancel; with the
     * title, "Select an Option".
     *
     * @param parentComponent determines the <code>Frame</code> in which the
     *			dialog is displayed; if <code>null</code>,
     *			or if the <code>parentComponent</code> has no
     *			<code>Frame</code>, a 
     *                  default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @return an integer indicating the option selected by the user
     */
    public static int showConfirmDialog(Component parentComponent, Object message) {
        return showConfirmDialog(parentComponent, message,
            UIManager.getString("OptionPane.titleText"), YES_NO_CANCEL_OPTION);
    }

    /**
     * Brings up a modal dialog where the number of choices is determined
     * by the <code>optionType</code> parameter.
     * 
     * @param parentComponent determines the <code>Frame</code> in which the
     *			dialog is displayed; if <code>null</code>,
     *			or if the <code>parentComponent</code> has no
     *			<code>Frame</code>, a 
     *                  default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @param title     the title string for the dialog
     * @param optionType an int designating the options available on the dialog:
     *                   YES_NO_OPTION, or YES_NO_CANCEL_OPTION
     * @return an int indicating the option selected by the user
     */
    public static int showConfirmDialog(Component parentComponent, Object message,
                                        String title, int optionType) {
        return showConfirmDialog(parentComponent, message, title, optionType,
                                 QUESTION_MESSAGE);
    }

    /**
     * Brings up a modal dialog where the number of choices is determined
     * by the <code>optionType</code> parameter, where the
     * <code>messageType</code>
     * parameter determines the icon to display.
     * The <code>messageType</code> parameter is primarily used to supply
     * a default icon from the Look and Feel.
     *
     * @param parentComponent determines the <code>Frame</code> in
     *			which the dialog is displayed; if <code>null</code>,
     *			or if the <code>parentComponent</code> has no
     *			<code>Frame</code>, a 
     *                  default <code>Frame</code> is used.
     * @param message   the <code>Object</code> to display
     * @param title     the title string for the dialog
     * @param optionType an integer designating the options available
     *			on the dialog:
     *                  YES_NO_OPTION, or YES_NO_CANCEL_OPTION
     * @param messageType an integer designating the kind of message this is, 
     *                  primarily used to determine the icon from the
     *			pluggable
     *                  Look and Feel: ERROR_MESSAGE, INFORMATION_MESSAGE, 
     *                  WARNING_MESSAGE, QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @return an integer indicating the option selected by the user
     */
    public static int showConfirmDialog(Component parentComponent, Object message,
                                        String title, int optionType,
                                        int messageType) {
        return showConfirmDialog(parentComponent, message, title, optionType,
                                messageType, null);
    }

    /**
     * Brings up a modal dialog with a specified icon, where the number of 
     * choices is determined by the <code>optionType</code> parameter.
     * The <code>messageType</code> parameter is primarily used to supply
     * a default icon from the Look and Feel.
     *
     * @param parentComponent determines the <code>Frame</code> in which the
     *			dialog is displayed; if <code>null</code>,
     *			or if the <code>parentComponent</code> has no
     *			<code>Frame</code>, a 
     *			default <code>Frame</code> is used
     * @param message   The Object to display
     * @param title     the title string for the dialog
     * @param optionType an int designating the options available on the dialog:
     *                   YES_NO_OPTION, or YES_NO_CANCEL_OPTION
     * @param messageType an int designating the kind of message this is, 
     *                    primarily used to determine the icon from the pluggable
     *                    Look and Feel: ERROR_MESSAGE, INFORMATION_MESSAGE, 
     *                    WARNING_MESSAGE, QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @param icon      the icon to display in the dialog
     * @return an int indicating the option selected by the user
     */
    public static int showConfirmDialog(Component parentComponent, Object message,
                                        String title, int optionType,
                                        int messageType, Icon icon) {
        return showOptionDialog(parentComponent, message, title, optionType,
                                messageType, icon, null, null);
    }

    /**
     * Brings up a modal dialog with a specified icon, where the initial
     * choice is dermined by the <code>initialValue</code> parameter and
     * the number of choices is determined by the <code>optionType</code> 
     * parameter.
     * <p>
     * If <code>optionType</code> is YES_NO_OPTION, or YES_NO_CANCEL_OPTION
     * and the <code>options</code> parameter is <code>null</code>,
     * then the options are
     * supplied by the Look and Feel. 
     * <p>
     * The <code>messageType</code> parameter is primarily used to supply
     * a default icon from the Look and Feel.
     *
     * @param parentComponent determines the <code>Frame</code>
     *			in which the dialog is displayed;  if 
     *                  <code>null</code>, or if the
     *			<code>parentComponent</code> has no
     *			<code>Frame</code>, a 
     *                  default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @param title     the title string for the dialog
     * @param optionType an integer designating the options available on the
     *			dialog: YES_NO_OPTION, or YES_NO_CANCEL_OPTION
     * @param messageType an integer designating the kind of message this is, 
     *                  primarily used to determine the icon from the
     *			pluggable
     *                  Look and Feel: ERROR_MESSAGE, INFORMATION_MESSAGE, 
     *                  WARNING_MESSAGE, QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @param icon      the icon to display in the dialog
     * @param options   an array of objects indicating the possible choices
     *                  the user can make; if the objects are components, they
     *                  are rendered properly; non-<code>String</code>
     *			objects are
     *                  rendered using their <code>toString</code> methods;
     *                  if this parameter is <code>null</code>,
     *			the options are determined by the Look and Feel.
     * @param initialValue the object that represents the default selection
     *                     for the dialog
     * @return an integer indicating the option chosen by the user, 
     *         or CLOSED_OPTION if the user closed the Dialog
     */
    public static int showOptionDialog(Component parentComponent, Object message,
                                       String title, int optionType,
                                       int messageType, Icon icon,
                                       Object[] options, Object initialValue) {
        JOptionPane             pane = new JOptionPane(message, messageType,
                                                       optionType, icon,
                                                       options, initialValue);

        pane.setInitialValue(initialValue);

        JDialog         dialog = pane.createDialog(parentComponent, title);

        pane.selectInitialValue();
        dialog.show();

        Object        selectedValue = pane.getValue();

        if(selectedValue == null)
            return CLOSED_OPTION;
        if(options == null) {
            if(selectedValue instanceof Integer)
                return ((Integer)selectedValue).intValue();
            return CLOSED_OPTION;
        }
        for(int counter = 0, maxCounter = options.length;
            counter < maxCounter; counter++) {
            if(options[counter].equals(selectedValue))
                return counter;
        }
        return CLOSED_OPTION;
    }

    /**
     * Creates and returns a new <code>JDialog</code> wrapping
     * <code>this</code> centered on the <code>parentComponent</code>
     * in the <code>parentComponent</code>'s frame.
     * <code>title</code> is the title of the returned dialog.
     * The returned <code>JDialog</code> will be set up such that
     * once it is closed, or the user clicks on the OK button,
     * the dialog will be disposed and closed.
     *
     * @param parentComponent determines the frame in which the dialog
     *		is displayed; if the <code>parentComponent</code> has
     *		no <code>Frame</code>, a default <code>Frame</code> is used
     * @param title     the title string for the dialog
     * @return a new <code>JDialog</code> containing this instance
     */
    public JDialog createDialog(Component parentComponent, String title) {

        final JDialog dialog;

        Window window = JOptionPane.getWindowForComponent(parentComponent);
        if (window instanceof Frame) {
            dialog = new JDialog((Frame)window, title, true);	
        } else {
            dialog = new JDialog((Dialog)window, title, true);
        }		

        Container             contentPane = dialog.getContentPane();

        contentPane.setLayout(new BorderLayout());
        contentPane.add(this, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(parentComponent);
        dialog.addWindowListener(new WindowAdapter() {
            boolean gotFocus = false;
            public void windowClosing(WindowEvent we) {
                setValue(null);
            }
            public void windowActivated(WindowEvent we) {
                // Once window gets focus, set initial focus
                if (!gotFocus) {
                    selectInitialValue();
                    gotFocus = true;
                }
            }
        });
        addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if(dialog.isVisible() && event.getSource() == JOptionPane.this &&
                   (event.getPropertyName().equals(VALUE_PROPERTY) ||
                    event.getPropertyName().equals(INPUT_VALUE_PROPERTY))) {
                    dialog.setVisible(false);
                    dialog.dispose();
                }
            }
        });
        return dialog;
    }
        

    /**
     * Brings up an internal confirmation dialog panel. The dialog
     * is a modal information-message dialog titled "Message".
     *
     * @param parentComponent determines the <code>Frame</code>
     *		in which the dialog is displayed; if <code>null</code>,
     *		or if the <code>parentComponent</code> has no
     *		<code>Frame</code>, a default <code>Frame</code> is used
     * @param message   the object to display
     */
    public static void showInternalMessageDialog(Component parentComponent,
                                                 Object message) {
        showInternalMessageDialog(parentComponent, message, "Message",
                                  INFORMATION_MESSAGE);
    }

    /** 
     * Brings up an internal dialog panel that displays a message 
     * using a default icon determined by the <code>messageType</code>
     * parameter.
     *
     * @param parentComponent determines the <code>Frame</code>
     *		in which the dialog is displayed; if <code>null</code>,
     *		or if the <code>parentComponent</code> has no
     *		<code>Frame</code>, a default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @param title     the title string for the dialog
     * @param messageType the type of message to be displayed:
     *                    ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *                    QUESTION_MESSAGE, or PLAIN_MESSAGE
     */
    public static void showInternalMessageDialog(Component parentComponent,
                                                 Object message, String title,
                                                 int messageType) {
        showInternalMessageDialog(parentComponent, message, title, messageType,null);
    }

    /**
     * Brings up an internal dialog panel displaying a message, 
     * specifying all parameters.
     *
     * @param parentComponent determines the <code>Frame</code>
     *		in which the dialog is displayed; if <code>null</code>,
     *		or if the <code>parentComponent</code> has no
     *		<code>Frame</code>, a default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @param title     the title string for the dialog
     * @param messageType the type of message to be displayed:
     *                    ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *                    QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @param icon      an icon to display in the dialog that helps the user
     *                  identify the kind of message that is being displayed
     */
    public static void showInternalMessageDialog(Component parentComponent,
                                         Object message,
                                         String title, int messageType,
                                         Icon icon){
        showInternalOptionDialog(parentComponent, message, title, DEFAULT_OPTION,
                                 messageType, icon, null, null);
    }

    /**
     * Brings up an internal dialog panel with the options Yes, No 
     * and Cancel; with the title, "Select an Option".
     *
     * @param parentComponent determines the <code>Frame</code> in 
     *		which the dialog is displayed; if <code>null</code>,
     *		or if the <code>parentComponent</code> has no
     *		<code>Frame</code>, a default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @return an integer indicating the option selected by the user
     */
    public static int showInternalConfirmDialog(Component parentComponent,
                                                Object message) {
        return showInternalConfirmDialog(parentComponent, message,
            UIManager.getString("OptionPane.titleText"), YES_NO_CANCEL_OPTION);
    }

    /**
     * Brings up a internal dialog panel where the number of choices 
     * is determined by the <code>optionType</code> parameter.
     * 
     * @param parentComponent determines the <code>Frame</code>
     *		in which the dialog is displayed; if <code>null</code>,
     *		or if the <code>parentComponent</code> has no
     *		<code>Frame</code>, a default <code>Frame</code> is used
     * @param message   the object to display in the dialog; a
     *		<code>Component</code> object is rendered as a
     *		<code>Component</code>; a <code>String</code>
     *		object is rendered as a string; other objects
     *		are converted to a <code>String</code> using the
     *		<code>toString</code> method
     * @param title     the title string for the dialog
     * @param optionType an integer designating the options
     *		available on the dialog: YES_NO_OPTION,
     *		or YES_NO_CANCEL_OPTION
     * @return an integer indicating the option selected by the user
     */
    public static int showInternalConfirmDialog(Component parentComponent,
                                                Object message, String title,
                                                int optionType) {
        return showInternalConfirmDialog(parentComponent, message, title, optionType,
                                         QUESTION_MESSAGE);
    }

    /**
     * Brings up an internal dialog panel where the number of choices
     * is determined by the <code>optionType</code> parameter, where
     * the <code>messageType</code> parameter determines the icon to display.
     * The <code>messageType</code> parameter is primarily used to supply
     * a default icon from the Look and Feel.
     *
     * @param parentComponent determines the <code>Frame</code> in
     *		which the dialog is displayed; if <code>null</code>,
     *		or if the <code>parentComponent</code> has no
     *		<code>Frame</code>, a default <code>Frame</code> is used
     * @param message   the object to display in the dialog; a
     *		<code>Component</code> object is rendered as a
     *		<code>Component</code>; a <code>String</code>
     *		object is rendered as a string; other objects are
     *		converted to a <code>String</code> using the
     *		<code>toString</code> method
     * @param title     the title string for the dialog
     * @param optionType an integer designating the options
     *		available on the dialog:
     *		YES_NO_OPTION, or YES_NO_CANCEL_OPTION
     * @param messageType an integer designating the kind of message this is, 
     *          primarily used to determine the icon from the
     *		pluggable Look and Feel: ERROR_MESSAGE, INFORMATION_MESSAGE, 
     *		WARNING_MESSAGE, QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @return an integer indicating the option selected by the user
     */
    public static int showInternalConfirmDialog(Component parentComponent, 
                                        Object message,
                                        String title, int optionType,
                                        int messageType) {
        return showInternalConfirmDialog(parentComponent, message, title, optionType,
                                         messageType, null);
    }

    /**
     * Brings up an internal dialog panel with a specified icon, where
     * the number of choices is determined by the <code>optionType</code>
     * parameter. 
     * The <code>messageType</code> parameter is primarily used to supply
     * a default icon from the Look and Feel.
     *
     * @param parentComponent determines the <code>Frame</code>
     *		in which the dialog is displayed; if <code>null</code>,
     *		or if the parentComponent has no Frame, a 
     *          default <code>Frame</code> is used
     * @param message   the object to display in the dialog; a
     *		<code>Component</code> object is rendered as a
     *		<code>Component</code>; a <code>String</code>
     *		object is rendered as a string; other objects are
     *		converted to a <code>String</code> using the
     *		<code>toString</code> method
     * @param title     the title string for the dialog
     * @param optionType an integer designating the options available
     *		on the dialog:
     *          YES_NO_OPTION, or YES_NO_CANCEL_OPTION
     * @param messageType an integer designating the kind of message this is, 
     *		primarily used to determine the icon from the pluggable
     *		Look and Feel: ERROR_MESSAGE, INFORMATION_MESSAGE, 
     *		WARNING_MESSAGE, QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @param icon      the icon to display in the dialog
     * @return an integer indicating the option selected by the user
     */
    public static int showInternalConfirmDialog(Component parentComponent,
                                        Object message,
                                        String title, int optionType,
                                        int messageType, Icon icon) {
        return showInternalOptionDialog(parentComponent, message, title, optionType,
                                        messageType, icon, null, null);
    }

    /**
     * Brings up an internal dialog panel with a specified icon, where
     * the initial choice is dermined by the <code>initialValue</code>
     * parameter and the number of choices is determined by the 
     * <code>optionType</code> parameter.
     * <p>
     * If <code>optionType</code> is YES_NO_OPTION, or YES_NO_CANCEL_OPTION
     * and the <code>options</code> parameter is <code>null</code>,
     * then the options are supplied by the Look and Feel. 
     * <p>
     * The <code>messageType</code> parameter is primarily used to supply
     * a default icon from the Look and Feel.
     *
     * @param parentComponent determines the <code>Frame</code>
     *		in which the dialog is displayed; if <code>null</code>,
     *		or if the <code>parentComponent</code> has no
     *		<code>Frame</code>, a default <code>Frame</code> is used
     * @param message   the object to display in the dialog; a
     *		<code>Component</code> object is rendered as a
     *		<code>Component</code>; a <code>String</code>
     *		object is rendered as a string. Other objects are
     *		converted to a <code>String</code> using the
     *		<code>toString</code> method
     * @param title     the title string for the dialog
     * @param optionType an integer designating the options available
     *		on the dialog:
     *          YES_NO_OPTION, or YES_NO_CANCEL_OPTION
     * @param messageType an integer designating the kind of message this is, 
     *		primarily used to determine the icon from the
     *		pluggable Look and Feel: ERROR_MESSAGE, INFORMATION_MESSAGE, 
     *          WARNING_MESSAGE, QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @param icon      the icon to display in the dialog
     * @param options   an array of objects indicating the possible choices
     *          the user can make; if the objects are components, they
     *          are rendered properly; non-<code>String</code>
     *		objects are rendered using their <code>toString</code>
     *		methods; if this parameter is <code>null</code>,
     *		the options are determined by the Look and Feel
     * @param initialValue the object that represents the default selection
     *                     for the dialog
     * @return an integer indicating the option chosen by the user, 
     *         or CLOSED_OPTION if the user closed the Dialog
     */
    public static int showInternalOptionDialog(Component parentComponent,
                                       Object message,
                                       String title, int optionType,
                                       int messageType, Icon icon,
                                       Object[] options, Object initialValue) {
        JOptionPane             pane = new JOptionPane(message, messageType,
                                                       optionType, icon,
                                                       options, initialValue);

        pane.setInitialValue(initialValue);

        JInternalFrame   dialog = pane.createInternalFrame(parentComponent, title);
        pane.selectInitialValue();
	dialog.setVisible(true);
        dialog.startModal();

        Object        selectedValue = pane.getValue();

        if(selectedValue == null)
            return CLOSED_OPTION;
        if(options == null) {
            if(selectedValue instanceof Integer)
                return ((Integer)selectedValue).intValue();
            return CLOSED_OPTION;
        }
        for(int counter = 0, maxCounter = options.length;
            counter < maxCounter; counter++) {
            if(options[counter].equals(selectedValue))
                return counter;
        }
        return CLOSED_OPTION;
    }

    /**
     * Shows an internal question-message dialog requesting input from
     * the user parented to  <code>parentComponent</code>. The dialog
     * is displayed in the <code>Component</code>'s frame,
     * and is usually positioned below the <code>Component</code>. 
     *
     * @param parentComponent  the parent <code>Component</code>
     *		for the dialog
     * @param message  the <code>Object</code> to display
     */
    public static String showInternalInputDialog(Component parentComponent,
                                                 Object message) {
        return showInternalInputDialog(parentComponent, message, "Input",
                                       QUESTION_MESSAGE);
    }

    /**
     * Shows an internal dialog requesting input from the user parented
     * to <code>parentComponent</code> with the dialog having the title
     * <code>title</code> and message type <code>messageType</code>.
     *
     * @param parentComponent the parent <code>Component</code> for the dialog
     * @param message  the <code>Object</code> to display
     * @param title    the <code>String</code> to display in the
     *		dialog title bar
     * @param messageType the type of message that is to be displayed:
     *                    ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *                    QUESTION_MESSAGE, or PLAIN_MESSAGE
     */
    public static String showInternalInputDialog(Component parentComponent,
                             Object message, String title, int messageType) {
        return (String)showInternalInputDialog(parentComponent, message, title,
                                       messageType, null, null, null);
    }

    /**
     * Prompts the user for input in a blocking internal dialog where
     * the initial selection, possible selections, and all other 
     * options can be specified. The user will able to choose from
     * <code>selectionValues</code>, where <code>null</code>
     * implies the user can input
     * whatever they wish, usually by means of a <code>JTextField</code>. 
     * <code>initialSelectionValue</code> is the initial value to prompt
     * the user with. It is up to the UI to decide how best to represent
     * the <code>selectionValues</code>, but usually a
     * <code>JComboBox</code>, <code>JList</code>, or
     * <code>JTextField</code> will be used.
     *
     * @param parentComponent the parent <code>Component</code> for the dialog
     * @param message  the <code>Object</code> to display
     * @param title    the <code>String</code> to display in the dialog
     *		title bar
     * @param messageType the type of message to be displayed:
     *                    ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *                    QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @param icon     the <code>Icon</code> image to display
     * @param selectionValues an array of <code>Objects</code> that
     *		gives the possible selections
     * @param initialSelectionValue the value used to initialize the input
     *                              field
     * @return users input, or <code>null</code> meaning the user
     *		cancelled the input
     */
    public static Object showInternalInputDialog(Component parentComponent,
                      Object message, String title, int messageType, Icon icon,
                      Object[] selectionValues, Object initialSelectionValue) {
        JOptionPane             pane = new JOptionPane(message, messageType,
                                                       OK_CANCEL_OPTION, icon,
                                                       null, null);

        pane.setWantsInput(true);
        pane.setSelectionValues(selectionValues);
        pane.setInitialSelectionValue(initialSelectionValue);

        JInternalFrame   dialog = pane.createInternalFrame(parentComponent, title);

        pane.selectInitialValue();
	dialog.setVisible(true);
        dialog.startModal();

        Object value = pane.getInputValue();

        if(value == UNINITIALIZED_VALUE)
            return null;
        return (String)value;
    }

    /**
     * Creates and returns an instance of <code>JInternalFrame</code>. 
     * The internal frame is created with the specified title,
     * and wrapping the <code>JOptionPane</code>.
     * The returned <code>JInternalFrame</code> is
     * added to the <code>JDesktopPane</code> ancestor of
     * <code>parentComponent</code>, or components
     * parent if one its ancestors isn't a <code>JDesktopPane</code>,
     * or if <code>parentComponent</code>
     * doesn't have a parent then a <code>RuntimeException</code> is thrown.
     *
     * @param parentComponent  the parent <code>Component</code> for
     *		the internal frame
     * @param title    the <code>String</code> to display in the
     *		frame's title bar
     * @return a <code>JInternalFrame</code> containing a
     *		<code>JOptionPane</code>
     * @exception RuntimeException if <code>parentComponent</code> does
     *		not have a valid parent
     */
    public JInternalFrame createInternalFrame(Component parentComponent,
                                 String title) {
        Container          parent = JOptionPane.
                                    getDesktopPaneForComponent(parentComponent);

        if(parent == null && (parentComponent == null || 
                              (parent = parentComponent.getParent()) == null))
            throw new RuntimeException("JOptionPane: parentComponent does not have a valid parent");

        final JInternalFrame  iFrame = new JInternalFrame(title, false, false,
                                                           false, false);

	iFrame.putClientProperty( "JInternalFrame.frameType", "optionDialog" ); //jcs
        addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if(iFrame.isVisible() && event.getSource() == JOptionPane.this &&
                   (event.getPropertyName().equals(VALUE_PROPERTY) ||
                    event.getPropertyName().equals(INPUT_VALUE_PROPERTY))) {
                    try {
                        iFrame.setClosed(true);
                    } catch (java.beans.PropertyVetoException e) {}
                    iFrame.setVisible(false);
                    iFrame.stopModal();
                }
            }
        });
        iFrame.getContentPane().add(this, BorderLayout.CENTER);
        if(parent instanceof JDesktopPane) {
            parent.add(iFrame, JLayeredPane.MODAL_LAYER);
        } else {
            parent.add(iFrame, BorderLayout.CENTER);
        }
        Dimension            iFrameSize = iFrame.getPreferredSize();
        Dimension            rootSize = parent.getSize();

       	iFrame.setBounds((rootSize.width - iFrameSize.width) / 2,
                         (rootSize.height - iFrameSize.height) / 2,
                         iFrameSize.width, iFrameSize.height); 
       	parent.validate(); 
        try {
            iFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {} 
        return iFrame; 
    }

    /**
     * Returns the specified component's <code>Frame</code>.
     * 
     * @param parentComponent the <code>Component</code> to check for a
     *		<code>Frame</code>
     * @return the <code>Frame</code> that contains the component,
     *		or the default frame if the component is <code>null</code>,
     *		or does not have a valid <code>Frame</code> parent
     */
    public static Frame getFrameForComponent(Component parentComponent) {
        if (parentComponent == null)
            return getRootFrame();
        if (parentComponent instanceof Frame)
            return (Frame)parentComponent;
        return JOptionPane.getFrameForComponent(parentComponent.getParent());
    }

    /**
     * Returns the specified component's toplevel <code>Frame</code> or
     * <code>Dialog</code>.
     * 
     * @param parentComponent the <code>Component</code> to check for a 
     *		<code>Frame</code> or <code>Dialog</code>
     * @return the <code>Frame</code> or <code>Dialog</code> that
     *		contains the component, or the default
     *         	frame if the component is <code>null</code>,
     *		or does not have a valid 
     *         	<code>Frame</code> or <code>Dialog</code> parent
     */
    static Window getWindowForComponent(Component parentComponent) {
        if (parentComponent == null)
            return getRootFrame();
        if (parentComponent instanceof Frame || parentComponent instanceof Dialog)
            return (Window)parentComponent;
        return JOptionPane.getWindowForComponent(parentComponent.getParent());
    }


    /**
     * Returns the specified component's desktop pane.
     * 
     * @param parentComponent the <code>Component</code> to check for a
     *		desktop
     * @return the <code>JDesktopPane</code> that contains the component,
     *		or <code>null</code> if the component is <code>null</code>
     *		or does not have an ancestor that is a
     *		<code>JInternalFrame</code>
     */
    public static JDesktopPane getDesktopPaneForComponent(Component parentComponent) {
        if(parentComponent == null)
            return null;
        if(parentComponent instanceof JDesktopPane)
            return (JDesktopPane)parentComponent;
        return getDesktopPaneForComponent(parentComponent.getParent());
    }

    private static final Object sharedFrameKey = JOptionPane.class;

    /**
     * Sets the frame to use for class methods in which a frame is
     * not provided.
     *
     * @param newRootFrame the default <code>Frame</code> to use
     */
    public static void setRootFrame(Frame newRootFrame) {
        if (newRootFrame != null) {
            SwingUtilities.appContextPut(sharedFrameKey, newRootFrame);
        } else {
            SwingUtilities.appContextRemove(sharedFrameKey);
        }
    }

    /**
     * Returns the <code>Frame</code> to use for the class methods in
     * which a frame is not provided.
     *
     * @return the default <code>Frame</code> to use
     */
    public static Frame getRootFrame() {
        Frame sharedFrame = 
            (Frame)SwingUtilities.appContextGet(sharedFrameKey);
        if (sharedFrame == null) {
            sharedFrame = SwingUtilities.getSharedOwnerFrame();
            SwingUtilities.appContextPut(sharedFrameKey, sharedFrame);
        }
        return sharedFrame;
    }

    /**
     * Creates a <code>JOptionPane</code> with a test message.
     */
    public JOptionPane() {
        this("JOptionPane message");
    }

    /**
     * Creates a instance of <code>JOptionPane</code> to display a
     * message using the 
     * plain-message message type and the default options delivered by
     * the UI.
     *
     * @param message the <code>Object</code> to display
     */
    public JOptionPane(Object message) {
        this(message, PLAIN_MESSAGE);
    }

    /**
     * Creates an instance of <code>JOptionPane</code> to display a message
     * with the specified message type and the default options,
     *
     * @param message the <code>Object</code> to display
     * @param messageType the type of message to be displayed:
     *                    ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *                    QUESTION_MESSAGE, or PLAIN_MESSAGE
     */
    public JOptionPane(Object message, int messageType) {
        this(message, messageType, DEFAULT_OPTION);
    }

    /**
     * Creates an instance of <code>JOptionPane</code> to display a message
     * with the specified message type and options.
     *
     * @param message the <code>Object</code> to display
     * @param messageType the type of message to be displayed:
     *                    ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *                    QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @param optionType the options to display in the pane:
     *                   DEFAULT_OPTION, YES_NO_OPTION, YES_NO_CANCEL_OPTION
     *                   OK_CANCEL_OPTION
     */
    public JOptionPane(Object message, int messageType, int optionType) {
        this(message, messageType, optionType, null);
    }

    /**
     * Creates an instance of <code>JOptionPane</code> to display a message
     * with the specified message type, options, and icon.
     *
     * @param message the <code>Object</code> to display
     * @param messageType the type of message to be displayed:
     *                    ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *                    QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @param optionType the options to display in the pane:
     *                   DEFAULT_OPTION, YES_NO_OPTION, YES_NO_CANCEL_OPTION
     *                   OK_CANCEL_OPTION
     * @param icon the <code>Icon</code> image to display
     */
    public JOptionPane(Object message, int messageType, int optionType,
                       Icon icon) {
        this(message, messageType, optionType, icon, null);
    }

    /**
     * Creates an instance of JOptionPane to display a message
     * with the specified message type, icon, and options.
     * None of the options is initially selected.
     * <p>
     * The options objects should contain either instances of
     * <code>Component</code>s, (which are added directly) or
     * <code>Strings</code> (which are wrapped in a <code>JButton</code>).
     * If you provide <code>Component</code>s, you must ensure that when the
     * <code>Component</code> is clicked it messages <code>setValue</code>
     * in the created <code>JOptionPane</code>.
     *
     * @param message the <code>Object</code> to display
     * @param messageType the type of message to be displayed:
     *                    ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *                    QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @param optionType the options to display in the pane:
     *                   DEFAULT_OPTION, YES_NO_OPTION, YES_NO_CANCEL_OPTION
     *                   OK_CANCEL_OPTION; only meaningful if the 
     *                   <code>options</code> parameter is <code>null</code>
     * @param icon the <code>Icon</code> image to display
     * @param options  the choices the user can select
     */
    public JOptionPane(Object message, int messageType, int optionType,
                       Icon icon, Object[] options) {
        this(message, messageType, optionType, icon, options, null);
    }

    /**
     * Creates an instance of <code>JOptionPane</code> to display a message
     * with the specified message type, icon, and options, with the 
     * initially-selected option specified.
     *
     * @param message the <code>Object</code> to display
     * @param messageType the type of message to be displayed:
     *                    ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *                    QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @param optionType the options to display in the pane:
     *                   DEFAULT_OPTION, YES_NO_OPTION, YES_NO_CANCEL_OPTION
     *                   OK_CANCEL_OPTION; only meaningful if the
     *                   <code>options</code> parameter is <code>null</code>
     * @param icon the Icon image to display
     * @param options  the choices the user can select
     * @param initialValue the choice that is initially selected
     */
    public JOptionPane(Object message, int messageType, int optionType,
                       Icon icon, Object[] options, Object initialValue) {
        this.message = message;
        this.options = options;
        this.initialValue = initialValue;
        this.icon = icon;
        setMessageType(messageType);
        setOptionType(optionType);
        value = UNINITIALIZED_VALUE;
        inputValue = UNINITIALIZED_VALUE;
        updateUI();
    }

    /**
     * Sets the UI object which implements the L&F for this component.
     *
     * @param ui  the <code>OptionPaneUI</code> L&F object
     * @see UIDefaults#getUI
     * @beaninfo
     *       bound: true
     *      hidden: true
     * description: The UI object that implements the optionpane's LookAndFeel
     */
    public void setUI(OptionPaneUI ui) {
        if ((OptionPaneUI)this.ui != ui) {
            super.setUI(ui);
            invalidate();
        }
    }

    /**
     * Returns the UI object which implements the L&F for this component.
     *
     * @return the <code>OptionPaneUI</code> object
     */
    public OptionPaneUI getUI() {
        return (OptionPaneUI)ui;
    }

    /**
     * Notification from the <code>UIManager</code> that the L&F has changed. 
     * Replaces the current UI object with the latest version from the 
     * <code>UIManager</code>.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((OptionPaneUI)UIManager.getUI(this));
    }


    /**
     * Returns the name of the UI class that implements the
     * L&F for this component.
     *
     * @return the string "OptionPaneUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /**
     * Sets the option pane's message-object.
     * @param newMessage the <code>Object</code> to display
     * @see #getMessage
     *
     * @beaninfo
     *   preferred: true
     *   bound: true
     * description: The optionpane's message object.
     */
    public void setMessage(Object newMessage) {
        Object           oldMessage = message;

        message = newMessage;
        firePropertyChange(MESSAGE_PROPERTY, oldMessage, message);
    }

    /**
     * Returns the message-object this pane displays.
     * @see #setMessage
     *
     * @return the <code>Object</code> that is displayed
     */
    public Object getMessage() {
        return message;
    }

    /**
     * Sets the icon to display. If non-<code>null</code>, the Look and Feel 
     * does not provide an icon.
     * @param icon the <code>Icon</code> to display
     *
     * @see #getIcon
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The option pane's type icon.
     */
    public void setIcon(Icon newIcon) {
        Object              oldIcon = icon;

        icon = newIcon;
        firePropertyChange(ICON_PROPERTY, oldIcon, icon);
    }

    /**
     * Returns the icon this pane displays.
     * @return the <code>Icon</code> that is displayed
     *
     * @see #setIcon
     */
    public Icon getIcon() {
        return icon;
    }

    /**
     * Sets the value the user has chosen. 
     * @param newValue  the chosen value
     *
     * @see #getValue
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The option pane's value object.
     */
    public void setValue(Object newValue) {
        Object               oldValue = value;

        value = newValue;
        firePropertyChange(VALUE_PROPERTY, oldValue, value);
    }

    /**
     * Returns the value the user has selected. UNINITIALIZED_VALUE
     * implies the user has not yet made a choice, <code>null</code> means the
     * user closed the window with out choosing anything. Otherwise
     * the returned value will be one of the options defined in this
     * object.
     *
     * @return the <code>Object</code> chosen by the user, UNINITIALIZED_VALUE
     *         if the user has not yet made a choice, or <code>null</code> if
     *         the user closed the window without making a choice
     *
     * @see #setValue
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the options this pane displays. If an element in
     * <code>newOptions</code> is a <code>Component</code>
     * it is added directly to the pane,
     * otherwise a button is created for the element.
     *
     * @param newOptions an array of <code>Objects</code> that create the
     *		buttons the user can click on, or arbitrary 
     *		<code>Components</code> to add to the pane
     *
     * @see #getOptions
     * @beaninfo
     *       bound: true
     * description: The option pane's options objects.
     */
    public void setOptions(Object[] newOptions) {
        Object[]           oldOptions = options;

        options = newOptions;
        firePropertyChange(OPTIONS_PROPERTY, oldOptions, options);
    }

    /**
     * Returns the choices the user can make.
     * @return the array of <code>Objects</code> that give the user's choices
     *
     * @see #setOptions
     */
    public Object[] getOptions() {
        if(options != null) {
            int             optionCount = options.length;
            Object[]        retOptions = new Object[optionCount];

            System.arraycopy(options, 0, retOptions, 0, optionCount);
            return retOptions;
        }
        return options;
    }

    /**
     * Sets the initial value that is to be enabled -- the
     * <code>Component</code>
     * that has the focus when the pane is initially displayed.
     *
     * @param newInitialValue the <code>Object</code> that gets the initial 
     *                         keyboard focus
     *
     * @see #getInitialValue
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The option pane's initial value object.
     */
    public void setInitialValue(Object newInitialValue) {
        Object            oldIV = initialValue;

        initialValue = newInitialValue;
        firePropertyChange(INITIAL_VALUE_PROPERTY, oldIV, initialValue);
    }

    /**
     * Returns the initial value.
     *
     * @return the <code>Object</code> that gets the initial keyboard focus
     *
     * @see #setInitialValue
     */
    public Object getInitialValue() {
        return initialValue;
    }

    /**
     * Sets the option pane's message type.
     * The message type is used by the Look and Feel to determine the
     * icon to display (if not supplied) as well as potentially how to
     * lay out the <code>parentComponent</code>.
     * @param newType an integer specifying the kind of message to display:
     *                ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *                QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @exception RuntimeException if <code>newType</code> is not one of the
     *		legal values listed above

     * @see #getMessageType
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The option pane's message type.
     */
    public void setMessageType(int newType) {
        if(newType != ERROR_MESSAGE && newType != INFORMATION_MESSAGE &&
           newType != WARNING_MESSAGE && newType != QUESTION_MESSAGE &&
           newType != PLAIN_MESSAGE)
            throw new RuntimeException("JOptionPane: type must be one of JOptionPane.ERROR_MESSAGE, JOptionPane.INFORMATION_MESSAGE, JOptionPane.WARNING_MESSAGE, JOptionPane.QUESTION_MESSAGE or JOptionPane.PLAIN_MESSAGE");

        int           oldType = messageType;

        messageType = newType;
        firePropertyChange(MESSAGE_TYPE_PROPERTY, oldType, messageType);
    }

    /**
     * Returns the message type.
     *
     * @return an integer specifying the message type
     *
     * @see #setMessageType
     */
    public int getMessageType() {
        return messageType;
    }

    /**
     * Sets the options to display. 
     * The option type is used by the Look and Feel to
     * determine what buttons to show (unless options are supplied).
     * @param newType an integer specifying the options the L&F is to display:
     *                DEFAULT_OPTION, YES_NO_OPTION, YES_NO_CANCEL_OPTION,
     *                or OK_CANCEL_OPTION
     * @exception RuntimeException if <code>newType</code> is not one of
     *		the legal values listed above
     *
     * @see #getOptionType
     * @see #setOptions
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The option pane's option type.
      */
    public void setOptionType(int newType) {
        if(newType != DEFAULT_OPTION && newType != YES_NO_OPTION &&
           newType != YES_NO_CANCEL_OPTION && newType != OK_CANCEL_OPTION)
            throw new RuntimeException("JOptionPane: option type must be one of JOptionPane.DEFAULT_OPTION, JOptionPane.YES_NO_OPTION, JOptionPane.YES_NO_CANCEL_OPTION or JOptionPane.OK_CANCEL_OPTION");

        int            oldType = optionType;

        optionType = newType;
        firePropertyChange(OPTION_TYPE_PROPERTY, oldType, optionType);
    }

    /**
     * Returns the type of options that are displayed.
     *
     * @return an integer specifying the user-selectable options
     *
     * @see #setOptionType
     */
    public int getOptionType() {
        return optionType;
    }

    /** 
     * Sets the selection values for a pane that provides the user
     * with a list of items to choose from. (The UI provides a widget 
     * for choosing one of the values.) 
     * <p>
     * Sets <code>wantsInput</code> to true. Use
     * <code>setInitialSelectionValue</code> to specify the initially-chosen
     * value. After the pane as been enabled, <code>inputValue</code> is 
     * set to the value the user has selected.
     * @param newValues an array of <code>Objects</code> the user to be
     *			displayed
     *                  (usually in a list or combo-box) from which
     *                  the user can make a selection
     * @see #setWantsInput
     * @see #setInitialSelectionValue
     * @see #getSelectionValues
     * @beaninfo
     *       bound: true
     * description: The option pane's selection values.
     */
    public void setSelectionValues(Object[] newValues) {
        Object[]           oldValues = selectionValues;

        selectionValues = newValues;
        firePropertyChange(SELECTION_VALUES_PROPERTY, oldValues, newValues);
        if(selectionValues != null)
            setWantsInput(true);
    }

    /**
     * Returns the selection values.
     *
     * @param return the array of <code>Objects</code> the user can select
     * @see #setSelectionValues
     */
    public Object[] getSelectionValues() {
        return selectionValues;
    }

    /**
     * Sets the initial selection value. Only used if <code>wantsInput</code>
     * is true.
     * @param newValue the initially selected value
     * @see #setSelectionValues
     * @see #getInitialSelectionValue
     * @beaninfo
     *       bound: true
     * description: The option pane's initial selection value object.
     */
    public void setInitialSelectionValue(Object newValue) {
        Object          oldValue = initialSelectionValue;

        initialSelectionValue = newValue;
        firePropertyChange(INITIAL_SELECTION_VALUE_PROPERTY, oldValue,
                           newValue);
    }

    /**
     * Returns the initial-selection value.
     *
     * @return the initially selected value
     * @see #setInitialSelectionValue
     * @see #setSelectionValues
     */
    public Object getInitialSelectionValue() {
        return initialSelectionValue;
    }

    /**
     * Sets the user's input-value.
     *
     * @param newValue the <code>Object</code> used to initialized the
     *		value that the user specified (usually in a text field)
     * @see #setSelectionValues
     * @see #setWantsInput
     * @see #getInputValue
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The option pane's input value object.
     */
    public void setInputValue(Object newValue) {
        Object              oldValue = inputValue;

        inputValue = newValue;
        firePropertyChange(INPUT_VALUE_PROPERTY, oldValue, newValue);
    }

    /**
     * Returns the value the user has input, if <code>wantsInput</code>
     * is true.
     *
     * @return the <code>Object</code> the user specified,
     *		if it was one of the objects, or a 
     *         	<code>String</code> if it was a value typed into a
     *         	field
     * @see #setSelectionValues
     * @see #setWantsInput
     * @see #setInputValue
     */
    public Object getInputValue() {
        return inputValue;
    }

    /**
     * Returns the maximum number of characters to place on a line in a
     * message. Default is to return <code>Integer.MAX_VALUE</code>.
     * The value can be 
     * changed by overriding this method in a subclass.
     *
     * @return an integer giving the maximum number of characters on a line
     */
    public int getMaxCharactersPerLineCount() {
        return Integer.MAX_VALUE;
    }

    /**
     * If <code>newValue</code> is true, a <code>parentComponent</code>
     * is provided to
     * allow the user to input a value. If <code>getSelectionValues</code>
     * returns a non-<code>null</code> the input value is one of the
     * objects in that 
     * array. Otherwise the input value is whatever the user inputs.
     * <p>
     * This is a bound property.
     *
     * @see #setSelectionValues
     * @see #setInputValue
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: Flag which allows the user to input a value.
     */
    public void setWantsInput(boolean newValue) {
        boolean            oldValue = wantsInput;

        wantsInput = newValue;
        firePropertyChange(WANTS_INPUT_PROPERTY, oldValue, newValue);
    }

    /**
     * Returns true if a <code>parentComponent</code> will be provided
     * for the user to input.
     *
     * @return true if a <code>parentComponent</code> will be provided
     * @see #setWantsInput
     */
    public boolean getWantsInput() {
        return wantsInput;
    }

    /**
     * Requests that the initial value be selected, which will set
     * focus to the initial value. This method
     * should be invoked after the window containing the option pane
     * is made visible.
     */
    public void selectInitialValue() {
        OptionPaneUI         ui = getUI();
        if (ui != null) {
            ui.selectInitialValue(this);
        }
    }

    
    // Serialization support.  
    private void writeObject(ObjectOutputStream s) throws IOException {
        Vector      values = new Vector();

        s.defaultWriteObject();
        // Save the icon, if its Serializable.
        if(icon != null && icon instanceof Serializable) {
            values.addElement("icon");
            values.addElement(icon);
        }
        // Save the message, if its Serializable.
        if(message != null && message instanceof Serializable) {
            values.addElement("message");
            values.addElement(message);
        }
        // Save the treeModel, if its Serializable.
        if(options != null) {
            Vector           serOptions = new Vector();

            for(int counter = 0, maxCounter = options.length;
                counter < maxCounter; counter++)
                if(options[counter] instanceof Serializable)
                    serOptions.addElement(options[counter]);
            if(serOptions.size() > 0) {
                int             optionCount = serOptions.size();
                Object[]        arrayOptions = new Object[optionCount];

                serOptions.copyInto(arrayOptions);
                values.addElement("options");
                values.addElement(arrayOptions);
            }
        }
        // Save the initialValue, if its Serializable.
        if(initialValue != null && initialValue instanceof Serializable) {
            values.addElement("initialValue");
            values.addElement(initialValue);
        }
        // Save the value, if its Serializable.
        if(value != null && value instanceof Serializable) {
            values.addElement("value");
            values.addElement(value);
        }
        // Save the selectionValues, if its Serializable.
        if(selectionValues != null) {
            boolean            serialize = true;

            for(int counter = 0, maxCounter = selectionValues.length;
                counter < maxCounter; counter++) {
                if(selectionValues[counter] != null &&
                   !(selectionValues[counter] instanceof Serializable)) {
                    serialize = false;
                    break;
                }
            }
            if(serialize) {
                values.addElement("selectionValues");
                values.addElement(selectionValues);
            }
        }
        // Save the inputValue, if its Serializable.
        if(inputValue != null && inputValue instanceof Serializable) {
            values.addElement("inputValue");
            values.addElement(inputValue);
        }
        // Save the initialSelectionValue, if its Serializable.
        if(initialSelectionValue != null &&
           initialSelectionValue instanceof Serializable) {
            values.addElement("initialSelectionValue");
            values.addElement(initialSelectionValue);
        }
        s.writeObject(values);
    }

    private void readObject(ObjectInputStream s) 
        throws IOException, ClassNotFoundException {
        s.defaultReadObject();

        Vector          values = (Vector)s.readObject();
        int             indexCounter = 0;
        int             maxCounter = values.size();

        if(indexCounter < maxCounter && values.elementAt(indexCounter).
           equals("icon")) {
            icon = (Icon)values.elementAt(++indexCounter);
            indexCounter++;
        }
        if(indexCounter < maxCounter && values.elementAt(indexCounter).
           equals("message")) {
            message = values.elementAt(++indexCounter);
            indexCounter++;
        }
        if(indexCounter < maxCounter && values.elementAt(indexCounter).
           equals("options")) {
            options = (Object[])values.elementAt(++indexCounter);
            indexCounter++;
        }
        if(indexCounter < maxCounter && values.elementAt(indexCounter).
           equals("initialValue")) {
            initialValue = values.elementAt(++indexCounter);
            indexCounter++;
        }
        if(indexCounter < maxCounter && values.elementAt(indexCounter).
           equals("value")) {
            value = values.elementAt(++indexCounter);
            indexCounter++;
        }
        if(indexCounter < maxCounter && values.elementAt(indexCounter).
           equals("selectionValues")) {
            selectionValues = (Object[])values.elementAt(++indexCounter);
            indexCounter++;
        }
        if(indexCounter < maxCounter && values.elementAt(indexCounter).
           equals("inputValue")) {
            inputValue = values.elementAt(++indexCounter);
            indexCounter++;
        }
        if(indexCounter < maxCounter && values.elementAt(indexCounter).
           equals("initialSelectionValue")) {
            initialSelectionValue = values.elementAt(++indexCounter);
            indexCounter++;
        }
	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    ui.installUI(this);
	}
    }


    /**
     * Returns a string representation of this <code>JOptionPane</code>.
     * This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this <code>JOptionPane</code>
     */
    protected String paramString() {
        String iconString = (icon != null ?
			     icon.toString() : "");
        String initialValueString = (initialValue != null ?
				     initialValue.toString() : "");
        String messageString = (message != null ?
				message.toString() : "");
        String messageTypeString;
        if (messageType == ERROR_MESSAGE) {
            messageTypeString = "ERROR_MESSAGE";
        } else if (messageType == INFORMATION_MESSAGE) {
            messageTypeString = "INFORMATION_MESSAGE";
        } else if (messageType == WARNING_MESSAGE) {
            messageTypeString = "WARNING_MESSAGE";
        } else if (messageType == QUESTION_MESSAGE) {
            messageTypeString = "QUESTION_MESSAGE";
        } else if (messageType == PLAIN_MESSAGE)  {
            messageTypeString = "PLAIN_MESSAGE";
        } else messageTypeString = "";
        String optionTypeString;
        if (optionType == DEFAULT_OPTION) {
            optionTypeString = "DEFAULT_OPTION";
        } else if (optionType == YES_NO_OPTION) {
            optionTypeString = "YES_NO_OPTION";
        } else if (optionType == YES_NO_CANCEL_OPTION) {
            optionTypeString = "YES_NO_CANCEL_OPTION";
        } else if (optionType == OK_CANCEL_OPTION) {
            optionTypeString = "OK_CANCEL_OPTION";
        } else optionTypeString = "";
        String wantsInputString = (wantsInput ?
				   "true" : "false");

        return super.paramString() +
        ",icon=" + iconString +
        ",initialValue=" + initialValueString +
        ",message=" + messageString +
        ",messageType=" + messageTypeString +
        ",optionType=" + optionTypeString +
        ",wantsInput=" + wantsInputString;
    }



///////////////////
// Accessibility support
///////////////////

    /**
     * Gets the AccessibleContext associated with this JOptionPane. 
     * For option panes, the AccessibleContext takes the form of an 
     * AccessibleJOptionPane. 
     * A new AccessibleJOptionPane instance is created if necessary.
     *
     * @return an AccessibleJOptionPane that serves as the 
     *         AccessibleContext of this AccessibleJOptionPane
     * @beaninfo
     *       expert: true
     *  description: The AccessibleContext associated with this option pane
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJOptionPane();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>JOptionPane</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to option pane user-interface 
     * elements.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class AccessibleJOptionPane extends AccessibleJComponent {

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.OPTION_PANE;
        }

    } // inner class AccessibleJOptionPane
}


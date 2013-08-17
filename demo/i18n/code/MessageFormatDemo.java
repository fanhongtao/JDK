/*
 * @(#)MessageFormatDemo.java	1.1 96/11/23
 *
 * (C) Copyright Taligent, Inc. 1996 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - All Rights Reserved
 *
 * Portions copyright (c) 1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */

import java.applet.Applet;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.lang.*;
import java.text.*;

/**
 * <P> Pattern formats are used to put together sequences of strings, numbers,
 * dates, and other formats to create messages.  The pattern formatters
 * facilitate localization because they prevent both hard-coding of message
 * strings, <I> and </I> hard-coding of the concatenation sequence for portions
 * of message strings.  This means localizers can change the content, format,
 * and order of any text as appropriate for any language.
 * </P>
 * <P><CENTER>
 * <A HREF="#format">About the Pattern Format</A>&nbsp;
 * <A HREF="#arguments">Formatting Arguments</A><BR>
 * <A HREF="#choices">Supporting Multiple Choices</A>
 * </CENTER></P>
 * <HR>
 * <H3><A NAME="format">About the Pattern Format </A></H3>
 * <P> The pattern is a sequence of text and arguments, all of which can be
 * edited.  The positions of the arguments within the sequence are indicated
 * by a &quot;&#37;&quot; followed by a digit <I> n </I> identifying the
 * argument.  The pattern in the demo applet has three arguments, numbered 0,
 * 1, and 2.  The arguments can appear in any order within the sequence, which
 * can be edited and modified.
 * </P>
 * <TABLE WIDTH=100% CELLPADDING=5>
 * <TR>
 * <TH ALIGN=left VALIGN=bottom>To See This...</TH>
 * <TH ALIGN=left VALIGN=bottom>Do This...</TH>
 * </TR>
 * <TR>
 * <TD WIDTH=50% VALIGN=top> You can move arguments freely within the sequence
 * or delete arguments.  You can also edit or translate any of the unformatted
 * text.
 * </TD>
 * <TD VALIGN=top>
 * <TABLE CELLSPACING=0 CELLPADDING=0>
 * <TR>
 * <TD WIDTH=10 VALIGN=top> 1.
 * </TD>
 * <TD> Move the string &quot;on &#37;2&quot; from the end of the pattern to
 * the front and correct the capitalization
 * </TD>
 * </TR>
 * </TABLE>
 * </TD>
 * </TR>
 * <TD WIDTH=50% VALIGN=top> Translations are provided in the demo applet for
 * the G7 countries.
 * </TD>
 * <TD VALIGN=top>
 * <TABLE CELLSPACING=0 CELLPADDING=0>
 * <TR>
 * <TD WIDTH=10 VALIGN=top> 1.
 * </TD>
 * <TD> Pull down the Locale menu
 * </TD>
 * </TR>
 * <TR>
 * <TD WIDTH=10 VALIGN=top> 2.
 * </TD>
 * <TD> Try several different locales with the up and down arrow keys (on
 * Windows) or the mouse button (on Macintosh)
 * </TD>
 * </TR>
 * </TABLE>
 * </TD>
 * </TR>
 * </TABLE>
 * <P><STRONG><I>Note:</I></STRONG> To add a real percentage character to the
 * pattern, enter &quot;&#37;&#37;&quot;.
 * </P>
 * <HR WIDTH=50% ALIGN=left>
 * <H3><A NAME="arguments"> Formatting Arguments </A></H3>
 * <P> The arguments can be either simple text strings or formattable,
 * localizable objects.  The pattern in the demo applet, for example, includes
 * a date, an unformatted string, and a more complex format called a <I> choice
 * </I> format (described below).  You can edit these arguments at will.  When
 * localizing, you can also select any format to be associated with an
 * argument.
 * </P>
 * <TABLE WIDTH=100% CELLPADDING=5>
 * <TR>
 * <TH ALIGN=left VALIGN=bottom>To See This... </TH>
 * <TH ALIGN=left VALIGN=bottom>Do This...</TH>
 * </TR>
 * <TR>
 * <TD WIDTH=50% VALIGN=top> You can modify the value of any argument.
 * </TD>
 * <TD VALIGN=top>
 * <TABLE CELLSPACING=0 CELLPADDING=0>
 * <TR>
 * <TD WIDTH=10 VALIGN=top> 1.
 * </TD>
 * <TD> Select the &quot;3&quot; in argument 2 and change it to another number
 * &#151;the formatted date adjusts to the new value
 * </TD>
 * </TR>
 * </TABLE>
 * </TD>
 * </TR>
 * <TR>
 * <TD WIDTH=50% VALIGN=top> You can change the format of any argument, and can
 * specify &quot;unformatted&quot; arguments that are not localized.
 * </TD>
 * <TD VALIGN=top>
 * <TABLE CELLSPACING=0 CELLPADDING=0>
 * <TR>
 * <TD WIDTH=10 VALIGN=top> 1.
 * </TD>
 * <TD> Change the format type for argument 2 to <I> None </I>
 * </TD>
 * </TR>
 * <TR>
 * <TD WIDTH=10 VALIGN=top> 2.
 * </TD>
 * <TD> Try different locales and notice that the date does not reformat for
 * different locales
 * </TD>
 * </TR>
 * <TR>
 * <TD WIDTH=10 VALIGN=top> 3.
 * </TD>
 * <TD> Return the format back to <I>Date </I> and try different locales again.
 * The date reformats.
 * </TD>
 * </TR>
 * </TABLE>
 * </TD>
 * </TR>
 * </TABLE>
 * <HR WIDTH=50% ALIGN=left>
 * <H3> <A NAME="choices">Supporting Multiple Choices </A></H3>
 * <P> Choice formats, like that used for argument 0, let localizers create
 * more natural messages, avoiding phrases like &quot;3 file(s)&quot;.  As
 * shown here, the correct text can be chosen for different numbers.  This
 * works even in more complicated contexts, such as Slavic languages which
 * have more than one plural format based on the number involved.  A particular
 * choice is chosen based on the value of the argument, and each choice can be
 * edited individually.
 * </P>
 * <P> Look also at the format for the choice associated with values of 2 and
 * higher, &quot;&#37;0|3&quot;.  The vertical bar indicates that the choice
 * uses the value for argument 0, but the format for argument 3 (in this case,
 * a Number).  This allows for a degree of flexibility in using different
 * formats.
 * </P>
 * <TABLE WIDTH=100% CELLPADDING=5>
 * <TR>
 * <TH ALIGN=left VALIGN=bottom>To See This...</TH>
 * <TH ALIGN=left VALIGN=bottom>Do This... </TH>
 * </TR>
 * <TR>
 * <TD WIDTH=50% VALIGN=top> You can edit the value of any of the choice
 * options.
 * </TD>
 * <TD VALIGN=top>
 * <TABLE CELLSPACING=0 CELLPADDING=0>
 * <TR>
 * <TD WIDTH=10 VALIGN=top> 1.
 * </TD>
 * <TD> Select the Choice String &quot;no files&quot; and type in &quot;not a
 * single file&quot;
 * </TD>
 * </TR>
 * <TR>
 * <TD WIDTH=10 VALIGN=top> 2.
 * </TD>
 * <TD> Select the value for argument 0 and type in &quot;0&quot;
 * </TD>
 * </TR>
 * </TABLE>
 * </TD>
 * </TR>
 * <TR>
 * <TD WIDTH=50% VALIGN=top> You can establish different choices for parameters
 * based on the value of an argument, so that strings are substituted that
 * agree numerically.
 * </TD>
 * <TD VALIGN=top>
 * <TABLE CELLSPACING=0 CELLPADDING=0>
 * <TR>
 * <TD WIDTH=10 VALIGN=top> 1.
 * </TD>
 * <TD> Select the value for argument 0 and type in &quot;0&quot;, then replace
 * it with a &quot;1&quot;, and then with a &quot;2&quot;.  The string changes
 * correspondingly.
 * </TD>
 * </TR>
 * <TR>
 * <TD WIDTH=10 VALIGN=top> 2.
 * </TD>
 * <TD> Select argument 0 and return the value to &quot;0&quot;.  Choose the
 * French or German locale and notice that it makes correct substitutions in
 * any language.
 * </TD>
 * </TR>
 * </TABLE>
 * </TD>
 * </TR>
 * <TR>
 * <TD WIDTH=50% VALIGN=top> You can add as many alternatives as you need for
 * different value ranges.
 * </TD>
 * <TD VALIGN=top>
 * <TABLE CELLSPACING=0 CELLPADDING=0>
 * <TR>
 * <TD WIDTH=10 VALIGN=top> 1.
 * </TD>
 * <TD> Select the U.S. English locale
 * </TD>
 * </TR>
 * <TR>
 * <TD WIDTH=10 VALIGN=top> 2.
 * </TD>
 * <TD> Select the empty choice value box (under the &quot;2&quot;) and type
 * in &quot;10&quot;
 * </TD>
 * </TR>
 * <TR>
 * <TD WIDTH=10 VALIGN=top> 3.
 * </TD>
 * <TD> Type in &quot;many files&quot; in the corresponding <I> Choice Strings
 * </I> field
 * </TD>
 * </TR>
 * <TR>
 * <TD WIDTH=10 VALIGN=top> 4.
 * </TD>
 * <TD> Enter a number larger than10 for the value of argument 0&#151;&quot;
 * many files&quot; is substituted in the resulting message
 * </TD>
 * </TR>
 * </TABLE>
 * </TD>
 * </TR>
 * </TABLE>
 * <P>  You can type in other text in the pattern, arguments, or choices fields
 * to  see different formatting behaviors.  Try it out!
 * </P>
 * @see        java.util.Format
 * @see        java.util.MessageFormat
 * @version    1.1 11/23/96
 * @author     Laura Werner, Mark Davis
*/

public class MessageFormatDemo extends DemoApplet
{
    /**
     * The main function which defines the behavior of the MessageFormatDemo
     * applet when an applet is started.
     */
    public static void main(String argv[]) {
        DemoApplet.showDemo(new MessageFormatFrame(null));
    }

    /**
     * This creates a MessageFormatFrame for the demo applet.
     */
    public Frame createDemoFrame(DemoApplet applet) {
        return new MessageFormatFrame(applet);
    }
}

/**
 * A Frame is a top-level window with a title. The default layout for a frame
 * is BorderLayout.  The MessageFormatFrame class defines the window layout of
 * MessageFormatDemo.
 */
 class MessageFormatFrame extends Frame implements WindowListener, ItemListener, KeyListener {

    /**
     * Constructs a new MessageFormatFrame that is initially invisible.
     */
    public MessageFormatFrame(DemoApplet applet)
    {
        super("Message Formatting Demo");
        this.applet = applet;
	addWindowListener(this);
        init();
        start();
    }

    /**
     * Initializes the applet. You never need to call this directly, it
     * is called automatically by the system once the applet is created.
     */
    public void init() {

        //Get all locales for debugging, but only get G7 locales for demos.
        if (DEBUG == true)
             locales = NumberFormat.getAvailableLocales();
        else locales = Utility.getG7Locales();

        buildGUI();

    }

    /**
     * Called to start the applet. You never need to call this method
     * directly, it is called when the applet's document is visited.
     */
    public void start() {

        // Stick some initial data into the controls....
        arg1Text.setText("3");
        arg1Type.select(CHOICE);

        arg2Text.setText("MyDisk");
        arg2Type.select(NONE);

        arg3Text.setText("3 Mar 96");
        arg3Type.select(DATE);

        arg4Text.setText("");
        arg4Type.select(NUMBER);

        patternText.setText("The disk '%1' contained %0 on %2.");

        // Set up the choice format controls too
        choice1Num.setText("0");
        choice2Num.setText("1");
        choice3Num.setText("2");
        resetFormat();
        doFormatting();

    }

    /**
     * Reset to the default message format using the ResourceBundle mechanism.
     * @see java.util.ResourceBundle
     * @see java.util.ListResourceBundle
     */
    public void resetFormat() {
        Locale locale = locales[localeMenu.getSelectedIndex()];

	ClassLoader classLoader = this.getClass().getClassLoader();

        choice4Num.setText("");
        choice4Text.setText("");

        ResourceBundle choiceResources =
             ResourceBundle.getBundle("ChoiceResource", locale);

	patternText.setText(choiceResources.getString("patternText"));
        choice1Text.setText(choiceResources.getString("choice1"));
        choice2Text.setText(choiceResources.getString("choice2"));
        choice3Text.setText(choiceResources.getString("choice3"));
    }

    /**
     * Create a new format based on the selected type.  For example, a new
     * format needs to be created if a different locale or format type is
     * selected.
     */
    public Format createFormat(Choice typeMenu)
    {
        int type = typeMenu.getSelectedIndex();
        Locale locale = locales[localeMenu.getSelectedIndex()];

        Format result = null;
        if (type == NUMBER) {
            result = NumberFormat.getInstance(locale);
        }
        else if (type == DATE) {
            result = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
        }
        else if (type == CHOICE) {
            result = choiceFormat;    // XXX
        }
        return result;
    }

    /**
     * Create a new format based on the selection changes and update the
     * output text area.
     */
    public void doFormatting() {

        // Create a ChoiceFormat based on the settings in the choice field
        double[] limits = new double[4];
        limits[0] = doubleValue(choice1Num.getText());
        limits[1] = doubleValue(choice2Num.getText());
        limits[2] = doubleValue(choice3Num.getText());
        limits[3] = doubleValue(choice4Num.getText());

        String[] choices = new String[4];
        choices[0] = choice1Text.getText();
        choices[1] = choice2Text.getText();
        choices[2] = choice3Text.getText();
        choices[3] = choice4Text.getText();


        choiceFormat = new ChoiceFormat(limits, choices);    // XXX

        // Create the individual formatters for the items in the pattern....
        Format[] formats = new Format[4];
        formats[0] = createFormat(arg1Type);
        formats[1] = createFormat(arg2Type);
        formats[2] = createFormat(arg3Type);
        formats[3] = createFormat(arg4Type);

        // Now create the Message Formatter itself
        MessageFormat format = new MessageFormat(patternText.getText());

        // Create the array of objects to format....
        Object[] objects = new Object[4];
        objects[0] = createObject(arg1Type, arg1Text);
        objects[1] = createObject(arg2Type, arg2Text);
        objects[2] = createObject(arg3Type, arg3Text);
        objects[3] = createObject(arg4Type, arg4Text);

        String result = null;
        try {
            result = format.format(objects);
        }
        catch (Exception e)
        {
            errorText("format threw an exception: " + e.toString());
            result = "ERROR";
        }
        resultText.setText(result);
    }

  /* ItemListener method */
  public void itemStateChanged(ItemEvent e) {
    if((e.getSource() == arg1Type) || (e.getSource() == arg2Type) || (e.getSource() == arg3Type)
       || (e.getSource() == arg4Type)) {
      doFormatting();
    } else if (e.getSource() == localeMenu) {
      resetFormat();
      doFormatting();
    }
  }
  
  /* KeyListener methods */
  public void keyPressed(KeyEvent e) {
  }

  public void keyReleased(KeyEvent e) {
    if ((e.getSource() == choice1Text) || (e.getSource() == choice2Text) || 
	(e.getSource() == choice3Text) || (e.getSource() == choice4Text)) {
      e.consume();
      doFormatting();
    } else if ((e.getSource() == choice1Num) ||(e.getSource() == choice2Num) ||
	       (e.getSource() == choice3Num) ||(e.getSource() == choice4Num)) {
      e.consume();
      doFormatting();
    } else if ((e.getSource() == arg1Text) ||(e.getSource() == arg2Text) ||
	       (e.getSource() == arg3Text) ||(e.getSource() == arg4Text)) {
      e.consume();
      doFormatting();
    } else if (e.getSource() == patternText) {
      e.consume();
      doFormatting();
    }
      
  }

  public void keyTyped(KeyEvent e) {
  }
  
  /* Window Listener methods */
  public void windowClosed(WindowEvent e) {
  }

  public void windowDeiconified(WindowEvent e) {
  }

  public void windowIconified(WindowEvent e) {
  }

  public void windowActivated(WindowEvent e) {
  }

  public void windowDeactivated(WindowEvent e) {
  }

  public void windowOpened(WindowEvent e) {
  }

  public void windowClosing(WindowEvent e) {
    setVisible(false);
    dispose();

    if (applet != null) {
      applet.demoClosed();
    } else System.exit(0);

  }

    //------------------------------------------------------------
    // package private
    //------------------------------------------------------------

    double doubleValue(String s) {
        try {
            return Double.valueOf(s).doubleValue();
        } catch (Exception foo) {
            return Double.POSITIVE_INFINITY;
        }
    }

    void constrainedAdd(GridBagConstraints c,
                        Container container,
                        Component foo,
                        Font font) {
        GridBagLayout gridbag = (GridBagLayout)container.getLayout();
        if (font != null)
            foo.setFont(font);
        gridbag.setConstraints(foo, c);
        container.add(foo);
    }

    Choice cloneChoice (Choice source) {
        Choice result = new Choice();
        for (int i = 0; i < source.getItemCount(); ++i)
            result.addItem(source.getItem(i));
        result.setFont(source.getFont());
        return result;
    }


    void addWithFont(Container container, Component foo, Font font) {
        if (font != null)
            foo.setFont(font);
        container.add(foo);
    }

    //{{DECLARE_CONTROLS
    Label label1;
    Label label2;
    TextField patternText;
    Label label3;
    Label label4;
    Label label5;
    Label labelArg;
    Label labelForm;
    TextField arg1Text;
    TextField arg2Text;
    TextField arg3Text;
    Choice arg1Type;
    Choice arg2Type;
    Choice arg3Type;
    Label label6;
    Choice localeMenu;
    Label localeLabel;
    Label label13;
    Label label14;
    TextField resultText;
    Label label7;
    Label label8;
    Label label9;
    TextField choice1Num;
    TextField choice2Num;
    TextField choice3Num;
    TextField choice4Num;
    TextField choice1Text;
    TextField choice2Text;
    TextField choice3Text;
    TextField choice4Text;
    Label label10;
    Label label11;
    Label label12;
    TextField arg4Text;
    Choice arg4Type;
    //}}

    //------------------------------------------------------------
    // private
    //------------------------------------------------------------
    private void buildGUI() {

        //{{INIT_CONTROLS
        setLayout(new FlowLayout(FlowLayout.CENTER,2,2));   // MD 8/7
        setBackground(Color.white); // MD 8/7

        // Main Title

        Panel titleCreditPanel = new Panel();
        label6=new Label("Message Format Demo", Label.CENTER);
        label6.setFont(Utility.titleFont);
        titleCreditPanel.add(label6);

        Panel creditPanel = new Panel();
        label13=new Label(creditString);
        label13.setFont(Utility.creditFont);
        creditPanel.add(label13);

        titleCreditPanel.add(creditPanel);

        Utility.fixGrid(titleCreditPanel,1);

        // result text

        Panel patternResultPanel = new Panel();

        addWithFont(patternResultPanel,new
            Label("Result", Label.RIGHT),Utility.labelFont);
        addWithFont(patternResultPanel,resultText= new
            TextField(FIELD_COLUMNS),Utility.editFont);

        addWithFont(patternResultPanel,new
            Label("Pattern", Label.RIGHT),Utility.labelFont);
        addWithFont(patternResultPanel,patternText=new
            TextField(FIELD_COLUMNS),Utility.editFont);

	patternText.addKeyListener(this);
        Utility.fixGrid(patternResultPanel,2);

        // Locale and credits

        Panel localeCreditPanel = new Panel();
        // localeCreditPanel.setLayout(new GridBagLayout());

        localeLabel=new Label("Locale:",Label.LEFT);
        localeLabel.setFont(Utility.labelFont);
        //localeCreditPanel.add("loc",localeLabel);

        // LOCALE
        localeMenu= new Choice();
	localeMenu.addItemListener(this);
        // Stick the names of the locales into the locale popup menu
        Locale displayLocale = Locale.getDefault();
        for (int i = 0; i < locales.length; i++) {
            if (locales[i].getCountry().length() > 0) {
                localeMenu.addItem( locales[i].getDisplayName() );
                if (locales[i].equals(Locale.getDefault())) {
                    localeMenu.select(i);
                }
            }
        }
           localeMenu.setFont(Utility.choiceFont);

        Panel localePanel=new Panel();
        localePanel.add(localeLabel);
        localePanel.add(localeMenu);
        localeCreditPanel.add(localePanel);

        arg1Type= new Choice();
           arg1Type.setFont(Utility.choiceFont);
        arg1Type.addItem("Number");
        arg1Type.addItem("Date");
        arg1Type.addItem("Choice");
        arg1Type.addItem("None");
	arg1Type.addItemListener(this);

        // PUT THE ARGUMENTS/ FORMATS into GRID
        Panel allArgs = new Panel();

        addWithFont(allArgs,new Label(" "),Utility.labelFont);
        addWithFont(allArgs,new Label("Arguments", Label.LEFT),
                                      Utility.labelFont);
        addWithFont(allArgs,new Label("0",Label.RIGHT),Utility.labelFont);
        addWithFont(allArgs,arg1Text=new TextField(12),Utility.editFont);
        addWithFont(allArgs,new Label("1",Label.RIGHT),Utility.labelFont);
        addWithFont(allArgs,arg2Text=new TextField(12),Utility.editFont);
        addWithFont(allArgs,new Label("2",Label.RIGHT),Utility.labelFont);
        addWithFont(allArgs,arg3Text=new TextField(12),Utility.editFont);
        addWithFont(allArgs,new Label("3",Label.RIGHT),Utility.labelFont);
        addWithFont(allArgs,arg4Text=new TextField(12),Utility.editFont);

	arg1Text.addKeyListener(this);
	arg2Text.addKeyListener(this);
	arg3Text.addKeyListener(this);
	arg4Text.addKeyListener(this);

        Utility.fixGrid(allArgs,2);

        Panel formatPanel = new Panel();
        addWithFont(formatPanel,new Label(" "),Utility.labelFont);
        addWithFont(formatPanel,new Label("Formats", Label.LEFT),
                    Utility.labelFont);

        addWithFont(formatPanel,new Label("0",Label.RIGHT),Utility.labelFont);
        addWithFont(formatPanel,arg1Type,Utility.choiceFont);

        addWithFont(formatPanel,new Label("1",Label.RIGHT),Utility.labelFont);
        addWithFont(formatPanel,arg2Type = cloneChoice(arg1Type),
                    Utility.choiceFont);

        addWithFont(formatPanel,new Label("2",Label.RIGHT),Utility.labelFont);
        addWithFont(formatPanel,arg3Type = cloneChoice(arg1Type),
                    Utility.choiceFont);

        addWithFont(formatPanel,new Label("3",Label.RIGHT),Utility.labelFont);
        addWithFont(formatPanel,arg4Type = cloneChoice(arg1Type),
                    Utility.choiceFont);

	arg2Type.addItemListener(this);
	arg3Type.addItemListener(this);
	arg4Type.addItemListener(this);

        Utility.fixGrid(formatPanel,2);

        // Choices panel

        Panel choicesPanel = new Panel();
        addWithFont(choicesPanel,new Label(">=", Label.LEFT),
                    Utility.labelFont);
        addWithFont(choicesPanel,new Label("Choice Strings", Label.LEFT),
                    Utility.labelFont);

        addWithFont(choicesPanel,choice1Num=new TextField(4),
                    Utility.editFont);
        addWithFont(choicesPanel,choice1Text=new TextField(16),
                    Utility.editFont);
        addWithFont(choicesPanel,choice2Num=new TextField(4),
                    Utility.editFont);
        addWithFont(choicesPanel,choice2Text=new TextField(16),
                    Utility.editFont);
        addWithFont(choicesPanel,choice3Num=new TextField(4),
                    Utility.editFont);
        addWithFont(choicesPanel,choice3Text=new TextField(16),
                    Utility.editFont);
        addWithFont(choicesPanel,choice4Num=new TextField(4),
                    Utility.editFont);
        addWithFont(choicesPanel,choice4Text=new TextField(16),
                    Utility.editFont);

	choice1Text.addKeyListener(this);
	choice2Text.addKeyListener(this);
	choice3Text.addKeyListener(this);
	choice4Text.addKeyListener(this);
	choice1Num.addKeyListener(this);
	choice2Num.addKeyListener(this);
	choice3Num.addKeyListener(this);
	choice4Num.addKeyListener(this);

        Utility.fixGrid(choicesPanel,2);
        add(titleCreditPanel);
        add(patternResultPanel);
        add(localeCreditPanel);
        Panel bottomPanel = new Panel();
        bottomPanel.add(allArgs);

        XBorderPanel x = new XBorderPanel(); // MD 8/7
        x.setBackground(Color.lightGray);
        x.setLayout(null);
        x.setSize(8,130);
        bottomPanel.add(x);

        bottomPanel.add(formatPanel);

        XBorderPanel x1 = new XBorderPanel(); // MD 8/7
        x1.setBackground(Color.lightGray);
        x1.setLayout(null);
        x1.setSize(8,130);
        bottomPanel.add(x1);

        bottomPanel.add(choicesPanel);
        Utility.fixGrid(bottomPanel,5);
        // MD 8/7 only after fixGrid
        Utility.setInsets(bottomPanel,x,new Insets(20,20,2,2));
        Utility.setInsets(bottomPanel,x1,new Insets(20,20,2,20));

        add(bottomPanel);

        Panel copyrightPanel = new Panel();
        addWithFont (copyrightPanel,new Label(copyrightString, Label.LEFT),
                     Utility.creditFont);
        addWithFont (copyrightPanel,new Label(copyrightString2, Label.LEFT),
                     Utility.creditFont);
        Utility.fixGrid(copyrightPanel,1);
        add(copyrightPanel);

    }

    private Object createObject(Choice typeMenu, TextField textField )
    {
        int type = typeMenu.getSelectedIndex();
        String text = textField.getText();

        Object result = null;

        try {
            if (type == NUMBER || type == CHOICE)
            {
                result = new Double(text);
            }
            else if (type == DATE)
            {
	      // Still use the deprecated new Date(text) until 
	      // the DateFormat.parse(text) is working properly.
	      result = new Long( (new Date(text)).getTime() + 1);
	      // 1 millisecond was added to display the date correctly.
	      // This is done to fix the following scenario, eg,
	      // "27 Sept 96" ==> "26 Sept 96 12:00 AM PDT" which is
	      // equvalent to "27 Sept 96 00:00 AM PDT".  -- CLH 9/27/96
            }
            else if (type == NONE)
            {
                result = text;
            }
        }
        catch (RuntimeException e) {
        }
        return result;
    }

    private void errorText(String s)
    {
        if (DEBUG)
        {
           System.out.println(s);

        }
    }
    private static final String creditString =
        "v1.1a7, Demos";
    private static final String copyrightString =
        "";
    private static final String copyrightString2 =
        "";
    private static final int FIELD_COLUMNS = 60;

    static private final int NUMBER = 0;
    static private final int DATE = 1;
    static private final int CHOICE = 2;
    static private final int NONE = 3;

    private static final boolean DEBUG = false;

    private Locale[] locales;

    private DemoApplet applet;

    private ChoiceFormat choiceFormat;    // XXX
}

// MD 8/7 whole class, from Ralf. Use different name!
class XBorderPanel extends Panel
{
   /**
    * Panel shadow border width
    */
   protected int shadow = 4;

   /**
    * Panel raised vs depressed look
    */
   protected boolean raised = true;

    public XBorderPanel() {
        this.raised=true;
    }

    public XBorderPanel(boolean raised) {
        this.raised=raised;
    }


   /**
    * Re-layout parent. Called when a panel changes
    * size etc.
    */
   protected void layoutParent() {
      Container parent = getParent();
      if (parent != null) {
     parent.doLayout();
      }
   }

   public void paint(Graphics g) {
        super.paint(g);
        Dimension size = getSize();
        paintBorder(g, size);
    }

   protected void paintBorder(Graphics g, Dimension size) {
      Color c = getBackground();
      g.setColor(c);
      g.fillRect(0, 0, size.width, size.height);
      draw3DRect(g, 0, 0, size.width, size.height, raised);
   }

   /**
    * Draw a 3D Rectangle.
    * @param g the specified Graphics window
    * @param x, y, width, height
    * @param raised - true if border should be painted as raised.
    * @see #paint
    */
   public void draw3DRect(Graphics g, int x, int y, int width, int height,
              boolean raised) {
      Color c = g.getColor();
      Color brighter = avgColor(c,Color.white);
      Color darker = avgColor(c,Color.black);


      // upper left corner
      g.setColor(raised ? brighter : darker);
      for (int i=0; i<shadow; i++) {
      g.drawLine(x+i, y+i, x+width-1-i, y+i);
      g.drawLine(x+i, y+i, x+i, y+height-1-i);
      }
      // lower right corner
      g.setColor(raised ? darker : brighter);
      for (int i=0; i<shadow; i++) {
      g.drawLine(x+i, y+height-1-i, x+width-1-i, y+height-1-i);
      g.drawLine(x+width-1-i, y+height-1-i, x+width-1-i, y+i);
      }
      g.setColor(c);
      // added by rip.
      g.setColor(Color.black);
      g.drawRect(x,y,width+2,height+2);

   }

   public static Color avgColor(Color c1, Color c2) {
    return new Color(
        (c1.getRed()+c2.getRed())/2,
        (c1.getGreen()+c2.getGreen())/2,
        (c1.getBlue()+c2.getBlue())/2
        );
   }

}

/*
 * @(#)DateTimeDemo.java	1.1 96/11/23
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
import java.lang.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.text.*;


/**
 * DateTimeDemo demonstrates how Date/Time formatter works. 
 */
public class DateTimeDemo extends DemoApplet
{
    /**
     * The main function which defines the behavior of the DateTimeDemo
     * applet when an applet is started.
     */
    public static void main(String argv[]) {
        DemoApplet.showDemo(new DateTimeFrame(null));
    }

    /**
     * This creates a DateTimeFrame for the demo applet.
     */
    public Frame createDemoFrame(DemoApplet applet) {
        return new DateTimeFrame(applet);
    }
}

/**
 * A Frame is a top-level window with a title. The default layout for a frame
 * is BorderLayout.  The DateTimeFrame class defines the window layout of
 * DateTimeDemo.
 */
class DateTimeFrame extends Frame implements WindowListener, ActionListener, ItemListener, KeyListener
{
    private static final String creditString
        = "";
    private static final String copyrightString
        = "";
    private static final String copyrightString2
        = "";

    private static final int FIELD_COLUMNS = 45;

    private static final boolean DEBUG = false;

    private static final int millisPerHour = 60 * 60 * 1000;
    private boolean isLocalized = false;
    private boolean validationMode = false;
    private Locale curLocale = Locale.US;

    private SimpleDateFormat format;
    private Locale[] locales;
    private DemoApplet applet;

    // Mapping tables for displaying Rep. Cities for given timezones.
    private static final int kAdjCityIndex[]
    // many-to-1 mapping:
    // locale index --> rep city index
    = { 1,
        3,
        2,
        4,
        0,
        5,  // eg, Locale Index: 5 --> Rep. City index: 5
	0,
	0,
        6};

    private static final int kZoneOffsets[]
    // 1-to-1 maping:
    // kZoneOffsets returns the zone offset for a given rep. city index.
    = { 1*millisPerHour,
       -8*millisPerHour,
                      0,
       -5*millisPerHour,
       -7*millisPerHour,
       -6*millisPerHour,
        9*millisPerHour};

    private static final String kZoneIDs[]
    // 1-1 maping:
    // kZoneIDs returns the zone ID string for a given rep. city index.
    = {"ECT",
       "PST",
       "GMT",
       "EST",
       "MST",
       "CST",
       "JST"};


    /**
     * Constructs a new DateTimeFrame that is initially invisible.
     */
    public DateTimeFrame(DemoApplet applet)
    {
        super("Date/Time Formatting Demo");
        this.applet = applet;
	addWindowListener(this);
        init();
        start();
    }


    /**
     * Initializes the applet. You never need to call this directly, it
     * is called automatically by the system once the applet is created.
     */
    public void init()
    {
        // Get G7 locales only for demo purpose. To get all the locales
        // supported, switch to calling TimeFormat.getAvailableLocales().
        // commented.  However, the mapping tables such as kAdjCityIndex
        // must be expended as well.
        locales = Utility.getG7Locales();
//        locales = TimeFormat.getAvailableLocales();

        buildGUI();

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

        // For starters, use the default format for the selected locale
        // in the menu
        setFormatFromLocale(true);
        formatText();
    }

    //------------------------------------------------------------
    // package private
    //------------------------------------------------------------
    void addWithFont(Container container, Component foo, Font font) {
        if (font != null)
            foo.setFont(font);
        container.add(foo);
    }

    /**
     * Called to start the applet. You never need to call this method
     * directly, it is called when the applet's document is visited.
     */
    public void start()
    {
        // do nothing
    }

    /**
     * This function is called when it is necessary to display a new
     * format pattern. This happens when the state of the "Localized Pattern"
     * CheckBox is changed.
     */
    public void handleNewFormat()
    {
        Utility.setText(patternText, format.toPattern() );
    }

    /**
     * This function is called when users change the Calendar (time fields)
     * validation mode. When the state of the "Validation Mode" CheckBox
     * is changed, the time string in the "New Date" text field will be
     * re-parsed, and the parsing result will be displayed in the "1.0 Date"
     * text field.
     */
    public void validationModeChanged()
    {
        format.setLenient(validationMode);

        parseText();
    }

    //{{DECLARE_CONTROLS
    Panel localePanel;
    Panel formatPanel;
    CheckboxGroup group1;
    CheckboxGroup group2;
    Label label1;
    Label label2;
    Label label3;
    Label demo;
    Label code;
    Choice localeMenu;
    Choice dateStyleMenu;
    Choice timeStyleMenu;
    Choice dateMenu;
    Choice cityMenu;
    Label dateLabel;
    Label cityLabel;
    TextField millisText;
    Label millisLabel;
    Button up;
    Button down;
    Label localeLabel;
    Label dateStyleLabel;
    Label timeStyleLabel;
    TextField inputText;
    TextField outputText;
    Label formatLabel;
    Label parseLabel;
    //Button rightButton;
    //Button leftButton;
    TextField patternText;
    Label label4;
    Checkbox getDateInstance;
    Checkbox getTimeInstance;
    Checkbox getDateTimeInstance;
    Checkbox getRoll;
    Checkbox getAdd;
    Checkbox getLocalized;
    Checkbox getValidationMode;

    //}}

    Color color = Color.white;
    public void buildGUI()
    {
        //{{INIT_CONTROLS

        setBackground(color);
        setLayout(new FlowLayout()); // shouldn't be necessary, but it is.

// TITLE
        Panel titlePanel = new Panel();


        label1=new Label("Date/Time Formatting Demo", Label.CENTER);
        label1.setFont(Utility.titleFont);

        titlePanel.add(label1);

// CREDITS

        Panel creditPanel = new Panel();

        demo=new Label(creditString, Label.CENTER);
        demo.setFont(Utility.creditFont);
        creditPanel.add(demo);

        titlePanel.add(creditPanel);

        Utility.fixGrid(titlePanel,1);
         add(titlePanel);


// IO Panel
        Panel topPanel = new Panel();
        topPanel.setLayout(new FlowLayout());

        label3=new Label("New Date", Label.RIGHT);
        label3.setFont(Utility.labelFont);
        topPanel.add(label3);

        outputText=new TextField(FIELD_COLUMNS);
	outputText.addKeyListener(this);
        outputText.setFont(Utility.editFont);
        topPanel.add(outputText);


        label2=new Label("1.0 Date", Label.RIGHT);
        label2.setFont(Utility.labelFont);
        topPanel.add(label2);

	// intentional use of deprecated method
        inputText=new TextField(new Date().toGMTString(),FIELD_COLUMNS);
	inputText.addKeyListener(this);
        inputText.setFont(Utility.editFont);
        topPanel.add(inputText);


        millisLabel=new Label("Millis", Label.RIGHT);
        millisLabel.setFont(Utility.labelFont);
        topPanel.add(millisLabel);

        millisText=new TextField(FIELD_COLUMNS);
	millisText.addKeyListener(this);
        millisText.setFont(Utility.editFont);
        topPanel.add(millisText);

        label4=new Label("Pattern", Label.RIGHT);
        label4.setFont(Utility.labelFont);
        topPanel.add(label4);

        patternText=new TextField(FIELD_COLUMNS);
	patternText.addKeyListener(this);
        patternText.setFont(Utility.editFont);
        topPanel.add(patternText);

        topPanel.add(new Label(" "));

        getLocalized=new Checkbox("Localized Pattern");
	getLocalized.addItemListener(this);
        getLocalized.setFont(Utility.labelFont);

        getValidationMode=new Checkbox("Validation Mode");
	getValidationMode.addItemListener(this);
        getValidationMode.setFont(Utility.labelFont);

        Panel checkBoxesPanel = new Panel();
        checkBoxesPanel.setLayout(new GridLayout(1,2,40,0));
        checkBoxesPanel.add(getLocalized);
        checkBoxesPanel.add(getValidationMode);

        topPanel.add(checkBoxesPanel);

        Utility.fixGrid(topPanel,2);
         add(topPanel);

// DATE

        Panel datePanel=new Panel();
        datePanel.setLayout(new FlowLayout());

        group2= new CheckboxGroup();

        getRoll=new Checkbox("Roll",group2, true);
        getAdd=new Checkbox("Add",group2, false);
	getRoll.addItemListener(this);
	getAdd.addItemListener(this);

        dateLabel=new Label("Date Fields");
        dateLabel.setFont(Utility.labelFont);

        Panel upDown = new Panel();
        upDown.setLayout(new GridLayout(2,1));

        // *** If the images are not found, we use the label.
        up = new Button("^");
        down = new Button("v");
	up.addActionListener(this);
	down.addActionListener(this);
        up.setBackground(Color.lightGray);
        down.setBackground(Color.lightGray);
        upDown.add(up);
        upDown.add(down);


        Panel rollAddBoxes = new Panel();
        rollAddBoxes.setLayout(new GridLayout(2,1));

        rollAddBoxes.add(getRoll);
        rollAddBoxes.add(getAdd);

        Panel rollAddPanel = new Panel();
        rollAddPanel.setLayout(new FlowLayout());
        rollAddPanel.add(rollAddBoxes);
        rollAddPanel.add(upDown);

        dateMenu= new Choice();
        dateMenu.addItem( "Year");
        dateMenu.addItem( "Month");
        dateMenu.addItem( "Day of Month");
        dateMenu.addItem( "Hour of Day");
        dateMenu.addItem( "Minute");
        dateMenu.addItem( "Second");
        dateMenu.addItem( "Millisecond");

        Panel dateLM = new Panel();
        dateLM.setLayout(new GridLayout(2,1));
        dateLM.add(dateLabel);
        dateLM.add(dateMenu);

        datePanel.add(dateLM);

// CITIES

        Panel citiesPanel=new Panel();
        citiesPanel.setLayout(new FlowLayout());
        Panel cityPanel=new Panel();
        cityPanel.setLayout(new GridLayout(2,1));
        cityMenu= new Choice();
	cityMenu.addItemListener(this);
        cityMenu.addItem( "Paris");
        cityMenu.addItem( "Copenhagen");
        cityMenu.addItem( "London");
        cityMenu.addItem( "Washington");
        cityMenu.addItem( "Toronto");
        cityMenu.addItem( "Montreal");
        cityMenu.addItem( "Tokyo");

        cityLabel=new Label("City");
        cityLabel.setFont(Utility.labelFont);

        cityPanel.add(cityLabel);
        cityPanel.add(cityMenu);
        citiesPanel.add(cityPanel);

        Panel cityDatePanel = new Panel();
        cityDatePanel.add(citiesPanel);
        cityDatePanel.add(datePanel);
        cityDatePanel.add(rollAddPanel);    // choices
        Utility.fixGrid(cityDatePanel,1);
         add(cityDatePanel);

// BORDER
        // true means raised, false = depressed
        BorderPanel borderPanel = new BorderPanel(true);
        borderPanel.setBackground(Color.lightGray);
        borderPanel.setLayout(null);
        borderPanel.setSize(8,150);
         add(borderPanel);

// LOCALE

        // sets up localePanel
        localePanel=new Panel();
        localePanel.setLayout(new GridLayout(2,1));

        localeLabel=new Label("Locale");
        localeLabel.setFont(Utility.labelFont);
        localeMenu= new Choice();
	localeMenu.addItemListener(this);

        localePanel.add("loc",localeLabel);
        localePanel.add(localeMenu);

        // sets up formatPanel
        formatPanel=new Panel();

        group1= new CheckboxGroup();
        getDateInstance=new Checkbox("Date Format",group1, false);
        getTimeInstance=new Checkbox("Time Format",group1, false);
        getDateTimeInstance=new Checkbox("Date and Time Format",group1, true);

	getDateInstance.addItemListener(this);
	getTimeInstance.addItemListener(this);
	getDateTimeInstance.addItemListener(this);

        Panel formatButtons = new Panel();
        formatButtons.setLayout(new GridLayout(3,1));

        formatButtons.add(getDateInstance);
        formatButtons.add(getTimeInstance);
        formatButtons.add(getDateTimeInstance);

        Panel dateStylePanel=new Panel();
        dateStylePanel.setLayout(new GridLayout(2,1));
        dateStyleLabel=new Label("Date Style");
        dateStyleLabel.setFont(Utility.labelFont);
        dateStyleMenu= new Choice();
	dateStyleMenu.addItemListener(this);
        dateStyleMenu.addItem("Full");
        dateStyleMenu.addItem("Long");
        dateStyleMenu.addItem("Default");
        dateStyleMenu.addItem("Short");
        dateStylePanel.add("loc",dateStyleLabel);
        dateStylePanel.add(dateStyleMenu);

        Panel timeStylePanel=new Panel();
        timeStylePanel.setLayout(new GridLayout(2,1));
        timeStyleLabel=new Label("Time Style");
        timeStyleLabel.setFont(Utility.labelFont);
        timeStyleMenu= new Choice();
	timeStyleMenu.addItemListener(this);
        timeStyleMenu.addItem("Full");
        timeStyleMenu.addItem("Long");
        timeStyleMenu.addItem("Default");
        timeStyleMenu.addItem("Short");
        timeStylePanel.add("loc",timeStyleLabel);
        timeStylePanel.add(timeStyleMenu);

        Panel dtStylePanel = new Panel();
        dtStylePanel.setLayout(new GridLayout(1,2,20,0));
        dtStylePanel.add(dateStylePanel);
        dtStylePanel.add(timeStylePanel);

        formatPanel.add(formatButtons);
        formatPanel.add(dtStylePanel);
        Utility.fixGrid(formatPanel,1);

        Panel localesFormatPanel = new Panel();

        localesFormatPanel.add(localePanel);
        localesFormatPanel.add(formatPanel);
        Utility.fixGrid(localesFormatPanel,1);
         add(localesFormatPanel);

        Panel copyrightPanel = new Panel();
        addWithFont (copyrightPanel,new Label(copyrightString, Label.LEFT),
            Utility.creditFont);
        addWithFont (copyrightPanel,new Label(copyrightString2, Label.LEFT),
            Utility.creditFont);
        Utility.fixGrid(copyrightPanel,1);
         add(copyrightPanel);

        //}}

    }


  /* ActionListener method */
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == up){
      dateFieldChanged(true);
    } else if (e.getSource() == down) {
      dateFieldChanged(false);
    }
  }

  /* ItemListener method */
  public void itemStateChanged(ItemEvent e) {
    if (e.getSource() == localeMenu) {
      selectedLocaleMenu();
    } else if (e.getSource() == dateStyleMenu) {
      selectedDateStyleMenu();
    } else if (e.getSource() == timeStyleMenu) {
      selectedTimeStyleMenu();
    } else if (e.getSource() == cityMenu) {
      cityChanged();
    } else if (e.getSource() == getRoll) {
      clickedGetRoll();
    } else if (e.getSource() == getAdd) {
      clickedGetAdd();
    } else if (e.getSource() == getLocalized) {
      isLocalized = getLocalized.getState();
      handleNewFormat();
    } else if (e.getSource() == getValidationMode) {
      validationMode = getValidationMode.getState();
      validationModeChanged();      
    } else if (e.getSource() == getDateInstance) {
      clickedGetDateFormat();
    } else if (e.getSource() == getTimeInstance) {
      clickedGetTimeFormat();
    } else if (e.getSource() == getDateTimeInstance) {
      clickedGetDateTimeFormat();
    }
 
  }
  
  /* KeyListener methods */
  public void keyPressed(KeyEvent e) {
  }

  public void keyReleased(KeyEvent e) {
    if (e.getSource() == patternText) {
      e.consume();
      patternTextChanged();
    } else if (e.getSource() == inputText) {
      e.consume();
      formatText();
    } else if (e.getSource() == outputText) {
      e.consume();
      parseText();
    } else if (e.getSource() == millisText) {
      e.consume();
      millisChanged();
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


    /**
     * This function is called when users select a new time and/or date
     * format pattern, or a new locale.
     */
    public void setFormatFromLocale(boolean localChanged) {
        int localeIndex = localeMenu.getSelectedIndex();
        int dateStyleIndex = dateStyleMenu.getSelectedIndex()
                             + DateFormat.FULL;
        int timeStyleIndex = timeStyleMenu.getSelectedIndex()
                             + DateFormat.FULL;
        if (localChanged)
            // Find the locale corresponding to the selected menu item
            curLocale = locales[localeIndex];

        if (getDateInstance.getState()) {
            format =
            (SimpleDateFormat) DateFormat.getDateInstance(dateStyleIndex,
                                                        curLocale);
        } else if (getTimeInstance.getState()) {
            format =
            (SimpleDateFormat) DateFormat.getTimeInstance(timeStyleIndex,
                                                        curLocale);
        } else {
            format =
            (SimpleDateFormat) DateFormat.getDateTimeInstance(dateStyleIndex,
                                                            timeStyleIndex,
                                                            curLocale);
        }

        patternText.setText( format.toPattern() );
        if (!localChanged)
        {
            // locale not changed, only pattern format is changed.
            setMillisText();
            millisFormat();
        }
        else // change to selecting a rep. city based on new locale selected
        {
            cityMenu.select(kAdjCityIndex[localeIndex]);
            cityChanged();
        }
    }

    /**
     * This function is called when users change the pattern text.
     */
    public void setFormatFromPattern() {
        String timePattern = patternText.getText();
        format.applyPattern(timePattern);
        millisFormat();
        millisParse();
    }

    private boolean add = false;

    /**
     * This function is called when the "Roll" radio button is selected.
     */
    public  void clickedGetRoll() {
        add=false;
    }

    /**
     * This function is called when the "Add" radio button is selected.
     */
    public void clickedGetAdd() {
        add=true;
    }

    /**
     * This function is called when the "Date Format" radio button is selected.
     */
    public void clickedGetDateFormat() {
        setFormatFromLocale(false);
    }

    /**
     * This function is called when the "Time Format" radio button is selected.
     */
    public void clickedGetTimeFormat() {
        setFormatFromLocale(false);
    }

    /**
     * This function is called when the "Date and Time Format" radio button
     * is selected.
     */
    public void clickedGetDateTimeFormat() {
        setFormatFromLocale(false);
    }

    /**
     * This function is called when a new locale is selected.
     */
    public void selectedLocaleMenu() {
        setFormatFromLocale(true);
    }

    /**
     * This function is called when a new Date (Format) Style is selected.
     */
    public void selectedDateStyleMenu() {
        setFormatFromLocale(false);
    }

    /**
     * This function is called when a new Time (Format) Style is selected.
     */
    public void selectedTimeStyleMenu() {
        setFormatFromLocale(false);
    }

    /**
     * Store the current time in milliseconds.
     */
    long time = System.currentTimeMillis();

    /**
     * This function is called when it is necessary to parse the time
     * string in the "1.0 Date" text field.
     */
    public void formatText() {
        String leftString = inputText.getText();
        if (leftString.length() == 0)
        {
            errorText("Error: no input to format!");
            return;
        }

        try {
	  // intentional use of deprecated method
	  time = Date.parse(leftString); 
        }
        catch (Exception e) {
            outputText.setText("ERROR");
            errorText("Exception: Date.parse: "+leftString);
            return;
        }
        setMillisText();
        millisFormat();
    }

    /**
     * This function is called when it is necessary to parse the time
     * string in the "New Date" text field.
     */
    public void parseText() {
        String rightString = outputText.getText();

        ParsePosition status = new ParsePosition(0);

        if (rightString.length() == 0)
        {
            errorText("Error: no input to parse!");
            return;
        }

        try {
            time = format.parse(rightString, status).getTime();
        }
        catch (Exception e) {
            inputText.setText("ERROR");
            errorText("Exception: parse: "+rightString);
            return;
        }
        setMillisText();
        millisParse();

    }

    /**
     * This function is called when it is necessary to format the time
     * in the "Millis" text field.
     */
    public void millisFormat() {
        String out = "";
        try {
            out = format.format(new Date(time));
        }
        catch (Exception e) {
            outputText.setText("ERROR");
            errorText("Exception: format: "+time);
            return;
        }
        outputText.setText( out );
        errorText("Formatted...");
    }

    /**
     * This function is called when it is necessary to display the time
     * value parsed using GMT string in the "1.0 Date" text field.
     */
    public void millisParse() {
        String input = "";
        try {
	  // intentional use of deprecated method
	  input = new Date(time).toGMTString(); 
            }
        catch (Exception e) {
            inputText.setText("ERROR");
            errorText("Exception: in toGMTString: "+time); 
            return;
        }
        inputText.setText( input );
        errorText("Parsed...");
    }

    /**
     * This function is called when the time value in the "Millis" text field
     * is changed. The new time value will be formatted and displayed in both
     * "New Date" and "1.0 Date" text fields.
     */
    public void millisChanged() {
        String millisString = millisText.getText();
        try {
            time = Long.parseLong(millisString);
            }
        catch (Exception e) {
            errorText("Exception: Bad value for millis. Must be Long");
            return;
        }
        millisFormat();
        millisParse();
        errorText("Millis changed...");
    }

    /**
     * This function is called when it is necessary to display the time
     * value in milliseconds in the "Millis" text field.
     */
    public void setMillisText() {
        millisText.setText(Long.toString(time));
    }

    /**
     * This function is called when users change the pattern text.
     */
    public void patternTextChanged() {
        setFormatFromPattern();
    }

    /**
     * This function is called when users select a new representative city.
     */
    public void cityChanged() {
        int index = cityMenu.getSelectedIndex();

        SimpleTimeZone timeZone = new SimpleTimeZone(kZoneOffsets[index],
                                                     kZoneIDs[index]);
        timeZone.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY,
            2 * millisPerHour);
        timeZone.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY,
            2 * millisPerHour);

        format.setTimeZone(timeZone);

        millisFormat();
        millisParse();
    }

    private boolean addMode() {
        return add;
    }

    /**
     * This function is called when users select a new time field
     * to add or roll its value.
     */
    public void dateFieldChanged(boolean up) {
        String d = dateMenu.getSelectedItem();
        byte field = 0;

        if (d.equals("Year")) {
            field = (byte) Calendar.YEAR;
        } else
        if (d.equals("Month")) {
            field = (byte) Calendar.MONTH;
        } else
        if (d.equals("Day of Month")) {
            field = (byte) Calendar.DATE;
        } else
        if (d.equals("Hour of Day")) {
            field = (byte) Calendar.HOUR_OF_DAY;
        } else
        if (d.equals("Minute")) {
            field = (byte) Calendar.MINUTE;
        } else
        if (d.equals("Second")) {
            field = (byte) Calendar.SECOND;
        } else
        if (d.equals("Millisecond")) {
            field = (byte) Calendar.MILLISECOND;
        }

        format.getCalendar().setTime(new Date(time));
	//        format.getCalendar().computeFields();

        if (up) {
            if (addMode()) {
                format.getCalendar().add(field, 1);
            } else {
                format.getCalendar().roll(field, true);
            }
        } else {
            if (addMode()) {
                format.getCalendar().add(field, -1);
            } else {
                format.getCalendar().roll(field, false);
            }
        }

        time = format.getCalendar().getTime().getTime();

        setMillisText();

        millisFormat();

        millisParse();

    }

    /**
     * Print out the error message while debugging this program.
     */
    public void errorText(String s)
    {
        if (DEBUG)
        {
            System.out.println(s);
        }
    }

}



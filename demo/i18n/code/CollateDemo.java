/*
 * @(#)CollateDemo.java	1.1 96/11/23
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
import java.util.Locale;
import java.util.Vector;
import java.text.NumberFormat;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.text.ParseException;

/**
 * Concrete class for demonstrating language sensitive collation.
 * The following is the instruction on how to run the collation demo.
 * <p>
 * ===================
 * <H3>Customization</H3>
 * You can produce a new collation by adding to or changing an existing
 * one.
 * <H4>To show...</H4>
 * <BLOCKQUOTE>You can modify an existing collation to show how this works.
 * By adding items at the end of a collation you override earlier
 * information.
 * Watch how you can make the letter P sort at the end of the
 * alphabet.</BLOCKQUOTE>
 * <H4>Do...</H4>
 * <BLOCKQUOTE>1. Scroll to the end of the Sequence field. After the Z,
 * type
 * &quot;&lt; p , P&quot;. This will put the letter P (with both of its
 * case
 * variants) at the end of the alphabet. Hit the Set Rule button. This creates
 * a new collation with the name &quot;Custom-1&quot; (you could give it a
 * different name by typing in the Collator Name field). When you now look
 * at the Text field, you will see that you have changed the sequence to put
 * <I>Pat</I>
 * at the end. (If you did not have Sort Ascending on, click it
 * now.)</BLOCKQUOTE>
 * <BR>
 * Making P sort at the end may not seem terribly useful, but it is used to
 * make modifications in the sorting sequence for different languages.
 * <H4>To show...</H4>
 * <BLOCKQUOTE>For example, you can add CH as a single letter after C, as
 * in
 * traditional Spanish sorting.</BLOCKQUOTE>
 * <H4>Do...</H4>
 * <BLOCKQUOTE>Enter in the following after Z; &quot;&amp; C < ch , cH, Ch,
 * CH&quot;.
 * Hit the Set Rule button, type in test words in the Text field (such as
 * &quot;czar&quot;,
 * &quot;churo&quot; and &quot;darn&quot;), and select Sort Ascending to
 * see
 * the resulting sort order.</BLOCKQUOTE>
 * <H4>To show...</H4>
 * <BLOCKQUOTE>You can also add other sequences to the collation rules,
 * such as sorting symbols with their alphabetic equivalents.</BLOCKQUOTE>
 * <H4>Do...</H4>
 * <BLOCKQUOTE>1. Scroll to the end of the Sequence field. After the end,
 * type the following list (you can just select this text in your browser and
 * paste it in, to avoid typing). Now type lines in the Text field with these
 * symbols on them, and select Sort Ascending to see the resulting sort
 * order.</BLOCKQUOTE>
 * <UL>
 *   <UL>
 *     <LI>&amp; Asterisk ; *
 *     <LI>&amp; Question-mark ; ?
 *     <LI>&amp; Hash-mark ; #
 *     <LI>&amp; Exclamation-mark ; !
 *     <LI>&amp; Dollar-sign ; $
 *     <LI>&amp; Ampersand ; '&amp';
 *   </UL>
 * </UL>
 * <H4>Details</H4>
 * If you are an advanced user and interested in trying out more rules,
 * here is a brief explanation of how they work. The sequence is a list of
 * rules. Each rule is of two forms:
 * <UL>
 *   <LI>&lt;modifier&gt;
 *   <LI>&lt;relation&gt; &lt;text-argument&gt;
 *   <LI>&lt;reset&gt; &lt;text-argument&gt;
 * </UL>
 * <H5>Modifier</H5>
 * <BLOCKQUOTE>@ Indicates that accents are sorted backwards, as in
 * French</BLOCKQUOTE>
 * <H5>Text-argument</H5>
 * The text can be any number of characters (if you want to include special
 * characters, such as space, use single-quotes around them).
 * <H5>Relation</H5>
 * The relations are the following:
 * <DL>
 *   <DD>&lt;	Greater, as a letter difference (primary)
 *   <DD>;	Greater, as an accent difference (secondary)
 *   <DD>,	Greater, as a case difference (tertiary)
 *   <DD>=	Equal
 *   <DD>&amp;	Reset previous comparison.
 * </DL>
 * <H5>Reset</H5>
 * The &quot;&amp;&quot; is special in that does not put the text-argument
 * into the sorting sequence; instead, it indicates that the <I>next</I>
 * rule is with respect to where the text-argument <I>would be</I> sorted.
 * This sounds more complicated than it is in practice. For example, the
 * following are equivalent ways of expressing the same thing:
 * <UL>
 *   <LI>a &lt; b &lt; c
 *   <LI>a &lt; b &amp; b &lt; c
 *   <LI>a &lt; c &amp; a &lt; b
 * </UL>
 * Notice that the order is important, since the subsequent item goes
 * <I>immediately</I>
 * after the text-argument. The following are <I>not</I> equivalent:
 * <UL>
 *   <LI>a &lt; b &amp; a &lt; c
 *   <LI>a &lt; c &amp; a &lt; b
 * </UL>
 * The text-argument must already be present in the sequence, or some
 * initial substring of the text-argument must be present. (e.g. &quot;a &lt;
 * b&amp; ae &lt; e&quot; is valid since &quot;a&quot; is present in the
 * sequence<I>before</I> &quot;ae&quot; is reset). In this latter case,
 * &quot;ae&quot;
 * is <B><I>not</I></B> entered and treated as a single character; instead,
 * &quot;e&quot; is sorted as if it were expanded to two characters:
 * &quot;a&quot;
 * followed by an &quot;e&quot;.<BR>
 * This difference appears in natural languages: in traditional Spanish
 * &quot;ch&quot;
 * is treated as though it <I>contracts</I> to a single character
 * (expressed
 * as &quot;c &lt; ch &lt; d&quot;), while in traditional German
 * &quot;&auml;&quot;
 * (a-umlaut) is treated as though it <I>expands</I> to two characters
 * (expressed
 * as &quot;a &amp; ae ; &auml; &lt; b&quot;).<BR>
 * <H5>Ignorable Characters</H5>
 * The first rule must start with a relation (the examples we have used are
 * fragments; &quot;a &lt; b&quot; really should be &quot;&lt; a &lt;
 * b&quot;).
 * If, however, the first relation is not &quot;&lt;&quot;, then all the
 * all
 * text-arguments up to the first &quot;&lt;&quot; are ignorable. For
 * example,
 * &quot;, - &lt; a &lt; b&quot; makes &quot;-&quot; an ignorable
 * character,
 * as we saw earlier in the word &quot;black-birds&quot;.<BR>
 * <H5>Accents</H5>
 * The Collator automatically normalizes text internally to separate
 * accents
 * from base characters where possible. So, if you type in an
 * &quot;&auml;&quot;
 * (a-umlaut), after you reset the collation you will see
 * &quot;a\u0308&quot;
 * in the sequence, where \u0308 is the Java syntax for umlaut. The
 * demonstration
 * program uses this syntax instead of just showing the umlaut since many
 * browsers are unable to display the umlaut yet.<BR>
 * <H4>Errors</H4>
 * The following are errors:
 * <UL>
 *   <LI>Two relations in a row (e.g. &quot;a &lt; , b&quot;
 *   <LI>Two text arguments in a row (e.g. &quot;a &lt; b c &lt; d&quot;)
 *   <LI>A reset where the text-argument is not already in the sequence
 * (e.g.&quot;a &lt; b &amp; e &lt; f&quot;)
 * </UL>
 * If you produce one of these errors, then the demonstration will beep at
 * you, and select the offending text (note: on some browsers, the
 * selection will not appear correctly).
 * @version    1.1 11/23/96
 * @author     Kathleen Wilson, Helena Shih
 * @see        java.util.Collator
 * @see        java.util.RuleBasedCollator
 * @see        java.demos.utilities.DemoApplet
 */
public class CollateDemo extends DemoApplet
{
    /**
     * The main function which defines the behavior of the CollateDemo applet
     * when an applet is started.
     */
    public static void main(String argv[]) {
        DemoApplet.showDemo(new CollateFrame(null));
    }

    /**
     * This creates a CollateFrame for the demo applet.
     */
    public Frame createDemoFrame(DemoApplet applet) {
        return new CollateFrame(applet);
    }
}

/**
 * A Frame is a top-level window with a title. The default layout for a frame
 * is BorderLayout.  The CollateFrame class defines the window layout of
 * CollateDemo.
 */
class CollateFrame extends Frame implements WindowListener, ActionListener, ItemListener, KeyListener
{

    /**
     * Constructs a new CollateFrame that is initially invisible.
     */
    public CollateFrame(DemoApplet applet)
    {
        super("Collate Demo");
        this.applet = applet;
	addWindowListener(this);
        init();
        start();
    }

    /**
     * Called if an action occurs in the CollateFrame object.
     */
    public boolean action(Event evt, Object obj)
    {
        return false;
    }

    /**
     * Initializes the applet. You never need to call this directly, it
     * is called automatically by the system once the applet is created.
     */
    public void init()
    {
        theLocale = Locale.US;
        theCollation = Collator.getInstance(theLocale);
        buildGUI();
    }

    /**
     * Called to start the applet. You never need to call this method
     * directly, it is called when the applet's document is visited.
     */
    public void start()
    {
    }

  public void itemStateChanged(ItemEvent e) {
    errorText("");
    if (e.getSource() == sortAscending)
      handleSort(true, false);
    else if (e.getSource() == sortDescending)
      handleSort(false, true);
    else if (e.getSource() == localeChoice) {
      handleLocale();
      handleSort(sortAscending.getState(), sortDescending.getState());
    }
    else if (e.getSource() == decompChoice) {
      handleSort(sortAscending.getState(), sortDescending.getState());
    }
    else if (e.getSource() == strengthChoice) {
      handleSort(sortAscending.getState(), sortDescending.getState());
    }
  }

  public void keyPressed(KeyEvent e) {
    if (e.getSource() == textentry) {
      checkboxes.setSelectedCheckbox(noSort);
    }
  }

  public void keyReleased(KeyEvent e) {
  }

  public void keyTyped(KeyEvent e) {
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == collateRulesButton) {
      errorText("");
      collateRulesButton.setLabel("setting");
      handleSetRules();
      collateRulesButton.setLabel("Set Rules");
      handleSort(sortAscending.getState(),
                 sortDescending.getState());
    }
    
  }

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
     * This function is called when you press the "SortDescend" button.
     * It does an extremely simple sort, using the Collator.compare function.
     * To improve performance, you could transform the strings into
     * a sort key which is a series of characters that can be
     * compared using bit-wise comparison.  In addition, you can also use
     * collation keys to compare two strings based on the collation rules.
     * @see java.util.Collator#compare
     * @see java.util.Collator#getSortKey
     * @see java.util.Collator#getCollationKey
     */

    public boolean handleSort(boolean ascending, boolean descending) {
        if (ascending == descending)
            return true;
        int exchangeResult = ascending ? -1 : 1;

        InitializeListVector();

        String targetItem;
        String sourceItem;
        byte compareResult;


        String strengthName = strengthChoice.getSelectedItem();
        if (strengthName.equals(tertiary)) {
             theCollation.setStrength(Collator.TERTIARY);
        }else if (strengthName.equals(secondary)) {
             theCollation.setStrength(Collator.SECONDARY);
        } else theCollation.setStrength(Collator.PRIMARY);

        int decompItem = decompChoice.getSelectedIndex();
        if (decompItem == 0) {
             theCollation.setDecomposition(Collator.NO_DECOMPOSITION);
        }else if (decompItem == 1) {
             theCollation.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
        } else theCollation.setDecomposition(Collator.FULL_DECOMPOSITION);

        int numItems = textList.size();
        for (int sourceIndex = 1; sourceIndex < numItems; sourceIndex++)
        {
           sourceItem = (String) textList.elementAt(sourceIndex);
           for (int targetIndex = 0; targetIndex < sourceIndex; targetIndex++)
           {
                 targetItem = (String) textList.elementAt(targetIndex);
              compareResult = (byte) theCollation.compare(sourceItem, targetItem);
              if (compareResult == exchangeResult)
              {
                textList.removeElementAt(sourceIndex);
                textList.insertElementAt(sourceItem, targetIndex);
                break;
              }
           }
        }
        resetTextArea();
        return true;
    }

    /**
     * This function is called when you press the Locale button.
     * It sets the locale that is to be used to create the collation object.
     * @see java.util.Collator#getInstance
     */
    public boolean handleLocale()
    {
        int index = localeChoice.getSelectedIndex();

        theCollation = collations[index];
        String theRules = ruleStrings[index];

        if (theRules != null)
        {
             ruleEntry.setText(createUnicodeString
                 (((RuleBasedCollator)theCollation).getRules()));
        } else ruleEntry.setText("");

        return true;
    }

    /**
     * This function is called when you press the "Set Rules" button.
     * It sets the rules that are to be used by the current collation object.
     * @see java.util.RuleBasedCollator#getRules
     */
    public void handleSetRules()
    {
        int index = localeChoice.getSelectedIndex();

        String rules = ruleEntry.getText();
        if ((rules.equals(ruleStrings[index]) == false)
             && (theCollation instanceof RuleBasedCollator) )
        {
            int idx = 0;
            try
            {
                String collName = collationName.getText();
                for (int n = 0; n < localeChoice.getItemCount(); n++)
                {
                    if (collName.equals(localeChoice.getItem(n)))
                    {
                        theCollation = new
                            RuleBasedCollator(convertStringToRules(rules));
                        collations[n] = theCollation;
                        idx = n;
                        break;
                    }
                }
                if (idx == 0)
                {
                    if (collName.startsWith("custom-"))
                    {
                        untitledIndex++;
                    }
                    if (localeChoice.getItemCount() < MAX_COLLATIONS)
                    {
                        idx = localeChoice.getItemCount();
                        theCollation = new
                            RuleBasedCollator(convertStringToRules(rules));
                        collations[idx] = theCollation;
                        localeChoice.addItem( collName );
                    }
                    else
                    {
                        throw new ParseException("Max # exceeded!" +
                            "Please replace an existing one.", 0);
                    }
                }
                localeChoice.select(idx);
                ruleStrings[idx] = rules;
                ruleEntry.setText(createUnicodeString
                    (((RuleBasedCollator)theCollation).getRules()));
                if (localeChoice.getItemCount() < MAX_COLLATIONS)
                    collationName.setText("custom-" + untitledIndex);
            }
            catch (ParseException foo)
            {
                collateRulesButton.setLabel("Set Rules");
                errorText(foo.getMessage());
            }
        }
    }
    /**
     * Must be called after the array of locales has been initialized.
     */

    public void loadCollationTables()
    {
        MAX_COLLATIONS = locales.length + 10;
        collations = new Collator[MAX_COLLATIONS];
        ruleStrings = new String[MAX_COLLATIONS];
        for (int i = 0; i < locales.length; i++)
        {
           Locale currentLocale = locales[i];
           Collator newcollation = null;

           //If the collation for this language has already
           //been created, then use it.
           int j = 0;
           for (; j < i; j++)
           {
                if (currentLocale.getLanguage() == locales[j].getLanguage())
                {
                    newcollation = collations[j];
                break;
                }
           }

            if (newcollation == null)
            {
                collations[i] = Collator.getInstance(currentLocale);
                if (collations[i] instanceof RuleBasedCollator)
                {
                    String ruleText = ((RuleBasedCollator)
                        collations[i]).getRules();
                    ruleStrings[i] = createUnicodeString(ruleText);
                }
            }else {
                collations[i] = newcollation;
                ruleStrings[i] = ruleStrings[j];
            }
        }
    }

    //------------------------------------------------------------
    // package private
    //------------------------------------------------------------

    Panel makePanel(Component demo, Component code) {
        Panel temp = new Panel();

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        temp.setLayout(gridbag);

        constraints.anchor = GridBagConstraints.NORTHWEST;
           constraints.gridwidth = GridBagConstraints.REMAINDER; //end row
        gridbag.setConstraints(demo, constraints);

           constraints.gridheight = GridBagConstraints.REMAINDER; //end column
        gridbag.setConstraints(code, constraints);

        temp.add(demo);
        temp.add(code);
        return temp;
    }

    void addWithFont(Container container, Component foo, Font font) {
        if (font != null)
            foo.setFont(font);
        container.add(foo);
    }

    void buildGUI()
    {
        setBackground(Color.white);
        setLayout(new BorderLayout());

    // TITLE

        Label title=new Label("Collate Demo", Label.CENTER);
        title.setFont(Utility.titleFont);

        Label demo=new Label(creditString, Label.CENTER);
        demo.setFont(Utility.creditFont);

        Panel titlePanel = new Panel();
        titlePanel.add(title);
        titlePanel.add(demo);
        Utility.fixGrid(titlePanel,1);
        add("North", titlePanel);

    // CHECKBOXES

        checkboxes= new CheckboxGroup();

        sortAscending = new Checkbox("Sort Ascending",checkboxes, false);
	sortAscending.addItemListener(this);
        sortAscending.setFont(Utility.choiceFont);
        sortAscending.setSize(100, 20);

        sortDescending = new Checkbox("Sort Descending",checkboxes, false);
	sortDescending.addItemListener(this);
        sortDescending.setSize(100, 20);
        sortDescending.setFont(Utility.choiceFont);

        noSort = new Checkbox("Not Sorted",checkboxes, true);
        noSort.setFont(Utility.choiceFont);
        noSort.setSize(100, 20);

        Panel buttonPanel = new Panel();
        buttonPanel.setLayout(new GridLayout(3,1,0,0));
        buttonPanel.add(sortAscending);
        buttonPanel.add(sortDescending);
        buttonPanel.add(noSort);

    // LOCALE
        Locale myLocale = new Locale("","","");

        Label localeLabel = new Label("Locale:   ");
        localeLabel.setFont(Utility.choiceFont);

        localeChoice = new Choice();
	localeChoice.addItemListener(this);

        //Get all locales for debugging, but only get G7 locales for demos.
        if (DEBUG == true)
             locales = Collator.getAvailableLocales();
        else locales = Utility.getG7Locales();

        Locale displayLocale = null;
        displayLocale = Locale.getDefault();
        int defaultLoc = 0;
        for (int i = 0; i < locales.length; i++) {
            if (locales[i].getCountry().length() > 0) {
                localeChoice.addItem
                    (locales[i].getDisplayName());
                if (locales[i].equals(displayLocale)) {
                    defaultLoc = i;
                }
            }
        }
        localeChoice.setFont(Utility.choiceFont);

        //must be called after the array of locales has been initialized
        loadCollationTables();
        localeChoice.select(defaultLoc);

        Panel localePanel = new Panel();
        localePanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        localePanel.add(localeChoice);
        // localePanel.setSize(200, 20);

        Label decompLabel = new Label("Decomposition Mode:");
        decompLabel.setFont(Utility.labelFont);
        decompChoice = new Choice();
	decompChoice.addItemListener(this);
        decompChoice.addItem(no_decomposition);
        decompChoice.addItem(canonical_decomposition);
        decompChoice.addItem(full_decomposition);
        decompChoice.select(canonical_decomposition);
        decompChoice.setFont(Utility.choiceFont);

        Panel decompPanel = new Panel();
        decompPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0,0));
        decompPanel.add(decompChoice);

        Label strengthLabel = new Label("Strength:");
        strengthLabel.setFont(Utility.labelFont);

        strengthChoice = new Choice();
	strengthChoice.addItemListener(this);
        strengthChoice.addItem(tertiary);
        strengthChoice.addItem(secondary);
        strengthChoice.addItem(primary);
        strengthChoice.setFont(Utility.choiceFont);

        Panel strengthPanel = new Panel();
        strengthPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        strengthPanel.add(strengthChoice);


        Panel topPanel = new Panel();
        //topPanel.setLayout(new GridLayout(4,1,0,0));
        topPanel.add(buttonPanel);
        topPanel.add(new Label(" ")); // quick & dirty gap
        topPanel.add(localeLabel);
        topPanel.add(localePanel);
        topPanel.add(decompLabel);
        topPanel.add(decompPanel);
        topPanel.add(strengthLabel);
        topPanel.add(strengthPanel);
        Utility.fixGrid(topPanel,1);

        // TEXT

	textentry.addKeyListener(this);
        textentry.setFont(editFont);
        textentry.setText("black-birds\nPat\np\u00E9ch\u00E9\np\u00EAche\n" +
            "p\u00E9cher\np\u00EAcher\nTod\nT\u00F6ne\nTofu\nblackbirds\n" +
            "Ton\nPAT\nblackbird\nblack-bird\npat\n");

        //RULE ENTRY AREA
        Panel ruleEntryPanel = new Panel();

        Panel rulePanel = new Panel();

        if (theCollation instanceof RuleBasedCollator)
        {
            String ruleText = ((RuleBasedCollator)theCollation).getRules();
            ruleEntry.setFont(ruleFont);
            ruleEntry.setText(createUnicodeString(ruleText));
        }

        ruleEntryPanel.add(new Label("Collator Rules", Label.LEFT));
        ruleEntryPanel.add(ruleEntry);

        collateRulesButton = new Button("Set Rules");
	collateRulesButton.addActionListener(this);
        collationName = new TextField(10);
        collationName.setText("custom-" + untitledIndex);

        ruleEntryPanel.add(collateRulesButton);
        ruleEntryPanel.add(new Label("Collator Name", Label.LEFT));
        ruleEntryPanel.add(collationName);
        Utility.fixGrid(ruleEntryPanel,1);


        // PUT ALL TOGETHER

        Panel centerPanel = new Panel();
        centerPanel.add(textentry);
        centerPanel.add(topPanel);
        centerPanel.add(ruleEntryPanel);
        errorMsg.setFont(Utility.labelFont);
        centerPanel.add(errorMsg);
        Utility.fixGrid(centerPanel,3);

        add("Center", centerPanel);

        Panel bottomPanel = new Panel();
        bottomPanel.setLayout(new GridLayout(2,1,0,0));

        addWithFont (bottomPanel,
                     new Label(copyrightString, Label.LEFT),
                     Utility.creditFont);
        addWithFont (bottomPanel,
                     new Label(copyrightString2, Label.LEFT),
                     Utility.creditFont);
        Utility.fixGrid(bottomPanel,1);
        add("South", bottomPanel);
    }


    //------------------------------------------------------------
    // private
    //------------------------------------------------------------
    /**
     * This function is called by handleSortAscend and handleSortDescend
     * to seperate each line of the textArea into individual strings,
     * and add them to the vector to be sorted.
     */
    private void InitializeListVector()
    {
        textList.removeAllElements();

        String composite = textentry.getText();
        char compositeArray[] = composite.toCharArray();
        for (int i = 0; i < compositeArray.length; i++)
        {
           compositeArray[i] = (char) (compositeArray[i] & 0xFF);
        }
        composite = new String(compositeArray);

        String substr;

        int startOffset = 0;
        int endOffset = 0;
        for (; endOffset < composite.length(); endOffset++)
        {
            char ch = composite.charAt(endOffset);
            if (ch == '\n' || ch == '\r')
            {
                if (endOffset > startOffset)
                {
                    substr = composite.substring(startOffset, endOffset);
                    textList.addElement(substr);
                }
                startOffset = endOffset + 1;
            }
        }

        if (startOffset < composite.length())
        {
            substr = composite.substring(startOffset, composite.length());
            textList.addElement(substr);
        }

    }


    /**
     * This function is called by handleSortAscend and handleSortDescend
     * to reset the text area based on the sort results stored as a vector
     * of substrings.
     */
    private void resetTextArea()
    {
        String composite = new String();

        int i = 0;
        for (; i < textList.size() - 1; i++)
        {
            composite = composite.concat((String) textList.elementAt(i));
            composite = composite.concat("\n");
        }
        //don't add the \n to the end
           composite = composite.concat((String) textList.elementAt(i));

        textentry.setText(composite);
        textList.removeAllElements();
    }



    private void errorText(String s)
    {
        if (!s.equals(errorMsg.getText()))
        {
            errorMsg.setText(s);
            errorMsg.setSize(300, 50);
        }
    }

    private static StringBuffer oneChar = new StringBuffer(7);
    private String makeDisplayString(char ch) {
        oneChar.setLength(0);
        if (ch < 0x0020 ||
            (ch > 0x007D && ch < 0x00A0) ||
            ch > 0x00FF) {
            String temp = Integer.toString((int)ch,16).toUpperCase();
            oneChar.append(temp);
            if (temp.length() < 4)
                oneChar.append(zeros.substring(0,4-temp.length()));
        } else {
            oneChar.append(ch);
        }
        return oneChar.toString();
    }

     /**
    Changes a string into a representation that can be pasted into a program.
    Uses \ t, \ uxxxx, etc. to display characters outside of Latin1.
    Others are themselves.
    */
    private String createUnicodeString(String source)
    {
    StringBuffer result = new StringBuffer(source.length());
    int len = 0;
    int i = 0;
    while (i < source.length()) {
        char ch = source.charAt(i);
        switch(ch) {
       case '\'':
            int mytemp = i;
            int n = 0;
            result.append(ch);
            ch = source.charAt(++i);
            while((ch != '\'') && (i < source.length() -1))
            {
                String inquote = makeDisplayString(ch);
                n++; i++;
                if (inquote.length() > 1) {
                    result.append("\\u");
                    result.append(inquote);
                    len += 6;
                } else {
                    result.append(inquote);
                    len++;
                }
                ch = source.charAt(i);
            }
            if (n == 0) {
                result.append('\'');
                n++; i++;
                ch = source.charAt(i);
            }
            result.append(ch);
            len += n;
            break;
        case ';': case '=':
            if (len > 15)
            {
                result.append('\n');
                len = 0;
            }
            result.append(' ');
            result.append(ch);
            len += 2;
            break;
        case ',':
            result.append(' ');
            result.append(ch);
            len += 2;
            break;
        case '<': case '&':
            result.append('\n');
            result.append(ch);
            result.append(' ');
            len = 0;
            len += 2;
            break;

        case '\n':
            break;
        default:
            String dspString = makeDisplayString(ch);
            if (dspString.length() > 1) {
                result.append("\\u");
                result.append(dspString);
                len += 6;
            } else {
                result.append(dspString);
                len++;
            }
            break;
        }
        i++;
    }
    return result.toString();
    }

    private static final String zeros = "0000";


    private String convertStringToRules(String source)
    {
    StringBuffer result = new StringBuffer(source.length());
    for (int i = 0; i < source.length(); ++i) {
        //hack around TextArea bug
        char ch = (char) (source.charAt(i) & 0xFF);
        switch(ch) {
        case '\n':
            break;
        case '\'':
	    if ((i+6 < source.length()) && (source.charAt(i+2) == 'u')) {
		String temp = source.substring(i+3,i+7);
		if (temp.equals("005c")) {
		    //have to handle backslash differently since 
		    //it is a special character in Java
		    result.append("'\\u005c'");
		} else
		    result.append((char)(Integer.parseInt(temp,16)));
		i = i + 7;
	    }
	    else result.append(ch);
            break;
        case '\\':
          if ( (i+5 < source.length()) && (source.charAt(i+1) == 'u') )
          {
            String temp = source.substring(i+2,i+6);
            result.append((char)(Integer.parseInt(temp,16)));
            i = i + 6;
           }
           else result.append(ch);

            break;
        default:
            result.append(ch);
            break;
        }
    }
    return result.toString();
    }

    private static final String creditString
        = "v1.1a7, Demo";
    private static final String copyrightString
        = "";
    private static final String copyrightString2
        = "";

    // HSYS : fix
    private static int MAX_COLLATIONS = 12;
    private int untitledIndex = 1;
    private static final int FIELD_COLUMNS = 45;
    private static final Font editFont = new Font("TimesRoman",Font.PLAIN,18);
    private static final Font ruleFont = new Font("TimesRoman",Font.BOLD,14);

    private static final boolean DEBUG = false;

    private TextArea textentry = new TextArea(10, 10);
    private TextArea ruleEntry = new TextArea(10, 20);
    private Vector textList = new Vector(15, 10);

    private Choice localeChoice;

    private static final String no_decomposition = "None";
    private static final String canonical_decomposition = "Canonical";
    private static final String full_decomposition = "Full";
    private Choice decompChoice;

    private static final String tertiary = "Tertiary - a vs. A";
    private static final String secondary = "Secondary - a vs. \u00E0";
    private static final String primary = "Primary - a vs. b";
    private Choice strengthChoice;

    private Locale theLocale = null;
    private Collator theCollation = null;

    private Locale[] locales;
    private Collator[] collations;
    private String[] ruleStrings;
    private Label errorMsg = new Label("", Label.LEFT);

    CheckboxGroup checkboxes;
    Checkbox sortAscending;
    Checkbox sortDescending;
    Checkbox noSort;

    Button collateRulesButton;
    TextField collationName;

    private DemoApplet applet;

}

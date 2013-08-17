/*
 * @(#)TextBoundDemo.java	1.1 96/11/23
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
import java.text.*;


public class TextBoundDemo extends DemoApplet
{
    public static void main(String argv[]) {
        DemoApplet.showDemo(new TextBoundFrame(null));
    }

    public Frame createDemoFrame(DemoApplet applet) {
        return new TextBoundFrame(applet);
    }
}



class TextBoundFrame extends Frame implements WindowListener, ItemListener , KeyListener
{

	private static final String creditString =

		"v1.1a9, Demo";

	private static final String copyrightString =

		"";

	private static final String copyrightString2 =

		"";



	private static final int FIELD_COLUMNS = 45;

    private static final Font choiceFont = null;

    private static final boolean DEBUG = false;

    private DemoApplet applet;


    final String right = "-->";

    final String left = "<--";



	private BreakIterator enum;



    TextArea text;

    Choice bound;



    public TextBoundFrame(DemoApplet applet)

    {
        this.applet = applet;
	addWindowListener(this);
        init();
        start();

    }



    public void run()

    {

    }



    public int getSelectionStart()

    {

    	return text.getSelectionStart() & 0x7FFF;

    }



    public int getSelectionEnd()

    {

    	return text.getSelectionEnd() & 0x7FFF;

    }



    public synchronized void checkChange()

    {

		int e = enum.following(getSelectionStart());

		int s = enum.previous();

		selectRange(s, e);

    }



	public final synchronized void selectRange(int s, int e)

	{

		try {

			//if (getSelectionStart() != s || getSelectionEnd() != e) {

	    		text.select(s, e);

			//}

			if (getSelectionStart() != s || getSelectionEnd() != e) {

				System.out.println("AGH! select("+s+","+e+") -> ("+

				getSelectionStart()+","+getSelectionEnd()+")");

			}

		} catch (Exception exp) {

			errorText(exp.toString());

		}

	}



    public void init()

    {

        buildGUI();

    }



    public void start()

    {

    }


	void addWithFont(Container container, Component foo, Font font) {

		if (font != null)

			foo.setFont(font);

		container.add(foo);

	}



   public void buildGUI()
  {
      setBackground(Color.white);

      setLayout(new BorderLayout());

      Panel topPanel = new Panel();

      Label titleLabel = new Label("Text Boundary Demo", Label.CENTER);

      titleLabel.setFont(Utility.titleFont);

      topPanel.add(titleLabel);

      Label demo=new Label(creditString, Label.CENTER);

      demo.setFont(Utility.creditFont);

      topPanel.add(demo);

      Panel choicePanel = new Panel();

      Label demo1=new Label("Boundaries", Label.LEFT);

      demo1.setFont(Utility.labelFont);

      choicePanel.add(demo1);

      bound = new Choice();

      bound.addItemListener(this);

      bound.addItem("Sentence");

      bound.addItem("Line Break");

      bound.addItem("Word");

      bound.addItem("Char");

      if (choiceFont != null)
	bound.setFont(choiceFont);

      choicePanel.add(bound);

      topPanel.add(choicePanel);

      Utility.fixGrid(topPanel,1);

      add("North", topPanel);

      int ROWS = 15;

      int COLUMNS = 50;

      text = new TextArea(getInitialText(), ROWS, COLUMNS);

      text.addKeyListener(this);

      text.setEditable(true);

      text.selectAll();

      text.setFont(Utility.editFont);

      add("Center", text);

      Panel copyrightPanel = new Panel();

      addWithFont (copyrightPanel, 
		   new Label(copyrightString, Label.LEFT),Utility.creditFont);

      addWithFont (copyrightPanel,

		   new Label(copyrightString2, Label.LEFT),Utility.creditFont);

      Utility.fixGrid(copyrightPanel,1);

      add("South", copyrightPanel);

      //layout();

      handleEnumChanged();

      // (new Thread(this)).start();

  }



    public String getInitialText()

    {

    	return

	/*

			"\"This is a sentence.\" This is not.\" \"because. And go. " +

			"This is a simple 012.566,5 sample sentence. \n"+

			"It does not have to make any sense as you can see. \n"+

			"Nel mezzo del cammin di nostra vita, mi ritrovai in "+

				"una selva oscura. \n"+

			"Che la dritta via aveo smarrita. \n"+

			"He said, that I said, that you said!! \n"+

			"Don't rock the boat.\n\n"+

			"Because I am the daddy, that is why. \n"+

			"Not on my time (el timo.)! \n"+

			"Tab\tTab\rTab\tWow."+

			"So what!!\n\n"+

			"Is this a question???  " +

			"I wonder...Hmm.\n" +

			"Harris thumbed down several, including \"Away We Go\" "+

				"(which became the huge success Oklahoma!). \n"+

			"One species, B. anthracis, is highly virulent.\n"+

			"Wolf said about Sounder: \"Beautifully thought-out and "+

				"directed.\"\n"+

			"Have you ever said, \"This is where I shall live\"? \n"+

			"He 1000,233,456.000 answered, \"You may not!\" \n"+

			"Another popular saying is: \"How do you do?\". \n"+

			"What is the proper use of the abbreviation pp.? \n"+

			"Yes, I am 1,23.322% definatelly 12\" tall!!";

	*/

			"(\"This is a complete sentence.\") This is (\"not.\") also. \n"

			+"An abbreviation in the middle, etc. and one at the end, etc. "+

				"This\n"

			+"is a simple sample 012.566,5 sentence. It doesn't\n"

			+"have to make any sense, as you can see. Nel mezzo del \nc"

			+"ammin di nostra vita, mi ritrovai in una selva oscura. Che\n"

			+"la dritta via aveo smarrita. Not on my time (el timo.)! And\n"

			+"tabulated columns: \tCol1\tCol2\t3,456%.\t\n"

			+"Is this a question???  I wonder... Hmm. Harris thumbed\n"

			+"down several, including \"Away We Go\" (which became the \n"

			+"huge success Oklahoma!). One species, B. anthracis, is \n"

			+"highly virulent. Wolf said about Sounder: \"Beautifully \n"

			+"thought-out and directed.\" Have you ever said, \"This is "+

				"where I\n"

			+"shall live\"? He said 1000,233,456.000 and answered, \"You "+

				"may not!\" \n"

			+"Another popular saying is: \"How do you do?\". What is the \n"

			+"proper use of the abbreviation pp.? Yes, I am 12\' 3\" tall!!";

    }





	public void handleEnumChanged()

	{

    	String s = bound.getSelectedItem();

    	if (s.equals("Char")) {

			errorText("getCharacterInstance");

    		enum = BreakIterator.getCharacterInstance();

    	}

    	else if (s.equals("Word")) {

			errorText("tWordBreak");

    		enum = BreakIterator.getWordInstance();

    	}

    	else if (s.equals("Line Break")) {

			errorText("getLineInstance");

    		enum = BreakIterator.getLineInstance();

    	}

    	else /* if (s.equals("Sentence")) */ {

			errorText("getSentenceInstance");

    		enum = BreakIterator.getSentenceInstance();

    	}

    	enum.setText(text.getText());

		selectRange(0, 0);

    	//text.select(0,0);

	}



	public void handleForward()

	{

		try {

			enum.setText(text.getText());

			int oldStart = getSelectionStart();

			int oldEnd = getSelectionEnd();

			if (oldEnd < 1) {

				selectRange(0, enum.following(0));

			}

			else {

				int s = enum.following(oldEnd-1);

				int e = enum.next();

				if (e == -1) {

					e = s;

				}
				selectRange(s, e);

			}

			//text.select(s, e);

			//errorText("<" + oldStart + "," + oldEnd + "> -> <" +

				//s + "," + e + ">");

		}

		catch (Exception exp) {

			errorText(exp.toString());

		}

	}



	public void handleBackward()

	{

		try {

			enum.setText(text.getText());

			int oldStart = getSelectionStart();

			int oldEnd = getSelectionEnd();

			if (oldStart < 1) {

				selectRange(0, 0);

			}

			else {

				int e = enum.following(oldStart-1);

				int s = enum.previous();

				selectRange(s, e);

			}

			//text.select(s, e);

			//errorText("<" + oldStart + "," + oldEnd + "> -> <" + s + "," + e + ">");

		}

		catch (Exception exp) {

			errorText(exp.toString());

		}

	}

  /* ItemListener method */
  public void itemStateChanged(ItemEvent e) {
    if(e.getSource() instanceof Choice)
      handleEnumChanged();
  }
  
  /* KeyListener methods */
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_RIGHT){
      e.consume();// don't deliver the event to the native widget
      handleForward();
    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
      e.consume();// don't deliver the event to the native widget
      handleBackward();
    }
  }

  public void keyReleased(KeyEvent e) {
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
  

    public void errorText(String s)
    {
        if (DEBUG)
            System.out.println(s);
    }

}


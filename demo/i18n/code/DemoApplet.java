/*
 * @(#)DemoApplet.java	1.1 96/11/23
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

public abstract class DemoApplet extends java.applet.Applet implements ActionListener
{
    private Button   demoButton;
    private Frame    demoFrame;

    public abstract Frame createDemoFrame(DemoApplet applet);

    //Create a button that will display the demo
    public void init()
    {
        setBackground(Color.white);
        demoButton = new Button("Demo");
	demoButton.addActionListener(this);
        demoButton.setBackground(Color.yellow);
        add( demoButton );
    }

    public static void showDemo(Frame demoFrame)
    {
        demoFrame.setSize(550,500);
        demoFrame.show();
    }

    public void demoClosed()
    {
        demoFrame = null;
    }

  public void actionPerformed(ActionEvent e) {
    String arg = e.getActionCommand();
    if (arg.equals("Demo")) {
      demoButton.setLabel("loading");
      if (demoFrame == null) {
	demoFrame = createDemoFrame(this);
	showDemo(demoFrame);
      }
      demoButton.setLabel("Demo");
    }
  }

}


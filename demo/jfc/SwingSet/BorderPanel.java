/*
 * @(#)BorderPanel.java	1.6 98/08/26
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

import javax.swing.*;

import java.awt.Panel;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Font;

import javax.swing.event.*;
import javax.swing.border.*;

/*
 * @version 1.6 08/26/98
 * @author Dave Kloba
 */
public class BorderPanel extends JPanel      {

    public BorderPanel()    {
        JLabel l;
        JPanel tp;
	GridLayout g;

        setLayout(new BorderLayout());
        tp = new JPanel();
	g = new GridLayout(0, 2);
	g.setHgap(3);
	g.setVgap(1);
        tp.setLayout(g);


        tp.add(new BorderLabel("LineBorder", 
                               new LineBorder(Color.darkGray, 2)));
        tp.add(new BorderLabel("BevelBorder RAISED",
                               BorderFactory.createRaisedBevelBorder()));
        tp.add(new BorderLabel("BevelBorder LOWERED", 
                               BorderFactory.createLoweredBevelBorder()));
        tp.add(new BorderLabel("EtchedBorder",
                               BorderFactory.createEtchedBorder()));
        tp.add(new BorderLabel("TitledBorder 1",
                               new TitledBorder(LineBorder.createBlackLineBorder(),
                               "Using LineBorder")));
        tp.add(new BorderLabel("TitledBorder 2",
                               new TitledBorder(BorderFactory.createRaisedBevelBorder(),
                               "Using BevelBorder")));
        tp.add(new BorderLabel("TitledBorder 3",
                                new TitledBorder(
                                    new TitledBorder(LineBorder.createBlackLineBorder(),
                                        "Using a TitledBorder"),
                                    "as the Border",
                                TitledBorder.RIGHT ,
                                TitledBorder.BOTTOM )));

        tp.add(new BorderLabel("TitledBorder 4",
                         new TitledBorder(new LineBorder(Color.black, 1), 
                                "Using Courier 16 bold",
                                 TitledBorder.LEFT,
                                 TitledBorder.TOP,
                                 new Font("Courier", Font.BOLD, 16))));

        tp.add(new BorderLabel("TitledBorder 5",
                               new TitledBorder(new EmptyBorder(1, 1, 1, 1),
                               "Using EmptyBorder",
                               TitledBorder.LEFT , TitledBorder.TOP )));

        tp.add(new BorderLabel("Matte Border",
                               new MatteBorder(18,18,18,18, 
                                   SwingSet.sharedInstance().loadImageIcon("images/swirl.gif","Swirl"))));


        add(tp, BorderLayout.CENTER);


    }
}

class BorderLabel extends JLabel {
    public BorderLabel(String text, Border b) {
        super(text);
        setBorder(b);
        setHorizontalAlignment(SwingConstants.CENTER);
    }
}

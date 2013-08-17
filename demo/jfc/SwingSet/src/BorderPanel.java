/*
 * @(#)BorderPanel.java	1.7 99/04/23
 *
 * Copyright (c) 1997-1999 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
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
 * @version 1.7 04/23/99
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

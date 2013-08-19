/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

/*
 * @(#)AppletFrame.java	1.12 03/01/23
 */

import java.awt.Frame;
import java.awt.Event;
import java.awt.Dimension;
import java.applet.Applet;
import java.awt.AWTEvent;

// Applet to Application Frame window
class AppletFrame extends Frame
{

    public static void startApplet(String className, 
                                   String title, 
                                   String args[])
    {
       // local variables
       Applet a;
       Dimension appletSize;

       try 
       {
          // create an instance of your applet class
          a = (Applet) Class.forName(className).newInstance();
       }
       catch (ClassNotFoundException e) { return; }
       catch (InstantiationException e) { return; }
       catch (IllegalAccessException e) { return; }

       // initialize the applet
       a.init();
       a.start();
  
       // create new application frame window
       AppletFrame f = new AppletFrame(title);
  
       // add applet to frame window
       f.add("Center", a);
  
       // resize frame window to fit applet
       // assumes that the applet sets its own size
       // otherwise, you should set a specific size here.
       appletSize =  a.getSize();
       f.pack();
       f.setSize(appletSize);  

       // show the window
       f.show();
  
    }  // end startApplet()
  
  
    // constructor needed to pass window title to class Frame
    public AppletFrame(String name)
    {
       // call java.awt.Frame(String) constructor
       super(name);
    }

    // needed to allow window close
    public void processEvent(AWTEvent e)
    {
       // Window Destroy event
       if (e.getID() == Event.WINDOW_DESTROY)
       {
          // exit the program
          System.exit(0);
       }    
   }  // end handleEvent()

}   // end class AppletFrame







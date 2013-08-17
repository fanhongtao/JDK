/*
 * @(#)AppletFrame.java	1.8 99/07/12
 *
 * Copyright (c) 1997 Sun Microsystems, Inc. All Rights Reserved.
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







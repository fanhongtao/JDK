SwingApplet illustrates how it's possible to run a Swing-based
applet, as long as the Swing classes are in the browser's class
path. For example, on JDK1.2:

   appletviewer SwingApplet.html

When you successfully run the applet, you might see a message about a
security exception.  It starts like this:

   sun.applet.AppletSecurityException: checkawteventqueueaccess

Please ignore this exception.

SwingApplet illustrates how it's possible to run a Swing-based
applet, as long as the Swing classes are in the browser's class
path.  In Java 2 (including v 1.3), the Swing classes are core,
and thus are always in the class path.  For example, in 1.3
you can run SwingApplet with this command:

   appletviewer SwingApplet.html


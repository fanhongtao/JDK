About Metalworks
================
Metalworks is a simple Swing-based simulated email application.  It shows off several features of Swing including: JInternalFrame, JTabbedPane, JFileChooser, JEditorPane and JRadioButtonMenuItem.  It is optimized to work with the Metal Look & Feel and shows use of several Metal specific features including themes.

Running Metalworks
==================

To run the Metalworks demo on 1.2 <all platforms>:
  java -jar Metalworks.jar


To run the Metalworks demo on 1.1.x on Solaris:
  setenv SWING_HOME <path to swing release>
  setenv JAVA_HOME <path to jdk1.1.x release>
  runnit


To run the Metalworks demo on 1.1.x on win32:
  set CLASSPATH=<path to jdk1.1.x release>\lib\classes.zip
  set SWING_HOME=<path to swing release>
  runnit

Metalworks Features
===================
The functionality of the Metalworks demo is minimal, and many controls are non-functional.  They are only intended to show how to construct the UI for such interfaces.  Things that do work in the Metalworks demo include:

1. Choosing New from the File menu displays an email composition window.

2. Choosing Open from the File menu brings up the file chooser.

3. Choosing Preferences from the Edit menu will bring up a dialog.  Most of this dialog is only for show.

4. Choosing About Metalworks from the help menu brings up a JOptionPane with a brief description of the application.

5. Choosing Open Help Window from the Help menu brings up an internal frame which displays a set of HTML files containing all sorts of useful info.  Look through these for tips about using Metal.

6. Selecting from the Theme menu allows you to change the color theme of the application.  The default theme (Steel) and several other demo themes are included.  One note that the themes can control the sizes, as well as the colors of many controls.  Also included with this release is the PropertiesMetalTheme class which allows you to read a theme's colors from a text file.  The Charcoal theme is an example of using this.

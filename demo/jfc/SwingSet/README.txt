This file tells you how to run SwingSet, both as an applet and
as an application.


==================================
RUNNING SWINGSET AS AN APPLICATION
==================================

JDK 1.1.x (Solaris)
-------------------
  setenv SWING_HOME <path to swing release>
  setenv JAVA_HOME <path to jdk1.1.x release>
  runnit

JDK 1.1.x (win32)
-----------------
  set CLASSPATH=<path to jdk1.1.x release>\lib\classes.zip
  set SWING_HOME=<path to swing release>
  runnit


=============================
RUNNING SWINGSET AS AN APPLET
=============================

JDK 1.1.x
----------------------
You can choose from several approaches to running the SwingSet demo as
an applet.  You need to apply only one approach, not all.  This section
tells you how to run SwingSet as an applet in the following browsers:

- JDK Applet Viewer 
- IE 4.0
- Netscape Communicator 4.04 with the latest 1.1 patch
  (obtained from http://developer.netscape.com/software/jdk/download.html)

The usual approach to running a Swing applet is to put the Swing class
libraries in a local directory where the browser can find them.
Another (much slower) scheme is to store the Swing class libraries with
the applet's classes, which allows clients to download the Swing class
libraries over the network.

Here are the three ways to put Swing class libraries in a local directory
where the browser can find them:

1. Use the CLASSPATH environment variable.
2. Specify the class path in another way (only the JDK Applet Viewer
   allows this, to our knowledge).
3. Put the Swing classes in a browser-specific directory.

Setting the CLASSPATH environment variable appears to work for all
browsers.  However, setting CLASSPATH can sometimes lead to trouble,
since it's easy to forget to update it when you update your JDK version
or your browser.  Take care if you choose to use CLASSPATH.

The file SwingSetApplet.html contains the <APPLET> tag necessary for
running SwingSet, assuming that the Swing classes are locally
available to the browser.  


JDK Applet Viewer (any platform)
--------------------------------
Enter this command:
  runapplet


IE 4.0 or Netscape Communicator 4.04 with latest 1.1 patch (Win NT)
-------------------------------------------------------------------
This is the CLASSPATH solution for Windows NT.

1) Open ControlPanel->System->Environment.
2) In *User* Variables window, add or modify SWING_HOME and CLASSPATH
   to look like like following:
	SWING_HOME=<path to swing installation>
	CLASSPATH=<currentclasspath>;%SWING_HOME%\swing.jar;%SWING_HOME%\motif.jar;%SWING_HOME%\windows.jar
3) Remember to "Set" the changes, then press OK.
4) To ensure the changes take effect, restart your computer.


IE 4.0 or Netscape Communicator 4.04 with latest 1.1 patch (Win95)
-------------------------------------------------------------------
This is the CLASSPATH solution for Windows 95.

1) Use your favorite ASCII editor to add the following to the
   c:\autoexec.bat file:
   set SWING_HOME=<location of Swing directory>
   set CLASSPATH=%CLASSPATH%;%SWING_HOME%\swing.jar;%SWING_HOME%\windows.jar;%SWING_HOME\motif.jar
2) Restart the computer.


IE4.0 (win32)
-------------
To put the Swing classes where IE4.0 can find them (without using
CLASSPATH), you must first unjar the Swing archives and then put the
resulting files in the java\classes directory under the IE installation
directory.  For example, on Windows NT:

1) Go to IE's java/classes directory:
   cd <location-of-IE-install>/java/classes
2) Unarchive the files:
   jar xf %SWING_HOME%/swing.jar
   jar xf %SWING_HOME%/windows.jar
   jar xf %SWING_HOME%/motif.jar


Netscape Communicator 4.04 with latest 1.1 patch (Win95)
--------------------------------------------------------
To put the Swing classes where Netscape Communicator can find them
(without using CLASSPATH), put the Swing JAR files under the
Communicator installation's Java\Classes directory.  By default, this
directory will be located here:

   c:\Program Files\Netscape\Communicator\Program\Java\Classes

For example:

   copy %SWING_HOME%/*.jar c:\<netscape-dir>\Communicator\Program\Java\Classes


Any browser: Loading Swing classes over the network
---------------------------------------------------
If you wish to download the Swing classes over the network, you need to
first put the Swing class libraries in the same directory as the
applet's class files.  Then create a JAR file containing all the other
files needed by SwingSet.  (This last step is not necessary for every
browser, but it will help it load faster.)  For example:

  cd classes
  jar cvf SwingSet.jar *.class *.txt images
  copy ..\..\..\*.jar .

Once you've performed these steps, you should be able to visit the
SwingSetApplet2.html page in any 1.1 browser. It will take several
minutes for the applet to start running, since the Swing class libraries
have to be completely downloaded before the applet can run.


Known problems with running Swing applets:
------------------------------------------
- Internet Explorer supports either class files or archive files in
  the <APPLET> tag, not both.  To work around this, you can put the
  SwingSet classes in a JAR files, as described above.

- Both browsers exhibit different problems with missed repaints.
  Moving the mouse around or minimizing and restoring the frame "fixes"
  this problem.  We're investigating how to workaround these problems.

- Internet Explorer doesn't implement Class.getResource() for JAR
  files.  The visual effect is missing text and graphics files in
  SwingSet.

- Internet Explorer may throw exceptions "Event queue access denied".
  These can be ignored.

- Netscape Communicator loads large JAR files very slowly.  

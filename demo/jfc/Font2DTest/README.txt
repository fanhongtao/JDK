Font2DTest
----------

To run Font2DTest:

% java -jar Font2DTest.jar
    or 
% appletviewer Font2DTest.html

If you wish to modify any of the source code, you may want to extract
the contents of the Font2DTest.jar file by executing this command:

% jar -xvf Font2DTest.jar


-----------------------------------------------------------------------
Introduction
-----------------------------------------------------------------------

Font2DTest is an encompassing application for testing various fonts
found on the user's system.  A number of controls are available to 
change many attributes of the current font including style, size, and
rendering hints.  The user can select from multiple display modes,
such as one Unicode range at a time, all glyphs of a particular font, 
or sample text.  In addition, the user can control which Java 2D 
method will render the text to the screen (or to any printer).

-----------------------------------------------------------------------
Tips on usage 
----------------------------------------------------------------------- 

- For the "Font Size" and "Unicode Base" fields to have an effect, the 
user must press ENTER when finished inputting data in those fields.

- You can change the Unicode Base to any number by selecting "Other..." 
from the "Unicode Range" pulldown box and typing the value of the 
desired range base in the TextField named "Unicode Base".

- There is now a TextArea on the right side of the window available for
entering any sort of text in "User Text" display mode.  This includes
any Unicode control codes in the form of \uXXXX... Just press the
"Update" button under the text area to refresh the display.  Your text
will be painted using the current "Draw Method" (drawChars(),
TextLayout.draw(), etc...)

- You can save and load the current control "state" (the state of the
widgets on the top of the window) using the "File|Read Control Info..." 
and "File|Write Control Info..." options.  

- When changing the Unicode range to be displayed, the Font list is now
updated immediately and displays a "*" to the left of each font which
can successfully display at least one character from the current
256-character range.  (NOTE: this feature only works under Windows 
9x/NT due to the famous getAllFonts() bug in Solaris...)

- There is a new Display Mode called "All Glyphs" which displays all
possible glyphs for the current font.

- There is another new Display Mode called "Resource Text" which loads 
a string from each locale's resource bundle specified in a file called
"resource.data" (if present).  The format for the resource.data file
is (per line):

<ISO language code> <ISO country code>

Also, a resource bundle file must be made available for each locale
you wish to represent with a string, plus a default resource file.  
These files must be called:

TextResources_<ISO-language-code>_<ISO-country-code>.properties

and contain a line such as the following:

string=This is some text for a specific lo\u0063ale...

NOTE: The reason for using the resource bundle files is to make an 
easily extensible way to import test strings for all kinds of locales. 
Examples of these various resource files can be found in the resources 
subdirectory.

- Of course, you can still use the "File|Read Image Data..." option to
read a JPEG file into a new window for comparision, or any sort of
UTF-8 encoded text file (not too large) for use in "User Text" display 
mode.  Use the "File|Write Image Data..." option to save a 
high-quality JPEG image of the current data found on the canvas.
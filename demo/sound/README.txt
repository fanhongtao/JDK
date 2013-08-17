The classes for the Java Sound demo are contained in the JavaSound.jar 
file.  Running the demo requires the Java 2 SDK, Standard Edition 1.3.
To run the Java Sound demo :

% java -jar JavaSound.jar

Although it's not necessary to unpack the JavaSound.jar file to run 
the demo, you may want to extract its contents if you plan to modify
any of the demo source code. To extract the contents of JavaSound.jar, 
run this command from the JavaSound directory :

    jar xvf JavaSound.jar


This JavaSound demo consists of a set of demos housed in one GUI 
framework that uses a JTabbedPane.  You can access different groups of 
demos by clicking the tabs at the top of the pane. There are demo 
groups for Juke Box, Capture & Playback, Midi Synthesizer and Rhythm
Groove Box.

Juke Box :

    A Juke Box for sampled (au, wav, aif) and midi (rmf, midi) sound 
    files.  Features duration progress, seek slider, pan and volume 
    controls.  


Capture & Playback :

    A Capture/Playback sample.  Record audio in different formats
    and then playback the recorded audio.  The captured audio can 
    be saved either as a WAVE, AU or AIFF.  Or load an audio file
    for streaming playback.


Midi Synthesizer :

    Illustrates general MIDI melody instruments and MIDI controllers.
    A piano keyboard represents notes on and off.  Features capture
    and playback of note on and off events. 


Rhythm Groove Box :

    Illustrates how to build a track of short events.  Features
    a tempo dial to increase or descrease the beats per minute.


You can run anyone of the samples in stand-alone mode by issuing a 
commands like this from the JavaSound directory:

        java -cp JavaSound.jar Juke 
        java -cp JavaSound.jar CapturePlayback
        java -cp JavaSound.jar MidiSynth
        java -cp JavaSound.jar Groove


When running the Java Sound demo as an applet these permissions
are necessary in order to load/save files and record audio :  

grant {
  permission java.io.FilePermission "<<ALL FILES>>", "read, write";
  permission javax.sound.sampled.AudioPermission "record";
  permission java.util.PropertyPermission "user.dir", "read";
};

The permissions need to be added to the .java.policy file.

======================================================================

You may send comments via the javasound-comments@sun.com alias, 
which is a one-way alias to Sun's Java Sound API developers, or via the
javasound-interest@sun.com alias, which is a public discussion list. 

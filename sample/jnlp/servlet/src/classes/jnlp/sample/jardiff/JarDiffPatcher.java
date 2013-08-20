/*
 * @(#)JarDiffPatcher.java	1.11 03/06/26
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package jnlp.sample.jardiff;
 
import java.io.*;
import java.util.*;
import java.net.URL;
import java.util.jar.*;
import java.util.zip.*;

/**
 * JarDiff is able to create a jar file containing the delta between two
 * jar files (old and new). The delta jar file can then be applied to the
 * old jar file to reconstruct the new jar file.
 * <p>
 * Refer to the JNLP spec for details on how this is done.
 *
 * @version 1.11, 06/26/03
 */
public class JarDiffPatcher implements JarDiffConstants, Patcher {
    private static final int DEFAULT_READ_SIZE = 2048;
    private static byte[] newBytes = new byte[DEFAULT_READ_SIZE];
    private static byte[] oldBytes = new byte[DEFAULT_READ_SIZE];
    private static ResourceBundle _resources = JarDiff.getResources();

    public static ResourceBundle getResources() {
	return JarDiff.getResources();
    }
    
    public void applyPatch(Patcher.PatchDelegate delegate, String oldJarPath,
                           String jarDiffPath, OutputStream result) throws IOException {
            File oldFile = new File(oldJarPath);
            File diffFile = new File(jarDiffPath);
            JarOutputStream jos = new JarOutputStream(result);
            JarFile oldJar = new JarFile(oldFile);	    
            JarFile jarDiff = new JarFile(diffFile);
            Set ignoreSet = new HashSet();
            Map renameMap = new HashMap();
	  
            
            determineNameMapping(jarDiff, ignoreSet, renameMap);

	    // get all keys in renameMap
	    Object[] keys = renameMap.keySet().toArray();
	   
	   
	    // Files to implicit move
	    Set oldjarNames  = new HashSet();
	    
	    Enumeration oldEntries = oldJar.entries();
	    if (oldEntries != null) {
		while  (oldEntries.hasMoreElements()) {
		    oldjarNames.add(((JarEntry)oldEntries.nextElement()).getName());
		}
	    }
	   
	    // size depends on the three parameters below, which is 
	    // basically the counter for each loop that do the actual
	    // writes to the output file
	    // since oldjarNames.size() changes in the first two loop
	    // below, we need to adjust the size accordingly also when
	    // oldjarNames.size() changes
	    double size = oldjarNames.size() + keys.length + jarDiff.size();   
	    double currentEntry = 0;

	    // Handle all remove commands	   
	    oldjarNames.removeAll(ignoreSet);
	    size -= ignoreSet.size();
           	   
            	 
	    // Add content from JARDiff 
	    Enumeration entries = jarDiff.entries();
	    if (entries != null) {
		while (entries.hasMoreElements()) {
		    JarEntry entry = (JarEntry)entries.nextElement();
		    
		   

		    if (!INDEX_NAME.equals(entry.getName())) {
			
			updateDelegate(delegate, currentEntry, size);
			currentEntry++;
				
			writeEntry(jos, entry, jarDiff);

			// Remove entry from oldjarNames since no implicit 
			//move is needed
			boolean wasInOld = oldjarNames.remove(entry.getName());

			// Update progress counters. If it was in old, we do 
			// not need an implicit move, so adjust total size.
			if (wasInOld) size--;
		
		    }		    	
		    else {
			// no write is done, decrement size
			size--;		
		    }
		}
	    }

	   

	    // go through the renameMap and apply move for each entry
	    for (int j = 0; j < keys.length; j++) {
	
	

		// Apply move <oldName> <newName> command
		String newName = (String)keys[j];
		String oldName = (String)renameMap.get(newName);

		// Get source JarEntry
		JarEntry oldEntry = oldJar.getJarEntry(oldName);
			
		if (oldEntry == null) {
		    String moveCmd = MOVE_COMMAND + oldName + " " + newName;
		    handleException("jardiff.error.badmove", moveCmd);
		} 

		// Create dest JarEntry  
		JarEntry newEntry = new JarEntry(newName);	
		newEntry.setTime(oldEntry.getTime());
		newEntry.setSize(oldEntry.getSize());
		newEntry.setCompressedSize(oldEntry.getCompressedSize());
		newEntry.setCrc(oldEntry.getCrc());
		newEntry.setMethod(oldEntry.getMethod());
		newEntry.setExtra(oldEntry.getExtra());
		newEntry.setComment(oldEntry.getComment());

	
		updateDelegate(delegate, currentEntry, size);
		currentEntry++;
	
		writeEntry(jos, newEntry, oldJar.getInputStream(oldEntry));

		// Remove entry from oldjarNames since no implicit 
		//move is needed
		boolean wasInOld = oldjarNames.remove(oldName);

		// Update progress counters. If it was in old, we do 
		// not need an implicit move, so adjust total size.
		if (wasInOld) size--;
	
	    }	    

	    // implicit move
	    Iterator iEntries = oldjarNames.iterator();
            if (iEntries != null) {
                while (iEntries.hasNext()) {
		   
                    String name = (String)iEntries.next();
		    JarEntry entry = oldJar.getJarEntry(name);
		   
		    updateDelegate(delegate, currentEntry, size);
		    currentEntry++;
                                                  
		    writeEntry(jos, entry, oldJar);                  
                }
            }
            
	    updateDelegate(delegate, currentEntry, size);
          
            jos.finish();
    }
    
    private void updateDelegate(Patcher.PatchDelegate delegate, double currentSize, double size) {
	if (delegate != null) {
	    delegate.patching((int)(currentSize/size));
	}
    }

    private void determineNameMapping(JarFile jarDiff, Set ignoreSet,
                                      Map renameMap) throws IOException {
        InputStream is = jarDiff.getInputStream(jarDiff.getEntry(INDEX_NAME));
        
        if (is == null) {
	    handleException("jardiff.error.noindex", null);
	   
	}
        LineNumberReader indexReader = new LineNumberReader
            (new InputStreamReader(is, "UTF-8"));
        String line = indexReader.readLine();
        
        if (line == null || !line.equals(VERSION_HEADER)) {
	    handleException("jardiff.error.badheader", line);
	    
        }
        
        while ((line = indexReader.readLine()) != null) {
            if (line.startsWith(REMOVE_COMMAND)) {
                List sub = getSubpaths(line.substring(REMOVE_COMMAND.
                                                          length()));
                
                if (sub.size() != 1) {
		    handleException("jardiff.error.badremove", line);
		   
                }
                ignoreSet.add(sub.get(0));
            }
            else if (line.startsWith(MOVE_COMMAND)) {
                List sub = getSubpaths(line.substring(MOVE_COMMAND.length()));
                
                if (sub.size() != 2) {
		    handleException("jardiff.error.badmove", line);
		    
                }
		// target of move should be the key
                if (renameMap.put(sub.get(1), sub.get(0)) != null) {
		    // invalid move - should not move to same target twice
		    handleException("jardiff.error.badmove", line);
		}
            }
            else if (line.length() > 0) {
		handleException("jardiff.error.badcommand", line);
	
            }
        }
    }
    
    private void handleException(String errorMsg, String line) throws IOException {
	try {
	    throw new IOException(getResources().getString(errorMsg) + " " + line);
	} catch (MissingResourceException mre) {
	     System.err.println("Fatal error: " + errorMsg);
	     new Throwable().printStackTrace(System.err);
	     System.exit(-1);
	}
    }

    private List getSubpaths(String path) {
        int index = 0;
        int length = path.length();
        ArrayList sub = new ArrayList();
        
        while (index < length) {
            while (index < length && Character.isWhitespace
                       (path.charAt(index))) {
                index++;
            }
            if (index < length) {
                int start = index;
                int last = start;
                String subString = null;
                
                while (index < length) {
                    char aChar = path.charAt(index);
                    if (aChar == '\\' && (index + 1) < length &&
                        path.charAt(index + 1) == ' ') {
                        
                        if (subString == null) {
                            subString = path.substring(last, index);
                        }
                        else {
                            subString += path.substring(last, index);
                        }
                        last = ++index;
                    }
                    else if (Character.isWhitespace(aChar)) {
                        break;
                    }
                    index++;
                }
                if (last != index) {
                    if (subString == null) {
                        subString = path.substring(last, index);
                    }
                    else {
                        subString += path.substring(last, index);
                    }
                }
                sub.add(subString);
            }
        }
        return sub;
    }
    
    private void writeEntry(JarOutputStream jos, JarEntry entry,
                            JarFile file) throws IOException {
        writeEntry(jos, entry, file.getInputStream(entry));
    }
    
    private void writeEntry(JarOutputStream jos, JarEntry entry,
                            InputStream data) throws IOException {
        jos.putNextEntry(entry);
        
        // Read the entry
        int size = data.read(newBytes);
        
        while (size != -1) {
            jos.write(newBytes, 0, size);
            size = data.read(newBytes);
        }
        data.close();
    }
}



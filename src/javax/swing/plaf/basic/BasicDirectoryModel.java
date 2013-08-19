/*
 * @(#)BasicDirectoryModel.java	1.28 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.event.*;
import java.beans.*;

import sun.awt.shell.ShellFolder;

/**
 * Basic implementation of a file list.
 *
 * @version %i% %g%
 * @author Jeff Dinkins
 */
public class BasicDirectoryModel extends AbstractListModel implements PropertyChangeListener {

    private JFileChooser filechooser = null;
    // PENDING(jeff) pick the size more sensibly
    private Vector fileCache = new Vector(50);
    private LoadFilesThread loadThread = null;
    private Vector files = null;
    private Vector directories = null;
    private int fetchID = 0;

    public BasicDirectoryModel(JFileChooser filechooser) {
	this.filechooser = filechooser;
	validateFileCache();
    }

    public void propertyChange(PropertyChangeEvent e) {
	String prop = e.getPropertyName();
	if(prop == JFileChooser.DIRECTORY_CHANGED_PROPERTY ||
	   prop == JFileChooser.FILE_VIEW_CHANGED_PROPERTY ||
	   prop == JFileChooser.FILE_FILTER_CHANGED_PROPERTY ||
	   prop == JFileChooser.FILE_HIDING_CHANGED_PROPERTY ||
	   prop == JFileChooser.FILE_SELECTION_MODE_CHANGED_PROPERTY) {
	    validateFileCache();
	}
    }

    /**
     * Obsolete - not used.
     */
    public void invalidateFileCache() {
    }

    public Vector getDirectories() {
	synchronized(fileCache) {
	    if (directories != null) {
		return directories;
	    }
	    Vector fls = getFiles();
	    return directories;
	}
    }

    public Vector getFiles() {
	synchronized(fileCache) {
	    if (files != null) {
		return files;
	    }
	    files = new Vector();
	    directories = new Vector();
	    directories.addElement(filechooser.getFileSystemView().createFileObject(
		filechooser.getCurrentDirectory(), "..")
	    );

	    for (int i = 0; i < getSize(); i++) {
		File f = (File)fileCache.get(i);
		if (filechooser.isTraversable(f)) {
		    directories.add(f);
		} else {
		    files.add(f);
		}
	    }
	    return files;
	}
    }

    public void validateFileCache() {
	File currentDirectory = filechooser.getCurrentDirectory();
	if (currentDirectory == null) {
	    return;
	}
	if (loadThread != null) {
	    loadThread.interrupt();
	}
	fetchID++;
	loadThread = new LoadFilesThread(currentDirectory, fetchID);
	loadThread.start();
    }

    /**
     * Renames a file in the underlying file system.
     *
     * @param oldFile a <code>File</code> object representing
     *        the existing file
     * @param newFile a <code>File</code> object representing
     *        the desired new file name
     * @return <code>true</code> if rename succeeded,
     *        otherwise <code>false</code>
     * @since 1.4
     */
    public boolean renameFile(File oldFile, File newFile) {
	synchronized(fileCache) {
	    if (oldFile.renameTo(newFile)) {
		validateFileCache();
		return true;
	    }
	    return false;
	}
    }


    public void fireContentsChanged() {
	// System.out.println("BasicDirectoryModel: firecontentschanged");
	fireContentsChanged(this, 0, getSize()-1);
    }

    public int getSize() {
	return fileCache.size();
    }

    public boolean contains(Object o) {
	return fileCache.contains(o);
    }

    public int indexOf(Object o) {
	return fileCache.indexOf(o);
    }

    public Object getElementAt(int index) {
	return fileCache.get(index);
    }

    /**
     * Obsolete - not used.
     */
    public void intervalAdded(ListDataEvent e) {
    }

    /**
     * Obsolete - not used.
     */
    public void intervalRemoved(ListDataEvent e) {
    }

    protected void sort(Vector v){
	ShellFolder.sortFiles(v);
    }

    // Obsolete - not used
    protected boolean lt(File a, File b) {
	// First ignore case when comparing
	int diff = a.getName().toLowerCase().compareTo(b.getName().toLowerCase());
	if (diff != 0) {
	    return diff < 0;
	} else {
	    // May differ in case (e.g. "mail" vs. "Mail")
	    return a.getName().compareTo(b.getName()) < 0;
	}
    }


    class LoadFilesThread extends Thread {
	File currentDirectory = null;
	int fid;
	Vector runnables = new Vector(10);
	
	public LoadFilesThread(File currentDirectory, int fid) {
	    super("Basic L&F File Loading Thread");
	    this.currentDirectory = currentDirectory;
	    this.fid = fid;
	}
	
	private void invokeLater(Runnable runnable) {
	    runnables.addElement(runnable);
	    SwingUtilities.invokeLater(runnable);
	}

	public void run() {
	    FileSystemView fileSystem = filechooser.getFileSystemView();

	    File[] list = fileSystem.getFiles(currentDirectory, filechooser.isFileHidingEnabled());

	    Vector acceptsList = new Vector();

	    if (isInterrupted()) {
		return;
	    }

	    // run through the file list, add directories and selectable files to fileCache
	    for (int i = 0; i < list.length; i++) {
		if(filechooser.accept(list[i])) {
		    acceptsList.addElement(list[i]);
		}
	    }

	    if (isInterrupted()) {
		return;
	    }

	    // First sort alphabetically by filename
	    sort(acceptsList);

	    Vector newDirectories = new Vector(50);
	    Vector newFiles = new Vector();
	    // run through list grabbing directories in chunks of ten
	    for(int i = 0; i < acceptsList.size(); i++) {
		File f = (File) acceptsList.elementAt(i);
		boolean isTraversable = filechooser.isTraversable(f);
		if (isTraversable) {
		    newDirectories.addElement(f);
		} else if (!isTraversable && filechooser.isFileSelectionEnabled()) {
		    newFiles.addElement(f);
		}
		if(isInterrupted()) {
		    return;
		}
	    }

	    Vector newFileCache = new Vector(newDirectories);
	    newFileCache.addAll(newFiles);

	    int newSize = newFileCache.size();
	    int oldSize = fileCache.size();

	    if (newSize > oldSize) {
		//see if interval is added
		int start = oldSize;
		int end = newSize;
		for (int i = 0; i < oldSize; i++) {
		    if (!newFileCache.get(i).equals(fileCache.get(i))) {
			start = i;
			for (int j = i; j < newSize; j++) {
			    if (newFileCache.get(j).equals(fileCache.get(i))) {
				end = j;
				break;
			    }
			}
			break;
		    }
		}
		if (start >= 0 && end > start
		    && newFileCache.subList(end, newSize).equals(fileCache.subList(start, oldSize))) {
		    invokeLater(new DoChangeContents(newFileCache.subList(start, end), start, null, 0, fid));
		    newFileCache = null;
		}
	    } else if (newSize < oldSize) {
		//see if interval is removed
		int start = -1;
		int end = -1;
		for (int i = 0; i < newSize; i++) {
		    if (!newFileCache.get(i).equals(fileCache.get(i))) {
			start = i;
			end = i + oldSize - newSize;
			break;
		    }
		}
		if (start >= 0 && end > start
		    && fileCache.subList(end, oldSize).equals(newFileCache.subList(start, newSize))) {
		    invokeLater(new DoChangeContents(null, 0, new Vector(fileCache.subList(start, end)),
						     start, fid));
		    newFileCache = null;
		}
	    }
	    if (newFileCache != null && !fileCache.equals(newFileCache)) {
		invokeLater(new DoChangeContents(newFileCache, 0, fileCache, 0, fid));
	    }
	    if (isInterrupted()) {
		cancelRunnables(runnables);
	    }
	}


	public void cancelRunnables(Vector runnables) {
	    for(int i = 0; i < runnables.size(); i++) {
		((DoChangeContents)runnables.elementAt(i)).cancel();
	    }
	}
    }

    class DoChangeContents implements Runnable {
	private List addFiles;
	private List remFiles;
	private boolean doFire = true;
	private int fid;
	private int addStart = 0;
	private int remStart = 0;
	private int change;
	
	public DoChangeContents(List addFiles, int addStart, List remFiles, int remStart, int fid) {
	    this.addFiles = addFiles;
	    this.addStart = addStart;
	    this.remFiles = remFiles;
	    this.remStart = remStart;
	    this.fid = fid;
	}

	synchronized void cancel() {
		doFire = false;
	}
	
	public synchronized void run() {
	    if (fetchID == fid && doFire) {
		int remSize = (remFiles == null) ? 0 : remFiles.size();
		int addSize = (addFiles == null) ? 0 : addFiles.size();
		synchronized(fileCache) {
		    if (remSize > 0) {
			fileCache.removeAll(remFiles);
		    }
		    if (addSize > 0) {
			fileCache.addAll(addStart, addFiles);
		    }
		    files = null;
		    directories = null;
		}
		if (remSize > 0 && addSize == 0) {
		    fireIntervalRemoved(BasicDirectoryModel.this, remStart, remStart + remSize - 1);
		} else if (addSize > 0 && remSize == 0 && fileCache.size() > addSize) {
		    fireIntervalAdded(BasicDirectoryModel.this, addStart, addStart + addSize - 1);
		} else {
		    fireContentsChanged();
		}
	    }
	}
    }
}


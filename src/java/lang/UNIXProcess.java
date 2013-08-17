/*
 * @(#)UNIXProcess.java.linux	1.32 00/10/11
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;

import java.io.*;
import java.util.Hashtable;
import java.util.Vector;

/* java.lang.Process subclass in the UNIX environment.
 *
 * @author Dave Brown
 */

class UNIXProcess extends Process {
    /* hastable of subprocesses, indexed by pid */
    static Hashtable subprocs = null;

    private boolean isalive = false;
    private int exit_code = 0;
    private FileDescriptor stdin_fd;
    private FileDescriptor stdout_fd;
    private FileDescriptor stderr_fd;
    private FileDescriptor sync_fd;
    int pid;

    /* stdin of the process */
    private OutputStream stdin_stream;

    /* streams directly to the process */

    private InputStream raw_stdout;
    private InputStream raw_stderr;

    /* what we hand back when someone calls
     * Process.getInputStream(), Process.getErrorStream()
     */
    private ProcessInputStream piped_stdout_in;
    private ProcessInputStream piped_stderr_in;

    /* The output streams attached to the above */

    private PipedOutputStream piped_stdout_out;
    private PipedOutputStream piped_stderr_out;

    /* the number of currently active thread readers */

    private int numReaders;

    static {
	subprocs = new Hashtable();
	/* start the reaper thread */

	initLockObject();
	java.security.AccessController.doPrivileged(
			    new java.security.PrivilegedAction() {
	    public Object run() {
		Thread t = new Thread(new UNIXProcessReaper(), "process reaper");
		t.setDaemon(true);
		t.start();
		t = new Thread(new UNIXProcessForker(), "process forker");
		t.setDaemon(true);
		t.start();
		return null;
	    }
	});
    }

    UNIXProcess() {};

    /* this is the reaping method */
    static native void initLockObject();
    private native int forkAndExec(String cmd[], String env[], String path,
				   FileDescriptor stdin_fd,
				   FileDescriptor stdout_fd,
				   FileDescriptor stderr_fd,
				   FileDescriptor sync_fd) throws java.io.IOException;

    /*
    This method is called in response to receiving a signal
    */
    private static void deadChild(int pid, int exitcode) {
	UNIXProcess p = (UNIXProcess) subprocs.get(new Integer(pid));
	if (p != null) {
	    synchronized (p) {
	        p.isalive = false;
		subprocs.remove(new Integer(pid));
		p.exit_code = exitcode;
		p.notifyAll();
	    }
	} else {
	    /* is there anything we should do if we reap a child that we know nothing about? */
	}
    }

    /* called for a reader thread to unregister itself and
     * notify waiters
     */

    synchronized int getNumReaders() throws InterruptedException {
	return numReaders;
    }

    synchronized void decrNumReaders() {
	if (--numReaders <= 0) {
	    /*	    System.out.println(Thread.currentThread().getName()+":UnixProc: closing streams:"); */
	    try {
		stdin_stream.close();
	    } catch (IOException e){}

	    /* for now we just swallow exceptions here */
	    try {
		raw_stdout.close();
	    } catch (IOException e){}
	    try {
		raw_stderr.close();
	    } catch (IOException e){}
	}
	notifyAll();
    }

    UNIXProcess(String cmdarray[], String env[]) throws java.io.IOException {
        this(cmdarray, env, null);
    }
    
    UNIXProcess(String cmdarray[], String env[], String path) throws java.io.IOException {
	stdin_fd = new FileDescriptor();
	stdout_fd = new FileDescriptor();
	stderr_fd = new FileDescriptor();
	sync_fd = new FileDescriptor();

	pid = forkAndExec(cmdarray, env, path,
			  stdin_fd, stdout_fd, stderr_fd, sync_fd);

	/* parent process */
	isalive = true;
	java.security.AccessController.doPrivileged(
				    new java.security.PrivilegedAction() {
	    public Object run() {
		stdin_stream = new BufferedOutputStream(new
						 FileOutputStream(stdin_fd));
		raw_stdout = new FileInputStream(stdout_fd);
		raw_stderr = new FileInputStream(stderr_fd);
		return null;
	    }
	});

	piped_stdout_out = new PipedOutputStream();
	piped_stderr_out = new PipedOutputStream();

	piped_stdout_in = new ProcessInputStream(this,
						 piped_stdout_out, raw_stdout);
	piped_stderr_in = new ProcessInputStream(this,
						 piped_stderr_out, raw_stderr);


	java.security.AccessController.doPrivileged(
				    new java.security.PrivilegedAction() {
	    public Object run() {
		Thread stdout_thread = new Thread(piped_stdout_in,
						  "stdout reader pid="+pid);

		stdout_thread.setDaemon(true);

		Thread stderr_thread = new Thread(piped_stderr_in,
					  "stderr reader pid="+pid);
		stderr_thread.setDaemon(true);

		stdout_thread.start();
		stderr_thread.start();
		return null;
	    }
	});

	numReaders = 2;

	subprocs.put(new Integer(pid), this);

	/* notfiy child to start */
	FileOutputStream f = (FileOutputStream)
	java.security.AccessController.doPrivileged(
				    new java.security.PrivilegedAction() {
	    public Object run() {
		return new FileOutputStream(sync_fd);
	    }
	});

	f.write('A'); // for Audrey.
	f.close();
    }

    public OutputStream getOutputStream() {
	return stdin_stream;
    }

    public InputStream getInputStream() {
	return piped_stdout_in;
    }

    public InputStream getErrorStream() {
	return piped_stderr_in;
    }

    public synchronized int waitFor() throws InterruptedException {
        while (isalive) {
	    wait();
	}
	return exit_code;
    }

    public synchronized int exitValue() {
	if (isalive) {
	    throw new IllegalThreadStateException("process hasn't exited");
	}
	return exit_code;
    }

    public native void destroy();
}


/* The process reaper.  The native run method sits in a loop
 * waiting for child processes to exit.  On receiving a
 * child death, the method calls deadChild on UnixProcess
 * to record the process exit code.
 */ 

class UNIXProcessReaper extends UNIXProcess implements Runnable {
    public native void run();
}

/* The process forker.  Due to a limitation in LinuxThreads,
 * if a child is created by a thread which dies before the
 * process no SIGCHLD will be received and no exit code will
 * be recorded.  To workaround this, a new thread is introduced
 * which forks all sub-processes and which will remain alive
 * until the VM is exited.
 */

class UNIXProcessForker extends UNIXProcess implements Runnable {
    public native void run();
}

/* The current problem w/ UNIXProcess is that the file descriptors
 * associated with stdout and stderr must be closed when the process
 * exits.  Ideally, we'd close these in the Process's finalize(), but
 * practice shows that the finalizer doesn't get run quickly enough:
 * under stress-testing we run out of file descriptors and the whole
 * runtime dies (ugly).
 * Closing the fd's after the exec'd process exits creates the race
 * condition that the caller of exec() must read() the entire stream
 * before exit, which doesn't work reliably.
 * As a workaround, we create a thread each to read from stdout/stderr
 * and save the data in a buffer, and Process.getInputStream()/getErrorStream()
 * read from these buffers.  It doesn't matter in this case that the fd's
 * are closed.  The process's output can be read long after it exits.
 * The code that closes the streams is synchronized around the readers
 * finishing.
 */


/* A kind of PipedInputStream that won't block the associated
 * PipedOutputStream from writing when the internal buffer fills
 * up.  Here, we chain buffers for reading and tack a new one
 * on the end for writing.
 *
 * It has a run() method b/c it's meant to be wrapped in a thread
 * to read from a process's stdout/stderr and store in the buffer
 * chain for the PipedInputStream (this)
 */

class ProcessInputStream extends PipedInputStream implements Runnable {

    /* the raw input stream of the process */
    InputStream ins;

    /* where we put what the process is saying */
    OutputStream outs;

    /* the process we're associated with */
    UNIXProcess p;

    /* the current buffer where written bytes get put.
     * reads come from the superclass's buffer[].
     */
    byte[] writeBuf = null;

    /* Whether our current write buffer is the superclass's
     * write buffer - i.e., is the mbuf chain active?
     */
    boolean chaining;

    /* current write position -valid only if chaining==true */
    int inPos;

    /* Where we store the chain of mbuf's.  Operates as a queue -
     * when the current read buffer ("buffer") gets depleted,
     * we switch to the next buffer in the chain.
     */
    Vector chain;

    ProcessInputStream(UNIXProcess p, PipedOutputStream o, InputStream i)
    throws IOException {
	super(o);
	this.p = p;
	outs = o;
	ins = i;
	writeBuf = null;
	chaining = false;
	inPos = 0;
	chain = new Vector();
    }

    protected synchronized void receive(int b) throws IOException {

	if (chaining) {

	    if (inPos == PIPE_SIZE) {
		/* current buffer full - chain it */
		writeBuf = new byte[PIPE_SIZE];
		chain.addElement(writeBuf);
		inPos = 0;
	    }
	    writeBuf[inPos++] = (byte) b;
	} else {
	    super.receive(b);
	    if (in == out) { /* now full - start chaining */
		inPos = 0;
		chaining = true;
		writeBuf = new byte[PIPE_SIZE];
		chain.addElement(writeBuf);
	    }
	}
    }

    public synchronized int read() throws IOException {
	if (!chaining) {
	    return super.read();
	}
	if (in == -1 && chain.size() != 0) {
	    /* superclass buffer depleted - cycle to next in chain */
	    buffer = (byte[]) chain.elementAt(0);
	    chain.removeElementAt(0);
	    in = out = 0;
	    if (chain.size() == 0) {
		/* chain empty */
		if (inPos == 0) {
		    in = -1;
		} else if (inPos == PIPE_SIZE) {
		    in = 0;
		} else {
		    in = inPos;
		}
		chaining = false;
		/* set end of stream to last byte we wrote in last mbuf */
	    }
	}
	return super.read();
    }

    public int available() throws IOException {
	int superAvailable = super.available();
	int chainSize = chain.size();
	if (chainSize == 0) {
	    return superAvailable;
	}
	return superAvailable + PIPE_SIZE * chainSize - (PIPE_SIZE - inPos);
    }

    public void run() {
	/* simply loop, reading from in and pushing to out, until
	 * in dies.
	 */
	byte[] buf = new byte[512];
	int nread;
	while (true) {
	    try {
		if ((nread = ins.read(buf)) < 0)
		    break;
		outs.write(buf, 0, nread);
		outs.flush();
	    } catch (IOException e) {
		break;
	    }
	}
	try {
	    outs.close();
	} catch (IOException e) {}

	/* notify process we're done reading */
	p.decrNumReaders();
    }
}

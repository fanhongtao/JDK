/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: ThreadControllerWrapper.java,v 1.3 2004/02/17 04:21:14 minchau Exp $
 */
package com.sun.org.apache.xml.internal.utils;

/**
 * A utility class that wraps the ThreadController, which is used
 * by IncrementalSAXSource for the incremental building of DTM.
 */
public class ThreadControllerWrapper
{
  
  /** The ThreadController pool   */
  static ThreadController m_tpool = new ThreadController();

  /**
   * Change the ThreadController that will be used to
   * manage the transform threads.
   *
   * @param tp A ThreadController object
   */
  public static void setThreadController(ThreadController tpool)
  {
    m_tpool = tpool;
  }
  
  public static Thread runThread(Runnable runnable, int priority)
  {
    return m_tpool.run(runnable, priority);
  }
  
  public static void waitThread(Thread worker, Runnable task)
    throws InterruptedException
  {
    m_tpool.waitThread(worker, task);
  }
  
  /**
   * Thread controller utility class for incremental SAX source. Must 
   * be overriden with a derived class to support thread pooling.
   *
   * All thread-related stuff is in this class.
   */
  public static class ThreadController
  {
    /**
     * This class was introduced as a fix for CR 6607339.
     */
    final class SafeThread extends Thread {
        private volatile boolean ran = false;
             
        public SafeThread(Runnable target) {
            super(target);
        }
                 
        public final void run() {
            if (Thread.currentThread() != this) {
                throw new IllegalStateException("The run() method in a"
                    + " SafeThread cannot be called from another thread.");
            }
            synchronized (this) {
               if (!ran) {
                   ran = true;
               }
               else {
                throw new IllegalStateException("The run() method in a"
                    + " SafeThread cannot be called more than once.");
               }                 
            }             
            super.run();
        }
    }
 

    /**
     * Will get a thread from the pool, execute the task
     *  and return the thread to the pool.
     *
     *  The return value is used only to wait for completion
     *
     *
     * NEEDSDOC @param task
     * @param priority if >0 the task will run with the given priority
     *  ( doesn't seem to be used in xalan, since it's allways the default )
     * @returns The thread that is running the task, can be used
     *          to wait for completion
     *
     * NEEDSDOC ($objectName$) @return
     */
    public Thread run(Runnable task, int priority)
    {

      Thread t = new SafeThread(task);

      t.start();

      //       if( priority > 0 )
      //      t.setPriority( priority );
      return t;
    }

    /**
     *  Wait until the task is completed on the worker
     *  thread.
     *
     * NEEDSDOC @param worker
     * NEEDSDOC @param task
     *
     * @throws InterruptedException
     */
    public void waitThread(Thread worker, Runnable task)
            throws InterruptedException
    {

      // This should wait until the transformThread is considered not alive.
      worker.join();
    }
  }
 
}

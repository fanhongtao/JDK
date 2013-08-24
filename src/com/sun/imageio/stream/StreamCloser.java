/*
 * @(#)StreamCloser.java	1.2 09/05/07
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.imageio.stream;

import java.io.IOException;
import java.util.Set;
import java.util.WeakHashMap;
import javax.imageio.stream.ImageInputStream;

/**
 * This class provide means to properly close hanging 
 * image input/output streams on VM shutdown.
 * This might be usefull for proper cleanup such as removal 
 * of temporary files.
 *
 * Addition of stream do not prevent it from being garbage collected
 * if no other references to it exists. Stream can be closed 
 * explicitly without removal from StremCloser queue. 
 * Explicit removal from the queue only helps to save some memory. 
 */
public class StreamCloser {

    private static WeakHashMap<CloseAction, Object> toCloseQueue = null;
    private static Thread streamCloser = null;

    public static void addToQueue(CloseAction ca) {
        synchronized (StreamCloser.class) {
            if (toCloseQueue == null) {
                toCloseQueue =
                    new WeakHashMap<CloseAction, Object>();
            }
            
            toCloseQueue.put(ca, null);

            if (streamCloser == null) {
                final Runnable streamCloserRunnable = new Runnable() {
                    public void run() {
                        if (toCloseQueue != null) {
                            synchronized (StreamCloser.class) {
                                Set<CloseAction> set =
                                    toCloseQueue.keySet();
                                CloseAction[] actions =
                                    new CloseAction[set.size()];
                                actions = set.toArray(actions);
                                for (CloseAction ca : actions) {
                                    if (ca != null) {
                                        try {
                                            ca.performAction();
                                        } catch (IOException e) {
                                        }
                                    }
                                }
                            }
                        }
                    }
                };
                
                java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction() {
                        public Object run() {
                            /* The thread must be a member of a thread group
                             * which will not get GCed before VM exit.
                             * Make its parent the top-level thread group.
                             */
                            ThreadGroup tg =
                                Thread.currentThread().getThreadGroup();
                            for (ThreadGroup tgn = tg;
                                 tgn != null;
                                 tg = tgn, tgn = tg.getParent());
                            streamCloser = new Thread(tg, streamCloserRunnable);
                            Runtime.getRuntime().addShutdownHook(streamCloser);
                            return null;
                        }
                    });
            }
        }
    }

    public static void removeFromQueue(CloseAction ca) {
         synchronized (StreamCloser.class) {
             if (toCloseQueue != null) {
                 toCloseQueue.remove(ca);
             }
         }
     }
 
     public static CloseAction createCloseAction(ImageInputStream iis) {
         return new CloseAction(iis);
     }
 
     public static final class CloseAction {
         private ImageInputStream iis;
 
         private CloseAction(ImageInputStream iis) {
             this.iis = iis;
         }
 
         public void performAction() throws IOException {
             if (iis != null) {
                 iis.close();
             }
         }
     }
 }

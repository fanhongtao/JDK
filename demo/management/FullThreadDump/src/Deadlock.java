/*
 * @(#)Deadlock.java	1.3 04/07/27
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)Deadlock.java	1.3 04/07/27
 */

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.io.IOException;

/**
 * This Deadlock class demonstrates the capability of performing
 * deadlock detection programmatically within the application using
 * the java.lang.management API.
 *
 * See ThreadMonitor.java for the use of java.lang.management.ThreadMXBean
 * API.
 */
public class Deadlock {
    public static void main(String[] argv) {
        Deadlock dl = new Deadlock();
 
        // Now find deadlock
        ThreadMonitor monitor = new ThreadMonitor();
        boolean found = false;
        while (!found) {
            found = monitor.findDeadlock();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.exit(1);
            }
        }

        System.out.println("\nPress <Enter> to exit this Deadlock program.\n");
        waitForEnterPressed();
    }

    private CyclicBarrier barrier = new CyclicBarrier(3);
    private Object a = new Object();
    private Object b = new Object();
    private Object c = new Object();
    public Deadlock() {
        // start 3 deadlocked threads 
        Thread d1 = new DeadlockThread1();
        Thread d2 = new DeadlockThread2();
        Thread d3 = new DeadlockThread3();
        d1.setDaemon(true); 
        d1.start();
        d2.setDaemon(true);
        d2.start();
        d3.setDaemon(true);
        d3.start();
    }

    class DeadlockThread1 extends Thread {
        public DeadlockThread1() {
            super("DeadlockedThread-1");
        }
        public void run() {
            A();
        }
        private void A() {
            synchronized (a) {
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    System.exit(1);
                } catch (BrokenBarrierException e) {
                    System.exit(1);
                }
                B();
            }
        }
        private void B() {
            try {
                barrier.await();
            } catch (InterruptedException e) {
                System.exit(1);
            } catch (BrokenBarrierException e) {
                System.exit(1);
            }
            synchronized (b) {
                throw new RuntimeException("D1 should not reach here.");
            }
        }
    }

    class DeadlockThread2 extends Thread {
        public DeadlockThread2() {
            super("DeadlockedThread-2");
        }
        public void run() {
            B();
        }
        private void B() {
            synchronized (b) {
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    System.exit(1);
                } catch (BrokenBarrierException e) {
                    System.exit(1);
                }
                C();
            }
        }
       private void C() {
            try {
                barrier.await();
            } catch (InterruptedException e) {
                System.exit(1);
            } catch (BrokenBarrierException e) {
                System.exit(1);
            }
            synchronized (c) {
                throw new RuntimeException("D2 should not reach here.");
            }
        }

    }

    class DeadlockThread3 extends Thread {
        public DeadlockThread3() {
            super("DeadlockedThread-3");
        }
        public void run() {
            C();
        }
        private void C() {
            synchronized (c) {
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    System.exit(1);
                } catch (BrokenBarrierException e) {
                    System.exit(1);
                }
                A();
            }
        }
        private void A() {
            try {
                barrier.await();
            } catch (InterruptedException e) {
                System.exit(1);
            } catch (BrokenBarrierException e) {
                System.exit(1);
            }
            synchronized (a) {
                throw new RuntimeException("D3 should not reach here.");
            }
        }
    }

    private static void waitForEnterPressed() {
        try {
            boolean done = false;
            while (!done) {
                char ch = (char) System.in.read();
                if (ch<0||ch=='\n') {
                    done = true;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}

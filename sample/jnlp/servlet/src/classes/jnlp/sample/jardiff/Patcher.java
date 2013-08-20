/*
 * @(#)Patcher.java	1.8 03/01/23
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package jnlp.sample.jardiff;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

/**
 * Patcher describes the necessary method to apply and create deltas.
 *
 * @version 1.8, 01/23/03
 */
public interface Patcher {
    /**
     * Applies a patch previously created with <code>createPatch</code>.
     * Pass in a delegate to be notified of the status of the patch.
     */
    public void applyPatch(PatchDelegate delegate, String oldJarPath,
                           String deltaPath, OutputStream result) throws IOException;
        
    /**
     * Callback used when patching a file.
     */
    public interface PatchDelegate {
        public void patching(int percentDone);
    }
}

/*
 * @(#)SortAlgorithm.java	1.11 04/07/26
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
 * @(#)SortAlgorithm.java	1.11 04/07/26
 */

/**
 * A generic sort demonstration algorithm
 * SortAlgorithm.java, Thu Oct 27 10:32:35 1994
 *
 * @author James Gosling
 * @version 	1.6f, 31 Jan 1995
 */

class SortAlgorithm {
    /**
     * The sort item.
     */
    private SortItem parent;

    /**
     * When true stop sorting.
     */
    protected boolean stopRequested = false;

    /**
     * Set the parent.
     */
    public void setParent(SortItem p) {
	parent = p;
    }

    /**
     * Pause for a while.
     */
    protected void pause() throws Exception {
	if (stopRequested) {
	    throw new Exception("Sort Algorithm");
	}
	parent.pause(parent.h1, parent.h2);
    }

    /**
     * Pause for a while and mark item 1.
     */
    protected void pause(int H1) throws Exception {
	if (stopRequested) {
	    throw new Exception("Sort Algorithm");
	}
	parent.pause(H1, parent.h2);
    }

    /**
     * Pause for a while and mark item 1 & 2.
     */
    protected void pause(int H1, int H2) throws Exception {
	if (stopRequested) {
	    throw new Exception("Sort Algorithm");
	}
	parent.pause(H1, H2);
    }

    /**
     * Stop sorting.
     */
    public void stop() {
	stopRequested = true;
    }

    /**
     * Initialize
     */
    public void init() {
	stopRequested = false;
    }

    /**
     * This method will be called to
     * sort an array of integers.
     */
    void sort(int a[]) throws Exception {
    }
}

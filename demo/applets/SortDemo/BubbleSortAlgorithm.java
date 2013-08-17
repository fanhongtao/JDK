/*
 * @(#)BubbleSortAlgorithm.java	1.6 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * A bubble sort demonstration algorithm
 * SortAlgorithm.java, Thu Oct 27 10:32:35 1994
 *
 * @author James Gosling
 * @version 	1.6f, 31 Jan 1995
 */
class BubbleSortAlgorithm extends SortAlgorithm {
    void sort(int a[]) throws Exception {
	for (int i = a.length; --i>=0; ) {
	    boolean swapped = false;
	    for (int j = 0; j<i; j++) {
		if (stopRequested) {
		    return;
		}
		if (a[j] > a[j+1]) {
		    int T = a[j];
		    a[j] = a[j+1];
		    a[j+1] = T;
		    swapped = true;
		}
		pause(i,j);
	    }
	    if (!swapped)
		return;
	}
    }
}

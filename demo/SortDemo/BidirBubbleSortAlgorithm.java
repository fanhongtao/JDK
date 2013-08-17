/*
 * @(#)BidirBubbleSortAlgorithm.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * A bi-directional bubble sort demonstration algorithm
 * SortAlgorithm.java, Thu Oct 27 10:32:35 1994
 *
 * @author James Gosling
 * @version 	1.6f, 31 Jan 1995
 */
class BidirBubbleSortAlgorithm extends SortAlgorithm {
    void sort(int a[]) throws Exception {
	int j;
	int limit = a.length;
	int st = -1;
	while (st < limit) {
	    st++;
	    limit--;
	    boolean swapped = false;
	    for (j = st; j < limit; j++) {
		if (stopRequested) {
		    return;
		}
		if (a[j] > a[j + 1]) {
		    int T = a[j];
		    a[j] = a[j + 1];
		    a[j + 1] = T;
		    swapped = true;
		}
		pause(st, limit);
	    }
	    if (!swapped) {
		return;
	    }
	    else
		swapped = false;
	    for (j = limit; --j >= st;) {
		if (stopRequested) {
		    return;
		}
		if (a[j] > a[j + 1]) {
		    int T = a[j];
		    a[j] = a[j + 1];
		    a[j + 1] = T;
		    swapped = true;
		}
		pause(st, limit);
	    }
	    if (!swapped) {
		return;
	    }
	}
    }
}

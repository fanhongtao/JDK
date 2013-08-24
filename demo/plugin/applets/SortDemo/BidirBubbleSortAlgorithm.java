/*
 * @(#)BidirBubbleSortAlgorithm.java	1.13 06/02/22
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
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
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
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
 * @(#)BidirBubbleSortAlgorithm.java	1.13 06/02/22
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

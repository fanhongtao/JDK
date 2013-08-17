/*
 * @(#)gc_md.h	1.5 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

#ifndef _GC_MD_H_
#define _GC_MD_H_

/*---- Win32 defines for garbage collection ----*/

#ifdef DEBUG
/* local debug & error checking flag */
/* #define LDEBUG  1 */
/* Define below if you want lots of verbose info at runtime */
/* #define TRACEGC 1 */
/* define to send strs to debugger (on Mac) */
#undef DPRINTFDEBUG
#endif

#ifdef TRACEGC
/* define below for REALLY detailed print */
#undef TRACEMARK
#undef TRACEFREE
#undef TRACECOMPACT
#undef PDEBUG
#endif

#ifdef PAGED_HEAPS  /************ PAGED HEAPS: ********************/

/* In order for the OS to allocate many chunks of aligned memory, leave a little
 * room at the end of our chunks that the OS can use for it's own purposes for 
 * the next allocation.
 */ 
#define OS_BLOCK_OVERHEAD  0	    /* in bytes */

/* On MacOS for instance, sysMemAlign allocates a block that may not be aligned
 * and returns an aligned pointer into that block.  We may be able to use that
 * space, so keep track of it.
 */
#undef WASTED_SPACE_IN_LEADER

/* TUNING ISSUES:
 * On machines that don't have a real memalign, WASTED_SPACE_IN_LEADER will be
 * true.  So chosing a page size is a trade off between wasting up to a page
 * per chunk to achive alignment, and using up memory for a large page map.
 * Note that a page is the smallest ammount that the gc will request from the
 * OS, so it should be reasonable (eg. not 1M on a 4M machine!)
 *
 * AlignmentWaste(worst) = PAGE_ALIGNMENT / MIN_XXX_PAGES * 
 *                         memory used / PAGE_ALIGNMENT;
 *                       = memory used / MIN_XXX_PAGES;
 * AlignmentWaste(avg)   = memory used / (2 * MIN_XXX_PAGES);
 *
 * PageMapSize(worst) = address space / PAGE_ALIGNMENT * 8
 *
 * MITIGATING FACTORS:
 *   A) The allocator tries to store the mark bits for a chunk in the aligment
 *      waste area.  Note that we need two mark bits for every two words, so:
 *          MarkBitsSize = memory used / 32;
 *      There are various round-off issues, but accounting for the savings of 
 *      storing the marks in the waste:
 * AlignmentWaste(mitigated_avg) = memory used / (2 * MIN_XXX_PAGES) - 
 *                                 memory used / 32;
 *      So, if you keep	MIN_XXX_PAGES > 16, AlignmentWaste approaches zero.
 *
 *   B) The page map is only large enough to span the pages handed to us by the
 *      OS, and it's unlikly that the OS will give us some pages at 0x00000000,
 *      then a few more pages near 0xFFFFFFFF.
 *
 * So, keep MIN_XXX_PAGES > 16, maximize PAGE_ALIGNMENT (especially if address 
 * space is large), but try to keep PAGE_ALIGNMENT * MIN_XXX_PAGES to a
 * size that the OS will usually be able to allocate.
 *
 */
#define PAGE_ALIGNMENT	    (64 * 1024)  /* page size is the same. */

/* # of bits to shift a pointer to get a page number
 * 2 ^ PTR_2_PAGE_SHIFT == PAGE_ALIGNMENT  */
#define PTR_2_PAGE_SHIFT    (16)


#ifdef LDEBUG		/* stress testing */
#define MIN_OBJ_PAGES (2)	  /* min # of pages to allocate for objects */
#define MIN_HANDLE_PAGES (2)	  /* min # of pages to allocate for handles */
#else
#define MIN_OBJ_PAGES (32)	  /* min # of pages to allocate for objects */
#define MIN_HANDLE_PAGES (8)	  /* min # of pages to allocate for handles */
#endif /* LDEBUG */

#endif  /************ END PAGED HEAPS ********************/
#endif /* !_GC_MD_H_ */

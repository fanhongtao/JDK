/*
 * @(#)alloc_cache.h	1.8 98/09/15
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

/*
 * Per-thread allocation cache
 */

#ifndef _JAVASOFT_ALLOC_CACHE_H_
#define _JAVASOFT_ALLOC_CACHE_H_

/* Default cache (refill) size */
#define	ALLOC_CACHE_SIZE	1024
/* Default maximum local allocation size, must be less than cache size */
#define	ALLOC_LOCAL_SIZE	(ALLOC_CACHE_SIZE/4)
/* Default handle cache refill count */
#define	ALLOC_HANDLE_COUNT	(ALLOC_CACHE_SIZE/8/3)

/*
 * Per-thread structure. Many fields are defined to be volatile because
 * they are manipulated by multiple threads outside of lock protection.
 */
struct alloc_cache {
    volatile int 	cache_busy;
    volatile long	cache_size;
    void	* volatile cache_tail;
    void	* volatile cache_handles;
    /* Meters: leave in for now */
    long	cache_allocations;
    long	cache_block_fills;
    long	cache_handle_fills;
};

/* Cache (refill) size */
extern int allocCacheSize;

/* Allocations smaller than this are attempted from local cache.  Use
   0 to turn off local allocation.  Must be less than cache size. */
extern int allocLocalSize;

/* Cache handle refill count */
extern int allocHandleCount;

/* Callback when thread exits */
extern void allocCacheCleanup(struct alloc_cache *);

#endif /* !_JAVASOFT_ALLOC_CACHE_H_ */

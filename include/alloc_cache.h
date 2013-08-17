/*
 * @(#)alloc_cache.h	1.5 98/07/01
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

#ifndef	_ALLOC_CACHE_H_
#define	_ALLOC_CACHE_H_

/* Default cache (refill) size */
#define	ALLOC_CACHE_SIZE	1024
/* Default maximum local allocation size, must be less than cache size */
#define	ALLOC_LOCAL_SIZE	(ALLOC_CACHE_SIZE/4)
/* Default handle cache refill count */
#define	ALLOC_HANDLE_COUNT	(ALLOC_CACHE_SIZE/8/3)

/*
 * Per-thread structure
 */
struct alloc_cache {
    volatile char	cache_busy;
    char	cache_pad[3];
    long	cache_size;
    void	*cache_tail;
    void	*cache_handles;
};

/* Cache (refill) size */
extern long allocCacheSize;

/* Allocations smaller than this are attempted from local cache.  Use
   0 to turn off local allocation.  Must be less than cache size. */
extern long allocLocalSize;

/* Cache handle refill count */
extern long allocHandleCount;

/* Callback when thread exits */
extern void allocCacheCleanup(struct alloc_cache *);

#endif /* _ALLOC_CACHE_H */

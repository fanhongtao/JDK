/*
 * @(#)alloc_cache.h	1.6 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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

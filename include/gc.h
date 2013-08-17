/*
 * @(#)gc.h	1.11 98/07/01
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

#ifndef _GC_H_
#define _GC_H_

#include "gc_md.h"


/*
 * Lock against heap modification.
 */
extern sys_mon_t *_heap_lock;
#define HEAP_LOCK_INIT()    monitorRegister(_heap_lock, "Heap lock")
#define HEAP_LOCK()	    sysMonitorEnter(_heap_lock)
#define HEAP_UNLOCK()	    sysMonitorExit(_heap_lock)
#define HEAP_LOCKED()	    sysMonitorEntered( _heap_lock)

/*
 * Define this if you want the mark phase to detect pointers into the
 * interior of objects.
 */
/* #define CHECK_INTERIOR_POINTERS */

#define OBJECTGRAIN     8
#define HANDLEGRAIN     8
#define BITSPERCHAR     8

/*
 * Types of overflows: we might respond to an overflow of a particular
 * error differently, e.g. expanding only the overflowing area.
 */
#define OVERFLOW_NONE	 0
#define OVERFLOW_OBJECTS 1
#define OVERFLOW_HANDLES 2

/*
 * Possible actions to take on overflows.  manageAllocFailure()
 * decides between these.
 */
#define OVERFLOW_ACT_FAIL	0
#define OVERFLOW_ACT_GC		1
#define OVERFLOW_ACT_FINALIZE	2
#define OVERFLOW_ACT_REFS	3
#define OVERFLOW_ACT_EXPAND	4
#define OVERFLOW_ACT_DESPERATE	5

/*
 * Memory block header (bottom three bits are flags):
 *
 * -------------------------------------------------------------
 * | <--- length --->| pinned | <- obj swapped -> | <- free -> |
 * -------------------------------------------------------------
 * 31		     3	      2			  1	       0
 */
typedef long hdr;

#define obj_geth(p) (*((hdr *)(p)))
#define obj_seth(p, h) (*((hdr *)(p)) = (h))
#define h_len(h) ((h) & ~(OBJECTGRAIN-1))
#define h_free(h) ((h) & 1)
#define h_bumplen(h, l) ((h) += (l))

#define obj_len(p) (obj_geth(p)&~(OBJECTGRAIN-1))
#define obj_setlf(p, l, f) (obj_geth(p) = (l)|(f))
#define obj_bumplen(p, l) (obj_geth(p) += (l))
#define obj_free(p) (obj_geth(p)&1)
#define obj_setfree(p) (obj_geth(p) |= 1)
#define obj_clearfree(p) (obj_geth(p) &= ~1)
#define obj_pinned(p) (obj_geth(p) & 4)
#define obj_pin(p) (obj_geth(p) |= 4)
#define obj_unpin(p) (obj_geth(p) &= ~4)

/*
 * The marking code relies upon the values representing the three mark
 * states to be ordered numerically: NoMark < SoftMark < HardMark.
 */
#define NoMark   0
#define SoftMark 1
#define HardMark 3

#define MarkPtr(p, v) _MarkPtr(((unsigned int) (p) & ~(OBJECTGRAIN - 1)), v)
#define ClearMarkPtr(p, v) _ClearMarkPtr(((unsigned int)(p)&~(OBJECTGRAIN-1)),v)
#define IsMarked(p) _IsMarked((unsigned int) (p) & ~(OBJECTGRAIN - 1))

#define SOFTREFBAGSIZE 200  /* max number of soft refs to kill in one cycle */


#ifndef PAGED_HEAPS /************ CONTIGUOUS HEAPS: ********************/

#define ValidObject(p)	((((int)(p)) & (OBJECTGRAIN-1)) == 0 &&	    	\
			 (unsigned char *)(p) >= opmin &&	      	\
			 (unsigned char *)(p) <  opmax)
#define ValidHandle(p)	(((int) (p) & (sizeof(JHandle)-1)) == 0 &&  	\
			 (unsigned char *)(p) >= hpmin &&	        \
			 (unsigned char *)(p) <= hpmax)
/* ValidHorO() assumes OBJECTGRAIN=sizeof(JHandle)... */
#define ValidHorO(p)	(((int) (p) & (OBJECTGRAIN-1)) == 0 &&	    	\
			 (unsigned char *)(p) >= hpmin &&		\
			 (unsigned char *)(p) <= opmax)
#define SetLimits()							\
    register unsigned char *const opmin = opool,	    		\
                           *const opmax = opoollimit,	    		\
		           *const hpmin = hpool,	   	       	\
		           *const hpmax = hpoollimit-sizeof(JHandle)

#define POP_FREE_HANDLE(hp) 						\
    hp = (JHandle *)hpoolfreelist; 					\
    if (hp) {								\
        hpoolfreelist = (unsigned char *)hp->methods;			\
    }

#define PUSH_FREE_HANDLE(hp) \
    hp->methods = (struct methodtable *)hpoolfreelist; \
    hpoolfreelist = (unsigned char *)hp;

/* Mark bit access assumes contiguity of handles and objects */
#define MARKINDEX(p)	(((unsigned char *)(p) - hpmin) >> 7)
#define BITOFFSET(p)	((((unsigned char *)(p) - hpmin) >> 2) & 0x1e)

#define _MarkPtr(p, v)	(markbits[MARKINDEX(p)] |= (v) << BITOFFSET(p))
#define _ClearMarkPtr(p, v) (markbits[MARKINDEX(p)] &= ~((v) << BITOFFSET(p)))
#define _IsMarked(p)	((markbits[MARKINDEX(p)] >> BITOFFSET(p)) &3)

/* set the second word in an object (from ptr to header) to 0x55555555 */
#define CHECK_WORD_INDEX 1

#define MAP_OVER_HANDLES_FROM_START(MO_hp) {		\
    JHandle *MOH_limit = (JHandle *) hpmax;		\
    for (MO_hp = (JHandle *) hpool; MO_hp <= MOH_limit; MO_hp++) {

#define END_MAP_OVER_HANDLES_FROM_START			\
    } /* end for */					\
} /* end MAP_OVER_HANDLES_FROM_START */

#define MAP_OVER_OBJECTS_FROM_START(p) {		\
    unsigned char *MOO_limit = opmax;			\
    unsigned char *MOO_start = opmin;			\
    for (p = opmin;					\
	 p < MOO_limit;					\
	 p += obj_len(p)) {

#define END_MAP_OVER_OBJECTS_FROM_START			\
    } /* end for */					\
} /* end END_MAP_OVER_OBJECTS_FROM_START */


#else /************ PAGED HEAPS: ********************/

/* gc philosophy makes it necessary to detect if an arbitrary int is
 * (possibly) a handle or object ref.
 * A value is (possibly) valid if it is properly aligned, and it 
 * points into a page that has a page map entry of the proper type.
 */
/* assumes ValidHorO already */
#define GetPageMapEntry(p)					  	\
	     (page_map[((int)(p) - (int)mem_base) >> PTR_2_PAGE_SHIFT])

#define ValidObject(p)	((((int)(p)) & (OBJECTGRAIN-1)) == 0 &&	    	\
	     (void *)(p) >= mem_base &&		    			\
	     (void *)(p) <  mem_top &&			    		\
	     (GetPageMapEntry((p)).chunk_size > 0))
#define ValidHandle(p)	(((((int)(p)) & (HANDLEGRAIN-1)) == 0) &&   	\
	     ((void *)(p) >= mem_base) &&		    		\
	     ((void *)(p) <  mem_top) &&		    		\
	     (GetPageMapEntry((p)).chunk_size < 0))
/* ValidHorO() assumes OBJECTGRAIN == HANDLEGRAIN... */
#define ValidHorO(p)	((((int)(p)) & (HANDLEGRAIN-1)) == 0 &&	    	\
	     (void *)(p) >= mem_base &&		    			\
	     (void *)(p) <  mem_top &&			    		\
	     (GetPageMapEntry((p)).chunk_size != 0))

#define SetLimits() int SL_dufus = 0

/* assumes ValidHorO already */
#define ChunkBase(p)	(void *)				    	\
	     (((int)(p) & ~(PAGE_ALIGNMENT - 1)) -			\
	      (GetPageMapEntry((p)).page_number << PTR_2_PAGE_SHIFT))
	     
/* curHanBlkP must be set in advance!!! */
#define POP_FREE_HANDLE(hp) 						\
    hp = (JHandle *)curHanBlkP->freePtr; 			    	\
    if (hp) {							    	\
        curHanBlkP->freePtr = (unsigned char *)hp->methods;	    	\
    }

/* Can only be called within a MAP_OVER_HANDLES_FROM_START loop 
 * - uses MOH_chunk instead of curHanBlkP for efficiency.
 */
#define PUSH_FREE_HANDLE(hp) \
    hp->methods = (struct methodtable *)MOH_chunk->freePtr; \
    MOH_chunk->freePtr = (unsigned char *)hp;

#define MARKINDEX(p)	(((int)(p) & (PAGE_ALIGNMENT - 1)) >> 7)
#define BITOFFSET(p)	((((int)(p) & (PAGE_ALIGNMENT - 1)) >> 2) & 0x1e)

#define _MarkPtr(p, v)	(GetPageMapEntry(p).mark_bits[MARKINDEX(p)] |=	   \
                         (v) << BITOFFSET(p))
#define _ClearMarkPtr(p, v) (GetPageMapEntry(p).mark_bits[MARKINDEX(p)] &= \
                             ~((v) << BITOFFSET(p)))
#define _IsMarked(p)	((GetPageMapEntry(p).mark_bits[MARKINDEX(p)]	   \
                          >> BITOFFSET(p)) & 3)

/* # of bytes of markbits we need per page: */
#define MARK_BITS_SIZE	    ((PAGE_ALIGNMENT / (OBJECTGRAIN * BITSPERCHAR)) * 2)

/*
 * Part of Java memory management and garbage collection.
 *
 * This supports a discontiguous gcable heap, which is useful for the
 * Mac OS, or other platforms without good memory mapping support.
 *
 * CHUNKS:
 * Memory is requested from the OS in "Chunks" of n pages, which are
 * PAGE_ALIGNMENT aligned and sized. Handles and objects are allocated out of
 * different chunks.  When more memory is needed, additional chunks can be
 * allocated.  When chunks are free, they may be returned to  the OS.  Chunks
 * don't need to be contiguous.  Handle chunks and object chunks are linked 
 * into seperate, doubly linked lists, which are sorted by chunk address.  On
 * platforms without real "memalign" support, there may be unaligned (wasted)
 * space that precedes the true chunk that we can use for something else 
 * (markbits come to mind).
 */
 
/* fields marked ### MUST BE OBJECT AND/OR HANDLE GRAIN ALIGNED */
typedef struct CHUNK_BLK {	/* a chunk of pages */
    void* chunkHandle;		/* OS handle to this chunk */
    struct CHUNK_BLK *nextPtr;	/* ptr to next chunk header */
    struct CHUNK_BLK *prevPtr;	/* ptr to previous chunk header */
    long chunkFlags;		/* misc flags */
    long allocSize;		/* == (endPtr - startPtr)### */
    long freeCnt;		/* # of free bytes in this chunk */
    unsigned char *startPtr;	/* ptr to starting byte### */
    unsigned char *endPtr;	/* ptr past last byte### */
    unsigned char *freePtr;	/* ptr to first free space CANDIDATE 
	* (may not really be free), or it might be a ptr to a free list
	* of objects, depending on phase of the moon.
	*/
	/* users may modify start and end ptrs, but not this one: */
    unsigned char *physEndPtr;			    
#ifdef WASTED_SPACE_IN_LEADER
	/* WARNING:  clearLocalMarkBits assumes that only markbits are stored
     	 * in the waste !!!
     	 */
    unsigned char *wasteStartPtr; /* ptr to starting wasted byte */
    unsigned char *wasteFreePtr;  /* ptr to first free wasted byte */
    				  /* wasteEndPtr == the ChunkBlk pointer */
#endif /* WASTED_SPACE_IN_LEADER*/
} ChunkBlk, *ChunkBlkP;

/* CHUNK_BLK->chunkFlags bits: */
     /* set this bit in chunkFlags if any objects in the chunk are pinned */
#define CHUNK_PINNED 1		

/* doubly-linked list of handle chunks, in address order: */
extern ChunkBlkP firstHanBlkP;	  
extern ChunkBlkP lastHanBlkP;
extern ChunkBlkP curHanBlkP;
/* doubly-linked list of object chunks, in address order: */
extern ChunkBlkP firstObjBlkP;
extern ChunkBlkP lastObjBlkP;
extern ChunkBlkP curObjBlkP;

/* store this into the last two words of the chunk to detect overwrites */
/* Odd to make a poor pointer, 111 in the low bits looks like a swapped 
 * free block if a header! */
#define ALMOST_WORD    0x77777777
#define ULTIMATE_WORD  0xBAADDEED  /* Why not. */
/* set the third word in an object (from ptr to header) to 0x55555555 */
#define CHECK_WORD_INDEX 2	   

/* Macros to abstract out looping over all handles or objects.
 * Note that you can't "break" out of this loop. Use goto or return instead.
 */
#define MAP_OVER_HANDLES_FROM_START(MO_hp) {		\
    ChunkBlkP MOH_chunk = firstHanBlkP;			\
    JHandle *MOH_limit;					\
    do {						\
	for (MO_hp = (JHandle *)MOH_chunk->startPtr,	\
	     MOH_limit = (JHandle *)MOH_chunk->endPtr;	\
	     MO_hp < MOH_limit; MO_hp++) {

#define END_MAP_OVER_HANDLES_FROM_START			\
	}  /* end for */				\
	MOH_chunk = MOH_chunk->nextPtr;			\
    } while (MOH_chunk != firstHanBlkP);		\
} /* end MAP_OVER_HANDLES_FROM_START */

#define MAP_OVER_OBJECTS_FROM_START(MO_p)   {		\
    ChunkBlkP MOO_chunk = firstObjBlkP;			\
    unsigned char *MOO_limit;				\
    unsigned char *MOO_start;				\
    do {						\
	for ((MO_p) = MOO_chunk->startPtr, 		\
	     MOO_start = MOO_chunk->startPtr,		\
	     MOO_limit = MOO_chunk->endPtr;		\
	     (MO_p) < MOO_limit;			\
	     (MO_p) += obj_len(MO_p)) {

#define END_MAP_OVER_OBJECTS_FROM_START			\
	}  /* end for */				\
	MOO_chunk = MOO_chunk->nextPtr;			\
    } while (MOO_chunk != firstObjBlkP);		\
} /* end END_MAP_OVER_OBJECTS_FROM_START */

#endif /************ END PAGED HEAPS ********************/	


#ifdef WASTED_SPACE_IN_LEADER
/* following functions defined in gc_md.c: */
void initWastedSpaceInChunk(ChunkBlkP chunk);
void sysCheckWastedSpace(ChunkBlkP chunk);
void clearLocalMarkBits(void);
void* allocMarkBitsLocally(ChunkBlkP blkP);
#else
#define initWastedSpaceInChunk(xxx) 0
#define sysCheckWastedSpace(xxx) 0
#define clearLocalMarkBits() 0
#define allocMarkBitsLocally(xxx) 0
#endif

#endif /* !_GC_H_ */

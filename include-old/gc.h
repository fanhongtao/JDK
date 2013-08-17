/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

#ifndef _JAVASOFT_GC_H_
#define _JAVASOFT_GC_H_

#include "gc_md.h"
#include "oobj.h"
#include "util.h"
#include "interpreter.h"
#include "monitor.h"

/*
 * Utility function to lock all systems locks
 */
void lock_for_debugger(struct execenv *ee);
void unlock_for_debugger(struct execenv *ee);

/*
 * Calls FreeClass and clears out the ClassClass content inside the HEAP_LOCK.
 */
void GCFreeClass(ClassClass *cb);

struct Hjava_lang_String;
struct Hjava_lang_String * internString(struct Hjava_lang_String *str);

bool_t isObject(void *p);
bool_t isHandle(void *p);

void pin_object(void *obj);
void unpin_object(void *obj);
int pinned_object(void *obj);

/*
 * Lock against heap modification.
 */
extern sys_mon_t *_heap_lock;
#define HEAP_LOCK_INIT()    monitorRegister(_heap_lock, "Heap lock")
#define HEAP_LOCK(self)	    sysMonitorEnter(self, _heap_lock)
#define HEAP_UNLOCK(self)   sysMonitorExit(self, _heap_lock)
#define HEAP_LOCKED(self)   sysMonitorEntered(self, _heap_lock)

/*
 * Heap parameters.
 */
extern float minHeapFreePercent;
extern float maxHeapFreePercent;
extern long minHeapExpansion;
extern long maxHeapExpansion;

/*
 * Time of most recent garbage collection, in millisecond ticks
 */
extern jlong timeOfLastGC;

/*
 * Define this if you want the mark phase to detect pointers into the
 * interior of objects.
 */
/* #define CHECK_INTERIOR_POINTERS */

#define OBJECTGRAIN     2 * sizeof (void *)
#define HANDLEGRAIN     2 * sizeof (void *)
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
#define OVERFLOW_ACT_EXPAND	2
#define OVERFLOW_ACT_REFS	3
#define OVERFLOW_ACT_DESPERATE	4

/*
 * The size of a contiguous region to move in the compacter.  If you set
 * to 0, then the compacter will never move more than one object at time.
 * There's a tradeoff involved in picking a value for this.  The code to
 * move multiple objects requires multiple passes over the object, one to
 * find the extent of the region to move, the actual move and finally a
 * pass to swap the handle back.  If you slide a really large region of
 * memory you'll probably blow the cache, so a relatively small value is
 * good.
 */
#ifndef GC_SLIDE_LENGTH
#define GC_SLIDE_LENGTH 256
#endif

/* 
 * These are the flag bits used by the garbage collector.  They may be
 * overridden by definitions placed in the gc_md.h file.
 */
#ifndef OBJ_SWAPPED
#define OBJ_SWAPPED  ((unsigned)1<<2)
#endif

#ifndef OBJ_PINNED
#define OBJ_PINNED   ((unsigned)1<<1)
#endif

#ifndef OBJ_FREE
#define OBJ_FREE     ((unsigned)1<<0)
#endif

#ifndef FAST_MONITOR

/*
 * Memory block header (bottom three bits are flags), and swapped isn't
 * strictly necessary for the CONTIGUOUS_HEAPS implemented.  It's in there
 * right now in DEBUG mode and for compatibility with PAGED_HEAPS.
 *
 * ----------------------------------------------
 * | <--- length ---> | swapped | pinned | free |
 * ----------------------------------------------
 *  31              3          2        1      0
 */
typedef int32_t hdr;

#define obj_geth(p) (*((hdr *)(p)))
#define obj_seth(p, h) (*((hdr *)(p)) = (h))

#define obj_swap(hp) \
if (1) { \
    OBJECT T = *hp->obj; \
    *hp->obj = (OBJECT) hp; \
    hp->obj = (OBJECT *) T; \
} else ((void) 0)

#else  /* FAST_MONITOR */

/*
 * Memory block header (bottom three bits are flags), and swapped isn't
 * strictly necessary for the CONTIGUOUS_HEAPS implemented.  It's in there
 * right now in DEBUG mode and for compatibility with PAGED_HEAPS.
 *
 * ----------------------------------------------
 * | <--- length ---> | swapped | pinned | free |
 * ----------------------------------------------
 *  31              3          2        1      0
 * -------------------------------------------------------------------
 * | heavy   | (struct execenv *ee) or (sys_mon_t *mid) |    zero    |
 * -------------------------------------------------------------------
 *         31 30                                       3   2   1   0
 */
typedef int64_t hdr;
typedef int32_t ohdr;                       /* Old object header */

#define obj_geth(p) (*((int32_t *)(p)))
#define obj_seth(p, h) (*((int32_t *)(p)) = (h))

#define obj_swap(hp) \
if (1) { \
    OBJECT T = *(hp->obj - 1); \
    *(hp->obj - 1) = (OBJECT) hp; \
    hp->obj = (OBJECT *) T; \
} else ((void) 0)

#endif /* FAST_MONITOR */

/*
 * These macros operate on the header word of an object.
 */
#define h_len(h) ((h) & ~(OBJ_SWAPPED | OBJ_FREE | OBJ_PINNED))
#define h_setlf(h, l, f) (h = (l)|(f))
#define h_bumplen(h, l) ((h) += (l))
#define h_free(h) ((h) & OBJ_FREE)
#define h_setfree(h) (h |= OBJ_FREE)
#define h_clearfree(h) (h &= ~OBJ_FREE)
#define h_hbf(h) (h & OBJ_HBF)
#define h_sethbf(h) (h |= OBJ_HBF)
#define h_pinned(h) (h & OBJ_PINNED)
#define h_pin(h) (h |= OBJ_PINNED)
#define h_unpin(h) (h &= ~OBJ_PINNED)
#define h_swapped(h) (h & OBJ_SWAPPED)
#define h_setswapped(h) (h |= OBJ_SWAPPED)
#define h_clearswapped(h) (h &= ~OBJ_SWAPPED)

/*
 * These macros operate on a pointer to the header word of an object.
 */
#define obj_len(p) (h_len(obj_geth(p)))
#define obj_setlf(p, l, f) (h_setlf(obj_geth(p), (l), (f)))
#define obj_bumplen(p, l) (h_bumplen(obj_geth(p), (l)))
#define obj_free(p) (h_free(obj_geth(p)))
#define obj_setfree(p) (h_setfree(obj_geth(p)))
#define obj_clearfree(p) (h_clearfree(obj_geth(p)))
#define obj_hbf(p) (h_hbf(obj_geth(p)))
#define obj_sethbf(p) (h_sethbf(obj_geth(p)))
#define obj_pinned(p) (h_pinned(obj_geth(p)))
#define obj_pin(p) (h_pin((obj_geth(p))))
#define obj_unpin(p) (h_unpin(obj_geth(p)))
#define obj_swapped(p) (h_swapped(obj_geth(p)))
#define obj_setswapped(p) (h_setswapped(obj_geth(p)))
#define obj_clearswapped(p) (h_clearswapped((obj_geth(p))))


#ifndef PAGED_HEAPS /************ CONTIGUOUS HEAPS: ********************/

#define ValidObject(p) (IS_ALIGNED(((uintptr_t)(p)), OBJECTGRAIN) &&    \
			 (unsigned char *)(p) >= opmin &&	      	\
			 (unsigned char *)(p) <  opmax)
#define ValidHandle(p) (IS_ALIGNED(((uintptr_t)(p)), sizeof(JHandle)) && \
			 (unsigned char *)(p) >= hpmin &&	         \
			 (unsigned char *)(p) <= hpmax)
/* ValidHorO() assumes OBJECTGRAIN=sizeof(JHandle)... */
#define ValidHorO(p)   (IS_ALIGNED(((uintptr_t)(p)), OBJECTGRAIN) &&    \
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
#define MARKINDEX(p)	(((unsigned char *)(p) - hpmin) >> 8)
#define BITOFFSET(p)	((((unsigned char *)(p) - hpmin) >> 3) & 0x1f)

#define MarkPtr(p)	(markbits[MARKINDEX(p)] |= 1 << BITOFFSET(p))
#define ClearMarkPtr(p) (markbits[MARKINDEX(p)] &= ~(1 << BITOFFSET(p)))
#define IsMarked(p)	((markbits[MARKINDEX(p)] >> BITOFFSET(p)) & 1)

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

/*
 * To override this macro, define a platform specific version
 * in gc_md.h on your platform.
 */
#ifndef PTR_2_PAGENUM
#define PTR_2_PAGENUM(ptr)	(ptr >> PTR_2_PAGE_SHIFT)
#endif 	/* PTR_2_PAGENUM */

/* gc philosophy makes it necessary to detect if an arbitrary int is
 * (possibly) a handle or object ref.
 * A value is (possibly) valid if it is properly aligned, and it 
 * points into a page that has a page map entry of the proper type.
 */
/* assumes ValidHorO already */
#define GetPageMapEntry(p)					  	\
	     (page_map[PTR_2_PAGENUM((uintptr_t)(p) - (uintptr_t)mem_base)])

#define ValidObject(p) (IS_ALIGNED(((uintptr_t)(p)), OBJECTGRAIN) && \
	     (void *)(p) >= mem_base &&		    			\
	     (void *)(p) <  mem_top &&			    		\
	     (GetPageMapEntry((p)).chunk_size > 0))
#define ValidHandle(p) (IS_ALIGNED(((uintptr_t)(p)), HANDLEGRAIN) && \
	     ((void *)(p) >= mem_base) &&		    		\
	     ((void *)(p) <  mem_top) &&		    		\
	     (GetPageMapEntry((p)).chunk_size < 0))
/* ValidHorO() assumes OBJECTGRAIN == HANDLEGRAIN... */
#define ValidHorO(p) (IS_ALIGNED(((uintptr_t)(p)), HANDLEGRAIN) && \
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

#ifndef MARKINDEX
#define MARKINDEX(p)	(((int)(p) & (PAGE_ALIGNMENT - 1)) >> HANDLEGRAIN)
#endif
#ifndef BITOFFSET
#define BITOFFSET(p)	((((int)(p) & (PAGE_ALIGNMENT - 1)) >> ((sizeof(int))-1)) & 0x1f)
#endif

#define MarkPtr(p)	(GetPageMapEntry(p).mark_bits[MARKINDEX(p)] |=	   \
                         1 << BITOFFSET(p))
#define ClearMarkPtr(p) (GetPageMapEntry(p).mark_bits[MARKINDEX(p)] &= \
			  ~(1 << BITOFFSET(p)))
#define IsMarked(p)	((GetPageMapEntry(p).mark_bits[MARKINDEX(p)]	   \
                          >> BITOFFSET(p)) & 1)

/* # of bytes of markbits we need per page: */
#define MARK_BITS_SIZE	    (PAGE_ALIGNMENT / (OBJECTGRAIN * BITSPERCHAR))

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


#ifdef DEBUG
#define FILL_CHECK_WORD(p) \
if (obj_len(p) >= ((CHECK_WORD_INDEX + 1) * sizeof(unsigned char *))) { \
    ((unsigned char **)(p))[CHECK_WORD_INDEX] = (unsigned char *) 0x55555555; \
} else ((void) 0)
#else
#define FILL_CHECK_WORD(p) ((void) 0)
#endif


#ifdef WASTED_SPACE_IN_LEADER
/* following functions defined in gc_md.c: */
void initWastedSpaceInChunk(ChunkBlkP chunk);
void sysCheckWastedSpace(ChunkBlkP chunk);
void clearLocalMarkBits(void);
void* allocMarkBitsLocally(ChunkBlkP blkP);
#else
#define initWastedSpaceInChunk(xxx) ((void)0)
#define sysCheckWastedSpace(xxx) ((void)0)
#define clearLocalMarkBits() ((void)0)
#define allocMarkBitsLocally(xxx) ((void *)0)
#endif

#endif /* !_JAVASOFT_GC_H_ */

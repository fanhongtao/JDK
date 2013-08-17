/*
 * @(#)sysmacros_md.h	1.13 97/02/07
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */

#ifndef _WIN32_SYSMACROS_MD_H_
#define _WIN32_SYSMACROS_MD_H_

#define sysMalloc	malloc
#define sysFree		free
#define sysCalloc 	calloc
#define sysRealloc	realloc

/* A macro for sneaking into a sys_mon_t to get the owner sys_thread_t */
#define sysMonitorOwner(mid)   ((mid)->monitor_owner)

#ifdef DEBUG
#define sysAssert(expression) {		\
    if (!(expression)) {		\
	DumpThreads();			\
	panic("\"%s\", line %d: assertion failure\n", __FILE__, __LINE__); \
    }					\
}
#else
#define sysAssert(expression) 0
#endif

/*
 * Check whether an exception occurred.  This also gives us the oppor-
 * tunity to use the already-required exception check as a trap for other
 * system-specific conditions.
 */
#define sysCheckException(ee) \
	if (!exceptionOccurred(ee)) { \
	   continue; \
	}

#define sysIsAbsolute(s) \
	(*(s) == '/' || *(s) == '\\' \
	 || (isalpha(*(s)) && *((s)+1) == ':' \
	     && (*((s)+2) == '\\' || *((s)+2) == '/')))

#define sysRead(fd, buf, n)	read(fd, buf, n)
#define sysWrite(fd, buf, n)	write(fd, buf, n)
#define sysClose(fd)		close(fd)
#define sysReadDir(dirp)	readdir(dirp)
#define sysCloseDir(dirp)	closedir(dirp)
#define sysSeek(fd,where,whence) lseek(fd,where,whence)
#define sysRename(src,dst)	rename(src,dst)
#define sysRmdir(dirp)          _rmdir(dirp)

/*
 * Simple, fast recursive lock for the monitor cache.
 */
#include "mutex_md.h"

typedef struct {
    mutex_t mutex;
    long entry_count;
    unsigned long owner;
} cache_lock_t;

/*
 * We do leave the mutex locked across the whole cache lock to avoid
 * the extra unlock and lock that a smaller critical region would entail.
 */
extern cache_lock_t _moncache_lock;
#define sysCacheLockInit() {   mutexInit(&_moncache_lock.mutex);	\
			       _moncache_lock.entry_count = 0;		\
			       _moncache_lock.owner = 0;		\
			   }
#define sysCacheLock()     {   mutexLock(&_moncache_lock.mutex);	\
			       sysAssert(_moncache_lock.entry_count >= 0);\
			       if (_moncache_lock.entry_count++ == 0) {	\
				  _moncache_lock.owner = GetCurrentThreadId();\
			       }					\
			   }
/* Should not need locking: */
#define sysCacheLocked()   (_moncache_lock.owner == GetCurrentThreadId())
#define sysCacheUnlock()   {   sysAssert(_moncache_lock.entry_count > 0);\
			       if (--_moncache_lock.entry_count == 0) {	\
				  _moncache_lock.owner = 0;		\
			       }					\
			       mutexUnlock(&_moncache_lock.mutex);	\
			   }

/* The current JIT interface requires sysMonitorExit to be a function */
#define sysMonitorExitLocked(mid)	sysMonitorExit(mid)

#endif /*_WIN32_SYSMACROS_MD_H_*/

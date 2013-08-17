/*
 * @(#)sys_api.h	1.66 99/01/22
 *
 * Copyright 1995-1999 by Sun Microsystems, Inc.,
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
 * System or Host dependent API.  This defines the "porting layer" for
 * POSIX.1 compliant operating systems.
 */

#ifndef _SYS_API_H_
#define _SYS_API_H_

/*
 * typedefs_md.h includes basic types for this platform;
 * any macros for HPI functions have been moved to "sysmacros_md.h"
 */
#include "typedefs_md.h"

/*
 * Miscellaneous system utility APIs that fall outside the POSIX.1
 * spec.
 *
 * Until POSIX (P1003.1a) formerly P.4 is standard, we'll use our
 * time struct definitions found in timeval.h.
 */
#include "timeval.h"
long 	sysGetMilliTicks(void);
long    sysTime(long *);
int64_t sysTimeMillis(void);

#include "time.h"
struct tm * sysLocaltime(time_t *, struct tm*);
struct tm * sysGmtime(time_t *, struct tm*);
void        sysStrftime(char *, int, char *, struct tm *);
time_t      sysMktime(struct tm*);

/*
 * System API for general allocations
 */
void *	sysMalloc(size_t);
void *	sysRealloc(void*, size_t);
void	sysFree(void*);
void *	sysCalloc(size_t, size_t);
#ifdef PAGED_HEAPS
void *  sysAllocBlock(size_t, void**);
void    sysFreeBlock(void *);
#endif /* PAGED_HEAPS */

/*
 * System API for dynamic linking libraries into the interpreter
 */
char *  sysInitializeLinker(void);
int     sysAddDLSegment(char *);
void    sysSaveLDPath(char *);
long    sysDynamicLink(char *);
void	sysBuildLibName(char *, int, char *, char *);
int     sysBuildFunName(char *, int, void *, int);
long *  sysInvokeNative(void *, void *, long *, char *, int, void *);

/*
 * System API for invoking the interpreter from native applications
 */
void    sysGetDefaultJavaVMInitArgs(void *);
int     sysInitializeJavaVM(void *, void *);
int     sysFinalizeJavaVM(void *);
void    sysAttachThreadLock();
void    sysAttachThreadUnlock();

/*
 * System API for threads
 */
typedef struct  sys_thread sys_thread_t;
typedef struct  sys_mon sys_mon_t;
typedef void *  stackp_t;

int 	sysThreadBootstrap(sys_thread_t **, void *);
void 	sysThreadInitializeSystemThreads(void);
int 	sysThreadCreate(long, uint_t flags, void *(*)(void *),
			sys_thread_t **, void *);
void	sysThreadExit(void);
sys_thread_t * sysThreadSelf(void);
void    sysThreadYield(void);
int     sysThreadVMSuspend(sys_thread_t *, sys_thread_t *);
void    sysThreadVMSuspendMe(void);
int     sysThreadVMUnsuspend(sys_thread_t *);
int	sysThreadSuspend(sys_thread_t *);
int	sysThreadResume(sys_thread_t *);
int	sysThreadSetPriority(sys_thread_t *, int);
int	sysThreadGetPriority(sys_thread_t *, int *);
void *  sysThreadStackPointer(sys_thread_t *); 
stackp_t sysThreadStackBase(sys_thread_t *); 
void    sysThreadSetStackBase(sys_thread_t *, stackp_t); 
int	sysThreadSingle(void);
void	sysThreadMulti(void);
int     sysThreadEnumerateOver(int (*)(sys_thread_t *, void *), void *);
void	sysThreadInit(sys_thread_t *, stackp_t);
void *  sysThreadGetBackPtr(sys_thread_t *);
int     sysThreadCheckStack(void);
int     sysInterruptsPending(void);
void	sysThreadPostException(sys_thread_t *, void *);
void	sysThreadDumpInfo(sys_thread_t *);
void    sysThreadInterrupt(sys_thread_t *);
int     sysThreadIsInterrupted(sys_thread_t *, long);
int     sysThreadAlloc(sys_thread_t **, stackp_t, void *);
int     sysThreadFree(sys_thread_t *);

/*
 * System API for monitors
 */
int     sysMonitorSizeof(void);
int     sysMonitorInit(sys_mon_t *);
int     sysMonitorDestroy(sys_mon_t *);
int     sysMonitorEnter(sys_mon_t *);
bool_t  sysMonitorEntered(sys_mon_t *);
int     sysMonitorExit(sys_mon_t *);
int     sysMonitorNotify(sys_mon_t *);
int     sysMonitorNotifyAll(sys_mon_t *);
int 	sysMonitorWait(sys_mon_t *, int, bool_t);
void    sysMonitorDumpInfo(sys_mon_t *);
bool_t  sysMonitorInUse(sys_mon_t *);

#define SYS_OK	        0
#define SYS_ERR	       -1
#define SYS_INTRPT     -2    /* Operation was interrupted */
#define SYS_TIMEOUT    -3    /* A timer ran out */
#define SYS_NOMEM      -5    /* Ran out of memory */
#define SYS_NORESOURCE -6    /* Ran out of some system resource */

/*
 * Symbolic constant to be used when waiting indefinitly on a condition
 * variable
 */
#define SYS_TIMEOUT_INFINITY -1

/*
 * System API for raw memory allocation
 */
void *  sysMapMem(long, long *);
void *  sysUnmapMem(void *, long, long *);
void *  sysCommitMem(void *, long, long *);
void *  sysDecommitMem(void *, long, long *);
void *  sysUncommitMem(void *, long, long *);

/*
 * System API for termination
 */
void	sysExit(int);
int	sysAtexit(void (*func)(void));
void	sysAbort(void);

/*
 * System API for files
 */
extern int sysIsAbsolute(const char* path);
extern int sysAccess(const char* pFile, int perm);

extern int sysStat(const char *path, struct stat *sbuf);
extern int sysFStat(int fd, struct stat * sbuf);

extern int sysOpen(const char *name, int openMode, int filePerm);
extern int sysClose(int fd);

extern long sysSeek(int fd, long offset, int whence);
extern int sysAvailable(int fd, long* bytes);
extern size_t sysRead(int fd, void *buf, unsigned int nBytes);
extern size_t sysWrite(int fd, const void *buf, unsigned int nBytes);

extern int sysRename(const char* srcName, const char* dstName);
extern int sysUnlink(const char* file);

extern int sysMkdir(const char* path, int mode);
extern int sysRmdir(const char* path);
extern int sysCanonicalPath(char *path, char *result, int result_len);

#include "io_md.h"
extern DIR* sysOpenDir(const char* path);
extern int sysCloseDir(DIR* dp);
extern struct dirent* sysReadDir(DIR* dp);

/*
 * API support needed for various multiprocessor related syncronization
 * primitives.  Systems that don't use real threads can just define
 * these to be 0 in their sysmacros_md.h.
 */

int sysIsMP();
void sysMemoryFlush();
void sysStoreBarrier();

/*
 * Include platform specific macros to override these
 */
#include "sysmacros_md.h"
 
#endif /* !_SYS_API_H_ */

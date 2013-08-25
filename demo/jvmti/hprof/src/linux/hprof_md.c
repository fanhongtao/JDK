/*
 * @(#)hprof_md.c	1.29 09/04/03
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
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
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

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#ifndef LINUX
#include <procfs.h>
#endif

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/errno.h>
#include <unistd.h>
#include <errno.h>
#include <dlfcn.h>
#include <sys/time.h>

#include <netdb.h>
#include <netinet/in.h>
#include <sys/param.h>
#include <time.h>

#include "jni.h"
#include "hprof.h"

#define PATH_SEPARATOR          ":"
#define PATH_SEPARATOR_CHAR     ':'

int
md_getpid(void)
{
    static int pid = -1;
    
    if ( pid >= 0 ) {
	return pid;
    }
    pid = getpid();
    return pid;
}

void 
md_sleep(unsigned seconds)
{
    sleep(seconds);
}

void
md_init(void)
{
#ifdef LINUX
    /* No Hi-Res timer option? */
#else
    if ( gdata->micro_state_accounting ) {
	char proc_ctl_fn[48];
	int  procfd;

	/* Turn on micro state accounting, once per process */
	(void)md_snprintf(proc_ctl_fn, sizeof(proc_ctl_fn),
		"/proc/%d/ctl", md_getpid());
     
	procfd = open(proc_ctl_fn, O_WRONLY);
	if (procfd >= 0) {
	    long ctl_op[2];
	    
	    ctl_op[0] = PCSET;
	    ctl_op[1] = PR_MSACCT;
	    (void)write(procfd, ctl_op, sizeof(ctl_op));
	    (void)close(procfd);
	}
    }
#endif
}

int
md_connect(char *hostname, unsigned short port)
{
    struct hostent *hentry;
    struct sockaddr_in s;
    int fd;

    /* create a socket */
    fd = socket(AF_INET, SOCK_STREAM, 0);

    /* find remote host's addr from name */
    if ((hentry = gethostbyname(hostname)) == NULL) {
	return -1;
    }
    (void)memset((char *)&s, 0, sizeof(s));
    /* set remote host's addr; its already in network byte order */
    (void)memcpy(&s.sin_addr.s_addr, *(hentry->h_addr_list),
	   (int)sizeof(s.sin_addr.s_addr));
    /* set remote host's port */
    s.sin_port = htons(port);
    s.sin_family = AF_INET;

    /* now try connecting */
    if (-1 == connect(fd, (struct sockaddr*)&s, sizeof(s))) {
	return 0;
    }
    return fd;
}

int
md_recv(int f, char *buf, int len, int option)
{
    return recv(f, buf, len, option);
}

int
md_shutdown(int filedes, int option)
{
    return shutdown(filedes, option);
}

int
md_open(const char *filename)
{
    return open(filename, O_RDONLY);
}

int
md_open_binary(const char *filename)
{
    return md_open(filename);
}

int
md_creat(const char *filename)
{
    return open(filename, O_WRONLY | O_CREAT | O_TRUNC,
	    S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH);
}

int
md_creat_binary(const char *filename)
{
    return md_creat(filename);
}

jlong
md_seek(int filedes, jlong cur)
{
    jlong new_pos;

    if ( cur == (jlong)-1 ) {
        new_pos = lseek(filedes, 0, SEEK_END);
    } else {
        new_pos = lseek(filedes, cur, SEEK_SET);
    }
    return new_pos;
}

void
md_close(int filedes)
{
    (void)close(filedes);
}

int 
md_send(int s, const char *msg, int len, int flags)
{
    int res;
    
    do {
        res = send(s, msg, len, flags);
    } while ((res < 0) && (errno == EINTR));
    
    return res;
}

int 
md_write(int filedes, const void *buf, int nbyte)
{
    int res;
    
    do {
        res = write(filedes, buf, nbyte);
    } while ((res < 0) && (errno == EINTR));

    return res;
}

int
md_read(int filedes, void *buf, int nbyte)
{
    int res;
    
    do {
        res = read(filedes, buf, nbyte);
    } while ((res < 0) && (errno == EINTR));

    return res;
}

/* Time of day in milli-seconds */
static jlong
md_timeofday(void)
{
    struct timeval tv;
 
    if ( gettimeofday(&tv, (void *)0) != 0 ) {
	return (jlong)0; /* EOVERFLOW ? */
    }
    /*LINTED*/
    return ((jlong)tv.tv_sec * (jlong)1000) + (jlong)(tv.tv_usec / 1000);
}

/* Hi-res timer in micro-seconds */
jlong 
md_get_microsecs(void)
{
#ifdef LINUX
    return (jlong)(md_timeofday() * (jlong)1000); /* Milli to micro */
#else
    return (jlong)(gethrtime()/(hrtime_t)1000); /* Nano seconds to micro seconds */
#endif
}

/* Time of day in milli-seconds */
jlong 
md_get_timemillis(void)
{
    return md_timeofday();
}

/* Current CPU hi-res CPU time used */
jlong
md_get_thread_cpu_timemillis(void)
{
#ifdef LINUX
    return md_timeofday();
#else
    return (jlong)(gethrvtime()/1000); /* Nano seconds to milli seconds */
#endif
}

void 
md_get_prelude_path(char *path, int path_len, char *filename)
{
    void *addr;
    char libdir[FILENAME_MAX+1];
    Dl_info dlinfo;

    libdir[0] = 0;
#ifdef LINUX
    addr = (void*)&Agent_OnLoad;
#else
    /* Just using &Agent_OnLoad will get the first external symbol with
     *   this name in the first .so, which may not be libhprof.so.
     *   On Solaris we can actually ask for the address of our Agent_OnLoad.
     */
    addr = dlsym(RTLD_SELF, "Agent_OnLoad");
    /* Just in case the above didn't work (missing linker patch?). */
    if ( addr == NULL ) {
	addr = (void*)&Agent_OnLoad;
    }
#endif

    /* Use dladdr() to get the full path to libhprof.so, which we use to find
     *  the prelude file.
     */
    dlinfo.dli_fname = NULL;
    (void)dladdr(addr, &dlinfo);
    if ( dlinfo.dli_fname != NULL ) {
	char * lastSlash;

	/* Full path to library name, need to move up one directory to 'lib' */
	(void)strcpy(libdir, (char *)dlinfo.dli_fname);
	lastSlash = strrchr(libdir, '/');
	if ( lastSlash != NULL ) {
	    *lastSlash = '\0';
	}
	lastSlash = strrchr(libdir, '/');
	if ( lastSlash != NULL ) {
	    *lastSlash = '\0';
	}
    }
    (void)snprintf(path, path_len, "%s/%s", libdir, filename);
}


int     
md_vsnprintf(char *s, int n, const char *format, va_list ap)
{
    return vsnprintf(s, n, format, ap);
}

int     
md_snprintf(char *s, int n, const char *format, ...)
{
    int ret;
    va_list ap;

    va_start(ap, format);
    ret = md_vsnprintf(s, n, format, ap);
    va_end(ap);
    return ret;
}

void
md_system_error(char *buf, int len)
{
    char *p;
    
    buf[0] = 0;
    p = strerror(errno);
    if ( p != NULL ) {
	(void)strcpy(buf, p);
    }
}

unsigned
md_htons(unsigned short s)
{
    return htons(s);
}

unsigned
md_htonl(unsigned l)
{
    return htonl(l);
}

unsigned	
md_ntohs(unsigned short s)
{
    return ntohs(s);
}

unsigned
md_ntohl(unsigned l)
{
    return ntohl(l);
}

/*
 * splits a path, based on its separator, the number of
 * elements is returned back in n. 
 * It is the callers responsibility to:
 *   a> check the value of n, and n may be 0
 *   b> ignore any empty path elements
 *   c> free up the data.
 */
static char** split_path(const char* path, int* n) {
    char* inpath;
    char** opath;
    char* p;
    int count = 1;
    int i;
    
    *n = 0;
    if (path == NULL || strlen(path) == 0) {
        return NULL;
    }
    inpath = strdup(path);
    if (inpath == NULL) {
        return NULL;
    }
    p = strchr(inpath, PATH_SEPARATOR_CHAR);
    // get a count of elements to allocate memory
    while (p != NULL) {
        count++;
        p++;
        p = strchr(p, PATH_SEPARATOR_CHAR);
    }
    opath = (char**) calloc(count, sizeof(char*));
    if (opath == NULL) {
        return NULL;
    }

    // do the actual splitting
    p = inpath;
    for (i = 0 ; i < count ; i++) {
        size_t len = strcspn(p, PATH_SEPARATOR);
	// allocate the string and add terminator storage
        char* s  = (char*)malloc((len + 1)*sizeof(char));
        if (s == NULL) {
            return NULL;
	}
        strncpy(s, p, len);
	s[len] = '\0';
        opath[i] = s;
        p += len + 1;
    }
    free(inpath);
    *n = count;
    return opath;
}

/* Create the actual fill filename for a dynamic library.  */
void
md_build_library_name(char *holder, int holderlen, char *pname, char *fname)
{
    int n;
    int i;
    char** pelements;
    struct stat statbuf;
    const int pnamelen = pname ? strlen(pname) : 0;

    /* Quietly truncate on buffer overflow.  Should be an error. */
    if (pnamelen + (int)strlen(fname) + 10 > holderlen) {
        *holder = '\0';
        return;
    }

    /* Construct path to library */
    if (pnamelen == 0) {
        (void)snprintf(holder, holderlen, "lib%s.so", fname);
    } else if (strchr(pname, PATH_SEPARATOR_CHAR) != NULL) {
        pelements = split_path(pname, &n);
        for (i = 0 ; i < n ; i++) {
            // really shouldn't be NULL but what the heck, check can't hurt
	    if (pelements[i] == NULL || strlen(pelements[i]) == 0) {
	       continue; // skip the empty path values
	    }
	    snprintf(holder, holderlen, "%s/lib%s.so", pelements[i], fname);
	    if (stat(holder, &statbuf) == 0) {
	       break;
	    }
        }
        // release the storage
        for (i = 0 ; i < n ; i++) {
            if (pelements[i] != NULL) {
               free(pelements[i]);
            }
        }
        if (pelements != NULL) {
            free(pelements);
        }
    } else {
        (void)snprintf(holder, holderlen, "%s/lib%s.so", pname, fname);
    }
}

/* Load this library (return NULL on error, and error message in err_buf) */
void *
md_load_library(const char *name, char *err_buf, int err_buflen)
{
    void * result;
    
    result = dlopen(name, RTLD_LAZY);
    if (result == NULL) {
	(void)strncpy(err_buf, dlerror(), err_buflen-2);
	err_buf[err_buflen-1] = '\0';
    }
    return result;
}

/* Unload this library */
void 
md_unload_library(void *handle)
{
    (void)dlclose(handle);
}

/* Find an entry point inside this library (return NULL if not found) */
void * 
md_find_library_entry(void *handle, const char *name)
{
    void * sym;
    
    sym =  dlsym(handle, name);
    return sym;
}


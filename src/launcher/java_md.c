/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

#include "java.h"
#include <dlfcn.h>
#include <string.h>
#include <stdlib.h>
#include <limits.h>
#include <sys/stat.h>
#include <unistd.h>

#ifdef DEBUG
#define JVM_DLL "libjvm_g.so"
#define JAVA_DLL "libjava_g.so"
#else
#define JVM_DLL "libjvm.so"
#define JAVA_DLL "libjava.so"
#endif

#ifndef ARCH
#include <sys/systeminfo.h>
#endif
static const char *
GetArch()
{
    static char *arch = NULL;
    static char buf[12];
    if (arch) {
	return arch;
    }

#ifdef ARCH
    strcpy(buf, ARCH);
#else
    sysinfo(SI_ARCHITECTURE, buf, sizeof(buf));
#endif
    arch = buf;
    return arch;
}

/*
 * On Solaris VM choosing is done by .java_wrapper.  The .exe is also
 * linked against libjvm.so, so we don't have to load any library, it
 * has already been loaded.
 */
jboolean
GetJVMPath(const char *jrepath, const char *jvmtype,
	   char *jvmpath, jint jvmpathsize)
{
    struct stat s;
    
    sprintf(jvmpath, "%s/lib/%s/%s/" JVM_DLL, jrepath, GetArch(), jvmtype);
    if (debug) printf("Does `%s' exist ... ", jvmpath);

    if (stat(jvmpath, &s) == 0) {
	if (debug) printf("yes.\n");
	return JNI_TRUE;
    } else {
	if (debug) printf("no.\n");
	return JNI_FALSE;
    }
}

/*
 * If the target VM is a symbolic link to another valid VM, return a pointer
 * to the name of that VM.  If the target VM is a link to something else
 * (not to be documented?) return an empty string.  Otherwise return NULL.
 */

const char *
ReadJVMLink(const char *jrepath, const char *jvmtype,
	    char* knownVMs[], int knownVMsCount) {
    char jvmpath[MAXPATHLEN];
    char link[MAXPATHLEN];
    int i;

    sprintf(jvmpath, "%s/lib/%s/%s", jrepath, GetArch(), jvmtype);
    if (debug) printf("Is `%s' a symbolic link ... ", jvmpath);
    if (readlink(jvmpath, link, sizeof link) == -1)
	return NULL;				/* Not a link. */

    for (i = 0; i < knownVMsCount; ++i) {
	if (strcmp(link, knownVMs[i]+1))
	    return knownVMs[i] + 1;		/* Return the link. */
    }
    return "";					/* Don't know, don't document.*/
}


/*
 * Find path to JRE based on .exe's location or registry settings.
 */
jboolean
GetJREPath(char *path, jint pathsize)
{
    char libjava[MAXPATHLEN];

    if (GetApplicationHome(path, pathsize)) {
	/* Is JRE co-located with the application? */
	sprintf(libjava, "%s/lib/%s/" JAVA_DLL, path, GetArch());
	if (access(libjava, F_OK) == 0) {
	    goto found;
	}

	/* Does the app ship a private JRE in <apphome>/jre directory? */
	sprintf(libjava, "%s/jre/lib/%s/" JAVA_DLL, path, GetArch());
	if (access(libjava, F_OK) == 0) {
	    strcat(path, "/jre");
	    goto found;
	}
    }

    return JNI_FALSE;

 found:
    if (debug) printf("JRE path is %s\n", path);
    return JNI_TRUE;
}

jboolean
LoadJavaVM(const char *jvmpath, InvocationFunctions *ifn)
{
    Dl_info dlinfo;
    void *libjvm;

    if (debug) {
	printf("JVM path is %s\n", jvmpath);
    }

    libjvm = dlopen(jvmpath, RTLD_NOW + RTLD_GLOBAL);
    if (libjvm == NULL)
	goto error;

    ifn->CreateJavaVM = (CreateJavaVM_t)
	dlsym(libjvm, "JNI_CreateJavaVM");
    if (ifn->CreateJavaVM == NULL)
	goto error;

    ifn->GetDefaultJavaVMInitArgs = (GetDefaultJavaVMInitArgs_t)
	dlsym(libjvm, "JNI_GetDefaultJavaVMInitArgs");
    if (ifn->GetDefaultJavaVMInitArgs == NULL)
	goto error;

    return JNI_TRUE;

error:
    fprintf(stderr, "Error: failed %s, because %s\n", jvmpath, dlerror());
    return JNI_FALSE;
}

/*
 * Get the path to the file that has the usage message for -X options.
 */
void
GetXUsagePath(char *buf, jint bufsize)
{
    Dl_info dlinfo;
   
    /* we use RTLD_NOW because of problems with ld.so.1 and green threads */
    dladdr(dlsym(dlopen(JVM_DLL, RTLD_NOW), "JNI_CreateJavaVM"), &dlinfo);
#ifdef __linux__
    strncpy(buf, (char *)dlinfo.dli_fname, bufsize - 2);
#else
    strncpy(buf, (char *)dlinfo.dli_fname, bufsize - 1);
#endif

    buf[bufsize-1] = '\0';
    *(strrchr(buf, '/')) = '\0';
    strcat(buf, "/Xusage.txt");
}

/*
 * If app is "/foo/bin/sparc/green_threads/javac", then put "/foo" into buf.
 */
jboolean
GetApplicationHome(char *buf, jint bufsize)
{
#ifdef USE_APPHOME
    char *apphome = getenv("APPHOME");
    if (apphome) {
	strncpy(buf, apphome, bufsize-1);
	buf[bufsize-1] = '\0';
	return JNI_TRUE;
    } else {
	return JNI_FALSE;
    }
#else
    Dl_info dlinfo;

    dladdr((void *)GetApplicationHome, &dlinfo);
    strncpy(buf, dlinfo.dli_fname, bufsize - 1);
    buf[bufsize-1] = '\0';
    
    *(strrchr(buf, '/')) = '\0';  /* executable file      */
    *(strrchr(buf, '/')) = '\0';  /* green|native_threads */
    *(strrchr(buf, '/')) = '\0';  /* sparc|i386           */
    *(strrchr(buf, '/')) = '\0';  /* bin                  */

    {
	char real[PATH_MAX];
	if (realpath(buf, real) == NULL) {
	    fprintf(stderr, "Error: realpath(`%s') failed.\n", buf);
	    return JNI_FALSE;
	}
	strcpy(buf, real);
    }
    
    return JNI_TRUE;
#endif
}


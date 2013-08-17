/*
 * @(#)jre_main.c	1.17 99/01/22
 *
 * Copyright 1997-1999 by Sun Microsystems, Inc.,
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
 * Main program for invoking application with JRE (Java Runtime Environment)
 */

#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <jni.h>
#include "jre.h"

/* Name of this program */
#define PROGRAM "jre"

/* Title of this program */
#ifdef VERSION
#define TITLE "Java(tm) Runtime Loader Version " VERSION
#else
#define TITLE "Java(tm) Runtime Loader"
#endif

/* Check for null value and return */
#define NULL_CHECK(e) if ((e) == 0) return 0

/* Enable debugging output */
#ifdef JRE_DEBUG
jboolean debug;
#endif

heapoptions heapoptsJre;

/* Forward declarations */
jint ParseOptions(int *argcp, char ***argvp, JDK1_1InitArgs *vmargs);
void AddProperty(char *def);
void DeleteProperty(const char *name);
void PrintUsage(void);

jarray NewStringArray(JNIEnv *env, char **cpp, int count);
jstring NewPlatformString(JNIEnv *env, char *s); /* 4097707 */
long atoml(char *s);

/* Globals */
static char **props;		/* User-defined properties */
static int numProps, maxProps;	/* Current, max number of properties */

/*
 * Main program to invoke Java runtime using JNI invocation API. Supports
 * setting of VM arguments through standard command line options.
 */
void main(int argc, char *argv[])
{
    JRESettings set;
    void *handle;
    JDK1_1InitArgs vmargs;
    JavaVM *jvm;
    JNIEnv *env;
    char *s, *name;
    jclass cls;
    jmethodID mid;
    jarray args;
    int i;

    /* First scan arguments for help and debug options */
    for (i = 1; i < argc; i++) {
	char *arg = argv[i];
	if (*arg++ != '-') {
	    break;
	}
	if (strcmp(arg, "?") == 0 || strcmp(arg, "h") == 0 ||
	    strcmp(arg, "help") == 0) {
	    PrintUsage();
	    exit(1);
	}
#ifdef JRE_DEBUG
	if (strcmp(arg, "d") == 0) {
	    debug = JNI_TRUE;
	}
#endif
    }

    /* Get runtime settings */
#ifdef VERSION
    if (JRE_GetSettings(&set, VERSION) != 0) {
#else
    if (JRE_GetCurrentSettings(&set) != 0) {
#endif
	if (JRE_GetDefaultSettings(&set) != 0) {
	    fprintf(stderr, "Could not locate Java runtime\n");
	    exit(1);
	}
    }

#ifdef JRE_DEBUG
    if (debug) {
	char *ver = JRE_MakeVersion(set.majorVersion, set.minorVersion,
				    set.microVersion);
	fprintf(stderr, "Runtime Settings:\n");
	fprintf(stderr, " javaHome   = %s\n",
		set.javaHome != 0 ? set.javaHome : "<not set>");
	fprintf(stderr, " runtimeLib = %s\n",
		set.runtimeLib != 0 ? set.runtimeLib : "<not set>");
	fprintf(stderr, " version    = %s\n", ver != 0 ? ver : "<not set>");
	fprintf(stderr, " compiler   = %s\n\n",
		set.compiler != 0 ? set.compiler : "<not set>");
    }
#endif

    /* Load runtime library */
    handle = JRE_LoadLibrary(set.runtimeLib);
    if (handle == 0) {
	fprintf(stderr, "Could not load runtime library: %s\n",
		set.runtimeLib);
	exit(1);
    }

    /* Add pre-defined system properties */
    if (set.javaHome != 0) {
	char *def = JRE_Malloc(strlen(set.javaHome) + 16);
	sprintf(def, "java.home=%s", set.javaHome);
	AddProperty(def);
    }
    if (set.compiler != 0) {
	char *def = JRE_Malloc(strlen(set.compiler) + 16);
	sprintf(def, "java.compiler=%s", set.compiler);
	AddProperty(def);
    }

    /*
     * The following is used to specify that we require at least
     * JNI version 1.1. Currently, this field is not checked but
     * will be starting with JDK/JRE 1.2. The value returned after
     * calling JNI_GetDefaultJavaVMInitArgs() is the actual JNI version
     * supported, and is always higher that the requested version.
     */
    vmargs.version = 0x00010001;

    if (JRE_GetDefaultJavaVMInitArgs(handle, &vmargs) != 0) {
	fprintf(stderr, "Could not initialize Java VM\n");
	exit(1);
    }
    vmargs.classpath = set.classPath;

    JRE_InitHeapOptions(handle, &heapoptsJre);

    /* Parse command line options */
    --argc; argv++;
    if (ParseOptions(&argc, &argv, &vmargs) != 0) {
	PrintUsage();
	exit(1);
    }

    JRE_SetHeapOptions(handle, &heapoptsJre);

    /* Get name of class */
    if (*argv == 0) {
	PrintUsage();
	exit(1);
    }
    name = strdup(*argv++);
    for (s = name; *s != '\0'; s++) {
	if (*s == '.') *s = '/';
    }
    --argc;

#ifdef JRE_DEBUG
    if (debug) {
	fprintf(stderr, "CLASSPATH is %s\n\n", vmargs.classpath);
    }
#endif

    /* Set user-defined system properties for Java VM */
    if (props != 0) {
	if (numProps == maxProps) {
	    char **tmp = JRE_Malloc((numProps + 1) * sizeof(char **));
	    memcpy(tmp, props, numProps * sizeof(char **));
	    free(props);
	    props = tmp;
	}
	props[numProps] = 0;
	vmargs.properties = props;
    }

    /* Load and initialize Java VM */
    if (JRE_CreateJavaVM(handle, &jvm, &env, &vmargs) != 0) {
	fprintf(stderr, "Could not create Java VM\n");
	exit(1);
    }

    /* Free properties */
    if (props != 0) {
	free(props);
    }

    /* Find class */
    cls = (*env)->FindClass(env, name);
    if (cls == 0) {
	fprintf(stderr, "Class not found: %s\n", *--argv);
	exit(1);
    }

    /* Find main method of class */
    mid = (*env)->GetStaticMethodID(env, cls, "main",
    				    "([Ljava/lang/String;)V");
    if (mid == 0) {
	fprintf(stderr, "In class %s: public static void main(String args[])"
			" is not defined\n");
	exit(1);
    }

    /* Invoke main method */
    args = NewStringArray(env, argv, argc);

    if (args == 0) {
	JRE_FatalError(env, "Couldn't build argument list for main\n");
    }
    (*env)->CallStaticVoidMethod(env, cls, mid, args);
    if ((*env)->ExceptionOccurred(env)) {
	(*env)->ExceptionDescribe(env);
    }

    /* Wait until we are the only user thread remaining then unload VM */
    (*jvm)->DestroyJavaVM(jvm);

    /* Unload the runtime */
    JRE_UnloadLibrary(handle);
}

/*
 * Parses command line VM options. Returns 0 if successful otherwise
 * returns -1 if an invalid option was encountered.
 */
jint ParseOptions(int *argcp, char ***argvp, JDK1_1InitArgs *vmargs)
{
    char *arg, **argv = *argvp;

    while ((arg = *argv++) != 0 && *arg++ == '-') {
	if (strcmp(arg, "classpath") == 0) {
	    if (*argv == 0) {
		fprintf(stderr, "No class path given for %s option\n", arg);
		return -1;
	    }
	    vmargs->classpath = *argv++;
	} else if (strcmp(arg, "cp") == 0) {
	    char *cp = vmargs->classpath;
	    if (*argv == 0) {
		fprintf(stderr, "No class path given for %s option\n", arg);
		return -1;
	    }
	    vmargs->classpath = malloc(strlen(*argv) + strlen(cp) + 2);
	    if (vmargs->classpath == 0) {
		perror("malloc");
		exit(1);
	    }
	    sprintf(vmargs->classpath, "%s%c%s", *argv++, PATH_SEPARATOR, cp);
	} else if (strncmp(arg, "D", 1) == 0) {
	    AddProperty(arg + 1);
	} else if (strncmp(arg, "ss", 2) == 0) {
	    jint n = atoml(arg + 2);
	    if (n >= 1000) {
		vmargs->nativeStackSize = n;
	    }
	} else if (strncmp(arg, "oss", 3) == 0) {
	    jint n = atoml(arg + 3);
	    if (n >= 1000) {
		vmargs->javaStackSize = n;
	    }
	} else if (strncmp(arg, "ms", 2) == 0) {
	    jint n = atoml(arg + 2);
	    if (n >= 1000) {
		vmargs->minHeapSize = n;
	    }
	} else if (strncmp(arg, "mx", 2) == 0) {
	    jint n = atoml(arg + 2);
	    if (n >= 1000) {
		vmargs->maxHeapSize = n;
	    }
	} else if (strncmp(arg, "maxf", 4) == 0) {
	    float tmpF = atof(arg + 4);
	    if (tmpF >= (float)0 && tmpF <= (float)1) {
		heapoptsJre.maxHeapFreePercent = tmpF;
	    }
	} else if (strncmp(arg, "minf", 4) == 0) {
	    float tmpF = atof(arg + 4);
	    if (tmpF >= (float)0 && tmpF <= (float)1) {
		heapoptsJre.minHeapFreePercent = tmpF;
	    }
	} else if (strncmp(arg, "maxe", 4) == 0) {
	    jint n = atoml(arg + 4);
	    if (n >= 0) {
		heapoptsJre.maxHeapExpansion = n;
	    }
	} else if (strncmp(arg, "mine", 4) == 0) {
	    jint n = atoml(arg + 4);
	    if (n >= 0) {
		heapoptsJre.minHeapExpansion = n;
	    }
	} else if (strcmp(arg, "noasyncgc") == 0) {
	    vmargs->disableAsyncGC = JNI_TRUE;
	} else if (strcmp(arg, "noclassgc") == 0) {
	    vmargs->enableClassGC = JNI_FALSE;
	} else if (strcmp(arg, "verify") == 0) {
	    vmargs->verifyMode = 2;
	} else if (strcmp(arg, "verifyremote") == 0) {
	    vmargs->verifyMode = 1;
	} else if (strcmp(arg, "noverify") == 0) {
	    vmargs->verifyMode = 0;
	} else if (strcmp(arg, "nojit") == 0) {
	    /**
	     * Set the value of java.compiler equal to the empty 
	     * string.  At the jit library loading step nothing will
	     * loaded.
	     */
	    AddProperty("java.compiler=");
	} else if (strcmp(arg, "v") == 0 || strcmp(arg, "verbose") == 0) {
	    vmargs->verbose = JNI_TRUE;
#ifdef JRE_DEBUG
	} else if (strcmp(arg, "d") == 0) {
	    debug = JNI_TRUE;
#endif
	} else if (strcmp(arg, "verbosegc") == 0) {
	    vmargs->enableVerboseGC = JNI_TRUE;
	} else if (strcmp(arg, "?") == 0 || strcmp(arg, "h") == 0 ||
		   strcmp(arg, "help") == 0) {
	    return -1;
	} else {
	    fprintf(stderr, "Illegal option: -%s\n", arg);
	    return -1;
	}
    }
    *argcp -= --argv - *argvp;
    *argvp = argv;
    return 0;
}

/*
 * Adds a user-defined system property definition.
 */
void AddProperty(char *def)
{
    if (numProps >= maxProps) {
	if (props == 0) {
	    maxProps = 4;
	    props = JRE_Malloc(maxProps * sizeof(char **));
	} else {
	    char **tmp;
	    maxProps *= 2;
	    tmp = JRE_Malloc(maxProps * sizeof(char **));
	    memcpy(tmp, props, numProps * sizeof(char **));
	    free(props);
	    props = tmp;
	}
    }
    props[numProps++] = def;
}

/*
 * Deletes a property definition by name.
 */
void DeleteProperty(const char *name)
{
    int i;

    for (i = 0; i < numProps; ) {
	char *def = props[i];
	char *c = strchr(def, '=');
	int n;
	if (c != 0) {
	    n = c - def;
	} else {
	    n = strlen(def);
	}
	if (strncmp(name, def, n) == 0) {
	    if (i < --numProps) {
		memmove(&props[i], &props[i+1], (numProps-i) * sizeof(char **));
	    }
	} else {
	    i++;
	}
    }
}
/*
 * Returns a new Java string object for the specified platform string.
 * Added as part of fix for 4097707.
 */
jstring
NewPlatformString(JNIEnv *env, char *s)
{
    int len = strlen(s);
    jclass cls;
    jmethodID mid;
    jbyteArray ary;

    NULL_CHECK(cls = (*env)->FindClass(env, "java/lang/String"));
    NULL_CHECK(mid = (*env)->GetMethodID(env, cls, "<init>", "([B)V"));
    ary = (*env)->NewByteArray(env, len);
    if (ary != 0) {
	jstring str = 0;
	(*env)->SetByteArrayRegion(env, ary, 0, len, (jbyte *)s);
	if (!(*env)->ExceptionOccurred(env)) {
	    str = (*env)->NewObject(env, cls, mid, ary);
	}
	(*env)->DeleteLocalRef(env, ary);
	return str;
    }
    return 0;
}

/*
 * Creates an array of Java string objects from the specified array of C
 * strings. Returns 0 if the array could not be created.
 */
jarray NewStringArray(JNIEnv *env, char **cpp, int count)
{
    jclass cls;
    jarray ary;
    int i;

    NULL_CHECK(cls = (*env)->FindClass(env, "java/lang/String"));
    NULL_CHECK(ary = (*env)->NewObjectArray(env, count, cls, 0));
    for (i = 0; i < count; i++) {
       jstring str = NewPlatformString(env, *cpp++); /*4097707*/
       NULL_CHECK(str); 
       (*env)->SetObjectArrayElement(env, ary, i, str);
       (*env)->DeleteLocalRef(env, str); 
    }
    return ary;
}

/*
 * Parses a memory size specification from the specified C string.
 */
long atoml(char *s)
{
    long n = strtol(s, &s, 10);
    switch (*s++) {
    case 'M': case 'm':
	n *= 1024 * 1024;
	break;
    case 'K': case 'k':
	n *= 1024;
	break;
    case '\0':
	return n;
    default:
	return -1;
    }
    return *s == '\0' ? n : -1;
}

/*
 * Prints help message.
 */
void PrintUsage()
{
    fprintf(stderr, TITLE "\n"
    "Usage: " PROGRAM " [-options] classname [arguments]\n"
    "Options:\n"
    "    -?, -help         print out this message\n"
    "    -v, -verbose      turn on verbose mode\n"
    "    -verbosegc        print a message when garbage collection occurs\n"
    "    -noasyncgc        disable asynchronous garbage collection\n"
    "    -noclassgc        disable class garbage collection\n"
    "    -ss<number>       set the maximum native stack size for any thread\n"
    "    -oss<number>      set the maximum Java stack size for any thread\n"
    "    -ms<number>       set the initial Java heap size\n"
    "    -mx<number>       set the maximum Java heap size\n"
    "    -D<name>=<value>  set a system property\n"
    "    -classpath <path> set class path to <path>\n"
    "    -cp <path>        prepend <path> to base class path\n"
    "    -verify           verify all classes when loaded\n"
    "    -verifyremote     verify classes loaded from the network (default)\n"
    "    -noverify         do not verify any classes\n"
    "    -nojit            disable JIT compiler\n");
}

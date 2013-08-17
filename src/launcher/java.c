/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Shared source for 'java' command line tool.
 *
 * If JAVA_ARGS is defined, then acts as a launcher for applications. For
 * instance, the JDK command line tools such as javac and javadoc (see
 * makefiles for more details) are built with this program.  Any arguments
 * prefixed with '-J' will be passed directly to the 'java' command.
 *
 * If OLDJAVA is defined then enables old-style launcher behavior. In the
 * old launcher, both application and system classes are loaded from the
 * system class path.  In the new launcher, there is a separate class path
 * and class loader for loading application classes.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <jni.h>
#include "java.h"

#ifndef FULL_VERSION
#define FULL_VERSION "1.2"
#endif

static jboolean printVersion = JNI_FALSE; /* print and exit */
static jboolean showVersion = JNI_FALSE;  /* print but continue */
static char *progname;
jboolean debug = JNI_FALSE;
int      status = 0;

/*
 * List of VM options to be specified when the VM is created.
 */
static JavaVMOption *options;
static int numOptions, maxOptions;

/*
 * Prototypes for functions internal to launcher.
 */
static void AddOption(char *str, void *info);
static void SetClassPath(char *s);
static jboolean ParseArguments(int *pargc, char ***pargv, char **pjarfile,
			       char **pclassname, int *pret);
static jboolean InitializeJVM(JavaVM **pvm, JNIEnv **penv,
			      InvocationFunctions *ifn);
static void* MemAlloc(size_t size);
static jstring NewPlatformString(JNIEnv *env, char *s);
static jobjectArray NewPlatformStringArray(JNIEnv *env, char **strv, int strc);
static jstring NewPlatformString(JNIEnv *env, char *s);
static jclass LoadClass(JNIEnv *env, char *name);
static jstring GetMainClassName(JNIEnv *env, char *jarname);

#ifdef JAVA_ARGS
static void TranslateDashJArgs(int *pargc, char ***pargv);
static jboolean AddApplicationOptions(void);
#endif

static void PrintJavaVersion(JNIEnv *env);
static void PrintUsage(void);
static jint PrintXUsage(void);

/* Support for options such as -hotspot, -classic etc. */
#define MAX_KNOWN_VMS 10
static char *knownVMs[MAX_KNOWN_VMS];
static int knownVMsCount;
static jint ReadKnownVMs(const char *jrepath);
static void FreeKnownVMs();

/*
 * Entry point.
 */
int
main(int argc, char **argv)
{
    JavaVM *vm = 0;
    JNIEnv *env = 0;
    char *jarfile = 0;
    char *classname = 0;
    char *s = 0;
    jclass mainClass;
    jmethodID mainID;
    jobjectArray mainArgs;
    int ret;
    InvocationFunctions ifn;
    const char *jvmtype = 0;
    jboolean jvmspecified = JNI_FALSE;     /* Assume no option specified. */
    char jrepath[MAXPATHLEN], jvmpath[MAXPATHLEN];
    jlong start, end;
    int i;

    if (getenv("_JAVA_LAUNCHER_DEBUG") != 0) {
	debug = JNI_TRUE;
	printf("----_JAVA_LAUNCHER_DEBUG----\n");
    }

    /* Find out where the JRE is that we will be using. */
    if (!GetJREPath(jrepath, sizeof(jrepath))) {
	fprintf(stderr, "Error: could not find Java 2 Runtime Environment.\n");
	return 2;
    }

    knownVMsCount = ReadKnownVMs(jrepath);
    if (knownVMsCount < 1) { /* Error already printed. */
	return 3;
    }

    /* Did the user pass an explicit VM type? */
    if (argc > 1 && argv[1][0] == '-') {
	for (i = 0; i < knownVMsCount; i++) {
	    if (strcmp(argv[1], knownVMs[i]) == 0) {
		jvmtype = argv[1]+1; /* skip the - */
		jvmspecified = JNI_TRUE;
		break;
	    }
	}
    }
    if (jvmspecified) {
	jvmpath[0] = '\0';
	if (!GetJVMPath(jrepath, jvmtype, jvmpath, sizeof(jvmpath))) {
	    fprintf(stderr, "Error: no `%s' JVM at `%s'.\n", jvmtype, jvmpath);
	    return 4;
	}
    } else {
	/* Find an installed VM in the preferred order... */
	jboolean foundJVM = JNI_FALSE;
	for (i = 0; i < knownVMsCount; i++) {
	    jvmtype = knownVMs[i] + 1; /* skip the - */
	    if (GetJVMPath(jrepath, jvmtype, jvmpath, sizeof(jvmpath))) {
		foundJVM = JNI_TRUE;
		break;
	    }
	}
	if (!foundJVM) {
	    fprintf(stderr, "Error: could not find a JVM.\n");
	    return 5;
	}
    }

    /* If we got here, jvmpath has been correctly initialized. */
    ifn.CreateJavaVM = 0; ifn.GetDefaultJavaVMInitArgs = 0;
    if (!LoadJavaVM(jvmpath, &ifn)) {
        status = 1;
	return 6;
    }
    
#ifdef JAVA_ARGS  /* javac, jar and friends. */
    progname = "java";
#else             /* java, oldjava, javaw and friends */
#ifdef PROGNAME
    progname = PROGNAME;
#else
    progname = *argv;
    if ((s = strrchr(progname, FILE_SEPARATOR)) != 0) {
	progname = s + 1;
    }
#endif /* PROGNAME */
#endif /* JAVA_ARGS */
    ++argv;
    --argc;

    /* Skip over a specified -classic/-hotspot/-server option */
    if (jvmspecified) {
	argv++;
	argc--;
    }

#ifdef JAVA_ARGS
    /* Preprocess wrapper arguments */
    TranslateDashJArgs(&argc, &argv);
    if (!AddApplicationOptions()) {
        status = 2;
	return 1;
    }
#endif

    /* Set default CLASSPATH */
    if ((s = getenv("CLASSPATH")) == 0) {
	s = ".";
    }
#ifdef OLDJAVA
    /* Prepend system class path to default */
    {
	JDK1_1InitArgs args;
	char *buf;
	args.version = JNI_VERSION_1_1;
	if (ifn.GetDefaultJavaVMInitArgs(&args) != JNI_OK
                || args.classpath == 0) {
	    fprintf(stderr, "Could not get default system class path.\n");
            status = 2;
	    return 1;
	}
	buf = MemAlloc(strlen(args.classpath) + strlen(s) + 2);
	sprintf(buf, "%s%c%s", args.classpath, PATH_SEPARATOR, s);
	s = buf;
    }
#endif
#ifndef JAVA_ARGS
    SetClassPath(s);
#endif

    /* Parse command line options */
    if (!ParseArguments(&argc, &argv, &jarfile, &classname, &ret)) {
        status = 2;
	return ret;
    }

    /* Override class path if -jar flag was specified */
    if (jarfile != 0) {
	SetClassPath(jarfile);
    }

    /* Initialize the virtual machine */

    if (debug)
	start = CounterGet();
    if (!InitializeJVM(&vm, &env, &ifn)) {
	fprintf(stderr, "Could not create the Java virtual machine.\n");
        status = 3;
	return 1;
    }

    if (printVersion || showVersion) {
        PrintJavaVersion(env);
	if ((*env)->ExceptionOccurred(env)) {
	    (*env)->ExceptionDescribe(env);
	    goto leave;
	}
	if (printVersion) {
	    ret = 0;
	    goto leave;
	}
	if (showVersion) {
	    fprintf(stderr, "\n");
	}
    }

    /* If the user specified neither a class name or a JAR file */
    if (jarfile == 0 && classname == 0) {
	PrintUsage();
	goto leave;
    }

    FreeKnownVMs();  /* after last possible PrintUsage() */

    if (debug) {
	end   = CounterGet();
	printf("%ld micro seconds to InitializeJVM\n",
	       (jint)Counter2Micros(end-start));
    }

    /* At this stage, argc/argv have the applications' arguments */
    if (debug) {
	int i = 0;
	printf("Main-Class is '%s'\n", classname ? classname : "");
	printf("Apps' argc is %d\n", argc);
	for (; i < argc; i++) {
	    printf("    argv[%2d] = '%s'\n", i, argv[i]);
	}
    }

    ret = 1;

    /* Get the application's main class */
    if (jarfile != 0) {
	jstring mainClassName = GetMainClassName(env, jarfile);
	if ((*env)->ExceptionOccurred(env)) {
	    (*env)->ExceptionDescribe(env);
	    goto leave;
	}
	if (mainClassName == NULL) {
	    fprintf(stderr, "Failed to load Main-Class manifest attribute "
		    "from\n%s\n", jarfile);
	    goto leave;
	}
	classname = (char *)(*env)->GetStringUTFChars(env, mainClassName, 0);
	if (classname == NULL) {
	    (*env)->ExceptionDescribe(env);
	    goto leave;
	}
	mainClass = LoadClass(env, classname);
	(*env)->ReleaseStringUTFChars(env, mainClassName, classname);
    } else {
	mainClass = LoadClass(env, classname);
    }
    if (mainClass == NULL) {
        (*env)->ExceptionDescribe(env);
        status = 4;
	goto leave;
    }

    /* Get the application's main method */
    mainID = (*env)->GetStaticMethodID(env, mainClass, "main",
				       "([Ljava/lang/String;)V");
    if (mainID == NULL) {
	if ((*env)->ExceptionOccurred(env)) {
	    (*env)->ExceptionDescribe(env);
	} else {
	    fprintf(stderr, "No main method found in specified class.\n");
	}
        status = 5;
	goto leave;
    }

    /* Build argument array */
    mainArgs = NewPlatformStringArray(env, argv, argc);
    if (mainArgs == NULL) {
	(*env)->ExceptionDescribe(env);
	goto leave;
    }

    /* Invoke main method. */
    (*env)->CallStaticVoidMethod(env, mainClass, mainID, mainArgs);
    if ((*env)->ExceptionOccurred(env)) {
	/* Formerly, we used to call the "uncaughtException" method of the
	   main thread group, but this was later shown to be unnecessary
	   since the default definition merely printed out the same exception
	   stack trace as ExceptionDescribe and could never actually be
	   overridden by application programs. */
	(*env)->ExceptionDescribe(env);
	goto leave;
    }

    /*
     * Detach the current thread so that it appears to have exited when
     * the application's main method exits.
     */
    if ((*vm)->DetachCurrentThread(vm) != 0) {
	fprintf(stderr, "Could not detach main thread.\n");
	goto leave;
    }
    ret = 0;

leave:
    (*vm)->DestroyJavaVM(vm);
    return ret;
}

/*
 * Adds a new VM option with the given given name and value.
 */
static void
AddOption(char *str, void *info)
{
    /*
     * Expand options array if needed to accomodate at least one more
     * VM option.
     */
    if (numOptions >= maxOptions) {
	if (options == 0) {
	    maxOptions = 4;
	    options = MemAlloc(maxOptions * sizeof(JavaVMOption));
	} else {
	    JavaVMOption *tmp;
	    maxOptions *= 2;
	    tmp = MemAlloc(maxOptions * sizeof(JavaVMOption));
	    memcpy(tmp, options, numOptions * sizeof(JavaVMOption));
	    free(options);
	    options = tmp;
	}
    }
    options[numOptions].optionString = str;
    options[numOptions++].extraInfo = info;
}

static void
SetClassPath(char *s)
{
    char *def = MemAlloc(strlen(s) + 40);
#ifdef OLDJAVA
    sprintf(def, "-Xbootclasspath:%s", s);
#else
    sprintf(def, "-Djava.class.path=%s", s);
#endif
    AddOption(def, NULL);
}

/*
 * Parses command line arguments.
 */
static jboolean
ParseArguments(int *pargc, char ***pargv, char **pjarfile,
		       char **pclassname, int *pret)
{
    int argc = *pargc;
    char **argv = *pargv;
    jboolean jarflag = JNI_FALSE;
    char *arg;

    *pret = 1;
    while ((arg = *argv) != 0 && *arg == '-') {
	argv++; --argc;
	if (strcmp(arg, "-classpath") == 0 || strcmp(arg, "-cp") == 0) {
	    if (argc < 1) {
		fprintf(stderr, "%s requires class path specification\n", arg);
		PrintUsage();
		return JNI_FALSE;
	    }
	    SetClassPath(*argv);
	    argv++; --argc;
#ifndef OLDJAVA
	} else if (strcmp(arg, "-jar") == 0) {
	    jarflag = JNI_TRUE;
#endif
	} else if (strcmp(arg, "-help") == 0 ||
		   strcmp(arg, "-h") == 0 ||
		   strcmp(arg, "-?") == 0) {
	    PrintUsage();
	    *pret = 0;
	    return JNI_FALSE;
	} else if (strcmp(arg, "-version") == 0) {
	    printVersion = JNI_TRUE;
	    return JNI_TRUE;
	} else if (strcmp(arg, "-showversion") == 0) {
	    showVersion = JNI_TRUE;
	} else if (strcmp(arg, "-X") == 0) {
	    *pret = PrintXUsage();
	    return JNI_FALSE;
/*
 * The following case provide backward compatibility with old-style
 * command line options.
 */
	} else if (strcmp(arg, "-fullversion") == 0) {
	    fprintf(stderr, "%s full version \"%s\"\n", progname,
		    FULL_VERSION);
	    *pret = 0;
	    return JNI_FALSE;
	} else if (strcmp(arg, "-verbosegc") == 0) {
	    AddOption("-verbose:gc", NULL);
	} else if (strcmp(arg, "-t") == 0) {
	    AddOption("-Xt", NULL);
	} else if (strcmp(arg, "-tm") == 0) {
	    AddOption("-Xtm", NULL);
	} else if (strcmp(arg, "-debug") == 0) {
	    AddOption("-Xdebug", NULL);
	} else if (strcmp(arg, "-noclassgc") == 0) {
	    AddOption("-Xnoclassgc", NULL);
	} else if (strcmp(arg, "-Xfuture") == 0) {
	    AddOption("-Xverify:all", NULL);
	} else if (strcmp(arg, "-verify") == 0) {
	    AddOption("-Xverify:all", NULL);
	} else if (strcmp(arg, "-verifyremote") == 0) {
	    AddOption("-Xverify:remote", NULL);
	} else if (strcmp(arg, "-noverify") == 0) {
	    AddOption("-Xverify:none", NULL);
	} else if (strncmp(arg, "-prof", 5) == 0) {
	    char *p = arg + 5;
	    char *tmp = MemAlloc(strlen(arg) + 50);
	    if (*p) {
	        sprintf(tmp, "-Xrunhprof:cpu=old,file=%s", p + 1);
	    } else {
	        sprintf(tmp, "-Xrunhprof:cpu=old,file=java.prof");
	    }
	    AddOption(tmp, NULL);
	} else if (strncmp(arg, "-ss", 3) == 0 ||
		   strncmp(arg, "-oss", 4) == 0 ||
		   strncmp(arg, "-ms", 3) == 0 ||
		   strncmp(arg, "-mx", 3) == 0) {
	    char *tmp = MemAlloc(strlen(arg) + 6);
	    sprintf(tmp, "-X%s", arg + 1); /* skip '-' */
	    AddOption(tmp, NULL);
	} else if (strcmp(arg, "-checksource") == 0 ||
		   strcmp(arg, "-cs") == 0 ||
		   strcmp(arg, "-noasyncgc") == 0) {
	    /* No longer supported */
	    fprintf(stderr,
		    "Warning: %s option is no longer supported.\n",
		    arg);
	} else {
	    AddOption(arg, NULL);
	}
    }

    if (--argc >= 0) {
        if (jarflag) {
	    *pjarfile = *argv++;
	    *pclassname = 0;
	} else {
	    *pjarfile = 0;
	    *pclassname = *argv++;
	}
	*pargc = argc;
	*pargv = argv;
    }

    return JNI_TRUE;
}

/*
 * Initializes the Java Virtual Machine. Also frees options array when
 * finished.
 */
static jboolean
InitializeJVM(JavaVM **pvm, JNIEnv **penv, InvocationFunctions *ifn)
{
    JavaVMInitArgs args;
    jint r;

#ifdef OLDJAVA
    /* Indicate that we are using the old-style launcher */
    AddOption("-Xoldjava", NULL);
#endif

    memset(&args, 0, sizeof(args));
    args.version  = JNI_VERSION_1_2;
    args.nOptions = numOptions;
    args.options  = options;
    args.ignoreUnrecognized = JNI_FALSE;

    if (debug) {
	int i = 0;
	printf("JavaVM args:\n    ");
	printf("version 0x%08lx, ", args.version);
	printf("ignoreUnrecognized is %s, ",
	       args.ignoreUnrecognized ? "JNI_TRUE" : "JNI_FALSE");
	printf("nOptions is %ld\n", args.nOptions);
	for (i = 0; i < numOptions; i++)
	    printf("    option[%2d] = '%s'\n",
		   i, args.options[i].optionString);
    }

    r = ifn->CreateJavaVM(pvm, (void **)penv, &args);
    free(options);
    return r == JNI_OK;
}


#define NULL_CHECK0(e) if ((e) == 0) return 0
#define NULL_CHECK(e) if ((e) == 0) return

/*
 * Returns a pointer to a block of at least 'size' bytes of memory.
 * Prints error message and exits if the memory could not be allocated.
 */
static void *
MemAlloc(size_t size)
{
    void *p = malloc(size);
    if (p == 0) {
	perror("malloc");
	exit(1);
    }
    return p;
}

/*
 * Returns a new Java string object for the specified platform string.
 */
static jstring
NewPlatformString(JNIEnv *env, char *s)
{
    int len = strlen(s);
    jclass cls;
    jmethodID mid;
    jbyteArray ary;

    NULL_CHECK0(cls = (*env)->FindClass(env, "java/lang/String"));
    NULL_CHECK0(mid = (*env)->GetMethodID(env, cls, "<init>", "([B)V"));
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
 * Returns a new array of Java string objects for the specified
 * array of platform strings.
 */
static jobjectArray
NewPlatformStringArray(JNIEnv *env, char **strv, int strc)
{
    jarray cls;
    jarray ary;
    int i;

    NULL_CHECK0(cls = (*env)->FindClass(env, "java/lang/String"));
    NULL_CHECK0(ary = (*env)->NewObjectArray(env, strc, cls, 0));
    for (i = 0; i < strc; i++) {
	jstring str = NewPlatformString(env, *strv++);
	NULL_CHECK0(str);
	(*env)->SetObjectArrayElement(env, ary, i, str);
	(*env)->DeleteLocalRef(env, str);
    }
    return ary;
}

/*
 * Loads a class, convert the '.' to '/'.
 */
static jclass
LoadClass(JNIEnv *env, char *name)
{
    char *buf = MemAlloc(strlen(name) + 1);
    char *s = buf, *t = name, c;
    jclass cls;
    jlong start, end;

    if (debug)
	start = CounterGet();

    do {
        c = *t++;
	*s++ = (c == '.') ? '/' : c;
    } while (c != '\0');
    cls = (*env)->FindClass(env, buf);
    free(buf);

    if (debug) {
	end   = CounterGet();
	printf("%ld micro seconds to load main class\n",
	       (jint)Counter2Micros(end-start));
	printf("----_JAVA_LAUNCHER_DEBUG----\n");
    }

    return cls;
}

/*
 * Returns the main class name for the specified jar file.
 */
static jstring
GetMainClassName(JNIEnv *env, char *jarname)
{
#define MAIN_CLASS "Main-Class"
    jclass cls;
    jmethodID mid;
    jobject jar, man, attr;
    jstring str, result = 0;

    NULL_CHECK0(cls = (*env)->FindClass(env, "java/util/jar/JarFile"));
    NULL_CHECK0(mid = (*env)->GetMethodID(env, cls, "<init>",
					  "(Ljava/lang/String;)V"));
    NULL_CHECK0(str = NewPlatformString(env, jarname));
    NULL_CHECK0(jar = (*env)->NewObject(env, cls, mid, str));
    NULL_CHECK0(mid = (*env)->GetMethodID(env, cls, "getManifest",
					  "()Ljava/util/jar/Manifest;"));
    man = (*env)->CallObjectMethod(env, jar, mid);
    if (man != 0) {
	NULL_CHECK0(mid = (*env)->GetMethodID(env,
				    (*env)->GetObjectClass(env, man),
				    "getMainAttributes",
				    "()Ljava/util/jar/Attributes;"));
	attr = (*env)->CallObjectMethod(env, man, mid);
	if (attr != 0) {
	    NULL_CHECK0(mid = (*env)->GetMethodID(env,
				    (*env)->GetObjectClass(env, attr),
				    "getValue",
				    "(Ljava/lang/String;)Ljava/lang/String;"));
	    NULL_CHECK0(str = NewPlatformString(env, MAIN_CLASS));
	    result = (*env)->CallObjectMethod(env, attr, mid, str);
	}
    }
    return result;
}

#ifdef JAVA_ARGS
static char *java_args[] = JAVA_ARGS;
static char *app_classpath[] = APP_CLASSPATH;
#define NUM_ARGS (sizeof(java_args) / sizeof(char *))
#define NUM_APP_CLASSPATH (sizeof(app_classpath) / sizeof(char *))

/*
 * For tools convert 'javac -J-ms32m' to 'java -ms32m ...'
 */
static void
TranslateDashJArgs(int *pargc, char ***pargv)
{
    int argc = *pargc;
    char **argv = *pargv;
    int nargc = argc + NUM_ARGS;
    char **nargv = MemAlloc((nargc + 1) * sizeof(char *));
    int i;

    *pargc = nargc;
    *pargv = nargv;

    /* Copy the VM arguments (i.e. prefixed with -J) */
    for (i = 0; i < NUM_ARGS; i++) {
	char *arg = java_args[i];
	if (arg[0] == '-' && arg[1] == 'J')
	    *nargv++ = arg + 2;
    }

    for (i = 0; i < argc; i++) {
	char *arg = argv[i];
	if (arg[0] == '-' && arg[1] == 'J')
	    *nargv++ = arg + 2;
    }

    /* Copy the rest of the arguments */
    for (i = 0; i < NUM_ARGS; i++) {
	char *arg = java_args[i];
	if (arg[0] != '-' || arg[1] != 'J') {
	    *nargv++ = arg;
	}
    }
    for (i = 0; i < argc; i++) {
	char *arg = argv[i];
	if (arg[0] != '-' || arg[1] != 'J') {
	    *nargv++ = arg;
	}
    }
    *nargv = 0;
}

/*
 * For our tools, we try to add 3 VM options:
 *	-Denv.class.path=<envcp>
 *	-Dapplication.home=<apphome>
 *	-Djava.class.path=<appcp>
 * <envcp>   is the user's setting of CLASSPATH -- for instance the user
 *           tells javac where to find binary classes through this environment
 *           variable.  Notice that users will be able to compile against our
 *           tools classes (sun.tools.javac.Main) only if they explicitly add
 *           tools.jar to CLASSPATH.
 * <apphome> is the directory where the application is installed.
 * <appcp>   is the classpath to where our apps' classfiles are.
 */
static jboolean
AddApplicationOptions()
{
    char *s, *envcp, *appcp, *apphome;
    char home[MAXPATHLEN]; /* application home */
    char separator[] = { PATH_SEPARATOR, '\0' };
    int size, i;
    int strlenHome;

    s = getenv("CLASSPATH");
    if (s) {
	/* 40 for -Denv.class.path= */
	envcp = (char *)MemAlloc(strlen(s) + 40);
	sprintf(envcp, "-Denv.class.path=%s", s);
	AddOption(envcp, NULL);
    }

    if (!GetApplicationHome(home, sizeof(home))) {
	fprintf(stderr, "Can't determine application home\n");
	return JNI_FALSE;
    }

    /* 40 for '-Dapplication.home=' */
    apphome = (char *)MemAlloc(strlen(home) + 40);
    sprintf(apphome, "-Dapplication.home=%s", home);
    AddOption(apphome, NULL);

    /* How big is the application's classpath? */
    size = 40;                                 /* 40: "-Djava.class.path=" */
    strlenHome = strlen(home);
    for (i = 0; i < NUM_APP_CLASSPATH; i++) {
	size += strlenHome + strlen(app_classpath[i]) + 1; /* 1: separator */
    }
    appcp = (char *)MemAlloc(size + 1);
    strcpy(appcp, "-Djava.class.path=");
    for (i = 0; i < NUM_APP_CLASSPATH; i++) {
	strcat(appcp, home);			/* c:\program files\myapp */
	strcat(appcp, app_classpath[i]);	/* \lib\myapp.jar	  */
	strcat(appcp, separator);		/* ;			  */
    }
    appcp[strlen(appcp)-1] = '\0';  /* remove trailing path separator */
    AddOption(appcp, NULL);
    return JNI_TRUE;
}
#endif

/*
 * Prints the version information from the java.version and other properties.
 */
static void
PrintJavaVersion(JNIEnv *env)
{
    jclass ver;
    jmethodID print;

    NULL_CHECK(ver = (*env)->FindClass(env, "sun/misc/Version"));
    NULL_CHECK(print = (*env)->GetStaticMethodID(env, ver, "print", "()V"));

    (*env)->CallStaticVoidMethod(env, ver, print);
}

/*
 * Prints default usage message.
 */
static void
PrintUsage(void)
{
    int i;
    char jrepath[MAXPATHLEN], jvmpath[MAXPATHLEN];

    fprintf(stdout,
	"Usage: %s [-options] class [args...]\n"
#ifndef OLDJAVA
	"           (to execute a class)\n"
	"   or  %s -jar [-options] jarfile [args...]\n"
	"           (to execute a jar file)\n"
#endif
	"\n"
	"where options include:\n",
#ifndef OLDJAVA
	progname,
#endif
	progname);

    /*
     * Find out where the JRE is that we will be using.
     * Loop through the known VMs, printing a line for each existing VM.
     */
    if (GetJREPath(jrepath, sizeof(jrepath))) {
	for (i = 0; i < knownVMsCount; i++) {
	    const char *jvmtype = knownVMs[i]+1;

	    const char *synonym = ReadJVMLink(jrepath, jvmtype,
					      knownVMs, knownVMsCount);
	    if (synonym != NULL) {
		if (synonym[0] != '\0') {
		    fprintf(stdout, "    %s\t  is a synonym for "
			    "the \"%s\" VM  [deprecated]\n",
			    knownVMs[i], synonym);
		}
	    } else {
		if (GetJVMPath(jrepath, jvmtype, jvmpath, sizeof(jvmpath))) {
		    fprintf(stdout, "    %s\t  to select the \"%s\" VM\n",
			    knownVMs[i], knownVMs[i]+1);
		}
	    }
	}
	fprintf(stdout,
	    "                  If present, the option to select the VM must be first.\n"
	    "                  The default VM is %s.\n\n", knownVMs[0]);
    }

    fprintf(stdout,
#ifdef OLDJAVA
	"    -cp -classpath <directories and zip/jar files separated by %c>\n"
	"                  set search path for classes and resources\n"
#else
	"    -cp -classpath <directories and zip/jar files separated by %c>\n"
	"                  set search path for application classes and resources\n"
#endif
	"    -D<name>=<value>\n"
	"                  set a system property\n"
	"    -verbose[:class|gc|jni]\n"
	"                  enable verbose output\n"
	"    -version      print product version and exit\n"
	"    -showversion  print product version and continue\n"
	"    -? -help      print this help message\n"
	"    -X            print help on non-standard options\n",
	PATH_SEPARATOR);
}

/*
 * Print usage message for -X options.
 */
static jint
PrintXUsage(void)
{
    char path[MAXPATHLEN];
    char buf[128];
    int n;
    FILE *fp;

    GetXUsagePath(path, sizeof(path));
    fp = fopen(path, "r");
    if (fp == 0) {
        fprintf(stderr, "Can't open %s\n", path);
	return 1;
    }
    while ((n = fread(buf, 1, sizeof(buf), fp)) != 0) {
        fwrite(buf, 1, n, stdout);
    }
    fclose(fp);
    return 0;
}

/*
 * Read the jvm.cfg file and fill the knownJVMs[] array.
 */
static jint cfgLinesRead = 0;

static jint
ReadKnownVMs(const char *jrepath)
{
    FILE *jvmCfg;
    char jvmCfgName[MAXPATHLEN];
    char line[MAXPATHLEN];
    int cnt = 0;
    int lineno = 0;
    jlong start, end;

    if (debug) {
	start = CounterGet();
    }

    strcpy(jvmCfgName, jrepath);
    strcat(jvmCfgName, JVM_CFG);

    jvmCfg = fopen(jvmCfgName, "r");
    if (jvmCfg == NULL) {
	fprintf(stderr, "Error: could not open `%s'\n", jvmCfgName);
	return 0;
    }
    while (fgets(line, sizeof(line), jvmCfg) != NULL) {
	lineno++;
	if (line[0] == '#')
	    continue;
	if (line[0] != '-') {
	    fprintf(stderr, "Warning: no leading - on line %d of `%s'\n",
		    lineno, jvmCfgName);
	}
	if (cnt >= MAX_KNOWN_VMS) {
	    fprintf(stderr,
		    "Warning: can't read more than %d entries from\n`%s'\n",
		    MAX_KNOWN_VMS, jvmCfgName);
	    break;
	}
	line[strlen(line)-1] = '\0'; /* remove trailing newline */
	if (debug)
	    printf("jvm.cfg[%d] = ->%s<-\n", cnt, line);
	knownVMs[cnt++] = strdup(line);
    }
    fclose(jvmCfg);
    cfgLinesRead = cnt;

    if (debug) {
	end   = CounterGet();
	printf("%ld micro seconds to parse jvm.cfg\n",
	       (jint)Counter2Micros(end-start));
    }

    return cnt;
}


static void
FreeKnownVMs()
{
    int i;
    for (i = 0; i < cfgLinesRead; i++) {
	free(knownVMs[i]);
	knownVMs[i] = NULL;
    }
}

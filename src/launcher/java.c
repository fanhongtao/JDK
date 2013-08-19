/*
 * @(#)java.c	1.100 03/01/19
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Shared source for 'java' command line tool.
 *
 * If JAVA_ARGS is defined, then acts as a launcher for applications. For
 * instance, the JDK command line tools such as javac and javadoc (see
 * makefiles for more details) are built with this program.  Any arguments
 * prefixed with '-J' will be passed directly to the 'java' command.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#ifndef WIN32
#include <unistd.h>
#endif

#include <jni.h>
#include "java.h"

#ifndef FULL_VERSION
#define FULL_VERSION "1.4"
#endif

#ifdef WIN32
#define PATHSEP "\\"
#else
#define PATHSEP "/"
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
static void SetJavaCommandLineProp(char* classname, char* jarfile, int argc, char** argv);

#ifdef JAVA_ARGS
static void TranslateDashJArgs(int *pargc, char ***pargv);
static jboolean AddApplicationOptions(void);
#endif

static void PrintJavaVersion(JNIEnv *env);
static void PrintUsage(void);
static jint PrintXUsage(void);

static char *SetExecname(int argc, char **argv);
static void SetPaths(int argc, char **argv);
static char *CheckJvmType(int *argc, char ***argv);
static void SetDataModel(int *argc, char ***argv, char *execname, 
			    char *jrepath);
static void SetLibraryPath(char ** original_argv, char *execname, 
			   char *jrepath, char *jvmpath);
static void InitEncodingFlag(JNIEnv *env); /* Temp fix for UTF16-le encoding */

/* Support for options such as -hotspot, -classic etc. */
#define INIT_MAX_KNOWN_VMS 10
struct vmdesc {
    char *name;
#define VM_UNKNOWN -1
#define VM_KNOWN 0
#define VM_ALIASED_TO 1
#define VM_WARN 2
#define VM_ERROR 3
    int flag;
    char *alias;
};
static struct vmdesc *knownVMs = NULL;
static int knownVMsCount = 0;
static int knownVMsLimit = 0;

static jint ReadKnownVMs(const char *jrepath); 
static void GrowKnownVMs();
static int  KnownVMIndex(const char* name);
static void FreeKnownVMs(); 

/*
 * Entry point.
 */
int
main(int argc, char ** argv)
{
    JavaVM *vm = 0;
    JNIEnv *env = 0;
    char *jarfile = 0;
    char *classname = 0;
    char *s = 0;
    jstring mainClassName;
    jclass mainClass;
    jmethodID mainID;
    jobjectArray mainArgs;
    int ret;
    InvocationFunctions ifn;
    char *jvmtype = 0;
    jlong start, end;
    char jrepath[MAXPATHLEN], jvmpath[MAXPATHLEN];
    char ** original_argv = argv;

#ifndef WIN32
    char *execname = 0;
#endif

    if (getenv("_JAVA_LAUNCHER_DEBUG") != 0) {
	debug = JNI_TRUE;
	printf("----_JAVA_LAUNCHER_DEBUG----\n");
    }

#ifndef WIN32
    /* Compute the name of the executable */
    execname = SetExecname(argc, argv);
#endif

    /* Find out where the JRE is that we will be using. */
    if (!GetJREPath(jrepath, sizeof(jrepath))) {
	fprintf(stderr, "Error: could not find Java 2 Runtime Environment.\n");
	return 2;
    }

#ifndef WIN32
    /* Check for data model flags, and run a different executable, if necessary. */
    SetDataModel(&argc, &argv, execname, jrepath);
#endif

    /* Find the specified JVM type */
    if (ReadKnownVMs(jrepath) < 1) {
	fprintf(stderr, "Error: no known VMs. (check for corrupt jvm.cfg file)\n");
	exit(1);
    }
    jvmtype = CheckJvmType(&argc, &argv);

    jvmpath[0] = '\0';
    if (!GetJVMPath(jrepath, jvmtype, jvmpath, sizeof(jvmpath))) {
	fprintf(stderr, "Error: no `%s' JVM at `%s'.\n", jvmtype, jvmpath);
	return 4;
    }
    /* If we got here, jvmpath has been correctly initialized. */

#ifndef WIN32
    /* Set the LD_LIBRARY_PATH environment variable */
    SetLibraryPath(original_argv, execname, jrepath, jvmpath);
#endif

    ifn.CreateJavaVM = 0; ifn.GetDefaultJavaVMInitArgs = 0;
    if (debug)
	start = CounterGet();
    if (!LoadJavaVM(jvmpath, &ifn)) {
        status = 1;
	return 6;
    }
    if (debug) {
	end   = CounterGet();
	printf("%ld micro seconds to LoadJavaVM\n",
	       (long)(jint)Counter2Micros(end-start));
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

    /* set the -Dsun.java.command pseudo property */
    SetJavaCommandLineProp(classname, jarfile, argc, argv);

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
	       (long)(jint)Counter2Micros(end-start));
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

    /* Temporary fix for utf-16le encoding. Init encoding flag */
    InitEncodingFlag(env);

    /* Get the application's main class */
    if (jarfile != 0) {
	mainClassName = GetMainClassName(env, jarfile);
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
        mainClassName = NewPlatformString(env, classname);
        if (mainClassName == NULL) {
            fprintf(stderr, "Failed to load Main Class: %s\n", classname);
            goto leave;
        }
        classname = (char *)(*env)->GetStringUTFChars(env, mainClassName, 0);
        if (classname == NULL) {
            (*env)->ExceptionDescribe(env);
            goto leave;
        }
        mainClass = LoadClass(env, classname);
        (*env)->ReleaseStringUTFChars(env, mainClassName, classname);
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

    {    /* Make sure the main method is public */
	jobject obj = (*env)->ToReflectedMethod(env, mainClass, 
						mainID, JNI_TRUE);
	jint mods;
	jmethodID mid = 
	  (*env)->GetMethodID(env, 
			      (*env)->GetObjectClass(env, obj),
			      "getModifiers", "()I");
	if ((*env)->ExceptionOccurred(env)) {
	    (*env)->ExceptionDescribe(env);
	    status = 6;
	    goto leave;
	}

	mods = (*env)->CallIntMethod(env, obj, mid);
	if ((mods & 1) == 0) { /* if (!Modifier.isPublic(mods)) ... */
	    fprintf(stderr, "Main method not public.\n");
	    status = 8;
	    goto leave;
	}
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


#ifndef WIN32

#include <sys/stat.h>

/*
 * Return true if the named program exists
 */
static int
ProgramExists(char *name)
{
    struct stat sb;
    if (stat(name, &sb) != 0) return 0;
    if (S_ISDIR(sb.st_mode)) return 0;
    return (sb.st_mode & S_IEXEC) != 0;
}


/*
 * Find a command in a directory, returning the path.
 */
static char *
Resolve(char *indir, char *cmd)
{
    char name[PATH_MAX + 2], *real;

    if ((strlen(indir) + strlen(cmd) + 1)  > PATH_MAX) return 0;
    sprintf(name, "%s%c%s", indir, FILE_SEPARATOR, cmd);
    if (!ProgramExists(name)) return 0;
    real = malloc(PATH_MAX + 2);
    if (!realpath(name, real)) 
	strcpy(real, name);
    return real;
}


/*
 * Find a path for the executable
 */
static char *
FindExecName(char *program)
{
    char cwdbuf[PATH_MAX+2];
    char *path;
    char *tmp_path;
    char *f;
    char *result = 0;

    /* absolute path? */
    if (*program == FILE_SEPARATOR || 
	(FILE_SEPARATOR=='\\' && strrchr(program, ':')))
	return Resolve("", program+1);

    /* relative path? */
    if (strrchr(program, FILE_SEPARATOR) != 0) {
	char buf[PATH_MAX+2];
	return Resolve(getcwd(cwdbuf, sizeof(cwdbuf)), program);
    }

    /* from search path? */
    path = getenv("PATH");
    if (!path || !*path) path = ".";
    tmp_path = malloc(strlen(path) + 2);
    strcpy(tmp_path, path);

    for (f=tmp_path; *f && result==0; ) {
	char *s = f;
	while (*f && (*f != PATH_SEPARATOR)) ++f;
	if (*f) *f++ = 0;
	if (*s == FILE_SEPARATOR)
	    result = Resolve(s, program);
	else {
	    /* relative path element */
	    char dir[2*PATH_MAX];
	    sprintf(dir, "%s%c%s", getcwd(cwdbuf, sizeof(cwdbuf)), 
		    FILE_SEPARATOR, s);
	    result = Resolve(dir, program);
	}
	if (result != 0) break;
    }

    free(tmp_path);
    return result;
}


/* Store the name of the executable once computed */
static char *execname = 0;

/*
 * Compute the name of the executable
 */
static char *
SetExecname(int argc, char **argv)
{
    char *exec_path = FindExecName(argv[0]);
    execname = exec_path;
    return exec_path;
}

/*
 * Return the name of the executable.  Used in java_md.c to find the JRE area.
 */
char *
GetExecname()
{
    return execname;
}
#endif /* #ifndef WIN32 */


/*
 * Run another executable if necessary to change the data model.
 * Also, remove -d64 and the like from argc/argv.
 */
static void
SetDataModel(int *_argc, char ***_argv, 
		char *execname, char *jrepath)
{
#ifdef sparc
    int argc = *_argc;
    char **argv = *_argv;
    int i, running, wanted;

    char **newargv;
    int newargc = 0;

    newargv = (char **)malloc((argc+1) * sizeof(*newargv));
    newargv[newargc++] = argv[0];

#ifdef _LP64
    /* we're already running a 64-bit executable */
    running = 64;
#else
    running = 32;
#endif
    wanted = 0;

    /* scan for data model arg */
    for (i=1; i<argc; i++) {
	if (strcmp(argv[i], "-J-d64") == 0 || strcmp(argv[i], "-d64") == 0) {
	    wanted = 64;
	    continue;
	}
	if (strcmp(argv[i], "-J-d32") == 0 || strcmp(argv[i], "-d32") == 0) {
	    wanted = 32;
	    continue;
	}
	newargv[newargc++] = argv[i];

#ifdef JAVA_ARGS
	if (argv[i][0] != '-') continue;
#else
	if (strcmp(argv[i], "-classpath") == 0 || strcmp(argv[i], "-cp") == 0) {
	    i++;
	    if (i >= argc) break;
	    newargv[newargc++] = argv[i];
	    continue;
	}
	if (argv[i][0] != '-') { i++; break; }
#endif
    }

    /* copy rest of args [i .. argc) */
    while (i < argc) {
	newargv[newargc++] = argv[i++];
    }
    newargv[newargc] = 0;

    *_argc = argc = newargc;
    *_argv = argv = newargv;

    if (wanted == 0 || running == wanted) return;

    {
	char *oldexec = strcpy(malloc(strlen(execname) + 1), execname);
	char *olddir = oldexec;
	char *oldbase = strrchr(oldexec, '/');
	char *newexec;

	newexec = malloc(strlen(execname) + 20);
	*oldbase++ = 0;
	sprintf(newexec, "%s/%s/%s", olddir, 
		((wanted==64) ? "sparcv9" : ".."), oldbase);
	argv[0] = newexec;
	execv(newexec, argv);
	perror("execv()");
	exit(1);
    }
#endif /* sparc */
}


/*
 * Check for a specified JVM type
 */
static char *
CheckJvmType(int *pargc, char ***argv) {
    int i, argi;
    int argc;
    char **newArgv;
    int newArgvIdx = 0;
    int isVMType;
    int jvmidx = -1;
    char *jvmtype = getenv("JDK_ALTERNATE_VM");

    argc = *pargc;

    /* To make things simpler we always copy the argv array */
    newArgv = MemAlloc((argc + 1) * sizeof(char *));

    /* The program name is always present */
    newArgv[newArgvIdx++] = (*argv)[0];

    for (argi = 1; argi < argc; argi++) {
	char *arg = (*argv)[argi];
        isVMType = 0;

#ifdef JAVA_ARGS
 	if (arg[0] != '-') {
            newArgv[newArgvIdx++] = arg;
            continue;
        }
#else
 	if (strcmp(arg, "-classpath") == 0 || 
 	    strcmp(arg, "-cp") == 0) {
            newArgv[newArgvIdx++] = arg;
 	    argi++;
            if (argi < argc) {
                newArgv[newArgvIdx++] = (*argv)[argi];
            }
 	    continue;
 	}
 	if (arg[0] != '-') break;
#endif

 	/* Did the user pass an explicit VM type? */
	i = KnownVMIndex(arg);
	if (i >= 0) {
	    jvmtype = knownVMs[jvmidx = i].name + 1; /* skip the - */
	    isVMType = 1;
	    *pargc = *pargc - 1;
	}

	/* Did the user specify an "alternate" VM? */
	else if (strncmp(arg, "-XXaltjvm=", 10) == 0 || strncmp(arg, "-J-XXaltjvm=", 12) == 0) {
	    isVMType = 1;
	    jvmtype = arg+((arg[1]=='X')? 10 : 12);
	    jvmidx = -1;
	}

        if (!isVMType) {
            newArgv[newArgvIdx++] = arg;
        }
    }

    /* Finish copying the arguments if we aborted the above loop.
       NOTE that if we aborted via "break" then we did NOT copy the
       last argument above, and in addition argi will be less than
       argc. */
    while (argi < argc) {
        newArgv[newArgvIdx++] = (*argv)[argi];
        argi++;
    }

    /* argv is null-terminated */
    newArgv[newArgvIdx] = 0;

    /* Copy back argv */
    *argv = newArgv;
    *pargc = newArgvIdx;

    /* use the default VM type if not specified (no alias processing) */
    if (jvmtype == NULL) return knownVMs[0].name+1;

    /* if using an alternate VM, no alieas processing */
    if (jvmidx < 0) return jvmtype;

    /* Resolve aliases first */
    while (knownVMs[jvmidx].flag == VM_ALIASED_TO) {
        int nextIdx = KnownVMIndex(knownVMs[jvmidx].alias);
        if (nextIdx < 0) {
            fprintf(stderr, "Error: Unable to resolve VM alias %s\n", knownVMs[jvmidx].alias);
            exit(1);
        }
        jvmidx = nextIdx;
        jvmtype = knownVMs[jvmidx].name+1;
    }

    switch (knownVMs[jvmidx].flag) {
    case VM_WARN:
	fprintf(stderr, "Warning: %s VM not supported; %s VM will be used\n", 
		jvmtype, knownVMs[0].name + 1);
	jvmtype = knownVMs[jvmidx=0].name + 1;
	/* fall through */
    case VM_KNOWN:
	break;
    case VM_ERROR:
	fprintf(stderr, "Error: %s VM not supported\n", jvmtype);
	exit(1);
    }

    return jvmtype;
}


static void
SetLibraryPath(char **original_argv, char *execname, 
	       char *jrepath, char *_jvmpath)
{
#ifndef WIN32
    char *arch = (char *)GetArch(); /* like sparc or sparcv9 */
    char *oldpath = getenv("LD_LIBRARY_PATH");
    char *debugprog;

    /*
     * We need to set LD_LIBRARY_PATH as follows:
     *
     *     o		$JVMPATH (directory portion only)
     *     o		$JRE/lib/$ARCH
     *     o		$JRE/../lib/$ARCH
     *
     * followed by the user's previous $LD_LIBRARY_PATH, if any
     */
    char *jvmpath = strdup(_jvmpath);
    char *newenv = malloc((oldpath?strlen(oldpath):0) + 
			  2*strlen(jrepath) + 2*strlen(arch) +
			  strlen(jvmpath) + 52);
    char *newpath = newenv + strlen("LD_LIBRARY_PATH=");

    /* remove the name of the .so from the JVM path */
    char *lastslash = strrchr(jvmpath, '/');
    if (lastslash) *lastslash = '\0';

    sprintf(newenv, "LD_LIBRARY_PATH="
	    "%s:"
	    "%s/lib/%s:"
	    "%s/../lib/%s",
	    jvmpath,
	    jrepath, arch,
	    jrepath, arch);

    /* Check to make sure that the prefix of the current path is the 
     * desired environment variable setting. */
    if (oldpath != 0 && 
	strncmp(newpath, oldpath, strlen(newpath))==0 &&
	(oldpath[strlen(newpath)] == 0 || oldpath[strlen(newpath)] == ':'))
	return; /* already on the LD_LIBRARY_PATH */
    
    /* Place the desired environment setting onto the prefix of 
     * LD_LIBRARY_PATH.  Note that this prevents any possible infinite 
     * loop of execv() because we test for the prefix, above. */
    if (oldpath != 0) {
	strcat(newenv, ":");
	strcat(newenv, oldpath);
    }
    putenv(newenv);

    /* Unix systems document that they look at LD_LIBRARY_PATH 
     * only once at startup, so we have to re-exec the current executable 
     * to get the changed environment variable to have an effect. 
     *
     * If the DEBUGPROG env variable is set, exec the debugger 
     */

    if ( (debugprog = getenv("DEBUGPROG")) != NULL ) {
	char **new_argv;
    
        new_argv = (char **)malloc(3 * sizeof(char *));
	
	new_argv[0] = debugprog;
	new_argv[1] = execname;
	new_argv[2] = NULL;
        execv(debugprog, new_argv);
        perror("execv()");
        exit(1);
    }
    else {
        execv(execname, original_argv);
        perror("execv()");
        exit(1);
    }
#endif
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
    sprintf(def, "-Djava.class.path=%s", s);
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
	} else if (strcmp(arg, "-jar") == 0) {
	    jarflag = JNI_TRUE;
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

    memset(&args, 0, sizeof(args));
    args.version  = JNI_VERSION_1_2;
    args.nOptions = numOptions;
    args.options  = options;
    args.ignoreUnrecognized = JNI_FALSE;

    if (debug) {
	int i = 0;
	printf("JavaVM args:\n    ");
	printf("version 0x%08lx, ", (long)args.version);
	printf("ignoreUnrecognized is %s, ",
	       args.ignoreUnrecognized ? "JNI_TRUE" : "JNI_FALSE");
	printf("nOptions is %ld\n", (long)args.nOptions);
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

static int isUTF16;

static void
InitEncodingFlag(JNIEnv *env)
{
    jclass system;
    jmethodID getProperty;
    jstring fileEncoding;
    jstring value;
    const char* str;

    system = (*env)->FindClass(env, "java/lang/System");
    getProperty = 
            (*env)->GetStaticMethodID(env, system, "getProperty", 
                                     "(Ljava/lang/String;)Ljava/lang/String;");
    fileEncoding = (*env)->NewStringUTF(env, "file.encoding");
    value = 
       (*env)->CallStaticObjectMethod(env, system, getProperty, fileEncoding);
    if (value == NULL) {
        isUTF16 = JNI_FALSE;
        return;
    }
    str = (*env)->GetStringUTFChars(env, value, NULL);
    if (strcmp(str, "utf-16le") == 0)
        isUTF16 = JNI_TRUE;
    else
        isUTF16 = JNI_FALSE;
    (*env)->ReleaseStringUTFChars(env, value, str);
}

/*
 * Returns a new Java string object for the specified platform string.
 */
static jstring
NewPlatformString(JNIEnv *env, char *s)
{    
    int len = (int)strlen(s);
    jclass cls;
    jmethodID mid;
    jbyteArray ary;

    if (isUTF16)
        return (*env)->NewStringUTF(env, s);

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
	       (long)(jint)Counter2Micros(end-start));
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
	if (arg[0] == '-' && arg[1] == 'J') {
	    *nargv++ = arg + 2;
	}
    }

    for (i = 0; i < argc; i++) {
	char *arg = argv[i];
	if (arg[0] == '-' && arg[1] == 'J') {
	    if (arg[2] == '\0') {
		fprintf(stderr, "Error: the -J option should not be followed by a space.\n");
		exit(1);
	    }
	    *nargv++ = arg + 2;
	}
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
    strlenHome = (int)strlen(home);
    for (i = 0; i < NUM_APP_CLASSPATH; i++) {
	size += strlenHome + (int)strlen(app_classpath[i]) + 1; /* 1: separator */
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
 * inject the -Dsun.java.command pseudo property into the args structure
 * this pseudo property is used in the HotSpot VM to expose the
 * Java class name and arguments to the main method to the VM. The
 * HotSpot VM uses this pseudo property to store the Java class name
 * (or jar file name) and the arguments to the class's main method
 * to the instrumentation memory region. The sun.java.command pseudo
 * property is not exported by HotSpot to the Java layer.
 */
void
SetJavaCommandLineProp(char *classname, char *jarfile,
		       int argc, char **argv)
{

    int i = 0;
    size_t len = 0;
    char* javaCommand = NULL;
    char* dashDstr = "-Dsun.java.command=";

    if (classname == NULL && jarfile == NULL) {
        /* unexpected, one of these should be set. just return without
         * setting the property
         */
        return;
    }

    /* if the class name is not set, then use the jarfile name */
    if (classname == NULL) {
        classname = jarfile;
    }

    /* determine the amount of memory to allocate assuming
     * the individual components will be space separated
     */
    len = strlen(classname);
    for (i = 0; i < argc; i++) {
        len += strlen(argv[i]) + 1;
    }

    /* allocate the memory */
    javaCommand = (char*) MemAlloc(len + strlen(dashDstr) + 1);

    /* build the -D string */
    *javaCommand = '\0';
    strcat(javaCommand, dashDstr);
    strcat(javaCommand, classname);

    for (i = 0; i < argc; i++) {
        /* the components of the string are space separated. In
         * the case of embeded white space, the relationship of
         * the white space separated components to their true
         * positional arguments will be ambiguous. This issue may
         * be addressed in a future release.
         */
        strcat(javaCommand, " ");
        strcat(javaCommand, argv[i]);
    }

    AddOption(javaCommand, NULL);
}

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

    fprintf(stdout,
	"Usage: %s [-options] class [args...]\n"
	"           (to execute a class)\n"
	"   or  %s -jar [-options] jarfile [args...]\n"
	"           (to execute a jar file)\n"
	"\n"
	"where options include:\n",
	progname,
	progname);

#ifdef sparc
    fprintf(stdout,
	"    -d32\n"
	"                  use a 32-bit data model if available\n"
	"    -d64\n"
	"                  use a 64-bit data model if available\n");
#endif

    for (i=0; i<knownVMsCount; i++) {
	if (knownVMs[i].flag == VM_KNOWN)
	    fprintf(stdout, "    %s\t  to select the \"%s\" VM\n",
		    knownVMs[i].name, knownVMs[i].name+1);
    }
    for (i=0; i<knownVMsCount; i++) {
	if (knownVMs[i].flag == VM_ALIASED_TO)
	    fprintf(stdout, "    %s\t  is a synonym for "
		    "the \"%s\" VM  [deprecated]\n",
		    knownVMs[i].name, knownVMs[i].alias+1);
    }
    fprintf(stdout,
	"                  The default VM is %s.\n\n", knownVMs[0].name+1);

    fprintf(stdout,
"    -cp -classpath <directories and zip/jar files separated by %c>\n"
"                  set search path for application classes and resources\n"
"    -D<name>=<value>\n"
"                  set a system property\n"
"    -verbose[:class|gc|jni]\n"
"                  enable verbose output\n"
"    -version      print product version and exit\n"
"    -showversion  print product version and continue\n"
"    -? -help      print this help message\n"
"    -X            print help on non-standard options\n"
"    -ea[:<packagename>...|:<classname>]\n"
"    -enableassertions[:<packagename>...|:<classname>]\n"
"                  enable assertions\n"
"    -da[:<packagename>...|:<classname>]\n"
"    -disableassertions[:<packagename>...|:<classname>]\n"
"                  disable assertions\n"
"    -esa | -enablesystemassertions\n"
"                  enable system assertions\n"
"    -dsa | -disablesystemassertions\n"
"                  disable system assertions\n"
	    ,PATH_SEPARATOR);
}

/*
 * Print usage message for -X options.
 */
static jint
PrintXUsage(void)
{
    char path[MAXPATHLEN];
    char buf[128];
    size_t n;
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
static jint
ReadKnownVMs(const char *jrepath)
{
    char *arch = (char *)GetArch(); /* like sparcv9 */
    FILE *jvmCfg;
    char jvmCfgName[MAXPATHLEN+20];
    char line[MAXPATHLEN+20];
    int cnt = 0;
    int lineno = 0;
    jlong start, end;
    int vmType;
    char *tmpPtr;
    char *altVMName;
    static char *whiteSpace = " \t";
    if (debug) {
        start = CounterGet();
    }
    
    strcpy(jvmCfgName, jrepath);
    strcat(jvmCfgName, PATHSEP "lib" PATHSEP);
    strcat(jvmCfgName, arch);
    strcat(jvmCfgName, PATHSEP "jvm.cfg");
    
    jvmCfg = fopen(jvmCfgName, "r");
    if (jvmCfg == NULL) {
        fprintf(stderr, "Error: could not open `%s'\n", jvmCfgName);
	exit(1);
    }
    while (fgets(line, sizeof(line), jvmCfg) != NULL) {
        vmType = VM_UNKNOWN;
        lineno++;
        if (line[0] == '#')
            continue;
        if (line[0] != '-') {
            fprintf(stderr, "Warning: no leading - on line %d of `%s'\n",
                    lineno, jvmCfgName);
        }
        if (cnt >= knownVMsLimit) {
            GrowKnownVMs(cnt);
        }
        line[strlen(line)-1] = '\0'; /* remove trailing newline */
        tmpPtr = line + strcspn(line, whiteSpace);
        if (*tmpPtr == 0) {
            fprintf(stderr, "Warning: missing VM type on line %d of `%s'\n",
                    lineno, jvmCfgName);
        } else {
            /* Null-terminate this string for strdup below */
            *tmpPtr++ = 0;
            tmpPtr += strspn(tmpPtr, whiteSpace);
            if (*tmpPtr == 0) {
                fprintf(stderr, "Warning: missing VM type on line %d of `%s'\n",
                        lineno, jvmCfgName);
            } else {
                if (!strncmp(tmpPtr, "KNOWN", strlen("KNOWN"))) {
                    vmType = VM_KNOWN;
                } else if (!strncmp(tmpPtr, "ALIASED_TO", strlen("ALIASED_TO"))) {
                    tmpPtr += strcspn(tmpPtr, whiteSpace);
                    if (*tmpPtr != 0) {
                        tmpPtr += strspn(tmpPtr, whiteSpace);
                    }
                    if (*tmpPtr == 0) {
                        fprintf(stderr, "Warning: missing VM alias on line %d of `%s'\n",
                                lineno, jvmCfgName);
                    } else {
                        /* Null terminate altVMName */
                        altVMName = tmpPtr;
                        tmpPtr += strcspn(tmpPtr, whiteSpace);
                        *tmpPtr = 0;
                        vmType = VM_ALIASED_TO;
                    }
                } else if (!strncmp(tmpPtr, "WARN", strlen("WARN"))) {
                    vmType = VM_WARN;
                } else if (!strncmp(tmpPtr, "ERROR", strlen("ERROR"))) {
                    vmType = VM_ERROR;
                } else {
                    fprintf(stderr, "Warning: unknown VM type %s on line %d of `%s'\n",
                            vmType, lineno, &jvmCfgName[0]);
                    vmType = VM_KNOWN;
                }
            }
        }

        if (debug)
            printf("jvm.cfg[%d] = ->%s<-\n", cnt, line);
        if (vmType != VM_UNKNOWN) {
            knownVMs[cnt].name = strdup(line);
            knownVMs[cnt].flag = vmType;
            if (vmType == VM_ALIASED_TO) {
                knownVMs[cnt].alias = strdup(altVMName);
            }
            cnt++;
        }
    }
    fclose(jvmCfg);
    knownVMsCount = cnt;
    
    if (debug) {
        end   = CounterGet();
        printf("%ld micro seconds to parse jvm.cfg\n",
               (long)(jint)Counter2Micros(end-start));
    }
    
    return cnt;
}


static void
GrowKnownVMs(int minimum)
{
    struct vmdesc* newKnownVMs;
    int newMax;

    newMax = (knownVMsLimit == 0 ? INIT_MAX_KNOWN_VMS : (2 * knownVMsLimit));
    if (newMax <= minimum) {
        newMax = minimum;
    }
    newKnownVMs = (struct vmdesc*) malloc(newMax * sizeof(struct vmdesc));
    if (knownVMs != NULL) {
        memcpy(newKnownVMs, knownVMs, knownVMsLimit * sizeof(struct vmdesc));
    }
    free(knownVMs);
    knownVMs = newKnownVMs;
    knownVMsLimit = newMax;
}


/* Returns index of VM or -1 if not found */
static int
KnownVMIndex(const char* name)
{
    int i;
    if (strncmp(name, "-J", 2) == 0) name += 2;
    for (i = 0; i < knownVMsCount; i++) {
        if (!strcmp(name, knownVMs[i].name)) {
            return i;
        }
    }
    return -1;
}

static void
FreeKnownVMs()
{
    int i;
    for (i = 0; i < knownVMsCount; i++) {
        free(knownVMs[i].name);
        knownVMs[i].name = NULL;
    }
    free(knownVMs);
}


/*
 * @(#)java_md.c	1.33 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

#include "java.h"
#include <dlfcn.h>
#include <string.h>
#include <stdlib.h>
#include <limits.h>
#include <sys/stat.h>
#include <unistd.h>
#include <sys/types.h>

#ifdef DEBUG
#define JVM_DLL "libjvm_g.so"
#define JAVA_DLL "libjava_g.so"
#else
#define JVM_DLL "libjvm.so"
#define JAVA_DLL "libjava.so"
#endif

#ifdef _LP64
#ifdef ia64
#define ARCH "ia64"
#else
#define ARCH "sparcv9"
#endif
#endif

#ifdef i586
#define ARCH "i386"
#endif

#ifdef __sparc
#include <sys/systeminfo.h>
#include <sys/elf.h>
#include <stdio.h>
#else

#ifndef ARCH
#include <sys/systeminfo.h>
#endif

#endif

/* pointer to environment */
extern char **environ;

static void SetLibraryPath(int *_argc, char ***_argv,
			   int original_argc, char **original_argv,
			   char *execname, char *jrepath, char *_jvmpath);

static char *SetExecname(int argc, char **argv);
static char * GetExecname();
static jboolean GetJVMPath(const char *jrepath, const char *jvmtype,
			   char *jvmpath, jint jvmpathsize);
static jboolean GetJREPath(char *path, jint pathsize);

const char *
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

void
CreateExecutionEnvironment(int *_argc,
			   char ***_argv,
			   char jrepath[],
			   jint so_jrepath,
			   char jvmpath[],
			   jint so_jvmpath,
			   char **_jvmtype,
			   char **original_argv) {
    char *execname = NULL;
    int original_argc = *_argc;

    /* Compute the name of the executable */
    execname = SetExecname(*_argc, *_argv);

    /* Find out where the JRE is that we will be using. */
    if (!GetJREPath(jrepath, so_jrepath)) {
	fprintf(stderr, "Error: could not find Java 2 Runtime Environment.\n");
	exit(2);
    }

    /* Find the specified JVM type */
    if (ReadKnownVMs(jrepath) < 1) {
	fprintf(stderr, "Error: no known VMs. (check for corrupt jvm.cfg file)\n");
	exit(1);
    }
    *_jvmtype = CheckJvmType(_argc, _argv);

    jvmpath[0] = '\0';
    if (!GetJVMPath(jrepath, *_jvmtype, jvmpath, so_jvmpath)) {
	fprintf(stderr, "Error: no `%s' JVM at `%s'.\n", *_jvmtype, jvmpath);
	exit(4);
    }
    /* If we got here, jvmpath has been correctly initialized. */

    /* Set the LD_LIBRARY_PATH environment variable, check data model
       flags, and exec process, if needed */
    SetLibraryPath(_argc, _argv, original_argc, original_argv, execname, jrepath, jvmpath);
}


/*
 * On Solaris VM choosing is done by the launcher (java.c).
 */
static jboolean
GetJVMPath(const char *jrepath, const char *jvmtype,
	   char *jvmpath, jint jvmpathsize)
{
    struct stat s;
    
    if (strchr(jvmtype, '/')) {
	sprintf(jvmpath, "%s/" JVM_DLL, jvmtype);
    } else {
	sprintf(jvmpath, "%s/lib/%s/%s/" JVM_DLL, jrepath, GetArch(), jvmtype);
    }
    if (debug)
      printf("Does `%s' exist ... ", jvmpath);

    if (stat(jvmpath, &s) == 0) {
	if (debug) 
	  printf("yes.\n");
	return JNI_TRUE;
    } else {
	if (debug)
	  printf("no.\n");
	return JNI_FALSE;
    }
}

/*
 * Find path to JRE based on .exe's location or registry settings.
 */
static jboolean
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

    fprintf(stderr, "Error: could not find " JAVA_DLL "\n");
    return JNI_FALSE;

 found:
    if (debug)
      printf("JRE path is %s\n", path);
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
    if (libjvm == NULL) {
#ifdef __sparc
#ifndef _LP64

      FILE * fp;
      Elf32_Ehdr elf_head;
      int count;
      int location;
      
      fp = fopen(jvmpath, "r");
      if(fp == NULL)
	goto error;
    
      /* read in elf header */
      count = fread((void*)(&elf_head), sizeof(Elf32_Ehdr), 1, fp);
      fclose(fp);
      if(count < 1)
	goto error;

      /* 
       * Check for running a server vm (compiled with -xarch=v8plus)
       * on a stock v8 processor.  In this case, the machine type in
       * the elf header would not be included the architecture list
       * provided by the isalist command, which is turn is gotten from
       * sysinfo.  This case cannot occur on 64-bit hardware and thus
       * does not have to be checked for in binaries with an LP64 data
       * model.
       */
      if(elf_head.e_machine == EM_SPARC32PLUS) {
	char buf[257];  /* recommended buffer size from sysinfo man
			   page */
	long length;
	char* location;
	
	length = sysinfo(SI_ISALIST, buf, 257);
	if(length > 0) {
	  location = strstr(buf, "sparcv8plus ");
	  if(location == NULL) {
	    fprintf(stderr, "SPARC V8 processor detected; Server compiler requires V9 or better.\n");
	    fprintf(stderr, "Use Client compiler on V8 processors.\n");
	    fprintf(stderr, "Could not create the Java virtual machine.\n");
	    return JNI_FALSE;
	  }
	}
      }
#endif /* _LP64 */
#endif /* __sparc */
      fprintf(stderr, "dl failure on line %d", __LINE__);
      goto error;
    }

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
 * If app is "/foo/bin/javac", or "/foo/bin/sparcv9/javac" then put
 * "/foo" into buf.
 */
jboolean
GetApplicationHome(char *buf, jint bufsize)
{
#ifdef __linux__
    char *execname = GetExecname();
    if (execname) {
	strncpy(buf, execname, bufsize-1);
	buf[bufsize-1] = '\0';
    } else {
	return JNI_FALSE;
    }
#else
    Dl_info dlinfo;

    dladdr((void *)GetApplicationHome, &dlinfo);
    if (realpath(dlinfo.dli_fname, buf) == NULL) {
	fprintf(stderr, "Error: realpath(`%s') failed.\n", buf);
	return JNI_FALSE;
    }
#endif

    if (strrchr(buf, '/') == 0) {
	buf[0] = '\0';
	return JNI_FALSE;
    }
    *(strrchr(buf, '/')) = '\0';	/* executable file      */
    if (strlen(buf) < 4 || strrchr(buf, '/') == 0) {
	buf[0] = '\0';
	return JNI_FALSE;
    }
    if (strcmp("/bin", buf + strlen(buf) - 4) != 0) 
	*(strrchr(buf, '/')) = '\0';	/* sparcv9              */
    if (strlen(buf) < 4 || strcmp("/bin", buf + strlen(buf) - 4) != 0) {
	buf[0] = '\0';
	return JNI_FALSE;
    }
    *(strrchr(buf, '/')) = '\0';	/* bin                  */

    return JNI_TRUE;
}


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
    real = MemAlloc(PATH_MAX + 2);
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
    char *result = NULL;

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
    tmp_path = MemAlloc(strlen(path) + 2);
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
static char *execname = NULL;

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
static char *
GetExecname()
{
    return execname;
}

/*
 * Sets the proper runpath (via the LD_LIBRARY_PATH environment
 * variable) and ensures a jvm with the proper data model is invoked.
 * Both of these actions may require an exec; the current algorithm
 * requires at most one exec to change both runpath and data model.
 *
 * The options which specify the data model (currently only -d32 and
 * -d64 on Solaris SPARC) are removed from the argument list and not
 * passed to the exec'ed child.  The data model information is
 * preserved in the command string passed to exec; i.e. -d64 results
 * in a path to a 64-bit binary and -d32 results in a path to a 32-bit
 * binary.
 */
static void
SetLibraryPath(int *_argc, char ***_argv,
	       int original_argc,
	       char **original_argv,
	       char *execname, 
	       char *jrepath, char *_jvmpath)
{
    char *arch = (char *)GetArch(); /* like sparc or sparcv9 */

    int argc = original_argc;
    char **argv = original_argv;

    char *runpath=NULL; /* existing effective LD_LIBRARY_PATH
			   setting */

    int running=0;	/* What data model is being used?
			   ILP32 => 32 bit vm; LP64 => 64 bit vm */
    int wanted=0;	/* What data mode is being asked for? 0 means
			   no explicit request; i.e. current model is
			   fine */

    char* jvmpath =	NULL;
    char* new_runpath =	NULL;  /* desired new LD_LIBRARY_PATH string */
    char* newpath =	NULL; /* path on new LD_LIBRARY_PATH */
    char* lastslash =	NULL;

    char** newenvp =	NULL; /* current environment */

#if __sun
    char** newargv =	NULL;
    int    newargc =	0;
    char*  dmpath =	NULL;  /* data model specific LD_LIBRARY_PATH */
#endif    

    /* Record current data model.  The data model information is
       needed even on 32-bit only Solaris platforms, such as x86, to
       know which of LD_LIBRARY_PATH32 and LD_LIBRARY_PATH64 should be
       checked for run path setting. */

#ifdef _LP64 
    /* we're already running a 64-bit executable */
    running = 64;
#else
    running = 32;
#endif


    /*
     * At present, Solaris SPARC is the only supported platform which
     * accepts the -d32 and -d64 options since it is the only
     * supported platform that allows running either 32 or 64 bit
     * binaries.  If other such platforms are added in the future
     * (Linux on Hammer?), the #ifdef below will have to be adjusted
     * accordingly.
     */

#ifdef __sparc
    { /* open new scope to declare local variables */
      int i;

      newargv = (char **)MemAlloc((argc+1) * sizeof(*newargv));
      newargv[newargc++] = argv[0];

      /* scan for data model arguments and remove from argument list;
	 last occurrence determines desired data model */
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
	if (argv[i][0] != '-')
	  continue;
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
      newargv[newargc] = NULL;

      /* 
       * newargv has all proper arguments here
       */
    
      argc = newargc;
      argv = newargv;
    }
#endif /* end of __sparc */

    /*
     * We will set the LD_LIBRARY_PATH as follows:
     *
     *     o		$JVMPATH (directory portion only)
     *     o		$JRE/lib/$ARCH
     *     o		$JRE/../lib/$ARCH
     *
     * followed by the user's previous effective LD_LIBRARY_PATH, if
     * any.
     */

#ifdef __sun
    /* 
     * Starting in Solaris 7, ld.so.1 supports three LD_LIBRARY_PATH
     * variables:
     *
     * 1. LD_LIBRARY_PATH -- used for 32 and 64 bit searches if
     * data-model specific variables are not set.
     *
     * 2. LD_LIBRARY_PATH_64 -- overrides and replaces LD_LIBRARY_PATH
     * for 64-bit binaries.
     *
     * 3. LD_LIBRARY_PATH_32 -- overrides and replaces LD_LIBRARY_PATH
     * for 32-bit binaries.
     *
     * The vm uses LD_LIBRARY_PATH to set the java.library.path system
     * property.  To shield the vm from the complication of multiple
     * LD_LIBRARY_PATH variables, if the appropriate data model
     * specific variable is set, we will act as if LD_LIBRARY_PATH had
     * the value of the data model specific variant and the data model
     * specific variant will be unset.  Note that the variable for the
     * *wanted* data model must be used (if it is set), not simply the
     * current running data model.
     */

    switch(wanted) {
    case 0:
      if(running == 32) {
	dmpath = getenv("LD_LIBRARY_PATH_32");
	wanted = 32;
      }
      else {
	dmpath = getenv("LD_LIBRARY_PATH_64");
	wanted = 64;
      }
      break;

    case 32:
      dmpath = getenv("LD_LIBRARY_PATH_32");
      break;

    case 64:
      dmpath = getenv("LD_LIBRARY_PATH_64");
      break;
      
    default:
      fprintf(stderr, "Improper value at line %d.", __LINE__);
      exit(1); /* unknown value in wanted */
      break;
    }
    
    /* 
     * If dmpath is NULL, the relevant data model specific variable is
     * not set and normal LD_LIBRARY_PATH should be used.
     */
    if( dmpath == NULL) {
      runpath = getenv("LD_LIBRARY_PATH");
    }
    else {
      runpath = dmpath;
    }
#else


#if __linux
    /*
     * On linux, if a binary is running as sgid or suid, glibc sets
     * LD_LIBRARY_PATH to the empty string for security purposes.  (In
     * contrast, on Solaris the LD_LIBRARY_PATH variable for a
     * privileged binary does not lose its settings; but the dynamic
     * linker does apply more scrutiny to the path.) The launcher uses
     * the value of LD_LIBRARY_PATH to prevent an exec loop.
     * Therefore, if we are running sgid or suid, this function's
     * setting of LD_LIBRARY_PATH will be ineffective and we should
     * return from the function now.  Getting the right libraries to
     * be found must be handled through other mechanisms.
     */
    if((getgid() != getegid()) || (getuid() != geteuid()) )
      return;

#endif    

    /*
     * If not on Solaris, assume only a single LD_LIBRARY_PATH
     * variable.
     */
    runpath = getenv("LD_LIBRARY_PATH");
#endif /* __sun */

    /* runpath contains current effective LD_LIBRARY_PATH setting */

    jvmpath = strdup(_jvmpath);
    new_runpath = MemAlloc( ((runpath!=NULL)?strlen(runpath):0) + 
			   2*strlen(jrepath) + 2*strlen(arch) +
			   strlen(jvmpath) + 52);
    newpath = new_runpath + strlen("LD_LIBRARY_PATH=");

    /* remove the name of the .so from the JVM path */
    lastslash = strrchr(jvmpath, '/');
    if (lastslash)
      *lastslash = '\0';

    sprintf(new_runpath, "LD_LIBRARY_PATH="
	    "%s:"
	    "%s/lib/%s:"
	    "%s/../lib/%s",
	    jvmpath,
	    jrepath, arch,
	    jrepath, arch);

    /* Check to make sure that the prefix of the current path is the 
     * desired environment variable setting. */
    if (runpath != NULL && 
	strncmp(newpath, runpath, strlen(newpath))==0 &&
	(runpath[strlen(newpath)] == 0 || runpath[strlen(newpath)] == ':')
#if __sun
	&& (dmpath == NULL)    /* data model specific variables not set  */
	&& (running == wanted) /* data model does not have to be changed */
#endif
	)
      return; /* already have right LD_LIBRARY_PATH (and data model,
		 where appropriate)*/
    
    /* 
     * Place the desired environment setting onto the prefix of
     * LD_LIBRARY_PATH.  Note that this prevents any possible infinite
     * loop of execv() because we test for the prefix, above.
     */
    if (runpath != 0) {
      strcat(new_runpath, ":");
      strcat(new_runpath, runpath);
    }
    
    if( putenv(new_runpath) != 0)
      exit(1); /* problem allocating memory; LD_LIBRARY_PATH not set
		  properly */

    /* 
     * Unix systems document that they look at LD_LIBRARY_PATH only
     * once at startup, so we have to re-exec the current executable
     * to get the changed environment variable to have an effect.
     */

#if __sun
    /*
     * If dmpath is not NULL, remove the data model specific string
     * in the environment for the exec'ed child.
     * 
     * Solaris does not contain a unsetenv function; therefore, we
     * must manually remove a variable from the environment or
     * create an otherwise identical alternative environment
     * without the variable.
     *
     * At the moment, this is done by manipulating the environ array
     * directly (i.e. sliding the pointers down one array slot),
     * which is generally not a recommended practice (see section
     * 7.9 of Steven's "Advanced Programming in the UNIX(R)
     * Environment" for details).
     */

    if( dmpath != NULL) {
      int i = 0;
      int j = 0;

      int envc = 0; /* count of environment variables */
      
      char* dm_string = ((wanted==32)?
			 "LD_LIBRARY_PATH_32=":
			 "LD_LIBRARY_PATH_64=");

      while(environ[i] != NULL && strncmp(dm_string, environ[i], 19) != 0)
	i++;
	
      if(environ[i] != NULL) {
	i++;
	  
	do {
	  environ[i-1] = environ[i];
	  i++;
	} while (environ[i-1] == NULL);

      }

    }
    newenvp = environ;
#else
    newenvp = environ;
#endif


    {
      char *newexec = execname;

#ifdef __sparc
      /* 
       * If the data model is being changed, the path to the
       * executable must be updated accordingly; the executable name
       * and directory the executable resides in are separate.  In the
       * case of 32 => 64, the new bits are assumed to reside in
       * "olddir/sparcv9/execname"; in the case of 64 => 32, the bits
       * are assumed to be in "olddir/../execname".
       */

      if (wanted != 0 && running != wanted) {
	char *oldexec = strcpy(MemAlloc(strlen(execname) + 1), execname);
	char *olddir = oldexec;
	char *oldbase = strrchr(oldexec, '/');

	
	newexec = MemAlloc(strlen(execname) + 20);
	*oldbase++ = 0;
	sprintf(newexec, "%s/%s/%s", olddir, 
		((wanted==64) ? "sparcv9" : ".."), oldbase);
	argv[0] = newexec;
      } 
#endif
      
      
      execve(newexec, argv, newenvp);
      perror("execv()");

      fprintf(stderr, "Error trying to exec %s.\n", newexec);
      fprintf(stderr, "Check if file exists and permissions are set correctly.\n");

#ifdef __sparc
      if (running != wanted) {
	fprintf(stderr, "Failed to start a %d-bit JVM process from a %d bit JVM.\n",
		wanted, running);
	fprintf(stderr, "Verify all necessary J2SE components have been installed.\n" );
	fprintf(stderr,
		"(Solaris SPARC 64-bit components must be installed after 32-bit components.)\n" );
      }
#endif
    }
    exit(1);
}

void ReportErrorMessage(char * message, jboolean always) {
  if (always) {
    fprintf(stderr, "%s\n", message);
  }
}

void ReportErrorMessage2(char * format, char * string, jboolean always) {
  if (always) {
    fprintf(stderr, format, string);
    fprintf(stderr, "\n");
  }
}

/*
 * Return JNI_TRUE for an option string that has no effect but should
 * _not_ be passed on to the vm; return JNI_FALSE otherwise.  On
 * Solaris SPARC, this screening needs to be done if:
 * 1) LD_LIBRARY_PATH does _not_ need to be reset and
 * 2) -d32 or -d64 is passed to a binary with a matching data model
 *    (the exec in SetLibraryPath removes -d<n> options and points the
 *    exec to the proper binary).  When this exec is not done, these options
 *    would end up getting passed onto the vm.
 */
jboolean RemovableMachineDependentOption(char * option) {
#ifdef __sparc 
  /*
   * Unconditionally remove both -d32 and -d64 options since only
   * the last such options has an effect; e.g. 
   * java -d32 -d64 -d32 -version
   * is equivalent to 
   * java -d32 -version
   */

  if( (strcmp(option, "-d32")  == 0 ) || 
      (strcmp(option, "-d64")  == 0 ))
    return JNI_TRUE;
  else
    return JNI_FALSE;
#else /* not __sparc */
  return JNI_FALSE;
#endif
}

void PrintMachineDependentOptions() {
#ifdef __sparc
      fprintf(stdout,
	"    -d32\n"
	"                  use a 32-bit data model if available\n"
	"    -d64\n"
	"                  use a 64-bit data model if available\n");
#endif
      return;
}


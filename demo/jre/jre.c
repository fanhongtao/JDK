/*
 * @(#)jre.c	1.10 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Portable JRE Support functions.
 */

#include <string.h>
#include <stdlib.h>
#include <jni.h>
#include "jre.h"

/*
 * Exits the runtime with the specified error message.
 */
void
JRE_FatalError(JNIEnv *env, const char *msg)
{
    if ((*env)->ExceptionOccurred(env)) {
	(*env)->ExceptionDescribe(env);
    }
    (*env)->FatalError(env, msg);
}

/*
 * Parses a runtime version string. Returns 0 if the successful, otherwise
 * returns -1 if the format of the version string was invalid.
 */
jint
JRE_ParseVersion(const char *ver, char **majorp, char **minorp, char **microp)
{
    int n1 = 0, n2 = 0, n3 = 0;
    /* 4235948: jre micro version to match with registry key */
    sscanf(ver, "%*[0-9]%n.%*[0-9]%n.%*[0-9a-zA-Z_]%n", &n1, &n2, &n3);
    if (n1 == 0 || n2 == 0) {
	return -1;
    }
    if (n3 != 0) {
	if (n3 != strlen(ver)) {
	    return -1;
	}
    } else if (n2 != strlen(ver)) {
	return -1;
    }
    *majorp = JRE_Malloc(n1 + 1);
    strncpy(*majorp, ver, n1);
    *minorp = JRE_Malloc(n2 - n1);
    strncpy(*minorp, ver + n1 + 1, n2 - n1 - 1);
    if (n3 != 0) {
	*microp = JRE_Malloc(n3 - n2);
	strncpy(*microp, ver + n2 + 1, n3 - n2 - 1);
    }
    return 0;
}

/*
 * Creates a version number string from the specified major, minor, and
 * micro version numbers.
 */
char *
JRE_MakeVersion(const char *major, const char *minor, const char *micro)
{
    char *ver = 0;

    if (major != 0 && minor != 0) {
	int len = strlen(major) + strlen(minor);
	if (micro != 0) {
	    ver = JRE_Malloc(len + strlen(micro) + 3);
	    sprintf(ver, "%s.%s.%s", major, minor, micro);
	} else {
	    ver = JRE_Malloc(len + 2);
	    sprintf(ver, "%s.%s", major, minor);
	}
    }
    return ver;
}

/*
 * Allocate memory or die.
 */
void *
JRE_Malloc(size_t size)
{
    void *p = calloc(1, size);
    if (p == 0) {
	perror("calloc");
	exit(1);
    }
    return p;
}

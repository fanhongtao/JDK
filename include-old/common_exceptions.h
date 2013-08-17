/*
 * @(#)common_exceptions.h	1.17 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Common exceptions thrown from within JVM.  Add more here as you
 * find uses of SignalError. (Help the next guy avoid typos.)
 *
 * NATIVE LIBRARIES MUST NOT USE THESE ROUTINES.
 */

#ifndef _JAVASOFT_COMMON_EXCEPTIONS_H_
#define _JAVASOFT_COMMON_EXCEPTIONS_H_

/* JAVAPKG */
extern void ThrowArrayIndexOutOfBoundsException(ExecEnv *, char *);
extern void ThrowClassNotFoundException(ExecEnv *, char *);
extern void ThrowIllegalAccessError(ExecEnv *, char *);
extern void ThrowIllegalAccessException(ExecEnv *, char *);
extern void ThrowIllegalArgumentException(ExecEnv *, char *);
extern void ThrowInstantiationException(ExecEnv *, char *);
extern void ThrowInternalError(ExecEnv *, char *);
extern void ThrowInterruptedException(ExecEnv *, char *);
extern void ThrowNoClassDefFoundError(ExecEnv *, char *);
extern void ThrowNoSuchFieldError(ExecEnv *, char *);
extern void ThrowNoSuchFieldException(ExecEnv *, char *);
extern void ThrowNoSuchMethodError(ExecEnv *, char *);
extern void ThrowNoSuchMethodException(ExecEnv *, char *);
extern void ThrowNullPointerException(ExecEnv *, char *);
extern void ThrowNumberFormatException(ExecEnv *, char *);
extern void ThrowNegativeArraySizeException(ExecEnv *, char *);
extern void ThrowOutOfMemoryError(ExecEnv *, char *);
extern void ThrowStringIndexOutOfBoundsException(ExecEnv *, char *);

extern void ThrowAbstractMethodError(ExecEnv *, char *);
extern void ThrowVerifyError(ExecEnv *, char *);
extern void ThrowIncompatibleClassChangeError(ExecEnv *, char *);
extern void ThrowClassCircularityError(ExecEnv *, char *);
extern void ThrowClassFormatError(ExecEnv *, char *);
extern void ThrowUnsupportedClassVersionError(ExecEnv *, char *);
extern void ThrowUnsatisfiedLinkError(ExecEnv *, char *);
extern void ThrowStackOverflowError(ExecEnv *, char *);
extern void ThrowArrayStoreException(ExecEnv *, char *);
extern void ThrowIllegalMonitorStateException(ExecEnv *, char *);
extern void ThrowIllegalStateException(ExecEnv *, char *);
extern void ThrowCloneNotSupportedException(ExecEnv *, char *);
extern void ThrowIllegalThreadStateException(ExecEnv *, char *);
extern void ThrowClassCastException(ExecEnv *, char *);

/* JAVAIOPKG */
extern void ThrowIOException(ExecEnv *, char *);
extern void ThrowInterruptedIOException(ExecEnv *, char *);

#endif /* !_JAVASOFT_COMMON_EXCEPTIONS_H_ */

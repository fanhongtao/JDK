/*
 * @(#)hprof_error.h	1.14 04/07/27
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
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

#ifndef HPROF_ERROR_H
#define HPROF_ERROR_H

/* Macros over assert and error functions so we can capture the source loc. */

#define HPROF_BOOL(x) ((jboolean)((x)==0?JNI_FALSE:JNI_TRUE))

#define HPROF_ERROR(fatal,msg) \
    error_handler(HPROF_BOOL(fatal), JVMTI_ERROR_NONE, msg, __FILE__, __LINE__)

#define HPROF_JVMTI_ERROR(error,msg) \
    error_handler(HPROF_BOOL(error!=JVMTI_ERROR_NONE), \
	    error, msg, __FILE__, __LINE__)

#if defined(DEBUG) || !defined(NDEBUG)
    #define HPROF_ASSERT(cond) \
        (((int)(cond))?(void)0:error_assert(#cond, __FILE__, __LINE__))
#else
    #define HPROF_ASSERT(cond)
#endif

#define LOG_DUMP_MISC           0x1 /* Misc. logging info */
#define LOG_DUMP_LISTS          0x2 /* Dump tables at vm init and death */
#define LOG_CHECK_BINARY        0x4 /* If format=b, verify binary format */

#ifdef HPROF_LOGGING
    #define LOG_STDERR(args) \
        { \
            if ( gdata != NULL && (gdata->logflags & LOG_DUMP_MISC) ) { \
                (void)fprintf args ; \
            } \
        }
#else
    #define LOG_STDERR(args)
#endif

#define LOG_FORMAT(format)      "HPROF LOG: " format " [%s:%d]\n"

#define LOG1(str1)              LOG_STDERR((stderr, LOG_FORMAT("%s"), \
                                    str1, __FILE__, __LINE__ ))
#define LOG2(str1,str2)         LOG_STDERR((stderr, LOG_FORMAT("%s %s"), \
                                    str1, str2, __FILE__, __LINE__ ))
#define LOG3(str1,str2,num)     LOG_STDERR((stderr, LOG_FORMAT("%s %s 0x%x"), \
                                    str1, str2, num, __FILE__, __LINE__ ))

#define LOG(str) LOG1(str)

void       error_handler(jboolean fatal, jvmtiError error, 
		const char *message, const char *file, int line);
void       error_assert(const char *condition, const char *file, int line);
void       error_exit_process(int exit_code);
void       error_do_pause(void);
void       error_setup(void);
void       debug_message(const char * format, ...);
void       verbose_message(const char * format, ...);

#endif

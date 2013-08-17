/*
 * @(#)breakpoints.h	1.3 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
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
 * Definitions for the breakpoints, single-step and debugger events
 */

#ifndef _BREAKPOINTS_H_
#define _BREAKPOINTS_H_

#include <stdio.h>
#include <stdlib.h>

#include "bool.h"
#include "jni.h"
#include "interpreter.h"
#include "threads.h"

/*
 * This section for general use
 */

extern int get_opcode(ExecEnv *ee, unsigned char *pc);

/*
 * This section for use by a debugger 
 */

#ifdef BREAKPTS

/* Typedefs for debugger-notification hooks */
typedef void (*HandleSingleStepHook)(ExecEnv *ee, unsigned char *pc);
typedef void (*HandleBreakpointHook)(ExecEnv *ee, unsigned char *pc);
typedef void (*HandleExceptionHook)(ExecEnv *ee, unsigned char *pc,
				HObject *object);

JNIEXPORT void JNICALL
set_debugger_hooks(HandleSingleStepHook hssh,
		   HandleBreakpointHook hbh,
		   HandleExceptionHook heh);

extern void set_single_step_thread(TID tid, bool_t shouldStep);
extern bool_t set_breakpoint(ExecEnv *ee, unsigned char *pc);
extern bool_t clear_breakpoint(ExecEnv *ee, unsigned char *pc);
extern void clear_all_breakpoints(ExecEnv *ee);

extern bool_t JDB_loadclass_locked(ExecEnv *ee);
extern HArrayOfObject *get_linked_classes();

#endif BREAKPTS

/*
 * This section for use by the interpreter
 */
extern void notify_debugger_of_exception(unsigned char *pc, ExecEnv *ee, HObject *object);
extern void notify_debugger_of_single_step(unsigned char *pc, ExecEnv *ee);

#ifdef BREAKPTS
extern bool_t single_stepping; /* set ONLY by breakpoints.c */
extern int get_breakpoint_opcode(ExecEnv *ee, unsigned char *pc, bool_t notify);
extern bool_t set_breakpoint_opcode(ExecEnv *ee, unsigned char *pc, unsigned char opcode);
#endif BREAKPTS




#endif _BREAKPOINTS_H_ 

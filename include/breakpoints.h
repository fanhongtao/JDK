/*
 * @(#)breakpoints.h	1.4 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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

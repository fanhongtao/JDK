/*
 * @(#)breakpoints.h	1.18 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Definitions for the debugger events and operations.
 * Only for use when BREAKPTS is defined.
 */

#ifndef _JAVASOFT_BREAKPOINTS_H_
#define _JAVASOFT_BREAKPOINTS_H_

#ifdef BREAKPTS

#include <stdio.h>
#include <stdlib.h>

#include "bool.h"
#include "jni.h"
#include "interpreter.h"
#include "threads.h"

#define FRAME_POP_SENTINEL ((unsigned char *)1)

/* Typedefs for debugger-notification hooks */
typedef void (*HandleSingleStepHook)(ExecEnv *ee, unsigned char *pc);
typedef void (*HandleBreakpointHook)(ExecEnv *ee, unsigned char *pc);
typedef void (*HandleExceptionHook)(ExecEnv *ee, unsigned char *pc,
                                    HObject *object);

extern void set_debugger_hooks(HandleSingleStepHook hssh,
                               HandleBreakpointHook hbh,
                               HandleExceptionHook heh);

extern void set_single_step_thread(TID tid, bool_t shouldStep);
extern bool_t clear_breakpoint(ExecEnv *ee, unsigned char *pc);


/*
 * This section for use by the interpreter
 */
extern void notify_debugger_of_exception(ExecEnv *ee, unsigned char *pc,
                                         HObject *object);
extern void notify_debugger_of_exception_catch(ExecEnv *ee,
                                               unsigned char *pc,
                                               HObject *object);
extern void notify_debugger_of_single_step(ExecEnv *ee, unsigned char *pc);
extern void notify_debugger_of_field_access(ExecEnv *ee, JHandle *obj, 
                                            struct fieldblock *fb);
extern void notify_debugger_of_field_modification(ExecEnv *ee, JHandle *obj,
                                                  struct fieldblock *fb,
                                                  jvalue jval);
extern void notify_debugger_of_thread_start(ExecEnv *ee, JHandle *thread);
extern void notify_debugger_of_thread_end(ExecEnv *ee, JHandle *thread);
extern void notify_debugger_of_frame_push(ExecEnv *ee);
extern void notify_debugger_of_frame_pop(ExecEnv *ee);
extern void notify_debugger_of_class_load(ExecEnv *ee, JHandle *clazz);
extern void notify_debugger_of_class_prepare(ExecEnv *ee, JHandle *clazz);
extern void notify_debugger_of_class_unload(ExecEnv *ee, JHandle *clazz);
extern void notify_debugger_of_vm_init(ExecEnv *ee);

extern bool_t single_stepping; /* set ONLY by jvmdi.c */
extern bool_t watching_field_access; /* set ONLY by jvmdi.c */
extern bool_t watching_field_modification; /* set ONLY by jvmdi.c */

extern int get_breakpoint_opcode(ExecEnv *ee, unsigned char *pc, 
                                 bool_t notify);
extern bool_t set_breakpoint_opcode(ExecEnv *ee, unsigned char *pc, 
                                 unsigned char opcode);
#endif /* BREAKPTS */

#endif /* !_JAVASOFT_BREAKPOINTS_H_ */

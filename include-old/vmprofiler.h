/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

#ifndef _JAVASOFT_VMPROFILER_H_
#define _JAVASOFT_VMPROFILER_H_

#include "jvmpi.h"
#include "sys_api.h"

/* Support for JVMPI */

/* types */
typedef struct {
    ExecEnv *ee;                    /* ee whose current_method is compiled */
    void *code_addr;        /* virtual address of the the method */
    unsigned long code_size;        /* size of compiled method in memory */
    unsigned long lineno_table_len; /* number of lineno table entries */
    JVMPI_Lineno *lineno_table;     /* pointer to beginning of line table */
} compiled_method_t;

/* variables */
extern unsigned int jvmpi_event_flags;

/* functions called by other parts of the VM to support JVMPI */
JHandle *jvmpi_obj_reverse_map(void *obj);
void jvmpi_dump_add_trace(ExecEnv *ee);
void jvmpi_dump_add_root(JHandle *h, int kind, void *info, void *extra_info);
void jvmpi_dump_object(JHandle *h);
void jvmpi_in_dump_heap(bool_t flag);

void *jvmpi_interface(void);
void jvmpi_dump(void);
jint jvmpi_jvm_init_done(void);

void jvmpi_new_globalref(ExecEnv *ee, jobject ref, JHandle *h);
void jvmpi_delete_globalref(ExecEnv *ee, jobject ref);
void jvmpi_new_weakref(ExecEnv *ee, jobject ref, JHandle *h);
void jvmpi_delete_weakref(ExecEnv *ee, jobject ref);

void jvmpi_new_arena(void);
void jvmpi_alloc_object(ExecEnv *ee, JHandle *h);
void jvmpi_free_object(JHandle *h);
void jvmpi_move_object(void *oldobj, void *newobj);

void jvmpi_load_class(ClassClass *cb);
void jvmpi_load_class_hook(unsigned char **ptrP, unsigned char **end_ptrP,
			   void * (* malloc_f)(unsigned int));
void jvmpi_free_class(ClassClass *cb);
void jvmpi_superclass_link(ClassClass *cb, ClassClass *scb);

void jvmpi_thread_start(JHandle *t);
void jvmpi_thread_end(JHandle *t);

void jvmpi_method_entry(ExecEnv *ee, JHandle *h);
void jvmpi_method_exit(ExecEnv *ee);

void jvmpi_load_compiled_method(compiled_method_t *compiled_method_info);
void jvmpi_unload_compiled_method(struct methodblock *mb);

ClassClass * jvmpi_get_object_info(JHandle *h, int *is_array, int *size);
unsigned long jvmpi_get_heap_size(void);

void jvmpi_monitor_contended_enter(sys_thread_t *self, sys_mon_t *mid);
void jvmpi_monitor_contended_entered(sys_thread_t *self, sys_mon_t *mid);
void jvmpi_monitor_contended_exit(sys_thread_t *self, sys_mon_t *mid);

void jvmpi_monitor_wait(JHandle *obj, jlong millis);
void jvmpi_monitor_waited(JHandle *obj);

void jvmpi_gc_start(void);
void jvmpi_gc_finish(long used_objs, long used_obj_space, long total_obj_space);

void jvmpi_dump_context_lock(void);
void jvmpi_dump_context_unlock(void);

void jvmpi_trace_instr(ExecEnv *ee, unsigned char *pc, unsigned char opcode);
void jvmpi_trace_if(ExecEnv *ee, unsigned char *pc, int is_true);
void jvmpi_trace_tableswitch(ExecEnv *ee, unsigned char *pc, int key, int low, int hi);
void jvmpi_trace_lookupswitch(ExecEnv *ee,
                              unsigned char *pc,
                              int chosen_pair_index,
                              int pairs_total);

typedef struct {
    unsigned int flag;
    jint (*enable_handler)(void *arg);
    jint (*disable_handler)(void *arg);
} jvmpi_event_info_t;

extern jvmpi_event_info_t jvmpi_event_info[];

/* macros */
#define JVMPI_INVALID_CLASS ((ClassClass *)(-1))

#define JVMPI_EVENT_ENABLED    -2
#define JVMPI_EVENT_DISABLED   0

#ifdef __linux__
#define JVMPI_EVENT_IS_ENABLED(e) \
  ((e) > 32 ? (jvmpi_event_info[e].flag == (unsigned int)JVMPI_EVENT_ENABLED) : \
              (jvmpi_event_flags & (1 << e)))
#else
#define JVMPI_EVENT_IS_ENABLED(e) \
  ((e) > 32 ? (jvmpi_event_info[e].flag == JVMPI_EVENT_ENABLED) : \
              (jvmpi_event_flags & (1 << e)))
#endif

/* constants */
#define JVMPI_EVENT_PROFILING_OFF              0x00000000
#define JVMPI_EVENT_PROFILING_ON               0x80000000

/* convenient constants for the assembly loop */ 
#define JVMPI_EVENT_METHOD_ENTRY_ON \
    ((1 << JVMPI_EVENT_METHOD_ENTRY) | (1 << JVMPI_EVENT_METHOD_ENTRY2))
#define JVMPI_EVENT_METHOD_EXIT_ON      (1 << JVMPI_EVENT_METHOD_EXIT)

/* for JIT interface */
#define JVMPI_EVENT_LOAD_COMPILED_METHOD_ON \
    (1 << JVMPI_EVENT_LOAD_COMPILED_METHOD)
#define JVMPI_EVENT_UNLOAD_COMPILED_METHOD_ON \
    (1 << JVMPI_EVENT_UNLOAD_COMPILED_METHOD)

#define GC_ROOT_UNKNOWN       0xff
#define GC_ROOT_JNI_GLOBAL    0x01
#define GC_ROOT_JNI_LOCAL     0x02
#define GC_ROOT_JAVA_FRAME    0x03
#define GC_ROOT_NATIVE_STACK  0x04
#define GC_ROOT_STICKY_CLASS  0x05
#define GC_ROOT_THREAD_BLOCK  0x06
#define GC_ROOT_MONITOR_USED  0x07
#define GC_ROOT_THREAD_OBJ    0x08

#endif /* !_JAVASOFT_VMPROFILER_H_ */

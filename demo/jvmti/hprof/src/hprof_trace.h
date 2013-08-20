/*
 * @(#)hprof_trace.h	1.8 04/07/27
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

#ifndef HPROF_TRACE_H
#define HPROF_TRACE_H

void         trace_increment_all_sample_costs(jint count, jthread *threads, 
                        SerialNumber *thread_serial_nums, int depth,
			jboolean skip_init);

void         trace_get_all_current(jint count, jthread *threads, 
                        SerialNumber *thread_serial_nums, int depth,
			jboolean skip_init, TraceIndex *traces, 
			jboolean always_care);

TraceIndex   trace_get_current(jthread thread,
                        SerialNumber thread_serial_num, int depth,
                        jboolean skip_init,
                        FrameIndex *frames_buffer,
                        jvmtiFrameInfo *jframes_buffer);

void         trace_init(void);
TraceIndex   trace_find_or_create(SerialNumber thread_serial_num,
                        jint n_frames, FrameIndex *frames,
			jvmtiFrameInfo *jframes_buffer);
SerialNumber trace_get_serial_number(TraceIndex index);
void         trace_increment_cost(TraceIndex index, 
                        jint num_hits, jlong self_cost, jlong total_cost);
void         trace_list(void);
void         trace_cleanup(void);

void         trace_clear_cost(void);
void         trace_output_unmarked(JNIEnv *env);
void         trace_output_cost(JNIEnv *env, double cutoff);
void         trace_output_cost_in_prof_format(JNIEnv *env);

#endif


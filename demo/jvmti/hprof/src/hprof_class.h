/*
 * @(#)hprof_class.h	1.16 05/11/17
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
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

#ifndef HPROF_CLASS_H
#define HPROF_CLASS_H

void            class_init(void);
ClassIndex      class_find_or_create(const char *sig, LoaderIndex loader);
ClassIndex      class_create(const char *sig, LoaderIndex loader);
SerialNumber    class_get_serial_number(ClassIndex index);
StringIndex     class_get_signature(ClassIndex index);
ClassStatus     class_get_status(ClassIndex index);
void            class_add_status(ClassIndex index, ClassStatus status);
void            class_all_status_remove(ClassStatus status);
void            class_do_unloads(JNIEnv *env);
void            class_list(void);
void            class_delete_global_references(JNIEnv* env);
void            class_cleanup(void);
void            class_set_methods(ClassIndex index, const char**name,
                                const char**descr,  int count);
jmethodID       class_get_methodID(JNIEnv *env, ClassIndex index, 
                                MethodIndex mnum);
jclass          class_new_classref(JNIEnv *env, ClassIndex index, 
                                jclass classref);
void            class_delete_classref(JNIEnv *env, ClassIndex index);
jclass          class_get_class(JNIEnv *env, ClassIndex index);
void            class_set_inst_size(ClassIndex index, jint inst_size);
jint            class_get_inst_size(ClassIndex index);
void            class_set_object_index(ClassIndex index, 
				ObjectIndex object_index);
ObjectIndex     class_get_object_index(ClassIndex index);
ClassIndex      class_get_super(ClassIndex index);
void            class_set_super(ClassIndex index, ClassIndex super);
void            class_set_loader(ClassIndex index, LoaderIndex loader);
LoaderIndex     class_get_loader(ClassIndex index);
void            class_prime_system_classes(void);
jint            class_get_all_fields(JNIEnv *env, ClassIndex cnum,
				     jint *pfield_count, FieldInfo **pfield);

#endif

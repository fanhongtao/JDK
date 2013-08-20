/*
 * @(#)hprof_table.h	1.12 04/07/27
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

#ifndef HPROF_TABLE_H
#define HPROF_TABLE_H

/* Key based generic lookup table */

struct LookupTable;

typedef void (*LookupTableIterator)
                (TableIndex, void *key_ptr, int key_len, void*, void*);

struct LookupTable * table_initialize(const char *name, int size, 
                                int incr, int buckets, int esize);
int                  table_element_count(struct LookupTable *ltable);
TableIndex           table_create_entry(struct LookupTable *ltable,
                                void *key_ptr, int key_len, void *info_ptr);
TableIndex           table_find_entry(struct LookupTable *ltable,
                                void *key_ptr, int key_len);
TableIndex           table_find_or_create_entry(struct LookupTable *ltable,
                                void *key_ptr, int key_len, 
				jboolean *pnew_entry, void *info_ptr);
void                 table_free_entry(struct LookupTable *ltable,
				TableIndex index);
void                 table_cleanup(struct LookupTable *ltable,
                                LookupTableIterator func, void *arg);
void                 table_walk_items(struct LookupTable *ltable,
                                LookupTableIterator func, void *arg);
void *               table_get_info(struct LookupTable *ltable, 
                                TableIndex index);
void                 table_get_key(struct LookupTable *ltable, 
                                TableIndex index, void **pkey_ptr, 
				int *pkey_len);
void                 table_lock_enter(struct LookupTable *ltable);
void                 table_lock_exit(struct LookupTable *ltable);

#endif


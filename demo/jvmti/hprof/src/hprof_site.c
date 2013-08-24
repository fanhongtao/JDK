/*
 * @(#)hprof_site.c	1.35 05/09/30
 * 
 * Copyright (c) 2005 Sun Microsystems, Inc. All Rights Reserved.
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

/* Allocation site table. */

/*
 * Every object allocation will have a place where it was allocated,
 *  this is the purpose of the SiteIndex.
 *
 * The allocation site or SiteIndex is unique via a (class,trace) pair.
 *
 * The allocation statistics are accumulated in the SiteInfo for each
 *   site.
 *
 * This file also contains the heap iterate logic, which is closely
 *   associated with the site table, the object table, and the
 *   reference table.
 *
 */

#include "hprof.h"

typedef struct SiteKey {
    ClassIndex cnum;         /* Unique class number */
    TraceIndex trace_index;  /* Trace number */
} SiteKey;

typedef struct SiteInfo {
    int         changed;               /* Objects at this site changed? */
    unsigned    n_alloced_instances;   /* Total allocated instances */
    unsigned    n_alloced_bytes;       /* Total bytes allocated from here */
    unsigned    n_live_instances;      /* Live instances for this site. */
    unsigned    n_live_bytes;          /* Live byte count for this site. */
} SiteInfo;

typedef struct IterateInfo {
    SiteIndex * site_nums;
    int         count;
    int         changed_only;
} IterateInfo;

/* Private internal functions. */

static SiteKey*
get_pkey(SiteIndex index)
{
    void *key_ptr;
    int   key_len;

    table_get_key(gdata->site_table, index, &key_ptr, &key_len);
    HPROF_ASSERT(key_len==sizeof(SiteKey));
    HPROF_ASSERT(key_ptr!=NULL);
    return (SiteKey*)key_ptr;
}

ClassIndex
site_get_class_index(SiteIndex index) 
{
    SiteKey *pkey;

    pkey = get_pkey(index);
    return pkey->cnum;
}

TraceIndex
site_get_trace_index(SiteIndex index) 
{
    SiteKey *pkey;

    pkey = get_pkey(index);
    return pkey->trace_index;
}

static SiteInfo *
get_info(SiteIndex index)
{
    SiteInfo *info;

    info = (SiteInfo*)table_get_info(gdata->site_table, index);
    return info;
}

static void
list_item(TableIndex i, void *key_ptr, int key_len, void *info_ptr, void *arg)
{
    SiteKey         *pkey;
    jlong            n_alloced_instances;
    jlong            n_alloced_bytes;
    jlong            n_live_instances;
    jlong            n_live_bytes;

    HPROF_ASSERT(key_ptr!=NULL);
    HPROF_ASSERT(key_len==sizeof(SiteKey));
    pkey = (SiteKey*)key_ptr;
    
    if ( info_ptr != NULL ) {
        SiteInfo *info;
    
        info = (SiteInfo *)info_ptr;
        n_alloced_instances    = info->n_alloced_instances;
        n_alloced_bytes        = info->n_alloced_bytes;
        n_live_instances       = info->n_live_instances;
        n_live_bytes           = info->n_live_bytes;
    } else {
        n_alloced_instances    = 0;
        n_alloced_bytes        = 0;
        n_live_instances       = 0;
        n_live_bytes           = 0;
    }
    
    debug_message( "Site 0x%08x: class=0x%08x, trace=0x%08x, "
                          "Ninst=(%d,%d), Nbytes=(%d,%d), "
                          "Nlive=(%d,%d), NliveBytes=(%d,%d)\n",
             i, 
             pkey->cnum, 
             pkey->trace_index,
             jlong_high(n_alloced_instances), jlong_low(n_alloced_instances),
             jlong_high(n_alloced_bytes),     jlong_low(n_alloced_bytes),
             jlong_high(n_live_instances),    jlong_low(n_live_instances),
             jlong_high(n_live_bytes),        jlong_low(n_live_bytes));
}

static void
collect_iterator(TableIndex i, void *key_ptr, int key_len, void *info_ptr, void *arg)
{
    IterateInfo     *iterate;

    HPROF_ASSERT(key_ptr!=NULL);
    HPROF_ASSERT(key_len==sizeof(SiteKey));
    HPROF_ASSERT(arg!=NULL);
    iterate = (IterateInfo *)arg;

    if ( iterate->changed_only ) {
        SiteInfo *info;
        
        info = (SiteInfo *)info_ptr;
        if ( info==NULL || !info->changed ) {
            return;
        }
    }
    iterate->site_nums[iterate->count++] = i;
}

static void
mark_unchanged_iterator(TableIndex i, void *key_ptr, int key_len, void *info_ptr, void *arg)
{
    SiteInfo *info;

    HPROF_ASSERT(key_ptr!=NULL);
    HPROF_ASSERT(key_len==sizeof(SiteKey));
    
    info = (SiteInfo *)info_ptr;
    if ( info != NULL ) {
        info->changed = 0;
    }
}

static int 
qsort_compare_allocated_bytes(const void *p_site1, const void *p_site2)
{
    SiteIndex  site1;
    SiteIndex  site2;
    SiteInfo  *info1;
    SiteInfo  *info2;
    
    HPROF_ASSERT(p_site1!=NULL);
    HPROF_ASSERT(p_site2!=NULL);
    site1 = *(SiteIndex *)p_site1;
    site2 = *(SiteIndex *)p_site2;
    info1 = get_info(site1);
    info2 = get_info(site2);
    return info2->n_alloced_bytes - info1->n_alloced_bytes;
}

static int 
qsort_compare_live_bytes(const void *p_site1, const void *p_site2)
{
    SiteIndex  site1;
    SiteIndex  site2;
    SiteInfo  *info1;
    SiteInfo  *info2;
    
    HPROF_ASSERT(p_site1!=NULL);
    HPROF_ASSERT(p_site2!=NULL);
    site1 = *(SiteIndex *)p_site1;
    site2 = *(SiteIndex *)p_site2;
    info1 = get_info(site1);
    info2 = get_info(site2);
    return info2->n_live_bytes - info1->n_live_bytes;
}

static ClassIndex
find_cnum(jlong class_tag)
{
    ClassIndex cnum;

    if ( class_tag != (jlong)0 ) {
        ObjectIndex class_object_index;
	SiteIndex   site_index;
	SiteKey    *pkey;
	
	class_object_index = tag_extract(class_tag);
	site_index = object_get_site(class_object_index);
	pkey = get_pkey(site_index);
	cnum = pkey->cnum;
    } else {
	LoaderIndex loader_index;

	loader_index = loader_find_or_create(NULL,NULL);
	cnum = class_find_or_create("Ljava/lang/Object;", loader_index);
    }
    return cnum;
}

/* Setup tag on root object */
static void
setup_tag_on_root(jlong *tag_ptr, jlong class_tag, jlong size,
		  SerialNumber thread_serial_num,
                  ObjectIndex *pindex, SiteIndex *psite)
{
    ObjectIndex object_index;
    SiteIndex   object_site_index;
    
    if ( (*tag_ptr) != (jlong)0 ) {
	object_index      = tag_extract(*tag_ptr);
	if ( psite != NULL ) {
	    object_site_index = object_get_site(object_index);
	}
    } else {
	object_site_index = site_find_or_create(
				 find_cnum(class_tag), 
				 gdata->system_trace_index);
	object_index      = object_new(object_site_index, 
				 (jint)size, OBJECT_SYSTEM, 
				 thread_serial_num);
	/* Create and set the tag. */
	*tag_ptr = tag_create(object_index);
    }
    if ( pindex != NULL ) {
        *pindex = object_index;
    }
    if ( psite != NULL ) {
	*psite = object_site_index;
    }
}

/* JVMTI callback function. */
static jvmtiIterationControl JNICALL 
root_object(jvmtiHeapRootKind root_kind, jlong class_tag, jlong size, 
		jlong* tag_ptr, void *user_data)
{

   /* Only calls to Allocate, Deallocate, RawMonitorEnter & RawMonitorExit
    *   are allowed here (see the JVMTI Spec).
    */

    ObjectIndex   object_index;
    SiteIndex     object_site_index;
    
    HPROF_ASSERT(tag_ptr!=NULL);
    
    switch ( root_kind ) {
        case JVMTI_HEAP_ROOT_JNI_GLOBAL: {
		SerialNumber trace_serial_num;
		SerialNumber gref_serial_num;
		TraceIndex   trace_index;
		
                setup_tag_on_root(tag_ptr, class_tag, size,
			          gdata->unknown_thread_serial_num,
                                  &object_index, &object_site_index);
		if ( object_site_index != 0 ) {
		    SiteKey     *pkey;
		    
		    pkey = get_pkey(object_site_index);
		    trace_index = pkey->trace_index;
		} else {
		    trace_index = gdata->system_trace_index;
		}
		trace_serial_num = trace_get_serial_number(trace_index);
		gref_serial_num  = gdata->gref_serial_number_counter++;
		io_heap_root_jni_global(object_index, gref_serial_num, 
					trace_serial_num);
		break;
	    }
        case JVMTI_HEAP_ROOT_SYSTEM_CLASS: {
		char        *sig;
    
                setup_tag_on_root(tag_ptr, class_tag, size,
			          gdata->unknown_thread_serial_num,
                                  &object_index, &object_site_index);
		sig = "Unknown";
		if ( object_site_index != 0 ) {
		    SiteKey *pkey;
		    
		    pkey = get_pkey(object_site_index);
		    sig = string_get(class_get_signature(pkey->cnum));
		}
		io_heap_root_system_class(object_index, sig);
		break;
	    }
        case JVMTI_HEAP_ROOT_MONITOR: {
                setup_tag_on_root(tag_ptr, class_tag, size, 
			          gdata->unknown_thread_serial_num,
		                  &object_index, NULL);
		io_heap_root_monitor(object_index);
		break;
	    }
        case JVMTI_HEAP_ROOT_THREAD: {
		SerialNumber thread_serial_num;
		SerialNumber trace_serial_num;
		TraceIndex   trace_index;
		TlsIndex     tls_index;
		
		if ( (*tag_ptr) != (jlong)0 ) {
		    setup_tag_on_root(tag_ptr, class_tag, size, 0,
				      &object_index, &object_site_index);
		    trace_index       = site_get_trace_index(object_site_index);
		    /* Hopefully the ThreadStart event put this thread's
		     *   correct serial number on it's object.
		     */
		    thread_serial_num = object_get_thread_serial_number(object_index);
		} else {
		    /* Rare situation that a Thread object is not tagged.
		     *   Create special unique thread serial number in this
		     *   case, probably means we never saw a thread start
		     *   or thread end, or even an allocation of the thread
		     *   object.
		     */
		    thread_serial_num = gdata->thread_serial_number_counter++;
		    setup_tag_on_root(tag_ptr, class_tag, size,
			              thread_serial_num,
				      &object_index, &object_site_index);
		    trace_index = gdata->system_trace_index;
		}
                /* Get tls_index and set in_heap_dump, if we find it. */
                tls_index = tls_find(thread_serial_num);
                if ( tls_index != 0 ) {
                    tls_set_in_heap_dump(tls_index, 1);
                }
		trace_serial_num = trace_get_serial_number(trace_index);
		/* Issue thread object (must be before thread root) */
		io_heap_root_thread_object(object_index,
				 thread_serial_num, trace_serial_num);
		/* Issue thread root */
		io_heap_root_thread(object_index, thread_serial_num);
		break;
	    }
        case JVMTI_HEAP_ROOT_OTHER: {
                setup_tag_on_root(tag_ptr, class_tag, size,
			          gdata->unknown_thread_serial_num,
		                  &object_index, NULL);
		io_heap_root_unknown(object_index);
		break;
	    }
	default: {
                setup_tag_on_root(tag_ptr, class_tag, size,
			          gdata->unknown_thread_serial_num,
		                  NULL, NULL);
	        break;
	    }
    }
    return JVMTI_ITERATION_CONTINUE;
}

static SerialNumber
checkThreadSerialNumber(SerialNumber thread_serial_num)
{
    TlsIndex tls_index;

    if ( thread_serial_num == gdata->unknown_thread_serial_num ) {
        return thread_serial_num;
    }
    tls_index = tls_find(thread_serial_num);
    if ( tls_index != 0 && tls_get_in_heap_dump(tls_index) != 0 ) {
        return thread_serial_num;
    }
    return gdata->unknown_thread_serial_num;
}

/* JVMTI callback function. */
static jvmtiIterationControl JNICALL
stack_object(jvmtiHeapRootKind root_kind, 
		jlong class_tag, jlong size, jlong* tag_ptr, 
		jlong thread_tag, jint depth, jmethodID method, jint slot,
		void *user_data)
{

   /* Only calls to Allocate, Deallocate, RawMonitorEnter & RawMonitorExit
    *   are allowed here (see the JVMTI Spec).
    */

    ObjectIndex  object_index;
    SerialNumber thread_serial_num;

    HPROF_ASSERT(tag_ptr!=NULL);
    if ( (*tag_ptr) != (jlong)0 ) {
	object_index = tag_extract(*tag_ptr);
	thread_serial_num = object_get_thread_serial_number(object_index);
        thread_serial_num = checkThreadSerialNumber(thread_serial_num);
    } else {
	SiteIndex site_index;
	
        site_index = site_find_or_create(find_cnum(class_tag), 
				gdata->system_trace_index);
	if ( thread_tag != (jlong)0 ) {
	    ObjectIndex thread_object_index;

	    thread_object_index = tag_extract(thread_tag);
	    thread_serial_num = 
	           object_get_thread_serial_number(thread_object_index);
            thread_serial_num = checkThreadSerialNumber(thread_serial_num);
	} else {
	    thread_serial_num = gdata->unknown_thread_serial_num;
	}
	object_index = object_new(site_index, (jint)size, OBJECT_SYSTEM,
			    thread_serial_num);
	/* Create and set the tag. */
	*tag_ptr = tag_create(object_index);
    }

    HPROF_ASSERT(thread_serial_num!=0);
    HPROF_ASSERT(object_index!=0);
    switch ( root_kind ) {
        case JVMTI_HEAP_ROOT_STACK_LOCAL:
	    io_heap_root_java_frame(object_index, thread_serial_num, depth);
	    break;
        case JVMTI_HEAP_ROOT_JNI_LOCAL:
	    io_heap_root_jni_local(object_index, thread_serial_num, depth);
	    break;
	default:
	    break;
    }
    return JVMTI_ITERATION_CONTINUE;
}

/* JVMTI callback function. */
static jvmtiIterationControl JNICALL
reference_object(jvmtiObjectReferenceKind reference_kind, 
		jlong class_tag, jlong size, jlong* tag_ptr, 
		jlong referrer_tag, jint referrer_index, void *user_data)
{

   /* Only calls to Allocate, Deallocate, RawMonitorEnter & RawMonitorExit
    *   are allowed here (see the JVMTI Spec).
    */

    RefIndex      ref_index;
    RefIndex      prev_ref_index;
    ObjectIndex   referrer_object_index;
    ObjectIndex   object_index;
    jlong         object_tag;

    HPROF_ASSERT(tag_ptr!=NULL);
    HPROF_ASSERT(referrer_tag!=(jlong)0);
    
    if ( referrer_tag != (jlong)0 ) {
	referrer_object_index = tag_extract(referrer_tag);
    } else {
        return JVMTI_ITERATION_CONTINUE;
    }
    
    object_tag = *tag_ptr;
    if ( object_tag != (jlong)0 ) {
        object_index = tag_extract(object_tag);
    } else {
        SiteIndex site_index;
	
        site_index = site_find_or_create(find_cnum(class_tag), 
			gdata->system_trace_index);
	object_index = object_new(site_index, (jint)size, OBJECT_SYSTEM,
				gdata->unknown_thread_serial_num);
	object_tag = tag_create(object_index);
	*tag_ptr   = object_tag;
    }

    /* Save reference information */
    prev_ref_index = object_get_references(referrer_object_index);
    ref_index = reference_new(prev_ref_index, reference_kind,
		    class_tag, size, object_tag, referrer_index);
    object_set_references(referrer_object_index, ref_index);
    
    return JVMTI_ITERATION_CONTINUE;
}

/* External interfaces */

SiteIndex
site_find_or_create(ClassIndex cnum, TraceIndex trace_index)
{
    SiteIndex index;
    static SiteKey  empty_key;
    SiteKey   key;
    
    key = empty_key;
    HPROF_ASSERT(cnum!=0);
    HPROF_ASSERT(trace_index!=0);
    key.cnum        = cnum;
    key.trace_index = trace_index;
    index = table_find_or_create_entry(gdata->site_table, 
			    &key, (int)sizeof(key), NULL, NULL);
    return index;
}

void
site_init(void)
{
    HPROF_ASSERT(gdata->site_table==NULL);
    gdata->site_table = table_initialize("Site",
                            1024, 1024, 511, (int)sizeof(SiteInfo));
}

void
site_list(void)
{
    debug_message( 
        "--------------------- Site Table ------------------------\n");
    table_walk_items(gdata->site_table, &list_item, NULL);
    debug_message(
        "----------------------------------------------------------\n");
}

void
site_cleanup(void)
{
    table_cleanup(gdata->site_table, NULL, NULL);
    gdata->site_table = NULL;
}

void
site_update_stats(SiteIndex index, jint size, jint hits)
{
    SiteInfo *info;
    
    table_lock_enter(gdata->site_table); {
	info = get_info(index);
	
	info->n_live_instances          += hits;
	info->n_live_bytes              += size;
	info->changed                   = 1;
	
	gdata->total_live_bytes         += size;
	gdata->total_live_instances     += hits;
	 
	if ( size > 0 ) {
	    info->n_alloced_instances   += hits;
	    info->n_alloced_bytes       += size;
	    gdata->total_alloced_bytes = 
		jlong_add(gdata->total_alloced_bytes, jint_to_jlong(size));
	    gdata->total_alloced_instances = 
		jlong_add(gdata->total_alloced_instances, jint_to_jlong(hits));
	}
    } table_lock_exit(gdata->site_table);
}

/* Output allocation sites, up to the given cut-off point, and according
 * to the given flags:
 *
 *      SITE_DUMP_INCREMENTAL only dump what's changed since last dump.
 *      SITE_SORT_BY_ALLOC    sort sites by total allocation rather
 *                                  than live data.
 *      SITE_FORCE_GC         force a GC before the site dump.
 */

void 
site_write(JNIEnv *env, int flags, double cutoff)
{
    HPROF_ASSERT(gdata->site_table!=NULL);
    LOG3("site_write", "flags", flags);
    
    if (flags & SITE_FORCE_GC) {
        runGC();
    }

    HPROF_ASSERT(gdata->total_live_bytes!=0);

    rawMonitorEnter(gdata->data_access_lock); {
        
        IterateInfo     iterate;
        int             site_table_size;
        double          accum_percent;
        void *          comment_str;
        int             i;
        int             cutoff_count;
	int             nbytes;

        accum_percent = 0;
        site_table_size = table_element_count(gdata->site_table);
        
        (void)memset(&iterate, 0, sizeof(iterate));
	nbytes            = site_table_size * (int)sizeof(SiteIndex);
	if ( nbytes > 0 ) {
	    iterate.site_nums = HPROF_MALLOC(nbytes);
	    (void)memset(iterate.site_nums, 0, nbytes);
	}
        iterate.count   = 0;
        iterate.changed_only = flags & SITE_DUMP_INCREMENTAL;
        table_walk_items(gdata->site_table, &collect_iterator, &iterate);

        site_table_size = iterate.count;
        
        if (flags & SITE_SORT_BY_ALLOC) {
            comment_str = "allocated bytes";
            qsort(iterate.site_nums, site_table_size, sizeof(SiteIndex), 
                    &qsort_compare_allocated_bytes);
        } else {
            comment_str = "live bytes";
            qsort(iterate.site_nums, site_table_size, sizeof(SiteIndex), 
                    &qsort_compare_live_bytes); 
        }

        trace_output_unmarked(env);
        
        cutoff_count = 0;
        for (i = 0; i < site_table_size; i++) {
            SiteInfo   *info;
            SiteIndex   index;
            double      ratio;
            
            index= iterate.site_nums[i];
            HPROF_ASSERT(index!=0);
            info        = get_info(index);
            ratio       = (double)info->n_live_bytes / (double)gdata->total_live_bytes;
            if (ratio < cutoff) {
                break;
            }
            cutoff_count++;
        }
        
        io_write_sites_header(  comment_str,
                                flags,
                                cutoff,
                                gdata->total_live_bytes,
                                gdata->total_live_instances,
                                gdata->total_alloced_bytes,
                                gdata->total_alloced_instances,
                                cutoff_count);
        
        for (i = 0; i < cutoff_count; i++) {
            SiteInfo     *info;
            SiteKey      *pkey;
            SiteIndex     index;
            char         *class_signature;
            double        ratio;
            
            index = iterate.site_nums[i];
            pkey         = get_pkey(index);
            info         = get_info(index);
            
            ratio       = (double)info->n_live_bytes / (double)gdata->total_live_bytes;
            accum_percent += ratio;
            
            class_signature  = string_get(class_get_signature(pkey->cnum));
            
            io_write_sites_elem(i + 1,
                                ratio,
                                accum_percent,
                                class_signature,
                                class_get_serial_number(pkey->cnum),
                                trace_get_serial_number(pkey->trace_index),
                                info->n_live_bytes,
                                info->n_live_instances,
                                info->n_alloced_bytes,
                                info->n_alloced_instances);
        }
        
        io_write_sites_footer();

        table_walk_items(gdata->site_table, &mark_unchanged_iterator, NULL);

	if ( iterate.site_nums != NULL ) {
	    HPROF_FREE(iterate.site_nums);
        }

    } rawMonitorExit(gdata->data_access_lock);
}

void
site_heapdump(JNIEnv *env)
{
   
    rawMonitorEnter(gdata->data_access_lock); {
	
	struct { int i; } user_data; /* FIXUP */

	user_data.i = 0;

	/* Remove class dumped status, all classes must be dumped */
	class_all_status_remove(CLASS_DUMPED);

	/* Clear in_heap_dump flag */
	tls_clear_in_heap_dump();

	/* Dump the last thread traces and get the lists back we need */
	tls_dump_traces(env);
       
	/* Write header for heap dump */
	io_heap_header(gdata->total_live_instances, gdata->total_live_bytes);

	/* Setup a clean reference table */
	reference_init();
	
	/* Walk over all reachable objects and dump out roots */
	gdata->gref_serial_number_counter = gdata->gref_serial_number_start;

	/* Issue thread object for fake non-existent unknown thread
	 *   just in case someone refers to it. Real threads are handled
	 *   during iterate over reachable objects.
	 */
	io_heap_root_thread_object(0, gdata->unknown_thread_serial_num, 
			trace_get_serial_number(gdata->system_trace_index));

	/* Iterate over heap and get the real stuff */
	iterateOverReachableObjects(&root_object, &stack_object, 
			&reference_object, (void*)&user_data);

	/* Process reference information. */
	object_reference_dump(env);
	object_clear_references();
        reference_cleanup();

	/* Dump the last thread traces and get the lists back we need */
	tls_dump_traces(env);

	/* Write out footer for heap dump */
	io_heap_footer();
	
    } rawMonitorExit(gdata->data_access_lock);
}


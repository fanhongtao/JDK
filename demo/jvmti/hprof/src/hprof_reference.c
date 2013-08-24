/*
 * @(#)hprof_reference.c	1.33 05/09/30
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

/* Object references table (used in hprof_object.c). */

/* 
 * This table is used by the object table to store object reference
 *   information obtained from iterations over the heap (see hprof_site.c).
 *
 * These table elements have no Key so they are never looked up, use of the
 *   LookupTable was just an easy way to handle a unbounded table of
 *   elements. The object table (see hprof_object.c) will completely
 *   free this table after each heap dump or after processing the references.
 *
 * The hprof format required this accumulation of all heap iteration
 *   references from objects in order to compose an hprof records for it.
 *   essentially this is just a saving of the information returned for
 *   references by the JVMTI iterate heap interface.
 *
 * This file contains detailed understandings of how an hprof CLASS
 *   and INSTANCE dump is constructed, most of this is derived from the
 *   original hprof code, but some has been derived by reading the HAT
 *   code that accepts this format.
 *
 * The primitive data is still missing from these hprof dumps.
 *
 */

#include "hprof.h"

typedef struct RefInfo {
    jlong                     class_tag;
    jlong                     size;
    jlong                     object_tag;
    jvmtiObjectReferenceKind  kind;
    jint                      element_index;
    RefIndex                  next;
} RefInfo;

/* Private internal functions. */

static RefInfo *
get_info(RefIndex index)
{
    RefInfo *info;

    info = (RefInfo*)table_get_info(gdata->reference_table, index);
    return info;
}

static ObjectIndex
tag_to_object_index(jlong tag)
{
    ObjectIndex object_index;
    
    object_index = 0;
    if ( tag != (jlong)0 ) {
	object_index = tag_extract(tag);
    }
    return object_index;
}

/* Walk all references for an ObjectIndex and construct the hprof CLASS dump. */
static void
dump_class_and_supers(JNIEnv *env, ObjectIndex object_index, RefIndex list)
{
    SiteIndex    site_index;
    SerialNumber trace_serial_num;
    RefIndex     index;
    ClassIndex   super_cnum;
    ObjectIndex  super_index;
    LoaderIndex  loader_index;
    ObjectIndex  signers_index;
    ObjectIndex  domain_index;
    FieldInfo   *fields;
    jvalue      *fvalues;
    jint         n_fields;
    jlong        size;
    ClassIndex   cnum;
    char        *sig;
    ObjectKind   kind;
    TraceIndex   trace_index;
    Stack       *cpool_values;
    ConstantPoolValue *cpool;
    jint         cpool_count;
    jboolean     skip_fields;

    HPROF_ASSERT(object_index!=0);
    kind        = object_get_kind(object_index);
    if ( kind != OBJECT_CLASS ) {
	return;
    }
    site_index 	= object_get_site(object_index);
    HPROF_ASSERT(site_index!=0);
    cnum        = site_get_class_index(site_index);
    HPROF_ASSERT(cnum!=0);
    if ( class_get_status(cnum) & CLASS_DUMPED ) {
	return;
    }
    class_add_status(cnum, CLASS_DUMPED);
    size        = (jlong)object_get_size(object_index);

    super_index = 0;
    super_cnum  = class_get_super(cnum);
    if ( super_cnum != 0 ) {
	super_index  = class_get_object_index(super_cnum);
	if ( super_index != 0 ) {
	    dump_class_and_supers(env, super_index, 
			object_get_references(super_index));
	}
    }
    trace_index      = site_get_trace_index(site_index);
    HPROF_ASSERT(trace_index!=0);
    trace_serial_num = trace_get_serial_number(trace_index);
    sig              = string_get(class_get_signature(cnum));
    
    n_fields     = 0;
    fields       = NULL;
    fvalues      = NULL;
    skip_fields  = JNI_TRUE;
    if ( class_get_all_fields(env, cnum, &n_fields, &fields) == 0 ) {
        if ( n_fields > 0 ) {
            skip_fields  = JNI_FALSE;
	    fvalues      = (jvalue*)HPROF_MALLOC(n_fields*(int)sizeof(jvalue));
            (void)memset(fvalues, 0, n_fields*(int)sizeof(jvalue));
        }
    }
    
    /* We use a Stack just because it will automatically expand as needed */
    cpool_values = stack_init(16, 16, sizeof(ConstantPoolValue));
    cpool = NULL;
    cpool_count = 0;
    
    loader_index     = class_get_loader(cnum);
    signers_index    = 0;
    domain_index     = 0;

    index      = list;
    while ( index != 0 ) {
	RefInfo    *info;

	info = get_info(index);

	/* Process reference objects, many not used right now. */
	switch ( info->kind ) {
	    case JVMTI_REFERENCE_STATIC_FIELD:
		/* If the class_tag is 0, it is possible for 
		 *    info->element_index to be >= n_fields
		 *    and when this happens we just skip this field ref
		 *    for now. We probably have a java.lang.Object class
		 *    with n_fields==0, which is probably the wrong class.
		 */
		if (info->class_tag == (jlong)0 || skip_fields == JNI_TRUE ) {
		    break;
		}
		HPROF_ASSERT(info->element_index < n_fields);
		if (info->element_index < n_fields) {
	            ObjectIndex field_object_index;
		
		    /* Field index is referrer_index from referrer_tag */
		    field_object_index = tag_to_object_index(info->object_tag);
		    fvalues[info->element_index].i = field_object_index;
		}
		break;
	    case JVMTI_REFERENCE_CONSTANT_POOL: {
		ConstantPoolValue cpv;
		ObjectIndex       cp_object_index;
		SiteIndex         cp_site_index;
		ClassIndex        cp_cnum;
	        
		cp_object_index = tag_to_object_index(info->object_tag);
                HPROF_ASSERT(cp_object_index!=0);
                cp_site_index = object_get_site(cp_object_index);
                HPROF_ASSERT(cp_site_index!=0);
                cp_cnum = site_get_class_index(cp_site_index);
		cpv.constant_pool_index = info->element_index;
		cpv.sig_index = class_get_signature(cp_cnum); 
		cpv.value.i = cp_object_index;
		stack_push(cpool_values, (void*)&cpv);
		cpool_count++;
		break;
		}
	    default:
		break;
	}
	index = info->next;
    }

    /* FIXUP: Fill rest of static primitive fields? If requested? */
    /*   Use: value = getStaticFieldValue(env, klass, field, field_sig); ? */

    HPROF_ASSERT(cpool_count==stack_depth(cpool_values));
    if ( cpool_count > 0 ) {
	cpool = (ConstantPoolValue*)stack_element(cpool_values, 0);
    }
    io_heap_class_dump(cnum, sig, object_index, trace_serial_num,
	    super_index, loader_index, signers_index, domain_index,
	    (jint)size, cpool_count, cpool, n_fields, fields, fvalues);

    stack_term(cpool_values);
    if ( fvalues != NULL ) {
	HPROF_FREE(fvalues);
    }
}

/* Walk all references for an ObjectIndex and construct the hprof INST dump. */
static void
dump_instance(JNIEnv *env, ObjectIndex object_index, RefIndex list)
{
    SiteIndex    site_index;
    SerialNumber trace_serial_num;
    RefIndex     index;
    ObjectIndex  class_index;
    jlong        size;
    ClassIndex   cnum;
    char        *sig;
    jint         num_elements;
    jvalue      *values;
    FieldInfo   *fields;
    jvalue      *fvalues;
    jint         n_fields;
    ObjectKind   kind;
    TraceIndex   trace_index;
    jboolean     skip_fields;

    HPROF_ASSERT(object_index!=0);
    kind        = object_get_kind(object_index);
    if ( kind == OBJECT_CLASS ) {
	return;
    }
    site_index 	= object_get_site(object_index);
    HPROF_ASSERT(site_index!=0);
    cnum             = site_get_class_index(site_index);
    HPROF_ASSERT(cnum!=0);
    size             = (jlong)object_get_size(object_index);
    trace_index      = site_get_trace_index(site_index);
    HPROF_ASSERT(trace_index!=0);
    trace_serial_num = trace_get_serial_number(trace_index);
    sig              = string_get(class_get_signature(cnum));
    class_index      = class_get_object_index(cnum);
	
    values       = NULL;
    num_elements = 0;
    
    n_fields     = 0;
    fields       = NULL;
    fvalues      = NULL;
    
    index = list;
    
    skip_fields  = JNI_TRUE;
    if ( sig[0] != JVM_SIGNATURE_ARRAY ) {
	if ( class_get_all_fields(env, cnum, &n_fields, &fields) == 0 ) {
	    if ( n_fields > 0 ) {
                skip_fields  = JNI_FALSE;
		fvalues = (jvalue*)HPROF_MALLOC(n_fields*(int)sizeof(jvalue));
		(void)memset(fvalues, 0, n_fields*(int)sizeof(jvalue));
	    }
	}
    }

    while ( index != 0 ) {
	ObjectIndex field_object_index;
	RefInfo *info;

	info = get_info(index);

	/* Process reference objects, many not used right now. */
	switch ( info->kind ) {
	    case JVMTI_REFERENCE_FIELD:
		/* If the class_tag is 0, it is possible for 
		 *    info->element_index to be >= n_fields
		 *    and when this happens we just skip this field ref
		 *    for now. We probably have a java.lang.Object class
		 *    with n_fields==0, which is probably the wrong class.
		 */
		if (info->class_tag == (jlong)0 || skip_fields == JNI_TRUE ) {
		    break;
		}
		HPROF_ASSERT(info->element_index < n_fields);
		if (info->element_index < n_fields) {
		    /* Field index is referrer_index from referrer_tag */
		    field_object_index = tag_to_object_index(info->object_tag);
		    fvalues[info->element_index].i = field_object_index;
		}
		break;
	    case JVMTI_REFERENCE_ARRAY_ELEMENT:
		/* Array element index is referrer_index in referrer_tag */
		if ( num_elements <= info->element_index  ) {
		    int nbytes;
		    
		    if ( values == NULL ) {
		        num_elements = info->element_index + 1;
			nbytes = num_elements*(int)sizeof(jvalue);
			values = (jvalue*)HPROF_MALLOC(nbytes);
			(void)memset(values, 0, nbytes);
		    } else {
		        void *new_values;
			int   new_size;
			int   obytes;

			obytes = num_elements*(int)sizeof(jvalue);
			new_size = info->element_index + 1;
			nbytes = new_size*(int)sizeof(jvalue);
			new_values = (jvalue*)HPROF_MALLOC(nbytes);
		        (void)memcpy(new_values, values, obytes);
			(void)memset(((char*)new_values)+obytes, 0, 
						nbytes-obytes);
			HPROF_FREE(values);
			num_elements = new_size;
			values =  new_values;
		    }
		}
                field_object_index = tag_to_object_index(info->object_tag);
		HPROF_ASSERT(values[info->element_index].i==0);
		values[info->element_index].i = field_object_index;
		break;
	    default:
		break;
	}
	index = info->next;
    }
    
    if ( sig[0] == JVM_SIGNATURE_ARRAY ) {
	/* FIXUP: Fill primitive arrays? If requested? */
	switch ( sig[1] ) {
	    case JVM_SIGNATURE_CLASS:
	    case JVM_SIGNATURE_ENUM:
	    case JVM_SIGNATURE_ARRAY:
		io_heap_object_array(object_index, trace_serial_num,
			(jint)size, num_elements, class_index, values, sig);
		break;
	    case JVM_SIGNATURE_BYTE:
	    case JVM_SIGNATURE_BOOLEAN:
		io_heap_prim_array(object_index, (jint)size, 
			trace_serial_num, num_elements, sig, values);
		break;
	    case JVM_SIGNATURE_CHAR:
	    case JVM_SIGNATURE_SHORT:
		io_heap_prim_array(object_index, (jint)size, 
			trace_serial_num, num_elements, sig, values);
		break;
	    case JVM_SIGNATURE_INT:
	    case JVM_SIGNATURE_FLOAT:
		io_heap_prim_array(object_index, (jint)size, 
			    trace_serial_num, num_elements, sig, values);
		break;
	    case JVM_SIGNATURE_DOUBLE:
	    case JVM_SIGNATURE_LONG:
		io_heap_prim_array(object_index, (jint)size, 
			trace_serial_num, num_elements, sig, values);
		break;
	    default:
		HPROF_ASSERT(0);
		break;
	}
    } else { 
	/* FIXUP: Fill rest of primitive fields? If requested? */
        io_heap_instance_dump(cnum, object_index, trace_serial_num,
		    class_index, (jint)size, sig, fields, fvalues, n_fields);
    } 
    if ( values != NULL ) {
	HPROF_FREE(values);
    }
    if ( fvalues != NULL ) {
	HPROF_FREE(fvalues);
    }
}

/* External interfaces. */

void
reference_init(void)
{
    HPROF_ASSERT(gdata->reference_table==NULL);
    gdata->reference_table = table_initialize("Ref", 2048, 4096, 0,
                            (int)sizeof(RefInfo));
}

RefIndex
reference_new(RefIndex next, 
		jvmtiObjectReferenceKind kind,
		jlong class_tag, jlong size, jlong object_tag,
		jint element_index)
{
    RefIndex index;
    static RefInfo  empty_info;
    RefInfo  info;
    
    info                = empty_info;
    info.kind 		= kind;
    info.element_index  = element_index;
    info.class_tag 	= class_tag;
    info.size 		= size;
    info.object_tag 	= object_tag;
    info.next 		= next;
    index = table_create_entry(gdata->reference_table, NULL, 0, (void*)&info);
    return index;
}

void
reference_cleanup(void)
{
    if ( gdata->reference_table == NULL ) {
	return;
    }
    table_cleanup(gdata->reference_table, NULL, NULL);
    gdata->reference_table = NULL;
}

void     
reference_dump_instance(JNIEnv *env, ObjectIndex object_index, RefIndex list)
{
    dump_instance(env, object_index, list);
}

void     
reference_dump_class(JNIEnv *env, ObjectIndex object_index, RefIndex list)
{
    dump_class_and_supers(env, object_index, list);
}



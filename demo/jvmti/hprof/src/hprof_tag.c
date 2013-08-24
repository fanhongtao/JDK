/*
 * @(#)hprof_tag.c	1.12 05/09/30
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

/* JVMTI tag definitions. */

/*
 * JVMTI tags are jlongs (64 bits) and how the hprof information is
 *   turned into a tag and/or extracted from a tag is here.
 *
 * Currently a special TAG_CHECK is placed in the high order 32 bits of
 *    the tag as a check.
 * 
 */

#include "hprof.h"

#define TAG_CHECK 0xfad4dead

jlong
tag_create(ObjectIndex object_index)
{
    jlong               tag;
    
    HPROF_ASSERT(object_index != 0);
    tag = TAG_CHECK;
    tag = (tag << 32) | object_index;
    return tag;
}

ObjectIndex
tag_extract(jlong tag)
{
    HPROF_ASSERT(tag != (jlong)0);
    if ( ((tag >> 32) & 0xFFFFFFFF) != TAG_CHECK) {
	HPROF_ERROR(JNI_TRUE, "JVMTI tag value is not 0 and missing TAG_CHECK");
    }
    return  (ObjectIndex)(tag & 0xFFFFFFFF);
}

/* Tag a new jobject */
void
tag_new_object(jobject object, ObjectKind kind, SerialNumber thread_serial_num,
		jint size, SiteIndex site_index)
{
    ObjectIndex  object_index;
    jlong        tag;
  
    HPROF_ASSERT(site_index!=0);
    /* New object for this site. */
    object_index = object_new(site_index, size, kind, thread_serial_num);
    /* Create and set the tag. */
    tag = tag_create(object_index);
    setTag(object, tag);
    LOG3("tag_new_object", "tag", (int)tag);
}

/* Tag a jclass jobject if it hasn't been tagged. */
void
tag_class(JNIEnv *env, jclass klass, ClassIndex cnum, 
		SerialNumber thread_serial_num, SiteIndex site_index)
{
    ObjectIndex object_index;
  
    /* If the ClassIndex has an ObjectIndex, then we have tagged it. */
    object_index = class_get_object_index(cnum);
    if ( object_index == 0 ) {
        jint        size;
        jlong        tag;
        
	HPROF_ASSERT(site_index!=0);
	
	/* If we don't know the size of a java.lang.Class object, get it */
	size =  gdata->system_class_size;
	if ( size == 0 ) {
	    size  = (jint)getObjectSize(klass);
	    gdata->system_class_size = size;
	}
	
	/* Tag this java.lang.Class object if it hasn't been already */
	tag = getTag(klass);
	if ( tag == (jlong)0 ) {
	    /* New object for this site. */
	    object_index = object_new(site_index, size, OBJECT_CLASS,
					thread_serial_num);
	    /* Create and set the tag. */
	    tag = tag_create(object_index);
	    setTag(klass, tag);
	} else {
	    /* Get the ObjectIndex from the tag. */
	    object_index = tag_extract(tag);
	}
        
	/* Record this object index in the Class table */
	class_set_object_index(cnum, object_index);
    }
}


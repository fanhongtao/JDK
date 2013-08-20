/*
 * @(#)hprof_io.c	1.48 04/07/27
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

/* All I/O functionality for hprof. */

/* 
 * The hprof agent has many forms of output:
 *
 *   format=b   gdata->output_format=='b'
 *      Binary format. Defined below. This is used by HAT.
 *      This is NOT the same format as emitted by JVMPI.
 *
 *   format=a   gdata->output_format=='a'
 *      Ascii format. Not exactly an ascii representation of the binary format.
 *
 * And many forms of dumps:
 *
 *    heap=dump
 *        A large dump that in this implementation is written to a separate
 *        file first before being placed in the output file. Several reasons,
 *        the binary form needs a byte count of the length in the header, and
 *        references in this dump to other items need to be emitted first.
 *        So it's two pass, or use a temp file and copy.
 *    heap=sites
 *        Dumps the sites in the order of most allocations.
 *    cpu=samples
 *        Dumps the traces in order of most hits
 *    cpu=times
 *        Dumps the traces in the order of most time spent there.
 *    cpu=old   (format=a only)
 *        Dumps out an older form of cpu output (old -prof format)
 *    monitor=y (format=a only)
 *        Dumps out a list of monitors in order of most contended.
 *
 * This file also includes a binary format check function that will read
 *   back in the hprof binary format and verify the syntax looks correct.
 *
 * WARNING: Besides the comments below, there is little format spec on this,
 *          however see: 
 *           http://java.sun.com/j2se/1.4.2/docs/guide/jvmpi/jvmpi.html#hprof
 */

#include "hprof.h"
#include "hprof_ioname.h"

/* HPROF version */

#define HPROF_HEADER "JAVA PROFILE 1.0.1" /* Breaks test b4500875 if changed */

/* -------------------------------------------------------------------- */
/* -------------------------------------------------------------------- */
/* -------------------------------------------------------------------- */

/*
 * hprof binary format: (result either written to a file or sent over
 * the network).
 * 
 * WARNING: This format is still under development, and is subject to
 * change without notice.
 *
 *  header    "JAVA PROFILE 1.0.1" (0-terminated)
 *  u4        size of identifiers. Identifiers are used to represent
 *            UTF8 strings, objects, stack traces, etc. They usually
 *            have the same size as host pointers. For example, on
 *            Solaris and Win32, the size is 4.
 * u4         high word 
 * u4         low word    number of milliseconds since 0:00 GMT, 1/1/70
 * [record]*  a sequence of records.
 */

/*
 * Record format:
 *
 * u1         a TAG denoting the type of the record
 * u4         number of *microseconds* since the time stamp in the
 *            header. (wraps around in a little more than an hour)
 * u4         number of bytes *remaining* in the record. Note that
 *            this number excludes the tag and the length field itself.
 * [u1]*      BODY of the record (a sequence of bytes)
 */

/*
 * The following TAGs are supported:
 *
 * TAG           BODY       notes
 *----------------------------------------------------------
 * HPROF_UTF8               a UTF8-encoded name  
 *
 *               id         name ID
 *               [u1]*      UTF8 characters (no trailing zero)
 *
 * HPROF_LOAD_CLASS         a newly loaded class
 *
 *                u4        class serial number (> 0)
 *                id        class object ID
 *                u4        stack trace serial number
 *                id        class name ID
 *
 * HPROF_UNLOAD_CLASS       an unloading class
 *
 *                u4        class serial_number
 *
 * HPROF_FRAME              a Java stack frame
 *
 *                id        stack frame ID
 *                id        method name ID
 *                id        method signature ID
 *                id        source file name ID
 *                u4        class serial number
 *                i4        line number. >0: normal
 *                                       -1: unknown
 *                                       -2: compiled method
 *                                       -3: native method
 *
 * HPROF_TRACE              a Java stack trace
 *
 *               u4         stack trace serial number
 *               u4         thread serial number
 *               u4         number of frames
 *               [id]*      stack frame IDs
 *
 *
 * HPROF_ALLOC_SITES        a set of heap allocation sites, obtained after GC
 *
 *               u2         flags 0x0001: incremental vs. complete
 *                                0x0002: sorted by allocation vs. live
 *                                0x0004: whether to force a GC
 *               u4         cutoff ratio
 *               u4         total live bytes
 *               u4         total live instances
 *               u8         total bytes allocated
 *               u8         total instances allocated
 *               u4         number of sites that follow
 *               [u1        is_array: 0:  normal object
 *                                    2:  object array
 *                                    4:  boolean array
 *                                    5:  char array
 *                                    6:  float array
 *                                    7:  double array
 *                                    8:  byte array
 *                                    9:  short array
 *                                    10: int array
 *                                    11: long array
 *                u4        class serial number (may be zero during startup)
 *                u4        stack trace serial number
 *                u4        number of bytes alive
 *                u4        number of instances alive
 *                u4        number of bytes allocated
 *                u4]*      number of instance allocated
 *
 * HPROF_START_THREAD       a newly started thread.
 *
 *               u4         thread serial number (> 0)
 *               id         thread object ID
 *               u4         stack trace serial number
 *               id         thread name ID
 *               id         thread group name ID
 *               id         thread group parent name ID
 *
 * HPROF_END_THREAD         a terminating thread. 
 *
 *               u4         thread serial number
 *
 * HPROF_HEAP_SUMMARY       heap summary
 *
 *               u4         total live bytes
 *               u4         total live instances
 *               u8         total bytes allocated
 *               u8         total instances allocated
 *
 * HPROF_HEAP_DUMP          denote a heap dump
 *
 *               [heap dump sub-records]*
 *
 *                          There are four kinds of heap dump sub-records:
 *
 *               u1         sub-record type
 *
 *               HPROF_GC_ROOT_UNKNOWN         unknown root
 *
 *                          id         object ID
 *
 *               HPROF_GC_ROOT_THREAD_OBJ      thread object
 *
 *                          id         thread object ID  (may be 0 for a
 *                                     thread newly attached through JNI)
 *                          u4         thread sequence number
 *                          u4         stack trace sequence number
 *
 *               HPROF_GC_ROOT_JNI_GLOBAL      JNI global ref root
 *
 *                          id         object ID
 *                          id         JNI global ref ID
 *
 *               HPROF_GC_ROOT_JNI_LOCAL       JNI local ref
 *
 *                          id         object ID
 *                          u4         thread serial number
 *                          u4         frame # in stack trace (-1 for empty)
 *
 *               HPROF_GC_ROOT_JAVA_FRAME      Java stack frame
 *
 *                          id         object ID
 *                          u4         thread serial number
 *                          u4         frame # in stack trace (-1 for empty)
 *
 *               HPROF_GC_ROOT_NATIVE_STACK    Native stack
 *
 *                          id         object ID
 *                          u4         thread serial number
 *
 *               HPROF_GC_ROOT_STICKY_CLASS    System class
 *
 *                          id         object ID
 *
 *               HPROF_GC_ROOT_THREAD_BLOCK    Reference from thread block
 *
 *                          id         object ID
 *                          u4         thread serial number
 *
 *               HPROF_GC_ROOT_MONITOR_USED    Busy monitor
 *
 *                          id         object ID
 *
 *               HPROF_GC_CLASS_DUMP           dump of a class object
 *
 *                          id         class object ID
 *                          u4         stack trace serial number
 *                          id         super class object ID
 *                          id         class loader object ID
 *                          id         signers object ID
 *                          id         protection domain object ID
 *                          id         reserved
 *                          id         reserved
 *
 *                          u4         instance size (in bytes)
 *
 *                          u2         size of constant pool
 *                          [u2,       constant pool index,
 *                           ty,       type 
 *                                     2:  object
 *                                     4:  boolean
 *                                     5:  char
 *                                     6:  float
 *                                     7:  double
 *                                     8:  byte
 *                                     9:  short
 *                                     10: int
 *                                     11: long
 *                           vl]*      and value
 *
 *                          u2         number of static fields
 *                          [id,       static field name,
 *                           ty,       type,
 *                           vl]*      and value
 *
 *                          u2         number of inst. fields (not inc. super)
 *                          [id,       instance field name,
 *                           ty]*      type
 *
 *               HPROF_GC_INSTANCE_DUMP        dump of a normal object
 *
 *                          id         object ID
 *                          u4         stack trace serial number
 *                          id         class object ID
 *                          u4         number of bytes that follow
 *                          [vl]*      instance field values (class, followed
 *                                     by super, super's super ...)
 *
 *               HPROF_GC_OBJ_ARRAY_DUMP       dump of an object array
 *
 *                          id         array object ID
 *                          u4         stack trace serial number
 *                          u4         number of elements
 *                          id         element class ID
 *                          [id]*      elements
 *
 *               HPROF_GC_PRIM_ARRAY_DUMP      dump of a primitive array
 *
 *                          id         array object ID
 *                          u4         stack trace serial number
 *                          u4         number of elements
 *                          u1         element type
 *                                     4:  boolean array
 *                                     5:  char array
 *                                     6:  float array
 *                                     7:  double array
 *                                     8:  byte array
 *                                     9:  short array
 *                                     10: int array
 *                                     11: long array
 *                          [u1]*      elements
 *
 * HPROF_CPU_SAMPLES        a set of sample traces of running threads
 *
 *                u4        total number of samples
 *                u4        # of traces
 *               [u4        # of samples
 *                u4]*      stack trace serial number
 *
 * HPROF_CONTROL_SETTINGS   the settings of on/off switches
 *
 *                u4        0x00000001: alloc traces on/off
 *                          0x00000002: cpu sampling on/off
 *                u2        stack trace depth
 *
 */

typedef enum HprofTag {
    HPROF_UTF8                    = 0x01,
    HPROF_LOAD_CLASS              = 0x02,
    HPROF_UNLOAD_CLASS            = 0x03,
    HPROF_FRAME                   = 0x04,
    HPROF_TRACE                   = 0x05,
    HPROF_ALLOC_SITES             = 0x06,
    HPROF_HEAP_SUMMARY            = 0x07,
    HPROF_START_THREAD            = 0x0A,
    HPROF_END_THREAD              = 0x0B,
    HPROF_HEAP_DUMP               = 0x0C,
    HPROF_CPU_SAMPLES             = 0x0D,
    HPROF_CONTROL_SETTINGS        = 0x0E
} HprofTag;

/* 
 * Heap dump constants
 */

typedef enum HprofGcTag {
    HPROF_GC_ROOT_UNKNOWN       = 0xFF,
    HPROF_GC_ROOT_JNI_GLOBAL    = 0x01,
    HPROF_GC_ROOT_JNI_LOCAL     = 0x02,
    HPROF_GC_ROOT_JAVA_FRAME    = 0x03,
    HPROF_GC_ROOT_NATIVE_STACK  = 0x04,
    HPROF_GC_ROOT_STICKY_CLASS  = 0x05,
    HPROF_GC_ROOT_THREAD_BLOCK  = 0x06,
    HPROF_GC_ROOT_MONITOR_USED  = 0x07,
    HPROF_GC_ROOT_THREAD_OBJ    = 0x08,
    HPROF_GC_CLASS_DUMP         = 0x20,
    HPROF_GC_INSTANCE_DUMP      = 0x21,
    HPROF_GC_OBJ_ARRAY_DUMP     = 0x22,
    HPROF_GC_PRIM_ARRAY_DUMP    = 0x23
} HprofGcTag;

enum HprofType {
	HPROF_ARRAY_OBJECT 	= 1,
	HPROF_NORMAL_OBJECT 	= 2,
	HPROF_BOOLEAN 		= 4,
	HPROF_CHAR 		= 5,
	HPROF_FLOAT 		= 6,
	HPROF_DOUBLE 		= 7,
	HPROF_BYTE 		= 8,
	HPROF_SHORT 		= 9,
	HPROF_INT 		= 10,
	HPROF_LONG 		= 11
};
typedef unsigned char HprofType;

static int type_size[ /*HprofType*/ ] = 
	{ 
		/*Object?*/	sizeof(ObjectIndex), 
		/*Object?*/	sizeof(ObjectIndex), 
		/*Array*/	sizeof(ObjectIndex), 
		/*Object?*/	sizeof(ObjectIndex), 
	  	/*jboolean*/ 	1, 	
		/*jchar*/ 	2, 
		/*jfloat*/ 	4, 
		/*jdouble*/ 	8, 
		/*jbyte*/	1, 
		/*jshort*/	2, 
		/*jint*/	4, 
		/*jlong*/	8 
	};

#define _STR(arg) #arg
#define STR(arg) _STR(arg)
#define CHECK_FOR_ERROR(condition) \
	( (condition) ? \
	  (void)0 : \
	  HPROF_ERROR(JNI_TRUE, #condition) )
#define CHECK_SERIAL_NO(name, sno) \
	CHECK_FOR_ERROR( (sno) >= gdata->name##_serial_number_start  && \
		      (sno) <  gdata->name##_serial_number_counter)
#define CHECK_CLASS_SERIAL_NO(sno) CHECK_SERIAL_NO(class,sno)
#define CHECK_THREAD_SERIAL_NO(sno) CHECK_SERIAL_NO(thread,sno)
#define CHECK_TRACE_SERIAL_NO(sno) CHECK_SERIAL_NO(trace,sno)
#define CHECK_OBJECT_SERIAL_NO(sno) CHECK_SERIAL_NO(object,sno)

static void
not_implemented(void)
{
}

static IoNameIndex
get_name_index(char *name)
{
    if (name != NULL && gdata->output_format == 'b') {
        return ioname_find_or_create(name, NULL);
    }
    return 0;
}

static void *
get_binary_file_image(char *filename, int *pnbytes)
{
    unsigned char *image;
    int            fd;
    jlong          nbytes;
    int            nread;

    *pnbytes = 0;
    fd = md_open_binary(filename);
    CHECK_FOR_ERROR(fd>=0);
    if ( (nbytes = md_seek(fd, (jlong)-1)) == (jlong)-1 ) {
	HPROF_ERROR(JNI_TRUE, "Cannot md_seek() to end of file");
    }
    CHECK_FOR_ERROR(((jint)nbytes)>512);
    if ( md_seek(fd, (jlong)0) != (jlong)0 ) {
	HPROF_ERROR(JNI_TRUE, "Cannot md_seek() to start of file");
    }
    image = HPROF_MALLOC(((jint)nbytes)+1);
    CHECK_FOR_ERROR(image!=NULL);

    /* Read the entire file image into memory */
    nread = md_read(fd, image, (jint)nbytes);
    if ( nread <= 0 ) {
	HPROF_ERROR(JNI_TRUE, "System read failed.");
    }
    CHECK_FOR_ERROR(((jint)nbytes)==nread);
    md_close(fd);
    *pnbytes = (jint)nbytes;
    return image;
}

static int
type_is_primitive(HprofType kind)
{
    return ( kind >= HPROF_BOOLEAN );
}

static char *
signature_to_name(char *sig)
{
    char *ptr;
    char *basename;
    char *name;
    int i;
    int len;
    int name_len;

    if ( sig != NULL ) {
	switch ( sig[0] ) {
	    case JVM_SIGNATURE_CLASS:
		ptr = strchr(sig+1, JVM_SIGNATURE_ENDCLASS);
		if ( ptr == NULL ) {
		    basename = "Unknown_class";
		    break;
		}
		/*LINTED*/
		name_len = (jint)(ptr - (sig+1));
		name = HPROF_MALLOC(name_len+1);
		(void)memcpy(name, sig+1, name_len);
		name[name_len] = 0;
		for ( i = 0 ; i < name_len ; i++ ) {
		    if ( name[i] == '/' ) name[i] = '.';
		}
		return name;
	    case JVM_SIGNATURE_ARRAY:
		basename = signature_to_name(sig+1);
		len = strlen(basename);
		name_len = len+2;
		name = HPROF_MALLOC(name_len+1);
		(void)memcpy(name, basename, len);
		(void)memcpy(name+len, "[]", 2);
		name[name_len] = 0;
		HPROF_FREE(basename);
		return name;
	    case JVM_SIGNATURE_FUNC:
		ptr = strchr(sig+1, JVM_SIGNATURE_ENDFUNC);
		if ( ptr == NULL ) {
		    basename = "Unknown_method";
		    break;
		}
		basename = "()"; /* Someday deal with method signatures */
		break;
	    case JVM_SIGNATURE_BYTE:
		basename = "byte";
		break;
	    case JVM_SIGNATURE_CHAR:
		basename = "char";
		break;
	    case JVM_SIGNATURE_ENUM:
		basename = "enum";
		break;
	    case JVM_SIGNATURE_FLOAT:
		basename = "float";
		break;
	    case JVM_SIGNATURE_DOUBLE:
		basename = "double";
		break;
	    case JVM_SIGNATURE_INT:
		basename = "int";
		break;
	    case JVM_SIGNATURE_LONG:
		basename = "long";
		break;
	    case JVM_SIGNATURE_SHORT:
		basename = "short";
		break;
	    case JVM_SIGNATURE_VOID:
		basename = "void";
		break;
	    case JVM_SIGNATURE_BOOLEAN:
		basename = "boolean";
		break;
	    default:
		basename = "Unknown_class";
		break;
	}
    } else {
	basename = "Unknown_class";
    }

    /* Simple basename */
    name_len = strlen(basename);
    name = HPROF_MALLOC(name_len+1);
    (void)strcpy(name, basename);
    return name;
}

static void
type_from_signature(const char *sig, HprofType *kind, jint *size)
{
    *kind = HPROF_NORMAL_OBJECT;
    *size = 0;
    switch ( sig[0] ) {
	case JVM_SIGNATURE_ENUM:
	case JVM_SIGNATURE_CLASS:
	case JVM_SIGNATURE_ARRAY:
            *kind = HPROF_NORMAL_OBJECT;
	    break;
        case JVM_SIGNATURE_BOOLEAN:
            *kind = HPROF_BOOLEAN;
	    break;
        case JVM_SIGNATURE_CHAR:
            *kind = HPROF_CHAR;
	    break;
        case JVM_SIGNATURE_FLOAT:
            *kind = HPROF_FLOAT;
	    break;
        case JVM_SIGNATURE_DOUBLE:
            *kind = HPROF_DOUBLE;
	    break;
        case JVM_SIGNATURE_BYTE:
            *kind = HPROF_BYTE;
	    break;
        case JVM_SIGNATURE_SHORT:
            *kind = HPROF_SHORT;
	    break;
        case JVM_SIGNATURE_INT:
            *kind = HPROF_INT;
	    break;
        case JVM_SIGNATURE_LONG:
            *kind = HPROF_LONG;
	    break;
	default:
	    HPROF_ASSERT(0);
	    break;
    }
    *size = type_size[*kind];
}

static void
type_array(const char *sig, HprofType *kind, jint *elem_size)
{
    *kind = 0;
    *elem_size = 0;
    switch ( sig[0] ) {
        case JVM_SIGNATURE_ARRAY:
            type_from_signature(sig+1, kind, elem_size);
	    break;
    }
}

static void 
read_raw(unsigned char **pp, unsigned char *buf, int len) 
{
    while ( len > 0 ) {
	*buf = **pp;
	buf++;
	(*pp)++;
	len--;
    }
}

static unsigned 
read_u1(unsigned char **pp) 
{
    unsigned char b;
    
    read_raw(pp, &b, 1);
    return b;
}

static unsigned 
read_u2(unsigned char **pp) 
{
    unsigned short s;

    read_raw(pp, (void*)&s, 2);
    return md_htons(s);
}

static unsigned 
read_u4(unsigned char **pp) 
{
    unsigned int u;

    read_raw(pp, (void*)&u, 4);
    return md_htonl(u);
}

static jlong 
read_u8(unsigned char **pp) 
{
    unsigned int high;
    unsigned int low;
    jlong        x;

    high = read_u4(pp);
    low  = read_u4(pp);
    x = high;
    x = (x << 32) | low;
    return x;
}

static unsigned 
read_id(unsigned char **pp) 
{
    return read_u4(pp);
}

static void
system_error(const char *system_call, int rc, int errnum)
{
    char buf[256];
    char details[256];

    details[0] = 0;
    if ( errnum != 0 ) {
        md_system_error(details, (int)sizeof(details));
    } else if ( rc >= 0 ) {
	(void)strcpy(details,"Only part of buffer processed");
    }
    if ( details[0] == 0 ) {
        (void)strcpy(details,"Unknown system error condition");
    }
    (void)md_snprintf(buf, sizeof(buf), "System %s failed: %s\n",
			    system_call, details);
    HPROF_ERROR(JNI_TRUE, buf);
}

static void 
system_write(int fd, void *buf, int len, jboolean socket)
{
    int res;
   
    HPROF_ASSERT(fd>=0);
    if (socket) {
        res = md_send(fd, buf, len, 0);
	if (res < 0 || res!=len) {
	    system_error("send", res, errno);
	}
    } else {
        res = md_write(fd, buf, len);
	if (res < 0 || res!=len) {
	    system_error("write", res, errno);
	}
    }
}

static void
write_flush(void)
{
    HPROF_ASSERT(gdata->fd >= 0);
    if (gdata->write_buffer_index) {
        system_write(gdata->fd, gdata->write_buffer, gdata->write_buffer_index,
				gdata->socket);
        gdata->write_buffer_index = 0;
    }
}

static void
heap_flush(void)
{
    HPROF_ASSERT(gdata->heap_fd >= 0);
    if (gdata->heap_buffer_index) {
	gdata->heap_write_count += (jlong)gdata->heap_buffer_index;
        system_write(gdata->heap_fd, gdata->heap_buffer, gdata->heap_buffer_index,
				JNI_FALSE);
        gdata->heap_buffer_index = 0;
    }
}

static void
check_flush(void)
{
    if ( gdata->check_fd < 0 ) {
	return;
    }
    if (gdata->check_buffer_index) {
        system_write(gdata->check_fd, gdata->check_buffer, gdata->check_buffer_index,
				JNI_FALSE);
        gdata->check_buffer_index = 0;
    }
}

static void 
write_raw(void *buf, int len)
{
    HPROF_ASSERT(gdata->fd >= 0);
    if (gdata->write_buffer_index + len > gdata->write_buffer_size) {
        write_flush();
        if (len > gdata->write_buffer_size) {
            system_write(gdata->fd, buf, len, gdata->socket);
            return;
        }
    }
    (void)memcpy(gdata->write_buffer + gdata->write_buffer_index, buf, len);
    gdata->write_buffer_index += len;
}

static void
write_u4(unsigned i)
{
    i = md_htonl(i);
    write_raw(&i, (jint)sizeof(unsigned));
}

static void
write_u8(jlong t)
{
    write_u4((jint)jlong_high(t));
    write_u4((jint)jlong_low(t));
}

static void
write_u2(unsigned short i)
{
    i = md_htons(i);
    write_raw(&i, (jint)sizeof(unsigned short));
}

static void
write_u1(unsigned char i)
{
    write_raw(&i, (jint)sizeof(unsigned char));
}

static void
write_id(ObjectIndex i)
{
    write_u4(i);
}

static void 
write_current_ticks(void)
{
    write_u4(md_get_milliticks() * 1000 - gdata->micro_sec_ticks);
}

static void 
write_header(unsigned char type, jint length)
{
    write_u1(type);
    write_current_ticks();
    write_u4(length);
}

static void
write_index_id(TableIndex index)
{
    write_id((ObjectIndex)index);
}

static IoNameIndex
write_name_first(char *name)
{
    if ( name == NULL ) {
        return 0;
    }
    if (gdata->output_format == 'b') {
        IoNameIndex name_index;
	jboolean    new_one;
        
        new_one = JNI_FALSE;
	name_index = ioname_find_or_create(name, &new_one);
        if ( new_one ) {
            int      len;

            len = strlen(name);
            write_header(HPROF_UTF8, len + (jint)sizeof(ObjectIndex));
            write_index_id(name_index);
            write_raw(name, len);
    
        }
        return name_index;
    }
    return 0;
}

static void 
write_printf(char *fmt, ...)
{
    char buf[1024];
    va_list args;
    va_start(args, fmt);
    (void)md_vsnprintf(buf, sizeof(buf), fmt, args);
    buf[sizeof(buf)-1] = 0;
    write_raw(buf, strlen(buf));
    va_end(args);
}

static void
write_thread_serial_number(SerialNumber thread_serial_num, int with_comma)
{
    if ( thread_serial_num != 0 ) {
	CHECK_THREAD_SERIAL_NO(thread_serial_num);
        if ( with_comma ) {
	    write_printf(" thread %d,", thread_serial_num);
        } else {
	    write_printf(" thread %d", thread_serial_num);
	}
    } else {
        if ( with_comma ) {
	    write_printf(" <unknown thread>,");
        } else {
	    write_printf(" <unknown thread>");
	}
    }
}

static void 
heap_raw(void *buf, int len)
{
    HPROF_ASSERT(gdata->heap_fd >= 0);
    if (gdata->heap_buffer_index + len > gdata->heap_buffer_size) {
        heap_flush();
        if (len > gdata->heap_buffer_size) {
            gdata->heap_write_count += (jlong)len;
            system_write(gdata->heap_fd, buf, len, JNI_FALSE);
            return;
        }
    }
    (void)memcpy(gdata->heap_buffer + gdata->heap_buffer_index, buf, len);
    gdata->heap_buffer_index += len;
}

static void
heap_u4(unsigned i)
{
    i = md_htonl(i);
    heap_raw(&i, (jint)sizeof(unsigned));
}

static void
heap_u8(jlong i)
{
    heap_u4((jint)jlong_high(i));
    heap_u4((jint)jlong_low(i));
}

static void
heap_u2(unsigned short i)
{
    i = md_htons(i);
    heap_raw(&i, (jint)sizeof(unsigned short));
}

static void
heap_u1(unsigned char i)
{
    heap_raw(&i, (jint)sizeof(unsigned char));
}

static void
heap_id(ObjectIndex i)
{
    heap_u4(i);
}

static void
heap_index_id(TableIndex index)
{
    heap_id((ObjectIndex)index);
}

static void
heap_name(char *name)
{
    heap_index_id(get_name_index(name));
}

static void 
heap_printf(char *fmt, ...)
{
    char buf[1024];
    va_list args;
    va_start(args, fmt);
    (void)md_vsnprintf(buf, sizeof(buf), fmt, args);
    buf[sizeof(buf)-1] = 0;
    heap_raw(buf, strlen(buf));
    va_end(args);
}

static void
heap_element(HprofType kind, jint size, jvalue value)
{
    if ( !type_is_primitive(kind) ) {
	heap_id((ObjectIndex)value.i);
    } else {
	switch ( size ) {
	    case 8:
		heap_u8(value.j);
		break;
	    case 4:
		heap_u4(value.i);
		break;
	    case 2:
		heap_u2(value.s);
		break;
	    case 1:
		heap_u1(value.b);
		break;
	    default:
		HPROF_ASSERT(0);
		break;
	}
    }
}

static void
heap_elements(HprofType kind, jint num_elements, jint elem_size, jvalue *values)
{
    int i;
    
    for (i = 0; i < num_elements; i++) {  
	heap_element(kind, elem_size, values[i]);
    }
}

/* Checking function for binary format */

static jlong
read_val(unsigned char **pp, HprofType ty)
{
    jlong val;
    
    switch ( ty ) {
	case 0:
	case HPROF_ARRAY_OBJECT: 
	case HPROF_NORMAL_OBJECT: 
	    val = read_id(pp);
	    break;
	case HPROF_BYTE: 
	case HPROF_BOOLEAN:
	    val = read_u1(pp);
	    break;
	case HPROF_CHAR: 
	case HPROF_SHORT:
	    val = read_u2(pp);
	    break;
	case HPROF_FLOAT: 
	case HPROF_INT:
	    val = read_u4(pp);
	    break;
	case HPROF_DOUBLE: 
	case HPROF_LONG:
	    val = read_u8(pp);
	    break;
	default:
	    HPROF_ERROR(JNI_TRUE, "bad type number");
	    val = 0;
	    break;
    }
    return val;
}

static void 
check_raw(void *buf, int len)
{
    if ( gdata->check_fd < 0 ) {
	return;
    }

    if ( len <= 0 ) {
	return;
    }

    if (gdata->check_buffer_index + len > gdata->check_buffer_size) {
        check_flush();
        if (len > gdata->check_buffer_size) {
            system_write(gdata->check_fd, buf, len, JNI_FALSE);
            return;
        }
    }
    (void)memcpy(gdata->check_buffer + gdata->check_buffer_index, buf, len);
    gdata->check_buffer_index += len;
}

static void 
check_printf(char *fmt, ...)
{
    char buf[1024];
    va_list args;
   
    if ( gdata->check_fd < 0 ) {
	return;
    }

    va_start(args, fmt);
    (void)md_vsnprintf(buf, sizeof(buf), fmt, args);
    buf[sizeof(buf)-1] = 0;
    check_raw(buf, strlen(buf));
    va_end(args);
}

static int
check_heap_tags(unsigned char *pstart, int nbytes)
{
    int nrecords;
    unsigned char *p;
    
    nrecords = 0;
    p = pstart;
    while ( p < (pstart+nbytes) ) {
	unsigned tag;
	HprofType ty;
	unsigned id, id2, fr;
	int num_elements;
	SerialNumber trace_serial_num;
	SerialNumber thread_serial_num;
	char *label;
	int npos;
        int i;
	unsigned inst_size;
	
	nrecords++;
	/*LINTED*/
	npos = (int)(p - pstart);
        tag = read_u1(&p);
    #define CASE_HEAP(name) case name: label = #name;
	switch ( tag ) {
	    CASE_HEAP(HPROF_GC_ROOT_UNKNOWN)
		id = read_id(&p);
                check_printf("H#%d@%d %s: id=0x%x\n", 
			nrecords, npos, label, id);
		break;
	    CASE_HEAP(HPROF_GC_ROOT_JNI_GLOBAL)
		id = read_id(&p);
		id2 = read_id(&p);
                check_printf("H#%d@%d %s: id=0x%x, id2=0x%x\n", 
			nrecords, npos, label, id, id2);
		break;
	    CASE_HEAP(HPROF_GC_ROOT_JNI_LOCAL)
		id = read_id(&p);
		thread_serial_num = read_u4(&p);
		fr = read_u4(&p);
                check_printf("H#%d@%d %s: id=0x%x, thread_serial_num=%u, fr=0x%x\n", 
			nrecords, npos, label, id, thread_serial_num, fr);
		break;
	    CASE_HEAP(HPROF_GC_ROOT_JAVA_FRAME)
		id = read_id(&p);
		thread_serial_num = read_u4(&p);
		fr = read_u4(&p);
                check_printf("H#%d@%d %s: id=0x%x, thread_serial_num=%u, fr=0x%x\n", 
			nrecords, npos, label, id, thread_serial_num, fr);
		break;
	    CASE_HEAP(HPROF_GC_ROOT_NATIVE_STACK)
		id = read_id(&p);
		thread_serial_num = read_u4(&p);
                check_printf("H#%d@%d %s: id=0x%x, thread_serial_num=%u\n", 
			nrecords, npos, label, id, thread_serial_num);
		break;
	    CASE_HEAP(HPROF_GC_ROOT_STICKY_CLASS)
		id = read_id(&p);
                check_printf("H#%d@%d %s: id=0x%x\n", 
			nrecords, npos, label, id);
		break;
	    CASE_HEAP(HPROF_GC_ROOT_THREAD_BLOCK)
		id = read_id(&p);
		thread_serial_num = read_u4(&p);
                check_printf("H#%d@%d %s: id=0x%x, thread_serial_num=%u\n", 
			nrecords, npos, label, id, thread_serial_num);
		break;
	    CASE_HEAP(HPROF_GC_ROOT_MONITOR_USED)
		id = read_id(&p);
                check_printf("H#%d@%d %s: id=0x%x\n", 
			nrecords, npos, label, id);
		break;
	    CASE_HEAP(HPROF_GC_ROOT_THREAD_OBJ)
		id = read_id(&p);
		thread_serial_num = read_u4(&p);
		trace_serial_num = read_u4(&p);
                CHECK_TRACE_SERIAL_NO(trace_serial_num);
                check_printf("H#%d@%d %s: id=0x%x, thread_serial_num=%u, trace_serial_num=%u\n", 
			nrecords, npos, label, id, thread_serial_num, trace_serial_num);
		break;
	    CASE_HEAP(HPROF_GC_CLASS_DUMP)
		id = read_id(&p);
		trace_serial_num = read_u4(&p);
                CHECK_TRACE_SERIAL_NO(trace_serial_num);
                check_printf("H#%d@%d %s: id=0x%x, trace_serial_num=%u\n", 
			nrecords, npos, label, id, trace_serial_num);
		{
		    unsigned su, ld, si, pr, re1, re2;
		    
		    su = read_id(&p);
		    ld = read_id(&p);
		    si = read_id(&p);
		    pr = read_id(&p);
		    re1 = read_id(&p);
		    re2 = read_id(&p);
		    check_printf("  su=0x%x, ld=0x%x, si=0x%x, pr=0x%x, re1=0x%x, re2=0x%x\n", 
			su, ld, si, pr, re1, re2);
		}
		inst_size = read_u4(&p);
		check_printf("  instance_size=%d\n", inst_size); 
		
		num_elements = read_u2(&p);
		for(i=0; i<num_elements; i++) {
		    HprofType ty;
		    unsigned cpi;
		    jlong val;
		    
		    cpi = read_u2(&p);
		    ty = read_u1(&p);
		    val = read_val(&p, ty);
		    check_printf("  constant_pool %d: "
				 "cpi=%d, ty=%d, val=0x%x%08x\n", 
				i, cpi, ty, jlong_high(val), jlong_low(val));
		}
		
		num_elements = read_u2(&p);
		check_printf("  static_field_count=%d\n", num_elements);
		for(i=0; i<num_elements; i++) {
		    HprofType ty;
		    unsigned id;
		    jlong val;
		    
		    id = read_id(&p);
		    ty = read_u1(&p);
		    val = read_val(&p, ty);
		    check_printf("  static_field %d: "
				 "id=0x%x, ty=%d, val=0x%x%08x\n", 
				i, id, ty, jlong_high(val), jlong_low(val));
		}
		
		num_elements = read_u2(&p);
		check_printf("  instance_field_count=%d\n", num_elements);
		for(i=0; i<num_elements; i++) {
		    HprofType ty;
		    unsigned id;
		    
		    id = read_id(&p);
		    ty = read_u1(&p);
                    check_printf("  instance_field %d: id=0x%x, ty=%d\n", 
				i, id, ty);
		}
		break;
	    CASE_HEAP(HPROF_GC_INSTANCE_DUMP)
		id = read_id(&p);
		trace_serial_num = read_u4(&p);
                CHECK_TRACE_SERIAL_NO(trace_serial_num);
		id2 = read_id(&p);
		num_elements = read_u4(&p);
                check_printf("H#%d@%d %s: id=0x%x, trace_serial_num=%u,"
			     " cid=0x%x, nbytes=%d\n", 
			    nrecords, npos, label, id, trace_serial_num, 
			    id2, num_elements);
		check_printf("  ");
		for(i=0; i<num_elements; i++) {
                    check_printf("%02x", read_u1(&p));
		    if ( ( i % 4 ) == 3 ) {
                        check_printf(" ");
		    }
		    if ( ( i % 32 ) == 31 && i != (num_elements-1) ) {
                        check_printf("\n");
		        check_printf("  ");
		    }
		}
		check_printf("\n");
		break;
	    CASE_HEAP(HPROF_GC_OBJ_ARRAY_DUMP)
		id = read_id(&p);
		trace_serial_num = read_u4(&p);
                CHECK_TRACE_SERIAL_NO(trace_serial_num);
		num_elements = read_u4(&p);
		id2 = read_id(&p);
                check_printf("H#%d@%d %s: id=0x%x, trace_serial_num=%u, nelems=%d, eid=0x%x\n", 
				nrecords, npos, label, id, trace_serial_num, num_elements, id2);
		for(i=0; i<num_elements; i++) {
		    unsigned id;
		    
		    id = read_id(&p);
                    check_printf("  [%d]: id=0x%x\n", i, id);
		}
		break;
	    CASE_HEAP(HPROF_GC_PRIM_ARRAY_DUMP)
		id = read_id(&p);
		trace_serial_num = read_u4(&p);
                CHECK_TRACE_SERIAL_NO(trace_serial_num);
		num_elements = read_u4(&p);
		ty = read_u1(&p);
                check_printf("H#%d@%d %s: id=0x%x, trace_serial_num=%u, nelems=%d, ty=%d\n", 
				nrecords, npos, label, id, trace_serial_num, num_elements, ty);
		HPROF_ASSERT(type_is_primitive(ty));
		for(i=0; i<num_elements; i++) {
		    jlong val;
		    
		    val = read_val(&p, ty);
                    check_printf("  [%d]: val=0x%x%08x\n", i, 
					jlong_high(val), jlong_low(val));
		}
		break;
	    default:
                label = "UNKNOWN";
		check_printf("H#%d@%d %s: ERROR!\n", 
				nrecords, npos, label);
		HPROF_ERROR(JNI_TRUE, "unknown heap record type");
		break;
	}
    }
    CHECK_FOR_ERROR(p==pstart+nbytes);
    return nrecords;
}

static int 
check_tags(unsigned char *pstart, int nbytes)
{
    unsigned char *p;
    int      nrecord;

    p = pstart;
    check_printf("\nCHECK TAGS: starting\n");
    
    nrecord = 0;
    while ( p < (pstart+nbytes) ) {
	unsigned tag;
	unsigned size;
	int nheap_records;
	int npos;
	char *label;
	int i;
	int id, ty, nm, sg, so, li, num_elements, gr, gn;
	SerialNumber trace_serial_num;
	SerialNumber thread_serial_num;
	SerialNumber class_serial_num;
	unsigned flags;
	unsigned depth;
	float cutoff;
	unsigned temp;
	jint nblive ;
	jint nilive ;
	jlong tbytes;
	jlong tinsts;
	jint total_samples ;
	jint trace_count ;
	
	nrecord++;
	/*LINTED*/
	npos = (int)(p - pstart);
	tag = read_u1(&p);
	(void)read_u4(&p); /* microsecs */
	size = read_u4(&p);
	#define CASE_TAG(name) case name: label = #name;
	switch ( tag ) {
            CASE_TAG(HPROF_UTF8)
		CHECK_FOR_ERROR(size>=4);
	        id = read_u4(&p);
		check_printf("#%d@%d: %s, sz=%d, name_id=0x%x, \"", 
				nrecord, npos, label, size, id);
		check_raw(p, size-4);
		check_printf("\"\n");
		p += (size-4);
		break;
            CASE_TAG(HPROF_LOAD_CLASS)
		CHECK_FOR_ERROR(size==4*4);
                class_serial_num = read_u4(&p);
                CHECK_CLASS_SERIAL_NO(class_serial_num);
		id = read_u4(&p);
		trace_serial_num = read_u4(&p);
                CHECK_TRACE_SERIAL_NO(trace_serial_num);
		nm = read_u4(&p);
		check_printf("#%d@%d: %s, sz=%d, class_serial_num=%u,"
			     " id=0x%x, trace_serial_num=%u, name_id=0x%x\n", 
				nrecord, npos, label, size, class_serial_num, 
				id, trace_serial_num, nm);
		break;
            CASE_TAG(HPROF_UNLOAD_CLASS)
		CHECK_FOR_ERROR(size==4);
                class_serial_num = read_u4(&p);
                CHECK_CLASS_SERIAL_NO(class_serial_num);
		check_printf("#%d@%d: %s, sz=%d, class_serial_num=%u\n", 
				nrecord, npos, label, size, class_serial_num);
		break;
            CASE_TAG(HPROF_FRAME)
		CHECK_FOR_ERROR(size==6*4);
		id = read_u4(&p);
		nm = read_u4(&p);
		sg = read_u4(&p);
		so = read_u4(&p);
		class_serial_num = read_u4(&p);
                CHECK_CLASS_SERIAL_NO(class_serial_num);
		li = read_u4(&p);
		check_printf("#%d@%d: %s, sz=%d, id=0x%x, name_id=0x%x,"
			     " sig_id=0x%x, source_id=0x%x,"
			     " class_serial_num=%u, lineno=%d\n", 
				nrecord, npos, label, size, id, nm, sg, 
				so, class_serial_num, li);
		break;
            CASE_TAG(HPROF_TRACE)
		CHECK_FOR_ERROR(size>=3*4);
                trace_serial_num = read_u4(&p);
                CHECK_TRACE_SERIAL_NO(trace_serial_num);
		thread_serial_num = read_u4(&p); /* Can be 0 */
		num_elements = read_u4(&p);
		check_printf("#%d@%d: %s, sz=%d, trace_serial_num=%u,"
			     " thread_serial_num=%u, nelems=%d [", 
				nrecord, npos, label, size, 
				trace_serial_num, thread_serial_num, num_elements);
	        for(i=0; i< num_elements; i++) {
		    check_printf("0x%x,", read_u4(&p));
		}
		check_printf("]\n");
		break;
            CASE_TAG(HPROF_ALLOC_SITES)
		CHECK_FOR_ERROR(size>=2+4*4+2*8);
		flags = read_u2(&p);
		temp  = read_u4(&p);
		cutoff = *((float*)&temp);
		nblive = read_u4(&p);
		nilive = read_u4(&p);
		tbytes = read_u8(&p);
		tinsts = read_u8(&p);
		num_elements     = read_u4(&p);
		check_printf("#%d@%d: %s, sz=%d, flags=0x%x, cutoff=%g,"
			     " nblive=%d, nilive=%d, tbytes=(%d,%d),"
			     " tinsts=(%d,%d), num_elements=%d\n", 
				nrecord, npos, label, size,
				flags, cutoff, nblive, nilive, 
				jlong_high(tbytes), jlong_low(tbytes),
				jlong_high(tinsts), jlong_low(tinsts),
				num_elements);
	        for(i=0; i< num_elements; i++) {
		    ty = read_u1(&p);
		    class_serial_num = read_u4(&p);
                    CHECK_CLASS_SERIAL_NO(class_serial_num);
		    trace_serial_num = read_u4(&p);
                    CHECK_TRACE_SERIAL_NO(trace_serial_num);
		    nblive = read_u4(&p);
		    nilive = read_u4(&p);
		    tbytes = read_u4(&p);
		    tinsts = read_u4(&p);
		    check_printf("\t %d: ty=%d, class_serial_num=%u,"
				 " trace_serial_num=%u, nblive=%d, nilive=%d,"
				 " tbytes=%d, tinsts=%d\n",
				 i, ty, class_serial_num, trace_serial_num,
				 nblive, nilive, (jint)tbytes, (jint)tinsts);
	        }
		break;
            CASE_TAG(HPROF_HEAP_SUMMARY)
		CHECK_FOR_ERROR(size==2*4+2*8);
		nblive = read_u4(&p);
		nilive = read_u4(&p);
		tbytes = read_u8(&p);
		tinsts = read_u8(&p);
		check_printf("#%d@%d: %s, sz=%d,"
			     " nblive=%d, nilive=%d, tbytes=(%d,%d),"
			     " tinsts=(%d,%d)\n", 
				nrecord, npos, label, size,
				nblive, nilive, 
				jlong_high(tbytes), jlong_low(tbytes),
				jlong_high(tinsts), jlong_low(tinsts));
		break;
            CASE_TAG(HPROF_START_THREAD)
		CHECK_FOR_ERROR(size==6*4);
                thread_serial_num = read_u4(&p);
                CHECK_THREAD_SERIAL_NO(thread_serial_num);
		id = read_u4(&p);
		trace_serial_num = read_u4(&p);
                CHECK_TRACE_SERIAL_NO(trace_serial_num);
		nm = read_u4(&p);
		gr = read_u4(&p);
		gn = read_u4(&p);
		check_printf("#%d@%d: %s, sz=%d, thread_serial_num=%u,"
			     " id=0x%x, trace_serial_num=%u, nm=0x%x,"
			     " gr=0x%x, gn=0x%x\n", 
				nrecord, npos, label, size, 
				thread_serial_num, id, trace_serial_num, 
				nm, gr, gn);
		break;
            CASE_TAG(HPROF_END_THREAD)
		CHECK_FOR_ERROR(size==4);
                thread_serial_num = read_u4(&p);
                CHECK_THREAD_SERIAL_NO(thread_serial_num);
		check_printf("#%d@%d: %s, sz=%d, thread_serial_num=%u\n", 
				nrecord, npos, label, size, thread_serial_num);
		break;
            CASE_TAG(HPROF_HEAP_DUMP)
		check_printf("#%d@%d: BEGIN: %s, sz=%d\n", 
				nrecord, npos, label, size);
	        nheap_records = check_heap_tags(p, size);
		check_printf("#%d@%d: END: %s, sz=%d, nheap_recs=%d\n", 
				nrecord, npos, label, size, nheap_records);
	        p += size;
		break;
            CASE_TAG(HPROF_CPU_SAMPLES)
		CHECK_FOR_ERROR(size>=2*4);
		total_samples = read_u4(&p);
		trace_count = read_u4(&p);
		check_printf("#%d@%d: %s, sz=%d, total_samples=%d,"
			     " trace_count=%d\n", 
				nrecord, npos, label, size,
				total_samples, trace_count);
	        for(i=0; i< trace_count; i++) {
		    num_elements = read_u4(&p);
		    trace_serial_num = read_u4(&p);
                    CHECK_TRACE_SERIAL_NO(trace_serial_num);
		    check_printf("\t %d: samples=%d, trace_serial_num=%u\n",
				 trace_serial_num, num_elements);
	        }
		break;
            CASE_TAG(HPROF_CONTROL_SETTINGS)
		CHECK_FOR_ERROR(size==4+2);
		flags = read_u4(&p);
		depth = read_u2(&p);
		check_printf("#%d@%d: %s, sz=%d, flags=0x%x, depth=%d\n", 
				nrecord, npos, label, size, flags, depth);
		break;
            default:
                label = "UNKNOWN";
		check_printf("#%d@%d: %s, sz=%d\n", 
				nrecord, npos, label, size);
		HPROF_ERROR(JNI_TRUE, "unknown record type");
		p += size;
		break;
	}
        CHECK_FOR_ERROR(p<=(pstart+nbytes));
    }
    check_flush();
    CHECK_FOR_ERROR(p==(pstart+nbytes));
    return nrecord;
}

/* ------------------------------------------------------------------ */

void 
io_check_binary_file(char *filename)
{
    unsigned char *image;
    unsigned char *p;
    unsigned       idsize;
    int            nbytes;
    int            nrecords;

    image = get_binary_file_image(filename, &nbytes);
    if ( image == NULL ) {
        check_printf("No file image: %s\n", filename);
	return;
    }
    p = image;
    CHECK_FOR_ERROR(strcmp((char*)p, "JAVA PROFILE 1.0.1")==0);
    check_printf("Filename=%s, nbytes=%d, header=\"%s\"\n", 
			filename, nbytes, p);
    p+=(strlen((char*)p)+1);
    idsize = read_u4(&p);
    CHECK_FOR_ERROR(idsize==sizeof(ObjectIndex));
    (void)read_u4(&p);
    (void)read_u4(&p);
    /* LINTED */
    nrecords = check_tags(p, nbytes - ( p - image ) );
    check_printf("#%d total records found in %d bytes\n", nrecords, nbytes);
    HPROF_FREE(image);
}

void 
io_flush(void)
{
    write_flush();
}

void 
io_setup(void)
{
    gdata->write_buffer_size = FILE_IO_BUFFER_SIZE;
    gdata->write_buffer = HPROF_MALLOC(gdata->write_buffer_size);
    gdata->write_buffer_index = 0;
    
    gdata->heap_write_count = (jlong)0;
    gdata->heap_buffer_size = FILE_IO_BUFFER_SIZE;
    gdata->heap_buffer = HPROF_MALLOC(gdata->heap_buffer_size);
    gdata->heap_buffer_index = 0;
    
    if ( gdata->logflags & LOG_CHECK_BINARY ) {
	gdata->check_buffer_size = FILE_IO_BUFFER_SIZE;
	gdata->check_buffer = HPROF_MALLOC(gdata->check_buffer_size);
	gdata->check_buffer_index = 0;
    }

    ioname_init();
}

void 
io_cleanup(void)
{
    if ( gdata->write_buffer != NULL ) {
	HPROF_FREE(gdata->write_buffer);
    }
    gdata->write_buffer_size = 0;
    gdata->write_buffer = NULL;
    gdata->write_buffer_index = 0;
    
    if ( gdata->heap_buffer != NULL ) {
	HPROF_FREE(gdata->heap_buffer);
    }
    gdata->heap_write_count = (jlong)0;
    gdata->heap_buffer_size = 0;
    gdata->heap_buffer = NULL;
    gdata->heap_buffer_index = 0;
    
    if ( gdata->logflags & LOG_CHECK_BINARY ) {
	if ( gdata->check_buffer != NULL ) {
	    HPROF_FREE(gdata->check_buffer);
	}
	gdata->check_buffer_size = 0;
	gdata->check_buffer = NULL;
	gdata->check_buffer_index = 0;
    }

    ioname_cleanup();
}

void
io_write_file_header(void)
{
    if (gdata->output_format == 'b') {
        jint settings;
        jlong t;
        
        settings = 0;
        if (gdata->heap_dump || gdata->alloc_sites) {
            settings |= 1;
        }
        if (gdata->cpu_sampling) {
            settings |= 2;
        }
        t = md_get_timemillis();
        
        write_raw(HPROF_HEADER, strlen(HPROF_HEADER) + 1);
        write_u4((jint)sizeof(ObjectIndex));
        write_u8(t);
        
        write_header(HPROF_CONTROL_SETTINGS, 4 + 2);
        write_u4(settings);
        write_u2((unsigned short)gdata->max_trace_depth);
    
    } else if ((!gdata->cpu_timing) || (!gdata->old_timing_format)) {
        /* We don't want the prelude file for the old prof output format */
        time_t t;
        char prelude_file[FILENAME_MAX];
        int prelude_fd;
	int nbytes;
        
        t = time(0);

        md_get_prelude_path(prelude_file, sizeof(prelude_file), PRELUDE_FILE);

        prelude_fd = md_open(prelude_file);
        if (prelude_fd < 0) {
            char buf[FILENAME_MAX+80];
            
            (void)md_snprintf(buf, sizeof(buf), "Can't open %s", prelude_file);
            buf[sizeof(buf)-1] = 0;
            HPROF_ERROR(JNI_TRUE, buf);
        }
        
        write_printf("%s, created %s\n", HPROF_HEADER, ctime(&t));
        
        do {
            char buf[1024]; /* File is small, small buffer ok here */
            
            nbytes = md_read(prelude_fd, buf, sizeof(buf));
	    if ( nbytes < 0 ) {
		system_error("read", nbytes, errno);
                break;
	    }
            if (nbytes == 0) {
                break;
            }
            write_raw(buf, nbytes);
        } while ( nbytes > 0 );

        md_close(prelude_fd);
        
        write_printf("\n--------\n\n");
	
	write_flush();
    }
}

void
io_write_class_load(SerialNumber class_serial_num, ObjectIndex index,
		    SerialNumber trace_serial_num, char *sig)
{
    CHECK_CLASS_SERIAL_NO(class_serial_num);
    CHECK_TRACE_SERIAL_NO(trace_serial_num);
    if (gdata->output_format == 'b') {
        IoNameIndex name_index;
	char *class_name;

	class_name = signature_to_name(sig);
        name_index = write_name_first(class_name);
        write_header(HPROF_LOAD_CLASS, (2 * (jint)sizeof(ObjectIndex)) + (4 * 2));
        write_u4(class_serial_num);
        write_index_id(index);
        write_u4(trace_serial_num);
        write_index_id(name_index);
	HPROF_FREE(class_name);    
    }
}

void
io_write_class_unload(SerialNumber class_serial_num)
{
    CHECK_CLASS_SERIAL_NO(class_serial_num);
    if (gdata->output_format == 'b') {
        write_header(HPROF_UNLOAD_CLASS, 4);
        write_u4(class_serial_num);
    }
}

void
io_write_sites_header(const char * comment_str, jint flags, double cutoff,
		    jint total_live_bytes, jint total_live_instances,
		    jlong total_alloced_bytes, jlong total_alloced_instances,
		    jint count)
{
    if ( gdata->output_format == 'b') {
        write_header(HPROF_ALLOC_SITES, 2 + (8 * 4) + (count * (4 * 6 + 1)));
        write_u2((unsigned short)flags);
	write_u4(*(int *)(&cutoff));
        write_u4(total_live_bytes);
        write_u4(total_live_instances);
        write_u8(total_alloced_bytes);
        write_u8(total_alloced_instances);
        write_u4(count);
    } else {
        time_t t;

        t = time(0);
        write_printf("SITES BEGIN (ordered by %s) %s", comment_str, ctime(&t));
        write_printf(
            "          percent          live          alloc'ed  stack class\n");
        write_printf(
            " rank   self  accum     bytes objs     bytes  objs trace name\n");
    }
}

void
io_write_sites_elem(jint index, double ratio, double accum_percent,
		char *sig, SerialNumber class_serial_num,
		SerialNumber trace_serial_num, jint n_live_bytes,
		jint n_live_instances, jint n_alloced_bytes,
		jint n_alloced_instances)
{
    CHECK_CLASS_SERIAL_NO(class_serial_num);
    CHECK_TRACE_SERIAL_NO(trace_serial_num);
    if ( gdata->output_format == 'b') {
        HprofType kind;
	jint size;
	
	type_array(sig, &kind, &size);
	write_u1(kind);
        write_u4(class_serial_num);
        write_u4(trace_serial_num);
        write_u4(n_live_bytes);
        write_u4(n_live_instances);
        write_u4(n_alloced_bytes);
        write_u4(n_alloced_instances);
    } else {
	char *class_name;

	class_name = signature_to_name(sig);
        write_printf("%5u %5.2f%% %5.2f%% %9u %4u %9u %5u %5u %s\n",
                     index,
                     ratio * 100.0,
                     accum_percent * 100.0,
                     n_live_bytes,
                     n_live_instances,
                     n_alloced_bytes,
                     n_alloced_instances,
                     trace_serial_num,
                     class_name);
	HPROF_FREE(class_name);    
    }
}

void
io_write_sites_footer(void)
{
    if (gdata->output_format == 'b') {
        not_implemented();
    } else {
        write_printf("SITES END\n");
    }
}

void
io_write_thread_start(SerialNumber thread_serial_num, 
			ObjectIndex thread_obj_id,
			SerialNumber trace_serial_num, char *thread_name,
			char *thread_group_name, char *thread_parent_name)
{
    CHECK_THREAD_SERIAL_NO(thread_serial_num);
    CHECK_TRACE_SERIAL_NO(trace_serial_num);
    if (gdata->output_format == 'b') {
        IoNameIndex tname_index;
        IoNameIndex gname_index;
        IoNameIndex pname_index;
        
        tname_index = write_name_first(thread_name);
        gname_index = write_name_first(thread_group_name);
        pname_index = write_name_first(thread_parent_name);
        write_header(HPROF_START_THREAD, ((jint)sizeof(ObjectIndex) * 4) + (4 * 2));
        write_u4(thread_serial_num);
        write_index_id(thread_obj_id);
        write_u4(trace_serial_num);
        write_index_id(tname_index);
        write_index_id(gname_index);
        write_index_id(pname_index);
    
    } else if ( (!gdata->cpu_timing) || (!gdata->old_timing_format)) {
        /* We don't want thread info for the old prof output format */
        write_printf("THREAD START "
                     "(obj=%x, id = %d, name=\"%s\", group=\"%s\")\n",
                     thread_obj_id, thread_serial_num,
                     (thread_name==NULL?"":thread_name), 
		     (thread_group_name==NULL?"":thread_group_name));
    }
}

void
io_write_thread_end(SerialNumber thread_serial_num)
{
    CHECK_THREAD_SERIAL_NO(thread_serial_num);
    if (gdata->output_format == 'b') {
        write_header(HPROF_END_THREAD, 4);
        write_u4(thread_serial_num);
    
    } else if ( (!gdata->cpu_timing) || (!gdata->old_timing_format)) {
        /* we don't want thread info for the old prof output format */
        write_printf("THREAD END (id = %d)\n", thread_serial_num);
    }
}

void
io_write_frame(FrameIndex index, char *mname, char *msig, char *sname,
	    SerialNumber class_serial_num, jint lineno)
{
    CHECK_CLASS_SERIAL_NO(class_serial_num);
    if (gdata->output_format == 'b') {
        IoNameIndex mname_index;
        IoNameIndex msig_index;
        IoNameIndex sname_index;
        
        mname_index = write_name_first(mname);
        msig_index  = write_name_first(msig);
        sname_index = write_name_first(sname);

        write_header(HPROF_FRAME, ((jint)sizeof(ObjectIndex) * 4) + (4 * 2));
        write_index_id(index);
        write_index_id(mname_index);
        write_index_id(msig_index);
        write_index_id(sname_index);
        write_u4(class_serial_num);
        write_u4(lineno);
    }
}

void
io_write_trace_header(SerialNumber trace_serial_num, 
			SerialNumber thread_serial_num, jint n_frames)
{
    CHECK_TRACE_SERIAL_NO(trace_serial_num);
    if (gdata->output_format == 'b') {
        write_header(HPROF_TRACE, ((jint)sizeof(ObjectIndex) * n_frames) + (4 * 3));
        write_u4(trace_serial_num);
        write_u4(thread_serial_num);
        write_u4(n_frames);
    } else {
        write_printf("TRACE %u:", trace_serial_num);
        if (thread_serial_num) {
            write_printf(" (thread=%d)", thread_serial_num);
        }
        write_printf("\n");
        if (n_frames == 0) {
            write_printf("\t<empty>\n");
        }
    }
}

void
io_write_trace_elem(FrameIndex frame_index, char *csig, char *mname,
		    char *sname, jint lineno)
{
    if (gdata->output_format == 'b') {
        write_index_id(frame_index);
    } else {
	char *class_name;
        char linebuf[32];

        if (lineno == -2) {
            (void)md_snprintf(linebuf, sizeof(linebuf), "Compiled method");
        } else if (lineno == -3) {
            (void)md_snprintf(linebuf, sizeof(linebuf), "Native method");
        } else if (lineno == -1) {
            (void)md_snprintf(linebuf, sizeof(linebuf), "Unknown line");
        } else {
            (void)md_snprintf(linebuf, sizeof(linebuf), "%d", lineno);
        }
	linebuf[sizeof(linebuf)-1] = 0;
	class_name = signature_to_name(csig);
	if ( mname == NULL ) {
	    mname = "<Unknown Method>";
	}
	if ( sname == NULL ) {
	    sname = "<Unknown Source>";
	}
        write_printf("\t%s.%s(%s:%s)\n", class_name, mname, sname, linebuf);
	HPROF_FREE(class_name);    
    }
}

void
io_write_trace_footer(void)
{
}

#define CPU_SAMPLES_RECORD_NAME ("CPU SAMPLES")
#define CPU_TIMES_RECORD_NAME ("CPU TIME (ms)")

void
io_write_cpu_samples_header(jlong total_cost, jint n_items)
{

    if (gdata->output_format == 'b') {
        write_header(HPROF_CPU_SAMPLES, (n_items * (4 * 2)) + (4 * 2));
        write_u4((jint)total_cost);
        write_u4(n_items);
    } else {
        time_t t;
	char *record_name;

	if ( gdata->cpu_sampling ) {
            record_name = CPU_SAMPLES_RECORD_NAME;
	} else {
	    record_name = CPU_TIMES_RECORD_NAME;
	}
        t = time(0);
        write_printf("%s BEGIN (total = %d) %s", record_name,
                     /*jlong*/(int)total_cost, ctime(&t));
        if ( n_items > 0 ) {
	    write_printf("rank   self  accum   count trace method\n");
	}
    }
}

void
io_write_cpu_samples_elem(jint index, double percent, double accum,
		jint num_hits, jlong cost, SerialNumber trace_serial_num,
		jint n_frames, char *csig, char *mname)
{
    CHECK_TRACE_SERIAL_NO(trace_serial_num);
    if (gdata->output_format == 'b') {
        write_u4((jint)cost);
        write_u4(trace_serial_num);
    } else {
        write_printf("%4u %5.2f%% %5.2f%% %7u %5u",
                     index, percent, accum, num_hits,
                     trace_serial_num);
        if (n_frames > 0) {
	    char * class_name;
	    
	    class_name = signature_to_name(csig);
            write_printf(" %s.%s\n", class_name, mname);
	    HPROF_FREE(class_name);    
        } else {
            write_printf(" <empty trace>\n");
        }
    }
}

void
io_write_cpu_samples_footer(void)
{
    if (gdata->output_format == 'b') {
        not_implemented();
    } else {
	char *record_name;

	if ( gdata->cpu_sampling ) {
            record_name = CPU_SAMPLES_RECORD_NAME;
	} else {
	    record_name = CPU_TIMES_RECORD_NAME;
	}
	write_printf("%s END\n", record_name);
    }
}

void
io_write_heap_summary(jlong total_live_bytes, jlong total_live_instances,
		jlong total_alloced_bytes, jlong total_alloced_instances)
{
    if (gdata->output_format == 'b') {
        write_header(HPROF_HEAP_SUMMARY, 4 * 6);
        write_u4((jint)total_live_bytes);
        write_u4((jint)total_live_instances);
        write_u8(total_alloced_bytes);
        write_u8(total_alloced_instances);
    }
}

void
io_write_oldprof_header(void)
{
    if ( gdata->old_timing_format ) {
        write_printf("count callee caller time\n");
    }
}

void
io_write_oldprof_elem(jint num_hits, jint num_frames, char *csig_callee,
	    char *mname_callee, char *msig_callee, char *csig_caller,
	    char *mname_caller, char *msig_caller, jlong cost)
{
    if ( gdata->old_timing_format ) {
	char * class_name_callee;
	char * class_name_caller;
	
	class_name_callee = signature_to_name(csig_callee);
	class_name_caller = signature_to_name(csig_caller);
        write_printf("%d ", num_hits);
        if (num_frames >= 1) {
            write_printf("%s.%s%s ", class_name_callee,
		 mname_callee,  msig_callee);
        } else {
            write_printf("%s ", "<unknown callee>");
        }
        if (num_frames > 1) {
            write_printf("%s.%s%s ", class_name_caller,
		 mname_caller,  msig_caller);
        } else {
            write_printf("%s ", "<unknown caller>");
        }
        write_printf("%d\n", (int)cost);
	HPROF_FREE(class_name_callee);
	HPROF_FREE(class_name_caller);
    }
}

void
io_write_oldprof_footer(void)
{
}

void
io_write_monitor_header(jlong total_time)
{
    if (gdata->output_format == 'b') {
	not_implemented();
    } else {
        time_t t = time(0);
        
        t = time(0);
        write_printf("MONITOR TIME BEGIN (total = %u ms) %s",
                                (int)total_time, ctime(&t));
        if (total_time > 0) {
            write_printf("rank   self  accum   count trace monitor\n");
        }
    }
}

void
io_write_monitor_elem(jint index, double percent, double accum,
	    jint num_hits, SerialNumber trace_serial_num, char *sig)
{
    CHECK_TRACE_SERIAL_NO(trace_serial_num);
    if (gdata->output_format == 'b') {
	not_implemented();
    } else {
        char *class_name;
	
        class_name = signature_to_name(sig);
	write_printf("%4u %5.2f%% %5.2f%% %7u %5u %s (Java)\n",
                     index, percent, accum, num_hits,
                     trace_serial_num, class_name);
	HPROF_FREE(class_name);    
    }
}

void
io_write_monitor_footer(void)
{
    if (gdata->output_format == 'b') {
	not_implemented();
    } else {
        write_printf("MONITOR TIME END\n");
    }
}

void
io_write_monitor_sleep(jlong timeout, SerialNumber thread_serial_num)
{
    if (gdata->output_format == 'b') {
	not_implemented();
    } else {
	if ( thread_serial_num == 0 ) {
	    write_printf("SLEEP: timeout=%d, <unknown thread>\n",
			(int)timeout);
	} else {
            CHECK_THREAD_SERIAL_NO(thread_serial_num);
	    write_printf("SLEEP: timeout=%d, thread %d\n",
			(int)timeout, thread_serial_num);
	}
    }
}

void
io_write_monitor_wait(char *sig, jlong timeout, 
		SerialNumber thread_serial_num)
{
    if (gdata->output_format == 'b') {
	not_implemented();
    } else {
	if ( thread_serial_num == 0 ) {
	    write_printf("WAIT: MONITOR %s, timeout=%d, <unknown thread>\n",
			sig, (int)timeout);
	} else {
            CHECK_THREAD_SERIAL_NO(thread_serial_num);
	    write_printf("WAIT: MONITOR %s, timeout=%d, thread %d\n",
			sig, (int)timeout, thread_serial_num);
	}
    }
}

void
io_write_monitor_waited(char *sig, jlong time_waited, 
		SerialNumber thread_serial_num)
{
    if (gdata->output_format == 'b') {
	not_implemented();
    } else {
	if ( thread_serial_num == 0 ) {
	    write_printf("WAITED: MONITOR %s, time_waited=%d, <unknown thread>\n",
			sig, (int)time_waited);
	} else {
            CHECK_THREAD_SERIAL_NO(thread_serial_num);
	    write_printf("WAITED: MONITOR %s, time_waited=%d, thread %d\n",
			sig, (int)time_waited, thread_serial_num);
	}
    }
}

void
io_write_monitor_exit(char *sig, SerialNumber thread_serial_num)
{
    if (gdata->output_format == 'b') {
	not_implemented();
    } else {
	if ( thread_serial_num == 0 ) {
	    write_printf("EXIT: MONITOR %s, <unknown thread>\n", sig);
	} else {
            CHECK_THREAD_SERIAL_NO(thread_serial_num);
	    write_printf("EXIT: MONITOR %s, thread %d\n",
			sig, thread_serial_num);
	}
    }
}

void
io_write_monitor_dump_header(void)
{
    if (gdata->output_format == 'b') {
	not_implemented();
    } else {
        write_printf("MONITOR DUMP BEGIN\n");
    }
}

void
io_write_monitor_dump_thread_state(SerialNumber thread_serial_num, 
		      SerialNumber trace_serial_num,
                      jint threadState)
{
    CHECK_THREAD_SERIAL_NO(thread_serial_num);
    CHECK_TRACE_SERIAL_NO(trace_serial_num);
    if (gdata->output_format == 'b') {
	not_implemented();
    } else {
	char tstate[20];

	tstate[0] = 0;

	if (threadState & JVMTI_THREAD_STATE_SUSPENDED) {
	    (void)strcat(tstate,"S|");
	}
	if (threadState & JVMTI_THREAD_STATE_INTERRUPTED) {
	    (void)strcat(tstate,"intr|");
	}
	if (threadState & JVMTI_THREAD_STATE_IN_NATIVE) {
	    (void)strcat(tstate,"native|");
	}
	if ( ! ( threadState & JVMTI_THREAD_STATE_ALIVE ) ) {
	    if ( threadState & JVMTI_THREAD_STATE_TERMINATED ) {
		(void)strcat(tstate,"ZO");
	    } else {
		(void)strcat(tstate,"NS");
	    }
	} else {
	    if ( threadState & JVMTI_THREAD_STATE_SLEEPING ) {
		(void)strcat(tstate,"SL");
	    } else if ( threadState & JVMTI_THREAD_STATE_BLOCKED_ON_MONITOR_ENTER ) {
		(void)strcat(tstate,"MW");
	    } else if ( threadState & JVMTI_THREAD_STATE_WAITING ) {
		(void)strcat(tstate,"CW");
	    } else if ( threadState & JVMTI_THREAD_STATE_RUNNABLE ) {
		(void)strcat(tstate,"R");
	    } else {
		(void)strcat(tstate,"UN");
	    }
	}
	write_printf("    THREAD %d, trace %d, status: %s\n",
		     thread_serial_num, trace_serial_num, tstate);
    }
}

void
io_write_monitor_dump_state(char *sig, SerialNumber thread_serial_num,
		    jint entry_count,
		    SerialNumber *waiters, jint waiter_count,
		    SerialNumber *notify_waiters, jint notify_waiter_count)
{
    if (gdata->output_format == 'b') {
	not_implemented();
    } else {
	int i;

        if ( thread_serial_num != 0 ) {
            CHECK_THREAD_SERIAL_NO(thread_serial_num);
	    write_printf("    MONITOR %s\n", sig);
	    write_printf("\towner: thread %d, entry count: %d\n", 
		thread_serial_num, entry_count);
	} else {
	    write_printf("    MONITOR %s unowned\n", sig);
	}
	write_printf("\twaiting to enter:");
	for (i = 0; i < waiter_count; i++) {
            write_thread_serial_number(waiters[i], 
				(i != (waiter_count-1)));
	}
	write_printf("\n");
	write_printf("\twaiting to be notified:");
	for (i = 0; i < notify_waiter_count; i++) {
            write_thread_serial_number(notify_waiters[i], 
				(i != (notify_waiter_count-1)));
	}
	write_printf("\n");
    }
}

void
io_write_monitor_dump_footer(void)
{
    if (gdata->output_format == 'b') {
	not_implemented();
    } else {
        write_printf("MONITOR DUMP END\n");
    }
}

/* ----------------------------------------------------------------- */
/* These functions write to a separate file */

void
io_heap_header(jlong total_live_instances, jlong total_live_bytes)
{
    if (gdata->output_format != 'b') {
	time_t t;
	
	t = time(0);
	heap_printf("HEAP DUMP BEGIN (%u objects, %u bytes) %s",
			/*jlong*/(int)total_live_instances, 
			/*jlong*/(int)total_live_bytes, ctime(&t));
    }
}

void
io_heap_root_thread_object(ObjectIndex thread_obj_id, 
		SerialNumber thread_serial_num, SerialNumber trace_serial_num)
{
    CHECK_THREAD_SERIAL_NO(thread_serial_num);
    CHECK_TRACE_SERIAL_NO(trace_serial_num);
    if (gdata->output_format == 'b') {
	 heap_u1(HPROF_GC_ROOT_THREAD_OBJ);
	 heap_id(thread_obj_id);
	 heap_u4(thread_serial_num);
	 heap_u4(trace_serial_num);
    } else {
	heap_printf("ROOT %x (kind=<thread>, id=%u, trace=%u)\n",
		     thread_obj_id, thread_serial_num, trace_serial_num);
    }
}

void
io_heap_root_unknown(ObjectIndex obj_id)
{
    if (gdata->output_format == 'b') {
	heap_u1(HPROF_GC_ROOT_UNKNOWN);
	heap_id(obj_id);
    } else {
	heap_printf("ROOT %x (kind=<unknown>)\n", obj_id);
    }
}

void
io_heap_root_jni_global(ObjectIndex obj_id, SerialNumber gref_serial_num, 
			 SerialNumber trace_serial_num)
{
    CHECK_TRACE_SERIAL_NO(trace_serial_num);
    if (gdata->output_format == 'b') {
	heap_u1(HPROF_GC_ROOT_JNI_GLOBAL);
	heap_id(obj_id);
	heap_id(gref_serial_num);
    } else {
	heap_printf("ROOT %x (kind=<JNI global ref>, "
		     "id=%x, trace=%u)\n",
		     obj_id, gref_serial_num, trace_serial_num);
    }
}

void
io_heap_root_jni_local(ObjectIndex obj_id, SerialNumber thread_serial_num, 
	jint frame_depth)
{
    CHECK_THREAD_SERIAL_NO(thread_serial_num);
    if (gdata->output_format == 'b') {
	heap_u1(HPROF_GC_ROOT_JNI_LOCAL);
	heap_id(obj_id);
	heap_u4(thread_serial_num);
	heap_u4(frame_depth);
    } else {
	heap_printf("ROOT %x (kind=<JNI local ref>, "
		     "thread=%u, frame=%d)\n",
		     obj_id, thread_serial_num, frame_depth);
    }
}

void
io_heap_root_system_class(ObjectIndex obj_id, char *sig)
{
    if (gdata->output_format == 'b') {
	heap_u1(HPROF_GC_ROOT_STICKY_CLASS);
	heap_id(obj_id);
    } else {
	char *class_name;
	
	class_name = signature_to_name(sig);
	heap_printf("ROOT %x (kind=<system class>, name=%s)\n",
		     obj_id, class_name);
	HPROF_FREE(class_name);
    }
}

void
io_heap_root_monitor(ObjectIndex obj_id)
{
    if (gdata->output_format == 'b') {
	heap_u1(HPROF_GC_ROOT_MONITOR_USED);
	heap_id(obj_id);
    } else {
	heap_printf("ROOT %x (kind=<busy monitor>)\n", obj_id);
    }
}

void
io_heap_root_thread(ObjectIndex obj_id, SerialNumber thread_serial_num)
{
    CHECK_THREAD_SERIAL_NO(thread_serial_num);
    if (gdata->output_format == 'b') {
	heap_u1(HPROF_GC_ROOT_THREAD_BLOCK);
	heap_id(obj_id);
	heap_u4(thread_serial_num);
    } else {
	heap_printf("ROOT %x (kind=<thread block>, thread=%u)\n",
		     obj_id, thread_serial_num);
    }
}

void
io_heap_root_java_frame(ObjectIndex obj_id, SerialNumber thread_serial_num, 
	jint frame_depth)
{
    CHECK_THREAD_SERIAL_NO(thread_serial_num);
    if (gdata->output_format == 'b') {
	heap_u1(HPROF_GC_ROOT_JAVA_FRAME);
	heap_id(obj_id);
	heap_u4(thread_serial_num);
	heap_u4(frame_depth);
    } else {
	heap_printf("ROOT %x (kind=<Java stack>, "
		     "thread=%u, frame=%d)\n",
		     obj_id, thread_serial_num, frame_depth);
    }
}

void
io_heap_root_native_stack(ObjectIndex obj_id, SerialNumber thread_serial_num)
{
    CHECK_THREAD_SERIAL_NO(thread_serial_num);
    if (gdata->output_format == 'b') {
	heap_u1(HPROF_GC_ROOT_NATIVE_STACK);
	heap_id(obj_id);
	heap_u4(thread_serial_num);
    } else {
	heap_printf("ROOT %x (kind=<native stack>, thread=%u)\n",
		     obj_id, thread_serial_num);
    }
}

static jboolean
is_static_field(jint modifiers)
{
    if ( modifiers & JVM_ACC_STATIC ) {
	return JNI_TRUE;
    }
    return JNI_FALSE;
}

static jboolean
is_inst_field(jint modifiers)
{
    if ( modifiers & JVM_ACC_STATIC ) {
	return JNI_FALSE;
    }
    return JNI_TRUE;
}

void
io_heap_class_dump(ClassIndex cnum, char *sig, ObjectIndex class_id, 
		SerialNumber trace_serial_num, 
		ObjectIndex super_id, ObjectIndex loader_id, 
		ObjectIndex signers_id, ObjectIndex domain_id, 
		jint size, 
		jint n_cpool, ConstantPoolValue *cpool,
		jint n_fields, FieldInfo *fields, jvalue *fvalues)
{
    CHECK_TRACE_SERIAL_NO(trace_serial_num);
    if (gdata->output_format == 'b') {
	int  i;
	jint n_static_fields;
	jint n_inst_fields;
	jint inst_size;
	jint saved_inst_size;
	
	n_static_fields = 0;
	n_inst_fields = 0;
	inst_size = 0;

	/* These do NOT go into the heap output */
	for ( i = 0 ; i < n_fields ; i++ ) {
	    char *field_name;
	    
	    field_name = string_get(fields[i].name_index);
	    if ( is_static_field(fields[i].modifiers) ) {
		(void)write_name_first(field_name);
		n_static_fields++;
	    } 
	    if ( is_inst_field(fields[i].modifiers) ) {
		HprofType kind;
		jint size;

		type_from_signature(string_get(fields[i].sig_index), 
				    &kind, &size);
		inst_size += size;
		if ( fields[i].cnum == cnum ) {
		    (void)write_name_first(field_name);
		    n_inst_fields++;
		}
	    }
	}
	
	/* Verify that the instance size we have calculated as we went
	 *   through the fields, matches what is saved away with this
	 *   class.
	 */
	saved_inst_size = class_get_inst_size(cnum);
	if ( saved_inst_size == -1 ) {
	    class_set_inst_size(cnum, inst_size);
	} else if ( saved_inst_size != inst_size ) {
	    HPROF_ERROR(JNI_TRUE, "Mis-match on instance size in class dump");
	}

	heap_u1(HPROF_GC_CLASS_DUMP);
	heap_id(class_id);
	heap_u4(trace_serial_num);
	heap_id(super_id);
	heap_id(loader_id);
	heap_id(signers_id);
	heap_id(domain_id);
	heap_id(0);
	heap_id(0);
	heap_u4(inst_size); /* Must match inst_size in instance dump */
	
	heap_u2((unsigned short)n_cpool);
	for ( i = 0 ; i < n_cpool ; i++ ) {
	    HprofType kind;
	    jint size;

	    type_from_signature(string_get(cpool[i].sig_index), 
			    &kind, &size);
	    heap_u2((unsigned short)(cpool[i].constant_pool_index));
	    heap_u1(kind);
	    heap_element(kind, size, cpool[i].value);
	}
	
	heap_u2((unsigned short)n_static_fields);
	for ( i = 0 ; i < n_fields ; i++ ) {
	    if ( is_static_field(fields[i].modifiers) ) {
	        char *field_name;
		HprofType kind;
		jint size;

		type_from_signature(string_get(fields[i].sig_index), 
				&kind, &size);
	        field_name = string_get(fields[i].name_index);
		heap_name(field_name);
		heap_u1(kind);
		heap_element(kind, size, fvalues[i]);
	    }
	}
	
	heap_u2((unsigned short)n_inst_fields); /* Does not include super class */
	for ( i = 0 ; i < n_fields ; i++ ) {
	    if ( is_inst_field(fields[i].modifiers) && 
		 fields[i].cnum == cnum ) {
		HprofType kind;
		jint size;
		char *field_name;
		
		field_name = string_get(fields[i].name_index);
		type_from_signature(string_get(fields[i].sig_index), 
			    &kind, &size);
		heap_name(field_name);
		heap_u1(kind);
	    }
	}
    } else {
	char * class_name;
        int i;

	class_name = signature_to_name(sig);
	heap_printf("CLS %x (name=%s, trace=%u)\n",
		     class_id, class_name, trace_serial_num);
	HPROF_FREE(class_name);
	if (super_id) {
	    heap_printf("\tsuper\t\t%x\n", super_id);
	}
	if (loader_id) {
	    heap_printf("\tloader\t\t%x\n", loader_id);
	}
	if (signers_id) {
	    heap_printf("\tsigners\t\t%x\n", signers_id);
	}
	if (domain_id) {
	    heap_printf("\tdomain\t\t%x\n", domain_id);
	}
	for ( i = 0 ; i < n_fields ; i++ ) {
	    if ( is_static_field(fields[i].modifiers) ) {
		HprofType kind;
		jint size;
		
		type_from_signature(string_get(fields[i].sig_index), 
				&kind, &size);
		if ( !type_is_primitive(kind) ) {
		    if (fvalues[i].i != 0 ) {
	                char *field_name;
	    
	                field_name = string_get(fields[i].name_index);
			heap_printf("\tstatic %s\t%x\n", field_name,
			    fvalues[i].i);
		    }
		}
	    }
	}
    }
}

/* Dump the instance fields in the right order. */
static void
dump_instance_fields(ClassIndex cnum, 
		     FieldInfo *fields, jvalue *fvalues, jint n_fields)
{
    ClassIndex super_cnum;
    int        i;

    HPROF_ASSERT(cnum!=0);

    for (i = 0; i < n_fields; i++) {
	if ( fields[i].cnum == cnum && is_inst_field(fields[i].modifiers) ) {
	    HprofType kind;
	    int size;
    
	    type_from_signature(string_get(fields[i].sig_index), 
			    &kind, &size);
	    heap_element(kind, size, fvalues[i]);
	}
    }

    super_cnum = class_get_super(cnum);
    if ( super_cnum != 0 ) {
        dump_instance_fields(super_cnum, fields, fvalues, n_fields);
    }

}

void
io_heap_instance_dump(ClassIndex cnum, ObjectIndex obj_id, 
		SerialNumber trace_serial_num,  
		ObjectIndex class_id, jint size, char *sig,
		FieldInfo *fields, jvalue *fvalues, jint n_fields)
{
    CHECK_TRACE_SERIAL_NO(trace_serial_num);
    if (gdata->output_format == 'b') {
	jint inst_size;
	jint saved_inst_size;
	int  i;

	inst_size = 0;
	for (i = 0; i < n_fields; i++) {
	    if ( is_inst_field(fields[i].modifiers) ) {
		HprofType kind;
		int       size;
	    
		type_from_signature(string_get(fields[i].sig_index), 
				&kind, &size);
		inst_size += size;
	    }
	    
	}
	
	/* Verify that the instance size we have calculated as we went
	 *   through the fields, matches what is saved away with this
	 *   class.
	 */
	saved_inst_size = class_get_inst_size(cnum);
	if ( saved_inst_size == -1 ) {
	    class_set_inst_size(cnum, inst_size);
	} else if ( saved_inst_size != inst_size ) {
	    HPROF_ERROR(JNI_TRUE, "Mis-match on instance size in instance dump");
	}
	
	heap_u1(HPROF_GC_INSTANCE_DUMP);
	heap_id(obj_id);
	heap_u4(trace_serial_num);
	heap_id(class_id);
	heap_u4(inst_size); /* Must match inst_size in class dump */
	
        /* Order must be class, super, super's super, ... */
	dump_instance_fields(cnum, fields, fvalues, n_fields);
    } else {
	char * class_name;
	int i;

	class_name = signature_to_name(sig);
	heap_printf("OBJ %x (sz=%u, trace=%u, class=%s@%x)\n",
		     obj_id, size, trace_serial_num, class_name, class_id);
        HPROF_FREE(class_name);
	    
	for (i = 0; i < n_fields; i++) {
	    if ( is_inst_field(fields[i].modifiers) ) {
		HprofType kind;
		int size;

		type_from_signature(string_get(fields[i].sig_index), 
			    &kind, &size);
		if ( !type_is_primitive(kind) ) {
		    if (fvalues[i].i != 0 ) {
			char *sep;
			ObjectIndex val_id;
			char *field_name;
		    
			field_name = string_get(fields[i].name_index);
			val_id =  (ObjectIndex)(fvalues[i].i);
			sep = strlen(field_name) < 8 ? "\t" : "";
			heap_printf("\t%s\t%s%x\n", field_name, sep, val_id);
		    }
		}
	    }
	}
    }
}

void
io_heap_object_array(ObjectIndex obj_id, SerialNumber trace_serial_num, 
		jint size, jint num_elements, ObjectIndex class_id, 
		jvalue *values, char *sig)
{
    CHECK_TRACE_SERIAL_NO(trace_serial_num);
    if (gdata->output_format == 'b') {

	heap_u1(HPROF_GC_OBJ_ARRAY_DUMP);
	heap_id(obj_id);
	heap_u4(trace_serial_num);
	heap_u4(num_elements);
	heap_id(class_id);
	heap_elements(HPROF_NORMAL_OBJECT, num_elements, 
		(jint)sizeof(ObjectIndex), values);
    } else {
	char *name;
	int i;

	name = signature_to_name(sig);
	heap_printf("ARR %x (sz=%u, trace=%u, nelems=%u, elem type=%s@%x)\n",
		     obj_id, size, trace_serial_num, num_elements, 
		     name, class_id);
	for (i = 0; i < num_elements; i++) {  
	    ObjectIndex id;
	    
	    id = (ObjectIndex)values[i].i;
	    if (id != 0) {
		heap_printf("\t[%u]\t\t%x\n", i, id);
	    }
	}
	HPROF_FREE(name);
    }
}

void
io_heap_prim_array(ObjectIndex obj_id, jint size, 
			SerialNumber trace_serial_num, 
			jint num_elements, char *sig, jvalue *values)
{
    CHECK_TRACE_SERIAL_NO(trace_serial_num);
    if (gdata->output_format == 'b') {
	HprofType kind;
	jint  esize;

	type_array(sig, &kind, &esize);
	heap_u1(HPROF_GC_PRIM_ARRAY_DUMP);
	heap_id(obj_id);
	heap_u4(trace_serial_num);
	heap_u4(num_elements);
	heap_u1(kind);
	heap_elements(kind, num_elements, esize, values);
    } else {
	char *name;

	name = signature_to_name(sig);
	heap_printf("ARR %x (sz=%u, trace=%u, nelems=%u, elem type=%s)\n",
		     obj_id, size, trace_serial_num, num_elements, name);
	HPROF_FREE(name);
    }
}

void
io_heap_footer(void)
{
    char *buf;
    int   buf_len;
    jlong bytes_written;
    int   nbytes;
    int   left;
    int   fd;
    
    HPROF_ASSERT(gdata->heap_fd >= 0);
    
    /* Flush all bytes to the heap dump file */
    heap_flush();
    
    /* We kept track of how many bytes we wrote out. */
    bytes_written = gdata->heap_write_count;
 
    /* Re-open in proper way, binary vs. ascii is important */
    if (gdata->output_format == 'b') {
	/* Write header for binary heap dump (don't know size until now) */
	write_header(HPROF_HEAP_DUMP, (jint)bytes_written);
        
	fd = md_open_binary(gdata->heapfilename);
    } else {
        fd = md_open(gdata->heapfilename);
    }
    HPROF_ASSERT(fd >= 0);

    /* Move contents of this file into output file. */
    buf_len = FILE_IO_BUFFER_SIZE*2; /* Twice as big! */
    buf = HPROF_MALLOC(buf_len);
    HPROF_ASSERT(buf!=NULL);

    /* Keep track of how many we have left */
    left = (int)bytes_written;
    do {
	int count;

	count = buf_len;
	if ( count > left ) count = left;
	nbytes = md_read(fd, buf, count);
	if (nbytes < 0) {
	    system_error("read", nbytes, errno);
	    break;
	}
	if (nbytes == 0) {
	    break;
	}
	if ( nbytes > 0 ) {
	    write_raw(buf, nbytes);
	    left -= nbytes;
	}
    } while ( left > 0 );
    
    if (left > 0 && nbytes == 0) {
	HPROF_ERROR(JNI_TRUE, "File size is smaller than bytes written");
    }
    
    HPROF_FREE(buf);
    md_close(fd);

    /* Clear the byte counte and reset the file. */
    gdata->heap_write_count = (jlong)0;
    if ( md_seek(gdata->heap_fd, (jlong)0) != (jlong)0 ) {
	HPROF_ERROR(JNI_TRUE, "Cannot seek to beginning of heap info file");
    }
  
    /* For ascii mode, write out the final message */
    if (gdata->output_format != 'b') {
	write_printf("HEAP DUMP END\n");
    }
     
}



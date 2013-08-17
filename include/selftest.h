/*
 * @(#)selftest.h	1.6 98/01/12
 *
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

#ifndef	_SELFTEST_H_
#define	_SELFTEST_H_

/* 	Structures and typedefs in support of the Java runtime self-test. */

enum binop_operations
{
  	BINOP_ADD,
  	BINOP_SUB,
  	BINOP_MUL,
  	BINOP_DIV,
  	BINOP_REM,
  	BINOP_AND,
  	BINOP_OR,
  	BINOP_XOR
};

enum unop_operations
{
  	UNOP_NEG,
  	UNOP_SHL,
  	UNOP_ASHR, 	/* arithmetic shift right (with sign-extension) */
  	UNOP_LSHR 	/* logical shift right (no sign-extension) */
};

enum cmp_operations
{
  	CMP_EQ,
	CMP_LT,
	CMP_LE,
	CMP_NE,
	CMP_GT,
	CMP_GE,
	CMP_NULL,
	CMP_NONNULL
};

enum init_status
{
 	INIT_OK,		
	INIT_UNIMP,		/* initializer is not written yet */
	INIT_ERROR 	/* an error occurred during initialization */
};

enum check_status
{
	CHECK_PASS,		/* test passed */
	CHECK_FAIL,		/* test failed */
	CHECK_UNIMP,	/* test is not written yet */
	CHECK_ERROR	/* an error occurred during checking */
};

enum word_order
{
	little,			/* loword first, then hiword */
	big			/* hiword first, then loword */
};

enum types
{
	TYPE_BOOLEAN = 4, 	/* The number 4 is from VM Spec, 3.6 */
	TYPE_CHAR,
	TYPE_FLOAT,
	TYPE_DOUBLE,
	TYPE_BYTE,
	TYPE_SHORT,
	TYPE_INT,
	TYPE_LONG,
	TYPE_OBJECT,	/* dummy type I made up for testing convenience */
	TYPE_NUMTYPES	/* keep me last */
};

/* 	Field names correspond to the names given to fields in SelfTest.java.
	If you change names or add new fields in SelfTest.java be sure to
	reflect the changes here. */
enum fields
{
	field_i1,		/* local integer field */
	field_i2,		
	field_f1,		/* local float field */
	field_f2,		
	field_d1,		/* local double field */
	field_d2,
	field_l1,		/* local longlong field */
	field_l2,		
	field_si1,		/* static integer field */
	field_si2,		
	field_sf1,		/* static float field */
	field_sf2,		
	field_sd1,		/* static double field */
	field_sd2,		
	field_sl1,		/* static float field */
	field_sl2,	
	method_set_i1,
	method_set_i2,
	method_set_f1,
	method_set_f2,
	method_set_d1,
	method_set_d2,
	method_set_l1,
	method_set_l2,
	method_set_si1,
	method_set_si2,
	method_set_sf1,
	method_set_sf2,
	method_set_sd1,
	method_set_sd2,
	method_set_sl1,
	method_set_sl2,
	method_test_areturn,
	method_test_athrow1,
	method_test_athrow2,
	method_test_athrow3,
	method_test_athrow4,
	method_test_an_interface,
	method_equals,
	FIELD_NUMFIELDS 
};

/* The structure that defines a single self-test. */
struct opcode_test
{
    /* The Java opcode */
    unsigned char opcode;

    /* A pointer to a function to initialize the PC and ExecEnv for the test */
    int (* initer) (unsigned char **, ExecEnv *, int, int);

    /* A pointer to a function to check the stack after executing the opcode */
    int (* checker) (unsigned char *, ExecEnv *, int, int);

    /* Optional parameter for polymorphic initer/checkers like iload */
    int param;

    /* Optional parameter for wide/notwide opcodes like goto */
    int wide;
};

/* 	A simple structure to store some info that describes test objects.

	The order of the elements in the objcheck[] array follows the order
	of "enum fields", e.g. objcheck[method_set_si1] will contain 
	info about the method "set_s1".
		
	The objcheck[] array is initialized in SetupTstObjects. */
struct objcheck
{
	/* 	Pointer to fieldblock within class's fields[] array */
	struct fieldblock *fb;

	/* 	For methods, pointer to methodblock within class's methods[] array */
	struct methodblock *mb;

	/* Index of this field within constant pool */
	int cpindex;
};

#define fieldblock(field)	objcheck[field].fb
#define methodblock(field)	objcheck[field].mb
#define methodoffset(field) objcheck[field].fb->u.offset
#define objoffset(field) 	(objcheck[field].fb->u.offset / sizeof(long))
#define staticvalue(field)	objcheck[field].fb->u.static_value
#define staticaddr(field)	objcheck[field].fb->u.static_address
#define fldname(field)		objcheck[field].fb->name
#define cpindex(field)		objcheck[field].cpindex

/* 	A simple structure to store the object created to represent an array
	and its expected size.

	The order of the elements in the arraycheck[] array follows the order
	of "enum types", e.g. arraycheck[TYPE_FLOAT] will contain an object
	that is expected to describe an array of float.
		
	The arraycheck[] array is initialized in newarray_check1. Used by 
	newarray_check1, plus the complete set of array_load and array_store 
	tests. */
struct arraycheck
{
	/* 	Reference to the allocated object */
	JHandle *handle;

	/* 	Number of elements in the array */
	int numelem;
};

/*	A structure and two functions to aid in checking 64-bit
	arithmetic and conversions. */
struct longlong
{
	long msw;
	long lsw;
};

/* 	As initialized in SetupExecEnv, the currently executing frame can 
	count on there being NUMLOCALS locals in the previous frame. */
#define NUMLOCALS 16

/* 	The size of the program text buffer passed to ExecuteJava. */
#define PROGTEXT_SIZE 128

/* 	Define some constants for convenience in the testing that follows */
#define MAGIC_BYTE		123
#define MAGIC_SHORT		1234
#define	MAGIC_WORD		1234567
#define MAGIC_FLOAT		1.2e34
#define MAGIC_DOUBLE	1.2e234
#define MAGIC_OBJECT	tst_object
#define FLOAT_ERROR		1.0e-3
#define DOUBLE_ERROR	1.0e-6

/* 	A list of opcode names, for printing in error messages;
	defined in opcodes.c in the build directory */
extern char * const opnames[];

/*	Test an expression and print a message if FALSE. 
	NOTE: opcode names in the failure message will only be correct if the
	opcode appears as the first character of pc[] (the usual case, but
	not required.) */
#define CHECK(expr) ((expr) ? CHECK_PASS : (fprintf(stderr, "Self-Test %d FAILED, opcode: %s, file: %s, line: %d\n", testno, opnames[selftest_table[testno].opcode], __FILE__, __LINE__) , CHECK_FAIL))

/*	Compare two doubles without using == */
#define DEQUALS(d1, d2) (fabs(1.0 - (d1) / (d2)) < DOUBLE_ERROR)

/*	Compare two floats without using == */
#define FEQUALS(f1, f2) (fabs(1.0 - (f1) / (f2)) < FLOAT_ERROR)

/* 	For debugging the self-test; "verbose" variable comes from -v 
	command-line option; define DEBUG_SELFTEST in the Makefile to 
	always print a results summary even without -v. */
#if defined(DEBUG_SELFTEST)
#define PRINT_RESULTS_SUMMARY 1
#else
#define PRINT_RESULTS_SUMMARY verbose
#endif

/* 	A few miscellaneous prototypes used by the selftest */
static void	SetupExecEnv (ExecEnv *ee);
static void	SetupTstObjects (ExecEnv *ee);
static void SetupWordOrder (unsigned char *pc, ExecEnv *ee);
static void ReportSelfTestResults (int numtests, int numpasses, int numfails);
static cp_item_type *copy_constantpool (cp_item_type *src, int itemcount);
static void free_constantpool (cp_item_type *cp);
static struct longlong get_longlong (char *buf);
static void put_longlong (struct longlong ll, char *buf);
static void push_int (unsigned long ul, unsigned char **pc, ExecEnv *ee);
static void push_float (float f, unsigned char **pc, ExecEnv *ee);
static void push_double (double d, unsigned char **pc, ExecEnv *ee);
static void push_long (struct longlong ll, unsigned char **pc, ExecEnv *ee);

#if defined(DEBUG_SELFTEST)
static void dump_constantpool (char *filename, cp_item_type *cp, int intemcount);
#endif

/* The laundry list of initializers and checkers. */
static int nop_init1 (unsigned char **, ExecEnv *, int, int);
static int nop_check1 (unsigned char *, ExecEnv *, int, int);
static int iconst_init1 (unsigned char **, ExecEnv *, int, int);
static int iconst_check1 (unsigned char *, ExecEnv *, int, int);
static int lconst_init1 (unsigned char **, ExecEnv *, int, int);
static int lconst_check1 (unsigned char *, ExecEnv *, int, int);
static int fconst_init1 (unsigned char **, ExecEnv *, int, int);
static int fconst_check1 (unsigned char *, ExecEnv *, int, int);
static int dconst_init1 (unsigned char **, ExecEnv *, int, int);
static int dconst_check1 (unsigned char *, ExecEnv *, int, int);
static int bipush_init1 (unsigned char **, ExecEnv *, int, int);
static int bipush_check1 (unsigned char *, ExecEnv *, int, int);
static int bipush_init2 (unsigned char **, ExecEnv *, int, int);
static int bipush_check2 (unsigned char *, ExecEnv *, int, int);
static int bipush_init3 (unsigned char **, ExecEnv *, int, int);
static int bipush_check3 (unsigned char *, ExecEnv *, int, int);
static int sipush_init1 (unsigned char **, ExecEnv *, int, int);
static int sipush_check1 (unsigned char *, ExecEnv *, int, int);
static int sipush_init2 (unsigned char **, ExecEnv *, int, int);
static int sipush_check2 (unsigned char *, ExecEnv *, int, int);
static int sipush_init3 (unsigned char **, ExecEnv *, int, int);
static int sipush_check3 (unsigned char *, ExecEnv *, int, int);
static int ldc_init1 (unsigned char **, ExecEnv *, int, int);
static int ldc_check1 (unsigned char *, ExecEnv *, int, int);
static int ldc2_init1 (unsigned char **, ExecEnv *, int, int);
static int ldc2_check1 (unsigned char *, ExecEnv *, int, int);
static int iload_init1 (unsigned char **, ExecEnv *, int, int);
static int iload_check1 (unsigned char *, ExecEnv *, int, int);
static int aload_init1 (unsigned char **, ExecEnv *, int, int);
static int aload_check1 (unsigned char *, ExecEnv *, int, int);
static int lload_init1 (unsigned char **, ExecEnv *, int, int);
static int lload_check1 (unsigned char *, ExecEnv *, int, int);
static int fload_init1 (unsigned char **, ExecEnv *, int, int);
static int fload_check1 (unsigned char *, ExecEnv *, int, int);
static int dload_init1 (unsigned char **, ExecEnv *, int, int);
static int dload_check1 (unsigned char *, ExecEnv *, int, int);
static int array_load_init1 (unsigned char **, ExecEnv *, int, int);
static int array_load_check1 (unsigned char *, ExecEnv *, int, int);
static int istore_init1 (unsigned char **, ExecEnv *, int, int);
static int istore_check1 (unsigned char *, ExecEnv *, int, int);
static int astore_init1 (unsigned char **, ExecEnv *, int, int);
static int astore_check1 (unsigned char *, ExecEnv *, int, int);
static int lstore_init1 (unsigned char **, ExecEnv *, int, int);
static int lstore_check1 (unsigned char *, ExecEnv *, int, int);
static int fstore_init1 (unsigned char **, ExecEnv *, int, int);
static int fstore_check1 (unsigned char *, ExecEnv *, int, int);
static int dstore_init1 (unsigned char **, ExecEnv *, int, int);
static int dstore_check1 (unsigned char *, ExecEnv *, int, int);
static int array_store_init1 (unsigned char **, ExecEnv *, int, int);
static int array_store_check1 (unsigned char *, ExecEnv *, int, int);
static int pop_init1 (unsigned char **, ExecEnv *, int, int);
static int pop_check1 (unsigned char *, ExecEnv *, int, int);
static int pop2_init1 (unsigned char **, ExecEnv *, int, int);
static int pop2_check1 (unsigned char *, ExecEnv *, int, int);
static int dup_init1 (unsigned char **, ExecEnv *, int, int);
static int dup_check1 (unsigned char *, ExecEnv *, int, int);
static int dup_x1_init1 (unsigned char **, ExecEnv *, int, int);
static int dup_x1_check1 (unsigned char *, ExecEnv *, int, int);
static int dup_x2_init1 (unsigned char **, ExecEnv *, int, int);
static int dup_x2_check1 (unsigned char *, ExecEnv *, int, int);
static int dup2_init1 (unsigned char **, ExecEnv *, int, int);
static int dup2_check1 (unsigned char *, ExecEnv *, int, int);
static int dup2_x1_init1 (unsigned char **, ExecEnv *, int, int);
static int dup2_x1_check1 (unsigned char *, ExecEnv *, int, int);
static int dup2_x2_init1 (unsigned char **, ExecEnv *, int, int);
static int dup2_x2_check1 (unsigned char *, ExecEnv *, int, int);
static int swap_init1 (unsigned char **, ExecEnv *, int, int);
static int swap_check1 (unsigned char *, ExecEnv *, int, int);
static int ibinop_init1 (unsigned char **, ExecEnv *, int, int);
static int ibinop_check1 (unsigned char *, ExecEnv *, int, int);
static int lbinop_init1 (unsigned char **, ExecEnv *, int, int);
static int lbinop_check1 (unsigned char *, ExecEnv *, int, int);
static int fbinop_init1 (unsigned char **, ExecEnv *, int, int);
static int fbinop_check1 (unsigned char *, ExecEnv *, int, int);
static int dbinop_init1 (unsigned char **, ExecEnv *, int, int);
static int dbinop_check1 (unsigned char *, ExecEnv *, int, int);
static int iunop_init1 (unsigned char **, ExecEnv *, int, int);
static int iunop_check1 (unsigned char *, ExecEnv *, int, int);
static int lunop_init1 (unsigned char **, ExecEnv *, int, int);
static int lunop_check1 (unsigned char *, ExecEnv *, int, int);
static int funop_init1 (unsigned char **, ExecEnv *, int, int);
static int funop_check1 (unsigned char *, ExecEnv *, int, int);
static int dunop_init1 (unsigned char **, ExecEnv *, int, int);
static int dunop_check1 (unsigned char *, ExecEnv *, int, int);
static int iinc_init1 (unsigned char **, ExecEnv *, int, int);
static int iinc_check1 (unsigned char *, ExecEnv *, int, int);
static int i2l_init1 (unsigned char **, ExecEnv *, int, int);
static int i2l_check1 (unsigned char *, ExecEnv *, int, int);
static int i2f_init1 (unsigned char **, ExecEnv *, int, int);
static int i2f_check1 (unsigned char *, ExecEnv *, int, int);
static int i2d_init1 (unsigned char **, ExecEnv *, int, int);
static int i2d_check1 (unsigned char *, ExecEnv *, int, int);
static int l2i_init1 (unsigned char **, ExecEnv *, int, int);
static int l2i_check1 (unsigned char *, ExecEnv *, int, int);
static int l2f_init1 (unsigned char **, ExecEnv *, int, int);
static int l2f_check1 (unsigned char *, ExecEnv *, int, int);
static int l2d_init1 (unsigned char **, ExecEnv *, int, int);
static int l2d_check1 (unsigned char *, ExecEnv *, int, int);
static int f2i_init1 (unsigned char **, ExecEnv *, int, int);
static int f2i_check1 (unsigned char *, ExecEnv *, int, int);
static int f2l_init1 (unsigned char **, ExecEnv *, int, int);
static int f2l_check1 (unsigned char *, ExecEnv *, int, int);
static int f2d_init1 (unsigned char **, ExecEnv *, int, int);
static int f2d_check1 (unsigned char *, ExecEnv *, int, int);
static int d2i_init1 (unsigned char **, ExecEnv *, int, int);
static int d2i_check1 (unsigned char *, ExecEnv *, int, int);
static int d2l_init1 (unsigned char **, ExecEnv *, int, int);
static int d2l_check1 (unsigned char *, ExecEnv *, int, int);
static int d2f_init1 (unsigned char **, ExecEnv *, int, int);
static int d2f_check1 (unsigned char *, ExecEnv *, int, int);
static int i2b_init1 (unsigned char **, ExecEnv *, int, int);
static int i2b_check1 (unsigned char *, ExecEnv *, int, int);
static int i2b_init2 (unsigned char **, ExecEnv *, int, int);
static int i2b_check2 (unsigned char *, ExecEnv *, int, int);
static int i2c_init1 (unsigned char **, ExecEnv *, int, int);
static int i2c_check1 (unsigned char *, ExecEnv *, int, int);
static int i2c_init2 (unsigned char **, ExecEnv *, int, int);
static int i2c_check2 (unsigned char *, ExecEnv *, int, int);
static int i2s_init1 (unsigned char **, ExecEnv *, int, int);
static int i2s_check1 (unsigned char *, ExecEnv *, int, int);
static int i2s_init2 (unsigned char **, ExecEnv *, int, int);
static int i2s_check2 (unsigned char *, ExecEnv *, int, int);
static int lcmp_init1 (unsigned char **, ExecEnv *, int, int);
static int lcmp_init2 (unsigned char **, ExecEnv *, int, int);
static int lcmp_check1 (unsigned char *, ExecEnv *, int, int);
static int fcmp_init1 (unsigned char **, ExecEnv *, int, int);
static int dcmp_init1 (unsigned char **, ExecEnv *, int, int);
static int if_init1 (unsigned char **, ExecEnv *, int, int);
static int if_check1 (unsigned char *, ExecEnv *, int, int);
static int if_init2 (unsigned char **, ExecEnv *, int, int);
static int if_check2 (unsigned char *, ExecEnv *, int, int);
static int if_init3 (unsigned char **, ExecEnv *, int, int);
static int if_check3 (unsigned char *, ExecEnv *, int, int);
static int if_icmp_init1 (unsigned char **, ExecEnv *, int, int);
static int if_icmp_check1 (unsigned char *, ExecEnv *, int, int);
static int if_icmp_init2 (unsigned char **, ExecEnv *, int, int);
static int if_icmp_check2 (unsigned char *, ExecEnv *, int, int);
static int if_icmp_init3 (unsigned char **, ExecEnv *, int, int);
static int if_icmp_check3 (unsigned char *, ExecEnv *, int, int);
static int goto_init1 (unsigned char **, ExecEnv *, int, int);
static int goto_check1 (unsigned char *, ExecEnv *, int, int);
static int goto_init2 (unsigned char **, ExecEnv *, int, int);
static int goto_check2 (unsigned char *, ExecEnv *, int, int);
static int jsr_init1 (unsigned char **, ExecEnv *, int, int);
static int jsr_check1 (unsigned char *, ExecEnv *, int, int);
static int jsr_init2 (unsigned char **, ExecEnv *, int, int);
static int jsr_check2 (unsigned char *, ExecEnv *, int, int);
static int ret_init1 (unsigned char **, ExecEnv *, int, int);
static int ret_check1 (unsigned char *, ExecEnv *, int, int);
static int ret_init2 (unsigned char **, ExecEnv *, int, int);
static int ret_check2 (unsigned char *, ExecEnv *, int, int);
static int tableswitch_init1 (unsigned char **, ExecEnv *, int, int);
static int tableswitch_check1 (unsigned char *, ExecEnv *, int, int);
static int lookupswitch_init1 (unsigned char **, ExecEnv *, int, int);
static int lookupswitch_check1 (unsigned char *, ExecEnv *, int, int);
static int ireturn_init1 (unsigned char **, ExecEnv *, int, int);
static int ireturn_check1 (unsigned char *, ExecEnv *, int, int);
static int lreturn_init1 (unsigned char **, ExecEnv *, int, int);
static int lreturn_check1 (unsigned char *, ExecEnv *, int, int);
static int freturn_init1 (unsigned char **, ExecEnv *, int, int);
static int freturn_check1 (unsigned char *, ExecEnv *, int, int);
static int dreturn_init1 (unsigned char **, ExecEnv *, int, int);
static int dreturn_check1 (unsigned char *, ExecEnv *, int, int);
static int areturn_init1 (unsigned char **, ExecEnv *, int, int);
static int areturn_check1 (unsigned char *, ExecEnv *, int, int);
static int return_init1 (unsigned char **, ExecEnv *, int, int);
static int return_check1 (unsigned char *, ExecEnv *, int, int);
static int getstatic_init1 (unsigned char **, ExecEnv *, int, int);
static int getstatic_check1 (unsigned char *, ExecEnv *, int, int);
static int putstatic_init1 (unsigned char **, ExecEnv *, int, int);
static int putstatic_check1 (unsigned char *, ExecEnv *, int, int);
static int getfield_init1 (unsigned char **, ExecEnv *, int, int);
static int getfield_check1 (unsigned char *, ExecEnv *, int, int);
static int putfield_init1 (unsigned char **, ExecEnv *, int, int);
static int putfield_check1 (unsigned char *, ExecEnv *, int, int);
static int getstatic_init2 (unsigned char **, ExecEnv *, int, int);
static int getstatic_check2 (unsigned char *, ExecEnv *, int, int);
static int putstatic_init2 (unsigned char **, ExecEnv *, int, int);
static int putstatic_check2 (unsigned char *, ExecEnv *, int, int);
static int getfield_init2 (unsigned char **, ExecEnv *, int, int);
static int getfield_check2 (unsigned char *, ExecEnv *, int, int);
static int putfield_init2 (unsigned char **, ExecEnv *, int, int);
static int putfield_check2 (unsigned char *, ExecEnv *, int, int);
static int invokevirtual_init1 (unsigned char **, ExecEnv *, int, int);
static int invokevirtual_check1 (unsigned char *, ExecEnv *, int, int);
static int invokespecial_init1 (unsigned char **, ExecEnv *, int, int);
static int invokespecial_check1 (unsigned char *, ExecEnv *, int, int);
static int invokestatic_init1 (unsigned char **, ExecEnv *, int, int);
static int invokestatic_check1 (unsigned char *, ExecEnv *, int, int);
static int invokeinterface_init1 (unsigned char **, ExecEnv *, int, int);
static int invokeinterface_check1 (unsigned char *, ExecEnv *, int, int);
static int new_init1 (unsigned char **, ExecEnv *, int, int);
static int new_check1 (unsigned char *, ExecEnv *, int, int);
static int newarray_init1 (unsigned char **, ExecEnv *, int, int);
static int newarray_check1 (unsigned char *, ExecEnv *, int, int);
static int arraylength_init1 (unsigned char **, ExecEnv *, int, int);
static int arraylength_check1 (unsigned char *, ExecEnv *, int, int);
static int athrow_init1 (unsigned char **, ExecEnv *, int, int);
static int athrow_init2 (unsigned char **, ExecEnv *, int, int);
static int athrow_init3 (unsigned char **, ExecEnv *, int, int);
static int athrow_init4 (unsigned char **, ExecEnv *, int, int);
static int athrow_check1 (unsigned char *, ExecEnv *, int, int);
static int athrow_check2 (unsigned char *, ExecEnv *, int, int);
static int athrow_check3 (unsigned char *, ExecEnv *, int, int);
static int athrow_check4 (unsigned char *, ExecEnv *, int, int);
static int checkcast_init1 (unsigned char **, ExecEnv *, int, int);
static int checkcast_check1 (unsigned char *, ExecEnv *, int, int);
static int instanceof_init1 (unsigned char **, ExecEnv *, int, int);
static int instanceof_check1 (unsigned char *, ExecEnv *, int, int);
static int monitorenter_init1 (unsigned char **, ExecEnv *, int, int);
static int monitorenter_check1 (unsigned char *, ExecEnv *, int, int);
static int monitorexit_init1 (unsigned char **, ExecEnv *, int, int);
static int monitorexit_check1 (unsigned char *, ExecEnv *, int, int);
static int wide_init1 (unsigned char **, ExecEnv *, int, int);
static int wide_check1 (unsigned char *, ExecEnv *, int, int);
static int multianewarray_init1 (unsigned char **, ExecEnv *, int, int);
static int multianewarray_check1 (unsigned char *, ExecEnv *, int, int);
static int breakpoint_init1 (unsigned char **, ExecEnv *, int, int);
static int breakpoint_check1 (unsigned char *, ExecEnv *, int, int);
static int ldc_quick_init1 (unsigned char **, ExecEnv *, int, int);
static int ldc_quick_check1 (unsigned char *, ExecEnv *, int, int);
static int ldc2_quick_init1 (unsigned char **, ExecEnv *, int, int);
static int ldc2_quick_check1 (unsigned char *, ExecEnv *, int, int);
static int getfield_quick_init1 (unsigned char **, ExecEnv *, int, int);
static int getfield_quick_check1 (unsigned char *, ExecEnv *, int, int);
static int putfield_quick_init1 (unsigned char **, ExecEnv *, int, int);
static int putfield_quick_check1 (unsigned char *, ExecEnv *, int, int);
static int getstatic_quick_init1 (unsigned char **, ExecEnv *, int, int);
static int getstatic_quick_check1 (unsigned char *, ExecEnv *, int, int);
static int putstatic_quick_init1 (unsigned char **, ExecEnv *, int, int);
static int putstatic_quick_check1 (unsigned char *, ExecEnv *, int, int);
static int getfield_quick_init2 (unsigned char **, ExecEnv *, int, int);
static int getfield_quick_check2 (unsigned char *, ExecEnv *, int, int);
static int putfield_quick_init2 (unsigned char **, ExecEnv *, int, int);
static int putfield_quick_check2 (unsigned char *, ExecEnv *, int, int);
static int getstatic_quick_init2 (unsigned char **, ExecEnv *, int, int);
static int getstatic_quick_check2 (unsigned char *, ExecEnv *, int, int);
static int putstatic_quick_init2 (unsigned char **, ExecEnv *, int, int);
static int putstatic_quick_check2 (unsigned char *, ExecEnv *, int, int);
static int invokevirtual_quick_init1 (unsigned char **, ExecEnv *, int, int);
static int invokevirtual_quick_check1 (unsigned char *, ExecEnv *, int, int);
static int invokenonvirtual_quick_init1 (unsigned char **, ExecEnv *, int, int);
static int invokenonvirtual_quick_check1 (unsigned char *, ExecEnv *, int, int);
static int invokestatic_quick_init1 (unsigned char **, ExecEnv *, int, int);
static int invokestatic_quick_check1 (unsigned char *, ExecEnv *, int, int);
static int invokeinterface_quick_init1 (unsigned char **, ExecEnv *, int, int);
static int invokeinterface_quick_check1 (unsigned char *, ExecEnv *, int, int);
static int invokeinterface_quick_init2 (unsigned char **, ExecEnv *, int, int);
static int invokeinterface_quick_check2 (unsigned char *, ExecEnv *, int, int);
static int invokevirtualobject_quick_init1 (unsigned char **, ExecEnv *, int, int);
static int invokevirtualobject_quick_check1 (unsigned char *, ExecEnv *, int, int);
static int new_quick_init1 (unsigned char **, ExecEnv *, int, int);
static int new_quick_check1 (unsigned char *, ExecEnv *, int, int);
static int anewarray_quick_init1 (unsigned char **, ExecEnv *, int, int);
static int anewarray_quick_check1 (unsigned char *, ExecEnv *, int, int);
static int multianewarray_quick_init1 (unsigned char **, ExecEnv *, int, int);
static int multianewarray_quick_check1 (unsigned char *, ExecEnv *, int, int);
static int checkcast_quick_init1 (unsigned char **, ExecEnv *, int, int);
static int checkcast_quick_check1 (unsigned char *, ExecEnv *, int, int);
static int instanceof_quick_init1 (unsigned char **, ExecEnv *, int, int);
static int instanceof_quick_check1 (unsigned char *, ExecEnv *, int, int);

#endif /* _SELFTEST_H_ */

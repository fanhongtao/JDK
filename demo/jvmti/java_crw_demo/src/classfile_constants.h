/*
 * @(#)classfile_constants.h	1.2 04/09/24
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

#ifndef CLASSFILE_CONSTANTS_H
#define CLASSFILE_CONSTANTS_H

/* Flags */

#define JVM_ACC_PUBLIC        0x0001  
#define JVM_ACC_PRIVATE       0x0002  
#define JVM_ACC_PROTECTED     0x0004  
#define JVM_ACC_STATIC        0x0008  
#define JVM_ACC_FINAL         0x0010  
#define JVM_ACC_SYNCHRONIZED  0x0020  
#define JVM_ACC_SUPER         0x0020  
#define JVM_ACC_VOLATILE      0x0040  
#define JVM_ACC_TRANSIENT     0x0080  
#define JVM_ACC_VARARGS       0x0080  
#define JVM_ACC_NATIVE        0x0100  
#define JVM_ACC_INTERFACE     0x0200  
#define JVM_ACC_ABSTRACT      0x0400  
#define JVM_ACC_STRICT	      0x0800  
#define JVM_ACC_SYNTHETIC     0x1000  
#define JVM_ACC_ANNOTATION    0x2000  
#define JVM_ACC_ENUM          0x4000  

enum {
    JVM_CONSTANT_Utf8 			= 1,
    JVM_CONSTANT_Unicode		= 2, /* unused */
    JVM_CONSTANT_Integer		= 3,
    JVM_CONSTANT_Float			= 4,
    JVM_CONSTANT_Long			= 5,      
    JVM_CONSTANT_Double			= 6,
    JVM_CONSTANT_Class			= 7,
    JVM_CONSTANT_String			= 8,
    JVM_CONSTANT_Fieldref		= 9,
    JVM_CONSTANT_Methodref		= 10,
    JVM_CONSTANT_InterfaceMethodref	= 11,
    JVM_CONSTANT_NameAndType		= 12
};

/* Type signatures */

#define JVM_SIGNATURE_ARRAY		'['
#define JVM_SIGNATURE_BYTE		'B'
#define JVM_SIGNATURE_CHAR		'C'
#define JVM_SIGNATURE_CLASS		'L'
#define JVM_SIGNATURE_ENDCLASS	        ';'
#define JVM_SIGNATURE_ENUM		'E'
#define JVM_SIGNATURE_FLOAT		'F'
#define JVM_SIGNATURE_DOUBLE             'D'
#define JVM_SIGNATURE_FUNC		'('
#define JVM_SIGNATURE_ENDFUNC	        ')'
#define JVM_SIGNATURE_INT		'I'
#define JVM_SIGNATURE_LONG		'J'
#define JVM_SIGNATURE_SHORT		'S'
#define JVM_SIGNATURE_VOID		'V'
#define JVM_SIGNATURE_BOOLEAN	        'Z'

/* Opcodes */

enum {
  opc_nop 		= 0,
  opc_aconst_null 	= 1,
  opc_iconst_m1 	= 2,
  opc_iconst_0 		= 3,
  opc_iconst_1 		= 4,
  opc_iconst_2 		= 5,
  opc_iconst_3 		= 6,
  opc_iconst_4 		= 7,
  opc_iconst_5 		= 8,
  opc_lconst_0 		= 9,
  opc_lconst_1 		= 10,
  opc_fconst_0 		= 11,
  opc_fconst_1 		= 12,
  opc_fconst_2 		= 13,
  opc_dconst_0 		= 14,
  opc_dconst_1 		= 15,
  opc_bipush 		= 16,
  opc_sipush 		= 17,
  opc_ldc 		= 18,
  opc_ldc_w 		= 19,
  opc_ldc2_w 		= 20,
  opc_iload 		= 21,
  opc_lload 		= 22,
  opc_fload 		= 23,
  opc_dload 		= 24,
  opc_aload 		= 25,
  opc_iload_0 		= 26,
  opc_iload_1 		= 27,
  opc_iload_2 		= 28,
  opc_iload_3 		= 29,
  opc_lload_0 		= 30,
  opc_lload_1 		= 31,
  opc_lload_2 		= 32,
  opc_lload_3 		= 33,
  opc_fload_0 		= 34,
  opc_fload_1 		= 35,
  opc_fload_2 		= 36,
  opc_fload_3 		= 37,
  opc_dload_0 		= 38,
  opc_dload_1 		= 39,
  opc_dload_2 		= 40,
  opc_dload_3 		= 41,
  opc_aload_0 		= 42,
  opc_aload_1 		= 43,
  opc_aload_2 		= 44,
  opc_aload_3 		= 45,
  opc_iaload 		= 46,
  opc_laload 		= 47,
  opc_faload 		= 48,
  opc_daload 		= 49,
  opc_aaload 		= 50,
  opc_baload 		= 51,
  opc_caload 		= 52,
  opc_saload 		= 53,
  opc_istore 		= 54,
  opc_lstore 		= 55,
  opc_fstore 		= 56,
  opc_dstore 		= 57,
  opc_astore 		= 58,
  opc_istore_0 		= 59,
  opc_istore_1 		= 60,
  opc_istore_2 		= 61,
  opc_istore_3 		= 62,
  opc_lstore_0 		= 63,
  opc_lstore_1 		= 64,
  opc_lstore_2 		= 65,
  opc_lstore_3 		= 66,
  opc_fstore_0 		= 67,
  opc_fstore_1 		= 68,
  opc_fstore_2 		= 69,
  opc_fstore_3 		= 70,
  opc_dstore_0 		= 71,
  opc_dstore_1 		= 72,
  opc_dstore_2 		= 73,
  opc_dstore_3 		= 74,
  opc_astore_0 		= 75,
  opc_astore_1 		= 76,
  opc_astore_2 		= 77,
  opc_astore_3 		= 78,
  opc_iastore 		= 79,
  opc_lastore 		= 80,
  opc_fastore 		= 81,
  opc_dastore 		= 82,
  opc_aastore 		= 83,
  opc_bastore 		= 84,
  opc_castore 		= 85,
  opc_sastore 		= 86,
  opc_pop 		= 87,
  opc_pop2 		= 88,
  opc_dup 		= 89,
  opc_dup_x1 		= 90,
  opc_dup_x2 		= 91,
  opc_dup2 		= 92,
  opc_dup2_x1 		= 93,
  opc_dup2_x2 		= 94,
  opc_swap 		= 95,
  opc_iadd 		= 96,
  opc_ladd 		= 97,
  opc_fadd 		= 98,
  opc_dadd 		= 99,
  opc_isub 		= 100,
  opc_lsub 		= 101,
  opc_fsub 		= 102,
  opc_dsub 		= 103,
  opc_imul 		= 104,
  opc_lmul 		= 105,
  opc_fmul 		= 106,
  opc_dmul 		= 107,
  opc_idiv 		= 108,
  opc_ldiv 		= 109,
  opc_fdiv 		= 110,
  opc_ddiv 		= 111,
  opc_irem 		= 112,
  opc_lrem 		= 113,
  opc_frem 		= 114,
  opc_drem 		= 115,
  opc_ineg 		= 116,
  opc_lneg 		= 117,
  opc_fneg 		= 118,
  opc_dneg 		= 119,
  opc_ishl 		= 120,
  opc_lshl 		= 121,
  opc_ishr 		= 122,
  opc_lshr 		= 123,
  opc_iushr 		= 124,
  opc_lushr 		= 125,
  opc_iand 		= 126,
  opc_land 		= 127,
  opc_ior 		= 128,
  opc_lor 		= 129,
  opc_ixor 		= 130,
  opc_lxor 		= 131,
  opc_iinc 		= 132,
  opc_i2l 		= 133,
  opc_i2f 		= 134,
  opc_i2d 		= 135,
  opc_l2i 		= 136,
  opc_l2f 		= 137,
  opc_l2d 		= 138,
  opc_f2i 		= 139,
  opc_f2l 		= 140,
  opc_f2d 		= 141,
  opc_d2i 		= 142,
  opc_d2l 		= 143,
  opc_d2f 		= 144,
  opc_i2b 		= 145,
  opc_i2c 		= 146,
  opc_i2s 		= 147,
  opc_lcmp 		= 148,
  opc_fcmpl 		= 149,
  opc_fcmpg 		= 150,
  opc_dcmpl 		= 151,
  opc_dcmpg 		= 152,
  opc_ifeq 		= 153,
  opc_ifne 		= 154,
  opc_iflt 		= 155,
  opc_ifge 		= 156,
  opc_ifgt 		= 157,
  opc_ifle 		= 158,
  opc_if_icmpeq 	= 159,
  opc_if_icmpne 	= 160,
  opc_if_icmplt 	= 161,
  opc_if_icmpge 	= 162,
  opc_if_icmpgt 	= 163,
  opc_if_icmple 	= 164,
  opc_if_acmpeq 	= 165,
  opc_if_acmpne 	= 166,
  opc_goto 		= 167,
  opc_jsr 		= 168,
  opc_ret 		= 169,
  opc_tableswitch 	= 170,
  opc_lookupswitch 	= 171,
  opc_ireturn 		= 172,
  opc_lreturn 		= 173,
  opc_freturn 		= 174,
  opc_dreturn 		= 175,
  opc_areturn 		= 176,
  opc_return 		= 177,
  opc_getstatic 	= 178,
  opc_putstatic 	= 179,
  opc_getfield 		= 180,
  opc_putfield 		= 181,
  opc_invokevirtual 	= 182,
  opc_invokespecial 	= 183,
  opc_invokestatic 	= 184,
  opc_invokeinterface 	= 185,
  opc_xxxunusedxxx 	= 186,
  opc_new 		= 187,
  opc_newarray 		= 188,
  opc_anewarray 	= 189,
  opc_arraylength 	= 190,
  opc_athrow 		= 191,
  opc_checkcast 	= 192,
  opc_instanceof 	= 193,
  opc_monitorenter 	= 194,
  opc_monitorexit 	= 195,
  opc_wide 		= 196,
  opc_multianewarray 	= 197,
  opc_ifnull 		= 198,
  opc_ifnonnull 	= 199,
  opc_goto_w 		= 200,
  opc_jsr_w 		= 201,
  opc_MAX 		= 201
};

/* Opcode length initializer, use with something like:
 *   unsigned char opcode_length[opc_MAX+1] = JVM_OPCODE_LENGTH_INITIALIZER;
 */
#define JVM_OPCODE_LENGTH_INITIALIZER { 	\
   1,	/* nop */			\
   1,	/* aconst_null */		\
   1,	/* iconst_m1 */			\
   1,	/* iconst_0 */			\
   1,	/* iconst_1 */			\
   1,	/* iconst_2 */			\
   1,	/* iconst_3 */			\
   1,	/* iconst_4 */			\
   1,	/* iconst_5 */			\
   1,	/* lconst_0 */			\
   1,	/* lconst_1 */			\
   1,	/* fconst_0 */			\
   1,	/* fconst_1 */			\
   1,	/* fconst_2 */			\
   1,	/* dconst_0 */			\
   1,	/* dconst_1 */			\
   2,	/* bipush */			\
   3,	/* sipush */			\
   2,	/* ldc */			\
   3,	/* ldc_w */			\
   3,	/* ldc2_w */			\
   2,	/* iload */			\
   2,	/* lload */			\
   2,	/* fload */			\
   2,	/* dload */			\
   2,	/* aload */			\
   1,	/* iload_0 */			\
   1,	/* iload_1 */			\
   1,	/* iload_2 */			\
   1,	/* iload_3 */			\
   1,	/* lload_0 */			\
   1,	/* lload_1 */			\
   1,	/* lload_2 */			\
   1,	/* lload_3 */			\
   1,	/* fload_0 */			\
   1,	/* fload_1 */			\
   1,	/* fload_2 */			\
   1,	/* fload_3 */			\
   1,	/* dload_0 */			\
   1,	/* dload_1 */			\
   1,	/* dload_2 */			\
   1,	/* dload_3 */			\
   1,	/* aload_0 */			\
   1,	/* aload_1 */			\
   1,	/* aload_2 */			\
   1,	/* aload_3 */			\
   1,	/* iaload */			\
   1,	/* laload */			\
   1,	/* faload */			\
   1,	/* daload */			\
   1,	/* aaload */			\
   1,	/* baload */			\
   1,	/* caload */			\
   1,	/* saload */			\
   2,	/* istore */			\
   2,	/* lstore */			\
   2,	/* fstore */			\
   2,	/* dstore */			\
   2,	/* astore */			\
   1,	/* istore_0 */			\
   1,	/* istore_1 */			\
   1,	/* istore_2 */			\
   1,	/* istore_3 */			\
   1,	/* lstore_0 */			\
   1,	/* lstore_1 */			\
   1,	/* lstore_2 */			\
   1,	/* lstore_3 */			\
   1,	/* fstore_0 */			\
   1,	/* fstore_1 */			\
   1,	/* fstore_2 */			\
   1,	/* fstore_3 */			\
   1,	/* dstore_0 */			\
   1,	/* dstore_1 */			\
   1,	/* dstore_2 */			\
   1,	/* dstore_3 */			\
   1,	/* astore_0 */			\
   1,	/* astore_1 */			\
   1,	/* astore_2 */			\
   1,	/* astore_3 */			\
   1,	/* iastore */			\
   1,	/* lastore */			\
   1,	/* fastore */			\
   1,	/* dastore */			\
   1,	/* aastore */			\
   1,	/* bastore */			\
   1,	/* castore */			\
   1,	/* sastore */			\
   1,	/* pop */			\
   1,	/* pop2 */			\
   1,	/* dup */			\
   1,	/* dup_x1 */			\
   1,	/* dup_x2 */			\
   1,	/* dup2 */			\
   1,	/* dup2_x1 */			\
   1,	/* dup2_x2 */			\
   1,	/* swap */			\
   1,	/* iadd */			\
   1,	/* ladd */			\
   1,	/* fadd */			\
   1,	/* dadd */			\
   1,	/* isub */			\
   1,	/* lsub */			\
   1,	/* fsub */			\
   1,	/* dsub */			\
   1,	/* imul */			\
   1,	/* lmul */			\
   1,	/* fmul */			\
   1,	/* dmul */			\
   1,	/* idiv */			\
   1,	/* ldiv */			\
   1,	/* fdiv */			\
   1,	/* ddiv */			\
   1,	/* irem */			\
   1,	/* lrem */			\
   1,	/* frem */			\
   1,	/* drem */			\
   1,	/* ineg */			\
   1,	/* lneg */			\
   1,	/* fneg */			\
   1,	/* dneg */			\
   1,	/* ishl */			\
   1,	/* lshl */			\
   1,	/* ishr */			\
   1,	/* lshr */			\
   1,	/* iushr */			\
   1,	/* lushr */			\
   1,	/* iand */			\
   1,	/* land */			\
   1,	/* ior */			\
   1,	/* lor */			\
   1,	/* ixor */			\
   1,	/* lxor */			\
   3,	/* iinc */			\
   1,	/* i2l */			\
   1,	/* i2f */			\
   1,	/* i2d */			\
   1,	/* l2i */			\
   1,	/* l2f */			\
   1,	/* l2d */			\
   1,	/* f2i */			\
   1,	/* f2l */			\
   1,	/* f2d */			\
   1,	/* d2i */			\
   1,	/* d2l */			\
   1,	/* d2f */			\
   1,	/* i2b */			\
   1,	/* i2c */			\
   1,	/* i2s */			\
   1,	/* lcmp */			\
   1,	/* fcmpl */			\
   1,	/* fcmpg */			\
   1,	/* dcmpl */			\
   1,	/* dcmpg */			\
   3,	/* ifeq */			\
   3,	/* ifne */			\
   3,	/* iflt */			\
   3,	/* ifge */			\
   3,	/* ifgt */			\
   3,	/* ifle */			\
   3,	/* if_icmpeq */			\
   3,	/* if_icmpne */			\
   3,	/* if_icmplt */			\
   3,	/* if_icmpge */			\
   3,	/* if_icmpgt */			\
   3,	/* if_icmple */			\
   3,	/* if_acmpeq */			\
   3,	/* if_acmpne */			\
   3,	/* goto */			\
   3,	/* jsr */			\
   2,	/* ret */			\
   99,	/* tableswitch */		\
   99,	/* lookupswitch */		\
   1,	/* ireturn */			\
   1,	/* lreturn */			\
   1,	/* freturn */			\
   1,	/* dreturn */			\
   1,	/* areturn */			\
   1,	/* return */			\
   3,	/* getstatic */			\
   3,	/* putstatic */			\
   3,	/* getfield */			\
   3,	/* putfield */			\
   3,	/* invokevirtual */		\
   3,	/* invokespecial */		\
   3,	/* invokestatic */		\
   5,	/* invokeinterface */		\
   0,	/* xxxunusedxxx */		\
   3,	/* new */			\
   2,	/* newarray */			\
   3,	/* anewarray */			\
   1,	/* arraylength */		\
   1,	/* athrow */			\
   3,	/* checkcast */			\
   3,	/* instanceof */		\
   1,	/* monitorenter */		\
   1,	/* monitorexit */		\
   0,	/* wide */			\
   4,	/* multianewarray */		\
   3,	/* ifnull */			\
   3,	/* ifnonnull */			\
   5,	/* goto_w */			\
   5	/* jsr_w */			\
}

#endif


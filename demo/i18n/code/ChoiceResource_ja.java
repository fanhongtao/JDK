/*
 *  @(#)ChoiceResource_ja.java	1.1 02/14/97
 *
 * (C) Copyright Taligent, Inc. 1996 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - All Rights Reserved
 *
 * Portions copyright (c) 1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
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
 *
 */

import java.util.ListResourceBundle;

public class ChoiceResource_ja  extends ListResourceBundle {
	public Object[][] getContents() {
		return contents;
	}
	static final Object[][] contents = {
	// LOCALIZE THIS
		{"patternText", "%1 \u30c7\u30a3\u30b9\u30af\u306f %2 \u306b %0 \u3092\u542b\u3093\u3067\u3044\u307e\u3059\u3002"},
		{"choice1", "\u30d5\u30a1\u30a4\u30eb\u304c\u3042\u308a\u307e\u305b\u3093"},
		{"choice2", "\u4e00\u3064\u306e\u30d5\u30a1\u30a4\u30eb"},
		{"choice3", "%0|3 \u3064\u306e\u30d5\u30a1\u30a4\u30eb"}};
	// END OF MATERIAL TO LOCALIZE
	};


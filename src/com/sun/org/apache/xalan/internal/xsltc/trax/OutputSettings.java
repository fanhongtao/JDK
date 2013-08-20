/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: OutputSettings.java,v 1.4 2004/02/16 22:57:21 minchau Exp $
 */

package com.sun.org.apache.xalan.internal.xsltc.trax;

import java.util.Properties;

/**
 * @author Morten Jorgensen
 */
public final class OutputSettings {
    
    private String _cdata_section_elements = null;
    private String _doctype_public = null;
    private String _encoding = null;
    private String _indent = null;
    private String _media_type = null;
    private String _method = null;
    private String _omit_xml_declaration = null;
    private String _standalone = null;
    private String _version = null;

    public Properties getProperties() {
	Properties properties = new Properties();
	return(properties);
    }

    
}

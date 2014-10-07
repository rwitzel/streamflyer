/**
 * Copyright (C) 2011 rwoo@gmx.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.streamflyer.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This reader makes the XML version of the XML document in the character stream available.
 * 
 * @author rwoo
 * @since 27.06.2011
 */
public class XmlVersionReader extends BufferedReader {

    private String xmlVersion;

    //
    // constructors
    //

    public XmlVersionReader(Reader in) throws IOException {
        super(in);

        String prolog = prolog();
        xmlVersion = xmlVersion(prolog);
    }

    //
    // private methods
    //

    private String xmlVersion(String prolog) {

        // (Should we do aware of BOMs here? No. I consider it the
        // responsibility of the caller to provide characters without BOM.)

        Matcher matcher = Pattern.compile("<\\?xml[^>]*version\\s*=\\s*['\"]((1.0)|(1.1))['\"].*").matcher(prolog);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            // the default, see class comment.
            return "1.0";
        }
    }

    private String prolog() throws IOException {

        // the prolog may contain many, many whitespace characters -> 4096
        final int MAX_SNIFFED_CHARACTERS = 4096;

        mark(MAX_SNIFFED_CHARACTERS);
        try {

            char[] cbuf = new char[MAX_SNIFFED_CHARACTERS];
            int read = read(cbuf);
            if (read == -1) {
                return "";
            } else {
                return new String(cbuf, 0, read);
            }
        } finally {
            reset();
        }
    }

    //
    // public methods
    //

    /**
     * @return Returns the XML version read from the character stream. If there is XML prolog given, then version "1.0"
     *         is assumed and returned.
     */
    public String getXmlVersion() {
        return xmlVersion;
    }
}

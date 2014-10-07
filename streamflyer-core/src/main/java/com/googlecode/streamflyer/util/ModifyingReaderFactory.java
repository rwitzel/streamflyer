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

package com.googlecode.streamflyer.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.commons.io.input.XmlStreamReader;

import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.regex.RegexModifier;
import com.googlecode.streamflyer.xml.InvalidXmlCharacterModifier;
import com.googlecode.streamflyer.xml.XmlVersionModifier;
import com.googlecode.streamflyer.xml.XmlVersionReader;

/**
 * Provides short-cuts to create {@link ModifyingReader modifying readers} using defaults.
 * 
 * @author rwoo
 * @since 27.06.2011
 */
public class ModifyingReaderFactory {

    public ModifyingReader createInvalidXmlCharacterRemovingReader(InputStream xmlStream) throws IOException {
        return createInvalidXmlCharacterReplacingReader(xmlStream, "");
    }

    public ModifyingReader createInvalidXmlCharacterReplacingReader(InputStream xmlStream, String replacement)
            throws IOException {

        // buffer stream
        // (is this really necessary to get optimal performance?)
        if (!(xmlStream instanceof BufferedInputStream)) {
            xmlStream = new BufferedInputStream(xmlStream);
        }

        // get the XML version
        XmlStreamReader xmlReader = new XmlStreamReader(xmlStream);
        XmlVersionReader xmlVersionReader = new XmlVersionReader(xmlReader);
        String xmlVersion = xmlVersionReader.getXmlVersion();

        // what kind of replacement?
        boolean dollarZero;
        if (replacement.contains("$0")) {
            dollarZero = true;
        } else {
            dollarZero = false;
        }

        // create the reader that replaces invalid XML characters
        Modifier modifier = new InvalidXmlCharacterModifier(8192, replacement, xmlVersion, dollarZero);
        return new ModifyingReader(xmlVersionReader, modifier);
    }

    public ModifyingReader createXmlVersionModifyingReader(InputStream xmlStream, String newXmlVersion)
            throws IOException {

        // buffer stream
        // (is this really necessary to get optimal performance?)
        if (!(xmlStream instanceof BufferedInputStream)) {
            xmlStream = new BufferedInputStream(xmlStream);
        }

        XmlStreamReader xmlReader = new XmlStreamReader(xmlStream);
        XmlVersionReader xmlVersionReader = new XmlVersionReader(xmlReader);

        // create the reader that replaces the XML version in prolog
        Modifier modifier = new XmlVersionModifier(newXmlVersion, 8192);
        return new ModifyingReader(xmlVersionReader, modifier);
    }

    public ModifyingReader createRegexModifyingReader(Reader input, String regex, String replacement) {
        return createRegexModifyingReader(input, regex, 0, replacement, 0, 8192);
    }

    public ModifyingReader createRegexModifyingReader(Reader input, String regex, int flags, String replacement,
            int minimumLengthOfLookBehind, int requestedCapacityOfCharacterBuffer) {

        // buffer stream
        // (is this really necessary to get optimal performance?)
        if (!(input instanceof BufferedReader)) {
            input = new BufferedReader(input);
        }

        // create modifier
        Modifier modifier = new RegexModifier(regex, flags, replacement, minimumLengthOfLookBehind,
                requestedCapacityOfCharacterBuffer);

        // create and return reader
        return new ModifyingReader(input, modifier);
    }
}

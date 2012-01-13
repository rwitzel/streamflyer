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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.thirdparty.ZzzValidate;
import com.googlecode.streamflyer.util.ModificationFactory;
import com.googlecode.streamflyer.util.ModifyingReaderFactory;

/**
 * This modifier replaces invalid XML characters in an stream that contains an
 * XML document.
 * <p>
 * Use {@link XmlVersionModifier} instead of this class if only the version in
 * the prolog of the XML document restricts the range of valid characters.
 * <p>
 * The memory consumption of this modifier is roughly given by the first
 * argument of
 * {@link #InvalidXmlCharacterModifier(int, String, String, boolean)}.
 * <p>
 * See
 * {@link ModifyingReaderFactory#createInvalidXmlCharacterRemovingModifier(java.io.InputStream)}.
 * 
 * @author rwoo
 * 
 * @since 23.06.2011
 */
public class InvalidXmlCharacterModifier implements Modifier {

    /**
     * http://www.w3.org/TR/xml/#charsets (referring to the fifth edition of the
     * specification)
     */
    public static final String XML_10_VERSION = "1.0";

    /**
     * http://www.w3.org/TR/xml11/#charsets
     */
    public static final String XML_11_VERSION = "1.1";


    //
    // injected properties
    //

    protected ModificationFactory factory;

    protected String replacement;

    protected Matcher matcher;

    protected boolean dollarZero;


    //
    // constructors
    //

    /**
     * @param newNumberOfChars
     * @param replacement the string that shall replace invalid XML characters.
     *        This string may contain "$0" which refers to the replaced
     *        character, see {@link Matcher#replaceAll(String)}
     * @param xmlVersion Must not be <code>null</code>.
     * @param dollarZero
     */
    public InvalidXmlCharacterModifier(int newNumberOfChars,
            String replacement, String xmlVersion, boolean dollarZero) {

        ZzzValidate.notNull(replacement, "replacement must not be null");
        ZzzValidate.notNull(xmlVersion, "xmlVersion must not be null");

        this.factory = new ModificationFactory(0, newNumberOfChars);
        this.replacement = replacement;
        this.dollarZero = dollarZero;

        // initialize matcher
        Pattern pattern;
        if (XML_10_VERSION.equals(xmlVersion)) {
            pattern = Pattern.compile(getInvalidXmlCharacterRegex_Xml10());
        }
        else if (XML_11_VERSION.equals(xmlVersion)) {
            pattern = Pattern.compile(getInvalidXmlCharacterRegex_Xml11());
        }
        else {
            throw new IllegalArgumentException("xmlVersion has the illegal "
                    + "(or unsupported) value " + xmlVersion);
        }
        this.matcher = pattern.matcher("");
    }


    /**
     * <pre>
     *      [2]    Char       ::=      #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
     *       // any Unicode character, excluding the surrogate blocks, FFFE, and FFFF.
     * </pre>
     * 
     * [Source: http://www.w3.org/TR/xml/#charsets ]
     * 
     * @return Returns a regular expression that matches invalid XML 1.0
     *         characters.
     */
    protected String getInvalidXmlCharacterRegex_Xml10() {
        // Most characters are probably from the range U+0020 -U+D7FF.
        // Therefore, in order to optimize performance, we move this range to
        // the start of the regular expression.
        return "[^\\u0020-\\uD7FF\\u0009\\u000A\\u000D\\uE000-\\uFFFD\\u10000-\\u10FFFF]";
    }

    /**
     * <pre>
     * [2]     Char       ::=      [#x1-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
     * // any Unicode character, excluding the surrogate blocks, FFFE, and FFFF.
     * </pre>
     * 
     * [Source: http://www.w3.org/TR/xml11/#charsets ]
     * 
     * @return Returns a regular expression that matches invalid XML 1.1
     *         characters.
     */
    protected String getInvalidXmlCharacterRegex_Xml11() {
        return "[^\\u0001-\\uD7FF\\uE000-\\uFFFD\\u10000-\\u10FFFF]";
    }

    //
    // override Modifier.*
    //

    /**
     * @see com.googlecode.streamflyer.core.Modifier#modify(java.lang.StringBuilder,
     *      int, boolean)
     */
    @Override
    public AfterModification modify(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        matcher.reset(characterBuffer);
        matcher.region(firstModifiableCharacterInBuffer,
                characterBuffer.length());

        // String newCharacterBuffer = matcher.replaceAll(replacement);
        // characterBuffer.setLength(0);
        // characterBuffer.append(newCharacterBuffer);

        int start = firstModifiableCharacterInBuffer;
        while (matcher.find(start)) {
            start = onMatch(characterBuffer);
        }

        return factory.skipEntireBuffer(characterBuffer,
                firstModifiableCharacterInBuffer, endOfStreamHit);
    }

    /**
     * Replaces the found invalid XML character with the given replacement.
     * <p>
     * You may override this method to insert some information about invalid
     * character in to the character buffer.
     * 
     * @param characterBuffer
     */
    protected int onMatch(StringBuilder characterBuffer) {

        String replacement_ = replacement(characterBuffer);
        characterBuffer.replace(matcher.start(), matcher.end(), replacement_);

        // calling reset(..) is redundant as find(start) calls reset() first
        // if (replacement_.length() != 1) {
        // matcher.reset(characterBuffer);
        // }

        return matcher.start() + replacement_.length();

    }

    protected String replacement(StringBuilder characterBuffer) {
        if (dollarZero) {

            // character formatted as "U+\d{4}"
            char ch = characterBuffer.charAt(matcher.start());
            String chHex = Integer.toString(ch, 16).toUpperCase();
            while (chHex.length() < 4) {
                chHex = "0" + chHex;
            }
            chHex = "U+" + chHex;

            // TODO should I do some caching of chHex to improve performance?

            return replacement.replace("$0", chHex);
        }
        else {
            return replacement;
        }
    }


    //
    // override Object.*
    //


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("InvalidXmlCharacterModifier [\nreplacement=");
        builder.append(replacement);
        builder.append(", \nmatcher=");
        builder.append(matcher);
        builder.append(", \ndollarZero=");
        builder.append(dollarZero);
        builder.append("]");
        return builder.toString();
    }


}

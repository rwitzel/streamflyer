/**
 * Copyright (C) 2011 rwitzel75@googlemail.com
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
package com.github.rwitzel.streamflyer.xml;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.rwitzel.streamflyer.core.AfterModification;
import com.github.rwitzel.streamflyer.core.Modifier;
import com.github.rwitzel.streamflyer.internal.thirdparty.ZzzValidate;
import com.github.rwitzel.streamflyer.util.ModificationFactory;
import com.github.rwitzel.streamflyer.util.ModifyingReaderFactory;
import com.github.rwitzel.streamflyer.util.statistics.LineColumnAwareModificationFactory;
import com.github.rwitzel.streamflyer.util.statistics.PositionAwareModificationFactory;

/**
 * This modifier replaces invalid XML characters in an stream that contains an XML document.
 * <p>
 * <h1>Contents</h1>
 * <p>
 * <b> <a href="#g1">1. How do I use this modifier?</a><br/>
 * <a href="#g2">2. In addition to the replacement of the invalid characters I want to record the replacements and sent
 * notifications. How can I do this?</a> <br/>
 * <a href="#g3">3. How can I find out the position of the matches within the stream?</a> <br/>
 * <a href="#g4">4. How do I use this class with an {@link InputStream}?</a><br/>
 * <a href="#g5">5. How much memory does the modifier consume?</a><br/>
 * <a href="#g6">6. When should I not use this modifier?</a><br/>
 * </b> <!-- ++++++++++++++++++++++++++++++ -->
 * <p>
 * <h3 id="g1">1. How do I use this modifier?</h3>
 * <p>
 * EXAMPLE 1. This example shows how to remove the invalid characters:
 * <code><pre class="prettyprint lang-java">// choose the character stream to modify
Reader reader = new StringReader("foo\uD8FFbar");

// define what and how to replace (the empty string as replacement)
Modifier modifier = new InvalidXmlCharacterModifier("",
        InvalidXmlCharacterModifier.XML_11_VERSION);

// create the modifying reader that wraps the original reader
ModifyingReader modifyingReader = new ModifyingReader(reader, modifier);

// use the modifying reader instead of the original reader
String actualOutput = IOUtils.toString(modifyingReader);

assertEquals("foobar", actualOutput);</pre></code>
 * <p>
 * EXAMPLE 2. This example shows how to replace the invalid characters with an error message:
 * <code><pre class="prettyprint lang-java">// choose the character stream to modify
Reader reader = new StringReader("foo\uD8FFbar");

// define what and how to replace (an error message as replacement)
Modifier modifier = new InvalidXmlCharacterModifier(
        "[INVALID XML CHAR FOUND: $0]",
        InvalidXmlCharacterModifier.XML_11_VERSION);

// create the modifying reader that wraps the original reader
ModifyingReader modifyingReader = new ModifyingReader(reader, modifier);

// use the modifying reader instead of the original reader
String actualOutput = IOUtils.toString(modifyingReader);

assertEquals("foo[INVALID XML CHAR FOUND: U+D8FF]bar", actualOutput);</pre></code>
 * <h3 id="g2">2. In addition to the replacement of the invalid characters I want to record the replacements and sent
 * notifications. How can I do this?</h3>
 * <p>
 * Subclass {@link InvalidXmlCharacterModifier} and override
 * {@link InvalidXmlCharacterModifier#replacement(StringBuilder)}.
 * <h3 href="#g3">3. How can I find out the position of the invalid characters within the stream?</h3> <br/>
 * <p>
 * You must count the characters that you skip. You can do this by subclassing {@link InvalidXmlCharacterModifier} and
 * overwrite {@link Modifier#modify(StringBuilder, int, boolean)}.
 * <p>
 * You might find {@link LineColumnAwareModificationFactory} and {@link PositionAwareModificationFactory} helpful as
 * well.
 * <h3 id="g4">4. How do I use this class with an {@link InputStream}?</h3>
 * <p>
 * See {@link ModifyingReaderFactory#createInvalidXmlCharacterRemovingReader(InputStream)}.
 * <h3 id="g5">5. How much memory does the modifier consume?</h3>
 * <p>
 * The memory consumption of this modifier is roughly given by the first argument of
 * {@link #InvalidXmlCharacterModifier(int, String, String, boolean)}.
 * <p>
 * <h3 id="g5">6. When should I not use this modifier?</h3>
 * <p>
 * Use {@link XmlVersionModifier} instead of this class if only the (wrong) version in the prolog of the XML document
 * restricts the range of valid characters.
 * 
 * @author rwoo
 * @since 23.06.2011
 */
public class InvalidXmlCharacterModifier implements Modifier {

    /**
     * http://www.w3.org/TR/xml/#charsets (referring to the fifth edition of the specification)
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

    /**
     * The replacement for each invalid XML character.
     */
    protected String replacement;

    /**
     * This matcher matches invalid XML characters.
     */
    protected Matcher matcher;

    /**
     * If this is true, then the string "$0" in the replacement is replaced with the hexadecimal representation of the
     * XML character.
     */
    protected boolean dollarZero;

    //
    // constructors
    //

    /**
     * Like {@link InvalidXmlCharacterModifier#InvalidXmlCharacterModifier(int, String, String, boolean)} but uses 8192
     * as default for <code>newNumberOfChars</code> and sets <code>dollarZero</code> to true if the replacement string
     * contains "$0".
     */
    public InvalidXmlCharacterModifier(String replacement, String xmlVersion) {
        this(8192, replacement, xmlVersion, replacement.contains("$0"));
    }

    /**
     * @param newNumberOfChars
     * @param replacement
     *            the string that shall replace invalid XML characters. This string may contain "$0" which refers to the
     *            replaced character, see {@link Matcher#replaceAll(String)}
     * @param xmlVersion
     *            Must not be <code>null</code>.
     * @param dollarZero
     */
    public InvalidXmlCharacterModifier(int newNumberOfChars, String replacement, String xmlVersion, boolean dollarZero) {

        ZzzValidate.notNull(replacement, "replacement must not be null");
        ZzzValidate.notNull(xmlVersion, "xmlVersion must not be null");

        this.factory = new ModificationFactory(0, newNumberOfChars);
        this.replacement = replacement;
        this.dollarZero = dollarZero;

        // initialize matcher
        Pattern pattern;
        if (XML_10_VERSION.equals(xmlVersion)) {
            pattern = Pattern.compile(getInvalidXmlCharacterRegex_Xml10());
        } else if (XML_11_VERSION.equals(xmlVersion)) {
            pattern = Pattern.compile(getInvalidXmlCharacterRegex_Xml11());
        } else {
            throw new IllegalArgumentException("xmlVersion has the illegal " + "(or unsupported) value " + xmlVersion);
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
     * @return Returns a regular expression that matches invalid XML 1.0 characters.
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
     * @return Returns a regular expression that matches invalid XML 1.1 characters.
     */
    protected String getInvalidXmlCharacterRegex_Xml11() {
        return "[^\\u0001-\\uD7FF\\uE000-\\uFFFD\\u10000-\\u10FFFF]";
    }

    //
    // override Modifier.*
    //

    /**
     * @see com.github.rwitzel.streamflyer.core.Modifier#modify(java.lang.StringBuilder, int, boolean)
     */
    @Override
    public AfterModification modify(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit) {

        matcher.reset(characterBuffer);
        matcher.region(firstModifiableCharacterInBuffer, characterBuffer.length());

        // String newCharacterBuffer = matcher.replaceAll(replacement);
        // characterBuffer.setLength(0);
        // characterBuffer.append(newCharacterBuffer);

        int start = firstModifiableCharacterInBuffer;
        while (matcher.find(start)) {
            start = onMatch(characterBuffer);
        }

        return factory.skipEntireBuffer(characterBuffer, firstModifiableCharacterInBuffer, endOfStreamHit);
    }

    /**
     * Replaces the found invalid XML character with the given replacement.
     * <p>
     * You may override this method to insert some information about invalid character in to the character buffer.
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
        } else {
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

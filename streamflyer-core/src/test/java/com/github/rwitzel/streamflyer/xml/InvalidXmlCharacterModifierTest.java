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

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;

import com.github.rwitzel.streamflyer.core.Modifier;
import com.github.rwitzel.streamflyer.core.ModifyingReader;
import com.github.rwitzel.streamflyer.xml.InvalidXmlCharacterModifier;

/**
 * Tests {@link InvalidXmlCharacterModifier}.
 * 
 * @author rwoo
 * @since 23.06.2011
 */
public class InvalidXmlCharacterModifierTest {

    @Test
    public void testModify_dollarZero() throws Exception {

        assertModify("<INVALID XML CHAR $0>", "a\uD8FFb", "a<INVALID XML CHAR U+D8FF>b", "1.0", 8192, true);

        // two dollarZeros
        assertModify("<INVALID XML CHAR $0 $0>", "a\uD8FFb", "a<INVALID XML CHAR U+D8FF U+D8FF>b", "1.0", 8192, true);

        // dollarZero deactivated
        assertModify("<INVALID XML CHAR $0 $0>", "a\uD8FFb", "a<INVALID XML CHAR $0 $0>b", "1.0", 8192, false);

        // three replacements
        assertModify("$0", "\uD8FF x \uD8FF y \uD8FF", "U+D8FF x U+D8FF y U+D8FF", "1.0", 8192, true);
    }

    @Test
    public void testModify() throws Exception {

        // replace one character with whitespace
        assertModify(" ", "a\uD8FFb", "a b", "1.0", 8192, false);

        // replace two characters with whitespace
        assertModify(" ", "a\uD8FFb\uD8FFc", "a b c", "1.0", 8192, false);

        // delete two characters
        assertModify("", "a\uD8FFb\uD8FFc", "abc", "1.0", 8192, false);

        // delete two characters (a newline in the string)
        assertModify("", "a\uD8FF\n\uD8FFc", "a\nc", "1.0", 8192, false);
    }

    @Test
    public void testModify_XML_10() throws Exception {

        // valid chars
        // 0009 = \t, 000A = \n, 000D = \r
        assertModify("IIIIII", "\t_1_\n_2_\r_3_\u0020_4_\uD7FF_5_\uE000_6_\uFFFD",
                "\t_1_\n_2_\r_3_\u0020_4_\uD7FF_5_\uE000_6_\uFFFD", "1.0", 8192, false);

        // invalid chars
        assertModify("IIIIII", "\u0007_1_\u0008_2_\u000B_3_\u001F_4_\uD800_5_\uDFFF_6_\uFFFE",
                "IIIIII_1_IIIIII_2_IIIIII_3_IIIIII_4_IIIIII_5_IIIIII_6_IIIIII", "1.0", 8192, false);

    }

    @Test
    public void testModify_XML_11() throws Exception {

        // invalid chars
        assertModify("IIIIII", //
                "\uD800_1_\uDFFF_2_\uFFFE", //
                "IIIIII_1_IIIIII_2_IIIIII", "1.1", 8192, false);

        // valid chars
        assertModify("IIIIII", //
                "\u0001_1_\uD7FF_2_\uE000_3_\uFFFD", //
                "\u0001_1_\uD7FF_2_\uE000_3_\uFFFD", "1.1", 8192, false);

    }

    private void assertModify(String replacement, String input, String expectedOutput, String xmlVersion,
            int requestedCapacityOfCharacterBuffer, boolean dollarZero) throws Exception {

        Reader reader = new StringReader(input);
        Modifier modifier = new InvalidXmlCharacterModifier(requestedCapacityOfCharacterBuffer, replacement,
                xmlVersion, dollarZero);
        ModifyingReader modifyingReader = new ModifyingReader(reader, modifier);

        String actualOutput = IOUtils.toString(modifyingReader);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void testExampleFromHomepage_removeInvalidCharacters() throws Exception {

        // choose the character stream to modify
        Reader reader = new StringReader("foo\uD8FFbar");

        // define what and how to replace
        Modifier modifier = new InvalidXmlCharacterModifier("", InvalidXmlCharacterModifier.XML_11_VERSION);

        // create the modifying reader that wraps the original reader
        ModifyingReader modifyingReader = new ModifyingReader(reader, modifier);

        // use the modifying reader instead of the original reader
        String actualOutput = IOUtils.toString(modifyingReader);

        assertEquals("foobar", actualOutput);
    }

    @Test
    public void testExampleFromHomepage_replaceWithErrorMessage() throws Exception {

        // choose the character stream to modify
        Reader reader = new StringReader("foo\uD8FFbar");

        // define what and how to replace
        Modifier modifier = new InvalidXmlCharacterModifier("[INVALID XML CHAR FOUND: $0]",
                InvalidXmlCharacterModifier.XML_11_VERSION);

        // create the modifying reader that wraps the original reader
        ModifyingReader modifyingReader = new ModifyingReader(reader, modifier);

        // use the modifying reader instead of the original reader
        String actualOutput = IOUtils.toString(modifyingReader);

        assertEquals("foo[INVALID XML CHAR FOUND: U+D8FF]bar", actualOutput);
    }
}

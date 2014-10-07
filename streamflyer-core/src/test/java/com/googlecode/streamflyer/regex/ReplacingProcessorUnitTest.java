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

package com.googlecode.streamflyer.regex;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;

import java.util.List;

import junit.framework.TestCase;

import com.googlecode.catchexception.CatchException;

/**
 * Tests {@link ReplacingProcessor}.
 * 
 * @author rwoo
 * @since 18.06.2011
 */
public class ReplacingProcessorUnitTest extends TestCase {

    public void testCompileReplacement_all() throws Exception {
        assertCompiledReplacement("12\\345$6789ABC\\\\0\\$DEF", "12345", 6789, "ABC\\0$DEF");
    }

    public void testCompileReplacement_normalCharacters() throws Exception {
        assertCompiledReplacement("123", "123");
    }

    public void testCompileReplacement_escapedLiteral() throws Exception {
        assertCompiledReplacement("\\x", "x");
        assertCompiledReplacement("\\1", "1");
        assertCompiledReplacement("\\$", "$");
        assertCompiledReplacement("\\\\", "\\");

        assertCompiledReplacement("abc\\xabc", "abcxabc");
        assertCompiledReplacement("abc\\1abc", "abc1abc");
        assertCompiledReplacement("abc\\$abc", "abc$abc");
        assertCompiledReplacement("abc\\\\abc", "abc\\abc");
    }

    public void testCompileReplacement_groupReference() throws Exception {
        assertCompiledReplacement("$1", 1);
        assertCompiledReplacement("$9", 9);
        assertCompiledReplacement("$1234", 1234);

        // TODO this should be invalid?
        assertCompiledReplacement("$01", 1);

        assertCompiledReplacement("abc$1def", "abc", 1, "def");
        assertCompiledReplacement("abc$9def", "abc", 9, "def");
        assertCompiledReplacement("abc$1234def", "abc", 1234, "def");

        // TODO this should be invalid?
        assertCompiledReplacement("abc$01def", "abc", 1, "def");
    }

    public void testCompileReplacement_groupReference_invalid() throws Exception {

        catchException(new ReplacingProcessor()).parseReplacement("hossa$");

        assertTrue(caughtException() instanceof IllegalArgumentException);
        assertEquals("group reference $ without number at the end of the" + " replacement string (hossa$)",
                CatchException.caughtException().getMessage());
    }

    /**
     * @param matchProcessor
     * @param expectedParts
     */
    private void assertCompiledReplacement(String replacement, Object... expectedParts) {

        List<Object> foundParts = new ReplacingProcessor().parseReplacement(replacement);
        assertEquals(expectedParts.length, foundParts.size());
        for (int index = 0; index < expectedParts.length; index++) {
            assertEquals(expectedParts[index], foundParts.get(index));
        }
    }
}

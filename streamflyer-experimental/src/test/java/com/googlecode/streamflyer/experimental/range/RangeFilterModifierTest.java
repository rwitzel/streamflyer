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

package com.googlecode.streamflyer.experimental.range;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;

import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.regex.MatchProcessor;
import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.regex.OnStreamMatcher;
import com.googlecode.streamflyer.regex.OnStreamStandardMatcher;
import com.googlecode.streamflyer.regex.RegexModifier;

/**
 * Tests {@link RangeFilterModifier}.
 * 
 * @author rwoo
 * 
 * @since 14.09.2011
 */
public class RangeFilterModifierTest extends TestCase {

    public void testRangeFiltered_1_initiallyOff() throws Exception {
        assertRangeFilter("aaaXbbbYccc", "XbbbY", "X", "Y", true, true, false);
        assertRangeFilter("aaaXbbbYccc", "Xbbb", "X", "Y", true, false, false);
        assertRangeFilter("aaaXbbbYccc", "bbbY", "X", "Y", false, true, false);
        assertRangeFilter("aaaXbbbYccc", "bbb", "X", "Y", false, false, false);
    }

    public void testRangeFiltered_2_initiallyOn() throws Exception {
        assertRangeFilter("aaXbbbYccc", "aaXbbbY", "X", "Y", true, true, true);
        assertRangeFilter("aaXbbbYccc", "aaXbbb", "X", "Y", true, false, true);
        assertRangeFilter("aaXbbbYccc", "aaXbbbY", "X", "Y", false, true, true);
        assertRangeFilter("aaXbbbYccc", "aaXbbb", "X", "Y", false, false, true);
    }

    public void testRangeFiltered_3_twoRangesFound() throws Exception {
        assertRangeFilter("aaXbYcXdYee", "XbYXdY", "X", "Y", true, true, false);
        assertRangeFilter("aaXbYcXdYee", "XbXd", "X", "Y", true, false, false);
        assertRangeFilter("aaXbYcXdYee", "bYdY", "X", "Y", false, true, false);
        assertRangeFilter("aaXbYcXdYee", "bd", "X", "Y", false, false, false);

        assertRangeFilter("aaXbYcXdYee", "aaXbYXdY", "X", "Y", true, true, true);
        assertRangeFilter("aaXbYcXdYee", "aaXbXd", "X", "Y", true, false, true);
        assertRangeFilter("aaXbYcXdYee", "aaXbYdY", "X", "Y", false, true, true);
        assertRangeFilter("aaXbYcXdYee", "aaXbd", "X", "Y", false, false, true);
    }

    private void assertRangeFilter(String input, String expectedOutput,
            String startTag, String endTag, boolean includeStart,
            boolean includeEnd, boolean initiallyOn) throws Exception {

        assertOutputByReader(
                input,
                expectedOutput,
                createModifier(startTag, endTag, includeStart, includeEnd,
                        initiallyOn));
    }

    private RegexModifier createRegexModifier(String regex) {

        Matcher matcher = Pattern.compile(regex, 0).matcher("");
        matcher.useTransparentBounds(true);
        matcher.useAnchoringBounds(false);
        OnStreamMatcher onStreamMatcher = new OnStreamStandardMatcher(matcher);
        return new RegexModifier(onStreamMatcher, new MatchProcessor() {

            @Override
            public MatchProcessorResult process(StringBuilder characterBuffer,
                    int firstModifiableCharacterInBuffer,
                    MatchResult matchResult) {
                throw new UnsupportedOperationException(
                        "this match processor must be replaced with another one");
            }
        }, 12, 345);
    }

    private RangeFilterModifier createModifier(String startTag, String endTag,
            boolean includeStart, boolean includeEnd, boolean initiallyOn) {

        RegexModifier startModifier = createRegexModifier(startTag);
        RegexModifier endModifier = createRegexModifier(endTag);
        RangeFilterModifier rangeFilterModifier = new RangeFilterModifier(
                startModifier, endModifier, includeStart, includeEnd,
                initiallyOn);

        return rangeFilterModifier;
    }

    private void assertOutputByReader(String input, String expectedOutput,
            Modifier modifier) throws Exception {

        // create reader
        Reader reader = new ModifyingReader(new BufferedReader(
                new StringReader(input)), modifier);

        // read the stream into an output stream
        String foundOutput = IOUtils.toString(reader);

        assertEquals(expectedOutput, foundOutput);
    }

}

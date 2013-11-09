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
package com.googlecode.streamflyer.experimental.range2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.googlecode.streamflyer.experimental.range2.MatchResultShifted;

/**
 * Tests {@link MatchResultShifted}.
 * 
 * @author rwoo
 * 
 */
public class MatchResultShiftedTest {

    @Test
    public void testGroupCountAndMore() throws Exception {
        StringBuilder input = new StringBuilder("1234567890123451xx2yy5aa6bb");
        String regex = "(1xx(2yy((4zz)|(5aa)))(6bb))";
        Matcher matcher = Pattern.compile(regex).matcher(input);
        assertTrue(matcher.find());
        assertEquals(6, matcher.groupCount());
        assertEquals("1xx2yy5aa6bb", matcher.group());

        {
            assertGroups(matcher, 0);
        }

        // delete the first ten characters from the input
        input.delete(0, 10);
        assertEquals(6, matcher.groupCount());
        try {
            // learning test: the matcher works directly on the string builder (it does not cache groups)
            assertEquals("1xx2yy5aa6bb", matcher.group());
            fail("StringIndexOutOfBoundsException expected");
        } catch (StringIndexOutOfBoundsException e) {

        }

        {
            // offset = 0 -> "1xx2yy5aa6bb" will be the match result
            MatchResult result = new MatchResultShifted(matcher, input, -10);
            assertGroups(result, -10);
        }

    }

    private void assertGroups(MatchResult result, int shift) {
        assertGroup(-1, "1xx2yy5aa6bb", 15 + shift, 27 + shift, result);
        assertGroup(0, "1xx2yy5aa6bb", 15 + shift, 27 + shift, result);
        assertGroup(1, "1xx2yy5aa6bb", 15 + shift, 27 + shift, result);
        assertGroup(2, "2yy5aa", 18 + shift, 24 + shift, result);
        assertGroup(3, "5aa", 21 + shift, 24 + shift, result);
        assertGroup(4, null, -1, -1, result);
        assertGroup(5, "5aa", 21 + shift, 24 + shift, result);
        assertGroup(6, "6bb", 24 + shift, 27 + shift, result);
        assertEquals(6, result.groupCount());
    }

    private void assertGroup(int groupIndex, String group, int start, int end, MatchResult matchResult) {
        if (groupIndex >= 0) {
            assertEquals(group, matchResult.group(groupIndex));
            assertEquals(start, matchResult.start(groupIndex));
            assertEquals(end, matchResult.end(groupIndex));
        } else {
            assertEquals(group, matchResult.group());
            assertEquals(start, matchResult.start());
            assertEquals(end, matchResult.end());
        }
    }

}

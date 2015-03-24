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
package com.github.rwitzel.streamflyer.regex.addons.tokens;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.github.rwitzel.streamflyer.regex.addons.tokens.MatchResultWithOffset;

/**
 * Tests {@link MatchResultWithOffset}.
 * 
 * @author rwoo
 * 
 */
public class MatchResultWithOffsetTest {

    /**
     * Test for the unexpected behaviour of empty groups
     * 
     * @throws Exception
     */
    @Test
    public void testGroupCountAndMore_UNEXPECTED_emptyGroupsAfterEndOfTargetGroupIncluded() throws Exception {

        String input = "1xxnnnnnnnnnn";
        String regex = "(1xx())()";
        Matcher matcher = Pattern.compile(regex).matcher(input);
        assertTrue(matcher.find());
        assertEquals(3, matcher.groupCount()); // principle of the longest possible match

        {
            // offset = 1 -> "1xx" will be the match result
            MatchResultWithOffset mo = new MatchResultWithOffset(matcher, 1);
            assertGroup(-1, "1xx", 0, 3, mo);
            assertGroup(0, "1xx", 0, 3, mo);
            assertGroup(1, "", 3, 3, mo); // group "()"
            assertGroup(2, "", 3, 3, mo); // group "()" UNEXPECTED!
            assertEquals(2, mo.groupCount()); // UNEXPECTED!
        }

    }

    /**
     * Test for the unexpected behaviour of unmatched groups
     * 
     * @throws Exception
     */
    @Test
    public void testGroupCountAndMore_UNEXPECTED_notMatchingGroupsAfterEndOfTargetGroupIncluded() throws Exception {

        String input = "1xxnnnnnnnnnn";
        String regex = "(1xx(2yy)?)(3zz)?";
        Matcher matcher = Pattern.compile(regex).matcher(input);
        assertTrue(matcher.find());
        assertEquals(3, matcher.groupCount()); // principle of the longest possible match

        {
            // offset = 1 -> "1xx" will be the match result
            MatchResultWithOffset mo = new MatchResultWithOffset(matcher, 1);
            assertGroup(-1, "1xx", 0, 3, mo);
            assertGroup(0, "1xx", 0, 3, mo);
            assertGroup(1, null, -1, -1, mo); // group "(2yy)?"
            assertGroup(2, null, -1, -1, mo); // group "(3yy)?" UNEXPECTED!
            assertEquals(2, mo.groupCount()); // UNEXPECTED!
        }

    }

    @Test
    public void testGroupCountAndMore() throws Exception {
        String input = "1234567890123451xx2yy5aa6bb";
        String regex = "(1xx(2yy((4zz)|(5aa)))(6bb))";
        Matcher matcher = Pattern.compile(regex).matcher(input);
        assertTrue(matcher.find());
        assertEquals(6, matcher.groupCount());

        {
            // offset = 0 -> "1xx2yy5aa6bb" will be the match result
            MatchResultWithOffset mo = new MatchResultWithOffset(matcher, 0);
            assertGroup(-1, "1xx2yy5aa6bb", 15, 27, mo);
            assertGroup(0, "1xx2yy5aa6bb", 15, 27, mo);
            assertGroup(1, "1xx2yy5aa6bb", 15, 27, mo);
            assertGroup(2, "2yy5aa", 18, 24, mo);
            assertGroup(3, "5aa", 21, 24, mo);
            assertGroup(4, null, -1, -1, mo);
            assertGroup(5, "5aa", 21, 24, mo);
            assertGroup(6, "6bb", 24, 27, mo);
            assertEquals(6, mo.groupCount());
        }

        {
            // offset = 1 -> "1xx2yy5aa6bb" will be the match result
            MatchResultWithOffset mo = new MatchResultWithOffset(matcher, 1);
            assertGroup(-1, "1xx2yy5aa6bb", 15, 27, mo);
            assertGroup(0, "1xx2yy5aa6bb", 15, 27, mo);
            assertGroup(1, "2yy5aa", 18, 24, mo);
            assertGroup(2, "5aa", 21, 24, mo);
            assertGroup(3, null, -1, -1, mo);
            assertGroup(4, "5aa", 21, 24, mo);
            assertGroup(5, "6bb", 24, 27, mo);
            assertEquals(5, mo.groupCount());
        }

        {
            // offset = 2 -> "2yy5aa" will be the match result
            MatchResultWithOffset mo = new MatchResultWithOffset(matcher, 2);
            assertEquals("2yy5aa", input.substring(18, 24));
            assertGroup(-1, "2yy5aa", 18, 24, mo);
            assertGroup(0, "2yy5aa", 18, 24, mo);
            assertGroup(1, "5aa", 21, 24, mo);
            assertGroup(2, null, -1, -1, mo);
            assertGroup(3, "5aa", 21, 24, mo);
            assertEquals(3, mo.groupCount());
        }

        {
            // offset = 3 -> "5aa" will be the match result
            MatchResultWithOffset mo = new MatchResultWithOffset(matcher, 3);
            assertGroup(-1, "5aa", 21, 24, mo);
            assertGroup(0, "5aa", 21, 24, mo);
            assertGroup(1, null, -1, -1, mo);
            assertGroup(2, "5aa", 21, 24, mo);
            assertEquals(2, mo.groupCount());
        }

        {
            // offset = 4 -> null will be the match result
            MatchResultWithOffset mo = new MatchResultWithOffset(matcher, 4);
            assertGroup(-1, null, -1, -1, mo);
            assertEquals(0, mo.groupCount());
        }

        {
            // offset = 5 -> "5aa" will be the match result
            MatchResultWithOffset mo = new MatchResultWithOffset(matcher, 5);
            assertGroup(-1, "5aa", 21, 24, mo);
            assertGroup(0, "5aa", 21, 24, mo);
            assertEquals(0, mo.groupCount());
        }

        {
            // offset = 6 -> "6bb" will be the match result
            MatchResultWithOffset mo = new MatchResultWithOffset(matcher, 6);
            assertGroup(-1, "6bb", 24, 27, mo);
            assertGroup(0, "6bb", 24, 27, mo);
            assertEquals(0, mo.groupCount());
        }

        {
            // offset = 7 -> invalid group
            try {
                new MatchResultWithOffset(matcher, 7);
                fail("IndexOutOfBoundsException expected");
            } catch (IndexOutOfBoundsException e) {
                assertEquals("No group 7", e.getMessage());
            }
        }

        {
            // offset = -1 -> invalid group
            try {
                new MatchResultWithOffset(matcher, -1);
                fail("IndexOutOfBoundsException expected");
            } catch (IndexOutOfBoundsException e) {
                assertEquals("No group -1", e.getMessage());
            }
        }

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

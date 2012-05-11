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

import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.core.ModifyingWriter;

/**
 * Tests {@link RegexModifier}.
 * 
 * @author rwoo
 * @since 23.06.2011
 */
public class RegexModifierTest extends AbstractRegexModifierTest {

    public void testExampleFromHomepage_usage() throws Exception {

        // choose the character stream to modify
        Reader originalReader = new StringReader("edit stream");

        // select the modifier
        Modifier myModifier = new RegexModifier("edit stream", 0,
                "modify stream");

        // create the modifying reader that wraps the original reader
        Reader modifyingReader = new ModifyingReader(originalReader, myModifier);

        // use the modifying reader instead of the original reader
        String output = IOUtils.toString(modifyingReader);
        assertEquals("modify stream", output);
    }

    private String modify(String input, Modifier myModifier) throws Exception {
        // choose the character stream to modify
        Reader originalReader = new StringReader(input);

        // create the modifying reader that wraps the original reader
        Reader modifyingReader = new ModifyingReader(originalReader, myModifier);

        // use the modifying reader instead of the original reader
        String output = IOUtils.toString(modifyingReader);

        return output;
    }

    /**
     * Asserts that two objects are not equals. Otherwise an
     * AssertionFailedError is thrown.
     */
    static public void assertNotEquals(Object expected, Object actual) {
        assertTrue(expected != actual || !expected.equals(actual));
    }

    public void testExampleFromHomepage_advancedExample_firstImprovement()
            throws Exception {

        // select the modifier
        Modifier myModifier = new RegexModifier("edit stream", 0,
                "modify stream");

        // test: does not support other whitespace characters
        assertNotEquals("modify\tstream", modify("edit\tstream", myModifier));
        // test: does not support new line characters
        assertNotEquals("modify\nstream", modify("edit\nstream", myModifier));

        // first improvement
        Modifier myModifier1 = new RegexModifier("edit\\sstream",
                Pattern.DOTALL, "modify stream");

        // test: supports other whitespace characters
        assertEquals("modify stream", modify("edit\tstream", myModifier1));
        // test: supports new line characters
        assertEquals("modify stream", modify("edit\nstream", myModifier1));

    }

    public void testExampleFromHomepage_advancedExample_secondImprovement()
            throws Exception {

        // first improvement
        Modifier myModifier1 = new RegexModifier("edit\\sstream",
                Pattern.DOTALL, "modify stream");


        // test: does not preserve type of whitespace characters
        assertNotEquals("modify\tstream", modify("edit\tstream", myModifier1));
        // test: does not support many whitespace characters
        assertNotEquals("modify  stream", modify("edit  stream", myModifier1));

        // second improvement
        Modifier myModifier2 = new RegexModifier("edit(\\s++stream)",
                Pattern.DOTALL, "modify$1");

        // test: preserves type of whitespace characters
        assertEquals("modify\tstream", modify("edit\tstream", myModifier2));
        // test: supports many whitespace characters
        assertEquals("modify  stream", modify("edit  stream", myModifier2));
    }

    public void testExampleFromHomepage_advancedExample_thirdImprovement()
            throws Exception {

        // second improvement
        Modifier myModifier2 = new RegexModifier("edit(\\s++stream)",
                Pattern.DOTALL, "modify$1");

        // test: does not use look-behind
        assertNotEquals("credit stream", modify("credit stream", myModifier2));

        // third and final improvement
        Modifier myModifier3 = new RegexModifier("(?<=\\s)edit(\\s++stream)",
                Pattern.DOTALL, "modify$1", 1, 2048);

        // test: uses look-behind
        assertEquals("credit stream", modify("credit stream", myModifier3));
    }

    public void testExampleFromHomepage_advancedExample_greedyQuantifierOnDot()
            throws Exception {

        // Don't do this! This example uses a greedy quantifier on a dot
        Modifier myModifier4 = new RegexModifier("edit.*stream", 0,
                "modify stream");

        // test: does not find the nearest match
        assertNotEquals("modify stream modify stream",
                modify("edit stream edit stream", myModifier4));

        // modifier with greedy quantifier on whitespace
        Modifier myModifier5 = new RegexModifier("edit\\s*stream", 0,
                "modify stream");

        // test: finds the nearest match
        assertNotEquals("modify stream modify stream",
                modify("edit stream edit stream", myModifier5));
    }

    public void learningTest_matchingAtZeroLengthRegion() {
        String input = "abcdeghi";
        Matcher matcher = Pattern.compile("x").matcher(input);

        matcher.region(4, 4);
        boolean result = matcher.lookingAt();
        // System.out.println("result: " + result);
        // System.out.println("hitEnd: " + matcher.hitEnd());
        assertEquals(false, result);
        assertEquals(true, matcher.hitEnd());
    }

    public void temptestReplacement_OneCharacterReplacedWithAnother()
            throws Exception {

        // once in the middle of the string
        assertReplacementByReader("abcdefghi", "e", "X", 0, 20, "abcdXfghi");
    }

    public void testReplacement_OneCharacterReplacedWithAnother()
            throws Exception {

        // once in the middle of the string
        assertReplacement("abcdefghi", "e", "X", "abcdXfghi");

        // once at the being, at the end, and three times in the middle of the
        // string
        assertReplacement("ebcdeefehe", "e", "X", "XbcdXXfXhX");
    }


    public void testReplacement_TwoCharactersReplacedWithThreeOthers()
            throws Exception {

        // once in the middle of the string
        assertReplacement("abcdefghi", "de", "XYZ", "abcXYZfghi");

        // once at the being, at the end, and three times in the middle of the
        // string
        assertReplacement("DEbcDEDEfDEgDE", "DE", "XYZ", "XYZbcXYZXYZfXYZgXYZ");
    }

    public void testReplacement_ThreeCharactersReplacedWithTwoOthers()
            throws Exception {

        // once in the middle of the string
        assertReplacement("abcDEFghi", "DEF", "XY", "abcXYghi");

        // once at the being, at the end, and three times in the middle of the
        // string
        assertReplacement("DEFbcDEFDEFgDEFhiDEF", "DEF", "XY",
                "XYbcXYXYgXYhiXY");
    }

    public void testReplacement_GroupsReplaced() throws Exception {

        assertReplacement("abcDEFFghi", "D(E)(FF)", "X$2$1Y", "abcXFFEYghi");
    }

    public void testReplacement_ReluctantOp() throws Exception {

        assertReplacement("abcDEEFFghi", "D(.*?)F", "X$1Y", "abcXEEYFghi");
    }

    public void testReplacement_GreedyOp_AttentionThisLoadsTheEntireInputIntoMemory()
            throws Exception {

        assertReplacement("abcDEEFFghi", "D(.*)F", "X$1Y", "abcXEEFYghi");
    }

    public void testReplacement_GreedyOp_Vs_ReluctantOp() throws Exception {

        // greedy
        assertReplacement("<x>...</x>...</x>", "<x>(.*)</x>", "<y/>", 0, 6,
                "<y/>");
        // reluctant
        assertReplacement("<x>...</x>...</x>", "<x>(.*?)</x>", "<y/>", 0, 6,
                "<y/>...</x>");
    }

    public void testReplacement_lookBehind() throws Exception {
        // (?<=X) X, via zero-width positive lookbehind

        // once in the middle of the string
        assertReplacement("abcdefghi", "(?<=c)de", "XYZ", "abcXYZfghi");

        // once at the being, at the end, and three times in the middle of the
        // string
        assertReplacement("DEbcDEDEcDEgDE", "(?<=c)DE", "XYZ",
                "DEbcXYZDEcXYZgDE");
    }

    public void testReplacement_lookAhead() throws Exception {
        // (?=X) X, via zero-width positive lookahead

        // once in the middle of the string
        assertReplacement("abcdefghi", "de(?=f)", "XYZ", "abcXYZfghi");

        // once at the being, at the end, and three times in the middle of the
        // string
        assertReplacement("DEfcDEDEfDEfDE", "DE(?=f)", "XYZ",
                "XYZfcDEXYZfXYZfDE");
    }

    /**
     * Defaults: Varies requestedMinimumLengthsOfLookBehind and
     * requestedCapacityOfCharacterBuffers.
     */
    protected void assertReplacement(String input, String regex,
            String replacement, String expectedOutput) throws Exception {

        for (int lookBehind = 0; lookBehind <= 2; lookBehind++) {

            for (int capacityCharBuf = 1; capacityCharBuf <= 18; capacityCharBuf++) {

                if (regex.contains("(?<=") && lookBehind == 0) {
                    // for a regex with look-behind we need look-behind > 0.
                    // Therefore, we skip this test as the parameter is not
                    // appropriate
                }
                else {
                    assertReplacement(input, regex, replacement, lookBehind,
                            capacityCharBuf, expectedOutput);
                }
            }
        }
    }

    /**
     * Defaults: Combines tests for {@link ModifyingReader} and
     * {@link ModifyingWriter}.
     */
    protected void assertReplacement(String input, String regex,
            String replacement, int minimumLengthOfLookBehind,
            int requestedCapacityOfCharacterBuffer, String expectedOutput)
            throws Exception {

        assertReplacementByReader(input, regex, replacement,
                minimumLengthOfLookBehind, requestedCapacityOfCharacterBuffer,
                expectedOutput);

        assertReplacementByWriter(input, regex, replacement,
                minimumLengthOfLookBehind, requestedCapacityOfCharacterBuffer,
                expectedOutput);

    }
}

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

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;

import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.core.ModifyingWriter;
import com.googlecode.streamflyer.regex.OnStreamMatcher;
import com.googlecode.streamflyer.regex.OnStreamStandardMatcher;
import com.googlecode.streamflyer.regex.RegexModifier;
import com.googlecode.streamflyer.regex.ReplacingProcessor;

/**
 * Tests {@link RegexModifier}.
 * 
 * @author rwoo
 * @since 23.06.2011
 */
public class RegexModifierTest extends TestCase {

    public void testExampleFromHomepage() throws Exception {

        // choose the character stream to modify
        Reader originalReader = new StringReader(
                " 1edit stream  2 edit \t \n stream 3 editstream");

        // select the modifier
        @SuppressWarnings("deprecation")
        Modifier myModifier = new RegexModifier("(?<=\\s)edit(\\s+?stream)",
                Pattern.DOTALL, "modify$1", 1, 2048);

        // create the modifying reader that wraps the original reader
        Reader modifyingReader = new ModifyingReader(originalReader, myModifier);

        // use the modifying reader instead of the original reader
        String output = IOUtils.toString(modifyingReader);
        assertEquals(" 1edit stream  2 modify \t \n stream 3 editstream",
                output);
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

    protected OnStreamMatcher createMatcher(String regex) {
        Matcher matcher = Pattern.compile(regex).matcher("");
        matcher.useTransparentBounds(true);
        return new OnStreamStandardMatcher(matcher);
    }

    protected RegexModifierWithStatistics createModifier(String regex,
            String replacement, int minimumLengthOfLookBehind,
            int requestedCapacityOfCharacterBuffer) {
        // create matcher
        OnStreamMatcher matcher = createMatcher(regex);

        // create modifier
        RegexModifierWithStatistics modifier = new RegexModifierWithStatistics( //
                matcher, //
                new ReplacingProcessor(replacement), //
                minimumLengthOfLookBehind, //
                requestedCapacityOfCharacterBuffer);

        return modifier;
    }

    public void assertReplacementByReader(String input, String regex,
            String replacement, int minimumLengthOfLookBehind,
            int requestedCapacityOfCharacterBuffer, String expectedOutput)
            throws Exception {

        // create modifier
        RegexModifierWithStatistics modifier = createModifier(regex,
                replacement, minimumLengthOfLookBehind,
                requestedCapacityOfCharacterBuffer);

        // create reader
        Reader reader = new ModifyingReader(new BufferedReader(
                new StringReader(input)), modifier);

        // read the stream into an output stream
        String foundOutput = IOUtils.toString(reader);

        // compare the expected result with the found result
        if (!expectedOutput.equals(foundOutput)) {

            System.out.println("minimumLengthOfLookBehind: "
                    + minimumLengthOfLookBehind);
            System.out.println("requestedCapacityOfCharacterBuffer: "
                    + requestedCapacityOfCharacterBuffer);

            assertEquals(expectedOutput, foundOutput);
        }

        // assertStatistics(minimumLengthOfLookBehind,
        // requestedCapacityOfCharacterBuffer, regex, modifier);
    }

    public void assertReplacementByWriter(String input, String regex,
            String replacement, int minimumLengthOfLookBehind,
            int requestedCapacityOfCharacterBuffer, String expectedOutput)
            throws Exception {

        // create modifier
        RegexModifierWithStatistics modifier = createModifier(regex,
                replacement, minimumLengthOfLookBehind,
                requestedCapacityOfCharacterBuffer);

        // setup: create modifier and writer
        StringWriter stringWriter = new StringWriter();
        ModifyingWriter writer = new ModifyingWriter(stringWriter, modifier);

        // write the stream to an output stream
        for (int index = 0; index < input.length(); index++) {
            writer.append(input.charAt(index));
        }
        writer.flush();
        writer.close();

        String foundOutput = stringWriter.toString();

        // compare the expected result with the found result
        if (!expectedOutput.equals(foundOutput)) {

            System.out.println("minimumLengthOfLookBehind: "
                    + minimumLengthOfLookBehind);
            System.out.println("requestedCapacityOfCharacterBuffer: "
                    + requestedCapacityOfCharacterBuffer);

            assertEquals(expectedOutput, foundOutput);
        }

        // assertStatistics(minimumLengthOfLookBehind,
        // requestedCapacityOfCharacterBuffer, regex, modifier);
    }

}

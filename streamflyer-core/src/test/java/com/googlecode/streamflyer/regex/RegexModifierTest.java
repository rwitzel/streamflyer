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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Reader;
import java.io.StringReader;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.core.ModifyingWriter;
import com.googlecode.streamflyer.util.StringUtils;

/**
 * Tests {@link RegexModifier}.
 * 
 * @author rwoo
 * @since 23.06.2011
 */
public class RegexModifierTest extends AbstractRegexModifierTest {

    private class MatchPrinter implements MatchProcessor {

        @Override
        public MatchProcessorResult process(StringBuilder characterBuffer,
                int firstModifiableCharacterInBuffer, MatchResult matchResult) {

            // print the matches text
            System.out.println("match: " + matchResult.group());

            // continue matching behind the end of the matched text
            return new MatchProcessorResult(matchResult.end(), true);
        }

    }

    @Test
    public void testExampleFromRegexModifierJavadoc_OwnMatchProcessor()
            throws Exception {

        String fakeErrorLog = "1 ERROR aa\n2 WARN bb\n3 ERROR cc";

        // choose the character stream to modify
        Reader originalReader = new StringReader( //
                fakeErrorLog);

        // select the modifier
        Modifier myModifier = new RegexModifier("^.*ERROR.*$",
                Pattern.MULTILINE, new MatchPrinter(), 0, 2048);

        // create the modifying reader that wraps the original reader
        Reader modifyingReader = new ModifyingReader(originalReader, myModifier);

        // use the modifying reader instead of the original reader
        String output = IOUtils.toString(modifyingReader);
        // stream content is not modified
        assertEquals(fakeErrorLog, output);
    }

    @Test
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

    @Test
    public void testExampleFromHomepage_usage2() throws Exception {

        // choose the character stream to modify
        Reader originalReader = new StringReader("edit\n\nstream");

        // select the modifier
        Modifier myModifier = new RegexModifier("edit\\s+stream", 0,
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
    public void testReplacement_OneCharacterReplacedWithAnother()
            throws Exception {

        // once in the middle of the string
        assertReplacement("abcdefghi", "e", "X", "abcdXfghi");

        // once at the being, at the end, and three times in the middle of the
        // string
        assertReplacement("ebcdeefehe", "e", "X", "XbcdXXfXhX");
    }


    @Test
    public void testReplacement_TwoCharactersReplacedWithThreeOthers()
            throws Exception {

        // once in the middle of the string
        assertReplacement("abcdefghi", "de", "XYZ", "abcXYZfghi");

        // once at the being, at the end, and three times in the middle of the
        // string
        assertReplacement("DEbcDEDEfDEgDE", "DE", "XYZ", "XYZbcXYZXYZfXYZgXYZ");
    }

    @Test
    public void testReplacement_ThreeCharactersReplacedWithTwoOthers()
            throws Exception {

        // once in the middle of the string
        assertReplacement("abcDEFghi", "DEF", "XY", "abcXYghi");

        // once at the being, at the end, and three times in the middle of the
        // string
        assertReplacement("DEFbcDEFDEFgDEFhiDEF", "DEF", "XY",
                "XYbcXYXYgXYhiXY");
    }

    @Test
    public void testReplacement_GroupsReplaced() throws Exception {

        assertReplacement("abcDEFFghi", "D(E)(FF)", "X$2$1Y", "abcXFFEYghi");
    }

    @Test
    public void testReplacement_ReluctantOp() throws Exception {

        assertReplacement("abcDEEFFghi", "D(.*?)F", "X$1Y", "abcXEEYFghi");
    }

    @Test
    public void testReplacement_GreedyOp_AttentionThisLoadsTheEntireInputIntoMemory()
            throws Exception {

        assertReplacement("abcDEEFFghi", "D(.*)F", "X$1Y", "abcXEEFYghi");
    }

    @Test
    public void testReplacement_GreedyOp_Vs_ReluctantOp() throws Exception {

        // greedy
        assertReplacement("<x>...</x>...</x>", "<x>(.*)</x>", "<y/>", 0, 6,
                "<y/>", 0);
        // reluctant
        assertReplacement("<x>...</x>...</x>", "<x>(.*?)</x>", "<y/>", 0, 6,
                "<y/>...</x>", 0);
    }

    @Test
    public void testReplacement_lookBehind() throws Exception {
        // (?<=X) X, via zero-width positive lookbehind

        // once in the middle of the string
        assertReplacement("abcdefghi", "(?<=c)de", "XYZ", "abcXYZfghi");

        // once at the being, at the end, and three times in the middle of the
        // string
        assertReplacement("DEbcDEDEcDEgDE", "(?<=c)DE", "XYZ",
                "DEbcXYZDEcXYZgDE");
    }

    @Test
    public void testReplacement_lookAhead() throws Exception {
        // (?=X) X, via zero-width positive lookahead

        // once in the middle of the string
        assertReplacement("abcdefghi", "de(?=f)", "XYZ", "abcXYZfghi");

        // once at the being, at the end, and three times in the middle of the
        // string
        assertReplacement("DEfcDEDEfDEfDE", "DE(?=f)", "XYZ",
                "XYZfcDEXYZfXYZfDE");
    }

    @Test
    public void testReplacement_endOfStream() throws Exception {

        String regex = "Y$";
        String replacement = "YY";
        String input = "YYY";
        String expectedOutput = "YYYY";

        // Java:
        assertEquals(expectedOutput, input.replaceAll(regex, replacement));

        // Streamflyer:
        assertReplacement(input, regex, replacement, expectedOutput);
    }

    @Test
    public void testReplacement_endOfStream_matchOnEmptyString_noEndlessLoop()
            throws Exception {

        String regex = "((XXX)|$)";
        String replacement = "YYY";
        String input = "XXX";
        String expectedOutput = "YYYYYY";

        // Java:
        assertEquals(expectedOutput, input.replaceAll(regex, replacement));

        // Streamflyer:
        assertReplacement(input, regex, replacement, expectedOutput);
    }

    @Test
    public void testReplacement_matchOnEmptyString_noEndlessLoop()
            throws Exception {

        String regex = "()";
        String replacement = "Y";
        String input = "XX";
        String expectedOutput = "YXYXY";

        // Java:
        assertEquals(expectedOutput, input.replaceAll(regex, replacement));

        // Streamflyer:
        assertReplacement(input, regex, replacement, expectedOutput);
    }

    /**
     * Defaults: Varies requestedMinimumLengthsOfLookBehind and
     * requestedCapacityOfCharacterBuffers.
     */
    protected void assertReplacement(String input, String regex,
            String replacement, String expectedOutput) throws Exception {

        for (int lookBehind = 0; lookBehind <= 2; lookBehind++) {

            for (int capacityCharBuf = 1; capacityCharBuf <= 18; capacityCharBuf++) {

                if (containsLookBehind(regex) && lookBehind == 0) {
                    // for a regex with look-behind we need look-behind > 0.
                    // Therefore, we skip this test as the parameter is not
                    // appropriate
                }
                else {
                    assertReplacement(input, regex, replacement, lookBehind,
                            capacityCharBuf, expectedOutput, 0);
                }
            }
        }
    }

    /**
     * @param regex regular expression
     * @return Returns true if the regular expression contains a look-behind
     *         construct (rule of thumb applied).
     */
    private boolean containsLookBehind(String regex) {
        return regex.contains("(?<=") //
                || regex.startsWith("^") //
                || regex.contains("\\b") //
                || regex.contains("\\B");
    }

    /**
     * Defaults: Combines tests for {@link ModifyingReader} and
     * {@link ModifyingWriter}.
     * 
     * @param flags TODO
     */
    protected void assertReplacement(String input, String regex,
            String replacement, int minimumLengthOfLookBehind,
            int requestedCapacityOfCharacterBuffer, String expectedOutput,
            int flags) throws Exception {

        // Java: (do we get the same result with Java's Regex package?)
        assertEquals(
                expectedOutput,
                Pattern.compile(regex, flags).matcher(input)
                        .replaceAll(replacement));

        // Streamflyer:
        assertReplacementByReader(input, regex, replacement,
                minimumLengthOfLookBehind, requestedCapacityOfCharacterBuffer,
                expectedOutput, flags);

        assertReplacementByWriter(input, regex, replacement,
                minimumLengthOfLookBehind, requestedCapacityOfCharacterBuffer,
                expectedOutput, flags);
    }

    @Ignore
    @Test
    public void testLookBehindAfterReplacement_ExampleFromWebpage1()
            throws Exception {

        String regex = "^a";
        int flags = 0;
        String replacement = "";
        String input = "aaabb";
        String expectedOutput = "aabb";
        int lookBehind = 3;
        int capacityCharBuf = 10;

        assertReplacement(input, regex, replacement, lookBehind,
                capacityCharBuf, expectedOutput, flags);
    }

    @Ignore
    @Test
    public void testLookBehindAfterReplacement_ExampleFromWebpage2()
            throws Exception {

        String regex = "(?<=foo)bar";
        int flags = 0;
        String replacement = "foo";
        String input = "foobarbar";
        String expectedOutput = "foofoobar";
        int lookBehind = 3;
        int capacityCharBuf = 10;

        assertReplacement(input, regex, replacement, lookBehind,
                capacityCharBuf, expectedOutput, flags);
    }


    //
    // Q and A - tests that result from questions asked in the discussion group
    //

    @Test
    public void testRemovalAtTheEndOfStream_notUsingMultiLineFlag()
            throws Exception {

        String endlessAbc = StringUtils.repeat("abc", 10000);
        String inputPrefix = endlessAbc + "abc\n" + endlessAbc + "abc\n"
                + endlessAbc;

        String regex = "abc$";
        int flags = 0;
        String replacement = "";
        String input = inputPrefix + "abc";
        String expectedOutput = inputPrefix;
        int lookBehind = 0;
        int capacityCharBuf = 3;

        assertReplacement(input, regex, replacement, lookBehind,
                capacityCharBuf, expectedOutput, flags);
    }

    @Test
    public void testRemovalAtTheEndOfLine_usingMultiLineFlag() throws Exception {

        String endlessAbc = StringUtils.repeat("abc", 10000);

        String regex = "abc$";
        int flags = Pattern.MULTILINE;
        String replacement = "";
        String input = endlessAbc + "abc\n" + endlessAbc + "abc\n" + endlessAbc
                + "abc";
        String expectedOutput = endlessAbc + "\n" + endlessAbc + "\n"
                + endlessAbc;
        int lookBehind = 0;
        int capacityCharBuf = 3;

        assertReplacement(input, regex, replacement, lookBehind,
                capacityCharBuf, expectedOutput, flags);
    }

    @Ignore
    @Test
    public void testRemovalAtTheStartOfStream_notUsingMultiLineFlag()
            throws Exception {

        String endlessAbc = StringUtils.repeat("abc", 10000);
        String inputSuffix = "\n" + endlessAbc + "\nabc" + endlessAbc + "\nabc"
                + endlessAbc;

        String regex = "^abc";
        int flags = 0;
        String replacement = "";
        String input = endlessAbc + "abc\n" + endlessAbc + "abc\n" + endlessAbc
                + "abc";
        String expectedOutput = inputSuffix;
        int lookBehind = 1;
        int capacityCharBuf = 3;

        assertReplacement(input, regex, replacement, lookBehind,
                capacityCharBuf, expectedOutput, flags);
    }

    @Ignore
    @Test
    public void testRemovalAtTheStartOfLine_usingMultiLineFlag()
            throws Exception {

        String endlessAbc = StringUtils.repeat("abc", 10000);

        String regex = "^abc";
        int flags = Pattern.MULTILINE;
        String replacement = "";
        String input = "\nabc" + endlessAbc + "\nabc" + endlessAbc + "\nabc"
                + endlessAbc;
        String expectedOutput = "\n" + endlessAbc + "\n" + endlessAbc + "\n"
                + endlessAbc;
        int lookBehind = 1;
        int capacityCharBuf = 3;

        assertReplacement(input, regex, replacement, lookBehind,
                capacityCharBuf, expectedOutput, flags);
    }

    //
    // ...
    //

    @Test
    public void testMatchEmptyStringAtTheEndOfStream_replaceWithNonEmptyString()
            throws Exception {

        String regex = "$";
        String replacement = "yyy";
        String input = "barbarbar";
        String expectedOutput = "barbarbaryyy";
        int flags = 0;
        int lookBehind = 0;
        int capacityCharBuf = 3;

        assertReplacement(input, regex, replacement, lookBehind,
                capacityCharBuf, expectedOutput, flags);
    }

    @Test
    public void testMatchEmptyStringAtTheEndOfStream_replaceWithEmptyString()
            throws Exception {

        String regex = "$";
        String replacement = "";
        String input = "barbarbar";
        String expectedOutput = "barbarbar";
        int flags = 0;
        int lookBehind = 0;
        int capacityCharBuf = 3;

        assertReplacement(input, regex, replacement, lookBehind,
                capacityCharBuf, expectedOutput, flags);
    }


}

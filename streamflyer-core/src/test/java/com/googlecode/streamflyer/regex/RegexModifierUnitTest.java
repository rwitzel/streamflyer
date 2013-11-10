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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.ComparisonFailure;
import org.junit.Test;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.internal.thirdparty.ZzzAssert;
import com.googlecode.streamflyer.util.StringUtils;

/**
 * Tests {@link RegexModifier} (white-box tests). These tests may fail if the
 * implementation of {@link RegexModifier} is changed.
 * 
 * @author rwoo
 * @since 23.06.2011
 */
public class RegexModifierUnitTest extends AbstractRegexModifierTest {

    public class RegexModifierWithCheckpoints extends RegexModifier {

        protected List<Object[]> __passedCheckpoints = new ArrayList<Object[]>();

        public RegexModifierWithCheckpoints(OnStreamMatcher matcher,
                MatchProcessor matchProcessor, int minimumLengthOfLookBehind,
                int newNumberOfChars) {
            super(matcher, matchProcessor, minimumLengthOfLookBehind,
                    newNumberOfChars);
        }

        /**
         * This method is called if a certain line of code is reached
         * ("checkpoint").
         * <p>
         * This method should be called only if the modifier is tested.
         * Otherwise you might experience a severe performance penalties.
         * 
         * @param checkpointDescription A list of objects describing the
         *        checkpoint. The objects should be given as name-value-pairs.
         * @return Returns true. This allows you to use this method as
         *         side-effect in Java assertions.
         */
        @Override
        protected boolean __checkpoint(Object... checkpointDescription) {
            for (int index = 0; index < checkpointDescription.length; index = index + 2) {
                if (checkpointDescription[index + 1] instanceof StringBuilder) {
                    checkpointDescription[index + 1] = ((StringBuilder) checkpointDescription[index + 1])
                            .toString();
                }
            }
            return __passedCheckpoints.add(checkpointDescription);
        }

        /**
         * @return Returns the {@link #__passedCheckpoints}.
         */
        public List<Object[]> __passedCheckpoints() {
            return __passedCheckpoints;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return super.toString() + "[sizeOf(__passedCheckpoints)="
                    + __passedCheckpoints.size() + "]";
        }

    }

    /**
     * @see com.googlecode.streamflyer.regex.AbstractRegexModifierTest#createModifier(java.lang.String,
     *      java.lang.String, int, int)
     */
    @Override
    protected RegexModifier createModifier(String regex, String replacement,
            int minimumLengthOfLookBehind,
            int requestedCapacityOfCharacterBuffer, int flags) {
        // create matcher
        OnStreamMatcher matcher = createMatcher(regex, flags);

        // create modifier
        RegexModifier modifier = new RegexModifierWithCheckpoints( //
                matcher, //
                new ReplacingProcessor(replacement), //
                minimumLengthOfLookBehind, //
                requestedCapacityOfCharacterBuffer);

        return modifier;
    }

    /**
     * @see com.googlecode.streamflyer.regex.AbstractRegexModifierTest#assertReplacementByReader(java.lang.String,
     *      java.lang.String, java.lang.String, int, int, java.lang.String, int)
     */
    @Override
    protected RegexModifierWithCheckpoints assertReplacementByReader(
            String input, String regex, String replacement,
            int minimumLengthOfLookBehind,
            int requestedCapacityOfCharacterBuffer, String expectedOutput,
            int flags) throws Exception {

        System.out.println(String.format("Replacing '%s' " + "with '%s' with "
                + "buffer size %s (look-behind %s) shall convert\n '%s' to "
                + "\n '%s'", regex, replacement,
                requestedCapacityOfCharacterBuffer, minimumLengthOfLookBehind,
                input, expectedOutput));

        return (RegexModifierWithCheckpoints) super.assertReplacementByReader(
                input, regex, replacement, minimumLengthOfLookBehind,
                requestedCapacityOfCharacterBuffer, expectedOutput, flags);
    }

    private void print(List<Object[]> passedCheckpoints) {
        // printXml(passedCheckpoints);
        // printYaml(passedCheckpoints);
        // printNice(passedCheckpoints);
    }

    @SuppressWarnings("unused")
    private void printXml(List<Object[]> passedCheckpoints) {

        // XML:
        int checkpointIndex = 0;
        for (Object[] passedCheckpoint : passedCheckpoints) {

            checkpointIndex++;
            System.out.println( //
                    "<checkpoint index=\"" + checkpointIndex + "\">");

            for (int index = 0; index < passedCheckpoint.length; index = index + 2) {
                String name = "" + passedCheckpoint[index];
                String value = "" + passedCheckpoint[index + 1];

                System.out.println("  <" + name + ">" + value + "</" + name
                        + ">");
            }

            System.out.println("</checkpoint>");
        }
    }

    @SuppressWarnings("unused")
    private void printYaml(List<Object[]> passedCheckpoints) {

        // YAML-like:
        int checkpointIndex = 0;
        for (Object[] passedCheckpoint : passedCheckpoints) {

            checkpointIndex++;
            System.out.println( //
                    "checkpoint: &" + checkpointIndex);

            for (int index = 0; index < passedCheckpoint.length; index = index + 2) {
                String name = "" + passedCheckpoint[index];
                String value = "" + passedCheckpoint[index + 1];

                System.out.println("  " + name + ": " + value);
            }
        }
    }

    @SuppressWarnings("unused")
    private void printNice(List<Object[]> passedCheckpoints) {

        System.out
                .println(String.format("%3s %16s | %3s %5s %s %s", "num",
                        "name/afterMod", "loB", "buLe", "buffer/skipped chars",
                        "eos?")); //

        //
        int checkpointIndex = 0;
        for (Object[] passedCheckpoint : passedCheckpoints) {

            checkpointIndex++;

            Map<String, Object> data = new HashMap<String, Object>();
            for (int index = 0; index < passedCheckpoint.length; index = index + 2) {
                String name = "" + passedCheckpoint[index];
                Object value = passedCheckpoint[index + 1];
                data.put(name, value);
            }

            // first line:
            // - checkpoint number,
            // - checkpoint name,
            // - look-behind width,
            // - total character size,
            // - buffer content,
            // - "EOS" if end of stream hit
            String name = (String) data.get("name");
            Integer minLen = (Integer) data.get("minLen");
            String characterBuffer = (String) data.get("characterBuffer");
            // System.out.println(characterBuffer + " " + minLen + " " + );
            Boolean endOfStreamHit = (Boolean) data.get("endOfStreamHit");
            System.out.println(String.format("%3d %16s | %3d %5d '%s' %s",
                    checkpointIndex, name, minLen, characterBuffer.length(),
                    characterBuffer.toString(), endOfStreamHit ? "EOS" : "")); //

            // second line
            if (name.equals("match_n_continue")) {
                // second line: "continue"
                // - empty,
                // - empty,
                // - empty,
                // - empty,
                // - "_" for the characters that are in the look-behind area,
                // ">" for the characters that are already processed, "?" for
                // the characters behind
                // - "EOS" if end of stream hit

                Integer minFrom = (Integer) data.get("minFrom");
                ZzzAssert.notNull(minFrom);

                String bufferDescription = StringUtils.repeat("_", minLen)
                        + StringUtils.repeat(">", minFrom - minLen)
                        + StringUtils.repeat("?", characterBuffer.length()
                                - minFrom - minLen);
                System.out.println(String.format("%3s %16s | %3s %5s '%s'", "",
                        " ", " ", " ", bufferDescription)); //
            }
            else {
                // second line: 'AfterModification' is returned
                // - empty,
                // - type of AfterModification,
                // - requested look-behind width,
                // - requested total character size,
                // - "_" for the characters that are in the look-behind area,
                // "X" for the characters that are to skip, "?" for
                // the characters behind
                // - "EOS" if end of stream hit

                AfterModification mod = (AfterModification) data
                        .get("afterModification");
                ZzzAssert.notNull(mod);

                String bufferDescription = StringUtils.repeat("_", minLen)
                        + StringUtils.repeat("X",
                                mod.getNumberOfCharactersToSkip())
                        + StringUtils.repeat("?", characterBuffer.length()
                                - mod.getNumberOfCharactersToSkip() - minLen);
                Integer newMinLen = mod.getNewMinimumLengthOfLookBehind();
                Integer newCharLen = mod.getNewNumberOfChars();
                String modificationType = mod.getMessageType();
                if (modificationType.equals("MODIFY AGAIN IMMEDIATELY")) {
                    modificationType = "MODIFY AGAIN"; // shorten the string
                }
                System.out.println(String.format("%3s %16s | %3d %5d '%s'", "",
                        modificationType, newMinLen, newCharLen,
                        bufferDescription)); //
            }
        }
    }

    @Test
    public void testReplacement_matchEmptyString_ReplaceWithNothingSoThatNothingToSkip_AtEndStream()
            throws Exception {
        String regex = "";
        String replacement = "";
        String input = "";
        String expectedOutput = "";

        // System.out.println("Java...");
        assertEquals(expectedOutput, input.replaceAll(regex, replacement));

        // System.out.println("Streamflyer...");
        List<Object[]> passedCheckpoints = assertReplacementByReader(input,
                regex, replacement, 0, 2, expectedOutput, 0)
                .__passedCheckpoints();
        print(passedCheckpoints);
    }

    @Test
    public void testBoundaryMatchers1_caret_TheBeginningOfALine_multiline_correctUsage_withLookBehind()
            throws Exception {

        // test: match "^bar" in "foobar" - with look-behind
        RegexModifier modifier = createModifier("^bar", "boom", 1, 100,
                Pattern.MULTILINE);
        StringBuilder charBuf = new StringBuilder("obar");
        modifier.modify(charBuf, 1, false);
        assertEquals("obar", charBuf.toString()); // assert no match

        // test: match "^bar" in "foo\nbar" - with look-behind
        modifier = createModifier("^bar", "boom", 1, 100, Pattern.MULTILINE);
        charBuf = new StringBuilder("\nbar");
        modifier.modify(charBuf, 1, false);
        assertEquals("\nboom", charBuf.toString()); // assert match

        // test: match "^bar" in "bar" - with look-behind
        modifier = createModifier("^bar", "boom", 1, 100, 0);
        charBuf = new StringBuilder("bar");
        modifier.modify(charBuf, 0, false);
        assertEquals("boom", charBuf.toString()); // assert match
    }

    @Test
    public void testBoundaryMatchers1_caret_TheBeginningOfALine_noMultiline_correctUsage_withLookBehind()
            throws Exception {

        // test: match "^bar" in "foobar" - with look-behind
        RegexModifier modifier = createModifier("^bar", "boom", 1, 100, 0);
        StringBuilder charBuf = new StringBuilder("obar");
        modifier.modify(charBuf, 1, false);
        assertEquals("obar", charBuf.toString()); // assert no match

        // test: match "^bar" in "foo\nbar" - with look-behind
        modifier = createModifier("^bar", "boom", 1, 100, 0);
        charBuf = new StringBuilder("\nbar");
        modifier.modify(charBuf, 1, false);
        assertEquals("\nbar", charBuf.toString()); // assert no match

        // test: match "^bar" in "bar" - with look-behind
        modifier = createModifier("^bar", "boom", 1, 100, 0);
        charBuf = new StringBuilder("bar");
        modifier.modify(charBuf, 0, false);
        assertEquals("boom", charBuf.toString()); // assert match
    }

    @Test
    public void testBoundaryMatchers2_dollar_TheEndOfALine_multiline()
            throws Exception {

        // test: match "foo$" in "foobar"
        RegexModifier modifier = createModifier("foo$", "hoo", 0, 100,
                Pattern.MULTILINE);
        StringBuilder charBuf = new StringBuilder("foo");
        AfterModification modification = modifier.modify(charBuf, 0, false);
        // System.out.println(modification);
        assertTrue(modification.isModifyAgainImmediately());
        assertEquals(0, modification.getNumberOfCharactersToSkip());
        assertEquals("foo", charBuf.toString()); // not changed

        charBuf = new StringBuilder("foob");
        modification = modifier.modify(charBuf, 0, false);
        // System.out.println(modification);
        assertFalse(modification.isModifyAgainImmediately());
        assertEquals(4, modification.getNumberOfCharactersToSkip());
        assertEquals("foob", charBuf.toString()); // not changed

        // test: match "foo$" in "foo\nbar"
        charBuf = new StringBuilder("foo\n");
        modification = modifier.modify(charBuf, 0, false);
        // System.out.println(modification);
        assertFalse(modification.isModifyAgainImmediately());
        assertEquals(4, modification.getNumberOfCharactersToSkip());
        assertEquals("hoo\n", charBuf.toString()); // changed
    }

    @Test
    public void testBoundaryMatchers3_b_AWordBoundary_AtTheBeginning_correctUsage_withLookBehind()
            throws Exception {

        // test: match "\bbar" in "foobar" - with look-behind
        RegexModifier modifier = createModifier("\\bbar", "boom", 1, 100, 0);
        StringBuilder charBuf = new StringBuilder("obar");
        modifier.modify(charBuf, 1, false);
        assertEquals("obar", charBuf.toString()); // assert no match

        // test: match "\bbar" in "foo bar" - with look-behind
        modifier = createModifier("\\bbar", "boom", 1, 100, 0);
        charBuf = new StringBuilder(" bar");
        modifier.modify(charBuf, 1, false);
        assertEquals(" boom", charBuf.toString()); // assert match

        // test: match "\bbar" in "bar" - with look-behind
        modifier = createModifier("\\bbar", "boom", 1, 100, 0);
        charBuf = new StringBuilder("bar");
        modifier.modify(charBuf, 0, false);
        assertEquals("boom", charBuf.toString()); // assert match
    }

    @Test
    public void testBoundaryMatchers3_b_AWordBoundary_AtTheEnd()
            throws Exception {

        // test: match "foo\b" in "foobar"
        RegexModifier modifier = createModifier("foo\\b", "hoo", 0, 100, 0);
        StringBuilder charBuf = new StringBuilder("foo");
        AfterModification modification = modifier.modify(charBuf, 0, false);
        // System.out.println(modification);
        assertTrue(modification.isModifyAgainImmediately());
        assertEquals(0, modification.getNumberOfCharactersToSkip());
        assertEquals("foo", charBuf.toString()); // not changed

        charBuf = new StringBuilder("foob");
        modification = modifier.modify(charBuf, 0, false);
        // System.out.println(modification);
        assertFalse(modification.isModifyAgainImmediately());
        assertEquals(4, modification.getNumberOfCharactersToSkip());
        assertEquals("foob", charBuf.toString()); // not changed

        // test: match "foo\b" in "foo bar"
        charBuf = new StringBuilder("foo ");
        modification = modifier.modify(charBuf, 0, false);
        // System.out.println(modification);
        assertFalse(modification.isModifyAgainImmediately());
        // four characters are skipped because the modifier continues matching
        // so that finally all characters are skipped
        assertEquals(4, modification.getNumberOfCharactersToSkip());
        assertEquals("hoo ", charBuf.toString()); // changed
    }

    @Test
    public void testBoundaryMatchers3_B_ANonWordBoundary_AtTheBeginning_correctUsage_withLookBehind()
            throws Exception {

        // test: match "\B,,," in "x,,," - with look-behind
        RegexModifier modifier = createModifier("\\B,,,", "boom", 1, 100, 0);
        StringBuilder charBuf = new StringBuilder("x,,,");
        modifier.modify(charBuf, 1, false);
        assertEquals("x,,,", charBuf.toString()); // assert no match

        // test: match "\B,,," in "-,,," - with look-behind
        modifier = createModifier("\\B,,,", "boom", 1, 100, 0);
        charBuf = new StringBuilder("-,,,");
        modifier.modify(charBuf, 1, false);
        assertEquals("-boom", charBuf.toString()); // assert match

        // test: match "\B,,," in ",,," - with look-behind
        modifier = createModifier("\\B,,,", "boom", 1, 100, 0);
        charBuf = new StringBuilder(",,,");
        modifier.modify(charBuf, 0, false);
        assertEquals("boom", charBuf.toString()); // assert match
    }

    @Test
    public void testBoundaryMatchers4_B_ANonWordBoundary_AtTheEnd()
            throws Exception {

        // test: match "foo\B" in ",,,x"
        RegexModifier modifier = createModifier(",,,\\B", "hoo", 0, 100, 0);
        StringBuilder charBuf = new StringBuilder(",,,");
        AfterModification modification = modifier.modify(charBuf, 0, false);
        // System.out.println(modification);
        assertTrue(modification.isModifyAgainImmediately());
        assertEquals(0, modification.getNumberOfCharactersToSkip());
        assertEquals(",,,", charBuf.toString()); // not changed

        charBuf = new StringBuilder(",,,x");
        modification = modifier.modify(charBuf, 0, false);
        // System.out.println(modification);
        assertFalse(modification.isModifyAgainImmediately());
        assertEquals(4, modification.getNumberOfCharactersToSkip());
        assertEquals(",,,x", charBuf.toString()); // not changed

        // test: match "foo\B" in ",,,-"
        charBuf = new StringBuilder(",,,-");
        modification = modifier.modify(charBuf, 0, false);
        // System.out.println(modification);
        assertFalse(modification.isModifyAgainImmediately());
        // four characters are skipped because the modifier continues matching
        // so that finally all characters are skipped
        assertEquals(4, modification.getNumberOfCharactersToSkip());
        assertEquals("hoo-", charBuf.toString()); // changed
    }

    @Test
    public void testBoundaryMatchers5_A_TheBeginningOfTheInput_correctUsage_withLookBehind()
            throws Exception {

        // test: match "\Abar" in "foobar" - with look-behind
        RegexModifier modifier = createModifier("\\Abar", "boom", 1, 100, 0);
        StringBuilder charBuf = new StringBuilder("obar");
        modifier.modify(charBuf, 1, false);
        assertEquals("obar", charBuf.toString()); // assert no match

        // test: match "\Abar" in "bar" - with look-behind
        modifier = createModifier("\\Abar", "boom", 1, 100, 0);
        charBuf = new StringBuilder("bar");
        modifier.modify(charBuf, 0, false);
        assertEquals("boom", charBuf.toString()); // assert match
    }

    /**
     * See <a href="http://www.regular-expressions.info/continue.html>Continuing
     * at The End of The Previous Match</a>
     * 
     * @throws Exception
     */
    @Test
    public void testBoundaryMatchers6_G_TheEndOfThePreviousMatch_MISSING_FEATURE()
            throws Exception {

        // it's nice that this works here but this is because it matches at
        // EVERY position here
        assertReplacementByReader("yzyz", "\\G(y|z)", "x", 1, 1024, "xxxx", 0);
        assertReplacementByReader("yzyzyzyzyzyz", "\\G(y|z)", "x", 1, 2,
                "xxxxxxxxxxxx", 0);

        // there are other cases that are not supported:
        try {
            assertReplacementByReader("azyzazyz", "(y)|(\\Gz)", "x", 1, 2,
                    "azxxazxx", 0);
            fail("ComparisonFailure expected");
        }
        catch (ComparisonFailure e) {
            assertEquals("expected:<a[zxxaz]xx> but was:<a[xxxax]xx>",
                    e.getMessage());
        }
    }

    /**
     * See <a href="http://www.regular-expressions.info/anchors.html">Strings
     * Ending with a Line Break</a>
     * 
     * @throws Exception
     */
    @Test
    public void testBoundaryMatchers7_Z_TheEndOfTheInput() throws Exception {

        // test: match "foo\Z" in "foobar"
        RegexModifier modifier = createModifier("foo\\Z", "hoo", 0, 100, 0);
        StringBuilder charBuf = new StringBuilder("foo");
        AfterModification modification = modifier.modify(charBuf, 0, false);
        // System.out.println(modification);
        assertTrue(modification.isModifyAgainImmediately());
        assertEquals(0, modification.getNumberOfCharactersToSkip());
        assertEquals("foo", charBuf.toString()); // not changed

        charBuf = new StringBuilder("foob");
        modification = modifier.modify(charBuf, 0, false);
        // System.out.println(modification);
        assertFalse(modification.isModifyAgainImmediately());
        assertEquals(4, modification.getNumberOfCharactersToSkip());
        assertEquals("foob", charBuf.toString()); // not changed

        // test: match "foo\Z" in "foo"
        charBuf = new StringBuilder("foo");
        modification = modifier.modify(charBuf, 0, true);
        // System.out.println(modification);
        assertFalse(modification.isModifyAgainImmediately());
        // four characters are skipped because the modifier continues matching
        // so that finally all characters are skipped
        assertEquals(3, modification.getNumberOfCharactersToSkip());
        assertEquals("hoo", charBuf.toString()); // changed

        // test: match "foo\Z" in "foo\n"
        charBuf = new StringBuilder("foo\n");
        modification = modifier.modify(charBuf, 0, true);
        // System.out.println(modification);
        assertFalse(modification.isModifyAgainImmediately());
        // four characters are skipped because the modifier continues matching
        // so that finally all characters are skipped
        assertEquals(4, modification.getNumberOfCharactersToSkip());
        assertEquals("hoo\n", charBuf.toString()); // changed
    }

    @Test
    public void testBoundaryMatchers8_Z_TheEndOfTheInput() throws Exception {

        // test: match "foo\z" in "foobar"
        RegexModifier modifier = createModifier("foo\\z", "hoo", 0, 100, 0);
        StringBuilder charBuf = new StringBuilder("foo");
        AfterModification modification = modifier.modify(charBuf, 0, false);
        // System.out.println(modification);
        assertTrue(modification.isModifyAgainImmediately());
        assertEquals(0, modification.getNumberOfCharactersToSkip());
        assertEquals("foo", charBuf.toString()); // not changed

        charBuf = new StringBuilder("foob");
        modification = modifier.modify(charBuf, 0, false);
        // System.out.println(modification);
        assertFalse(modification.isModifyAgainImmediately());
        assertEquals(4, modification.getNumberOfCharactersToSkip());
        assertEquals("foob", charBuf.toString()); // not changed

        // test: match "foo\z" in "foo"
        charBuf = new StringBuilder("foo");
        modification = modifier.modify(charBuf, 0, true);
        // System.out.println(modification);
        assertFalse(modification.isModifyAgainImmediately());
        // four characters are skipped because the modifier continues matching
        // so that finally all characters are skipped
        assertEquals(3, modification.getNumberOfCharactersToSkip());
        assertEquals("hoo", charBuf.toString()); // changed

        // test: match "foo\z" in "foo\n"
        charBuf = new StringBuilder("foo\n");
        modification = modifier.modify(charBuf, 0, true);
        // System.out.println(modification);
        assertFalse(modification.isModifyAgainImmediately());
        // four characters are skipped because the modifier continues matching
        // so that finally all characters are skipped
        assertEquals(4, modification.getNumberOfCharactersToSkip());
        assertEquals("foo\n", charBuf.toString()); // not changed
    }
}

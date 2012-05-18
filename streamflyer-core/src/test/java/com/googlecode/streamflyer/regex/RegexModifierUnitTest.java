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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.thirdparty.ZzzAssert;
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

    @Override
    protected RegexModifier createModifier(String regex, String replacement,
            int minimumLengthOfLookBehind,
            int requestedCapacityOfCharacterBuffer) {
        // create matcher
        OnStreamMatcher matcher = createMatcher(regex);

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
     *      java.lang.String, java.lang.String, int, int, java.lang.String)
     */
    @Override
    protected RegexModifierWithCheckpoints assertReplacementByReader(
            String input, String regex, String replacement,
            int minimumLengthOfLookBehind,
            int requestedCapacityOfCharacterBuffer, String expectedOutput)
            throws Exception {

        System.out.println(String.format("Replacing '%s' " + "with '%s' with "
                + "buffer size %s (look-behind %s) shall convert\n %s to "
                + "\n %s", regex, replacement,
                requestedCapacityOfCharacterBuffer, minimumLengthOfLookBehind,
                input, expectedOutput));

        return (RegexModifierWithCheckpoints) super.assertReplacementByReader(
                input, regex, replacement, minimumLengthOfLookBehind,
                requestedCapacityOfCharacterBuffer, expectedOutput);
    }

    public void playground() throws Exception {

        List<Object[]> passedCheckpoints = assertReplacementByReader("abcdedg",
                "de", "DE", 0, 2, "abcDEdg").__passedCheckpoints();
        print(passedCheckpoints);

    }

    private void print(List<Object[]> passedCheckpoints) {
        // printXml(passedCheckpoints);
        // printYaml(passedCheckpoints);
        printNice(passedCheckpoints);
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
                System.out.println(String.format("%3s %16s | %3d %5d '%s'", "",
                        modificationType, newMinLen, newCharLen,
                        bufferDescription)); //
            }
        }
    }

    public void ttttestInit_optimalCapacity() throws Exception {
        // expected: after the first modification, the capacity of the buffer
        // should be calculated like this:
        // capacity = lookBehind + numberOfCharsForMatching +
        // maxNumberOfCharactersInsertedByAllMatchings
        // Although this is not really important for performance, we should test
        // this because it illuminates the functionality of the
        // RegExModifier
        // TODO
    }

    public void ttttestModify_UC1X_matchAndReplaceOnceThenNoMatch()
            throws Exception {
        // expected: skip all chars
        // TODO
    }


    public void ttttestModify_UC21100_noMatch_matchStartOfRegex_endOfStreamHit()
            throws Exception {
        // expected: skip all chars in the buffer
        // TODO
    }

    public void ttttestModify_UC21211_noMatch_matchStartOfRegex_endOfStreamNotHit_matchStartsAfterFirstModifiableCharacter_expand()
            throws Exception {
        // expected: skip chars before the first matching character (as by
        // skipping the chars we get automatically more input)
        // TODO
    }

    public void ttttestModify_UC21212_noMatch_matchStartOfRegex_endOfStreamNotHit_matchStartsAfterFirstModifiableCharacter_doNotExpand()
            throws Exception {
        // expected: skip chars before the first matching character AND expand
        // the requested content of the buffer (as skipping the chars alone
        // would not provide new chars in the buffer)
        // TODO
    }

    public void ttttestModify_UC21200_noMatch_matchStartOfRegex_endOfStreamNotHit_matchStartsAtFirstModifiableCharacter()
            throws Exception {
        // expected: modify again with more characters in the buffer (this must
        // not involve expanding the capacity of the buffer!)
        // TODO
    }

    public void ttttestModify_UC22000_noMatch() throws Exception {
        // expected: skip all chars
        // TODO
    }


}

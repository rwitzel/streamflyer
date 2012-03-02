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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

/**
 * Tests {@link RegexModifier}.
 * 
 * @author rwoo
 * @since 23.06.2011
 */
public class RegexModifierUnitTest extends TestCase {


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


    protected OnStreamMatcher createMatcher(String regex) {
        Matcher matcher = Pattern.compile(regex).matcher("");
        matcher.useTransparentBounds(true);
        return new OnStreamStandardMatcher(matcher);
    }

    protected RegexModifier createModifier(String regex, String replacement,
            int minimumLengthOfLookBehind,
            int requestedCapacityOfCharacterBuffer) {
        // create matcher
        OnStreamMatcher matcher = createMatcher(regex);

        // create modifier
        RegexModifier modifier = new RegexModifier( //
                matcher, //
                new ReplacingProcessor(replacement), //
                minimumLengthOfLookBehind, //
                requestedCapacityOfCharacterBuffer);

        return modifier;
    }

}

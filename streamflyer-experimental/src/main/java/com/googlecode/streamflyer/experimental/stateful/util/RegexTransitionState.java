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

package com.googlecode.streamflyer.experimental.stateful.util;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.experimental.stateful.State;
import com.googlecode.streamflyer.experimental.stateful.StatefulAfterModification;
import com.googlecode.streamflyer.internal.thirdparty.ZzzValidate;
import com.googlecode.streamflyer.regex.MatchProcessor;
import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.regex.OnStreamMatcher;
import com.googlecode.streamflyer.regex.OnStreamStandardMatcher;
import com.googlecode.streamflyer.regex.RegexModifier;

/**
 * This state refers to a token (described by a regular expression). If that
 * token is found, then the state might be changed.
 * <p>
 * The default behavior of this class never changes the state nor does it modify
 * the character buffer. Override
 * <ul>
 * <li>{@link #findState(StringBuilder, int, MatchResult)},
 * <li> {@link #modifyBuffer(StringBuilder, int, MatchResult)},
 * <li>
 * {@link #processWithoutMatch(StringBuilder, int, boolean, AfterModification)}
 * </ul>
 * in order to modify the behavior.
 * 
 * @author rwoo
 * 
 * @since 14.09.2011
 */
public class RegexTransitionState implements State, MatchProcessor {

    private RegexModifier nextTokensRegexModifier;

    /**
     * The next state determined by evaluating the result of a match. Null if
     * there was no match.
     */
    private State nextState;

    /**
     * @param nextTokensRegexModifier
     */
    public RegexTransitionState(String nextTokensRegex) {
        super();

        ZzzValidate
                .notNull(nextTokensRegex, "nextTokensRegex must not be null");

        // create onStreamMatcher
        // TODO inject matcher instead of regex
        Matcher matcher = Pattern.compile(nextTokensRegex).matcher("");
        matcher.useTransparentBounds(true);
        OnStreamMatcher onStreamMatcher = new OnStreamStandardMatcher(matcher);

        // create nextTokensRegexModifier using 'this' as match processor
        // TODO remove magic numbers
        this.nextTokensRegexModifier = new RegexModifier(onStreamMatcher, this,
                12, 345);
    }

    /**
     * @see com.googlecode.streamflyer.experimental.stateful.State#modify(java.lang.StringBuilder,
     *      int, boolean)
     */
    @Override
    public StatefulAfterModification modify(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        AfterModification afterModification = nextTokensRegexModifier.modify(
                characterBuffer, firstModifiableCharacterInBuffer,
                endOfStreamHit);

        StatefulAfterModification result;
        if (nextState != null) {
            // there was a match -> use the next state
            result = new StatefulAfterModification(afterModification, nextState);
            nextState = null;
        }
        else {
            // there was no match -> use the existing state
            afterModification = processWithoutMatch(characterBuffer,
                    firstModifiableCharacterInBuffer, endOfStreamHit,
                    afterModification);
            result = new StatefulAfterModification(afterModification, this);
        }

        return result;
    }

    /**
     * @see com.googlecode.streamflyer.regex.MatchProcessor#process(java.lang.StringBuilder,
     *      int, java.util.regex.MatchResult)
     */
    @Override
    public MatchProcessorResult process(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {

        // (1) find the next state (this depends on the match result)
        // TODO use the content of the top-level groups
        nextState = findState(characterBuffer,
                firstModifiableCharacterInBuffer, matchResult);

        // (2) modify the buffer
        firstModifiableCharacterInBuffer = modifyBuffer(characterBuffer,
                firstModifiableCharacterInBuffer, matchResult);

        // do we have to change the state?
        if (!nextState.equals(this)) {
            // yes, state must be changed -> we must not continue to match with
            // this object
            return new MatchProcessorResult(firstModifiableCharacterInBuffer,
                    false);
        }
        else {
            // no, state must not be changed -> we continue to match with this
            // object
            return new MatchProcessorResult(firstModifiableCharacterInBuffer,
                    true);
        }
    }

    /**
     * Finds the next state (evaluating the given match result).
     * <p>
     * The default implementation returns 'this' state.
     */
    protected State findState(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {
        return this;
    }

    /**
     * Modifies the character buffer if the token was found. Returns the
     * position of the first character that is modifiable.
     * <p>
     * The default implementation does not modify the buffer. Returns the end of
     * the match as new first modifiable character in the buffer.
     */
    protected int modifyBuffer(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {
        return matchResult.end();
    }

    /**
     * Modifies the character buffer if no token is found.
     * <p>
     * The default implementation returns the given {@link AfterModification}.
     */
    protected AfterModification processWithoutMatch(
            StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit,
            AfterModification afterModification) {
        return afterModification;
    }


}

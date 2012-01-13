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

import java.util.regex.MatchResult;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.experimental.stateful.State;
import com.googlecode.streamflyer.experimental.stateful.util.RegexTransitionState;

/**
 * This state has exactly one other succeeding state. That state will be the
 * next state if the token this state refers to is found.
 * <p>
 * Additionally, you can define
 * <ul>
 * <li>whether the characters that appear before the found token shall be
 * deleted from the stream,
 * <li>whether the token itself shall be deleted from the stream.
 * 
 * @author rwoo
 * 
 * @since 14.09.2011
 */
public class RangeFilterState extends RegexTransitionState {

    /**
     * The next state if the token the state refers to is found.
     */
    private State nextState;

    /**
     * True if the characters before the token the state refers to shall be
     * deleted from the character buffer.
     */
    private boolean deleteCharactersBeforeToken;

    /**
     * True if the the token the state refers to shall be deleted from the
     * character buffer.
     */
    private boolean deleteToken;

    /**
     * A description for this state. This is useful for debugging.
     */
    private String description;

    public RangeFilterState(String nextTokenRegex,
            boolean deleteCharactersBeforeToken, boolean deleteToken) {
        super(nextTokenRegex);

        this.description = nextTokenRegex;
        this.deleteCharactersBeforeToken = deleteCharactersBeforeToken;
        this.deleteToken = deleteToken;
    }

    /**
     * @see com.googlecode.streamflyer.experimental.stateful.util.RegexTransitionState#findState(java.lang.StringBuilder,
     *      int, java.util.regex.MatchResult)
     */
    @Override
    protected State findState(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {
        return nextState;
    }

    /**
     * @see com.googlecode.streamflyer.experimental.stateful.util.RegexTransitionState#modifyBuffer(java.lang.StringBuilder,
     *      int, java.util.regex.MatchResult)
     */
    @Override
    protected int modifyBuffer(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {

        int shiftMatchResult = 0;

        // delete characters before token
        if (deleteCharactersBeforeToken) {
            characterBuffer.delete(firstModifiableCharacterInBuffer,
                    matchResult.start());
            shiftMatchResult += matchResult.start()
                    - firstModifiableCharacterInBuffer;
        }

        // delete token
        if (deleteToken) {
            characterBuffer.delete(matchResult.start() - shiftMatchResult,
                    matchResult.end() - shiftMatchResult);
            shiftMatchResult += matchResult.end() - matchResult.start();
        }

        return matchResult.end() - shiftMatchResult;
    }

    /**
     * @see com.googlecode.streamflyer.experimental.stateful.util.RegexTransitionState#processWithoutMatch(java.lang.StringBuilder,
     *      int, boolean, com.googlecode.streamflyer.core.AfterModification)
     */
    @Override
    protected AfterModification processWithoutMatch(
            StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit,
            AfterModification afterModification) {

        if (deleteCharactersBeforeToken
                && afterModification.getNumberOfCharactersToSkip() > 0) {

            // delete the characters to skip
            characterBuffer.delete(
                    firstModifiableCharacterInBuffer,
                    firstModifiableCharacterInBuffer
                            + afterModification.getNumberOfCharactersToSkip());

            // modify again immediately (??? TODO please review:
            // MinimumLengthOfLookBehind should be recalculated? and so on...
            return new AfterModification(0, true,
                    firstModifiableCharacterInBuffer,
                    afterModification.getNewNumberOfChars());
        }
        else {
            return afterModification;
        }
    }

    //
    // setter
    //

    /**
     * @param state The {@link #nextState} to set.
     */
    public void setNextState(State state) {
        this.nextState = state;
    }

    //
    // override Object.*
    //


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return description;
    }
}

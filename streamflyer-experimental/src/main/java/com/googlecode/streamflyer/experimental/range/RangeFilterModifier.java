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

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.experimental.stateful.State;
import com.googlecode.streamflyer.experimental.stateful.StatefulModifier;
import com.googlecode.streamflyer.internal.thirdparty.ZzzValidate;
import com.googlecode.streamflyer.regex.RegexModifier;

/**
 * This modifier deletes all data from the character stream that is not placed
 * between the given start and end tag. This is a bit similar to Perl's or
 * Ruby's range operator.
 * <p>
 * Additionally, you can choose whether the start or end tags shall be removed
 * from the input as well.
 * <p>
 * EXAMPLES: These examples show the impact of the parameters
 * <i>includeStart</i>, <i>includeEnd</i>, and <i>initiallyBeforeEnd</i>.
 * <code><pre>
                  start end   keep  starts original                    modified 
task              tag   tag   tags? open?  input                       input
---------------   ----- ----- ----- ------ --------------------------- --------------
extract text of 
comments          &lt;%    %>    no    no     "my &lt% comment %> text"     " comment "

extract comment 
tags              &lt;%    %>    yes   no     "my &lt% comment %> text"     "&lt% comment %>"

strip comments    %>    &lt;%    no    yes    "my &lt% comment %> text"     "my  text" 

clear comments    %>    &lt;%    yes   yes     "my &lt% comment %> text"    "my &lt%%> text" 
  </pre></code>
 * 
 * @author rwoo
 * 
 * @since 13.09.2011
 */
public class RangeFilterModifier implements Modifier {

    private StatefulModifier statefulModifier;

    /**
     * The match processor of both given modifiers are replaced with another
     * one. So you don't have to care about the match processor you have used on
     * 
     * @param startModifier
     *            Must not be <code>null</code>.
     * @param endModifier
     *            Must not be <code>null</code>.
     * @param includeStart
     *            True if the start tag shall not be deleted from the input.
     *            False if the start tag shall be deleted from the input.
     * @param includeEnd
     *            True if the end tag shall not be deleted from the input. False
     *            if the end tag shall be deleted from the input.
     * @param initiallyBeforeEnd
     *            True if the given input shall be parsed assuming the input is
     *            prefixed with an end tag. False if the given input shall be
     *            parsed assuming the input is prefixed with a start tag.
     */
    public RangeFilterModifier(RegexModifier startModifier,
            RegexModifier endModifier, boolean includeStart,
            boolean includeEnd, boolean initiallyBeforeEnd) {

        ZzzValidate.notNull(startModifier, "startModifier must not be null");
        ZzzValidate.notNull(endModifier, "endModifier must not be null");

        // *** create two state that will be used by the stateful modifier

        // create the state for the end tag
        RangeFilterState beforeEndState = new RangeFilterState(endModifier,
                false, !includeEnd);
        endModifier.setMatchProcessor(beforeEndState);

        // create the state for the start tag
        RangeFilterState beforeStartState = new RangeFilterState(startModifier,
                true, !includeStart);
        startModifier.setMatchProcessor(beforeStartState);

        // *** link the states
        beforeEndState.setNextState(beforeStartState);
        beforeStartState.setNextState(beforeEndState);

        // define the initial state
        State initialState;
        if (initiallyBeforeEnd) {
            initialState = beforeEndState;
        } else {
            initialState = beforeStartState;
        }

        this.statefulModifier = new StatefulModifier(initialState);
    }

    /**
     * @see com.googlecode.streamflyer.core.Modifier#modify(java.lang.StringBuilder,
     *      int, boolean)
     */
    @Override
    public AfterModification modify(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        // delegate
        return statefulModifier.modify(characterBuffer,
                firstModifiableCharacterInBuffer, endOfStreamHit);
    }
}

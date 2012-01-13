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

/**
 * This modifier deletes all data from the character stream that is not placed
 * between the given start and end tag. This is a bit similar to Perl's or
 * Ruby's range operator.
 * <p>
 * EXAMPLES: <code><pre>
  start tag   end tag     init. input                modified input
  ----------- ----------- ----- -------------------- --------------
  STA (excl.) END (excl.) END   "from STA to END."   " to " 
  STA (incl.) END (incl.) END   "from STA to END."   "STA to END"
  STA (incl.) END (incl.) STA   "from STA to END."   "from STA to END"
  S (excl.)   E (excl.)   E     "a S, E, S, E, ..."  ", , "  
  S (incl.)   E (incl.)   E     "a S, E, S, E, ..."  "S, ES, E" 
  
  </pre></code>
 * 
 * @author rwoo
 * 
 * @since 13.09.2011
 */
public class RangeFilterModifier implements Modifier {

    private StatefulModifier statefulModifier;


    public RangeFilterModifier(String regexStart, String regexEnd,
            boolean includeStart, boolean includeEnd, boolean initiallyBeforeEnd) {

        // create two state that will be used by the stateful modifier
        RangeFilterState beforeEndState = new RangeFilterState(regexEnd,
                false, !includeEnd);
        RangeFilterState beforeStartState = new RangeFilterState(
                regexStart, true, !includeStart);

        // link the states
        beforeEndState.setNextState(beforeStartState);
        beforeStartState.setNextState(beforeEndState);

        // define the initial state
        State initialState;
        if (initiallyBeforeEnd) {
            initialState = beforeEndState;
        }
        else {
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

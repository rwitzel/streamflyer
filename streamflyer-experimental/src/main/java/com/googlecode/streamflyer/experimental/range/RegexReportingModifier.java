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

import java.util.List;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.core.Documentation;
import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.experimental.stateful.StatefulModifier;
import com.googlecode.streamflyer.experimental.stateful.util.IdleModifierState;
import com.googlecode.streamflyer.util.ModificationFactory;

/**
 * TODO implement me, test, and refer to this class in {@link Documentation}.
 * 
 * @author rwoo
 * 
 * @since 14.09.2011
 */
public class RegexReportingModifier implements Modifier {

    private StatefulModifier statefulModifier;

    private RegexReportingState state;

    public RegexReportingModifier(String regex, boolean reportAllOccurences) {

        // create the state handler that reports matches
        this.state = new RegexReportingState(regex);

        // link the state to the next state
        if (reportAllOccurences) {
            // keep watching for matches (do not switch to another state)
            state.setNextState(state);
        }
        else {
            // report only one occurrence -> become idle after the first match
            state.setNextState(new IdleModifierState(new ModificationFactory(0,
                    500)));
        }

        this.statefulModifier = new StatefulModifier(state);
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

    /**
     * @return Returns the matches found so far.
     */
    public List<String> getMatches() {
        return state.getMatches();
    }
}

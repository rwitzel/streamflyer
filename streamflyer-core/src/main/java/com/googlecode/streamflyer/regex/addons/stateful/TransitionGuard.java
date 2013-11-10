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
package com.googlecode.streamflyer.regex.addons.stateful;

import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessorResult;

/**
 * A transition guard can be used to stop a {@link Transitions}. By default,
 * transitions are not stopped. Use a subclass when some transitions shall be
 * stopped.
 * <p>
 * Apart from that, the guard is a good place to execute logic that shall be
 * executed during the transition, before the new state is reached. The logic
 * must not be specific to this transition.
 * 
 * @author rwoo
 */
public class TransitionGuard {

    /**
     * @param nextState
     * @param characterBuffer
     * @param firstModifiableCharacterInBuffer
     * @param matchResult
     * @return Returns null if the transition shall be executed. Returns a
     *         {@link MatchProcessorResult} that shall be returned if the
     *         transition shall be stopped. If not overwritten, this method
     *         returns null.
     */
    public MatchProcessorResult stopTransition(State nextState,
            StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {
        // by default we don't stop the transition
        return null;
    }

}

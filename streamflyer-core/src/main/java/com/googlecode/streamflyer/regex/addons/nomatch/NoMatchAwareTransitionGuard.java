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
package com.googlecode.streamflyer.regex.addons.nomatch;

import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.regex.addons.stateful.State;
import com.googlecode.streamflyer.regex.addons.stateful.TransitionGuard;

/**
 * This {@link TransitionGuard} processes a {@link NoMatch noMatch} if the noMatch is not empty, and then delegates to
 * the original transition guard.
 * 
 * @author rwoo
 * 
 */
public class NoMatchAwareTransitionGuard extends TransitionGuard {

    private TransitionGuard delegate;

    private NoMatch noMatch;

    public NoMatchAwareTransitionGuard(TransitionGuard delegate, NoMatch noMatch) {
        super();
        this.delegate = delegate;
        this.noMatch = noMatch;
    }

    @Override
    public MatchProcessorResult stopTransition(State nextState, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {

        // are there characters that belong to a noMatch?
        if (noMatch.getStartPosition() < matchResult.start()) {
            // -> process the noMatch
            matchResult = noMatch.processNoMatch(characterBuffer, firstModifiableCharacterInBuffer, matchResult);
        }

        // delegate to the real guard
        return delegate.stopTransition(nextState, characterBuffer, firstModifiableCharacterInBuffer, matchResult);
    }

}

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
package com.github.rwitzel.streamflyer.regex.addons.stateful;

import java.util.regex.MatchResult;

import com.github.rwitzel.streamflyer.regex.MatchProcessor;
import com.github.rwitzel.streamflyer.regex.MatchProcessorResult;
import com.github.rwitzel.streamflyer.regex.OnStreamMatcher;
import com.github.rwitzel.streamflyer.regex.RegexModifier;
import com.github.rwitzel.streamflyer.regex.addons.util.DelegatingMatcher;

/**
 * This {@link MatchProcessor} changes the {@link OnStreamMatcher} that is used by a {@link RegexModifier} if another
 * {@link State} is reached.
 * 
 * @author rwoo
 * @since 1.1.0
 */
public class StateMachine implements MatchProcessor {

    /**
     * The most recently reached state.
     */
    private State currentState;

    /**
     * The transitions that starts from {@link #currentState}.
     */
    private Transitions transitions;

    /**
     * The matcher that is used by the {@link RegexModifier}. By changing the delegate we can exchange the regex the
     * modifier looks for. The matcher looks for the tokens that belong to the end states of {@link #transitions}.
     */
    private DelegatingMatcher delegatingMatcher;

    /**
     * 
     * @param initialState
     *            the initial state for the state machine
     * @param delegatingMatcher
     *            See {@link #delegatingMatcher}.
     */
    public StateMachine(State initialState, DelegatingMatcher delegatingMatcher) {
        super();
        this.delegatingMatcher = delegatingMatcher;
        changeStateTo(initialState);
    }

    /**
     * @return the currentState
     */
    public State getCurrentState() {
        return currentState;
    }

    /**
     * Changes the {@link #currentState} to the given state.
     * 
     * @param state
     */
    protected void changeStateTo(State state) {
        currentState = state;
        transitions = state.getTransitions();
        delegatingMatcher.setDelegate(state.getTransitions().getMatcher());
    }

    @Override
    public MatchProcessorResult process(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            MatchResult matchResult) {

        // modify stream
        MatchProcessorResult result = transitions.process(characterBuffer, firstModifiableCharacterInBuffer,
                matchResult);

        // change state if needed
        State newState = transitions.pollNewState();
        if (newState != currentState) {
            changeStateTo(newState);
        }

        return result;
    }

}

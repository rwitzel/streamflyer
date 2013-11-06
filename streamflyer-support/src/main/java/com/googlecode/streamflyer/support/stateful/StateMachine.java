package com.googlecode.streamflyer.support.stateful;

import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessor;
import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.regex.OnStreamMatcher;
import com.googlecode.streamflyer.regex.RegexModifier;
import com.googlecode.streamflyer.support.util.DelegatingMatcher;

/**
 * This {@link MatchProcessor} changes the {@link OnStreamMatcher} that is used by a {@link RegexModifier} if another
 * {@link State} is reached.
 * 
 * @author rwoo
 * 
 */
public class StateMachine implements MatchProcessor {

    /**
     * The most recently reached state.
     */
    private State currentState;

    /**
     * The transition that starts from {@link #currentState}.
     */
    private Transitions transition;

    private DelegatingMatcher delegatingMatcher;

    public StateMachine(State currentState, DelegatingMatcher delegatingMatcher) {
        super();
        this.delegatingMatcher = delegatingMatcher;
        transitionTo(currentState);
    }

    protected void transitionTo(State state) {
        delegatingMatcher.setDelegate(state.getMatcher());
        transition = state.getTransitions();
        currentState = state;
    }

    @Override
    public MatchProcessorResult process(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            MatchResult matchResult) {

        MatchProcessorResult result = transition
                .process(characterBuffer, firstModifiableCharacterInBuffer, matchResult);

        // look for another next state if the state has been changed
        State nextState = transition.pollNewState();
        if (nextState != currentState) {
            transitionTo(nextState);
        }

        return result;
    }

}

package com.googlecode.streamflyer.support.stateful;

import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessor;
import com.googlecode.streamflyer.regex.MatchProcessorResult;

public class StateMachine implements MatchProcessor {

    private State currentState;

    private Transition transition;

    private DelegatingMatcher delegatingMatcher;

    public StateMachine(State currentState, DelegatingMatcher delegatingMatcher) {
        super();
        this.delegatingMatcher = delegatingMatcher;
        transitionTo(currentState);
    }

    private void transitionTo(State state) {
        delegatingMatcher.setDelegate(state.getMatcher());
        transition = state.getTransition();
        currentState = state;
    }

    @Override
    public MatchProcessorResult process(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            MatchResult matchResult) {

        MatchProcessorResult result = transition.process(characterBuffer, firstModifiableCharacterInBuffer, matchResult);

        // look for another next state if the state has been changed
        State nextState = transition.getNextState();
        transition.setNextState(null);
        if (nextState != currentState) {
            transitionTo(nextState);
        }

        return result;
    }

}

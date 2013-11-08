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

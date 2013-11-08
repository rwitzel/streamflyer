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
     * The matcher that is used by the {@link RegexModifier}. By changing the delegate we can exchange the regex the
     * modifier looks for.
     */
    private DelegatingMatcher delegatingMatcher;

    /**
     * The transitions that starts from {@link #currentState}.
     */
    private Transitions transitions;

    public StateMachine(State currentState, DelegatingMatcher delegatingMatcher) {
        super();
        this.delegatingMatcher = delegatingMatcher;
        changeStateTo(currentState);
    }

    /**
     * Changes the {@link #currentState} to the given state.
     * 
     * @param state
     */
    protected void changeStateTo(State state) {
        currentState = state;
        delegatingMatcher.setDelegate(state.getTransitions().getMatcher());
        transitions = state.getTransitions();
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

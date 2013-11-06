package com.googlecode.streamflyer.support.stateful;

import java.util.List;
import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.support.tokens.Token;
import com.googlecode.streamflyer.support.tokens.TokenProcessor;

/**
 * Represents the transitions that lead away from a {@link State start state} to the end states.
 * <p>
 * This class is used in conjunction with {@link StateMachine}.
 * 
 * @author rwoo
 * 
 */
public class Transitions extends TokenProcessor {

    /**
     * If the transition is applied, this property contains the end state of the transition.
     * <p>
     * This property is reset to null by the state machine as soon as the state machine updates its current state.
     */
    private State newState;

    /**
     * This guard is asked to find out whether a transition shall be applied.
     */
    private TransitionGuard transitionGuard;

    /**
     * This list must contain a state for each token that can be matched by this token processor.
     */
    private List<State> endStates;

    public Transitions(List<Token> tokens, List<State> endStates, TransitionGuard transitionGuard) {
        super(tokens);
        this.endStates = endStates;
        this.transitionGuard = transitionGuard;
    }

    @Override
    protected MatchProcessorResult processToken(Token token, int groupOffset, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {

        // +++ process the token
        for (State endState : endStates) {

            if (endState.getStateName().equals(token.getName())) {

                MatchProcessorResult stop = transitionGuard.stopTransition(endState, groupOffset, characterBuffer,
                        firstModifiableCharacterInBuffer, matchResult);
                if (stop != null) {
                    return stop;
                }

                newState = endState;

                // delegate to the token-specific match processors
                return super.processToken(token, groupOffset, characterBuffer, firstModifiableCharacterInBuffer,
                        matchResult);
            }
        }

        throw new RuntimeException("never to happen if the class is used according to the class comment");
    }

    public State pollNewState() {
        try {
            return newState;
        } finally {
            newState = null;
        }
    }
}

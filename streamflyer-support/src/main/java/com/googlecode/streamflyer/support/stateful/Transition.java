package com.googlecode.streamflyer.support.stateful;

import java.util.List;
import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.support.tokens.MatchResultWithOffset;
import com.googlecode.streamflyer.support.tokens.Token;
import com.googlecode.streamflyer.support.tokens.TokenProcessor;

/**
 * Must be used in conjunction with {@link StateMachine}.
 * 
 * @author rwoo
 * 
 */
public class Transition extends TokenProcessor {

    /**
     * If the transition is applied, this property contains the end state of the transition.
     * <p>
     * This property is reset to null by the state machine as soon as the state machine updates its current state.
     */
    private State nextState;

    /**
     * This guard is asked to find out whether the transition shall be applied.
     */
    private TransitionGuard transitionGuard;

    /**
     * This list must contain a state for each token that can be matched by this token processor.
     */
    private List<State> states;

    public Transition(List<Token> tokens, List<State> states, TransitionGuard transitionGuard) {
        super(tokens);
        this.states = states;
        this.transitionGuard = transitionGuard;
    }

    @Override
    protected MatchProcessorResult processToken(Token token, int groupOffset, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {

        // +++ process the token
        for (State state : states) {

            if (state.getStateName().equals(token.getName())) {

                MatchProcessorResult stop = transitionGuard.stopTransition(state, groupOffset, characterBuffer,
                        firstModifiableCharacterInBuffer, matchResult);
                if (stop != null) {
                    return stop;
                }

                nextState = state;

                // replace and return
                return token.getMatchProcessor().process(characterBuffer, firstModifiableCharacterInBuffer,
                        new MatchResultWithOffset(matchResult, groupOffset));

            }
        }

        throw new RuntimeException("never to happen if the class is used according to the class comment");
    }

    public State getNextState() {
        return nextState;
    }

    public void setNextState(State nextState) {
        this.nextState = nextState;
    }
}

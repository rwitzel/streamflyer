package com.googlecode.streamflyer.support.stateful;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.regex.OnStreamMatcher;
import com.googlecode.streamflyer.support.tokens.Token;
import com.googlecode.streamflyer.support.tokens.TokenProcessor;
import com.googlecode.streamflyer.support.tokens.TokensMatcher;

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

    /**
     * The matcher that has to match before a transition to another state can happen.
     */
    private OnStreamMatcher matcher;

    /**
     * Constructs transitions to the given states.
     * 
     * @param endStates
     *            the states that can be reached by these transitions. Must not be null.
     * @param transitionGuard
     *            The guard that is called before a transition is executed. Must not be null.
     */
    public Transitions(List<State> endStates, TransitionGuard transitionGuard) {
        super(mapToTokens(endStates));
        this.endStates = endStates;
        this.transitionGuard = transitionGuard;
        this.matcher = new TokensMatcher(mapToTokens(endStates));
    }

    /**
     * (Functional style. Waiting for Java 8.)
     * 
     * @param states
     * @return Returns the tokens that belong to the states.
     */
    private static List<Token> mapToTokens(List<State> states) {
        List<Token> tokens = new ArrayList<Token>();
        for (State state : states) {
            tokens.add(state.getToken());
        }
        return tokens;
    }

    /**
     * (Functional style. Waiting for Java 8.)
     * 
     * @param states
     * @return Returns the state that belongs to the given token.
     */
    private static State findStateByToken(List<State> states, Token token) {
        for (State state : states) {
            if (state.getToken() == token) {
                return state;
            }
        }
        return null;
    }

    /**
     * 
     * @return Returns the matcher that must be used to find the next state.
     */
    public OnStreamMatcher getMatcher() {
        return matcher;
    }

    @Override
    protected MatchProcessorResult processToken(Token token, int groupOffset, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {

        // find the state that belongs to the token
        State endState = findStateByToken(endStates, token);

        if (endState == null) {
            throw new RuntimeException("never to happen if the class is used according to the class comment");
        }

        // stop the transition?
        MatchProcessorResult stop = transitionGuard.stopTransition(endState, groupOffset, characterBuffer,
                firstModifiableCharacterInBuffer, matchResult);
        if (stop != null) {
            return stop;
        }

        // allow the state machine to access the new state
        newState = endState;

        // process the token (by delegating to the token-specific match processors)
        return super.processToken(token, groupOffset, characterBuffer, firstModifiableCharacterInBuffer, matchResult);
    }

    /**
     * 
     * @return If the transition was successful (in terms of stream modification), this methods returns the state the
     *         state machine should switch to. If the method is called, the state is reset to <code>null</code>.
     */
    public State pollNewState() {
        try {
            return newState;
        } finally {
            newState = null;
        }
    }
}

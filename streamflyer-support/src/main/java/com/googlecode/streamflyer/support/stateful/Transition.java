package com.googlecode.streamflyer.support.stateful;

import java.util.List;
import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.regex.ReplacingProcessor;
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
     * The list of all found tokens. Each element contains the name of the token and the content of the token.
     * <p>
     * This property is added only for unit testing.
     */
    private List<String> foundTokens;

    /**
     * The next state.
     */
    private State nextState;

    /**
     * This list must contain a state for each token that can be matched by this token processor.
     */
    private List<State> states;

    public Transition(List<Token> tokens, List<String> foundTokens, List<State> states) {
        super(tokens);
        this.foundTokens = foundTokens;
        this.states = states;
    }

    @Override
    protected MatchProcessorResult processToken(Token token, int groupOffset, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {

        // +++ log the found token
        String foundToken = token.getName() + ":" + matchResult.group(groupOffset);
        foundTokens.add(foundToken);

        // +++ process the token
        for (State state : states) {

            if (state.getStateName().equals(token.getName())) {

                nextState = state;

                // replace and return
                ReplacingProcessor processor = new ReplacingProcessor(state.getReplacement());
                return processor.process(characterBuffer, firstModifiableCharacterInBuffer, new MatchResultWithOffset(
                        matchResult, groupOffset));

            }
        }

        throw new RuntimeException("never to happen if used according to the class comment");
    }

    public State getNextState() {
        return nextState;
    }

    public void setNextState(State nextState) {
        this.nextState = nextState;
    }
}

package com.googlecode.streamflyer.support.stateful;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.streamflyer.regex.OnStreamMatcher;
import com.googlecode.streamflyer.support.tokens.Token;
import com.googlecode.streamflyer.support.tokens.Tokens;

/**
 * A state of the match process. The state is reached if the corresponding {@link #getToken() token} is matched.
 * <p>
 * The subsequent states are given by {@link #defineNextStates(List, List)}.
 * 
 * @author rwoo
 * 
 */
public class State {

    /**
     * A unique name of the state.
     */
    private String stateName;

    /**
     * A regular expression
     */
    private String regex;

    /**
     * The replacement defines how the text that is matched via {@link #regex} shall be replaced.
     */
    private String replacement;

    private OnStreamMatcher matcher;

    private Transition transition;

    public State(String stateName, String regex, String replacement) {
        super();
        this.stateName = stateName;
        this.regex = regex;
        this.replacement = replacement;
    }

    public String getStateName() {
        return stateName;
    }

    public String getReplacement() {
        return replacement;
    }

    /**
     * 
     * @param nextStates
     *            the states that can be reached from the this state.
     * @param foundTokens
     *            only for testing
     */
    public void defineNextStates(List<State> nextStates, List<String> foundTokens) {

        // +++ create tokens for the given states
        List<Token> tokenList = new ArrayList<Token>();
        for (State nextState : nextStates) {
            tokenList.add(nextState.getToken());
        }

        // +++ create matcher
        Tokens tokens = new Tokens(tokenList);
        matcher = tokens.getMatcher();

        // +++ create a token processor that logs the found tokens
        // and replaces some text
        transition = new Transition(tokenList, foundTokens, nextStates);
    }

    /**
     * @return Returns the token that must be matched to reach this state.
     */
    public Token getToken() {
        return new Token(stateName, regex);
    }

    /**
     * 
     * @return Returns the matcher that must be used to find the next state.
     */
    public OnStreamMatcher getMatcher() {
        return matcher;
    }

    /**
     * 
     * @return Returns the token processor that must be used to switch to the next state.
     */
    public Transition getTransition() {
        return transition;
    }

    @Override
    public String toString() {
        return "State [stateName=" + stateName + ", regex=" + regex + ", replacement=" + replacement + "]";
    }

}

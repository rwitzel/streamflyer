package com.googlecode.streamflyer.regex.addons.stateful;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.regex.addons.stateful.State;
import com.googlecode.streamflyer.regex.addons.stateful.TransitionGuard;

/**
 * This {@link TransitionGuard} does not stop the transition but saves the matched token.
 * 
 * @author rwoo
 * 
 */
public class TokenCollector extends TransitionGuard {

    private List<String> foundTokens = new ArrayList<String>();

    public TokenCollector(List<String> foundTokens) {
        super();
        this.foundTokens = foundTokens;
    }

    @Override
    public MatchProcessorResult stopTransition(State nextState, StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            MatchResult matchResult) {

        // log the found token
        String foundToken = nextState.getToken().getName() + ":" + matchResult.group();
        foundTokens.add(foundToken);

        // don't stop the transition
        return null;
    }
}

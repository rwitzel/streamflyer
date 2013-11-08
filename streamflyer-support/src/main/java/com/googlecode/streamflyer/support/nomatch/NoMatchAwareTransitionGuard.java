package com.googlecode.streamflyer.support.nomatch;

import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.support.stateful.State;
import com.googlecode.streamflyer.support.stateful.TransitionGuard;

/**
 * This {@link TransitionGuard} processes a {@link NoMatch noMatch} if the noMatch is not empty, and then delegates to
 * the original transition guard.
 * 
 * @author rwoo
 * 
 */
public class NoMatchAwareTransitionGuard extends TransitionGuard {

    private TransitionGuard delegate;

    private NoMatch noMatch;

    public NoMatchAwareTransitionGuard(TransitionGuard delegate, NoMatch noMatch) {
        super();
        this.delegate = delegate;
        this.noMatch = noMatch;
    }

    @Override
    public MatchProcessorResult stopTransition(State nextState, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {

        // are there characters that belong to a noMatch?
        if (noMatch.getStartPosition() < matchResult.start()) {
            // -> process the noMatch
            matchResult = noMatch.processNoMatch(characterBuffer, firstModifiableCharacterInBuffer, matchResult);
        }

        // delegate to the real guard
        return delegate.stopTransition(nextState, characterBuffer, firstModifiableCharacterInBuffer, matchResult);
    }

}

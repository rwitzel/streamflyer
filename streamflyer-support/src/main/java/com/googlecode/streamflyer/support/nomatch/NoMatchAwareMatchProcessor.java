package com.googlecode.streamflyer.support.nomatch;

import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessor;
import com.googlecode.streamflyer.regex.MatchProcessorResult;

/**
 * This {@link MatchProcessor} delegates to another match processor and updates the start position of the next
 * {@link NoMatch noMatch}.
 * 
 * @author rwoo
 * 
 */
public class NoMatchAwareMatchProcessor implements MatchProcessor {

    protected MatchProcessor delegate;

    protected NoMatch noMatch;

    public NoMatchAwareMatchProcessor(MatchProcessor delegate, NoMatch noMatch) {
        super();
        this.delegate = delegate;
        this.noMatch = noMatch;
    }

    @Override
    public MatchProcessorResult process(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            MatchResult matchResult) {

        MatchProcessorResult result = delegate.process(characterBuffer, firstModifiableCharacterInBuffer, matchResult);

        // set the start position of the next noMatch
        noMatch.setStartPosition(result.getFirstModifiableCharacterInBuffer());

        return result;
    }

}

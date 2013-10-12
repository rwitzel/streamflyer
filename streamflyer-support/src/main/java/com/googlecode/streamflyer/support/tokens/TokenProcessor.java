package com.googlecode.streamflyer.support.tokens;

import java.util.List;
import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessor;
import com.googlecode.streamflyer.regex.MatchProcessorResult;

/**
 * This {@link MatchProcessor} processes a matched token (defined by a list of
 * {@link Token tokens}). Must be used together with the matcher produced by
 * {@link Tokens#getMatcher()}.
 * 
 * @author rwoo
 * 
 */
public abstract class TokenProcessor implements MatchProcessor {

    /**
     * The tokens to process.
     */
    private List<Token> tokens;

    public TokenProcessor(List<Token> tokens) {
        super();
        this.tokens = tokens;
    }

    @Override
    public MatchProcessorResult process(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            MatchResult matchResult) {

        // find out which token is matched
        int groupOffset = 1;
        for (Token token : tokens) {
            int groupsInToken = token.getCapturingGroupCount();

            String matchedGroup = matchResult.group(groupOffset);
            if (matchedGroup != null) {
                // this token is matched! -> process this token + return the
                // result
                return processToken(token, groupOffset, characterBuffer, firstModifiableCharacterInBuffer, matchResult);
            }

            groupOffset += groupsInToken + 1;
        }

        throw new RuntimeException("never to happen if used with " + Tokens.class);
    }

    /**
     * Processes the matched token. The token can be found in the match result
     * at the given group offset.
     * 
     */
    protected abstract MatchProcessorResult processToken(Token token, int groupOffset, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult);

}

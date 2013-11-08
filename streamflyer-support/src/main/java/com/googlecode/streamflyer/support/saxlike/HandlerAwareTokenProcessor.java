package com.googlecode.streamflyer.support.saxlike;

import java.util.List;
import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.support.tokens.Token;
import com.googlecode.streamflyer.support.tokens.TokenProcessor;

/**
 * Helper class for {@link HandlerAwareModifier}.
 * 
 * @author rwoo
 * 
 */
public class HandlerAwareTokenProcessor extends TokenProcessor {

    private Handler handler;

    public HandlerAwareTokenProcessor(List<Token> tokens, Handler handler) {
        super(tokens);
        this.handler = handler;
    }

    @Override
    protected MatchProcessorResult processToken(Token token, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {

        MatchProcessorResult result = handler.processToken(token, characterBuffer, firstModifiableCharacterInBuffer,
                matchResult);

        if (result == null) {
            // fall-back to the default processor
            result = super.processToken(token, characterBuffer, firstModifiableCharacterInBuffer, matchResult);
        }
        return result;
    }
}

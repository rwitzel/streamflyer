package com.googlecode.streamflyer.support.saxlike;

import java.util.List;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.regex.RegexModifier;
import com.googlecode.streamflyer.support.nomatch.NoMatchAwareMatchProcessor;
import com.googlecode.streamflyer.support.nomatch.NoMatchAwareModifier;
import com.googlecode.streamflyer.support.tokens.Token;
import com.googlecode.streamflyer.support.tokens.TokensMatcher;

/**
 * This {@link Modifier} allows you to use a kind of SAX handler to process tokens and the texts between the tokens:
 * {@link Handler}.
 * 
 * @author rwoo
 * 
 */
public class HandlerAwareModifier implements Modifier {

    private Modifier delegate;

    public HandlerAwareModifier() {
        super();
    }

    public HandlerAwareModifier(List<Token> tokens, Handler handler, int minimumLengthOfLookBehind, int newNumberOfChars) {
        initialize(tokens, handler, minimumLengthOfLookBehind, newNumberOfChars);
    }

    protected void initialize(List<Token> tokens, Handler handler, int minimumLengthOfLookBehind, int newNumberOfChars) {

        HandlerAwareNoMatch noMatch = new HandlerAwareNoMatch(handler);

        HandlerAwareTokenProcessor tokenProcessor = new HandlerAwareTokenProcessor(tokens, handler);

        NoMatchAwareMatchProcessor matchProcessor = new NoMatchAwareMatchProcessor(tokenProcessor, noMatch, true);

        TokensMatcher tokensMatcher = new TokensMatcher(tokens);

        Modifier modifier = new RegexModifier(tokensMatcher, matchProcessor, minimumLengthOfLookBehind,
                newNumberOfChars);

        delegate = new NoMatchAwareModifier(modifier, noMatch);
    }

    @Override
    public AfterModification modify(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit) {

        // delegate
        return delegate.modify(characterBuffer, firstModifiableCharacterInBuffer, endOfStreamHit);
    }
}

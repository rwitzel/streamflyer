/**
 * Copyright (C) 2011 rwoo@gmx.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.streamflyer.regex.addons.saxlike;

import java.util.List;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.regex.RegexModifier;
import com.googlecode.streamflyer.regex.addons.nomatch.NoMatchAwareMatchProcessor;
import com.googlecode.streamflyer.regex.addons.nomatch.NoMatchAwareModifier;
import com.googlecode.streamflyer.regex.addons.tokens.Token;
import com.googlecode.streamflyer.regex.addons.tokens.TokensMatcher;

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

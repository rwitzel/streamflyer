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
import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.regex.addons.tokens.Token;
import com.googlecode.streamflyer.regex.addons.tokens.TokenProcessor;

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

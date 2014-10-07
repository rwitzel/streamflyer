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
package com.googlecode.streamflyer.regex.addons.tokens;

import java.util.List;
import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.regex.addons.util.DoNothingProcessor;

/**
 * Stores the found tokens and replaces text in tokens with type <code>SectionTitle</code> and <code>ListItem</code>.
 * 
 * @author rwoo
 * 
 */
public class MyTokenProcessor extends TokenProcessor {

    /**
     * The list of all found tokens. Each element contains the name of the token and the content of the token.
     */
    private List<String> foundTokens;

    /**
     * True if the parser is between the tokens "SectionStart" and "SectionEnd".
     */
    private boolean insideSection = false;

    public MyTokenProcessor(List<Token> tokens, List<String> foundTokens) {
        super(tokens);
        this.foundTokens = foundTokens;
    }

    @Override
    protected MatchProcessorResult processToken(Token token, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {

        // +++ save the found token
        String foundToken = token.getName() + ":" + matchResult.group();
        foundTokens.add(foundToken);

        // +++ process the token
        if (token.getName().equals("SectionStart")) {

            insideSection = true;

        } else if (token.getName().equals("SectionEnd")) {

            insideSection = false;

        } else if (!insideSection) {

            // do nothing if not inside section!
            return new DoNothingProcessor().process(characterBuffer, firstModifiableCharacterInBuffer, matchResult);

        }

        // delegate to the default token-specific match processors.
        return super.processToken(token, characterBuffer, firstModifiableCharacterInBuffer, matchResult);
    }
}

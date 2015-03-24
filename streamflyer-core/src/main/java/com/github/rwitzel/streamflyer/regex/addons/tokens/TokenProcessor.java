/**
 * Copyright (C) 2011 rwitzel75@googlemail.com
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
package com.github.rwitzel.streamflyer.regex.addons.tokens;

import java.util.List;
import java.util.regex.MatchResult;

import com.github.rwitzel.streamflyer.internal.thirdparty.ZzzValidate;
import com.github.rwitzel.streamflyer.regex.MatchProcessor;
import com.github.rwitzel.streamflyer.regex.MatchProcessorResult;

/**
 * This {@link MatchProcessor} processes a matched token (defined by a list of {@link Token tokens}). Must be used
 * together with a {@link TokensMatcher}.
 * <p>
 * If you want to adjust the matching behaviour, please overwrite
 * {@link #processToken(Token, StringBuilder, int, MatchResult)} in subclasses.
 * 
 * @author rwoo
 * @since 1.1.0
 */
public class TokenProcessor implements MatchProcessor {

    /**
     * The tokens to process.
     */
    private List<Token> tokens;

    public TokenProcessor(List<Token> tokens) {
        super();

        ZzzValidate.isNotEmpty(tokens, "tokens");

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
                // this token is matched! -> process this token + return the result
                return processToken(token, characterBuffer, firstModifiableCharacterInBuffer,
                        new MatchResultWithOffset(matchResult, groupOffset));
            }

            groupOffset += groupsInToken + 1;
        }

        throw new RuntimeException("never to happen if used with " + TokensMatcher.class);
    }

    /**
     * Processes the matched token using {@link Token#getMatchProcessor()}. The token can be found in the match result
     * at the given group offset.
     */
    protected MatchProcessorResult processToken(Token token, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {

        return token.getMatchProcessor().process(characterBuffer, firstModifiableCharacterInBuffer, matchResult);
    }

}

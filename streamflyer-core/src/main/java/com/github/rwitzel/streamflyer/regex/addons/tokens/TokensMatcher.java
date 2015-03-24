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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.rwitzel.streamflyer.regex.OnStreamMatcher;
import com.github.rwitzel.streamflyer.regex.OnStreamStandardMatcher;
import com.github.rwitzel.streamflyer.regex.RegexModifier;
import com.github.rwitzel.streamflyer.regex.addons.util.DelegatingMatcher;

/**
 * An {@link OnStreamMatcher} that matches a {@link Token token} of a list of tokens.
 * 
 * @author rwoo
 * @since 1.1.0
 */
public class TokensMatcher extends DelegatingMatcher {

    /**
     * This constructor is only for unit tests.
     */
    public TokensMatcher() {
        super();
    }

    public TokensMatcher(List<Token> tokens) {
        super();
        setDelegate(createMatcher(createRegexThatMatchesAnyToken(tokens)));
    }

    /**
     * @return Returns a regular expression that matches all tokens.
     */
    String createRegexThatMatchesAnyToken(List<Token> tokens) {
        String regex = null;
        for (Token token : tokens) {
            if (regex == null) {
                regex = "(" + token.getRegex() + ")";
            } else {
                regex = regex + "|(" + token.getRegex() + ")";
            }
        }
        return regex;
    }

    /**
     * @return Returns the matcher that can be used with a {@link RegexModifier} to match an alternative of tokens
     */
    protected OnStreamMatcher createMatcher(String regexTokenAlternatives) {
        // use the default implementation
        Matcher matcher = Pattern.compile(regexTokenAlternatives, 0).matcher("");
        matcher.useTransparentBounds(true);
        matcher.useAnchoringBounds(false);
        return new OnStreamStandardMatcher(matcher);
    }

}

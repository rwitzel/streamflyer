package com.googlecode.streamflyer.support.tokens;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.streamflyer.regex.OnStreamMatcher;
import com.googlecode.streamflyer.regex.OnStreamStandardMatcher;
import com.googlecode.streamflyer.regex.RegexModifier;

/**
 * An {@link OnStreamMatcher} that matches a {@link Token token} of a list of tokens.
 * 
 * @author rwoo
 * 
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

package com.googlecode.streamflyer.support.tokens;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.streamflyer.regex.OnStreamMatcher;
import com.googlecode.streamflyer.regex.OnStreamStandardMatcher;
import com.googlecode.streamflyer.regex.RegexModifier;

/**
 * Provides an {@link OnStreamMatcher} for a list of {@link Token tokens}.
 * 
 * @author rwoo
 * 
 */
public class Tokens {

    private List<Token> tokens;

    public Tokens(List<Token> tokens) {
        super();
        this.tokens = tokens;
    }

    /**
     * @return Returns an unmodifiable list of tokens.
     */
    public List<Token> getTokens() {
        return Collections.unmodifiableList(tokens);
    }

    /**
     * @return Returns a regular expression that matches all tokens.
     */
    String createRegexThatMatchesAllTokens() {
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
     * @return Returns the matcher that can be used with a {@link RegexModifier}
     */
    public OnStreamMatcher getMatcher() {
        // use the default implementation
        Matcher matcher = Pattern.compile(createRegexThatMatchesAllTokens(), 0).matcher("");
        matcher.useTransparentBounds(true);
        matcher.useAnchoringBounds(false);
        return new OnStreamStandardMatcher(matcher);
    }

}

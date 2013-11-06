package com.googlecode.streamflyer.support.tokens;

import java.util.regex.Pattern;

import com.googlecode.streamflyer.internal.thirdparty.ZzzValidate;
import com.googlecode.streamflyer.regex.MatchProcessor;

/**
 * A token that shall be matched in a stream.
 * 
 * @author rwoo
 * 
 */
public class Token {

    /**
     * A unique ID for the token.
     */
    private String name;

    /**
     * This regular expression describes how a token can be matched.
     */
    private String regex;

    /**
     * The number of capturing groups that are contained in the {@link #regex}.
     */
    private int capturingGroupCount;

    /**
     * This object processes the match if the token is matched.
     */
    private MatchProcessor matchProcessor;

    public Token(String regex) {
        this("" + System.currentTimeMillis(), regex, new DoNothingProcessor());
    }

    public Token(String name, String regex) {
        this(name, regex, new DoNothingProcessor());
    }

    public Token(String name, String regex, MatchProcessor matchProcessor) {
        super();

        ZzzValidate.notNull(matchProcessor, "matchProcessor must not be null");

        this.name = name;
        this.regex = regex;
        this.matchProcessor = matchProcessor;
        this.capturingGroupCount = Pattern.compile(regex).matcher("").groupCount();
    }

    public String getName() {
        return name;
    }

    public String getRegex() {
        return regex;
    }

    public int getCapturingGroupCount() {
        return capturingGroupCount;
    }

    public MatchProcessor getMatchProcessor() {
        return matchProcessor;
    }

    @Override
    public String toString() {
        return "Token [name=" + name + ", regex=" + regex + ", capturingGroupCount=" + capturingGroupCount
                + ", matchProcessor=" + matchProcessor + "]";
    }

}

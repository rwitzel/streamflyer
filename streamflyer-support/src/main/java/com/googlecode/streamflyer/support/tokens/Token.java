package com.googlecode.streamflyer.support.tokens;

import java.util.regex.Pattern;

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

    public Token(String regex) {
        this("" + System.currentTimeMillis(), regex);
    }

    public Token(String name, String regex) {
        super();
        this.name = name;
        this.regex = regex;
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

    @Override
    public String toString() {
        return "Token [name=" + name + ", regex=" + regex + ", capturingGroupCount=" + capturingGroupCount + "]";
    }

}

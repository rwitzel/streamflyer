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

import java.util.regex.Pattern;

import com.github.rwitzel.streamflyer.internal.thirdparty.ZzzValidate;
import com.github.rwitzel.streamflyer.regex.MatchProcessor;
import com.github.rwitzel.streamflyer.regex.ReplacingProcessor;
import com.github.rwitzel.streamflyer.regex.addons.util.DoNothingProcessor;
import com.github.rwitzel.streamflyer.regex.addons.util.EmbeddedFlagUtil;

/**
 * A token that shall be matched via a regular expression in a stream. Each token is associated with a
 * {@link MatchProcessor} which defines how a {@link TokenProcessor} shall process the matched token.
 * 
 * @author rwoo
 * @since 1.1.0
 */
public class Token {

    /**
     * An ID for the token. The ID shall be unique among all tokens used with the {@link TokenProcessor}. Immutable.
     */
    private String name;

    /**
     * This regular expression describes how a token can be matched. Flags should be embedded via
     * {@link EmbeddedFlagUtil}. Immutable.
     */
    private String regex;

    /**
     * The number of capturing groups that are contained in the {@link #regex}. Immutable.
     * <p>
     * Calculated from {@link #regex} and saved here to improve the performance of a {@link TokenProcessor}.
     */
    private int capturingGroupCount;

    /**
     * This object processes the match if the token is matched.
     */
    private MatchProcessor matchProcessor;

    /**
     * This constructor should be used only in tests!
     * 
     * @param regex
     *            The regex describes how a token can be matched. Embed flags via {@link EmbeddedFlagUtil}.
     */
    public Token(String regex) {
        this("" + System.currentTimeMillis(), regex, new DoNothingProcessor());
    }

    /**
     * This token matches the given regex but the match processor does {@link DoNothingProcessor nothing}.
     * 
     * @param name
     *            See {@link #name}.
     * @param regex
     *            The regex describes how a token can be matched. Embed flags via {@link EmbeddedFlagUtil}.
     */
    public Token(String name, String regex) {
        this(name, regex, new DoNothingProcessor());
    }

    /**
     * This token matches the given regex and {@link ReplacingProcessor replaces} the match with the replacement.
     * 
     * @param name
     *            See {@link #name}.
     * @param regex
     *            The regex describes how a token can be matched. Embed flags via {@link EmbeddedFlagUtil}.
     * @param replacement
     */
    public Token(String name, String regex, String replacement) {
        this(name, regex, new ReplacingProcessor(replacement));
    }

    /**
     * This token matches the given regex and the match will be processed with the given {@link MatchProcessor}.
     * 
     * @param name
     *            See {@link #name}
     * @param regex
     *            The regex describes how a token can be matched. Embed flags via {@link EmbeddedFlagUtil}.
     * @param matchProcessor
     */
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

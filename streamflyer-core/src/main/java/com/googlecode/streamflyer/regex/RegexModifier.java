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

package com.googlecode.streamflyer.regex;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.util.ModificationFactory;
import com.googlecode.streamflyer.util.ModifyingReaderFactory;
import com.googlecode.streamflyer.util.ModifyingWriterFactory;

/**
 * Finds text that matches a given regular expression. The match is processed by
 * the given {@link MatchProcessor}.
 * <p>
 * TODO Memory consumption in detail including greedy operators
 * <p>
 * TODO explain lethal regex like "(look-behind expression)()".
 * 
 * @author rwoo
 * @since 18.06.2011
 */
public class RegexModifier implements Modifier {

    //
    // injected
    //

    protected ModificationFactory factory;

    protected MatchProcessor matchProcessor;

    /**
     * The compiled representation of a regular expression. If the regular
     * expression matches, then a modification shall be carried out.
     */
    protected OnStreamMatcher matcher;

    protected int newNumberOfChars = -1;


    //
    // state
    //

    //
    // constructors
    //

    /**
     * Like {@link RegexModifier#RegexModifier(String, int, String, int, int)}
     * but uses defaults for <code>minimumLengthOfLookBehind</code> (zero) and
     * <code>newNumberOfChars</code> (2048).
     */
    public RegexModifier(String regex, int flags, String replacement) {
        this(regex, flags, replacement, 0, 2048);
    }

    /**
     * Creates a modifier that matches a regular expression on character streams
     * and replaces the matches.
     * <p>
     * This modifier uses {@link OnStreamStandardMatcher} which is not the
     * fastest implementation of {@link OnStreamMatcher}. If you want to use a
     * faster matcher, use
     * {@link #RegexModifier(OnStreamMatcher, MatchProcessor, int, int)}
     * instead.
     * <p>
     * A more convenient use of a {@link RegexModifier} is provided by the
     * {@link ModifyingReaderFactory} respectively
     * {@link ModifyingWriterFactory}.
     * 
     * @param regex the regular expression that describe the text that shall be
     *        replaced. See {@link Pattern#compile(String, int)}.
     * @param flags the flags that are to use when the regex is applied on the
     *        character stream. See {@link Pattern#compile(String, int)}.
     * @param replacement the replacement for the text that is matched via
     *        <code>regex</code>. See
     *        {@link Matcher#appendReplacement(StringBuffer, String)}.
     * @param minimumLengthOfLookBehind See
     *        {@link RegexModifier#RegexModifier(OnStreamMatcher, MatchProcessor, int, int)}
     *        .
     * @param newNumberOfChars See
     *        {@link RegexModifier#RegexModifier(OnStreamMatcher, MatchProcessor, int, int)}
     *        .
     */
    public RegexModifier(String regex, int flags, String replacement,
            int minimumLengthOfLookBehind, int newNumberOfChars) {

        Matcher jdkMatcher = Pattern.compile(regex, flags).matcher("");
        jdkMatcher.useTransparentBounds(true);
        init(new OnStreamStandardMatcher(jdkMatcher), new ReplacingProcessor(
                replacement), minimumLengthOfLookBehind, newNumberOfChars);
    }

    /**
     * Creates a modifier that matches a regular expression on character streams
     * and does 'something' if matches are found.
     * <p>
     * 
     * @param matcher Matches a regular expression on a {@link CharSequence}.
     * @param matchProcessor Defines what to do if the regular expression
     *        matches some text in the stream.
     * @param minimumLengthOfLookBehind See
     *        {@link AfterModification#getNewMinimumLengthOfLookBehind()}.
     * @param newNumberOfChars See
     *        {@link AfterModification#getNewNumberOfChars()}. This should not
     *        be smaller than the length of the characters sequence the
     *        {@link #pattern} needs to match properly. In case you want to
     *        match more than once, the value should be higher.
     */
    public RegexModifier(OnStreamMatcher matcher,
            MatchProcessor matchProcessor, int minimumLengthOfLookBehind,
            int newNumberOfChars) {

        init(matcher, matchProcessor, minimumLengthOfLookBehind,
                newNumberOfChars);
    }

    @SuppressWarnings("hiding")
    protected void init(OnStreamMatcher matcher, MatchProcessor matchProcessor,
            int minimumLengthOfLookBehind, int newNumberOfChars) {

        this.factory = new ModificationFactory(minimumLengthOfLookBehind,
                newNumberOfChars);
        this.matchProcessor = matchProcessor;
        this.matcher = matcher;
        this.newNumberOfChars = newNumberOfChars;
    }


    //
    // interface Modifier
    //

    /**
     * @see com.googlecode.streamflyer.core.Modifier#modify(java.lang.StringBuilder,
     *      int, boolean)
     */
    @Override
    public AfterModification modify(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        // the first position we will match from.
        Integer minFrom = null;

        while (true) {

            // determine the range [minFrom, maxFrom] that will contain the
            // first character of the matching string

            if (minFrom == null) {
                minFrom = firstModifiableCharacterInBuffer;
            }

            int maxFrom = firstModifiableCharacterInBuffer + newNumberOfChars;
            if (maxFrom > characterBuffer.length()) {
                // we have to restrict maxFrom in order to prevent that the
                // requested number of characters increases more and more

                // this is NOT set to characterBuffer.length() -1 by intention
                // as a regular expression might match on the zero length string
                // (but with positive look-behind)!
                maxFrom = characterBuffer.length();
            }

            // find first match
            // (as the match processor might have modified the buffer, we reset
            // the matcher inside the loop instead of outside of the loop)
            matcher.reset(characterBuffer);
            boolean matchFound = matcher.findUnlessHitEnd(minFrom, maxFrom);

            if (matchFound) {
                // we found a match

                // could change this positive match into a negative one
                // (matcher.requireEnd()) or into a longer one (greedy
                // operator)?
                if (matcher.hitEnd() && !endOfStreamHit) {
                    // (UC11000) yes, it could -> we need more input

                    int numberOfCharactersToSkip = matcher.lastFrom()
                            - firstModifiableCharacterInBuffer;

                    // read more input (skip some characters if possible)
                    return factory.fetchMoreInput(numberOfCharactersToSkip,
                            characterBuffer, firstModifiableCharacterInBuffer,
                            endOfStreamHit);
                }
                else {
                    // no -> thus we can use this match

                    // process the match
                    MatchResult matchResult = matcher; // TODO .toMatchResult()?
                    // TODO please review: should I pass
                    // firstModifiableCharacterInBuffer instead of minFrom?
                    MatchProcessorResult matchProcessorResult = matchProcessor
                            .process(characterBuffer, minFrom, matchResult);
                    minFrom = matchProcessorResult
                            .getFirstModifiableCharacterInBuffer();

                    // match again? (even for minFrom == maxFrom we try a match)
                    if (minFrom <= maxFrom
                            && matchProcessorResult.isContinueMatching()) {
                        // (UC12100)

                        // We try the next match on the modified input, i.e.
                        // not match only once -> next loop
                        continue;
                    }
                    else {

                        // (UC12200)

                        // TODO please doc, this is IMPORTANT!

                        int numberOfCharactersToSkip = minFrom
                                - firstModifiableCharacterInBuffer;

                        if (numberOfCharactersToSkip == 0) {
                            // TODO passing false for endOfStreamHit is ugly!!!
                            // we should offer a new method in
                            // ModificationFactory, something like
                            // continueAfterModification(...) that chooses the
                            // appropriate action. the following code is always
                            // a MODIFY_AGAIN_IMMEDIATELY
                            return factory.fetchMoreInput(
                                    numberOfCharactersToSkip, characterBuffer,
                                    firstModifiableCharacterInBuffer, false);
                        }
                        else {
                            return factory.skipOrStop(numberOfCharactersToSkip,
                                    characterBuffer,
                                    firstModifiableCharacterInBuffer,
                                    endOfStreamHit);
                        }
                    }

                }
            }
            else {
                // we haven't found a match

                // did we match at least one character in range [from, maxFrom]?
                // TODO matcher.lastFrom() < maxFrom or matcher.lastFrom() <=
                // maxFrom? add a test!
                if (matcher.lastFrom() <= maxFrom) {
                    // yes, we are matching something (at least one character)

                    // we did no match and we hit the end of the input from a
                    // position that is not the at the end of the input.
                    // Therefore, we must assume that more input could find a
                    // match

                    // can we get more input?
                    if (endOfStreamHit) {
                        // (UC21100) no, in the entire stream we will not found
                        // more matches that start in range [from,
                        // maxFrom] -> skip the characters from range [from,
                        // maxFrom]

                        int numberOfCharactersToSkip = maxFrom
                                - firstModifiableCharacterInBuffer;
                        return factory.skipOrStop(numberOfCharactersToSkip,
                                characterBuffer,
                                firstModifiableCharacterInBuffer,
                                endOfStreamHit);

                    }
                    else {
                        // yes > we should fetch more input (because end
                        // of stream is not hit yet)

                        int numberOfCharactersToSkip = matcher.lastFrom()
                                - firstModifiableCharacterInBuffer;
                        return factory.fetchMoreInput(numberOfCharactersToSkip,
                                characterBuffer,
                                firstModifiableCharacterInBuffer,
                                endOfStreamHit);

                    }

                }
                else { // matcher.lastFrom() == maxFrom + 1

                    // (UC22000) no, we are matching not a single character

                    // -> skip the characters from range [from, maxFrom]
                    int numberOfCharactersToSkip = maxFrom
                            - firstModifiableCharacterInBuffer;
                    return factory.skipOrStop(numberOfCharactersToSkip,
                            characterBuffer, firstModifiableCharacterInBuffer,
                            endOfStreamHit);
                }
            }
        }

    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RegexModifier [\nfactory=");
        builder.append(factory);
        builder.append(", \nreplacement=");
        builder.append(matchProcessor);
        builder.append(", \nmatcher=");
        builder.append(matcher);
        builder.append(", \nnewNumberOfChars=");
        builder.append(newNumberOfChars);
        builder.append("]");
        return builder.toString();
    }


}

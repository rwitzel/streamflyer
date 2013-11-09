package com.googlecode.streamflyer.regex.addons.saxlike;

import java.util.regex.MatchResult;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.regex.RegexModifier;
import com.googlecode.streamflyer.regex.addons.nomatch.NoMatch;
import com.googlecode.streamflyer.regex.addons.tokens.Token;
import com.googlecode.streamflyer.regex.addons.tokens.TokenProcessor;

/**
 * By implementing a handler like this, you get informed about matched tokens
 * and the text between matched tokens (so-called {@link NoMatch noMatches}),
 * i.e about the entire content of the stream. The noMatches may come in chunks
 * if the character buffer window of the use {@link RegexModifier} is smaller
 * than the entire text between two tokens.
 * <p>
 * If you are familiar with SAX, you can compare this handler with a SAX
 * handler.
 * <p>
 * EXAMPLE: {@link RangeFilterHandler}, TODO more examples
 * 
 * @author rwoo
 */
public interface Handler {

    //
    // process matched token
    //

    /**
     * The method is allowed to modify the stream. Very similar to the method
     * with the same name in {@link TokenProcessor}.
     * 
     * @return Returns null if the {@link Token#getMatchProcessor() default
     *         processor of the token} shall be applied.
     */
    public MatchProcessorResult processToken(Token token,
            StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult);

    //
    // process noMatch
    //

    /**
     * {@link NoMatch#processNoMatch(StringBuilder, int, MatchResult)} describes
     * how the end of the noMatch can be determined from the method parameters.
     * 
     * @return Returns null if the stream is not modified. Otherwise a match
     *         result must be returned that is still valid for the modified
     *         stream. If you insert or remove characters before the match
     *         {@link MatchResultShifted} helps you.
     */
    public MatchResult processNoMatch(int startPosition,
            StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult);

    /**
     * {@link NoMatch#processNoMatch(StringBuilder, int, boolean, AfterModification, Modifier)}
     * describes how the end of the noMatch can be determined from the method
     * parameters.
     * 
     * @return Returns null if the stream is not modified.
     */
    public AfterModification processNoMatch(int startPosition,
            StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit,
            AfterModification mod, Modifier modifier);

}

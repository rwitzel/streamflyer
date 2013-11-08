package com.googlecode.streamflyer.support.saxlike;

import java.util.regex.MatchResult;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.support.nomatch.NoMatch;
import com.googlecode.streamflyer.support.tokens.Token;
import com.googlecode.streamflyer.support.tokens.TokenProcessor;

/**
 * By implementing a handler like this, you get informed about matched tokens and the text between matched tokens, i.e
 * about the entire text of the stream.
 * <p>
 * If you are familar with SAX, you can compare this with a SAX handler.
 * <p>
 * EXAMPLE: TODO
 * 
 * @author rwoo
 * 
 */
public interface Handler {

    //
    // process matched token
    //

    /**
     * The method is allowed to modify the stream. Very similar to the method with the same name in
     * {@link TokenProcessor}.
     * 
     * @return Returns null if the {@link Token#getMatchProcessor() default processor of the token} shall be applied.
     */
    public MatchProcessorResult processToken(Token token, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult);

    //
    // process noMatch
    //

    /**
     * {@link NoMatch#processNoMatch(StringBuilder, int, MatchResult)} describes how the end of the noMatch can be
     * determined from the method parameters.
     * 
     * @return Returns null if the stream is not modified.
     */
    public MatchResult processNoMatch(int startPosition, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult);

    /**
     * {@link NoMatch#processNoMatch(StringBuilder, int, boolean, AfterModification, Modifier)} describes how the end of
     * the noMatch can be determined from the method parameters.
     * 
     * @return Returns null if the stream is not modified.
     */
    public AfterModification processNoMatch(int startPosition, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit, AfterModification mod, Modifier modifier);

}

package com.googlecode.streamflyer.support.nomatch;

import java.util.regex.MatchResult;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.regex.RegexModifier;

/**
 * This class can be used with {@link RegexModifier}: A noMatch is a character sequence that is not matched by the
 * regular expressions the modifier uses.
 * <p>
 * EXAMPLE: If the modifier looks for 'abc' and 'def' and the stream contains 'xx_abc_yy_def_zz', then all noMatches
 * combined are the sequence 'xx__yy__zz'.
 * <p>
 * Attention! The noMatches are not normalized. In the above mentioned example, the first noMatch could be the first 'x'
 * and the second noMatch could be the second 'x'. If you are familiar with XML: This is similar to text nodes that are
 * created by SAX parsers.
 * 
 * @author rwoo
 * 
 */
public class NoMatch {

    /**
     * The position of the first character that might belong to a noMatch.
     */
    private int startPosition;

    /**
     * @return the startPosition
     */
    public int getStartPosition() {
        return startPosition;
    }

    /**
     * @param startPosition
     *            the startPosition to set
     */
    public void setStartPosition(int position) {
        this.startPosition = position;
    }

    /**
     * Called by {@link NoMatchAwareTransitionGuard} before a match (when the noMatch is not empty). By default, we
     * return the original match result but you are allowed to modify the stream. In the latter case you have to adjust
     * the returned match result.
     * <p>
     * The end of the noMatch is given by {@link MatchResult#start()}.
     */
    public MatchResult processNoMatch(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            MatchResult matchResult) {
        return matchResult;
    }

    /**
     * Called by {@link NoMatchAwareModifier} when new characters are fetched from the stream (when the noMatch is not
     * empty). By default, we return the original 'after modification' but you are allowed to modify the stream. In the
     * latter case you have to adjust the returned match result.
     * <p>
     * The end of the noMatch is given by <code>firstModifiableCharacterInBuffer</code> +
     * {@link AfterModification#getNumberOfCharactersToSkip()}.
     */
    public AfterModification processNoMatch(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit, AfterModification mod, Modifier modifier) {
        return mod;
    }

}

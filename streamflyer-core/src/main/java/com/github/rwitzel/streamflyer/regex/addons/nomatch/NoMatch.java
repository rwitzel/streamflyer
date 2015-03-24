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
package com.github.rwitzel.streamflyer.regex.addons.nomatch;

import java.util.regex.MatchResult;

import com.github.rwitzel.streamflyer.core.AfterModification;
import com.github.rwitzel.streamflyer.core.Modifier;
import com.github.rwitzel.streamflyer.regex.RegexModifier;

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
 * <p>
 * TODO more examples (the unit tests already provide them)
 * 
 * 
 * @author rwoo
 * @since 1.1.0
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
     * @param position
     *            the startPosition to set
     */
    public void setStartPosition(int position) {
        this.startPosition = position;
    }

    /**
     * Called by {@link NoMatchAwareTransitionGuard} or {@link NoMatchAwareMatchProcessor} before a match (when the
     * noMatch is not empty). By default, we return the original match result but you are allowed to modify the stream.
     * In the latter case you have to adjust the returned match result.
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

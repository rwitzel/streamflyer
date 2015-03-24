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
package com.github.rwitzel.streamflyer.regex.addons.saxlike;

import java.util.regex.MatchResult;

import com.github.rwitzel.streamflyer.core.AfterModification;
import com.github.rwitzel.streamflyer.core.Modifier;
import com.github.rwitzel.streamflyer.regex.MatchProcessorResult;
import com.github.rwitzel.streamflyer.regex.RegexModifier;
import com.github.rwitzel.streamflyer.regex.addons.nomatch.NoMatch;
import com.github.rwitzel.streamflyer.regex.addons.tokens.Token;
import com.github.rwitzel.streamflyer.regex.addons.tokens.TokenProcessor;

/**
 * By implementing a handler like this, you get informed about matched tokens and the text between matched tokens
 * (so-called {@link NoMatch noMatches}), i.e about the entire content of the stream. The noMatches may come in chunks
 * if the character buffer window of the used {@link RegexModifier} is smaller than the entire text between two tokens.
 * <p>
 * If you are familiar with SAX, you can compare this handler with a SAX handler.
 * <p>
 * EXAMPLE: <code>RangeFilterHandler</code>, TODO more examples
 * 
 * @author rwoo
 * @since 1.1.0
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
     * @return Returns null if the stream is not modified. Otherwise a match result must be returned that is still valid
     *         for the modified stream. If you insert or remove characters before the match
     *         <code>MatchResultShifted</code> helps you.
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

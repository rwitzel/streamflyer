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
package com.github.rwitzel.streamflyer.regex.addons.nomatch;

import com.github.rwitzel.streamflyer.core.AfterModification;
import com.github.rwitzel.streamflyer.core.Modifier;

/**
 * This {@link Modifier} updates the start position of the next {@link NoMatch noMatch} and delegates to another
 * modifier. Additionally, it triggers the processing of a noMatch if necessary.
 * 
 * @author rwoo
 * @since 1.1.0
 */
public class NoMatchAwareModifier implements Modifier {

    private Modifier delegate;

    private NoMatch noMatch;

    public NoMatchAwareModifier(Modifier delegate, NoMatch noMatch) {
        super();
        this.delegate = delegate;
        this.noMatch = noMatch;
    }

    @Override
    public AfterModification modify(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit) {

        // set the start position of the next noMatch
        noMatch.setStartPosition(firstModifiableCharacterInBuffer);

        AfterModification mod = delegate.modify(characterBuffer, firstModifiableCharacterInBuffer, endOfStreamHit);

        if (noMatch.getStartPosition() < firstModifiableCharacterInBuffer + mod.getNumberOfCharactersToSkip()) {
            // there are characters for the noMatch -> process them
            mod = noMatch.processNoMatch(characterBuffer, firstModifiableCharacterInBuffer, endOfStreamHit, mod,
                    delegate);
        }

        return mod;
    }

}

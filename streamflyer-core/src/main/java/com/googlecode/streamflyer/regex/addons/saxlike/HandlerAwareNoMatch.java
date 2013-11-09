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
package com.googlecode.streamflyer.regex.addons.saxlike;

import java.util.regex.MatchResult;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.regex.addons.nomatch.NoMatch;

/**
 * Helper class for {@link HandlerAwareModifier}.
 * 
 * @author rwoo
 * 
 */
public class HandlerAwareNoMatch extends NoMatch {

    private Handler delegate;

    public HandlerAwareNoMatch(Handler delegate) {
        super();
        this.delegate = delegate;
    }

    @Override
    public AfterModification processNoMatch(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit, AfterModification mod, Modifier modifier) {

        AfterModification newMod = delegate.processNoMatch(getStartPosition(), characterBuffer,
                firstModifiableCharacterInBuffer, endOfStreamHit, mod, modifier);

        if (newMod != null) {
            return newMod;
        } else {
            // return the original afterModification
            return mod;
        }
    }

    @Override
    public MatchResult processNoMatch(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            MatchResult matchResult) {

        MatchResult newMatchResult = delegate.processNoMatch(getStartPosition(), characterBuffer,
                firstModifiableCharacterInBuffer, matchResult);

        if (newMatchResult != null) {
            return newMatchResult;
        } else {
            // return the original result
            return matchResult;
        }
    }

}

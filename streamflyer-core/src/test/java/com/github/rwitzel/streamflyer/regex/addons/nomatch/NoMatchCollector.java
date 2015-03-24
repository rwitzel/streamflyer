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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;

import com.github.rwitzel.streamflyer.core.AfterModification;
import com.github.rwitzel.streamflyer.core.Modifier;
import com.github.rwitzel.streamflyer.regex.addons.nomatch.NoMatch;

/**
 * Collects the information about noMatches during the stream processing to test them in unit tests.
 * 
 * @author rwoo
 * 
 */
public class NoMatchCollector extends NoMatch {

    private List<String> noMatchInfos = new ArrayList<String>();

    @Override
    public MatchResult processNoMatch(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            MatchResult matchResult) {

        String noMatch = characterBuffer.substring(getStartPosition(), matchResult.start());

        noMatchInfos.add(noMatch + "[MATCH]");
        // System.out.println(noMatchInfos.get(noMatchInfos.size() - 1)); // print info

        return super.processNoMatch(characterBuffer, firstModifiableCharacterInBuffer, matchResult);
    }

    @Override
    public AfterModification processNoMatch(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit, AfterModification mod, Modifier modifier) {

        String noMatch = characterBuffer.substring(getStartPosition(),
                firstModifiableCharacterInBuffer + mod.getNumberOfCharactersToSkip());

        noMatchInfos.add(noMatch + "[FETCH]");
        // System.out.println(noMatchInfos.get(noMatchInfos.size() - 1)); // print info

        return super.processNoMatch(characterBuffer, firstModifiableCharacterInBuffer, endOfStreamHit, mod, modifier);
    }

    /**
     * @return Returns {@link #getNoMatchInfos()}.
     */
    public List<String> getNoMatchInfos() {
        return noMatchInfos;
    }

}

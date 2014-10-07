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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.regex.addons.tokens.Token;

public class MyHandler implements Handler {

    private List<String> foundParts = new ArrayList<String>();

    public MyHandler(List<String> foundParts) {
        super();
        this.foundParts = foundParts;
    }

    //
    // process matched token
    //

    @Override
    public MatchProcessorResult processToken(Token token, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {

        foundParts.add(token.getName() + ":" + matchResult.group() + "[MATCH]");

        // don't modify anything directly (the default processors will be applied
        return null;
    }

    //
    // process noMatch
    //

    @Override
    public MatchResult processNoMatch(int startPosition, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {

        String noMatch = characterBuffer.substring(startPosition, matchResult.start());

        foundParts.add(noMatch + "[BEFORE_MATCH]");

        // don't modify anything
        return null;
    }

    @Override
    public AfterModification processNoMatch(int startPosition, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit, AfterModification mod, Modifier modifier) {

        String noMatch = characterBuffer.substring(startPosition,
                firstModifiableCharacterInBuffer + mod.getNumberOfCharactersToSkip());

        foundParts.add(noMatch + "[BEFORE_FETCH]");
        // System.out.println(noMatchInfos.get(noMatchInfos.size() - 1)); // print info

        // don't modify anything
        return null;
    }

}

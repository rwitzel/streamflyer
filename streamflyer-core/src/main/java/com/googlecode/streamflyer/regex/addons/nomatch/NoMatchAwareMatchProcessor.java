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
package com.googlecode.streamflyer.regex.addons.nomatch;

import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessor;
import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.regex.RegexModifier;
import com.googlecode.streamflyer.regex.addons.tokens.StateMachine;

/**
 * This {@link MatchProcessor} delegates to another match processor and updates the start position of the next
 * {@link NoMatch noMatch} after the match. If {@link #processNoMatch} is true, then this noMatches are processed before
 * the match.
 * 
 * @author rwoo
 * 
 */
public class NoMatchAwareMatchProcessor implements MatchProcessor {

    protected MatchProcessor delegate;

    protected NoMatch noMatch;

    /**
     * True if this processor shall process noMatches. This should be false if there is already a
     * {@link NoMatchAwareTransitionGuard} configured. This applies only if the {@link RegexModifier} uses a
     * {@link StateMachine}.
     */
    private boolean processNoMatch;

    public NoMatchAwareMatchProcessor(MatchProcessor delegate, NoMatch noMatch, boolean processNoMatch) {
        super();
        this.delegate = delegate;
        this.noMatch = noMatch;
        this.processNoMatch = processNoMatch;
    }

    @Override
    public MatchProcessorResult process(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            MatchResult matchResult) {

        // are there characters that belong to a noMatch?
        if (processNoMatch && noMatch.getStartPosition() < matchResult.start()) {
            // -> process the noMatch
            matchResult = noMatch.processNoMatch(characterBuffer, firstModifiableCharacterInBuffer, matchResult);
        }

        MatchProcessorResult result = delegate.process(characterBuffer, firstModifiableCharacterInBuffer, matchResult);

        // set the start position of the next noMatch
        noMatch.setStartPosition(result.getFirstModifiableCharacterInBuffer());

        return result;
    }

}

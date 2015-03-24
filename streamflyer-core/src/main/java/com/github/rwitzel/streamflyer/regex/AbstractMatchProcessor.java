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
/*
 * $Id$
 */
package com.github.rwitzel.streamflyer.regex;

import java.util.regex.MatchResult;

/**
 * Helps to return a {@link MatchProcessorResult} that cannot cause an endless loop.
 * 
 * @author rwoo
 * @since 10.11.2013
 */
public abstract class AbstractMatchProcessor implements MatchProcessor {

    /**
     * @param matchResult
     *            the {@link MatchResult} given to the {@link MatchProcessor}.
     * @param newMatchEnd
     *            the position of the end of the matched string after the character buffer is modified.
     * @param continueMatching
     *            See {@link MatchProcessorResult#isContinueMatching()}
     * @return Returns a {@link MatchProcessorResult} that cannot cause an endless loop.
     */
    protected MatchProcessorResult createResult(MatchResult matchResult, int newMatchEnd, boolean continueMatching) {

        int matchStart = matchResult.start();
        int matchEnd = matchResult.end();

        // if the empty string is matched, then we increase the position
        // to avoid endless loops
        // (compare to Matcher.find() where we see the following code:
        // int i = last; if(i == first) i++;
        // in words: set the *from* for the next match at the
        // end of the last match. if this is equal to the start
        // of the last match (a match on the empty string(, then
        // increase the *from* to avoid endless loops)
        int offset = matchStart == matchEnd ? 1 : 0;

        return new MatchProcessorResult(newMatchEnd + offset, continueMatching);
    }

}

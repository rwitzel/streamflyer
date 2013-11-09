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
package com.googlecode.streamflyer.regex.addons.util;

import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessor;
import com.googlecode.streamflyer.regex.MatchProcessorResult;

/**
 * This {@link MatchProcessor} does not modify the stream. Prevents endless loops if the empty string is matched.
 * 
 * @author rwoo
 * 
 */
public class DoNothingProcessor implements MatchProcessor {

    @Override
    public MatchProcessorResult process(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            MatchResult matchResult) {

        int start = matchResult.start();
        int end = matchResult.end();

        // if the empty string is matched, then we increase the position
        // to avoid endless loops
        // (compare to Matcher.find() where we see the following code:
        // int i = last; if(i == first) i++;
        // in words: set the *from* for the next match at the
        // end of the last match. if this is equal to the start
        // of the last match (a match on the empty string(, then
        // increase the *from* to avoid endless loops)
        int offset = start == end ? 1 : 0;

        return new MatchProcessorResult(end + offset, true);
    }

}

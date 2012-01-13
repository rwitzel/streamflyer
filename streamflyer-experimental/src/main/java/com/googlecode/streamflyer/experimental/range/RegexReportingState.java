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

package com.googlecode.streamflyer.experimental.range;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.MatchResult;

/**
 * TODO please doc
 * 
 * @author rwoo
 * 
 * @since 14.09.2011
 */
public class RegexReportingState extends RangeFilterState {

    private List<String> matches = new ArrayList<String>();

    public RegexReportingState(String regex) {
        // (I hope deleteCharactersBeforeToken == true is time-saving)
        super(regex, true, false);
    }

    /**
     * @see com.googlecode.streamflyer.experimental.range.RangeFilterState#modifyBuffer(java.lang.StringBuilder,
     *      int, java.util.regex.MatchResult)
     */
    @Override
    protected int modifyBuffer(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {

        matches.add(characterBuffer.substring(matchResult.start(),
                matchResult.end()));

        return super.modifyBuffer(characterBuffer,
                firstModifiableCharacterInBuffer, matchResult);
    }

    /**
     * @return Returns the {@link #matches} in an unmodifiable list.
     */
    public List<String> getMatches() {
        return Collections.unmodifiableList(matches);
    }


}

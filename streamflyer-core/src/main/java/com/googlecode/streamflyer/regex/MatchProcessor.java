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

package com.googlecode.streamflyer.regex;

import java.util.regex.MatchResult;

import com.googlecode.streamflyer.core.Modifier;

/**
 * This interface is used by {@link RegexModifier}. This interface defines what
 * to do if the regular expression matches some text in the stream.
 * 
 * @author rwoo
 * @since 18.06.2011
 */
public interface MatchProcessor {

    /**
     * Processes the given match. The match processor is allowed to modify the
     * given character buffer but it must not modify the characters before the
     * first modifiable character in the buffer because the characters before
     * that position are considered unmodifiable.
     * 
     * @param characterBuffer Must not be <code>null</code>.
     * @param firstModifiableCharacterInBuffer See the parameter with the same
     *        name in {@link Modifier#modify(StringBuilder, int, boolean)}.
     * @param matchResult The match that is found in the given character buffer.
     *        Must not be <code>null</code>.
     * @return Returns an {@link MatchProcessorResult object} that describes
     *         where to continue the matching.
     */
    public MatchProcessorResult process(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult);

}

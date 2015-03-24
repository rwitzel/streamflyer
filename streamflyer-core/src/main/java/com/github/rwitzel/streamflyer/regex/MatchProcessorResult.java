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
package com.github.rwitzel.streamflyer.regex;

/**
 * Result of {@link MatchProcessor#process(StringBuilder, int, java.util.regex.MatchResult)} .
 * 
 * @author rwoo
 * @since 18.06.2011
 */
public class MatchProcessorResult {

    /**
     * A position in the character buffer. The characters before this position shall be considered unmodifiable by the
     * {@link RegexModifier}.
     * <p>
     * This position must not be smaller than the (originally) first modifiable character in the buffer.
     * <p>
     * This position must not be greater than the size of the buffer plus one.
     * <p>
     * EXAMPLE: This position is equal to the size of the buffer plus one if the empty string is matched (and processed)
     * and the matching shall be continued after the first character after the empty match. This avoids endless loops.
     */
    private int firstModifiableCharacterInBuffer;

    /**
     * True if the matching shall be continued using the characters that already available in the buffer.
     */
    private boolean continueMatching;

    /**
     * @param firstModifiableCharacterInBuffer
     * @param continueMatching
     */
    public MatchProcessorResult(int firstModifiableCharacterInBuffer, boolean continueMatching) {
        super();
        this.firstModifiableCharacterInBuffer = firstModifiableCharacterInBuffer;
        this.continueMatching = continueMatching;
    }

    /**
     * @return Returns the {@link #firstModifiableCharacterInBuffer}.
     */
    public int getFirstModifiableCharacterInBuffer() {
        return firstModifiableCharacterInBuffer;
    }

    /**
     * @return Returns the {@link #continueMatching}.
     */
    public boolean isContinueMatching() {
        return continueMatching;
    }
}

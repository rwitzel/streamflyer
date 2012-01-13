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


/**
 * Result of
 * {@link MatchProcessor#process(StringBuilder, int, java.util.regex.MatchResult)}
 * . The type is documented there. .
 * 
 * @author rwoo
 * 
 * @since 18.06.2011
 */
public class MatchProcessorResult {

    private int firstModifiableCharacterInBuffer;

    private boolean continueMatching;

    /**
     * @param firstModifiableCharacterInBuffer
     * @param continueMatching
     */
    public MatchProcessorResult(int firstModifiableCharacterInBuffer,
            boolean continueMatching) {
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

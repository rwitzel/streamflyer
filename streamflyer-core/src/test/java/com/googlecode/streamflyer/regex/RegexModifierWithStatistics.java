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

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.regex.MatchProcessor;
import com.googlecode.streamflyer.regex.OnStreamMatcher;
import com.googlecode.streamflyer.regex.RegexModifier;
import com.googlecode.streamflyer.util.statistics.StatisticsModificationFactory;

/**
 * @author rwoo
 * 
 * @since 30.06.2011
 */
public class RegexModifierWithStatistics extends RegexModifier {

    //
    // injected
    //

    protected StatisticsModificationFactory factoryWithStatistics;

    //
    // state
    //

    protected int maxLenCharBuf;

    protected int maxCapacityCharBuf;

    protected int lastCapacityCharBuf;

    protected int changedCapacityCharBuf;

    //
    // constructors
    //

    public RegexModifierWithStatistics(OnStreamMatcher matcher,
            MatchProcessor matchProcessor, int minimumLengthOfLookBehind,
            int requestedCapacityOfCharacterBuffer) {
        super(matcher, matchProcessor, minimumLengthOfLookBehind,
                requestedCapacityOfCharacterBuffer);
    }

    /**
     * @see com.googlecode.streamflyer.regex.RegexModifier#init(com.googlecode.streamflyer.regex.OnStreamMatcher,
     *      java.lang.String, int, int)
     */
    @SuppressWarnings("hiding")
    @Override
    protected void init(OnStreamMatcher matcher, MatchProcessor matchProcessor,
            int minimumLengthOfLookBehind,
            int requestedCapacityOfCharacterBuffer) {
        super.init(matcher, matchProcessor, minimumLengthOfLookBehind,
                requestedCapacityOfCharacterBuffer);
        // replace the factory with a factory that provides some statistics
        this.factory = new StatisticsModificationFactory(this.factory);
    }

    //
    // Modifier.*()
    //

    /**
     * @see com.googlecode.streamflyer.regex.RegexModifier#modify(java.lang.StringBuilder,
     *      int, boolean)
     */
    @Override
    public AfterModification modify(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        if (maxLenCharBuf < characterBuffer.length()) {
            maxLenCharBuf = characterBuffer.length();
        }

        if (maxCapacityCharBuf < characterBuffer.capacity()) {
            maxCapacityCharBuf = characterBuffer.capacity();
        }

        if (lastCapacityCharBuf != characterBuffer.capacity()) {
            changedCapacityCharBuf++;
            lastCapacityCharBuf = characterBuffer.capacity();
        }

        return super.modify(characterBuffer, firstModifiableCharacterInBuffer,
                endOfStreamHit);
    }

    //
    // getter
    //

    /**
     * @return Returns the {@link #maxLenCharBuf}.
     */
    public int getMaxLenCharBuf() {
        return maxLenCharBuf;
    }

    /**
     * @return Returns the {@link #maxCapacityCharBuf}.
     */
    public int getMaxCapacityCharBuf() {
        return maxCapacityCharBuf;
    }

    /**
     * @return Returns the {@link #changedCapacityCharBuf}.
     */
    public int getChangedCapacityCharBuf() {
        return changedCapacityCharBuf;
    }

    /**
     * @return Returns the {@link #factoryWithStatistics}.
     */
    public StatisticsModificationFactory getFactoryWithStatistics() {
        return factoryWithStatistics;
    }

}
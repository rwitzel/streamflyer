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
package com.github.rwitzel.streamflyer.core;

import com.github.rwitzel.streamflyer.core.AfterModification;
import com.github.rwitzel.streamflyer.core.Modifier;
import com.github.rwitzel.streamflyer.util.ModificationFactory;
import com.github.rwitzel.streamflyer.util.statistics.StatisticsModificationFactory;

/**
 * Does not modify the character stream.
 * <p>
 * TODO this modifier for a special unit test that varies numberOfCharactersToSkip ... so implement this modifier
 * accordingly and rename the modifier
 * 
 * @author rwoo
 * @since 28.06.2011
 */
class IdleModifier implements Modifier {

    private StatisticsModificationFactory factory;

    /**
     * The maximum size of the character buffer given to {@link #modify(StringBuilder, int, boolean)} so far.
     */
    private int maxSizeOfCharacterBuffer = 0;

    /**
     * The maximum capacity of the character buffer given to {@link #modify(StringBuilder, int, boolean)} so far.
     */
    private int maxCapacityOfCharacterBuffer = 0;

    public IdleModifier() {
        this(0, 4096, 0);
    }

    public IdleModifier(int minimumLengthOfLookBehind, int newNumberOfChars, int numberOfCharactersToSkip) {

        this.factory = new StatisticsModificationFactory(new ModificationFactory(minimumLengthOfLookBehind,
                newNumberOfChars));
    }

    /**
     * @see com.github.rwitzel.streamflyer.core.Modifier#modify(java.lang.StringBuilder, int, boolean)
     */
    @Override
    public AfterModification modify(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit) {

        if (maxSizeOfCharacterBuffer < characterBuffer.length()) {
            maxSizeOfCharacterBuffer = characterBuffer.length();
        }
        if (maxCapacityOfCharacterBuffer < characterBuffer.capacity()) {
            maxCapacityOfCharacterBuffer = characterBuffer.capacity();
        }

        // skip as much characters as possible
        return factory.skipEntireBuffer(characterBuffer, firstModifiableCharacterInBuffer, endOfStreamHit);

    }

    /**
     * @return Returns the {@link #maxSizeOfCharacterBuffer}.
     */
    public int getMaxSizeOfCharacterBuffer() {
        return maxSizeOfCharacterBuffer;
    }

    /**
     * @return Returns the {@link #maxCapacityOfCharacterBuffer}.
     */
    public int getMaxCapacityOfCharacterBuffer() {
        return maxCapacityOfCharacterBuffer;
    }

    /**
     * @return Returns the {@link #factory}.
     */
    public StatisticsModificationFactory getFactory() {
        return factory;
    }

}
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
package com.github.rwitzel.streamflyer.util.statistics;

import com.github.rwitzel.streamflyer.core.AfterModification;
import com.github.rwitzel.streamflyer.util.ModificationFactory;

/**
 * Keeps track of the current position in the stream.
 * <p>
 * This might be useful for reporting modifications.
 * 
 * @author rwoo
 * @since 28.06.2011
 */
public class PositionAwareModificationFactory extends ModificationFactoryDecorator {

    //
    // properties that represent the internal mutable state
    //

    protected long currentPosition = 0;

    //
    // constructors
    //

    public PositionAwareModificationFactory(ModificationFactory delegate) {
        super(delegate);
    }

    //
    // interface ModificationFactory.* methods
    //

    /**
     * @see com.github.rwitzel.streamflyer.util.ModificationFactory#skipOrStop(int, java.lang.StringBuilder, int, boolean)
     */
    @Override
    public AfterModification skipOrStop(int numberOfCharactersToSkip, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        AfterModification afterModification = super.skipOrStop(numberOfCharactersToSkip, characterBuffer,
                firstModifiableCharacterInBuffer, endOfStreamHit);

        currentPosition += afterModification.getNumberOfCharactersToSkip();

        return afterModification;
    }

    /**
     * @see com.github.rwitzel.streamflyer.util.statistics.ModificationFactoryDecorator#skip(int, java.lang.StringBuilder,
     *      int, boolean)
     */
    @Override
    public AfterModification skip(int numberOfCharactersToSkip, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        AfterModification afterModification = super.skip(numberOfCharactersToSkip, characterBuffer,
                firstModifiableCharacterInBuffer, endOfStreamHit);

        currentPosition += afterModification.getNumberOfCharactersToSkip();

        return afterModification;
    }

    /**
     * @see com.github.rwitzel.streamflyer.util.statistics.ModificationFactoryDecorator#fetchMoreInput(int,
     *      java.lang.StringBuilder, int, boolean)
     */
    @Override
    public AfterModification fetchMoreInput(int numberOfCharactersToSkip, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        AfterModification afterModification = super.fetchMoreInput(numberOfCharactersToSkip, characterBuffer,
                firstModifiableCharacterInBuffer, endOfStreamHit);

        currentPosition += afterModification.getNumberOfCharactersToSkip();

        return afterModification;
    }

    /**
     * @see com.github.rwitzel.streamflyer.util.statistics.ModificationFactoryDecorator#modifyAgainImmediately(int, int)
     */
    @Override
    public AfterModification modifyAgainImmediately(int newNumberOfChars, int firstModifiableCharacterInBuffer) {

        AfterModification afterModification = super.modifyAgainImmediately(newNumberOfChars,
                firstModifiableCharacterInBuffer);

        currentPosition += afterModification.getNumberOfCharactersToSkip();

        return afterModification;
    }

    /**
     * @see com.github.rwitzel.streamflyer.util.ModificationFactory#skipEntireBuffer(java.lang.StringBuilder, int, boolean)
     */
    @Override
    public AfterModification skipEntireBuffer(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit) {

        AfterModification afterModification = super.skipEntireBuffer(characterBuffer, firstModifiableCharacterInBuffer,
                endOfStreamHit);

        currentPosition += afterModification.getNumberOfCharactersToSkip();

        return afterModification;
    }

    /**
     * @see com.github.rwitzel.streamflyer.util.statistics.ModificationFactoryDecorator#stop(java.lang.StringBuilder, int,
     *      boolean)
     */
    @Override
    public AfterModification stop(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit) {

        AfterModification afterModification = super.stop(characterBuffer, firstModifiableCharacterInBuffer,
                endOfStreamHit);

        currentPosition += afterModification.getNumberOfCharactersToSkip();

        return afterModification;
    }

    //
    // getter methods
    //

    /**
     * @return Returns the {@link #currentPosition}.
     */
    public long getCurrentPosition() {
        return currentPosition;
    }

}
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
package com.googlecode.streamflyer.util.statistics;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.util.ModificationFactory;

/**
 * Records some statistics of the usage of the underlying
 * {@link ModificationFactory}. This might be useful for reporting
 * modifications.
 * 
 * @author rwoo
 * @since 28.06.2011
 */
public class StatisticsModificationFactory extends ModificationFactoryDecorator {

    //
    // properties that represent the internal mutable state
    //

    protected int numAllModifications = 0;

    protected int numModifyAgainImmediately = 0;

    protected int numMoreInputThanDefault;

    protected int maxRequestedNewNumberOfChars;


    //
    // constructors
    //

    /**
     * @param delegate
     */
    public StatisticsModificationFactory(ModificationFactory delegate) {
        super(delegate);

    }

    //
    // interface ModificationFactory.* methods
    //

    /**
     * @see com.googlecode.streamflyer.util.statistics.ModificationFactoryDecorator#modifyAgainImmediately(int,
     *      int)
     */
    @Override
    public AfterModification modifyAgainImmediately(int newNumberOfChars,
            int firstModifiableCharacterInBuffer) {

        return count(super.modifyAgainImmediately(newNumberOfChars,
                firstModifiableCharacterInBuffer));
    }

    /**
     * @see com.googlecode.streamflyer.util.ModificationFactory#modifyAgainWithMoreInput(java.lang.StringBuilder,
     *      int)
     */
    @Override
    public AfterModification fetchMoreInput(int numberOfCharactersToSkip,
            StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        return count(super.fetchMoreInput(numberOfCharactersToSkip,
                characterBuffer, firstModifiableCharacterInBuffer,
                endOfStreamHit));
    }

    /**
     * @see com.googlecode.streamflyer.util.statistics.LineColumnAwareModificationFactory#skipOrStop(int,
     *      java.lang.StringBuilder, int, boolean)
     */
    @Override
    public AfterModification skipOrStop(int numberOfCharactersToSkip,
            StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        return count(super.skipOrStop(numberOfCharactersToSkip,
                characterBuffer, firstModifiableCharacterInBuffer,
                endOfStreamHit));
    }

    /**
     * @see com.googlecode.streamflyer.util.statistics.ModificationFactoryDecorator#skip(int,
     *      java.lang.StringBuilder, int, boolean)
     */
    @Override
    public AfterModification skip(int numberOfCharactersToSkip,
            StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        return count(super.skip(numberOfCharactersToSkip, characterBuffer,
                firstModifiableCharacterInBuffer, endOfStreamHit));
    }

    /**
     * @see com.googlecode.streamflyer.util.ModificationFactory#skipEntireBuffer(java.lang.StringBuilder,
     *      int, boolean)
     */
    @Override
    public AfterModification skipEntireBuffer(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        return count(super.skipEntireBuffer(characterBuffer,
                firstModifiableCharacterInBuffer, endOfStreamHit));
    }

    /**
     * @see com.googlecode.streamflyer.util.statistics.ModificationFactoryDecorator#stop(java.lang.StringBuilder,
     *      int, boolean)
     */
    @Override
    public AfterModification stop(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        return count(super.stop(characterBuffer,
                firstModifiableCharacterInBuffer, endOfStreamHit));
    }

    protected AfterModification count(AfterModification afterModification) {

        // update
        numAllModifications++;

        // update numModifyAgainImmediately
        if (afterModification.isModifyAgainImmediately()) {
            numModifyAgainImmediately++;
        }

        // update maxRequestedNewNumberOfChars
        if (maxRequestedNewNumberOfChars < afterModification
                .getNewNumberOfChars()) {
            maxRequestedNewNumberOfChars = afterModification
                    .getNewNumberOfChars();
        }

        if (afterModification.getNewNumberOfChars() < afterModification
                .getNewNumberOfChars()) {
            numMoreInputThanDefault++;
        }

        return afterModification;
    }

    //
    // getter methods
    //

    /**
     * @return Returns the {@link #numAllModifications}.
     */
    public int getNumAllModifications() {
        return numAllModifications;
    }

    /**
     * @return Returns the {@link #numModifyAgainImmediately}.
     */
    public int getNumModifyAgainImmediately() {
        return numModifyAgainImmediately;
    }

    /**
     * @return Returns the {@link #numMoreInputThanDefault}.
     */
    public int getNumMoreInputThanDefault() {
        return numMoreInputThanDefault;
    }

    /**
     * @return Returns the {@link #maxRequestedNewNumberOfChars}.
     */
    public int getMaxRequestedNewNumberOfChars() {
        return maxRequestedNewNumberOfChars;
    }

}
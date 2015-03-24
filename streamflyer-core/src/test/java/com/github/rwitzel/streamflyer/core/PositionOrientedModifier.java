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
package com.github.rwitzel.streamflyer.core;

import java.util.Map;

import com.github.rwitzel.streamflyer.core.AfterModification;
import com.github.rwitzel.streamflyer.core.FaultyModifierException;
import com.github.rwitzel.streamflyer.core.Modifier;
import com.github.rwitzel.streamflyer.internal.thirdparty.ZzzValidate;
import com.github.rwitzel.streamflyer.util.ModificationFactory;
import com.github.rwitzel.streamflyer.util.statistics.PositionAwareModificationFactory;

/**
 * Applies modifications depending on the position of the next unread character.
 * 
 * @author rwoo
 * @since 28.06.2011
 */
class PositionOrientedModifier implements Modifier {

    private PositionAwareModificationFactory factory;

    /**
     * Maps positions in streams to modifications that are apply (at the given position).
     */
    private Map<Long, Change> modifications;

    public PositionOrientedModifier(Map<Long, Change> modifications) {

        ZzzValidate.notNull(modifications, "modifications must not be null");

        this.modifications = modifications;
        this.factory = new PositionAwareModificationFactory(new ModificationFactory(3, 10));
    }

    /**
     * @see com.github.rwitzel.streamflyer.core.Modifier#modify(java.lang.StringBuilder, int, boolean)
     */
    @Override
    public AfterModification modify(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit) {

        long currentPosition = factory.getCurrentPosition();

        if (modifications.containsKey(currentPosition)) {

            Change change = modifications.get(currentPosition);

            if (firstModifiableCharacterInBuffer + change.getNumberOfCharactersToDelete() > characterBuffer.length()) {

                if (endOfStreamHit) {
                    throw new FaultyModifierException("gvgfd", null);
                } else {

                    return factory.fetchMoreInput(0, characterBuffer, firstModifiableCharacterInBuffer, endOfStreamHit);
                }

            } else {

                if (change.getNumberOfCharactersToDelete() > 0) {

                    characterBuffer.delete(firstModifiableCharacterInBuffer,
                            firstModifiableCharacterInBuffer + change.getNumberOfCharactersToDelete());
                }

                if (change.getInsertion() != null) {
                    characterBuffer.insert(firstModifiableCharacterInBuffer, change.getInsertion());
                }

                return nextPosition(characterBuffer, firstModifiableCharacterInBuffer, endOfStreamHit);
            }

        } else {

            return nextPosition(characterBuffer, firstModifiableCharacterInBuffer, endOfStreamHit);
        }
    }

    private AfterModification nextPosition(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit) {

        if (endOfStreamHit && characterBuffer.length() - firstModifiableCharacterInBuffer == 0) {
            return factory.stop(characterBuffer, firstModifiableCharacterInBuffer, endOfStreamHit);
        } else {
            if (characterBuffer.length() - firstModifiableCharacterInBuffer == 0) {
                return factory.skip(0, characterBuffer, firstModifiableCharacterInBuffer, endOfStreamHit);
            } else {
                // skip one character
                return factory.skip(1, characterBuffer, firstModifiableCharacterInBuffer, endOfStreamHit);
            }
        }
    }
}
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
package com.github.rwitzel.streamflyer.util.statistics;

import com.github.rwitzel.streamflyer.core.AfterModification;
import com.github.rwitzel.streamflyer.internal.thirdparty.ZzzValidate;
import com.github.rwitzel.streamflyer.util.ModificationFactory;

/**
 * Abstract decorator for {@link ModificationFactory}.
 * <p>
 * (I wish Java could provide mixins as Scala does ... Sorry, dear reader, I know this package is over-engineered but I
 * could not resist.)
 * 
 * @author rwoo
 * @since 27.06.2011
 */
public abstract class ModificationFactoryDecorator extends ModificationFactory {

    private ModificationFactory delegate;

    public ModificationFactoryDecorator(ModificationFactory delegate) {
        super();

        ZzzValidate.notNull(delegate, "delegate must not be null");

        this.delegate = delegate;
    }

    //
    // public methods
    //

    @Override
    public AfterModification skip(int numberOfCharactersToSkip, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        // delegate
        return delegate.skip(numberOfCharactersToSkip, characterBuffer, firstModifiableCharacterInBuffer,
                endOfStreamHit);

    }

    @Override
    public AfterModification skipOrStop(int numberOfCharactersToSkip, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        // delegate
        return delegate.skipOrStop(numberOfCharactersToSkip, characterBuffer, firstModifiableCharacterInBuffer,
                endOfStreamHit);
    }

    @Override
    public AfterModification fetchMoreInput(int numberOfCharactersToSkip, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        // delegate
        return delegate.fetchMoreInput(numberOfCharactersToSkip, characterBuffer, firstModifiableCharacterInBuffer,
                endOfStreamHit);
    }

    @Override
    public AfterModification modifyAgainImmediately(int newNumberOfChars, int firstModifiableCharacterInBuffer) {

        // delegate
        return delegate.modifyAgainImmediately(newNumberOfChars, firstModifiableCharacterInBuffer);
    }

    @Override
    public AfterModification stop(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit) {

        // delegate
        return delegate.stop(characterBuffer, firstModifiableCharacterInBuffer, endOfStreamHit);
    }

    /**
     * @see com.github.rwitzel.streamflyer.util.ModificationFactory#getNewNumberOfChars()
     */
    @Override
    public int getNewNumberOfChars() {

        // delegate
        return delegate.getNewNumberOfChars();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ModificationFactoryDecorator [\ndelegate=");
        builder.append(delegate);
        builder.append("]");
        return builder.toString();
    }

}

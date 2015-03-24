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

/**
 * A modifier replaces, deletes, inserts characters in a character stream. This happens in interaction with a
 * <em>stream processor</em>, i. e. either a {@link ModifyingReader} or a {@link ModifyingWriter}. The modifier can be
 * considered a callback that is called by the stream processor to make the desired modifications. The pivotal part of
 * the interaction between the modifier and the stream processor is a character buffer the stream processor facilitates.
 * That buffer contains the characters of the underlying character stream.
 * <p>
 * Besides the modifications of the buffer, the modifier manages the capacity (size) of the character buffer and
 * {@link AfterModification defines} what the stream processor shall do before the stream processor
 * {@link Modifier#modify(StringBuilder, int, boolean) calls} the modifier the next time.
 * <p>
 * Implementations of this interface should inform the user about its specific memory consumption because the most
 * important feature of modifiers should be their small memory foot-print.
 * 
 * @author rwoo
 * @since 06.05.2011
 */
public interface Modifier {

    /**
     * Processes the characters in the given character buffer, i.e. deletes or replaces or inserts characters, or keeps
     * the characters as they are.
     * 
     * @param characterBuffer
     *            The next characters provided from the modifiable stream. It contains the modifiable characters,
     *            (optionally) preceded by unmodifiable characters.
     *            <p>
     *            The modifier can modify the content of the buffer as appropriate the modifier must not modify the
     *            unmodifiable characters, i.e. the characters before position
     *            <code>firstModifiableCharacterInBufferIndex</code> must not be modified.
     *            <p>
     *            Your modifier should manage the {@link StringBuilder#capacity() capacity} of the buffer on its own -
     *            as the optimal management of the capacity depends on the specific purpose of the modifier.
     *            <p>
     *            The given buffer must be never null.
     * @param firstModifiableCharacterInBuffer
     *            index of the first modifiable character in the buffer. The index of the first character in the buffer
     *            is zero. If there is not modifiable character in the buffer, then this value is equal the length of
     *            the buffer.
     * @param endOfStreamHit
     *            True if no more characters can be read from the stream, i.e. the character buffer contains the all the
     *            characters up to the end of the stream.
     * @return Returns an object that defines how {@link ModifyingReader} or {@link ModifyingWriter} shall behave before
     *         calling {@link Modifier#modify(StringBuilder, int, boolean)} again.
     */
    public AfterModification modify(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit);
}

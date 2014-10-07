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
 * Keeps track of the current line and column in the stream.
 * <p>
 * This might be useful for reporting modifications.
 * 
 * @author rwoo
 * @since 28.06.2011
 */
public class LineColumnAwareModificationFactory extends ModificationFactoryDecorator {

    //
    // properties that represent the internal mutable state
    //

    /**
     * The index of the line of the position of the next unread character.
     * <p>
     * The index of the first line is zero.
     * <p>
     * The number of lines is aware of aware of the three line endings - <code>\r</code>, <code>\n</code>,
     * <code>\r\n</code>.
     */
    protected long currentLine = 0;

    /**
     * The index of the column of the position of the next unread character. The index of the column is the number of
     * characters read after the last line break, see {@link #currentLine}.
     * <p>
     * The index of the first column is zero.
     */
    protected long currentColumn = 0;

    /**
     * The last character read.
     */
    protected char lastChar = ' ';

    //
    // constructors
    //

    /**
     * @param delegate
     */
    public LineColumnAwareModificationFactory(ModificationFactory delegate) {
        super(delegate);

    }

    //
    // interface ModificationFactory.* methods
    //

    @Override
    public AfterModification fetchMoreInput(int numberOfCharactersToSkip, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        AfterModification afterModification = super.fetchMoreInput(numberOfCharactersToSkip, characterBuffer,
                firstModifiableCharacterInBuffer, endOfStreamHit);

        onCharactersSkipped(afterModification.getNumberOfCharactersToSkip(), characterBuffer,
                firstModifiableCharacterInBuffer);

        return afterModification;
    }

    /**
     * @see com.googlecode.streamflyer.util.statistics.ModificationFactoryDecorator#modifyAgainImmediately(int, int)
     */
    @Override
    public AfterModification modifyAgainImmediately(int newNumberOfChars, int firstModifiableCharacterInBuffer) {

        AfterModification afterModification = super.modifyAgainImmediately(newNumberOfChars,
                firstModifiableCharacterInBuffer);

        // no characters to skip

        return afterModification;
    }

    /**
     * @see com.googlecode.streamflyer.util.statistics.ModificationFactoryDecorator#skip(int, java.lang.StringBuilder,
     *      int, boolean)
     */
    @Override
    public AfterModification skip(int numberOfCharactersToSkip, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        AfterModification afterModification = super.skip(numberOfCharactersToSkip, characterBuffer,
                firstModifiableCharacterInBuffer, endOfStreamHit);

        onCharactersSkipped(afterModification.getNumberOfCharactersToSkip(), characterBuffer,
                firstModifiableCharacterInBuffer);

        return afterModification;
    }

    /**
     * @see com.googlecode.streamflyer.util.ModificationFactory#skipEntireBuffer(java.lang.StringBuilder, int, boolean)
     */
    @Override
    public AfterModification skipEntireBuffer(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit) {

        AfterModification afterModification = super.skipEntireBuffer(characterBuffer, firstModifiableCharacterInBuffer,
                endOfStreamHit);

        onCharactersSkipped(afterModification.getNumberOfCharactersToSkip(), characterBuffer,
                firstModifiableCharacterInBuffer);

        return afterModification;
    }

    /**
     * @see com.googlecode.streamflyer.util.ModificationFactory#skipOrStop(int, java.lang.StringBuilder, int, boolean)
     */
    @Override
    public AfterModification skipOrStop(int numberOfCharactersToSkip, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        AfterModification afterModification = super.skipOrStop(numberOfCharactersToSkip, characterBuffer,
                firstModifiableCharacterInBuffer, endOfStreamHit);

        onCharactersSkipped(afterModification.getNumberOfCharactersToSkip(), characterBuffer,
                firstModifiableCharacterInBuffer);

        return afterModification;
    }

    /**
     * @see com.googlecode.streamflyer.util.statistics.ModificationFactoryDecorator#stop(java.lang.StringBuilder, int,
     *      boolean)
     */
    @Override
    public AfterModification stop(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit) {

        AfterModification afterModification = super.stop(characterBuffer, firstModifiableCharacterInBuffer,
                endOfStreamHit);

        onCharactersSkipped(afterModification.getNumberOfCharactersToSkip(), characterBuffer,
                firstModifiableCharacterInBuffer);

        return afterModification;
    }

    protected void onCharactersSkipped(int numberOfCharactersToSkip, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer) {

        int end = firstModifiableCharacterInBuffer + numberOfCharactersToSkip;

        for (int index = firstModifiableCharacterInBuffer; index < end; index++) {
            onCharacterSkipped(characterBuffer.charAt(index));
        }
    }

    protected void onCharacterSkipped(char ch) {

        // update current position, current line, current column

        if (ch == '\r') {
            currentLine++;
            currentColumn = 0;
        } else if (ch == '\n') {
            if (lastChar != '\r') {
                currentLine++;
                currentColumn = 0;
            }
        } else {
            currentColumn++;
        }
        lastChar = ch;
    }

    //
    // getter methods
    //

    /**
     * @return Returns the {@link #currentColumn}.
     */
    public long getCurrentColumn() {
        return currentColumn;
    }

    /**
     * @return Returns the {@link #currentLine}.
     */
    public long getCurrentLine() {
        return currentLine;
    }

}
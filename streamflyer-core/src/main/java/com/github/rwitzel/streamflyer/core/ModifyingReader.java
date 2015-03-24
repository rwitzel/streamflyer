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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link Reader} that allows a {@link Modifier} to modify the characters in the underlying stream.
 * <p>
 * This class is not synchronized.
 * <p>
 * {@link #markSupported() Marking} is not supported by this reader.
 * 
 * @author rwoo
 * @since 06.05.2011
 */
public class ModifyingReader extends Reader {

    //
    // injected properties
    //

    /**
     * The reader that the reads from the underlying character stream.
     */
    protected Reader delegate;

    /**
     * The modifier thats replaces, deletes, inserts characters of the underlying stream, and manages the buffer size.
     */
    private Modifier modifier;

    //
    // properties that represent the mutable state
    //

    /**
     * At its begin the buffer contains unmodifiable characters (might be needed for look-behind matches as known from
     * regular expression matching). After the unmodifiable characters the buffer holds the modifiable characters. See
     * {@link #firstModifiableCharacterInBuffer}.
     */
    private StringBuilder characterBuffer;

    /**
     * The position of the first character in the {@link #characterBuffer input buffer} that is modifiable.
     * <p>
     * This character and the following characters in {@link #characterBuffer} can be modified by the {@link #modifier}.
     */
    private int firstModifiableCharacterInBuffer;

    /**
     * The value is taken from {@link AfterModification#getNewMinimumLengthOfLookBehind()} after
     * {@link Modifier#modify(StringBuilder, int, boolean)} is called. The value is <em>requested</em> by the
     * {@link Modifier}.
     * <p>
     * If this value is greater than {@link #firstModifiableCharacterInBuffer}, the modifier is probably faulty.
     */
    private int minimumLengthOfLookBehind;

    /**
     * The value is the sum of {@link AfterModification#getNewMinimumLengthOfLookBehind()} and
     * {@link AfterModification#getNewNumberOfChars()} after {@link Modifier#modify(StringBuilder, int, boolean)} is
     * called. The value is <em>requested</em> by the {@link Modifier}.
     */
    private int requestedNumCharactersInBuffer;

    /**
     * The value is initially taken from {@link AfterModification#getNumberOfCharactersToSkip()} after
     * {@link Modifier#modify(StringBuilder, int, boolean)} is called. Then during reading characters from the reader
     * this value is decremented.
     */
    private int numberOfCharactersToSkip;

    /**
     * True if the end of stream was detected.
     */
    private boolean endOfStreamHit = false;

    /**
     * The holds the last {@link AfterModification} provided by the {@link #modifier}. This property serves debugging
     * purposes only.
     */
    private AfterModification lastAfterModificationForDebuggingOnly = null;

    /**
     * @param reader
     *            The underlying reader that provides the original, not modified characters. For optimal performance
     *            choose a {@link BufferedReader}.
     * @param modifier
     *            the object that modifies the stream.
     */
    public ModifyingReader(Reader reader, Modifier modifier) {
        super();

        this.delegate = reader;

        // take injected properties
        this.modifier = modifier;

        // initialize the mutable state
        this.numberOfCharactersToSkip = 0;
        this.minimumLengthOfLookBehind = 0; // we cannot do anything else
        this.firstModifiableCharacterInBuffer = 0;
        this.requestedNumCharactersInBuffer = 1; // any value
        this.characterBuffer = new StringBuilder(requestedNumCharactersInBuffer);
        adjustCapacityOfBuffer();
    }

    /**
     * Updates the input buffer according to {@link #minimumLengthOfLookBehind} and
     * {@link #requestedNumCharactersInBuffer}, and then fills the buffer up to its capacity.
     */
    private void updateBuffer() throws IOException {

        removeCharactersInBufferNotNeededAnyLonger();

        adjustCapacityOfBuffer();

        fill();
    }

    /**
     * Deletes those characters at the start of the buffer we do not need any longer.
     */
    private void removeCharactersInBufferNotNeededAnyLonger() {

        int charactersToDelete = firstModifiableCharacterInBuffer - minimumLengthOfLookBehind;

        if (charactersToDelete > 0) {
            characterBuffer.delete(0, charactersToDelete);
            firstModifiableCharacterInBuffer -= charactersToDelete;
        } else if (charactersToDelete < 0) {
            onFaultyModifier(-52, charactersToDelete + " characters to delete but this is not possible ("
                    + firstModifiableCharacterInBuffer + "," + minimumLengthOfLookBehind + ")");
        }
    }

    /**
     * Adjusts the capacity of the buffer.
     */
    protected void adjustCapacityOfBuffer() {

        // is the current capacity not big enough?
        if (characterBuffer.capacity() < requestedNumCharactersInBuffer) {

            // increase the capacity (we delegate to the default behavior of
            // StringBuffer)
            characterBuffer.ensureCapacity(requestedNumCharactersInBuffer);

        }

    }

    /**
     * Reads more characters into the input buffer. This method will block until the buffer is filled with
     * {@link #requestedNumCharactersInBuffer} characters , or an I/O error occurs, or the end of the stream is reached.
     */
    private void fill() throws IOException {

        int length = requestedNumCharactersInBuffer - characterBuffer.length();
        if (length <= 0) {
            // nothing to do
            return;
        }

        char[] buffer = new char[length];
        int offset = 0;

        while (length > 0) {

            int readChars = delegate.read(buffer, offset, length);
            if (readChars != -1) {
                characterBuffer.append(buffer, offset, readChars);
                offset += readChars;
                length -= readChars;
            } else {
                endOfStreamHit = true;
                break;
            }

        }

    }

    protected void onFaultyModifier(int errorCode, String errorMessage) {
        // should we silently ignore any errors and fallback to a meaningful
        // behavior? No because a faulty modifier should be fixed by a
        // ValidModifier, i.e. the user must take the responsibility for the
        // errors and deal with them. Therefore, we throw an exception here.

        // (1) collect the current state of the involved objects
        Map<String, Object> description = new HashMap<String, Object>();

        // describe arguments
        description.put("argument errorCode", errorCode);
        description.put("argument errorMessage", errorMessage);

        // describe reader
        description.put("this", this);

        // (2) create an error message
        List<String> keys = new ArrayList<String>(description.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append("\n" + key + ": " + description.get(key));
        }

        // (3) throw exception
        throw new FaultyModifierException(sb.toString(), description);
    }

    //
    // interface Reader
    //

    /**
     * TODO more doc
     * 
     * @see java.io.Reader#read(char[], int, int)
     */
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int index = 0;
        int read = 0;
        while (index < len && (read = readCharacter()) != -1) {
            cbuf[off + index] = (char) read;
            index++;
        }

        if (index == 0 && read == -1) {
            return -1;
        } else {
            return index;
        }
    }

    /**
     * Contract is similar to {@link java.io.Reader#read()}.
     * <p>
     * TODO more doc
     */
    protected int readCharacter() throws IOException {

        if (numberOfCharactersToSkip == 0) {

            boolean modifyAgainImmediately = false;

            AfterModification afterModification = null;
            do {

                updateBuffer();

                afterModification = modifier.modify(characterBuffer, firstModifiableCharacterInBuffer, endOfStreamHit);

                lastAfterModificationForDebuggingOnly = afterModification;

                // update minimumLengthOfLookBehind
                minimumLengthOfLookBehind = afterModification.getNewMinimumLengthOfLookBehind();
                if (minimumLengthOfLookBehind > firstModifiableCharacterInBuffer
                        + afterModification.getNumberOfCharactersToSkip()) {
                    onFaultyModifier(-11, "Requested Look Behind" + " is impossible because there are not enough "
                            + "characters in the stream.");
                }

                // update requestedNumCharactersInBuffer
                requestedNumCharactersInBuffer = minimumLengthOfLookBehind + afterModification.getNewNumberOfChars();
                if (requestedNumCharactersInBuffer < minimumLengthOfLookBehind + 1) {
                    onFaultyModifier(-13, "Requested Capacity" + " is two small because there must at least one"
                            + " unread characters available after the " + "look behind characters characters in the"
                            + " stream.");
                }

                modifyAgainImmediately = false;
                // do we have to read at least a single character as there is no
                // modifiable character left in the character buffer?
                if (firstModifiableCharacterInBuffer >= characterBuffer.length() && !endOfStreamHit) {
                    // yes, we need fresh input ->
                    modifyAgainImmediately = true;
                }
                // has the modifier requested a modification at the current
                // position?
                else if (afterModification.isModifyAgainImmediately()) {
                    modifyAgainImmediately = true;
                }

            } while (modifyAgainImmediately);

            numberOfCharactersToSkip = afterModification.getNumberOfCharactersToSkip();

            if (!afterModification.isModifyAgainImmediately() && numberOfCharactersToSkip == 0 && !endOfStreamHit) {
                onFaultyModifier(-16, "Not a single characters shall be " + "skipped but this is not possible of "
                        + "modifyAgain() returns false and the end of " + "stream is not reached yet.");
            }
            numberOfCharactersToSkip--;
        } else {
            numberOfCharactersToSkip--;
        }

        // is the end of the stream reached?
        if (firstModifiableCharacterInBuffer >= characterBuffer.length()) {
            // yes -> return -1
            return -1;
        } else {
            // no -> return the next unread character
            char result = characterBuffer.charAt(firstModifiableCharacterInBuffer);

            firstModifiableCharacterInBuffer++;

            return result;
        }
    }

    //
    // override Object.*
    //

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ModifyingReader [\ncharacterBuffer=");
        builder.append(characterBuffer);
        builder.append(", \nnextUnreadCharacterInBuffer=");
        builder.append(firstModifiableCharacterInBuffer);
        builder.append(", \nmodifier=");
        builder.append(modifier);
        builder.append(", \nminimumLengthOfLookBehind=");
        builder.append(minimumLengthOfLookBehind);
        builder.append(", \nrequestedCapacityOfCharacterBuffer=");
        builder.append(requestedNumCharactersInBuffer);
        builder.append(", \nlockedCharacters=");
        builder.append(numberOfCharactersToSkip);
        builder.append(", \nendOfStreamHit=");
        builder.append(endOfStreamHit);
        builder.append(", \nlastModificationForDebuggingOnly=");
        builder.append(lastAfterModificationForDebuggingOnly);
        builder.append("]");
        return builder.toString();
    }

    //
    // delegating methods
    //

    /**
     * @see java.io.Reader#close()
     */
    @Override
    public void close() throws IOException {
        delegate.close();
    }

    // What do I need this delegating method for? As long I don't know I don't
    // delegate.
    // /**
    // * @see java.io.Reader#ready()
    // */
    // @Override
    // public boolean ready() throws IOException {
    // return delegate.ready();
    // }

}

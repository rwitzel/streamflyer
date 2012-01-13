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

package com.googlecode.streamflyer.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A {@link Writer} that allows a {@link Modifier} to modify the characters in
 * the underlying stream.
 * <p>
 * This class is not synchronized.
 * <p>
 * ATTENTION! This writer flushes only characters that are confirmed by the
 * given {@link Modifier}. EXAMPLE: Assume you wrote 50 bytes to the writer and
 * the modifier confirmed 25 of them by
 * {@link AfterModification#getNumberOfCharactersToSkip() skipping } them, then
 * subsequent flushing will write only these 25 confirmed characters.
 * 
 * @author rwoo
 * 
 * @since 06.05.2011
 */
public class ModifyingWriter extends Writer {

    //
    // injected properties
    //

    protected Writer delegate;

    /**
     * The modifier thats replaces, deletes, inserts characters of the
     * underlying stream, and manages the buffer size.
     */
    private Modifier modifier;


    //
    // properties that represent the mutable state
    //

    /**
     * At its begin the buffer contains unmodifiable characters (might be needed
     * for look-behind matches as known from regular expression matching). After
     * the unmodifiable characters the buffer holds the modifiable characters.
     * See {@link #firstModifiableCharacterInBuffer}.
     */
    private StringBuilder characterBuffer;


    /**
     * The position of the first character in the {@link #characterBuffer input
     * buffer} that is modifiable.
     * <p>
     * This character and the following characters in {@link #characterBuffer}
     * can be modified by the {@link #modifier}.
     */
    private int firstModifiableCharacterInBuffer;


    /**
     * The value is taken from
     * {@link AfterModification#getNewMinimumLengthOfLookBehind()} after
     * {@link Modifier#modify(StringBuilder, int, boolean)} is called. The value
     * is <em>requested</em> by the {@link Modifier}.
     * <p>
     * If this value is greater than {@link #firstModifiableCharacterInBuffer},
     * the modifier is probably faulty.
     */
    private int minimumLengthOfLookBehind;

    /**
     * The value is the sum of
     * {@link AfterModification#getNewMinimumLengthOfLookBehind()} and
     * {@link AfterModification#getNewNumberOfChars()} after
     * {@link Modifier#modify(StringBuilder, int, boolean)} is called. The value
     * is <em>requested</em> by the {@link Modifier}.
     */
    private int requestedNumCharactersInBuffer;

    /**
     * The value is initially taken from
     * {@link AfterModification#getNumberOfCharactersToSkip()} after
     * {@link Modifier#modify(StringBuilder, int, boolean)} is called. Then
     * during reading characters from the reader this value is decremented.
     */
    private int numberOfCharactersToSkip;

    /**
     * True if the end of stream was detected.
     */
    private boolean endOfStreamHit = false;

    /**
     * The holds the last modification provided by the {@link #modifier}. This
     * property serves debugging purposes only.
     */
    private AfterModification lastAfterModificationForDebuggingOnly = null;

    /**
     * @param writer The underlying writer that provides the original, not
     *        modified characters. For optimal performance choose a
     *        {@link BufferedWriter}.
     * @param modifier
     */
    public ModifyingWriter(Writer writer, Modifier modifier) {
        super(writer);

        // take injected properties
        this.modifier = modifier;
        this.delegate = writer;

        // initialize the mutable state
        this.numberOfCharactersToSkip = 0;
        this.minimumLengthOfLookBehind = 0; // we cannot do anything else
        this.firstModifiableCharacterInBuffer = 0;
        this.requestedNumCharactersInBuffer = 1; // any value
        this.characterBuffer = new StringBuilder(requestedNumCharactersInBuffer);
        // adjustCapacityOfBuffer();
    }

    //
    // interface Writer
    //

    /**
     * @see java.io.Writer#flush()
     */
    @Override
    public void flush() throws IOException {

        // delegate to the underlying writer
        delegate.flush();
    }

    /**
     * @see java.io.Writer#close()
     */
    @Override
    public void close() throws IOException {

        endOfStreamHit = true;

        // modify as long as characters are skipped (modify again makes not
        // much sense but there may some use cases that require modfiyAgain even
        // on the position after the last character)
        while (modify()) {
            // loop
        }

        // write the remaining bytes to the underlying writer
        delegate.append(characterBuffer, firstModifiableCharacterInBuffer,
                characterBuffer.length());

        // clear same variables (only to tidy up)
        characterBuffer = null;

        flush();
    }

    /**
     * @see java.io.Writer#write(char[], int, int)
     * @param off the first character in cbuf not appended to characterBuffer
     *        yet
     * @param len the number of characters in cbuf not appended to //
     *        characterBuffer yet
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {

        // is the stream already closed?
        if (endOfStreamHit) {
            throw new IOException("the stream is already closed");
        }

        // are there some characters to append to the characterBuffer yet?
        while (len > 0) {
            // yes -> append characters

            // determine the number of characters to add to the buffer
            int numberOfCharactersToAppend;
            // are there are more characters available than requested?
            if (len > requestedNumCharactersInBuffer - characterBuffer.length()) {
                // yes -> we won't append more characters than are requested (in
                // order to save memory)
                numberOfCharactersToAppend = requestedNumCharactersInBuffer
                        - characterBuffer.length();
            }
            else {
                // we will append all given characters
                numberOfCharactersToAppend = len;
            }

            // append the characters to the characterBuffer
            characterBuffer.append(cbuf, off, numberOfCharactersToAppend);

            // update the variables offset and length
            off = off + numberOfCharactersToAppend;
            len = len - numberOfCharactersToAppend;

            // modify if there are enough characters in the buffer
            while (characterBuffer.length() >= requestedNumCharactersInBuffer
                    && modify()) {
                // loop
            }

        }
    }

    /**
     * @return Returns true if some characters are skipped, i.e. written to the
     *         underlying writer, or the content of the buffer shall be modified
     *         again immediately.
     * @throws IOException
     */
    private boolean modify() throws IOException {
        AfterModification afterModification = modifier.modify(characterBuffer,
                firstModifiableCharacterInBuffer, endOfStreamHit);

        lastAfterModificationForDebuggingOnly = afterModification;

        numberOfCharactersToSkip = afterModification
                .getNumberOfCharactersToSkip();

        boolean someCharactersSkippedOrModifyAgainImmediately = false;

        if (afterModification.isModifyAgainImmediately()) {

            if (endOfStreamHit) {
                // A modifier might do "modify again" although there is no
                // more input. Assume the modifier want to edit its own
                // insertions -> Therefore, we throw no exception here.
            }

            // nothing to do apart from adjusting capacity and look-behind

            someCharactersSkippedOrModifyAgainImmediately = true;

        }
        else {

            // write away the locked characters
            if (numberOfCharactersToSkip > 0) {
                int end = firstModifiableCharacterInBuffer
                        + numberOfCharactersToSkip;
                if (end > characterBuffer.length()) {
                    onFaultyModifier(-51, String.format("You try to skip "
                            + "characters that you have not"
                            + " seen yet(%s %s %s %s)",
                            firstModifiableCharacterInBuffer,
                            numberOfCharactersToSkip, characterBuffer.length(),
                            characterBuffer));
                }

                delegate.append(characterBuffer,
                        firstModifiableCharacterInBuffer, end);
                someCharactersSkippedOrModifyAgainImmediately = true;
                firstModifiableCharacterInBuffer = end;
            }
            else { // if (numberOfCharactersToSkip == 0)

                // This block is usually entered when the modifier decided to do
                // nothing at the position after the last characters of the
                // stream

            }
        }


        // update minimumLengthOfLookBehind
        minimumLengthOfLookBehind = afterModification
                .getNewMinimumLengthOfLookBehind();
        if (minimumLengthOfLookBehind > firstModifiableCharacterInBuffer) {
            onFaultyModifier(-11, "Requested Look Behind"
                    + " is impossible because there are not enough "
                    + "characters in the stream.");
        }

        // update requestedNumCharactersInBuffer
        requestedNumCharactersInBuffer = minimumLengthOfLookBehind
                + afterModification.getNewNumberOfChars();
        if (requestedNumCharactersInBuffer < minimumLengthOfLookBehind + 1) {
            onFaultyModifier(-13, "Requested Capacity"
                    + " is two small because there must at least one"
                    + " unread characters available after the "
                    + "look behind characters characters in the" + " stream.");
        }

        updateBuffer();

        return someCharactersSkippedOrModifyAgainImmediately;
    }

    /**
     * Updates the input buffer according to {@link #minimumLengthOfLookBehind}
     * and {@link #requestedNumCharactersInBuffer}, and then fills the buffer up
     * to its capacity.
     */
    private void updateBuffer() {

        removeCharactersInBufferNotNeededAnyLonger();

        adjustCapacityOfBuffer();

    }

    /**
     * Deletes those characters at the start of the buffer we do not need any
     * longer.
     */
    private void removeCharactersInBufferNotNeededAnyLonger() {

        int charactersToDelete = firstModifiableCharacterInBuffer
                - minimumLengthOfLookBehind;

        if (charactersToDelete > 0) {
            characterBuffer.delete(0, charactersToDelete);
            firstModifiableCharacterInBuffer -= charactersToDelete;
        }
        else if (charactersToDelete < 0) {
            onFaultyModifier(-52, charactersToDelete
                    + " characters to delete but this is not possible ("
                    + firstModifiableCharacterInBuffer + ","
                    + minimumLengthOfLookBehind + ")");
        }
    }

    /**
     * Adjusts the capacity of the buffer.
     */
    private void adjustCapacityOfBuffer() {

        // is the current capacity not big enough?
        if (characterBuffer.capacity() < requestedNumCharactersInBuffer) {

            // increase the capacity (we delegate to the default behavior of
            // StringBuffer)
            characterBuffer.ensureCapacity(requestedNumCharactersInBuffer);

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

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        // TODO update this method if new properties are added
        StringBuilder builder = new StringBuilder();
        builder.append("ModifyingWriter [\ndelegate=");
        builder.append(delegate);
        builder.append(", \nmodifier=");
        builder.append(modifier);
        builder.append(", \ncharacterBuffer=");
        builder.append(characterBuffer);
        builder.append(", \nnextUnreadCharacterInBuffer=");
        builder.append(firstModifiableCharacterInBuffer);
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

}

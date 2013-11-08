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

package com.googlecode.streamflyer.util;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.internal.thirdparty.ZzzValidate;

/**
 * This class creates {@link AfterModification} instances for the use cases that
 * are often needed by implementations of {@link Modifier}.
 * <p>
 * This class creates {@link AfterModification} instances for the most common
 * use cases, see
 * <ul>
 * <li>{@link #modifyAgainImmediately(int, int)}
 * <li>{@link #fetchMoreInput(int, StringBuilder, int, boolean)}
 * <li>{@link #skipOrStop(int, StringBuilder, int, boolean)}
 * <li>{@link #stop()}
 * </ul>
 * 
 * @author rwoo
 * @since 27.06.2011
 */
public class ModificationFactory {

    private int minimumLengthOfLookBehind;

    private int newNumberOfChars;

    /**
     * To be used by subclasses only.
     */
    protected ModificationFactory() {
        // nothing to do here
    }

    /**
     * @param minimumLengthOfLookBehind
     * @param newNumberOfChars
     */
    public ModificationFactory(int minimumLengthOfLookBehind,
            int newNumberOfChars) {

        ZzzValidate.isGreaterThanZero(newNumberOfChars, "newNumberOfChars");
        ZzzValidate.isZeroOrPositiveNumber(minimumLengthOfLookBehind,
                "minimumLengthOfLookBehind");

        this.minimumLengthOfLookBehind = minimumLengthOfLookBehind;
        this.newNumberOfChars = newNumberOfChars;
    }

    //
    // public methods
    //

    /**
     * @param characterBuffer
     * @param firstModifiableCharacterInBuffer
     * @param endOfStreamHit
     * @return Returns a modification that skips some characters (SKIP), i.e.
     *         marks them as not modifiable, or returns a STOP.
     */
    public AfterModification skipEntireBuffer(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        return skipOrStop(characterBuffer.length()
                - firstModifiableCharacterInBuffer, characterBuffer,
                firstModifiableCharacterInBuffer, endOfStreamHit);
    }


    /**
     * Use this method if you
     * <ul>
     * <li>either want to skip some characters (SKIP) or don't skip any
     * characters as there are no modifiable characters in the buffer left but
     * the end of stream is not hit (SKIP), or
     * <li>don't skip any characters as there are no modifiable characters in
     * the buffer left and the end of stream is hit (STOP).
     * </ul>
     * 
     * @param numberOfCharactersToSkip the number of characters to skip. This
     *        must greater than zero unless the end of the stream is hit or the
     *        buffer does not contain modifiable characters.
     * @param characterBuffer
     * @param firstModifiableCharacterInBuffer
     * @param endOfStreamHit See parameter in
     *        {@link Modifier#modify(StringBuilder, int, boolean)}.
     * @return Returns a modification that skips some characters (SKIP), i.e.
     *         marks them as not modifiable, or returns a STOP.
     */
    public AfterModification skipOrStop(int numberOfCharactersToSkip,
            StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        if (endOfStreamHit
                && characterBuffer.length() - firstModifiableCharacterInBuffer == 0) {

            ZzzValidate.isTrue(numberOfCharactersToSkip == 0,
                    "numberOfCharactersToSkip must be zero");

            return stop(characterBuffer, firstModifiableCharacterInBuffer,
                    endOfStreamHit);
        }
        else {
            return skip(numberOfCharactersToSkip, characterBuffer,
                    firstModifiableCharacterInBuffer, endOfStreamHit);
        }
    }

    /**
     * Skips some characters if there are some in the buffer. This method
     * requires that are modifiable characters somewhere in the stream.
     * 
     * @param numberOfCharactersToSkip the number of characters to skip. This
     *        must greater than zero unless the end of stream is not hit and
     *        there no modifiable characters in the buffer.
     * @param characterBuffer
     * @param firstModifiableCharacterInBuffer
     * @param endOfStreamHit
     * @return Returns a modification that skips some characters (SKIP).
     */
    public AfterModification skip(int numberOfCharactersToSkip,
            StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        if (numberOfCharactersToSkip == 0) {
            if (characterBuffer.length() - firstModifiableCharacterInBuffer == 0
                    && !endOfStreamHit) {
                // OK, buffer is empty but the stream is not empty yet.
            }
            else {
                String msg = String.format(
                        "Probably a programming error. Therefore, "
                                + "we don't fix it automatically."
                                + " (%s, %s, %s, %s)", //
                        characterBuffer, firstModifiableCharacterInBuffer,
                        characterBuffer.length(), endOfStreamHit);
                // System.out.println(msg);
                throw new IllegalStateException(msg);
            }
        }

        // (1) check numberOfCharactersToSkip
        int numberOfModifiableCharactersInBuffer = characterBuffer.length()
                - firstModifiableCharacterInBuffer;
        if (numberOfCharactersToSkip > numberOfModifiableCharactersInBuffer) {
            String msg = String
                    .format("Probably a programming error. Therefore, "
                            + "we don't fix it automatically. (%s, %s, %s, %s)",
                            characterBuffer, firstModifiableCharacterInBuffer,
                            numberOfCharactersToSkip, characterBuffer.length());
            // System.out.println(msg);
            throw new IllegalStateException(msg);
        }


        // (2) adjust minimumLengthOfLookBehind
        int minimumLengthOfLookBehind_ = minimumLengthOfLookBehind;
        if (minimumLengthOfLookBehind_ > firstModifiableCharacterInBuffer
                + numberOfCharactersToSkip) {
            // requested value is too big -> adjust
            minimumLengthOfLookBehind_ = firstModifiableCharacterInBuffer
                    + numberOfCharactersToSkip;
        }

        // (3) adjust numberOfChars (nothing to do)
        int newNumberOfChars_ = this.newNumberOfChars;

        // (4) create and return the modification
        return new AfterModification(numberOfCharactersToSkip, false,
                minimumLengthOfLookBehind_, newNumberOfChars_);
    }

    /**
     * This method
     * <ul>
     * <li>skips the given characters to use the character buffer as optimal as
     * possible,
     * <li>checks whether skipping and the current value of
     * {@link AfterModification#getNewNumberOfChars()} would fetch more input in
     * the character buffer. If not then
     * {@link AfterModification#getNewNumberOfChars()} is doubled until the
     * number would fetch more input in the character buffer.
     * </ul>
     * 
     * @param numberOfCharactersToSkip
     * @param characterBuffer
     * @param firstModifiableCharacterInBuffer
     * @param endOfStreamHit
     * @return Returns a SKIP or MODIFY AGAIN IMMEDIATELY.
     */
    public AfterModification fetchMoreInput(int numberOfCharactersToSkip,
            StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        ZzzValidate.isTrue(!endOfStreamHit, "endOfStreamHit must not be false");

        // (1) check numberOfCharactersToSkip
        int numberOfModifiableCharactersInBuffer = characterBuffer.length()
                - firstModifiableCharacterInBuffer;
        if (numberOfCharactersToSkip > numberOfModifiableCharactersInBuffer) {
            String msg = String
                    .format("Probably a programming error. Therefore, "
                            + "we don't fix it automatically. (%s, %s, %s, %s)",
                            characterBuffer, firstModifiableCharacterInBuffer,
                            numberOfCharactersToSkip, characterBuffer.length());
            // System.out.println(msg);
            throw new IllegalStateException(msg);
        }

        // (2) adjust minimumLengthOfLookBehind
        int minimumLengthOfLookBehind_ = this.minimumLengthOfLookBehind;
        if (minimumLengthOfLookBehind_ > firstModifiableCharacterInBuffer
                + numberOfCharactersToSkip) {
            // requested value is too big -> adjust
            minimumLengthOfLookBehind_ = firstModifiableCharacterInBuffer
                    + numberOfCharactersToSkip;
        }

        // (3) adjust newNumberOfChars
        // Can we get automatically more input by skipping
        // the characters? Or - rephrased - does the
        // current character buffer already provide
        // more characters than requested?
        int newNumberOfChars_ = this.newNumberOfChars;
        int modifiableCharsInBufferAfterSkip = numberOfModifiableCharactersInBuffer
                - numberOfCharactersToSkip;
        while (newNumberOfChars_ <= modifiableCharsInBufferAfterSkip) {
            // yes, by skipping the characters and
            // request the standard number of characters
            // (newNumberOfChars) we would not see more
            // input -> request more input
            newNumberOfChars_ *= 2;
        }

        // (4) choose appropriate modification
        if (numberOfCharactersToSkip > 0) {

            // we skip some characters - and try to match again then
            return new AfterModification(numberOfCharactersToSkip, false,
                    minimumLengthOfLookBehind_, newNumberOfChars_);
        }
        else {
            // modify again immediately
            return new AfterModification(0, true, //
                    minimumLengthOfLookBehind_, newNumberOfChars_);
        }
    }

    /**
     * Use this method if you want to request another number of characters from
     * the {@link ModifyingReader} and re-try a modification (MODIFY AGAIN
     * IMMEDIATELY).
     * 
     * @return Returns a modification that requests another number of characters
     *         from the {@link ModifyingReader} and requests a call to
     *         {@link Modifier#modify(StringBuilder, int, boolean)} via
     *         {@link AfterModification#isModifyAgainImmediately()}.
     */
    public AfterModification modifyAgainImmediately(int newNumberOfChars_,
            int firstModifiableCharacterInBuffer) {

        return new AfterModification(0, true, firstModifiableCharacterInBuffer,
                newNumberOfChars_);
    }

    /**
     * @param characterBuffer
     * @param firstModifiableCharacterInBuffer
     * @param endOfStreamHit
     * @return Returns an {@link AfterModification} that is appropriate if the
     *         end of stream is hit and there are no more modifiable characters
     *         and you don't want to add characters at the end of the stream. If
     *         there are still modifiable characters in the stream yet, then
     *         this method will throw an exception.
     */
    public AfterModification stop(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        // validate arguments
        if (characterBuffer.length() - firstModifiableCharacterInBuffer != 0
                || !endOfStreamHit) {
            String msg = String.format(
                    "Probably a programming error. Therefore, "
                            + "we don't fix it automatically."
                            + " (%s, %s, %s, %s)", //
                    characterBuffer, firstModifiableCharacterInBuffer,
                    characterBuffer.length(), endOfStreamHit);
            // System.out.println(msg);
            throw new IllegalStateException(msg);
        }

        return new AfterModification(0, false, 0, 1); // 1 <- doesn't matter
    }

    //
    // getter methods
    //

    /**
     * @return Returns the {@link #newNumberOfChars}.
     */
    public int getNewNumberOfChars() {
        return newNumberOfChars;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ModificationFactory [\nminimumLengthOfLookBehind=");
        builder.append(minimumLengthOfLookBehind);
        builder.append(", \nnewNumberOfChars=");
        builder.append(newNumberOfChars);
        builder.append("]");
        return builder.toString();
    }

}

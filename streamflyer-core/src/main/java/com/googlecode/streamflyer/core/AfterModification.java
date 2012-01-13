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

import com.googlecode.streamflyer.thirdparty.ZzzValidate;


/**
 * This class is a message from the modifier to the stream processor, or
 * technically spoken, this class represents the return value of
 * {@link Modifier#modify(StringBuilder, int, boolean)}. This return value
 * defines how the stream processor, i. e. either a {@link ModifyingReader} or a
 * {@link ModifyingWriter}, shall behave before calling
 * {@link Modifier#modify(StringBuilder, int, boolean)} again.
 * <p>
 * Therefore, this class defines
 * <ul>
 * <li>how many {@link AfterModification#getNewNumberOfChars() modifiable
 * characters} the stream processor shall add to the character buffer,
 * <li>how many {@link AfterModification#getNewMinimumLengthOfLookBehind()
 * unmodifiable characters} the stream processor shall keep in the character
 * buffer,
 * <li>{@link AfterModification#isModifyAgainImmediately() whether} modifiable
 * characters shall be shall be made unmodifiable, and if so, how many
 * {@link AfterModification#getNewMinimumLengthOfLookBehind() modifiable
 * characters} shall be shall be made unmodifiable.
 * </ul>
 * <p>
 * {@link AfterModification afterModifications} can divided into three types of
 * messages - depending on how many characters shall be skipped and how many
 * characters are left in the buffer and how many characters are left in the
 * entire stream.
 * <ol>
 * <li>SKIP - Skip at least one modifiable character in the buffer, or do not
 * skip any character if there are no modifiable characters are left in the
 * buffer but the end of stream is not hit yet. A synonym for SKIP could be
 * CONTINUE.
 * <li>MODIFY AGAIN IMMEDIATELY - Do not skip any character. This type is useful
 * only if the end of stream is not hit yet and more
 * {@link #getNewNumberOfChars() input} is requested.
 * <li>STOP - Do not skip any character - This requires that the end of stream
 * is hit and no modifiable characters are left in the buffer.
 * </ol>
 * 
 * @author rwoo
 * 
 * @since 03.06.2011
 */
public class AfterModification {


    //
    // injected
    //

    /**
     * See {@link ModifyingReader#newMinimumLengthOfLookBehind}.
     * <p>
     * The value of this property must be a non-negative number.
     */
    private int newMinimumLengthOfLookBehind;

    /**
     * See {@link #getNewNumberOfChars()}
     */
    private int newNumberOfChars;


    /**
     * See {@link #getNumberOfCharactersToSkip()}
     */
    private int numberOfCharactersToSkip;

    /**
     * See {@link #isModifyAgainImmediately()}.
     */
    private boolean modifyAgainImmediately;


    //
    // constructors
    //

    /**
     * @param numberOfCharactersToSkip See
     *        {@link #getNumberOfCharactersToSkip()}
     * @param modifyAgainImmediately See {@link #isModifyAgainImmediately()}
     * @param newMinimumLengthOfLookBehind See
     *        {@link #getNewMinimumLengthOfLookBehind()}
     * @param newNumberOfChars See {@link #getNewNumberOfChars()}
     */
    public AfterModification(int numberOfCharactersToSkip,
            boolean modifyAgainImmediately, int newMinimumLengthOfLookBehind,
            int newNumberOfChars) {

        init(numberOfCharactersToSkip, modifyAgainImmediately,
                newMinimumLengthOfLookBehind, newNumberOfChars);

    }

    protected void init(int numberOfCharactersToSkip_,
            boolean modifyAgainImmediately_, int newMinimumLengthOfLookBehind_,
            int newNumberOfChars_) {

        ZzzValidate.isZeroOrPositiveNumber(numberOfCharactersToSkip_,
                "numberOfCharactersToSkip");
        ZzzValidate.isZeroOrPositiveNumber(newMinimumLengthOfLookBehind_,
                "minimumLengthOfLookBehind");
        // (newNumberOfChars should not be zero if end of stream is
        // not hit but we cannot check this constraint here as in this method we
        // don't know whether the end of stream is hit.)
        ZzzValidate.isZeroOrPositiveNumber(newNumberOfChars_,
                "newNumberOfChars_");

        if (modifyAgainImmediately && numberOfCharactersToSkip_ != 0) {
            throw new IllegalArgumentException("if modify again immediately,"
                    + " the number of characters to skip are ignored .. "
                    + "so why is the number of characters to skip "
                    + numberOfCharactersToSkip_ + " instead of zero?");
        }

        this.numberOfCharactersToSkip = numberOfCharactersToSkip_;
        this.modifyAgainImmediately = modifyAgainImmediately_;
        this.newMinimumLengthOfLookBehind = newMinimumLengthOfLookBehind_;
        this.newNumberOfChars = newNumberOfChars_;
    }

    //
    // getter
    //

    /**
     * @return Returns the {@link #newMinimumLengthOfLookBehind}.
     *         <p>
     *         The value must be a positive number or zero.
     */
    public int getNewMinimumLengthOfLookBehind() {
        return newMinimumLengthOfLookBehind;
    }

    /**
     * @return Returns the number of unmodifiable characters (look-behind) and
     *         modifiable characters in the buffer that shall be passed to
     *         {@link Modifier#modify(StringBuilder, int, boolean)} the next
     *         time this method is called.
     *         <p>
     *         Unless the end of the stream is hit, {@link ModifyingReader} and
     *         {@link ModifyingWriter} do not provide fewer characters to the
     *         modifier than requested by the value returned here.
     *         <p>
     *         The value must be a positive number greater than zero.
     */
    public int getNewNumberOfChars() {
        return newNumberOfChars;
    }

    /**
     * @return Returns true if the modifier shall be called again immediately.
     *         <p>
     *         This is usually true either if we want to edit the insertion
     *         itself or if the modifier has to read more characters to decide
     *         again about an appropriate modification. The latter one is
     *         particularly useful for modifiers that use (greedy) regular
     *         expressions.
     *         <p>
     *         See also {@link #getNumberOfCharactersToSkip()}.
     */
    public boolean isModifyAgainImmediately() {
        return modifyAgainImmediately;
    }

    /**
     * @return Returns the number of characters to skip if the modification
     *         shall no be tried again, i.e. if
     *         {@link #isModifyAgainImmediately()} is false. In this case this
     *         number must be at least one unless the end of the stream is hit.
     *         <p>
     *         After the modifiable characters are investigated by the modifier
     *         and some characters might be deleted or inserted, you have to
     *         specify the number of characters that are no longer considered
     *         modifiable. If the end of the stream is not hit and
     *         {@link #isModifyAgainImmediately()} returns false, then this
     *         method must return at least 1 -- so that the processing of the
     *         characters of the stream makes any progress.
     *         <p>
     *         In all cases the value of this property must be a non-negative
     *         number.
     *         <p>
     *         This property will be ignored by {@link ModifyingReader} and
     *         {@link ModifyingWriter} if {@link #isModifyAgainImmediately()}
     *         returns true.
     */
    public int getNumberOfCharactersToSkip() {
        return numberOfCharactersToSkip;
    }

    /**
     * @return Returns an informal description of the type of the message, i.e.
     *         either SKIP or MODIFY AGAIN IMMEDIATELY or STOP.
     */
    public String getMessageType() {
        if (numberOfCharactersToSkip != 0) {
            return "SKIP";
        }
        else if (modifyAgainImmediately) {
            return "MODIFY AGAIN IMMEDIATELY";
        }
        else {
            return "STOP";
        }
    }


    //
    // Object.*
    //

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AfterModification [\nnewMinimumLengthOfLookBehind=");
        builder.append(newMinimumLengthOfLookBehind);
        builder.append(", \nnewNumberOfModifiableChars=");
        builder.append(newNumberOfChars);
        builder.append(", \nnumberOfCharactersToSkip=");
        builder.append(numberOfCharactersToSkip);
        builder.append(", \nmodifyAgainImmediately=");
        builder.append(modifyAgainImmediately);
        builder.append("]");
        return builder.toString();
    }
}
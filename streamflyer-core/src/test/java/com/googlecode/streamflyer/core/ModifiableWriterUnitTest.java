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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.output.NullWriter;
import org.junit.Test;

/**
 * Tests {@link ModifyingWriter}.
 * 
 * @author rwoo
 * @since 17.06.2011
 */
public class ModifiableWriterUnitTest {

    @Test
    public void testCharacterBufferDoesNotExceedRequestedLength() throws Exception {

        assertCharacterBufferDoesNotExceedRequestedLength(0, 7, 5, 100000, 30000);
        assertCharacterBufferDoesNotExceedRequestedLength(0, 5, 5, 100000, 30000);

        assertCharacterBufferDoesNotExceedRequestedLength(3, 7, 5, 100000, 30000);
        assertCharacterBufferDoesNotExceedRequestedLength(3, 5, 5, 100000, 30000);
    }

    /**
     * Asserts that the content of the character buffer is never bigger than the newNumberOfChars plus the look-behind.
     * Additionally asserts that the capacity of the character buffer does not exceed twice the size of
     * newNumberOfChars.
     * 
     * @param minimumLengthOfLookBehind
     * @param newNumberOfChars
     * @param numberOfCharactersToSkip
     * @param sizeOfInput
     * @param bufferSize
     * @throws Exception
     */
    private void assertCharacterBufferDoesNotExceedRequestedLength(int minimumLengthOfLookBehind, int newNumberOfChars,
            int numberOfCharactersToSkip, int sizeOfInput, int bufferSize) throws Exception {

        // setup: create modifier and reader
        IdleModifier modifier = new IdleModifier(minimumLengthOfLookBehind, newNumberOfChars, numberOfCharactersToSkip);
        ModifyingWriter writer = new ModifyingWriter(new NullWriter(), modifier);
        Writer bufferedWriter = new BufferedWriter(writer, bufferSize);

        // read the stream into an output stream
        for (int index = 0; index < sizeOfInput; index++) {
            bufferedWriter.append('a');
        }
        writer.flush();
        writer.close();

        assertTrue("max requested length of character buffer "
                + modifier.getFactory().getMaxRequestedNewNumberOfChars() + " should be smaller than "
                + newNumberOfChars + " but was not",
                modifier.getFactory().getMaxRequestedNewNumberOfChars() <= newNumberOfChars);

        assertTrue("max size of character buffer " + modifier.getMaxSizeOfCharacterBuffer()
                + " should be smaller than " + (minimumLengthOfLookBehind + newNumberOfChars) + " but was not",
                modifier.getMaxSizeOfCharacterBuffer() <= minimumLengthOfLookBehind + newNumberOfChars);

        // as we know that StringBuilder.ensureCapacity(int) doubles the input
        // size if appropriate, we test for (look-behind + numChars) * 2
        assertTrue("max capacity of character buffer " + modifier.getMaxCapacityOfCharacterBuffer()
                + " should be smaller than " + (minimumLengthOfLookBehind + newNumberOfChars) * 2 + " but was not",
                modifier.getMaxCapacityOfCharacterBuffer() <= (minimumLengthOfLookBehind + newNumberOfChars) * 2);
    }

    @Test
    public void testCloseUnderlyingWriter() throws Exception {

        // given
        NullWriter underlyingWriter = mock(NullWriter.class);
        ModifyingWriter writer = new ModifyingWriter(underlyingWriter, new IdleModifier());

        // when
        writer.append("some text");

        // then
        verify(underlyingWriter, never()).close();

        // when
        writer.flush();

        // then
        verify(underlyingWriter, never()).close();

        // when
        writer.close();

        // then
        verify(underlyingWriter).close();

        // when
        writer.close();

        // then
        // calling close() a second time must not have an effect

        // when
        try {
            writer.write("anything");
            fail(IOException.class.getSimpleName() + " expected");
        } catch (IOException e) {
            // then expect IOException
        }
    }

}

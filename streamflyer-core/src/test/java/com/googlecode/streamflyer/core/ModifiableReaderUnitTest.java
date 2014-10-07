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

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;

/**
 * Tests {@link ModifyingReader}.
 * 
 * @author rwoo
 * @since 17.06.2011
 */
public class ModifiableReaderUnitTest {

    /**
     * Well, this is not particularly useful...
     * 
     * @throws Exception
     */
    @Test
    public void testInsertNoCharacters() throws Exception {
        assertModification("abcd", "abcd", 1, insertion(""));
        assertModification("abcd", "abcd", 0, insertion(""));
        assertModification("abcd", "abcd", 4, insertion(""));
    }

    @Test
    public void testInsertTwoCharacters() throws Exception {
        assertModification("abcd", "a<xy>bcd", 1, insertion("<xy>"));
        assertModification("abcd", "<xy>abcd", 0, insertion("<xy>"));
        assertModification("abcd", "abcd<xy>", 4, insertion("<xy>"));
    }

    @Test
    public void testInsertManyCharacters() throws Exception {
        assertModification("abcd", "a<xy12345678901234567890>bcd", 1, insertion("<xy12345678901234567890>"));
    }

    @Test
    public void testDeleteTwoCharacters() throws Exception {
        assertModification("abcd", "ad", 1, deletion(2));
        assertModification("abcd", "cd", 0, deletion(2));
    }

    @Test
    public void testDeleteMoreCharactersThanAreAvailableInStream() throws Exception {

        assertFaultyModifierException("abcd", 4, deletion(2), "End of stream"
                + " is hit but the modification requested more characters to "
                + "delete than are available in the stream.", "endOfStreamHit=true");

        assertFaultyModifierException("abcd", 3, deletion(2), "End of stream"
                + " is hit but the modification requested more characters to "
                + "delete than are available in the stream.", "endOfStreamHit=true");
    }

    @Test
    public void testReplaceTwoCharactersWithFourCharacters() throws Exception {
        assertModification("abcd", "a<xy>d", 1, replace("<xy>", 2));
        assertModification("abcd", "<xy>cd", 0, replace("<xy>", 2));
        assertModification("abcd", "abcd<xy>", 4, replace("<xy>", 0));
    }

    //
    // helper methods
    //

    private Change insertion(String insertion) {
        return new Change(insertion, 0);
    }

    private Change deletion(int numberOfCharactersToDelete) {
        return new Change(null, numberOfCharactersToDelete);
    }

    private Change replace(String insertion, int numberOfCharactersToDelete) {
        return new Change(insertion, numberOfCharactersToDelete);
    }

    /**
     * Asserts that the modified input is equal to the given (expected) output.
     * 
     * @param input
     *            the input that is to modify by the given modification
     * @param expectedOutput
     *            the expected input after the input is modified by the given modification
     * @param positionForModification
     *            the position at that the given modification is to apply
     * @param modificationToApply
     *            the modification to apply
     * @throws Exception
     */
    private void assertModification(String input, String expectedOutput, long positionForModification,
            Change modificationToApply) throws Exception {

        assertModificationByReader(input, expectedOutput, positionForModification, modificationToApply);

        assertModificationByWriter(input, expectedOutput, positionForModification, modificationToApply);

    }

    private void assertModificationByReader(String input, String expectedOutput, long positionForModification,
            Change modificationToApply) throws Exception {

        // setup: create modifier and reader
        Reader reader = createReader(input, positionForModification, modificationToApply);

        // read the stream into an output stream
        String foundOutput = IOUtils.toString(reader);

        // compare the expected result with the found result
        assertEquals(expectedOutput, foundOutput);

    }

    private void assertModificationByWriter(String input, String expectedOutput, long positionForModification,
            Change modificationToApply) throws Exception {

        // setup: create modifier and reader
        StringWriter stringWriter = new StringWriter();
        Writer writer = createWriter(stringWriter, positionForModification, modificationToApply);

        // read the stream into an output stream
        for (int index = 0; index < input.length(); index++) {
            writer.append(input.charAt(index));
        }
        writer.flush();
        writer.close();

        String foundOutput = stringWriter.toString();

        // compare the expected result with the found result
        assertEquals(expectedOutput, foundOutput);
    }

    /**
     * Asserts that the modified input is equal to the given (expected) output.
     * 
     * @param input
     *            the input that is to modify by the given modification
     * @param expectedOutput
     *            the expected input after the input is modified by the given modification
     * @param positionForModification
     *            the position at that the given modification is to apply
     * @param modificationToApply
     *            the modification to apply
     * @throws Exception
     */
    private void assertFaultyModifierException(String input, long positionForModification, Change modificationToApply,
            String... expectedExceptionMessageParts) throws Exception {

        Reader reader = createReader(input, positionForModification, modificationToApply);

        // read the stream into an output stream
        try {
            IOUtils.toString(reader);
            fail("FaultyModifierException expected");
        } catch (FaultyModifierException e) {
            // OK

        }
    }

    /**
     * @param input
     * @param positionForModification
     * @param modificationToApply
     * @return Returns a new reader for the given input so that the input can be modified as defined by the given
     *         modification.
     */
    private Reader createReader(String input, long positionForModification, Change modificationToApply) {

        Map<Long, Change> modifications = new HashMap<Long, Change>();
        modifications.put(positionForModification, modificationToApply);

        PositionOrientedModifier modifier = new PositionOrientedModifier(modifications);
        ModifyingReader reader = new ModifyingReader(new BufferedReader(new StringReader(input)), modifier);

        return reader;
    }

    /**
     * @param stringWriter
     * @param positionForModification
     * @param modificationToApply
     * @return
     */
    private Writer createWriter(StringWriter stringWriter, long positionForModification, Change modificationToApply) {

        Map<Long, Change> modifications = new HashMap<Long, Change>();
        modifications.put(positionForModification, modificationToApply);

        PositionOrientedModifier modifier = new PositionOrientedModifier(modifications);
        ModifyingWriter writer = new ModifyingWriter(stringWriter, modifier);

        return writer;
    }
}

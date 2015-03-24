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
package com.github.rwitzel.streamflyer.experimental.stateful.util;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;

import com.github.rwitzel.streamflyer.core.Modifier;
import com.github.rwitzel.streamflyer.core.ModifyingReader;
import com.github.rwitzel.streamflyer.core.ModifyingWriter;
import com.github.rwitzel.streamflyer.experimental.stateful.State;
import com.github.rwitzel.streamflyer.experimental.stateful.StatefulModifier;
import com.github.rwitzel.streamflyer.experimental.stateful.util.IdleModifierState;
import com.github.rwitzel.streamflyer.util.ModificationFactory;
import com.github.rwitzel.streamflyer.util.StringUtils;

/**
 * Tests the performance (overhead) of the {@link IdleModifier}.
 * 
 * @author rwoo
 * 
 * @since 13.09.2011
 */
public class IdleModifierStatePerformanceTest extends TestCase {

    /**
     * <code><pre>
+++ Measurements +++
Time spent by ModifyingReader: Found seconds 0.424 shall not exceed expected maximum of seconds 1.1 but did exceed
Overhead   by ModifyingReader: Found seconds 0.194 shall not exceed expected maximum of seconds 0.85 but did exceed
Time spent by ModifyingWriter: Found seconds 1.178 shall not exceed expected maximum of seconds 1.25 but did exceed
Overhead   by ModifyingWriter: Found seconds 0.413 shall not exceed expected maximum of seconds 0.5 but did exceed
Summary:
(1) Processing a 10M character stream (using a buffer of 500 characters)
takes roughly
 - 0.4 seconds using a modifying reader (0.2 seconds overhead), 
 - 1.2 seconds using a modifying writer (0.4 seconds overhead).
</pre></code>
     */
    public void testOverheadOfDoingNothing() throws Exception {

        int size = 10 * 1000 * 1000; // (10M characters, i.e. 20MB)
        String input = StringUtils.repeat(" ", size);

        assertOverheadByReader(input, 0.5, 0.2);
        assertOverheadByWriter(input, 1.2, 0.4);
    }

    private void assertOverheadByReader(String input, double expectedMaxSpentTime, double expectedMaxOverhead)
            throws Exception {

        // create reader
        Reader reader = new ModifyingReader(new BufferedReader(new StringReader(input)), createIdleModifier());

        // read the stream into an output stream
        long start = System.currentTimeMillis();
        IOUtils.toString(reader);
        long end = System.currentTimeMillis();

        Reader reader2 = new BufferedReader(new StringReader(input));

        // read the stream into an output stream
        long start2 = System.currentTimeMillis();
        IOUtils.toString(reader2);
        long end2 = System.currentTimeMillis();

        long overhead = (end - start) - (end2 - start2);

        assertTime(end - start, expectedMaxSpentTime, "Time spent by ModifyingReader:");
        assertTime(overhead, expectedMaxOverhead, "Overhead   by ModifyingReader:");
    }

    private void assertOverheadByWriter(String input, double expectedMaxSpentTime, double expectedMaxOverhead)
            throws Exception {

        // setup: create modifier and writer
        Writer originalWriter = new StringWriter();
        ModifyingWriter writer = new ModifyingWriter(originalWriter, createIdleModifier());

        // write the stream to an output stream
        long start = System.currentTimeMillis();
        for (int index = 0; index < input.length(); index++) {
            writer.append(input.charAt(index));
        }
        writer.flush();
        writer.close();
        long end = System.currentTimeMillis();

        originalWriter.toString();

        Writer writer2 = new StringWriter();

        // read the stream into an output stream
        long start2 = System.currentTimeMillis();
        for (int index = 0; index < input.length(); index++) {
            writer2.append(input.charAt(index));
        }
        writer2.flush();
        writer2.close();
        long end2 = System.currentTimeMillis();

        long overhead = (end - start) - (end2 - start2);

        assertTime(end - start, expectedMaxSpentTime, "Time spent by ModifyingWriter:");
        assertTime(overhead, expectedMaxOverhead, "Overhead   by ModifyingWriter:");
    }

    private Modifier createIdleModifier() {
        // create the state for the modifier (there is only a single state)
        // TODO refactor magic number
        State state = new IdleModifierState(new ModificationFactory(0, 500));
        return new StatefulModifier(state);
    }

    /**
     * @param duration
     *            in milliseconds
     * @param expectedMaxSeconds
     * @param callerDescription
     * @throws Exception
     */
    private void assertTime(long duration, double expectedMaxSeconds, String callerDescription) throws Exception {
        double foundSeconds = duration / 1000.0;
        String message = String.format(callerDescription + " Found seconds %s shall not exceed"
                + " expected maximum of seconds %s but did exceed", foundSeconds, expectedMaxSeconds);
        // System.out.println(message);
        assertTrue(message, foundSeconds <= expectedMaxSeconds);
    }
}

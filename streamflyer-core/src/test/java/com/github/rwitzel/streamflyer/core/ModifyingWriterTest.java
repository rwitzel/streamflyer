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

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import org.junit.Test;

import com.github.rwitzel.streamflyer.core.ModifyingWriter;
import com.github.rwitzel.streamflyer.regex.RegexModifier;
import com.github.rwitzel.streamflyer.util.ModifyingWriterFactory;

/**
 * Tests {@link ModifyingWriter}.
 * 
 * @author rwoo
 */
public class ModifyingWriterTest {

    @Test
    public void testClose_closeUnderlyingWriter() throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter originalWriter = new OutputStreamWriter(outputStream, "UTF-8");
        ModifyingWriter modifyingWriter = new ModifyingWriter(originalWriter, new RegexModifier("9999", 0, "1234"));
        modifyingWriter.write("def ");
        modifyingWriter.close();

        // don't write after close to the writer
        catchException(modifyingWriter).write("xyz");
        assertEquals("the stream is already closed", caughtException().getMessage());

        // don't write after close to the underlying writer
        catchException(originalWriter).write("abc");
        assertEquals("Stream closed", caughtException().getMessage());

        String result = new String(outputStream.toByteArray(), "UTF-8");
        assertEquals("def ", result);
    }

    /**
     * In this test two different modifying writers write to an underlying writer.
     * 
     * @throws Exception
     */
    @Test
    public void testClose_doNotCloseUnderlyingWriter() throws Exception {

        StringWriter originalWriter = new StringWriter();

        // write to the underlying writer
        originalWriter.write("abc ");
        originalWriter.flush(); // make sure everything is written

        // add part 1
        ModifyingWriter modifyingWriterPart1 = new ModifyingWriter(originalWriter, new RegexModifier("9999", 0, "1234"));
        modifyingWriterPart1.write("def ");
        modifyingWriterPart1.close(false);

        // write to the underlying writer
        originalWriter.write("ghi ");
        originalWriter.flush();

        // add part 2
        ModifyingWriter modifyingWriterPart2 = new ModifyingWriterFactory().createRegexModifyingWriter(originalWriter,
                "9999", "1234");
        modifyingWriterPart2.write("jkl ");
        modifyingWriterPart2.close(false);

        // close the underlying writer
        originalWriter.close();

        assertEquals("abc def ghi jkl ", originalWriter.getBuffer().toString());
    }

}

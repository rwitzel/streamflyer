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
package com.googlecode.streamflyer.experimental.bytestream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.io.output.WriterOutputStream;

import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.core.ModifyingWriter;
import com.googlecode.streamflyer.regex.RegexModifier;

/**
 * @author rwoo
 */
public class ByteStreamTest extends TestCase {

    private byte[] createBytes() {
        byte[] bytes = new byte[256];
        byte value = -128;
        for (int index = 0; index < bytes.length; index++) {
            bytes[index] = value;
            value++;
        }
        return bytes;
    }

    private void assertBytes(byte[] original, byte[] converted, boolean differenceExpected) throws Exception {

        if (original.length != converted.length) {
            if (differenceExpected) {
                // OK, we got our difference
                return;
            } else {
                // this will always fail
                assertEquals(original.length, converted.length);
            }
        }

        int conversionErrors = 0;
        for (int index = 0; index < original.length; index++) {

            if (original[index] != converted[index]) {
                conversionErrors++;
            }
        }

        assertEquals(differenceExpected, conversionErrors > 0);
    }

    public void testConvert_utf8_errors() throws Exception {
        assertInputConversion_viaCharsetName("UTF-8", true);
        assertInputConversion_viaCharsetDecoder("UTF-8", true);
        // ArrayIndexOutOfBoundException
        // assertOutputConversion_viaCharsetName("UTF-8", true);
        // assertOutputConversion_viaCharsetEncoder("UTF-8", true);
    }

    public void testConvert_ascii_errors() throws Exception {
        // uh, the test does not finish when the called with charset name
        // assertInputConversion_viaCharsetName("ASCII", true);
        assertInputConversion_viaCharsetDecoder("ASCII", true);
        assertOutputConversion_viaCharsetName("ASCII", true);
        // uh, the charset encoding cannot be configured properly
        // assertOutputConversion_viaCharsetEncoder("ASCII", true);
    }

    public void testConvert_utf16e_errors() throws Exception {
        assertInputConversion_viaCharsetName("UTF-16", true);
        assertInputConversion_viaCharsetDecoder("UTF-16", true);
        assertOutputConversion_viaCharsetName("UTF-16", true);
        // assertOutputConversion_viaCharsetEncoder("UTF-16", true);
    }

    public void testConvert_iso88591_NoErrors() throws Exception {
        assertConvert("ISO-8859-1", false);
    }

    private void assertConvert(String charsetName, boolean conversionErrorsExpected) throws Exception {
        assertInputConversion_viaCharsetName(charsetName, conversionErrorsExpected);
        assertInputConversion_viaCharsetDecoder(charsetName, conversionErrorsExpected);
        assertOutputConversion_viaCharsetName(charsetName, conversionErrorsExpected);
        assertOutputConversion_viaCharsetEncoder(charsetName, conversionErrorsExpected);
    }

    private void assertOutputConversion_viaCharsetName(String charsetName, boolean conversionErrorsExpected)
            throws Exception {

        byte[] originalBytes = createBytes();

        {
            // byte array as byte stream
            ByteArrayOutputStream targetByteStream = new ByteArrayOutputStream();
            // byte stream as character stream
            Writer targetWriter = new OutputStreamWriter(targetByteStream, charsetName);
            // modifying writer (we don't modify here)
            Writer modifyingWriter = new ModifyingWriter(targetWriter, new RegexModifier("a", 0, "a"));
            // character stream as byte stream
            OutputStream modifyingByteStream = new WriterOutputStream(modifyingWriter, charsetName);
            // byte stream as byte array
            IOUtils.write(originalBytes, modifyingByteStream);
            modifyingByteStream.close();

            assertBytes(originalBytes, targetByteStream.toByteArray(), conversionErrorsExpected);
        }
    }

    private void assertOutputConversion_viaCharsetEncoder(String charsetName, boolean conversionErrorsExpected)
            throws Exception {

        // find charset
        Charset charset = Charset.forName(charsetName);

        // // configure decoder
        // CharsetDecoder decoder = charset.newDecoder();
        // decoder.onUnmappableCharacter(CodingErrorAction.REPORT);

        // configure encoder
        CharsetEncoder encoder = charset.newEncoder();
        encoder.onUnmappableCharacter(CodingErrorAction.REPORT);

        byte[] originalBytes = createBytes();
        boolean conversionErrorsFound;
        try {
            // byte array as byte stream
            ByteArrayOutputStream targetByteStream = new ByteArrayOutputStream();
            // byte stream as character stream
            Writer targetWriter = new OutputStreamWriter(targetByteStream, encoder);
            // modifying writer (we don't modify here)
            Writer modifyingWriter = new ModifyingWriter(targetWriter, new RegexModifier("a", 0, "a"));
            // character stream as byte stream
            OutputStream modifyingByteStream = new WriterOutputStream(modifyingWriter, charset); // encoder
                                                                                                 // not
                                                                                                 // supported
                                                                                                 // here!!!
            // byte stream as byte array
            IOUtils.write(originalBytes, modifyingByteStream);
            modifyingByteStream.close();

            assertBytes(originalBytes, targetByteStream.toByteArray(), conversionErrorsExpected);

            conversionErrorsFound = false;
        } catch (MalformedInputException e) {
            conversionErrorsFound = true;
        }
        assertEquals(conversionErrorsExpected, conversionErrorsFound);
    }

    private void assertInputConversion_viaCharsetName(String charsetName, boolean conversionErrorsExpected)
            throws Exception {

        byte[] originalBytes = createBytes();

        {
            // byte array as byte stream
            InputStream originalByteStream = new ByteArrayInputStream(originalBytes);
            // byte stream as character stream
            Reader originalReader = new InputStreamReader(originalByteStream, charsetName);
            // modifying reader (we don't modify here)
            Reader modifyingReader = new ModifyingReader(originalReader, new RegexModifier("a", 0, "a"));
            // character stream as byte stream
            InputStream modifyingByteStream = new ReaderInputStream(modifyingReader, charsetName);
            // byte stream as byte array
            byte[] modifiedBytes = IOUtils.toByteArray(modifyingByteStream);

            assertBytes(originalBytes, modifiedBytes, conversionErrorsExpected);
        }
    }

    private void assertInputConversion_viaCharsetDecoder(String charsetName, boolean conversionErrorsExpected)
            throws Exception {

        // find charset
        Charset charset = Charset.forName(charsetName);

        // configure decoder
        CharsetDecoder decoder = charset.newDecoder();
        decoder.onUnmappableCharacter(CodingErrorAction.REPORT);

        // // configure encoder
        // CharsetEncoder encoder = charset.newEncoder();
        // encoder.onUnmappableCharacter(CodingErrorAction.REPORT);

        byte[] originalBytes = createBytes();

        boolean conversionErrorsFound;
        try {
            // byte array as byte stream
            InputStream originalByteStream = new ByteArrayInputStream(originalBytes);
            // byte stream as character stream
            Reader originalReader = new InputStreamReader(originalByteStream, decoder);
            // modifying reader (we don't modify anything here)
            Reader modifyingReader = new ModifyingReader(originalReader, new RegexModifier("a", 0, "a"));
            // character stream as byte stream
            InputStream modifyingByteStream = new ReaderInputStream(modifyingReader, charset); // encoder
                                                                                               // not
                                                                                               // supported
            // byte stream as byte array
            byte[] modifiedBytes = IOUtils.toByteArray(modifyingByteStream);

            assertBytes(originalBytes, modifiedBytes, conversionErrorsExpected);

            conversionErrorsFound = false;
        } catch (MalformedInputException e) {
            conversionErrorsFound = true;
        }
        assertEquals(conversionErrorsExpected, conversionErrorsFound);
    }

    public void testHomepageExample_InputStream() throws Exception {

        String charsetName = "ISO-8859-1";

        byte[] originalBytes = new byte[] { 1, 2, "\r".getBytes("ISO-8859-1")[0], 4, 5 };

        // get byte stream
        InputStream originalByteStream = new ByteArrayInputStream(originalBytes);

        // byte stream as character stream
        Reader originalReader = new InputStreamReader(originalByteStream, charsetName);

        // create the modifying reader
        Reader modifyingReader = new ModifyingReader(originalReader, new RegexModifier("\r", 0, ""));

        // character stream as byte stream
        InputStream modifyingByteStream = new ReaderInputStream(modifyingReader, charsetName);

        byte[] expectedBytes = new byte[] { 1, 2, 4, 5 };

        assertBytes(expectedBytes, IOUtils.toByteArray(modifyingByteStream), false);
    }

    public void testHomepageExample_OutputStream() throws Exception {

        String charsetName = "ISO-8859-1";

        byte[] originalBytes = new byte[] { 1, 2, "\r".getBytes("ISO-8859-1")[0], 4, 5 };

        // get byte stream
        ByteArrayOutputStream targetByteStream = new ByteArrayOutputStream();

        // byte stream as character stream
        Writer targetWriter = new OutputStreamWriter(targetByteStream, charsetName);

        // create the modifying writer
        Writer modifyingWriter = new ModifyingWriter(targetWriter, new RegexModifier("\r", 0, ""));

        // character stream as byte stream
        OutputStream modifyingByteStream = new WriterOutputStream(modifyingWriter, charsetName);

        modifyingByteStream.write(originalBytes);
        // modifyingByteStream.flush();
        modifyingByteStream.close();

        byte[] expectedBytes = new byte[] { 1, 2, 4, 5 };

        assertBytes(expectedBytes, targetByteStream.toByteArray(), false);
    }

}

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
package com.github.rwitzel.streamflyer.support;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.junit.Test;

import com.github.rwitzel.streamflyer.core.ModifyingReader;
import com.github.rwitzel.streamflyer.regex.RegexModifier;
import com.github.rwitzel.streamflyer.util.StringUtils;
import com.googlecode.streamflyer.regex.fast.FastRegexModifier;

public class ProcessEndOfStreamTest {

    @Test
    public void test2013_14_03_ProcessEndOfStream_beAwareOfFlush() throws Exception {
        assertTrue(rewriteContent(true, "http://mydomain/"));
        assertFalse(rewriteContent(false, "http://mydomain/"));
    }

    /**
     * @param flush
     * @return Returns true if the actual output is equals to the expected
     *         output.
     * @throws Exception
     */
    private boolean rewriteContent(boolean flush, String domainPrefix) throws Exception {
        String contentPart = StringUtils.repeat("text", 2);
        String oldUrl = domainPrefix + "something";
        String expectedNewUrl = domainPrefix + "anything";
        String oldHtml = "<html><body>" + contentPart + oldUrl + contentPart + "</body></html>";
        String expectedNewHtml = "<html><body>" + contentPart + expectedNewUrl + contentPart + "</body></html>";
        String encoding = "UTF-8";

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        long written = rewriteContent(new ReaderInputStream(new StringReader(oldHtml)), os, encoding, flush);
        System.out.println("written: " + written);
        System.out.println("oldHtml.length(): " + oldHtml.length());
        System.out.println("expectedNewHtml.length(): " + expectedNewHtml.length());
        System.out.println("expectedNewHtml: \n" + expectedNewHtml);
        os.flush();
        String newHtml = new String(os.toByteArray(), encoding);
        System.out.println(newHtml);
        return expectedNewHtml.equals(newHtml);
    }

    protected long rewriteContent(InputStream input, OutputStream output, String encoding, boolean flush)
            throws IOException {

        Charset charset = Charset.forName(encoding);
        String oldPath = "something";
        String newPath = "anything";
        String regex = "((https?://)([^/]+/))?(" + oldPath + ")";
        String replacement = "$1" + newPath;
//        FastRegexModifier modifier = new FastRegexModifier(regex, Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ,
//                replacement);
        RegexModifier modifier = new RegexModifier(regex, Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ,
                replacement);

        Reader reader = new ModifyingReader(new InputStreamReader(input, charset), modifier);
        Writer writer = new OutputStreamWriter(output, charset);

        int copied = IOUtils.copy(reader, writer);

        if (flush) {
            writer.flush();
        }

        return copied;
    }
}

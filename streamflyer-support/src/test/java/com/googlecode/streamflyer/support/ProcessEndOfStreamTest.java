package com.googlecode.streamflyer.support;

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

import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.regex.fast.FastRegexModifier;
import com.googlecode.streamflyer.util.StringUtils;

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
        FastRegexModifier modifier = new FastRegexModifier(regex, Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ,
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

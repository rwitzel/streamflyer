package com.github.rwitzel.streamflyer.support;


import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import org.junit.Test;

import com.github.rwitzel.streamflyer.core.ModifyingWriter;
import com.github.rwitzel.streamflyer.util.ModifyingWriterFactory;

/**
 * See <a href="https://groups.google.com/forum/#!topic/streamflyer-discuss/ScxMu_bKpvA">Using multiple writers to
 * process multipart responses</a>.
 * 
 * @author rwitzel
 *
 */
public class MultiPartResponseTest {
    
    @Test
    public void testWritingMultipartResponse() throws Exception {
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter originalWriter = new OutputStreamWriter(outputStream, "UTF-8");

        originalWriter.flush(); // make sure everything is written

        // write part 1
        ModifyingWriter modifyingWriterPart1 =
                new ModifyingWriterFactory().createRegexModifyingWriter(originalWriter, "abcd", "1234");
        modifyingWriterPart1.write("mod ");
        modifyingWriterPart1.close(false);

        // write to the underlying writer
        originalWriter.write("orig ");
        originalWriter.flush();

        // write part 2
        ModifyingWriter modifyingWriterPart2 =
                new ModifyingWriterFactory().createRegexModifyingWriter(originalWriter, "abcd", "1234");
        modifyingWriterPart2.write("mod ");
        modifyingWriterPart2.close(false);

        originalWriter.close();
        
        String result = new String(outputStream.toByteArray(), "UTF-8");
        assertEquals("mod orig mod ", result);
    }

}

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

	private void assertBytes(byte[] original, byte[] converted,
			boolean differenceExpected) throws Exception {

		int conversionErrors = 0;
		for (int index = 0; index < original.length; index++) {

			if (original[index] != converted[index]) {
				System.out.println(index + ": " + original[index] + " <=> "
						+ converted[index]);
				conversionErrors++;
			}
		}

		System.out.println(conversionErrors + " conversionErrors");

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

	private void assertConvert(String charsetName,
			boolean conversionErrorsExpected) throws Exception {
		assertInputConversion_viaCharsetName(charsetName,
				conversionErrorsExpected);
		assertInputConversion_viaCharsetDecoder(charsetName,
				conversionErrorsExpected);
		assertOutputConversion_viaCharsetName(charsetName,
				conversionErrorsExpected);
		assertOutputConversion_viaCharsetEncoder(charsetName,
				conversionErrorsExpected);
	}

	private void assertOutputConversion_viaCharsetName(String charsetName,
			boolean conversionErrorsExpected) throws Exception {
		System.out
				.println("+++ test out: charset name " + charsetName + " +++");

		byte[] originalBytes = createBytes();

		{
			// byte array as byte stream
			ByteArrayOutputStream targetByteStream = new ByteArrayOutputStream();
			// byte stream as character stream
			Writer targetWriter = new OutputStreamWriter(targetByteStream,
					charsetName);
			// modifying writer (we don't modify here)
			Writer modifyingWriter = new ModifyingWriter(targetWriter,
					new RegexModifier("a", 0, "a"));
			// character stream as byte stream
			OutputStream modifyingByteStream = new WriterOutputStream(
					modifyingWriter, charsetName);
			// byte stream as byte array
			IOUtils.write(originalBytes, modifyingByteStream);
			modifyingByteStream.close();

			assertBytes(originalBytes, targetByteStream.toByteArray(),
					conversionErrorsExpected);
		}
	}

	private void assertOutputConversion_viaCharsetEncoder(String charsetName,
			boolean conversionErrorsExpected) throws Exception {
		System.out.println("+++ test out: charset encoder " + charsetName
				+ " +++");

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
			Writer targetWriter = new OutputStreamWriter(targetByteStream,
					encoder);
			// modifying writer (we don't modify here)
			Writer modifyingWriter = new ModifyingWriter(targetWriter,
					new RegexModifier("a", 0, "a"));
			// character stream as byte stream
			OutputStream modifyingByteStream = new WriterOutputStream(
					modifyingWriter, charset); // encoder not supported here!!!
			// byte stream as byte array
			IOUtils.write(originalBytes, modifyingByteStream);
			modifyingByteStream.close();

			assertBytes(originalBytes, targetByteStream.toByteArray(),
					conversionErrorsExpected);

			System.out.println("no conversion error");
			conversionErrorsFound = false;
		} catch (MalformedInputException e) {
			System.out.println("conversion error expected: " + e.getMessage());
			conversionErrorsFound = true;
		}
		assertEquals(conversionErrorsExpected, conversionErrorsFound);
	}

	private void assertInputConversion_viaCharsetName(String charsetName,
			boolean conversionErrorsExpected) throws Exception {
		System.out.println("+++ test in: charset name " + charsetName + " +++");

		byte[] originalBytes = createBytes();

		{
			// byte array as byte stream
			InputStream originalByteStream = new ByteArrayInputStream(
					originalBytes);
			// byte stream as character stream
			Reader originalReader = new InputStreamReader(originalByteStream,
					charsetName);
			// modifying reader (we don't modify here)
			Reader modifyingReader = new ModifyingReader(originalReader,
					new RegexModifier("a", 0, "a"));
			// character stream as byte stream
			InputStream modifyingByteStream = new ReaderInputStream(
					modifyingReader, charsetName);
			// byte stream as byte array
			byte[] modifiedBytes = IOUtils.toByteArray(modifyingByteStream);

			assertBytes(originalBytes, modifiedBytes, conversionErrorsExpected);
		}
	}

	private void assertInputConversion_viaCharsetDecoder(String charsetName,
			boolean conversionErrorsExpected) throws Exception {
		System.out.println("+++ test in: charset decoder" + charsetName
				+ " +++");

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
			InputStream originalByteStream = new ByteArrayInputStream(
					originalBytes);
			// byte stream as character stream
			Reader originalReader = new InputStreamReader(originalByteStream,
					decoder);
			// modifying reader (we don't modify anything here)
			Reader modifyingReader = new ModifyingReader(originalReader,
					new RegexModifier("a", 0, "a"));
			// character stream as byte stream
			InputStream modifyingByteStream = new ReaderInputStream(
					modifyingReader, charset); // encoder not supported
			// byte stream as byte array
			byte[] modifiedBytes = IOUtils.toByteArray(modifyingByteStream);

			assertBytes(originalBytes, modifiedBytes, conversionErrorsExpected);

			System.out.println("no conversion error");
			conversionErrorsFound = false;
		} catch (MalformedInputException e) {
			System.out.println("conversion error expected: " + e.getMessage());
			conversionErrorsFound = true;
		}
		assertEquals(conversionErrorsExpected, conversionErrorsFound);
	}
}

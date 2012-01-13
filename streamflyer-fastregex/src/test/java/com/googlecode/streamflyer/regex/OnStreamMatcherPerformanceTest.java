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

package com.googlecode.streamflyer.regex;

import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.core.ModifyingWriter;
import com.googlecode.streamflyer.experimental.regexj6.OnStreamJava6Matcher;
import com.googlecode.streamflyer.regex.OnStreamMatcher;
import com.googlecode.streamflyer.regex.OnStreamStandardMatcher;
import com.googlecode.streamflyer.regex.RegexModifierWithStatistics;
import com.googlecode.streamflyer.regex.ReplacingProcessor;
import com.googlecode.streamflyer.regex.fast.OnStreamExtendedMatcher;
import com.googlecode.streamflyer.thirdparty.ZzzAssert;

/**
 * Tests the performance of different implementations of {@link OnStreamMatcher}
 * .
 * 
 * @author rwoo
 * @since 23.06.2011
 */
public class OnStreamMatcherPerformanceTest extends TestCase {

	/**
	 * <code><pre>
+++ Measurements +++
OnStreamStandardMatcher (wrapping java.util.regex package):
Time spent by   ModifyingReader: Found seconds 2.211 shall not exceed expected maximum of seconds 2.9 but did exceed
Time spent by   ModifyingWriter: Found seconds 3.038 shall not exceed expected maximum of seconds 3.1 but did exceed

OnStreamJava6Matcher (using reflection on the java.util.regex package):
Time spent by  ModifyingReader: Found seconds 2.335 shall not exceed expected maximum of seconds 3.0 but did exceed
Time spent by  ModifyingWriter: Found seconds 2.46 shall not exceed expected maximum of seconds 3.0 but did exceed

OnStreamExtendedMatcher (using an extended java.util.regex package):
Time spent by  ModifyingReader: Found seconds 0.982 shall not exceed expected maximum of seconds 3.0 but did exceed
Time spent by  ModifyingWriter: Found seconds 1.058 shall not exceed expected maximum of seconds 3.0 but did exceed

Replacement in memory (using String.replaceAll):
Time spent by String.replaceAll: Found seconds 0.613 shall not exceed expected maximum of seconds 3.0 but did exceed

Replacement by Perl (perl 5, version 12, subversion 2 (v5.12.2) built for MSWin32-x86-multi-thread):
$ ./regex-via-perl.sh
It took 31 seconds

Summary:

(1) Replacing strings in a 10M character stream (using a buffer of 500 characters if stream-based),
takes roughly
 - 0.6 seconds (in memory, i.e. not stream based),
 - 0.9 / 1.1 seconds (extended java.util.regex package),
 - 2.2 / 2.6 seconds (using reflection on the java.util.regex package),
 - 2.3 / 3.1 seconds (wrapping java.util.regex package),
 - 30 seconds (by Perl, probably not stream-based, includes writing the result to HD).
 
 This measurement for Perl is not very plausible. Even the output
 differs from the expected one. What went wrong?
 
 A pair of numbers indicates measurement for modifying readers and writers.
 
 (2) Replacing strings in a 10M character stream takes from 20% to 30%
 more time if a modifying writer is used instead of a modifying reader.
</pre></code>
	 */
	public void testPerformanceOfReplacements() throws Exception {

		int size = 10 * 1000 * 1000; // (10M characters, i.e. 20MB)
		assertPerformanceOfReplacements(size, OnStreamStandardMatcher.class,
				2.3, 3.1, 0.7);
		assertPerformanceOfReplacements(size, OnStreamJava6Matcher.class, //
				2.2, 2.6, 0.7);
		assertPerformanceOfReplacements(size, OnStreamExtendedMatcher.class,
				0.9, 1.1, 0.7);
	}

	private void assertPerformanceOfReplacements(
			int numberOfCharactersInStream,
			Class<? extends OnStreamMatcher> onStreamMatcherClass,
			double expectedMaxSpentTimeByModifyingReader,
			double expectedMaxSpentTimeByModifyingWriter,
			double expectedMaxSpentTimeAllInMemory) throws Exception {

		String regex = "<x>(.*?)</x>";
		String replacement = "<y>$1</y>";

		// create matcher
		OnStreamMatcher matcher = createMatcher(onStreamMatcherClass, regex);

		// create modifier
		RegexModifierWithStatistics modifier = new RegexModifierWithStatistics( //
				matcher, //
				new ReplacingProcessor(replacement), //
				0, //
				1000);

		String input = createInput(numberOfCharactersInStream);
		long start = System.currentTimeMillis();
		String expectedOutput = input.replaceAll(regex, replacement);
		long end = System.currentTimeMillis();
		assertTime(end - start, expectedMaxSpentTimeAllInMemory,
				"Time spent by String.replaceAll:");

		// writeFileForComparisonWithPerl(input, expectedOutput);

		assertReplacementByReader(input, modifier, expectedOutput,
				expectedMaxSpentTimeByModifyingReader);
		assertReplacementByWriter(input, modifier, expectedOutput,
				expectedMaxSpentTimeByModifyingWriter);
	}

	private String createInput(int numberOfCharactersInStream) {

		StringBuilder sb = new StringBuilder(numberOfCharactersInStream);
		Random random = new Random();
		random.setSeed(43753658);

		int charsToAppend = 0;
		while (sb.length() < numberOfCharactersInStream - 500) {
			// append either any characters or characters to replace
			if (random.nextBoolean()) {

				// append some characters
				charsToAppend = random.nextInt(400 - 3);
				for (int index = 0; index < charsToAppend; index++) {
					sb.append(' ');
				}

				sb.append("<x>");

				// append some characters
				charsToAppend = random.nextInt(100 - 4);
				for (int index = 0; index < charsToAppend; index++) {
					sb.append(' ');
				}
				sb.append("</x>");
			}
		}

		while (sb.length() < numberOfCharactersInStream) {
			// append some characters
			sb.append(' ');
		}

		ZzzAssert.isTrue(sb.length() == numberOfCharactersInStream);

		return sb.toString();
	}

	private OnStreamMatcher createMatcher(
			Class<? extends OnStreamMatcher> onStreamMatcherClass, String regex) {

		if (onStreamMatcherClass.isAssignableFrom(OnStreamJava6Matcher.class)) {
			Matcher matcher = Pattern.compile(regex).matcher("");
			matcher.useTransparentBounds(true);
			return new OnStreamJava6Matcher(matcher);

		} else if (onStreamMatcherClass
				.isAssignableFrom(OnStreamStandardMatcher.class)) {
			Matcher matcher = Pattern.compile(regex).matcher("");
			matcher.useTransparentBounds(true);
			return new OnStreamStandardMatcher(matcher);
		} else if (onStreamMatcherClass
				.isAssignableFrom(OnStreamExtendedMatcher.class)) {
			com.googlecode.streamflyer.regex.fast.Matcher matcher = com.googlecode.streamflyer.regex.fast.Pattern
					.compile(regex).matcher("");
			matcher.useTransparentBounds(true);
			return new OnStreamExtendedMatcher(matcher);
		} else {
			throw new IllegalArgumentException(String.format(
					"class %s is not supported or of wrong type",
					onStreamMatcherClass));
		}
	}

	private void assertReplacementByReader(String input,
			RegexModifierWithStatistics modifier, String expectedOutput,
			double expectedMaxSpentTime) throws Exception {

		// create reader
		Reader reader = new ModifyingReader(new BufferedReader(
				new StringReader(input)), modifier);

		// read the stream into an output stream
		long start = System.currentTimeMillis();
		String foundOutput = IOUtils.toString(reader);
		long end = System.currentTimeMillis();

		// compare the expected result with the found result
		if (!expectedOutput.equals(foundOutput)) {

			assertEquals(expectedOutput, foundOutput);
		}

		assertTime(end - start, expectedMaxSpentTime,
				"Time spent by   ModifyingReader:");
	}

	private void assertReplacementByWriter(String input,
			RegexModifierWithStatistics modifier, String expectedOutput,
			double expectedMaxSpentTime) throws Exception {

		// setup: create modifier and writer
		StringWriter stringWriter = new StringWriter();
		ModifyingWriter writer = new ModifyingWriter(stringWriter, modifier);

		// write the stream to an output stream
		long start = System.currentTimeMillis();
		for (int index = 0; index < input.length(); index++) {
			writer.append(input.charAt(index));
		}
		writer.flush();
		writer.close();
		long end = System.currentTimeMillis();

		String foundOutput = stringWriter.toString();

		// compare the expected result with the found result
		if (!expectedOutput.equals(foundOutput)) {

			assertEquals(expectedOutput, foundOutput);
		}

		assertTime(end - start, expectedMaxSpentTime,
				"Time spent by   ModifyingWriter:");
	}

	private void assertTime(long duration, double expectedMaxSeconds,
			String callerDescription) throws Exception {
		double foundSeconds = duration / 1000.0;
		String message = String.format(callerDescription
				+ " Found seconds %s shall not exceed"
				+ " expected maximum of seconds %s but did exceed",
				foundSeconds, expectedMaxSeconds);
		System.out.println(message);
		assertTrue(message, foundSeconds <= expectedMaxSeconds);
	}

	private void assertStatistics(int minimumLengthOfLookBehind,
			int requestedCapacityOfCharacterBuffer, String regex,
			RegexModifierWithStatistics modifier) {

		// (1) We assert that the capacity of the character buffer should not
		// exceed the requested capacity by the factor two.

		// let�s assume the regex matches not more than m characters.
		// let's assume the buffer contains c characters (its capacity)
		// the first c-1 characters do not match.
		// the c-th character might be the start of the match
		// the maximum allowed capacity may be n = c (initial value)
		// then doubling the capacity n = n*2 until n >= (c-1) + m helps

		// FIXME if the regex is greedy or contains groups or ...
		int m = regex.length();
		int n = requestedCapacityOfCharacterBuffer * 2;
		while (n < (requestedCapacityOfCharacterBuffer - 1) + m) {
			n = n * 2;
		}

		int maximumAllowedCapacity = n;
		assertTrue(String.format("The maximum used"
				+ " capacity of the character buffer (%s) should not"
				+ " exceed the requested length of the character "
				+ "buffer by the factor two (%s) but it does",
				modifier.getMaxCapacityCharBuf(), maximumAllowedCapacity),
				modifier.getMaxCapacityCharBuf() <= maximumAllowedCapacity);

		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unused")
	private void writeFileForComparisonWithPerl(String input,
			String expectedOutput) throws Exception {

		// print shell script
		File scriptFile = File.createTempFile("regex-via-perl", ".sh");
		FileUtils.copyURLToFile(getClass().getResource("regex-via-perl.sh"),
				scriptFile);
		System.out.println("scriptFile: " + scriptFile.getAbsolutePath());

		// print input file
		File inputFile = File.createTempFile("input", ".txt");
		FileUtils.write(inputFile, input);
		System.out.println("inputFile: " + inputFile.getAbsolutePath());

		// print expected output file
		File expectedOutputFile = File.createTempFile("expectedOutput", ".txt");
		FileUtils.write(expectedOutputFile, input);

		System.out.println("expectedOutputFile: "
				+ expectedOutputFile.getAbsolutePath());
	}
}

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
package com.github.rwitzel.streamflyer.regex;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.github.rwitzel.streamflyer.core.ModifyingReader;
import com.github.rwitzel.streamflyer.core.ModifyingWriter;
import com.github.rwitzel.streamflyer.regex.OnStreamMatcher;
import com.github.rwitzel.streamflyer.regex.OnStreamStandardMatcher;
import com.github.rwitzel.streamflyer.regex.RegexModifier;
import com.github.rwitzel.streamflyer.regex.ReplacingProcessor;

/**
 * @author rwoo
 * @since 04.05.2012
 */
public abstract class AbstractRegexModifierTest {

    protected OnStreamMatcher createMatcher(String regex, int flags) {
        Matcher matcher = Pattern.compile(regex, flags).matcher("");
        matcher.useTransparentBounds(true);
        matcher.useAnchoringBounds(false);
        return new OnStreamStandardMatcher(matcher);
    }

    protected RegexModifier createModifier(String regex, String replacement, int minimumLengthOfLookBehind,
            int requestedCapacityOfCharacterBuffer, int flags) {
        // create matcher
        OnStreamMatcher matcher = createMatcher(regex, flags);

        // create modifier
        RegexModifier modifier = new RegexModifier( //
                matcher, //
                new ReplacingProcessor(replacement), //
                minimumLengthOfLookBehind, //
                requestedCapacityOfCharacterBuffer);

        return modifier;
    }

    protected RegexModifier assertReplacementByReader(String input, String regex, String replacement,
            int minimumLengthOfLookBehind, int requestedCapacityOfCharacterBuffer, String expectedOutput, int flags)
            throws Exception {

        // create modifier
        RegexModifier modifier = createModifier(regex, replacement, minimumLengthOfLookBehind,
                requestedCapacityOfCharacterBuffer, flags);

        // create reader
        Reader reader = new ModifyingReader(new BufferedReader(new StringReader(input)), modifier);

        // read the stream into an output stream
        String foundOutput = IOUtils.toString(reader);

        // compare the expected result with the found result
        if (!expectedOutput.equals(foundOutput)) {

            System.out.println("minimumLengthOfLookBehind: " + minimumLengthOfLookBehind);
            System.out.println("requestedCapacityOfCharacterBuffer: " + requestedCapacityOfCharacterBuffer);

            assertEquals(expectedOutput, foundOutput);
        }

        return modifier;
    }

    protected RegexModifier assertReplacementByWriter(String input, String regex, String replacement,
            int minimumLengthOfLookBehind, int requestedCapacityOfCharacterBuffer, String expectedOutput, int flags)
            throws Exception {

        // create modifier
        RegexModifier modifier = createModifier(regex, replacement, minimumLengthOfLookBehind,
                requestedCapacityOfCharacterBuffer, flags);

        // setup: create modifier and writer
        StringWriter stringWriter = new StringWriter();
        ModifyingWriter writer = new ModifyingWriter(stringWriter, modifier);

        // write the stream to an output stream
        for (int index = 0; index < input.length(); index++) {
            writer.append(input.charAt(index));
        }
        writer.flush();
        writer.close();

        String foundOutput = stringWriter.toString();

        // compare the expected result with the found result
        if (!expectedOutput.equals(foundOutput)) {

            System.out.println("minimumLengthOfLookBehind: " + minimumLengthOfLookBehind);
            System.out.println("requestedCapacityOfCharacterBuffer: " + requestedCapacityOfCharacterBuffer);

            assertEquals(expectedOutput, foundOutput);
        }

        return modifier;
    }

}

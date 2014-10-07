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
package com.googlecode.streamflyer.regex.addons.saxlike;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.regex.addons.tokens.Token;

/**
 * Tests {@link HandlerAwareModifier}.
 * 
 * @author rwoo
 * 
 */
public class HandlerAwareModifierTest {

    @Test
    public void testHandler() throws Exception {

        List<String> foundParts = new ArrayList<String>();

        // define tokens but no special match processors!
        // +++ define the tokens we are looking for
        List<Token> tokens = new ArrayList<Token>();
        tokens.add(new Token("FirstToken", "ab()c", "REPLACED"));
        tokens.add(new Token("SecondToken", "d(())e(zzz)?f", "REPLACED"));

        // +++ create a token processor that stores the found tokens
        // and replaces some text
        MyHandler handler = new MyHandler(foundParts);
        HandlerAwareModifier modifier = new HandlerAwareModifier(tokens, handler, 1, 2048);

        String input = "xx_abc_yy_def_zz";

        // apply the modifying reader
        Reader reader = new ModifyingReader(new StringReader(input), modifier);
        String foundOutput = IOUtils.toString(reader);

        String output = "xx_REPLACED_yy_REPLACED_zz";

        assertEquals(output, foundOutput);

        // test for not matched parts
        String expectedParts = "";
        expectedParts += "x[BEFORE_FETCH]";
        expectedParts += "x_[BEFORE_MATCH]";
        expectedParts += "FirstToken:abc[MATCH]";
        expectedParts += "_yy_[BEFORE_MATCH]";
        expectedParts += "SecondToken:def[MATCH]";
        expectedParts += "_zz[BEFORE_FETCH]";

        // for (String foundPart : foundParts) {
        // System.out.println(foundPart);
        // }

        String joinedFoundParts = join(foundParts, "");

        // "[BEFORE_FETCH]" does not make a difference -> remove it
        assertEquals(expectedParts.replaceAll("[BEFORE_FETCH]", ""), joinedFoundParts.replaceAll("[BEFORE_FETCH]", ""));

    }

    private String join(Collection<String> texts, String separator) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String text : texts) {
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }
            sb.append(text);
        }
        return sb.toString();
    }

}

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
package com.github.rwitzel.streamflyer.regex.addons.tokens;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * Learning tests for group related methods in {@link java.util.regex.Matcher}.
 * 
 * @author rwoo
 */
public class GroupLearningTest {

    @Test
    public void learningTest() throws Exception {

        Matcher matcher = Pattern.compile("((abc)|(def))").matcher("def");
        assertTrue(matcher.matches());
        assertEquals(3, matcher.groupCount());
        assertEquals("def", matcher.group()); // everything
        assertEquals("def", matcher.group(0)); // everything
        assertEquals("def", matcher.group(1)); // () that span the entire input
        assertEquals(null, matcher.group(2)); // () for "abc"
        assertEquals("def", matcher.group(3)); // () for "def"
        try {
            assertEquals(null, matcher.group(4));
            fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            // OK, expected exception
        }
    }

}

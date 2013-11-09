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
package com.googlecode.streamflyer.regex.addons.tokens;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.googlecode.streamflyer.regex.addons.tokens.Token;

/**
 * Tests {@link Token}.
 * 
 * @author rwoo
 * 
 */
public class TokenTest {

    @Test
    public void testGetCapturingGroupCount() throws Exception {
        assertEquals(0, new Token("").getCapturingGroupCount());
        assertEquals(1, new Token("a(b)c").getCapturingGroupCount());
        assertEquals(0, new Token("a(?:b)c").getCapturingGroupCount());
    }

    @Test
    public void testGetRegex() throws Exception {
        assertEquals("", new Token("").getRegex());
        assertEquals("a(b)c", new Token("a(b)c").getRegex());
    }

}

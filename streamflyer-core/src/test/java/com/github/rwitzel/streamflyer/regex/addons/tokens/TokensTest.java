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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.github.rwitzel.streamflyer.regex.addons.tokens.Token;
import com.github.rwitzel.streamflyer.regex.addons.tokens.TokensMatcher;

/**
 * Tests {@link TokensMatcher}.
 * 
 * @author rwoo
 * 
 */
public class TokensTest {

    @Test
    public void testCreateRegexThatMatchesAnyToken() throws Exception {

        List<Token> tokenList = new ArrayList<Token>();
        tokenList.add(new Token("abc"));
        tokenList.add(new Token("def"));
        tokenList.add(new Token("ghi"));

        TokensMatcher tokensMatcher = new TokensMatcher();
        String regex = tokensMatcher.createRegexThatMatchesAnyToken(tokenList);
        assertTrue("abc".matches(regex));
        assertTrue("def".matches(regex));
        assertTrue("ghi".matches(regex));
        assertFalse("aaa".matches(regex));
        assertFalse("abcdef".matches(regex));
    }

}

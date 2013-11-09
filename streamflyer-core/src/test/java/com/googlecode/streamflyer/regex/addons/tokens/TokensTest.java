package com.googlecode.streamflyer.regex.addons.tokens;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.googlecode.streamflyer.regex.addons.tokens.Token;
import com.googlecode.streamflyer.regex.addons.tokens.TokensMatcher;

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

package com.googlecode.streamflyer.support.tokens;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.googlecode.streamflyer.support.tokens.Token;
import com.googlecode.streamflyer.support.tokens.Tokens;

/**
 * Tests {@link Tokens}.
 * 
 * @author rwoo
 * 
 */
public class TokensTest {

    @Test
    public void testCreateRegexThatMatchesAllTokens() throws Exception {

        List<Token> tokenList = new ArrayList<Token>();
        tokenList.add(new Token("abc"));
        tokenList.add(new Token("def"));
        tokenList.add(new Token("ghi"));

        Tokens tokens = new Tokens(tokenList);
        String regex = tokens.createRegexThatMatchesAllTokens();
        assertTrue("abc".matches(regex));
        assertTrue("def".matches(regex));
        assertTrue("ghi".matches(regex));
        assertFalse("aaa".matches(regex));
        assertFalse("abcdef".matches(regex));
    }

}

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

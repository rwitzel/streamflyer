package com.googlecode.streamflyer.regex.addons.tokens;

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
        }
        catch (IndexOutOfBoundsException e) {
            // OK, expected exception
        }
    }

}

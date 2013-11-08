package com.googlecode.streamflyer.support.util;

import static org.junit.Assert.assertEquals;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * Tests {@link EmbeddedFlagUtil}.
 * 
 * @author rwoo
 * 
 */
public class EmbeddedFlagUtilTest {

    private EmbeddedFlagUtil util = new EmbeddedFlagUtil();

    @Test
    public void testEmbedFlags() throws Exception {
        assertEquals("(?im:abc)", util.embedFlags("abc", Pattern.CASE_INSENSITIVE ^ Pattern.MULTILINE));
        assertEquals("abc", util.embedFlags("abc", 0));
    }

    @Test
    public void testEmbedFlags_differentFlags() throws Exception {

        String regex1 = "ABC";
        String regex2 = "XYZ";

        String regex = "(" + util.embedFlags(regex1, 0) + ")|(" + util.embedFlags(regex2, Pattern.CASE_INSENSITIVE)
                + ")";

        assertEquals(true, "ABC".matches(regex));
        assertEquals(false, "abc".matches(regex));

        assertEquals(true, "XYZ".matches(regex));
        assertEquals(true, "xyz".matches(regex));
    }

    @Test
    public void testEmbedFlags_CASE_INSENSITIVE() throws Exception {

        String input = "aBc";
        String regex = "Abc";
        int testedFlag = Pattern.CASE_INSENSITIVE;
        match(input, regex, 0, false);
        match(input, regex, testedFlag, true);
    }

    @Test
    public void testEmbedFlags_UNIX_LINES() throws Exception {

        String input = " \ra";
        String regex = "^a";
        int testedFlag = Pattern.UNIX_LINES;
        match(input, regex, 0 ^ Pattern.MULTILINE, true);
        match(input, regex, testedFlag ^ Pattern.MULTILINE, false);
    }

    @Test
    public void testEmbedFlags_MULTILINE() throws Exception {

        String input = " \r\na";
        String regex = "^a";
        int testedFlag = Pattern.MULTILINE;
        match(input, regex, 0, false);
        match(input, regex, testedFlag, true);
    }

    @Test
    public void testEmbedFlags_DOTALL() throws Exception {

        String input = "\n";
        String regex = ".";
        int testedFlag = Pattern.DOTALL;
        match(input, regex, 0, false);
        match(input, regex, testedFlag, true);
    }

    @Test
    public void testEmbedFlags_UNICODE_CASE() throws Exception {

        String input = "äBc";
        String regex = "Äbc";
        int testedFlag = Pattern.UNICODE_CASE;
        match(input, regex, 0 ^ Pattern.CASE_INSENSITIVE, false);
        match(input, regex, testedFlag ^ Pattern.CASE_INSENSITIVE, true);
    }

    @Test
    public void testEmbedFlags_COMMENTS_whitespace() throws Exception {

        String input = "ab";
        String regex = "a    b";
        int testedFlag = Pattern.COMMENTS;
        match(input, regex, 0, false);
        match(input, regex, testedFlag, true);
    }

    @Test
    public void testEmbedFlags_COMMENTS_comment() throws Exception {

        String input = "ab";
        String regex = "a#comment\nb";
        int testedFlag = Pattern.COMMENTS;
        match(input, regex, 0, false);
        match(input, regex, testedFlag, true);
    }

    private MatchResult match(String input, String regex, int flags, boolean matchExpected) {
        // match with flags
        Matcher matcher = Pattern.compile(regex, flags).matcher(input);
        assertEquals(matchExpected, matcher.find());
        MatchResult result = matcher;

        // match with inline flags
        Matcher matcherInline = Pattern.compile(util.embedFlags(regex, flags)).matcher(input);
        assertEquals(matchExpected, matcherInline.find());
        MatchResult resultInline = matcherInline;

        if (matchExpected) {
            assertEqualMatchResult(result, resultInline);
        }
        return result;
    }

    private void assertEqualMatchResult(MatchResult result, MatchResult resultInline) {
        assertEquals(result.group(), resultInline.group());
        assertEquals(result.start(), resultInline.start());
        assertEquals(result.end(), resultInline.end());
        assertEquals(result.groupCount(), resultInline.groupCount());
        for (int index = 0; index < result.groupCount(); index++) {
            assertEquals(result.group(index), resultInline.group(index));
            assertEquals(result.start(index), resultInline.start(index));
            assertEquals(result.end(index), resultInline.end(index));
        }
    }
}

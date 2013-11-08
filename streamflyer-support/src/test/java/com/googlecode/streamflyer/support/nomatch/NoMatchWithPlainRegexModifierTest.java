package com.googlecode.streamflyer.support.nomatch;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.regex.RegexModifier;
import com.googlecode.streamflyer.regex.ReplacingProcessor;

/**
 * Tests {@link NoMatch} and the classes of the same package together with a plain {@link RegexModifier}.
 * 
 * @author rwoo
 * 
 */
public class NoMatchWithPlainRegexModifierTest {

    /**
     * Rather an integration test than a unit test.
     * 
     * @throws Exception
     */
    @Test
    public void testNoMatches() throws Exception {

        NoMatchCollector noMatch = new NoMatchCollector();

        // +++ create the modifier
        Modifier modifier = new RegexModifier("(abc)|(def)", 0, new NoMatchAwareMatchProcessor(new ReplacingProcessor(
                "REPLACED"), noMatch, true), 1, 2048);

        modifier = new NoMatchAwareModifier(modifier, noMatch);

        String input = "xx_abc_yy_def_zz";

        // apply the modifying reader
        Reader reader = new ModifyingReader(new StringReader(input), modifier);
        String foundOutput = IOUtils.toString(reader);

        String output = "xx_REPLACED_yy_REPLACED_zz";

        assertEquals(output, foundOutput);

        // test for not matched parts
        String expectedNoMatchInfos = "";
        expectedNoMatchInfos += "x[FETCH]";
        expectedNoMatchInfos += "x_[MATCH]";
        expectedNoMatchInfos += "_yy_[MATCH]";
        expectedNoMatchInfos += "_zz[FETCH]";

        String foundNoMatchInfos = join(noMatch.getNoMatchInfos(), "");

        // "[FETCH]" does not make a difference -> remove it
        assertEquals(expectedNoMatchInfos.replaceAll("[FETCH]", ""), foundNoMatchInfos.replaceAll("[FETCH]", ""));
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

package com.googlecode.streamflyer.support.saxlike;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.support.tokens.Token;

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
        tokens.add(new Token("FirstToken", "abc", "REPLACED"));
        tokens.add(new Token("SecondToken", "def", "REPLACED"));

        // +++ create a token processor that stores the found tokens
        // and replaces some text
        MyHandler handler = new MyHandler(foundParts);
        HandlerAwareModifier modifier = new HandlerAwareModifier(tokens, handler);

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

        for (String foundPart : foundParts) {
            System.out.println(foundPart);
        }

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

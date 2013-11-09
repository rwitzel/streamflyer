package com.googlecode.streamflyer.regex.addons.tokens;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.regex.RegexModifier;
import com.googlecode.streamflyer.regex.addons.tokens.Token;
import com.googlecode.streamflyer.regex.addons.tokens.TokenProcessor;
import com.googlecode.streamflyer.regex.addons.tokens.TokensMatcher;

/**
 * Tests {@link MyTokenProcessor} and {@link TokenProcessor}.
 * 
 * @author rwoo
 * 
 */
public class MyTokenProcessorTest {

    /**
     * Rather an integration test than a unit test.
     * 
     * @throws Exception
     */
    @Test
    public void testProcess() throws Exception {

        // +++ define the tokens we are looking for
        List<Token> tokenList = new ArrayList<Token>();
        tokenList.add(new Token("SectionStart", "<section class='abc'>"));
        tokenList.add(new Token("SectionTitle", "(<h1>)([^<>]*)(</h1>)", "$1TITLE_FOUND$3"));
        tokenList.add(new Token("ListItem", "(<li>)([^<>]*)(</li>)", "$1LIST_ITEM_FOUND$3"));
        tokenList.add(new Token("SectionEnd", "</section>"));
        TokensMatcher tokensMatcher = new TokensMatcher(tokenList);

        // +++ create a token processor that stores the found tokens
        // and replaces some text
        List<String> foundTokens = new ArrayList<String>();
        MyTokenProcessor tokenProcessor = new MyTokenProcessor(tokenList, foundTokens);

        // +++ create the modifier
        Modifier modifier = new RegexModifier(tokensMatcher, tokenProcessor, 1, 2048);

        String input = "";
        input += "text <section class='abc'>";
        input += "text <h1>my title</h1>";
        input += "text <ul>";
        input += "text <li>my first list item</li>";
        input += "text <li>my second list item</li>";
        input += "text </ul>";
        input += "text </section>";
        input += "text <h1>title outside section</h1>";
        input += "text <li>list item outside section</li>";

        // apply the modifying reader
        Reader reader = new ModifyingReader(new StringReader(input), modifier);
        String foundOutput = IOUtils.toString(reader);

        assertEquals(7, foundTokens.size());
        assertEquals("SectionStart:<section class='abc'>", foundTokens.get(0));
        assertEquals("SectionTitle:<h1>my title</h1>", foundTokens.get(1));
        assertEquals("ListItem:<li>my first list item</li>", foundTokens.get(2));
        assertEquals("ListItem:<li>my second list item</li>", foundTokens.get(3));
        assertEquals("SectionEnd:</section>", foundTokens.get(4));
        assertEquals("SectionTitle:<h1>title outside section</h1>", foundTokens.get(5));
        assertEquals("ListItem:<li>list item outside section</li>", foundTokens.get(6));

        String output = "";
        output += "text <section class='abc'>";
        output += "text <h1>TITLE_FOUND</h1>";
        output += "text <ul>";
        output += "text <li>LIST_ITEM_FOUND</li>";
        output += "text <li>LIST_ITEM_FOUND</li>";
        output += "text </ul>";
        output += "text </section>";
        output += "text <h1>title outside section</h1>";
        output += "text <li>list item outside section</li>";

        assertEquals(output, foundOutput);

    }
}

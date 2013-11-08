package com.googlecode.streamflyer.support.stateful;

import static java.util.Arrays.asList;
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
import com.googlecode.streamflyer.support.util.DelegatingMatcher;

/**
 * Tests {@link StateMachine}.
 * 
 * @author rwoo
 * 
 */
public class StateMachineTest {

    /**
     * Rather an integration test than a unit test.
     * 
     * @throws Exception
     */
    @Test
    public void testProcess() throws Exception {

        List<String> foundTokens = new ArrayList<String>();
        TokenCollector tokenCollector = new TokenCollector(foundTokens);

        // +++ define the states
        // (remember: (1) title and item are optional + (2) a list of items is possible)
        State state0 = new State("Start"); // the initial state
        State state1 = new State("SectionStart", "<section class='abc'>");
        State state2 = new State("SectionTitle", "(<h1>)([^<>]*)(</h1>)", "$1TITLE_FOUND$3");
        State state3 = new State("ListItem", "(<li>)([^<>]*)(</li>)", "$1LIST_ITEM_FOUND$3");
        State state4 = new State("SectionEnd", "</section>");
        state0.setTransitions(asList(state1), tokenCollector);
        state1.setTransitions(asList(state2, state3, state4), tokenCollector);
        state2.setTransitions(asList(state3, state4), tokenCollector);
        state3.setTransitions(asList(state3, state4), tokenCollector);
        state4.setTransitions(asList(state1), tokenCollector);

        // +++ create a processor that stores the found states and replaces some text
        DelegatingMatcher delegatingMatcher = new DelegatingMatcher();
        StateMachine stateMachine = new StateMachine(state0, delegatingMatcher);

        // +++ create the modifier
        Modifier modifier = new RegexModifier(delegatingMatcher, stateMachine, 1, 2048);

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

        assertEquals(5, foundTokens.size());
        assertEquals("SectionStart:<section class='abc'>", foundTokens.get(0));
        assertEquals("SectionTitle:<h1>my title</h1>", foundTokens.get(1));
        assertEquals("ListItem:<li>my first list item</li>", foundTokens.get(2));
        assertEquals("ListItem:<li>my second list item</li>", foundTokens.get(3));
        assertEquals("SectionEnd:</section>", foundTokens.get(4));

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

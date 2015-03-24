/**
 * Copyright (C) 2011 rwitzel75@googlemail.com
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
package com.github.rwitzel.streamflyer.regex.addons.nomatch;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.github.rwitzel.streamflyer.core.Modifier;
import com.github.rwitzel.streamflyer.core.ModifyingReader;
import com.github.rwitzel.streamflyer.regex.RegexModifier;
import com.github.rwitzel.streamflyer.regex.ReplacingProcessor;
import com.github.rwitzel.streamflyer.regex.addons.nomatch.NoMatch;
import com.github.rwitzel.streamflyer.regex.addons.nomatch.NoMatchAwareMatchProcessor;
import com.github.rwitzel.streamflyer.regex.addons.nomatch.NoMatchAwareModifier;
import com.github.rwitzel.streamflyer.regex.addons.nomatch.NoMatchAwareTransitionGuard;
import com.github.rwitzel.streamflyer.regex.addons.stateful.State;
import com.github.rwitzel.streamflyer.regex.addons.stateful.StateMachine;
import com.github.rwitzel.streamflyer.regex.addons.stateful.TokenCollector;
import com.github.rwitzel.streamflyer.regex.addons.util.DelegatingMatcher;
import com.github.rwitzel.streamflyer.regex.addons.util.DoNothingProcessor;

/**
 * Tests {@link NoMatch} and the classes of the same package together with a {@link StateMachine}.
 * 
 * @author rwoo
 * 
 */
public class NoMatchWithStateMachineTest {

    private State createState(String stateName, NoMatch noMatch) {
        // we don't need NoMatchAwareMatchProcessor for the initial state because the initial state is never reached
        return new State(stateName);
    }

    private State createState(String stateName, String regex, NoMatch pos) {
        return new State(stateName, regex, new NoMatchAwareMatchProcessor(new DoNothingProcessor(), pos, false));
    }

    private State createState(String stateName, String regex, String replacement, NoMatch pos) {
        return new State(stateName, regex, new NoMatchAwareMatchProcessor(new ReplacingProcessor(replacement), pos,
                false));
    }

    /**
     * Rather an integration test than a unit test.
     * 
     * @throws Exception
     */
    @Test
    public void testNoMatches() throws Exception {

        NoMatchCollector noMatch = new NoMatchCollector();

        List<String> foundTokens = new ArrayList<String>();
        TokenCollector tokenCollector = new TokenCollector(foundTokens);
        NoMatchAwareTransitionGuard guard = new NoMatchAwareTransitionGuard(tokenCollector, noMatch);

        // +++ define the states
        // (remember: (1) title and item are optional + (2) a list of items is possible)
        State state0 = createState("Start", noMatch); // the initial state
        State state1 = createState("SectionStart", "<section class='abc'>", noMatch);
        State state2 = createState("SectionTitle", "(<h1>)([^<>]*)(</h1>)", "$1TITLE_FOUND$3", noMatch);
        State state3 = createState("ListItem", "(<li>)([^<>]*)(</li>)", "$1LIST_ITEM_FOUND$3", noMatch);
        State state4 = createState("SectionEnd", "</section>", noMatch);
        state0.setTransitions(asList(state1), guard);
        state1.setTransitions(asList(state2, state3, state4), guard);
        state2.setTransitions(asList(state3, state4), guard);
        state3.setTransitions(asList(state3, state4), guard);
        state4.setTransitions(asList(state1), guard);

        // +++ create a processor that stores the found states and replaces some text
        DelegatingMatcher delegatingMatcher = new DelegatingMatcher();
        StateMachine stateMachine = new StateMachine(state0, delegatingMatcher);

        // +++ create the modifier
        Modifier modifier = new RegexModifier(delegatingMatcher, stateMachine, 1, 2048);

        modifier = new NoMatchAwareModifier(modifier, noMatch);

        String input = "";
        input += "atext01 <section class='abc'>";
        input += "btext02 <h1>my title</h1>";
        input += "ctext03 <ul>";
        input += "dtext04 <li>my first list item</li>";
        input += "etext05 <li>my second list item</li>";
        input += "ftext06 </ul>";
        input += "gtext07 </section>";
        input += "htext08 <h1>title outside section</h1>";
        input += "itext09 <li>list item outside section</li>";

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
        output += "atext01 <section class='abc'>";
        output += "btext02 <h1>TITLE_FOUND</h1>";
        output += "ctext03 <ul>";
        output += "dtext04 <li>LIST_ITEM_FOUND</li>";
        output += "etext05 <li>LIST_ITEM_FOUND</li>";
        output += "ftext06 </ul>";
        output += "gtext07 </section>";
        output += "htext08 <h1>title outside section</h1>";
        output += "itext09 <li>list item outside section</li>";

        assertEquals(output, foundOutput);

        // test for not matched parts
        String expectedNoMatchInfos = "";
        expectedNoMatchInfos += "a[FETCH]";
        expectedNoMatchInfos += "text01 [MATCH]";
        expectedNoMatchInfos += "btext02 [MATCH]";
        expectedNoMatchInfos += "ctext03 <ul>dtext04 [MATCH]";
        expectedNoMatchInfos += "etext05 [MATCH]";
        expectedNoMatchInfos += "ftext06 </ul>gtext07 [MATCH]";
        expectedNoMatchInfos += "htext08 <h1>title outside section</h1>";
        expectedNoMatchInfos += "itext09 <li>list item outside section</li>[FETCH]";

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

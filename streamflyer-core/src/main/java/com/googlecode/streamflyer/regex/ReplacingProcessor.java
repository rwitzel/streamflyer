/**
 * Copyright (C) 2011 rwoo@gmx.de
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

package com.googlecode.streamflyer.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

import com.googlecode.streamflyer.internal.thirdparty.ZzzValidate;

/**
 * This {@link MatchProcessor} replaces the matching text (given by the
 * {@link MatchResult}) with the configured replacement.
 * <p>
 * The replacement can be defined with the same syntax as defined by
 * {@link Matcher#appendReplacement(StringBuffer, String)}
 * <p>
 * EXAMPLES: <code><pre class="prettyprint lang-java">
new ReplacingProcessor("aaa") // replaces each match with "aaa"
new ReplacingProcessor("$2") // replaces each match with the content of the second {@link MatchResult#group() group} of the match
new ReplacingProcessor("aaa$2bbb$1ccc") // replaces each match with "aaa$2bbb$1ccc"
                           // wherein $2 and $1 are replaced with the content
                           // of the second and the first group of the match
</pre></code>
 * 
 * @author rwoo
 * @since 18.06.2011
 */
public class ReplacingProcessor implements MatchProcessor {

    //
    // injected properties
    //

    /**
     * This list contains Strings and Numbers. The latter ones refer to groups.
     */
    private List<Object> parts = null;

    /**
     * If not null, then the replacement do not uses groups. Then this property
     * contains the entire replacement.
     */
    private CharSequence replacementWithoutGroupReferences;

    //
    // constructors
    //

    /**
     * For unit tests only.
     */
    public ReplacingProcessor() {
        // nothing to do here
    }

    /**
     * @param replacement
     */
    public ReplacingProcessor(String replacement) {
        super();

        // validate arguments
        ZzzValidate.notNull(replacement, "replacement must not be null");

        // parse replacement
        this.parts = parseReplacement(replacement);

        if (parts.size() == 1 && (parts.get(0) instanceof CharSequence)) {

            this.replacementWithoutGroupReferences = (CharSequence) parts
                    .get(0);

        }
        else {

            this.replacementWithoutGroupReferences = null;

        }
    }


    /**
     * Package-private for JUnit-tests.
     * 
     * @return Returns the parts of the matchProcessor
     */
    List<Object> parseReplacement(String replacement) {

        List<Object> compiledReplacement = new ArrayList<Object>();

        // we look for escaped literals and references to groups
        int position = 0;
        StringBuilder notGroupReference = new StringBuilder();

        while (position < replacement.length()) {
            char ch = replacement.charAt(position);

            // reference to group?
            if (ch == '$') {

                if (notGroupReference.length() != 0) {
                    compiledReplacement.add(notGroupReference.toString());
                    notGroupReference.setLength(0);
                }

                position++;

                int groupNumberStartPosition = position;
                if (groupNumberStartPosition == replacement.length()) {
                    throw new IllegalArgumentException("group reference $ "
                            + "without number at the end " + "of the "
                            + "replacement string (" + replacement + ")");
                }

                ch = replacement.charAt(position);

                while ('0' <= ch && ch <= '9') {
                    position++;
                    if (position == replacement.length()) {
                        break;
                    }
                    ch = replacement.charAt(position);
                }
                // position is the position of the first character that does not
                // belong to the group reference

                if (position == groupNumberStartPosition) {
                    throw new IllegalArgumentException(
                            "invalid reference to group at position "
                                    + position + " in replacement string "
                                    + replacement.substring(0, position) + "[]"
                                    + replacement.substring(position));
                }

                String groupNumberString = replacement.substring(
                        groupNumberStartPosition, position);
                int groupNumber = Integer.parseInt(groupNumberString);

                compiledReplacement.add(groupNumber);
            }
            // escaped literal?
            else if (ch == '\\') {

                // skip the backslash
                position++;
                ch = replacement.charAt(position);

                notGroupReference.append(ch);
                position++;
            }
            // this is a normal character
            else {

                notGroupReference.append(ch);
                position++;
            }
        }

        if (notGroupReference.length() != 0) {
            compiledReplacement.add(notGroupReference.toString());
        }

        return compiledReplacement;
    }

    /**
     * Replaces the match as given by the {@link MatchResult} with the
     * configured replacement.
     * 
     * @see com.googlecode.streamflyer.regex.MatchProcessor#process(java.lang.StringBuilder,
     *      int, java.util.regex.MatchResult)
     */
    @Override
    public MatchProcessorResult process(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {

        int start = matchResult.start();
        int end = matchResult.end();

        // if the empty string is matched, then we increase the position
        // to continue from to avoid endless loops
        // (compare to Matcher.find() where we see the following code:
        // int i = last; if(i == first) i++;
        // in words: set the *from* for the next match at the
        // end of the last match. if this is equal to the start
        // of the last match (a match on the empty string(, then
        // increase the *from* to avoid endless loops)
        int offset = start == end ? 1 : 0;

        if (replacementWithoutGroupReferences != null) {

            characterBuffer.delete(start, end);
            characterBuffer.insert(start, replacementWithoutGroupReferences);
            return new MatchProcessorResult(start
                    + replacementWithoutGroupReferences.length() + offset, true);

        }
        else {

            // (1) create replacement
            // (one could set a reasonable value for the initial size of the
            // string builder somewhere)
            StringBuilder replacement = new StringBuilder();

            for (Object part : parts) {
                if (part instanceof Integer) {
                    // append the value of the referred group
                    replacement.append(matchResult.group((Integer) part));
                }
                else {
                    // append the static part
                    replacement.append(part); // part is instance of String
                }
            }

            // (2) replace the match with the replacement
            characterBuffer.delete(start, end);
            characterBuffer.insert(start, replacement);

            return new MatchProcessorResult(start + replacement.length()
                    + offset, true);
        }

    }

}

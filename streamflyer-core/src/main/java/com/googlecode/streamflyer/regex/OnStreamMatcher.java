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

import java.util.regex.MatchResult;
import java.util.regex.Matcher;

/**
 * A reduced variant of a {@link Matcher}. Implementations of this interface match a regular expression on a
 * {@link CharSequence}.
 * <p>
 * In comparison to {@link Matcher}, this type provides the method {@link #findUnlessHitEnd(int, int)} that is
 * particularly useful when you want to match a regular expression on a character stream.
 * 
 * @author rwoo
 * @since 20.06.2011
 */
public interface OnStreamMatcher extends MatchResult {

    // * <p>
    // * What is the technical idea behind this matcher? The purpose of this
    // matcher
    // * is too extend the class {@link Matcher} with the following code.
    // <code><pre>
    // private int lastFrom;
    //
    // public boolean findUntilMatchOrHitEnd(int from, int to) {
    // this.hitEnd = false;
    // this.requireEnd = false;
    // from = from < 0 ? 0 : from;
    // this.first = from;
    // this.oldLast = oldLast < 0 ? from : oldLast;
    // for (int i = 0; i < groups.length; i++)
    // groups[i] = -1;
    // acceptMode = NOANCHOR;
    // boolean result = false;
    // for (this.lastFrom = from; lastFrom <= to; lastFrom++) {
    // result = parentPattern.matchRoot.match(this, lastFrom, text);
    // if (result || hitEnd()) {
    // break;
    // }
    // }
    // if (!result)
    // this.first = -1;
    // this.oldLast = this.last;
    // return result;
    // }
    //
    // public int lastFrom() {
    // return lastFrom;
    // }
    // </pre></code>
    // * <p>
    // * Unfortunately, {@link Matcher} cannot by extended via subclassing
    // because
    // * {@link Matcher} is a final type. The new interface {@link
    // OnStreamMatcher}
    // * provides all methods necessary to do match regular expresions on
    // character
    // * stream including the method {@link #findUnlessHitEnd(int, int)}.

    /**
     * @see Matcher#reset(CharSequence)
     */
    public void reset(CharSequence input);

    /**
     * Looks for the first position that could be the start of a match that starts in the range [ <code>from</code>
     * (including), <code>maxFrom</code> (including)]. If such a position is found, then the method stops and returns
     * true if the input provides enough characters to make a match.
     * <p>
     * ATTENTION! maxFrom can be the position behind(!) the last character of the input.
     * <p>
     * This first position can retrieved via {@link #lastFrom()}.
     * 
     * @param minFrom
     * @param maxFrom
     * @return Returns true if the method found a match at the position {@link #lastFrom()}.
     */
    public boolean findUnlessHitEnd(int minFrom, int maxFrom);

    /**
     * @see Matcher#hitEnd()
     */
    public boolean hitEnd();

    /**
     * @see Matcher#requireEnd()
     */
    public boolean requireEnd();

    /**
     * This property is set by calling {@link #findUnlessHitEnd(int, int)} .
     * <p>
     * ATTENTION! This property returns maxFrom + 1, if {@link #findUnlessHitEnd(int, int)} neither has found a match
     * nor has hit the end of input as long it looked for matches that started somewhere in the interval [minFrom,
     * maxFrom].
     * <p>
     * ATTENTION! This information is not really helpful when the buffer size is equal to maxFrom. In this case you
     * cannot say whether the position of lastFrom is the start of a potential match or not.
     * 
     * @return Returns the last position {@link #findUnlessHitEnd(int, int)} has investigated before it returned.
     */
    public int lastFrom();
}

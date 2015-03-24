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
package com.github.rwitzel.streamflyer.experimental.range2;

import java.util.ArrayList;
import java.util.List;

import com.github.rwitzel.streamflyer.regex.addons.saxlike.HandlerAwareModifier;
import com.github.rwitzel.streamflyer.regex.addons.tokens.Token;

/**
 * Same functionality like {@link com.github.rwitzel.streamflyer.experimental.range.RangeFilterModifier} but different
 * implementation.
 * <p>
 * Please keep in mind that if you use look-behind constructs like "^" the filter may not work as expected because the
 * content outside the range is removed. But the removed content is crucial to match the look-behind construct.
 * <p>
 * TODO use a StateMachine instead of a simple TokenProcessor
 * 
 * @author rwoo
 * @deprecated This class is not fully functional yet (look-behind constructs do not work)
 * @since 1.1.0
 */
@Deprecated
public class RangeFilterModifier extends HandlerAwareModifier {

    public RangeFilterModifier(String startRegex, String endRegex, boolean includeStartToken, boolean includeEndToken,
            boolean initiallyBetweenStartAndEnd, int minimumLengthOfLookBehind, int newNumberOfChars) {
        super();

        // define the behaviour for matched tokens and noMatches
        RangeFilterHandler handler = new RangeFilterHandler(initiallyBetweenStartAndEnd, includeStartToken,
                includeEndToken, minimumLengthOfLookBehind, newNumberOfChars);

        // define the tokens
        List<Token> tokens = new ArrayList<Token>();
        tokens.add(new Token("Start", startRegex));
        tokens.add(new Token("End", endRegex));

        // initialize the handler aware modifier
        initialize(tokens, handler, minimumLengthOfLookBehind, newNumberOfChars);
    }

}

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

package com.googlecode.streamflyer.experimental.regexj6;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.streamflyer.regex.OnStreamMatcher;

/**
 * Tests {@link OnStreamJava6Matcher}.
 * 
 * @author rwoo
 * 
 * @since 28.06.2011
 */
public class RegexModifierJava6Test extends com.googlecode.streamflyer.regex.RegexModifierTest {

    @Override
    protected OnStreamMatcher createMatcher(String regex, int flags) {
        Matcher matcher = Pattern.compile(regex, flags).matcher("");
        matcher.useTransparentBounds(true);
        matcher.useAnchoringBounds(false);
        return new OnStreamJava6Matcher(matcher);
    }

}

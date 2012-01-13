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

package com.googlecode.streamflyer.regex.fast;

import com.googlecode.streamflyer.regex.OnStreamMatcher;
import com.googlecode.streamflyer.regex.fast.Matcher;
import com.googlecode.streamflyer.regex.fast.OnStreamExtendedMatcher;
import com.googlecode.streamflyer.regex.fast.Pattern;


/**
 * Tests {@link OnStreamExtendedMatcher}.
 * 
 * @author rwoo
 * 
 * @since 28.06.2011
 */
public class RegexModifierExtendedTest extends
        com.googlecode.streamflyer.regex.RegexModifierTest {

    @Override
    protected OnStreamMatcher createMatcher(String regex) {
        Matcher matcher = Pattern.compile(regex).matcher("");
        matcher.useTransparentBounds(true);
        return new OnStreamExtendedMatcher(matcher);
    }

}

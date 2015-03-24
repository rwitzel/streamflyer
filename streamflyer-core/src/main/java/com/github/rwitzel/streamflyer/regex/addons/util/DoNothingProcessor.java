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
package com.github.rwitzel.streamflyer.regex.addons.util;

import java.util.regex.MatchResult;

import com.github.rwitzel.streamflyer.regex.AbstractMatchProcessor;
import com.github.rwitzel.streamflyer.regex.MatchProcessor;
import com.github.rwitzel.streamflyer.regex.MatchProcessorResult;

/**
 * This {@link MatchProcessor} does not modify the stream.
 * 
 * @author rwoo
 * @since 1.1.0
 */
public class DoNothingProcessor extends AbstractMatchProcessor implements MatchProcessor {

    @Override
    public MatchProcessorResult process(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            MatchResult matchResult) {

        return createResult(matchResult, matchResult.end(), true);
    }

}

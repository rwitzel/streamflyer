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

package com.googlecode.streamflyer.util;

import java.io.BufferedWriter;
import java.io.Writer;

import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.core.ModifyingWriter;
import com.googlecode.streamflyer.regex.RegexModifier;

/**
 * Provides short-cuts to create {@link ModifyingWriter modifying writers} using
 * defaults.
 * 
 * @author rwoo
 * @since 27.06.2011
 */
public class ModifyingWriterFactory {

    /**
     * @param output t
     * @param regex the r
     * @param replacement the
     * @return Returns a writer that replaces the written characters on-the-fly.
     */
    public ModifyingWriter createRegexModifyingWriter(Writer output,
            String regex, String replacement) {
        return createRegexModifyingWriter(output, regex, 0, replacement, 0,
                8192);
    }

    public ModifyingWriter createRegexModifyingWriter(Writer output,
            String regex, int flags, String replacement,
            int minimumLengthOfLookBehind,
            int requestedCapacityOfCharacterBuffer) {

        // buffer stream
        // (is this really necessary to get optimal performance?)
        if (!(output instanceof BufferedWriter)) {
            output = new BufferedWriter(output);
        }

        // create modifier
        Modifier modifier = new RegexModifier(regex, flags, replacement,
                minimumLengthOfLookBehind, requestedCapacityOfCharacterBuffer);

        // create and return Writer
        return new ModifyingWriter(output, modifier);
    }
}

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
package com.googlecode.streamflyer.regex.addons.util;

import java.util.regex.Pattern;

/**
 * This class supports the creation of regular expressions with embedded flag expressions.
 * 
 * @author rwoo
 * @since 1.1.0
 */
public class EmbeddedFlagUtil {

    /**
     * Creates a regular expression with an embedded flag expression.
     * <p>
     * Supports all flags of JDK7 {@link java.util.regex.Pattern}, i.e. the following flags:
     * <ul>
     * <li>{@link Pattern#CASE_INSENSITIVE}
     * <li>{@link Pattern#UNIX_LINES}
     * <li>{@link Pattern#MULTILINE}
     * <li>{@link Pattern#DOTALL}
     * <li>{@link Pattern#UNICODE_CASE}
     * <li>{@link Pattern#COMMENTS}
     * </ul>
     * <p>
     * EXAMPLE:
     * <ul>
     * <li>For <code>("abc", Pattern.CASE_INSENSITIVE ^ Pattern.MULTILINE)</code> the method returns <code>
     *         "(?im:abc)"</code>.</li>
     * <li>For <code>("abc", 0)</code> the method returns <code>"abc"</code>.</li>
     * </ul>
     * 
     * @param regex
     * @param flags
     * @return Returns the given regex enriched with an embedded flag expression that represents the given flags. If
     *         there is no flag given, the returned regex is equal to the given regex.
     */
    public String embedFlags(String regex, int flags) {
        if (flags == 0) {
            return regex;
        } else {
            return "(?" + mapFlagsToEmbeddedFlags(flags) + ":" + regex + ")";
        }
    }

    /**
     * See {@link #embedFlags(String, int)}.
     */
    protected String mapFlagsToEmbeddedFlags(int flags) {
        String flagsAsString = "";
        if ((flags & Pattern.CASE_INSENSITIVE) != 0) {
            flagsAsString += "i";
        }
        if ((flags & Pattern.UNIX_LINES) != 0) {
            flagsAsString += "d";
        }
        if ((flags & Pattern.MULTILINE) != 0) {
            flagsAsString += "m";
        }
        if ((flags & Pattern.DOTALL) != 0) {
            flagsAsString += "s";
        }
        if ((flags & Pattern.UNICODE_CASE) != 0) {
            flagsAsString += "u";
        }
        if ((flags & Pattern.COMMENTS) != 0) {
            flagsAsString += "x";
        }

        return flagsAsString;
    }
}

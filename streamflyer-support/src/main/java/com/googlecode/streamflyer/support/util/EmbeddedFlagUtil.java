package com.googlecode.streamflyer.support.util;

import com.googlecode.streamflyer.regex.fast.Pattern;

/**
 * This class supports the creation of regular expressions with embedded flag expressions.
 * 
 * @author rwoo
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
     * @return Return the given regex enriched with an embedded flag expression that represents the given flags. If
     *         there is no flag given, the returned regex is equal to the given regex.
     * 
     */
    public String embedFlags(String regex, int flags) {
        if (flags == 0) {
            return regex;
        } else {
            return "(?" + mapFlagsToEmbeddedFlags(regex, flags) + ":" + regex + ")";
        }
    }

    /**
     * See {@link #embedFlags(String, int)}.
     */
    protected String mapFlagsToEmbeddedFlags(String regex, int flags) {
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

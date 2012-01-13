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


/*
 * @(#)PatternSyntaxException.java  1.17 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.googlecode.streamflyer.regex.fast;

import sun.security.action.GetPropertyAction;


/**
 * Unchecked exception thrown to indicate a syntax error in a regular-expression
 * pattern.
 * 
 * @author unascribed
 * @version 1.17, 10/03/23
 * @since 1.4
 * @spec JSR-51
 */

@SuppressWarnings({ "serial", "restriction" })
// NEW @SuppressWarnings ADDED BY rwoo
public class PatternSyntaxException extends IllegalArgumentException {

    private final String desc;

    private final String pattern;

    private final int index;

    /**
     * Constructs a new instance of this class.
     * 
     * @param desc A description of the error
     * @param regex The erroneous pattern
     * @param index The approximate index in the pattern of the error, or
     *        <tt>-1</tt> if the index is not known
     */
    public PatternSyntaxException(String desc, String regex, int index) {
        this.desc = desc;
        this.pattern = regex;
        this.index = index;
    }

    /**
     * Retrieves the error index.
     * 
     * @return The approximate index in the pattern of the error, or <tt>-1</tt>
     *         if the index is not known
     */
    public int getIndex() {
        return index;
    }

    /**
     * Retrieves the description of the error.
     * 
     * @return The description of the error
     */
    public String getDescription() {
        return desc;
    }

    /**
     * Retrieves the erroneous regular-expression pattern.
     * 
     * @return The erroneous pattern
     */
    public String getPattern() {
        return pattern;
    }

    private static final String nl = java.security.AccessController
            .doPrivileged(new GetPropertyAction("line.separator"));

    /**
     * Returns a multi-line string containing the description of the syntax
     * error and its index, the erroneous regular-expression pattern, and a
     * visual indication of the error index within the pattern.
     * 
     * @return The full detail message
     */
    @Override
    public String getMessage() {
        StringBuffer sb = new StringBuffer();
        sb.append(desc);
        if (index >= 0) {
            sb.append(" near index ");
            sb.append(index);
        }
        sb.append(nl);
        sb.append(pattern);
        if (index >= 0) {
            sb.append(nl);
            for (int i = 0; i < index; i++) {
                sb.append(' ');
            }
            sb.append('^');
        }
        return sb.toString();
    }

}

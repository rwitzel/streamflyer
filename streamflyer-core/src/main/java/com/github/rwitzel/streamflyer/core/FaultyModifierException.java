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
package com.github.rwitzel.streamflyer.core;

import java.util.Map;

/**
 * Thrown if an implementation of a {@link Modifier} returns invalid {@link AfterModification messages} to the
 * {@link ModifyingReader} or {@link ModifyingWriter}.
 * 
 * @author rwoo
 * @since 17.06.2011
 */
public class FaultyModifierException extends RuntimeException {

    private static final long serialVersionUID = 5841928584991080360L;

    //
    // injected properties
    //

    private Map<String, Object> description;

    //
    //
    //

    /**
     * @param message
     * @param description
     */
    public FaultyModifierException(String message, Map<String, Object> description) {
        super(message);
        this.description = description;
    }

    //
    // getter methods
    //

    /**
     * @return Returns the {@link #description}.
     */
    public Map<String, Object> getDescription() {
        return description;
    }
}

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

package com.github.rwitzel.streamflyer.experimental.stateful;

import com.github.rwitzel.streamflyer.core.AfterModification;
import com.github.rwitzel.streamflyer.core.Modifier;
import com.github.rwitzel.streamflyer.internal.thirdparty.ZzzValidate;

/**
 * This modifier has a {@link #currentState state}. The actual work of the modifier is delegated to this state object.
 * The state object processes the buffer content, then it {@link State#modify(StringBuilder, int, boolean) returns} the
 * new state and the {@link AfterModification message} to the modifying reader or writer.
 * 
 * @author rwoo
 * @since 14.09.2011
 */
public class StatefulModifier implements Modifier {

    private State currentState;

    /**
     * @param initialState
     *            the initial state of the modifier
     */
    public StatefulModifier(State initialState) {
        super();

        ZzzValidate.notNull(initialState, "initialState must not be null");

        this.currentState = initialState;
    }

    /**
     * @see com.github.rwitzel.streamflyer.core.Modifier#modify(java.lang.StringBuilder, int, boolean)
     */
    @Override
    public AfterModification modify(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit) {

        // delegate the work to the state object
        StatefulAfterModification result = currentState.modify(characterBuffer, firstModifiableCharacterInBuffer,
                endOfStreamHit);

        // update the state
        currentState = result.getNextState();

        // return the result
        return result.getAfterModification();
    }
}

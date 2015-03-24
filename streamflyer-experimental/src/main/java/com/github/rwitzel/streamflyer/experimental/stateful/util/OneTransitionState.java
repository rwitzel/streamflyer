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
package com.github.rwitzel.streamflyer.experimental.stateful.util;

import com.github.rwitzel.streamflyer.core.AfterModification;
import com.github.rwitzel.streamflyer.experimental.stateful.State;
import com.github.rwitzel.streamflyer.experimental.stateful.StatefulAfterModification;
import com.github.rwitzel.streamflyer.internal.thirdparty.ZzzValidate;

/**
 * This state has exactly one other succeeding state.
 * 
 * @author rwoo
 * 
 * @since 14.09.2011
 */
public abstract class OneTransitionState implements State {

    /**
     * The next state.
     */
    private State nextState;

    /**
     * A description for this state. This is useful for debugging.
     */
    private String description;

    public OneTransitionState(String name) {

        ZzzValidate.notNull(name, "name must not be null");
        this.description = name;
    }

    /**
     * @see com.github.rwitzel.streamflyer.experimental.stateful.State#modify(java.lang.StringBuilder, int, boolean)
     */
    @Override
    public StatefulAfterModification modify(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit) {

        return new StatefulAfterModification(innerModify(characterBuffer, firstModifiableCharacterInBuffer,
                endOfStreamHit), nextState);
    }

    public abstract AfterModification innerModify(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit);

    //
    // setter
    //

    public void setNextState(State state) {
        this.nextState = state;
    }

    //
    // override Object.*
    //

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return description;
    }
}
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

package com.googlecode.streamflyer.experimental.stateful.util;

import com.googlecode.streamflyer.experimental.stateful.State;
import com.googlecode.streamflyer.experimental.stateful.StatefulAfterModification;
import com.googlecode.streamflyer.internal.thirdparty.ZzzValidate;
import com.googlecode.streamflyer.util.ModificationFactory;

/**
 * This state does not modify the character stream and does not switch to another state.
 * 
 * @author rwoo
 * 
 * @since 14.09.2011
 */
public class IdleModifierState implements State {

    private ModificationFactory factory;

    public IdleModifierState(ModificationFactory factory) {
        super();

        ZzzValidate.notNull(factory, "factory must not be null");

        this.factory = factory;
    }

    /**
     * @see com.googlecode.streamflyer.experimental.stateful.State#modify(java.lang.StringBuilder, int, boolean)
     */
    @Override
    public StatefulAfterModification modify(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit) {

        return new StatefulAfterModification(factory.skipEntireBuffer(characterBuffer,
                firstModifiableCharacterInBuffer, endOfStreamHit), this);
    }
}

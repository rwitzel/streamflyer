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

package com.googlecode.streamflyer.experimental.stateful;

import com.googlecode.streamflyer.core.Modifier;


/**
 * Represents the state of a {@link StatefulModifier}. It carries out the actual
 * work of the {@link StatefulModifier}. Additionally, it defines the next state
 * of the {@link StatefulModifier}.
 * 
 * @author rwoo
 * 
 * @since 14.09.2011
 */
public interface State {

    /**
     * Does the same as {@link Modifier#modify(StringBuilder, int, boolean)}
     * but, additionally, the returned result contains the new state for the
     * {@link StatefulModifier}.
     */
    public StatefulAfterModification modify(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit);


}

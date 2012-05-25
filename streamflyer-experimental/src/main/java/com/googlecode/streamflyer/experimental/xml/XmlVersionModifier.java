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

package com.googlecode.streamflyer.experimental.xml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.experimental.stateful.State;
import com.googlecode.streamflyer.experimental.stateful.StatefulModifier;
import com.googlecode.streamflyer.experimental.stateful.util.IdleModifierState;
import com.googlecode.streamflyer.experimental.stateful.util.OneTransitionState;
import com.googlecode.streamflyer.internal.thirdparty.ZzzValidate;
import com.googlecode.streamflyer.util.ModificationFactory;

/**
 * Same semantics as {@link com.googlecode.streamflyer.xml.XmlVersionModifier}
 * but the implementation uses a {@link StatefulModifier}.
 * 
 * @author rwoo
 * 
 * @since 14.09.2011
 */
public class XmlVersionModifier implements Modifier {

    private StatefulModifier statefulModifier;

    //
    // constants
    //

    public final int INITIAL_NUMBER_OF_CHARACTERS = 4096;


    //
    // injected properties
    //

    protected ModificationFactory factory;

    protected String xmlVersion;

    //
    // constructors
    //

    public XmlVersionModifier(String xmlVersion, int newNumberOfChars) {

        ZzzValidate.notNull(xmlVersion, "xmlVersion must not be null");

        this.factory = new ModificationFactory(0, newNumberOfChars);
        this.xmlVersion = xmlVersion;

        // (1) create the initial state: No input read yet.
        OneTransitionState initialState = new OneTransitionState("initial") {

            @Override
            public AfterModification innerModify(StringBuilder characterBuffer,
                    int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

                // you never know how many whitespace characters are in the
                // prolog
                return factory.modifyAgainImmediately(
                        INITIAL_NUMBER_OF_CHARACTERS,
                        firstModifiableCharacterInBuffer);
            }
        };

        // (2) create version modifying state: The modifier has requested to
        // read the XML prolog. The version of prolog is modified or a prolog is
        // added if the prolog is missing.
        OneTransitionState versionModifyingState = new OneTransitionState(
                "versionModifying") {

            @Override
            public AfterModification innerModify(StringBuilder characterBuffer,
                    int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

                // TODO test for BOM because people don't care ...

                Matcher matcher = Pattern
                        .compile(
                                "<\\?xml[^>]*version\\s*=\\s*['\"]((1.0)|(1.1))['\"].*")
                        .matcher(characterBuffer);
                if (matcher.matches()) {

                    // replace version in prolog
                    characterBuffer.replace(matcher.start(1), matcher.end(1),
                            XmlVersionModifier.this.xmlVersion);
                }
                else {
                    // insert prolog
                    characterBuffer.insert(0, "<?xml version='"
                            + XmlVersionModifier.this.xmlVersion + "'>");
                }

                return factory.skipEntireBuffer(characterBuffer,
                        firstModifiableCharacterInBuffer, endOfStreamHit);
            }
        };

        // (3) create idle state: The modifier has read the XML prolog, and has
        // modified it if necessary. Nothing more to do for the modifier.
        State idleState = new IdleModifierState(factory);

        // (4) link the states: The state transitions are from "initial" to
        // "version modifying" to "idle"
        initialState.setNextState(versionModifyingState);
        versionModifyingState.setNextState(idleState);

        // 5) create the stateful modifier
        this.statefulModifier = new StatefulModifier(initialState);
    }

    //
    // override Modifier.*
    //

    /**
     * @see com.googlecode.streamflyer.core.Modifier#modify(java.lang.StringBuilder,
     *      int, boolean)
     */
    @Override
    public AfterModification modify(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        // delegate
        return statefulModifier.modify(characterBuffer,
                firstModifiableCharacterInBuffer, endOfStreamHit);
    }
}

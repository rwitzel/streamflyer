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

package com.googlecode.streamflyer.xml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.thirdparty.ZzzValidate;
import com.googlecode.streamflyer.util.ModificationFactory;

/**
 * Replaces the XML version in the XML prolog with the given XML version.
 * <p>
 * TODO please test: must this work on a BOM skipping reader / writer?
 * <p>
 * TODO should this implemented in a way so that the number of whitespace in the
 * prolog cannot break the logic of this class?
 * <p>
 * This is an alternative to {@link InvalidXmlCharacterModifier}.
 * <p>
 * The memory consumption of this modifier during the stream processing is
 * roughly given by the second argument of
 * {@link #XmlVersionModifier(String, int)} but the initial memory consumption
 * is given by {@link #INITIAL_NUMBER_OF_CHARACTERS}.
 * 
 * @author rwoo
 * @since 27.06.2011
 */
public class XmlVersionModifier implements Modifier {

    //
    // constants
    //

    public final int INITIAL_NUMBER_OF_CHARACTERS = 4096;

    /**
     * The internal state of {@link XmlVersionModifier}.
     * <p>
     * The state transitions are: from {@value #INITIAL} to
     * {@value #PROLOG_REQUEST} to {@value #NO_LONGER_MODIFYING}.
     */
    private enum XmlVersionModifierState {
        /**
         * The initial state. No input read yet.
         */
        INITIAL,

        /**
         * The modifier has requested to read the XML prolog.
         */
        PROLOG_REQUEST,

        /**
         * The modifier has read the XML prolog, modified it if necessary.
         * Nothing more to do for the modifier.
         */
        NO_LONGER_MODIFYING
    }


    //
    // injected properties
    //

    protected ModificationFactory factory;

    protected String xmlVersion;

    //
    // properties that represent the mutable state
    //

    private XmlVersionModifierState state = XmlVersionModifierState.INITIAL;


    //
    // constructors
    //

    public XmlVersionModifier(String xmlVersion, int newNumberOfChars) {

        ZzzValidate.notNull(xmlVersion, "xmlVersion must not be null");

        this.factory = new ModificationFactory(0, newNumberOfChars);
        this.xmlVersion = xmlVersion;
    }

    //
    // Modifier.* methods
    //

    /**
     * @see com.googlecode.streamflyer.core.Modifier#modify(java.lang.StringBuilder,
     *      int, boolean)
     */
    @Override
    public AfterModification modify(StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

        switch (state) {

        case NO_LONGER_MODIFYING:

            return factory.skipEntireBuffer(characterBuffer,
                    firstModifiableCharacterInBuffer, endOfStreamHit);

        case INITIAL:

            state = XmlVersionModifierState.PROLOG_REQUEST;

            // you never know how many whitespace characters are in the prolog
            return factory.modifyAgainImmediately(INITIAL_NUMBER_OF_CHARACTERS,
                    firstModifiableCharacterInBuffer);

        case PROLOG_REQUEST:

            // (Should we do aware of BOMs here? No. I consider it the
            // responsibility of the caller to provide characters without BOM.)

            Matcher matcher = Pattern.compile(
                    "<\\?xml[^>]*version\\s*=\\s*['\"]((1.0)|(1.1))['\"].*")
                    .matcher(characterBuffer);
            if (matcher.matches()) {

                // replace version in prolog
                characterBuffer.replace(matcher.start(1), matcher.end(1),
                        xmlVersion);
            }
            else {
                // insert prolog
                characterBuffer
                        .insert(0, "<?xml version='" + xmlVersion + "'>");
            }

            state = XmlVersionModifierState.NO_LONGER_MODIFYING;

            return factory.skipEntireBuffer(characterBuffer,
                    firstModifiableCharacterInBuffer, endOfStreamHit);

        default:
            throw new IllegalStateException("state " + state + " not supported");

        }

    }
}

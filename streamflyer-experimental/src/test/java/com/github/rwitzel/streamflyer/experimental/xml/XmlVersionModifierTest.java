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

package com.github.rwitzel.streamflyer.experimental.xml;

import com.github.rwitzel.streamflyer.core.Modifier;
import com.github.rwitzel.streamflyer.experimental.xml.XmlVersionModifier;

/**
 * Tests {@link XmlVersionModifier}.
 * 
 * @author rwoo
 * 
 * @since 14.09.2011
 */
public class XmlVersionModifierTest extends com.github.rwitzel.streamflyer.xml.XmlVersionModifierTest {

    /**
     * @see com.github.rwitzel.streamflyer.xml.XmlVersionModifierTest#createModifier(java.lang.String, int)
     */
    @Override
    protected Modifier createModifier(String newXmlVersion, int newNumberOfChars) {
        return new XmlVersionModifier(newXmlVersion, newNumberOfChars);
    }

}

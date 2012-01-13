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

import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;

import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.util.StringUtils;
import com.googlecode.streamflyer.xml.XmlVersionModifier;
import com.googlecode.streamflyer.xml.XmlVersionReader;

/**
 * Tests {@link XmlVersionModifier}.
 * 
 * @author rwoo
 * 
 * @since 27.06.2011
 */
public class XmlVersionModifierTest extends TestCase {

    public void testXmlVersion() throws Exception {

        // version in prolog is 1.0
        assertXmlVersionInProlog("<?xml version='1.0'>", "1.1",
                "<?xml version='1.1'>");

        // version in prolog is 1.1
        assertXmlVersionInProlog("<?xml version='1.1'>", "1.0",
                "<?xml version='1.0'>");

        // no version in prolog
        assertXmlVersionInProlog("<html version='1.1'>", "1.0",
                "<?xml version='1.0'><html version='1.1'>");

        assertXmlVersionInProlog("<html version='1.1'>", "1.1",
                "<?xml version='1.1'><html version='1.1'>");

        // version in prolog has double quotes
        assertXmlVersionInProlog("<?xml version=\"1.1\">", "1.0",
                "<?xml version=\"1.0\">");
    }

    /**
     * TODO prove that XML encoding detection by Apache commons XmlReader fails
     * for this document anyway. Then we have 'proven' we don't have a 'real
     * world' problem here ...
     * 
     * @throws Exception
     */
    public void failingtestXmlVersion_manyWhitespaceCharacters()
            throws Exception {

        String prefix = "<?xml " + StringUtils.repeat("      ", 100);
        assertXmlVersionInProlog(prefix + " version='1.0'>", "1.1", prefix
                + " version='1.1'>");
    }

    private void assertXmlVersionInProlog(String input, String newXmlVersion,
            String expectedProlog) throws Exception {

        XmlVersionReader xmlVersionReader = new XmlVersionReader(
                new StringReader(input));

        // create the reader that modifies the XML version
        ModifyingReader reader = new ModifyingReader(xmlVersionReader,
                createModifier(newXmlVersion, 5));

        String actualProlog = IOUtils.toString(reader);

        assertEquals(expectedProlog, actualProlog);
    }

    protected Modifier createModifier(String newXmlVersion, int newNumberOfChars) {
        return new XmlVersionModifier(newXmlVersion, newNumberOfChars);
    }

}

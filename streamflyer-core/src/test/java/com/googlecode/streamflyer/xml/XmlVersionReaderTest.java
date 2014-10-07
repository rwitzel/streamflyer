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

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.StringReader;

import junit.framework.TestCase;

/**
 * Tests {@link XmlVersionReader}.
 * 
 * @author rwoo
 * @since 27.06.2011
 */
public class XmlVersionReaderTest {

    @Test
    public void testNoXmlVersionInProlog() throws Exception {
        assertXmlVersion("1.0", "<html>");
        assertXmlVersion("1.0", "<html version='1.1'>");
        assertXmlVersion("1.1", "<?xml version='1.1'><html version='1.0'>");
        assertXmlVersion("1.0", "");
    }

    @Test
    public void testXmlVersion10InProlog() throws Exception {
        assertXmlVersion("1.0", "<?xml encoding='hossa' version='1.0' standalone='true'");
        assertXmlVersion("1.0", "<?xml encoding=\"hossa\" version=\"1.0\" standalone=\"true\"");

        assertXmlVersion("1.0", "<?xml encoding='hossa' version = '1.0' standalone='true'");
        assertXmlVersion("1.0", "<?xml encoding=\"hossa\" version  =  \"1.0\" standalone=\"true\"");

    }

    @Test
    public void testXmlVersion11InProlog() throws Exception {
        assertXmlVersion("1.1", "<?xml encoding='hossa' version='1.1' standalone='true'");
        assertXmlVersion("1.1", "<?xml encoding=\"hossa\" version=\"1.1\" standalone=\"true\"");

        assertXmlVersion("1.1", "<?xml encoding='hossa' version = '1.1' standalone='true'");
        assertXmlVersion("1.1", "<?xml encoding=\"hossa\" version  =  \"1.1\" standalone=\"true\"");
    }

    private void assertXmlVersion(String expectedXmlVersion, String xml) throws Exception {
        XmlVersionReader reader = new XmlVersionReader(new StringReader(xml));

        assertEquals(expectedXmlVersion, reader.getXmlVersion());

        reader.close();
    }

}

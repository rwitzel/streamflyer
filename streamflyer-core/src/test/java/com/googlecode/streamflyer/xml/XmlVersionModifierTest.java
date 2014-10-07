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

import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.XmlStreamReader;

import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.util.StringUtils;

/**
 * Tests {@link XmlVersionModifier}.
 * 
 * @author rwoo
 * @since 27.06.2011
 */
public class XmlVersionModifierTest extends TestCase {

    public void testXmlVersion() throws Exception {

        // version in prolog is 1.0
        assertXmlVersionInProlog("<?xml version='1.0'>", "1.1", "<?xml version='1.1'>");

        // version in prolog is 1.1
        assertXmlVersionInProlog("<?xml version='1.1'>", "1.0", "<?xml version='1.0'>");

        // no version in prolog
        assertXmlVersionInProlog("<html version='1.1'>", "1.0", "<?xml version='1.0'><html version='1.1'>");

        assertXmlVersionInProlog("<html version='1.1'>", "1.1", "<?xml version='1.1'><html version='1.1'>");

        // version in prolog has double quotes
        assertXmlVersionInProlog("<?xml version=\"1.1\">", "1.0", "<?xml version=\"1.0\">");
    }

    public void testXmlVersion_utf8Bom_withoutByteSkippingReader() throws Exception {

        byte UTF8_BOM_BYTE_1 = (byte) 0xEF;
        byte UTF8_BOM_BYTE_2 = (byte) 0xBB;
        byte UTF8_BOM_BYTE_3 = (byte) 0xBF;

        // version in prolog is 1.0
        String input = "<?xml version='1.0'>";
        byte[] bytes = input.getBytes();
        byte[] bytesWithUtf8Bom = new byte[bytes.length + 3];
        bytesWithUtf8Bom[0] = UTF8_BOM_BYTE_1;
        bytesWithUtf8Bom[1] = UTF8_BOM_BYTE_2;
        bytesWithUtf8Bom[2] = UTF8_BOM_BYTE_3;
        System.arraycopy(bytes, 0, bytesWithUtf8Bom, 3, bytes.length);
        String inputWithBom = new String(bytesWithUtf8Bom);
        // System.out.println("inputWithBom: " + inputWithBom);
        try {
            assertXmlVersionInProlog(inputWithBom, "1.1", "<?xml version='1.1'>");
            fail("AssertionError expected");
        } catch (ComparisonFailure e) {
            // OK
        }
    }

    public void testXmlVersion_utf8Bom() throws Exception {

        byte UTF8_BOM_BYTE_1 = (byte) 0xEF;
        byte UTF8_BOM_BYTE_2 = (byte) 0xBB;
        byte UTF8_BOM_BYTE_3 = (byte) 0xBF;

        // version in prolog is 1.0
        String input = "<?xml version='1.0'>";
        byte[] bytes = input.getBytes("UTF-8");
        byte[] bytesWithBom = new byte[bytes.length + 3];
        bytesWithBom[0] = UTF8_BOM_BYTE_1;
        bytesWithBom[1] = UTF8_BOM_BYTE_2;
        bytesWithBom[2] = UTF8_BOM_BYTE_3;
        System.arraycopy(bytes, 0, bytesWithBom, 3, bytes.length);
        // System.out.println("inputWithBom: " + inputWithBom);
        assertXmlVersionInProlog(bytesWithBom, "1.1", "<?xml version='1.1'>");
    }

    public void testXmlVersion_utf16BeBom() throws Exception {

        byte UTF16BE_BOM_BYTE_1 = (byte) 0xFE;
        byte UTF16BE_BOM_BYTE_2 = (byte) 0xFF;

        // version in prolog is 1.0
        String input = "<?xml version='1.0'>";
        byte[] bytes = input.getBytes("UTF-16BE");
        byte[] bytesWithBom = new byte[bytes.length + 2];
        bytesWithBom[0] = UTF16BE_BOM_BYTE_1;
        bytesWithBom[1] = UTF16BE_BOM_BYTE_2;
        System.arraycopy(bytes, 0, bytesWithBom, 2, bytes.length);
        // System.out.println("inputWithBom: " + inputWithBom);
        assertXmlVersionInProlog(bytesWithBom, "1.1", "<?xml version='1.1'>");
    }

    public void testXmlVersion_utf16LeBom() throws Exception {

        byte UTF16LE_BOM_BYTE_1 = (byte) 0xFF;
        byte UTF16LE_BOM_BYTE_2 = (byte) 0xFE;

        // version in prolog is 1.0
        String input = "<?xml version='1.0'>";
        byte[] bytes = input.getBytes("UTF-16LE");
        byte[] bytesWithBom = new byte[bytes.length + 2];
        bytesWithBom[0] = UTF16LE_BOM_BYTE_1;
        bytesWithBom[1] = UTF16LE_BOM_BYTE_2;
        System.arraycopy(bytes, 0, bytesWithBom, 2, bytes.length);
        // System.out.println("inputWithBom: " + inputWithBom);
        assertXmlVersionInProlog(bytesWithBom, "1.1", "<?xml version='1.1'>");
    }

    private void assertXmlVersionInProlog(byte[] input, String newXmlVersion, String expectedProlog) throws Exception {

        XmlVersionReader xmlVersionReader = new XmlVersionReader(new XmlStreamReader(new ByteArrayInputStream(input)));

        // create the reader that modifies the XML version
        ModifyingReader reader = new ModifyingReader(xmlVersionReader, createModifier(newXmlVersion, 5));

        String actualProlog = IOUtils.toString(reader);

        assertEquals(expectedProlog, actualProlog);
    }

    /**
     * TODO prove that XML encoding detection by Apache commons XmlReader fails for this document anyway. Then we have
     * 'proven' we don't have a 'real world' problem here ...
     * 
     * @throws Exception
     */
    public void testXmlVersion_XmlPrologTooLong_manyWhitespaceCharacters() throws Exception {

        String prefix = "<?xml " + StringUtils.repeat("      ", 1000);
        try {
            assertXmlVersionInProlog(prefix + " version='1.0'>", "1.1", prefix + " version='1.1'>");
            fail("XmlPrologRidiculouslyLongException expected");
        } catch (XmlPrologRidiculouslyLongException e) {
            assertTrue(e.getMessage().contains("the XML prolog of an XML document is too long:"));
            assertTrue(e.getMessage().contains("<?xml                                         "));
        }
    }

    private void assertXmlVersionInProlog(String input, String newXmlVersion, String expectedProlog) throws Exception {

        XmlVersionReader xmlVersionReader = new XmlVersionReader(new StringReader(input));

        // create the reader that modifies the XML version
        ModifyingReader reader = new ModifyingReader(xmlVersionReader, createModifier(newXmlVersion, 5));

        String actualProlog = IOUtils.toString(reader);

        assertEquals(expectedProlog, actualProlog);
    }

    protected Modifier createModifier(String newXmlVersion, int newNumberOfChars) {
        return new XmlVersionModifier(newXmlVersion, newNumberOfChars);
    }

    public void testExampleFromJavadoc() throws Exception {

        byte UTF16LE_BOM_BYTE_1 = (byte) 0xFF;
        byte UTF16LE_BOM_BYTE_2 = (byte) 0xFE;

        // version in prolog is 1.0
        String input = "<?xml version='1.0'>";
        byte[] bytes = input.getBytes("UTF-16LE");
        byte[] bytesWithBom = new byte[bytes.length + 2];
        bytesWithBom[0] = UTF16LE_BOM_BYTE_1;
        bytesWithBom[1] = UTF16LE_BOM_BYTE_2;
        System.arraycopy(bytes, 0, bytesWithBom, 2, bytes.length);

        // choose the input stream to modify
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytesWithBom);

        // wrap the input stream by BOM skipping reader
        Reader reader = new XmlStreamReader(inputStream);

        // create the reader that changes the XML version to 1.1
        ModifyingReader modifyingReader = new ModifyingReader(reader, new XmlVersionModifier("1.1", 8192));

        // use the modifying reader instead of the original reader
        String xml = IOUtils.toString(modifyingReader);

        assertTrue(xml.startsWith("<?xml version='1.1'"));
    }

}

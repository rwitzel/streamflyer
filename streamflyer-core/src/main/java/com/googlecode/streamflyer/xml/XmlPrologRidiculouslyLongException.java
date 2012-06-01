/*
 * $Id$
 */
package com.googlecode.streamflyer.xml;

/**
 * Thrown if the XML prolog of an XML document is very long.
 * 
 * @author rwoo
 * @version $Rev$ $Date$
 * @since 01.06. 2012
 */
@SuppressWarnings("serial")
public class XmlPrologRidiculouslyLongException extends RuntimeException {

    public XmlPrologRidiculouslyLongException(String startOfXmlProlog) {
        super("the XML prolog of an XML document is too long: "
                + startOfXmlProlog);

    }
}

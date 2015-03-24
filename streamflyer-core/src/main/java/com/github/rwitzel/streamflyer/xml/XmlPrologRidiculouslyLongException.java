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
/*
 * $Id$
 */
package com.github.rwitzel.streamflyer.xml;

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
        super("the XML prolog of an XML document is too long: " + startOfXmlProlog);

    }
}

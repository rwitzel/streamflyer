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

package com.googlecode.streamflyer.regex.fast;

import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;

import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.regex.OnStreamMatcher;

/**
 * Tests {@link OnStreamExtendedMatcher}.
 * 
 * @author rwoo
 * 
 * @since 28.06.2011
 */
public class RegexModifierExtendedTest extends
		com.googlecode.streamflyer.regex.RegexModifierTest {

	@Override
	protected OnStreamMatcher createMatcher(String regex) {
		Matcher matcher = Pattern.compile(regex).matcher("");
		matcher.useTransparentBounds(true);
		return new OnStreamExtendedMatcher(matcher);
	}

	public void testExampleFromHomepage_usageRegexFast() throws Exception {

		// choose the character stream to modify
		Reader originalReader = new StringReader("edit\n\tstream");

		// we use FastRegexModifier instead of RegexModifier
		Modifier fastModifier = new FastRegexModifier("edit(\\s+)stream",
				Pattern.DOTALL, "modify$1stream");

		// create the modifying reader that wraps the original reader
		Reader modifyingReader = new ModifyingReader(originalReader,
				fastModifier);

		// use the modifying reader instead of the original reader
		String output = IOUtils.toString(modifyingReader);
		assertEquals("modify\n\tstream", output);
	}
}

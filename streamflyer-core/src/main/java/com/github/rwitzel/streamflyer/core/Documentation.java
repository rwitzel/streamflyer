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

package com.github.rwitzel.streamflyer.core;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import com.github.rwitzel.streamflyer.regex.MatchProcessor;
import com.github.rwitzel.streamflyer.regex.RegexModifier;
import com.github.rwitzel.streamflyer.regex.addons.stateful.StateMachine;
import com.github.rwitzel.streamflyer.util.ModifyingReaderFactory;
import com.github.rwitzel.streamflyer.util.ModifyingWriterFactory;
import com.github.rwitzel.streamflyer.xml.InvalidXmlCharacterModifier;
import com.github.rwitzel.streamflyer.xml.XmlVersionModifier;

/**
 * Read this first, before you use Streamflyer. This Javadoc content is also available on the <a
 * href="http://code.google.com/p/streamflyer/">Streamflyer</a> web page.
 * <p>
 * <h1>Contents</h1>
 * <p>
 * <b> <a href="#g1">1. When shall I use this library?</a><br/>
 * <a href="#g3">2. How do I modify character streams?</a><br/>
 * <a href="#g4">3. Which type of modifications are provided out-of-the-box?</a> <br/>
 * <a href="#g5">4. What is the benefit of this library in comparison to sed or Perl?</a><br/>
 * <a href="#g7">5. Can I use regular expressions to modify streams?</a><br/>
 * <a href="#g8">6. How much memory does a modifying reader or writer consume?</a><br/>
 * <a href="#g9">7. How big is the performance overhead of a modifying reader or modfying writer?</a><br/>
 * <a href="#g10">8. How do I process regex patterns found in the character stream a more specific way?</a><br/>
 * <a href="#g11">9. Is there a way to modify byte streams instead of character streams, i.e. is there something like a
 * modifying input stream or output stream?</a><br/>
 * </b> <!-- ++++++++++++++++++++++++++++++ -->
 * <h3 id="g1">1. When shall I use this library?</h3>
 * <p>
 * This library provides filter that modify character streams on-the-fly with a small memory foot-print using the Java
 * interfaces {@link Reader} and {@link Writer}. Additionally, you can use these filters to report some patterns found
 * in character streams without modifying the content of the character streams.
 * <p>
 * Use this library only if you use Java 6 or higher.
 * <p>
 * If you want to optimize the speed of the stream modifications and you don't care of memory consumption (assuming you
 * are in the cloud), then load the entire content of the stream into the memory and modify the stream content there. In
 * this case this library is not the right choice for you because this library minimizes the use of memory at the
 * expense of a reduced speed of the stream modifications.
 * <h3 id="g3">2. How do I modify character streams?</h3>
 * <p>
 * First you have to implement a callback that makes the desired modifications on a small part of the entire character
 * stream. The contract of that callback is defined by {@link Modifier}. For some common use cases pre-defined modifiers
 * exist.
 * <p>
 * Second, wrap the original reader or writer with a modifying reader or writer.
 * <p>
 * Example for a modifying reader that removes invalid characters from XML files:
 * <code><pre class="prettyprint lang-java">// choose the character stream to modify
Reader originalReader = ... // this reader is connected to the original data source

// select the modifier
Modifier myModifier = new InvalidXmlCharacterModifier(1024, "", "1.0", false);

// create the modifying reader that wraps the original reader
Reader modifyingReader = new ModifyingReader(originalReader, myModifier);

... // use the modifying reader instead of the original reader</pre></code>
 * <h3 id="g4">3. Which modifiers are provided out-of-the-box?</h3>
 * <p>
 * The following modifications are provided out-of-the-box.
 * <ul>
 * <li>{@link RegexModifier}: Replaces characters using regular expressions. Some add-ons provide more functionality.
 * <li>{@link XmlVersionModifier}: Changes the version in the prolog of XML files.
 * <li>{@link InvalidXmlCharacterModifier}: Replaces invalid XML characters in XML files or removes them from XML files.
 * <li>
 * <code>RangeFilterModifier</code> (experimental module): Removes everything from the stream apart from the content
 * between a start tag and an end tag.
 * </ul>
 * The factories {@link ModifyingReaderFactory} and {@link ModifyingWriterFactory} ease the use of these modifiers.
 * <p>
 * Do you know another common use case that should be supported by this library? Then, please let the author know. A
 * corresponding modifier might be added to the library in a future version.
 * <h3 id="g5">4. What is the benefit of this library in comparison to sed or awk or Perl?</h3>
 * <p>
 * The line-based approach of <em>sed</em> consumes a lot of memory if you modify a large XML file that does contain no
 * or only a few line breaks. The modifications <em>sed</em> can carry out are limited to replacements using regular
 * expressions. This library instead supports any kind of modification and consumes not much memory how big the
 * processed file may be. In comparison to <em>awk</em> and <em>perl</em>, this library seamlessly integrates with Java
 * software that uses the interfaces for character streams ({@link Reader} and {@link Writer}).
 * <h3 id="g7">5. Can I use regular expressions to modify streams?</h3>
 * <p>
 * Yes. Use the {@link RegexModifier} directly or, more convenient, use the {@link ModifyingReaderFactory} or the
 * {@link ModifyingWriterFactory}.
 * <h3 id="g8">6. How much memory does a modifying reader or writer consume?</h3>
 * <p>
 * This depends entirely on the used {@link Modifier} and the task the modifier has to fulfill. Basically the modifier
 * defines the number of characters to process at once by {@link AfterModification#getNewNumberOfChars()}. This is the
 * minimum of characters in the memory. The API documentation of each modifier implementation should make clear how much
 * memory the modifier consumes at most. Double that number (the maximum number of characters to be processed at once),
 * then you got the maximum capacity of the used character buffer, i.e. the number of characters in the memory. Hint:
 * The number must be doubled due to the internal implementation of Java's {@link StringBuilder#ensureCapacity(int)}.
 * <h3 id="g9">7. How big is the performance overhead of a modifying reader or modifying writer that does nothing (no
 * modification, no logging etc.) in comparison to the use of a reader or writer that reads its data directly from
 * memory?</h3>
 * <p>
 * If you use such an idle modifier to process 10 million characters using a buffer of 500 characters, then the overhead
 * is 0.2 seconds using a modifying reader and 0.4 seconds using a modifying writer (normal PC, 2011).
 * <p>
 * <h3 id="g10">8. How do I process regex patterns found in the character stream?</h3>
 * <p>
 * By default {@link RegexModifier} will replace the matches with the given replacement. If you want to do more or
 * something else (logging, arbitrary insertions), then implement your own {@link MatchProcessor}, and use it with the
 * {@link RegexModifier}. There are already some add-ons like {@link StateMachine} which ease the customization of
 * modifiers. Have a look at the project page for more information about customization of modifiers.
 * <p>
 * <h3 id="g11">9. Is there a way to modify byte streams instead of character streams, i.e. is there something like a
 * modifying input stream or output stream?</h3>
 * <p>
 * No, at the moment there is no special code for byte streams. An approach could be to define your own
 * {@link CharsetDecoder} and {@link CharsetEncoder} , then convert the byte stream to a pseudo character stream, modify
 * the character stream, and then convert the pseudo characters back to bytes. Is the performance of this approach still
 * sufficient despite the two conversions? Let me know if you tried this.
 * 
 * @author rwoo
 * @since 13.09.2011
 */
public class Documentation {
    // nothing to do here
}

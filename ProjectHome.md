+++ 2014-10-08: Streamflyer [1.1.3](ReleaseNotes.md) released. Available in Maven Central. +++

+++ 2013-11-10: New wiki page: [How to implement a custom modifier](ImplementCustomModifier.md) for [release 1.1.1](ReleaseNotes.md) +++

+++ 2013-03-10: Regular expression on `InputStream`: Differences to Java Regex [explained](#Look-behind_constructs.md) +++

![http://wiki.streamflyer.googlecode.com/hg/images/streamflyer-body.png](http://wiki.streamflyer.googlecode.com/hg/images/streamflyer-body.png)

## What it does ##

Wraps Java's Reader and Writer to modify characters in a stream - to apply regular expressions, to fix XML documents, whatever you want to do. Streamflyer is a convenient alternative to Java's [FilterReader](http://docs.oracle.com/javase/6/docs/api/java/io/FilterReader.html) and [FilterInputStream](http://docs.oracle.com/javase/6/docs/api/java/io/FilterInputStream.html).

## Contents ##


## Usage ##

An example:
```
// choose the character stream to modify
Reader originalReader = ... // this reader is connected to the original data source

// select the modifier of your choice
Modifier myModifier = new RegexModifier("edit stream", 0, "modify stream");

// create the modifying reader that wraps the original reader
Reader modifyingReader = new ModifyingReader(originalReader, myModifier);

... // use the modifying reader instead of the original reader
```

In this example the chosen
[Modifier](http://javadoc.streamflyer.googlecode.com/hg/index.html?com/googlecode/streamflyer/core/Modifier.html)
replaces the string "edit stream" with "modify stream". You can write your own custom modifier or use a modifier that is shipped with Streamflyer, like the
[RegexModifier](http://javadoc.streamflyer.googlecode.com/hg/index.html?com/googlecode/streamflyer/regex/RegexModifier.html)
that replaces characters by using regular expressions.

The same can be done with a Writer instead of a Reader.

More information about the usage you find in the [API documentation](http://javadoc.streamflyer.googlecode.com/hg/index.html?com/googlecode/streamflyer/core/Documentation.html).

## Implement custom modifiers ##

Read ImplementCustomModifier.

## Compatibility to Java's Regular Expressions package ##

[RegexModifier](http://javadoc.streamflyer.googlecode.com/hg/index.html?com/googlecode/streamflyer/regex/RegexModifier.html) internally uses Java's Regex package. This is why it supports
pattern flags, quantifiers, capturing groups the same way as Java does. An exception are look-behinds, see Section [#Known\_limitations](#Known_limitations.md).

There is a small tutorial: AdvancedRegularExpressionsExample.

## Speed up your regular expressions ##

Have a look at [streamflyer-regex-fast](http://code.google.com/p/streamflyer-regex-fast/).


## Fix invalid characters in XML streams ##

Sometimes you have to open XML documents that contain characters that are allowed in XML 1.1 documents but not allowed in XML 1.0 documents. And sometimes you have to open XML documents that contain characters that are entirely forbidden. For these kind of documents some pre-defined modifier exist so that the modified stream can be opened by standard XML parsers:

  * [InvalidXmlCharacterModifier](http://javadoc.streamflyer.googlecode.com/hg/index.html?com/googlecode/streamflyer/xml/InvalidXmlCharacterModifier.html) - replaces the invalid characters
  * [XmlVersionModifier](http://javadoc.streamflyer.googlecode.com/hg/index.html?com/googlecode/streamflyer/xml/XmlVersionModifier.html) - fixes the XML version in the prolog of the XML stream


## Modify byte streams ##

Streamflyer does not support modifications of byte streams out of the box.
But you can convert your byte stream to a character stream, wrap the character stream by a modifying character stream, and then convert the character stream back to a byte stream. Don't expect an outstanding performance by this approach.

You find examples for modifying both InputStream and OutputStream on HowToModifyByteStreams.


## Download ##

Go to the
[Installation page](http://code.google.com/p/streamflyer/wiki/Installation)
to get the latest release. This page provides also the Maven coordinates, prerequisites, and information about dependencies to other libraries.

## Known limitations ##

### RegexModifier ###

#### Look-behind constructs ####

If your regular expression contains look-behind constructs like

  * ^
  * \b
  * \B
  * (?<=X
  * (?<!X)

then Streamflyer's behaviour (version 1.1.1) differs from the behaviour of Java's Regex package.

What exactly is the difference? Java's String.replaceAll() finds all matches in the original string and creates a modified string in parallel. In contrast to this, Streamflyer looks for the next match, applies the replacement on the original string, then looks for the next match behind the replacement. Therefore, if the regular expression contains look-behind constructs this can lead to varying results.

Examples:

| **Regex** | **Replacement** | **Input** | **Output (Java Regex)** | **Output (Streamflyer)** |
|:----------|:----------------|:----------|:------------------------|:-------------------------|
| ^a | (the empty string) | aaabb | aabb | bb |
| (?<=foo)bar | foo | foobarbar | foofoobar | foofoofoo |

Streamflyer's behaviour is unexpected for Java users and ,therefore, this behaviour could be changed by the next major release. But as long nobody asks for a new release, as long no new major release is planned.

If you want to use look-behind constructs, please keep in mind that you can replace them with other expressions in many cases. As Streamflyer reads the entire stream, look-behind constructs are not of big use.

#### Boundary matcher \G ####

The boundary matcher that matches the end of the previous match
(\G) is not supported yet.

### XmlVersionModifier ###

This modifier does not work for XML documents with a prolog that contains more than 4096 characters.

## Questions, Suggestions, Issues ##

Questions and suggestions are welcome and can be sent to the [discussion group](http://groups.google.com/group/streamflyer-discuss). Issues can be reported on the  [Issues page](http://code.google.com/p/streamflyer/issues/list) of this project.

Some answered questions can be found in the [FAQ](FAQ.md).

Please give me feedback of any kind. It is highly appreciated.

## Future enhancements, third party modifiers ##

The next release will change the behaviour of RegexModifier regarding
[#Look-behind\_constructs](#Look-behind_constructs.md).

Please let us know if you made a modifier that could be useful for others. Such modifiers could ...
  * [normalize unicode](http://docs.oracle.com/javase/tutorial/i18n/text/normalizerapi.html), i.e. transform characters into their canonical composed or decomposed form
  * [include nested content](http://stackoverflow.com/questions/11084382/recursively-replace-regex-find-with-path-in-the-regex), i.e. markup in the stream is replaced with the content of another stream which itself can contain such markup

The [API documentation](http://javadoc.streamflyer.googlecode.com/hg/index.html?com/googlecode/streamflyer/core/Documentation.html) is full of typos. This will be fixed by the next release, hopefully.

## Acknowledgments ##

The logo is based on drafts by K. Dabels.
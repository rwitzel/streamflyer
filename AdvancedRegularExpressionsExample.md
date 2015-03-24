## Apply a regular expression on a Java Reader and a Java Writer ##

Modify a Reader:
```
// choose the character stream to modify
Reader originalReader = ... // this reader is connected to the original data source

// select the modifier of your choice
Modifier myModifier = new RegexModifier("up.*to.*date", Pattern.DOTALL, "up-to-date");

// create the modifying reader that wraps the original reader
Reader modifyingReader = new ModifyingReader(originalReader, myModifier);

... // use the modifying reader instead of the original reader
```

Modifying a Writer is analog.


### Advanced example with regular expressions ###

This section discusses some shortcomings in the example mentioned on the start page and demonstrates how to get rid of them by using **pattern flags, different kinds of quantifiers, capturing groups, and look-behinds**. So you should be familiar with Java's regular expressions.

First the regular expression does not match if there is a line break or a tab between `edit` and `stream`. An improved regular expression would be `edit\\sstream`.

```
// first improvement
Modifier myModifier = new RegexModifier("edit\\sstream", 0, "modify stream");
```

Second the regular expression does not match if there are more then one white space character between `edit` and `stream`. The improved regular expression would use a possessive quantifier as in `edit(\\s+stream)` with `modify$1` as replacement. The chosen replacement expression uses a group so that the original whitespace is preserved.

```
// second improvement
Modifier myModifier = new RegexModifier("edit(\\s++stream)", 0, "modify$1");
```

Note: Here a possessive quantifier is expected to be faster than a greedy or reluctant quantifier (read [Quantifiers](http://docs.oracle.com/javase/tutorial/essential/regex/quant.html)).

Third the regular expression should match only if there is a whitespace character before `edit` so that words like `credit` are not matched. Let's use a positive look-behind to implement this (see section _Special constructs_ in Java's
[Pattern](http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html)):

```
// third and final improvement
Modifier myModifier = new RegexModifier("(?<=\\s)edit(\\s++stream)", 0, "modify$1", 1, 2048);
```

We had to specify the maximum length of the look-behind so that the modifier knows how to organize its internal buffer. Additionally we specified the initial size of the internal buffer. During the stream processing the internal buffer will grow and shrink as necessary. You will vary the size to find a value that maximizes the performance for your own regular expressions.

Finally, a warning. Think about twice before using greedy quantifiers in your regular expressions as greedy quantifiers try to find the longest possible match. That is a greedy quantifier might read almost the entire content that is left in the stream into the internal buffer of streamflyer at once and ,therefore, a lot of memory might be consumed.

```
// Don't do this! This example uses a greedy quantifier on a dot
Modifier myModifier = new RegexModifier("edit.*stream", 0, "modify stream");
```
#### Are $ and ^ supported? ####

Yes.

If you use the flag `Pattern.MULTI_LINE`, then $ and ^ match the end of line, the end of stream resp. the start of the stream.

If you don't use this flag, then $ and ^ match the end of stream resp. the start of the stream.

Attention! There is an limitation regarding [look-behind constructs](../../#user-content-look-behind-constructs) at the moment (Streamflyer version 1.2.0).

#### Can I share Modifier instances between threads? ####

This depends on the used Modifier, e.g. a `RegexModifier` instance cannot be shared between threads, i.e. it is not thread-safe.

Use thread-specific instances if the modifier is not thread-safe.

#### How do the methods flush() and close() work on a ModifyingWriter? ####

The method flush() simply delegates to the underlying writer but does not force the writer to flush characters to the underlying writer.

But when are characters written to the underlying writer?

If you write on a modifying writer, the writer decides on its own when to write to the underlying writer. The decision depends on the used modifier.

Assume we use a `RegexModifier` and this modifier is configured to process 8000 characters at once. Only if you write 8000 characters in total to the writer, the characters are handed over to the modifier. The modifier informs the writer how many characters have to be written to the underlying writer. Then these characters are written to the underlying writer.

But how do I force the writer to flush everything?

Use close(). When you close the writer, the modifier is informed about that and ,therefore, both modifier and writer know that all remaining characters must be modified and written subsequently. This is the way to 'flush' everything.

The latest version of Streamflyer will contain the additional method `close(closeUnderlyingWriter)`. Pass `false` to this method when you do not want to close the underlying writer.

#### How do I print the correct position (line and column) of a regex match? ####

Look at the support module. There is a working example.

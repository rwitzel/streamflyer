# How to modify byte streams? #

The following sections assume that your stream contains binary data, not encoded characters.

## Character set issues ##

If byte streams shall be converted to character streams you must NOT use UTF-8, UTF-16, or ASCII because the encoders and decoders of these character sets would either claim about invalid data or destroy your data (depending on their configuration).

If you choose ISO-8859-1 the bytes in your stream can be properly converted to characters and after the modifications  converted back to bytes.

You must take care that your modifications do no insert characters that  cannot be converted back to bytes by the chosen character set.


## Modify an InputStream ##

Example:
```
String charsetName = "ISO-8859-1";

// get byte stream
InputStream originalByteStream = ...;

// byte stream as character stream
Reader originalReader = new InputStreamReader(originalByteStream, charsetName);

// create the modifying reader
Reader modifyingReader = new ModifyingReader(originalReader, new RegexModifier("\\s+", 0, " "));

// character stream as byte stream
InputStream modifyingByteStream = new ReaderInputStream(modifyingReader, charsetName);
```


## Modify an OutputStream ##

Example:
```
String charsetName = "ISO-8859-1";

// get byte stream
ByteArrayOutputStream targetByteStream = new ByteArrayOutputStream();

// byte stream as character stream
Writer targetWriter = new OutputStreamWriter(targetByteStream, charsetName);

// create the modifying writer
Writer modifyingWriter = new ModifyingWriter(targetWriter, new RegexModifier("\\s+", 0, " "));

// character stream as byte stream
OutputStream modifyingByteStream = new WriterOutputStream(modifyingWriter, charsetName);
```

Both examples show how to normalize whitespace in byte streams. These examples are not really useful as this normalization could be easily implemented with a FilterInputStream as well.
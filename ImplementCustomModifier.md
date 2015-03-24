# How to implement a custom modifier #

## Introduction ##

The implementation of a custom modifier from scratch is not easy because the protocol between modifier and modifying reader respectively writer is quite difficult.

Therefore, there is a number of additional classes built on top of the `RegexModifier` (since 1.1.1, November 2013) . These classes provide much easier ways to implement  custom modifiers. As regular expressions are a powerful way to express search queries and to modify streams, these classes will help you in many cases.

Hint: Have a look at the unit tests. You will find useful examples there.


## Basics ##

The `RegexModifier` uses an `OnStreamMatcher` and a `MatchProcessor`. The `OnStreamMatcher` determines what is searched (by wrapping a regular expression). The `MatchProcessor` determines how the match is processed.

Many of the additional classes use a special `MatchProcessor`.

## Token Processing ##

The `TokenProcessor` allows the user to search for many regular expressions in parallel. Each regular expression is called a token.

Each token gets assigned a default match processor, like the `ReplacingProcessor` or the `DoNothingProcessor`. But it is also possible to do more processing or different processing when you subclass `TokenProcessor`.

## Embedded Flags ##

Tokens do not have an API for explicit flags, like `Pattern.DOTALL`. Therefore, you can use `EmbeddedFlagUtil` to convert explicit flags to embedded flags. Read about Java class `Pattern` to learn more about embedded flags.

## Stateful token processing ##

Sometimes you do not want to search for all tokens in parallel but only for a subset of tokens.

Assume you want to replace some words in specific sections of a streamed document. When the modifier reads the text within these specific sections the modifier should look for the words. Outside of sections the modifier should look only for the start of the next section but not for the words.

This scenario can be implemented with `StateMachine`. For a `StateMachine` you define transitions between tokens and the initial state. Apart from that, a `StateMachine` works exactly like an `TokenProcessor`.

## No match ##

A noMatch is any text between matched tokens.
Sometimes you are interested in that text. In this case, you have to deal with the package `nomatch`.

Attention! If you want to modify streams on the appearance of a noMatch, you leave the comfort zone. This is because you have to create `AfterModification` objects which are part of the above-mentioned difficult protocol between a modifier and the modifying reader resp. writer.

## Handler ##

If you are interested in noMatches and you want to do rather complex stream modifications, you will find out that your logic will be spread across many classes so that the idea of your modifications may not become clear to other developers.

In this case you might be interested in a callback API as a single point to implement the entire logic. This callback API is `Handler`.

## Look-Behind constructs ##

Finally, a general hint for using the `RegexModifier`.

On the start page the restriction for look-behind constructs is mentioned. Thus the recommendation: Avoid look-behind constructs. Try to include them as normal group in your regular expressions.

## Greedy quantifiers ##

This cannot be said often enough.

If you use something like `.*` in your regular expression the chance is high that your modifier loads almost the entire content of the stream into the memory at once. This is because the asterisk is a greedy operator. Use reluctant quantifiers instead. Study the documentation of Java's Pattern class if you are not familiar with the difference between greedy quantifiers and reluctant quantifiers.

## More help needed? ##

Please contact us via the discussion group.
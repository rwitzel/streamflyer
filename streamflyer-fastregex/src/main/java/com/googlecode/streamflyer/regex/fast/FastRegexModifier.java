package com.googlecode.streamflyer.regex.fast;

import com.googlecode.streamflyer.regex.OnStreamStandardMatcher;
import com.googlecode.streamflyer.regex.RegexModifier;
import com.googlecode.streamflyer.regex.ReplacingProcessor;

/**
 * Uses the fast OnStreamExtendedMatcher instead of the slower
 * {@link OnStreamStandardMatcher}.
 * 
 * @author rwoo
 * 
 */
public class FastRegexModifier extends RegexModifier {

	public FastRegexModifier(String regex, int flags, String replacement) {
		this(regex, flags, replacement, 0, 2048);
	}

	public FastRegexModifier(String regex, int flags, String replacement,
			int minimumLengthOfLookBehind, int newNumberOfChars) {
		super();

		Matcher jdkMatcher = Pattern.compile(regex, flags).matcher("");
		jdkMatcher.useTransparentBounds(true);
		init(new OnStreamExtendedMatcher(jdkMatcher), new ReplacingProcessor(
				replacement), minimumLengthOfLookBehind, newNumberOfChars);
	}

}

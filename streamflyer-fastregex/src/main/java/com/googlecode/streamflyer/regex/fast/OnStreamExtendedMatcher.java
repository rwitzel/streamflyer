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

import com.googlecode.streamflyer.regex.OnStreamMatcher;

/**
 * Implements {@link OnStreamMatcher} using an extended
 * <code>java.util.regex</code> package. See class comment at
 * {@link OnStreamMatcher}.
 * <p>
 * This implementation is the fastest implementation of {@link OnStreamMatcher}
 * at the moment - it takes less than twice as much time as
 * {@link String#replaceAll(String, String)} needs to match and replace data in
 * an character stream that is read entirely into a {@link CharSequence}.
 * 
 * @author rwoo
 * 
 * @since 20.06.2011
 */
public class OnStreamExtendedMatcher implements OnStreamMatcher {

	//
	// injected properties
	//

	protected Matcher matcher;

	//
	// constructors
	//

	/**
	 * @param matcher
	 */
	public OnStreamExtendedMatcher(Matcher matcher) {
		super();
		this.matcher = matcher;
	}

	/**
	 * @see com.googlecode.streamflyer.regex.OnStreamMatcher#reset(java.lang.CharSequence)
	 */
	@Override
	public void reset(CharSequence input_) {
		matcher.reset(input_);
	}

	/**
	 * 
	 <code><pre>
        boolean result = parentPattern.matchRoot.match(this, from, text);
     </pre></code>
	 * 
	 * @see com.googlecode.streamflyer.regex.OnStreamMatcher#findUntilMatchOrHitEnd(int)
	 */
	@Override
	public boolean findUnlessHitEnd(int minFrom, int maxFrom) {
		return matcher.findUnlessHitEnd(minFrom, maxFrom);
	}

	/**
	 * @see com.googlecode.streamflyer.regex.OnStreamMatcher#lastFrom()
	 */
	@Override
	public int lastFrom() {
		return matcher.lastFrom();
	}

	/**
	 * @see com.googlecode.streamflyer.regex.OnStreamMatcher#hitEnd()
	 */
	@Override
	public boolean hitEnd() {
		return matcher.hitEnd();
	}

	/**
	 * @see com.googlecode.streamflyer.regex.OnStreamMatcher#requireEnd()
	 */
	@Override
	public boolean requireEnd() {
		return matcher.requireEnd();
	}

	//
	// implement interface MatchResult by delegating to underlying matcher
	//

	/**
	 * @see java.util.regex.MatchResult#start()
	 */
	@Override
	public int start() {
		return matcher.start();
	}

	/**
	 * @see java.util.regex.MatchResult#start(int)
	 */
	@Override
	public int start(int group) {
		return matcher.start(group);
	}

	/**
	 * @see java.util.regex.MatchResult#end()
	 */
	@Override
	public int end() {
		return matcher.end();
	}

	/**
	 * @see java.util.regex.MatchResult#end(int)
	 */
	@Override
	public int end(int group) {
		return matcher.end(group);
	}

	/**
	 * @see java.util.regex.MatchResult#group()
	 */
	@Override
	public String group() {
		return matcher.group();
	}

	/**
	 * @see java.util.regex.MatchResult#group(int)
	 */
	@Override
	public String group(int group) {
		return matcher.group(group);
	}

	/**
	 * @see java.util.regex.MatchResult#groupCount()
	 */
	@Override
	public int groupCount() {
		return matcher.groupCount();
	}
}
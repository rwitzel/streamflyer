/**
 * Copyright (C) 2011 rwitzel75@googlemail.com
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
package com.github.rwitzel.streamflyer.regex.addons.util;

import com.github.rwitzel.streamflyer.regex.OnStreamMatcher;

/**
 * This {@link OnStreamMatcher} delegates to another matcher.
 * 
 * @author rwoo
 * @since 1.1.0
 */
public class DelegatingMatcher implements OnStreamMatcher {

    private OnStreamMatcher delegate;

    public OnStreamMatcher getDelegate() {
        return delegate;
    }

    public void setDelegate(OnStreamMatcher delegate) {
        this.delegate = delegate;
    }

    //
    // override OnStreamMatcher.*
    //

    @Override
    public int start() {
        return delegate.start();
    }

    @Override
    public int start(int group) {
        return delegate.start(group);
    }

    @Override
    public int end() {
        return delegate.end();
    }

    @Override
    public int end(int group) {
        return delegate.end(group);
    }

    @Override
    public String group() {
        return delegate.group();
    }

    @Override
    public String group(int group) {
        return delegate.group(group);
    }

    @Override
    public int groupCount() {
        return delegate.groupCount();
    }

    @Override
    public void reset(CharSequence input) {
        delegate.reset(input);
    }

    @Override
    public boolean findUnlessHitEnd(int minFrom, int maxFrom) {
        return delegate.findUnlessHitEnd(minFrom, maxFrom);
    }

    @Override
    public boolean hitEnd() {
        return delegate.hitEnd();
    }

    @Override
    public boolean requireEnd() {
        return delegate.requireEnd();
    }

    @Override
    public int lastFrom() {
        return delegate.lastFrom();
    }

}

package com.googlecode.streamflyer.support.stateful;

import com.googlecode.streamflyer.regex.OnStreamMatcher;

/**
 * Delegates to another matcher.
 * 
 * @author rwoo
 * 
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

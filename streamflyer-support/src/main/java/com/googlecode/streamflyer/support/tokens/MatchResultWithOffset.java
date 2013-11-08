package com.googlecode.streamflyer.support.tokens;

import java.util.regex.MatchResult;

/**
 * This {@link MatchResult} delegates to another match result but applies a group offset,s i.e the delegate is used with
 * a group number that is increased by the given group offset.
 * 
 * @author rwoo
 * 
 */
public class MatchResultWithOffset implements MatchResult {

    private MatchResult delegate;

    private int groupOffset;

    public MatchResultWithOffset(MatchResult delegate, int groupOffset) {
        super();
        this.delegate = delegate;
        this.groupOffset = groupOffset;
    }

    @Override
    public int start() {
        return delegate.start(groupOffset);
    }

    @Override
    public int start(int group) {
        return delegate.start(groupOffset + group);
    }

    @Override
    public int end() {
        return delegate.end(groupOffset);
    }

    @Override
    public int end(int group) {
        return delegate.end(groupOffset + group);
    }

    @Override
    public String group() {
        return delegate.group(groupOffset);
    }

    @Override
    public String group(int group) {
        return delegate.group(groupOffset + group);
    }

    @Override
    public int groupCount() {
        // not needed yet by TokenProcessors
        throw new UnsupportedOperationException("not implemented yet");
    }

}

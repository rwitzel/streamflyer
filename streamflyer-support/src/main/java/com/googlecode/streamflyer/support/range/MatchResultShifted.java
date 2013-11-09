package com.googlecode.streamflyer.support.range;

import java.util.regex.MatchResult;

/**
 * This {@link MatchResult} is useful if another match result is no longer valid because the input for the match result
 * has been shifted either to the begin or to the end of the input. This can happen if the input was a modifiable
 * {@link CharSequence} like {@link StringBuilder} and in this character sequence some characters are deleted or
 * inserted before the match.
 * 
 * @author rwoo
 * 
 */
public class MatchResultShifted implements MatchResult {

    private MatchResult delegate;

    private StringBuilder input;

    private int shift;

    /**
     * 
     * @param delegate
     *            the original match result
     * @param input
     *            the input where some character before the match have been inserted or removed
     * @param shift
     *            the number of characters that are inserted or removed. If the characters are removed, the shift must
     *            be negative number.
     */
    public MatchResultShifted(MatchResult delegate, StringBuilder input, int shift) {
        super();
        this.delegate = delegate;
        this.input = input;
        this.shift = shift;
    }

    @Override
    public int start() {
        int result = delegate.start();
        if (result == -1) {
            return result;
        } else {
            return result + shift;
        }
    }

    @Override
    public int start(int group) {
        int result = delegate.start(group);
        if (result == -1) {
            return result;
        } else {
            return result + shift;
        }
    }

    @Override
    public int end() {
        int result = delegate.end();
        if (result == -1) {
            return result;
        } else {
            return result + shift;
        }
    }

    @Override
    public int end(int group) {
        int result = delegate.end(group);
        if (result == -1) {
            return result;
        } else {
            return result + shift;
        }
    }

    @Override
    public String group() {
        int start = start();
        int end = end();
        if (start == -1 || end == -1) {
            return null;
        } else {
            return input.substring(start, end);
        }
    }

    @Override
    public String group(int group) {
        int start = start(group);
        int end = end(group);
        if (start == -1 || end == -1) {
            return null;
        } else {
            return input.substring(start, end);
        }
    }

    @Override
    public int groupCount() {
        return delegate.groupCount();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MatchResultShifted [group=" + group() + ", start=" + start() + ", end=" + end() + "]";
    }

}

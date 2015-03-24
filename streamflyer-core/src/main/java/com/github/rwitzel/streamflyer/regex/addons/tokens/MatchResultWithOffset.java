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
package com.github.rwitzel.streamflyer.regex.addons.tokens;

import java.util.regex.MatchResult;

/**
 * This {@link MatchResult} delegates to another match result but applies a group offset, i.e the delegate is used with
 * a group number that is increased by the given group offset.
 * <p>
 * Please, pay attention to the limited capability of the implementation of {@link #groupCount()}.
 * 
 * @author rwoo
 * @since 1.1.0
 */
public class MatchResultWithOffset implements MatchResult {

    private MatchResult delegate;

    private int groupOffset;

    /**
     * Calculated on demand.
     */
    private Integer groupCount;

    public MatchResultWithOffset(MatchResult delegate, int groupOffset) {
        super();

        if (groupOffset > delegate.groupCount() || groupOffset < 0) {
            throw new IndexOutOfBoundsException("No group " + groupOffset);
        }

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

    /**
     * Attention! Without Java reflection we cannot find out whether an unmatched or empty group immediately placed
     * before or after the end of a group is within or outside the other group. That is the returned group count might
     * be to high because it includes additional unmatched or empty groups.
     */
    @Override
    public int groupCount() {
        if (groupCount == null) {
            int groupIndex = groupOffset + 1;
            while (groupIndex <= delegate.groupCount() && delegate.end(groupIndex) <= delegate.end(groupOffset)) {
                groupIndex++;
            }
            groupCount = (groupIndex - 1) - groupOffset;
        }
        return groupCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MatchResultWithOffset [delegate=" + delegate + ", groupOffset=" + groupOffset + ", groupCount="
                + groupCount + "]";
    }

}

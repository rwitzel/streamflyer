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

package com.googlecode.streamflyer.experimental.regexj6;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.streamflyer.internal.thirdparty.ZzzAssert;
import com.googlecode.streamflyer.internal.thirdparty.ZzzReflectionUtils;
import com.googlecode.streamflyer.regex.OnStreamMatcher;
import com.googlecode.streamflyer.regex.OnStreamStandardMatcher;

/**
 * Implements {@link OnStreamMatcher} knowing internal fields, methods, and
 * classes of the JDK 6 classes {@link Pattern} and {@link Matcher}.
 * <p>
 * This implementation takes four times as much time as
 * {@link String#replaceAll(String, String)} needs to match and replace data in
 * an character stream that is read entirely into a {@link CharSequence}.
 * 
 * @author rwoo
 * 
 * @since 20.06.2011
 */
public class OnStreamJava6Matcher extends OnStreamStandardMatcher {

    //
    // properties initialized by constructor
    //

    /**
     * The field to access the private property <code>first</code> of
     * {@link Matcher}.
     */
    private Field firstField;

    private Field lastField;

    private Field oldLastField;

    private Field groupsField;

    private Field fromField;

    private Field hitEndField;

    private Field requireEndField;

    private Field acceptModeField;

    private Object matchRoot;

    private Method matchMethod;

    //
    //
    //

    /**
     * @param matcher
     */
    public OnStreamJava6Matcher(Matcher matcher) {
        super(matcher);

        this.firstField = findAccessiblePrivateField("first");
        this.lastField = findAccessiblePrivateField("last");
        this.oldLastField = findAccessiblePrivateField("oldLast");
        this.groupsField = findAccessiblePrivateField("groups");
        this.fromField = findAccessiblePrivateField("from");
        this.hitEndField = findAccessiblePrivateField("hitEnd");
        this.requireEndField = findAccessiblePrivateField("requireEnd");
        this.acceptModeField = findAccessiblePrivateField("acceptMode");

        findMatchMethodOfRootMatchNode();
    }

    /**
     * Initializes {@link #matchRoot} and {@link #matchMethod}.
     * <p>
     * <code><pre>
    boolean result = parentPattern.matchRoot.match(this, from, text);
 </pre></code>
     * 
     * @return Returns the field with the given namen of the class
     *         {@link Matcher}.
     */
    private void findMatchMethodOfRootMatchNode() {
        try {

            // find field Matcher.parentPattern
            Field parentPatternField = ZzzReflectionUtils.findField(
                    Matcher.class, "parentPattern");
            ZzzReflectionUtils.makeAccessible(parentPatternField);
            ZzzAssert.notNull(parentPatternField,
                    "field parentPattern must not be null");
            Pattern parentPattern = (Pattern) parentPatternField.get(matcher);
            ZzzAssert.notNull(parentPattern,
                    "field parentPattern must not be null");

            // find field Pattern.matchRoot
            Field matchRootField = ZzzReflectionUtils.findField(Pattern.class,
                    "matchRoot");
            ZzzReflectionUtils.makeAccessible(matchRootField);
            ZzzAssert.notNull(matchRootField,
                    "field matchRoot must not be null");
            matchRoot = matchRootField.get(parentPattern);
            ZzzAssert.notNull(matchRoot, "matchRoot must not be null");

            // find method Node.match()
            matchMethod = ZzzReflectionUtils.findMethod(matchRoot.getClass(),
                    "match", Matcher.class, Integer.TYPE, CharSequence.class);
            ZzzAssert.notNull(matchMethod, "method match(..) must not be null");
            ZzzReflectionUtils.makeAccessible(matchMethod);

        }
        catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }


    /**
     * @param fieldName
     * @return Returns the field with the given namen of the class
     *         {@link Matcher}.
     */
    private Field findAccessiblePrivateField(String fieldName) {

        Field field = ZzzReflectionUtils.findField(Matcher.class, fieldName);
        ZzzReflectionUtils.makeAccessible(field);

        ZzzAssert.notNull(field, String.format(
                "field with name '%s' of class %s must not be null but was",
                fieldName, Matcher.class));

        return field;
    }

    private void arrayValuesToMinusOne(Field field)
            throws IllegalAccessException {
        int[] array = (int[]) field.get(matcher);
        Arrays.fill(array, -1);
        field.set(matcher, array); // <- is this redundant?
    }

    /**
     * 
     <code><pre>
        boolean result = parentPattern.matchRoot.match(this, from, text);
     </pre></code>
     * 
     * @see com.googlecode.streamflyer.regex.OnStreamStandardMatcher#findUnlessHitEnd(int,
     *      int)
     */
    @Override
    public boolean findUnlessHitEnd(int minFrom, int maxFrom) {

        lastFrom = minFrom;

        beforeFind(lastFrom);

        boolean result = false;
        for (; lastFrom <= maxFrom; lastFrom++) {

            result = invoke(lastFrom, input.length());

            if (result || matcher.hitEnd()) {
                break;
            }

        }

        if (result) {

            try {
                // this.first = this.lastFrom;
                // this.groups[0] = this.first;
                // this.groups[1] = this.last;
                firstField.setInt(matcher, lastFrom);
                int[] array = (int[]) groupsField.get(matcher);
                array[0] = lastFrom;
                array[1] = lastField.getInt(matcher);
                groupsField.set(matcher, array); // <- is this redundant?
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        }

        afterFind(result);

        return result;
    }

    private boolean invoke(int lastFrom_, int to) {
        try {
            // TODO region should be made redundant!!!
            matcher.region(lastFrom, to);
            return (Boolean) matchMethod.invoke(matchRoot, matcher, lastFrom_,
                    input);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * <code><pre>
        this.hitEnd = false;
        this.requireEnd = false;
        from        = from < 0 ? 0 : from;
        this.first  = from;
        this.oldLast = oldLast < 0 ? from : oldLast;
        for (int i = 0; i < groups.length; i++)
            groups[i] = -1;
        acceptMode = NOANCHOR;
     </pre></code>
     */
    private void beforeFind(int from) {
        try {
            hitEndField.setBoolean(matcher, false);
            requireEndField.setBoolean(matcher, false);
            if (from < 0) {
                from = 0;
            }
            fromField.setInt(matcher, 0);
            firstField.setInt(matcher, from);
            if (oldLastField.getInt(matcher) < 0) {
                oldLastField.setInt(matcher, from);
            }
            arrayValuesToMinusOne(groupsField);
            acceptModeField.setInt(matcher, 0); // NOANCHOR = 0
        }
        catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * <code><pre>
        if (!result)
            this.first = -1;
        this.oldLast = this.last;
     </pre></code>
     */
    private void afterFind(boolean result) {
        try {

            if (!result) {
                firstField.setInt(matcher, -1);
            }
            oldLastField.setInt(matcher, lastField.getInt(matcher));
        }
        catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
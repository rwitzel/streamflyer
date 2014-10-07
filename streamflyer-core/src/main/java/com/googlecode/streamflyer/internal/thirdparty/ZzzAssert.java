/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.streamflyer.internal.thirdparty;

/**
 * The code of this class is copied from class <code>org.springframework.util.Assert</code>
 * in order to avoid additional dependencies to other projects.
 * <p>
 * The name is prefixed with <code>Zzz</code> to avoid confusion in case the
 * original classes are also available in classpath.  
 */

/**
 * Assertion utility class that assists in validating arguments. Useful for identifying programmer errors early and
 * clearly at runtime.
 * <p>
 * For example, if the contract of a public method states it does not allow <code>null</code> arguments, ZzzAssert can
 * be used to validate that contract. Doing this clearly indicates a contract violation when it occurs and protects the
 * class's invariants.
 * <p>
 * Typically used to validate method arguments rather than configuration properties, to check for cases that are usually
 * programmer errors rather than configuration errors. In contrast to config initialization code, there is usally no
 * point in falling back to defaults in such methods.
 * <p>
 * This class is similar to JUnit's assertion library. If an argument value is deemed invalid, an
 * {@link IllegalArgumentException} is thrown (typically). For example:
 * 
 * <pre class="code">
 * ZzzAssert.notNull(clazz, &quot;The class must not be null&quot;);
 * ZzzAssert.isTrue(i &gt; 0, &quot;The value must be greater than zero&quot;);
 * </pre>
 * 
 * Mainly for internal use within the framework; consider Jakarta's Commons Lang >= 2.0 for a more comprehensive suite
 * of assertion utilities.
 * 
 * @author Keith Donald
 * @author Juergen Hoeller
 * @author Colin Sampaleanu
 * @author Rob Harrop
 * @since 1.1.2
 */
public abstract class ZzzAssert {

    /**
     * ZzzAssert that an object is not <code>null</code> .
     * 
     * <pre class="code">
     * ZzzAssert.notNull(clazz, &quot;The class must not be null&quot;);
     * </pre>
     * 
     * @param object
     *            the object to check
     * @param message
     *            the exception message to use if the assertion fails
     * @throws IllegalArgumentException
     *             if the object is <code>null</code>
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * ZzzAssert that an object is not <code>null</code> .
     * 
     * <pre class="code">
     * ZzzAssert.notNull(clazz);
     * </pre>
     * 
     * @param object
     *            the object to check
     * @throws IllegalArgumentException
     *             if the object is <code>null</code>
     */
    public static void notNull(Object object) {
        notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    /**
     * ZzzAssert a boolean expression, throwing <code>IllegalArgumentException</code> if the test result is
     * <code>false</code>.
     * 
     * <pre class="code">
     * ZzzAssert.isTrue(i &gt; 0, &quot;The value must be greater than zero&quot;);
     * </pre>
     * 
     * @param expression
     *            a boolean expression
     * @param message
     *            the exception message to use if the assertion fails
     * @throws IllegalArgumentException
     *             if expression is <code>false</code>
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * ZzzAssert a boolean expression, throwing <code>IllegalArgumentException</code> if the test result is
     * <code>false</code>.
     * 
     * <pre class="code">
     * ZzzAssert.isTrue(i &gt; 0);
     * </pre>
     * 
     * @param expression
     *            a boolean expression
     * @throws IllegalArgumentException
     *             if expression is <code>false</code>
     */
    public static void isTrue(boolean expression) {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }
}

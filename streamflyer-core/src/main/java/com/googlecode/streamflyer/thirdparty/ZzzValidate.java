/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.streamflyer.thirdparty;

/**
 * The code of this class is copied from <code>org.apache.commons.lang.Validate</code> 
 * in order to avoid additional dependencies to other projects.
 * <p>  
 * The name is prefixed with <code>Zzz</code> to avoid confusion in case the
 * original classes are also available in classpath.  
 */


/**
 * <p>
 * This class assists in validating arguments.
 * </p>
 * <p>
 * The class is based along the lines of JUnit. If an argument value is deemed
 * invalid, an IllegalArgumentException is thrown. For example:
 * </p>
 * 
 * <pre>
 * Validate.isTrue(i &gt; 0, &quot;The value must be greater than zero: &quot;, i);
 * Validate.notNull(surname, &quot;The surname must not be null&quot;);
 * </pre>
 * 
 * @author Apache Software Foundation
 * @author <a href="mailto:ola.berg@arkitema.se">Ola Berg</a>
 * @author Gary Gregory
 * @author Norm Deane
 * @since 2.0
 * @version $Id: Validate.java 905636 2010-02-02 14:03:32Z niallp $
 */
public class ZzzValidate {
    // Validate has no dependencies on other classes in Commons Lang at present

    /**
     * Constructor. This class should not normally be instantiated.
     */
    public ZzzValidate() {
        super();
    }

    // notNull
    // ---------------------------------------------------------------------------------

    /**
     * <p>
     * Validate that the specified argument is not <code>null</code>; otherwise
     * throwing an exception.
     * 
     * <pre>
     * Validate.notNull(myObject);
     * </pre>
     * <p>
     * The message of the exception is &quot;The validated object is null&quot;.
     * </p>
     * 
     * @param object the object to check
     * @throws IllegalArgumentException if the object is <code>null</code>
     */
    public static void notNull(Object object) {
        notNull(object, "The validated object is null");
    }

    /**
     * <p>
     * Validate that the specified argument is not <code>null</code>; otherwise
     * throwing an exception with the specified message.
     * 
     * <pre>
     * Validate.notNull(myObject, &quot;The object must not be null&quot;);
     * </pre>
     * 
     * @param object the object to check
     * @param message the exception message if invalid
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * <p>
     * Validate that the argument condition is <code>true</code>; otherwise
     * throwing an exception with the specified message. This method is useful
     * when validating according to an arbitrary boolean expression, such as
     * validating a primitive number or using your own custom validation
     * expression.
     * </p>
     * 
     * <pre>
     * Validate.isTrue((i &gt; 0), &quot;The value must be greater than zero&quot;);
     * Validate.isTrue(myObject.isOk(), &quot;The object is not OK&quot;);
     * </pre>
     * 
     * @param expression the boolean expression to check
     * @param message the exception message if invalid
     * @throws IllegalArgumentException if expression is <code>false</code>
     */
    public static void isTrue(boolean expression, String message) {
        if (expression == false) {
            throw new IllegalArgumentException(message);
        }
    }

    //
    // extensions of the third-party code (not part of the third-party code)
    //

    public static void isZeroOrPositiveNumber(double number, String variableName) {
        if (number < 0) {
            throw new IllegalArgumentException(variableName
                    + " must be a zero or a positive number but was " + number);
        }
    }

    public static void isGreaterThanZero(double number, String variableName) {
        if (number <= 0) {
            throw new IllegalArgumentException(variableName
                    + " must be greather than zero but was " + number);
        }
    }
}

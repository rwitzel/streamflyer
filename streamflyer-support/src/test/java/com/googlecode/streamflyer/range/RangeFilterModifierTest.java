package com.googlecode.streamflyer.range;

import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.support.range.RangeFilterModifier;

/**
 * Tests {@link RangeFilterModifier}.
 * 
 * @author rwoo
 * 
 */
public class RangeFilterModifierTest extends com.googlecode.streamflyer.experimental.range.RangeFilterModifierTest {

    @Override
    protected Modifier createModifier(String startTag, String endTag, boolean includeStart, boolean includeEnd,
            boolean initiallyOn) {

        return new RangeFilterModifier(startTag, endTag, includeStart, includeEnd, initiallyOn, 1, 2048);
    }

}

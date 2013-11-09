package com.googlecode.streamflyer.support.range;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.streamflyer.support.saxlike.HandlerAwareModifier;
import com.googlecode.streamflyer.support.tokens.Token;

/**
 * Same functionality like {@link com.googlecode.streamflyer.experimental.range.RangeFilterModifier} but different
 * implementation
 * 
 * @author rwoo
 * 
 */
public class RangeFilterModifier extends HandlerAwareModifier {

    public RangeFilterModifier(String startRegex, String endRegex, boolean includeStartToken, boolean includeEndToken,
            boolean initiallyBetweenStartAndEnd, int minimumLengthOfLookBehind, int newNumberOfChars) {
        super();

        // define the behaviour for matched tokens and noMatches
        RangeFilterHandler handler = new RangeFilterHandler(initiallyBetweenStartAndEnd, includeStartToken,
                includeEndToken, minimumLengthOfLookBehind, newNumberOfChars);

        // define the tokens
        List<Token> tokens = new ArrayList<Token>();
        tokens.add(new Token("Start", startRegex));
        tokens.add(new Token("End", endRegex));

        // initialize the handler aware modifier
        initialize(tokens, handler, minimumLengthOfLookBehind, newNumberOfChars);
    }

}

package com.googlecode.streamflyer.support.nomatch;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.core.Modifier;

/**
 * This {@link Modifier} updates the start position of the next {@link NoMatch noMatch} and delegates to another
 * modifier. Additionally, it triggers the processing of a noMatch if necessary.
 * 
 * @author rwoo
 * 
 */
public class NoMatchAwareModifier implements Modifier {

    private Modifier delegate;

    private NoMatch noMatch;

    public NoMatchAwareModifier(Modifier delegate, NoMatch noMatch) {
        super();
        this.delegate = delegate;
        this.noMatch = noMatch;
    }

    @Override
    public AfterModification modify(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit) {

        // set the start position of the next noMatch
        noMatch.setStartPosition(firstModifiableCharacterInBuffer);

        AfterModification mod = delegate.modify(characterBuffer, firstModifiableCharacterInBuffer, endOfStreamHit);

        if (noMatch.getStartPosition() < firstModifiableCharacterInBuffer + mod.getNumberOfCharactersToSkip()) {
            // there are characters for the noMatch -> process them
            mod = noMatch.processNoMatch(characterBuffer, firstModifiableCharacterInBuffer, endOfStreamHit, mod,
                    delegate);
        }

        return mod;
    }

}

package com.googlecode.streamflyer.support.range;

import java.util.regex.MatchResult;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.regex.MatchProcessor;
import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.regex.ReplacingProcessor;
import com.googlecode.streamflyer.support.saxlike.Handler;
import com.googlecode.streamflyer.support.tokens.Token;
import com.googlecode.streamflyer.util.ModificationFactory;

/**
 * This {@link Handler} is a helper class for {@link RangeFilterModifier}.
 * 
 * @author rwoo
 * 
 */
public class RangeFilterHandler implements Handler {

    private boolean includeStartToken;

    private boolean includeEndToken;

    private ModificationFactory factory;

    private MatchProcessor removingProcessor = new ReplacingProcessor("");

    //
    // state
    //

    private boolean betweenStartAndEnd;

    public RangeFilterHandler(boolean initiallyBetweenStartAndEnd, boolean includeStartToken, boolean includeEndToken,
            int minimumLengthOfLookBehind, int newNumberOfChars) {
        super();
        this.betweenStartAndEnd = initiallyBetweenStartAndEnd;
        this.factory = new ModificationFactory(minimumLengthOfLookBehind, newNumberOfChars);
        this.includeStartToken = includeStartToken;
        this.includeEndToken = includeEndToken;
    }

    @Override
    public MatchProcessorResult processToken(Token token, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {

        if (token.getName().equals("Start")) {

            if (betweenStartAndEnd) {

                // we are already in the range -> nothing to do
                return null;

            } else {

                betweenStartAndEnd = true;

                // we entered the range -> -> delete the start token if configured so

                if (includeStartToken) {

                    // nothing to do
                    return null;

                } else {

                    // remove the start token and return
                    return removingProcessor.process(characterBuffer, firstModifiableCharacterInBuffer, matchResult);

                }

            }

        } else { // the end token

            if (betweenStartAndEnd) {

                betweenStartAndEnd = false;

                // we left the range -> delete the end token if configured so

                if (includeEndToken) {

                    // nothing to do
                    return null;

                } else {

                    // remove the end token and return
                    return removingProcessor.process(characterBuffer, firstModifiableCharacterInBuffer, matchResult);

                }

            } else {

                // we are already outside the range -> nothing to do
                return null;

            }

        }
    }

    @Override
    public MatchResult processNoMatch(int startPosition, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {

        if (!betweenStartAndEnd) {
            // -> delete the noMatch

            // The end of the noMatch is given by {@link MatchResult#start()}.
            int shiftMatchResult = matchResult.start() - startPosition;
            MatchResultShifted newMatchResult = new MatchResultShifted(matchResult, characterBuffer, -shiftMatchResult);

            // delete characters before token
            characterBuffer.delete(startPosition, matchResult.start());

            return newMatchResult;

        } else {
            return null;
        }
    }

    @Override
    public AfterModification processNoMatch(int startPosition, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, boolean endOfStreamHit, AfterModification mod, Modifier modifier) {

        if (!betweenStartAndEnd) {
            // -> delete the noMatch

            // The end of the noMatch is given by <code>firstModifiableCharacterInBuffer</code> +
            // {@link AfterModification#getNumberOfCharactersToSkip()}.
            int endPosition = firstModifiableCharacterInBuffer + mod.getNumberOfCharactersToSkip();
            int numCharactersToDelete = endPosition - startPosition;

            // delete characters before token
            characterBuffer.delete(startPosition, endPosition);

            // TODO please review: do we create the correct AfterModification object?
            int numberOfCharactersToSkip = mod.getNumberOfCharactersToSkip() - numCharactersToDelete;
            return factory.skip(numberOfCharactersToSkip, characterBuffer, firstModifiableCharacterInBuffer,
                    endOfStreamHit);

        } else {
            return mod;

        }
    }

}

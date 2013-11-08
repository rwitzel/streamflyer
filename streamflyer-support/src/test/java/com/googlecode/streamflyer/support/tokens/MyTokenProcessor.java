package com.googlecode.streamflyer.support.tokens;

import java.util.List;
import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.support.util.DoNothingProcessor;

/**
 * Stores the found tokens and replaces text in tokens with type <code>SectionTitle</code> and <code>ListItem</code>.
 * 
 * @author rwoo
 * 
 */
public class MyTokenProcessor extends TokenProcessor {

    /**
     * The list of all found tokens. Each element contains the name of the token and the content of the token.
     */
    private List<String> foundTokens;

    /**
     * True if the parser is between the tokens "SectionStart" and "SectionEnd".
     */
    private boolean insideSection = false;

    public MyTokenProcessor(List<Token> tokens, List<String> foundTokens) {
        super(tokens);
        this.foundTokens = foundTokens;
    }

    @Override
    protected MatchProcessorResult processToken(Token token, StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            MatchResult matchResult) {

        // +++ save the found token
        String foundToken = token.getName() + ":" + matchResult.group();
        foundTokens.add(foundToken);

        // +++ process the token
        if (token.getName().equals("SectionStart")) {

            insideSection = true;

        } else if (token.getName().equals("SectionEnd")) {

            insideSection = false;

        } else if (!insideSection) {

            // do nothing if not inside section!
            return new DoNothingProcessor().process(characterBuffer, firstModifiableCharacterInBuffer, matchResult);

        }

        // delegate to the default token-specific match processors.
        return super.processToken(token, characterBuffer, firstModifiableCharacterInBuffer, matchResult);
    }
}

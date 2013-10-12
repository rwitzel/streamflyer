package com.googlecode.streamflyer.support.tokens;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessorResult;
import com.googlecode.streamflyer.regex.ReplacingProcessor;
import com.googlecode.streamflyer.support.tokens.MatchResultWithOffset;
import com.googlecode.streamflyer.support.tokens.Token;
import com.googlecode.streamflyer.support.tokens.TokenProcessor;

/**
 * Stores the found tokens and replaces text in tokens with type
 * <code>SectionTitle</code> and <code>ListItem</code>.
 * 
 * @author rwoo
 * 
 */
public class MyTokenProcessor extends TokenProcessor {

    /**
     * The list of all found tokens. Each element contains the name of the token
     * and the content of the token.
     */
    private List<String> foundTokens = new ArrayList<String>();

    /**
     * True if the parser is between the tokens "SectionStart" and "SectionEnd".
     */
    private boolean insideSection = false;

    public MyTokenProcessor(List<Token> tokens) {
        super(tokens);
    }

    @Override
    protected MatchProcessorResult processToken(Token token, int groupOffset, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {

        // +++ save the found token
        String foundToken = token.getName() + ":" + matchResult.group(groupOffset);
        foundTokens.add(foundToken);

        // +++ process the token
        int behindToken = matchResult.end(groupOffset);

        if (token.getName().equals("SectionStart")) {

            insideSection = true;

        } else if (token.getName().equals("SectionEnd")) {

            insideSection = false;

        } else if (token.getName().equals("SectionTitle") && insideSection) {

            // replace the title with "TITLE_FOUND"
            ReplacingProcessor processor = new ReplacingProcessor("$1TITLE_FOUND$3");
            return processor.process(characterBuffer, firstModifiableCharacterInBuffer, new MatchResultWithOffset(
                    matchResult, groupOffset));

        } else if (token.getName().equals("ListItem") && insideSection) {

            // replace the content of the list item with "LIST_ITEM_FOUND"
            ReplacingProcessor processor = new ReplacingProcessor("$1LIST_ITEM_FOUND$3");
            return processor.process(characterBuffer, firstModifiableCharacterInBuffer, new MatchResultWithOffset(
                    matchResult, groupOffset));

        }

        return new MatchProcessorResult(behindToken, true);
    }

    public List<String> getFoundTokens() {
        return foundTokens;
    }
}

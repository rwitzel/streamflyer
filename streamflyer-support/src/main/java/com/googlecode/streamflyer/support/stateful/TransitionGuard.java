package com.googlecode.streamflyer.support.stateful;

import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessorResult;

/**
 * A transition guard can be used to stop a {@link Transition}. Overwrite this class to define when the transition shall
 * not be executed.
 * <p>
 * Apart from that, the guard is a good place to execute logic that is not necessarily specific to the transition.
 * 
 * @author rwoo
 * 
 */
public class TransitionGuard {

    /**
     * 
     * @param nextState
     * @param groupOffset
     * @param characterBuffer
     * @param firstModifiableCharacterInBuffer
     * @param matchResult
     * @return Returns null if the transition shall be executed. Returns the {@link MatchProcessorResult} that shall be
     *         returned if the transition shall be stopped. If not overwritten, this method returns null.
     */
    public MatchProcessorResult stopTransition(State nextState, int groupOffset, StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {
        // by default we don't stop the transition
        return null;
    }

}

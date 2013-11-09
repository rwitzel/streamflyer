package com.googlecode.streamflyer.regex.addons.stateful;

import java.util.regex.MatchResult;

import com.googlecode.streamflyer.regex.MatchProcessorResult;

/**
 * A transition guard can be used to stop a {@link Transitions}. By default,
 * transitions are not stopped. Use a subclass when some transitions shall not
 * be executed.
 * <p>
 * Apart from that, the guard is a good place to execute logic that shall be
 * executed during the transition, before the new state is reached. The logic
 * must not be specific to this transition.
 * 
 * @author rwoo
 */
public class TransitionGuard {

    /**
     * @param nextState
     * @param characterBuffer
     * @param firstModifiableCharacterInBuffer
     * @param matchResult
     * @return Returns null if the transition shall be executed. Returns a
     *         {@link MatchProcessorResult} that shall be returned if the
     *         transition shall be stopped. If not overwritten, this method
     *         returns null.
     */
    public MatchProcessorResult stopTransition(State nextState,
            StringBuilder characterBuffer,
            int firstModifiableCharacterInBuffer, MatchResult matchResult) {
        // by default we don't stop the transition
        return null;
    }

}

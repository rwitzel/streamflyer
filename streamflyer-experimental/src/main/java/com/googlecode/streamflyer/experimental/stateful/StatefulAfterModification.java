/**
 * Copyright (C) 2011 rwoo@gmx.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.streamflyer.experimental.stateful;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.internal.thirdparty.ZzzValidate;

/**
 * The return value of {@link State#modify(StringBuilder, int, boolean)} which
 * contains an {@link AfterModification} and a {@link State}.
 * 
 * @author rwoo
 * @since 14.09.2011
 */
public class StatefulAfterModification {

	/**
	 * The message that is to pass to the modifying reader or writer.
	 */
	private AfterModification afterModification;

	/**
	 * The state that should be used by the {@link StatefulModifier}.
	 */
	private State nextState;

	/**
	 * @param afterModification
	 * @param nextState
	 */
	public StatefulAfterModification(AfterModification afterModification,
			State nextState) {
		super();

		ZzzValidate.notNull(nextState, "nextState must not be null");
		ZzzValidate.notNull(afterModification,
				"afterModification must not be null");

		this.afterModification = afterModification;
		this.nextState = nextState;
	}

	/**
	 * @return Returns the {@link #afterModification}.
	 */
	public AfterModification getAfterModification() {
		return afterModification;
	}

	/**
	 * @return Returns the {@link #nextState}.
	 */
	public State getNextState() {
		return nextState;
	}
}

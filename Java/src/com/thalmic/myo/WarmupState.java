package com.thalmic.myo;

/**
 * Possible warmup states for a {@link Myo}.
 *
 */
public enum WarmupState {
	
	/**
	 * Unknown warmup state.
	 */
	warmupStateUnknown, 
	/**
	 * {@link Myo} is not warmed up.
	 */
	warmupStateCold, 
	/**
	 * {@link Myo} is warmed up.
	 */
	warmupStateWarm;
}

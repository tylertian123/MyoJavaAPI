package com.thalmic.myo;

/**
 * Possible warmup results for a {@link Myo}.
 *
 */
public enum WarmupResult {
	/**
	 * Unknown warmup result.
	 */
	warmupResultUnknown,
	/**
	 * {@link Myo} was successfully warmed up.
	 */
	warmupResultSuccess, 
	/**
	 * {@link Myo}'s warmup failed due to timeout.
	 */
	warmupResultFailedTimeout;
}

package com.thalmic.myo;

/**
 * Possible directions for {@link Myo}'s +x axis relative to a user's arm. 
 *
 */
public enum XDirection {

	/**
	 * The positive x axis points towards the user's wrist.
	 */
	xDirectionTowardsWrist, 
	/**
	 * The positive x axis points towards the user's elbow.
	 */
	xDirectionTowardsElbow, 
	/**
	 * The direction of the positive x axis is unknown.
	 */
	xDirectionUnknown;
}

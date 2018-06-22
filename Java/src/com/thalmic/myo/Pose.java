package com.thalmic.myo;

/**
 * A pose represents a detected configuration of the user's hand.
 *
 */
public enum Pose {
	
	/**
	 * Unknown pose.
	 */
	unknown, 
	/**
	 * The rest pose.
	 */
	rest, 
	/**
	 * The fist pose.
	 */
	fist, 
	/**
	 * The spread fingers pose.
	 */
	fingersSpread,
	/**
	 * The waving in pose.
	 */
	waveIn, 
	/**
	 * The waving out pose.
	 */
	waveOut, 
	/**
	 * The double tap (middle finger to thumb) pose.
	 */
	doubleTap;
	
	/**
	 * Returns the type of this pose.
	 * @deprecated This method is only here because it is in the C++ Myo API. As {@link Pose} is a enum itself, instead of
	 * a class containing an enum, this method simply does "{@code return this;}".
	 * @return The type of this pose (the pose itself)
	 */
	public Pose type() {
		return this;
	}
	
	/**
	 * Returns a human-readable string representation of the pose.
	 */
	@Override
	public String toString() {
		switch(this) {
		case unknown:
			return "unknown";
		case rest:
			return "rest";
		case fist:
			return "fist";
		case fingersSpread:
			return "fingersSpread";
		case waveIn:
			return "waveIn";
		case waveOut:
			return "waveOut";
		case doubleTap:
			return "doubleTap";
		default:
			return "unknown";
		}
	}
}

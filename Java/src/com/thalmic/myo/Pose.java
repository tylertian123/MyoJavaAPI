package com.thalmic.myo;

public enum Pose {
	
	unknown, rest, fist, fingersSpread, waveIn, waveOut, doubleTap;
	
	public Pose type() {
		return this;
	}
	
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

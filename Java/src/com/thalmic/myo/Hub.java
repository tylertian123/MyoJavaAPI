package com.thalmic.myo;

//TODO: Implement waitForMyo and addDeviceListener and removeDeviceListener
public final class Hub {
	
	static {
		System.loadLibrary("myo_jni");
	}
	
	private long _nativePointer;
	
	private native void _initHub(String appID);
	public Hub(String applicationIdentifier) {
		String[] segments = applicationIdentifier.split("\\.");
		if(segments.length < 3) {
			throw new IllegalArgumentException("Application identifier must consist of 3 or more segments");
		}
		for(int i = 0; i < segments.length; i ++) {
			if(segments[i].length() < 1) {
				throw new IllegalArgumentException("Invalid application identifier");
			}
			if(i == 0) {
				for(int j = 0; j < segments[i].length(); j ++) {
					if(!Character.isLetterOrDigit(segments[i].charAt(j))) {
						throw new IllegalArgumentException("First segment of application identifier must only contain alphanumeric characters");
					}
				}
				continue;
			}
			
			for(int j = 0; j < segments[i].length(); j ++) {
				if(!Character.isLetterOrDigit(segments[i].charAt(j))) {
					if(segments[i].charAt(j) == '_' || segments[i].charAt(j) == '-') {
						if(j == 0 || j == segments[i].length() - 1) {
							throw new IllegalArgumentException("Dashes and underscores cannot be at the beginning or end of a segment of the application identifier");
						}
					}
					else {
						throw new IllegalArgumentException("Application identifier can only consist of alphanumeric characters, dashes or underscores");
					}
				}
			}
		}
		
		_initHub(applicationIdentifier);
	}
	
	private native void _release();
	public void release() {
		_release();
	}
	
	enum LockingPolicy {
		lockingPolicyNone,
		lockingPolicyStandard
	}
	
	private static final int POLICY_NONE = 0;
	private static final int POLICY_STANDARD = 1;
	private native void _setLockingPolicy(int policy);
	public void setLockingPolicy(LockingPolicy policy) {
		if(policy == LockingPolicy.lockingPolicyNone) {
			_setLockingPolicy(POLICY_NONE);
		}
		else {
			_setLockingPolicy(POLICY_STANDARD);
		}
	}
	
	private native void _run(int duration);
	public void run(int durationMs) {
		_run(durationMs);
	}
	
	private native void _runOnce(int duration);
	public void runOnce(int durationMs) {
		_runOnce(durationMs);
	}
}

package com.thalmic.myo;

import java.util.HashMap;

public final class Hub {
	
	static {
		System.loadLibrary("myo_jni");
	}
	
	private static final int POLICY_NONE = 0;
	private static final int POLICY_STANDARD = 1;
	public enum LockingPolicy {
		lockingPolicyNone,
		lockingPolicyStandard;
		
		protected int translate() {
			if(this == lockingPolicyNone) {
				return POLICY_NONE;
			}
			else {
				return POLICY_STANDARD;
			}
		}
	}
	
	private boolean deleted = false;
	public boolean isReleased() {
		return deleted;
	}
	
	private void checkExcept() {
		if(deleted) {
			throw new MyoException("This Hub has already been released");
		}
	}
	
	private HashMap<DeviceListener, Long> deviceListenerAddresses = new HashMap<DeviceListener, Long>();
	
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
		deleted = true;
	}
	
	
	private native void _setLockingPolicy(int policy);
	public void setLockingPolicy(LockingPolicy policy) {
		checkExcept();
		_setLockingPolicy(policy.translate());
	}
	
	private native void _run(int duration);
	public void run(int durationMs) {
		checkExcept();
		_run(durationMs);
	}
	
	private native void _runOnce(int duration);
	public void runOnce(int durationMs) {
		checkExcept();
		_runOnce(durationMs);
	}
	
	private native boolean _waitForMyo(int duration);
	private long _myoAddress;
	public Myo waitForMyo() {
		return waitForMyo(0);
	}
	public Myo waitForMyo(int durationMs) {
		checkExcept();
		return _waitForMyo(durationMs) ? new Myo(_myoAddress) : null;
	}
	
	private native long _addDeviceListener(DeviceListener listener);
	public void addDeviceListener(DeviceListener listener) {
		checkExcept();
		long address = _addDeviceListener(listener);
		deviceListenerAddresses.put(listener, address);
	}
	
	private native void _removeDeviceListener(long address);
	public void removeDeviceListener(DeviceListener listener) {
		checkExcept();
		if(!deviceListenerAddresses.containsKey(listener)) {
			return;
		}
		_removeDeviceListener(deviceListenerAddresses.get(listener));
		deviceListenerAddresses.remove(listener);
	}
}

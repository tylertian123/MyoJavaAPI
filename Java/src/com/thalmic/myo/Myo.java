package com.thalmic.myo;

public class Myo {
	static {
		System.loadLibrary("myo_jni");
	}
	
	private static final int VIB_SHORT = 0;
	private static final int VIB_MEDIUM = 1;
	private static final int VIB_LONG = 2;
	public enum VibrationType {
		vibrationShort, vibrationMedium, vibrationLong;
		
		protected int translate() {
			if(this == vibrationShort) {
				return VIB_SHORT;
			}
			else if(this == vibrationMedium) {
				return VIB_MEDIUM;
			}
			return VIB_LONG;
		}
	}
	
	private static final int UT_TIMED = 0;
	private static final int UT_HOLD = 1;
	public enum UnlockType {
		unlockTimed, unlockHold;
		
		protected int translate() {
			if(this == unlockTimed) {
				return UT_TIMED;
			}
			return UT_HOLD;
		}
	}
	
	private static final int SET_DISABLED = 0;
	private static final int SET_ENABLED = 1;
	public enum StreamEmgType {
		streamEmgDisabled, streamEmgEnabled;
		
		protected int translate() {
			if(this == streamEmgDisabled) {
				return SET_DISABLED;
			}
			return SET_ENABLED;
		}
	}
	
	private long _nativePointer;
	//Keep it package-private
	Myo(long nativeAddress) {
		_nativePointer = nativeAddress;
	}
	
	private native void _vibrate(int type);
	public void vibrate(VibrationType type) {
		_vibrate(type.translate());
	}
	
	private native void _requestRssi();
	public void requestRssi() {
		_requestRssi();
	}
	
	private native void _requestBattLevel();
	public void requestBatteryLevel() {
		_requestBattLevel();
	}
	
	private native void _unlock(int type);
	public void unlock(UnlockType type) {
		_unlock(type.translate());
	}
	
	private native void _lock();
	public void lock() {
		_lock();
	}
	
	private native void _notifyAction();
	public void notifyUserAction() {
		_notifyAction();
	}
	
	private native void _setStreamEmg(int type);
	public void setStreamEmg(StreamEmgType type) {
		_setStreamEmg(type.translate());
	}
}

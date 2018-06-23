package com.thalmic.myo;

/**
 * Represents a {@link Myo} device with a specific MAC address.<br>
 * <br>
 * This class can not be instantiated directly; instead, use {@link Hub} to get access to a {@link Myo}. 
 * There is only one {@link Myo} instance corresponding to each device; thus, if the addresses of two {@link Myo}
 * instances compare equal, they refer to the same device. 
 *
 */
public final class Myo {
	static {
		System.loadLibrary("myo_jni");
	}
	
	//Same as the enums in Hub, int values are passed to native methods instead of enums to make things simpler
	private static final int VIB_SHORT = 0;
	private static final int VIB_MEDIUM = 1;
	private static final int VIB_LONG = 2;
	/**
	 * Types of vibration supported by the {@link Myo}.
	 *
	 */
	public enum VibrationType {
		/**
		 * Short vibration.
		 */
		vibrationShort,
		/**
		 * Medium vibration.
		 */
		vibrationMedium, 
		/**
		 * Long vibration.
		 */
		vibrationLong;
		
		//"Translates" a enum value into an integer to be passed into the native methods.
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
	/**
	 * Unlock types supported by the {@link Myo}.
	 *
	 */
	public enum UnlockType {
		/**
		 * Unlock the Myo only for a short period of time. Commonly used to allow for pose transitions.
		 */
		unlockTimed, 
		/**
		 * Unlock the Myo until being told otherwise.
		 */
		unlockHold;
		
		protected int translate() {
			if(this == unlockTimed) {
				return UT_TIMED;
			}
			return UT_HOLD;
		}
	}
	
	private static final int SET_DISABLED = 0;
	private static final int SET_ENABLED = 1;
	/**
	 * Valid EMG streaming modes for a {@link Myo}.
	 *
	 */
	public enum StreamEmgType {
		/**
		 * Disable streaming of EMG data.
		 */
		streamEmgDisabled,
		/**
		 * Enable streaming of EMG data.
		 */
		streamEmgEnabled;
		
		protected int translate() {
			if(this == streamEmgDisabled) {
				return SET_DISABLED;
			}
			return SET_ENABLED;
		}
	}
	
	/*
	 * This native pointer field functions in the same way as Hub. However, because the Myo objects are created
	 * by the Myo API, there is no release() method. Instead, the release() method of the Hub will cause all 
	 * connected Myos to be released.
	 */
	private long _nativePointer;
	//Constructor has to be kept package-private.
	//If the wrong nativeAddress is passed in, this will cause heap corruption and crash the VM.
	Myo(long nativeAddress) {
		_nativePointer = nativeAddress;
	}
	
	/**
	 * Returns true if <em>obj</em> refers to the same device as this {@link Myo} object.
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Myo)) {
			return false;
		}
		Myo otherMyo = (Myo) obj;
		
		if(otherMyo._nativePointer == this._nativePointer) {
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * Return the address of the native C++ {@link Myo} object.<br>
	 * <br>
	 * This value should only be used to test for equality with another {@link Myo} instance.
	 * @return The address of the native C++ {@link Myo} object.
	 */
	public long getNativeAddress() {
		return _nativePointer;
	}
	
	//Native method that directly calls Myo::vibrate().
	private native void _vibrate(int type);
	/**
	 * Vibrate the {@link Myo}.
	 * @param type The vibration type.
	 */
	public void vibrate(VibrationType type) {
		_vibrate(type.translate());
	}
	
	//Native method that directly calls Myo::requestRssi().
	private native void _requestRssi();
	/**
	 * Request the RSSI of the {@link Myo}.<br>
	 * <br>
	 * An onRssi event will likely be generated with the value of the RSSI. 
	 * @see DeviceListener#onRssi(Myo, long, byte)
	 */
	public void requestRssi() {
		_requestRssi();
	}
	
	//Native method that directly calls Myo::requestBatteryLevel().
	private native void _requestBattLevel();
	/**
	 * Request the battery level of the {@link Myo}.<br>
	 * <br>
	 * An onBatteryLevelReceived event will be generated with the value. 
	 * @see DeviceListener#onBatteryLevelReceived(Myo, long, byte)
	 */
	public void requestBatteryLevel() {
		_requestBattLevel();
	}
	
	//Native method that directly calls Myo::unlock().
	private native void _unlock(int type);
	/**
	 * Unlock the {@link Myo}.<br>
	 * <br>
	 * The behavior of the {@link Myo} depends on the {@link Myo.UnlockType UnlockType} used. 
	 * If {@link Myo} was locked, an onUnlock event will be generated. 
	 * @param type The unlock type
	 */
	public void unlock(UnlockType type) {
		_unlock(type.translate());
	}
	
	//Native method that directly calls Myo::lock();
	private native void _lock();
	/**
	 * Force the {@link Myo} to lock immediately.<br>
	 * <br>
	 * If {@link Myo} was unlocked, an onLock event will be generated. 
	 */
	public void lock() {
		_lock();
	}
	
	//Native method that directly calls Myo::notifyUserAction().
	private native void _notifyAction();
	/**
	 * Notify the {@link Myo} that a user action was recognized.<br>
	 * <br>
	 * Will cause {@link Myo} to vibrate.
	 */
	public void notifyUserAction() {
		_notifyAction();
	}
	
	//Native method that directly calls Myo::setStreamEmg().
	private native void _setStreamEmg(int type);
	/**
	 * Sets the EMG streaming mode for a {@link Myo}.
	 * @param type The EMG steaming mode.
	 * @see DeviceListener#onEmgData(Myo, long, byte[])
	 */
	public void setStreamEmg(StreamEmgType type) {
		_setStreamEmg(type.translate());
	}
}

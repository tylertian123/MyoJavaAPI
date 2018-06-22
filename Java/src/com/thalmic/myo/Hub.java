package com.thalmic.myo;

import java.util.Collection;
import java.util.HashMap;

/**
 * A {@link Hub} provides access to one or more {@link Myo} instances.
 *
 */
public final class Hub {
	
	static {
		System.loadLibrary("myo_jni");
	}
	
	//These int values are passed to the native method instead of the enum.
	//They're much easier to compare than a Java enum object.
	private static final int POLICY_NONE = 0;
	private static final int POLICY_STANDARD = 1;
	/**
	 * Locking policies supported by {@link Myo}.
	 *
	 */
	public enum LockingPolicy {
		/**
		 * No locking policy. Under this, the Myo will remain locked or unlocked until being told otherwise
		 * by the {@link Myo#lock()} or {@link Myo#unlock(Myo.UnlockType)} methods.
		 */
		lockingPolicyNone,
		/**
		 * Standard locking policy. Under this, the Myo will unlock upon the unlock gesture and lock shortly
		 * after a period of inactivity.
		 */
		lockingPolicyStandard;
		
		//"Translates" a LockingPolicy into an integer that can be passed to the native method.
		protected int translate() {
			if(this == lockingPolicyNone) {
				return POLICY_NONE;
			}
			else {
				return POLICY_STANDARD;
			}
		}
	}
	
	//Whether the resources have been released.
	//See below and the release method for details.
	private boolean deleted = false;
	/**
	 * Returns whether the resources associated with this {@link Hub} have been released.
	 * If this method returns true, subsequent calls to any method, excluding this one and {@link #release()},
	 * will throw a {@link MyoException}.
	 * @return Whether the resources associated with this {@link Hub} have been released.
	 * @see #release()
	 */
	public boolean isReleased() {
		return deleted;
	}
	
	//Verifies that the Hub is still valid.
	private void checkExcept() {
		if(deleted) {
			throw new MyoException("This Hub has already been released");
		}
	}
	
	//This map matches DeviceListener objects to physical locations in memory.
	//For details please see the addListener and removeListener methods.
	private HashMap<DeviceListener, Long> deviceListenerAddresses = new HashMap<DeviceListener, Long>();
	
	/*
	 * The physical location in memory that the C++ Hub object is stored.
	 * 
	 * When the constructor is called, the native C++ code creates a native Hub object to work with,
	 * of which the address is stored in this long. To make sure the reference to the native object is
	 * valid throughout the lifetime of the Hub, the memory for it is allocated on the heap. This is
	 * also why release() should be called when the Hub is no longer used.
	 */
	private long _nativePointer;
	
	//Native method that initializes the Hub.
	//This method also sets the value of _nativePointer; for details, see above.
	private native void _initHub(String appID);
	/**
	 * Construct a hub.<br>
	 * <br>
	 * <em>applicationIdentifier</em> must follow a reverse domain name format (ex. com.domainname.appname).
	 * Application identifiers can be formed from the set of alphanumeric ASCII characters (a-z, A-Z, 0-9).
	 * The hyphen (-) and underscore (_) characters are permitted if they are not adjacent to a period (.) 
	 * character (i.e. not at the start or end of each segment), but are not permitted in the top-level domain.
	 * Application identifiers must have three or more segments. For example, if a company's domain is 
	 * example.com and the application is named hello-world, one could use "com.example.hello-world" as a valid
	 * application identifier. <em>applicationIdentifier</em> can be an empty string.
	 * @param applicationIdentifier The application identifier.
	 * @throws IllegalArgumentException If <em>applicationIdentifier</em> is not in the proper reverse domain name format
	 * or is longer than 255 characters.
	 * @throws MyoException If the hub initialization failed for some reason, typically because Myo Connect is not
	 * running and thus a connection cannot be established.
	 */
	public Hub(String applicationIdentifier) {
		//Check if the app ID is valid first before passing to the native method.
		//Native exceptions can be messy and often crashes the VM.
		if(applicationIdentifier.length() > 255) {
			throw new IllegalArgumentException("Application identifier cannot be longer than 255 characters");
		}
		String[] segments = applicationIdentifier.split("\\.");
		if(segments.length < 3) {
			throw new IllegalArgumentException("Application identifier must consist of 3 or more segments");
		}
		for(int i = 0; i < segments.length; i ++) {
			if(segments[i].length() < 1) {
				throw new IllegalArgumentException("Invalid application identifier");
			}
			//Special treatment for first segment; can only contain alphanumeric characters
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
	
	//Releases the native Hub object back to the OS.
	//Implemented by calling delete on the native pointer.
	private native void _release();
	/**
	 * Releases any resources associated with this Hub.<br>
	 * <br>
	 * After this method is called, {@link #isReleased()} will start returning true. Subsequent calls to
	 * any method, excluding this one and {@link #isReleased()}, will throw a {@link MyoException}.<br>
	 * <br>
	 * Calling this method on a {@link Hub} that has already been released will have no effect. This method also
	 * removes all device listeners.
	 */
	public void release() {
		if(!deleted) {
			//Get the addresses of all the listeners
			Collection<Long> addresses = deviceListenerAddresses.values();
			//Release all of them, one by one
			//For more information see addListener and removeListener
			for(long address : addresses) {
				_removeDeviceListener(address);
			}
			deviceListenerAddresses.clear();
			
			_release();
			deleted = true;
		}
	}
	
	//Native method that sets the locking policy.
	//Accepts an int instead of LockingPolicy enum for simplicity.
	private native void _setLockingPolicy(int policy);
	/**
	 * Set the locking policy for {@link Myo}s connected to the {@link Hub}.
	 * @param policy The new locking policy.
	 * @throws MyoException If this {@link Hub}'s resources have already been released.
	 */
	public void setLockingPolicy(LockingPolicy policy) {
		checkExcept();
		_setLockingPolicy(policy.translate());
	}
	
	//Native method that directly calls the C++ Hub::run().
	private native void _run(int duration);
	/**
	 * Run the event loop for the specified duration (in milliseconds).<br>
	 * <br>
	 * During that time, this method will block. For more information, see 
	 * <a href="http://developerblog.myo.com/hub-run/">http://developerblog.myo.com/hub-run/</a>.
	 * @param durationMs The duration to run the event loop for, in milliseconds.
	 * @throws MyoException If this {@link Hub}'s resources have already been released.
	 * @see #runOnce(int)
	 */
	public void run(int durationMs) {
		checkExcept();
		_run(durationMs);
	}
	
	//Native method that directly calls the C++ Hub::runOnce().
	private native void _runOnce(int duration);
	/**
	 * Run the event loop until a single event occurs, or the specified duration (in milliseconds) has elapsed.<br>
	 * <br>
	 * During that time, this method will block. For more information, see 
	 * <a href="http://developerblog.myo.com/hub-run/">http://developerblog.myo.com/hub-run/</a>.
	 * @param durationMs The duration to run the even loop for, in milliseconds.
	 * @throws MyoException If this {@link Hub}'s resources have already been released.
	 * @see #run(int)
	 */
	public void runOnce(int durationMs) {
		checkExcept();
		_runOnce(durationMs);
	}
	
	//Native method that directly calls Hub::waitForMyo().
	//Returns true if Myo is connected, false if timed out.
	private native boolean _waitForMyo(int duration);
	//A physical location in memory that the native C++ Myo object is stored.
	//Set by the _waitForMyo() native method after it returns.
	private long _myoAddress;
	/**
	 * Wait for a {@link Myo} to become paired.<br>
	 * <br>
	 * This method blocks indefinitely until a {@link Myo} is found. This method must not be called concurrently
	 * with {@link #run(int)} or {@link #runOnce(int)}.
	 * @throws MyoException If this {@link Hub}'s resources have already been released.
	 * @return A paired {@link Myo}.
	 */
	public Myo waitForMyo() {
		return waitForMyo(0);
	}
	/**
	 * Wait for a {@link Myo} to become paired, or time out after <em>timeoutMs</em> milliseconds.<br>
	 * <br>
	 * If <em>timeoutMs</em> is zero, this method blocks indefinitely until a {@link Myo} is found. 
	 * This method must not be called concurrently with {@link #run(int)} or {@link #runOnce(int)}.
	 * @param timeoutMs The amount of milliseconds to wait for before timing out.
	 * @throws MyoException If this {@link Hub}'s resources have already been released.
	 * @return A paired {@link Myo}, or {@code null} if wait timed out.
	 */
	public Myo waitForMyo(int timeoutMs) {
		checkExcept();
		return _waitForMyo(timeoutMs) ? new Myo(_myoAddress) : null;
	}
	
	/*
	 * Attaching and removing DeviceListeners
	 * 
	 * Because of the polymorphism in the C++ Myo API, extra steps have to be taken for adding and removing
	 * DeviceListeners.
	 * 
	 * When the Java addListener() method is called, the DeviceLisener object is passed into a native method.
	 * This native method then creates a wrapper around the Java DeviceListener (because, obviously, the C++
	 * Myo API wants a C++ listener, not a Java one). When the methods of the C++ wrapper is called by the Myo
	 * API, the wrapper calls the respective Java method of the listener, making the whole thing work.
	 * 
	 * The address of that wrapper object is then passed from the native code back to the Java addListener()
	 * method. The Java code stores the DeviceListener reference and wrapper address into the map declared earlier.
	 * When removeListener() is called, the code retrieves the address of the wrapper from the map, and passes
	 * it into a native method, which removes the listener and destroys the wrapper.
	 * 
	 * Because calling Java methods from C++ can be costly, some optimization is used. When addListener() is
	 * called, it checks each DeviceListener method to see if it's overridden. That information is then passed
	 * to the native code. When the Myo API calls a listener method that is not implemented by the Java code,
	 * the C++ code will just return immediately, since there's no use calling an empty method.
	 * 
	 */
	//This method checks if a certain method is overriden by the DeviceListener object.
	private static boolean isImplemented(DeviceListener listener, String name, Class<?>... paramTypes) {
		try {
			return !listener.getClass().getMethod(name, paramTypes).getDeclaringClass().equals(DeviceListener.class);
		} 
		//Should never happen
		catch (NoSuchMethodException e) {
			e.printStackTrace();
		} 
		catch (SecurityException e) {
			e.printStackTrace();
		}
		return false;
	}
	//Native method that creates a wrapper, registers the listener and returns the address of the wrapper.
	//For more information see above.
	//This long list of boolean values is used to determine if a certain method is actually used in the listener.
	private native long _addDeviceListener(DeviceListener listener, 
			boolean onPairImplemented,
			boolean onUnpairImplemented,
			boolean onConnectImplemented,
			boolean onDisconnectImplemented,
			boolean onArmSyncImplemented,
			boolean onArmUnsyncImplemented,
			boolean onUnlockImplemented,
			boolean onLockImplemented,
			boolean onPoseImplemented,
			boolean onOrientationDataImplemented,
			boolean onAccelerometerDataImplemented,
			boolean onGyroscopeDataImplemented,
			boolean onRssiImplemented,
			boolean onBatteryLevelReceivedImplemented,
			boolean onEmgDataImplemented, 
			boolean onWarmupCompletedImplemented);
	/**
	 * Register a listener to be called when device events occur. 
	 * @param listener The listener to register.
	 * @throws MyoException If this {@link Hub}'s resources have already been released.
	 */
	public void addListener(DeviceListener listener) {
		checkExcept();
		//Call native method and check if each method is implemented
		long address = _addDeviceListener(listener,
				isImplemented(listener, "onPair", Myo.class, long.class, FirmwareVersion.class),
				isImplemented(listener, "onUnpair", Myo.class, long.class),
				isImplemented(listener, "onConnect", Myo.class, long.class, FirmwareVersion.class),
				isImplemented(listener, "onDisconnect", Myo.class, long.class),
				isImplemented(listener, "onArmSync", Myo.class, long.class, Arm.class, XDirection.class, float.class, WarmupState.class),
				isImplemented(listener, "onArmUnsync", Myo.class, long.class),
				isImplemented(listener, "onUnlock", Myo.class, long.class),
				isImplemented(listener, "onLock", Myo.class, long.class),
				isImplemented(listener, "onPose", Myo.class, long.class, Pose.class),
				isImplemented(listener, "onOrientationData", Myo.class, long.class, Quaternion.class),
				isImplemented(listener, "onAccelerometerData", Myo.class, long.class, Vector3.class),
				isImplemented(listener, "onGyroscopeData", Myo.class, long.class, Vector3.class),
				isImplemented(listener, "onRssi", Myo.class, long.class, byte.class),
				isImplemented(listener, "onBatteryLevelReceived", Myo.class, long.class, byte.class),
				isImplemented(listener, "onEmgData", Myo.class, long.class, byte[].class),
				isImplemented(listener, "onWarmupCompleted", Myo.class, long.class, WarmupResult.class));
		//Store the wrapper address in the map
		deviceListenerAddresses.put(listener, address);
	}
	
	//Native method that removes the registered listener and destroys the wrapper.
	private native void _removeDeviceListener(long address);
	/**
	 * Remove a previously registered listener. If the listener was never registered, this method will do nothing.
	 * @param listener The listener to remove.
	 * @throws MyoException If this {@link Hub}'s resources have already been released.
	 */
	public void removeListener(DeviceListener listener) {
		checkExcept();
		//Check if registered first
		if(!deviceListenerAddresses.containsKey(listener)) {
			return;
		}
		//Take the address and pass into native method
		_removeDeviceListener(deviceListenerAddresses.get(listener));
		//Remove from map so we don't accidentally use it again and corrupt the heap
		deviceListenerAddresses.remove(listener);
	}
}

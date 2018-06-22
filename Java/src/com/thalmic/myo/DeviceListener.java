package com.thalmic.myo;

/**
 * A DeviceListener receives events about a {@link Myo}. 
 * @author Tyler Tian
 * @see Hub#addListener(DeviceListener)
 */
public class DeviceListener {

	/**
	 * Called when a {@link Myo} has been paired.
	 * @param myo The {@link Myo} for this event.
	 * @param timestamp The timestamp of when the event is received by the SDK. Timestamps are 64 bit unsigned 
	 * integers that correspond to a number of microseconds since some (unspecified) period in time. 
	 * Timestamps are monotonically non-decreasing. 
	 * @param firmwareVersion The firmware version of <em>myo</em>.
	 */
	public void onPair(Myo myo, long timestamp, FirmwareVersion firmwareVersion) {
	}
	/**
	 * Called when a {@link Myo} has been unpaired.
	 * @param myo The {@link Myo} for this event.
	 * @param timestamp The timestamp of when the event is received by the SDK. Timestamps are 64 bit unsigned 
	 * integers that correspond to a number of microseconds since some (unspecified) period in time. 
	 */
	public void onUnpair(Myo myo, long timestamp) {
	}
	/**
	 * Called when a paired {@link Myo} has been connected.
	 * @param myo The {@link Myo} for this event.
	 * @param timestamp The timestamp of when the event is received by the SDK. Timestamps are 64 bit unsigned 
	 * integers that correspond to a number of microseconds since some (unspecified) period in time. 
	 * Timestamps are monotonically non-decreasing. 
	 * @param firmwareVersion The firmware version of <em>myo</em>.
	 */
	public void onConnect(Myo myo, long timestamp, FirmwareVersion firmwareVersion) {
	}
	/**
	 * Called when a paired {@link Myo} has been disconnected.
	 * @param myo The {@link Myo} for this event.
	 * @param timestamp The timestamp of when the event is received by the SDK. Timestamps are 64 bit unsigned 
	 * integers that correspond to a number of microseconds since some (unspecified) period in time. 
	 */
	public void onDisconnect(Myo myo, long timestamp) {
	}
	/**
	 * Called when a paired {@link Myo} recognizes that it is on an arm.
	 * @param myo The {@link Myo} for this event.
	 * @param timestamp The timestamp of when the event is received by the SDK. Timestamps are 64 bit unsigned 
	 * integers that correspond to a number of microseconds since some (unspecified) period in time. 
	 * @param arm The identified Arm of <em>myo</em>.
	 * @param xDirection The identified XDirection of <em>myo</em>.
	 * @param rotation The estimated rotation of {@link Myo} on the user's arm after a sync. 
	 * @param warmupState The warmup state of <em>myo</em>. If warmupState is equal to {@link WarmupState#warmupStateCold}, 
	 * {@link #onWarmupCompleted(Myo, long, WarmupResult)} will be called when the warmup period has completed. 
	 */
	public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection, float rotation, WarmupState warmupState) {
	}
	/**
	 * Called when a paired {@link Myo} is moved or removed from the arm.
	 * @param myo The {@link Myo} for this event.
	 * @param timestamp The timestamp of when the event is received by the SDK. Timestamps are 64 bit unsigned 
	 * integers that correspond to a number of microseconds since some (unspecified) period in time. 
	 */
	public void onArmUnsync(Myo myo, long timestamp) {
	}
	/**
	 * Called when a paired {@link Myo} becomes unlocked.
	 * @param myo The {@link Myo} for this event.
	 * @param timestamp The timestamp of when the event is received by the SDK. Timestamps are 64 bit unsigned 
	 * integers that correspond to a number of microseconds since some (unspecified) period in time. 
	 */
	public void onUnlock(Myo myo, long timestamp) {
	}
	/**
	 * Called when a paired {@link Myo} becomes locked.
	 * @param myo The {@link Myo} for this event.
	 * @param timestamp The timestamp of when the event is received by the SDK. Timestamps are 64 bit unsigned 
	 * integers that correspond to a number of microseconds since some (unspecified) period in time. 
	 */
	public void onLock(Myo myo, long timestamp) {
	}
	/**
	 * Called when a paired {@link Myo} has provided a new pose.
	 * @param myo The {@link Myo} for this event.
	 * @param timestamp The timestamp of when the event is received by the SDK. Timestamps are 64 bit unsigned 
	 * integers that correspond to a number of microseconds since some (unspecified) period in time. 
	 * @param pose The identified pose of <em>myo</em>.
	 */
	public void onPose(Myo myo, long timestamp, Pose pose) {
	}
	/**
	 * Called when a paired {@link Myo} has provided new orientation data.
	 * @param myo The {@link Myo} for this event.
	 * @param timestamp The timestamp of when the event is received by the SDK. Timestamps are 64 bit unsigned 
	 * integers that correspond to a number of microseconds since some (unspecified) period in time. 
	 * @param rotation The orientation of <em>myo</em>, as a {@link Quaternion}.
	 */
	public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
	}
	/**
	 * Called when a paired {@link Myo} has provided new accelerometer data in units of g.
	 * @param myo The {@link Myo} for this event.
	 * @param timestamp The timestamp of when the event is received by the SDK. Timestamps are 64 bit unsigned 
	 * integers that correspond to a number of microseconds since some (unspecified) period in time. 
	 * @param accel The accelerometer data of <em>myo</em>, in units of g.
	 */
	public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel) {
	}
	/**
	 * Called when a paired {@link Myo} has provided new gyroscope data in units of deg/s.
	 * @param myo The {@link Myo} for this event.
	 * @param timestamp The timestamp of when the event is received by the SDK. Timestamps are 64 bit unsigned 
	 * integers that correspond to a number of microseconds since some (unspecified) period in time. 
	 * @param gyro The gyroscope data of <em>myo</em>, in units of deg/s.
	 */
	public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyro) {
	}
	/**
	 * Called when a paired {@link Myo} has provided a new RSSI value.
	 * @param myo The {@link Myo} for this event.
	 * @param timestamp The timestamp of when the event is received by the SDK. Timestamps are 64 bit unsigned 
	 * integers that correspond to a number of microseconds since some (unspecified) period in time.
	 * @param rssi The RSSI (received signal strength indication) of <em>myo</em>.
	 * @see Myo#requestRssi()
	 */
	public void onRssi(Myo myo, long timestamp, byte rssi) {
	}
	/**
	 * Called when a paired {@link Myo} receives a battery level update.<br>
	 * <br>
	 * Updates occur when the battery level changes and when the battery level is explicitly requested. 
	 * <p>
	 * Note: Technically, the parameter {@code level} is an <em>unsigned</em> 8 bit integer. However, as the
	 * maximum battery level is 100, this detail can be ignored.
	 * </p>
	 * @param myo The {@link Myo} for this event.
	 * @param timestamp The timestamp of when the event is received by the SDK. Timestamps are 64 bit unsigned 
	 * integers that correspond to a number of microseconds since some (unspecified) period in time.
	 * @param level The battery level reported by the myo; the value is a number from 0 to 100 representing the 
	 * percentage of battery life remaining. 
	 * @see Myo#requestBatteryLevel()
	 */
	public void onBatteryLevelReceived(Myo myo, long timestamp, byte level) {
	}
	/**
	 * Called when a paired {@link Myo} has provided new EMG data.
	 * @param myo The {@link Myo} for this event.
	 * @param timestamp The timestamp of when the event is received by the SDK. Timestamps are 64 bit unsigned 
	 * integers that correspond to a number of microseconds since some (unspecified) period in time.
	 * @param emg An array of 8 elements, each corresponding to one sensor.
	 * @see Myo#setStreamEmg(Myo.StreamEmgType)
	 */
	public void onEmgData(Myo myo, long timestamp, byte[] emg) {
	}
	/**
	 * Called when the warmup period for a {@link Myo} has completed.
	 * @param myo The {@link Myo} for this event.
	 * @param timestamp The timestamp of when the event is received by the SDK. Timestamps are 64 bit unsigned 
	 * integers that correspond to a number of microseconds since some (unspecified) period in time.
	 * @param warmupResult The warmup result of <em>myo</em>.
	 */
	public void onWarmupCompleted(Myo myo, long timestamp, WarmupResult warmupResult) {
	}
}

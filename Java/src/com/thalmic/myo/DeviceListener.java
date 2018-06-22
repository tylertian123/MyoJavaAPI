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
	 * {@link #onWarmupCompleted()} will be called when the warmup period has completed. 
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
	public void onUnlock(Myo myo, long timestamp) {
	}
	public void onLock(Myo myo, long timestamp) {
	}
	public void onPose(Myo myo, long timestamp, Pose pose) {
	}
	public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
	}
	public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel) {
	}
	public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyro) {
	}
	public void onRssi(Myo myo, long timestamp, byte rssi) {
	}
	public void onBatteryLevelReceived(Myo myo, long timestamp, byte level) {
	}
	public void onEmgData(Myo myo, long timestamp, byte[] emg) {
	}
	public void onWarmupCompleted(Myo myo, long timestamp, WarmupResult warmupResult) {
	}
}

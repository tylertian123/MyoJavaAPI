package com.thalmic.myo;

public class DeviceListener {
	
	public void debug(int i) {
		System.out.println(i);
	}
	
	public void onPair(Myo myo, long timestamp, FirmwareVersion firmwareVersion) {
	}
	public void onUnpair(Myo myo, long timestamp) {
	}
	public void onConnect(Myo myo, long timestamp, FirmwareVersion firmwareVersion) {
	}
	public void onDisconnect(Myo myo, long timestamp) {
	}
	public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection, float rotation, WarmupState warmupState) {
	}
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
	public void onBatteryLevelReceived(Myo myo, long timestamp, short level) {
	}
	public void onEmgData(Myo myo, long timestamp, byte[] emg) {
	}
	public void onWarmupCompleted(Myo myo, long timestamp, WarmupResult warmupResult) {
	}
}

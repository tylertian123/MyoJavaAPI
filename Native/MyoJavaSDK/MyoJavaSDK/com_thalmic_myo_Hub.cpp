#include "com_thalmic_myo_Hub.h"
#include <stdexcept>
#include <myo/myo.hpp>

using namespace std;
using namespace myo;

#define JNI_CHECK_EXCEPT(env) if(env->ExceptionCheck() == JNI_TRUE) { env->ExceptionDescribe(); }
#define THROW_JNI_EXCEPTION(env, message) env->ThrowNew(env->FindClass("com/thalmic/myo/JNIException"), message)

Hub* getPointer(JNIEnv *env, jobject obj) {
	jfieldID fid = env->GetFieldID(env->GetObjectClass(obj), "_nativePointer", "J");
	return reinterpret_cast<Hub*>(env->GetLongField(obj, fid));
}

class ListenerWrapper : public DeviceListener {

public:
	jobject jlistener;

	jclass listenerClass;

	JavaVM *jvm;
	
	jclass myoClass, firmwareVersionClass, armClass, xDirectionClass, warmupStateClass, poseClass,
		quaternionClass, vector3Class, warmupResultClass;

	jmethodID myoConstructor, firmwareVersionConstructor, quaternionConstructor, vector3Constructor;

	jmethodID onPairMid, onUnpairMid, onConnectMid, onDisconnectMid, onArmSyncMid, onArmUnsyncMid,
		onLockMid, onUnlockMid, onPoseMid, onOrientationDataMid, onAccelerometerDataMid, onGyroscopeDataMid,
		onRssiMid, onBatteryLevelReceivedMid, onEmgDataMid, onWarmupCompletedMid;

	jfieldID fvMajorFid, fvMinorFid, fvPatchFid, fvHardwareRevFid;
	jfieldID armLeftFid, armRightFid, armUnknownFid;
	jfieldID xDirElbowFid, xDirWristFid, xDirUnknownFid;
	jfieldID warmupWarmFid, warmupColdFid, warmupUnknownFid;
	jfieldID poseRestFid, poseUnknownFid, poseFistFid, poseFingersSpreadFid, poseWaveInFid, poseWaveOutFid,
		poseDoubleTapFid;
	jfieldID warmupResultSuccessFid, warmupResultFailedFid, warmupResultUnknownFid;

	JNIEnv* getJNIEnv() {
		JNIEnv *env;
		int result = jvm->GetEnv((void **)&env, JNI_VERSION_1_8);
		if (result == JNI_EDETACHED) {
			result = jvm->AttachCurrentThread((void **)&env, nullptr);
			if (result != JNI_OK) {
				THROW_JNI_EXCEPTION(env, (string("Unexpected error: Cannot attach current thread: ") + to_string(result)).c_str());
				return nullptr;
			}
		}
		else if (result != JNI_OK) {
			THROW_JNI_EXCEPTION(env, (string("Unexpected error: Cannot get JNI environment: ") + to_string(result)).c_str());
			return nullptr;
		}
		return env;
	}
	
	static jclass makeGlobal(JNIEnv *env, jclass clazz) {
		jclass ref = (jclass)env->NewGlobalRef(clazz);
		if (!ref) {
			THROW_JNI_EXCEPTION(env, "Failed to make global reference for class; JVM is out of memory");
			return nullptr;
		}
		return ref;
	}

	ListenerWrapper(jobject listener, JNIEnv *env) {
		jint result = env->GetJavaVM(&jvm);
		if (result != JNI_OK) {
			THROW_JNI_EXCEPTION(env, (string("Unexpected error: Cannot get JVM pointer: ") + to_string(result)).c_str());
			return;
		}
		listenerClass = makeGlobal(env, env->GetObjectClass(listener));
		jlistener = env->NewGlobalRef(listener);
		if (!jlistener) {
			THROW_JNI_EXCEPTION(env, "Failed to make global reference for object; JVM is out of memory");
			return;
		}

		onPairMid = env->GetMethodID(listenerClass, "onPair", "(Lcom/thalmic/myo/Myo;JLcom/thalmic/myo/FirmwareVersion;)V");
		onUnpairMid = env->GetMethodID(listenerClass, "onUnpair", "(Lcom/thalmic/myo/Myo;J)V");
		onConnectMid = env->GetMethodID(listenerClass, "onConnect", "(Lcom/thalmic/myo/Myo;JLcom/thalmic/myo/FirmwareVersion;)V");
		onDisconnectMid = env->GetMethodID(listenerClass, "onDisconnect", "(Lcom/thalmic/myo/Myo;J)V");
		onArmSyncMid = env->GetMethodID(listenerClass, "onArmSync", "(Lcom/thalmic/myo/Myo;JLcom/thalmic/myo/Arm;Lcom/thalmic/myo/XDirection;FLcom/thalmic/myo/WarmupState;)V");
		onArmUnsyncMid = env->GetMethodID(listenerClass, "onArmUnsync", "(Lcom/thalmic/myo/Myo;J)V");
		onLockMid = env->GetMethodID(listenerClass, "onLock", "(Lcom/thalmic/myo/Myo;J)V");
		onUnlockMid = env->GetMethodID(listenerClass, "onUnlock", "(Lcom/thalmic/myo/Myo;J)V");
		onPoseMid = env->GetMethodID(listenerClass, "onPose", "(Lcom/thalmic/myo/Myo;JLcom/thalmic/myo/Pose;)V");
		onOrientationDataMid = env->GetMethodID(listenerClass, "onOrientationData", "(Lcom/thalmic/myo/Myo;JLcom/thalmic/myo/Quaternion;)V");
		onAccelerometerDataMid = env->GetMethodID(listenerClass, "onAccelerometerData", "(Lcom/thalmic/myo/Myo;JLcom/thalmic/myo/Vector3;)V");
		onGyroscopeDataMid = env->GetMethodID(listenerClass, "onGyroscopeData", "(Lcom/thalmic/myo/Myo;JLcom/thalmic/myo/Vector3;)V");
		onRssiMid = env->GetMethodID(listenerClass, "onRssi", "(Lcom/thalmic/myo/Myo;JB)V");
		onBatteryLevelReceivedMid = env->GetMethodID(listenerClass, "onBatteryLevelReceived", "(Lcom/thalmic/myo/Myo;JB)V");
		onEmgDataMid = env->GetMethodID(listenerClass, "onEmgData", "(Lcom/thalmic/myo/Myo;J[B)V");
		onWarmupCompletedMid = env->GetMethodID(listenerClass, "onWarmupCompleted", "(Lcom/thalmic/myo/Myo;JLcom/thalmic/myo/WarmupResult;)V");

		myoClass = makeGlobal(env, env->FindClass("com/thalmic/myo/Myo"));
		firmwareVersionClass = makeGlobal(env, env->FindClass("com/thalmic/myo/FirmwareVersion"));
		armClass = makeGlobal(env, env->FindClass("com/thalmic/myo/Arm"));
		xDirectionClass = makeGlobal(env, env->FindClass("com/thalmic/myo/XDirection"));
		warmupStateClass = makeGlobal(env, env->FindClass("com/thalmic/myo/WarmupState"));
		poseClass = makeGlobal(env, env->FindClass("com/thalmic/myo/Pose"));
		quaternionClass = makeGlobal(env, env->FindClass("com/thalmic/myo/Quaternion"));
		vector3Class = makeGlobal(env, env->FindClass("com/thalmic/myo/Vector3"));
		warmupResultClass = makeGlobal(env, env->FindClass("com/thalmic/myo/WarmupResult"));

		myoConstructor = env->GetMethodID(myoClass, "<init>", "(J)V");
		firmwareVersionConstructor = env->GetMethodID(firmwareVersionClass, "<init>", "()V");
		quaternionConstructor = env->GetMethodID(quaternionClass, "<init>", "(DDDD)V");
		vector3Constructor = env->GetMethodID(vector3Class, "<init>", "(DDD)V");

		fvMajorFid = env->GetFieldID(firmwareVersionClass, "firmwareVersionMajor", "I");
		fvMinorFid = env->GetFieldID(firmwareVersionClass, "firmwareVersionMinor", "I");
		fvPatchFid = env->GetFieldID(firmwareVersionClass, "firmwareVersionPatch", "I");
		fvHardwareRevFid = env->GetFieldID(firmwareVersionClass, "firmwareVersionHardwareRev", "I");

		armLeftFid = env->GetStaticFieldID(env->FindClass("com/thalmic/myo/Arm"), "armLeft", "Lcom/thalmic/myo/Arm;");
		armRightFid = env->GetStaticFieldID(armClass, "armRight", "Lcom/thalmic/myo/Arm;");
		armUnknownFid = env->GetStaticFieldID(armClass, "armUnknown", "Lcom/thalmic/myo/Arm;");

		xDirElbowFid = env->GetStaticFieldID(xDirectionClass, "xDirectionTowardsElbow", "Lcom/thalmic/myo/XDirection;");
		xDirWristFid = env->GetStaticFieldID(xDirectionClass, "xDirectionTowardsWrist", "Lcom/thalmic/myo/XDirection;");
		xDirUnknownFid = env->GetStaticFieldID(xDirectionClass, "xDirectionUnknown", "Lcom/thalmic/myo/XDirection;");

		warmupColdFid = env->GetStaticFieldID(warmupStateClass, "warmupStateCold", "Lcom/thalmic/myo/WarmupState;");
		warmupWarmFid = env->GetStaticFieldID(warmupStateClass, "warmupStateWarm", "Lcom/thalmic/myo/WarmupState;");
		warmupUnknownFid = env->GetStaticFieldID(warmupStateClass, "warmupStateUnknown", "Lcom/thalmic/myo/WarmupState;");

		poseRestFid = env->GetStaticFieldID(poseClass, "rest", "Lcom/thalmic/myo/Pose;");
		poseUnknownFid = env->GetStaticFieldID(poseClass, "unknown", "Lcom/thalmic/myo/Pose;");
		poseFistFid = env->GetStaticFieldID(poseClass, "fist", "Lcom/thalmic/myo/Pose;");
		poseFingersSpreadFid = env->GetStaticFieldID(poseClass, "fingersSpread", "Lcom/thalmic/myo/Pose;");
		poseWaveInFid = env->GetStaticFieldID(poseClass, "waveIn", "Lcom/thalmic/myo/Pose;");
		poseWaveOutFid = env->GetStaticFieldID(poseClass, "waveOut", "Lcom/thalmic/myo/Pose;");
		poseDoubleTapFid = env->GetStaticFieldID(poseClass, "doubleTap", "Lcom/thalmic/myo/Pose;");

		warmupResultSuccessFid = env->GetStaticFieldID(warmupResultClass, "warmupResultSuccess", "Lcom/thalmic/myo/WarmupResult;");
		warmupResultFailedFid = env->GetStaticFieldID(warmupResultClass, "warmupResultFailedTimeout", "Lcom/thalmic/myo/WarmupResult");
		warmupResultUnknownFid = env->GetStaticFieldID(warmupResultClass, "warmupResultUnknown", "Lcom/thalmic/myo/WarmupResult");
	}

	~ListenerWrapper() {
		JNIEnv *env = getJNIEnv();

		env->DeleteGlobalRef(listenerClass);
		env->DeleteGlobalRef(myoClass);
		env->DeleteGlobalRef(firmwareVersionClass);
		env->DeleteGlobalRef(armClass);
		env->DeleteGlobalRef(xDirectionClass);
		env->DeleteGlobalRef(warmupStateClass);
		env->DeleteGlobalRef(poseClass);
		env->DeleteGlobalRef(quaternionClass);
		env->DeleteGlobalRef(vector3Class);
		env->DeleteGlobalRef(warmupResultClass);
		env->DeleteGlobalRef(jlistener);
	}

	jobject createMyo(JNIEnv *env, Myo *myo) {
		jobject m = env->NewObject(myoClass, myoConstructor, reinterpret_cast<jlong>(myo));
		if (env->ExceptionCheck() == JNI_TRUE) {
			cerr << "Exception when creating Myo object" << endl;
			env->ExceptionDescribe();
			return nullptr;
		}
		return m;
	}
	jobject createFirmwareVersion(JNIEnv *env, FirmwareVersion firmwareVersion) {
		jobject fv = env->NewObject(firmwareVersionClass, firmwareVersionConstructor);

		if (env->ExceptionCheck() == JNI_TRUE) {
			cerr << "Exception when creating FirmwareVersion object" << endl;
			env->ExceptionDescribe();
			return nullptr;
		}

		env->SetIntField(fv, fvMajorFid, firmwareVersion.firmwareVersionMajor);
		env->SetIntField(fv, fvMinorFid, firmwareVersion.firmwareVersionMinor);
		env->SetIntField(fv, fvPatchFid, firmwareVersion.firmwareVersionPatch);
		env->SetIntField(fv, fvHardwareRevFid, firmwareVersion.firmwareVersionHardwareRev);

		if (env->ExceptionCheck() == JNI_TRUE) {
			cerr << "Exception when setting fields for FirmwareVersion object" << endl;
			env->ExceptionDescribe();
			return nullptr;
		}
		return fv;
	}
	jobject createQuaternion(JNIEnv *env, const Quaternion<float> *q) {
		jobject quatObject = env->NewObject(quaternionClass, quaternionConstructor, 
			static_cast<jdouble>(q->x()), static_cast<jdouble>(q->y()), static_cast<jdouble>(q->z()), static_cast<jdouble>(q->w()));
		if (env->ExceptionCheck == JNI_TRUE) {
			cerr << "Exception when creating Quaternion object" << endl;
			env->ExceptionDescribe();
			return nullptr;
		}

		return quatObject;
	}
	jobject createVector3(JNIEnv *env, const Vector3<float> *v) {
		jobject vecObject = env->NewObject(vector3Class, vector3Constructor,
			static_cast<jdouble>(v->x()), static_cast<jdouble>(v->y()), static_cast<jdouble>(v->z()));
		if (env->ExceptionCheck == JNI_TRUE) {
			cerr << "Exception when creating Vector3 object" << endl;
			env->ExceptionDescribe();
			return nullptr;
		}

		return vecObject;
	}

	void onPair(Myo *myo, uint64_t timestamp, FirmwareVersion firmwareVersion) override {
		JNIEnv *env = getJNIEnv();
		jobject myoObject = createMyo(env, myo);
		jlong time = (jlong) timestamp;
		jobject fv = createFirmwareVersion(env, firmwareVersion);

		env->CallVoidMethod(jlistener, onPairMid, myoObject, time, fv);
	}

	void onUnpair(Myo *myo, uint64_t timestamp) override {
		JNIEnv *env = getJNIEnv();
		jobject myoObject = createMyo(env, myo);
		jlong time = (jlong) timestamp;

		env->CallVoidMethod(jlistener, onUnpairMid, myoObject, time);
	}

	void onConnect(Myo *myo, uint64_t timestamp, FirmwareVersion firmwareVersion) override {
		JNIEnv *env = getJNIEnv();
		jobject myoObject = createMyo(env, myo);
		jlong time = (jlong) timestamp;
		jobject fv = createFirmwareVersion(env, firmwareVersion);

		env->CallVoidMethod(jlistener, onConnectMid, myoObject, time, fv);
	}

	void onDisconnect(Myo *myo, uint64_t timestamp) override {
		JNIEnv *env = getJNIEnv();
		jobject myoObject = createMyo(env, myo);
		jlong time = (jlong) timestamp;

		env->CallVoidMethod(jlistener, onDisconnectMid, myoObject, time);
	}

	void onArmSync(Myo *myo, uint64_t timestamp, Arm arm, XDirection xDirection, float rotation, WarmupState warmupState) override {
		JNIEnv *env = getJNIEnv();
		jobject myoObject = createMyo(env, myo);
		jlong time = (jlong)timestamp;

		jobject armEnum;
		if (arm == Arm::armLeft) {
			armEnum = env->GetStaticObjectField(armClass, armLeftFid);
		}
		else if (arm == Arm::armRight) {
			armEnum = env->GetStaticObjectField(armClass, armRightFid);
		}
		else {
			armEnum = env->GetStaticObjectField(armClass, armUnknownFid);
		}
		JNI_CHECK_EXCEPT(env);

		jobject xDirectionEnum;
		if (xDirection == XDirection::xDirectionTowardElbow) {
			xDirectionEnum = env->GetStaticObjectField(xDirectionClass, xDirElbowFid);
		}
		else if (xDirection == XDirection::xDirectionTowardWrist) {
			xDirectionEnum = env->GetStaticObjectField(xDirectionClass, xDirWristFid);
		}
		else {
			xDirectionEnum = env->GetStaticObjectField(xDirectionClass, xDirUnknownFid);
		}
		JNI_CHECK_EXCEPT(env);

		jfloat jRotation = rotation;

		jobject warmupStateEnum;
		if (warmupState == WarmupState::warmupStateCold) {
			warmupStateEnum = env->GetStaticObjectField(warmupStateClass, warmupColdFid);
		}
		else if (warmupState == WarmupState::warmupStateWarm) {
			warmupStateEnum = env->GetStaticObjectField(warmupStateClass, warmupWarmFid);
		}
		else {
			warmupStateEnum = env->GetStaticObjectField(warmupStateClass, warmupUnknownFid);
		}
		JNI_CHECK_EXCEPT(env);

		env->CallVoidMethod(jlistener, onArmSyncMid, myoObject, time, armEnum, xDirectionEnum, jRotation, warmupStateEnum);
	}

	void onArmUnsync(Myo *myo, uint64_t timestamp) override {
		JNIEnv *env = getJNIEnv();
		jobject myoObject = createMyo(env, myo);
		jlong time = (jlong)timestamp;
		
		env->CallVoidMethod(jlistener, onArmUnsyncMid, myoObject, time);
	}
	
	void onLock(Myo *myo, uint64_t timestamp) override {
		JNIEnv *env = getJNIEnv();
		jobject myoObject = createMyo(env, myo);
		jlong time = (jlong)timestamp;

		env->CallVoidMethod(jlistener, onLockMid, myoObject, time);
	}

	void onUnlock(Myo *myo, uint64_t timestamp) override {
		JNIEnv *env = getJNIEnv();
		jobject myoObject = createMyo(env, myo);
		jlong time = (jlong)timestamp;

		env->CallVoidMethod(jlistener, onUnlockMid, myoObject, time);
	}

	void onPose(Myo *myo, uint64_t timestamp, Pose pose) override {
		JNIEnv *env = getJNIEnv();
		jobject myoObject = createMyo(env, myo);
		jlong time = (jlong)timestamp;

		jobject poseEnum;
		switch (pose.type()) {
		case Pose::fist:
			poseEnum = env->GetStaticObjectField(poseClass, poseFistFid);
			break;
		case Pose::fingersSpread:
			poseEnum = env->GetStaticObjectField(poseClass, poseFingersSpreadFid);
			break;
		case Pose::waveIn:
			poseEnum = env->GetStaticObjectField(poseClass, poseWaveInFid);
			break;
		case Pose::waveOut:
			poseEnum = env->GetStaticObjectField(poseClass, poseWaveOutFid);
			break;
		case Pose::doubleTap:
			poseEnum = env->GetStaticObjectField(poseClass, poseDoubleTapFid);
			break;
		case Pose::rest:
			poseEnum = env->GetStaticObjectField(poseClass, poseRestFid);
			break;
		case Pose::unknown:
			poseEnum = env->GetStaticObjectField(poseClass, poseUnknownFid);
			break;
		default:
			poseEnum = env->GetStaticObjectField(poseClass, poseUnknownFid);
		}

		env->CallVoidMethod(jlistener, onPoseMid, myoObject, time, poseEnum);
	}

	void onOrientationData(Myo *myo, uint64_t timestamp, const Quaternion<float> &orientation) override {
		JNIEnv *env = getJNIEnv();
		jobject myoObject = createMyo(env, myo);
		jlong time = (jlong)timestamp;
		jobject quatObject = createQuaternion(env, &orientation);

		env->CallVoidMethod(jlistener, onOrientationDataMid, myoObject, time, quatObject);
	}

	void onAccelerometerData(Myo *myo, uint64_t timestamp, const Vector3<float> &accel) override {
		JNIEnv *env = getJNIEnv();
		jobject myoObject = createMyo(env, myo);
		jlong time = (jlong)timestamp;
		jobject vecObject = createVector3(env, &accel);

		env->CallVoidMethod(jlistener, onAccelerometerDataMid, myoObject, time, vecObject);
	}

	void onGyroscopeData(Myo *myo, uint64_t timestamp, const Vector3<float> &gyro) override {
		JNIEnv *env = getJNIEnv();
		jobject myoObject = createMyo(env, myo);
		jlong time = (jlong)timestamp;
		jobject vecObject = createVector3(env, &gyro);

		env->CallVoidMethod(jlistener, onGyroscopeDataMid, myoObject, time, vecObject);
	}

	void onRssi(Myo *myo, uint64_t timestamp, int8_t rssi) override {
		JNIEnv *env = getJNIEnv();
		jobject myoObject = createMyo(env, myo);
		jlong time = (jlong)timestamp;
		jbyte jRssi = static_cast<jbyte>(rssi);

		env->CallVoidMethod(jlistener, onRssiMid, myoObject, time, jRssi);
	}

	void onBatteryLevelReceived(Myo *myo, uint64_t timestamp, uint8_t batteryLevel) override {
		JNIEnv *env = getJNIEnv();
		jobject myoObject = createMyo(env, myo);
		jlong time = (jlong)timestamp;
		jbyte jBattLevel = static_cast<jbyte>(batteryLevel);

		env->CallVoidMethod(jlistener, onBatteryLevelReceivedMid, myoObject, time, jBattLevel);
	}

	void onEmgData(Myo *myo, uint64_t timestamp, const int8_t *emg) {
		JNIEnv *env = getJNIEnv();
		jobject myoObject = createMyo(env, myo);
		jlong time = (jlong)timestamp;
		
		jbyteArray emgArray = env->NewByteArray(8);
		env->SetByteArrayRegion(emgArray, 0, 8, emg);

		env->CallVoidMethod(jlistener, onEmgDataMid, myoObject, time, emgArray);
	}

	void onWarmupCompleted(Myo *myo, uint64_t timestamp, WarmupResult warmupResult) override {
		JNIEnv *env = getJNIEnv();
		jobject myoObject = createMyo(env, myo);
		jlong time = (jlong)timestamp;

		jobject warmupResultEnum;
		if (warmupResult == WarmupResult::warmupResultSuccess) {
			warmupResultEnum = env->GetStaticObjectField(listenerClass, warmupResultSuccessFid);
		}
		else if (warmupResult = WarmupResult::warmupResultFailedTimeout) {
			warmupResultEnum = env->GetStaticObjectField(listenerClass, warmupResultFailedFid);
		}
		else {
			warmupResultEnum = env->GetStaticObjectField(listenerClass, warmupResultUnknownFid);
		}

		env->CallVoidMethod(jlistener, onWarmupCompletedMid, myoObject, time, warmupResultEnum);
	}
};

JNIEXPORT void JNICALL Java_com_thalmic_myo_Hub__1initHub(JNIEnv *env, jobject obj, jstring appID) {
	try {
		const char *appIDNative = env->GetStringUTFChars(appID, 0);
		Hub *hub = new Hub(appIDNative);
		env->ReleaseStringUTFChars(appID, appIDNative);

		jfieldID pointerFid = env->GetFieldID(env->GetObjectClass(obj), "_nativePointer", "J");
		env->SetLongField(obj, pointerFid, reinterpret_cast<jlong>(hub));
	}
	catch (invalid_argument &e) {
		jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
		env->ThrowNew(exceptionClass, e.what());
	}
	catch (runtime_error &e) {
		jclass exceptionClass = env->FindClass("com/thalmic/myo/MyoException");
		env->ThrowNew(exceptionClass, e.what());
	}
	catch (...) {
		jclass exceptionClass = env->FindClass("java/lang/Exception");
		env->ThrowNew(exceptionClass, "Unexpected error");
	}
}

JNIEXPORT void JNICALL Java_com_thalmic_myo_Hub__1release(JNIEnv *env, jobject obj) {
	Hub *hub = getPointer(env, obj);
	delete hub;
}

JNIEXPORT void JNICALL Java_com_thalmic_myo_Hub__1setLockingPolicy(JNIEnv *env, jobject obj, jint policy) {
	if (policy == com_thalmic_myo_Hub_POLICY_NONE) {
		getPointer(env, obj)->setLockingPolicy(Hub::lockingPolicyNone);
	}
	else {
		getPointer(env, obj)->setLockingPolicy(Hub::lockingPolicyStandard);
	}
}

JNIEXPORT void JNICALL Java_com_thalmic_myo_Hub__1run(JNIEnv *env, jobject obj, jint duration) {
	getPointer(env, obj)->run(duration);
}

JNIEXPORT void JNICALL Java_com_thalmic_myo_Hub__1runOnce(JNIEnv *env, jobject obj, jint duration) {
	getPointer(env, obj)->runOnce(duration);
}

JNIEXPORT jboolean JNICALL Java_com_thalmic_myo_Hub__1waitForMyo(JNIEnv *env, jobject obj, jint duration) {
	Myo *myo = getPointer(env, obj)->waitForMyo(duration);
	
	if (!myo) {
		return false;
	}

	jfieldID pointerFid = env->GetFieldID(env->GetObjectClass(obj), "_myoAddress", "J");
	env->SetLongField(obj, pointerFid, reinterpret_cast<jlong>(myo));
	return true;
}

JNIEXPORT jlong JNICALL Java_com_thalmic_myo_Hub__1addDeviceListener(JNIEnv *env, jobject obj, jobject listener) {
	ListenerWrapper *wrapper = new ListenerWrapper(listener, env);
	getPointer(env, obj)->addListener(wrapper);
	
	return reinterpret_cast<jlong>(wrapper);
}

JNIEXPORT void JNICALL Java_com_thalmic_myo_Hub__1removeDeviceListener(JNIEnv *env, jobject obj, jlong address) {
	ListenerWrapper *wrapper = reinterpret_cast<ListenerWrapper*>(address);
	getPointer(env, obj)->removeListener(wrapper);

	delete wrapper;
}
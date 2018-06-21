#include "com_thalmic_myo_Hub.h"
#include <stdexcept>
#include <myo/myo.hpp>

using namespace std;
using namespace myo;

Hub* getPointer(JNIEnv *env, jobject obj) {
	jfieldID fid = env->GetFieldID(env->GetObjectClass(obj), "_nativePointer", "J");
	return reinterpret_cast<Hub*>(env->GetLongField(obj, fid));
}

class ListenerWrapper : public DeviceListener {

public:
	jobject jlistener;
	jclass listenerClass;
	JNIEnv *env;

	jmethodID onPairMid, onUnpairMid, onConnectMid, onDisconnectMid;

	jclass myoClass, firmwareVersionClass;

	jmethodID myoConstructor, firmwareVersionConstructor;

	jfieldID firmwareVersionMajorFid, firmwareVersionMinorFid, firmwareVersionPatchFid, firmwareVersionHardwareRevFid;

	ListenerWrapper(JNIEnv *env, jobject listener) : jlistener(listener), env(env) {
		listenerClass = env->GetObjectClass(listener);

		onPairMid = env->GetMethodID(listenerClass, "onPair", "(Lcom/thalmic/myo/Myo;JLcom/thalmic/myo/FirmwareVersion;)V");
		onUnpairMid = env->GetMethodID(listenerClass, "onUnpair", "(Lcom/thalmic/myo/Myo;J)V");
		onConnectMid = env->GetMethodID(listenerClass, "onConnect", "(Lcom/thalmic/myo/Myo;JLcom/thalmic/myo/FirmwareVersion;)V");
		onDisconnectMid = env->GetMethodID(listenerClass, "onDisconnect", "(Lcom/thalmic/myo/Myo;J)V");

		myoClass = env->FindClass("com/thalmic/myo/Myo");
		firmwareVersionClass = env->FindClass("com/thalmic/myo/FirmwareVersion");

		myoConstructor = env->GetMethodID(myoClass, "<init>", "(J)V");
		firmwareVersionConstructor = env->GetMethodID(firmwareVersionClass, "<init>", "()V");

		firmwareVersionMajorFid = env->GetFieldID(firmwareVersionClass, "firmwareVersionMajor", "I");
		firmwareVersionMinorFid = env->GetFieldID(firmwareVersionClass, "firmwareVersionMinor", "I");
		firmwareVersionPatchFid = env->GetFieldID(firmwareVersionClass, "firmwareVersionPatch", "I");
		firmwareVersionHardwareRevFid = env->GetFieldID(firmwareVersionClass, "firmwareVersionHardwareRev", "I");
	}

	jobject createMyo(Myo *myo) {
		return env->NewObject(myoClass, myoConstructor, reinterpret_cast<jlong>(myo));
	}
	jobject createFirmwareVersion(FirmwareVersion firmwareVersion) {
		jobject fv = env->NewObject(firmwareVersionClass, firmwareVersionConstructor);

		env->SetIntField(fv, firmwareVersionMajorFid, firmwareVersion.firmwareVersionMajor);
		env->SetIntField(fv, firmwareVersionMinorFid, firmwareVersion.firmwareVersionMinor);
		env->SetIntField(fv, firmwareVersionPatchFid, firmwareVersion.firmwareVersionPatch);
		env->SetIntField(fv, firmwareVersionHardwareRevFid, firmwareVersion.firmwareVersionHardwareRev);
		return fv;
	}

	void onPair(Myo *myo, uint64_t timestamp, FirmwareVersion firmwareVersion) override {
		jobject myoObject = createMyo(myo);
		jlong time = timestamp;
		jobject fv = createFirmwareVersion(firmwareVersion);

		env->CallVoidMethod(jlistener, onPairMid, myoObject, time, fv);
	}

	void onUnpair(Myo *myo, uint64_t timestamp) override {
		jobject myoObject = createMyo(myo);
		jlong time = timestamp;

		env->CallVoidMethod(jlistener, onUnpairMid, myoObject, time);
	}

	void onConnect(Myo *myo, uint64_t timestamp, FirmwareVersion firmwareVersion) override {
		jobject myoObject = createMyo(myo);
		jlong time = timestamp;
		jobject fv = createFirmwareVersion(firmwareVersion);

		env->CallVoidMethod(jlistener, onConnectMid, myoObject, time, fv);
	}

	void onDisconnect(Myo *myo, uint64_t timestamp) override {
		jobject myoObject = createMyo(myo);
		jlong time = timestamp;

		env->CallVoidMethod(jlistener, onDisconnectMid, myoObject, time);
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
		env->ThrowNew(exceptionClass, "Invalid application identifier");
	}
	catch (runtime_error &e) {
		jclass exceptionClass = env->FindClass("com/thalmic/myo/MyoException");
		env->ThrowNew(exceptionClass, "Failed to connect to Hub");
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
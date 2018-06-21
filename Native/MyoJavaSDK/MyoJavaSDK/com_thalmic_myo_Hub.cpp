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

	JavaVM *jvm;
	
	jclass myoClass, firmwareVersionClass;

	jmethodID onPairMid, onUnpairMid, onConnectMid, onDisconnectMid;

	jmethodID myoConstructor, firmwareVersionConstructor;

	jfieldID fvMajorFid, fvMinorFid, fvPatchFid, fvHardwareRevFid;

	JNIEnv* getJNIEnv() {
		JNIEnv *env;
		int result = jvm->GetEnv((void **)&env, JNI_VERSION_1_8);
		if (result == JNI_EDETACHED) {
			result = jvm->AttachCurrentThread((void **)&env, nullptr);
			if (result != JNI_OK) {
				jclass exceptionClass = env->FindClass("com/thalmic/myo/JNIException");
				env->ThrowNew(exceptionClass, (string("Unexpected error: Cannot attach current thread: ") + to_string(result)).c_str());
				return nullptr;
			}
		}
		else if (result != JNI_OK) {
			jclass exceptionClass = env->FindClass("com/thalmic/myo/JNIException");
			env->ThrowNew(exceptionClass, (string("Unexpected error: Cannot get JNI environment: ") + to_string(result)).c_str());
			return nullptr;
		}
		return env;
	}
	
	static jclass makeGlobal(JNIEnv *env, jclass clazz) {
		jclass ref = (jclass)env->NewGlobalRef(clazz);
		if (!ref) {
			jclass exceptionClass = env->FindClass("com/thalmic/myo/JNIException");
			env->ThrowNew(exceptionClass, "Failed to make global reference for class; JVM is out of memory");
			return nullptr;
		}
		return ref;
	}

	ListenerWrapper(jobject listener, JNIEnv *env) {
		jint result = env->GetJavaVM(&jvm);
		if (result != JNI_OK) {
			jclass exceptionClass = env->FindClass("com/thalmic/myo/JNIException");
			env->ThrowNew(exceptionClass, (string("Unexpected error: Cannot get JVM pointer: ") + to_string(result)).c_str());
			return;
		}
		listenerClass = makeGlobal(env, env->GetObjectClass(listener));
		jlistener = env->NewGlobalRef(listener);
		if (!jlistener) {
			jclass exceptionClass = env->FindClass("com/thalmic/myo/JNIException");
			env->ThrowNew(exceptionClass, "Failed to make global reference for object; JVM is out of memory");
			return;
		}

		onPairMid = env->GetMethodID(listenerClass, "onPair", "(Lcom/thalmic/myo/Myo;JLcom/thalmic/myo/FirmwareVersion;)V");
		onUnpairMid = env->GetMethodID(listenerClass, "onUnpair", "(Lcom/thalmic/myo/Myo;J)V");
		onConnectMid = env->GetMethodID(listenerClass, "onConnect", "(Lcom/thalmic/myo/Myo;JLcom/thalmic/myo/FirmwareVersion;)V");
		onDisconnectMid = env->GetMethodID(listenerClass, "onDisconnect", "(Lcom/thalmic/myo/Myo;J)V");

		myoClass = makeGlobal(env, env->FindClass("com/thalmic/myo/Myo"));
		firmwareVersionClass = makeGlobal(env, env->FindClass("com/thalmic/myo/FirmwareVersion"));

		myoConstructor = env->GetMethodID(myoClass, "<init>", "(J)V");
		firmwareVersionConstructor = env->GetMethodID(firmwareVersionClass, "<init>", "()V");

		fvMajorFid = env->GetFieldID(firmwareVersionClass, "firmwareVersionMajor", "I");
		fvMinorFid = env->GetFieldID(firmwareVersionClass, "firmwareVersionMinor", "I");
		fvPatchFid = env->GetFieldID(firmwareVersionClass, "firmwareVersionPatch", "I");
		fvHardwareRevFid = env->GetFieldID(firmwareVersionClass, "firmwareVersionHardwareRev", "I");
	}

	~ListenerWrapper() {
		JNIEnv *env = getJNIEnv();

		env->DeleteGlobalRef(listenerClass);
		env->DeleteGlobalRef(myoClass);
		env->DeleteGlobalRef(firmwareVersionClass);
		env->DeleteGlobalRef(jlistener);
	}

	jobject createMyo(JNIEnv *env, Myo *myo) {
		jobject m = env->NewObject(myoClass, myoConstructor, reinterpret_cast<jlong>(myo));
		if (env->ExceptionCheck() == JNI_TRUE) {
			cerr << "Exception occurred" << endl;
			env->ExceptionDescribe();
			return nullptr;
		}
		return m;
	}
	jobject createFirmwareVersion(JNIEnv *env, FirmwareVersion firmwareVersion) {
		jobject fv = env->NewObject(firmwareVersionClass, firmwareVersionConstructor);

		if (env->ExceptionCheck() == JNI_TRUE) {
			cerr << "Exception occurred" << endl;
			env->ExceptionDescribe();
			return nullptr;
		}

		env->SetIntField(fv, fvMajorFid, firmwareVersion.firmwareVersionMajor);
		env->SetIntField(fv, fvMinorFid, firmwareVersion.firmwareVersionMinor);
		env->SetIntField(fv, fvPatchFid, firmwareVersion.firmwareVersionPatch);
		env->SetIntField(fv, fvHardwareRevFid, firmwareVersion.firmwareVersionHardwareRev);

		if (env->ExceptionCheck() == JNI_TRUE) {
			cerr << "Exception occurred" << endl;
			env->ExceptionDescribe();
			return nullptr;
		}
		return fv;
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
#include "com_thalmic_myo_Myo.h"
#include <myo/myo.hpp>

using namespace myo;

Myo* getPointer(JNIEnv *env, jobject obj) {
	jfieldID fid = env->GetFieldID(env->GetObjectClass(obj), "_nativePointer", "J");
	return reinterpret_cast<Myo*>(env->GetLongField(obj, fid));
}

JNIEXPORT void JNICALL Java_com_thalmic_myo_Myo__1vibrate(JNIEnv *env, jobject obj, jint type) {
	if (type == com_thalmic_myo_Myo_VIB_SHORT) {
		getPointer(env, obj)->vibrate(Myo::vibrationShort);
	}
	else if (type == com_thalmic_myo_Myo_VIB_MEDIUM) {
		getPointer(env, obj)->vibrate(Myo::vibrationMedium);
	}
	else {
		getPointer(env, obj)->vibrate(Myo::vibrationLong);
	}
}

JNIEXPORT void JNICALL Java_com_thalmic_myo_Myo__1requestRssi(JNIEnv *env, jobject obj) {
	getPointer(env, obj)->requestRssi();
}

JNIEXPORT void JNICALL Java_com_thalmic_myo_Myo__1requestBattLevel(JNIEnv *env, jobject obj) {
	getPointer(env, obj)->requestBatteryLevel();
}

JNIEXPORT void JNICALL Java_com_thalmic_myo_Myo__1unlock(JNIEnv *env, jobject obj, jint type) {
	if (type == com_thalmic_myo_Myo_UT_HOLD) {
		getPointer(env, obj)->unlock(Myo::unlockHold);
	}
	else {
		getPointer(env, obj)->unlock(Myo::unlockTimed);
	}
}

JNIEXPORT void JNICALL Java_com_thalmic_myo_Myo__1notifyAction(JNIEnv *env, jobject obj) {
	getPointer(env, obj)->notifyUserAction();
}

JNIEXPORT void JNICALL Java_com_thalmic_myo_Myo__1setStreamEmg(JNIEnv *env, jobject obj, jint type) {
	if (type == com_thalmic_myo_Myo_SET_DISABLED) {
		getPointer(env, obj)->setStreamEmg(Myo::streamEmgDisabled);
	}
	else {
		getPointer(env, obj)->setStreamEmg(Myo::streamEmgEnabled);
	}
}
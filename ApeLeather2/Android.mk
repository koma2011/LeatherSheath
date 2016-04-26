LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_SRC_FILES += \
    src/com/ape/music/IMediaPlaybackService.aidl \
    src/com/android/music/IMediaPlaybackService.aidl

LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/, res) \
    frameworks/support/v7/appcompat/res

LOCAL_ASSET_DIR := $(LOCAL_PATH)/assets

LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages android.support.v7.appcompat

LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-v7-appcompat \
    android-support-v4 \
    fastjson-1.2.6

LOCAL_JAVA_LIBRARIES := telephony-common

LOCAL_PACKAGE_NAME := Leather2

LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_PROGUARD_ENABLED := disabled

LOCAL_PRIVILEGED_MODULE := true
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    fastjson-1.2.6:libs/fastjson-1.2.6.jar

include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under, $(LOCAL_PATH))

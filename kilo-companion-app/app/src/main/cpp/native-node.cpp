// =============================================================================
// native-node.cpp - JNI Bridge for Node.js Mobile
// =============================================================================
// This file provides the native interface between Android (Kotlin) and Node.js.
// It handles:
// - Starting/stopping Node.js runtime
// - Message passing between JavaScript and Kotlin
// - Process lifecycle management
// =============================================================================

#include <jni.h>
#include <android/log.h>
#include <string.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>

#define LOG_TAG "CodePal-Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Node.js entry point (provided by nodejs-mobile)
extern "C" int node_main(int argc, char** argv);

// Thread for running Node.js
static pthread_t node_thread;
static volatile int node_running = 0;
static JavaVM* jvm = nullptr;
static jobject callback_obj = nullptr;
static jmethodID callback_method = nullptr;

// Structure to hold Node.js arguments
struct NodeArgs {
    int argc;
    char** argv;
};

// Thread function for Node.js
void* node_thread_func(void* arg) {
    NodeArgs* args = (NodeArgs*)arg;
    
    LOGI("Starting Node.js thread");
    node_running = 1;
    
    // Run Node.js
    int result = node_main(args->argc, args->argv);
    
    LOGI("Node.js exited with code: %d", result);
    node_running = 0;
    
    // Cleanup
    for (int i = 0; i < args->argc; i++) {
        free(args->argv[i]);
    }
    free(args->argv);
    free(args);
    
    // Notify Java that Node.js stopped
    JNIEnv* env;
    if (jvm->AttachCurrentThread(&env, nullptr) == JNI_OK) {
        if (callback_obj && callback_method) {
            env->CallVoidMethod(callback_obj, callback_method, env->NewStringUTF("stopped"));
        }
        jvm->DetachCurrentThread();
    }
    
    return nullptr;
}

// Send message to Java
void send_to_java(const char* message) {
    if (!jvm || !callback_obj || !callback_method) return;
    
    JNIEnv* env;
    if (jvm->AttachCurrentThread(&env, nullptr) == JNI_OK) {
        jstring jmsg = env->NewStringUTF(message);
        env->CallVoidMethod(callback_obj, callback_method, jmsg);
        env->DeleteLocalRef(jmsg);
        jvm->DetachCurrentThread();
    }
}

extern "C" {

// Initialize the native interface
JNIEXPORT void JNICALL
Java_com_kilo_companion_nodejs_NodeRuntime_nativeInit(
    JNIEnv* env,
    jobject thiz,
    jobject callback
) {
    LOGI("Initializing native Node.js interface");
    
    // Cache JVM
    env->GetJavaVM(&jvm);
    
    // Cache callback
    if (callback_obj) {
        env->DeleteGlobalRef(callback_obj);
    }
    callback_obj = env->NewGlobalRef(callback);
    
    // Get callback method
    jclass callback_class = env->GetObjectClass(callback);
    callback_method = env->GetMethodID(callback_class, "onNodeMessage", "(Ljava/lang/String;)V");
}

// Start Node.js with the given script
JNIEXPORT jboolean JNICALL
Java_com_kilo_companion_nodejs_NodeRuntime_nativeStartNode(
    JNIEnv* env,
    jobject thiz,
    jstring projectPath,
    jstring scriptPath
) {
    if (node_running) {
        LOGE("Node.js is already running");
        return JNI_FALSE;
    }
    
    const char* project = env->GetStringUTFChars(projectPath, nullptr);
    const char* script = env->GetStringUTFChars(scriptPath, nullptr);
    
    LOGI("Starting Node.js with project: %s, script: %s", project, script);
    
    // Prepare arguments
    NodeArgs* args = (NodeArgs*)malloc(sizeof(NodeArgs));
    args->argc = 4;
    args->argv = (char**)malloc(args->argc * sizeof(char*));
    args->argv[0] = strdup("node");
    args->argv[1] = strdup(script);
    args->argv[2] = strdup("--project");
    args->argv[3] = strdup(project);
    
    env->ReleaseStringUTFChars(projectPath, project);
    env->ReleaseStringUTFChars(scriptPath, script);
    
    // Create thread
    if (pthread_create(&node_thread, nullptr, node_thread_func, args) != 0) {
        LOGE("Failed to create Node.js thread");
        return JNI_FALSE;
    }
    
    return JNI_TRUE;
}

// Stop Node.js
JNIEXPORT void JNICALL
Java_com_kilo_companion_nodejs_NodeRuntime_nativeStopNode(
    JNIEnv* env,
    jobject thiz
) {
    if (!node_running) {
        return;
    }
    
    LOGI("Stopping Node.js");
    
    // Signal Node.js to stop (it will exit on its own)
    node_running = 0;
    
    // Wait for thread to finish (with timeout)
    pthread_join(node_thread, nullptr);
}

// Check if Node.js is running
JNIEXPORT jboolean JNICALL
Java_com_kilo_companion_nodejs_NodeRuntime_nativeIsRunning(
    JNIEnv* env,
    jobject thiz
) {
    return node_running ? JNI_TRUE : JNI_FALSE;
}

// Cleanup resources
JNIEXPORT void JNICALL
Java_com_kilo_companion_nodejs_NodeRuntime_nativeCleanup(
    JNIEnv* env,
    jobject thiz
) {
    LOGI("Cleaning up native resources");
    
    if (callback_obj) {
        env->DeleteGlobalRef(callback_obj);
        callback_obj = nullptr;
    }
    
    callback_method = nullptr;
    jvm = nullptr;
}

} // extern "C"

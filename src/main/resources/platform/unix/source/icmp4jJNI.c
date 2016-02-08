#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "icmp4j.h"
#include "icmp4jJNI.h"


static void makeIcmp4jStr(struct Icmp4jStruct *str, JNIEnv *env, jobject Obj) {
   jclass thisClass = (*env)->GetObjectClass(env, Obj);

   jfieldID fidHost = (*env)->GetFieldID(env, thisClass, "host", "Ljava/lang/String;");
   jstring host = (*env)->GetObjectField(env, Obj, fidHost);
   const char *nativeString = (*env)->GetStringUTFChars(env, host, 0);
   //printf("[%s] %d %lu\n", nativeString,  (*env)->GetStringLength(env, host), sizeof(nativeString));
   //fflush(stdout);
   str->host = (char*) malloc(sizeof(char) * (1 + (*env)->GetStringLength(env, host)));
   memset(str->host, 0,  1+ (*env)->GetStringLength(env, host));
   memcpy(str->host, nativeString,  (*env)->GetStringLength(env, host));
   (*env)->ReleaseStringUTFChars(env, host, nativeString);

   jfieldID jField = (*env)->GetFieldID(env, thisClass, "ttl", "I");
   jint number = (*env)->GetIntField(env, Obj, jField);
   str->ttl = (int)number;

   jField = (*env)->GetFieldID(env, thisClass, "packetSize", "I");
   number = (*env)->GetIntField(env, Obj, jField);
   str->packetSize = (int)number;

   jField = (*env)->GetFieldID(env, thisClass, "timeOut", "J");
   number = (*env)->GetIntField(env, Obj, jField);
   str->timeout = (int)number;
}

JNIEXPORT jstring JNICALL Java_org_icmp4j_platform_unix_jni_Icmp4jJNI_icmp_1test (JNIEnv *env, jobject thisObj) {

    jstring retString;
    char    *version;
    icmp4j_exist(&version);
    retString = (*env)->NewStringUTF(env, version);
    icmp4j_exist_free(version);

    return retString;
}

JNIEXPORT void JNICALL Java_org_icmp4j_platform_unix_jni_Icmp4jJNI_icmp_1start
(JNIEnv *env, jobject thisObj) {
    struct Icmp4jStruct str;    
    struct Icmp4jStruct *ptr;    
    ptr = (struct Icmp4jStruct *) malloc(sizeof(struct Icmp4jStruct ));

    // make an Icmp4jStruct from the obj
    makeIcmp4jStr(ptr, env, thisObj);

    // pass it to the pinger
   icmp4j_start(ptr);

   jclass thisClass = (*env)->GetObjectClass(env, thisObj);

   jfieldID jField = (*env)->GetFieldID(env, thisClass, "retCode", "I");
   (*env)->SetIntField(env, thisObj, jField, (jint)ptr->retCode);

   jField = (*env)->GetFieldID(env, thisClass, "hasTimeout", "I");
   (*env)->SetIntField(env, thisObj, jField, (jint)ptr->hasTimeout);

   jField = (*env)->GetFieldID(env, thisClass, "bytes", "I");
   (*env)->SetIntField(env, thisObj, jField, (jint)ptr->bytes);

   jField = (*env)->GetFieldID(env, thisClass, "returnTtl", "I");
   (*env)->SetIntField(env, thisObj, jField, (jint)ptr->returnTtl);

   jField = (*env)->GetFieldID(env, thisClass, "rtt", "I");
   (*env)->SetIntField(env, thisObj, jField, (jint)ptr->rtt);

   jstring tmpStr;
   jstring tmpStr2;
   if (ptr->address != NULL) {
       jField = (*env)->GetFieldID(env, thisClass, "address", "Ljava/lang/String;");
       tmpStr = (*env)->GetObjectField(env, thisObj, jField);
       tmpStr = (*env)->NewStringUTF(env, ptr->address);
       (*env)->SetObjectField(env, thisObj, jField, tmpStr);
   }
   if (ptr->errorMsg != NULL) {
       jField = (*env)->GetFieldID(env, thisClass, "errorMsg", "Ljava/lang/String;");
       tmpStr2 = (*env)->GetObjectField(env, thisObj, jField);
       tmpStr2 = (*env)->NewStringUTF(env, ptr->errorMsg);
       (*env)->SetObjectField(env, thisObj, jField, tmpStr2);
   }

   jField = (*env)->GetFieldID(env, thisClass, "errno", "I");
   (*env)->SetIntField(env, thisObj, jField, (jint)ptr->errorNo);

   icmp4j_free(ptr);
   free(ptr);
   return;
}

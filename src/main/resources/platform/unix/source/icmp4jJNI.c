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
   str->host = (char*) malloc(sizeof(char) * (1+sizeof(nativeString)));
   strcpy(str->host, nativeString);

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

    // make an Icmp4jStruct from the obj
    struct Icmp4jStruct icmp4jStr;
    makeIcmp4jStr(&icmp4jStr, env, thisObj);

    // pass it to the pinger
   icmp4j_start(&icmp4jStr);

   jclass thisClass = (*env)->GetObjectClass(env, thisObj);

   jfieldID jField = (*env)->GetFieldID(env, thisClass, "retCode", "I");
   (*env)->SetIntField(env, thisObj, jField, (jint)icmp4jStr.retCode);

   jField = (*env)->GetFieldID(env, thisClass, "hasTimeout", "I");
   (*env)->SetIntField(env, thisObj, jField, (jint)icmp4jStr.hasTimeout);

   jField = (*env)->GetFieldID(env, thisClass, "bytes", "I");
   (*env)->SetIntField(env, thisObj, jField, (jint)icmp4jStr.bytes);

   jField = (*env)->GetFieldID(env, thisClass, "returnTtl", "I");
   (*env)->SetIntField(env, thisObj, jField, (jint)icmp4jStr.returnTtl);

   jField = (*env)->GetFieldID(env, thisClass, "rtt", "I");
   (*env)->SetIntField(env, thisObj, jField, (jint)icmp4jStr.rtt);

   jstring tmpStr;
   if (icmp4jStr.address != NULL) {
       jField = (*env)->GetFieldID(env, thisClass, "address", "Ljava/lang/String;");
       tmpStr = (*env)->GetObjectField(env, thisObj, jField);
       tmpStr = (*env)->NewStringUTF(env, icmp4jStr.address);
       (*env)->SetObjectField(env, thisObj, jField, tmpStr);
   }
   if (icmp4jStr.errorMsg != NULL) {
       jField = (*env)->GetFieldID(env, thisClass, "errorMsg", "Ljava/lang/String;");
       tmpStr = (*env)->GetObjectField(env, thisObj, jField);
       tmpStr = (*env)->NewStringUTF(env, icmp4jStr.errorMsg);
       (*env)->SetObjectField(env, thisObj, jField, tmpStr);
   }

   jField = (*env)->GetFieldID(env, thisClass, "errno", "I");
   (*env)->SetIntField(env, thisObj, jField, (jint)icmp4jStr.errorNo);


   // clear the structure
   icmp4j_free(&icmp4jStr);
}

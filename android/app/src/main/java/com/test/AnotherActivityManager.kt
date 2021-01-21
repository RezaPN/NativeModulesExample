package com.test

import android.content.Intent
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class AnotherActivityManager(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    override fun getName(): String {
        return "AnotherActivityManager"
    }

    //inten memulai activity baru, aksinya di sini
    @ReactMethod
    fun switchToAnotherActivity(){
        val intent = Intent(reactApplicationContext, AnotherActivity::class.java)
                //version?
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        reactApplicationContext.startActivity(intent);
    }
}
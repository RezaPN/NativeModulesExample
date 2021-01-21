package com.test;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class ToastModule extends ReactContextBaseJavaModule {

    //command + n
    public ToastModule(@Nullable ReactApplicationContext reactContext) {
        super(reactContext);
    }

    //nama module yang akan dipanggil di react
    @NonNull
    @Override
    public String getName() {
        return "ToastModule";
    }

    //CustomFunction
    @ReactMethod
    public void showToast(String message){
        Toast.makeText(getReactApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}

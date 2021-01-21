package com.test;

import androidx.annotation.NonNull;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

public class CustomButtonManager extends SimpleViewManager <CustomButtonExample> {

    @NonNull
    @Override
    public String getName() {
        return "CustomButtonExample";
    }

    @NonNull
    @Override
    protected CustomButtonExample createViewInstance(@NonNull ThemedReactContext reactContext) {
        return new CustomButtonExample(reactContext);
    }
}

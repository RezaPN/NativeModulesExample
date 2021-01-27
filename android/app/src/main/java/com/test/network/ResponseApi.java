package com.test.network;

import com.androidnetworking.error.ANError;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

import okhttp3.Response;


public class ResponseApi {

    public final Status status;

    public final Response data;

    public final String responseString;

    public final ANError error;

    public final String type;

    private ResponseApi(Status status, @Nullable Response data, @Nullable ANError error, @Nullable String responseString, @Nullable String type) {
        this.status = status;
        this.data = data;
        this.error = error;
        this.responseString = responseString;
        this.type = type;
    }

    public static ResponseApi loading() {
        return new ResponseApi(Status.LOADING, null, null, null, null);
    }

    public static ResponseApi success(@NonNull Response data, @NonNull String responseString, @Nullable String type) {
        return new ResponseApi(Status.SUCCESS, data, null, responseString, type);
    }

    public static ResponseApi error(@NonNull ANError error) {
        return new ResponseApi(Status.ERROR, null, error, null, null);
    }

}

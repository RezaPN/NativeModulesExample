package com.test.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.OkHttpResponseAndStringRequestListener
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.megvii.meglive_sdk.listener.PreCallback
import com.megvii.meglive_sdk.manager.MegLiveManager
import com.test.AnotherActivity
import com.test.network.BaseEndpoint
import com.test.network.ResponseApi
import com.test.network.Status
import com.test.utils.SignUtils
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class LivenessViewModel : ViewModel(), BaseEndpoint {

    private var context: Context? = null
    private val megLiveManager = MegLiveManager.getInstance()
    private var callBackLivenessViewModel: CallBackLivenessViewModel? = null

    private var okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.MINUTES)
            .writeTimeout(30, TimeUnit.MINUTES)
            .build()

    fun init(context: Context, callBackLivenessViewModel: AnotherActivity) {
        this.context = context
        this.callBackLivenessViewModel = callBackLivenessViewModel
    }

    fun getLivenessBizToken(photo: File): MutableLiveData<ResponseApi> {
        val bizTokenResponse: MutableLiveData<ResponseApi> = MutableLiveData()
        bizTokenResponse.postValue(ResponseApi.loading())
        val url = BaseEndpoint.baseUrlFaceID.plus(BaseEndpoint.livenessBizToken)
        AndroidNetworking.upload(url)
                .setOkHttpClient(okHttpClient)
                .addMultipartParameter("sign", SignUtils().getSign())
                .addMultipartParameter("sign_version", SignUtils().getVersion())
                .addMultipartParameter("uuid", UUID.randomUUID().toString())
                .addMultipartParameter("liveness_type", "meglive")
                .addMultipartParameter("comparison_type", "0")
                .addMultipartFile("image_ref1", photo)
                .build()
                .getAsOkHttpResponseAndString(object : OkHttpResponseAndStringRequestListener {
                    override fun onResponse(okHttpResponse: Response, response: String) {
                        bizTokenResponse.postValue(ResponseApi.success(okHttpResponse, response, null))
                    }

                    override fun onError(anError: ANError?) {
                        bizTokenResponse.postValue(ResponseApi.error(anError!!))
                    }
                })

        return bizTokenResponse
    }

    fun processGenerateBizToken(responseApi: ResponseApi) {
        when (responseApi.status) {
            Status.LOADING -> {
                callBackLivenessViewModel?.loading()
            }
            Status.SUCCESS -> {
                callBackLivenessViewModel?.success(responseApi)
            }
            Status.ERROR -> {
                callBackLivenessViewModel?.error(responseApi)
            }
        }
    }

    fun preDetectLiveness(bizToken: String) {
        megLiveManager.preDetect(context, bizToken, "en", BaseEndpoint.baseUrlFaceID,
                object: PreCallback {
                    override fun onPreFinish(token: String?, errorCode: Int, errorMessage: String?) {
                        var stringCuwk = errorCode.toString()
                        Log.i("BAFBRIDGE", stringCuwk);
                        Log.i("BAFBRIDGE", token);
                        Log.i("BAFBRIDGE", errorMessage);
                        if (errorCode == 1000) {
                            megLiveManager.setVerticalDetectionType(MegLiveManager.DETECT_VERITICAL_FRONT)
                            megLiveManager.startDetect { detectToken, detectErrorCode, detectErrorMessage, data ->
                                if (data != null) {
                                    callBackLivenessViewModel?.onResult(detectToken, detectErrorCode, detectErrorMessage, data)
                                }
                            }
                        }
                    }

                    override fun onPreStart() {

                    }
                })
    }

    fun verifyLivenessData(token: String, data: String, handler: AsyncHttpResponseHandler) {
        val verifyLivenessURL =  BaseEndpoint.baseUrlFaceID.plus(BaseEndpoint.livenessVerify)
        val params = RequestParams()
        params.put("sign", SignUtils().getSign())
        params.put("sign_version", SignUtils().getVersion())
        params.put("biz_token", token)
        params.put("meglive_data", ByteArrayInputStream(data.toByteArray()))
        val asyncHttpClient = AsyncHttpClient()
        asyncHttpClient.connectTimeout = 10000
        asyncHttpClient.responseTimeout = 10000
        asyncHttpClient.setTimeout(10000)
        asyncHttpClient.post(verifyLivenessURL, params, handler)
    }

    interface CallBackLivenessViewModel {
        fun loading()
        fun success(data: ResponseApi)
        fun error(data: ResponseApi)
        fun onResult(detectToken: String, detectErrorCode: Int, detectErrorMessage: String, data: String)
    }

}

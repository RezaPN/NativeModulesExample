package com.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.test.network.ResponseApi
import com.test.utils.PermissionUtils
import com.test.viewmodel.LivenessViewModel

import kotlinx.android.synthetic.main.another_screen_layouts.*
import kotlinx.android.synthetic.main.dialog_bottom_sheet.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class AnotherActivity : AppCompatActivity(), LivenessViewModel.CallBackLivenessViewModel {

    private val messageDialog: BottomSheetDialog by lazy { BottomSheetDialog(this, R.style.BottomSheetDialogTheme) }
    private val REQUEST_CODE_PERMISSION = 100
    private lateinit var livenessViewModel: LivenessViewModel
    private var idCardFace: String? = ""
    private var pathSelfie: String? = "null"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.another_screen_layouts)

        initLoadData()
        initLoadView()
    }

    private fun initLoadData() {
        livenessViewModel = ViewModelProviders.of(this).get(LivenessViewModel::class.java)
        livenessViewModel.init(this, this)

        getPhotoLiveness()
    }

    private fun initLoadView() {
        button_start.setOnClickListener {
            if (PermissionUtils.requestPermission(this, REQUEST_CODE_PERMISSION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                openLiveness()
            }
        }
    }

    private fun openLiveness() {
        if (PermissionUtils.with(this).isCameraReadyGranted) {
            livenessViewModel.getLivenessBizToken(File(idCardFace)).observe(this, Observer {
                livenessViewModel.processGenerateBizToken(it!!)
            })
        }
    }

    private fun getPhotoLiveness() {
        val imageBitmap = BitmapFactory.decodeResource(resources, R.drawable.photo_liveness)
        val imageOpts: BitmapFactory.Options = BitmapFactory.Options()
        imageOpts.inSampleSize = 2
        val bitmap = Bitmap.createScaledBitmap(imageBitmap,
                (imageBitmap.width / 1.5).toInt(), (imageBitmap.height / 1.5).toInt(),
                false)
        idCardFace = saveFilePhotoLiveness(bitmap).absolutePath
    }

    private fun saveFilePhotoLiveness(imageBitmap: Bitmap): File {
        val contextWrapper = ContextWrapper(getApplicationContext())
        val dir = contextWrapper.getDir("liveness", Context.MODE_PRIVATE)
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "photo.jpg")
        val fileOutput = FileOutputStream(file)

        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutput)
        fileOutput.flush()
        fileOutput.close()
        return file
    }

    private fun loadingProgress(isLoading: Boolean) {
        runOnUiThread {
            if (isLoading) {
                progress.visibility = View.VISIBLE
                button_start.visibility = View.INVISIBLE
            } else {
                progress.visibility = View.INVISIBLE
                button_start.visibility = View.VISIBLE
            }
        }
    }

    private fun showMessageDialog(message: String) {
        runOnUiThread {
            initMessageDialog(message)
            messageDialog.show()
        }
    }

    private fun initMessageDialog(message: String) {
        messageDialog.apply {
            setContentView(R.layout.dialog_bottom_sheet)
            textview_title_dialog.text = getString(R.string.oops_something_happen)
            if (message == "LIVENESS_FAILURE") {
                textview_message_dialog.text = getString(R.string.liveness_failed)
            } else if (message == "connectionError") {
                textview_message_dialog.text = getString(R.string.no_connection_description)
            } else if (message == "responseFromServerError") {
                textview_message_dialog.text = "Internal Server Error"
            } else {
                textview_message_dialog.text = message
            }
            button_ok.text = getString(R.string.okay)
            button_ok.isAllCaps = false

            button_ok.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtils.permissionGranted(requestCode, REQUEST_CODE_PERMISSION, grantResults)) {
            openLiveness()
        }
    }


    override fun loading() {
        loadingProgress(true)
    }

    override fun success(data: ResponseApi) {
        loadingProgress(false)
        Log.i("BAFBRIDGE", data.responseString);

        val bizToken = JSONObject(data.responseString).optString("biz_token")

        if (bizToken != null) {
            livenessViewModel.preDetectLiveness(bizToken)
        }
    }

    override fun error(data: ResponseApi) {
        loadingProgress(false)
        showMessageDialog(data.error?.errorDetail.toString())
    }

    override fun onResult(detectToken: String, detectErrorCode: Int, detectErrorMessage: String, data: String) {
        TODO("Not yet implemented")
    }


}
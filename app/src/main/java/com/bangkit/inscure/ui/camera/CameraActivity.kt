package com.bangkit.inscure.ui.camera

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bangkit.inscure.R
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.bangkit.inscure.databinding.ActivityCameraBinding
import com.bangkit.inscure.ui.upload.UploadActivity
import com.bangkit.inscure.utils.Helper

class CameraActivity : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var openGalleryLauncher: ActivityResultLauncher<Intent>

    private lateinit var binding: ActivityCameraBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupWindow()

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initGallery()
        binding.let {
            it.btnShutter.setOnClickListener {
                takePhoto()
            }
            it.btnSwitch.setOnClickListener {
                cameraSelector =
                    if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                    else CameraSelector.DEFAULT_BACK_CAMERA
                startCamera()
            }
            it.btnGallery.setOnClickListener {
                startGallery()
            }
            it.btnBack.setOnClickListener {
                @Suppress("DEPRECATION")
                onBackPressed()
            }
        }
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            @Suppress("DEPRECATION") val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(480, 720))
                .build()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            @Suppress("DEPRECATION")
            imageCapture = ImageCapture.Builder().setTargetResolution(Size(480, 720)).build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture, imageAnalysis
                )
            } catch (e: Exception) {
                Helper.showDialogInfo(this, "Failed to launch camera : ${e.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun initGallery() {
        openGalleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.i("GALLERY TEST", "Succes")
                val selectedImg: Uri = result.data?.data as Uri
                val myFile = Helper.uriToFile(selectedImg, this@CameraActivity)
                val intent = Intent(this@CameraActivity, UploadActivity::class.java)
                intent.putExtra(UploadActivity.EXTRA_PHOTO_RESULT, myFile)
                intent.putExtra(
                    UploadActivity.EXTRA_CAMERA_MODE,
                    cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                )
                this@CameraActivity.finish()
                startActivity(intent)
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setupWindow() {
        if (Build.VERSION.SDK_INT >= 21) {
            @Suppress("DEPRECATION")
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= 30) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        openGalleryLauncher.launch(chooser)
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = Helper.createFile(application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Helper.showDialogInfo(
                        this@CameraActivity,
                        "${getString(R.string.error_take_photo)} : ${exc.message}"
                    )
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {

                    val intent = Intent(this@CameraActivity, UploadActivity::class.java)
                    intent.putExtra(UploadActivity.EXTRA_PHOTO_RESULT, photoFile)
                    intent.putExtra(
                        UploadActivity.EXTRA_CAMERA_MODE,
                        cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                    )
                    this@CameraActivity.finish()
                    startActivity(intent)
                }
            }
        )
    }
}
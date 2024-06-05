package com.bangkit.inscure.ui.upload

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.inscure.R
import com.bangkit.inscure.databinding.ActivityUploadBinding
import com.bangkit.inscure.utils.Helper
import java.io.File

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myFile = intent?.getSerializableExtra(EXTRA_PHOTO_RESULT) as File
        //val isBackCamera = intent?.getBooleanExtra(EXTRA_CAMERA_MODE, true) as Boolean
        val bitmap = BitmapFactory.decodeFile(myFile.path)
        binding.storyImage.setImageBitmap(bitmap)

        binding.btnAdd.setOnClickListener {
            if (binding.edAddDescription.text.isNotEmpty()) {
            } else {
                Helper.showDialogInfo(
                    this,
                    getString(R.string.empty_story_description)
                )
            }
        }
    }

    companion object {
        const val EXTRA_PHOTO_RESULT = "PHOTO_RESULT"
        const val EXTRA_CAMERA_MODE = "CAMERA_MODE"
    }
}
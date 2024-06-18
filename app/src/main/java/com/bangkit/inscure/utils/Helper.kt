package com.bangkit.inscure.utils

import android.annotation.SuppressLint
import android.app.Application
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.bangkit.inscure.R
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Helper {

     fun extractImageId(url: String): String {
        return url.substringAfterLast("/")
    }

    fun formatDate(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            val date: Date = inputFormat.parse(inputDate) ?: return inputDate

            val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            inputDate
        }
    }

    /** UI CONTROLLER **/
    // Custom dialog info builder
    fun dialogInfoBuilder(
        context: Context,
        message: String,
        alignment: Int = Gravity.CENTER
    ): Dialog {
        val dialog = Dialog(context)
        dialog.setCancelable(false)
        dialog.window!!.apply {
            val params: WindowManager.LayoutParams = this.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            attributes.windowAnimations = android.R.transition.fade
            setGravity(Gravity.CENTER)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog.setContentView(R.layout.custom_dialog_info)
        val tvMessage = dialog.findViewById<TextView>(R.id.message)
        when (alignment) {
            Gravity.CENTER -> tvMessage.gravity = Gravity.CENTER_VERTICAL or Gravity.CENTER
            Gravity.START -> tvMessage.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            Gravity.END -> tvMessage.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        }
        tvMessage.text = message
        return dialog
    }

    fun showDialogInfo(
        context: Context,
        message: String,
        alignment: Int = Gravity.CENTER
    ) {
        val dialog = dialogInfoBuilder(context, message, alignment)
        val btnOk = dialog.findViewById<Button>(R.id.button_ok)
        btnOk.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    /** CAMERA INSTANCE HELPER **/
    private fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(currentTimestamp, ".jpg", storageDir)
    }

    @SuppressLint("ConstantLocale")
    val currentTimestamp: String = SimpleDateFormat(
        "ddMMyySSSSS",
        Locale.getDefault()
    ).format(System.currentTimeMillis())

    fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)
        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()
        return myFile
    }

    fun createFile(application: Application): File {
        val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
            File(it, "story").apply { mkdirs() }
        }
        val outputDirectory = if (
            mediaDir != null && mediaDir.exists()
        ) mediaDir else application.filesDir

        return File(outputDirectory, "STORY-$currentTimestamp.jpg")
    }
}
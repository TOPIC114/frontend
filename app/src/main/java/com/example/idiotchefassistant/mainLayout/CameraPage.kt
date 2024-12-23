package com.example.idiotchefassistant.mainLayout

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.Toast
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import android.util.Log
import android.util.Size
import com.example.idiotchefassistant.result.ResultPage
import com.example.idiotchefassistant.databinding.ActivityCameraPageBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import android.view.Surface
import android.view.ViewTreeObserver
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import java.io.FileOutputStream

class CameraPage : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCameraPageBinding
    private var photoFilePaths = ArrayList<String>()
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraPageBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            initializeCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        // Set up the listeners for take photo and video capture buttons
        viewBinding.captureButton.setOnClickListener { capturePhotos() }
        viewBinding.uploadButton.setOnClickListener { uploadPhotos() }
        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    private fun initializeCamera(){
        viewBinding.viewFinder.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                viewBinding.viewFinder.viewTreeObserver.removeOnGlobalLayoutListener(this)
                startCamera(viewBinding.viewFinder.display?.rotation ?: Surface.ROTATION_0)
            }
        })
    }

    private fun rotateImage(file:File): File{
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val matrix = Matrix()
        matrix.postRotate(90f)  // 旋转90度
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        val rotatedFile = File(file.parent, "rotated_${file.name}")
        FileOutputStream(rotatedFile).use { out ->
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        return rotatedFile
    }

    private fun capturePhotos(){
        val imageCapture = imageCapture ?:return
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val photoFile = File(cacheDir, "${name}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object: ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e("take photo", "Photo capture failed: ${exc.message}", exc)
            }
            override fun onImageSaved(output: ImageCapture.OutputFileResults){
                val savedUri = photoFile.toURI().toString()
                val rotateFile = rotateImage(photoFile)
                photoFilePaths.add(rotateFile.absolutePath)
                Log.d("take photo", "Photo capture succeeded: $savedUri")
                Toast.makeText(baseContext, "Photo capture succeeded", Toast.LENGTH_SHORT).show()
                }
            })
        }

    private fun uploadPhotos() {
        if(photoFilePaths.isEmpty()){
            Toast.makeText(this, "No photos to upload", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, ResultPage::class.java).apply {
            putStringArrayListExtra("photoFilePaths", photoFilePaths)
        }
        startActivity(intent)
    }

    private fun startCamera(displayRotation: Int) {
        // 綁定相機的生命週期到生命週期所有者，包含了開啟和關閉相機的任務
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            // 以下是執行功能
            // 綁定相機到進程中的lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Preview
            val targetResolution = Size(1280, 720)
            val preview = Preview.Builder()
                .setTargetRotation(displayRotation)
                .setTargetResolution(targetResolution)
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .setTargetResolution(targetResolution)
                .setTargetRotation(displayRotation)
                .build()
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                initializeCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
package com.example.android_hit.ui.twibbon

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.android_hit.databinding.FragmentTwibbonBinding
import com.example.android_hit.room.TransactionDB
import com.example.android_hit.room.TransactionEntity
import com.google.common.util.concurrent.ListenableFuture
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class FragmentTwibbon : Fragment() {
    private var _binding: FragmentTwibbonBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var imageCapture: ImageCapture

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTwibbonBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCamera()

        binding.capture.setOnClickListener {
            captureImage()
        }

        binding.retakeButton.setOnClickListener {
            retakeImage()
        }
    }

    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            val preview: Preview = Preview.Builder().build()

            val cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            preview.setSurfaceProvider(binding.previewView.surfaceProvider)

            imageCapture = ImageCapture.Builder()
                .setTargetRotation(requireView().display.rotation)
                .build()

            cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun captureImage() {
        imageCapture.takePicture(ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)
                var bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                val orientation = resources.configuration.orientation
                if(orientation == Configuration.ORIENTATION_PORTRAIT){
                    bitmap = rotateBitmap90(bitmap)
                }else{
                    bitmap = rotateBitmap180(bitmap)
                }
                displayCapturedImage(bitmap)
                image.close()
                cameraProvider.unbindAll()
            }

            override fun onError(exception: ImageCaptureException) {
                // Handle capture error
                Toast.makeText(requireContext(), "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun retakeImage() {
        // Clear the captured image and hide the retake button
        binding.imageView.setImageBitmap(null)
        binding.retakeButton.visibility = View.GONE
        // Show capture button
        binding.capture.visibility = View.VISIBLE
        binding.previewView.visibility = View.VISIBLE
        binding.imageView.visibility = View.GONE
        setupCamera()
    }

    private fun displayCapturedImage(bitmap: Bitmap?) {
        binding.imageView.setImageBitmap(bitmap)
        binding.imageView.scaleType = ImageView.ScaleType.CENTER_CROP

        // Show/hide appropriate views
        binding.retakeButton.visibility = View.VISIBLE
        binding.previewView.visibility = View.GONE
        binding.capture.visibility = View.GONE
        binding.imageView.visibility = View.VISIBLE
    }

    fun rotateBitmap90(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(90f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    fun rotateBitmap180(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(180f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraProvider.unbindAll()
    }

}
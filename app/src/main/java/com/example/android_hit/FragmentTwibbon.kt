package com.example.android_hit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
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

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTwibbonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this.requireContext())
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            var preview : Preview = Preview.Builder()
                .build()

            var cameraSelector : CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            preview.setSurfaceProvider(binding.previewView.surfaceProvider)

            val imageCapture = ImageCapture.Builder()
                .setTargetRotation(view.display.rotation)
                .build()
            cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, imageCapture,
                preview)



            binding.capture.setOnClickListener {
                imageCapture.takePicture(ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        //    Convert ImageProxy to Bitmap
                        val buffer = image.planes[0].buffer
                        val bytes = ByteArray(buffer.remaining())
                        buffer.get(bytes)
                        val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                        binding.imageView.setImageBitmap(bitmap)
                        binding.imageView.scaleType = ImageView.ScaleType.FIT_XY
                        binding.imageView.layoutParams.width = bitmap.width
                        binding.imageView.layoutParams.height = bitmap.height

                        binding.retakeButton.visibility = View.VISIBLE
                        binding.previewView.visibility = View.GONE
                        binding.capture.visibility = View.GONE
                        binding.imageView.visibility = View.VISIBLE

                        Log.e("PICT","capture")
                        image.close()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        // Handle capture error
                        Toast.makeText(requireContext(), "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        binding.retakeButton.setOnClickListener {
            // Clear the captured image and hide the retake button
            binding.imageView.setImageBitmap(null)
            binding.retakeButton.visibility = View.GONE
            // Show capture button
            binding.capture.visibility = View.VISIBLE
            binding.previewView.visibility = View.VISIBLE

            binding.imageView.visibility = View.GONE
        }

        }, ContextCompat.getMainExecutor(this.requireContext()))







    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
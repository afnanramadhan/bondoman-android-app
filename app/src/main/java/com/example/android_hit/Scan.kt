package com.example.android_hit

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.android_hit.api.RetrofitClient
import com.example.android_hit.data.ScanResponse
import com.example.android_hit.room.TransactionDB
import com.example.android_hit.room.TransactionEntity
import com.example.android_hit.utils.TokenManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class Scan : Fragment() {
    var pickedPhoto : Uri? = null
    var pickedBitMap : Bitmap? = null
    private val CAMERA_REQUEST_CODE = 1
    private lateinit var btnCapture : Button
    private lateinit var btnPick : Button
    private lateinit var ivPicture : ImageView
    private lateinit var sharedPref : TokenManager





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnCapture = view.findViewById(R.id.captureImgBtn)
        ivPicture = view.findViewById(R.id.captureImageView)
        btnPick = view.findViewById(R.id.pickImgBtn)
        btnCapture.isEnabled = true
        sharedPref = TokenManager(requireContext())
        if(ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA),
                100)
        } else{
            btnCapture.isEnabled = true
        }
        btnPick.setOnClickListener {
            pickedPhoto()
        }


        btnCapture.setOnClickListener{
            val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(i, 101)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 101){
            var pic : Bitmap? = data?.getParcelableExtra<Bitmap>("data")
            ivPicture.setImageBitmap(pic)
            pickedBitMap = pic
            val savedImageUri = saveImageToInternalStorage(pickedBitMap!!)
            pickedPhoto = savedImageUri
            Handler(Looper.getMainLooper()).postDelayed({
                showConfirmationDialog()
            }, 700)

        }
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){
            pickedPhoto = data.data
            if(Build.VERSION.SDK_INT>=20){
                val source = ImageDecoder.createSource(requireContext().contentResolver, pickedPhoto!!)
                pickedBitMap = ImageDecoder.decodeBitmap(source)
                ivPicture.setImageBitmap(pickedBitMap)
                Handler(Looper.getMainLooper()).postDelayed({
                    showConfirmationDialog()
                }, 700)
            }else{
                pickedBitMap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, pickedPhoto)
                ivPicture.setImageBitmap(pickedBitMap)
                Handler(Looper.getMainLooper()).postDelayed({
                    showConfirmationDialog()
                }, 700)

            }
        }

    }


    private fun saveImageToInternalStorage(bitmap: Bitmap?): Uri? {
        // Check for null
        if (bitmap == null) {
            return null
        }

        // Save the bitmap to a file
        val filename = "${System.currentTimeMillis()}.jpg"
        val file = File(requireContext().externalCacheDir, filename)
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream.close()

        // Get the Uri of the file
        return Uri.fromFile(file)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==100 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            btnCapture.isEnabled = true
        }
        if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            val galleryIntext = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntext, 2)

        }

    }
    fun pickedPhoto() {
        if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1)
        } else{
            val galleryIntext = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntext, 2)
        }
    }
    private fun showConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this.context)
        alertDialogBuilder.apply {
            setTitle("Confirmation")
            setMessage("Are you sure to use this image?")
            setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->


                dialogInterface.dismiss()


                // Ubah Bitmap menjadi ByteArrayOutputStream
                val byteArrayOutputStream = ByteArrayOutputStream()
                pickedBitMap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                // Ubah ByteArrayOutputStream menjadi byte array
                val byteArray = byteArrayOutputStream.toByteArray()

                // Ubah byte array menjadi RequestBody
                val requestBody = byteArray.toRequestBody("image/jpg".toMediaTypeOrNull())

                // Gunakan requestBody ini untuk mengirim gambar melalui Retrofit
                val token = sharedPref.getToken()
                val file = File(pickedPhoto?.path)
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)
                val call = RetrofitClient.apiService.uploadNota("Bearer $token", filePart)
                call.enqueue(object : Callback<ScanResponse> {
                    override fun onResponse(call: Call<ScanResponse>, response: Response<ScanResponse>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            Log.e("POST Success", "Response: ${responseBody.toString()}")
                            Toast.makeText(requireContext(), "Upload Success", Toast.LENGTH_SHORT).show()

                            // Get the items from the response
                            val items = responseBody?.items?.items

                            // Get a reference to the database
                            val db = TransactionDB.getInstance(requireContext())
                            val transactionDao = db.transactionDao

                            // Loop through the items and convert them to transactions
                            items?.forEach { item ->
                                val amount = item.qty * item.price
                                val transaction = TransactionEntity(
                                    title = item.name,
                                    amount = amount.toInt(),
                                    category = "Expense",
                                    location = "Location", // Replace with actual location
                                    coordinate = "0,0", // Replace with actual coordinate
                                    timestamp = System.currentTimeMillis().toString() // Replace with actual timestamp
                                )

                                // Insert the transaction into the database
                                transactionDao.addTransaction(transaction)
                            }

                        } else {
                            Log.e("POST Error", "Failed to make POST request: ${response.message()}")
                            Toast.makeText(requireContext(), "Upload Failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<ScanResponse>, t: Throwable) {
                        Log.e("POST Error", "Failed to make POST request: ${t.message}")
                    }
                })
            }
            setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                ivPicture.setImageBitmap(null)
            }
            setCancelable(false)
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

}
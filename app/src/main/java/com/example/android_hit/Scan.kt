package com.example.android_hit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Scan.newInstance] factory method to
 * create an instance of this fragment.
 */
class Scan : Fragment() {
    // TODO: Rename and change types of parameters
    var pickedPhoto : Uri? = null
    var pickedBitMap : Bitmap? = null
    private val CAMERA_REQUEST_CODE = 1
    private lateinit var btnCapture : Button
    private lateinit var btnPick : Button
    private lateinit var ivPicture : ImageView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_scan, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnCapture = view.findViewById(R.id.captureImgBtn)
        ivPicture = view.findViewById(R.id.captureImageView)
        btnPick = view.findViewById(R.id.pickImgBtn)
        btnCapture.isEnabled = true

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
        }
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){
            pickedPhoto = data.data
            if(Build.VERSION.SDK_INT>=20){
                val source = ImageDecoder.createSource(requireContext().contentResolver, pickedPhoto!!)
                pickedBitMap = ImageDecoder.decodeBitmap(source)
                ivPicture.setImageBitmap(pickedBitMap)
            }else{
                pickedBitMap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, pickedPhoto)
                ivPicture.setImageBitmap(pickedBitMap)
            }
        }

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
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Scan.
         */

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Scan().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
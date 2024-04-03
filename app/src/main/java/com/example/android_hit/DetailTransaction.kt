package com.example.android_hit

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.google.android.gms.location.LocationRequest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.android_hit.databinding.FragmentDetailTransactionBinding
import com.example.android_hit.room.TransactionDB
import com.example.android_hit.room.TransactionEntity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailTransaction : Fragment(), LocationListener {
    private var _binding: FragmentDetailTransactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: TransactionDB
    private var category: String = ""
    private var coordinate: String = "-6.927314530264154, 107.77007155415649"
    private var location: String = ""
    private lateinit var transactionReceiver: TransactionReceiver
    private val RANDOMIZE_ACTION = "com.example.android_hit.RANDOMIZE_ACTION"
    private lateinit var locationManager: LocationManager
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        var fragmentCounter = 0
        var amountInput = ""
    }



    inner class TransactionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == RANDOMIZE_ACTION) {
                // Handle the broadcast message here
                val data = intent.getStringExtra("hargaRandom")
                Log.e("BROD", "Received randomize broadcast message $data")
                if (data != null) {
                    amountInput = data
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCounter++
        _binding = FragmentDetailTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = TransactionDB.getInstance(requireContext())

        transactionReceiver = TransactionReceiver()
        val filter = IntentFilter(RANDOMIZE_ACTION)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(transactionReceiver, filter)

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        binding.inputLocation.setOnClickListener {
            Log.d("Location", "Input location clicked")
            checkLocationPermission()
        }


        binding.radioExpense.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                category = "Expense"
                binding.radioExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary4))
            } else {
                binding.radioExpense.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primary_color_1
                    )
                )
            }
        }

        binding.radioIncome.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                category = "Income"
                binding.radioIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary5))
            } else {
                binding.radioIncome.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primary_color_1
                    )
                )
            }
        }

        if(amountInput!=""){
            binding.inputAmount.setText(amountInput)
        }
        val intent = requireActivity().intent.extras
        if (intent != null) {
            val id = intent.getInt("id", 0)
            val transaction = database.transactionDao.getId(id)

            binding.radioExpense.isEnabled = false
            binding.radioIncome.isEnabled = false

            binding.inputTitle.setText(transaction.title)
            binding.inputAmount.setText(transaction.amount.toString())
            if (transaction.category == "Expense") {
                binding.radioExpense.isChecked = true
            } else if (transaction.category == "Income") {
                binding.radioIncome.isChecked = true
            }
            binding.inputLocation.setText(transaction.location)
        }

        binding.buttonSave.setOnClickListener {
            val title = binding.inputTitle.text.toString()
            val amount = binding.inputAmount.text.toString()
            location = binding.inputLocation.text.toString()
            Log.d("koordinat", "Coordinate: $coordinate")
            Log.d("lokasi", "Location: $location")

            if (title.isNotEmpty() && amount.isNotEmpty() && location.isNotEmpty() && (binding.radioExpense.isChecked || binding.radioIncome.isChecked)) {
                try {
                    var timestamp: String? = null

                    if (intent != null) {
                        val id = intent.getInt("id", 0)

                        val transaction = database.transactionDao.getId(id)
                        timestamp = transaction.timestamp

                        database.transactionDao.updateTransaction(
                            TransactionEntity(
                                intent.getInt("id", 0),
                                title,
                                amount.toInt(),
                                category,
                                location,
                                coordinate,
                                timestamp.toString()
                            )
                        )
                        requireActivity().setResult(Activity.RESULT_OK)
                    } else {
                        val amountValue = amount.toInt()

                        val timestamp = System.currentTimeMillis()
                        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                        val currentDateAndTime: String = sdf.format(Date(timestamp))

                        database.transactionDao.addTransaction(
                            TransactionEntity(
                                null,
                                title,
                                amountValue,
                                category,
                                location,
                                coordinate,
                                currentDateAndTime
                            )
                        )
                    }

                    Toast.makeText(
                        requireContext(),
                        "Transaction saved",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                } catch (e: NumberFormatException) {
                    Toast.makeText(
                        requireContext(),
                        "Invalid amount entered",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (ex: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Error occurred: ${ex.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please fill all the fields",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkLocationPermission() {
        if(ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("Location", "Permission granted")
            checkGPS()
        } else {
            Log.d("Location", "Requesting location permission")
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                44
            )
        }
    }

    private fun getUserLocation() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { task ->

            val location = task

            if (location != null) {
                try {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val address_line = address?.get(0)?.getAddressLine(0)

                    coordinate = "${location.latitude},${location.longitude}"
                    binding.inputLocation.setText(address_line)

                    Log.d("Location", "Address: $address_line")
                    Log.d("Location", "Coordinate: $coordinate")

                } catch (e: Exception) {
                    e.printStackTrace()

                }
            }
        }

    }

    private fun checkGPS() {
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        builder.setAlwaysShow(true)

        var result = LocationServices.getSettingsClient(requireContext().applicationContext).checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                var response = task.getResult(
                    ApiException::class.java
                )

                getUserLocation()

            } catch (ex: ApiException) {
                when (ex.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            var resolvableApiException = ex as ResolvableApiException
                            resolvableApiException.startResolutionForResult(
                                this@DetailTransaction.requireActivity(),
                                200
                            )
                        } catch (ex: IntentSender.SendIntentException) {
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        Unit
                    }
                    else -> {}
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        amountInput=""
        if(fragmentCounter>1){
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(transactionReceiver)
        }

    }

    override fun onLocationChanged(location: Location) {
        TODO("Not yet implemented")
    }
}

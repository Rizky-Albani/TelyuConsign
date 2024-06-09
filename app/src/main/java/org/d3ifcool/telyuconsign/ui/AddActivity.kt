package org.d3ifcool.telyuconsign.ui

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.jaredrummler.materialspinner.MaterialSpinner
import org.d3ifcool.telyuconsign.Adapter.ImageAdapter
import org.d3ifcool.telyuconsign.R
import org.d3ifcool.telyuconsign.customUi.RupiahTextWatcher
import org.d3ifcool.telyuconsign.databinding.ActivityAddBinding
import java.util.Locale
import java.util.UUID

class AddActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }

    private lateinit var binding: ActivityAddBinding
    private var imageUri: Uri? = null
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var editTextLokasi: EditText
    private lateinit var googleMap: GoogleMap
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private val imageList = mutableListOf<Uri>()

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // Customizations (if any)
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true

        val bandungLatLng = LatLng(-6.2088, 106.8456)
        googleMap.addMarker(MarkerOptions().position(bandungLatLng).title("Marker in Bandung"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bandungLatLng, 12f))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editTextLokasi = findViewById(R.id.editText_lokasi_barang)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.mapContainer, mapFragment)
            .commit()
        mapFragment.getMapAsync(this)
        val tilLokasiBarang = findViewById<TextInputLayout>(R.id.til_lokasi_barang)
        tilLokasiBarang.setEndIconOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
            } else {
                getLastLocation()
            }
        }
        val editTextItemPrice = binding.editTextItemPrice
        editTextItemPrice.addTextChangedListener(RupiahTextWatcher(editTextItemPrice))
        val categorySpinner = findViewById<MaterialSpinner>(R.id.categorySpinner)
        categorySpinner.setItems("Electronic", "Furniture", "Kitchen", "Vehicle", "Sports", "Other")

        val recyclerView = findViewById<RecyclerView>(R.id.imageRecyclerView)

        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imageAdapter = ImageAdapter(imageList) { position ->
            // Handle delete button click
            imageList.removeAt(position)
            imageAdapter.notifyItemRemoved(position)
        }
        recyclerView.adapter = imageAdapter

        binding.buttonSubmit.setOnClickListener {
            if (validateForm()) {
                uploadItemData()
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonPickImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    123
                )
            }
        }

    }


    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses: List<Address> =
                        geocoder.getFromLocation(latitude, longitude, 1)!!

                    if (addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val fullAddress = address.getAddressLine(0)
                        editTextLokasi.setText(fullAddress)

                        // Add marker on the map
                        val currentLocation = LatLng(latitude, longitude)
                        googleMap.addMarker(
                            MarkerOptions().position(currentLocation).title(fullAddress)
                        )

                        // Zoom to the location with desired zoom level (e.g., level 15)
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLocation,
                                15f
                            )
                        )
                    }
                } else {
                    Toast.makeText(this, "Location not found!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateForm(): Boolean {
        val itemName = binding.editTextNamaBarang.text.toString()
        val itemCategory = binding.categorySpinner.text.toString()
        val itemPrice = binding.editTextItemPrice.text.toString()
        val itemLocation = binding.editTextLokasiBarang.text.toString()
        val itemDescription = binding.editTextDeskripsiBarang.text.toString()

        return itemName.isNotEmpty() &&
                itemCategory.isNotEmpty() &&
                itemPrice.isNotEmpty() &&
                itemLocation.isNotEmpty() &&
                itemDescription.isNotEmpty()
    }

    private fun uploadItemData() {
        val itemName = binding.editTextNamaBarang.text.toString()
        val itemPrice = binding.editTextItemPrice.text.toString()
        val itemDescription = binding.editTextDeskripsiBarang.text.toString()

        // Mendapatkan ID RadioButton yang dipilih
        val selectedRadioButtonId = binding.radioGroupPilihanBarang.checkedRadioButtonId

        // Menentukan nilai conditionRb berdasarkan ID RadioButton yang dipilih
        val conditionRb = when (selectedRadioButtonId) {
            R.id.radioButton_baru -> "New"
            R.id.radioButton_bekas -> "Used"
            else -> false
        }

        val catSpinner = binding.categorySpinner.selectedIndex.toString()
        val storageRefs = imageList.map { uri ->
            Firebase.storage.reference.child("images/${UUID.randomUUID()}")
        }

        val uploadTasks = mutableListOf<Task<Uri>>()
        val imageUrls = mutableListOf<String>()

        storageRefs.forEachIndexed { index, storageRef ->
            val uploadTask = storageRef.putFile(imageList[index])
            uploadTasks.add(uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                storageRef.downloadUrl
            }.addOnSuccessListener { uri ->
                // Add each image URL to the list
                imageUrls.add(uri.toString())

                // Check if all images are uploaded
                if (imageUrls.size == storageRefs.size) {
                    // All images are uploaded, proceed with other data
                    val location = getLocationDetails()

                    auth = FirebaseAuth.getInstance()
                    val user = auth.currentUser

                    if (user != null) {
                        val userId = user.uid
                        val username = user.displayName
                        val imageId = user.photoUrl

                        val add = hashMapOf(
                            "userId" to userId,
                            "itemName" to itemName,
                            "categories" to catSpinner,
                            "images" to imageUrls,  // Store the array of image URLs
                            "condition" to conditionRb,
                            "price" to itemPrice,
                            "latitude" to location.first,
                            "longitude" to location.second,
                            "desc" to itemDescription,
                            "username" to username,
                            "userImage" to imageId
                        )

                        // Add to Firestore
                        db.collection("items")
                            .add(add)
                            .addOnSuccessListener { documentReference ->
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                Toast.makeText(
                                    this,
                                    "Item added successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error adding data", e)
                                Toast.makeText(
                                    this,
                                    "Failed to add item. Please try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Log.d(ContentValues.TAG, "User not authenticated")
                    }
                }
            })
        }

        Tasks.whenAllComplete(uploadTasks)
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error uploading images!", e)
            }
    }

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        pickImageLauncher.launch(gallery)
    }

    // Add image to the image list
    private fun displaySelectedImage(uri: Uri?) {
        uri?.let {
            imageList.add(uri)
            imageAdapter.notifyDataSetChanged()
        }
    }

    private fun getLocationDetails(): Pair<Double, Double> {
        val itemLocation = binding.editTextLokasiBarang.text.toString()
        var latitude = 0.0
        var longitude = 0.0

        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocationName(itemLocation, 1) ?: emptyList()

        if (addresses.isNotEmpty()) {
            latitude = addresses[0].latitude
            longitude = addresses[0].longitude
        }

        return Pair(latitude, longitude)
    }

    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let {
                    imageUri = it
                    // Add imageUri to the image list
                    displaySelectedImage(imageUri)
                }
            }
        }
}
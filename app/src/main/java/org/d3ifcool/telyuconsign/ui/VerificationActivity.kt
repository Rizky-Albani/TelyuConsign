package org.d3ifcool.telyuconsign.ui

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.createBitmap
import org.d3ifcool.telyuconsign.databinding.ActivityVerifBinding

class VerificationActivity : AppCompatActivity() {

    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001

    private lateinit var binding: ActivityVerifBinding
    private var selectedImageUri: Uri? = null
    private var imageFileName: String? = null

    private val pickImageCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            displaySelectedImage(selectedImageUri)
        } else {
            Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getImageUri(imageBitmap: Bitmap): Uri? {
        val path = MediaStore.Images.Media.insertImage(contentResolver, imageBitmap, "Title", null)
        return Uri.parse(path)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user?.uid
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        //check if user already sent verification request
        db.collection("verification").whereEqualTo("uid", uid).get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    binding.buttonSubmit.isEnabled = true
                    binding.progressBar.visibility = ImageView.GONE
                } else {
                    for (document in documents) {
                        val status = document.data["status"].toString()
                        if (status == "pending") {
                            binding.buttonSubmit.isEnabled = false
                            binding.buttonSubmit.text = "Verification request pending"
                            binding.progressBar.visibility = ImageView.GONE
                            Toast.makeText(this, "You already sent a verification request, please wait", Toast.LENGTH_SHORT).show()
                        } else if (status == "rejected") {
                            binding.buttonSubmit.isEnabled = true
                            binding.progressBar.visibility = ImageView.GONE
                            Toast.makeText(this, "Your verification request has been rejected, please try again", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to check verification request", Toast.LENGTH_SHORT).show()
            }

        binding.buttonPickImage.setOnClickListener {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                val permission = arrayOf(android.Manifest.permission.CAMERA)
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                openCamera()
            }
        }

        binding.buttonSubmit.setOnClickListener {

            //show loading and disable button
            binding.buttonSubmit.isEnabled = false
            binding.progressBar.visibility = ImageView.VISIBLE

            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            val user = auth.currentUser
            val uid = user?.uid
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            val storage = com.google.firebase.storage.FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val imageRef = storageRef.child("images/${uid}.jpg")
            val uploadTask = imageRef.putFile(selectedImageUri!!)
            uploadTask.addOnFailureListener {
                Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {
                Toast.makeText(this, "Upload success", Toast.LENGTH_SHORT).show()
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val data = hashMapOf(
                        "image" to uri.toString(),
                        "uid" to uid,
                        "status" to "pending"
                    )
                    db.collection("verification").add(data)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Verification request sent", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Verification request failed", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK)
        gallery.type = "image/*"
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)

        val timestamp = System.currentTimeMillis()
        imageFileName = "JPEG_" + timestamp + "_"

        selectedImageUri = getImageUri(createBitmap(100, 100, Bitmap.Config.ARGB_8888))
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri)
        pickImageCameraLauncher.launch(cameraIntent)
    }



    private fun displaySelectedImage(uri: Uri?) {
        uri?.let {
            binding.previewImageView.setImageURI(it)
            binding.previewImageView.visibility = ImageView.VISIBLE
        }
    }
}
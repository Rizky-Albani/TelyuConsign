package org.d3ifcool.telyuconsign.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import org.d3ifcool.telyuconsign.R

class DetailItemActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // Customizations (if any)
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
    }

    @SuppressLint("WrongViewCast", "MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_item)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = " "

        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()
        mapFragment.getMapAsync(this)

        val suggestId = intent.getStringExtra("id")
        val db = Firebase.firestore
        val auth = Firebase.auth
        val user = auth.currentUser
        val uid = user?.uid
        db.collection("items").document(suggestId!!).get()
            .addOnSuccessListener { document ->
                if (document != null) {
//                    val image = document.getString("image")
                    val imageUrls = document.get("images") as? List<String>
                    val itemName = document.getString("itemName")
                    val desc = document.getString("desc")
                    val price = document.getString("price")
                    val condition = document.getString("condition")
                    val user = document.getString("username")
                    val uImage = document.getString("userImage")
                    val latitude = document.getDouble("latitude")
                    val longitude = document.getDouble("longitude")

                    val imageList = ArrayList<SlideModel>()
                    var no = 0

                    if (imageUrls != null) {
                        for (imageUrl in imageUrls) {
                            imageList.add(SlideModel(imageUrls[no].toString()))
                            no += 1
                        }
                    }

                    val imageSlider = findViewById<ImageSlider>(R.id.detail_image_slider)
                    val itemNameDetail = findViewById<TextView>(R.id.detail_item_nama)
                    val descDetail = findViewById<TextView>(R.id.detail_item_deskripsi)
                    val priceDetail = findViewById<TextView>(R.id.detail_item_harga)
                    val conditionDetail = findViewById<TextView>(R.id.detail_item_kondisi)
                    val userDetail = findViewById<TextView>(R.id.user_detail)
                    val userImage = findViewById<ImageView>(R.id.profile_pic_image_user)

                    //set map location
                    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync { googleMap ->
                        val location = LatLng(latitude!!, longitude!!)
                        googleMap.addMarker(MarkerOptions().position(location).title(itemName))
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13f))
                        googleMap.addCircle(
                            com.google.android.gms.maps.model.CircleOptions()
                                .center(location)
                                .radius(500.0)
                                .strokeWidth(3f)
                                .strokeColor(R.color.gray)
                                .fillColor(R.color.blue)
                        )
                    }

                    imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)
                    itemNameDetail.text = itemName
                    descDetail.text = desc
                    priceDetail.text = "Rp.$price"
                    conditionDetail.text = condition
                    userDetail.text = user
                    Glide.with(this)
                        .load(uImage)
                        .into(userImage)

                    val chatButton = findViewById<Button>(R.id.chat_btn)
                    if (uid == document.getString("userId")) {
                        chatButton.text = "Delete data"
                    } else {
                        chatButton.text = "CHAT NOW!"
                    }



                    chatButton.setOnClickListener {
                        if (chatButton.text == "Delete data") {
                            //make dialog
                            val builder = AlertDialog.Builder(this)
                            builder.setTitle("Delete Data")
                            builder.setMessage("Are you sure want to delete this data?")
                            builder.setPositiveButton("Yes") { dialog, which ->
                                db.collection("items").document(suggestId).delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Data berhasil dihapus",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                        val intent = android.content.Intent(
                                            this,
                                            MainActivity::class.java
                                        )
                                        startActivity(intent)

                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Data gagal dihapus", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                            }
                            builder.setNegativeButton("No") { dialog, which ->
                                dialog.dismiss()
                            }
                            val dialog: AlertDialog = builder.create()
                            dialog.show()
                        } else {
                            val db = Firebase.firestore
                            val auth = Firebase.auth
                            val user = auth.currentUser
                            val uid = user?.uid
                            val username = user?.displayName
                            val userImage = user?.photoUrl.toString()
                            //get seller data
                            db.collection("items").document(suggestId).get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        val sellerId = document.getString("userId")
                                        val sellerName = document.getString("username")
                                        val sellerImage = document.getString("userImage")
                                        val chatRoom = hashMapOf(
                                            "userIdBuyer" to uid,
                                            "userIdSeller" to sellerId,
                                            "usernameBuyer" to username,
                                            "usernameSeller" to sellerName,
                                            "userImageBuyer" to userImage,
                                            "userImageSeller" to sellerImage,
                                            "lastMessage" to "",
                                            "lastMessageTimestamp" to "",
                                        )
                                        db.collection("chatRoom").get()
                                            .addOnSuccessListener { documents ->
                                                var isExist = false
                                                val chatroomId = ""
                                                for (document in documents) {
                                                    if (document.data["userIdBuyer"] == uid && document.data["userIdSeller"] == sellerId) {
                                                        isExist = true
                                                        break
                                                    }
                                                }
                                                if (isExist) {
                                                    db.collection("chatRoom")
                                                        .whereEqualTo("userIdBuyer", uid)
                                                        .whereEqualTo("userIdSeller", sellerId)
                                                        .get()
                                                        .addOnSuccessListener { documents ->
                                                            for (document in documents) {
                                                                val chatroomId = document.id
                                                                val intent = android.content.Intent(
                                                                    this,
                                                                    ChatActivity::class.java
                                                                )
                                                                intent.putExtra(
                                                                    "chatroomId",
                                                                    chatroomId
                                                                )
                                                                startActivity(intent)
                                                            }
                                                        }
                                                        .addOnFailureListener {
                                                            Toast.makeText(
                                                                this,
                                                                "Chat room failed to create",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                } else {
                                                    db.collection("chatRoom").add(chatRoom)
                                                        .addOnSuccessListener {
                                                            val intent = android.content.Intent(
                                                                this,
                                                                ChatActivity::class.java
                                                            )
                                                            intent.putExtra("chatroomId", it.id)
                                                            startActivity(intent)
                                                        }
                                                        .addOnFailureListener {
                                                            Toast.makeText(
                                                                this,
                                                                "Chat room failed to create",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                }
                                            }
                                    }
                                }
                        }
                    }


                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_item_menu, menu)
        return true
    }


}
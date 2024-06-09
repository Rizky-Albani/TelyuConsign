package org.d3ifcool.telyuconsign.FragmentMain

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import org.d3ifcool.telyuconsign.Adapter.ProfileItemRecyclerAdapter
import org.d3ifcool.telyuconsign.Model.BarangRoomModel
import org.d3ifcool.telyuconsign.Model.SuggestModel
import org.d3ifcool.telyuconsign.R
import org.d3ifcool.telyuconsign.databinding.FragmentProfileBinding
import org.d3ifcool.telyuconsign.ui.AddActivity
import org.d3ifcool.telyuconsign.ui.DetailItemActivity
import org.d3ifcool.telyuconsign.ui.LoginActivity
import org.d3ifcool.telyuconsign.ui.VerificationActivity


class ProfileFragment : Fragment() {

    private lateinit var bindingFragment: FragmentProfileBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var profileAdapter: ProfileItemRecyclerAdapter
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        bindingFragment = FragmentProfileBinding.inflate(layoutInflater, container, false)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if(user != null){
            bindingFragment.btnLogout.visibility = View.VISIBLE
        }else{
            bindingFragment.btnLogout.visibility = View.GONE
        }

        profileSellingRecyclerView(view)

        var buttonSell = view.findViewById<Button>(R.id.startchatf)
        buttonSell.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser != null) {
                val auth = FirebaseAuth.getInstance()
                val user = auth.currentUser
                val uid = user?.uid
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                db.collection("users").document(uid.toString()).get()
                    .addOnSuccessListener { document ->
                        if(document.data?.get("verified").toString() == "false") {
                            val intent = Intent(activity, VerificationActivity::class.java)
                            startActivity(intent)
                        } else {
                            val intent = Intent(activity, AddActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(activity, "Error getting data", Toast.LENGTH_SHORT).show()
                    }
            } else {
                goToActivity(LoginActivity::class.java)
                Toast.makeText(activity, "Login First to upload item!", Toast.LENGTH_SHORT).show()
            }
        }


        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnLogout = view.findViewById<ImageView>(R.id.btn_logout)
        btnLogout.setOnClickListener {
            btnLogout()
        }

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val emailText = view.findViewById<TextView>(R.id.email_profile)
        if (user != null) {
            emailText.text = user.email
        }
        val usernameText = view.findViewById<TextView>(R.id.name_profile)
        if (user != null) {
            usernameText.text = user.displayName
        }
        val photoImage = view.findViewById<ImageView>(R.id.profile_pic_image_view)
        if (user != null) {
            Glide.with(this).load(user.photoUrl).into(photoImage)
        }

        if (usernameText.text == "null" && user?.photoUrl == null){
            val db = Firebase.firestore
            val docRef = db.collection("users").document(user!!.uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val username = document.getString("username")
                        val email = document.getString("email")
                        emailText.text = email
                        usernameText.text = username
                        photoImage.setImageResource(R.drawable.profile_icon)
                    } else {
                        Toast.makeText(this.context, "Document does not exist", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this.context, "Failed to get data", Toast.LENGTH_SHORT).show()
                }
        }

        val db = Firebase.firestore
        db.collection("users").document(user!!.uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val verification = document.getString("verified")
                    if (verification == "true") {
                        val verified = view.findViewById<TextView>(R.id.verified)
                        verified.visibility = View.VISIBLE
                    }else{
                        val verified = view.findViewById<TextView>(R.id.verified)
                        verified.text = "Not Verified"
                    }
                } else {
                    Toast.makeText(this.context, "Document does not exist", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this.context, "Failed to get data", Toast.LENGTH_SHORT).show()
            }


    }

    private fun profileSellingRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view_profile_buying)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        // Example data
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        val db = Firebase.firestore
        val dataList = ArrayList<SuggestModel>()
        db.collection("items").whereEqualTo("userId", uid).get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    val nodata = view.findViewById<TextView>(R.id.no_data)
                    nodata.visibility = View.VISIBLE
                    val sellbutton = view.findViewById<Button>(R.id.startchatf)
                    sellbutton.visibility = View.VISIBLE
                } else {
                    val nodata = view.findViewById<TextView>(R.id.no_data)
                    nodata.visibility = View.GONE
                    val sellbutton = view.findViewById<Button>(R.id.startchatf)
                    sellbutton.visibility = View.GONE
                    for (document in documents) {
                        val imageUrls = document.get("images") as? List<String>

                        if (imageUrls != null && imageUrls.isNotEmpty()) {
                            // Now you have the array of image URLs
                            // You can use it as needed

                            // Example: Print each URL
                            for (imageUrl in imageUrls) {
                                println(imageUrl)
                            }

                            val data = SuggestModel(
                                document.data["uid"].toString(),
                                document.id,
                                document.data["itemName"].toString(),
                                imageUrls?.get(0).toString(),
                                document.data["price"].toString(),
                                document.data["condition"].toString(),
                                document.data["desc"].toString(),
                                document.data["categories"].toString(),
                                document.data["longitude"].toString(),
                                document.data["latitude"].toString(),
                            )
                            dataList.add(data)
                        }
                    }
                    profileAdapter = ProfileItemRecyclerAdapter(dataList)
                    profileAdapter.onItemClick = { selectedData ->
                        val intent = Intent(activity, DetailItemActivity::class.java)
                        intent.putExtra("id", selectedData.id)
                        startActivity(intent)
                    }
                    recyclerView.adapter = profileAdapter
                }

            }
            .addOnFailureListener { exception ->
                Toast.makeText(this.context, "Failed to get data", Toast.LENGTH_SHORT).show()
            }
    }




    private fun btnLogout() {
        GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN).signOut().addOnCompleteListener {
            auth.signOut()
            val intent = Intent(this.context, LoginActivity::class.java)
            startActivity(intent)
            Toast.makeText(this.context, "Logout Success", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToActivity(activi: Class<*>) {
        val intent = Intent(activity, activi)
        startActivity(intent)
    }


}
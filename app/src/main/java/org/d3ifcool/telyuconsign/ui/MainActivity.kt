package org.d3ifcool.telyuconsign.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import org.d3ifcool.telyuconsign.FragmentMain.AddFragment
import org.d3ifcool.telyuconsign.FragmentMain.ChatFragment
import org.d3ifcool.telyuconsign.FragmentMain.HomeFragment
import org.d3ifcool.telyuconsign.FragmentMain.ProfileFragment
import org.d3ifcool.telyuconsign.R
import org.d3ifcool.telyuconsign.chat.BuyerChatActivity
import org.d3ifcool.telyuconsign.chat.SellerChatActivity
import org.d3ifcool.telyuconsign.ui.customView.MustLoginFragment


class MainActivity : AppCompatActivity() {

    lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment()
        val profileFragment = ProfileFragment()
        makeCurrentFragment(homeFragment)


        bottomNav = findViewById(R.id.navigation_bottom) as BottomNavigationView
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> makeCurrentFragment(homeFragment)
                R.id.add ->
                {
                    if (FirebaseAuth.getInstance().currentUser != null) {
                        val auth = FirebaseAuth.getInstance()
                        val user = auth.currentUser
                        val uid = user?.uid
                        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        db.collection("users").document(uid.toString()).get()
                            .addOnSuccessListener { document ->
                                if(document.data?.get("verified").toString() == "false") {
                                    val intent = Intent(this, VerificationActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    val intent = Intent(this, AddActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this, "Error getting data", Toast.LENGTH_SHORT).show()
                            }
                        bottomNav.selectedItemId = R.id.home
                    } else {
                        goToActivity(LoginActivity::class.java)
                        Toast.makeText(this, "Login First to upload item!", Toast.LENGTH_SHORT).show()
                    }

                }



                R.id.profile -> if (FirebaseAuth.getInstance().currentUser != null) {
                    makeCurrentFragment(profileFragment)
                } else {
                    goToActivity(LoginActivity::class.java)
                    Toast.makeText(this, "Login first to see Profile!", Toast.LENGTH_SHORT).show()
                }
                R.id.chat ->
                    //pop up menu chat
                    PopupMenu(this, findViewById(R.id.chat)).apply {
                        menuInflater.inflate(R.menu.chat_menu, menu)
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.chat_buyer -> if (FirebaseAuth.getInstance().currentUser != null) {
                                    val intent = Intent(this@MainActivity, BuyerChatActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    goToActivity(LoginActivity::class.java)
                                    Toast.makeText(this@MainActivity, "Login first to use Chat!", Toast.LENGTH_SHORT).show()
                                }
                                R.id.chat_seller -> if (FirebaseAuth.getInstance().currentUser != null) {
                                    val intent = Intent(this@MainActivity, SellerChatActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    goToActivity(LoginActivity::class.java)
                                    Toast.makeText(this@MainActivity, "Login first to use Chat!", Toast.LENGTH_SHORT).show()
                                }
                            }
                            true
                        }
                    }.show()
            }
            true
        }
    }
    private fun goToActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }



    private fun makeCurrentFragment (fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }

    override fun onPause() {
        super.onPause()
        val fragment = supportFragmentManager.findFragmentById(R.id.fl_wrapper)
        if (fragment is HomeFragment) {
            bottomNav.selectedItemId = R.id.home
        }
        if (fragment is ProfileFragment) {
            bottomNav.selectedItemId = R.id.profile
        }
    }

}

package org.d3ifcool.telyuconsign.ui

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import org.d3ifcool.telyuconsign.R
import org.d3ifcool.telyuconsign.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val db = Firebase.firestore

        binding.btnregister .setOnClickListener {
            val email = binding.emailTextField.text.toString()
            val password = binding.passwordTextField.text.toString()
            val confirmpassword = binding.confirmPasswordTextField.text.toString()
            val fullname = binding.fullnameTextField.text.toString()
            val username = binding.usernameTextField.text.toString()

            //Validasi email
            if (email.isEmpty()) {
                binding.emailTextField.error = "Email Harus Diisi"
                binding.emailTextField.requestFocus()
                return@setOnClickListener
            }

            //Validasi email tidak sesuai
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailTextField.error = "Email Tidak Valid"
                binding.emailTextField.requestFocus()
                return@setOnClickListener
            }

            //Validasi password
            if (password.isEmpty()) {
                binding.passwordTextField.error = "Password Harus Diisi"
                binding.passwordTextField.requestFocus()
                return@setOnClickListener
            }

            //Validasi panjang password
            if (password.length < 8) {
                binding.passwordTextField.error = "Password Minimal 6 Karakter"
                binding.passwordTextField.requestFocus()
                return@setOnClickListener
            }

            if (password != confirmpassword) {
                binding.confirmPasswordTextField.error = "Password tidak sesuai"
                binding.confirmPasswordTextField.requestFocus()
                return@setOnClickListener
            }

            RegisterFirebase(email, password, fullname, username)
            // Create a new user with a first, middle, and last name
            val user = hashMapOf(
                "fullname" to fullname,
                "username" to username,
                "email" to email,
                "verified" to "false",
                "uid" to "${username}uid+${email}"
            )

            // Add a new document with a generated ID
            db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }

    private fun RegisterFirebase(email: String, password: String, fullname: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Register Berhasil", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }

    }
}
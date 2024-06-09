package org.d3ifcool.telyuconsign.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.firestore
import org.d3ifcool.telyuconsign.Model.User
import org.d3ifcool.telyuconsign.R
import org.d3ifcool.telyuconsign.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    lateinit var auth: FirebaseAuth
    private val contract = FirebaseAuthUIActivityResultContract()
    private val signInLauncher = registerForActivityResult(contract) {
        this.onSignInResult(it)
    }
    lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val forgotPasswordText = findViewById<TextView>(R.id.forgotPasswordTextView)
        forgotPasswordText.setOnClickListener {
            // TODO 12 : Implement intent to ForgotPasswordActivity
            val intent = android.content.Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        val signUpText = findViewById<TextView>(R.id.signUpTextView)
        signUpText.setOnClickListener {
            // TODO 13 : Implement intent to SignUpActivity
            val intent = android.content.Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }

        val skipForNowText = findViewById<TextView>(R.id.skipForNowTextView)
        skipForNowText.setOnClickListener {
            // TODO 14 : Implement intent to MainActivity
            val intent = android.content.Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        actionButton()
        setButton()

        auth = FirebaseAuth.getInstance()
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            //Validasi email
            if (email.isEmpty()) {
                binding.emailEditText.error = "Email Harus Diisi"
                binding.emailEditText.requestFocus()
                return@setOnClickListener
            }

            //Validasi email tidak sesuai
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailEditText.error = "Email Tidak Valid"
                binding.emailEditText.requestFocus()
                return@setOnClickListener
            }

            //Validasi password
            if (password.isEmpty()) {
                binding.passwordEditText.error = "Password Harus Diisi"
                binding.passwordEditText.requestFocus()
                return@setOnClickListener
            }
            LoginFirebase(email, password)
        }

        binding.newGooglebtn.setOnClickListener {
            loginWithGoogle()
        }

    }

    private fun loginWithGoogle() {
        val providers = arrayListOf(
            com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val intent = com.firebase.ui.auth.AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(intent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            val nama = FirebaseAuth.getInstance().currentUser?.displayName
            addToDatabase(
                nama,
                FirebaseAuth.getInstance().currentUser?.email.toString(),
                FirebaseAuth.getInstance().currentUser?.photoUrl.toString(),
                FirebaseAuth.getInstance().currentUser?.uid.toString()
            )
            Toast.makeText(this, "Welcome $nama", Toast.LENGTH_SHORT).show()
            val intent = android.content.Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Log.i("LOGIN", "Login gagal: ${response?.error?.errorCode}")
        }
    }

    private fun setButton() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        binding.loginButton.isEnabled = true && email.isNotEmpty() && password.length >= 8
    }

    private fun actionButton() {

        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {


            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setButton()
            }

            override fun afterTextChanged(s: Editable) {

            }


        })

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {


            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setButton()
            }

            override fun afterTextChanged(s: Editable) {

            }


        })
    }

    private fun LoginFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val db = Firebase.firestore
                    db.collection("users").get().addOnSuccessListener { result ->
                        for (document in result) {
                            if (document.data["email"] == email) {
                                val username = document.data["username"].toString()
                                Toast.makeText(this, "Welcome $username", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addToDatabase(username: String?, email: String, photoUrl: String?, uid: String) {
        val db = Firebase.firestore
        val user = hashMapOf(
            "username" to username,
            "email" to email,
            "photoUrl" to photoUrl,
            "uid" to uid,
            "verified" to "false"
        )

        db.collection("users").get().addOnSuccessListener { result ->
            var isExist = false
            for (document in result) {
                if (document.data["email"] == email) {
                    isExist = true
                }
            }
            if (!isExist) {
                db.collection("users").document(uid).set(user)
            }
        }

        database = FirebaseDatabase.getInstance()
        val myRef: DatabaseReference = database.getReference("users")
        myRef.child(uid).setValue(User(uid, username, username, email, photoUrl))

    }
}
package com.example.idtokentest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var btnLogin: Button

    private lateinit var mAuth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Bind UI elements
        inputEmail = findViewById(R.id.inputemail)
        inputPassword = findViewById(R.id.inputpassword)
        btnLogin = findViewById(R.id.btnlogin)

        // Button click listener
        btnLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email = inputEmail.text.toString().trim()
        val password = inputPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // Fetch the ID Token after successful login
                fetchIdToken()
            }
            .addOnFailureListener { e ->
                Log.e("LoginError", "Login failed", e)
                Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchIdToken() {
        val user: FirebaseUser? = mAuth.currentUser
        user?.let {
            it.getIdToken(true)
                .addOnSuccessListener { getTokenResult ->
                    val idToken = getTokenResult.token
                    Log.d("IDToken", "Token: $idToken")
                    Toast.makeText(this, "Login successful! Token retrieved", Toast.LENGTH_SHORT).show()

                    // Navigate to another activity and pass the ID Token
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("idToken", idToken) // Pass the token to the next activity
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("TokenError", "Failed to fetch ID token", e)
                    Toast.makeText(this, "Failed to fetch ID token", Toast.LENGTH_SHORT).show()
                }
        }
    }
}

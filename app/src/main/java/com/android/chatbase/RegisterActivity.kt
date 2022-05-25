package com.android.chatbase

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var name: EditText
    private lateinit var btn_register: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        username = findViewById(R.id.username)
        email = findViewById(R.id.Email)
        password = findViewById(R.id.password)
        btn_register = findViewById(R.id.btn_register)
        name = findViewById(R.id.name)
        auth = FirebaseAuth.getInstance()
        btn_register.setOnClickListener {
            val text_username = username.text.toString()
            val text_email = email.text.toString()
            val text_password = password.text.toString()
            val text_name = name.text.toString()
            if (TextUtils.isEmpty(text_username) || TextUtils.isEmpty(text_email) || TextUtils.isEmpty(
                    text_password
                ) || TextUtils.isEmpty(text_name)
            ) {
                Toast.makeText(this@RegisterActivity, "All Field Required", Toast.LENGTH_SHORT)
                    .show()
            } else if (text_password.length < 6) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Password length is less than 6 character",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                register(text_username, text_email, text_password, text_name)
            }
        }
    }

    private fun register(username: String, email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser:FirebaseUser = auth.currentUser!!
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name).build()
                    firebaseUser.updateProfile(profileUpdates)
                        .addOnCompleteListener { v ->
                            if (v.isSuccessful) {
                                Log.d("Profile", "onComplete: Updated Profile")
                            }
                        }
                    val userId = firebaseUser.uid
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
                    val hashMap = HashMap<String, String>()
                    hashMap["id"] = userId
                    hashMap["username"] = username
                    hashMap["imageURL"] = R.mipmap.ic_launcher.toString()
                    reference.setValue(hashMap).addOnCompleteListener { v ->
                        if (v.isSuccessful) {
                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "You Can't Register with this Email or Password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}